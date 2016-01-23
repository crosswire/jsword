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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.crosswire.common.util.Filter;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.IniSection;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.KeyType;
import org.crosswire.jsword.book.MetaDataLocator;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.filter.SourceFilterFactory;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for loading and representing Sword book configs.
 *
 * <p>
 * Config file format. See also: <a href=
 * "http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout">
 * http://sword.sourceforge.net/cgi-bin/twiki/view/Swordapi/ConfFileLayout</a>
 * <p>
 * In addition, the SwordBookMetaData is hierarchical. The Level
 * indicates where the file originates from. The full hierarchy could be laid
 * out as followed:
 * 
 * <pre>
 *     - sword
 *         - jsword
 *            - front-end
 * </pre>
 * 
 * Various rules govern where attributes are read from. The general rule is that
 * the highest level (front-end write) will override values from the lowest
 * common denominator (sword). Various parts of the tree may be missing as the
 * files may not exist on disk. There are exceptions however and each method in
 * this file documents its behavior.
 *
 * <p>
 * The contents of the About field are in RTF.
 * <p>
 * \ is used as a continuation line.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Mark Goodwin
 * @author Joe Walker
 * @author Jacky Cheung
 * @author DM Smith
 */
public final class SwordBookMetaData extends AbstractBookMetaData {
    // These plus a few from BookMetaData are the only ones defined by SWORD
    // These ones from BookMetaData are:
    // KEY_VERSIFICATION
    // KEY_FONT
    // KEY_LANG
    // KEY_CATEGORY
    // Two others are for JSword and are defined in BookMetaData:
    // KEY_SCOPE
    // KEY_BOOKLIST
    public static final String KEY_ABBREVIATION = "Abbreviation";
    public static final String KEY_ABOUT = "About";
    public static final String KEY_BLOCK_COUNT = "BlockCount";
    public static final String KEY_BLOCK_TYPE = "BlockType";
    public static final String KEY_CASE_SENSITIVE_KEYS = "CaseSensitiveKeys";
    public static final String KEY_CIPHER_KEY = "CipherKey";
    public static final String KEY_COMPRESS_TYPE = "CompressType";
    public static final String KEY_COPYRIGHT = "Copyright";
    public static final String KEY_COPYRIGHT_CONTACT_ADDRESS = "CopyrightContactAddress";
    public static final String KEY_COPYRIGHT_CONTACT_EMAIL = "CopyrightContactEmail";
    public static final String KEY_COPYRIGHT_CONTACT_NAME = "CopyrightContactName";
    public static final String KEY_COPYRIGHT_CONTACT_NOTES = "CopyrightContactNotes";
    public static final String KEY_COPYRIGHT_DATE = "CopyrightDate";
    public static final String KEY_COPYRIGHT_HOLDER = "CopyrightHolder";
    public static final String KEY_COPYRIGHT_NOTES = "CopyrightNotes";
    public static final String KEY_DATA_PATH = "DataPath";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_DIRECTION = "Direction";
    public static final String KEY_DISPLAY_LEVEL = "DisplayLevel";
    public static final String KEY_DISTRIBUTION_LICENSE = "DistributionLicense";
    public static final String KEY_DISTRIBUTION_NOTES = "DistributionNotes";
    public static final String KEY_DISTRIBUTION_SOURCE = "DistributionSource";
    public static final String KEY_ENCODING = "Encoding";
    public static final String KEY_FEATURE = "Feature";
    public static final String KEY_GLOBAL_OPTION_FILTER = "GlobalOptionFilter";
    public static final String KEY_SIGLUM1 = "Siglum1";
    public static final String KEY_SIGLUM2 = "Siglum2";
    public static final String KEY_SIGLUM3 = "Siglum3";
    public static final String KEY_SIGLUM4 = "Siglum4";
    public static final String KEY_SIGLUM5 = "Siglum5";
    public static final String KEY_GLOSSARY_FROM = "GlossaryFrom";
    public static final String KEY_GLOSSARY_TO = "GlossaryTo";
    public static final String KEY_HISTORY = "History";
    public static final String KEY_INSTALL_SIZE = "InstallSize";
    public static final String KEY_KEY_TYPE = "KeyType";
    public static final String KEY_LCSH = "LCSH";
    public static final String KEY_LOCAL_STRIP_FILTER = "LocalStripFilter";
    public static final String KEY_MINIMUM_VERSION = "MinimumVersion";
    public static final String KEY_MOD_DRV = "ModDrv";
    public static final String KEY_OBSOLETES = "Obsoletes";
    public static final String KEY_OSIS_Q_TO_TICK = "OSISqToTick";
    public static final String KEY_OSIS_VERSION = "OSISVersion";
    public static final String KEY_PREFERRED_CSS_XHTML = "PreferredCSSXHTML";
    public static final String KEY_SEARCH_OPTION = "SearchOption";
    public static final String KEY_SHORT_COPYRIGHT = "ShortCopyright";
    public static final String KEY_SHORT_PROMO = "ShortPromo";
    public static final String KEY_SOURCE_TYPE = "SourceType";
    public static final String KEY_STRONGS_PADDING = "StrongsPadding";
    public static final String KEY_SWORD_VERSION_DATE = "SwordVersionDate";
    public static final String KEY_TEXT_SOURCE = "TextSource";
    public static final String KEY_UNLOCK_URL = "UnlockURL";
    public static final String KEY_VERSION = "Version";

