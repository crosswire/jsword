package org.crosswire.jsword.book.sword;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.JAXBUtil;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.basic.ReadOnlyKeyList;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisTextType;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * A Sword version of Dictionary.
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
public class SwordDictionary extends AbstractBook
{
    /**
     * Start and to as much checking as we can without using memory.
     * (i.e. actually reading the indexes)
     */
    protected SwordDictionary(SwordBookDriver driver, SwordConfig config, Backend backend, BookType type) throws MalformedURLException, ParseException
    {
        Properties prop = config.getProperties();
        prop.setProperty(BookMetaData.KEY_EDITION, "");
        prop.setProperty(BookMetaData.KEY_NAME, config.getDescription());
        prop.setProperty(BookMetaData.KEY_OPENNESS, Openness.UNKNOWN.getName());
        prop.setProperty(BookMetaData.KEY_SPEED, Integer.toString(Books.SPEED_FAST));
        prop.setProperty(BookMetaData.KEY_TYPE, type.getName());

        BookMetaData bmd = new DefaultBookMetaData(driver, this, prop);
        setBookMetaData(bmd);

        initSearchEngine();

        this.config = config;
        this.backend = backend;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        super.activate(lock);

        set = backend.readIndex();

        map = new HashMap();
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            Key key = (Key) it.next();
            map.put(key.getName(), key);
        }

        global = new ReadOnlyKeyList(set, false);

        // We don't need to activate the backend because it should be capable
        // of doing it for itself.
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        super.deactivate(lock);

        map = null;
        set = null;
        global = null;

        Activator.deactivate(backend);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.passage.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        checkActive();

        if (key == null)
        {
            throw new NullPointerException();
        }

        if (backend == null)
        {
            throw new BookException(Msg.MISSING_BACKEND);
        }

        try
        {
            Osis osis = JAXBUtil.createOsisFramework(getBookMetaData());
            OsisTextType text = osis.getOsisText();

            Div div = JAXBUtil.factory().createDiv();
            div.setDivTitle(key.getName());

            text.getDiv().add(div);

            byte[] data = backend.getRawText(key);
            String charset = config.getModuleCharset();
            String txt = null;
            try
            {
                txt = new String(data, charset);
            }
            catch (UnsupportedEncodingException ex)
            {
                // It is impossible! In case, use system default...
                log.error("Encoding: " + charset + " not supported", ex);
                txt = new String(data);
            }

            config.getFilter().toOSIS(div, txt);

            BookData bdata = new BookData(osis);
            return bdata;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public KeyList getGlobalKeyList()
    {
        checkActive();

        return global;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String text) throws NoSuchKeyException
    {
        checkActive();

        Key key = (Key) map.get(text);
        if (key == null)
        {
            // So we need to find a matching key.

            // First check for keys that match ignoring case
            for (Iterator it = map.keySet().iterator(); it.hasNext();)
            {
                key = (Key) it.next();
                String match = key.getName();
                if (match.equalsIgnoreCase(text))
                {
                    return key;
                }
            }

            // Next keys that start with the given text
            for (Iterator it = map.keySet().iterator(); it.hasNext();)
            {
                key = (Key) it.next();
                String match = key.getName();
                if (match.startsWith(text))
                {
                    return key;
                }
            }

            // Next try keys that contain the given text
            for (Iterator it = map.keySet().iterator(); it.hasNext();)
            {
                key = (Key) it.next();
                String match = key.getName();
                if (match.indexOf(text) != -1)
                {
                    return key;
                }
            }

            throw new NoSuchKeyException(Msg.NO_KEY, new Object[] { text });
        }

        return key;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    private final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * The global key list
     */
    private KeyList global;

    /**
     * Are we active
     */
    private boolean active = false;

    /**
     * So we can quickly find a Key given the text for the key
     */
    private Map map = null;
    
    /**
     * So we can implement getIndex() easily
     */
    private KeyList set = null;

    /**
     * To read the data from the disk
     */
    private Backend backend;

    /**
     * The Sword configuration file
     */
    private SwordConfig config;

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(SwordDictionary.class);
}
