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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for loading an INI style, Multimap configuration file.
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
final class Ini {

    /**
     * Create an empty INI Config.
     */
    Ini() {
        sectionMap = new TreeMap<String, IniSection>(String.CASE_INSENSITIVE_ORDER);
        list = new ArrayList();
    }

    /**
     * Start over.
     */
    public void clear() {
        sectionMap.clear();
        list.clear();
    }

    /**
     * Get the number of sections
     * 
     * @return the number of known sections
     */
    public int size() {
        return sectionMap.size();
    }

    /**
     * Get an unmodifiable collection of the sections in this INI.
     * 
     * @return the ordered section names
     */
    public List<String> getSections() {
        return Collections.unmodifiableList(list);
    }

    public String getSectionName(int index) {
        return list.get(index);
    }

    /**
     * Get the name of the first section.
     * 
     * @return the name of the first section or null if there are no sections
     * @throws ArrayIndexOutOfBoundsException if there are no sections
     */
    public String getSectionName() {
        return size() == 0 ? null : list.get(0);
    }

    public int getValueSize(String sectionName, String key) {
        IniSection section = doGetSection(sectionName);
        return section == null ? 0 : section.size(key);
    }

    /**
     * Get the number of values for a key in the first section
     * 
     * @param key the key
     * @return the number of values for a key in the first section
     */
    public int getValueSize(String key) {
        IniSection section = getSection();
        return section == null ? 0 : section.size(key);
    }

    /**
     * Get the value for the key specified by the index and the section.
     * 
     * @param sectionName the name of the section
     * @param key the key for the section
     * @param index the index in the list of values
     * @return the value at the specified index
     * @throws ArrayIndexOutOfBoundsException when the index is out of bounds
     */
    public String getValue(String sectionName, String key, int index) {
        IniSection section = doGetSection(sectionName);
        return section == null ? null : section.get(key, index);
    }

    /**
     * Get the first value for the key specified by the index and the section.
     * 
     * @param sectionName the name of the section
     * @param key the key for the section
     * @return the value at the specified index
     * @throws ArrayIndexOutOfBoundsException when the index is out of bounds
     */
    public String getValue(String sectionName, String key) {
        IniSection section = doGetSection(sectionName);
        return section == null ? null : section.get(key, 0);
    }

    /**
     * Get the value for the key specified by the index for the first section.
     * 
     * @param key the key
     * @param index the index
     * @return the value at the specified index
     * @throws ArrayIndexOutOfBoundsException when the index is out of bounds
     */
    public String getValue(String key, int index) {
        IniSection section = getSection();
        return section == null ? null : section.get(key, index);
    }

    /**
     * Get the first value for the key in the first section.
     * 
     * @param key the key
     * @return the value at the specified index
     * @throws ArrayIndexOutOfBoundsException when the index is out of bounds
     */
    public String getValue(String key) {
        IniSection section = getSection();
        return section == null ? null : section.get(key);
    }

    /**
     * Add a key/value pair to a section.
     * If the section does not exist, it is created.
     * A null for key or value is not allowed.
     * An empty string for a key is not allowed.
     *
     * @param sectionName the name of the section
     * @param key the key for the section
     * @param value the value for the key
     * @return {@code true} if the element was added or already was present
     */
    public boolean add(String sectionName, String key, String value) {
        IniSection section = getOrCreateSection(sectionName);
        return section.add(key, value);
    }

    /**
     * Replace a value for a key.
     * A null for key or value is not allowed.
     * An empty string for a key is not allowed.
     *
     * @param sectionName the name of the section
     * @param key the key for the section
     * @param value the value for the key
     * @return {@code true} if the element was added or already was present
     */
    public boolean replace(String sectionName, String key, String value) {
        IniSection section = getOrCreateSection(sectionName);
        return section.replace(key, value);
    }

    /**
     * Remove the value if present.
     * If it were the last value for the key, the key is removed.
     * If it were the last key, the section is removed.
     * 
     * @param sectionName the name of the section
     * @param key the key for the section
     * @param value the value for the key
     * @return whether the value was present and removed
     */
    public boolean remove(String sectionName, String key, String value) {
        IniSection section = sectionMap.get(sectionName);
        if (section == null) {
            return false;
        }
        boolean changed = section.remove(key, value);
        if (changed) {
            if (section.isEmpty()) {
                sectionMap.remove(sectionName);
                list.remove(sectionName);
            }
        }

        return changed;
    }

    /**
     * Remove the key if present.
     * If it were the last key for the section, the section is removed.
     * 
     * @param sectionName the name of the section
     * @param key the key for the section
     * @return whether the key was present and removed
     */
    public boolean remove(String sectionName, String key) {
        IniSection section = sectionMap.get(sectionName);
        if (section == null) {
            return false;
        }
        boolean changed = section.remove(key);
        sectionMap.remove(sectionName);
        list.remove(sectionName);

        return changed;
    }

