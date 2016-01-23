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
 * © CrossWire Bible Society, 2015 - 2016
 */
package org.crosswire.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A utility class for a section of an INI style configuration file.
 * Keys and values are maintained in insertion order. A key may have more than one value.
 * <p>
 * SWORD defines a conf as an INI file with one or more sections.
 * Originally, all modules were described in a single conf, but
 * now each module has its own conf.
 * </p>
 * <p>
 * SWORD will be using a single conf to hold overrides for many
 * modules. This is the motivation for this class as opposed to
 * allowing only a single section as {@link IniSection}.
 * </p>
 * <p>
 * Since the most common use case is for a single section, this
 * implementation has an API for delegating to the first IniSection.
 * </p>
 * 
 * This implementation allows for:
 * <ul>
 * <li><strong>Case Insensitive</strong> -- Section names, keys and values are case insensitive.</li>
 * <li><strong>Comments</strong> -- ; and # preceded only by white space indicate that a line is a comment.
 *              Note: SWORD does not support ; but it is present in some 3rd Party repositories such as IBT.</li>
 * <li><strong>Multiple Values</strong> -- Each key can have one or more values.</li>
 * <li><strong>Order</strong> -- Order of sections, keys and values are retained</li>
 * </ul>
 * 
 * This implementation does not allow for:
 * <ul>
 * <li><strong>Globals</strong> -- (key,value) pairs before the first section.</li>
 * <li><strong>Quoted Values</strong> -- Values surrounded by "" or ''.
 *              If present they are part of the value.</li>
 * <li><strong>Retaining comments</strong> -- Comments are ignored.</li>
 * <li><strong>Comments after content</strong> -- Comments are on lines to themselves.</li>
 * <li><strong>:</strong> -- as an alternative for =.</li>
 * <li><strong>nulls</strong> -- null values.</li>
 * </ul>
 *

 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 */
public final class IniSection implements Iterable {

    /**
     * Create an empty INI config without a name.
     */
    public IniSection() {
        this((String) null);
    }
    /**
     * Create an empty INI Config.
     * @param name the section name
     */
    public IniSection(String name) {
        this.name = name;
        section = new HashMap<String, List<String>>();
        warnings = new StringBuilder();
    }

    /**
     * Copy constructor
     * 
     * @param config the config to copy
     */
    public IniSection(IniSection config) {
        this.name = config.getName();
        section = new HashMap<String, List<String>>();
        for (String key : config.getKeys()) {
            for (String value : config.getValues(key)) {
                add(key, value);
            }
        }
    }
    /**
     * Start over.
     */
    public void clear() {
        section.clear();
        warnings.setLength(0);
        warnings.trimToSize();
        report = "";
    }

    /**
     * Set the name of this INI config.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the [name] of this section
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of keys in this section.
     * 
     * @return the count
     */
    public int size() {
        return section.size();
    }

    /**
     * Get the number of values for a key.
     * 
     * @param key the key
     * @return the number of values for a key or 0 if the key does not exist.
     */
    public int size(String key) {
        Collection<String> values = section.get(key);
        return values == null ? 0 : values.size();
    }

    /**
     * Determine whether this section has any keys
     *
     * @return {@code true} if this section is empty
     */
    public boolean isEmpty() {
        return section.isEmpty();
    }

    public Iterator iterator() {
        return section.keySet().iterator();
    }
    /**
     * Get the unmodifiable unordered list of keys.
     *
     * @return the set of keys
     */
    public Collection<String> getKeys() {
        return Collections.unmodifiableSet(section.keySet());
    }

    /**
     * Returns {@code true} if the IniSection contains any values for the specified key.
     *
     * @param key key to search for in IniSection
     * @return {@code true} if the key exists
     */
    public boolean containsKey(String key) {
        return section.containsKey(key);
    }

