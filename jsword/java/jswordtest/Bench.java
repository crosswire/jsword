
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.control.test.Speed;
import org.crosswire.jsword.util.Project;

/**
 * Bench is a command line utility that runs the Speed benchmark program.
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
public class Bench
{
    /**
     * Basic constructor
     */
    public static void main(String[] args)
    {
        Project.init();

        Bible version = null;

        if (args.length == 0)
        {
            usage();
            versions();
            System.exit(1);
        }

        try
        {
            version = Defaults.getBibleMetaData().getBible();
        }
        catch (Exception ex)
        {
            Reporter.informUser(Bench.class, ex);

            System.out.println("Failed to load version '" + args[0] + "'");
            System.out.println("System message: " + ex);
            System.out.println("");
            usage();
            System.exit(1);
        }

        Speed speed = new Speed(version);
        speed.run();

        try
        {
            float time = speed.getBenchmark() / 1000;
            System.out.println("CBench mark for '" + args[0] + "': " + time + "s");
        }
        catch (Exception ex)
        {
            System.out.println("Benchmark failed. No timing available.");
        }
    }

    /**
     * Print a usage message to stdout
     */
    private static void usage()
    {
        System.out.println("Usage: CBench [<version>] [disk]");
        System.out.println("  where <version> is the name of a version to benchmark.");
        System.out.println("  and 'disk' specifies if the Raw version should not cache data.");
        System.out.println("  Remember to quote the version name if it includes spaces.");
    }

    /**
     * List the available versions
     */
    private static void versions()
    {
        System.out.println("  Available versions:");
        List lbmds = Books.getBooks();
        for (Iterator it = lbmds.iterator(); it.hasNext();)
        {
            BibleMetaData bmd = (BibleMetaData) it.next();
            System.out.println("    " + bmd.getName());
        }
    }
}
