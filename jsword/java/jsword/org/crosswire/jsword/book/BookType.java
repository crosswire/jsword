package org.crosswire.jsword.book;

import java.io.Serializable;


/**
 * An Enumeration of the possible types of Book.
 * 
 * <p>NOTE(joe): consider giving each a number (1,2,4,8) and allowing combinations
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
public class BookType implements Serializable
{
    /**
     * Books that are Bibles
     */
    public static final BookType BIBLE = new BookType("Bible"); //$NON-NLS-1$

    /**
     * Books that are Dictionaries
     */
    public static final BookType DICTIONARY = new BookType("Dictionary"); //$NON-NLS-1$

    /**
     * Books that are Commentaries
     */
    public static final BookType COMMENTARY = new BookType("Commentary"); //$NON-NLS-1$

    /**
     * Books that are not any of the above
     */
    public static final BookType OTHER = new BookType("Other"); //$NON-NLS-1$

    /**
     * @param name The name of the BookType
     */
    private BookType(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static BookType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            BookType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static BookType fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the BookType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final BookType[] VALUES =
    {
        BIBLE,
        DICTIONARY,
        COMMENTARY,
        OTHER
    };

    /**
     * SERIALUID(dms): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;
}
