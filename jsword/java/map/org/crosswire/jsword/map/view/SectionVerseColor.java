
package org.crosswire.jsword.map.view;

import java.awt.Color;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * SectionVerseColor gives a color to a selected group of books,
 * leaving the others grey. 
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
public class SectionVerseColor implements VerseColor
{
    /**
    * Basic constructor
    */
    public SectionVerseColor(int section)
    {
        this.section = section;
    }

    /**
    * Accessor for the currently highlighted section
    * @param section The new highlighted section
    */
    public void setSection(int section)
    {
        this.section = section;
    }

    /**
    * Accessor for the currently highlighted section
    * @return The current highlighted section
    */
    public int getSection()
    {
        return section;
    }

    /**
    * What Color should we use to represent this verse
    * @param book The book number (Gen=1, Rev=66)
    * @param chapter The chapter number
    * @param verse The verse number
    * @return The Color for this verse
    */
    public Color getColor(int book, int chapter, int verse)
    {
        if (section != BibleInfo.getSection(book))
        {
            return Color.gray;
        }
        else
        {
            return Color.red;
        }
    }

    /**
    * What Color would set off the Verses painted on it
    * @return An appropriate background color
    */
    public Color getBackground()
    {
        return Color.black;
    }

    /**
    * What Color should text be painted in
    * @return An appropriate font color
    */
    public Color getForeground()
    {
        return Color.white;
    }

    /**
    * The name for display in a combo box
    */
    public String toString()
    {
        try
        {
            return "Section - "+BibleInfo.getSectionName(section);
        }
        catch (NoSuchVerseException ex)
        {
            return "Section - Error";
        }
    }

    /** The currently highlighted section */
    private int section;
}
