
package org.crosswire.jsword.book.data;

import org.crosswire.common.util.LogicError;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.util.Project;
import org.xml.sax.SAXException;

/**
 * A generic way of creating new BookData, SectionData and VerseData classes.
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
public abstract class DataFactory
{
    /**
     * Create a BibleData from a SAXEventProvider
     * @param doc
     */
    public abstract BookData createBibleData(SAXEventProvider provider) throws SAXException;

    /**
     * A SAX style event reciever.
     */
    public abstract BookDataListener createBookDataListnener();

    /**
     * Singleton access method.
     */
    public static synchronized DataFactory getInstance()
    {
        if (df == null)
        {
            try
            {
                Class impl = Project.resource().getImplementor(DataFactory.class);
                df = (DataFactory) impl.newInstance();
            }
            catch (Exception ex)
            {
                throw new LogicError(ex);
            }
        }

        return df;
    }

    /**
     * The current DataFactory
     */
    private static DataFactory df = null;
}
