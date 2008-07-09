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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.passage.Key;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Test to check that all books can be read.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ReadEverything
{
    /**
     * Prevent instantiation
     */
    private ReadEverything()
    {
    }

    /**
     * Read all the books that we can get our hands on.
     */
    public static void main(String[] args) throws IOException, JDOMException
    {
        Logger.outputEverything();

        // This must be the first static in the program.
        // To ensure this we place it at the top of the class!
        // This will set it as a place to look for overrides for
        // ResourceBundles, properties and other resources
        CWProject.setHome("jsword.home", ".jsword", "JSword"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // And the array of allowed osis>html converters
        ChoiceFactory.getDataMap().put("converters", new String[] {}); //$NON-NLS-1$

        // The choice of configurable XSL stylesheets
        ChoiceFactory.getDataMap().put("cswing-styles", new String[] {}); //$NON-NLS-1$

        // Load the desktop configuration so we can find the sword drivers
        Config config = new Config("Desktop Options"); //$NON-NLS-1$
        Document xmlconfig = XMLUtil.getDocument("config"); //$NON-NLS-1$

        Locale defaultLocale = Locale.getDefault();
        ResourceBundle configResources = ResourceBundle.getBundle("config", defaultLocale, CWClassLoader.instance(ReadEverything.class)); //$NON-NLS-1$

        config.add(xmlconfig, configResources);

        config.setProperties(ResourceUtil.getProperties("desktop")); //$NON-NLS-1$
        config.localToApplication();

        // Loop through all the Bookks
        log.warn("*** Reading all known Books"); //$NON-NLS-1$
        List comments = Books.installed().getBooks();
        for (Iterator cit = comments.iterator(); cit.hasNext();)
        {
            Book book = (Book) cit.next();

            log.warn("****** Reading: " + book.getInitials()); //$NON-NLS-1$

            Key set = book.getGlobalKeyList();

            testReadMultiple(book, set);
        }
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void testReadMultiple(Book book, Key set)
    {
        DataPolice.setBook(book.getBookMetaData());

        //log.info("Testing: "+bmd.getInitials()+"="+bmd.getName());
        long start = System.currentTimeMillis();
        int entries = 0;

        Iterator it = set.iterator();
        while (it.hasNext())
        {
            testReadSingle(book, (Key) it.next());

            entries++;
        }

        long end = System.currentTimeMillis();
        float time = (end - start) / 1000F;

        log.info("Tested: book="+book.getInitials()+" entries="+entries+" time="+time+"s ("+(1000*time/entries)+"ms per entry)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

    /**
     * Perform a test read on a single key
     */    
    private static void testReadSingle(Book book, Key key)
    {
        try
        {
            //log.debug("reading: "+bmd.getInitials()+"/"+key.getText());

            BookData data = new BookData(book, key);
            if (data.getOsisFragment() == null)
            {
                log.warn("No output from: "+book.getInitials()+", "+key.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // This might be a useful extra test, except that a failure gives you no help at all.
            //data.validate();
        }
        catch (Throwable ex)
        {
            log.error("Unexpected error reading: "+book.getInitials()+", "+key.getName(), ex); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ReadEverything.class);
}
