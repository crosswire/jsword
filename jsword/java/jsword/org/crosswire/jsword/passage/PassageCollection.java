
package org.crosswire.jsword.passage;

import java.util.Collection;
import java.util.Iterator;

/**
 * Since a Passage no longer implements Collection, a proxy
 * interface might be useful. This is it. However it is not complete,
 * and won't be until I know it is needed.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PassageCollection implements Collection
{
    /**
     * Construct a Collection proxy with a Passage to proxy to.
     * @param ref The real store od data
     */
    public PassageCollection(Passage ref)
    {
        this.ref = ref;
    }

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * @return the number of elements in this collection
     */
    public int size()
    {
        return ref.countVerses();
    }

    /**
     * @return <tt>true</tt> if this collection contains no elements
     */
    public boolean isEmpty()
    {
        return ref.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this collection contains the specified
     * element.  More formally, returns <tt>true</tt> if and only if this
     * collection contains at least one element <tt>e</tt> such that
     * <tt>(o==null ? e==null : o.equals(e))</tt>.
     * @param o element whose presence in this collection is to be tested.
     * @return <tt>true</tt> if this collection contains the specified
     *         element
     */
    public boolean contains(Object o)
    {
        return ref.contains(AbstractPassage.toVerseRange(o));
    }

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).
     * @returns an <tt>Iterator</tt> over the elements in this collection
     */
    public Iterator iterator()
    {
        return null;
    }

    /**
     * Returns an array containing all of the elements in this collection.  If
     * the collection makes any guarantees as to what order its elements are
     * returned by its iterator, this method must return the elements in the
     * same order.<p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.<p>
     * This method acts as bridge between array-based and collection-based
     * APIs.
     * @return an array containing all of the elements in this collection
     */
    public Object[] toArray()
    {
        if (ref instanceof RangedPassage)
        {
            // return ((RangedPassage) ref).store.toArray();
            return null;
        }

        return null;
    }

    /**
     * Returns an array containing all of the elements in this collection
     * whose runtime type is that of the specified array.  If the collection
     * fits in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and the
     * size of this collection.<p>
     * If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * <tt>null</tt>.  This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any <tt>null</tt> elements.)<p>
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.<p>
     * Like the <tt>toArray</tt> method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs<p>
     * Suppose <tt>l</tt> is a <tt>List</tt> known to contain only strings.
     * The following code can be used to dump the list into a newly allocated
     * array of <tt>String</tt>:
     * <pre>
     *     String[] x = (String[]) v.toArray(new String[0]);
     * </pre><p>
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     * @param arr the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return an array containing the elements of this collection
     * @throws ArrayStoreException the runtime type of the specified array is
     *         not a supertype of the runtime type of every element in this
     *         collection.
     */
    public Object[] toArray(Object[] arr)
    {
        if (ref instanceof RangedPassage)
        {
            return null;

            /* Maybe one day we'll be bothered to make this work
            if (arr instanceof Verse[])
            {
                try
                {
                    // The special case for Verse to de-scope VerseRanges
                    // Create a destination array of the correct size:
                    Verse[] retcode = new Verse[countVerses()];
                    int count = 0;

                    Enumeration en = rangeElements();
                    while (en.hasMoreElements())
                    {
                        // Fill the array with all the Verses
                        VerseRange range = (VerseRange) en.nextElement();

                        for (int i=0; i<range.getVerseCount(); i++)
                        {
                            retcode[count+1] = new Verse(range.getStart().getOrdinal()+i);
                        }
                        count += range.getVerseCount();
                    }

                    return retcode;
                }
                catch (NoSuchVerseException ex)
                {
                    throw new Error("Logic Error");
                }
            }
            else
            {
                return store.toArray(arr);
            }
            */
        }

        return null;
    }

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if this collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning <tt>false</tt>).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     * @param o element whose presence in this collection is to be ensured.
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException add is not supported by this collection.
     * @throws ClassCastException class of the specified element prevents it from being added to this collection.
     * @throws IllegalArgumentException some aspect of this element prevents it from being added to this collection.
     */
    public boolean add(Object o)
    {
        boolean retcode = contains(o);
        ref.add(AbstractPassage.toVerseRange(o));
        return !retcode;
    }

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that <tt>(o==null ?  e==null :
     * o.equals(e))</tt>, if this collection contains one or more such
     * elements.  Returns true if this collection contained the specified
     * element (or equivalently, if this collection changed as a result of the
     * call).
     * @param o element to be removed from this collection, if present.
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException remove is not supported by this collection.
     */
    public boolean remove(Object o)
    {
        boolean retcode = contains(o);
        ref.remove(AbstractPassage.toVerseRange(o));
        return !retcode;
    }

    /**
     * Returns <tt>true</tt> if this collection contains all of the elements
     * in the specified collection.
     * @param that collection to be checked for containment in this collection.
     * @return <tt>true</tt> if this collection contains all of the elements in the specified collection
     * @see #contains(Object)
     */
    public synchronized boolean containsAll(Collection that)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is nonempty.)
     * @param c elements to be inserted into this collection.
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if this collection does not support the <tt>addAll</tt> method.
     * @throws ClassCastException if the class of an element of the specified collection prevents it from being added to this collection.
     * @throws IllegalArgumentException some aspect of an element of the specified collection prevents it from being added to this collection.
     * @see #add(Object)
     */
    public boolean addAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes all this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     * @param c elements to be removed from this collection.
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method is not supported by this collection.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     * @param c elements to be retained in this collection.
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> method is not supported by this Collection.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes all of the elements from this collection (optional operation).
     * This collection will be empty after this method returns unless it
     * throws an exception.
     * @throws UnsupportedOperationException if the <tt>clear</tt> method is not supported by this collection.
     */
    public void clear()
    {
        ref.clear();
    }

    /**
     * Compares the specified object with this collection for equality. <p>
     * While the <tt>Collection</tt> interface adds no stipulations to the
     * general contract for the <tt>Object.equals</tt>, programmers who
     * implement the <tt>Collection</tt> interface "directly" (in other words,
     * create a class that is a <tt>Collection</tt> but is not a <tt>Set</tt>
     * or a <tt>List</tt>) must exercise care if they choose to override the
     * <tt>Object.equals</tt>.  It is not necessary to do so, and the simplest
     * course of action is to rely on <tt>Object</tt>'s implementation, but
     * the implementer may wish to implement a "value comparison" in place of
     * the default "reference comparison."  (The <tt>List</tt> and
     * <tt>Set</tt> interfaces mandate such value comparisons.)<p>
     * The general contract for the <tt>Object.equals</tt> method states that
     * equals must be symmetric (in other words, <tt>a.equals(b)</tt> if and
     * only if <tt>b.equals(a)</tt>).  The contracts for <tt>List.equals</tt>
     * and <tt>Set.equals</tt> state that lists are only equal to other lists,
     * and sets to other sets.  Thus, a custom <tt>equals</tt> method for a
     * collection class that implements neither the <tt>List</tt> nor
     * <tt>Set</tt> interface must return <tt>false</tt> when this collection
     * is compared to any list or set.  (By the same logic, it is not possible
     * to write a class that correctly implements both the <tt>Set</tt> and
     * <tt>List</tt> interfaces.)
     * @param o Object to be compared for equality with this collection.
     * @return <tt>true</tt> if the specified object is equal to this collection
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    public boolean equals(Object o)
    {
        return ref.equals(o);
    }

    /**
     * Returns the hash code value for this collection.  While the
     * <tt>Collection</tt> interface adds no stipulations to the general
     * contract for the <tt>Object.hashCode</tt> method, programmers should
     * take note that any class that overrides the <tt>Object.equals</tt>
     * method must also override the <tt>Object.hashCode</tt> method in order
     * to satisfy the general contract for the <tt>Object.hashCode</tt>method.
     * In particular, <tt>c1.equals(c2)</tt> implies that
     * <tt>c1.hashCode()==c2.hashCode()</tt>.
     * @return the hash code value for this collection
     * @see Object#hashCode()
     * @see Object#equals(Object)
     */
    public int hashCode()
    {
        return ref.hashCode();
    }

    /**
     * The real store of data
     * @label Passage to proxy to
     */
    private Passage ref = null;
}
