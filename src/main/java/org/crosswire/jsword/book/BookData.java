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
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.crosswire.common.diff.Diff;
import org.crosswire.common.diff.DiffCleanup;
import org.crosswire.common.diff.Difference;
import org.crosswire.common.util.Language;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.VersificationsMapper;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

/**
 * BookData is the assembler of the OSIS that is returned by the filters. As
 * such it puts that into an OSIS document. When several books are supplied, it
 * gets the data from each and puts it into a parallel or interlinear view.
 * Note: it is critical that all the books are able to understand the same key.
 * That does not mean that each has to have content for each key. Missing keys
 * are represented by empty cells.
 *
 * @author Joe Walker
 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 */
public class BookData implements BookProvider {
    /**
     * Create a BookData.
     * 
     * @param book the Book to which the data belongs
     * @param key the Key specifying the data
     */
    public BookData(Book book, Key key) {
        assert book != null;
        assert key != null;

        this.key = key;

        books = new Book[1];
        books[0] = book;
    }

    /**
     * Create BookData for multiple books.
     * 
     * @param books the set of Books to which the data belongs
     * @param key the Key specifying the data
     * @param compare when true each pair of adjacent books is to be compared
     */
    public BookData(Book[] books, Key key, boolean compare) {
        assert books != null && books.length > 0;
        assert key != null;

        this.books = books.clone();
        this.key = key;
        this.comparingBooks = compare;
    }

