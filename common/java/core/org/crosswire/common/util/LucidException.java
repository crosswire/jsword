package org.crosswire.common.util;

import java.text.MessageFormat;

/**
 * A LucidException adds 2 concepts to a base Exception, that of a wrapped
 * Exception, that of internationalised (i18n) messages.
 *
 * <p>The first addition is the concept of an optional wrapped Exception
 * (actually a Throwable), which describes what caused this to happen. Any well
 * defined interface will define the exact exceptions that the methods of that
 * interface will throw, and not leave it to the ambiguous "throws Exception".
 * However the interface should have no idea how it will be implemented and so
 * the details of exactly what broke under the covers gets lost. With
 * LucidException this detail is kept in the wrapped Exception. This
 * functionallity has been added to the base Exception class in J2SE 1.4</p>
 *
 * <p>The second addition is the concept of i18n messages. Normal Exceptions are
 * created with an almost random string in the message field, LucidExceptions
 * define this string to be a key into a resource bundle, and to help formatting
 * this string there is an optional Object array of format options. There is
 * a constructor that allows us to specify no i18n lookup, which is useful
 * if this lookup may have been done already.</p>
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
 * @see LucidRuntimeException
 */
public class LucidException extends Exception
{
    /**
     * All LucidExceptions are constructed with references to resources in
     * an i18n properties file.
     * @param msg The resource id to read
     */
    public LucidException(MsgBase msg)
    {
        this(msg, null, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an i18n properties file.
     * @param msg The resource id to read
     */
    public LucidException(MsgBase msg, Throwable cause)
    {
        this(msg, cause, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an i18n properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(MsgBase msg, Object[] params)
    {
        this(msg, null, params);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an i18n properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(MsgBase msg, Throwable cause, Object[] params)
    {
        super(msg.toString(), cause);
        this.deprecated = false;
        this.params = params;
    }

    /**
     * We only unravel the message when we need to to save time
     * @return The unraveled i18n string
     */
    public String getMessage()
    {
        String out = super.getMessage();

        if (deprecated || params == null)
        {
            return out;
        }

        try
        {
            return MessageFormat.format(out, params);
        }
        catch (IllegalArgumentException ex)
        {
            log.warn("Format fail for '" + out + "'", ex); //$NON-NLS-1$ //$NON-NLS-2$
            return "Error formatting message '" + out + "'"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Accessor of the full detailed version of the string
     * @return The full unraveled i18n string
     */
    public String getDetailedMessage()
    {
        Throwable cause = getCause();
        if (cause == null)
        {
            return getMessage();
        }

        if (cause instanceof LucidException)
        {
            LucidException lex = (LucidException) cause;
            return getMessage() + Msg.REASON + lex.getDetailedMessage();
        }
        else
        {
            return getMessage() + Msg.REASON + cause.getMessage();
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LucidException.class);

    /**
     * SERIALUID(dm): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Is the message to be included literally (ie passed a string), or should we look it up as a
     * resource (ie passed a MsgBase).
     */
    private final boolean deprecated;

    /**
     * The array of parameters
     */
    protected final Object[] params;
}