    // Routines that work on the first section
    /**
     * Get the first section.
     * 
     * @return the first section or null if there are no sections
     */
    public IniSection getSection() {
        return size() == 0 ? null : sectionMap.get(list.get(0));
    }

    /**
     * Get the unmodifiable set of keys of the first section.
     * The set has insertion order.
     * 
     * @return the keys of the first section
     */
    public Collection<String> getKeys() {
        IniSection section = getSection();
        return section == null ? null : section.getKeys();
    }

    /**
     * Get the values of a key of the first section.
     * The collection has insertion order.
     * Note many keys only have one value.
     * A key that has no values returns null.
     * 
     * @param key the key
     * @return the keyed values or null if the key doesn't exist
     */
    public Collection<String> getValues(String key) {
        IniSection section = getSection();
        return section == null ? null : section.getValues(key);
    }

    /**
     * Add a value for the key. Duplicate values are not allowed.
     *
     * @param key the key for the section
     * @param value the value for the key
     * @return whether the value was added or is already present.
     */
    public boolean addValue(String key, String value) {
        IniSection section = getSection();
        return section == null || section.add(key, value);
    }

    /**
     * Remove the value if present in the first section.
     * If it were the last value for the key, the key is removed.
     * If it were the last key, the section is removed.
     * 
     * @param key the key for the section
     * @param value the value for the key
     * @return whether the value was present and removed
     */
    public boolean removeValue(String key, String value) {
        String section = getSectionName();
        return section == null || remove(section, key, value);
    }

    /**
     * Remove the key if present.
     * If it were the last key for the section, the section is removed.
     * 
     * @param key the key for the section
     * @return whether the key was present and removed
     */
    public boolean removeValue(String key) {
        String section = getSectionName();
        return section == null || remove(section, key);
    }

    /**
     * Replace a value for a key.
     * A null for key or value is not allowed.
     * An empty string for a key is not allowed.
     *
     * @param key the key for the section
     * @param value the value for the key
     * @return {@code true} if the element was added or already was present
     */
    public boolean replaceValue(String key, String value) {
        IniSection section = getSection();
        return section == null || section.replace(key, value);
    }

    public void load(InputStream is, String encoding) throws IOException {
        Reader in = null;
        try {
            in = new InputStreamReader(is, encoding);
            doLoad(in);
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
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            load(in, encoding);
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
     * @param encoding the character encoding for this INI
     * @throws IOException
     */
    public void load(byte[] buffer, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(buffer);
            load(in, encoding);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
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
     * Output the Ini to the given Writer.
     * 
     * @param out the Writer to which this Ini should be written
     */
    private void save(Writer out) {
        PrintWriter writer = null;
        if (out instanceof PrintWriter) {
            writer = (PrintWriter) out;
        } else {
            writer = new PrintWriter(out);
        }

        for (String sectionName : list) {
            IniSection section = doGetSection(sectionName);
            section.save(writer);
        }
    }

    private IniSection doGetSection(String sectionName) {
        return sectionMap.get(sectionName);
    }

    /**
     * Get a section, creating it if necessary.
     *
     * @param sectionName
     * @return the found or created section
     */
    private IniSection getOrCreateSection(final String sectionName) {
        IniSection section = sectionMap.get(sectionName);
        if (section == null) {
            section = new IniSection(sectionName);
            sectionMap.put(sectionName, section);
            list.add(sectionName);
        }
        return section;
    }

    private void doLoad(Reader in) throws IOException {
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

            String sectionName = "";
            StringBuilder buf = new StringBuilder();
            while (true) {
                // Empty out the buffer
                buf.setLength(0);
                String line = advance(bin);
                if (line == null) {
                    break;
                }

                if (isSectionLine(line)) {
                    // The conf file contains a leading line of the form [KJV]
                    // This is the acronym by which Sword refers to it.
                    sectionName = line.substring(1, line.length() - 1);
                    continue;
                }

                // Is this a key line?
                int splitPos = getSplitPos(line);
                if (splitPos < 0) {
                    LOGGER.warn("Expected to see '=' in [{}]: {}", sectionName, line);
                    continue;
                }

                String key = line.substring(0, splitPos).trim();
                if (key.length() == 0) {
                    LOGGER.warn("Empty key in [{}]: {}", sectionName, line);
                }
                String value = more(bin, line.substring(splitPos + 1).trim());
                add(sectionName, key, value);
            }
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
            }
        } while (moreCowBell && line != null);
        return buf.toString();
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

    /**
     * A map of sections by section names.
     */
    private Map<String, IniSection> sectionMap;

    /**
     * Indexed list of sections maintaining insertion order.
     */
    private List<String> list;

    /**
     * Buffer size is based on file size but keep it with within reasonable limits
     */
    private static final int MAX_BUFF_SIZE = 8 * 1024;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Ini.class);
}
