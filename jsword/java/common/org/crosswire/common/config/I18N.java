
package org.crosswire.common.config;

import org.crosswire.common.util.I18NBase;

/**
 * Compile safe I18N resource settings.
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
public class I18N extends I18NBase
{
    public static final I18N CONFIG_NOCLASS = new I18N("Specified class not found '{0}'");
    public static final I18N CONFIG_MISSINGELE = new I18N("Missing {0} element in config.xml");
    public static final I18N CONFIG_NOSETTER = new I18N("Specified method not found {0}.set{1}({2} arg0)");
    public static final I18N CONFIG_NOGETTER = new I18N("Specified method not found {0}.get{1}()");
    public static final I18N CONFIG_NORETURN = new I18N("Mismatch of return types, found: {0} required: {1}");
    public static final I18N CONFIG_NOMAP = new I18N("Missing <map> element.");

    /** Initialise any resource bundles */
    static
    {
        init(I18N.class.getName());
    }

    /** Passthrough ctor */
    private I18N(String name)
    {
        super(name);
    }
}
