
package org.crosswire.jsword.book.sword;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Logger;
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
public class SwordDictionary extends AbstractDictionary
{
    /**
     * Start and to as much checking as we can without using memory.
     * (i.e. actually reading the indexes)
     */
    protected SwordDictionary(SwordDictionaryMetaData data, SwordConfig config) throws BookException
    {
        this.config = config;
        this.sdmd = data;

        int ctype = config.getModDrv(); 
        switch (ctype)
        {
        case SwordConstants.DRIVER_RAW_LD:
            backend = new RawKeyBackend(config, 2);
            break;

        case SwordConstants.DRIVER_RAW_LD4:
            backend = new RawKeyBackend(config, 4);
            break;

        case SwordConstants.DRIVER_Z_LD:
            backend = new ZKeyBackend();
            break;

        default:
            throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { new Integer(ctype) });
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        List raw = backend.readIndex();

        map = new HashMap();
        for (Iterator it = raw.iterator(); it.hasNext();)
        {
            Key key = (Key) it.next();
            map.put(key.getText(), key);
        }

        set = new TreeSet(new KeyComparator());
        set.addAll(raw);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        map = null;
        set = null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getDictionaryMetaData()
     */
    public DictionaryMetaData getDictionaryMetaData()
    {
        return sdmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#getIndex(java.lang.String)
     */
    public SortedSet getIndex(String startswith)
    {
        checkActive();

        if (startswith == null)
        {
            return Collections.unmodifiableSortedSet(set);
        }

        return Collections.unmodifiableSortedSet(set.subSet(startswith, startswith+"\u9999"));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#getKey(java.lang.String)
     */
    public Key getKey(String text) throws BookException
    {
        checkActive();

        Key key = (Key) map.get(text);
        if (key == null)
        {
            throw new BookException(Msg.NO_KEY, new Object[] { text });
        }

        return key;
    }


    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.Search)
     */
    public Key find(Search search) throws BookException
    {
        checkActive();

        // URGENT(joe): write
        return getKey("");
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getKeyFuzzy(java.lang.String)
     */
    public Key getKeyFuzzy(String text) throws BookException
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
                String match = key.getText();
                if (match.equalsIgnoreCase(text))
                {
                    return key;
                }
            }

            // Next keys that start with the given text
            for (Iterator it = map.keySet().iterator(); it.hasNext();)
            {
                key = (Key) it.next();
                String match = key.getText();
                if (match.startsWith(text))
                {
                    return key;
                }
            }

            // Next try keys that contain the given text
            for (Iterator it = map.keySet().iterator(); it.hasNext();)
            {
                key = (Key) it.next();
                String match = key.getText();
                if (match.indexOf(text) != -1)
                {
                    return key;
                }
            }

            throw new BookException(Msg.NO_KEY, new Object[] { text });
        }

        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        checkActive();

        if (key == null)
        {
            throw new NullPointerException();
        }

        try
        {
            String osisid = getDictionaryMetaData().getInitials();
            Osis osis = JAXBUtil.factory().createOsis();

            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);

            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);

            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Bible."+osisid);
            text.setHeader(header);

            osis.setOsisText(text);

            Div div = JAXBUtil.factory().createDiv();
            div.setDivTitle(key.getText());

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
    private SortedSet set = null;

    /**
     * To read data from disk
     */
    private KeyBackend backend;

    /**
     * Sword configuration data
     */
    private SwordConfig config;

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(SwordDictionary.class);
    
    /**
     * our meta data
     */
    private SwordDictionaryMetaData sdmd;

    /**
     * So we can order Keys in the SortedSet and cut them up with subSet using.
     */
    private static class KeyComparator implements Comparator
    {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2)
        {
            // Create strings from the objects
            String s1 = null;
            String s2 = null;

            if (o1 instanceof Key)
            {
                s1 = ((Key) o1).getText();
            }
            else
            {
                s1 = o1.toString();
            }
            
            if (o2 instanceof Key)
            {
                s2 = ((Key) o2).getText();
            }
            else
            {
                s2 = o2.toString();
            }
            
            return s1.compareTo(s2);
        }
    }
}
