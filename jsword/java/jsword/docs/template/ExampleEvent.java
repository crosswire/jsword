
package docs.template;

import java.util.*;

/**
 * An ExampleEvent happens whenever an example happens.
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
 * @see Bible
 * @see ProgressListener
 * @author Joe Walker
 * @version D7.I6.T2
 */
public class ExampleEvent extends EventObject
{
    /**
     * Initialize a ExampleEvent
     * @param source The thing that started this off
     * @param param The parameter to this event
     */
    public ExampleEvent(Object source, String param)
    {
        super(source);

        this.param = param;
    }

    /**
     * Initialize an ExampleEvent
     * @param source The thing that started this off
     */
    public ExampleEvent(Object source)
    {
        super(source);

        param = "";
    }

    /**
     * Get the parameter
     * @return The parameter
     */
    public String getParam()
    {
        return param;
    }

    /**
     * The parameter
     */
    private String param;
}