    // Some keys have defaults
    public static final Map<String, String> DEFAULTS;
    static {
        Map<String, String> tempMap = new HashMap<String, String>();
        tempMap.put(KEY_COMPRESS_TYPE, "LZSS");
        tempMap.put(KEY_BLOCK_TYPE, "CHAPTER");
        tempMap.put(KEY_BLOCK_COUNT, "200");
        tempMap.put(KEY_KEY_TYPE, "TreeKey");
        tempMap.put(KEY_VERSIFICATION, "KJV");
        tempMap.put(KEY_DIRECTION, "LtoR");
        tempMap.put(KEY_SOURCE_TYPE, "Plaintext");
        tempMap.put(KEY_ENCODING, "Latin-1");
        tempMap.put(KEY_DISPLAY_LEVEL, "1");
        tempMap.put(KEY_OSIS_Q_TO_TICK, "true");
        tempMap.put(KEY_VERSION, "1.0");
        tempMap.put(KEY_MINIMUM_VERSION, "1.5.1a");
        tempMap.put(KEY_CATEGORY, "Other");
        tempMap.put(KEY_LANG, "en");
        tempMap.put(KEY_DISTRIBUTION_LICENSE, "Public Domain");
        tempMap.put(KEY_CASE_SENSITIVE_KEYS, "false");
        tempMap.put(KEY_STRONGS_PADDING, "true");
        DEFAULTS = Collections.unmodifiableMap(tempMap);
    }

    /**
     * Loads a sword config from a given File.
     *
     * @param file
     *            the config file
     * @param bookRootPath
     *            the root path for the book
     * @throws IOException
     * @throws BookException
     *             indicates missing data files
     */
    public SwordBookMetaData(File file, URI bookRootPath) throws IOException, BookException {
        this.installed = true;
        this.configFile = file;
        this.bookConf = file.getName(); // something like kjv.conf
        setLibrary(bookRootPath);

        this.configAll = new IniSection();
        this.filtered = true; // Force it to run.
        reload(keyKeepers);
    }

