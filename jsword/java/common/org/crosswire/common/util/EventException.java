
package org.crosswire.common.util;

import java.util.ResourceBundle;
import java.text.*;

/**
* EventExceptions are generally used for passing problems through
* the event system which does not allow checked exceptions through.
*
* <p>So EventException is a LucidException in all but inheritance -
* LucidException inherits from Exception and so is checked, where
* EventEception inherits from RuntimeException and so is not
* checked. In general you would create a subclass of LucidException
* before you used it, however EventExceptions would be used directly.
* </p>
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
* @see LucidException
* @author Joe Walker
* @version D0.I0.T0
*/
public class EventException extends RuntimeException
{
    /**
    * All LucidExceptions are constructed with references to resources in
    * an I18N properties file.
    * @param msg The resource id to read
    */
    public EventException(String msg)
    {
        super(msg);

        this.ex = null;
        this.params = null;
    }

    /**
    * All LucidExceptions are constructed with references to resources in
    * an I18N properties file.
    * @param msg The resource id to read
    */
    public EventException(String msg, Throwable ex)
    {
        super(msg);

        this.ex = ex;
        this.params = null;
    }

    /**
    * All LucidExceptions are constructed with references to resources in
    * an I18N properties file. This version allows us to add parameters
    * @param msg The resource id to read
    * @param params An array of parameters
    */
    public EventException(String msg, Object[] params)
    {
        super(msg);

        this.ex = null;
        this.params = params;
    }

    /**
    * All LucidExceptions are constructed with references to resources in
    * an I18N properties file. This version allows us to add parameters
    * @param msg The resource id to read
    * @param params An array of parameters
    */
    public EventException(String msg, Throwable ex, Object[] params)
    {
        super(msg);

        this.ex = ex;
        this.params = params;
    }

    /**
    * We only unravel the message when we need to to save time
    * @return The unraveled I18N string
    */
    public String getMessage()
    {
        String id = super.getMessage();
        String msg;

        try
        {
            msg = res.getString(id);
        }
        catch (Exception ex)
        {
            return "Error fetching resource for '"+id+"'";
        }

        try
        {
            MessageFormat formatter = new MessageFormat(msg);
            return formatter.format(params);
        }
        catch (Exception ex)
        {
            return "Error formatting message '"+msg+"'";
        }
    }

    /**
    * The nested Exception (is any)
    * @return The Exception
    */
    public Throwable getException()
    {
        return ex;
    }

    /** An embedded exception */
    protected Throwable ex;

    /** The array of parameters */
    protected Object[] params;

    /** The resource hash */
    protected static ResourceBundle res = ResourceBundle.getBundle("Exception");
}
