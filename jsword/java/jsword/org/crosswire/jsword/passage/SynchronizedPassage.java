package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * This is a simple proxy to a real Passage object that makes all accesses
 * synchronized. It is final to give the VM as much hope as possible at
 * being able to inline stuff.
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
final class SynchronizedPassage implements Passage
{
    /**
     * Construct a SynchronizedPassage from a real Passage to which we proxy.
     * @param ref The real Passage
     */
    protected SynchronizedPassage(Passage ref)
    {
        this.ref = ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public synchronized void addAll(Key key)
    {
        ref.addAll(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public synchronized void removeAll(Key key)
    {
        ref.removeAll(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public synchronized void retainAll(Key key)
    {
        ref.retainAll(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public synchronized boolean contains(Key key)
    {
        return ref.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#size()
     */
    public synchronized int getChildCount()
    {
        return ref.getChildCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isLeaf()
     */
    public boolean canHaveChildren()
    {
        return ref.canHaveChildren();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public synchronized Iterator iterator()
    {
        return ref.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public synchronized Key get(int index)
    {
        return ref.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public synchronized int indexOf(Key that)
    {
        return ref.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public synchronized Key getParent()
    {
        return ref.getParent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getName()
     */
    public synchronized String getName()
    {
        return ref.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getOSISName()
     */
    public synchronized String getOSISName()
    {
        return ref.getOSISName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getOverview()
     */
    public synchronized String getOverview()
    {
        return ref.getOverview();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#isEmpty()
     */
    public synchronized boolean isEmpty()
    {
        return ref.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public synchronized int countVerses()
    {
        return ref.countVerses();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countRanges(int)
     */
    public synchronized int countRanges(int restrict)
    {
        return ref.countRanges(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimVerses(int)
     */
    public synchronized Passage trimVerses(int count)
    {
        return ref.trimVerses(count);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimRanges(int, int)
     */
    public synchronized Passage trimRanges(int count, int restrict)
    {
        return ref.trimRanges(count, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    public synchronized int booksInPassage()
    {
        return ref.booksInPassage();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#chaptersInPassage(int)
     */
    public synchronized int chaptersInPassage(int book) throws NoSuchVerseException
    {
        return ref.chaptersInPassage(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#versesInPassage(int, int)
     */
    public synchronized int versesInPassage(int book, int chapter) throws NoSuchVerseException
    {
        return ref.versesInPassage(book, chapter);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVerseAt(int)
     */
    public synchronized Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        return ref.getVerseAt(offset);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVerseRangeAt(int, int)
     */
    public synchronized VerseRange getRangeAt(int offset, int restrict) throws ArrayIndexOutOfBoundsException
    {
        return ref.getRangeAt(offset, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#rangeIterator(int)
     */
    public synchronized Iterator rangeIterator(int restrict)
    {
        return ref.rangeIterator(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#contains(org.crosswire.jsword.passage.VerseBase)
     */
    public synchronized boolean contains(VerseBase that)
    {
        return ref.contains(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage.VerseBase)
     */
    public synchronized void add(VerseBase that)
    {
        ref.add(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage.VerseBase)
     */
    public synchronized void remove(VerseBase that)
    {
        ref.remove(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword.passage.Passage)
     */
    public synchronized boolean containsAll(Passage that)
    {
        return ref.containsAll(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#clear()
     */
    public synchronized void clear()
    {
        ref.clear();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public synchronized void blur(int by, int bounds)
    {
        ref.blur(by, bounds);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#readDescription(java.io.Reader)
     */
    public synchronized void readDescription(Reader in) throws IOException, NoSuchVerseException
    {
        ref.readDescription(in);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#writeDescription(java.io.Writer)
     */
    public synchronized void writeDescription(Writer out) throws IOException
    {
        ref.writeDescription(out);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#optimizeReads()
     */
    public synchronized void optimizeReads()
    {
        ref.optimizeReads();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#addPassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public synchronized void addPassageListener(PassageListener li)
    {
        ref.addPassageListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#removePassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public synchronized void removePassageListener(PassageListener li)
    {
        ref.removePassageListener(li);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public synchronized Object clone()
    {
        return ref.clone();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public synchronized int compareTo(Object o)
    {
        return ref.compareTo(o);
    }

    /**
     * The object we are proxying to
     */
    private Passage ref;
}