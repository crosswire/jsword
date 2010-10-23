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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: Msg.java 1466 2007-07-02 02:48:09Z dmsmith $
 */
package org.crosswire.jsword.book.readings;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class UserMsg extends MsgBase {

    /**
     * Get the internationalized text, but return key if key is unknown.
     * 
     * @param key
     * @return the internationalized text
     */
    public static String gettext(String key)
    {
        return msg.lookup(key);
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     * The text requires one parameter to be passed.
     * 
     * @param key
     * @param param
     * @return the formatted, internationalized text
     */
    public static String gettext(String key, Object param)
    {
        return msg.toString(key, param);
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     * The text requires one parameter to be passed.
     * 
     * @param key
     * @param param
     * @return the formatted, internationalized text
     */
    public static String gettext(String key, Object[] params)
    {
        return msg.toString(key, params);
    }

    private static MsgBase msg = new UserMsg();
}
