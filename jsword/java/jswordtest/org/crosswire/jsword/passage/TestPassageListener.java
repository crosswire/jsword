
package org.crosswire.jsword.passage;

/**
 * To help us test the VerseCollectionListener interface.
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
class TestPassageListener implements PassageListener
{
    public int adds = 0;
    public int removals = 0;
    public int changes = 0;

    public TestPassageListener()
    {
    }

    public void versesAdded(PassageEvent ev)
    {
        adds++;
    }

    public void versesRemoved(PassageEvent ev)
    {
        removals++;
    }

    public void versesChanged(PassageEvent ev)
    {
        changes++;
    }

    public boolean check(int adds, int removals, int changes) throws Exception
    {
        if (this.adds != adds)
        {
            throw new Exception("ADD: should have: "+adds+", noted "+this.adds);
        }

        if (this.removals != removals)
        {
            throw new Exception("REMOVALS: should have: "+removals+", noted "+this.removals);
        }

        if (this.changes != changes)
        {
            throw new Exception("CHANGES: should have: "+changes+", noted "+this.changes);
        }

        return true;
    }
}
