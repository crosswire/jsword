
package org.crosswire.jsword.book.remote;

import junit.framework.TestCase;

import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.remote.Converter;
import org.crosswire.jsword.book.remote.LocalRemoter;
import org.crosswire.jsword.book.remote.RemoteConstants;
import org.crosswire.jsword.book.remote.RemoteMethod;
import org.crosswire.jsword.book.remote.Remoter;
import org.crosswire.jsword.util.Project;
import org.jdom.Document;

/**
 * JUnit tests.
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
 * @version $Id: Bible.java,v 1.2 2002/10/08 21:36:07 joe Exp $
 */
public class LocalRemoterTest extends TestCase
{

    /**
     * Constructor for LocalRemoterTest.
     * @param arg0
     */
    public LocalRemoterTest(String arg0)
    {
        super(arg0);
        Project.init();
    }

    private Remoter remote = new LocalRemoter();

    public void testGetBibles() throws Exception
    {
        BibleMetaData[] names1 = Bibles.getFastBibles(Bibles.SPEED_SLOWEST); 

        RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_GETBIBLES);
        Document doc = remote.execute(method);
        BibleMetaData[] names2 = Converter.convertDocumentToBibleMetaDatas(doc, new FixtureRemoter(), Bibles.SPEED_INACCURATE);

        assertEquals(names1.length, names2.length);
        for (int i=0; i<names1.length; i++)
        {
            assertEquals(names1[i].getName(), names2[i].getName());
        }
    }

    public void assertEquals(Object[] o1, Object[] o2)
    {
        assertEquals(o1.length, o2.length);
        for (int i=0; i<o1.length; i++)
        {
            assertEquals(o1[i], o2[i]);
        }
    }
}
