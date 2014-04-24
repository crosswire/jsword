package org.crosswire.jsword.index.lucene;

import org.crosswire.common.util.*;
import org.crosswire.jsword.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
* A singleton that Reads and Maintains Installed Index Metadata (for e.g. version indexed on client machine) in properties file
*   File location: {WritableProjectDir}/JSword/lucene/InstalledIndex.properties
*
* @see gnu.lgpl.License for license details.<br>
*      The copyright to this program is held by it's authors.
* @author Sijo Cherian [sijocherian at yahoo dot com]
*/

/*
# todo where to implement method reindexAllInstalledBooks() ? :  If this succeeds, update Installed.Index.DefaultVersion prop on the client computer

 */
public final class InstalledIndex {
    public static final String INSTALLED_INDEX_DEFAULT_VERSION = "Installed.Index.DefaultVersion";
    public float defaultInstalledIndexVersionIfMetadataFileNotPresent =  IndexMetadata.INDEX_VERSION_1_2;   //todo change this value on lucene upgrade

    /**
     * All access through this single instance.
     *
     * @return the singleton instance
     */
    public static InstalledIndex instance() {
        return myInstance;
    }

    public float getInstalledIndexDefaultVersion() {
        float toReturn = defaultInstalledIndexVersionIfMetadataFileNotPresent;
        String value = props.get(INSTALLED_INDEX_DEFAULT_VERSION);
        if(value!=null)
            toReturn =Float.parseFloat(value );

        return toReturn;
    }

    public float getInstalledIndexVersion(Book b) {
        if(b==null) return getInstalledIndexDefaultVersion();

        //e.g. look for Installed.Index.Version.Book.ESV[1.0.1] , else use Installed.Index.DefaultVersion
        String value = props.get(IndexMetadata.PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE +IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                props.get(INSTALLED_INDEX_DEFAULT_VERSION ) );

        if(value==null)
            return defaultInstalledIndexVersionIfMetadataFileNotPresent;
        else
            return Float.parseFloat(value);
    }

    //Store the LatestIndexVersion for a book in the metadata file : typically after a new index creation
    public void storeLatestVersionAsInstalledIndexMetadata(Book b) throws IOException {

        synchronized (writeLock) {

            props.put(IndexMetadata.PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE +IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                    String.valueOf( IndexMetadata.instance().getLatestIndexVersion(b) ) );

            try {
                NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
            } catch (IOException e) {
                log.error("Failed to store InstalledIndex metadata ", e);
            }
        }
    }



    public URI getPropertyFileURI() {
        return CWProject.instance().getWritableURI(LuceneIndexManager.DIR_LUCENE+"/"+getClass().getName(), FileUtil.EXTENSION_PROPERTIES);
    }

    protected void storeInstalledIndexMetadata() throws IOException {


        try {
            synchronized (writeLock) {
                NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
            }
        } catch (IOException e) {
            log.error("Failed to store InstalledIndex metadata ", e);
        }
    }

    private InstalledIndex() {
        URI propURI= getPropertyFileURI();
        try {
            //props = ResourceUtil.getProperties(getClass());//,

            if(NetUtil.canRead(propURI) )
                props = NetUtil.loadProperties( propURI);

            /** **** Initial values if prop file non--existent ****** **/
            if (props==null || props.size() == 0) {
                props = new PropertyMap();
                props.put(INSTALLED_INDEX_DEFAULT_VERSION, String.valueOf(defaultInstalledIndexVersionIfMetadataFileNotPresent));

                storeInstalledIndexMetadata();
            }

        } catch (IOException e) {
            log.error("Property file read error: "+propURI.toString(), e);
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
            try {
                NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
            } catch (IOException e) {
                log.error("Failed to store InstalledIndex metadata ", e);
            }
        }
    }

    //Store a client specified IndexVersion in the metadata file: Can be used for client managed downloadable index
    public void storeInstalledIndexMetadata(Book b, String installedIndexVersionToStore) throws IOException {

        synchronized (writeLock) {

            props.put(IndexMetadata.PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE +IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()),
                    installedIndexVersionToStore );

            try {
                NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
            } catch (IOException e) {
                log.error("Failed to store InstalledIndex metadata ", e);
            }
        }
    }

    public void removeFromInstalledIndexMetadata(Book b) throws IOException {

        synchronized (writeLock) {

            props.remove(IndexMetadata.PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE +IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()) );

            try {
                NetUtil.storeProperties(props, getPropertyFileURI(), metadataFileComment);
            } catch (IOException e) {
                log.error("Failed to store removed Index metadata ", e);
            }
        }
    }

    private Object writeLock = new Object();
    private static String metadataFileComment = "Properties that stay persistent on clients computer between upgrades." +
            "\n#Contains Default index version for all books that are currently indexed\n" +
            "#Also specifies Book specific installed index version over-ride. ";

    private static final Logger log = LoggerFactory.getLogger(InstalledIndex.class);
    private static InstalledIndex myInstance = new InstalledIndex();
    private PropertyMap props=null;
}
