
package org.crosswire.common.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.enum.Enum;
import org.apache.log4j.Logger;

/**
 * A base class for implementing type safe internationalization (i18n) that is
 * easy for most cases. See { @see org.crosswire.common.util.Msg } for an
 * example of how to inherit from here.
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
 * @see org.crosswire.common.util.Msg
 */
public class MsgBase extends Enum
{
    /* (non-Javadoc)
     * @see org.apache.commons.lang.enum.Enum#toString()
     */
    public String toString()
    {
        String base = super.getName();

        if (resources != null)
        {
            try
            {
                return resources.getString(base);
            }
            catch (MissingResourceException ex)
            {
                log.warn("missing resource in "+Locale.getDefault().getDisplayName()+" for "+base);
                return base;
            }
        }

        return base;
    }

    /**
     * Initialise any resource bundles
     */
    protected static void init(String pkgname)
    {
        try
        {
            resources = ResourceBundle.getBundle(pkgname);
            log.debug("Using resources for "+pkgname+" from locale "+Locale.getDefault().getDisplayName());
        }
        catch (MissingResourceException ex)
        {
            log.debug("Using default resources for "+pkgname);
        }
    }

    /**
     * Simple ctor
     */
    protected MsgBase(String name)
    {
        super(name);
    }

    protected static ResourceBundle resources;
    protected static Logger log = Logger.getLogger(MsgBase.class);
}
