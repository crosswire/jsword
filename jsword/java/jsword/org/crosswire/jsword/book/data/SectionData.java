
package org.crosswire.jsword.book.data;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.osis.Div;

/**
 * A SectionData contains a list of references, and a note that
 * describes them. We can also override the version settting on the bible
 * element here.
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
public class SectionData
{
    /**
     * We only want to be created by OsisUtil
     * @see OsisUtil
     */
    protected SectionData()
    {
    }

    /**
     * The list of references
     */
    protected List refs = new ArrayList();

    /**
     * JAXB element
     */
    protected Div div;
}
