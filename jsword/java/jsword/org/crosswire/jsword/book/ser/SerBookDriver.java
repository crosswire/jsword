package org.crosswire.jsword.book.ser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.util.Project;

/**
 * This represents all of the SerBibles.
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
public class SerBookDriver extends AbstractBookDriver
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        try
        {
            URL dir = Project.instance().findBibleRoot(getDriverName());

            if (!NetUtil.isDirectory(dir))
            {
                log.debug("Missing ser directory: "+dir.toExternalForm());
                return new BookMetaData[0];
            }

            String[] names = null;
            if (dir == null)
            {
                names = new String[0];
            }
            else
            {
                names = NetUtil.list(dir, new Project.IsDirectoryURLFilter(dir));
            }

            List bmds = new ArrayList();

            for (int i=0; i<names.length; i++)
            {
                URL url = NetUtil.lengthenURL(dir, names[i]);
                URL prop_url = NetUtil.lengthenURL(url, "bible.properties");

                Properties prop = new Properties();
                prop.load(prop_url.openStream());

                Book book = new SerBook(this, prop, url);

                bmds.add(book.getBookMetaData());
            }

            return (BookMetaData[]) bmds.toArray(new BookMetaData[bmds.size()]);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return new BookMetaData[0];
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName()
    {
        return "ser";
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(SerBookDriver.class);
}
