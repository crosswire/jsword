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
package org.crosswire.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import org.crosswire.common.config.ChoiceFactory;
import org.slf4j.LoggerFactory;

/**
 * Translations provides a list of locales that BibleDesktop has been translated
 * into.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class Translations {
    /**
     * Singleton classes have private constructors.
     */
    private Translations() {
        try {
            loadSupportedTranslations();
            PropertyMap props = ResourceUtil.getProperties(getClass());
            translation = props.get(TRANSLATION_KEY);
            if (translation == null || translation.length() == 0) {
                // check for a match against language and country
                // This pertains to zh_TW and zh_CN
                for (int i = 0; i < translations.length; i++) {
                    Locale supportedLocale = new Locale(translations[i]);
                    if (supportedLocale.getLanguage().equals(originalLocale.getLanguage()) && supportedLocale.getCountry().equals(originalLocale.getCountry())) {
                        translation = translations[i];
                        return;
                    }
                }

                // check for a match against just language
                for (int i = 0; i < translations.length; i++) {
                    Locale supportedLocale = new Locale(translations[i]);
                    if (supportedLocale.getLanguage().equals(originalLocale.getLanguage())) {
                        translation = translations[i];
                        return;
                    }
                }

                // if we don't have a matching locale then just use the default.
                translation = DEFAULT_TRANSLATION;
            }
        } catch (IOException e) {
            translation = DEFAULT_TRANSLATION;
        }
    }

    /**
     * All access to Translations is through this single instance.
     * 
     * @return the singleton instance
     */
    public static Translations instance() {
        return instance;
    }

    /**
     * Gets a listing of all the translations that Bible Desktop supports.
     * 
     * @return an string array of translations in locale friendly names.
     */
    public PropertyMap getSupported() {
        loadSupportedTranslations();

        // I18N(DMS) Collate these according to the current locale, putting the
        // current locale's locale first.
        PropertyMap names = new PropertyMap();

        for (int i = 0; i < translations.length; i++) {
            names.put(translations[i], toString(translations[i]));
        }

        return names;
    }

    /**
     * Get the locale for the current translation.
     * 
     * @return the translation's locale
     */
    public Locale getCurrentLocale() {
        // If there is no particular translation, then return the default
        // locale.
        if (translation == null || DEFAULT_TRANSLATION.equals(translation)) {
            return DEFAULT_LOCALE;
        }

        // If the local consists of a language and a country then use both
        if (translation.indexOf('_') != -1) {
            String[] locale = StringUtil.split(translation, '_');
            return new Locale(locale[0], locale[1]);
        }

        // otherwise just use the country.
        return new Locale(translation);
    }

    /**
     * Get the current translation as a human readable string.
     * 
     * @return the current translation
     */
    public String getCurrent() {
        return toString(translation);
    }

    /**
     * Set the current translation, using human readable string.
     * 
     * @param newTranslation
     *            the translation to use
     */
    public void setCurrent(String newTranslation) {
        String found = DEFAULT_TRANSLATION;
        String currentTranslation = "";
        for (int i = 0; i < translations.length; i++) {
            String trans = translations[i];
            currentTranslation = toString(translation);

            if (trans.equals(newTranslation) || currentTranslation.equals(newTranslation)) {
                found = trans;
                break;
            }
        }

        try {
            translation = found;
            PropertyMap props = new PropertyMap();
            if (!DEFAULT_TRANSLATION.equals(translation)) {
                props.put(TRANSLATION_KEY, translation);
            }

            URI outputURI = CWProject.instance().getWritableURI(getClass().getName(), FileUtil.EXTENSION_PROPERTIES);
            NetUtil.storeProperties(props, outputURI, "BibleDesktop UI Translation");
        } catch (IOException ex) {
            log.error("Failed to save BibleDesktop UI Translation", ex);
        }
    }

    /**
     * Set the locale for the program to the one the user has selected. But
     * don't set it to the default translation, so that the user's actual
     * locale, is used for Bible book names.
     * 
     * This only makes sense after config has called setCurrentTranslation.
     */
    public void setLocale() {
        Locale.setDefault(getCurrentLocale());
    }

    /**
     * Register this class with the common config engine.
     */
    public void register() {
        ChoiceFactory.getDataMap().put(TRANSLATION_KEY, getSupportedTranslations());
    }

    /**
     * Get the current translation as a human readable string.
     * 
     * @return the current translation
     */
    public static String getCurrentTranslation() {
        return Translations.instance().getCurrent();
    }

    /**
     * Set the current translation, using human readable string.
     * 
     * @param newTranslation
     *            the translation to use
     */
    public static void setCurrentTranslation(String newTranslation) {
        Translations.instance().setCurrent(newTranslation);
    }

    /**
     * Gets a listing of all the translations that Bible Desktop supports.
     * 
     * @return an string array of translations in locale friendly names.
     */
    public static Map<String, String> getSupportedTranslations() {
        return Translations.instance().getSupported();
    }

    /**
     * Get a list of the supported translations
     */
    private void loadSupportedTranslations() {
        if (translations == null) {
            try {
                URL index = ResourceUtil.getResource(Translations.class, "translations.txt");
                translations = NetUtil.listByIndexFile(NetUtil.toURI(index));
            } catch (IOException ex) {
                translations = new String[0];
            }
        }
    }

    public String toString(String translationCode) {
        StringBuilder currentTranslation = new StringBuilder(Languages.getName(translationCode));

        if (translationCode.indexOf('_') != -1) {
            String[] locale = StringUtil.split(translationCode, '_');
            currentTranslation.append(", ");
            currentTranslation.append(Countries.getCountry(locale[1]));
        }

        return currentTranslation.toString();
    }

    /**
     * The key used in config.xml
     */
    private static final String TRANSLATION_KEY = "translation-codes";

    /**
     * The default translation, if the user has not chosen anything else.
     */
    public static final String DEFAULT_TRANSLATION = "en";

    /**
     * The default Locale, it the user has not chosen anything else.
     */
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * The translation that BibleDesktop should use.
     */
    private String translation;

    /**
     * List of available translations.
     */
    private String[] translations;

    /**
     * The locale that the program starts with. This needs to precede
     * "instance."
     */
    private static Locale originalLocale = Locale.getDefault();

    private static Translations instance = new Translations();

    /**
     * The log stream
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Translations.class);
}
