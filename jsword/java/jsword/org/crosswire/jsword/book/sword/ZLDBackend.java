package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;

/**
 * An implementation KeyBackend to read Z format files.
 * LATER(joe): implement ZLDBackend
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
public class ZLDBackend implements Backend
{
    /**
     * Simple ctor
     */
    public ZLDBackend(SwordConfig config)
    {
        this.config = config;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#readIndex()
     */
    public KeyList readIndex()
    {
        return new DefaultKeyList(config.getDescription());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.KeyBackend#getRawText(org.crosswire.jsword.book.Key)
     */
    public byte[] getRawText(Key key) throws BookException
    {
        // LATER(joe): implement this
        throw new BookException(Msg.COMPRESSION_UNSUPPORTED);
    }

    /**
     * The book driver that we are providing data for
     */
    private SwordConfig config;
}
