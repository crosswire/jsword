/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
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

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a base class to help with some of the common implementation details
 * of being a Passage.
 * <p>
 * Importantly, this class takes care of Serialization in a general yet
 * optimized way. I think I am going to have a look at replacement here.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public abstract class AbstractPassage implements Passage {
    /**
     * Setup that leaves original name being null
     *
     * @param v11n
     *            The Versification to which this Passage belongs.
     */
    protected AbstractPassage(Versification v11n) {
        this(v11n, null);
    }

    /**
     * Setup the original name of this reference
     *
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param passageName
     *            The text originally used to create this Passage.
     */
    protected AbstractPassage(Versification v11n, String passageName) {
        this.v11n = v11n;
        this.originalName = passageName;
        this.listeners = new ArrayList<PassageListener>();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVersification()
     */
    public Versification getVersification() {
        return v11n;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#reversify(org.crosswire.jsword.versification.Versification)
     */
    public Passage reversify(Versification newVersification) {
        if (v11n.equals(newVersification)) {
            return this;
        }
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#isWhole()
     */
    public boolean isWhole() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#getWhole()
     */
    public Passage getWhole() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key obj) {
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

        return getVersification().distance(thatfirst, thisfirst);
    }

    @Override
    public AbstractPassage clone() {
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

    @Override
    public boolean equals(Object obj) {
        // This is cheating because I am supposed to say:
        // <code>!obj.getClass().equals(this.getClass())</code>
        // However I think it is entirely valid for a RangedPassage
        // to equal a DistinctPassage since the point of the Factory
        // is that the user does not need to know the actual type of the
        // Object he is using.
        if (!(obj instanceof Passage)) {
            return false;
        }
        Passage that = (Passage) obj;
        // The real test
        // FIXME: this is not really true since the versification any longer.
        return that.getOsisRef().equals(getOsisRef());
    }

    @Override
    public int hashCode() {
        return getOsisRef().hashCode();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName() {
        if (PassageUtil.isPersistentNaming() && originalName != null) {
            return originalName;
        }

        StringBuilder retcode = new StringBuilder();

        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        Verse current = null;
        while (it.hasNext()) {
            VerseRange range = it.next();
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public String getName(Key base) {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext()) {
            VerseRange range = it.next();
            return range.getRootName();
        }

        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public String getOsisRef() {
        StringBuilder retcode = new StringBuilder();

        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisID() {
        StringBuilder retcode = new StringBuilder();

        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
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

    @Override
    public String toString() {
        return getName();
    }

    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty() {
        // Is there any content?
        return !iterator().hasNext();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public int countVerses() {
        int count = 0;

        for (Iterator<?> iter = iterator(); iter.hasNext(); iter.next()) {
            count++;
        }

        return count;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#hasRanges(org.crosswire.jsword.passage.RestrictionType)
     */
    public boolean hasRanges(RestrictionType restrict) {
        int count = 0;

        Iterator<VerseRange> it = rangeIterator(restrict);
        while (it.hasNext()) {
            it.next();
            count++;
            if (count == 2) {
                return true;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countRanges(org.crosswire.jsword.passage.RestrictionType)
     */
    public int countRanges(RestrictionType restrict) {
        int count = 0;

        Iterator<VerseRange> it = rangeIterator(restrict);
        while (it.hasNext()) {
            it.next();
            count++;
        }

        return count;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    public int booksInPassage() {
        // FIXME(DMS): a passage does not have to be ordered, for example PassageTally.
        BibleBook currentBook = null;
        int bookCount = 0;

        for (Key aKey : this) {
            Verse verse = (Verse) aKey;
            if (currentBook != verse.getBook()) {
                currentBook = verse.getBook();
                bookCount++;
            }
        }

        return bookCount;
    }

    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getRangeAt(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        Iterator<VerseRange> it = rangeIterator(restrict);
        Object retcode = null;

        for (int i = 0; i <= offset; i++) {
            if (!it.hasNext()) {
                throw new ArrayIndexOutOfBoundsException(JSOtherMsg.lookupText("Index out of range (Given {0,number,integer}, Max {1,number,integer}).", Integer.valueOf(offset), Integer.valueOf(countVerses())));
            }

            retcode = it.next();
        }

        return (VerseRange) retcode;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#rangeIterator(org.crosswire.jsword.passage.RestrictionType)
     */
    public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        return new VerseRangeIterator(getVersification(), iterator(), restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword.passage.Passage)
     */
    public boolean containsAll(Passage that) {
        if (that instanceof RangedPassage) {
            Iterator<VerseRange> iter = null;

            iter = ((RangedPassage) that).rangeIterator(RestrictionType.NONE);
            while (iter.hasNext()) {
                if (!contains(iter.next())) {
                    return false;
                }
            }
        } else {
            Iterator<Key> iter = that.iterator();
            while (iter.hasNext()) {
                if (!contains(iter.next())) {
                    return false;
                }
            }

        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimVerses(int)
     */
    public Passage trimVerses(int count) {
        optimizeWrites();
        raiseNormalizeProtection();

        int i = 0;
        boolean overflow = false;

        Passage remainder = this.clone();

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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimRanges(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public Passage trimRanges(int count, RestrictionType restrict) {
        optimizeWrites();
        raiseNormalizeProtection();

        int i = 0;
        boolean overflow = false;

        Passage remainder = this.clone();

        Iterator<VerseRange> it = rangeIterator(restrict);
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

    /* Now supports adding keys from different versifications.
     * (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#addAll(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        //check for key empty. This avoids the AIOBounds with that.getVerseAt, during event firing
        if (key.isEmpty()) {
            //nothing to add
            return;
        }

        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();


        if (key instanceof RangedPassage) {
            Iterator<VerseRange> it = ((RangedPassage) key).rangeIterator(RestrictionType.NONE);
            while (it.hasNext()) {
                // Avoid touching store to make thread safety easier.
                add(it.next());
            }
        } else {
            for (Key subkey : key) {
                add(subkey);
            }
        }

        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            if (key instanceof Passage) {
                Passage that = (Passage) key;
                fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
            } else if (key instanceof VerseRange) {
                VerseRange that = (VerseRange) key;
                fireIntervalAdded(this, that.getStart(), that.getEnd());
            } else if (key instanceof Verse) {
                Verse that = (Verse) key;
                fireIntervalAdded(this, that, that);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#removeAll(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        if (key instanceof RangedPassage) {
            Iterator<VerseRange> it = ((RangedPassage) key).rangeIterator(RestrictionType.NONE);
            while (it.hasNext()) {
                // Avoid touching store to make thread safety easier.
                remove(it.next());
            }
        } else {
            Iterator<Key> it = key.iterator();
            while (it.hasNext()) {
                // Avoid touching store to make thread safety easier.
                remove(it.next());
            }
        }

        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            if (key instanceof Passage) {
                Passage that = (Passage) key;
                fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
            } else if (key instanceof VerseRange) {
                VerseRange that = (VerseRange) key;
                fireIntervalRemoved(this, that.getStart(), that.getEnd());
            } else if (key instanceof Verse) {
                Verse that = (Verse) key;
                fireIntervalRemoved(this, that, that);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retainAll(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Passage temp = this.clone();
        for (Key verse : temp) {
            if (!key.contains(verse)) {
                remove(verse);
            }
        }

        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            fireIntervalRemoved(this, null, null);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
        optimizeWrites();
        raiseNormalizeProtection();

        remove(getVersification().getAllVerses());

        if (lowerEventSuppressionAndTest()) {
            fireIntervalRemoved(this, null, null);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public void blur(int verses, RestrictionType restrict) {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Passage temp = this.clone();
        Iterator<VerseRange> it = temp.rangeIterator(RestrictionType.NONE);

        while (it.hasNext()) {
            VerseRange range = restrict.blur(getVersification(), it.next(), verses, verses);
            add(range);
        }

        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            fireIntervalAdded(this, null, null);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#writeDescription(java.io.Writer)
     */
    public void writeDescription(Writer out) throws IOException {
        BufferedWriter bout = new BufferedWriter(out);
        bout.write(v11n.getName());
        bout.newLine();

        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);

        while (it.hasNext()) {
            Key range = it.next();
            bout.write(range.getName());
            bout.newLine();
        }

        bout.flush();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#readDescription(java.io.Reader)
     */
    public void readDescription(Reader in) throws IOException, NoSuchVerseException {
        raiseEventSuppresion();
        raiseNormalizeProtection();

        int count = 0; // number of lines read
        // Quiet Android from complaining about using the default BufferReader buffer size.
        // The actual buffer size is undocumented. So this is a good idea any way.
        BufferedReader bin = new BufferedReader(in, 8192);

        String v11nName = bin.readLine();
        v11n = Versifications.instance().getVersification(v11nName);

        while (true) {
            String line = bin.readLine();
            if (line == null) {
                break;
            }

            count++;
            addVerses(line, null);
        }

        // If the file was empty then there is nothing to do
        if (count == 0) {
            return;
        }

        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            fireIntervalAdded(this, getVerseAt(0), getVerseAt(countVerses() - 1));
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#optimizeReads()
     */
    public void optimizeReads() {
    }

    /**
     * Simple method to instruct children to stop caching results
     */
    protected void optimizeWrites() {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#addPassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public void addPassageListener(PassageListener li) {
        synchronized (listeners) {
            listeners.add(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#removePassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public void removePassageListener(PassageListener li) {
        synchronized (listeners) {
            listeners.remove(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        Passage ref = KeyUtil.getPassage(key);
        return containsAll(ref);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return countVerses();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return getVerseAt(index);
    }

    /* (non-Javadoc)
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
        PassageEvent ev = new PassageEvent(source, PassageEvent.EventType.ADDED, start, end);

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
        PassageEvent ev = new PassageEvent(source, PassageEvent.EventType.REMOVED, start, end);

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
        PassageEvent ev = new PassageEvent(source, PassageEvent.EventType.CHANGED, start, end);

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
     * control of <code>suppress_events</code> up to the people
     * that call it.
     *
     * @param refs
     *            A String containing the text of the RangedPassage
     * @param basis
     *            The basis for understanding refs
     * @throws NoSuchVerseException
     *             if the string is invalid
     */
    protected void addVerses(String refs, Key basis) throws NoSuchVerseException {
        optimizeWrites();

        String[] parts = StringUtil.split(refs, AbstractPassage.REF_ALLOWED_DELIMS);
        if (parts.length == 0) {
            return;
        }

        int start = 0;
        VerseRange vrBasis = null;
        if (basis instanceof Verse) {
            vrBasis = new VerseRange(v11n, (Verse) basis);
        } else if (basis instanceof VerseRange) {
            vrBasis = (VerseRange) basis;
        } else {
            // If we are not passed a useful basis,
            // then we treat the first as a special case because there is
            // nothing to sensibly base this reference on
            vrBasis = VerseRangeFactory.fromString(v11n, parts[0].trim());
            // We add it because it was part of the given input
            add(vrBasis);
            start = 1;
        }

        // Loop for the other verses, interpreting each on the
        // basis of the one before.
        for (int i = start; i < parts.length; i++) {
            VerseRange next = VerseRangeFactory.fromString(v11n, parts[i].trim(), vrBasis);
            add(next);
            vrBasis = next;
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

            log.warn("skip_normalization={}", Integer.toString(skipNormalization));
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

            log.warn("suppress_events={}", Integer.toString(suppressEvents));
        }
    }

    /**
     * If things want to prevent event firing because they are doing a set of
     * changes that should be notified in one go, they should call
     * raiseEventSuppression() and when done call this.
     *
     * @return true if it is then safe to fire an event.
     */
    public boolean lowerEventSuppressionAndTest() {
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
    protected static VerseRange toVerseRange(Versification v11n, Object base) throws ClassCastException {
        assert base != null;

        if (base instanceof VerseRange) {
            return (VerseRange) base;
        } else if (base instanceof Verse) {
            return new VerseRange(v11n, (Verse) base);
        }

        throw new ClassCastException(JSOtherMsg.lookupText("Can only use Verses and VerseRanges in this Collection"));
    }

    /**
     * Skip over verses that are part of a range
     */
    protected static final class VerseRangeIterator implements Iterator<VerseRange> {
        /**
         * iterate, amalgamating Verses into VerseRanges
         */
        protected VerseRangeIterator(Versification v11n, Iterator<Key> it, RestrictionType restrict) {
            this.v11n = v11n;
            this.it = it;
            this.restrict = restrict;

            if (it.hasNext()) {
                nextVerse = (Verse) it.next();
            }

            calculateNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return nextRange != null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public VerseRange next() throws NoSuchElementException {
            VerseRange retcode = nextRange;

            if (retcode == null) {
                throw new NoSuchElementException();
            }

            calculateNext();
            return retcode;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * Find the next VerseRange
         */
        private void calculateNext() {
            if (nextVerse == null) {
                nextRange = null;
                return;
            }

            Verse start = nextVerse;
            Verse end = nextVerse;

            findnext: while (true) {
                if (!it.hasNext()) {
                    nextVerse = null;
                    break;
                }

                nextVerse = (Verse) it.next();

                // If the next verse adjacent
                if (!v11n.isAdjacentVerse(end, nextVerse)) {
                    break;
                }

                // Even if the next verse is adjacent we might want to break
                // if we have moved into a new chapter/book
                if (!restrict.isSameScope(v11n, end, nextVerse)) {
                    break findnext;
                }

                end = nextVerse;
            }

            nextRange = new VerseRange(v11n, start, end);
        }

        /**
         * The Versification to which these verses belong.
         */
        private Versification v11n;

        /**
         * The Iterator that we are proxying to
         */
        private Iterator<Key> it;

        /**
         * What is the next VerseRange to be considered
         */
        private VerseRange nextRange;

        /**
         * What is the next Verse to be considered
         */
        private Verse nextVerse;

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
        // Save off the versification by name
        out.writeUTF(v11n.getName());

        // the size in bits of teach storage method
        int bitwiseSize = v11n.maximumOrdinal();
        int rangedSize = 8 * countRanges(RestrictionType.NONE);
        int distinctSize = 4 * countVerses();

        // if bitwise is equal smallest
        if (bitwiseSize <= rangedSize && bitwiseSize <= distinctSize) {
            out.writeInt(BITWISE);

            BitSet store = new BitSet(bitwiseSize);
            Iterator<Key> iter = iterator();
            while (iter.hasNext()) {
                Verse verse = (Verse) iter.next();
                store.set(verse.getOrdinal());
            }

            out.writeObject(store);
        } else if (distinctSize <= rangedSize) {
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
            Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
            while (it.hasNext()) {
                VerseRange range = it.next();
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

        // Read the versification by name
        String v11nName = is.readUTF();
        v11n = Versifications.instance().getVersification(v11nName);

        int type = is.readInt();
        switch (type) {
        case BITWISE:
            BitSet store = (BitSet) is.readObject();
            for (int i = 0; i < v11n.maximumOrdinal(); i++) {
                if (store.get(i)) {
                    add(v11n.decodeOrdinal(i));
                }
            }
            break;

        case DISTINCT:
            int verses = is.readInt();
            for (int i = 0; i < verses; i++) {
                int ord = is.readInt();
                add(v11n.decodeOrdinal(ord));
            }
            break;

        case RANGED:
            int ranges = is.readInt();
            for (int i = 0; i < ranges; i++) {
                int ord = is.readInt();
                int count = is.readInt();
                add(RestrictionType.NONE.toRange(getVersification(), v11n.decodeOrdinal(ord), count));
            }
            break;

        default:
            throw new ClassCastException(JSOtherMsg.lookupText("Can only use Verses and VerseRanges in this Collection"));
        }

        // We are ignoring the originalName. It was set to null in the
        // default ctor so I will ignore it here.

        // We don't bother to call fireContentsChanged(...) because
        // nothing can have registered at this point
        lowerEventSuppressionAndTest();
        lowerNormalizeProtection();
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractPassage.class);

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
     * The Versification to which this passage belongs.
     */
    private transient Versification v11n;

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
    private static final long serialVersionUID = -5931560451407396276L;
}