    /**
     * Returns {@code true} if the IniSection contains the specified value for any key.
     *
     * @param value value to search for in IniSection
     * @return {@code true} if the value exists.
     */
    public boolean containsValue(String value) {
        for (Collection<String> collection : section.values()) {
            if (collection.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the IniSection contains the specified value for the given key.
     *
     * @param key the key for the section
     * @param value value to search for in IniSection
     * @return {@code true} if the value exists.
     */
    public boolean containsValue(String key, String value) {
        Collection<String> values = section.get(key);
        return values != null && values.contains(value);
    }

    /**
     * Add a value for the key. Duplicate values are not allowed.
     *
     * @param key the key for the section
     * @param value the value for the key
     * @return whether the value was added or is already present.
     */
    public boolean add(String key, String value) {
        if (!allowed(key, value)) {
            return false;
        }

        Collection<String> values = getOrCreateValues(key);
        if (values.contains(value)) {
            warnings.append("Duplicate value: ").append(key).append(" = ").append(value).append('\n');
            return true;
        }
        return values.add(value);
    }

    /**
     * Get the unmodifiable collection of values of a key.
     * The collection has insertion order.
     * Note many keys only have one value.
     * A key that has no values returns null.
     *
     * @param key the key
     * @return the keyed values or null if the key doesn't exist
     */
    public Collection<String> getValues(String key) {
        if (section.containsKey(key)) {
            return Collections.unmodifiableCollection(section.get(key));
        }
        return null;
    }

    /**
     * Get the value for the key specified by the index.
     * 
     * @param key the key
     * @param index the index
     * @return the value at the specified index
     * @throws ArrayIndexOutOfBoundsException when the index is out of bounds
     */
    public String get(String key, int index) {
        List<String> values = section.get(key);
        return values == null ? null : values.get(index);
    }

    /**
     * Get the first value for the key.
     * 
     * @param key the key
     * @return the value at the specified index or null
     */
    public String get(String key) {
        List<String> values = section.get(key);
        return values == null ? null : values.get(0);
    }

    public String get(String key, String defaultValue) {
        List<String> values = section.get(key);
        return values == null ? defaultValue : values.get(0);
    }

    /**
     * Remove the value if present.
     * If it were the last value for the key, the key is removed.
     * 
     * @param key the key for the section
     * @param value the value for the key
     * @return whether the value was present and removed
     */
    public boolean remove(String key, String value) {
        Collection<String> values = section.get(key);
        if (values == null) {
            return false;
        }

        boolean changed = values.remove(value);
        if (changed) {
            if (values.isEmpty()) {
                section.remove(key);
            }
        }

        return changed;
    }

    /**
     * Remove the key and all its values, if present.
     * 
     * @param key the key for the section
     * @return whether the key was present and removed
     */
    public boolean remove(String key) {
        Collection<String> values = section.get(key);
        if (values == null) {
            return false;
        }
        section.remove(key);
        return true;
    }

    /**
     * Replace the value(s) for the key with a new value.
     *
     * @param key the key for the section
     * @param value the value for the key
     * @return whether the replace happened
     */
    public boolean replace(String key, String value) {
        if (!allowed(key, value)) {
            return false;
        }

        Collection<String> values = getOrCreateValues(key);
        values.clear();
        return values.add(value);
    }

    /**
     * Load the INI from an InputStream using the given encoding.
     *
     * @param is the InputStream to read from
     * @param encoding the encoding of the file
     * @throws IOException
     */
    public void load(InputStream is, String encoding) throws IOException {
        load(is, encoding, null);
    }

    /**
     * Load the INI from an InputStream using the given encoding. Filter keys as specified.
     *
     * @param is the InputStream to read from
     * @param encoding the encoding of the file
     * @param filter the filter, possibly null, for the desired keys
     * @throws IOException
     */
    public void load(InputStream is, String encoding, Filter<String> filter) throws IOException {
        Reader in = null;
        try {
            in = new InputStreamReader(is, encoding);
            doLoad(in, filter);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    /**
     * Load the INI from a file using the given encoding.
     *
     * @param file the file to load
     * @param encoding the encoding of the file
     * @throws IOException
     */
    public void load(File file, String encoding) throws IOException {
        load(file, encoding, null);
    }

    /**
     * Load the INI from a file using the given encoding. Filter keys as specified.
     *
     * @param file the file to load
     * @param encoding the encoding of the file
     * @param filter the filter, possibly null, for the desired keys
     * @throws IOException
     */
    public void load(File file, String encoding, Filter<String> filter) throws IOException {
        this.configFile = file;
        this.charset = encoding;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            load(in, encoding, filter);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    /**
     * Load the conf from a buffer. This is used to load conf entries from the
     * mods.d.tar.gz file.
     *
     * @param buffer the buffer to load
     * @param encoding the character encoding of this INI
     * @throws IOException
     */
    public void load(byte[] buffer, String encoding) throws IOException {
        load(buffer, encoding, null);
    }

    /**
     * Load the conf from a buffer. Filter keys as specified.
     * This is used to load conf entries from the mods.d.tar.gz file.
     *
     * @param buffer the buffer to load
     * @param encoding the character encoding of this INI
     * @param filter the filter, possibly null, for the desired keys
     * @throws IOException
     */
    public void load(byte[] buffer, String encoding, Filter<String> filter) throws IOException {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(buffer);
            load(in, encoding, filter);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    /**
     * Save this INI to the file from which it was loaded.
     * @throws IOException
     */
    public void save() throws IOException {
        assert configFile != null;
        assert charset != null;
        if (configFile != null && charset != null) {
            save(configFile, charset);
        }
    }

    /**
     * Save the INI to a file using the given encoding.
     *
     * @param file the file to load
     * @param encoding the encoding of the file
     * @throws IOException
     */
    public void save(File file, String encoding) throws IOException {
        this.configFile = file;
        this.charset = encoding;
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), encoding);
            save(out);
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
        }
    }

    /**
     * Output this section using the print writer. The section ends with a blank line.
     * The items are output in insertion order.
     * 
     * @param out the output stream
     */
    public void save(Writer out) {
        PrintWriter writer = null;
        if (out instanceof PrintWriter) {
            writer = (PrintWriter) out;
        } else {
            writer = new PrintWriter(out);
        }

        writer.print("[");
        writer.print(name);
        writer.print("]");
        writer.println();

        boolean first = true;
        Iterator<String> keys = section.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Collection<String> values = section.get(key);
            Iterator<String> iter = values.iterator();
            String value;
            while (iter.hasNext()) {
                if (!first) {
                    writer.println();
                    first = false;
                }
                value = iter.next();
                writer.print(key);
                writer.print(" = ");
                writer.print(format(value));
                writer.println();
            }
        }

        writer.flush();
    }

    /**
     * Obtain a report of issues with this IniSection. It only reports once per load.
     * 
     * @return the report with one issue per line or an empty string if there are no issues
     */
    public String report() {
        String str = report;
        report = "";
        return str;
    }

    /**
     * A helper to format the output of the content as expected
     * @param value the value to be formatted
     * @return the transformed value
     */
    private String format(final String value) {
        // Find continuations and replace newlines with a ' \'
        // Indenting the next line
        // Note: if the quoting of values is allowed this may need to be revisited.
        return value.replaceAll("\n", " \\\\\n\t");
    }

    private Collection<String> getOrCreateValues(final String key) {
        List<String> values = section.get(key);
        if (values == null) {
            values = new ArrayList<String>();
            section.put(key, values);
        }
        return values;
    }

    private void doLoad(Reader in, Filter<String> filter) throws IOException {
        BufferedReader bin = null;
        try {
            if (in instanceof BufferedReader) {
                bin = (BufferedReader) in;
            } else {
                // Quiet Android from complaining about using the default
                // BufferReader buffer size.
                // The actual buffer size is undocumented. So this is a good
                // idea any way.
                bin = new BufferedReader(in, MAX_BUFF_SIZE);
            }

            while (true) {
                String line = advance(bin);
                if (line == null) {
                    break;
                }

                if (isSectionLine(line)) {
                    // The conf file contains a leading line of the form [KJV]
                    // This is the acronym by which Sword refers to it.
                    name = line.substring(1, line.length() - 1);
                    continue;
                }

                // Is this a key line?
                int splitPos = getSplitPos(line);
                if (splitPos < 0) {
                    warnings.append("Skipping: Expected to see '=' in: ").append(line).append('\n');
                    continue;
                }

                String key = line.substring(0, splitPos).trim();
                String value = more(bin, line.substring(splitPos + 1).trim());
                if (filter == null || filter.test(key)) {
                    add(key, value);
                }
            }
            report = warnings.toString();
            warnings.setLength(0);
            warnings.trimToSize();
        } finally {
            if (bin != null) {
                bin.close();
                bin = null;
            }
        }
    }

    /**
     * Get the next line from the input
     *
     * @param bin The reader to get data from
     * @return the next line or null if there is nothing more
     * @throws IOException if encountered
     */
    private String advance(BufferedReader bin) throws IOException {
        // Get the next non-blank, non-comment line
        String trimmed = null;
        for (String line = bin.readLine(); line != null; line = bin.readLine()) {
            // Remove leading and trailing whitespace
            trimmed = line.trim();

            // skip blank and comment lines
            if (!isCommentLine(trimmed)) {
                return trimmed;
            }
        }
        return null;
    }

    /**
     * Determine if the given line is a blank or a comment line.
     *
     * @param line The line to check.
     * @return true if the line is empty or starts with one of the comment
     *         characters
     */
    private boolean isCommentLine(final String line) {
        if (line == null) {
            return false;
        }
        if (line.length() == 0) {
            return true;
        }
        char firstChar = line.charAt(0);
        return firstChar == ';' || firstChar == '#';
    }

    /**
     * Is this line a [section]?
     *
     * @param line The line to check.
     * @return true if the line designates a section
     */
    private boolean isSectionLine(final String line) {
        return line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']';
    }

    /**
     * Does this line of text represent a key/value pair?
     * 
     * @param line The line to check.
     * @return the position of the split position or -1
     */
    private int getSplitPos(final String line) {
        return line.indexOf('=');
    }

    /**
     * Get continuation lines, if any.
     */
    private String more(BufferedReader bin, String value) throws IOException {
        boolean moreCowBell = false;
        String line = value;
        StringBuilder buf = new StringBuilder();

        do {
            moreCowBell = more(line);
            if (moreCowBell) {
                line = line.substring(0, line.length() - 1).trim();
            }
            buf.append(line);
            if (moreCowBell) {
                buf.append('\n');
                line = advance(bin);
                // Is this new line a potential key line?
                // It cannot both continue the prior
                // and also be a key line.
                int splitPos = getSplitPos(line);
                if (splitPos >= 0) {
                    warnings.append("Possible trailing continuation on previous line. Found: ").append(line).append('\n');
                }
            }
        } while (moreCowBell && line != null);
        String cowBell = buf.toString();
        buf = null;
        line = null;
        return cowBell;
    }

    /**
     * Is there more following this line
     *
     * @param line the trimmed string to check
     * @return whether this line continues
     */
    private static boolean more(final String line) {
        int length = line.length();
        return length > 0 && line.charAt(length - 1) == '\\';
    }

    private boolean allowed(String key, String value) {
        if (key == null || key.length() == 0 || value == null) {
            if (key == null) {
                warnings.append("Null keys not allowed: ").append(" = ").append(value).append('\n');
            } else if (key.length() == 0) {
                warnings.append("Empty keys not allowed: ").append(" = ").append(value).append('\n');
            }
            if (value == null) {
                warnings.append("Null values are not allowed: ").append(key).append(" = ").append('\n');
            }
            return false;
        }
        return true;
    }

    /**
     * The name of the section.
     */
    private String name;

    /**
     * A map of values by key names.
     */
    private Map<String, List<String>> section;

    private File configFile;

    private String charset;

    private StringBuilder warnings;

    private String report;

    /**
     * Buffer size is based on file size but keep it with within reasonable limits
     */
    private static final int MAX_BUFF_SIZE = 2 * 1024;
}
