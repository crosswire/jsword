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
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

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
import org.jdom.Document;

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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class SwordBookMetaData extends AbstractBookMetaData {
    /**
     * Loads a sword config from a given File.
     * 
     * @param file
     * @param internal
     * @throws IOException
     * @throws MissingDataFilesException indicates missing data files
     */
    public SwordBookMetaData(File file, String internal, URI bookRootPath) throws IOException, MissingDataFilesException {
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    @Override
    public boolean isQuestionable() {
        return cet.isQuestionable();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isSupported()
     */
    @Override
    public boolean isSupported() {
        return cet.isSupported() && cet.getBookType().isSupported(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isEnciphered()
     */
    @Override
    public boolean isEnciphered() {
        return cet.isEnciphered();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isLocked()
     */
    @Override
    public boolean isLocked() {
        return cet.isLocked();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#unlock(java.lang.String)
     */
    @Override
    public boolean unlock(String unlockKey) {
        return cet.unlock(unlockKey);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#getUnlockKey()
     */
    @Override
    public String getUnlockKey() {
        return cet.getUnlockKey();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName() {
        return (String) getProperty(ConfigEntryType.DESCRIPTION);
    }

    /**
     * Returns the Charset of the book based on the encoding attribute
     * 
     * @return the charset of the book.
     */
    public String getBookCharset() {
        return ENCODING_JAVA.get(getProperty(ConfigEntryType.ENCODING));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#getKeyType()
     */
    @Override
    public KeyType getKeyType() {
        BookType bookType = getBookType();
        if (bookType == null) {
            return null;
        }
        return bookType.getKeyType();
    }

    /**
     * Returns the Book Type.
     */
    public BookType getBookType() {
        return cet.getBookType();
    }

    /**
     * Returns the sourceType.
     */
    public Filter getFilter() {
        String sourcetype = (String) getProperty(ConfigEntryType.SOURCE_TYPE);
        return FilterFactory.getFilter(sourcetype);
    }

    /**
     * @return Returns the relative path of the book's conf.
     */
    public String getConfPath() {
        return SwordConstants.DIR_CONF + '/' + getInitials().toLowerCase(Locale.ENGLISH) + SwordConstants.EXTENSION_CONF;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#setLibrary(java.net.URI)
     */
    @Override
    public void setLibrary(URI library) throws MissingDataFilesException {
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
            if(isDirectoryPath) {
                // TRANSLATOR: This indicates that the Book is only partially installed.
                throw new MissingDataFilesException(JSMsg.gettext("The book is missing its data files", cet.getValue(ConfigEntryType.INITIALS)));
            }
            
            //not a directory path
            //try appending .dat on the end to see if we have a file, if not, then 
            if(!new File(datapath + ".dat").exists()) {
             // TRANSLATOR: This indicates that the Book is only partially installed.
                throw new MissingDataFilesException(JSMsg.gettext("The book is missing its data files", cet.getValue(ConfigEntryType.INITIALS)));                
            }
            
            //then we have a module that has a prefix
                
            // Shorten it by one segment and test again.
            lastSlash = datapath.lastIndexOf('/');
            datapath = datapath.substring(0, lastSlash);
            location = NetUtil.lengthenURI(library, datapath);
        }

        cet.add(ConfigEntryType.LOCATION_URL, location.toString());
        super.setLocation(location);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCategory()
     */
    public BookCategory getBookCategory() {
        if (type == null) {
            type = (BookCategory) getProperty(ConfigEntryType.CATEGORY);
            if (type == BookCategory.OTHER) {
                type = getBookType().getBookCategory();
            }
        }
        return type;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#toOSIS()
     */
    @Override
    public Document toOSIS() {
        return new Document(cet.toOSIS());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials() {
        return (String) getProperty(ConfigEntryType.INITIALS);
    }

    /**
     * Get the string value for the property or null if it is not defined. It is
     * assumed that all properties gotten with this method are single line.
     * 
     * @param entry
     *            the ConfigEntryType
     * @return the property or null
     */
    public Object getProperty(ConfigEntryType entry) {
        return cet.getValue(entry);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight() {
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
        if (cet.match(ConfigEntryType.FEATURE, feature.toString())) {
            return true;
        }
        // Many "features" are GlobalOptionFilters, which in the Sword C++ API
        // indicate a class to use for filtering.
        // These mostly have the source type prepended to the feature
        StringBuilder buffer = new StringBuilder((String) getProperty(ConfigEntryType.SOURCE_TYPE));
        buffer.append(feature);
        if (cet.match(ConfigEntryType.GLOBAL_OPTION_FILTER, buffer.toString())) {
            return true;
        }
        // But some do not
        return cet.match(ConfigEntryType.GLOBAL_OPTION_FILTER, feature.toString());
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

    private ConfigEntryTable cet;
    private BookCategory type;
}
