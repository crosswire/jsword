package org.crosswire.jsword.book.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import org.crosswire.jsword.book.BookMetaData;

/**
 * An implementaion of the Propery Change methods from BookMetaData.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class AbstractBookMetaData implements BookMetaData
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (listeners == null)
        {
            listeners = new EventListenerList();
        }
        listeners.add(PropertyChangeListener.class, listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (listeners == null)
        {
            return;
        }

        listeners.remove(PropertyChangeListener.class, listener);
    }

    /**
     * Reports bound property changes.
     * If <code>oldValue</code> and <code>newValue</code> are not equal and the
     * <code>PropertyChangeEvent</code> listener list isn't empty,
     * then fire a <code>PropertyChange</code> event to each listener.
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue the old value of the property (as an Object)
     * @param newValue the new value of the property (as an Object)
     * @see java.beans.PropertyChangeSupport
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (listeners != null)
        {
            if (oldValue != null && newValue != null && oldValue.equals(newValue))
            {
                return;
            }

            if (listeners != null)
            {
                Object[] listenerList = listeners.getListenerList();
                for (int i = 0; i <= listenerList.length - 2; i += 2)
                {
                    if (listenerList[i] == PropertyChangeListener.class)
                    {
                        PropertyChangeEvent ev = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
                        PropertyChangeListener li = (PropertyChangeListener) listenerList[i + 1];
                        li.propertyChange(ev);
                    }
                }
            }
        }
    }

    /**
     * The list of property change listeners
     */
    private transient EventListenerList listeners;
}
