
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * Blur the current passage action.
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
public class BlurAction extends DesktopAbstractAction
{
    /**
     * Ctor
     */
    public BlurAction(Desktop tools, int amount, int restrict)
    {
        super(tools,
              "Blur by "+amount+" verse",
              null,
              null,
              "Blur passage by "+amount+" verse", "Blur the current passage by "+amount+" verse.",
              '0'+(char) amount, null);

        this.amount = amount;
        this.restrict = restrict;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        if (view != null)
        {
            Passage ref = view.getPassage();
            ref.blur(amount, restrict);
            view.setPassage(ref);
        }
    }

    private int amount;
    private int restrict;
}


