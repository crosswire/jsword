package org.crosswire.jsword.book.sword;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.ReadOnlyKeyList;
import org.jdom.Element;

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
    protected SwordDictionary(SwordBookMetaData sbmd, Backend backend)
    {
        setBookMetaData(sbmd);

        this.sbmd = sbmd;
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
        for (Iterator it = set.iterator(); it.hasNext(); )
        {
            Key key = (Key) it.next();
            map.put(key.getName(), key);
        }

        global = new ReadOnlyKeyList(set, false);

        active = true;

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

        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.passage.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        checkActive();

        assert key != null;
        assert backend != null;

        try
        {
            Element osis = OSISUtil.createOsisFramework(getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);

            Element div = OSISUtil.factory().createDiv();
            Element title = OSISUtil.factory().createTitle();
            title.addContent(key.getName());
            div.addContent(title);
            text.addContent(div);

            String txt = backend.getRawText(key, sbmd.getModuleCharset());

            List osisContent = sbmd.getFilter().toOSIS(txt);
            div.addContent(osisContent);

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
        checkActive();

        assert key != null;
        assert backend != null;

        try
        {
            return backend.getRawText(key, sbmd.getModuleCharset());
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public Key getGlobalKeyList()
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
        if (key != null)
        {
            return key;
        }

        // So we need to find a matching key.
        // TODO(DM): This is a hack.
        key = getStrongsKey(text);

        if (key != null)
        {
            return key;
        }

        // First check for keys that match ignoring case
        for (Iterator it = map.keySet().iterator(); it.hasNext(); )
        {
            String keyName = (String) it.next();
            if (keyName.equalsIgnoreCase(text))
            {
                return (Key) map.get(keyName);
            }
        }

        // Next keys that start with the given text
        for (Iterator it = map.keySet().iterator(); it.hasNext(); )
        {
            String keyName = (String) it.next();
            if (keyName.startsWith(text))
            {
                return (Key) map.get(keyName);
            }
        }

        // Next try keys that contain the given text
        for (Iterator it = map.keySet().iterator(); it.hasNext(); )
        {
            String keyName = (String) it.next();
            if (keyName.indexOf(text) != -1)
            {
                return (Key) map.get(keyName);
            }
        }

        throw new NoSuchKeyException(Msg.NO_KEY, new Object[] { text });
    }

    // TODO(DM): Hack alert!!! This is not in the right place!!!
    private Key getStrongsKey(String text)
    {
        // Is the string all digits?
        Matcher m = STRONGS_PATTERN.matcher(text);
        if (!m.matches())
        {
            return null;
        }

        char firstDigit = text.charAt(1);

        // Get the number after the G or H
        int strongsNumber = Integer.parseInt(text.substring(1));

        Key key = null;
        String internalName = sbmd.getInitials();
        if (internalName.equals("StrongsGreek") && firstDigit != '0') //$NON-NLS-1$
        {
            key = (Key) map.get(ZERO_PAD.format(strongsNumber));
        }
        else if (internalName.equals("StrongsHebrew") && firstDigit == '0') //$NON-NLS-1$
        {
            key = (Key) map.get(ZERO_PAD.format(strongsNumber));
        }
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public Key createEmptyKeyList()
    {
        return new DefaultKeyList();
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

    // This should move along with getStrongsKey
    private static final Pattern STRONGS_PATTERN = Pattern.compile("^[GH]\\d+$"); //$NON-NLS-1$
    private static final DecimalFormat ZERO_PAD = new DecimalFormat("00000"); //$NON-NLS-1$

    /**
     * The global key list
     */
    private Key global;

    /**
     * Are we active
     */
    private boolean active;

    /**
     * So we can quickly find a Key given the text for the key
     */
    private Map map;

    /**
     * So we can implement getIndex() easily
     */
    private Key set;

    /**
     * To read the data from the disk
     */
    private Backend backend;

    /**
     * The Sword configuration file
     */
    private SwordBookMetaData sbmd;
}
