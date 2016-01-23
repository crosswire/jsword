/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.book;

import org.crosswire.jsword.passage.NoSuchKeyException;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class ConcurrencyTest {

    @Test
    public void testConcurrencyIssueOnBookData() throws InterruptedException {
        final String[] names = { "KJV", "ESV" };
        final String ref = "Rom.1.1";

        final Runnable r1 = new Runnable() {
            public void run() {
                final Book b0 = Books.installed().getBook(names[0]);
                BookData bd1;
                try {
                    bd1 = new BookData(b0, b0.getKey(ref));
                    bd1.getSAXEventProvider();
                } catch (final NoSuchKeyException e) {
                    System.err.println("A jsword error during test");
                    e.printStackTrace();
                    Assert.fail("JSword bug has occured");
                } catch (final BookException e) {
                    System.err.println("A jsword error during test");
                    e.printStackTrace();
                    Assert.fail("JSword bug has occured");
                }
            }
        };

        final Runnable r2 = new Runnable() {
            public void run() {
                final Book b0 = Books.installed().getBook(names[1]);
                BookData bd1;
                try {
                    bd1 = new BookData(b0, b0.getKey(ref));
                    bd1.getSAXEventProvider();
                } catch (final NoSuchKeyException e) {
                    System.err.println("A jsword error during test");
                    e.printStackTrace();
                    Assert.fail("JSword bug has occured");
                } catch (final BookException e) {
                    System.err.println("A jsword error during test");
                    e.printStackTrace();
                    Assert.fail("JSword bug has occured");
                }
            }
        };

        int ii = 0;
        while (ii++ < 50) {
            final Thread t1 = new Thread(r1);
            final Thread t2 = new Thread(r2);
            t1.start();
            t2.start();

            t1.join();
            t2.join();
        }
    }
}
