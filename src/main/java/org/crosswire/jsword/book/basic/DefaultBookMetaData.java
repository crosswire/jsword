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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.crosswire.common.util.Language;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.VerseKey;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * DefaultBookMetaData is an implementation of the of the BookMetaData
 * interface. A less complete implementation design for inheritance is available
 * in AbstractBookMetaData where the complexity is in the setup rather than the
 * inheritance. DefaultBookMetaData is probably the preferred implementation.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class DefaultBookMetaData extends AbstractBookMetaData {
    /**
     * Ctor with some default values. A call to setBook() is still required
     * after this ctor is called
     * @param driver the driver for this book
     * @param name the name of this book
     * @param type the type of this book
     */
    public DefaultBookMetaData(BookDriver driver, String name, BookCategory type) {
        props = new HashMap<String, String>();
        setDriver(driver);
        setName(name);
        setBookCategory(type);
        setLanguage(Language.DEFAULT_LANG); // Default language
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCharset()
     */
    public String getBookCharset() {
        return DEFAULT_CHARSET;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getPropertyKeys()
     */
    public Set<String> getPropertyKeys() {
        return Collections.unmodifiableSet(props.keySet());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperty(java.lang.String)
     */
    public String getProperty(String key) {
        if (KEY_LANGUAGE.equals(key)) {
            return getLanguage().getName();
        }
        return props.get(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty(String key, String value) {
        props.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#putProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void putProperty(String key, String value) {
        setProperty(key, value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#putProperty(java.lang.String, java.lang.String, boolean)
     */
    public void putProperty(String key, String value, boolean forFrontend) {
        setProperty(key, value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCategory()
     */
    public BookCategory getBookCategory() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getAbbreviation()
     */
    public String getAbbreviation() {
        return initials;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials() {
        return initials;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight() {
        return getLanguage().isLeftToRight();
    }

    /**
     * See note on setName() for side effect on setInitials(). If a value of
     * null is used then the initials are defaulted using the name
     * 
     * @see DefaultBookMetaData#setName(String)
     * @param initials
     *            The initials to set.
     */
    public void setInitials(String initials) {
        if (initials == null) {
            if (name == null) {
                this.initials = "";
            } else {
                this.initials = StringUtil.getInitials(name);
            }
        } else {
            this.initials = initials;
        }
    }

    /**
     * Setting the name also sets some default initials, so if you wish to set
     * some specific initials then it should be done after setting the name.
     * 
     * @see DefaultBookMetaData#setInitials(String)
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;

        putProperty(KEY_NAME, this.name);

        setInitials(StringUtil.getInitials(name));
    }

    /**
     * @param aType
     *            The type to set.
     */
    public void setBookCategory(BookCategory aType) {
        BookCategory t = aType;
        if (t == null) {
            t = BookCategory.BIBLE;
        }
        type = t;

        putProperty(KEY_CATEGORY, type.toString());
    }

    /**
     * @param typestr
     *            The string version of the type to set.
     */
    public void setType(String typestr) {
        BookCategory newType = null;
        if (typestr != null) {
            newType = BookCategory.fromString(typestr);
        }

        setBookCategory(newType);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#toOSIS()
     */
    @Override
    public Document toOSIS() {
        OSISUtil.OSISFactory factory = OSISUtil.factory();
        Element ele = factory.createTable();
        addRow(factory, ele, "Initials", getInitials());
        addRow(factory, ele, "Description", getName());
        addRow(factory, ele, "Key", getBookCategory().toString());
        addRow(factory, ele, "Language", getLanguage().getName());
        return new Document(ele);
    }

    /**
     * @return The scope
     */
    public VerseKey getScope() {
        // The following method is only available for Sword books
        throw new UnsupportedOperationException();
    }

    private void addRow(OSISUtil.OSISFactory factory, Element table, String key, String value) {
        if (value == null) {
            return;
        }

        Element rowEle = factory.createRow();

        Element nameEle = factory.createCell();
        Element hiEle = factory.createHI();
        hiEle.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_BOLD);
        nameEle.addContent(hiEle);
        Element valueElement = factory.createCell();
        rowEle.addContent(nameEle);
        rowEle.addContent(valueElement);

        // I18N(DMS): use name to lookup translation.
        hiEle.addContent(key);

        String expandedValue = XMLUtil.escape(value);
        valueElement.addContent(expandedValue);

        table.addContent(rowEle);
    }

    private BookCategory type;
    private String name;
    private String initials;
    private Map<String, String> props;
    private static final String DEFAULT_CHARSET = "UTF-8";
}
