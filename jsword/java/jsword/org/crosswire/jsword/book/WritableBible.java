
package org.crosswire.jsword.book;

import org.crosswire.jsword.book.events.ProgressListener;

/**
 * A Bible that can store new data.
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
public interface WritableBible extends Bible
{
    /**
     * Generation: Read from the given source version to generate
     * ourselves. It should periodically call:
     *   <code>Thread.currentThread().isInterrupted()</code>
     * to check that it is safe to continue, and clear up if not.
     * @param version The source
     * @throws BookException If anything goes wrong with this method
     */
    public void generate(Bible version) throws BookException;

    /**
     * Generation: Add a progress listener to the list of things wanting
     * to know whenever we make some progress
     * @param li The listener to add
     */
    public void addProgressListener(ProgressListener li);

    /**
     * Generation: Remove a progress listener from the list of things
     * wanting to know whenever we make some progress
     * @param li The listener to remove
     */
    public void removeProgressListener(ProgressListener li);
}