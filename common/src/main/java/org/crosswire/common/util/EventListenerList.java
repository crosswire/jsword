/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;

/**
 * A class which holds a list of EventListeners. This code is lifted from
 * javax.sw*ng.event.EventListnerList. It is very useful in non GUI code which
 * does not need the rest of sw*ng. BORROWED: From
 * javax.sw*ng.event.EventListnerList
 * 
 * <p>
 * It differs in that it is fully synchronized, thus thread safe.
 * 
 * <p>
 * If you inculde sw*ng code in non-gui code then you can end up not being able
 * to run your code in a headerless environment because X includes Y which
 * includes Font which tries to lookup font metrics and then everything dies. I
 * appreciate the Headerless changes in 1.4, but my rule (from before 1.4) was
 * "Don't inculde sw*ng code from non-sw*ng code", and I enforced that by making
 * sure all my sw*ng code was in a package with sw*ng in the name and by making
 * sure that the word sw*ng was not in any non-sw*ng code (hence I spelled it
 * sw*ng in comments) That way some simple greps will tell you if the servlet
 * front end was likely to die.
 * 
 * <p>
 * A single instance can be used to hold all listeners (of all types) for the
 * instance using the list. It is the responsiblity of the class using the
 * EventListenerList to provide type-safe API (preferably conforming to the
 * JavaBeans spec) and methods which dispatch event notification methods to
 * appropriate Event Listeners on the list.
 * 
 * The main benefits which this class provides are that it is relatively cheap
 * in the case of no listeners, and provides serialization for eventlistener
 * lists in a single place, as well as MT safety.
 * 
 * Usage example: Say one is defining a class which sends out FooEvents, and
 * wantds to allow users of the class to register FooListeners and receive
 * notification when FooEvents occur. The following should be added to the class
 * definition:
 * 
 * <pre>
 * EventListenerList listenrList = new EventListnerList();
 * FooEvent fooEvent = null;
 * 
 * public void addFooListener(FooListener l) {
 *     listenerList.add(FooListener.class, l);
 * }
 * 
 * public void removeFooListener(FooListener l) {
 *     listenerList.remove(FooListener.class, l);
 * }
 * 
 * // Notify all listeners that have registered interest for
 * // notification on this event type.  The event instance
 * // is lazily created using the parameters passed into
 * // the fire method.
 * 
 * protected void firefooXXX() {
 *     // Guaranteed to return a non-null array
 *     Object[] listeners = listenerList.getListenerList();
 *     // Process the listeners last to first, notifying
 *     // those that are interested in this event
 *     for (int i = listeners.length - 2; i &gt;= 0; i -= 2) {
 *         if (listeners[i] == FooListener.class) {
 *             // Lazily create the event:
 *             if (fooEvent == null)
 *                 fooEvent = new FooEvent(this);
 *             ((FooListener) listeners[i + 1]).fooXXX(fooEvent);
 *         }
 *     }
 * }
 * </pre>
 * 
 * foo should be changed to the appropriate name, and Method to the appropriate
 * method name (one fire method should exist for each notification method in the
 * FooListener interface).
 * <p>
 * <strong>Warning:</strong> Serialized objects of this class will not be
 * compatible with future Sw*ng releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Sw*ng. A future release of Sw*ng will provide support for
 * long term persistence.
 * 
 * @version 1.34 01/23/03
 * @author Georges Saab
 * @author Hans Muller
 * @author James Gosling
 */
public class EventListenerList implements Serializable {
    /**
     * This passes back the event listener list as an array of ListenerType -
     * listener pairs.
     * 
     * This method is guaranteed to pass back a non-null array, so that no
     * null-checking is required in fire methods. A zero-length array of Object
     * should be returned if there are currently no listeners.
     */
    public synchronized Object[] getListenerList() {
        int i = listenerList.length;
        Object[] tmp = new Object[i];
        System.arraycopy(listenerList, 0, tmp, 0, i);
        return tmp;
    }

    /**
     * Return an array of all the listeners of the given type.
     * 
     * @return all of the listeners of the specified type.
     * @exception ClassCastException
     *                if the supplied class is not assignable to EventListener
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class t) {
        Object[] lList = getListenerList();
        int n = getListenerCount(lList, t);
        EventListener[] result = (EventListener[]) Array.newInstance(t, n);
        int j = 0;
        for (int i = lList.length - 2; i >= 0; i -= 2) {
            if (lList[i] == t) {
                result[j++] = (EventListener) lList[i + 1];
            }
        }
        return result;
    }

    /**
     * Returns the total number of listeners for this listener list.
     */
    public synchronized int getListenerCount() {
        return listenerList.length / 2;
    }

