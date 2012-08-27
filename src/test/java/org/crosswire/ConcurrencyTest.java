package org.crosswire;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.NoSuchKeyException;

public class ConcurrencyTest extends TestCase {

//    public void testConcurrencyIssueOnBookData() throws NoSuchKeyException, BookException,
//            InterruptedException {
//        final String[] names = { "KJV", "ESV" };
//        final String ref = "Rom.1.1";
//
//        final Runnable r1 = new Runnable() {
//            public void run() {
//                final Book b0 = Books.installed().getBook(names[0]);
//                BookData bd1;
//                try {
//                    bd1 = new BookData(b0, b0.getKey(ref));
//                    bd1.getSAXEventProvider();
//                } catch (final NoSuchKeyException e) {
//                    System.err.println("A jsword error during test");
//                    e.printStackTrace();
//                    Assert.fail("JSword bug has occured");
//                } catch (final BookException e) {
//                    System.err.println("A jsword error during test");
//                    e.printStackTrace();
//                    Assert.fail("JSword bug has occured");
//                }
//            }
//        };
//
//        final Runnable r2 = new Runnable() {
//            public void run() {
//                final Book b0 = Books.installed().getBook(names[1]);
//                BookData bd1;
//                try {
//                    bd1 = new BookData(b0, b0.getKey(ref));
//                    bd1.getSAXEventProvider();
//                } catch (final NoSuchKeyException e) {
//                    System.err.println("A jsword error during test");
//                    e.printStackTrace();
//                    Assert.fail("JSword bug has occured");
//                } catch (final BookException e) {
//                    System.err.println("A jsword error during test");
//                    e.printStackTrace();
//                    Assert.fail("JSword bug has occured");
//                }
//            }
//        };
//
//        int ii = 0;
//        while (ii++ < 300) {
//            final Thread t1 = new Thread(r1);
//            final Thread t2 = new Thread(r2);
//            t1.start();
//            t2.start();
//
//            t1.join();
//            t2.join();
//        }
//    }
}
