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
package org.crosswire.jsword.passage;

import java.io.Serializable;
import java.util.Iterator;

/**
 * The base unit that is collected by a Passage.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface VerseBase extends Cloneable, Serializable, Key
{
    /**
     * Translate the Passage into a human readable string
     * @return The string representation
     */
    String getName();

    /**
     * Translate the Passage into a human readable string, with the
     * assumption that the specified Verse has just been output, so if we
     * are in the same book, we do not need to display the book name, and
     * so on.
     * @param base The verse to use to cut down unnecessary output.
     * @return The string representation
     */
    String getName(Verse base);

    /**
     * The OSIS defined specification for this Verse/VerseRange.
     * Uses short books names, with "." as a verse part separator.
     * @return a String containing the OSIS description of the verses
     */
    String getOsisRef();

    /**
     * Create an array of Verses.
     * See note on verseElements()
     * @return The array of verses that this makes up
     * @see #verseIterator()
     */
    Verse[] toVerseArray();

    /**
     * Enumerate over the verses in this object. I remember thinking at some
     * stage that I ought to just use one of toVerseArray() and verseElements()
     * and contemplated removing the other one, but didn't make the change. I
     * suspect the newer (and therefore probably better) implementation is going
     * to be further down the file (i.e. this one), and so toVerseArray should
     * not be used anymore. However I can't remember the reasoning behind it
     * other than the possibility of less Object generation if you are not
     * going to itterate over the whole array.
     * @return A verse iterator
     */
    Iterator verseIterator();
}
