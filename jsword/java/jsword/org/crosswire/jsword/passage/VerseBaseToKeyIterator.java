package org.crosswire.jsword.passage;

import java.util.Iterator;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseBase;

/**
 * Allows you to iterate over the Verses in a Passage and have the instances
 * chucked out by the Iterator be Passages rather than Verses or VerseRanges.
 * This originated in a test class.
 * TODO: This can probably be deleted because it is of marginal use
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
public class VerseBaseToKeyIterator implements Iterator
{
    /**
     * Simple ctor
     */
    public VerseBaseToKeyIterator(Iterator orig)
    {
        this.orig = orig;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        orig.remove();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        return orig.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next()
    {
        VerseBase vb = (VerseBase) orig.next();
        Passage fetch = PassageFactory.createPassage();
        fetch.add(vb);
        return fetch;
    }

    private Iterator orig;
}
