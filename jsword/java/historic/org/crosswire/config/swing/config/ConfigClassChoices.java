
package org.crosswire.config.swing.config;

import java.util.*;

import org.crosswire.config.*;
import org.crosswire.config.choices.*;
import org.crosswire.config.swing.*;

/**
* The ConfigChoices class creates some Choices that
* control the display on the JConfigure class itself.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class ConfigClassChoices extends ClassChoices
{
    /** Changing the look of the config dialog */
    public ConfigClassChoices() throws ClassNotFoundException
    {
        super(BaseConfig.class, defaults);
    }

    /** The Options customization */
    protected Class getCurrentClass()
    {
        return SwingConfig.getDisplayClass();
    }

    /** The Options customization */
    protected void setCurrentClass(Class new_class)
    {
        SwingConfig.setDisplayClass(new_class);
    }

    /** The default Configs */
    private static Hashtable defaults = new Hashtable();

    /**
    * Setup the defaults Hashtable
    */
    static
    {
        defaults.put("Tree", "com.eireneh.config.swing.TreeConfig");
        defaults.put("Tabbed", "com.eireneh.config.swing.TabbedConfig");
        defaults.put("Wizard", "com.eireneh.config.swing.WizardConfig");
        defaults.put("Advanced", "com.eireneh.config.swing.AdvancedConfig");
    }
}

