
package org.crosswire.jsword.book.test;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;

/**
 * Speed is a simple benchmark that tests how fast a version is. The
 * current set of tasks that we perform are rather arbitry (sp?).
 * But that is something that we can improve on when we have more
 * usage information.
 *
 * <p>Progress report. All builds are Debug unless *ed:
 * <pre>
 * Date          Bible       VM              Time/s
 * 1999.12.08    Raw (Mem)   HS 1.0.1         20
 * 1999.12.08    Raw (Mem)   MVM 5.00.3167   541
 * 1999.12.09    Raw (Disk)  HS 1.0.1       >600
 * 1999.12.10    Ser         HS 1.0.1         78
 * 1999.12.11    Ser         HS 1.0.1          6.7
 * 1999.12.11    Raw (Mem)   HS 1.0.1         11
 * 1999.12.11    Raw (Disk)  HS 1.0.1       1072
 * 1999.12.11    Ser         MVM 5.00.3167     8
 * 1999.12.12    Ser         HS 1.0.1          4
 * 1999.12.12    Ser *       HS 1.0.1          3
 * </pre>
 * </p>
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
public class Speed implements Runnable
{
    /**
     * Basic constructor
     */
    public Speed(Book book)
    {
        this.book = book;
    }

    /**
     * This is what to call to execute a benchmark
     */
    public void run()
    {
        try
        {
            start_time = System.currentTimeMillis();

            PassageTally tally;

            // Part 1, a best match, and doc generate
            tally = (PassageTally) book.find(new Search("In the beginning god created the heavens and the earth", true));
            tally.trimVerses(35);
            dummyDisplay(tally);
            tally = null;

            // Part 2, another best match, and doc generate
            tally = (PassageTally) book.find(new Search("for god so loves the world that he gave his only begotten son", true));
            tally.trimVerses(35);
            dummyDisplay(tally);
            tally = null;

            // Part 3, a power match, and doc generate
            String next_input = book.find(new Search("aaron & manna", false)).getName();
            Passage ref = PassageFactory.createPassage(next_input);
            ref.trimVerses(35);
            dummyDisplay(ref);
            ref = null;

            end_time = System.currentTimeMillis();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Dummy display routine. We might want to add some XSL styling to this.
     * @param ref The passage to format for display
     */
    private void dummyDisplay(Passage ref) throws Exception
    {
        if (ref == null)
        {    
            throw new NullPointerException("Null Passage in dummyDisplay.");
        }

        book.getData(ref);
    }

    /**
     * Accessor for the version that we are testing
     */
    public long getBenchmark()
    {
        if (start_time == 0 || end_time == 0)
        {    
            throw new IllegalStateException("The benchmark has not finished yet.");
        }

        return end_time - start_time;
    }

    /**
     * Accessor for the version that we are testing
     */
    public Book getBook()
    {
        return book;
    }

    /**
     * Accessor for the version that we are testing
     */
    public void setBook(Book book)
    {
        this.book = book;
    }

    /**
     * The start time of the benchmark
     */
    private long start_time = 0;

    /**
     * The end time of the benchmark
     */
    private long end_time = 0;

    /**
     * The version to test
     */
    private Book book;
}
