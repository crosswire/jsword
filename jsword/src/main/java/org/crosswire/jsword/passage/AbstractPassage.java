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
package org.crosswire.jsword.passage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * This is a base class to help with some of the common implementation details
 * of being a Passage.
 * <p>
 * Importantly, this class takes care of Serialization in a general yet
 * optimized way. I think I am going to have a look at replacement here.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractPassage implements Passage {
    /**
     * Setup that leaves original name being null
     */
    protected AbstractPassage() {
        this(null);
    }

    /**
     * Setup the original name of this reference
     * 
     * @param passageName
     *            The text originally used to create this Passage.
     */
    protected AbstractPassage(String passageName) {
        originalName = passageName;
        listeners = new ArrayList<PassageListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key obj) {
        if (!(obj instanceof Passage)) {
            log.warn("Can't compare a Passage to a " + obj.getClass().getName());
            return -1;
        }

        Passage thatref = (Passage) obj;

        if (thatref.countVerses() == 0) {
            if (countVerses() == 0) {
                return 0;
            }
            // that is empty so he should come before me
            return -1;
        }

        if (countVerses() == 0) {
            // we are empty be he isn't so we are first
            return 1;
        }

        Verse thatfirst = thatref.getVerseAt(0);
        Verse thisfirst = getVerseAt(0);

        return thisfirst.compareTo(thatfirst);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        // This gets us a shallow copy
        AbstractPassage copy = null;

        try {
            copy = (AbstractPassage) super.clone();
            copy.listeners = new ArrayList<PassageListener>();
            copy.listeners.addAll(listeners);

            copy.originalName = originalName;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }

        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // This is cheating because I am supposed to say:
        // <code>!obj.getClass().equals(this.getClass())</code>
        // However I think it is entirely valid for a RangedPassage
        // to equal a DistinctPassage since the point of the Factory
        // is that the user does not need to know the actual type of the
        // Object he is using.
        if (!(obj instanceof Passage)) {
            return false;
        }

        Passage ref = (Passage) obj;
        // The real test
        if (!ref.getName().equals(getName())) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getName()
     */
    public String getName() {
        if (PassageUtil.isPersistentNaming() && originalName != null) {
            return originalName;
        }

        StringBuilder retcode = new StringBuilder();

        Iterator<Key> it = rangeIterator(RestrictionType.NONE);
        Verse current = null;
        while (it.hasNext()) {
            VerseRange range = (VerseRange) it.next();
            retcode.append(range.getName(current));

            // FIXME: Potential bug. According to iterator contract hasNext and
            // next must be paired.
            if (it.hasNext()) {
                retcode.append(AbstractPassage.REF_PREF_DELIM);
            }

            current = range.getStart();
        }

        return retcode.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage
     * .Key)
     */
    public String getName(Key base) {
        return getName();
    }

    public String getRootName() {
        Iterator<Key> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext()) {
            VerseRange range = (VerseRange) it.next();
            return range.getRootName();
        }

        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getOSISName()
     */
    public String getOsisRef() {
        StringBuilder retcode = new StringBuilder();

        Iterator<Key> it = rangeIterator(RestrictionType.NONE);
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Key range = it.next();
            retcode.append(range.getOsisRef());

            hasNext = it.hasNext();
            if (hasNext) {
                retcode.append(AbstractPassage.REF_OSIS_DELIM);
            }
        }

        return retcode.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getOSISId()
     */
    public String getOsisID() {
        StringBuilder retcode = new StringBuilder();

        Iterator<Key> it = rangeIterator(RestrictionType.NONE);
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Key range = it.next();
            retcode.append(range.getOsisID());

            hasNext = it.hasNext();
            if (hasNext) {
                retcode.append(AbstractPassage.REF_OSIS_DELIM);
            }
        }

        return retcode.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getOverview()
     */
    public String getOverview() {
        // TRANSLATOR: This provides an overview of the verses in one or more books. The placeholders here deserve extra comment.
        // {0,number,integer} is a placeholder for the count of verses. It will be displayed as an integer using the number system of the user's locale.
        // {0,choice,0#verses|1#verse|1<verses} uses the value of the number of verses to display the correct singular or plural form for the word "verse"
        //    Choices are separated by |. And each choice consists of a number, a comparison and the value to use when the comparison is met.
        //    Choices are ordered from smallest to largest. The numbers represent boundaries that determine when a choice is used.
        //    The comparison # means to match exactly.
        //    The comparison < means that the number on the left is less than the number being evaluated.
        //    Here, 0 is the first boundary specified by a #. So every number less than or equal to 0 get the first choice.
        //    In this situation, we are dealing with counting numbers, so we'll never have negative numbers.
        //    Next choice is 1 with a boundary specified by #. So all numbers greater than 0 (the first choice) but less than or equal to 1 get the second choice.
        //    In this situation, the only number that will match is 1.
        //    The final choice is 1<. This means that every number greater than 1 will get this choice.
        // Putting the first two placeholders together we get "0 verses", "1 verse" or "n verses" (where n is 2 or more)
        // The reason to go into this is that this pattern works for English. Other languages might have different ways of representing singular and plurals.
        // {1,number,integer} is a placeholder for the count of Bible books. It works the same way as the count of verses.
        // {1,choice,0#books|1#book|1<books} is the placeholder for the singular or plural of "book"
        return JSMsg.gettext("{0,number,integer} {0,choice,0#verses|1#verse|1<verses} in {1,number,integer} {1,choice,0#books|1#book|1<books}",
                Integer.valueOf(countVerses()), Integer.valueOf(booksInPassage()
                ));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#isEmpty()
     */
    public boolean isEmpty() {
        // Is there any content?
        return !iterator().hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public int countVerses() {
        int count = 0;

        for (Iterator<?> iter = iterator(); iter.hasNext(); iter.next()) {
            count++;
        }

        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#hasRanges(int)
     */
    public boolean hasRanges(RestrictionType restrict) {
        int count = 0;

        Iterator<Key> it = rangeIterator(restrict);
        while (it.hasNext()) {
            it.next();
            count++;
            if (count == 2) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#countRanges(int)
     */
    public int countRanges(RestrictionType restrict) {
        int count = 0;

        Iterator<Key> it = rangeIterator(restrict);
        while (it.hasNext()) {
            it.next();
            count++;
        }

        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    public int booksInPassage() {
        int current_book = 0;
        int book_count = 0;

        for (Key aKey : this) {
            Verse verse = (Verse) aKey;
            if (current_book != verse.getBook()) {
                current_book = verse.getBook();
                book_count++;
            }
        }

        return book_count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#chaptersInPassage(int)
     */
    public int chaptersInPassage(int book) throws NoSuchVerseException {
        if (book != 0) {
            BibleInfo.validate(book, 1, 1);
        }

        int current_chapter = 0;
        int chapter_count = 0;

        for (Key aKey : this) {
            Verse verse = (Verse) aKey;

            if ((book == 0 || verse.getBook() == book) && current_chapter != verse.getChapter()) {
                current_chapter = verse.getChapter();
                chapter_count++;
            }
        }

        return chapter_count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#versesInPassage(int, int)
     */
    public int versesInPassage(int book, int chapter) throws NoSuchVerseException {
        BibleInfo.validate(book == 0 ? 1 : book, chapter == 0 ? 1 : chapter, 1);

        int verse_count = 0;

        for (Key aKey : this) {
            Verse verse = (Verse) aKey;

            if ((book == 0 || verse.getBook() == book) && (chapter == 0 || verse.getChapter() == chapter)) {
                verse_count++;
            }
        }

        return verse_count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getVerseAt(int)
     */
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
        Iterator<Key> it = iterator();
        Object retcode = null;

        for (int i = 0; i <= offset; i++) {
            if (!it.hasNext()) {
                throw new ArrayIndexOutOfBoundsException(JSOtherMsg.lookupText("Index out of range (Given {0,number,integer}, Max {1,number,integer}).", Integer.valueOf(offset), Integer.valueOf(countVerses())));
            }

            retcode = it.next();
        }

        return (Verse) retcode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getVerseRangeAt(int, int)
     */
    public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        Iterator<Key> it = rangeIterator(restrict);
        Object retcode = null;

        for (int i = 0; i <= offset; i++) {
            if (!it.hasNext()) {
                throw new ArrayIndexOutOfBoundsException(JSOtherMsg.lookupText("Index out of range (Given {0,number,integer}, Max {1,number,integer}).", Integer.valueOf(offset), Integer.valueOf(countVerses())));
            }

            retcode = it.next();
        }

        return (VerseRange) retcode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#rangeIterator()
     */
    public Iterator<Key> rangeIterator(RestrictionType restrict) {
        return new VerseRangeIterator(iterator(), restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword
     * .passage.Passage)
     */
    public boolean containsAll(Passage that) {
        Iterator<Key> that_it = null;

        if (that instanceof RangedPassage) {
            that_it = ((RangedPassage) that).rangeIterator(RestrictionType.NONE);
        } else {
            that_it = that.iterator();
        }

        while (that_it.hasNext()) {
            if (!contains(that_it.next())) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#trimVerses(int)
     */
    public Passage trimVerses(int count) {
        optimizeWrites();
        raiseNormalizeProtection();

        int i = 0;
        boolean overflow = false;

        Passage remainder = (Passage) this.clone();

        for (Key verse : this) {
            i++;
            if (i > count) {
                remove(verse);
                overflow = true;
            } else {
                remainder.remove(verse);
            }
        }

        lowerNormalizeProtection();
        // The event notification is done by the remove above

        if (overflow) {
            return remainder;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#trimRanges(int, int)
     */
    public Passage trimRanges(int count, RestrictionType restrict) {
        optimizeWrites();
        raiseNormalizeProtection();

        int i = 0;
        boolean overflow = false;

        Passage remainder = (Passage) this.clone();

        Iterator<Key> it = rangeIterator(restrict);
        while (it.hasNext()) {
            i++;
            Key range = it.next();

            if (i > count) {
                remove(range);
                overflow = true;
            } else {
                remainder.remove(range);
            }
        }

        lowerNormalizeProtection();
        // The event notification is done by the remove above

        if (overflow) {
            return remainder;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#addAll(org.crosswire.jsword.passage
     * .Passage)
     */
    public void addAll(Key key) {
        Passage that = KeyUtil.getPassage(key);

        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Iterator<?> that_it = null;

        if (that instanceof RangedPassage) {
            that_it = that.rangeIterator(RestrictionType.NONE);
            while (that_it.hasNext()) {
                // Avoid touching store to make thread safety easier.
                add((Key) that_it.next());
            }
        } else {
            for (Key subkey : that) {
                add(subkey);
            }
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest()) {
            fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#removeAll(org.crosswire.jsword.passage
     * .Passage)
     */
    public void removeAll(Key key) {
        Passage that = KeyUtil.getPassage(key);

        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Iterator<?> that_it = null;

        if (that instanceof RangedPassage) {
            that_it = that.rangeIterator(RestrictionType.NONE);
        } else {
            that_it = that.iterator();
        }

        while (that_it.hasNext()) {
            // Avoid touching store to make thread safety easier.
            remove((Key) that_it.next());
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest()) {
            fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        Passage that = KeyUtil.getPassage(key);

        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Passage temp = (Passage) this.clone();
        for (Key verse : temp) {
            if (!that.contains(verse)) {
                remove(verse);
            }
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest()) {
            fireIntervalRemoved(this, null, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#clear()
     */
    public void clear() {
        optimizeWrites();
        raiseNormalizeProtection();

        remove(VerseRange.getWholeBibleVerseRange());

        if (lowerEventSuppresionAndTest()) {
            fireIntervalRemoved(this, null, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#blur(int, int)
     */
    public void blur(int verses, RestrictionType restrict) {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Passage temp = (Passage) this.clone();
        Iterator<Key> it = temp.rangeIterator(RestrictionType.NONE);

        while (it.hasNext()) {
            VerseRange range = restrict.blur((VerseRange) it.next(), verses, verses);
            add(range);
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest()) {
            fireIntervalAdded(this, null, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#writeDescription(java.io.Writer)
     */
    public void writeDescription(Writer out) throws IOException {
        BufferedWriter bout = new BufferedWriter(out);

        Iterator<Key> it = rangeIterator(RestrictionType.NONE);

        while (it.hasNext()) {
            Key range = it.next();
            bout.write(range.getName());
            bout.newLine();
        }

        bout.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#readDescription(java.io.Reader)
     */
    public void readDescription(Reader in) throws IOException, NoSuchVerseException {
        raiseEventSuppresion();
        raiseNormalizeProtection();

        int count = 0; // number of lines read
        // Quiet Android from complaining about using the default BufferReader buffer size.
        // The actual buffer size is undocumented. So this is a good idea any way.
        BufferedReader bin = new BufferedReader(in, 8192);
        while (true) {
            String line = bin.readLine();
            if (line == null) {
                break;
            }

            count++;
            addVerses(line);
        }

        // If the file was empty then there is nothing to do
        if (count == 0) {
            return;
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest()) {
            fireIntervalAdded(this, getVerseAt(0), getVerseAt(countVerses() - 1));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#optimizeReads()
     */
    public void optimizeReads() {
    }

    /**
     * Simple method to instruct children to stop caching results
     */
    protected void optimizeWrites() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#addPassageListener(org.crosswire
     * .jsword.passage.PassageListener)
     */
    public void addPassageListener(PassageListener li) {
        synchronized (listeners) {
            listeners.add(li);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#removePassageListener(org.crosswire
     * .jsword.passage.PassageListener)
     */
    public void removePassageListener(PassageListener li) {
        synchronized (listeners) {
            listeners.remove(li);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage
     * .Key)
     */
    public boolean contains(Key key) {
        Passage ref = KeyUtil.getPassage(key);
        return containsAll(ref);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return countVerses();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage
     * .Key)
     */
    public int indexOf(Key that) {
        int index = 0;
        for (Key key : this) {
            if (key.equals(that)) {
                return index;
            }

            index++;
        }

        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return getVerseAt(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return parent;
    }

    /**
     * Set a parent Key. This allows us to follow the Key interface more
     * closely, although the concept of a parent for a verse is fairly alien.
     * 
     * @param parent
     *            The parent Key for this verse
     */
    public void setParent(Key parent) {
        this.parent = parent;
    }

    /**
     * AbstractPassage subclasses must call this method <b>after</b> one or more
     * elements of the list are added. The changed elements are specified by a
     * closed interval from start to end.
     * 
     * @param source
     *            The thing that changed, typically "this".
     * @param start
     *            One end of the new interval.
     * @param end
     *            The other end of the new interval.
     * @see PassageListener
     */
    protected void fireIntervalAdded(Object source, Verse start, Verse end) {
        if (suppressEvents != 0) {
            return;
        }

        // Create Event
        PassageEvent ev = new PassageEvent(source, PassageEvent.VERSES_ADDED, start, end);

        // Copy listener vector so it won't change while firing
        List<PassageListener> temp;
        synchronized (listeners) {
            temp = new ArrayList<PassageListener>();
            temp.addAll(listeners);
        }

        // And run through the list shouting
        for (int i = 0; i < temp.size(); i++) {
            PassageListener rl = temp.get(i);
            rl.versesAdded(ev);
        }
    }

    /**
     * AbstractPassage subclasses must call this method <b>before</b> one or
     * more elements of the list are added. The changed elements are specified
     * by a closed interval from start to end.
     * 
     * @param source
     *            The thing that changed, typically "this".
     * @param start
     *            One end of the new interval.
     * @param end
     *            The other end of the new interval.
     * @see PassageListener
     */
    protected void fireIntervalRemoved(Object source, Verse start, Verse end) {
        if (suppressEvents != 0) {
            return;
        }

        // Create Event
        PassageEvent ev = new PassageEvent(source, PassageEvent.VERSES_REMOVED, start, end);

        // Copy listener vector so it won't change while firing
        List<PassageListener> temp;
        synchronized (listeners) {
            temp = new ArrayList<PassageListener>();
            temp.addAll(listeners);
        }

        // And run through the list shouting
        for (int i = 0; i < temp.size(); i++) {
            PassageListener rl = temp.get(i);
            rl.versesRemoved(ev);
        }
    }

    /**
     * AbstractPassage subclasses must call this method <b>before</b> one or
     * more elements of the list are added. The changed elements are specified
     * by a closed interval from start to end.
     * 
     * @param source
     *            The thing that changed, typically "this".
     * @param start
     *            One end of the new interval.
     * @param end
     *            The other end of the new interval.
     * @see PassageListener
     */
    protected void fireContentsChanged(Object source, Verse start, Verse end) {
        if (suppressEvents != 0) {
            return;
        }

        // Create Event
        PassageEvent ev = new PassageEvent(source, PassageEvent.VERSES_CHANGED, start, end);

        // Copy listener vector so it won't change while firing
        List<PassageListener> temp;
        synchronized (listeners) {
            temp = new ArrayList<PassageListener>();
            temp.addAll(listeners);
        }

        // And run through the list shouting
        for (int i = 0; i < temp.size(); i++) {
            PassageListener rl = temp.get(i);
            rl.versesChanged(ev);
        }
    }

    /**
     * Create a Passage from a human readable string. The opposite of
     * <code>toString()</code>. Since this method is not public it leaves
     * control of <code>suppress_events<code> up to the people
     * that call it.
     * 
     * @param refs
     *            A String containing the text of the RangedPassage
     * @throws NoSuchVerseException
     *             if the string is invalid
     */
    protected void addVerses(String refs) throws NoSuchVerseException {
        optimizeWrites();

        String[] parts = StringUtil.split(refs, AbstractPassage.REF_ALLOWED_DELIMS);
        if (parts.length == 0) {
            return;
        }

        // We treat the first as a special case because there is
        // nothing to sensibly base this reference on
        VerseRange basis = VerseRangeFactory.fromString(parts[0].trim());
        add(basis);

        // Loop for the other verses, interpreting each on the
        // basis of the one before.
        for (int i = 1; i < parts.length; i++) {
            VerseRange next = VerseRangeFactory.fromString(parts[i].trim(), basis);
            add(next);
            basis = next;
        }
    }

    /**
     * We sometimes need to sort ourselves out ... I don't think we need to be
     * synchronized since we are private and we could check that all public
     * calling of normalize() are synchronized, however this is safe, and I
     * don't think there is a cost associated with a double synchronize. (?)
     */
    /* protected */void normalize() {
        // before doing any normalization we should be checking that
        // skip_normalization == 0, and just returning if so.
    }

    /**
     * If things want to prevent normalization because they are doing a set of
     * changes that should be normalized in one go, this is what to call. Be
     * sure to call lowerNormalizeProtection() when you are done.
     */
    public void raiseNormalizeProtection() {
        skipNormalization++;

        if (skipNormalization > 10) {
            // This is a bit drastic and does not give us much
            // chance to fix the error
            // throw new LogicError();

            log.warn("skip_normalization=" + skipNormalization);
        }
    }

    /**
     * If things want to prevent normalization because they are doing a set of
     * changes that should be normalized in one go, they should call
     * raiseNormalizeProtection() and when done call this. This also calls
     * normalize() if the count reaches zero.
     */
    public void lowerNormalizeProtection() {
        skipNormalization--;

        if (skipNormalization == 0) {
            normalize();
        }

        assert skipNormalization >= 0;
    }

    /**
     * If things want to prevent event firing because they are doing a set of
     * changes that should be notified in one go, this is what to call. Be sure
     * to call lowerEventSuppression() when you are done.
     */
    public void raiseEventSuppresion() {
        suppressEvents++;

        if (suppressEvents > 10) {
            // This is a bit drastic and does not give us much
            // chance to fix the error
            // throw new LogicError();

            log.warn("suppress_events=" + suppressEvents);
        }
    }

    /**
     * If things want to prevent event firing because they are doing a set of
     * changes that should be notified in one go, they should call
     * raiseEventSuppression() and when done call this.
     * 
     * @return true if it is then safe to fire an event.
     */
    public boolean lowerEventSuppresionAndTest() {
        suppressEvents--;
        assert suppressEvents >= 0;

        return suppressEvents == 0;
    }

    /**
     * Convert the Object to a VerseRange. If base is a Verse then return a
     * VerseRange of zero length.
     * 
     * @param base
     *            The object to be cast
     * @return The VerseRange
     * @exception java.lang.ClassCastException
     *                If this is not a Verse or a VerseRange
     */
    protected static VerseRange toVerseRange(Object base) throws ClassCastException {
        assert base != null;

        if (base instanceof VerseRange) {
            return (VerseRange) base;
        } else if (base instanceof Verse) {
            return new VerseRange((Verse) base);
        }

        throw new ClassCastException(JSOtherMsg.lookupText("Can only use Verses and VerseRanges in this Collection"));
    }

    /**
     * Skip over verses that are part of a range
     */
    protected static final class VerseRangeIterator implements Iterator<Key> {
        /**
         * iterate, amalgamating Verses into VerseRanges
         */
        protected VerseRangeIterator(Iterator<Key> it, RestrictionType restrict) {
            this.it = it;
            this.restrict = restrict;

            if (it.hasNext()) {
                next_verse = (Verse) it.next();
            }

            calculateNext();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return next_range != null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public VerseRange next() throws NoSuchElementException {
            VerseRange retcode = next_range;

            if (retcode == null) {
                throw new NoSuchElementException();
            }

            calculateNext();
            return retcode;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * Find the next VerseRange
         */
        private void calculateNext() {
            if (next_verse == null) {
                next_range = null;
                return;
            }

            Verse start = next_verse;
            Verse end = next_verse;

            findnext: while (true) {
                if (!it.hasNext()) {
                    next_verse = null;
                    break;
                }

                next_verse = (Verse) it.next();

                // If the next verse adjacent
                if (!end.adjacentTo(next_verse)) {
                    break;
                }

                // Even if the next verse is adjacent we might want to break
                // if we have moved into a new chapter/book
                if (!restrict.isSameScope(end, next_verse)) {
                    break findnext;
                }

                end = next_verse;
            }

            next_range = new VerseRange(start, end);
        }

        /**
         * The Iterator that we are proxying to
         */
        private Iterator<Key> it;

        /**
         * What is the next VerseRange to be considered
         */
        private VerseRange next_range;

        /**
         * What is the next Verse to be considered
         */
        private Verse next_verse;

        /**
         * Do we restrict ranges to not crossing chapter boundaries
         */
        private RestrictionType restrict;
    }

    /**
     * Write out the object to the given ObjectOutputStream. There are 3 ways of
     * doing this - according to the 3 implementations of Passage.
     * <ul>
     * <li>Distinct: If we write out a list if verse ordinals then the space
     * used is 4 bytes per verse.
     * <li>Bitwise: If we write out a bitmap then the space used is something
     * like 31104/8 = 4k bytes.
     * <li>Ranged: The we write a list of start/end pairs then the space used is
     * 8 bytes per range.
     * </ul>
     * Since we can take our time about this section, we calculate the optimal
     * storage method before we do the saving. If some methods come out equal
     * first then bitwise is preferred, then distinct, then ranged, because I
     * imagine that for speed of deserialization this is the sensible order.
     * I've not tested it though.
     * 
     * @param out
     *            The stream to write our state to
     * @throws IOException
     *             if the read fails
     */
    protected void writeObjectSupport(ObjectOutputStream out) throws IOException {
        // the size in bits of teach storage method
        int bitwise_size = BibleInfo.versesInBible();
        int ranged_size = 8 * countRanges(RestrictionType.NONE);
        int distinct_size = 4 * countVerses();

        // if bitwise is equal smallest
        if (bitwise_size <= ranged_size && bitwise_size <= distinct_size) {
            out.writeInt(BITWISE);

            BitSet store = new BitSet(BibleInfo.versesInBible());
            for (Key aKey : this) {
                Verse verse = (Verse) aKey;
                store.set(verse.getOrdinal() - 1);
            }

            out.writeObject(store);
        } else if (distinct_size <= ranged_size) {
            // if distinct is not bigger than ranged
            // write the Passage type and the number of verses
            out.writeInt(DISTINCT);
            out.writeInt(countVerses());

            // write the verse ordinals in a loop
            for (Key aKey : this) {
                Verse verse = (Verse) aKey;
                out.writeInt(verse.getOrdinal());
            }
        } else {
            // otherwise use ranges
            // write the Passage type and the number of ranges
            out.writeInt(RANGED);
            out.writeInt(countRanges(RestrictionType.NONE));

            // write the verse ordinals in a loop
            Iterator<Key> it = rangeIterator(RestrictionType.NONE);
            while (it.hasNext()) {
                VerseRange range = (VerseRange) it.next();
                out.writeInt(range.getStart().getOrdinal());
                out.writeInt(range.getCardinality());
            }
        }

        // Ignore the original name. Is this wise?
        // I am expecting that people are not that fussed about it and
        // it could make everything far more verbose
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        listeners = new ArrayList<PassageListener>();
        originalName = null;
        parent = null;
        skipNormalization = 0;
        suppressEvents = 0;

        is.defaultReadObject();
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * 
     * @param is
     *            The stream to read our state from
     * @throws IOException
     *             if the read fails
     * @throws ClassNotFoundException
     *             If the read data is incorrect
     */
    protected void readObjectSupport(ObjectInputStream is) throws IOException, ClassNotFoundException {
        raiseEventSuppresion();
        raiseNormalizeProtection();

        try {
            int type = is.readInt();
            switch (type) {
            case BITWISE:
                BitSet store = (BitSet) is.readObject();
                for (int i = 0; i < BibleInfo.versesInBible(); i++) {
                    if (store.get(i)) {
                        add(new Verse(i + 1));
                    }
                }
                break;

            case DISTINCT:
                int verses = is.readInt();
                for (int i = 0; i < verses; i++) {
                    int ord = is.readInt();
                    add(new Verse(ord));
                }
                break;

            case RANGED:
                int ranges = is.readInt();
                for (int i = 0; i < ranges; i++) {
                    int ord = is.readInt();
                    int count = is.readInt();
                    add(RestrictionType.NONE.toRange(new Verse(ord), count));
                }
                break;

            default:
                throw new ClassCastException(JSOtherMsg.lookupText("Can only use Verses and VerseRanges in this Collection"));
            }
        } catch (NoSuchVerseException ex) {
            throw new IOException(ex.getMessage());
        }

        // We are ignoring the original_name. It was set to null in the
        // default ctor so I will ignore it here.

        // We don't bother to call fireContentsChanged(...) because
        // nothing can have registered at this point
        lowerEventSuppresionAndTest();
        lowerNormalizeProtection();
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractPassage.class);

    /**
     * Serialization type constant for a BitWise layout
     */
    protected static final int BITWISE = 0;

    /**
     * Serialization type constant for a Distinct layout
     */
    protected static final int DISTINCT = 1;

    /**
     * Serialization type constant for a Ranged layout
     */
    protected static final int RANGED = 2;

    /**
     * Count of serializations methods
     */
    protected static final int METHOD_COUNT = 3;

    /**
     * The parent key. See the key interface for more information. NOTE(joe):
     * These keys are not serialized, should we?
     * 
     * @see Key
     */
    private transient Key parent;

    /**
     * Support for change notification
     */
    protected transient List<PassageListener> listeners;

    /**
     * The original string for picky users
     */
    protected transient String originalName;

    /**
     * If we have several changes to make then we increment this and then
     * decrement it when done (and fire an event off). If the cost of
     * calculating the parameters to the fire is high then we can check that
     * this is 0 before doing the calculation.
     */
    protected transient int suppressEvents;

    /**
     * Do we skip normalization for now - if we want to skip then we increment
     * this, and the decrement it when done.
     */
    protected transient int skipNormalization;

    /**
     * What characters can we use to separate VerseRanges in a Passage
     */
    public static final String REF_ALLOWED_DELIMS = ",;\n\r\t";

    /**
     * What characters should we use to separate VerseRanges in a Passage
     */
    public static final String REF_PREF_DELIM = ", ";

    /**
     * What characters should we use to separate VerseRanges in a Passage
     */
    public static final String REF_OSIS_DELIM = " ";

    /**
     * Serialization ID
     */
    static final long serialVersionUID = -5931560451407396276L;
}
