
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;

import org.crosswire.common.swing.TextViewPanel;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.view.swing.book.DisplayArea;

/**
 * View the OSIS source to the current window.
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
public class ViewSourceOSISAction extends DesktopAbstractAction
{
    /**
     * Setup configutarion
     */
    public ViewSourceOSISAction(Desktop tools)
    {
        super(tools,
              "View OSIS Source",
              null,
              null,
              "View OSIS Source", "View the OSIS source to the current window",
              'H', null);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            DisplayArea da = getDesktop().getDisplayArea();
            String html = da.getOSISSource();
            Key key = da.getKey();

            TextViewPanel viewer = new TextViewPanel(html, "OSIS source to "+key.getText());
            viewer.setEditable(true);
            viewer.showInFrame(getDesktop());
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }
}
