package org.crosswire.common.config.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import org.crosswire.common.config.Config;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.LucidRuntimeException;
import org.crosswire.common.util.Reporter;

/**
 * Allow a swing program to display a Dialog box displaying a set of
 * config options.
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
public class ConfigEditorFactory
{
    /**
     * Prevent Instansiation
     */
    private ConfigEditorFactory()
    {
    }

    /**
     * Create a dialog to house a TreeConfig component
     * using the default set of Fields
     * @param config The set of Choices to display
     * @param parent A component to use to find a frame to use as a dialog parent
     * @param al The action when the user clicks on ok or apply
     */
    public static void showDialog(Config config, Component parent, ActionListener al)
    {
        try
        {
            ConfigEditor base = (ConfigEditor) ClassUtil.getImplementation(ConfigEditor.class);
            base.init(config);
            base.showDialog(parent, al);
        }
        catch (Exception ex)
        {
            Reporter.informUser(parent, ex);
        }
    }

    /**
     * Create a dialog to house a TreeConfig component
     * using the default set of Fields, with the default accept action
     * of config.localToAppliation and config,localToPermanentURL
     * @param config The set of Choices to display
     * @param parent A component to use to find a frame to use as a dialog parent
     */
    public static void showDialog(Config config, Component parent, URL url)
    {
        showDialog(config, parent, new URLActionListener(config, url));
    }

    /**
     * A quick class to save a config to a url
     */
    static class URLActionListener implements ActionListener
    {
        /**
         * To save to a URL
         */
        public URLActionListener(Config config, URL url)
        {
            this.config = config;
            this.url = url;
        }

        /**
         * The save action
         */
        public void actionPerformed(ActionEvent ev)
        {
            try
            {
                config.localToApplication(false);
                config.localToPermanent(url);
            }
            catch (IOException ex)
            {
                throw new LucidRuntimeException(Msg.CONFIG_SAVE_FAILED, ex, new Object[] { url });
            }
        }

        /**
         * The URL to save to if needed
         */
        private Config config;

        /**
         * The URL to save to if needed
         */
        private URL url;
    }
}
