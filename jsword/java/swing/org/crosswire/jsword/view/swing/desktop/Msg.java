package org.crosswire.jsword.view.swing.desktop;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
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
public class Msg extends MsgBase
{
    static final Msg NO_HELP = new Msg("Currently on-line help is only available via the J-Sword website:\nhttp://www.crosswire.org/jsword");

    // Strings used by Desktop
    static final Msg UNKNOWN_PROTOCOL = new Msg("Unknown protocol {0}");
    static final Msg BAD_PROTOCOL_URL = new Msg("Missing : in {0}");

    // Strings for the preloading job
    static final Msg PRELOAD_TITLE = new Msg("Display Pre-load");
    static final Msg PRELOAD_SETUP = new Msg("Setup");
    static final Msg PRELOAD_DATA = new Msg("Getting initial data");
    static final Msg PRELOAD_PROVIDER = new Msg("Getting event provider");
    static final Msg PRELOAD_STYLE = new Msg("Compiling stylesheet");

    // Strings for the startup job
    static final Msg STARTUP_TITLE = new Msg("Startup");
    static final Msg STARTUP_CONFIG = new Msg("Setting-up config");
    static final Msg STARTUP_LOAD_CONFIG = new Msg("Loading Configuration System");
    static final Msg STARTUP_LOAD_SETTINGS = new Msg("Loading Stored Settings");
    static final Msg STARTUP_GENERATE = new Msg("Generating Components");
    static final Msg STARTUP_GENERAL_CONFIG = new Msg("General configuration");

    // Strings for DesktopAction
    static final Msg NOT_IMPLEMENTED = new Msg("{0} is not implemented");
    static final Msg NO_PASSAGE = new Msg("No Passage to Save");
    static final Msg SOURCE_MISSING = new Msg("No {0} source to view");
    static final Msg GHTML = new Msg("Generated HTML");
    static final Msg HTML = new Msg("HTML");
    static final Msg OSIS = new Msg("OSIS");
    static final Msg SOURCE_FOUND = new Msg("{0} source to {1}");

    // Strings for AboutPane and Splash
    // The splash image is of an English version of the application
    static final Msg SPLASH_IMAGE = new Msg("/images/splash.png");
    static final Msg VERSION_TITLE = new Msg("Version {0}");
    static final Msg ABOUT_TITLE = new Msg("About {0}");
    static final Msg TASK_TAB_TITLE = new Msg("Running Tasks");
    static final Msg ERROR_TAB_TITLE = new Msg("Errors");
    static final Msg SYSTEM_PROPS_TAB_TITLE = new Msg("System Properties");
    static final Msg DEBUG_TAB_TITLE = new Msg("Debug");

    // Strings for StatusBar
    static final Msg STATUS_DEFAULT = new Msg("Ready ...        ");

    // Strings for OptionsAction
    static final Msg CONFIG_TITLE = new Msg("Desktop Options");

    /**
     * Initialise any resource bundles
     */
    static
    {
        init(Msg.class.getName());
    }

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
