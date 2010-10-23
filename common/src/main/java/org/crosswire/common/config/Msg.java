/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.config;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase {
    static final Msg CONFIG_NOCLASS = new Msg("Specified class not found: {0}");
    static final Msg CONFIG_MISSINGELE = new Msg("Missing {0} element in config.xml");
    static final Msg CONFIG_NOSETTER = new Msg("Specified method not found {0}.set{1}({2} arg0)");
    static final Msg CONFIG_NOGETTER = new Msg("Specified method not found {0}.get{1}()");
    static final Msg CONFIG_NORETURN = new Msg("Mismatch of return types, found: {0} required: {1}");
    static final Msg CONFIG_NOMAP = new Msg("Missing <map> element.");
    static final Msg IGNORE = new Msg("Ignoring invalid option: {0}");
    static final Msg CONFIG_SETFAIL = new Msg("Failed to set option: {0}");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