    /**
     * Loads a sword config from a buffer gotten from mods.d.tar.gz or mods.d.zip.
     *
     * @param buffer
     * @param bookConf
     * @throws IOException
     * @throws BookException
     */
    public SwordBookMetaData(byte[] buffer, String bookConf) throws IOException, BookException {
        this.installed = false;
        this.bookConf = bookConf; // something like .../mods.d.zip!mods.d/kjv.conf
        this.supported = true;
        this.configAll = new IniSection();
        this.filtered = true; // Force it to run.
        loadBuffer(buffer, keyKeepers);
        adjustConfig();
        report(configAll);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#reload()
     */
    @Override
    public void reload() throws BookException {
        reload(null);
    }

    public void reload(Filter<String> keepers) throws BookException {
        // Always run if it is filtered
        // Always run if a filter is supplied
        // Do not run if it is already not filtered and no filter is supplied
        if (!filtered && keepers == null) {
            return;
        }
        this.supported = true;
        if (configJSword != null) {
            configJSword.clear();
        }
        if (configFrontend != null) {
            configFrontend.clear();
        }
        try {
            if (installed) {
                loadFile(keepers);
            } else {
                byte[] buffer = IOUtil.getZipEntry(bookConf);
                loadBuffer(buffer, keepers);
            }
            adjustConfig();
            report(configAll);

            this.configJSword = addConfig(MetaDataLocator.JSWORD);
            this.configFrontend = addConfig(MetaDataLocator.FRONTEND);
        } catch (IOException ex) {
            throw new BookException("unable to load conf", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    @Override
    public boolean isQuestionable() {
        // some parameters don't support overrides
        return questionable;
    }

    /* (non-Javadoc)
    * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isSupported()
    */
    @Override
    public boolean isSupported() {
        // The top most states whether the Book is supported
        return supported;
    }

    /* (non-Javadoc)
    * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isEnciphered()
    */
    @Override
    public boolean isEnciphered() {
        String cipher = getProperty(KEY_CIPHER_KEY);
        return cipher != null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#isLocked()
     */
    @Override
    public boolean isLocked() {
        // A locked book is enciphered but without a key.
        String cipher = getProperty(KEY_CIPHER_KEY);
        return cipher != null && cipher.length() == 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#unlock(java.lang.String)
     */
    @Override
    public boolean unlock(String unlockKey) {
        // Persist the unlock key so that all can see it
        putProperty(KEY_CIPHER_KEY, unlockKey, false);
        return true;
    }

    /*
     * Can be overridden by front-end/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#getUnlockKey()
     */
    @Override
    public String getUnlockKey() {
        return getProperty(KEY_CIPHER_KEY);
    }

    /*
     * Can be overridden by front-end/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName() {
        return getProperty(KEY_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCharset()
     */
    public String getBookCharset() {
        return ENCODING_JAVA.get(getProperty(KEY_ENCODING));
    }

    /* This value cannot be overridden by front-ends/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#getKeyType()
     */
    @Override
    public KeyType getKeyType() {
        BookType bt = getBookType();
        if (bt == null) {
            return null;
        }
        return bt.getKeyType();
    }

    /**
     * @return the book type
     */
    public BookType getBookType() {
        return bookType;
    }

    /**
     * @return the SourceFilter based upon the SourceType.
     */
    public SourceFilter getFilter() {
        String sourcetype = getProperty(KEY_SOURCE_TYPE);
        return SourceFilterFactory.getFilter(sourcetype);
    }

    /**
     * To maintain backwards compatibility, this always returns the Sword conf
     * file Get the conf file for this SwordMetaData.
     *
     * @return Returns the conf file or null if loaded from a byte buffer.
     */
    public File getConfigFile() {
        return configFile;
    }

    /* Cannot be overridden by a front-end/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getBookCategory()
     */
    public BookCategory getBookCategory() {
        if (bookCat == null) {
            bookCat = (BookCategory) getValue(KEY_CATEGORY);
            if (bookCat == BookCategory.OTHER) {
                BookType bt = getBookType();
                if (bt == null) {
                    return bookCat;
                }
                bookCat = bt.getBookCategory();
            }
        }
        return bookCat;
    }

    /* Cannot be overridden by a front-end/jsword
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#toOSIS()
     */
    @Override
    public Document toOSIS() {
        List<String> knownKeys = new ArrayList(configAll.getKeys());
        OSISUtil.OSISFactory factory = OSISUtil.factory();
        Element table = factory.createTable();
        Element row = toRow(factory, "Initials", getInitials());
        table.addContent(row);
        // Each key gets one row.
        for (String key : OSIS_INFO) {
            knownKeys.remove(key);
            row = toRow(factory, key);
            if (row != null) {
                table.addContent(row);
            }
        }
        // Output the rest in the order that they are in the conf
        // however, don't show those that should be hidden.
        List<String> hide = Arrays.asList(HIDDEN);
        for (String key : knownKeys) {
            if (hide.contains(key)) {
                continue;
            }
            row = toRow(factory, key);
            if (row != null) {
                table.addContent(row);
            }
        }
        return new Document(table);
    }

    public static void normalize(Writer out, final IniSection config, final String[] order) {
        PrintWriter writer = null;
        if (out instanceof PrintWriter) {
            writer = (PrintWriter) out;
        } else {
            writer = new PrintWriter(out);
        }
        IniSection copy = new IniSection(config);
        // History is a special case
        adjustHistory(copy);

        List<String> knownKeys = new ArrayList(copy.getKeys());
        writer.print("[");
        writer.print(copy.getName());
        writer.print("]");
        writer.println();

        // Get the keys out in the specified order
        for (String key : order) {
            knownKeys.remove(key);
            if (!copy.containsKey(key)) {
                continue;
            }
            Collection<String> values = copy.getValues(key);
            Iterator<String> iter = values.iterator();
            String value;
            while (iter.hasNext()) {
                value = iter.next();
                String newKey = key;
                // When key is History, it needs to be reversed
                if (KEY_HISTORY.equalsIgnoreCase(key)) {
                    int pos = value.indexOf(' ');
                    newKey += '_' + value.substring(0, pos);
                    value = value.substring(pos + 1);
                }
                writer.print(newKey);
                writer.print("=");
                writer.print(value.replaceAll("\n", " \\\\\n"));
                writer.println();
            }
        }

        Iterator<String> keys = knownKeys.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Collection<String> values = copy.getValues(key);
            Iterator<String> iter = values.iterator();
            String value;
            while (iter.hasNext()) {
                value = iter.next();
                writer.print(key);
                writer.print("=");
                writer.print(value.replaceAll("\n", " \\\\\n"));
                writer.println();
            }
        }

        writer.flush();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials() {
        return configAll.getName();
    }

    /**
     * @return the internal name of the module, useful when re-constructing all
     *         the meta-information, after installation for example
     */
    String getInternalName() {
        return configAll.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getAbbreviation()
     */
    public String getAbbreviation() {
        String abbreviation = getProperty(KEY_ABBREVIATION);
        if (abbreviation != null && abbreviation.length() > 0) {
            return abbreviation;
        }
        return getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLeftToRight()
     */
    public boolean isLeftToRight() {
        // This should return the dominate direction of the text, if it is BiDi,
        // then we have to guess.
        String dir = getProperty(KEY_DIRECTION);
        if (ConfigEntryType.DIRECTION_BIDI.equalsIgnoreCase(dir)) {
            // When BiDi, return the dominate direction based upon the Book's
            // Language not Direction
            Language lang = getLanguage();
            return lang.isLeftToRight();
        }

        return ConfigEntryType.DIRECTION_LTOR.equalsIgnoreCase(dir);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#hasFeature(org.crosswire.jsword.book.FeatureType)
     */
    @Override
    public boolean hasFeature(FeatureType feature) {
        String name = feature.toString();
        // Features are a positive statement.
        // If we find it mentioned anywhere, then it true
        if (configAll.containsValue(KEY_FEATURE, name)) {
            return true;
        }
        // Many "features" are GlobalOptionFilters, which in the Sword C++ API
        // indicate a class to use for filtering.
        // These mostly have the source type prepended to the feature
        StringBuilder buffer = new StringBuilder(getProperty(KEY_SOURCE_TYPE));
        buffer.append(name);
        if (configAll.containsValue(KEY_GLOBAL_OPTION_FILTER, buffer.toString())) {
            return true;
        }

        // Check for the alias prefixed by the source type
        String alias = feature.getAlias();
        buffer.setLength(0);
        buffer.append(getProperty(KEY_SOURCE_TYPE));
        buffer.append(alias);

        // But some do not
        return configAll.containsValue(KEY_GLOBAL_OPTION_FILTER, name) || configAll.containsValue(KEY_GLOBAL_OPTION_FILTER, buffer.toString());

    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getPropertyKeys()
     */
    public Set<String> getPropertyKeys() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperty(java.lang.String)
     */
    public String getProperty(String key) {
        if (KEY_LANGUAGE.equals(key)) {
            return getLanguage().getName();
        }
        return configAll.get(key, DEFAULTS.get(key));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookMetaData#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty(String key, String value) {
        configAll.replace(key, value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#putProperty(java.lang.String, java.lang.String, boolean)
     */
    public void putProperty(String key, String value, boolean forFrontend) {
        MetaDataLocator mdl = forFrontend ? MetaDataLocator.FRONTEND : MetaDataLocator.JSWORD;
        putProperty(key, value, mdl);
    }

    /**
     * Allow specification of a specific SwordMetaDataLocator when saving a property.
     * 
     * @param key the entry that we are saving
     * @param value the value of the entry
     * @param metaDataLocator Place to save - front end storage, shared storage, or don't save(transient)
     */
    public void putProperty(String key, String value, MetaDataLocator metaDataLocator) {
        // Set the property for all to see
        setProperty(key, value);

        // Properties are only saved for installed books.
        if (!installed) {
            return;
        }

        File writeLocation = metaDataLocator.getWriteLocation();
        // Properties are only saved if there is a place for saving
        if (writeLocation == null) {
            return;
        }
        // persist property for future sessions if JSword or Front-end MetaDataLocator
        // Wait until the first write to actually create the appropriate internal storage.
        IniSection config = null;
        switch (metaDataLocator) {
        case FRONTEND:
            if (this.configFrontend == null) {
                this.configFrontend = new IniSection(configAll.getName());
            }
            config = this.configFrontend;
            break;
        case JSWORD:
            if (this.configJSword == null) {
                this.configJSword = new IniSection(configAll.getName());
            }
            config = this.configJSword;
            break;
        case TRANSIENT:
        default:
            break;
        }

        if (config != null) {
            config.replace(key, value);
            try {
                config.save(new File(writeLocation, bookConf), getBookCharset());
            } catch (IOException ex) {
                LOGGER.error("Unable to save {}={}: conf file for [{}]; error={}", key, value, configAll.getName(), ex);
            }
        }
    }

    /**
     * Allow for partial loading of a minimum set of keys, saving time and space.
     * If partial, call reload(null) to fill it in before showing the conf contents to a user.
     * 
     * @param partial
     */
    public static void setPartialLoading(boolean partial) {

        if (partial != partialLoading) {
            if (partial) {
                keyKeepers = new KeyFilter(REQUIRED);
            } else {
                keyKeepers = null;
            }
        }
        partialLoading = partial;
    }

    /**
     * Load the conf from a file.
     *
     * @param keepers
     *            the keys to keep. When null keep all
     * @throws IOException
     */
    private void loadFile(Filter<String> keepers) throws IOException {
        filtered = keepers != null;

        configAll.clear();
        configAll.load(configFile, ENCODING_UTF8, keepers);
        String encoding = configAll.get(KEY_ENCODING);
        if (!ENCODING_UTF8.equalsIgnoreCase(encoding)) {
            configAll.clear();
            configAll.load(configFile, ENCODING_LATIN1, keepers);
        }
    }

    /**
     * Load the conf from a buffer. This is used to load conf entries from the cached mods.d.tar.gz or mods.d.zip file.
     *
     * @param buffer
     *            the buffer to load
     * @throws IOException
     */
    private void loadBuffer(byte[] buffer, Filter<String> keepers) throws IOException {
        filtered = keepers != null;

        configAll.clear();
        configAll.load(buffer, ENCODING_UTF8, keepers);
        String encoding = configAll.get(KEY_ENCODING);
        if (!ENCODING_UTF8.equalsIgnoreCase(encoding)) {
            configAll.clear();
            configAll.load(buffer, ENCODING_LATIN1, keepers);
        }
    }

    private IniSection addConfig(MetaDataLocator locator) {
        // The write location supersedes the read location
        File conf = new File(locator.getWriteLocation(), bookConf);
        if (!conf.exists()) {
            conf = new File(locator.getReadLocation(), bookConf);
        }

        if (conf.exists()) {
            // The additional confs have the same encoding as the SWORD conf.
            String encoding = getProperty(KEY_ENCODING);
            try {
                IniSection config = new IniSection();
                config.load(conf, encoding);
                mergeConfig(config);
                return config;
            } catch (IOException e) {
                LOGGER.error("Unable to load conf {}:{}", conf, e);
            }
        }

        return null;
    }

    private void mergeConfig(IniSection config) {
        for (String key : config.getKeys()) {
            ConfigEntryType type = ConfigEntryType.fromString(key);
            for (String value : config.getValues(key)) {
                if (type != null && type.mayRepeat()) {
                    if (!configAll.containsValue(key, value)) {
                        configAll.add(key, value);
                    }
                } else {
                    setProperty(key, value);
                }
            }
        }
    }

    /**
     * Gets a particular entry value by its type
     *
     * @param key of the entry
     * @return the requested value, the default (if there is no entry) or null
     *         (if there is no default)
     */
    private Object getValue(String key) {
        ConfigEntryType type = ConfigEntryType.fromString(key);
        String ce = getProperty(key);
        if (type == null) {
            return ce;
        }

        return ce == null ? null : type.convert(ce);
    }

    private Element toRow(OSISUtil.OSISFactory factory, String key, String value) {
        Element nameEle = toKeyCell(factory, key);

        Element valueElement = factory.createCell();
        valueElement.addContent(value);

        // Each key gets one row.
        Element rowEle = factory.createRow();
        rowEle.addContent(nameEle);
        rowEle.addContent(valueElement);
        return rowEle;
    }

    private Element toRow(OSISUtil.OSISFactory factory, String key) {
        int size = configAll.size(key);
        if (size == 0) {
            return null;
        }

        // See if it is a predefined type
        ConfigEntryType type = ConfigEntryType.fromString(key);
        Element nameEle = toKeyCell(factory, key);

        Element valueElement = factory.createCell();
        for (int j = 0; j < size; j++) {
            if (j > 0) {
                valueElement.addContent(factory.createLB());
            }

            String text = configAll.get(key, j);
            if (type != null && !type.isText() && type.isAllowed(text)) {
                text = type.convert(text).toString();
            }
            text = XMLUtil.escape(text);
            if (type != null && type.allowsRTF()) {
                valueElement.addContent(OSISUtil.rtfToOsis(text));
            } else {
                valueElement.addContent(text);
            }
        }

        // Each key gets one row.
        Element rowEle = factory.createRow();
        rowEle.addContent(nameEle);
        rowEle.addContent(valueElement);

        return rowEle;
    }

    private Element toKeyCell(OSISUtil.OSISFactory factory, String key) {
        Element nameEle = factory.createCell();
        Element hiEle = factory.createHI();
        hiEle.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.HI_BOLD);
        nameEle.addContent(hiEle);
        // I18N(DMS): use name to lookup translation.
        hiEle.addContent(key);
        return nameEle;
    }

    private void adjustConfig() throws BookException {
        adjustLocation();
        adjustLanguage();
        adjustBookType();
        adjustName();
        adjustHistory(configAll);
    }

    private void adjustLanguage() {
        String lang = getProperty(KEY_LANG);
        testLanguage(KEY_LANG, lang);

        String langFrom = configAll.get(KEY_GLOSSARY_FROM);
        String langTo = configAll.get(KEY_GLOSSARY_TO);

        // If we have either langFrom or langTo, we are dealing with a glossary
        if (langFrom != null || langTo != null) {
            if (langFrom == null) {
                langFrom = lang;
                setProperty(KEY_GLOSSARY_FROM, langFrom);
                LOGGER.warn("Missing data for [{}]. Assuming {}={}", configAll.getName(), KEY_GLOSSARY_FROM, langFrom);
            }
            testLanguage(KEY_GLOSSARY_FROM, langFrom);

            if (langTo == null) {
                langTo = Language.DEFAULT_LANG.getGivenSpecification();
                setProperty(KEY_GLOSSARY_TO, langTo);
                LOGGER.warn("Missing data for [{}]. Assuming {}={}", configAll.getName(), KEY_GLOSSARY_TO, langTo);
            }
            testLanguage(KEY_GLOSSARY_TO, langTo);

            // At least one of the two languages should match the lang entry
            if (!langFrom.equals(lang) && !langTo.equals(lang)) {
                LOGGER.error("Data error in [{}]. Neither {} or {} match {}", configAll.getName(), KEY_GLOSSARY_FROM, KEY_GLOSSARY_TO, KEY_LANG);
            }
        }

        setLanguage((Language) getValue(KEY_LANG));
    }

    private void testLanguage(String key, String lang) {
        Language language = new Language(lang);
        if (!language.isValidLanguage()) {
            LOGGER.warn("Unknown language [{}]{}={}", configAll.getName(), key, lang);
        }
    }

    private void adjustBookType() {
        // The book type represents the underlying category of book.
        // Fine tune it here.
        BookCategory focusedCategory = (BookCategory) getValue(KEY_CATEGORY);
        questionable = focusedCategory == BookCategory.QUESTIONABLE;

        String modTypeName = getProperty(KEY_MOD_DRV);
        if (modTypeName == null) {
            LOGGER.error("Book not supported: malformed conf file for [{}] no {} found.", configAll.getName(), KEY_MOD_DRV);
            supported = false;
            return;
        }

        String v11n = getProperty(KEY_VERSIFICATION);
        if (!Versifications.instance().isDefined(v11n)) {
            LOGGER.error("Book not supported: Unknown versification for [{}]{}={}.", configAll.getName(), KEY_VERSIFICATION, v11n);
            supported = false;
            return;
        }

        bookType = BookType.fromString(modTypeName);
        if (bookType == null) {
            LOGGER.error("Book not supported: malformed conf file for [{}] no book type found", configAll.getName());
            supported = false;
            return;
        }

        // The book type represents the underlying category of book.
        // Fine tune it here.
        if (focusedCategory == BookCategory.OTHER) {
            focusedCategory = bookType.getBookCategory();
        }

        setProperty(KEY_CATEGORY, focusedCategory.getName());
    }

    private void adjustName() {
        // If there is no name then use the initials name
        if (configAll.get(KEY_DESCRIPTION) == null) {
            LOGGER.error("Malformed conf file: missing [{}]{}=. Using {}", configAll.getName(), KEY_DESCRIPTION, configAll.getName());
            setProperty(KEY_DESCRIPTION, configAll.getName());
        }
    }

    /* This method sets the location on the sword conf file for an installed Book.
     */
    private void adjustLocation() throws BookException {

        URI library = getLibrary();
        if (library == null) {
            return;
        }

        // Previously, all DATA_PATH entries end in / to indicate dirs
        // or not to indicate file prefixes.
        // This is no longer true.
        // Now we need to test the file/url to see if it exists and is a
        // directory.
        String datapath = getProperty(KEY_DATA_PATH);
        int lastSlash = datapath.lastIndexOf('/');

        // There were modules that did not have a valid DataPath.
        // This should not be necessary
        if (lastSlash == -1) {
            return;
        }

        // DataPath typically ends in a '/' to indicate a directory.
        // If so remove it.
        boolean isDirectoryPath = false;
        if (lastSlash == datapath.length() - 1) {
            isDirectoryPath = true;
            datapath = datapath.substring(0, lastSlash);
        }

        URI location = NetUtil.lengthenURI(library, datapath);
        File bookDir = new File(location.getPath());
        // For some modules, the last element of the DataPath
        // is a prefix for file names.
        if (!bookDir.isDirectory()) {
            if (isDirectoryPath) {
                // TRANSLATOR: This indicates that the Book is only partially installed.
                throw new BookException(JSMsg.gettext("The book {0} is missing its data files", configAll.getName()));
            }

            // not a directory path
            // try appending .dat on the end to see if we have a file, if not,
            // then
            if (!new File(location.getPath() + ".dat").exists()) {
                // TRANSLATOR: This indicates that the Book is only partially
                // installed.
                throw new BookException(JSMsg.gettext("The book {0} is missing its data files", configAll.getName()));
            }

            // then we have a module that has a prefix
            // Shorten it by one segment and test again.
            lastSlash = datapath.lastIndexOf('/');
            datapath = datapath.substring(0, lastSlash);
            location = NetUtil.lengthenURI(library, datapath);
        }

        setLocation(location);
    }

    // History is a special case. It is of the form History_x.x
    // The ConfigEntryType is History without the _x.x.
    // We want to put x.x at the beginning of the string
    private static void adjustHistory(IniSection config) {
        // Iterate over a copy of the keys so that we don't get
        // a concurrent modification exception when we remove matching keys
        // and when we add new keys
        List<String> keys = new ArrayList(config.getKeys());
        for (String key : keys) {
            String value = config.get(key);
            ConfigEntryType type = ConfigEntryType.fromString(key);
            if (ConfigEntryType.HISTORY.equals(type)) {
                config.remove(key);
                int pos = key.indexOf('_');
                value = key.substring(pos + 1) + ' ' + value;
                config.add(KEY_HISTORY, value);
            }
        }
    }

    public static void report(final IniSection config) {
        StringBuilder buf = new StringBuilder(config.report());
        for (String key : config.getKeys()) {
            ConfigEntryType type = ConfigEntryType.fromString(key);

            if (type == null) {
                if (key.contains("_")) {
                    String baseKey = key.substring(0, key.indexOf('_'));
                    type = ConfigEntryType.fromString(baseKey);
                }
            }


            int count = config.size(key);
            for (int i = 0; i < count; i++) {
                String value = config.get(key, i);

                // If it is still unknown, report and skip
                if (type == null) {
                    buf.append("Unknown entry: ").append(key).append(" = ").append(value).append('\n');
                    continue;
                }

                // Only CIPHER_KEYS that are empty are not ignored
                if (value.length() == 0 && type != ConfigEntryType.CIPHER_KEY) {
                    buf.append("Unexpected empty entry: ").append(key).append(" = ").append(value).append('\n');
                    continue;
                }

                // Filter known types of entries
                value = type.filter(value);

                // Report on fields that shouldn't have RTF but do
                if (!type.allowsRTF() && RTF_PATTERN.matcher(value).find()) {
                    buf.append("Unexpected RTF: ").append(key).append(" = ").append(value).append('\n');
                }

                if (!type.allowsHTML() && HTML_PATTERN.matcher(value).find()) {
                    buf.append("Unexpected HTML: ").append(key).append(" = ").append(value).append('\n');
                }

                if (!type.isAllowed(value)) {
                    buf.append("Unknown config value: ").append(key).append(" = ").append(value).append('\n');
                }

                if (count > 1 && !type.mayRepeat()) {
                    buf.append("Unexpected repeated config key: ").append(key).append(" = ").append(value).append('\n');
                }
            }
        }
        if (buf.length() > 0) {
            LOGGER.info("Conf report for [{}]\n{}", config.getName(), buf.toString());
        }
    }

    /**
     * Indicates whether the Book is installed or not.
     */
    private boolean installed;

    /**
     * When true this BookMetaData is filtered and only partially loaded.
     * Reloading without a filter will change this to false.
     */
    private boolean filtered;

    /**
     * The name of the conf file, such as kjv.conf.
     */
    private String bookConf;

    /**
     * The configAll IniSection holds the merged view of the SWORD config,
     * configJSword, and configFrontend.
     */
    private IniSection configAll;

    /**
     * configJSword holds shared configuration for all front-ends.
     */
    private IniSection configJSword;

    /**
     * configFrontend contains the configuration for the current front-end.
     */
    private IniSection configFrontend;

    /**
     * True if this book's config type can be used by JSword.
     */
    private boolean supported;

    /**
     * The BookCategory for this Book
     */
    private BookCategory bookCat;

    /**
     * The BookType for this Book
     */
    private BookType bookType;

    /**
     * True if this book is considered questionable.
     */
    private boolean questionable;

    /**
     * If the module's config is tied to a file remember it so that it can be
     * updated.
     */
    private File configFile;

    /**
     * These are the elements that JSword uses for control when present in a conf.
     */
    private static final String[] REQUIRED = {
            KEY_ABBREVIATION,
            KEY_DESCRIPTION,
            KEY_LANG,
            KEY_CATEGORY,
            KEY_VERSION,
            KEY_FEATURE,
            KEY_GLOBAL_OPTION_FILTER,
            KEY_SIGLUM1,
            KEY_SIGLUM2,
            KEY_SIGLUM3,
            KEY_SIGLUM4,
            KEY_SIGLUM5,
            KEY_FONT,
            KEY_DATA_PATH,
            KEY_MOD_DRV,
            KEY_SOURCE_TYPE,
            KEY_BLOCK_TYPE,
            KEY_BLOCK_COUNT,
            KEY_COMPRESS_TYPE,
            KEY_ENCODING,
            KEY_DIRECTION,
            KEY_KEY_TYPE,
            KEY_DISPLAY_LEVEL,
            KEY_VERSIFICATION,
            KEY_CASE_SENSITIVE_KEYS,
            KEY_LOCAL_STRIP_FILTER,
            KEY_PREFERRED_CSS_XHTML,
            KEY_STRONGS_PADDING,
            KEY_SEARCH_OPTION,
            KEY_INSTALL_SIZE,
            KEY_SCOPE,
            KEY_BOOKLIST,
            KEY_CIPHER_KEY
    };

    /**
     * KeyFilter returns true for keys that should always be present.
     * A partially loaded SwordBookMetaData will satisfy this filter.
     */
    private static final class KeyFilter implements Filter<String> {
        /**
         * Create a KeyFilter for the expected keys.
         * @param keepers the list of keys that should be retained
         */
        KeyFilter(String[] keepers) {
            this.keepers = new HashSet();

            // Load up the keepers set
            for (String key : keepers) {
                this.keepers.add(key);
            }
        }
        public boolean test(String key) {
            return keepers.contains(key);
        }
        private Set keepers;
    }

    private static boolean partialLoading;

    private static Filter keyKeepers;

    private static final String[] OSIS_INFO = {
            KEY_ABBREVIATION,
            KEY_DESCRIPTION,
            KEY_LANG,
            KEY_CATEGORY,
            KEY_LCSH,
            KEY_SWORD_VERSION_DATE,
            KEY_VERSION,
            KEY_HISTORY,
            KEY_OBSOLETES,
            KEY_GLOSSARY_FROM,
            KEY_GLOSSARY_TO,
            KEY_ABOUT,
            KEY_SHORT_PROMO,
            KEY_DISTRIBUTION_LICENSE,
            KEY_DISTRIBUTION_NOTES,
            KEY_DISTRIBUTION_SOURCE,
            KEY_SHORT_COPYRIGHT,
            KEY_COPYRIGHT,
            KEY_COPYRIGHT_DATE,
            KEY_COPYRIGHT_HOLDER,
            KEY_COPYRIGHT_CONTACT_NAME,
            KEY_COPYRIGHT_CONTACT_ADDRESS,
            KEY_COPYRIGHT_CONTACT_EMAIL,
            KEY_COPYRIGHT_CONTACT_NOTES,
            KEY_COPYRIGHT_NOTES,
            KEY_TEXT_SOURCE,
            KEY_FEATURE,
            KEY_GLOBAL_OPTION_FILTER,
            KEY_SIGLUM1,
            KEY_SIGLUM2,
            KEY_SIGLUM3,
            KEY_SIGLUM4,
            KEY_SIGLUM5,
            KEY_FONT,
            KEY_DATA_PATH,
            KEY_MOD_DRV,
            KEY_SOURCE_TYPE,
            KEY_BLOCK_TYPE,
            KEY_BLOCK_COUNT,
            KEY_COMPRESS_TYPE,
            KEY_ENCODING,
            KEY_MINIMUM_VERSION,
            KEY_OSIS_VERSION,
            KEY_OSIS_Q_TO_TICK,
            KEY_DIRECTION,
            KEY_KEY_TYPE,
            KEY_DISPLAY_LEVEL,
            KEY_VERSIFICATION,
            KEY_CASE_SENSITIVE_KEYS,
            KEY_LOCAL_STRIP_FILTER,
            KEY_PREFERRED_CSS_XHTML,
            KEY_STRONGS_PADDING,
            KEY_SEARCH_OPTION,
            KEY_INSTALL_SIZE,
            KEY_SCOPE,
            KEY_BOOKLIST
    };

    private static final String[] HIDDEN = {
        KEY_CIPHER_KEY,
        KEY_LANGUAGE
    };

    private static final Pattern RTF_PATTERN = Pattern.compile("\\\\pard|\\\\pa[er]|\\\\qc|\\\\[bi]|\\\\u-?[0-9]{4,6}+");
    private static final Pattern HTML_PATTERN = Pattern.compile("(<[a-zA-Z]|[a-zA-Z]>)");

    /**
     * Sword only recognizes two encodings for its modules: UTF-8 and Latin-1
     * Sword uses MS Windows cp1252 for Latin 1 not the standard.
     * Arrgh! The encoding strings need to be converted to Java charsets
     */
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String ENCODING_LATIN1 = "WINDOWS-1252";
    private static final PropertyMap ENCODING_JAVA = new PropertyMap();
    static {
        ENCODING_JAVA.put("Latin-1", ENCODING_LATIN1);
        ENCODING_JAVA.put("UTF-8", ENCODING_UTF8);
    }

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwordBookMetaData.class);

}
