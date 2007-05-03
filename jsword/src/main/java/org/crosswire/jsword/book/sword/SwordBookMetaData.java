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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.jdom.Document;

/**
 * A utility class for loading and representing Sword book configs.
 *
 * <p>Config file format. See also:
 * <a href="http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
 *
 * <p> The contents of the About field are in rtf.
 * <p> \ is used as a continuation line.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @author Jacky Cheung
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class SwordBookMetaData extends AbstractBookMetaData
{
    /**
     * Loads a sword config from a given File.
     * The returned BookMetaData object will not be associated with a Book so
     * setBook() should be called before getBook() is expected to return
     * anything other than null.
     * 
     * @param file
     * @param internal
     * @throws IOException
     */
    public SwordBookMetaData(File file, String internal, URI bookRootPath) throws IOException
    {
        cet = new ConfigEntryTable(internal);
        cet.load(file);
        setLibrary(bookRootPath);
        buildProperties();
    }

    /**
     * Loads a sword config from a buffer.
     * The returned BookMetaData object will not be associated with a Book so
     * setBook() should be called before getBook() is expected to return
     * anything other than null.
     * 
     * @param buffer
     * @param internal
     * @throws IOException
     */
    public SwordBookMetaData(byte[] buffer, String internal) throws IOException
    {
        cet = new ConfigEntryTable(internal);
        cet.load(buffer);
        buildProperties();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    /* @Override */
    public boolean isQuestionable()
    {
        return cet.isQuestionable();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isSupported()
     */
    /* @Override */
    public boolean isSupported()
    {
        return cet.isSupported() && cet.getBookType().isSupported(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isEnciphered()
     */
    /* @Override */
    public boolean isEnciphered()
    {
        return cet.isEnciphered();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLocked()
     */
    /* @Override */
    public boolean isLocked()
    {
        return cet.isLocked();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#unlock(String)
     */
    /* @Override */
    public boolean unlock(String unlockKey)
    {
        return cet.unlock(unlockKey);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getUnlockKey()
     */
    public String getUnlockKey()
    {
        return cet.getUnlockKey();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return getProperty(ConfigEntryType.DESCRIPTION);
    }

    /**
     * Returns the Charset of the book based on the encoding attribute
     * @return the charset of the book.
     */
    public String getBookCharset()
    {
        return (String) ENCODING_JAVA.get(getProperty(ConfigEntryType.ENCODING));
    }

    /**
     * Returns the Book Type.
     */
    public BookType getBookType()
    {
        return cet.getBookType();
    }

    /**
     * Returns the sourceType.
     */
    public Filter getFilter()
    {
        String sourcetype = getProperty(ConfigEntryType.SOURCE_TYPE);
        return FilterFactory.getFilter(sourcetype);
    }

    /**
     * @return Returns the relative path of the book's conf.
     */
    public String getConfPath()
    {
        return SwordConstants.DIR_CONF + '/' + getInitials().toLowerCase(Locale.ENGLISH) + SwordConstants.EXTENSION_CONF;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#setLibrary(java.net.URI)
     */
    public void setLibrary(URI library)
    {
        cet.add(ConfigEntryType.LIBRARY_URL, library.toString());
        super.setLibrary(library);

        // Currently all DATA_PATH entries end in / to indicate dirs or not to indicate file prefixes
        String datapath = getProperty(ConfigEntryType.DATA_PATH);
        datapath = datapath.substring(0, datapath.lastIndexOf('/'));
        URI location = NetUtil.lengthenURI(library, datapath);
        cet.add(ConfigEntryType.LOCATION_URL, location.toString());
        super.setLocation(location);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCategory()
     */
    public BookCategory getBookCategory()
    {
        if (type == null)
        {
            type = BookCategory.fromString(getProperty(ConfigEntryType.CATEGORY));
            if (type == BookCategory.OTHER)
            {
                type = getBookType().getBookCategory();
            }
        }
        return type;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#toOSIS()
     */
    /* @Override */
    public Document toOSIS()
    {
        return new Document(cet.toOSIS());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials()
    {
        return getProperty(ConfigEntryType.INITIALS);
    }

    /**
     * Get the string value for the property or null if it is not defined.
     * It is assumed that all properties gotten with this method are single line.
     * @param entry the ConfigEntryType
     * @return the property or null
     */
    public String getProperty(ConfigEntryType entry)
    {
        Object obj = cet.getValue(entry);

        return obj != null ? obj.toString() : null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#lsLeftToRight()
     */
    public boolean isLeftToRight()
    {
        String dir = getProperty(ConfigEntryType.DIRECTION);
        return dir == null || dir.equals(ConfigEntryType.DIRECTION.getDefault());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#hasFeature(org.crosswire.jsword.book.FeatureType)
     */
    /* @Override */
    public boolean hasFeature(FeatureType feature)
    {
        return cet.match(ConfigEntryType.FEATURE, feature.toString());
    }

    private void buildProperties()
    {
        // merge entries into properties file
        Iterator iter = cet.getKeys().iterator();
        while (iter.hasNext())
        {
            ConfigEntryType key = (ConfigEntryType) iter.next();
            Object value = cet.getValue(key);
            // value is null if the config entry was rejected.
            if (value == null)
            {
                continue;
            }
            if (value instanceof List)
            {
                List list = (List) value;
                StringBuffer combined = new StringBuffer();
                boolean appendSeparator = false;
                Iterator it = list.iterator();
                while (it.hasNext())
                {
                    String element = (String) it.next();
                    if (appendSeparator)
                    {
                        combined.append('\n');
                    }
                    combined.append(element);
                    appendSeparator = true;
                }

                value = combined.toString();
            }

            putProperty(key.toString(), value.toString());
        }
//        Element ele = cet.toOSIS();
//        SAXEventProvider sep = new JDOMSAXEventProvider(new Document(ele));
//        try
//        {
//            System.out.println(XMLUtil.writeToString(sep));
//        }
//        catch(Exception e)
//        {
//        }
    }

    /**
     * Sword only recognizes two encodings for its modules: UTF-8 and LATIN1
     * Sword uses MS Windows cp1252 for Latin 1 not the standard. Arrgh!
     * The language strings need to be converted to Java charsets
     */
    private static final Map ENCODING_JAVA = new HashMap();
    static
    {
        ENCODING_JAVA.put("Latin-1", "WINDOWS-1252"); //$NON-NLS-1$ //$NON-NLS-2$
        ENCODING_JAVA.put("UTF-8", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private ConfigEntryTable cet;
    private BookCategory type;
}
