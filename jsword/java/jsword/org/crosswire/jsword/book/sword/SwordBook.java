package org.crosswire.jsword.book.sword;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.basic.PassageAbstractBook;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Verse;

/**
 * SwordBook is a base class for all sword type modules.
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
public class SwordBook extends PassageAbstractBook
{
    /**
     * Simple ctor
     */
    protected SwordBook(SwordBookDriver driver, SwordConfig config, Backend backend, BookType type) throws MalformedURLException, ParseException
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

    /**
     * Read the unfiltered data for a given verse
     */
    protected String getText(Verse verse) throws BookException
    {
        byte[] data = backend.getRawText(verse);
        String charset = config.getModuleCharset();

        try
        {
            return new String(data, charset);
        }
        catch (UnsupportedEncodingException ex)
        {
            // It is impossible! In case, use system default...
            log.error("Encoding: " + charset + " not supported", ex);
            return new String(data);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.PassageAbstractBook#setText(org.crosswire.jsword.passage.Verse, java.lang.String)
     */
    protected void setText(Verse verse, String text) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.PassageAbstractBook#getFilter()
     */
    protected Filter getFilter()
    {
        return config.getFilter();
    }

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
    private static Logger log = Logger.getLogger(SwordBook.class);
}
