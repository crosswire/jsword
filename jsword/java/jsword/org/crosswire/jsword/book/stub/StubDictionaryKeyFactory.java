package org.crosswire.jsword.book.stub;

import org.crosswire.jsword.book.basic.DefaultKey;
import org.crosswire.jsword.book.basic.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyList;

/**
 * A KeyFactory that handles a pretend set of keys.
 * PENDING(joe): make this more consistent it current returns any key from getKey()
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
public class StubDictionaryKeyFactory implements KeyFactory
{
    /**
     * Simple ctor
     */
    public StubDictionaryKeyFactory()
    {
        set.add(new DefaultKey("stub"));
        set.add(new DefaultKey("implementation"));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String name)
    {
        return new DefaultKey(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public KeyList getGlobalKeyList()
    {
        return set;
    }

    private KeyList set = new DefaultKeyList("Stub Dictionary");
}
