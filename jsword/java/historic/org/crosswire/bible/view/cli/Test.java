
package org.crosswire.bible.view.cli;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import org.crosswire.bible.control.test.TestList;
import org.crosswire.bible.util.Project;
import org.crosswire.util.TestBase;

/**
* Test is a command line utility that runs all the configured tests.
* <p>I may at some stage add some functionality to select the tests
* that can be run.
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
* @version D0.I0.T0
*/
public class Test
{
    /**
    * Basic constructor
    */
    public static void main(String[] args)
    {
        Project.init();

        switch (args.length)
        {
        case 0:
            Enumeration en = hash.elements();
            while (en.hasMoreElements())
            {
                TestBase all = (TestBase) en.nextElement();
                all.test(new PrintWriter(System.out), false);
            }
            break;

        case 1:
            TestBase single = (TestBase) hash.get(args[0]);
            if (single == null)
                usage();
            else
                single.test(new PrintWriter(System.out), false);
            break;

        default:
            usage();
            break;
        }

        // Since the Swing test creates some dialogs we need this to
        // make sure that we actually exit the program.
        System.exit(0);
    }

    /**
    * Usage text
    */
    public static void usage()
    {
        System.out.println("Syntax: ctest [module]");
        System.out.println("  Where [module] is blank to run all tests or one of:");
        Enumeration en = hash.keys();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            TestBase test = (TestBase) hash.get(key);
            System.out.println("    "+key+" (which runs "+test.getClass()+")");
        }
    }

    /** The set of tests */
    private static Hashtable hash = TestList.getTesters();
}
