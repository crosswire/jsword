
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
 * functionallity has been added to the base Exception class in JDK 1.4</p>
 *
 * <p>The second addition is the concept of i18n messages. Normal Exceptions are
 * created with an almost random string in the message field, LucidExceptions
 * define this string to be a key into a resource bundle, and to help formatting
 * this string there is an optional Object array of format options. There is
 * a constructor that allows us to specify no I18N lookup, which is useful
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 * @see EventException
 * @see LogicError
 */
public class LucidException extends Exception
{
    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file.
     * @param msg The resource id to read
     */
    public LucidException(MsgBase msg)
    {
        this(msg, null, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file.
     * @param msg The resource id to read
     */
    public LucidException(MsgBase msg, Throwable cause)
    {
        this(msg, cause, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(MsgBase msg, Object[] params)
    {
        this(msg, null, params);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(MsgBase msg, Throwable cause, Object[] params)
    {
        super(msg.toString());

        this.cause = cause;
        this.params = params;
    }

    /**
     * Special ctor, not for general use where the string is generated at
     * runtime, and can't be parameterized.
     * @param msg The resource id to read
     * @see org.crosswire.jsword.book.remote.RemoterException#RemoterException(String, Class)
     */
    public LucidException(String msg, boolean literal)
    {
        this(new MsgBase(msg), null, null);
        this.literal = literal;
    }

    /**
     * We only unravel the message when we need to to save time
     * @return The unraveled I18N string
     */
    public String getMessage()
    {
        String out = super.getMessage();

        if (literal || params == null)
        {
            return out;
        }

        try
        {
            MessageFormat formatter = new MessageFormat(out);
            return formatter.format(params);
        }
        catch (Exception ex)
        {
            log.warn("Format fail for '"+out+"'", ex);
            return "Error formatting message '"+out+"'";
        }
    }

    /**
     * Accessor of the full detailed version of the string
     * @return The full unraveled I18N string
     */
    public String getDetailedMessage()
    {
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
     * The nested Exception (if any)
     * @return The Exception
     */
    public Throwable getCause()
    {
        return cause;
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(LucidException.class);

    /**
     * Is the message to be included literally, or should we look it up as a
     * resource.
     */
    private boolean literal = false;

    /**
     * An embedded exception
     */
    protected Throwable cause;

    /**
     * The array of parameters
     */
    protected Object[] params;
}
