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
 * ID: $Id: ReadEverything.java 1185 2006-11-13 08:32:18 -0500 (Mon, 13 Nov 2006) dmsmith $
 */
package org.crosswire.jsword.book;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Gather all references.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class GatherAllReferences
{
    /**
     * Prevent instantiation
     */
    private GatherAllReferences()
    {
    }

    /**
     * Read all the books that we can get our hands on.
     */
    public static void main(String[] args) throws IOException, JDOMException
    {
        out = new PrintWriter(new BufferedWriter(new FileWriter("passages.log"))); //$NON-NLS-1$
        // Calling Project.instance() will set up the project's home directory
        //     ~/.jsword
        // This will set it as a place to look for overrides for
        // ResourceBundles, properties and other resources
        CWProject.instance();

        // And the array of allowed osis>html converters
        ChoiceFactory.getDataMap().put("converters", new String[] {}); //$NON-NLS-1$

        // The choice of configurable XSL stylesheets
        ChoiceFactory.getDataMap().put("cswing-styles", new String[] {}); //$NON-NLS-1$

        // Load the desktop configuration so we can find the sword drivers
        Config config = new Config("Desktop Options"); //$NON-NLS-1$
        Document xmlconfig = XMLUtil.getDocument("config"); //$NON-NLS-1$

        Locale defaultLocale = Locale.getDefault();
        ResourceBundle configResources = ResourceBundle.getBundle("config", defaultLocale, CWClassLoader.instance(GatherAllReferences.class)); //$NON-NLS-1$

        config.add(xmlconfig, configResources);

        config.setProperties(ResourceUtil.getProperties("desktop")); //$NON-NLS-1$
        config.localToApplication();

        // Loop through all the Bookks
        log.warn("*** Reading all known Books"); //$NON-NLS-1$
        List comments = Books.installed().getBooks();
        for (Iterator cit = comments.iterator(); cit.hasNext();)
        {
            Book book = (Book) cit.next();

            BookMetaData bmd = book.getBookMetaData();
            // Skip PlainText as they do not have references marked up
            if (bmd.getProperty("SourceType") != null) //$NON-NLS-1$
            {
                Key set = book.getGlobalKeyList();

                readBook(book, set);
            }
        }
        out.flush();
        out.close();
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void readBook(Book book, Key set)
    {
        DataPolice.setBook(book.getBookMetaData());

        int[] stats = new int[] { 0, 0 };

        Iterator it = set.iterator();
        while (it.hasNext())
        {
            readKey(book, (Key) it.next(), stats);
        }
        log.warn(book.getInitials() + ':' + stats[0] + ':' + stats[1]);

    }

    /**
     * Perform a test read on a single key
     */    
    private static void readKey(Book book, Key key, int[] stats)
    {
        try
        {
            String orig;
            try
            {
                orig = book.getRawText(key);
            }
            catch (BookException ex)
            {
                log.warn("Failed to read: " + book.getInitials() + '(' + key.getName() + "):" + ex.getMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }

            Matcher matcher = null;
            if (orig.indexOf("passage=\"") != -1) //$NON-NLS-1$
            {
               matcher = thmlPassagePattern.matcher(orig);
            }
            else if (orig.indexOf("osisRef=\"") != -1) //$NON-NLS-1$
            {
                matcher = osisPassagePattern.matcher(orig);
            }
            else if (orig.indexOf("<RX>") != -1) //$NON-NLS-1$
            {
                matcher = gbfPassagePattern.matcher(orig);
            }

            if (matcher != null)
            {
                while (matcher.find())
                {
                    String rawRef = matcher.group(1);
                    stats[0]++;
                    String message = book.getInitials() + ':' + key.getOsisRef() + '/' + rawRef;
                    try
                    {
                        Key ref = keyf.getKey(rawRef);
                        message += '/' + ref.getOsisRef();
                    }
                    catch (NoSuchKeyException e)
                    {
                        message += '!' + e.getMessage();
                        stats[1]++;
                    }
                    
                    out.println(message);
                }
            }

        }
        catch (Throwable ex)
        {
            log.error("Unexpected error reading: "+book.getInitials()+'(' + key.getName() + ')', ex); //$NON-NLS-1$
        }
    }

    private static Pattern thmlPassagePattern = Pattern.compile("passage=\"([^\"]*)"); //$NON-NLS-1$
    private static Pattern gbfPassagePattern = Pattern.compile("<RX>([^<]*)"); //$NON-NLS-1$
    private static Pattern osisPassagePattern = Pattern.compile("osisRef=\"([^\"]*)"); //$NON-NLS-1$
    private static KeyFactory keyf = PassageKeyFactory.instance();
    private static PrintWriter out;
    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(GatherAllReferences.class);
}
