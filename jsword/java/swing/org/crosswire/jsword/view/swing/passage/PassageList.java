
package org.crosswire.jsword.view.swing.passage;

import javax.swing.JList;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A Simple extension to JList to customize it to hold a Passage and
 * provide Passage related actions.
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
public class PassageList extends JList
{
    /**
     * Basic Ctor, using default range scope
     */
    public PassageList()
    {
        this(null);
    }

    /**
     * Ctor with a Passage, using default range scope
     */
    public PassageList(Passage ref)
    {
        this(ref, PassageListModel.LIST_RANGES);
    }

    /**
     * Ctor with a Passage, using default range scope
     */
    public PassageList(Passage ref, int scope)
    {
        plm = new PassageListModel(null, scope);
        plm.setPassage(ref);
        setModel(plm);
    }

    /**
     * Accessor for the current passage
     */
    public void setPassage(Passage ref)
    {
        plm.setPassage(ref);
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return plm.getPassage();
    }

    /**
     * Remove all of the selected verses from the passage
     */
    public void deleteSelected()
    {
        Passage ref = plm.getPassage();
        Object[] selected = this.getSelectedValues();
        for (int i=0; i<selected.length; i++)
        {
            VerseRange range = (VerseRange) selected[i];
            ref.remove(range);
        }

        setSelectedIndices(new int[0]);
    }

    private PassageListModel plm = null;
}

