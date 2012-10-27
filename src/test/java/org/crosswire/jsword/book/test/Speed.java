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
package org.crosswire.jsword.book.test;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.search.DefaultSearchModifier;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * Speed is a simple benchmark that tests how fast a version is. The current set
 * of tasks that we perform are rather arbitrary. But that is something that we
 * can improve on when we have more usage information.
 * 
 * <p>
 * Progress report. All builds are Debug unless *ed:
 * 
 * <pre>
 * Date          Bible       VM              Time/s
 * 1999.12.08    Raw (Mem)   HS 1.0.1         20
 * 1999.12.08    Raw (Mem)   MVM 5.00.3167   541
 * 1999.12.09    Raw (Disk)  HS 1.0.1       &gt;600
 * 1999.12.10    Ser         HS 1.0.1         78
 * 1999.12.11    Ser         HS 1.0.1          6.7
 * 1999.12.11    Raw (Mem)   HS 1.0.1         11
 * 1999.12.11    Raw (Disk)  HS 1.0.1       1072
 * 1999.12.11    Ser         MVM 5.00.3167     8
 * 1999.12.12    Ser         HS 1.0.1          4
 * 1999.12.12    Ser *       HS 1.0.1          3
 * </pre>
 * 
 * </p>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Speed implements Runnable {
    /**
     * Basic constructor
     */
    public Speed(Book book) {
        this.book = book;
    }

    /**
     * This is what to call to execute a benchmark
     */
    public void run() {
        try {
            start_time = System.currentTimeMillis();


            DefaultSearchModifier modifier = new DefaultSearchModifier();
            modifier.setRanked(true);

            // If ranking see if the results are being limited.
            int rankCount = 35;
            modifier.setMaxResults(rankCount);

            // Part 1, a best match, and doc generate
            String param = "\"In the beginning god created the heavens and the earth\"";
            Key results = book.find(new DefaultSearchRequest(param, modifier));

            // we should get PassageTallys for rank searches
            if (results instanceof PassageTally) {
                PassageTally tally = (PassageTally) results;
                tally.setOrdering(PassageTally.Order.TALLY);
                tally.trimVerses(rankCount);
                dummyDisplay(tally);
                results = null;
            }

            // Part 2, another best match, and doc generate
            param = "\"for god so loves the world that he gave his only begotten son\"";
            if (results instanceof PassageTally) {
                PassageTally tally = (PassageTally) results;
                tally.setOrdering(PassageTally.Order.TALLY);
                tally.trimVerses(rankCount);
                dummyDisplay(tally);
                results = null;
            }

            // Part 3, a power match, and doc generate
            String nextInput = book.find("aaron & manna").getName();
            Key key = book.getKey(nextInput);
            Passage ref = KeyUtil.getPassage(key,  Versifications.instance().getDefaultVersification());
            ref.trimVerses(35);
            dummyDisplay(ref);
            ref = null;

            end_time = System.currentTimeMillis();
        } catch (BookException ex) {
            Reporter.informUser(this, ex);
        } catch (NoSuchKeyException ex) {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Dummy display routine. We might want to add some XSL styling to this.
     * 
     * @param ref
     *            The passage to format for display
     * @throws BookException
     */
    private void dummyDisplay(Passage ref) throws BookException {
        new BookData(book, ref).getOsisFragment();
    }

    /**
     * Accessor for the version that we are testing
     */
    public long getBenchmark() {
        if (start_time == 0 || end_time == 0) {
            throw new IllegalStateException("The benchmark has not finished yet.");
        }

        return end_time - start_time;
    }

    /**
     * Accessor for the version that we are testing
     */
    public Book getBook() {
        return book;
    }

    /**
     * Accessor for the version that we are testing
     */
    public void setBook(Book book) {
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
