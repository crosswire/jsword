package org.crosswire.common.util;

import java.text.MessageFormat;

/**
 * EventExceptions are generally used for passing problems through
 * the event system which does not allow checked exceptions through.
 *
 * <p>So LucidRuntimeException is a LucidException in all but inheritance -
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
public class LucidRuntimeException extends RuntimeException
{
    /**
     * All LucidRuntimeException are constructed with references to resources in
     * an i18n properties file.
     * @param msg The resource id to read
     */
    public LucidRuntimeException(MsgBase msg)
    {
        this(msg, null, null);
    }

    /**
     * All LucidRuntimeException are constructed with references to resources in
     * an i18n properties file.
     * @param msg The resource id to read
     */
    public LucidRuntimeException(MsgBase msg, Throwable cause)
    {
        this(msg, cause, null);
    }

    /**
     * All LucidRuntimeException are constructed with references to resources in
     * an i18n properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidRuntimeException(MsgBase msg, Object[] params)
    {
        this(msg, null, params);
    }

    /**
     * All LucidRuntimeException are constructed with references to resources in
     * an i18n properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidRuntimeException(MsgBase msg, Throwable cause, Object[] params)
    {
        super(msg.toString(), cause);

        this.params = (Object[]) params.clone();
    }

    /**
     * We only unravel the message when we need to to save time
     * @return The unraveled i18n string
     */
    public String getMessage()
    {
        String out = super.getMessage();

        if (params == null)
        {
            return out;
        }

        try
        {
            return MessageFormat.format(out, params);
        }
        catch (IllegalArgumentException ex)
        {
            log.warn("Format fail for '" + out + '\'', ex); //$NON-NLS-1$
            return "Error formatting message '" + out + '\''; //$NON-NLS-1$
        }
     }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LucidRuntimeException.class);

    /**
     * SERIALUID(dm): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The array of parameters
     */
    protected final Object[] params;
}
