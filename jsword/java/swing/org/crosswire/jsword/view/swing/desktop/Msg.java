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
    static final Msg UNKNOWN_PROTOCOL = new Msg("Desktop.UnknownProtocol"); //$NON-NLS-1$
    static final Msg BAD_PROTOCOL_URL = new Msg("Desktop.BadProtocolUrl"); //$NON-NLS-1$

    // Strings for the preloading job
    static final Msg PRELOAD_TITLE = new Msg("Desktop.PreloadTitle"); //$NON-NLS-1$
    static final Msg PRELOAD_SETUP = new Msg("Desktop.PreloadSetup"); //$NON-NLS-1$
    static final Msg PRELOAD_DATA = new Msg("Desktop.PreloadData"); //$NON-NLS-1$
    static final Msg PRELOAD_PROVIDER = new Msg("Desktop.PreloadProvider"); //$NON-NLS-1$
    static final Msg PRELOAD_STYLE = new Msg("Desktop.PreloadStyle"); //$NON-NLS-1$

    // Strings for the startup job
    static final Msg STARTUP_TITLE = new Msg("Desktop.StartupTitle"); //$NON-NLS-1$
    static final Msg STARTUP_CONFIG = new Msg("Desktop.StartupConfig"); //$NON-NLS-1$
    static final Msg STARTUP_LOAD_CONFIG = new Msg("Desktop.StartupLoadConfig"); //$NON-NLS-1$
    static final Msg STARTUP_LOAD_SETTINGS = new Msg("Desktop.StartupLoadSettings"); //$NON-NLS-1$
    static final Msg STARTUP_GENERATE = new Msg("Desktop.StartupGenerate"); //$NON-NLS-1$
    static final Msg STARTUP_GENERAL_CONFIG = new Msg("Desktop.StartupGeneral"); //$NON-NLS-1$

    // Strings for DesktopAction
    static final Msg NO_HELP = new Msg("DesktopActions.NoHelp"); //$NON-NLS-1$
    static final Msg NOT_IMPLEMENTED = new Msg("DesktopActions.NotImplemented"); //$NON-NLS-1$
    static final Msg NO_PASSAGE = new Msg("DesktopActions.NoPassage"); //$NON-NLS-1$
    public static final Msg DEBUG_METHOD = new Msg("Method: "); //$NON-NLS-1$
    static final Msg SOURCE_MISSING = new Msg("DesktopActions.SourceMissing"); //$NON-NLS-1$
    public static final Msg DEBUG_GO = new Msg("GO"); //$NON-NLS-1$
    static final Msg GHTML = new Msg("DesktopActions.GHTML"); //$NON-NLS-1$
    static final Msg HTML = new Msg("DesktopActions.HTML"); //$NON-NLS-1$
    static final Msg OSIS = new Msg("DesktopActions.OSIS"); //$NON-NLS-1$
    static final Msg SOURCE_FOUND = new Msg("DesktopActions.SourceFound"); //$NON-NLS-1$

    // Strings for AboutPane and Splash
    // The splash image is of an English version of the application
    static final Msg SPLASH_IMAGE = new Msg("Splash.SplashImage"); //$NON-NLS-1$
    static final Msg VERSION_TITLE = new Msg("Splash.VersionTitle"); //$NON-NLS-1$
    static final Msg ABOUT_TITLE = new Msg("AboutPane.AboutTitle"); //$NON-NLS-1$
    static final Msg TASK_TAB_TITLE = new Msg("AboutPane.TaskTabTitle"); //$NON-NLS-1$
    static final Msg ERROR_TAB_TITLE = new Msg("AboutPane.ErrorTabTitle"); //$NON-NLS-1$
    static final Msg SYSTEM_PROPS_TAB_TITLE = new Msg("AboutPane.SystemPropsTabTitle"); //$NON-NLS-1$
    static final Msg DEBUG_TAB_TITLE = new Msg("AboutPane.DebugTabTitle"); //$NON-NLS-1$

    // Strings for StatusBar
    static final Msg STATUS_DEFAULT = new Msg("StatusBar.StatusDefault"); //$NON-NLS-1$

    // Strings for OptionsAction
    static final Msg CONFIG_TITLE = new Msg("OptionsAction.ConfigTitle"); //$NON-NLS-1$

    // Strings for ComparePane
    static final Msg COMPARE_DIALOG = new Msg("Bible Compare"); //$NON-NLS-1$
    static final Msg COMPARE_IDENT_QUESTION = new Msg("You are attempting to compare 2 Books that are identical.\nDo you want to continue?"); //$NON-NLS-1$
    static final Msg COMPARE_IDENT_TITLE = new Msg("Compare Identical Books?"); //$NON-NLS-1$
    public static final Msg DEBUG_VIEWS = new Msg("Views:"); //$NON-NLS-1$
    static final Msg COMPARE_WORDS = new Msg("Words:  "); //$NON-NLS-1$
    static final Msg COMPARE_GO = new Msg("Compare"); //$NON-NLS-1$
    static final Msg COMPARE_USING = new Msg("Compare Using"); //$NON-NLS-1$
    static final Msg COMPARE_WORDS_TIP = new Msg("[empty] - test no words; * - test all words, text - test all words starting with 'text'"); //$NON-NLS-1$
    static final Msg COMPARE_TITLE = new Msg("Books To Compare"); //$NON-NLS-1$
    static final Msg COMPARE_VERSES = new Msg("Verses: "); //$NON-NLS-1$

    // Strings for CompareResultsPane
    static final Msg RESULTS_TITLE = new Msg("Results"); //$NON-NLS-1$
    static final Msg RESULTS_START = new Msg("Start"); //$NON-NLS-1$
    static final Msg RESULTS_CLOSE = new Msg("Close"); //$NON-NLS-1$
    static final Msg RESULTS_DIALOG = new Msg("Verify Results"); //$NON-NLS-1$
    static final Msg RESULTS_BOOKS = new Msg("Books:"); //$NON-NLS-1$
    static final Msg RESULTS_COMPARING = new Msg("Comparing:"); //$NON-NLS-1$
    static final Msg RESULTS_PASSAGE = new Msg("Passage"); //$NON-NLS-1$
    static final Msg RESULTS_WORDS = new Msg("Word"); //$NON-NLS-1$
    protected static final Msg DEBUG_STEPS = new Msg("Step {0}/{1}"); //$NON-NLS-1$
    static final Msg RESULTS_STOP = new Msg("Stop"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
