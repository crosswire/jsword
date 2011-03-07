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
package org.crosswire.jsword.book;

import java.io.Serializable;

import org.crosswire.jsword.JSMsg;

/**
 * An Enumeration of the possible types of Book.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class BookCategory implements Serializable, Comparable<BookCategory> {
    /** Books that are Bibles */
    // I18N(DMS)
    public static final BookCategory BIBLE = new BookCategory("Biblical Texts", JSMsg.gettext("Biblical Texts"));

    /** Books that are Dictionaries */
    // I18N(DMS)
    public static final BookCategory DICTIONARY = new BookCategory("Lexicons / Dictionaries", JSMsg.gettext("Dictionaries"));

    /** Books that are Commentaries */
    // I18N(DMS)
    public static final BookCategory COMMENTARY = new BookCategory("Commentaries", JSMsg.gettext("Commentaries"));

    /** Books that are indexed by day. AKA, Daily Devotions */
    // I18N(DMS)
    public static final BookCategory DAILY_DEVOTIONS = new BookCategory("Daily Devotional", JSMsg.gettext("Daily Devotionals"));

    /** Books that map words from one language to another. */
    // I18N(DMS)
    public static final BookCategory GLOSSARY = new BookCategory("Glossaries", JSMsg.gettext("Glossaries"));

    /** Books that are questionable. */
    // I18N(DMS)
    public static final BookCategory QUESTIONABLE = new BookCategory("Cults / Unorthodox / Questionable Material", JSMsg.gettext("Cults / Unorthodox / Questionable Materials"));

    /** Books that are just essays. */
    // I18N(DMS)
    public static final BookCategory ESSAYS = new BookCategory("Essays", JSMsg.gettext("Essays"));

    /** Books that are predominately images. */
    // I18N(DMS)
    public static final BookCategory IMAGES = new BookCategory("Images", JSMsg.gettext("Images"));

    /** Books that are a collection of maps. */
    // I18N(DMS)
    public static final BookCategory MAPS = new BookCategory("Maps", JSMsg.gettext("Maps"));

    /** Books that are just books. */
    // I18N(DMS)
    public static final BookCategory GENERAL_BOOK = new BookCategory("Generic Books", JSMsg.gettext("General Books"));

    /** Books that are not any of the above. This is a catch all for new book categories. */
    // I18N(DMS)
    public static final BookCategory OTHER = new BookCategory("Other", JSMsg.gettext("Other"));

    /**
     * @param name
     *            The name of the BookCategory
     */
    private BookCategory(String name, String externalName) {
        this.name = name;
        this.externalName = externalName;
    }

    /**
     * Lookup method to convert from a String
     */
    public static BookCategory fromString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            BookCategory o = VALUES[i];
            if (o.name.equalsIgnoreCase(name)) {
                return o;
            }
        }
        return OTHER;
    }

    /**
     * Lookup method to convert from a String
     */
    public static BookCategory fromExternalString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            BookCategory o = VALUES[i];
            if (o.externalName.equalsIgnoreCase(name)) {
                return o;
            }
        }
        return OTHER;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static BookCategory fromInteger(int i) {
        return VALUES[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(BookCategory that) {
        return this.name.compareTo(that.name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @return the internal name.
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return externalName;
    }

    /**
     * The names of the BookCategory
     */
    private transient String name;
    private transient String externalName;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve() {
        return VALUES[obj];
    }

    private static final BookCategory[] VALUES = {
            BIBLE, DICTIONARY, COMMENTARY, DAILY_DEVOTIONS, GLOSSARY, QUESTIONABLE, ESSAYS, IMAGES, MAPS, GENERAL_BOOK, OTHER,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
