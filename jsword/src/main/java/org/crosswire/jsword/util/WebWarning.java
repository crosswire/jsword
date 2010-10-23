/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.util;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;

/**
 * Provide a configurable warning that the Internet is going to be accessed.
 * This is important in places where Internet activity may be monitored and
 * Christians may be persecuted.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class WebWarning {
    /**
     * This is a utility class, thus it's constructor is private.
     */
    private WebWarning() {
        try {
            Properties props = ResourceUtil.getProperties(getClass().getName());
            shown = Boolean.valueOf(props.getProperty(SHOWN_KEY, Boolean.valueOf(DEFAULT_SHOWN).toString())).booleanValue();
        } catch (IOException e) {
            shown = DEFAULT_SHOWN;
        }
    }

    /**
     * All access to WebWarning is through this single instance.
     * 
     * @return the singleton instance
     */
    public static WebWarning instance() {
        return instance;
    }

    /**
     * @param newShown
     *            Whether this WebWarning should be shown.
     */
    public void setShown(boolean newShown) {
        try {
            shown = newShown;
            Properties props = new Properties();
            props.put(SHOWN_KEY, Boolean.valueOf(shown).toString());
            URI outputURI = CWProject.instance().getWritableURI(getClass().getName(), FileUtil.EXTENSION_PROPERTIES);
            NetUtil.storeProperties(props, outputURI, "JSword WebWarning");
        } catch (IOException ex) {
            log.error("Failed to save JSword WebWarning", ex);
        }
    }

    /**
     * @return Whether this WebWarning should be shown.
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * From configuration set the state.
     * 
     * @param newShown
     *            Whether this WebWarning should be shown.
     */
    public static void setWarningShown(boolean newShown) {
        WebWarning.instance().setShown(newShown);
    }

    /**
     * @return Whether this WebWarning should be shown.
     */
    public static boolean isWarningShown() {
        return WebWarning.instance().isShown();
    }

    /**
     * @return a warning that the Internet is about to be accessed
     */
    public String getWarning() {
        // TRANSLATOR: Warn the user that the program is about to access the Internet.
        // In some countries, this warning may be too bland. It might be better to warn the user that this might
        // put them at risk of persecution.
        return UserMsg.gettext("You are about to access the Internet. Are you sure you want to do this?");
    }

    /**
     * @return indicate that the warning will be shown again
     */
    public String getShownWarningLabel() {
        // TRANSLATOR: This labels a checkbox, which is checked by default.
        // Unchecking it allows the user to not see the message again but the Internet will be accessed.
        return UserMsg.gettext("Show this warning every time the Internet is accessed.");
    }

    private static WebWarning instance = new WebWarning();

    private static final String SHOWN_KEY = "shown";
    private static final boolean DEFAULT_SHOWN = true;
    private boolean shown;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(WebWarning.class);
}
