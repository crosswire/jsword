
package org.crosswire.jsword.book.local;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBible;

/**
 * LocalURLBible is a helper for drivers that want to store files locally.
 * 
 * It takes care of providing you with a directory to work from and managing the
 * files stored in that directory.
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
public abstract class LocalURLBible extends AbstractBible
{
    /**
     * Initializer to check on resources.
     */
    public abstract void init() throws BookException;

    /**
     * Initializer to check on resources.
     */
    public abstract void activate();

    /**
     * Initializer to check on resources.
     */
    public abstract void deactivate();

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#getBibleMetaData()
     */
    public BibleMetaData getBibleMetaData()
    {
        return lbmd;
    }

    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public LocalURLBibleMetaData getLocalURLBibleMetaData()
    {
        return lbmd;
    }

    /**
     * Constructor SerBible.
     */
    public void setLocalURLBibleMetaData(LocalURLBibleMetaData lbmd)
    {
        this.lbmd = lbmd;
    }

    /**
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
        try
        {
            Properties prop = new Properties();
            prop.put("Version", getBibleMetaData().getFullName());

            URL prop_url = NetUtil.lengthenURL(getLocalURLBibleMetaData().getURL(), "bible.properties");
            OutputStream prop_out = NetUtil.getOutputStream(prop_url);
            prop.store(prop_out, "Bible Config");
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.FLUSH_FAIL, ex);
        }
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(LocalURLBible.class);

    /**
     * The Version of the Bible that this produces
     */
    private LocalURLBibleMetaData lbmd;
}
