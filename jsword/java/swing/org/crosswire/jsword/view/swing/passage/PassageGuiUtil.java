
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PassageGuiUtil
{
    /**
     * Prevent Instansiation
     */
    private PassageGuiUtil()
    {
    }

    /**
     * Remove all of the selected verses from the passage
     */
    public static void deleteSelectedVersesFromList(JList list)
    {
        PassageListModel plm = (PassageListModel) list.getModel();
 
        Passage ref = plm.getPassage();
        Object[] selected = list.getSelectedValues();
        for (int i=0; i<selected.length; i++)
        {
            VerseRange range = (VerseRange) selected[i];
            ref.remove(range);
        }

        list.setSelectedIndices(new int[0]);
    }
}
