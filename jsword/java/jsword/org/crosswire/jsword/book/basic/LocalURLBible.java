
package org.crosswire.jsword.book.basic;

import java.net.URL;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.events.ProgressListener;

/**
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
public abstract class LocalURLBible extends SearchableBible
{
    /**
     * Startup
     */
    public abstract void init(Bible source, ProgressListener li) throws BookException;

    /**
     * Startup
     */
    public abstract void init(ProgressListener li) throws BookException;

    /**
     * Where can we write the search indexes.
     */
    public URL getURL()
    {
        return lbmd.getURL();
    }

    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getMetaData()
    {
        return lbmd;
    }

    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
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
     * The Version of the Bible that this produces
     */
    private LocalURLBibleMetaData lbmd;
}
