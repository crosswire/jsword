
package org.crosswire.common.config.choices;

import org.crosswire.common.config.ChoiceSet;
import org.crosswire.common.config.Config;
import org.crosswire.common.util.Reporter;

/**
 * Select which Extenders to allow to extend the config.
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
public class ChoiceSetsChoice extends StringArrayChoice
{
    /**
     * Create a new ExtenderChoice
     */
    public ChoiceSetsChoice(Config config)
    {
        this.config = config;
    }

    /**
     * Generalized read Object from the Properties file
     * @return Found int or the default value
     */
    public String[] getArray()
    {
        return extenders;
    }

    /**
     * Generalized set Object to the Properties file
     * @param value The value to enter
     */
    public void setArray(String[] value) throws Exception
    {
        // First remove all the old ones
        for (int i=0; i<extenders.length; i++)
        {
            try
            {
                ChoiceSet ce = (ChoiceSet) Class.forName(extenders[i]).newInstance();
                ce.setConfig(config);
                ce.remove();
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }

        extenders = value;

        // The add the new ones
        for (int i=0; i<extenders.length; i++)
        {
            try
            {
                ChoiceSet ce = (ChoiceSet) Class.forName(extenders[i]).newInstance();
                ce.setConfig(config);
                ce.add();
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Sometimes we need to ensure that we configure items in a certain
     * order, the config package moves the changes to the application
     * starting with the highest priority, moving to the lowest
     * @return A priority level
     */
    public int priority()
    {
        return PRIORITY_EXTENDER;
    }

    /** The Config that we are extending */
    private Config config;

    /** The current list of extenders */
    private String[] extenders = new String[0];
}
