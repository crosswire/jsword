
package org.crosswire.common.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.text.*;

/**
 * A LucidException adds 3 concepts to a base Exception, that of a wrapped
 * Exception, that of internationalised (i18n) messages, and that of an internal
 * string which is for debugging/logging purposes.
 *
 * <p>The first addition is the concept of an optional wrapped Exception
 * (actually a Throwable), which describes what caused this to happen. Any well
 * defined interface will define the exact exceptions that the methods of that
 * interface will throw, and not leave it to the ambiguous "throws Exception".
 * However the interface should have no idea how it will be implemented and so
 * the details of exactly what broke under the covers gets lost. With
 * LucidException this detail is kept in the wrapped Exception. This
 * functionallity is expected to be added to the base Exception class in JDK
 * 1.4</p>
 *
 * <p>The second addition is the concept of i18n messages. Normal Exceptions are
 * created with an almost random string in the message field, LucidExceptions
 * define this string to be a key into a resource bundle, and to help formatting
 * this string there is an optional Object array of format options.</p>
 *
 * <p>The third addition is that of a debug/logging string. Some information
 * about what went wrong is not for the eyes of the user (especially in a web
 * environment) but we may like to remember what broke.</p>
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
    public LucidException(String msg)
    {
        this(msg, null, null, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file.
     * @param msg The resource id to read
     */
    public LucidException(String msg, Throwable ex)
    {
        this(msg, ex, null, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(String msg, Object[] params)
    {
        this(msg, null, params, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(String msg, Throwable ex, Object[] params)
    {
        this(msg, ex, params, null);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file.
     * @param msg The resource id to read
     */
    public LucidException(String msg, String internal)
    {
        this(msg, null, null, internal);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file.
     * @param msg The resource id to read
     */
    public LucidException(String msg, Throwable ex, String internal)
    {
        this(msg, ex, null, internal);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(String msg, Object[] params, String internal)
    {
        this(msg, null, params, internal);
    }

    /**
     * All LucidExceptions are constructed with references to resources in
     * an I18N properties file. This version allows us to add parameters
     * @param msg The resource id to read
     * @param params An array of parameters
     */
    public LucidException(String msg, Throwable ex, Object[] params, String internal)
    {
        super(msg);

        this.ex = ex;
        this.params = params;
        this.internal = internal;

        try
        {
            if (res == null)
                setDefaultResourceBundleName();
        }
        catch (Throwable ex2)
        {
            Reporter.informUser(this, ex2);
        }
    }

    /**
     * We only unravel the message when we need to to save time
     * @return The unraveled I18N string
     */
    public String getMessage()
    {
        String id = super.getMessage();
        String out = getResource(id);

        try
        {
            MessageFormat formatter = new MessageFormat(out);
            return formatter.format(params);
        }
        catch (Exception ex)
        {
            return "Error formatting message '"+out+"'";
        }
    }

    /**
     * Accessor of the full detailed version of the string
     * @return The full unraveled I18N string
     */
    public String getDetailedMessage()
    {
        if (ex == null)
            return getMessage();

        // avoid an NPE is res has not been set up.
        String reason = getResource("reason");

        if (ex instanceof LucidException)
        {
            LucidException lex = (LucidException) ex;
            return getMessage() + reason + lex.getDetailedMessage();
        }
        else
        {
            return getMessage() + reason + ex.getMessage();
        }
    }

    /**
     * Utility that enables us to have a single resource file for all the
     * passage classes
     * @param id The resource id to fetch
     * @return The String from the resource file
     */
    protected static String getResource(String id)
    {
        if (res == null)
            return "Missing resources when looking up: "+id;

        try
        {
            return res.getString(id);
        }
        catch (MissingResourceException ex)
        {
            return "Missing resource for: "+id;
        }
    }

    /**
     * Accessor for the message private to the developers
     * @return The internal message
     */
    public String getInternalMessage()
    {
        return internal;
    }

    /**
     * The nested Exception (if any)
     * @return The Exception
     */
    public Throwable getCause()
    {
        return ex;
    }

    /**
     * A way of setting the name of the resource bundle to use. The default
     * bundle is named Exception
     * @param name The new resource bundle name
     */
    public static final void setResourceBundleName(String name)
    {
        res = ResourceBundle.getBundle(name);
        
        if (res == null)
            System.err.println("Failed to find ResourceBundle for "+name);
    }

    /**
     * A way of setting the name of the resource bundle to use. The default
     * bundle is named Exception
     * @param name The new resource bundle name
     */
    public static final void setDefaultResourceBundleName()
    {
        setResourceBundleName("Exception");
    }

    /** An embedded exception */
    protected Throwable ex;

    /** The array of parameters */
    protected Object[] params;

    /** The array of parameters */
    protected String internal;

    /** The resource hash */
    protected static ResourceBundle res = null;
}
