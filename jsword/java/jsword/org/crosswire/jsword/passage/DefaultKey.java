
package org.crosswire.jsword.passage;


/**
 * A simple default implementation of the Key interface.
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
public class DefaultKey implements Key
{
    /**
     * Default ctor
     */
    public DefaultKey(String name)
    {
        this.name = name;
    }

    /**
     * Default ctor
     */
    public DefaultKey(String name, Key parent)
    {
        this.name = name;
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Key#getText()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Returns the parent of this key
     */
    public Key getParent()
    {
        return parent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        DefaultKey that = (DefaultKey) obj;
        return name.compareTo(that.name);
    }

    /**
     * The parent of this key
     */
    private Key parent;

    /**
     * The string that this key represents
     */
    private String name;
}
