/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.jsword.util;

import java.io.IOException;
import java.net.URI;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.JSMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide a configurable warning that the Internet is going to be accessed.
 * This is important in places where Internet activity may be monitored and
 * Christians may be persecuted.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class WebWarning {
    /**
     * This is a utility class, thus it's constructor is private.
     */
    private WebWarning() {
        try {
            PropertyMap props = ResourceUtil.getProperties(getClass().getName());
            shown = Boolean.valueOf(props.get(SHOWN_KEY, Boolean.valueOf(DEFAULT_SHOWN).toString())).booleanValue();
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
            PropertyMap props = new PropertyMap();
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
        return JSMsg.gettext("You are about to access the Internet. Are you sure you want to do this?");
    }

    /**
     * @return indicate that the warning will be shown again
     */
    public String getShownWarningLabel() {
        // TRANSLATOR: This labels a checkbox, which is checked by default.
        // Unchecking it allows the user to not see the message again but the Internet will be accessed.
        return JSMsg.gettext("Show this warning every time the Internet is accessed.");
    }

    private static WebWarning instance = new WebWarning();

    private static final String SHOWN_KEY = "shown";
    private static final boolean DEFAULT_SHOWN = true;
    private boolean shown;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(WebWarning.class);
}
