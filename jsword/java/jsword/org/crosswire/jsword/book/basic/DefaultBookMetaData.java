package org.crosswire.jsword.book.basic;

import java.util.Properties;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.book.search.IndexManagerFactory;

/**
 * DefaultBookMetaData is an implementation of the of the BookMetaData
 * interface. A less complete implementation design for imheritance is
 * available in AbstractBookMetaData where the complexity is in the setup rather
 * than the inheritance. DefaultBookMetaData is probably the preferred
 * implementation.
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
public class DefaultBookMetaData extends AbstractBookMetaData
{
    /**
     * Ctor with a properties from which to get values.
     * A call to setBook() is still required after this ctor is called
     */
    public DefaultBookMetaData(BookDriver driver, Book book, Properties prop)
    {
        setDriver(driver);
        setBook(book);

        setProperties(prop);
        setName(prop.getProperty(BookMetaData.KEY_NAME));
        setType(prop.getProperty(BookMetaData.KEY_TYPE));
        setLanguage(prop.getProperty(BookMetaData.KEY_LANGUAGE));

        IndexManager imanager = IndexManagerFactory.getIndexManager();
        if (imanager.isIndexed(book))
        {
            setIndexStatus(IndexStatus.DONE);
        }
        else
        {
            setIndexStatus(IndexStatus.UNDONE);
        }
    }

    /**
     * Ctor with some default values.
     * A call to setBook() is still required after this ctor is called
     */
    public DefaultBookMetaData(BookDriver driver, Book book, String name, BookType type)
    {
        setDriver(driver);
        setBook(book);
        setName(name);
        setType(type);
        setLanguage(null); // Default language
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getType()
     */
    public BookType getType()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials()
    {
        return initials;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight()
    {
        // TODO(joe): Do this correctly
        return true;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(String language)
    {
        putProperty(KEY_LANGUAGE, getLanguage(initials, language));
    }

    /**
     * See note on setName() for side effect on setInitials(). If a value of
     * null is used then the initials are defaulted using the name
     * @see DefaultBookMetaData#setName(String)
     * @param initials The initials to set.
     */
    public void setInitials(String initials)
    {
        if (initials == null)
        {
            if (name == null)
            {
                this.initials = ""; //$NON-NLS-1$
            }
            else
            {
                this.initials = StringUtil.getInitials(name);
            }
        }
        else
        {
            this.initials = initials;
        }

        putProperty(KEY_INITIALS, this.initials);
    }

    /**
     * Setting the name also sets some default initials, so if you wish to set
     * some specific initials then it should be done after setting the name.
     * @see DefaultBookMetaData#setInitials(String)
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;

        putProperty(KEY_NAME, this.name);

        setInitials(StringUtil.getInitials(name));
    }

    /**
     * @param type The type to set.
     */
    public void setType(BookType type)
    {
        if (type == null)
        {
            type = BookType.BIBLE;
        }
        this.type = type;

        putProperty(KEY_TYPE, type.toString());
    }

    /**
     * @param typestr The string version of the type to set.
     */
    public void setType(String typestr)
    {
        BookType newType = null;
        if (typestr != null)
        {
            newType = BookType.fromString(typestr);
        }

        setType(newType);
    }

    private BookType type;
    private String name;
    private String initials;
}
