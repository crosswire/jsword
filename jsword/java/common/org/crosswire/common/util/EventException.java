
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
 * @see LucidException
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
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file.
     * @param msg The resource id to read
     */
    public EventException(String msg, Throwable cause)
    {
        super(msg);

        this.cause = cause;
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

        this.params = params;
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public EventException(String msg, Throwable cause, Object[] params)
    {
        super(msg);

        this.cause = cause;
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
        return cause;
    }

    /** An embedded exception */
    protected Throwable cause = null;

    /** The array of parameters */
    protected Object[] params = null;

    /** The resource hash */
    protected static final ResourceBundle res = ResourceBundle.getBundle("Exception");
}
