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
    // Strings used by Desktop
    static final Msg UNKNOWN_PROTOCOL = new Msg("Desktop.UnknownProtocol");
    static final Msg BAD_PROTOCOL_URL = new Msg("Desktop.BadProtocolUrl");
    
    // Strings for the preloading job
    static final Msg PRELOAD_TITLE = new Msg("Desktop.PreloadTitle");
    static final Msg PRELOAD_SETUP = new Msg("Desktop.PreloadSetup");
    static final Msg PRELOAD_DATA = new Msg("Desktop.PreloadData");
    static final Msg PRELOAD_PROVIDER = new Msg("Desktop.PreloadProvider");
    static final Msg PRELOAD_STYLE = new Msg("Desktop.PreloadStyle");

   // Strings for the startup job
    static final Msg STARTUP_TITLE = new Msg("Desktop.StartupTitle");
    static final Msg STARTUP_CONFIG = new Msg("Desktop.StartupConfig");
    static final Msg STARTUP_LOAD_CONFIG = new Msg("Desktop.StartupLoadConfig");
    static final Msg STARTUP_LOAD_SETTINGS = new Msg("Desktop.StartupLoadSettings");
    static final Msg STARTUP_GENERATE = new Msg("Desktop.StartupGenerate");
    static final Msg STARTUP_GENERAL_CONFIG = new Msg("Desktop.StartupGeneral");

    // Strings for DesktopAction
    static final Msg NO_HELP = new Msg("DesktopActions.NoHelp");
    static final Msg NOT_IMPLEMENTED = new Msg("DesktopActions.NotImplemented");
    static final Msg NO_PASSAGE = new Msg("DesktopActions.NoPassage");
    static final Msg SOURCE_MISSING = new Msg("DesktopActions.SourceMissing");
    static final Msg GHTML = new Msg("DesktopActions.GHTML");
    static final Msg HTML = new Msg("DesktopActions.HTML");
    static final Msg OSIS = new Msg("DesktopActions.OSIS");
    static final Msg SOURCE_FOUND = new Msg("DesktopActions.SourceFound");

    // Strings for AboutPane and Splash
    // The splash image is of an English version of the application
    static final Msg SPLASH_IMAGE = new Msg("Splash.SplashImage");
    static final Msg VERSION_TITLE = new Msg("Splash.VersionTitle");
    static final Msg ABOUT_TITLE = new Msg("AboutPane.AboutTitle");
    static final Msg TASK_TAB_TITLE = new Msg("AboutPane.TaskTabTitle");
    static final Msg ERROR_TAB_TITLE = new Msg("AboutPane.ErrorTabTitle");
    static final Msg SYSTEM_PROPS_TAB_TITLE = new Msg("AboutPane.SystemPropsTabTitle");
    static final Msg DEBUG_TAB_TITLE = new Msg("AboutPane.DebugTabTitle");

    // Strings for StatusBar
    static final Msg STATUS_DEFAULT = new Msg("StatusBar.StatusDefault");

    // Strings for OptionsAction
    static final Msg CONFIG_TITLE = new Msg("OptionsAction.ConfigTitle");

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
