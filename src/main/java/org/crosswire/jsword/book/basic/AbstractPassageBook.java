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
package org.crosswire.jsword.book.basic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.Backend;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.VersificationsMapper;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of Book that lets implementors just concentrate on
 * reading book data.
 *
 * @author Joe Walker
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
public abstract class AbstractPassageBook extends AbstractBook {

    /**
     * Construct an AbstractPassageBook given the BookMetaData and the AbstractBackend.
     *
     * @param bmd     the metadata that describes the book
     * @param backend the means by which the resource is accessed
     */
    public AbstractPassageBook(BookMetaData bmd, Backend backend) {
        super(bmd, backend);
        keyf = PassageKeyFactory.instance();
        this.versification = bmd.getProperty(BookMetaData.KEY_VERSIFICATION);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage.Key, boolean, boolean)
     */
    public Iterator<Content> getOsisIterator(final Key key, final boolean allowEmpty, final boolean allowGenTitles) throws BookException {
        // Note: allowEmpty indicates parallel view
        // TODO(DMS): make the iterator be demand driven
        final SourceFilter filter = getFilter();

        // For all the ranges in this Passage
        //TODO(CJB): I'd prefer to do the key mapping in KeyUtil, and pass in our current versification.
        //we could remove the method that doesn't support the versification parameter.
        //but that has far reaching consequences.
        Passage ref = VersificationsMapper.instance().map(KeyUtil.getPassage(key), this.getVersification());

        // Generated titles are shown when
        // there are 2 or more ranges or
        // empty are not allowed and generated titles are allowed
        final boolean showTitles = ref.hasRanges(RestrictionType.CHAPTER) || (!allowEmpty && allowGenTitles);

        RawTextToXmlProcessor processor = new RawTextToXmlProcessor() {
            // track previous text to exclude duplicates caused by merged verses
            private String previousVerseText = "";

            public void preRange(VerseRange range, List<Content> partialDom) {
                if (showTitles) {
                    Element title = OSISUtil.factory().createGeneratedTitle();
                    title.addContent(range.getName());
                    partialDom.add(title);
                }
            }

            public void postVerse(Key verse, List<Content> partialDom, String rawText) {
                // If the verse is empty or repeated then we shouldn't add the verse
                if ((allowEmpty || rawText.length() > 0) && !previousVerseText.equals(rawText)) {
                    List<Content> osisContent = filter.toOSIS(AbstractPassageBook.this, verse, rawText);
                    addOSIS(verse, partialDom, osisContent);
                }
                previousVerseText = rawText;
            }

            public void init(List<Content> partialDom) {
                // no-op
            }
        };

        return getOsis(ref, processor).iterator();
    }

    /**
     * Add the OSIS elements to the div element. Note, this assumes that the
     * data is fully marked up.
     *
     * @param key         The key being added
     * @param div         The div element to which the key's OSIS representation is
     *                    being added
     * @param osisContent The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, Element div, List<Content> osisContent) {
        assert key != null;
        div.addContent(osisContent);
    }

    /**
     * Add the OSIS elements to the content list. Note, this assumes that the
     * data is fully marked up.
     *
     * @param key         The key being added
     * @param content     The list to which the key's OSIS representation is being added
     * @param osisContent The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, List<Content> content, List<Content> osisContent) {
        assert key != null;
        content.addAll(osisContent);
    }

    /**
     * What filter should be used to filter data in the format produced by this
     * Book?. In some ways this method is more suited to BookMetaData however we
     * do not have a specialization of BookMetaData to fit AbstractPassageBook
     * and it doesn't like any higher in the hierarchy at the moment so I will
     * leave this here.
     */
    protected abstract SourceFilter getFilter();

