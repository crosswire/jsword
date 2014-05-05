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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.index.lucene;

import java.io.IOException;

import org.apache.lucene.util.Version;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A singleton that Reads and Maintains IndexMetadata from properties file All
 * version number in the properties file must be float.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public final class IndexMetadata {

    /* latest version on top*/
    public static final float INDEX_VERSION_1_2 = 1.2f;
    @Deprecated
    public static final float INDEX_VERSION_1_1 = 1.1f;

    public static final String LATEST_INDEX_VERSION = "Latest.Index.Version";
    public static final String LUCENE_VERSION = "Lucene.Version";
    public static final org.apache.lucene.util.Version LUCENE_IDXVERSION_FOR_INDEXING = Version.LUCENE_30; //todo For now

    public static final String PREFIX_LATEST_INDEX_VERSION_BOOK_OVERRIDE = "Latest.Index.Version.Book.";
    @Deprecated
    public static final String INDEX_VERSION = "Installed.Index.Version";
    /**
     * All access to IndexMetadata is through this single instance.
     * 
     * @return the singleton instance
     */
    public static IndexMetadata instance() {
        return myInstance;
    }

    /**
     * default Installed IndexVersion
    @deprecated see InstalledIndex.java
     */
    public float getInstalledIndexVersion() {
        String value = props.get(INDEX_VERSION, "1.1"); //todo At some point default should be 1.2
        return Float.parseFloat(value);
    }


    //Default Latest IndexVersion : Default version number of Latest indexing schema: PerBook index version must be equal or greater than this
    public float getLatestIndexVersion() {
        String value = props.get(LATEST_INDEX_VERSION, "1.2");
        return Float.parseFloat(value);
    }
    public String getLatestIndexVersionStr() {
        String value = props.get(LATEST_INDEX_VERSION, "1.2");
        return value;
    }

    public float getLatestIndexVersion(Book b) {
        if(b==null) return getLatestIndexVersion();

        String value = props.get(PREFIX_LATEST_INDEX_VERSION_BOOK_OVERRIDE+IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                        props.get(LATEST_INDEX_VERSION) );
        return Float.parseFloat(value);
    }


    //used in property keys e.g.  Installed.Index.Version.Book.ESV[1.0.1]
    public static String getBookIdentifierPropSuffix(BookMetaData meta) {
        String moduleVer = null;
        if(meta.getProperty("Version") !=null)
            moduleVer = '['+ ((org.crosswire.common.util.Version)meta.getProperty("Version")).toString()+']';

        return
                meta.getInitials()+ moduleVer;

    }
    public float getLuceneVersion() {
        return Float.parseFloat(props.get(LUCENE_VERSION));
    }

    private IndexMetadata() {
        try {
            props = ResourceUtil.getProperties(getClass());
        } catch (IOException e) {
            log.error("Property file read error", e);
        }
    }

    //a index status summary in English
    public static String generateInstalledBooksIndexVersionReport() {
        StringBuilder toReturn= new StringBuilder();
        int installedBookCount=0, searchEnabledBookCount=0, reindexMandatoryBookCount=0;
        LuceneIndexManager indexManager = (LuceneIndexManager) IndexManagerFactory.getIndexManager();
        Books myBooks = Books.installed();
        toReturn.append("InstalledBooks:");
        for(Book insBook: myBooks.getBooks()) {
            installedBookCount++;
            toReturn.append("\n\t").append(insBook.getBookMetaData().getInitials()).append(": ");
            if(indexManager.isIndexed( insBook)) {
                searchEnabledBookCount++;
                toReturn.append("search enabled, ");
                if(indexManager.needsReindexing( insBook)) {
                    reindexMandatoryBookCount++;
                    toReturn.append("index outdated, ");
                }
            }

        }
        toReturn.append("\nSummary: installedBooks ").append(installedBookCount)
            .append(", searchEnabledBooks ").append(searchEnabledBookCount)
            .append(", booksWithOutdatedIndex ").append(reindexMandatoryBookCount)
            .append("\n");
        return toReturn.toString();
    }

    private static final Logger log = LoggerFactory.getLogger(IndexMetadata.class);
    private static IndexMetadata myInstance = new IndexMetadata();
    private PropertyMap props;
}
