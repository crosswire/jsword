
package org.crosswire.jsword.view.cli;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
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
        config = new Config("Tool Shed Options");
        Document xmlconfig = Project.resource().getDocument("config");
        config.add(xmlconfig);

        try
        {
            config.setProperties(Project.resource().getProperties("cli"));
            config.localToApplication(true);
        }
        catch (Exception ex)
        {
            // If there is no stored config, dont worry
        }
    }

    public String bible()
    {
        return Defaults.getBibleMetaData().getFullName();
    }

    public String comment()
    {
        return Defaults.getCommentaryMetaData().getFullName();
    }

    public String dict()
    {
        return Defaults.getDictionaryMetaData().getFullName();
    }

    public String bible(String sref) throws BookException, NoSuchVerseException
    {
        Book book = Defaults.getBibleMetaData().getBible();
        return getData(sref, book);
    }

    public String comment(String sref) throws BookException, NoSuchVerseException
    {
        Book book = Defaults.getCommentaryMetaData().getCommentary();
        return getData(sref, book);
    }

    public String dict(String sref) throws BookException, NoSuchVerseException
    {
        Book book = Defaults.getDictionaryMetaData().getDictionary();
        return getData(sref, book);
    }

    public String dictList(String startswith) throws BookException, NoSuchVerseException
    {
        Dictionary dict = Defaults.getDictionaryMetaData().getDictionary();
        SortedSet set = dict.getIndex(startswith);

        StringBuffer buffer = new StringBuffer();
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            Key key = (Key) it.next();
            buffer.append(key.getText());

            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    private String getData(String sref, Book book) throws BookException
    {
        Key key = book.getKey(sref);
        BookData bdata = book.getData(key);
        return bdata.getPlainText();
    }
    
    public String setBible(String spec) throws BookException
    {
        Defaults.setBibleByName(spec);
        return Defaults.getBibleByName();
    }

    public String setDict(String spec) throws BookException
    {
        Defaults.setDictionaryByName(spec);
        return Defaults.getDictionaryByName();
    }

    public String setComment(String spec) throws BookException
    {
        Defaults.setCommentaryByName(spec);
        return Defaults.getCommentaryByName();
    }

    public String search(String str) throws BookException
    {
        Bible book = Defaults.getBibleMetaData().getBible();
        Passage ref = book.findPassage(new Search(str, false));
        return ref.getName();
    }
    
    public String match(String str) throws BookException
    {
        Bible book = Defaults.getBibleMetaData().getBible();
        Passage ref = book.findPassage(new Search(str, true));
        return ref.getName();
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

        List list = Books.getBooks(filter);
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

            buffer.append(key+" = "+choice.getString());

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
        URL url = Project.resource().getWritablePropertiesURL("cli");
        config.applicationToLocal();
        config.localToPermanent(url);
        
        return "OK";
    }

    private Config config;
}
