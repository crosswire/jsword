package org.crosswire.jsword.view.cli;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.util.Project;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Quick helper for writing scripts to control JSword.
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
public class Model
{
    public Model() throws MalformedURLException, JDOMException, IOException
    {
        config = new Config("Tool Shed Options"); //$NON-NLS-1$
        Document xmlconfig = Project.instance().getDocument("config"); //$NON-NLS-1$
        config.add(xmlconfig);

        try
        {
            config.setProperties(Project.instance().getProperties("cli")); //$NON-NLS-1$
            config.localToApplication(true);
        }
        catch (Exception ex)
        {
            // If there is no stored config, dont worry
        }
    }

    public String dictList()
    {
        List dicts = Books.installed().getBookMetaDatas(BookFilters.getDictionaries());
        BookMetaData bmd = (BookMetaData) dicts.get(0);
        Book dict = bmd.getBook();

        KeyList set = dict.getGlobalKeyList();

        StringBuffer buffer = new StringBuffer();
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            Key key = (Key) it.next();
            buffer.append(key.getName());

            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    /**
     * This was private and called by methods that used Defaults (now
     * deprecated) we need to replace those methods, and probably still call
     * this method.
     */
    public String getData(String sref, Book book) throws NoSuchKeyException, BookException
    {
        Key key = book.getKey(sref);
        BookData bdata = book.getData(key);
        return bdata.getPlainText();
    }
    
    public String search(String str) throws BookException
    {
        List dicts = Books.installed().getBookMetaDatas(BookFilters.getBibles());
        BookMetaData bmd = (BookMetaData) dicts.get(0);
        Book book = bmd.getBook();
        
        Key key = book.find(new Search(str, false));
        return key.getName();
    }
    
    public String match(String str) throws BookException
    {
        List dicts = Books.installed().getBookMetaDatas(BookFilters.getBibles());
        BookMetaData bmd = (BookMetaData) dicts.get(0);
        Book book = bmd.getBook();

        Key key = book.find(new Search(str, true));
        return key.getName();
    }
    
    public String bibles()
    {
        return display(BookFilters.getBibles());
    }

    public String dicts()
    {
        return display(BookFilters.getDictionaries());
    }
    
    public String comments()
    {
        return display(BookFilters.getCommentaries());
    }
    
    public String books(BookFilter filter)
    {
        return display(filter);
    }
    
    private String display(BookFilter filter)
    {
        StringBuffer buffer = new StringBuffer();

        List list = Books.installed().getBookMetaDatas(filter);
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) it.next();
            buffer.append(bmd.getFullName());
            
            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }
    
    public String config()
    {
        StringBuffer buffer = new StringBuffer();
        
        for (Iterator it = config.getNames(); it.hasNext();)
        {
            String key = (String) it.next();
            Choice choice = config.getChoice(key);

            buffer.append(key+" = "+choice.getString()); //$NON-NLS-1$

            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    public String config(String key, String value) throws Exception
    {
        Choice choice = config.getChoice(key);
        choice.setString(value);

        return choice.getString();
    }

    public String save() throws IOException
    {
        URL url = Project.instance().getWritablePropertiesURL("cli"); //$NON-NLS-1$
        config.applicationToLocal();
        config.localToPermanent(url);
        
        return "OK"; //$NON-NLS-1$
    }

    private Config config;
}
