import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.test.Speed;

/**
 * Bench is a command line utility that runs the Speed benchmark program.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Bench
{
    /**
     * Basic constructor
     */
    public static void main(String[] args)
    {
        Book version = null;

        if (args.length == 0)
        {
            usage();
            versions();
            System.exit(1);
        }

        List dicts = Books.installed().getBooks(BookFilters.getOnlyBibles());
        version = (Book) dicts.get(0);

        Speed speed = new Speed(version);
        speed.run();

        float time = speed.getBenchmark() / 1000;
        System.out.println("CBench mark for '" + args[0] + "': " + time + "s"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Print a usage message to stdout
     */
    private static void usage()
    {
        System.out.println("Usage: CBench [<version>] [disk]"); //$NON-NLS-1$
        System.out.println("  where <version> is the name of a version to benchmark."); //$NON-NLS-1$
        System.out.println("  and 'disk' specifies if the Raw version should not cache data."); //$NON-NLS-1$
        System.out.println("  Remember to quote the version name if it includes spaces."); //$NON-NLS-1$
    }

    /**
     * List the available versions
     */
    private static void versions()
    {
        System.out.println("  Available versions:"); //$NON-NLS-1$
        List lbmds = Books.installed().getBooks();
        for (Iterator it = lbmds.iterator(); it.hasNext();)
        {
            Book book = (Book) it.next();
            System.out.println("    " + book.getName()); //$NON-NLS-1$
        }
    }
}
