
package org.crosswire.jsword.util.remoter;

import org.crosswire.jsword.book.remote.RemoteMethod;

import junit.framework.TestCase;

/**
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
public class RemoteMethodTest extends TestCase
{

    /**
     * Constructor for RemoteMethodTest.
     * @param arg0
     */
    public RemoteMethodTest(String arg0)
    {
        super(arg0);
    }
    public void testRemoteMethod()
    {
        RemoteMethod rmt1 = new RemoteMethod("rmt1");
        RemoteMethod rmt2 = new RemoteMethod("rmt2");
        RemoteMethod rmt3 = new RemoteMethod("rmt3");
        RemoteMethod rmt4 = new RemoteMethod("rmt4");

        assertEquals(rmt1.getMethodName(), "rmt1");
        assertEquals(rmt2.getMethodName(), "rmt2");
        assertEquals(rmt3.getMethodName(), "rmt3");
        assertEquals(rmt4.getMethodName(), "rmt4");

        assertTrue(!rmt1.getParameterKeys().hasNext());
        assertTrue(!rmt2.getParameterKeys().hasNext());
        assertTrue(!rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());

        rmt1.clearParams();
        rmt2.clearParams();

        assertTrue(!rmt1.getParameterKeys().hasNext());
        assertTrue(!rmt2.getParameterKeys().hasNext());
        assertTrue(!rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());

        rmt1.addParam("k1:1", "v1:1");
        rmt2.addParam("k2:1", "v2:1");
        rmt2.addParam("k2:2", "v2:2");
        rmt3.addParam("k3:1", "v3:1");
        rmt3.addParam("k3:2", "v3:2");
        rmt3.addParam("k3:3", "v3:3");

        assertTrue(rmt1.getParameterKeys().hasNext());
        assertTrue(rmt2.getParameterKeys().hasNext());
        assertTrue(rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());

        rmt1.clearParams();

        assertTrue(!rmt1.getParameterKeys().hasNext());
        assertTrue(rmt2.getParameterKeys().hasNext());
        assertTrue(rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());

        assertEquals(null, rmt1.getParameter("k1:1"));
        assertEquals("v2:1", rmt2.getParameter("k2:1"));
        assertEquals("v2:2", rmt2.getParameter("k2:2"));
        assertEquals("v3:1", rmt3.getParameter("k3:1"));
        assertEquals("v3:2", rmt3.getParameter("k3:2"));
        assertEquals("v3:3", rmt3.getParameter("k3:3"));
        assertEquals(null, rmt1.getParameter("k4:1"));

        assertTrue(((String) rmt2.getParameterKeys().next()).startsWith("k2"));
        assertTrue(((String) rmt3.getParameterKeys().next()).startsWith("k3"));

        rmt1.clearParams();
        rmt2.clearParams();
        rmt3.clearParams();

        assertTrue(!rmt1.getParameterKeys().hasNext());
        assertTrue(!rmt2.getParameterKeys().hasNext());
        assertTrue(!rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());
    }
}
