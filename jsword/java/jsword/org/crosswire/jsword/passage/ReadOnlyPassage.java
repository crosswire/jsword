package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * This is a simple proxy to a real Passage object that denies all attempts
 * to write to it.
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
final class ReadOnlyPassage implements Passage
{
    /**
     * Construct a ReadOnlyPassage from a real Passage to which we proxy.
     * @param ref The real Passage
     * @param ignore Do we throw up if someone tries to change us
     */
    protected ReadOnlyPassage(Passage ref, boolean ignore)
    {
        this.ref = ref;
        this.ignore = ignore;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key)
    {
        return ref.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isLeaf()
     */
    public boolean canHaveChildren()
    {
        return ref.canHaveChildren();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#size()
     */
    public int getChildCount()
    {
        return ref.getChildCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator iterator()
    {
        return ref.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index)
    {
        return ref.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that)
    {
        return ref.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent()
    {
        return ref.getParent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getName()
     */
    public String getName()
    {
        return ref.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getOSISName()
     */
    public String getOSISName()
    {
        return ref.getOSISName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getOverview()
     */
    public String getOverview()
    {
        return ref.getOverview();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#isEmpty()
     */
    public boolean isEmpty()
    {
        return ref.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public int countVerses()
    {
        return ref.countVerses();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countRanges(int)
     */
    public int countRanges(int restrict)
    {
        return ref.countRanges(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimVerses(int)
     */
    public Passage trimVerses(int count)
    {
        return ref.trimVerses(count);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimRanges(int, int)
     */
    public Passage trimRanges(int count, int restrict)
    {
        return ref.trimRanges(count, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    public int booksInPassage()
    {
        return ref.booksInPassage();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#chaptersInPassage(int)
     */
    public int chaptersInPassage(int book) throws NoSuchVerseException
    {
        return ref.chaptersInPassage(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#versesInPassage(int, int)
     */
    public int versesInPassage(int book, int chapter) throws NoSuchVerseException
    {
        return ref.versesInPassage(book, chapter);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVerseAt(int)
     */
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        return ref.getVerseAt(offset);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVerseRangeAt(int, int)
     */
    public VerseRange getRangeAt(int offset, int restrict) throws ArrayIndexOutOfBoundsException
    {
        return ref.getRangeAt(offset, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#rangeIterator(int)
     */
    public Iterator rangeIterator(int restrict)
    {
        return ref.rangeIterator(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#contains(org.crosswire.jsword.passage.VerseBase)
     */
    public boolean contains(VerseBase that)
    {
        return ref.contains(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage.VerseBase)
     */
    public void add(VerseBase that)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage.VerseBase)
     */
    public void remove(VerseBase that)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword.passage.Passage)
     */
    public boolean containsAll(Passage that)
    {
        return ref.containsAll(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#clear()
     */
    public void clear()
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, int bounds)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#readDescription(java.io.Reader)
     */
    public void readDescription(Reader in)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#writeDescription(java.io.Writer)
     */
    public void writeDescription(Writer out) throws IOException
    {
        ref.writeDescription(out);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#optimizeReads()
     */
    public void optimizeReads()
    {
        ref.optimizeReads();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#addPassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public void addPassageListener(PassageListener li)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#removePassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public void removePassageListener(PassageListener li)
    {
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        ReadOnlyPassage clone = null;
        try
        {
            clone = (ReadOnlyPassage) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        clone.ref = this.ref;
        clone.ignore = this.ignore;
        return clone;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        return ref.compareTo(o);
    }

    /**
     * The object we are proxying to
     */
    private Passage ref;

    /**
     * Do we just silently ignore change attempts or throw up
     */
    private boolean ignore;
}
