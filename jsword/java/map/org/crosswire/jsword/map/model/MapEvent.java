
package org.crosswire.jsword.map.model;

import java.util.EventObject;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * A MapEvent happens whenever a Map changes.
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
public class MapEvent extends EventObject
{
    /**
     * Initialize a MapEvent
     * @param source The map that started this off
     */
    public MapEvent(Map source, int book, int chapter)
    {
        super(source);

        try
        {
            if (chapter < 1 || chapter > BibleInfo.chaptersInBook(book))
                throw new IllegalArgumentException("Invalid chapter");
        }
        catch (NoSuchVerseException ex)
        {
            throw new IllegalArgumentException("Invalid book");
        }

        this.book = book;
        this.chapter = chapter;
    }

    /**
     * Initialize a MapEvent
     * @param source The Object that started this off
     */
    public MapEvent(Map source)
    {
        super(source);

        book = -1;
        chapter = -1;
    }

    /**
     * Get the verse ordinal that changed position or null if the whole
     * table changed
     * @return The progress
     */
    public int getChangedBook()
    {
        return book;
    }

    /**
     * Get the verse ordinal that changed position or null if the whole
     * table changed
     * @return The progress
     */
    public int getChangedChapter()
    {
        return chapter;
    }

    /** The book number */
    private int book;

    /** The chapter number */
    private int chapter;
}
