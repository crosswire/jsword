package org.crosswire.jsword.book.readings;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.JAXBUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisTextType;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.SetKeyList;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.util.Project;

/**
 * A Dictionary that displays daily Readings.
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
public class ReadingsBook extends AbstractBook
{
    /**
     * Constructor for ReadingsBook.
     */
    public ReadingsBook(ReadingsBookDriver driver, String name, BookType type, int speed)
    {
        BookMetaData bmd = new DefaultBookMetaData(driver, this, name, type, speed);
        setBookMetaData(bmd);

        initSearchEngine();

        String setname = ReadingsBookDriver.getReadingsSet();

        Properties prop;
        try
        {
            prop = Project.instance().getReadingsSet(setname);
        }
        catch (Exception ex)
        {
            prop = new Properties();
            log.error("Failed to read readings set", ex);
        }

        /*String title = (String)*/ prop.remove("title");

        // We use 1972 because it is a leap year.
        GregorianCalendar greg = new GregorianCalendar(1972, Calendar.JANUARY, 1);
        while (greg.get(Calendar.YEAR) == 1972)
        {
            String key = KEYBASE + (1+greg.get(Calendar.MONTH)) + "." + greg.get(Calendar.DATE);
            String readings = (String) prop.remove(key);
            if (readings == null)
            {
                log.warn("Missing resource: "+key+" while parsing: "+setname);
                readings = "";
            }

            hash.put(new ReadingsKey(greg.getTime()), readings);

            greg.add(Calendar.DATE, 1);
        }

        // Anything left is probably in error
        for (Iterator it = prop.keySet().iterator(); it.hasNext();)
        {
            String key = (String) it.next();
            String val = prop.getProperty(key);
            log.warn("Extra resource: "+key+"="+val+" while parsing: "+setname);
        }

        global = new SetKeyList(hash.keySet(), getBookMetaData().getName());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.passage.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        if (!(key instanceof ReadingsKey))
        {
            throw new BookException(Msg.NOT_FOUND, new Object[] { key.getName() });
        }

        try
        {
            Osis osis = JAXBUtil.createOsisFramework(getBookMetaData());
            OsisTextType text = osis.getOsisText();

            Div div = JAXBUtil.factory().createDiv();
            div.setDivTitle("Readings for "+key.getName());
            text.getDiv().add(div);

            String readings = (String) hash.get(key);
            if (readings == null)
            {
                throw new BookException(Msg.NOT_FOUND, new Object[] { key.getName() });
            }

            try
            {
                Passage ref = PassageFactory.createPassage(readings);
                for (Iterator it = ref.rangeIterator(PassageConstants.RESTRICT_NONE); it.hasNext();)
                {
                    VerseRange range = (VerseRange) it.next();

                    Div reading = JAXBUtil.factory().createDiv();
                    reading.setOsisID("bible://"+range.getOSISName());
                    reading.getContent().add(range.getName());

                    div.getContent().add(reading);                
                }
            }
            catch (NoSuchVerseException ex)
            {
                div.getContent().add("Error decoding: "+readings);
            }

            BookData bdata = new BookData(osis, this, key);
            return bdata;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        return new ReadingsKey(name, global);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public KeyList getGlobalKeyList()
    {
        return global;
    }

    /**
     * The global key list
     */
    private KeyList global = null;

    /**
     * The base for the keys in the properties file.
     */
    private static final String KEYBASE = "readings.";

    /**
     * The store of keys and data
     */
    private Map hash = new TreeMap(); 

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ReadingsBook.class);
}
