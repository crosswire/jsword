/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.icu;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DateFormat provides a wrapper of some of DateFormat and SimpleDateFormat
 * using ICU4J if present, otherwise from core Java. Note, only those methods in
 * DateFormat that are actually used are here.
 *
 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
public final class DateFormatter {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DateFormatter.class);
    private static final String DEFAULT_SIMPLE_DATE_FORMAT_CLASS = "com.ibm.icu.text.SimpleDateFormat";
    private static final String DEFAULT_DATE_FORMAT_CLASS = "com.ibm.icu.text.DateFormat";

    /**
     * The actual formatter.
     */
    private Object formatter;
    /**
     * The class of the formatter
     */
    private Class<?> formatterClass;
    private static Class<?> defaultSimpleDateFormat;
    private static Class<?> defaultDateFormat;

    static {
        try {
            defaultSimpleDateFormat = ClassUtil.forName(DEFAULT_SIMPLE_DATE_FORMAT_CLASS);
        } catch (ClassNotFoundException ex) {
            LOGGER.info("Error loading simple date format class [{}]", DEFAULT_SIMPLE_DATE_FORMAT_CLASS);
        }

        try {
            defaultDateFormat = ClassUtil.forName(DEFAULT_DATE_FORMAT_CLASS);
        } catch (ClassNotFoundException ex) {
            LOGGER.info("Error loading date format class [{}]", DEFAULT_SIMPLE_DATE_FORMAT_CLASS);
        }
    }

    /**
     * Prevent instantiation.
     */
    private DateFormatter() {
    }

    /**
     * Construct a DateFormatter with the given date format.
     *
     * @param format the date format
     * @return a DateFormatter of the given format
     * @see java.text.DateFormat#getDateInstance(int)
     */
    public static DateFormatter getDateInstance(int format) {
        DateFormatter fmt = new DateFormatter();
        boolean oops = false;
        try {
            fmt.formatterClass = defaultDateFormat;
            // To call a method taking a type of int, the type has to match but
            // the object has to be wrapped
            Class<?>[] instanceTypes = {
                int.class
            };
            Object[] instanceParams = {
                Integer.valueOf(format)
            };
            fmt.formatter = ReflectionUtil.invoke(fmt.formatterClass, fmt.formatterClass, "getDateInstance", instanceParams, instanceTypes);
        } catch (NoSuchMethodException e) {
            oops = true;
        } catch (IllegalAccessException e) {
            oops = true;
        } catch (InvocationTargetException e) {
            oops = true;
        } catch (NullPointerException e) {
            oops = true;
        }

        if (oops) {
            fmt.formatterClass = DateFormat.class;
            fmt.formatter = DateFormat.getDateInstance(format);
        }

        return fmt;
    }

    /**
     * Construct a DateFormatter with the default date format.
     *
     * @return a DateFormatter of the default format
     * @see java.text.DateFormat#getDateInstance()
     */
    public static DateFormatter getDateInstance() {
        return getDateInstance(DEFAULT);
    }

    /**
     * Construct a simple DateFormatter with the given date format.
     *
     * @param format the date format
     * @return a DateFormatter with the given date format
     * @see java.text.DateFormat#getDateInstance(int)
     */
    public static DateFormatter getSimpleDateInstance(String format) {
        DateFormatter fmt = new DateFormatter();
        boolean oops = false;
        try {
            fmt.formatterClass = defaultSimpleDateFormat;
            fmt.formatter = ReflectionUtil.construct(fmt.formatterClass, format);
        } catch (NoSuchMethodException e) {
            oops = true;
        } catch (IllegalAccessException e) {
            oops = true;
        } catch (InvocationTargetException e) {
            oops = true;
        } catch (NullPointerException e) {
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

    /**
     * Set whether this DataFormatter should be lenient in parsing dates.
     *
     * @param lenient whether to be lenient or not
     * @see java.text.DateFormat#setLenient(boolean)
     */
    public void setLenient(boolean lenient) {
        try {
            Class<?>[] lenientTypes = {
                    boolean.class
            };
            Object[] lenientParams = {
                    Boolean.valueOf(lenient)
            };
            ReflectionUtil.invoke(formatterClass, formatter, "setLenient", lenientParams, lenientTypes);
        } catch (NoSuchMethodException e) {
            assert false : e;
        } catch (IllegalAccessException e) {
            assert false : e;
        } catch (InvocationTargetException e) {
            assert false : e;
        }
    }

    /**
     * Formats a Date into a date/time string.
     *
     * @param date the time value to be formatted into a time string.
     * @return the formatted time string.
     * @see java.text.DateFormat#format(java.util.Date)
     */
    public String format(Date date) {
        try {
            return (String) ReflectionUtil.invoke(formatterClass, formatter, "format", date);
        } catch (NoSuchMethodException e) {
            assert false : e;
        } catch (IllegalAccessException e) {
            assert false : e;
        } catch (InvocationTargetException e) {
            assert false : e;
        }
        return "";
    }

    /**
     * Convert text to a date.
     *
     * @param text the input to parse as a date
     * @return the resultant date
     * @see java.text.DateFormat#parse(java.lang.String)
     */
    public Date parse(String text) {
        try {
            return (Date) ReflectionUtil.invoke(formatterClass, formatter, "parse", text);
        } catch (NoSuchMethodException e) {
            assert false : e;
        } catch (IllegalAccessException e) {
            assert false : e;
        } catch (InvocationTargetException e) {
            assert false : e;
        }
        return new Date();
    }
}
