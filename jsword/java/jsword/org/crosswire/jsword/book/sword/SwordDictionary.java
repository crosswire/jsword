
package org.crosswire.jsword.book.sword;

import java.util.SortedSet;
import java.util.TreeSet;

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
     * @param data
     */
    public SwordDictionary(SwordDictionaryMetaData data, SwordConfig config) throws BookException
    {
        this.config = config;
        this.data = data;

        backend = config.getKeyBackend();
        backend.init(config);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getDictionaryMetaData()
     */
    public DictionaryMetaData getDictionaryMetaData()
    {
        return data;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getIndex(java.lang.String)
     */
    public SortedSet getIndex(String startswith) throws BookException
    {
        return new TreeSet();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getKey(java.lang.String)
     */
    public Key getKey(final String text) throws BookException
    {
        return new Key()
        {
            public String getText()
            {
                return text;
            }
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key ref) throws BookException
    {
        try
        {
            BookDataListener li = new OSISBookDataListnener();

            li.startDocument(getDictionaryMetaData().getInitials());
            li.startSection(ref.getText());

            String text = new String(backend.getRawText(ref));
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
}
