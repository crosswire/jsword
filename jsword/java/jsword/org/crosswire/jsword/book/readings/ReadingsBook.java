package org.crosswire.jsword.book.readings;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.PreferredKey;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.SetKeyList;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Element;

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
public class ReadingsBook extends AbstractBook implements PreferredKey
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

        Locale defaultLocale = Locale.getDefault();
        ResourceBundle prop = ResourceBundle.getBundle(setname, defaultLocale, new CWClassLoader(ReadingsBookDriver.class));

        // We use 1972 because it is a leap year.
        GregorianCalendar greg = new GregorianCalendar(1972, Calendar.JANUARY, 1);
        while (greg.get(Calendar.YEAR) == 1972)
        {
            String key = KEYBASE + (1 + greg.get(Calendar.MONTH)) + "." + greg.get(Calendar.DATE); //$NON-NLS-1$
            String readings = ""; //$NON-NLS-1$

            try
            {
                readings = prop.getString(key);
            }
            catch (MissingResourceException e)
            {
                log.warn("Missing resource: " + key + " while parsing: " + setname); //$NON-NLS-1$ //$NON-NLS-2$
            }

            hash.put(new ReadingsKey(greg.getTime()), readings);

            greg.add(Calendar.DATE, 1);
        }

        global = new SetKeyList(hash.keySet(), getBookMetaData().getName());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.PreferredKey#getPreferred()
     */
    public Key getPreferred()
    {
        GregorianCalendar now = new GregorianCalendar();
        now.setTime(new Date());

        GregorianCalendar greg = new GregorianCalendar(1972, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        return new ReadingsKey(greg.getTime());
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
            Element osis = OSISUtil.createOsisFramework(getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);

            Element div = OSISUtil.factory().createDiv();
            Element title = OSISUtil.factory().createTitle();
            title.addContent(Msg.HEADING.toString(key.getName()));
            div.addContent(title);
            text.addContent(div);

            String readings = (String) hash.get(key);
            if (readings == null)
            {
                throw new BookException(Msg.NOT_FOUND, new Object[] { key.getName() });
            }

            try
            {
                KeyFactory keyf = PassageKeyFactory.instance();
                Passage ref = (Passage) keyf.getKey(readings);

                Element list = OSISUtil.factory().createList();
                div.getContent().add(list);
                for (Iterator it = ref.rangeIterator(RestrictionType.NONE); it.hasNext(); )
                {
                    VerseRange range = (VerseRange) it.next();

                    Element reading = OSISUtil.factory().createReference();
                    reading.setAttribute(OSISUtil.ATTRIBUTE_REFERENCE_OSISREF, range.getOSISName());
                    reading.addContent(range.getName());

                    Element item = OSISUtil.factory().createItem();
                    item.getContent().add(reading);
                    list.getContent().add(item);
                }
            }
            catch (NoSuchVerseException ex)
            {
                div.getContent().add(Msg.DECODE_ERROR.toString(readings));
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
     * @see org.crosswire.jsword.book.Book#getRawData(org.crosswire.jsword.passage.Key)
     */
    public String getRawData(Key key) throws BookException
    {
        StringBuffer buffer = new StringBuffer();
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        DefaultKeyList reply = new DefaultKeyList();
        reply.addAll(new ReadingsKey(name, name, global));
        return reply;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public Key getGlobalKeyList()
    {
        return global;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public Key createEmptyKeyList()
    {
        return new DefaultKeyList();
    }

    /**
     * The global key list
     */
    private Key global = null;

    /**
     * The base for the keys in the properties file.
     */
    private static final String KEYBASE = "readings."; //$NON-NLS-1$

    /**
     * The store of keys and data
     */
    private Map hash = new TreeMap();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ReadingsBook.class);
}
