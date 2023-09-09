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
package org.crosswire.jsword.book.readings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Language;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.PreferredKey;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.SetKeyList;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Dictionary that displays daily Readings.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class ReadingsBook extends AbstractBook implements PreferredKey {
    /**
     * Constructor for ReadingsBook.
     * @param driver the driver for this book
     * @param setname the name of this book
     * @param type the type of book
     */
    public ReadingsBook(ReadingsBookDriver driver, String setname, BookCategory type) {
        super(null, null); // set the book metadata later and no backend

        hash = new TreeMap<Key, String>();

        //although we're getting this from the LocaleProviderManager, we're still setting 
        //the language on the metadata once, so won't cope for dynamic changes
        Locale defaultLocale = LocaleProviderManager.getLocale();
        ResourceBundle prop = ResourceBundle.getBundle(setname, defaultLocale, CWClassLoader.instance(ReadingsBookDriver.class));

        // TRANSLATOR: The default name for JSword's Reading plan.
        String name = JSMsg.gettext("Readings");
        try {
            name = prop.getString("title");
        } catch (MissingResourceException e) {
            log.warn("Missing resource: 'title' while parsing: {}", setname);
        }

        DefaultBookMetaData bmd = new DefaultBookMetaData(driver, name, type);
        bmd.setInitials(setname);
        bmd.setLanguage(new Language(defaultLocale.getLanguage()));
        setBookMetaData(bmd);

        // Go through the current year
        java.util.Calendar greg = new java.util.GregorianCalendar();
        greg.set(java.util.Calendar.DAY_OF_MONTH, 1);
        greg.set(java.util.Calendar.MONDAY, java.util.Calendar.JANUARY);
        int currentYear = greg.get(java.util.Calendar.YEAR);

        while (greg.get(java.util.Calendar.YEAR) == currentYear) {
            String internalKey = ReadingsKey.external2internal(greg);
            String readings = "";

            try {
                readings = prop.getString(internalKey);
                hash.put(new ReadingsKey(greg.getTime()), readings);
            } catch (MissingResourceException e) {
                log.warn("Missing resource: {} while parsing: {}", internalKey, setname);
            }

            greg.add(java.util.Calendar.DATE, 1);
        }

        global = new SetKeyList(hash.keySet(), getName());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.PreferredKey#getPreferred()
     */
    public Key getPreferred() {
        return new ReadingsKey(new Date());
    }

    public Iterator<Content> getOsisIterator(final Key key, final boolean allowEmpty, final boolean allowGenTitles) throws BookException {
        if (!(key instanceof ReadingsKey)) {
            // TRANSLATOR: Error condition: Indicates that something could not be found in the book. {0} is a placeholder for the unknown key.
            throw new BookException(JSMsg.gettext("Key not found {0}", key.getName()));
        }

        // TODO(DMS): make the iterator be demand driven
        List<Content> content = new ArrayList<Content>();

        Element title = OSISUtil.factory().createTitle();
        title.addContent(key.getName());
        content.add(title);

        String readings = hash.get(key);
        if (readings == null) {
            // TRANSLATOR: Error condition: Indicates that something could not be found in the book. {0} is a placeholder for the unknown key.
            throw new BookException(JSMsg.gettext("Key not found {0}", key.getName()));
        }

        try {
            // AV11N(DMS): Is this right?
            // At this point and time, ReadingsBook is a verse list from the KJV.
            // Should store the v11n in the ReadingsBook
            PassageKeyFactory keyf = PassageKeyFactory.instance();
            Passage ref = keyf.getKey(Versifications.instance().getVersification("KJV"), readings);

            Element list = OSISUtil.factory().createList();
            content.add(list);

            Iterator<VerseRange> it = ref.rangeIterator(RestrictionType.NONE);
            while (it.hasNext()) {
                Key range = it.next();

                Element reading = OSISUtil.factory().createReference();
                reading.setAttribute(OSISUtil.OSIS_ATTR_REF, range.getOsisRef());
                reading.addContent(range.getName());

                Element item = OSISUtil.factory().createItem();
                item.addContent(reading);
                list.addContent(item);
            }
        } catch (NoSuchKeyException ex) {
            content.add(OSISUtil.factory().createText(JSOtherMsg.lookupText("Failed to parse {0}", readings)));
        }

        return content.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return false;
    }

    /** Returns an empty string
     * (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        return "";
    }

    /**
     * Returns an empty list
     */
    @Override
    public List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
    public boolean isWritable() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String rawData) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only."));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only."));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getValidKey(java.lang.String)
     */
    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (NoSuchKeyException e) {
            return createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException {
        DefaultKeyList reply = new DefaultKeyList();
        reply.addAll(new ReadingsKey(name, name, global));
        return reply;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getGlobalKeyList()
     */
    public Key getGlobalKeyList() {
        return global;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#createEmptyKeyList()
     */
    public Key createEmptyKeyList() {
        return new DefaultKeyList();
    }

    @Override
    public boolean hasFeature(FeatureType feature) {
        return feature == FeatureType.DAILY_DEVOTIONS;
    }

    /**
     * The global key list
     */
    private Key global;

    /**
     * The store of keys and data
     */
    private Map<Key, String> hash;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ReadingsBook.class);
}
