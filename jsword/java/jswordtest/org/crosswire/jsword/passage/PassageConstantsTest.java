
package org.crosswire.jsword.passage;

import junit.framework.TestCase;

/**
 * JUnit Test.
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
public class PassageConstantsTest extends TestCase
{
    public PassageConstantsTest(String s)
    {
        super(s);
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }

    public void testAllowedDelims() throws Exception
    {
        // Check that we're not re-using delimitters
        for (int i=0; i<PassageConstants.VERSE_ALLOWED_DELIMS.length(); i++)
        {
            assertEquals(PassageConstants.REF_ALLOWED_DELIMS.indexOf(PassageConstants.VERSE_ALLOWED_DELIMS.charAt(i)), -1);
            assertEquals(PassageConstants.RANGE_ALLOWED_DELIMS.indexOf(PassageConstants.VERSE_ALLOWED_DELIMS.charAt(i)), -1);
        }

        for (int i=0; i<PassageConstants.REF_ALLOWED_DELIMS.length(); i++)
        {
            assertEquals(PassageConstants.RANGE_ALLOWED_DELIMS.indexOf(PassageConstants.REF_ALLOWED_DELIMS.charAt(i)), -1);
        }
    }
}
