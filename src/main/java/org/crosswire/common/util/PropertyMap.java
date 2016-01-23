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
package org.crosswire.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Properties;


/**
 * A PropertyMap is a Map&lt;String,String&gt; sitting over top a Property file.
 * As such it must be defined in the same way as a java.util.Properties expects.
 *
 * @see java.util.Properties
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class PropertyMap extends LinkedHashMap<String, String> {
    /**
     * Creates an empty property list with no default values.
     */
    public PropertyMap() {
        this(null);
    }

    /**
     * Creates an empty property map with the specified defaults.
     *
     * @param   defaults   the defaults.
     */
    public PropertyMap(PropertyMap defaults) {
        this.defaults = defaults;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found.
     *
     * @param   key   the lookup key.
     * @return  the value in this property list with the specified key value.
     * @see     java.util.Properties#setProperty
     */
    public String get(String key) {
        String value = super.get(key);
        return ((value == null) && (defaults != null)) ? defaults.get(key) : value;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns the
     * default value argument if the property is not found.
     *
     * @param   key            the lookup key.
     * @param   defaultValue   a default value.
     *
     * @return  the value in this property list with the specified key value.
     * @see     java.util.Properties#setProperty
     */
    public String get(String key, String defaultValue) {
        String value = get(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Reads a property list (key and element pairs) from the input
     * byte stream. The input stream is in a simple line-oriented
     * format as specified in
     * {@link java.util.Properties#load(java.io.InputStream) load(InputStream)} and is assumed to use
     * the ISO 8859-1 character encoding; that is each byte is one Latin1
     * character. Characters not in Latin1, and certain special characters,
     * are represented in keys and elements using
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3">Unicode escapes</a>.
     * <p>
     * The specified stream remains open after this method returns.
     *
     * @param      inStream   the input stream.
     * @exception  IOException  if an error occurred when reading from the
     *             input stream.
     * @throws     IllegalArgumentException if the input stream contains a
     *         malformed Unicode escape sequence.
     * @since 1.2
     */
    public void load(InputStream inStream) throws IOException {
        Properties prop = new Properties();
        prop.load(inStream);
        for (Enumeration<Object> e = prop.keys(); e.hasMoreElements(); ) {
            Object k = e.nextElement();
            Object v = prop.get(k);
            if (k instanceof String && v instanceof String) {
                put((String) k, (String) v);
            }
        }
    }

    /**
     * Writes this property list (key and element pairs) in this
     * <code>PropertyMap</code> table to the output stream in a format suitable
     * for loading into a <code>PropertyMap</code> table using the
     * {@link #load(InputStream) load(InputStream)} method.
     * <p>
     * Properties from the defaults table of this <code>PropertyMap</code>
     * table (if any) are <i>not</i> written out by this method.
     * <p>
     * This method outputs the comments, properties keys and values in 
     * the same format as specified in
     * {@link java.util.Properties#store(java.io.OutputStream, java.lang.String) store(Writer)},
     * <p>
     * After the entries have been written, the output stream is flushed.  
     * The output stream remains open after this method returns.
     * <p>
     * @param   out      an output stream.
     * @param   comments   a description of the property list.
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  NullPointerException  if <code>out</code> is null.
     * @since 1.2
     */
    public void store(OutputStream out, String comments) throws IOException {
        Properties temp = new Properties();
        temp.putAll(this);
        temp.store(out, comments);
    }

    /**
     * Default values for any keys not found in this property map.
     */
    private PropertyMap defaults;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 2821277155924802795L;
}
