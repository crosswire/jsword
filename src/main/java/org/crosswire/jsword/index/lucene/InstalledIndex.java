package org.crosswire.jsword.index.lucene;

import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
* A singleton that Reads and Maintains Installed Index Metadata (for e.g. version indexed on client) from properties file
*
* @see gnu.lgpl.License for license details.<br>
*      The copyright to this program is held by it's authors.
* @author Sijo Cherian [sijocherian at yahoo dot com]
*/
public final class InstalledIndex {
    public static final String INSTALLED_INDEX_DEFAULT_VERSION = "Installed.Index.DefaultVersion";

    /**
     * All access through this single instance.
     *
     * @return the singleton instance
     */
    public static InstalledIndex instance() {
        return myInstance;
    }

    public float getInstalledIndexDefaultVersion() {
        float toReturn = IndexMetadata.INDEX_VERSION_1_2;   //defaultVersionIfNoMetadataFilePresent
        String value = props.get(INSTALLED_INDEX_DEFAULT_VERSION);
        if(value!=null)
            toReturn =Float.parseFloat(value );

        return toReturn;
    }

    public float getInstalledIndexVersion(Book b) {
        if(b==null) return getInstalledIndexDefaultVersion();
        //todo change this value on lucene upgrade
        float defaultVersionIfNoMetadataFilePresent = IndexMetadata.INDEX_VERSION_1_2;

        String value = props.get(IndexMetadata.PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE +b.getBookMetaData().getInitials(),
                props.get(INSTALLED_INDEX_DEFAULT_VERSION ) );

        if(value==null)
            return defaultVersionIfNoMetadataFilePresent;
        else
            return Float.parseFloat(value);
    }

    private InstalledIndex() {
        try {

            props = ResourceUtil.getProperties(getClass());//,
        } catch (IOException e) {
            log.error("Property file read error", e);
        }
    }



    private static final Logger log = LoggerFactory.getLogger(InstalledIndex.class);
    private static InstalledIndex myInstance = new InstalledIndex();
    private PropertyMap props;
}
