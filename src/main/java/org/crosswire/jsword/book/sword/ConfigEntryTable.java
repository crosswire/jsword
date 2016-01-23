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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

/**
 * A utility class for loading the entries in a Sword book's conf file. Since
 * the conf files are manually maintained, there can be all sorts of errors in
 * them. This class does robust checking and reporting.
 * <p/>
 * <p/>
 * Config file format. See also: <a href=
 * "http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
 * <p/>
 * <p/>
 * The contents of the About field are in rtf.
 * <p/>
 * \ is used as a continuation line.
 *
 * @author Mark Goodwin
 * @author Joe Walker
 * @author Jacky Cheung
 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
public final class ConfigEntryTable {

    /**
     * Create an empty Sword config for the named book.
     *
     * @param bookName the name of the book
     * @param isRootConfig true to indicate a root configuration
     */
    public ConfigEntryTable(String bookName, boolean isRootConfig) {
        this(bookName, isRootConfig, null);
    }

    /**
     * Sometimes, we're creating the config off another config, and so need to ensure the initials match exactly.
     *
     * @param bookName the name of the book
     * @param isRootConfig true to indicate a root configuration
     * @param initials the set of initials used to identify this module. This could be different to the bookName (which may be lowercase)
     */
    public ConfigEntryTable(String bookName, boolean isRootConfig, String initials) {
        this.initials = initials == null ? bookName : initials;
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
        buf.append('[');
        buf.append(initials);
        buf.append("]\n");
/*
        toConf(buf, BASIC_INFO);
        toConf(buf, SYSTEM_INFO);
        toConf(buf, HIDDEN);
        toConf(buf, FEATURE_INFO);
        toConf(buf, LANG_INFO);
        toConf(buf, COPYRIGHT_INFO);
        toConf(buf, extra);
*/
        return buf.toString();
    }

/*    
    protected void report() {
        Iterator<String> kIter = config.keySet().iterator();
        while (kIter.hasNext()) {
            String key = kIter.next();
            Iterator<String> vIter = config.getValues(key).iterator();
            while (vIter.hasNext()) {
                String value = vIter.next();

                // Only CIPHER_KEYS that are empty are not ignored
                if (value.length() == 0 && !SwordBookMetaData.KEY_CIPHER_KEY.equalsIgnoreCase(key)) {
                    log.warn("Ignoring empty entry in [{}]{}=", initials, key);
                    continue;
                }

                // Create a configEntry so that the name is normalized.
                ConfigEntry configEntry = new ConfigEntry(initials, key);
                ConfigEntryType type = configEntry.getType();
                ConfigEntry e = null; // table.get(type);

                if (e == null) {
                    if (type == null) {
                        log.warn("Extra entry in [{}]{}", initials, configEntry.getName());
//                        extra.put(key, configEntry);
                    } else if (type.isSynthetic()) {
                        log.warn("Ignoring unexpected entry in [{}]{}", initials, configEntry.getName());
                    } else {
//                        table.put(type, configEntry);
                    }
                } else {
                    configEntry = e;
                }
                // History is a special case it is of the form History_x.x
                // The config entry is History without the x.x.
                // We want to put x.x at the beginning of the string
                if (ConfigEntryType.HISTORY.equals(type)) {
                    int pos = key.indexOf('_');
                    value = key.substring(pos + 1) + ' ' + value;
                }

                configEntry.addValue(value);

                // Filter known types of entries
                if (type != null) {
                    value = type.filter(value);

                    // Report on fields that shouldn't have RTF but do
                    if (!type.allowsRTF() && RTF_PATTERN.matcher(value).find()) {
                        log.info("Unexpected RTF for [{}]{} = {}", initials, key, value);
                    }

                    if (type.mayRepeat()) {
                        if (!type.isAllowed(value)) {
                            log.info("Unknown config value for [{}]{} = {}", initials, key, value);
                        }
                    } else {
                        if (value != null) {
                            log.info("Ignoring unexpected additional entry for [{}]{} = {}", initials, key, value);
                        } else {
                            if (!type.isAllowed(value)) {
                                log.info("Unknown config value for [{}]{} = {}", initials, key, value);
                            }
                        }
                    }
                }
            }
        }
    }
 */

    /**
     * Build an ordered map so that it displays in a consistent order.
     */
    /*
    private void toConf(StringBuilder buf, ConfigEntryType[] category) {
        for (int i = 0; i < category.length; i++) {

            String entry = table.get(category[i]);

            if (entry != null && !category[i].isSynthetic()) {
                String text = entry.toConf();
                if (text != null && text.length() > 0) {
                    buf.append(entry.toConf());
                }
            }
        }
    }
*/
    /**
     * Build an ordered map so that it displays in a consistent order.
     */
/*
    private void toConf(StringBuilder buf, Map<String, ConfigEntry> map) {
        for (Map.Entry<String, ConfigEntry> mapEntry : map.entrySet()) {
            ConfigEntry entry = mapEntry.getValue();
            String text = entry.toConf();
            if (text != null && text.length() > 0) {
                buf.append(text);
            }
        }
    }
*/

    /**
     * The set of initials identifying this book
     */
    private String initials;

    /**
     * A pattern of allowable RTF in a SWORD conf. These are: \pard, \pae, \par, \qc \b, \i and embedded Unicode
     */
    // private static final Pattern RTF_PATTERN = Pattern.compile("\\\\pard|\\\\pa[er]|\\\\qc|\\\\[bi]|\\\\u-?[0-9]{4,6}+");

}
