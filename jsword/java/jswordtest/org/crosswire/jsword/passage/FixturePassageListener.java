
package org.crosswire.jsword.passage;

/**
 * To help us test the VerseCollectionListener interface.
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
 * @version $Id: FixturePassageListener.java,v 1.2 2002/10/08 21:36:08 joe Exp $
 */
class FixturePassageListener implements PassageListener
{
    public int adds = 0;
    public int removals = 0;
    public int changes = 0;

    public FixturePassageListener()
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

    public boolean check(int addcheck, int removalcheck, int changecheck) throws Exception
    {
        if (this.adds != addcheck)
        {
            throw new Exception("ADD: should have: "+addcheck+", noted "+this.adds);
        }

        if (this.removals != removalcheck)
        {
            throw new Exception("REMOVALS: should have: "+removalcheck+", noted "+this.removals);
        }

        if (this.changes != changecheck)
        {
            throw new Exception("CHANGES: should have: "+changecheck+", noted "+this.changes);
        }

        return true;
    }
}
