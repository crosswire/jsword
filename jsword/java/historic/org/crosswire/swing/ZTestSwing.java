
package org.crosswire.swing;

import java.io.PrintWriter;

import javax.swing.JTextArea;

import org.crosswire.util.LucidException;
import org.crosswire.util.TestBase;

/**
* Attepmted 100% code coverage testing.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class ZTestSwing extends TestBase
{
    /**
    * A basic are we OK type test, for the Passage package.
    */
    public void test(PrintWriter out, boolean fatal)
    {
        logPackageStart(out, fatal, ZTestSwing.class);

        testExceptionPane();
        testDocuments();

        logPackageStop();
    }

    /**
    * Some integration testing.
    */
    public static void testExceptionPane()
    {
        try
        {
            log("ExceptionPane.showExceptionDialog()");

            try
            {
                try
                {
                    try
                    {
                        try
                        {
                            int a=0, b=1/a;
                        }
                        catch (Exception ex)
                        {
                            throw new TestException("test_swing1", ex);
                        }
                    }
                    catch (Exception ex)
                    {
                        throw new TestException("test_swing2", ex, new Object[] { "fred" });
                    }
                }
                catch (Exception ex)
                {
                    throw new TestException("test_swing3", ex, new Object[] { "shiela", new Integer(6) });
                }
            }
            catch (Exception ex)
            {
                ExceptionPane.showExceptionDialog(null, ex);
            }
        }
        catch (Exception ex)
        {
            fail(ex);
        }
    }

    /**
    * Some integration testing.
    */
    public static void testDocuments()
    {
        try
        {
            log("UpperCaseDocument.insertText()");
            JTextArea text = new JTextArea(new UpperCaseDocument());
        }
        catch (Exception ex)
        {
            fail(ex);
        }
    }
}

/**
* Test nested exceptions.
*/
class TestException extends LucidException
{
    public TestException(String msg)
    {
        super(msg);
    }

    public TestException(String msg, Throwable ex)
    {
        super(msg, ex);
    }

    public TestException(String msg, Object[] params)
    {
        super(msg, params);
    }

    public TestException(String msg, Throwable ex, Object[] params)
    {
        super(msg, ex, params);
    }
}
