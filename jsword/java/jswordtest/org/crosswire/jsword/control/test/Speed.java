
package org.crosswire.jsword.control.test;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.control.search.Engine;
import org.crosswire.jsword.control.search.Matcher;
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
public class Speed implements Runnable
{
    /**
     * Basic constructor
     */
    public Speed(Bible version)
    {
        this.version = version;

        engine = new Engine(version);
        match = new Matcher(version);
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
            tally = match.bestMatch("In the beginning god created the heavens and the earth");
            tally.trimVerses(35);
            dummyDisplay(tally);
            tally = null;

            // Part 2, another best match, and doc generate
            tally = match.bestMatch("for god so loves the world that he gave his only begotten son");
            tally.trimVerses(35);
            dummyDisplay(tally);
            tally = null;

            // Part 3, a power match, and doc generate
            String next_input = engine.search("aaron & manna").getName();
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
            throw new NullPointerException("Null Passage in dummyDisplay.");

        version.getData(ref);
    }

    /**
     * Accessor for the version that we are testing
     */
    public long getBenchmark()
    {
        if (start_time == 0 || end_time == 0)
            throw new IllegalStateException("The benchmark has not finished yet.");

        return end_time - start_time;
    }

    /**
     * Accessor for the version that we are testing
     */
    public Bible getBible()
    {
        return version;
    }

    /**
     * Accessor for the version that we are testing
     */
    public void setBible(Bible version)
    {
        this.version = version;
    }

    /** The search engine. Only used by run() but construction is not under test */
    private Engine engine = null;

    /** The matching engine. Only used by run() but construction is not under test */
    private Matcher match = null;

    /** The start time of the benchmark */
    private long start_time = 0;

    /** The end time of the benchmark */
    private long end_time = 0;

    /** The version to test */
    private Bible version;
}
