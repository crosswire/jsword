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
    static final Msg CONFIG_NOCLASS = new Msg("AbstractReflectedChoice.ConfigNoClass"); //$NON-NLS-1$
    static final Msg CONFIG_MISSINGELE = new Msg("AbstractReflectedChoice.ConfigMissingEle"); //$NON-NLS-1$
    static final Msg CONFIG_NOSETTER = new Msg("AbstractReflectedChoice.ConfigNoSetter"); //$NON-NLS-1$
    static final Msg CONFIG_NOGETTER = new Msg("AbstractReflectedChoice.ConfigNoGetter"); //$NON-NLS-1$
    static final Msg CONFIG_NORETURN = new Msg("AbstractReflectedChoice.ConfigNoReturn"); //$NON-NLS-1$
    static final Msg CONFIG_NOMAP = new Msg("StringOptionsChoice.ConfigNoMap"); //$NON-NLS-1$
    static final Msg NO_HELP = new Msg("AbstractReflectedChoice.NoHelp"); //$NON-NLS-1$
    static final Msg IGNORE = new Msg("IntOptionsChoice.Ignore"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
