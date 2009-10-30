/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the Internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */

package org.crosswire.common.icu;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.ReflectionUtil;

/**
 * DateFormat provides a wrapper of some of DateFormat and SimpleDateFormat
 * using ICU4J if present, otherwise from core Java. Note, only those methods in
 * DateFormat that are actually used are here.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DateFormatter {
    /**
     * Prevent instantiation.
     */
    private DateFormatter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.DateFormat#getDateInstance(int)
     */
    public static DateFormatter getDateInstance(int format) {
        DateFormatter fmt = new DateFormatter();
        boolean oops = false;
        try {
            fmt.formatterClass = ClassUtil.forName("com.ibm.icu.text.DateFormat"); //$NON-NLS-1$
            // To call a method taking a type of int, the type has to match but
            // the object has to be wrapped
            Class[] instanceTypes = {
                int.class
            };
            Object[] instanceParams = {
                new Integer(format)
            };
            fmt.formatter = ReflectionUtil.invoke(fmt.formatterClass, fmt.formatterClass, "getDateInstance", instanceParams, instanceTypes); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            oops = true;
        } catch (IllegalAccessException e) {
            oops = true;
        } catch (InvocationTargetException e) {
            oops = true;
        } catch (ClassNotFoundException e) {
            oops = true;
        }

        if (oops) {
            fmt.formatterClass = DateFormat.class;
            fmt.formatter = DateFormat.getDateInstance(format);
        }

        return fmt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.DateFormat#getDateInstance()
     */
    public static DateFormatter getDateInstance() {
        return getDateInstance(DEFAULT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.DateFormat#getDateInstance(int)
     */
    public static DateFormatter getSimpleDateInstance(String format) {
        DateFormatter fmt = new DateFormatter();
        boolean oops = false;
        try {
            fmt.formatterClass = ClassUtil.forName("com.ibm.icu.text.SimpleDateFormat"); //$NON-NLS-1$
            Object[] instanceParams = {
                format
            };
            fmt.formatter = ReflectionUtil.construct("com.ibm.icu.text.SimpleDateFormat", instanceParams); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            oops = true;
        } catch (IllegalAccessException e) {
            oops = true;
        } catch (InvocationTargetException e) {
            oops = true;
        } catch (ClassNotFoundException e) {
            oops = true;
        } catch (InstantiationException e) {
            oops = true;
        }

        if (oops) {
            fmt.formatterClass = SimpleDateFormat.class;
            fmt.formatter = new SimpleDateFormat(format);
        }

        return fmt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.DateFormat#setLenient(boolean)
     */
    public void setLenient(boolean lenient) {
        try {
            Class[] lenientTypes = {
                boolean.class
            };
            Object[] lenientParams = {
                Boolean.valueOf(lenient)
            };
            ReflectionUtil.invoke(formatterClass, formatter, "setLenient", lenientParams, lenientTypes); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            assert false : e;
        } catch (IllegalAccessException e) {
            assert false : e;
        } catch (InvocationTargetException e) {
            assert false : e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.DateFormat#format(java.util.Date)
     */
    public String format(Date date) {
        try {
            Object[] formatParams = {
                date
            };
            return (String) ReflectionUtil.invoke(formatterClass, formatter, "format", formatParams); //$NON-NLS-1$
        } catch (Exception e) {
            assert false : e;
            return ""; //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.text.DateFormat#parse(java.lang.String)
     */
    public Date parse(String text) throws ParseException {
        try {
            Object[] parseParams = {
                text
            };
            return (Date) ReflectionUtil.invoke(formatterClass, formatter, "parse", parseParams); //$NON-NLS-1$
        } catch (Exception e) {
            if (e instanceof ParseException) {
                throw (ParseException) e;
            }

            assert false : e;
            return new Date();
        }
    }

    // Note these values are the same for Java and ICU4J
    /**
     * Constant for full style pattern.
     */
    public static final int FULL = 0;
    /**
     * Constant for long style pattern.
     */
    public static final int LONG = 1;
    /**
     * Constant for medium style pattern.
     */
    public static final int MEDIUM = 2;
    /**
     * Constant for short style pattern.
     */
    public static final int SHORT = 3;
    /**
     * Constant for default style pattern. Its value is MEDIUM.
     */
    public static final int DEFAULT = MEDIUM;

    /** The actual formatter. */
    private Object formatter;

    /** The class of the formatter */
    private Class formatterClass;
}
