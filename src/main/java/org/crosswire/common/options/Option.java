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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.common.options;

/**
 * An Option is representation of a single named parameter. An Option has a
 * short, or a long name, or both.
 * <p>
 * It's inspiration was for command-line argument processing, but it can be used
 * for any other purpose.
 * </p>
 * <ul>
 * <li>An Option has a description, suitable for a usage statement.</li>
 * <li>An Option's argument can be optional, required or unexpected. Default is
 * NO_ARGUMENT.</li>
 * <li>An Option can have an argument of a type. Default is DataType.BOOLEAN.</li>
 * <li>An Option can have short name consisting of a single character.</li>
 * <li>An Option can have a long name given by any string. What is allowed in
 * the long name is dependent upon usage, but typically does not allow spaces.</li>
 * <li>An Option can have a default value. Default is no default value.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class Option {
    /**
     * Create a BOOLEAN Option with a short name, having no default value.
     * 
     * @param description
     *            the description
     * @param shortName
     *            the short name
     */
    public Option(String description, char shortName) {
        this(description, ArgumentType.NO_ARGUMENT, DataType.BOOLEAN, shortName, null, null);
    }

    /**
     * Create a BOOLEAN Option with a long name, having no default value.
     * 
     * @param description
     *            the description
     * @param longName
     *            the long name
     */
    public Option(String description, String longName) {
        this(description, ArgumentType.NO_ARGUMENT, DataType.BOOLEAN, '\u0000', longName, null);
    }

    /**
     * Create a BOOLEAN Option with both short and long names, having no default
     * value.
     * 
     * @param description
     *            the description
     * @param shortName
     *            the short name
     * @param longName
     *            the long name
     */
    public Option(String description, char shortName, String longName) {
        this(description, ArgumentType.NO_ARGUMENT, DataType.BOOLEAN, shortName, longName, null);
    }

    /**
     * Create an Option with both short and long names of a given DataType
     * having a default value.
     * 
     * @param description
     *            the description
     * @param shortName
     *            the short name
     * @param longName
     *            the long name
     * @param defaultValue
     *            the default value for this Option
     */
    public Option(String description, char shortName, String longName, String defaultValue) {
        this(description, ArgumentType.NO_ARGUMENT, DataType.BOOLEAN, shortName, longName, defaultValue);
    }

    /**
     * Create an Option with a short name, having no default value.
     * 
     * @param description
     *            the description
     * @param argumentType
     *            the type of the argument
     * @param dataType
     *            the type of argument's data
     * @param shortName
     *            the short name
     */
    public Option(String description, ArgumentType argumentType, DataType dataType, char shortName) {
        this(description, argumentType, dataType, shortName, null, null);
    }

    /**
     * Create an Option with a long name, having no default value.
     * 
     * @param description
     *            the description
     * @param argumentType
     *            the type of the argument
     * @param dataType
     *            the type of argument's data
     * @param longName
     *            the long name
     */
    public Option(String description, ArgumentType argumentType, DataType dataType, String longName) {
        this(description, argumentType, dataType, '\u0000', longName, null);
    }

    /**
     * Create an Option with both short and long names, having no default value.
     * 
     * @param description
     *            the description
     * @param argumentType
     *            the type of the argument
     * @param dataType
     *            the type of argument's data
     * @param shortName
     *            the short name
     * @param longName
     *            the long name
     */
    public Option(String description, ArgumentType argumentType, DataType dataType, char shortName, String longName) {
        this(description, argumentType, dataType, shortName, longName, null);
    }

    /**
     * Create an Option with both short and long names of a given DataType
     * having a default value.
     * 
     * @param description
     *            the description
     * @param argumentType
     *            the type of the argument
     * @param dataType
     *            the type of argument's data
     * @param shortName
     *            the short name
     * @param longName
     *            the long name
     * @param defaultValue
     *            the default value for this Option
     */
    public Option(String description, ArgumentType argumentType, DataType dataType, char shortName, String longName, String defaultValue) {
        this.description = description;
        this.argumentType = argumentType;
        this.dataType = dataType;
        this.shortName = shortName;
        this.longName = longName;
        this.defaultValue = defaultValue;
    }

    /**
     * The description provides a brief explanation of the option.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * The short name of an Option is the single character by which this Option
     * is known. If it is not set then there is no short name for this Option.
     * 
     * @return the shortName
     */
    public char getShortName() {
        return shortName;
    }

    /**
     * The long name of an Option is the single character by which this Option
     * is known. If it is not set then there is no long name for this Option.
     * 
     * @return the longName
     */
    public String getLongName() {
        return longName;
    }

    /**
     * The ArgumentType indicates this Option's ability to use a following
     * argument.
     * 
     * @return the argumentType
     */
    public ArgumentType getArgumentType() {
        return argumentType;
    }

    /**
     * @return the dataType
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    private String description;
    private char shortName;
    private String longName;
    private DataType dataType;
    private ArgumentType argumentType;
    private String defaultValue;
}
