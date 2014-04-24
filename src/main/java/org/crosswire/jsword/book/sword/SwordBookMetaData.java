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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.crosswire.common.util.Language;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.KeyType;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.jdom2.Document;

/**
 * A utility class for loading and representing Sword book configs.
 *
 * <p>
 * Config file format. See also: <a href=
 * "http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
 *
 * <p>
 * The contents of the About field are in rtf.
 * <p>
 * \ is used as a continuation line.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @author Jacky Cheung
 * @author DM Smith
 */
/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public final class SwordBookMetaData extends AbstractBookMetaData {
    /**
     * Loads a sword config from a given File.
     *
     *
     * @param sbmd the parent metadata object, could be null
     * @param file
     * @param internal
     * @throws IOException
     * @throws MissingDataFilesException indicates missing data files
     */
    public SwordBookMetaData(SwordBookMetaData sbmd, File file, String internal, URI bookRootPath) throws IOException, MissingDataFilesException {
        this.sbmd = sbmd;
        cet = new ConfigEntryTable(internal);
        cet.load(file);

        setLibrary(bookRootPath);
        buildProperties();
    }

    /**
     * Loads a sword config from a buffer.
     *
     * @param buffer
     * @param internal
     * @throws IOException
     */
    public SwordBookMetaData(byte[] buffer, String internal) throws IOException {
        cet = new ConfigEntryTable(internal);
        cet.load(buffer);
        buildProperties();
    }

    /*  Cannot be overriden by jsword/frontends
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    @Override
    public boolean isQuestionable() {
        //some parameters don't support overrides
        if(this.sbmd != null) {
            return this.sbmd.isQuestionable();
        }
        return this.cet.isQuestionable();
    }


    /* If the top most entry reports the module is supported, then it is supported
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isSupported()
     */
    @Override
    public boolean isSupported() {
        if(this.sbmd != null) {
            return this.sbmd.isSupported();
        }

        return this.cet.isSupported();
    }

    /*
     * If all configurations all enciphered, then this module is enciphered
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isEnciphered()
     */
    @Override
    public boolean isEnciphered() {
        if(this.sbmd != null) {
            return cet.isEnciphered() && this.sbmd.isEnciphered();
        }
        return cet.isEnciphered();
    }

    /*
     * If all configurations are locked, then it is locked
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isLocked()
     */
    @Override
    public boolean isLocked() {
        if(this.sbmd != null) {
            return cet.isLocked() && this.sbmd.isLocked();
        }
        return cet.isLocked();
    }

    /* Unlock always happens at the top-level (frontend/jsword/sword)
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#unlock(java.lang.String)
     */
    @Override
    public boolean unlock(String unlockKey) {
        //some operations never delegate - this will either write to the frontend conf, or the jsword conf
        return cet.unlock(unlockKey);
    }

    /*
     * Can be overriden by frontend/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#getUnlockKey()
     */
    @Override
    public String getUnlockKey() {
        String unlockKey = cet.getUnlockKey();
        if(unlockKey == null && this.sbmd != null) {
            return sbmd.getUnlockKey();
        }
        return unlockKey;
    }

    /*
     * Can be overriden by frontend/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName() {
        //we allow overriding here
        String name = (String) getProperty(ConfigEntryType.DESCRIPTION);
        if(name != null) {
            return name;
        }

        if(this.sbmd != null) {
            return sbmd.getName();
        }
        return name;
    }

    /**
     * Returns the Charset of the book based on the encoding attribute.
     * This cannot be override
     *
     * @return the charset of the book.
     */
    public String getBookCharset() {
        if(this.sbmd != null) {
            return this.sbmd.getBookCharset();
        }
        return ENCODING_JAVA.get(getProperty(ConfigEntryType.ENCODING));
    }

    /* This value cannot be overriden by frontends/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#getKeyType()
     */
    @Override
    public KeyType getKeyType() {
        if(this.sbmd != null) {
            return this.sbmd.getKeyType();
        }

        BookType bookType = getBookType();
        if (bookType == null) {
            return null;
        }
        return bookType.getKeyType();
    }

    /**
     * This value cannot be overriden by frontend/jsword
     * Returns the Book Type.
     */
    public BookType getBookType() {
        if(this.sbmd != null) {
            return this.sbmd.getBookType();
        }

        return cet.getBookType();
    }

    /**
     * This value cannot be overriden by frontend/jsword
     * Returns the Filter based upon the SourceType.
     */
    public Filter getFilter() {
        if(this.sbmd != null) {
            return this.sbmd.getFilter();
        }

        String sourcetype = (String) getProperty(ConfigEntryType.SOURCE_TYPE);
        return FilterFactory.getFilter(sourcetype);
    }

    /**
     * To maintain backwards compatibility, this always returns the Sword conf file
     * Get the conf file for this SwordMetaData.
     *
     * @return Returns the conf file or null if loaded from a byte buffer.
     */
    public File getConfigFile() {
        if(this.sbmd != null) {
            return this.sbmd.getConfigFile();
        }

        return cet.getConfigFile();
    }

    /* This method sets the library on the sword conf file.
     *
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#setLibrary(java.net.URI)
     */
    @Override
    public void setLibrary(URI library) throws MissingDataFilesException {
        //always sets on the parent first
        if(this.sbmd != null) {
            this.sbmd.setLibrary(library);
            return;
        }


        // Ignore it if it is not supported.
        if (!isSupported()) {
            return;
        }

        cet.add(ConfigEntryType.LIBRARY_URL, library.toString());
        super.setLibrary(library);

        // Previously, all DATA_PATH entries end in / to indicate dirs
        // or not to indicate file prefixes.
        // This is no longer true.
        // Now we need to test the file/url to see if it exists and is a directory.
        String datapath = (String) getProperty(ConfigEntryType.DATA_PATH);
        int lastSlash = datapath.lastIndexOf('/');

        // There were modules that did not have a valid datapath.
        // This should not be necessary
        if (lastSlash == -1) {
            return;
        }

        // DataPath typically ends in a '/' to indicate a directory.
        // If so remove it.
        boolean isDirectoryPath = false;
        if (lastSlash == datapath.length() - 1) {
            isDirectoryPath = true;
            datapath = datapath.substring(0, lastSlash);
        }

        URI location = NetUtil.lengthenURI(library, datapath);
        File bookDir = new File(location.getPath());
        // For some modules, the last element of the DataPath
        // is a prefix for file names.
        if (!bookDir.isDirectory()) {
            if (isDirectoryPath) {
                // TRANSLATOR: This indicates that the Book is only partially installed.
                throw new MissingDataFilesException(JSMsg.gettext("The book is missing its data files", cet.getValue(ConfigEntryType.INITIALS)));
            }

            // not a directory path
            // try appending .dat on the end to see if we have a file, if not, then 
            if (!new File(location.getPath() + ".dat").exists()) {
             // TRANSLATOR: This indicates that the Book is only partially installed.
                throw new MissingDataFilesException(JSMsg.gettext("The book {0} is missing its data files", cet.getValue(ConfigEntryType.INITIALS)));
            }

            // then we have a module that has a prefix
            // Shorten it by one segment and test again.
            lastSlash = datapath.lastIndexOf('/');
            datapath = datapath.substring(0, lastSlash);
            location = NetUtil.lengthenURI(library, datapath);
        }

        cet.add(ConfigEntryType.LOCATION_URL, location.toString());
        super.setLocation(location);
    }

    /* Cannot be overriden by a frontend/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCategory()
     */
    public BookCategory getBookCategory() {
        if(this.sbmd != null) {
            return this.sbmd.getBookCategory();
        }


        if (type == null) {
            type = (BookCategory) getProperty(ConfigEntryType.CATEGORY);
            if (type == BookCategory.OTHER) {
                BookType bookType = getBookType();
                if(bookType == null) {
                    return null;
                }
                type = bookType.getBookCategory();
            }
        }
        return type;
    }

    /* Cannot be overriden by a frontend/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#toOSIS()
     */
    @Override
    public Document toOSIS() {
        if(this.sbmd != null) {
            return this.sbmd.toOSIS();
        }

        return new Document(cet.toOSIS());
    }

    /* Could be overriden by a frontend/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials() {
        String initials = (String) getProperty(ConfigEntryType.INITIALS);
        if(initials != null) {
            return initials;
        }

        if(this.sbmd != null) {
            return this.sbmd.getInitials();
        }

        return initials;
    }

    /**
     * Get the string value for the property or null if it is not defined. It is
     * assumed that all properties gotten with this method are single line.
     *
     * This is done first by examing the current value, and if null, delegating to the parent
     *
     * @param entry
     *            the ConfigEntryType
     * @return the property or null
     */
    public Object getProperty(ConfigEntryType entry) {
        Object value = cet.getValue(entry);
        if(value != null) {
            return value;
        }

        if(this.sbmd != null) {
            return this.sbmd.getBookCharset();
        }

        return value;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight() {
        if(this.sbmd != null) {
            return this.sbmd.isLeftToRight();
        }

        // This should return the dominate direction of the text, if it is BiDi,
        // then we have to guess.
        String dir = (String) getProperty(ConfigEntryType.DIRECTION);
        if (ConfigEntryType.DIRECTION_BIDI.equalsIgnoreCase(dir)) {
            // When BiDi, return the dominate direction based upon the Book's
            // Language not Direction
            Language lang = (Language) getProperty(ConfigEntryType.LANG);
            return lang.isLeftToRight();
        }

        return ConfigEntryType.DIRECTION_LTOR.equalsIgnoreCase(dir);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#hasFeature(org.crosswire.jsword.book.FeatureType)
     */
    @Override
    public boolean hasFeature(FeatureType feature) {
        String name = feature.toString();
        if (cet.match(ConfigEntryType.FEATURE, name)) {
            return true;
        }
        // Many "features" are GlobalOptionFilters, which in the Sword C++ API
        // indicate a class to use for filtering.
        // These mostly have the source type prepended to the feature
        StringBuilder buffer = new StringBuilder((String) getProperty(ConfigEntryType.SOURCE_TYPE));
        buffer.append(name);
        if (cet.match(ConfigEntryType.GLOBAL_OPTION_FILTER, buffer.toString())) {
            return true;
        }

        // Check for the alias prefixed by the source type
        String alias = feature.getAlias();
        buffer.setLength(0);
        buffer.append((String) getProperty(ConfigEntryType.SOURCE_TYPE));
        buffer.append(alias);

        // But some do not
        boolean matches = cet.match(ConfigEntryType.GLOBAL_OPTION_FILTER, name)
            || cet.match(ConfigEntryType.GLOBAL_OPTION_FILTER, buffer.toString());

        if(!matches && this.sbmd != null) {
            //look at the parent
            return this.sbmd.hasFeature(feature);
        }
        return matches;
    }

    private void buildProperties() {
        // merge entries into properties file
        for (ConfigEntryType key : cet.getKeys()) {
            Object value = cet.getValue(key);
            // value is null if the config entry was rejected.
            if (value == null) {
                continue;
            }
            if (value instanceof List<?>) {
                List<String> list = (List<String>) value;
                StringBuilder combined = new StringBuilder();
                boolean appendSeparator = false;
                for (String element : list) {
                    if (appendSeparator) {
                        combined.append('\n');
                    }
                    combined.append(element);
                    appendSeparator = true;
                }

                value = combined.toString();
            }

            putProperty(key.toString(), value);
        }
    }

    /**
     * Sword only recognizes two encodings for its modules: UTF-8 and LATIN1
     * Sword uses MS Windows cp1252 for Latin 1 not the standard. Arrgh! The
     * language strings need to be converted to Java charsets
     */
    private static final PropertyMap ENCODING_JAVA = new PropertyMap();
    static {
        ENCODING_JAVA.put("Latin-1", "WINDOWS-1252");
        ENCODING_JAVA.put("UTF-8", "UTF-8");
    }

    private SwordBookMetaData sbmd;
    private ConfigEntryTable cet;
    private BookCategory type;
}
