
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.common.config.Config;
import org.crosswire.common.config.swing.SwingConfig;
import org.crosswire.common.swing.config.DisplayExceptionChoice;
import org.crosswire.common.swing.config.LookAndFeelChoices;
import org.crosswire.common.swing.config.ShelfExceptionChoice;
import org.crosswire.common.swing.config.SourcePathChoice;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.config.UserLevelChoice;
import org.crosswire.jsword.book.config.CacheBiblesChoice;
import org.crosswire.jsword.book.config.DriversChoice;
import org.crosswire.jsword.book.raw.config.CacheDataChoice;
import org.crosswire.jsword.book.sword.config.SwordDirChoice;
import org.crosswire.jsword.util.Project;

/**
 * Action from clicking on the options button. Opens a config dialog.
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
public class OptionsAction extends DesktopAbstractAction
{
    public OptionsAction(Desktop tools)
    {
        super(tools,
              "Options ...",
              "/toolbarButtonGraphics/general/Properties16.gif",
              "/toolbarButtonGraphics/general/Properties24.gif",
              "Options", "Alter system settings.",
              'O', null);
    }

    public void createConfig() throws ClassNotFoundException
    {
        LookAndFeelChoices plaf_class = new LookAndFeelChoices();
        config = new Config("Tool Shed Options");
        config.add("Bibles.Cache Versions", new CacheBiblesChoice());
        config.add("Bibles.Raw.Cache Data", new CacheDataChoice());
        config.add("Bibles.Sword.Base Directory", new SwordDirChoice());

        config.add("Looks.Current Look", plaf_class.getCurrentChoice());
        config.add("Looks.Available Looks", plaf_class.getOptionsChoice());

        config.add("Reports.Exceptions to Dialog Box", new DisplayExceptionChoice());
        config.add("Reports.Exceptions to Log Window", new ShelfExceptionChoice());

        config.add("Advanced.Source Path", new SourcePathChoice());
        config.add("Advanced.User Level", new UserLevelChoice());
        config.add("Advanced.Available Drivers", new DriversChoice());
    }

    public void loadConfig() throws MalformedURLException, IOException
    {
        config.setProperties(Project.resource().getProperties("Desktop"));
        config.localToApplication(true);
    }

    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // SwingConfig.setDisplayClass(TreeConfigPane.class);
            URL config_url = Project.resource().getPropertiesURL("Desktop");
            SwingConfig.showDialog(config, getDesktop(), config_url);
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    private Config config = null;
}
