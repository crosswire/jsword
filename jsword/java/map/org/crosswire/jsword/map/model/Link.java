
package org.crosswire.jsword.map.model;

import java.io.Serializable;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.common.util.LogicError;

/**
 * A Link describes a destination verse and a link strength.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Link implements Serializable
{
    /**
     * Basic constructor
     */
    public Link(int book, int chapter, int strength)
    {
        this.book = book;
        this.chapter = chapter;
        this.strength = strength;
    }

    /**
     * The destination book
     * @return the book
     */
    public int getDestinationBook()
    {
        return book;
    }

    /**
     * The destination chapter
     * @return the chapter
     */
    public int getDestinationChapter()
    {
        return chapter;
    }

    /**
     * The strength of the attraction - an integer probably between 1 and 10
     * @return The strength of the attraction
     */
    public int getStrength()
    {
        return strength;
    }

    /**
     * Simple bit of debug
     */
    public String toString()
    {
        try
        {
            return ""+new VerseRange(new Verse(book, chapter, 1), new Verse(book, chapter, BibleInfo.versesInChapter(book, chapter)))+"("+strength+")";
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /** To make serialization work across new versions */
    static final long serialVersionUID = -3293524580874173927L;

    /** The destination book */
    private int book;

    /** The destination chapter */
    private int chapter;

    /** The strength of the link */
    private int strength;
}
