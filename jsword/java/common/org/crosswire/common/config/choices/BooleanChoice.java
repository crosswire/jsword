
package org.crosswire.common.config.choices;

import org.crosswire.common.util.Convert;

/**
 * BooleanChoice.
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
public abstract class BooleanChoice extends AbstractChoice
{
    /**
     * Generalized read boolean from the Properties file
     * @return Found boolean or the default value
     */
    public abstract boolean getBoolean();

    /**
     * Generalized set boolean to the Properties file
     * @param value The value to enter
     */
    public abstract void setBoolean(boolean value) throws Exception;

    /**
     * Generalized read boolean from the Properties file
     * @return Found boolean or the default value
     */
    public String getString()
    {
        return Convert.boolean2String(getBoolean());
    }

    /**
     * Generalized set boolean to the Properties file
     * @param value The value to enter
     */
    public void setString(String value) throws Exception
    {
        setBoolean(Convert.string2Boolean(value));
    }

    /**
     * Override this to check and note any change
     */
    public String getType()
    {
        return "boolean";
    }
}
