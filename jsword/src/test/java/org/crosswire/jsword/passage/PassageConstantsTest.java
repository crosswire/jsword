/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageConstantsTest extends TestCase
{
    public PassageConstantsTest(String s)
    {
        super(s);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    /* @Override */
    protected void setUp() throws Exception
    {
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    /* @Override */
    protected void tearDown() throws Exception
    {
    }

    public void testAllowedDelims() throws Exception
    {
        // Check that we're not re-using delimitters
        for (int i=0; i<AccuracyType.VERSE_ALLOWED_DELIMS.length(); i++)
        {
            assertEquals(AbstractPassage.REF_ALLOWED_DELIMS.indexOf(AccuracyType.VERSE_ALLOWED_DELIMS.charAt(i)), -1);
            assertEquals(VerseRange.RANGE_ALLOWED_DELIMS.indexOf(AccuracyType.VERSE_ALLOWED_DELIMS.charAt(i)), -1);
        }

        for (int i=0; i<AbstractPassage.REF_ALLOWED_DELIMS.length(); i++)
        {
            assertEquals(VerseRange.RANGE_ALLOWED_DELIMS.indexOf(AbstractPassage.REF_ALLOWED_DELIMS.charAt(i)), -1);
        }
    }
}
