package org.crosswire.jsword.book;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.crosswire.common.config.Config;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseBase;
import org.crosswire.jsword.util.Project;
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
        // Load JAXB so we don't mess up any timings later
        Logger.outputInfoMinimum();
        JAXBUtil.getJAXBContext();

        // Load the desktop configuration so we can find the sword drivers
        Config config = new Config("Desktop Options");
        Document xmlconfig = Project.resource().getDocument("config");
        config.add(xmlconfig);
        config.setProperties(Project.resource().getProperties("desktop"));
        config.localToApplication(true);

        // Loop through all the Dictionaries
        List dicts = Books.getBooks(BookFilters.getDictionaries());
        for (Iterator dit = dicts.iterator(); dit.hasNext();)
        {
            DictionaryMetaData dmd = (DictionaryMetaData) dit.next();

            if (dmd.getInitials().compareTo("j") < 1)
            {
                continue;
            }

            Dictionary dict = dmd.getDictionary();
            SortedSet set = dict.getIndex(null);

            Iterator it = set.iterator();
            testReadMultiple(dmd, it);
        }

        // Loop through all the Commentaries
        List comments = Books.getBooks(BookFilters.getCommentaries());
        for (Iterator cit = comments.iterator(); cit.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) cit.next();
            Iterator it = new KeyIterator(WHOLE.verseIterator());
            testReadMultiple(bmd, it);
        }

        // Loop through all the Bibles
        List bibles = Books.getBooks(BookFilters.getBibles());
        for (Iterator bit = bibles.iterator(); bit.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) bit.next();
            Iterator it = new KeyIterator(WHOLE.verseIterator());
            testReadMultiple(bmd, it);
        }
    }

    /**
     * Perform a test read on an iterator over a set of keys
     */
    private static void testReadMultiple(BookMetaData bmd, Iterator it)
    {
        DataPolice.setBook(bmd);

        //log.info("Testing: "+bmd.getInitials()+"="+bmd.getFullName());
        long start = System.currentTimeMillis();
        int entries = 0;

        Book book = bmd.getBook();
        while (it.hasNext())
        {
            Key key = (Key) it.next();
            testReadSingle(bmd, book, key);
            entries++;
        }

        long end = System.currentTimeMillis();
        float time = (end - start) / 1000F;

        log.info("Tested: book="+bmd.getInitials()+" entries="+entries+" time="+time+"s ("+(1000*time/entries)+"ms per entry)");
    }

    /**
     * Perform a test read on a single key
     */    
    private static void testReadSingle(BookMetaData bmd, Book bible, Key key)
    {
        try
        {
            //log.debug("reading: "+bmd.getInitials()+"/"+key.getText());

            BookData data = bible.getData(key);
            if (data.getPlainText() == null)
            {
                log.warn("No output from: "+bmd.getInitials()+", "+key.getText());
            }

            // This might be a useful extra test, except that a failure gives you no help at all.
            // data.validate();
        }
        catch (BookException ex)
        {
            log.warn("Failed to read: "+bmd.getInitials()+", "+key.getText()+", reason: "+ex.getMessage(), ex);
        }
        /*
        catch (ValidationException ex)
        {
            log.warn("Validation error reading: "+bmd.getInitials()+", "+key.getText()+", reason: "+ex.getMessage(), ex);
        }
        */
        catch (Throwable ex)
        {
            log.error("Unexpected error reading: "+bmd.getInitials()+", "+key.getText(), ex);
        }
    }

    private static class KeyIterator implements Iterator
    {
        /**
         * Simple ctor
         */
        public KeyIterator(Iterator orig)
        {
            this.orig = orig;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove()
        {
            orig.remove();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return orig.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next()
        {
            VerseBase vb = (VerseBase) orig.next();
            Passage fetch = PassageFactory.createPassage();
            fetch.add(vb);
            return new PassageKey(fetch);
        }

        private Iterator orig;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ReadEverything.class);

    /**
     * To allow us to iterate over the whole Bible
     */
    private static Passage WHOLE = PassageFactory.getWholeBiblePassage();
}
