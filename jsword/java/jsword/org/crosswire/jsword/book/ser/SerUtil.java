
package org.crosswire.jsword.book.ser;

import org.crosswire.jsword.book.config.CacheBiblesChoice;
import org.crosswire.jsword.book.config.DriversChoice;
import org.crosswire.jsword.book.raw.config.CacheDataChoice;
import org.crosswire.jsword.util.Project;
import org.crosswire.common.config.Config;
import org.crosswire.common.swing.config.DisplayExceptionChoice;
import org.crosswire.common.swing.config.ShelfExceptionChoice;
import org.crosswire.common.swing.config.SourcePathChoice;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.config.UserLevelChoice;

/**
 * Quick utils stuff for the SerBible classes.
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
 * @version D0.I0.T0
 */
public class SerUtil
{
    /**
     * A quick hack to convert the old serialized system to a the new ascii
     * version.
     */
    public static void main(String[] args)
    {
        try
        {
            Project.init("jbuilder");

            Config config = new Config("Tool Shed Options");
            config.add("Bibles.Cache Versions", new CacheBiblesChoice());
            config.add("Bibles.Raw.Cache Data", new CacheDataChoice());

            config.add("Reports.Exceptions to Dialog Box", new DisplayExceptionChoice());
            config.add("Reports.Exceptions to Log Window", new ShelfExceptionChoice());

            config.add("Advanced.Source Path", new SourcePathChoice());
            config.add("Advanced.User Level", new UserLevelChoice());
            config.add("Advanced.Available Drivers", new DriversChoice());

            config.setProperties(Project.resource().getProperties("Desktop"));
            config.localToApplication(true);

            // SerBible ser = (SerBible) Bibles.getBible("av-ser");
            // ser.convert();
        }
        catch (Exception ex)
        {
            Reporter.informUser(SerBible.class, ex);
        }
    }

}