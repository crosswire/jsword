
package org.crosswire.jsword.book.sword;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.basic.AbstractDictionary;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.BookDataListener;
import org.crosswire.jsword.book.data.FilterException;
import org.crosswire.jsword.book.data.OSISBookDataListnener;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class SwordDictionary extends AbstractDictionary implements Dictionary
{
    /**
     * Start and to as much checking as we can without using memory.
     * (i.e. actually reading the indexes)
     */
    protected SwordDictionary(SwordDictionaryMetaData data, SwordConfig config) throws BookException
    {
        this.config = config;
        this.data = data;

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

    /**
     * Called just before we are pressed into service.
     * Now is the time to read the indexes.
     */
    public void init()
    {
        List data = backend.readIndex();

        map = new HashMap();
        for (Iterator it = data.iterator(); it.hasNext();)
        {
            Key key = (Key) it.next();
            map.put(key.getText(), key);
        }

        set = new TreeSet(new KeyComparator());
        set.addAll(data);
    }

    /**
     * Call this method to save memory.
     */
    public void free()
    {
        map = null;
        set = null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getDictionaryMetaData()
     */
    public DictionaryMetaData getDictionaryMetaData()
    {
        return data;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#getIndex(java.lang.String)
     */
    public SortedSet getIndex(String startswith) throws BookException
    {
        if (set == null)
        {
            throw new BookException(Msg.READ_FAIL);
        }

        // I18N(joe): check that this idiom works with all languages
        return set.subSet(startswith, startswith+"zzzz");
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#getKey(java.lang.String)
     */
    public Key getKey(String text) throws BookException
    {
        if (map == null)
        {
            throw new BookException(Msg.READ_FAIL);
        }

        Key key = (Key) map.get(text);
        if (key == null)
        {
            throw new BookException(Msg.NO_KEY, new Object[] { text });
        }

        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        if (map == null)
        {
            throw new BookException(Msg.READ_FAIL);
        }

        try
        {
            BookDataListener li = new OSISBookDataListnener();

            li.startDocument(getDictionaryMetaData().getInitials());
            li.startSection(key.getText());

            String text = new String(backend.getRawText(key));
            config.getFilter().toOSIS(li, text);

            li.endSection();
            return li.endDocument();
        }
        catch (FilterException ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

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
     * our meta data
     */
    private SwordDictionaryMetaData data;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(SwordDictionary.class);

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
