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
package org.crosswire.jsword.book;

import org.crosswire.jsword.JSMsg;

/**
 * An Enumeration of the possible types of Book.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public enum BookCategory {
    /** Books that are Bibles */
    // TRANSLATOR: The name for the book category consisting of Bibles.
    BIBLE("Biblical Texts", JSMsg.gettext("Biblical Texts")),

    /** Books that are Dictionaries */
    // TRANSLATOR: The name for the book category consisting of Lexicons and Dictionaries.
    DICTIONARY("Lexicons / Dictionaries", JSMsg.gettext("Dictionaries")),

    /** Books that are Commentaries */
    // TRANSLATOR: The name for the book category consisting of Commentaries.
    COMMENTARY("Commentaries", JSMsg.gettext("Commentaries")),

    /** Books that are indexed by day. AKA, Daily Devotions */
    // TRANSLATOR: The name for the book category consisting of Daily Devotions, indexed by day of the year.
    DAILY_DEVOTIONS("Daily Devotional", JSMsg.gettext("Daily Devotionals")),

    /** Books that map words from one language to another. */
    // TRANSLATOR: The name for the book category consisting of Glossaries that map words/phrases from one language into another.
    GLOSSARY("Glossaries", JSMsg.gettext("Glossaries")),

    /** Books that are questionable. */
    // TRANSLATOR: The name for the book category consisting of books that are considered unorthodox by mainstream Christianity.
    QUESTIONABLE("Cults / Unorthodox / Questionable Material", JSMsg.gettext("Cults / Unorthodox / Questionable Materials")),

    /** Books that are just essays. */
    // TRANSLATOR: The name for the book category consisting of just essays.
    ESSAYS("Essays", JSMsg.gettext("Essays")),

    /** Books that are predominately images. */
    // TRANSLATOR: The name for the book category consisting of books containing mostly images.
    IMAGES("Images", JSMsg.gettext("Images")),

    /** Books that are a collection of maps. */
    // TRANSLATOR: The name for the book category consisting of books containing mostly maps.
    MAPS("Maps", JSMsg.gettext("Maps")),

    /** Books that are just books. */
    // TRANSLATOR: The name for the book category consisting of general books.
    GENERAL_BOOK("Generic Books", JSMsg.gettext("General Books")),

    /** Books that are not any of the above. This is a catch all for new book categories. */
    // TRANSLATOR: The name for the book category consisting of books not in any of the other categories.
    OTHER("Other", JSMsg.gettext("Other"));

    /**
     * @param name
     *            The name of the BookCategory
     * @param externalName the name of the BookCategory worthy of an end user
     */
    BookCategory(String name, String externalName) {
        this.name = name;
        this.externalName = externalName;
    }

    /**
     * Lookup method to convert from a String
     * 
     * @param name the internal name of a BookCategory
     * @return the matching BookCategory
     */
    public static BookCategory fromString(String name) {
        for (BookCategory o : BookCategory.values()) {
            if (o.name.equalsIgnoreCase(name)) {
                return o;
            }
        }
        return OTHER;
    }

    /**
     * Lookup method to convert from a String
     * 
     * @param name the external name of a BookCategory
     * @return the matching BookCategory
     */
    public static BookCategory fromExternalString(String name) {
        for (BookCategory o : BookCategory.values()) {
            if (o.externalName.equalsIgnoreCase(name)) {
                return o;
            }
        }
        return OTHER;
    }

    /**
     * Lookup method to convert from an integer
     * 
     * @param i the ordinal value of the BookCategory in this enumeration.
     * @return the i-th BookCategory
     */
    public static BookCategory fromInteger(int i) {
        for (BookCategory o : BookCategory.values()) {
            if (i == o.ordinal()) {
                return o;
            }
        }
        return OTHER;
    }

    /**
     * @return the internal name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the internationalized name.
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
}
