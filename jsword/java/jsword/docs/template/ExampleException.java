

package docs.template;

import org.crosswire.common.util.LucidException;

/**
 * Something failed.
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
public class ExampleException extends LucidException
{
    /**
     * Construct the Exception with a message
     * @param msg The resource id to read
     */
    public ExampleException(String msg)
    {
        super(msg);
    }

    /**
     * Construct the Exception with a message and a nested Exception
     * @param msg The resource id to read
     * @param ex The nested Exception
     */
    public ExampleException(String msg, Throwable ex)
    {
        super(msg, ex);
    }

    /**
     * Construct the Exception with a message and some I18N params
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public ExampleException(String msg, Object[] params)
    {
        super(msg, params);
    }

    /**
     * Construct the Exception with a message, a nested Exception
     * and some I18N params
     * @param msg The resource id to read
     * @param ex The nested Exception
     * @param params An array of parameters
     */
    public ExampleException(String msg, Throwable ex, Object[] params)
    {
        super(msg, ex, params);
    }
}

