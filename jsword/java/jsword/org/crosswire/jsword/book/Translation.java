package org.crosswire.jsword.book;

import org.crosswire.jsword.passage.Passage;

/**
 * A Translation links a Greek/Hebrew work with a localized translation, and can
 * link a number of verses that are examples of the given translation.
 * 
 * <p>Perhpas we should consider linking to the Bible that translated the
 * original in the ways listed in the Passage?.</p>
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Translation
{
    /**
     * All translations must be from a greek/hebrew word, to a localized word
     * @param word The localized word(s).
     * @param strongs The original word.
     */
    public Translation(String word, Strongs strongs)
    {
        this.word = word;
        this.strongs = strongs;
    }

    /**
     * Accessor for the localized translation
     */
    public String getWord()
    {
        return word;
    }

    /**
     * Accessor for the original word.
     */
    public Strongs getStrongs()
    {
        return strongs;
    }

    /**
     * Accessor for the passages that translate the word/number in this way.
     */
    public Passage getRef()
    {
        return ref;
    }

    /**
     * The localized word
     */
    private String word;

    /**
     * The original Strongs number
     */
    private Strongs strongs;

    /**
     * The matching verses
     */
    private Passage ref = null;
}