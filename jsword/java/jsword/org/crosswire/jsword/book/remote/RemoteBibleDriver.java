
package org.crosswire.jsword.book.remote;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BookException;
import org.jdom.Document;
import org.jdom.Element;

/**
 * This represents all of the SerBibles.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public abstract class RemoteBibleDriver implements BibleDriver
{
    /**
     * Some basic driver initialization
     */
    protected RemoteBibleDriver() throws MalformedURLException, IOException
    {
        log.debug("Starting");
    }

    /**
     * Method getXML.
     * @param string
     * @return Document
     */
    protected abstract Document getXML(String string);

    /**
     * @see org.crosswire.jsword.book.BibleDriver#countBibles()
     */
    public int countBibles()
    {
        if (names == null)
            getBibleNames();

        return names.length;            
    }

    /**
     * Get a list of the Books available from the driver
     * @return an array of book names
     */
    public String[] getBibleNames()
    {
        synchronized(names)
        {
            if (names == null)
            {
                Document doc = getXML("method=getBibleNames");
                Element root = doc.getRootElement();
                List namelist = root.getChildren("biblename");
    
                names = new String[namelist.size()];
                Iterator it = namelist.iterator();
                int i = 0;
                while (it.hasNext())
                {
                    Element name = (Element) it.next();
                    names[i++] = name.getTextTrim();
                }
            }
        }

        return names;
    }

    /**
     * Does the named Bible exist?
     * @param name The name of the version to test for
     * @return true if the Bible exists
     */
    public boolean exists(String name)
    {
        if (names == null)
            getBibleNames();
            
        for (int i=0; i<names.length; i++)
        {
            if (names[i].equals(name))
                return true;
        }

        return false;
    }

    /**
     * Featch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible(String name) throws BookException
    {
        return new RemoteBible(this, name, null);
    }

    /** The singleton driver */
    protected static RemoteBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger(RemoteBibleDriver.class);

    /**
     * The cache of Bible names.
     * At some stage it would be good to work out a way to clear the cache.
     */
    private String[] names;
}
