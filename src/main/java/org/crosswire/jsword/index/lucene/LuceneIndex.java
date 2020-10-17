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
package org.crosswire.jsword.index.lucene;

import java.io.Closeable;
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
import org.apache.lucene.util.Version;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.index.AbstractIndex;
import org.crosswire.jsword.index.IndexPolicy;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.lucene.analysis.LuceneAnalyzer;
import org.crosswire.jsword.index.search.SearchModifier;
import org.crosswire.jsword.passage.AbstractPassage;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement the SearchEngine using Lucene as the search engine.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class LuceneIndex extends AbstractIndex implements Closeable {
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
     * Combines the strong numbers with the morphology field
     */
    public static final String FIELD_MORPHOLOGY = "morph";

    /**
     * Combines the strong numbers with the morphology field
     */
    public static final String FIELD_INTRO = "intro";

    /**
     * An estimate of the percent of time spent indexing.
     * The remaining time, if any, is spent doing cleanup.
     */
    private static final int WORK_ESTIMATE = 98;

    /**
     * Read an existing index and use it.
     * 
     * @param book the book
     * @param storage 
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
        initDirectoryAndSearcher();
    }

    /**
     * Generate an index to use, telling the job about progress as you go.
     * 
     * @param book the book
     * @param storage 
     * @param policy 
     * @throws BookException
     *             If we fail to read the index files
     */
    public LuceneIndex(Book book, URI storage, IndexPolicy policy) throws BookException {

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
        Progress job = JobManager.createJob(String.format(Progress.CREATE_INDEX, book.getInitials()), jobName, null);
        job.beginJob(jobName);

        IndexStatus finalStatus = IndexStatus.UNDONE;

        List<Key> errors = new ArrayList<Key>();
        // Build to another location and rename in the end.
        File tempPath = new File(path + '.' + IndexStatus.CREATING.toString());

        // Ensure that the temp path is gone
        // It is not good for it to have been leftover from before.
        if (tempPath.exists()) {
            FileUtil.delete(tempPath);
        }

        // Lock on metadata to allow creation of multiple indexes, so long as they are on different books.
        // Otherwise lock on a single object to make this serial
        Object mutex = policy.isSerial() ? CREATING : book.getBookMetaData();
        synchronized (mutex) {

            try {
                // When misconfigured, this can throw errors.
                Analyzer analyzer = new LuceneAnalyzer(book);


                book.setIndexStatus(IndexStatus.CREATING);

                IndexWriter writer = null;
                try {
                    // Write the core index to disk.
                    final Directory destination = FSDirectory.open(new File(tempPath.getCanonicalPath()));
                    writer = new IndexWriter(destination, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
                    writer.setRAMBufferSizeMB(policy.getRAMBufferSize());

                    generateSearchIndexImpl(job, errors, writer, book.getGlobalKeyList(), 0, policy);

                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }

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
                initDirectoryAndSearcher();
            } catch (IOException ex) {
                job.cancel();
                // TRANSLATOR: Common error condition: Some error happened while creating a search index.
                throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine."), ex);
            } finally {
                book.setIndexStatus(finalStatus);
                job.done();
                // Ensure that the temp path is gone - errors can leave it there and cause further problems.
                if (tempPath.exists()) {
                    FileUtil.delete(tempPath);
                }
            }
        }
    }

    /**
     * Initializes the directory and searcher.
     */
    private void initDirectoryAndSearcher() {
        try {
            directory = FSDirectory.open(new File(path));
            searcher = new IndexSearcher(directory, true);
        } catch (IOException ex) {
            log.warn("second load failure", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.Index#find(java.lang.String)
     */
    public Key find(String search) throws BookException {
        String v11nName = book.getBookMetaData().getProperty("Versification").toString();
        Versification v11n = Versifications.instance().getVersification(v11nName);

        SearchModifier modifier = getSearchModifier();
        Key results = null;

        if (search != null) {
            Throwable theCause = null;
            try {
                Analyzer analyzer = new LuceneAnalyzer(book);

                QueryParser parser = new QueryParser(Version.LUCENE_29, LuceneIndex.FIELD_BODY, analyzer);
                parser.setAllowLeadingWildcard(true);
                Query query = parser.parse(search);
                log.info("ParsedQuery- {}", query.toString());

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
                    tally.lowerEventSuppressionAndTest();
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
                        passage.lowerEventSuppressionAndTest();
                    }
                }
            } catch (IOException e) {
                // The VerseCollector may throw IOExceptions that merely wrap a NoSuchVerseException
                Throwable cause = e.getCause();
                theCause = cause instanceof NoSuchVerseException ? cause : e;
            } catch (NoSuchVerseException e) {
                theCause = e;
            } catch (ParseException e) {
                theCause = e;
            }

            if (theCause != null) {
                // TRANSLATOR: Error condition: An unexpected error happened that caused search to fail.
                throw new BookException(JSMsg.gettext("Search failed."), theCause);
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.Index#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException {
        return book.getKey(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.Index#close()
     */
    public final void close() {
        IOUtil.close(searcher);
        searcher = null;
        IOUtil.close(directory);
        directory = null;
    }

    /**
     * Dig down into a Key indexing as we go.
     * @param policy 
     */
    private void generateSearchIndexImpl(Progress job, List<Key> errors, IndexWriter writer, Key key, int count, IndexPolicy policy) throws BookException, IOException {
        String v11nName = null;
        if (book.getBookMetaData().getProperty("Versification") != null) {
            v11nName = book.getBookMetaData().getProperty("Versification").toString();
        }
        Versification v11n = Versifications.instance().getVersification(v11nName);
        boolean includeStrongs = book.getBookMetaData().hasFeature(FeatureType.STRONGS_NUMBERS) && policy.isStrongsIndexed();
        boolean includeXrefs = book.getBookMetaData().hasFeature(FeatureType.SCRIPTURE_REFERENCES) && policy.isXrefIndexed();
        boolean includeNotes = book.getBookMetaData().hasFeature(FeatureType.FOOTNOTES) && policy.isNoteIndexed();
        boolean includeHeadings = book.getBookMetaData().hasFeature(FeatureType.HEADINGS) && policy.isTitleIndexed();
        boolean includeMorphology = book.getBookMetaData().hasFeature(FeatureType.MORPHOLOGY) && policy.isMorphIndexed();

        String oldRootName = "";
        int percent = 0;
        String rootName = "";
        BookData data = null;
        Element osis = null;

        // Set up for reuse.
        Document doc = new Document();
        Field keyField = new Field(FIELD_KEY, "", Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO);
        Field bodyField = new Field(FIELD_BODY, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field introField = new Field(FIELD_INTRO, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field strongField = new Field(FIELD_STRONG, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
        Field xrefField = new Field(FIELD_XREF, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field noteField = new Field(FIELD_NOTE, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field headingField = new Field(FIELD_HEADING, "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field morphologyField  = new Field(FIELD_MORPHOLOGY , "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);

        int size = key.getCardinality();
        int subCount = count;
        log.debug("Number of keys = {}", Integer.toString(size));
        for (Key subkey : key) {
            // Bibles and verse based commentaries don't have keys with children.
            // However, tree keyed Books do. So we only index the leaf keys.
            // FIXME(DMS): Should not use recursion!!!!
            if (subkey.canHaveChildren()) {
                generateSearchIndexImpl(job, errors, writer, subkey, subCount, policy);
                continue;
            }

            data = new BookData(book, subkey);
            osis = null;

            try {
                osis = data.getOsisFragment(false);
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

            if (subkey instanceof Verse && ((Verse) subkey).getVerse() == 0) {
                addField(doc, introField, OSISUtil.getCanonicalText(osis));
            } else {
                addField(doc, bodyField, OSISUtil.getCanonicalText(osis));
            }

            if (includeStrongs) {
                addField(doc, strongField, OSISUtil.getStrongsNumbers(osis));
            }

            if (includeXrefs) {
                // We pass book and key because the xref may not be valid and it needs to be reported.
                addField(doc, xrefField, OSISUtil.getReferences(this.book, subkey, v11n, osis));
            }

            if (includeNotes) {
                addField(doc, noteField, OSISUtil.getNotes(osis));
            }

            if (includeHeadings) {
                String heading = OSISUtil.getHeadings(osis);
                addField(doc, headingField, heading);
            }

            if (includeMorphology) {
                addField(doc, morphologyField, OSISUtil.getMorphologiesWithStrong(osis));
            }

            // Add the document if we added more than just the key.
            if (doc.getFields().size() > 1) {
                writer.addDocument(doc);
            }

            // report progress
            rootName = subkey.getRootName();
            if (!rootName.equals(oldRootName)) {
                oldRootName = rootName;
                // Note, this does not cause progress to be updated
                // It will show up the next time progress is updated.
                job.setSectionName(rootName);
            }

            subCount++;
            int oldPercent = percent;
            percent = WORK_ESTIMATE * subCount / size;

            // Only send out a max of 95 progress updates
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

    /**
     * Add the text to the Field and put the Field in the document,
     * ignoring null and empty text.
     * 
     * @param doc The Document to which the Field should be added
     * @param field The Field to add
     * @param text The text for the field
     */
    private void addField(Document doc, Field field, String text) {
        if (text != null && text.length() > 0) {
            field.setValue(text);
            doc.add(field);
        }
    }

    /**
     * Could be null if the index has been closed down. This is helpful to third party applications which wish to have greater control over 
     * the underlying Lucene functionality.
     * 
     * Note: by using this method, you need to ensure you don't close the searcher while it is being used.
     * See {@link org.crosswire.jsword.index.IndexManager#closeAllIndexes()} for more information
     * @return the searcher
     */
    public Searcher getSearcher() {
        return searcher;
    }

    /**
     * The Book that we are indexing
     */
    private Book book;

    /**
     * The location of this index
     */
    private String path;

    /**
     * The Lucene directory for the path.
     */
    private Directory directory;

    /**
     * The Lucene search engine
     */
    private Searcher searcher;

    /**
     * A synchronization lock point to prevent us from doing 2 index runs at a
     * time.
     */
    private static final Object CREATING = new Object();

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(LuceneIndex.class);
}