    /**
     * Accessor for the root OSIS element
     * 
     * @return the root of the OSIS document representing this data
     * @throws BookException if there is any problem with this request
     */
    public Element getOsis() throws BookException {
        if (osis == null) {
            // TODO(DMS): Determine the proper representation of the OSISWork
            // name for multiple books.
            osis = OSISUtil.createOsisFramework(getFirstBook().getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
            Element div = getOsisFragment();
            text.addContent(div);
        }

        return osis;
    }

    /**
     * Accessor for the requested data in OSIS format.
     * 
     * @return the fragment of the OSIS document representing this data
     * @throws BookException if there is any problem with this request
     */
    public Element getOsisFragment() throws BookException {
        if (fragment == null) {
            fragment = getOsisContent(true);
        }

        return fragment;
    }

    /**
     * Accessor for the root OSIS element
     * 
     * @param allowGenTitles whether to generate titles
     * @return the root of the document
     * @throws BookException if there is any problem with this request
     */
    public Element getOsisFragment(boolean allowGenTitles) throws BookException {
        if (fragment == null) {
            fragment = getOsisContent(allowGenTitles);
        }

        return fragment;
    }

    /**
     * Output the current data as a SAX stream.
     *
     * @return A way of posting SAX events
     * @throws BookException if there is any problem with this request
     */
    public SAXEventProvider getSAXEventProvider() throws BookException {
        // If the fragment is already in a document, then use that.
        Element frag = getOsisFragment();
        Document doc = frag.getDocument();
        if (doc == null) {
            doc = new Document(frag);
        }
        return new JDOMSAXEventProvider(doc);
    }

    /**
     * Who created this data.
     *
     * @return Returns the book.
     */
    public Book[] getBooks() {
        return books == null ? null : (Book[]) books.clone();
    }

    /**
     * Get the first book.
     * 
     * @return the first or only book
     */
    public Book getFirstBook() {
        return books != null && books.length > 0 ? books[0] : null;
    }

    /**
     * The key used to obtain data from one or more books.
     *
     * @return Returns the key.
     */
    public Key getKey() {
        return key;
    }

    /**
     * @return whether the books should be compared.
     */
    public boolean isComparingBooks() {
        return comparingBooks;
    }

    private Element getOsisContent(boolean allowGenTitles) throws BookException {
        Element div = OSISUtil.factory().createDiv();

        if (books.length == 1) {
            Iterator<Content> iter = books[0].getOsisIterator(key, false, allowGenTitles);
            while (iter.hasNext()) {
                Content content = iter.next();
                div.addContent(content);
            }
        } else {
            Element table = OSISUtil.factory().createTable();
            Element row = OSISUtil.factory().createRow();
            Element cell = OSISUtil.factory().createCell();

            table.addContent(row);

            Iterator<Content>[] iters = new Iterator[books.length];
            Passage[] passages = new Passage[books.length];
            boolean[] showDiffs = new boolean[books.length - 1];
            boolean doDiffs = false;

            //iterate through a first time mapping out our data. This enables us to detect a difference in number
            //of ranges later on and flag it to the user...
            boolean[] ommittedVerses = new boolean[books.length];
            int numRangesInMasterPassage = 0;
            for (int i = 0; i < books.length; i++) {
                //although the osis iterator now caters for keys in different versifications
                //we are going to want to analyse the resulting key, so let's do the conversion up-front
                passages[i] = VersificationsMapper.instance().map(KeyUtil.getPassage(key), getVersification(i));

                //iterator takes care of versification differences here...
                iters[i] = books[i].getOsisIterator(passages[i], true, true);

                if (i == 0) {
                    //we never omit a verse for the first passage, since we're going to output everything based on that.
                    ommittedVerses[i] = false;
                    numRangesInMasterPassage = passages[i].countRanges(RestrictionType.NONE);
                } else {
                    // basically, if we end up with more ranges than we started with, then we're omitting a verse
                    //somewhere along the lines.
                    ommittedVerses[i] = passages[i].countRanges(RestrictionType.NONE) > numRangesInMasterPassage;
                }
            }


            //now read the content and map it out
            BookVerseContent[] booksContents = new BookVerseContent[books.length];
            for (int i = 0; i < books.length; i++) {
                doDiffs |= addHeaderAndSetShowDiffsState(row, showDiffs, i, ommittedVerses[i]);
                booksContents[i] = keyIteratorContentByVerse(
                        getVersification(i),
                        iters[i]);
            }

            int cellCount = 0;
            int rowCount = 0;

            //we iterate through the first book's contents, and match the verses from all the other ones
            for (Map.Entry<Verse, List<Content>> verseContent : booksContents[0].entrySet()) {
                cellCount = 0;
                row = OSISUtil.factory().createRow();
                String firstText = "";

                for (int i = 0; i < books.length; i++) {
                    Book book = books[i];
                    cell = OSISUtil.factory().createCell();
                    Language lang = book.getLanguage();
                    if (lang != null) {
                        cell.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang.getCode(), Namespace.XML_NAMESPACE);
                    }

                    row.addContent(cell);

                    StringBuilder newText = new StringBuilder(doDiffs ? 32 : 0);

                    //get the contents from the mapped verse - key might be null if we had content outside of a verse.
                    //might be a no-op if it's in the same versification.
                    Key verseInRelavantBookContents = VersificationsMapper.instance().mapVerse(verseContent.getKey(), getVersification(i));

                    //key might have several child keys, ie. a verse mapping to a range, or list of verses
                    Passage passageOfInterest = KeyUtil.getPassage(verseInRelavantBookContents);
                    Iterator<Key> passageKeys = passageOfInterest.iterator();
                    while (passageKeys.hasNext()) {
                        Key singleKey = passageKeys.next();
                        //TODO(CJB): for performance, we probably want to avoid the instanceof, so either change the
                        //method signature, or cast directly and be optimistic
                        if (!(singleKey instanceof Verse)) {
                            throw new UnsupportedOperationException("Iterating through a passage gives non-verses");
                        }

                        List<Content> xmlContent = booksContents[i].get(singleKey);

                        //if the book simply did not contain that reference (say Greek book, with Gen.1 as a reference)
                        //then we end up with a key that doesn't exist in the map. Therefore, we need to cope for this.
                        if (xmlContent == null) {
                            xmlContent = new ArrayList<Content>(0);
                        }

                        addText(doDiffs, newText, xmlContent);

                        if (doDiffs) {
                            String thisText = newText.toString();
                            if (unaccenter != null) {
                                thisText = unaccenter.unaccent(thisText);
                            }

                            if (i > 0 && showDiffs[i - 1]) {
                                List<Difference> diffs = new Diff(firstText, thisText, false).compare();
                                DiffCleanup.cleanupSemantic(diffs);
                                cell.addContent(OSISUtil.diffToOsis(diffs));

                                // Since we used that cell create another
                                cell = OSISUtil.factory().createCell();
                                lang = book.getLanguage();
                                cell.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang.getCode(), Namespace.XML_NAMESPACE);
                                row.addContent(cell);
                            }
                            if (i == 0) {
                                firstText = thisText;
                            }
                        }

                        //TODO(CJB): wrong location - we should record the keys in a set and notify
                        //when there is a problem
                        //this should be outside of the loop?
                        addContentSafely(cell, xmlContent);
                        cellCount++;
                    }
                }

                if (cellCount == 0) {
                    break;
                }

                table.addContent(row);
                rowCount++;
            }
            if (rowCount > 0) {
                div.addContent(table);
            }
        }

