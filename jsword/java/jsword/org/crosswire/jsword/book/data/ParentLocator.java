package org.crosswire.jsword.book.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.Element;

/**
 * A cheat way to find parents of nodes in a JAXB tree.
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
public class ParentLocator
{
    /**
     * Create a cache of parent/child relationships.
     */
    public ParentLocator(Element root)
    {
        recurseChildren(root);
    }

    /**
     * What is the parent of the given child
     */
    public Element getParent(Element child)
    {
        return (Element) map.get(child);
    }

    /**
     * Recurse through the children of the given element finding and recording
     * parent/child relationships.
     */
    private void recurseChildren(Element parent)
    {
        Iterator it = JAXBUtil.getList(parent).iterator();
        while (it.hasNext())
        {
            Element child = (Element) it.next();
            recurseChildren(child);

            map.put(child, parent);
        }
    }

    /**
     * The map of parent/child relationships
     */
    private Map map = new HashMap();
}
