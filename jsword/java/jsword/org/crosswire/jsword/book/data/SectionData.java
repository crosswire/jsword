
package org.crosswire.jsword.book.data;

import java.util.Iterator;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Verse;

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
public interface SectionData
{
    /**
     * Accessor for our parent Element
     * @return The parent BibleData
     */
    public BibleData getParent();

    /**
     * This is an accessor for the list of references (verses) that we
     * hold
     * @return The list of RefDatas
     */
    public Iterator getRefDatas();

    /**
     * Get a reference to the real W3C Document.
     * @param verse The reference marker
     * @param para True if this is the start of a new section
     */
    public void addRefData(RefData ref) throws BookException;

    /**
     * Get a reference to the real W3C Document.
     * @param verse The reference marker
     * @param para True if this is the start of a new section
     */
    public RefData createRefData(Verse verse, boolean para) throws BookException;

    /**
     * A simplified plain text version of the data in this verse with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public String getPlainText();

    /**
     * @label contains
     * @clientCardinality 1
     * @supplierCardinality 1..*
     */
    /*#RefData refs;*/
}
