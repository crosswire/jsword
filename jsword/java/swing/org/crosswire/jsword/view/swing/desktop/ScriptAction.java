
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;

/**
 * Show the script pane - this is broken.
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
public class ScriptAction extends DesktopAbstractAction
{
    public ScriptAction(Desktop tools)
    {
        super(tools,
              "Scripting ...",
              "toolbarButtonGraphics/development/Applet16.gif",
              "toolbarButtonGraphics/development/Applet24.gif",
              "Scripting", "Run some commands in a scripting language.",
              'S', null);

        // Script pane setup
        /*
        pnl_script.declareBean("tools", this, this.getClass());
        pnl_script.declareBean("views", views, views.getClass());
        String[] names = Books.getBibleNames();
        for (int i=0; i<names.length; i++)
        {
            String varname = names[i].toLowerCase();
            varname = varname.replace(' ', '_');
            varname = varname.replace('-', '_');

            try
            {
                Bible bible = Books.getBible(names[i]);
                pnl_script.declareBean(varname, bible, bible.getClass());
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }
        */
    }

    public void actionPerformed(ActionEvent ev)
    {
        //pnl_script.showInDialog(Desktop.this);
    }

    /** The scripting interface to BSF */
    //protected ScriptPane pnl_script = null;
}
