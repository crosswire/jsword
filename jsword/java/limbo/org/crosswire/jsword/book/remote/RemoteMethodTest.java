
package org.crosswire.jsword.book.remote;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
        RemoteMethod rmt1 = new RemoteMethod(MethodName.FINDPASSAGE);
        RemoteMethod rmt2 = new RemoteMethod(MethodName.GETBIBLES);
        RemoteMethod rmt3 = new RemoteMethod(MethodName.GETDATA);
        RemoteMethod rmt4 = new RemoteMethod(MethodName.GETDATA);

        assertEquals(rmt1.getMethodName(), MethodName.FINDPASSAGE);
        assertEquals(rmt2.getMethodName(), MethodName.GETBIBLES);
        assertEquals(rmt3.getMethodName(), MethodName.GETDATA);
        assertEquals(rmt4.getMethodName(), MethodName.GETDATA);

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

        rmt1.addParam(ParamName.PARAM_BIBLE, "v1:1"); //$NON-NLS-1$
        rmt2.addParam(ParamName.PARAM_FINDMATCH, "v2:1"); //$NON-NLS-1$
        rmt2.addParam(ParamName.PARAM_FINDRANGE, "v2:2"); //$NON-NLS-1$
        rmt3.addParam(ParamName.PARAM_FINDSTRING, "v3:1"); //$NON-NLS-1$
        rmt3.addParam(ParamName.PARAM_PASSAGE, "v3:2"); //$NON-NLS-1$

        assertTrue(rmt1.getParameterKeys().hasNext());
        assertTrue(rmt2.getParameterKeys().hasNext());
        assertTrue(rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());

        rmt1.clearParams();

        assertTrue(!rmt1.getParameterKeys().hasNext());
        assertTrue(rmt2.getParameterKeys().hasNext());
        assertTrue(rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());

        assertEquals(null, rmt1.getParameter(ParamName.PARAM_BIBLE));
        assertEquals("v2:1", rmt2.getParameter(ParamName.PARAM_FINDMATCH)); //$NON-NLS-1$
        assertEquals("v2:2", rmt2.getParameter(ParamName.PARAM_FINDRANGE)); //$NON-NLS-1$
        assertEquals("v3:1", rmt3.getParameter(ParamName.PARAM_FINDSTRING)); //$NON-NLS-1$
        assertEquals("v3:2", rmt3.getParameter(ParamName.PARAM_PASSAGE)); //$NON-NLS-1$

        rmt1.clearParams();
        rmt2.clearParams();
        rmt3.clearParams();

        assertTrue(!rmt1.getParameterKeys().hasNext());
        assertTrue(!rmt2.getParameterKeys().hasNext());
        assertTrue(!rmt3.getParameterKeys().hasNext());
        assertTrue(!rmt4.getParameterKeys().hasNext());
    }
}
