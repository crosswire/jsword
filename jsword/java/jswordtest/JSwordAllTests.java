
// package default;

import org.crosswire.jsword.util.Project;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
public class JSwordAllTests extends TestCase
{
    public JSwordAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        Project.init();

        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.util.TestStyle.class);

        suite.addTestSuite(org.crosswire.jsword.passage.TestBooks.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageConstants.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageMix.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSize.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSpeed.class);
        // commented out because it causes OutOfMemoryErrors.
        //suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSpeedOpt.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageTally.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageTally2.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageUtil.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageWriteSpeed.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestVerse.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestVerseRange.class);

        suite.addTestSuite(org.crosswire.jsword.book.TestBibles.class);
        suite.addTestSuite(org.crosswire.jsword.book.TestBookMetaData.class);
        suite.addTestSuite(org.crosswire.jsword.book.TestBookUtil.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.TestJDBCBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.TestJDBCBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.TestJDBCBibleUtil.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.TestRawBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.TestRawBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.TestLocalRemoteBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.TestLocalRemoteBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.TestSerBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.TestSerBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.TestStubBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.TestStubBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.TestSwordBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.TestSwordBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.ConverterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.RemoteMethodTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.search.ser.TestDictionary.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.ser.TestCustomTokenizer.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.ser.TestParser.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.ser.TestSearchWords.class);

        return suite;
    }
}
