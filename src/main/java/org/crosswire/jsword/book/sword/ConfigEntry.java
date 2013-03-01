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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.crosswire.common.util.Histogram;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.OSISUtil;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ConfigEntry holds the value(s) for an entry of ConfigEntryType.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @see gnu.lgpl.License
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public final class ConfigEntry {

    /**
     * Create a ConfigEntry whose type is not certain and whose value is not
     * known.
     * 
     * @param bookName
     *            the internal name of the book
     * @param aName
     *            the name of the ConfigEntry.
     */
    public ConfigEntry(String bookName, String aName) {
        internal = bookName;
        name = aName;
        type = ConfigEntryType.fromString(aName);
    }

    /**
     * Create a ConfigEntry directly with an initial value.
     * 
     * @param bookName
     *            the internal name of the book
     * @param aType
     *            the kind of ConfigEntry
     * @param aValue
     *            the initial value for the ConfigEntry
     */
    public ConfigEntry(String bookName, ConfigEntryType aType, String aValue) {
        internal = bookName;
        name = aType.getName();
        type = aType;
        addValue(aValue);
    }

    /**
     * Get the key of this ConfigEntry
     */
    public String getName() {
        if (type != null) {
            return type.getName();
        }
        return name;
    }

    /**
     * Get the type of this ConfigEntry
     */
    public ConfigEntryType getType() {
        return type;
    }

    /**
     * Determines whether the string is allowed. For some config entries, the
     * value is expected to be one of a group, for others the format is defined.
     * 
     * @param aValue
     * @return true if the string is allowed
     */
    public boolean isAllowed(String aValue) {
        if (type != null) {
            return type.isAllowed(aValue);
        }
        return true;
    }

    /**
     * RTF is allowed in a few config entries.
     * 
     * @return true if RTF is allowed
     */
    public boolean allowsRTF() {
        if (type != null) {
            return type.allowsRTF();
        }
        return true;
    }

    /**
     * While most fields are single line or single value, some allow
     * continuation. A continuation mark is a backslash at the end of a line. It
     * is not to be followed by whitespace.
     * 
     * @return true if continuation is allowed
     */
    public boolean allowsContinuation() {
        if (type != null) {
            return type.allowsContinuation();
        }
        return true;
    }

    /**
     * Some keys can repeat. When this happens each is a single value pick from
     * a list of choices.
     * 
     * @return true if this ConfigEntryType can occur more than once
     */
    public boolean mayRepeat() {
        if (type != null) {
            return type.mayRepeat();
        }
        return true;
    }

    /**
     *
     */
    public boolean reportDetails() {
        if (type != null) {
            return type.reportDetails();
        }
        return true;
    }

    /**
     * Determine whether this config entry is supported.
     * 
     * @return true if this ConfigEntry has a type.
     */
    public boolean isSupported() {
        return type != null;
    }

    /**
     * Get the value(s) of this ConfigEntry. If mayRepeat() == true then it
     * returns a List. Otherwise it returns a string.
     * 
     * @return a list, value or null.
     */
    public Object getValue() {
        if (value != null) {
            return value;
        }
        if (values != null) {
            return values;
        }
        return type.getDefault();
    }

    /**
     * Determine whether this Config entry matches the value.
     * 
     * @param search
     *            the value to match against
     * @return true if this ConfigEntry matches the value
     */
    public boolean match(Object search) {
        if (value != null) {
            return value.equals(search);
        }
        if (values != null) {
            return values.contains(search);
        }
        Object def = type.getDefault();
        return def != null && def.equals(search);
    }

    /**
     * Add a value to the list of values for this ConfigEntry
     */
    public void addValue(String val) {
        String aValue = val;
        String confEntryName = getName();
        // Filter known types of entries
        if (type != null) {
            aValue = type.filter(aValue);
        }

        // Report on fields that shouldn't have RTF but do
        if (!allowsRTF() && RTF_PATTERN.matcher(aValue).find()) {
            log.info("Ignoring unexpected RTF for {} in {}: {}", confEntryName, internal, aValue);
        }

        if (mayRepeat()) {
            if (values == null) {
                histogram.increment(confEntryName);
                values = new ArrayList<String>();
            }
            if (reportDetails()) {
                histogram.increment(confEntryName + '.' + aValue);
            }
            if (!isAllowed(aValue)) {
                log.info("Ignoring unknown config value for {} in {}: {}", confEntryName, internal, aValue);
                return;
            }
            values.add(aValue);
        } else {
            if (value != null) {
                log.info("Ignoring unexpected additional entry for {} in {}: {}", confEntryName, internal, aValue);
            } else {
                histogram.increment(confEntryName);
                if (type.hasChoices()) {
                    histogram.increment(confEntryName + '.' + aValue);
                }
                if (!isAllowed(aValue)) {
                    log.info("Ignoring unknown config value for {} in {}: {}", confEntryName, internal, aValue);
                    return;
                }
                value = type.convert(aValue);
            }
        }
    }

    public Element toOSIS() {
        OSISUtil.OSISFactory factory = OSISUtil.factory();

        Element rowEle = factory.createRow();

        Element nameEle = factory.createCell();
        Element hiEle = factory.createHI();
        hiEle.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_BOLD);
        nameEle.addContent(hiEle);
        Element valueElement = factory.createCell();
        rowEle.addContent(nameEle);
        rowEle.addContent(valueElement);

        // I18N(DMS): use name to lookup translation.
        hiEle.addContent(getName());

        if (value != null) {
            String text = value.toString();
            text = XMLUtil.escape(text);
            if (allowsRTF()) {
                valueElement.addContent(OSISUtil.rtfToOsis(text));
            } else if (allowsContinuation()) {
                valueElement.addContent(processLines(factory, text));
            } else {
                valueElement.addContent(text);
            }
        }

        if (values != null) {
            Element listEle = factory.createLG();
            valueElement.addContent(listEle);

            for (String str : values) {
                String text = XMLUtil.escape(str);
                Element itemEle = factory.createL();
                listEle.addContent(itemEle);
                if (allowsRTF()) {
                    itemEle.addContent(OSISUtil.rtfToOsis(text));
                } else {
                    itemEle.addContent(text);
                }
            }
        }
        return rowEle;
    }

    public static void resetStatistics() {
        histogram.clear();
    }

    public static void dumpStatistics() {
        // Uncomment the following line to produce statistics
        // System.out.println(histogram.toString());
    }

    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        ConfigEntry that = (ConfigEntry) obj;
        return that.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Build's a SWORD conf file as a string. The result is not identical to the
     * original, cleaning up problems in the original and re-arranging the
     * entries into a predictable order.
     * 
     * @return the well-formed conf.
     */
    public String toConf() {
        StringBuilder buf = new StringBuilder();

        if (value != null) {
            buf.append(getName());
            buf.append('=');
            String text = getConfValue(value);
            if (allowsContinuation()) {
                // With continuation each line is ended with a '\', except the
                // last.
                text = text.replaceAll("\n", "\\\\\n");
            }
            buf.append(text);
            buf.append('\n');
        } else if (type.equals(ConfigEntryType.CIPHER_KEY)) {
            // CipherKey is empty to indicate that it is encrypted and locked.
            buf.append(getName());
            buf.append('=');
        }

        if (values != null) {
            // History values begin with the history value, e.g. 1.2
            // followed by a space.
            // These are to joined to the key.
            if (type.equals(ConfigEntryType.HISTORY)) {
                for (String text : values) {
                    buf.append(getName());
                    buf.append('_');
                    buf.append(text.replaceFirst(" ", "="));
                    buf.append('\n');
                }
            } else {
                for (String text : values) {
                    buf.append(getName());
                    buf.append('=');
                    buf.append(getConfValue(text));
                    buf.append('\n');
                }
            }
        }
        return buf.toString();
    }

    /**
     * The conf value is the internal representation of the string.
     * 
     * @param aValue
     *            either value or values[i]
     * @return the conf value.
     */
    private String getConfValue(Object aValue) {
        if (aValue != null) {
            if (type != null) {
                return type.unconvert(aValue);
            }
            return aValue.toString();
        }
        return null;
    }

    private List<Element> processLines(OSISUtil.OSISFactory factory, String aValue) {
        List<Element> list = new ArrayList<Element>();
        String[] lines = StringUtil.splitAll(aValue, '\n');
        for (int i = 0; i < lines.length; i++) {
            Element lineElement = factory.createL();
            lineElement.addContent(lines[i]);
            list.add(lineElement);
        }
        return list;
    }

    /**
     * A pattern of allowable RTF in a SWORD conf. These are: \pard, \pae, \par,
     * \qc \b, \i and embedded Unicode
     */
    private static final Pattern RTF_PATTERN = Pattern.compile("\\\\pard|\\\\pa[er]|\\\\qc|\\\\[bi]|\\\\u-?[0-9]{4,6}+");

    /**
     * A histogram for debugging.
     */
    private static Histogram histogram = new Histogram();

    private ConfigEntryType type;
    private String internal;
    private String name;
    private List<String> values;
    private Object value;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ConfigEntry.class);
}
