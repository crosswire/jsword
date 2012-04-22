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
 * ID: $Id:LuceneIndex.java 984 2006-01-23 14:18:33 -0500 (Mon, 23 Jan 2006) dmsmith $
 */
package org.crosswire.jsword.index.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.index.AbstractIndex;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.lucene.analysis.LuceneAnalyzer;
import org.crosswire.jsword.index.search.SearchModifier;
import org.crosswire.jsword.passage.AbstractPassage;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom.Element;

/**
 * Implement the SearchEngine using Lucene as the search engine.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LuceneIndex extends AbstractIndex implements Activatable {
    /*
     * The following fields are named the same as Sword in the hopes of sharing
     * indexes.
     */
    /**
     * The Lucene field for the osisID
     */
    public static final String FIELD_KEY = "key";

    /**
     * The Lucene field for the text contents
     */
    public static final String FIELD_BODY = "content";

    /**
     * The Lucene field for the strong numbers
     */
    public static final String FIELD_STRONG = "strong";

    /**
     * The Lucene field for headings
     */
    public static final String FIELD_HEADING = "heading";

    /**
     * The Lucene field for cross references
     */
    public static final String FIELD_XREF = "xref";

    /**
     * The Lucene field for the notes
     */
    public static final String FIELD_NOTE = "note";

    /**
     * Read an existing index and use it.
     * 
     * @throws BookException
     *             If we fail to read the index files
     */
    public LuceneIndex(Book book, URI storage) throws BookException {
        this.book = book;

        try {
            this.path = NetUtil.getAsFile(storage).getCanonicalPath();
        } catch (IOException ex) {
            // TRANSLATOR: Error condition: Could not initialize a search index.
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine."), ex);
        }
    }

    /**
     * Generate an index to use, telling the job about progress as you go.
     * 
     * @throws BookException
     *             If we fail to read the index files
     */
    public LuceneIndex(Book book, URI storage, boolean create) throws BookException {
        assert create;

        this.book = book;
        File finalPath = null;
        try {
            finalPath = NetUtil.getAsFile(storage);
            this.path = finalPath.getCanonicalPath();
        } catch (IOException ex) {
            // TRANSLATOR: Error condition: Could not initialize a search index. Lucene is the name of the search technology being used.
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine."), ex);
        }

        // TRANSLATOR: Progress label indicating the start of indexing. {0} is a placeholder for the book's short name.
        String jobName = JSMsg.gettext("Creating index. Processing {0}", book.getInitials());
        Progress job = JobManager.createJob(jobName, Thread.currentThread());
        job.beginJob(jobName);

        IndexStatus finalStatus = IndexStatus.UNDONE;

        Analyzer analyzer = new LuceneAnalyzer(book);

        List<Key> errors = new ArrayList<Key>();
        File tempPath = new File(path + '.' + IndexStatus.CREATING.toString());

        try {
            synchronized (CREATING) {

                book.setIndexStatus(IndexStatus.CREATING);

                // An index is created by opening an IndexWriter with the create
                // argument set to true.
                // IndexWriter writer = new
                // IndexWriter(tempPath.getCanonicalPath(), analyzer, true);

                // Create the index in core.
                final RAMDirectory ramDir = new RAMDirectory();
                IndexWriter writer = new IndexWriter(ramDir, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

                generateSearchIndexImpl(job, errors, writer, book.getGlobalKeyList(), 0);

                // TRANSLATOR: Progress label for optimizing a search index. This may take a bit of time, so we have a label for it.
                job.setSectionName(JSMsg.gettext("Optimizing"));
                job.setWork(95);

                // Consolidate the index into the minimum number of files.
                // writer.optimize(); /* Optimize is done by addIndexes */
                writer.close();

                // Write the core index to disk.
                final Directory destination = FSDirectory.open(new File(tempPath.getCanonicalPath()));
                IndexWriter fsWriter = new IndexWriter(destination, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
                fsWriter.addIndexesNoOptimize(new Directory[] {
                    ramDir
                });
                fsWriter.optimize();
                fsWriter.close();

                // Free up the space used by the ram directory
                ramDir.close();

                job.setCancelable(false);
                if (!job.isFinished()) {
                    if (!tempPath.renameTo(finalPath)) {
                        // TRANSLATOR: The search index could not be moved to it's final location.
                        throw new BookException(JSMsg.gettext("Installation failed."));
                    }
                }

                if (finalPath.exists()) {
                    finalStatus = IndexStatus.DONE;
                }

                if (!errors.isEmpty()) {
                    StringBuilder buf = new StringBuilder();
                    for (Key error : errors) {
                        buf.append(error);
                        buf.append('\n');
                    }
                    // TRANSLATOR: It is likely that one or more verses could not be indexed due to errors in those verses.
                    // This message gives a listing of them to the user.
                    Reporter.informUser(this, JSMsg.gettext("The following verses have errors and could not be indexed\n{0}", buf));
                }

            }
        } catch (IOException ex) {
            job.cancel();
            // TRANSLATOR: Common error condition: Some error happened while creating a search index.
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine."), ex);
        } finally {
            book.setIndexStatus(finalStatus);
            job.done();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.index.search.Index#findWord(java.lang.String)
     */
    public Key find(String search) throws BookException {
        checkActive();
        String v11nName = book.getBookMetaData().getProperty("Versification").toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);

        SearchModifier modifier = getSearchModifier();
        Key results = null;

        if (search != null) {
            try {
                Analyzer analyzer = new LuceneAnalyzer(book);

                QueryParser parser = new QueryParser(Version.LUCENE_29, LuceneIndex.FIELD_BODY, analyzer);
                parser.setAllowLeadingWildcard(true);
                Query query = parser.parse(search);
                log.info("ParsedQuery-" + query.toString());

                // For ranking we use a PassageTally
                if (modifier != null && modifier.isRanked()) {
                    PassageTally tally = new PassageTally(v11n);
                    tally.raiseEventSuppresion();
                    tally.raiseNormalizeProtection();
                    results = tally;

                    TopScoreDocCollector collector = TopScoreDocCollector.create(modifier.getMaxResults(), false);
                    searcher.search(query, collector);
                    tally.setTotal(collector.getTotalHits());
                    ScoreDoc[] hits = collector.topDocs().scoreDocs;
                    for (int i = 0; i < hits.length; i++) {
                        int docId = hits[i].doc;
                        Document doc = searcher.doc(docId);
                        Key key = VerseFactory.fromString(v11n, doc.get(LuceneIndex.FIELD_KEY));
                        // PassageTally understands a score of 0 as the verse
                        // not participating
                        int score = (int) (hits[i].score * 100 + 1);
                        tally.add(key, score);
                    }
                    tally.lowerNormalizeProtection();
                    tally.lowerEventSuppresionAndTest();
                } else {
                    results = book.createEmptyKeyList();
                    // If we have an abstract passage,
                    // make sure it does not try to fire change events.
                    AbstractPassage passage = null;
                    if (results instanceof AbstractPassage) {
                        passage = (AbstractPassage) results;
                        passage.raiseEventSuppresion();
                        passage.raiseNormalizeProtection();
                    }
                    searcher.search(query, new VerseCollector(v11n, searcher, results));
                    if (passage != null) {
                        passage.lowerNormalizeProtection();
                        passage.lowerEventSuppresionAndTest();
                    }
                }
            } catch (IOException e) {
                // The VerseCollector may throw IOExceptions that merely wrap a
                // NoSuchVerseException
                Throwable cause = e.getCause();
                if (cause instanceof NoSuchVerseException) {
                    // TRANSLATOR: Error condition: An unexpected error happened that caused search to fail.
                    throw new BookException(JSMsg.gettext("Search failed."), cause);
                }

                // TRANSLATOR: Error condition: An unexpected error happened that caused search to fail.
                throw new BookException(JSMsg.gettext("Search failed."), e);
            } catch (NoSuchVerseException e) {
                // TRANSLATOR: Error condition: An unexpected error happened that caused search to fail.
                throw new BookException(JSMsg.gettext("Search failed."), e);
            } catch (ParseException e) {
                // TRANSLATOR: Error condition: An unexpected error happened that caused search to fail.
                throw new BookException(JSMsg.gettext("Search failed."), e);
            } finally {
                Activator.deactivate(this);
            }
        }

        if (results == null) {
            if (modifier != null && modifier.isRanked()) {
                results = new PassageTally(v11n);
            } else {
                results = book.createEmptyKeyList();
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.index.search.Index#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException {
        return book.getKey(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#activate(org.crosswire.common
     * .activate.Lock)
     */
    public final void activate(Lock lock) {
        try {
            directory = FSDirectory.open(new File(path));
            searcher = new IndexSearcher(directory, true);
        } catch (IOException ex) {
            log.warn("second load failure", ex);
        }

        active = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common
     * .activate.Lock)
     */
    public final void deactivate(Lock lock) {
        try {
            searcher.close();
            directory.close();
        } catch (IOException ex) {
            Reporter.informUser(this, ex);
        } finally {
            searcher = null;
            directory = null;
        }

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive() {
        if (!active) {
            Activator.activate(this);
        }
    }

    /**
     * Dig down into a Key indexing as we go.
     */
    private void generateSearchIndexImpl(Progress job, List<Key> errors, IndexWriter writer, Key key, int count) throws BookException, IOException {
        String v11nName = book.getBookMetaData().getProperty("Versification").toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);
        boolean hasStrongs = book.getBookMetaData().hasFeature(FeatureType.STRONGS_NUMBERS);
        boolean hasXRefs = book.getBookMetaData().hasFeature(FeatureType.SCRIPTURE_REFERENCES);
        boolean hasNotes = book.getBookMetaData().hasFeature(FeatureType.FOOTNOTES);
        boolean hasHeadings = book.getBookMetaData().hasFeature(FeatureType.HEADINGS);

        String oldRootName = "";
        int percent = 0;
        String rootName = "";
        BookData data = null;
        Element osis = null;

        // Set up for reuse.
        Document doc = new Document();
        Field keyField = new Field(FIELD_KEY, "", Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO);
        Field bodyField = new Field(FIELD_BODY, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field strongField = new Field(FIELD_STRONG, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field xrefField = new Field(FIELD_XREF, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field noteField = new Field(FIELD_NOTE, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field headingField = new Field(FIELD_HEADING, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);

        int size = key.getCardinality();
        int subCount = count;
        for (Key subkey : key) {
            if (subkey.canHaveChildren()) {
                generateSearchIndexImpl(job, errors, writer, subkey, subCount);
            } else {
                data = new BookData(book, subkey);
                osis = null;

                try {
                    osis = data.getOsisFragment();
                } catch (BookException e) {
                    errors.add(subkey);
                    continue;
                }

                // Remove all fields from the document
                doc.getFields().clear();

                // Do the actual indexing
                // Always add the key
                keyField.setValue(subkey.getOsisRef());
                doc.add(keyField);

                addField(doc, bodyField, OSISUtil.getCanonicalText(osis));

                if (hasStrongs) {
                    addField(doc, strongField, OSISUtil.getStrongsNumbers(osis));
                }

                if (hasXRefs) {
                    addField(doc, xrefField, OSISUtil.getReferences(v11n, osis));
                }

                if (hasNotes) {
                    addField(doc, noteField, OSISUtil.getNotes(osis));
                }

                if (hasHeadings) {
                    addField(doc, headingField, OSISUtil.getHeadings(osis));
                }

                // Add the document if we added more than just the key.
                if (doc.getFields().size() > 1) {
                    writer.addDocument(doc);
                }

                // report progress
                rootName = subkey.getRootName();
                if (!rootName.equals(oldRootName)) {
                    oldRootName = rootName;
                    job.setSectionName(rootName);
                }

                subCount++;
                int oldPercent = percent;
                percent = 95 * subCount / size;

                if (oldPercent != percent) {
                    job.setWork(percent);
                }

                // This could take a long time ...
                Thread.yield();
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        }
    }

    private void addField(Document doc, Field field, String text) {
        if (text != null && text.length() > 0) {
            field.setValue(text);
            doc.add(field);
        }
    }

    /**
     * A synchronization lock point to prevent us from doing 2 index runs at a
     * time.
     */
    private static final Object CREATING = new Object();

    /**
     * Are we active
     */
    private boolean active;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneIndex.class);

    /**
     * The Book that we are indexing
     */
    protected Book book;

    /**
     * The location of this index
     */
    private String path;

    /**
     * The Lucene directory for the path.
     */
    protected Directory directory;

    /**
     * The Lucene search engine
     */
    protected Searcher searcher;
}
