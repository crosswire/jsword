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
package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Key;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BooksTest extends TestCase
{
    public BooksTest(String s)
    {
        super(s);
    }

    private static final Logger log = Logger.getLogger(BooksTest.class);
    protected Key[] gen11 = null;
    protected BookMetaData[] bmds = null;
    protected Book[] bibles = null;

    protected Class[] ignorebibles =
    {
    };

    protected void setUp() throws Exception
    {
        List lbmds = Books.installed().getBooks(BookFilters.getBibles());
        int numBibles = lbmds.size();
        bibles = new Book[numBibles];
        bmds = new BookMetaData[numBibles];
        gen11 = new Key[numBibles];

        int i = 0;
        for (Iterator it = lbmds.iterator(); it.hasNext();)
        {
            bibles[i] = (Book) it.next();
            bmds[i] = bibles[i].getBookMetaData();
            gen11[i] = bibles[i].getKey("Gen 1:1"); //$NON-NLS-1$
            i++;
        }
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetBible()
    {
        for (int i = 0; i < bibles.length; i++)
        {
            Book bible = bibles[i];
            log.debug("testing bible: " + bible.getFullName()); //$NON-NLS-1$
            assertTrue(bible != null);
        }
    }

    public void testGetBibleMetaData() throws Exception
    {
        for (int i = 0; i < bibles.length; i++)
        {
            Book bible = bibles[i];
            BookMetaData bmd = bible.getBookMetaData();
            assertEquals(bmd, bmds[i]);
        }
    }

    public void testMetaData() throws Exception
    {
        for (int i = 0; i < bmds.length; i++)
        {
            BookMetaData bmd = bmds[i];

            assertTrue(bmd.getFullName() != null);
            assertTrue(bmd.getFullName().length() > 0);
            assertTrue(bmd.getInitials() != null);
            assertTrue(bmd.getInitials().length() > 0);
            assertTrue(bmd.getName() != null);
            assertTrue(bmd.getName().length() > 0);
            assertTrue(bmd.getFullName().length() > bmd.getName().length());
            assertTrue(bmd.getName().length() > bmd.getInitials().length());
        }
    }

    public void testGetBookMetaData() throws Exception
    {
        for (int i = 0; i < bibles.length; i++)
        {
            Book bible = bibles[i];
            BookMetaData bmd = bible.getBookMetaData();
            assertEquals(bmd, bmds[i]);
        }
    }

    public void testGetDataKey() throws Exception
    {
        for (int i = 0; i < bibles.length; i++)
        {
            Book bible = bibles[i];
            Key key = bible.getKey("Gen 1:1"); //$NON-NLS-1$
            BookData data = bible.getData(key);
            assertNotNull(data);
        }
    }

    public void testGetDataPassage() throws Exception
    {
        for (int i = 0; i < bibles.length; i++)
        {
            Book bible = bibles[i];
            BookData data = bible.getData(gen11[i]);
            assertNotNull(data);
        }
    }

    /* FIXME: These are only valid if all bibles are English
    public void testGetFind() throws Exception
    {
        // This only checks that find() does something vaguely sensible
        // I assume that find() just calls findPassage(), where the real tests are
        for (int i = 0; i < bibles.length; i++)
        {
            Book bible = bibles[i];
            Key key = bible.find(new Search("aaron", false)); //$NON-NLS-1$
            assertNotNull("bible=" + bible.getFullName(), key); //$NON-NLS-1$
        }
    }

	FIXME: These are only valid if all bibles are English
    public void testFindPassage() throws Exception
    {
        for (int i = 0; i < bibles.length; i++)
        {
            Book ver = bibles[i];

            Key key = ver.find(new Search("aaron", false)); //$NON-NLS-1$
            assertTrue(key != null);
        }
    }

	FIXME: These are only valid if all bibles are English
    public void testFindPassage2() throws Exception
    {
        for (int i = 0; i < bibles.length; i++)
        {
            // Check that this is a type that we expect to return real Bible data
            Book ver = bibles[i];
            boolean skip = false;
            for (int j = 0; j < ignorebibles.length; j++)
            {
                // if (ver instanceof fullbibles[j])
                if (ignorebibles[j].isAssignableFrom(ver.getClass()))
                    skip = true;
            }
            if (skip)
                continue;
            log.debug("thorough testing bible: " + ver.getFullName()); //$NON-NLS-1$

            Key key = ver.find(new Search("aaron", false)); //$NON-NLS-1$
            Passage ref = KeyUtil.getPassage(key);
            assertTrue(ref.countVerses() > 10);
            key = ver.find(new Search("jerusalem", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            assertTrue(ref.countVerses() > 10);
            key = ver.find(new Search("god", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            assertTrue(ref.countVerses() > 10);
            key = ver.find(new Search("GOD", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            assertTrue(ref.countVerses() > 10);
            key = ver.find(new Search("brother's", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            assertTrue(ref.countVerses() > 2);
            key = ver.find(new Search("BROTHER'S", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            assertTrue(ref.countVerses() > 2);

            key = ver.find(new Search("maher-shalal-hash-baz", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            if (ref.isEmpty())
            {
                key = ver.find(new Search("mahershalalhashbaz", false)); //$NON-NLS-1$
                ref = KeyUtil.getPassage(key);
            }
            if (ref.isEmpty())
            {
                key = ver.find(new Search("maher*", false)); //$NON-NLS-1$
                ref = KeyUtil.getPassage(key);
            }
            assertEquals(ref.countVerses(), 2);
            assertEquals(ref.getVerseAt(0), new Verse("Isa 8:1")); //$NON-NLS-1$
            assertEquals(ref.getVerseAt(1), new Verse("Isa 8:3")); //$NON-NLS-1$

            key = ver.find(new Search("MAHER-SHALAL-HASH-BAZ", false)); //$NON-NLS-1$
            ref = KeyUtil.getPassage(key);
            if (ref.isEmpty())
            {
                key = ver.find(new Search("MAHERSHALALHASHBAZ", false)); //$NON-NLS-1$
                ref = KeyUtil.getPassage(key);
            }
            assertEquals(ref.countVerses(), 2);
            assertEquals(ref.getVerseAt(0), new Verse("Isa 8:1")); //$NON-NLS-1$
            assertEquals(ref.getVerseAt(1), new Verse("Isa 8:3")); //$NON-NLS-1$
        }
    }
	*/
	/*
    public void testGetStartsWith() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            Bible ver = bibles[i];
            
            if (ver instanceof SearchableBible)
            {
                String[] sa = Sentance2Util.toStringArray(((SearchableBible) ver).getSearcher().getStartsWith("a"));
                assertTrue(sa != null);
            }
        }
    }

    public void testGetStartsWith2() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
            // Check that this is a type that we expect to return real Bible data
            Bible origver = bibles[i];
            boolean skip = false;
            for (int j=0; j<ignorebibles.length; j++)
            {
                // if (ver instanceof fullbibles[j])
                if (origver.getClass().isAssignableFrom(ignorebibles[j]))
                    skip = true;
            }
            if (skip) continue;
            log.debug("thorough testing bible: "+origver.getBibleMetaData().getFullName());

            if (origver instanceof SearchableBible)
            {
                SearchableBible ver = (SearchableBible) origver;
                String[] sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("jos"));
                assertTrue(sa.length > 5);
                sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("jerusale"));
                assertEquals(sa[0], "jerusalem");
                sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("maher-shalal"));
                if (sa.length == 0)
                {
                    sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("mahershalal"));
                    assertEquals(sa[0], "mahershalalhashbaz");
                }
                else
                {
                    assertEquals(sa[0], "maher-shalal-hash-baz");
                }
                assertEquals(sa.length, 1);
                sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("MAHER-SHALAL"));
                if (sa.length == 0)
                {
                    sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("MAHERSHALAL"));
                    assertEquals(sa[0], "mahershalalhashbaz");
                }
                else
                {
                    assertEquals(sa[0], "maher-shalal-hash-baz");
                }
                assertEquals(sa.length, 1);
                sa = Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("XXX"));
                assertEquals(sa.length, 0);
            }
        }
    }
    */
}