    /**
     * For when we want to add writing functionality. This does not work.
     *
     * @param key
     * @param bdata
     * @throws BookException
     */
    public void setDocument(Key key, BookData bdata) throws BookException {
        // For all of the sections
        for (Content nextElem : OSISUtil.getFragment(bdata.getOsisFragment())) {
            if (nextElem instanceof Element) {
                Element div = (Element) nextElem;

                // For all of the Verses in the section
                for (Content data : div.getContent()) {
                    if (data instanceof Element) {
                        Element overse = (Element) data;
                        String text = OSISUtil.getPlainText(overse);

                        setRawText(key, text);
                    } else {
                        log.error("Ignoring non OSIS/Verse content of DIV.");
                    }
                }
            } else {
                log.error("Ignoring non OSIS/Verse content of DIV.");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
    public boolean isWritable() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#createEmptyKeyList()
     */
    public final Key createEmptyKeyList() {
        return keyf.createEmptyKeyList(Versifications.instance().getVersification(versification));
    }


    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getValidKey(java.lang.String)
     */
    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (NoSuchKeyException e) {
            return createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public final Key getKey(String text) throws NoSuchKeyException {
        return PassageKeyFactory.instance().getKey(Versifications.instance().getVersification(versification), text);
    }

    public Versification getVersification() {
        if (this.versificationSystem == null) {
            this.versificationSystem = Versifications.instance().getVersification(getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION));
        }
        return versificationSystem;
    }

    /**
     * This implementation lazily inits, saves to the JSword conf file and also caches the book list for future use.
     *
     * @return the list of Bible books contained in the module
     */
    public Set<BibleBook> getBibleBooks() {
        if (bibleBooks == null) {
            synchronized (this) {
                if (bibleBooks == null) {
                    bibleBooks = getBibleBooksInternal();
                }
            }
        }

        return bibleBooks;
    }

    /**
     * Obtains the set of bible books from the internal configuration file, creating it if required.
     * @return the bible books relevant to this module.
     */
    private Set<BibleBook> getBibleBooksInternal() {
        String list = this.getBookMetaData().getProperty(BookMetaData.KEY_BOOKLIST);
        Set<BibleBook> books;
        if (list == null) {
            //calculate and store
            books = calculateBibleBookList();
            String listOfBooks = toString(books);
            this.putProperty(BookMetaData.KEY_BOOKLIST, listOfBooks);
        } else {
            //iterate through each item and get the books as a bible books
            books = fromString(list);
        }

        return books;
    }

    private Set<BibleBook> fromString(String list) {
        Set<BibleBook> books = new LinkedHashSet<BibleBook>(list.length() / 2);
        final String[] bookOsis = StringUtil.split(list, ' ');
        for (String s : bookOsis) {
            books.add(BibleBook.fromExactOSIS(s));
        }
        return books;
    }

    private String toString(Set<BibleBook> books) {
        StringBuilder sb = new StringBuilder(books.size() * 8);
        for (Iterator<BibleBook> iterator = books.iterator(); iterator.hasNext(); ) {
            BibleBook b = iterator.next();
            sb.append(b.getOSIS());
            if (iterator.hasNext()) {
                sb.append(' ');
            }

        }
        return sb.toString();
    }

    /**
     * Iterate all books checking if document contains a verse from the book
     */
    private Set<BibleBook> calculateBibleBookList() {
        final BookMetaData bookMetaData = this.getBookMetaData();
        final VerseKey scope = (VerseKey) getScope();
        if (scope == null) {
            return new HashSet<BibleBook>();
        }

        final Set<BibleBook> bookList = new LinkedHashSet<BibleBook>();

        // iterate over all book possible in this document
        final Versification v11n = Versifications.instance().getVersification(bookMetaData.getProperty(BookMetaData.KEY_VERSIFICATION));
        final Iterator<BibleBook> v11nBookIterator = v11n.getBookIterator();

        while (v11nBookIterator.hasNext()) {
            BibleBook bibleBook = v11nBookIterator.next();
            // test some random verses - normally ch1 v 1 is sufficient - but we don't want to miss any
            if (scope.contains(new Verse(v11n, bibleBook, 1, 1))
                || scope.contains(new Verse(v11n, bibleBook, 1, 2)))
            {
                bookList.add(bibleBook);
            }
        }

        return bookList;
    }

    /**
     * The name of the versification or null
     */
    private String versification;

    /**
     * Versification system, created lazily, so use getter
     */
    private Versification versificationSystem;

    /**
     * Our key manager
     */
    private PassageKeyFactory keyf;

    /**
     * lazy of cache of bible books contained in the Book
     */
    private volatile Set<BibleBook> bibleBooks;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractPassageBook.class);

}
