package org.crosswire.jsword.book.stub;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Element;

/**
 * StubBook is a simple stub implementation of Book that is pretty much
 * always going to work because it has no dependancies on external files.
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
public class StubDictionary extends AbstractBook
{
    /**
     * Basic constructor for a StubBook
     */
    public StubDictionary(StubBookDriver driver, String name, BookType type, int speed)
    {
        BookMetaData bmd = new DefaultBookMetaData(driver, this, name, type, speed);
        setBookMetaData(bmd);

        initSearchEngine();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.passage.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        if (key == null)
        {
            throw new NullPointerException();
        }

        try
        {
            Element osis = OSISUtil.createOsisFramework(getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);

            Element div = OSISUtil.factory().createDiv();
            Element title = OSISUtil.factory().createTitle();
            title.addContent(key.getName());
            div.addContent(title);
            text.addContent(div);

            FilterFactory.getDefaultFilter().toOSIS(div, "stub implementation"); //$NON-NLS-1$

            BookData bdata = new BookData(osis, this, key);
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
        return keyf.getGlobalKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        return keyf.getKey(name);
    }
    
    private KeyFactory keyf = new StubDictionaryKeyFactory();
}
