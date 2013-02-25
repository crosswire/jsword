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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gather all references.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class GatherAllReferences {
    /**
     * Prevent instantiation
     */
    private GatherAllReferences() {
    }

    /**
     * Read all the books that we can get our hands on.
     */
    public static void main(String[] args) throws IOException, JDOMException {
        out = new PrintWriter(new BufferedWriter(new FileWriter("passages.log")));
        // Calling Project.instance() will set up the project's home directory
        // ~/.jsword
        // This will set it as a place to look for overrides for
        // ResourceBundles, properties and other resources
        CWProject.instance();

        // And the array of allowed osis>html converters
        ChoiceFactory.getDataMap().put("converters", new String[] {});

        // The choice of configurable XSL stylesheets
        ChoiceFactory.getDataMap().put("cswing-styles", new String[] {});

        // Load the desktop configuration so we can find the sword drivers
        Config config = new Config("Desktop Options");
        Document xmlconfig = XMLUtil.getDocument("config");

        Locale defaultLocale = Locale.getDefault();
        ResourceBundle configResources = ResourceBundle.getBundle("config", defaultLocale, CWClassLoader.instance(GatherAllReferences.class));

        config.add(xmlconfig, configResources);

        config.setProperties(ResourceUtil.getProperties("desktop"));
        config.localToApplication();

        // Loop through all the Books
        log.warn("*** Reading all known Books");
        List<Book> comments = Books.installed().getBooks();
        for (Book book : comments) {

            BookMetaData bmd = book.getBookMetaData();
            // Skip PlainText as they do not have references marked up
            if (bmd.getProperty("SourceType") != null)
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
    private static void readBook(Book book, Key set) {
        int[] stats = new int[] {
                0, 0
        };

        for (Key key : set) {
            readKey(book, key, stats);
        }
        log.warn(book.getInitials() + ':' + stats[0] + ':' + stats[1]);

    }

    /**
     * Perform a test read on a single key
     */
    private static void readKey(Book book, Key key, int[] stats) {
        try {
            String orig;
            try {
                orig = book.getRawText(key);
            } catch (BookException ex) {
                log.warn("Failed to read: " + book.getInitials() + '(' + key.getName() + "):" + ex.getMessage(), ex);
                return;
            }

            Matcher matcher = null;
            if (orig.indexOf("passage=\"") != -1) {
                matcher = thmlPassagePattern.matcher(orig);
            } else if (orig.indexOf("osisRef=\"") != -1) {
                matcher = osisPassagePattern.matcher(orig);
            } else if (orig.indexOf("<RX>") != -1) {
                matcher = gbfPassagePattern.matcher(orig);
            }

            if (matcher != null) {
                while (matcher.find()) {
                    String rawRef = matcher.group(1);
                    stats[0]++;
                    String message = book.getInitials() + ':' + key.getOsisRef() + '/' + rawRef;
                    try {
                        Key ref = book.getKey(rawRef);
                        message += '/' + ref.getOsisRef();
                    } catch (NoSuchKeyException e) {
                        message += '!' + e.getMessage();
                        stats[1]++;
                    }

                    out.println(message);
                }
            }

        } catch (Throwable ex) {
            log.error("Unexpected error reading: " + book.getInitials() + '(' + key.getName() + ')', ex);
        }
    }

    private static Pattern thmlPassagePattern = Pattern.compile("passage=\"([^\"]*)");
    private static Pattern gbfPassagePattern = Pattern.compile("<RX>([^<]*)");
    private static Pattern osisPassagePattern = Pattern.compile("osisRef=\"([^\"]*)");
    private static PrintWriter out;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(GatherAllReferences.class);
}
