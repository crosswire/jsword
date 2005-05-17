/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
// package default;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JSwordAllTests extends TestCase
{
    public JSwordAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.passage.BibleInfoTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageConstantsTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageMixTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSizeTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedTest.class);
        // commented out because it causes OutOfMemoryErrors.
        //suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedOptTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTallyTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTally2Test.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageUtilTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageWriteSpeedTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseRangeTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.BooksTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.BookMetaDataTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.SentanceUtilTest.class);
        // commented out because the tests were very poor.
        //suite.addTestSuite(org.crosswire.jsword.book.OsisTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.search.parse.DictionaryTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.CustomTokenizerTest.class);
        /* FIXME
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.ParserTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.WordsTest.class);
        */
        return suite;
    }
}
