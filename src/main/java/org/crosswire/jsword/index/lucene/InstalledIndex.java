package org.crosswire.jsword.index.lucene;

import java.io.IOException;
import java.net.URI;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.jsword.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A singleton that Reads and Maintains Installed Index Metadata (for e.g. version indexed on client machine) in properties file
 * If file does not exist on the client, it will be created
 *   File location: {WritableProjectDir}/JSword/lucene/org.crosswire.jsword.index.lucene.InstalledIndex.properties
 *
 *# todo do we need method reindexAllInstalledBooks() ? :  If this succeeds, update Installed.Index.DefaultVersion prop on the client computer
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public final class InstalledIndex {
    public static final String INSTALLED_INDEX_DEFAULT_VERSION = "Installed.Index.DefaultVersion";
    //Book's property key format Installed.Index.Version.Book.Initial[module-version]
    public static final String PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE = "Installed.Index.Version.Book.";
    public static final float DEFAULT_INSTALLED_INDEX_VERSION_IF_METADATA_FILE_NOT_PRESENT =  IndexMetadata.INDEX_VERSION_1_2;   //todo change this value on lucene upgrade

    /**
     * All access through this single instance.
     *
     * @return the singleton instance
     */
    public static InstalledIndex instance() {
        return myInstance;
    }

    public float getInstalledIndexDefaultVersion() {
        float toReturn = DEFAULT_INSTALLED_INDEX_VERSION_IF_METADATA_FILE_NOT_PRESENT;
        String value = props.get(INSTALLED_INDEX_DEFAULT_VERSION);
        if (value != null) {
            toReturn = Float.parseFloat(value);
        }

        return toReturn;
    }

    public float getInstalledIndexVersion(Book b) {
        if (b == null) {
            return getInstalledIndexDefaultVersion();
        }

        //e.g. look for Installed.Index.Version.Book.ESV[1.0.1] , else use Installed.Index.DefaultVersion
        String value = props.get(PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                props.get(INSTALLED_INDEX_DEFAULT_VERSION));

        if (value == null) {
            return DEFAULT_INSTALLED_INDEX_VERSION_IF_METADATA_FILE_NOT_PRESENT;
        }
        return Float.parseFloat(value);
    }

    //Store the LatestIndexVersion for a book in the metadata file : typically after a new index creation
    public void storeLatestVersionAsInstalledIndexMetadata(Book b) throws IOException {

        synchronized (writeLock) {

            props.put(PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                    String.valueOf(IndexMetadata.instance().getLatestIndexVersion(b)));

            NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
        }
    }



    public URI getPropertyFileURI() {
        return CWProject.instance().getWritableURI(LuceneIndexManager.DIR_LUCENE + "/" + getClass().getName(), FileUtil.EXTENSION_PROPERTIES);
    }

    protected void storeInstalledIndexMetadata() throws IOException {
        synchronized (writeLock) {
            NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
        }
    }

    private InstalledIndex() {
        URI propURI = getPropertyFileURI();
        try {
            //props = ResourceUtil.getProperties(getClass());//,

            if (NetUtil.canRead(propURI)) {
                props = NetUtil.loadProperties(propURI);
            }

            /** **** Initial values if prop file non--existent ****** **/
            if (props == null || props.size() == 0) {
                props = new PropertyMap();
                props.put(INSTALLED_INDEX_DEFAULT_VERSION, String.valueOf(DEFAULT_INSTALLED_INDEX_VERSION_IF_METADATA_FILE_NOT_PRESENT));

                storeInstalledIndexMetadata();
            }

        } catch (IOException e) {
            log.error("Property file read error: " + propURI.toString(), e);
        }
    }

    /** Use this method to add/update custom property in the metadata file.
     * Note: If all the installed books indices have been upgraded/downloaded, client can pass in property
     *  InstalledIndex.INSTALLED_INDEX_DEFAULT_VERSION = <VersionToStore>, for e.g for client managed downloadable index
     * @param updateProps
     * @throws IOException
     */
    public void storeInstalledIndexMetadata(PropertyMap updateProps) throws IOException {
        synchronized (writeLock) {
            props.putAll(updateProps);
            NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
        }
    }

    //Store a client specified IndexVersion in the metadata file: Can be used for client managed downloadable index
    public void storeInstalledIndexMetadata(Book b, String installedIndexVersionToStore) throws IOException {
        synchronized (writeLock) {
            props.put(PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                    installedIndexVersionToStore);
            NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
        }
    }

    public void removeFromInstalledIndexMetadata(Book b) throws IOException {

        synchronized (writeLock) {

            props.remove(PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()));

            NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
        }
    }

    private Object writeLock = new Object();
    private static String metadataFileComment = "Search index properties that stay persistent on clients computer. Used during index upgrades."
           + "\nContains Default index version, used for all searchable books, if book specific over-ride is not found.\n"
           + "JSword adds a Book specific installed index version over-ride property, after an index creation. ";

    private static final Logger log = LoggerFactory.getLogger(InstalledIndex.class);
    private static InstalledIndex myInstance = new InstalledIndex();
    private PropertyMap props;
}
