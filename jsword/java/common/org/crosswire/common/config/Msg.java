
package org.crosswire.common.config;

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
    static final Msg CONFIG_NOCLASS = new Msg("Specified class not found: {0}");
    static final Msg CONFIG_MISSINGELE = new Msg("Missing {0} element in config.xml");
    static final Msg CONFIG_NOSETTER = new Msg("Specified method not found {0}.set{1}({2} arg0)");
    static final Msg CONFIG_NOGETTER = new Msg("Specified method not found {0}.get{1}()");
    static final Msg CONFIG_NORETURN = new Msg("Mismatch of return types, found: {0} required: {1}");
    static final Msg CONFIG_NOMAP = new Msg("Missing <map> element.");

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
