package org.crosswire.jsword.book;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.config.Config;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.passage.Key;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Test to check that all modules can be read.
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
public class ReadEverything
{
    /**
     * Read all the modules that we can get our hands on.
     */
    public static void main(String[] args) throws IOException, JDOMException
    {
        Logger.outputInfoMinimum();

        // Load the desktop configuration so we can find the sword drivers
        Config config = new Config("Desktop Options"); //$NON-NLS-1$
        Document xmlconfig = XMLUtil.getDocument("config"); //$NON-NLS-1$
        config.add(xmlconfig, null);
        config.setProperties(ResourceUtil.getProperties("desktop")); //$NON-NLS-1$
        config.localToApplication(true);

        // Loop through all the Bookks
        log.info("*** Reading all known Books"); //$NON-NLS-1$
        List comments = Books.installed().getBookMetaDatas();
        for (Iterator cit = comments.iterator(); cit.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) cit.next();

            Book book = bmd.getBook();
            Key set = book.getGlobalKeyList();

            testReadMultiple(bmd, book, set);
        }
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void testReadMultiple(BookMetaData bmd, Book book, Key set)
    {
        DataPolice.setBook(bmd);

        //log.info("Testing: "+bmd.getInitials()+"="+bmd.getFullName());
        long start = System.currentTimeMillis();
        int entries = 0;

        Iterator it = set.iterator();
        while (it.hasNext())
        {
            Key subset = (Key) it.next();
            if (subset.canHaveChildren())
            {
                testReadSingle(bmd, book, subset);
            }
            else
            {
                testReadSingle(bmd, book, subset);
            }

            entries++;
        }

        long end = System.currentTimeMillis();
        float time = (end - start) / 1000F;

        log.info("Tested: book="+bmd.getInitials()+" entries="+entries+" time="+time+"s ("+(1000*time/entries)+"ms per entry)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

    /**
     * Perform a test read on a single key
     */    
    private static void testReadSingle(BookMetaData bmd, Book book, Key key)
    {
        try
        {
            //log.debug("reading: "+bmd.getInitials()+"/"+key.getText());

            BookData data = book.getData(key);
            if (data.getPlainText() == null)
            {
                log.warn("No output from: "+bmd.getInitials()+", "+key.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // LATER(joe): put in a test for invalid characters

            // This might be a useful extra test, except that a failure gives you no help at all.
            //data.validate();
        }
        /*
        catch (ValidationException ex)
        {
            log.warn("Validation error reading: "+bmd.getInitials()+", "+key.getText()+", code:"+ex.getErrorCode()+" reason: "+ex.getMessage());
        }
        */
        catch (BookException ex)
        {
            log.warn("Failed to read: "+bmd.getInitials()+", "+key.getName()+", reason: "+ex.getMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        catch (Throwable ex)
        {
            log.error("Unexpected error reading: "+bmd.getInitials()+", "+key.getName(), ex); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ReadEverything.class);
}