        return div;
    }

    /**
     * JDOM will throw an exception if we try and add the content to multiple parents.
     * As a result, we take the opportunity to add it safely, and add a note indicating
     * this content appears twice.
     *
     * @param cell the element to be added
     * @param xmlContent the collector of content
     */
    private void addContentSafely(final Element cell, final List<Content> xmlContent) {
        Element note = null;
        for (Content c : xmlContent) {
            if (c.getParent() == null) {
                cell.addContent(c);
            } else if (note != null) {
                note.addContent(c.clone());
            } else {
                //we're in the situation where we have added this already.
                //add note. In this case, we wrap the content that has already been applied.
                note = appendVersificationNotice(cell, "duplicate");
                note.addContent(c.clone());
            }
        }
    }

    /**
     * Creates a notice element.
     *
     * @param parent the parent to which the notice is added
     * @param notice the notice fragment to be applied to the sub-type
     * @return the new element
     */
    private Element appendVersificationNotice(Element parent, final String notice) {
        Element note = OSISUtil.factory().createDiv();
        note.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.GENERATED_CONTENT);
        note.setAttribute(OSISUtil.OSIS_ATTR_SUBTYPE, OSISUtil.TYPE_X_PREFIX + notice);
        parent.addContent(note);
        return note;
    }

    /**
     * @param i the current position in the array of books
     * @return the versification of the book.
     */
    private Versification getVersification(final int i) {
        return Versifications.instance().getVersification(
                books[i].getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION));
    }


    /**
     * We iterate through the content, making sure we key together those bits that belong together.
     * And separating out each verse.
     *
     * @param v11n the versification for the content
     * @param iter the iterator of OSIS content
     * @return the verse content for the book
     * @throws BookException if there is any problem with this request
     */
    private BookVerseContent keyIteratorContentByVerse(Versification v11n, final Iterator<Content> iter) throws BookException {
        BookVerseContent contentsByOsisID = new BookVerseContent();

        //we will be using this map later to track whi ch keys have been catered for in the order calculation
        Verse currentVerse = null;
        Content content;

        List<Content> contents = new ArrayList<Content>();
        while (iter.hasNext()) {
            content = iter.next();
            if (content instanceof Element && OSISUtil.OSIS_ELEMENT_VERSE.equals(((Element) content).getName())) {
                if (currentVerse != null) {
                    contentsByOsisID.put(currentVerse, contents);
                    contents = new ArrayList<Content>();
                }

                currentVerse = OSISUtil.getVerse(v11n, (Element) content);

                //if we still have stuff in here, then let's assign it to the previous verse (i.e.
                //we might have come across content that legitimately sits in verse 0 for example).
                //of perhaps we've somehow come across previous content. Either way, it clearly doesn't
                //belong to the current verse.
                if (contents.size() > 0) {
                    Verse previousVerse = new Verse(currentVerse.getVersification(), currentVerse.getOrdinal() - 1);
                    contentsByOsisID.put(previousVerse, contents);
                    contents = new ArrayList<Content>();
                }
            }

            contents.add(content);
        }

        //now append what's left into the last verse
        if (currentVerse != null) {
            contentsByOsisID.put(currentVerse, contents);
        }

        return contentsByOsisID;
    }

    /**
     * @param row           our current OSIS row
     * @param showDiffs     the array of states as to whether we are showing diffs for this column
     * @param i             our current place in the state
     * @param ommittedVerse true to indicate this column will be ommiting a verse
     * @return true if we are doing diffs
     */
    private boolean addHeaderAndSetShowDiffsState(final Element row, final boolean[] showDiffs, final int i, final boolean ommittedVerse) {
        boolean doDiffs = false;
        Book book = books[i];
        Element cell = OSISUtil.factory().createHeaderCell();

        if (i > 0) {
            Book firstBook = books[0];
            BookCategory category = book.getBookCategory();

            BookCategory prevCategory = firstBook.getBookCategory();
            String prevName = firstBook.getInitials();
            showDiffs[i - 1] = comparingBooks && BookCategory.BIBLE.equals(category) && category.equals(prevCategory)
                    && book.getLanguage().equals(firstBook.getLanguage()) && !book.getInitials().equals(prevName);

            if (showDiffs[i - 1]) {
                doDiffs = true;
                StringBuilder buf = new StringBuilder(firstBook.getInitials());
                buf.append(" ==> ");
                buf.append(book.getInitials());

                cell.addContent(OSISUtil.factory().createText(buf.toString()));
                row.addContent(cell);
                cell = OSISUtil.factory().createHeaderCell();
            }
        }

        final Text text = OSISUtil.factory().createText(book.getInitials());
        if (ommittedVerse) {
            Element notice = this.appendVersificationNotice(cell, "omitted-verses");
            notice.addContent(text);
        } else {
            cell.addContent(text);
        }
        row.addContent(cell);
        return doDiffs;
    }

    /**
     * Loops around contents and calls addText for a single element
     *
     * @param doDiffs  true for calculating differences
     * @param newText  the newText buffer used to compare one portion of text to another
     * @param contents the contents to be added
     */
    private void addText(boolean doDiffs, StringBuilder newText, List<Content> contents) {
        for (Content c : contents) {
            addText(doDiffs, newText, c);
        }
    }

    /**
     * Adds the text to the diff buffer
     *
     * @param doDiffs true for calculating differences
     * @param newText the newText buffer used to compare one portion of text to another
     * @param content the content element to be added
     */
    private void addText(boolean doDiffs, StringBuilder newText, Content content) {
        if (doDiffs) {
            // if we already have content, let's add a space to avoid chaining words together
            if (newText.length() != 0) {
                newText.append(' ');
            }

            if (content instanceof Element) {
                newText.append(OSISUtil.getCanonicalText((Element) content));
            } else if (content instanceof Text) {
                newText.append(((Text) content).getText());
            }
        }
    }

    /**
     * @param unaccenter the unaccenter to set
     */
    public void setUnaccenter(UnAccenter unaccenter) {
        this.unaccenter = unaccenter;
    }

    /**
     * A temporary holder for a map that links each verse ID to its set of OSIS elements.
     * Used purely to avoid having too many generic/array notations entangled in the code
     */
    class BookVerseContent extends TreeMap<Verse, List<Content>> {
        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -6508118172314227362L;
    }

    /**
     * What key was used to create this data
     */
    private Key key;

    /**
     * The books to which the key should be applied.
     */
    private Book[] books;

    /**
     * Whether the Books should be compared.
     */
    private boolean comparingBooks;

    /**
     * The complete OSIS container for the element
     */
    private Element osis;

    /**
     * Just the element
     */
    private Element fragment;

    private UnAccenter unaccenter;
}
