
package org.crosswire.jsword.book.readings;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractDictionary;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
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
public class ReadingsDictionary extends AbstractDictionary
{
    /**
     * Constructor ReadingsDictionary.
     */
    public ReadingsDictionary(ReadingsDictionaryMetaData dmd)
    {
        this.dmd = dmd;

        String name = ReadingsBookDriver.getReadingsSet();

        Properties prop;
        try
        {
            prop = Project.resource().getReadingsSet(name);
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
                log.warn("Missing resource: "+key+" while parsing: "+name);
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
            log.warn("Extra resource: "+key+"="+val+" while parsing: "+name);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getDictionaryMetaData()
     */
    public DictionaryMetaData getDictionaryMetaData()
    {
        return dmd;
    }

    /**
     * We are slightly evil here in that we ignore the startswith paramter and
     * just return the whole list anyway.
     * startswith was a bit strange when the keys are dates.
     * @see org.crosswire.jsword.book.Dictionary#getIndex(java.lang.String)
     */
    public SortedSet getIndex(String startswith)
    {
        SortedSet keys = new TreeSet();
        keys.addAll(hash.keySet());
        return keys;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getKey(java.lang.String)
     */
    public Key getKey(String text) throws BookException
    {
        try
        {
            return new ReadingsKey(text);
        }
        catch (ParseException ex)
        {
            throw new BookException(Msg.PARSE_FAIL, ex, new Object[] { text });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getKeyFuzzy(java.lang.String)
     */
    public Key getKeyFuzzy(String text) throws BookException
    {
        return getKey(text);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        String readings = (String) hash.get(key);
        if (readings == null)
        {
            throw new BookException(Msg.NOT_FOUND, new Object[] { key.getText() });
        }

        try
        {
            String osisid = dmd.getInitials();
            Osis osis = JAXBUtil.factory().createOsis();

            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);

            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);

            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Readings."+osisid);
            text.setHeader(header);

            osis.setOsisText(text);

            Div div = JAXBUtil.factory().createDiv();
            div.setDivTitle("Readings for "+key.getText());
            text.getDiv().add(div);

            try
            {
                Passage ref = PassageFactory.createPassage(readings);
                for (Iterator it = ref.rangeIterator(PassageConstants.RESTRICT_CHAPTER); it.hasNext();)
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

            BookData bdata = new BookData(osis);
            return bdata;
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.Search)
     */
    public Key find(Search search) throws BookException
    {
        return null;
    }

    /**
     * The base for the keys in the properties file.
     */
    private static final String KEYBASE = "readings.";

    /**
     * The store of keys and data
     */
    private Map hash = new HashMap(); 

    /**
     * Our meta-data entry
     */
    private ReadingsDictionaryMetaData dmd;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ReadingsDictionary.class);
}
