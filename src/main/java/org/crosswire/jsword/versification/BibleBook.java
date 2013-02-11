/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005 - 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.crosswire.jsword.internationalisation.LocaleProviderManager;

/**
 * A BibleBook is a book of the Bible. It may or may not be canonical.
 * Note that the ordering of these books varies from one Versification to another.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum BibleBook {
    // JSword introduction to the Bible
    INTRO_BIBLE("Intro.Bible"),
    // JSword introduction to the Old Testament
    INTRO_OT("Intro.OT"),
    // Old Testament
    GEN("Gen"),
    EXOD("Exod"),
    LEV("Lev"),
    NUM("Num"),
    DEUT("Deut"),
    JOSH("Josh"),
    JUDG("Judg"),
    RUTH("Ruth"),
    SAM1("1Sam"),
    SAM2("2Sam"),
    KGS1("1Kgs"),
    KGS2("2Kgs"),
    CHR1("1Chr"),
    CHR2("2Chr"),
    EZRA("Ezra"),
    NEH("Neh"),
    ESTH("Esth"),
    JOB("Job"),
    PS("Ps"),
    PROV("Prov"),
    ECCL("Eccl"),
    SONG("Song"),
    ISA("Isa"),
    JER("Jer"),
    LAM("Lam"),
    EZEK("Ezek"),
    DAN("Dan"),
    HOS("Hos"),
    JOEL("Joel"),
    AMOS("Amos"),
    OBAD("Obad"),
    JONAH("Jonah"),
    MIC("Mic"),
    NAH("Nah"),
    HAB("Hab"),
    ZEPH("Zeph"),
    HAG("Hag"),
    ZECH("Zech"),
    MAL("Mal"),
    // JSword introduction to the New Testament
    INTRO_NT("Intro.NT"),
    // New Testament
    MATT("Matt"),
    MARK("Mark"),
    LUKE("Luke"),
    JOHN("John"),
    ACTS("Acts"),
    ROM("Rom"),
    COR1("1Cor"),
    COR2("2Cor"),
    GAL("Gal"),
    EPH("Eph"),
    PHIL("Phil"),
    COL("Col"),
    THESS1("1Thess"),
    THESS2("2Thess"),
    TIM1("1Tim"),
    TIM2("2Tim"),
    TITUS("Titus"),
    PHLM("Phlm"),
    HEB("Heb"),
    JAS("Jas"),
    PET1("1Pet"),
    PET2("2Pet"),
    JOHN1("1John"),
    JOHN2("2John"),
    JOHN3("3John"),
    JUDE("Jude"),
    REV("Rev"),
    // Apocrypha
    TOB("Tob"),
    JDT("Jdt"),
    ADD_ESTH("AddEsth"),
    WIS("Wis"),
    SIR("Sir"),
    BAR("Bar"),
    EP_JER("EpJer"),
    PR_AZAR("PrAzar"),
    SUS("Sus"),
    BEL("Bel"),
    MACC1("1Macc"),
    MACC2("2Macc"),
    MACC3("3Macc"),
    MACC4("4Macc"),
    PR_MAN("PrMan"),
    ESD1("1Esd"),
    ESD2("2Esd"),
    PSS151("Ps151"),
    // Rahlfs' LXX
    ODES("Odes"),
    PSALM_SOL("PssSol"),
    // Vulgate & other later Latin mss
    EP_LAO("EpLao"),
    ESD3("3Esd"),
    ESD4("4Esd"),
    ESD5("5Esd"),
    // Ethiopian Orthodox Canon/Ge'ez Translation
    EN1("1En"),
    JUBS("Jub"),
    BAR4("4Bar"),
    ASCEN_ISA("AscenIsa"),
    PS_JOS("PsJos"),
    // Coptic Orthodox Canon
    APOSTOLIC("AposCon"),
    CLEM1("1Clem"),
    CLEM2("2Clem"),
    // Armenian Orthodox Canon
    COR3("3Cor"),
    EP_COR_PAUL("EpCorPaul"),
    JOS_ASEN("JosAsen"),
    T12PATR("T12Patr"),
    T12PATR_TASH("T12Patr.TAsh"),
    T12PATR_TBENJ("T12Patr.TBenj"),
    T12PATR_TDAN("T12Patr.TDan"),
    T12PATR_GAD("T12Patr.TGad"),
    T12PATR_TISS("T12Patr.TIss"),
    T12PATR_TJOS("T12Patr.TJos"),
    T12PATR_TJUD("T12Patr.TJud"),
    T12PATR_TLEVI("T12Patr.TLevi"),
    T12PATR_TNAPH("T12Patr.TNaph"),
    T12PATR_TREU("T12Patr.TReu"),
    T12PATR_TSIM("T12Patr.TSim"),
    T12PATR_TZeb("T12Patr.TZeb"),
    // Peshitta
    BAR2("2Bar"),
    EP_BAR("EpBar"),
    // Codex Sinaiticus
    BARN("Barn"),
    HERM("Herm"),
    HERM_MAND("Herm.Mand"),
    HERM_SIM("Herm.Sim"),
    HERM_VIS("Herm.Vis"),
    // Other books
    ADD_DAN("AddDan"),
    ADD_PS("AddPs"),
    ESTH_GR("EsthGr");

    BibleBook(String osis) {
        this.osis = osis;
    }

    /**
     * Get the OSIS representation of this BibleBook.
     *
     * @return the OSIS name
     */
    public String getOSIS() {
        return osis;
    }

    /**
     * Get the OSIS representation of this BibleBook.
     *
     * @return the OSIS name
     */
    @Override
    public String toString() {
        return osis;
    }

    /**
     * Case insensitive search for BibleBook for an OSIS name.
     *
     * @param osis
     * @return the matching BibleBook or null
     */
    public static BibleBook fromOSIS(String osis) {
        String match = BookName.normalize(osis, Locale.ENGLISH);
        return osisMap.get(match);
    }

    /**
     * Get the BookName.
     *
     * @return The requested BookName
     */
    public BookName getBookName() {
        return getLocalisedBibleNames().getBookName(this);
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     *
     * @return The full name of the book
     */
    public String getPreferredName() {
        return getLocalisedBibleNames().getPreferredName(this);
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     *
     * @return The full name of the book
     */
    public String getLongName() {
        return getLocalisedBibleNames().getLongName(this);
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     *
     * @return The short name of the book
     */
    public String getShortName() {
        return getLocalisedBibleNames().getShortName(this);
    }

    /**
     * Get a book from its name.
     *
     * @param find
     *            The string to identify
     * @return The BibleBook, On error null
     */
    public static BibleBook getBook(String find) {
        BibleBook book = null;
        if (containsLetter(find)) {
            book = fromOSIS(find);

            if (book == null) {
                book = getLocalisedBibleNames().getBook(find);
            }

            if (book == null && englishBibleNames != null) {
                book = englishBibleNames.getBook(find);
            }
        }
        return book;
    }

    /**
     * Gets the localised bible names, based on the {@link LocaleProviderManager}
     *
     * @return the localised bible names
     */
    private static BibleNames getLocalisedBibleNames() {
        //get the current Locale
        return getBibleNamesForLocale(LocaleProviderManager.getLocale());
    }

    /**
     * Gets the bible names for a specific locale.
     *
     * @param locale the locale
     * @return the bible names for locale
     */
    private static BibleNames getBibleNamesForLocale(Locale locale) {
        BibleNames bibleNames = localizedBibleNames.get(locale);
        if (bibleNames == null) {
            synchronized (BibleBook.class) {
                bibleNames = localizedBibleNames.get(locale);
                if (bibleNames == null) {
                    bibleNames = new BibleNames(locale);
                    localizedBibleNames.put(locale, bibleNames);
                }
            }
        }

        return bibleNames;
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBook() will return a BibleBook and not null.
     *
     * @param find
     *            The string to identify
     * @return true when the book name is recognized
     */
    public static boolean isBook(String find) {
        return getBook(find) != null;
    }

    /* package */ static BibleBook[] getBooks() {
        return books;
    }

    /**
     * Load up the resources for Bible book and section names.
     */
    private static void initialize() {
        //Always load up the English Locale Bible names as we can't guarantee how many different locales we are supporting.
        englishBibleNames = getBibleNamesForLocale(Locale.ENGLISH);
    }

    /**
     * This is simply a convenience function to wrap Character.isLetter()
     *
     * @param text
     *            The string to be parsed
     * @return true if the string contains letters
     */
    private static boolean containsLetter(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private String osis;

    /** A quick lookup based on OSIS name for the book */
    private static Map<String, BibleBook> osisMap = new HashMap<String, BibleBook>();

    /** The universe of ordered books, allowing for efficient previous next */
    private static BibleBook[] books = BibleBook.values();

    /** we cache the Localised Bible Names because there is quite a bit of processing going on for each individual Locale */
    private static Map<Locale, BibleNames> localizedBibleNames = new HashMap<Locale, BibleNames>();

    /** English BibleNames, or null when using the program's default locale */
    private static BibleNames englishBibleNames;

    static {
        for (BibleBook book : BibleBook.values()) {
            osisMap.put(BookName.normalize(book.getOSIS(), Locale.ENGLISH), book);
        }
        initialize();
    }

}