    /**
     * Returns the total number of listeners of the supplied type for this
     * listener list.
     */
    public int getListenerCount(Class t) {
        Object[] lList = getListenerList();
        return getListenerCount(lList, t);
    }

    private int getListenerCount(Object[] list, Class t) {
        int count = 0;
        for (int i = 0; i < list.length; i += 2) {
            if (t == (Class) list[i]) {
                count++;
            }
        }
        return count;
    }

    /**
     * Add the listener as a listener of the specified type.
     * 
     * @param t
     *            the type of the listener to be added
     * @param li
     *            the listener to be added
     */
    public synchronized void add(Class t, EventListener li) {
        if (li == null) {
            // In an ideal world, we would do an assertion here
            // to help developers know they are probably doing
            // something wrong
            return;
        }

        if (!t.isInstance(li)) {
            throw new IllegalArgumentException(Msg.WRONG_TYPE.toString(new Object[] {
                    li, t
            }));
        }

        if (listenerList == NULL_ARRAY) {
            // if this is the first listener added,
            // initialize the lists
            listenerList = new Object[] {
                    t, li
            };
        } else {
            // Otherwise copy the array and add the new listener
            int i = listenerList.length;
            Object[] tmp = new Object[i + 2];
            System.arraycopy(listenerList, 0, tmp, 0, i);

            tmp[i] = t;
            tmp[i + 1] = li;

            listenerList = tmp;
        }
    }

    /**
     * Remove the listener as a listener of the specified type.
     * 
     * @param t
     *            the type of the listener to be removed
     * @param li
     *            the listener to be removed
     */
    public synchronized void remove(Class t, EventListener li) {
        if (li == null) {
            // In an ideal world, we would do an assertion here
            // to help developers know they are probably doing
            // something wrong
            return;
        }

        if (!t.isInstance(li)) {
            throw new IllegalArgumentException(Msg.WRONG_TYPE.toString(new Object[] {
                    li, t
            }));
        }

        // Is li on the list?
        int index = -1;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == t && listenerList[i + 1].equals(li)) {
                index = i;
                break;
            }
        }

        // If so, remove it
        if (index != -1) {
            Object[] tmp = new Object[listenerList.length - 2];

            // Copy the list up to index
            System.arraycopy(listenerList, 0, tmp, 0, index);

            // Copy from two past the index, up to
            // the end of tmp (which is two elements
            // shorter than the old list)
            if (index < tmp.length) {
                System.arraycopy(listenerList, index + 2, tmp, index, tmp.length - index);
            }

            // set the listener array to the new array or null
            listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
        }
    }

    /**
     * Serialization support
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        Object[] lList = getListenerList();
        oos.defaultWriteObject();

        // Save the non-null event listeners:
        for (int i = 0; i < lList.length; i += 2) {
            Class t = (Class) lList[i];
            EventListener li = (EventListener) lList[i + 1];
            if ((li != null) && (li instanceof Serializable)) {
                oos.writeObject(t.getName());
                oos.writeObject(li);
            }
        }

        oos.writeObject(null);
    }

    /**
     * Serialization support
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        listenerList = NULL_ARRAY;
        ois.defaultReadObject();

        while (true) {
            Object listenerTypeOrNull = ois.readObject();
            if (listenerTypeOrNull == null) {
                break;
            }

            EventListener li = (EventListener) ois.readObject();
            add(ClassUtil.forName((String) listenerTypeOrNull), li);
        }
    }

    /**
     * Return a string representation of the EventListenerList.
     */
    /* @Override */
    public String toString() {
        Object[] lList = listenerList;
        StringBuffer s = new StringBuffer("EventListenerList: "); //$NON-NLS-1$
        s.append(lList.length / 2);
        s.append(" listeners: "); //$NON-NLS-1$

        for (int i = 0; i <= lList.length - 2; i += 2) {
            s.append(" type "); //$NON-NLS-1$
            s.append(((Class) lList[i]).getName());
            s.append(" listener "); //$NON-NLS-1$
            s.append(lList[i + 1]);
        }

        return s.toString();
    }

    /**
     * A null array to be shared by all empty listener lists
     */
    private static final Object[] NULL_ARRAY = new Object[0];

    /**
     * The list of ListenerType - Listener pairs
     */
    protected transient Object[] listenerList = NULL_ARRAY;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256999960636436785L;
}
