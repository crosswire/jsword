package org.crosswire.jsword.versification;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.crosswire.jsword.passage.NoSuchVerseException;

/**
 * A BibleBook is a book of the Bible. It may or may not be canonical.
 */
public enum BibleBook {
    // Old Testament
    GENESIS("Gen"),
    EXODUS("Exod"),
    LEVITICUS("Lev"),
    NUMBERS("Num"),
    DEUTERONOMY("Deut"),
    JOSHUA("Josh"),
    JUDGES("Judg"),
    RUTH("Ruth"),
    SAMUEL1("1Sam"),
    SAMUEL2("2Sam"),
    KINGS1("1Kgs"),
    KINGS2("2Kgs"),
    CHRONICLES1("1Chr"),
    CHRONICLES2("2Chr"),
    EZRA("Ezra"),
    NEHEMIAH("Neh"),
    ESTHER("Esth"),
    JOB("Job"),
    PSALMS("Ps"),
    PROVERBS("Prov"),
    ECCLESIASTES("Eccl"),
    SONGOFSOLOMON("Song"),
    ISAIAH("Isa"),
    JEREMIAH("Jer"),
    LAMENTATIONS("Lam"),
    EZEKIEL("Ezek"),
    DANIEL("Dan"),
    HOSEA("Hos"),
    JOEL("Joel"),
    AMOS("Amos"),
    OBADIAH("Obad"),
    JONAH("Jonah"),
    MICAH("Mic"),
    NAHUM("Nah"),
    HABAKKUK("Hab"),
    ZEPHANIAH("Zeph"),
    HAGGAI("Hag"),
    ZECHARIAH("Zech"),
    MALACHI("Mal"),
    // New Testament
    MATTHEW("Matt"),
    MARK("Mark"),
    LUKE("Luke"),
    JOHN("John"),
    ACTS("Acts"),
    ROMANS("Rom"),
    CORINTHIANS1("1Cor"),
    CORINTHIANS2("2Cor"),
    GALATIANS("Gal"),
    EPHESIANS("Eph"),
    PHILIPPIANS("Phil"),
    COLOSSIANS("Col"),
    THESSALONIANS1("1Thess"),
    THESSALONIANS2("2Thess"),
    TIMOTHY1("1Tim"),
    TIMOTHY2("2Tim"),
    TITUS("Titus"),
    PHILEMON("Phlm"),
    HEBREWS("Heb"),
    JAMES("Jas"),
    PETER1("1Pet"),
    PETER2("2Pet"),
    JOHN1("1John"),
    JOHN2("2John"),
    JOHN3("3John"),
    JUDE("Jude"),
    REVELATION("Rev"),
    // Apocrypha
    TOB("Tob"),
    JDT("Jdt"),
    ADD_EST("AddEsth"),
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
    HERM_VIS("Herm.Vis")
   ;

    BibleBook(String osis) {
        this.osis = osis;
    }

    public String getOSIS() {
        return osis;
    }

    public String toString() {
        return osis;
    }

    public static BibleBook fromOSIS(String osis) {
        String match = BookName.normalize(osis, EN_LOCALE);
        return osisMap.get(match);
    }

    /**
     * Get the BookName.
     * 
     * @param book
     *            The book of the Bible
     * @return The requested BookName
     * @exception NoSuchVerseException
     *                If the book number is not valid
     */
    public BookName getBookName() throws NoSuchVerseException {
        return bibleNames.getBookName(this);
    }

    /**
     * Get the preferred name of a book. Altered by the case setting (see
     * setBookCase() and isFullBookName())
     * 
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     */
    public String getPreferredName() throws NoSuchVerseException {
        return bibleNames.getPreferredName(this);
    }

    /**
     * Get the full name of a book (e.g. "Genesis"). Altered by the case setting
     * (see setBookCase())
     * 
     * @param book
     *            The book of the Bible
     * @return The full name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     */
    public String getLongName() throws NoSuchVerseException {
        return bibleNames.getLongName(this);
    }

    /**
     * Get the short name of a book (e.g. "Gen"). Altered by the case setting
     * (see setBookCase())
     * 
     * @param book
     *            The book of the Bible
     * @return The short name of the book
     * @exception NoSuchVerseException
     *                If the book is not valid
     */
    public String getShortName() throws NoSuchVerseException {
        return bibleNames.getShortName(this);
    }

    /* package */ BibleBook previous() {
        try {
            return BibleBook.books[ordinal() - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /* package */ BibleBook next() {
        try {
            return BibleBook.books[ordinal() + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get number of a book from its name.
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
                book = bibleNames.getBook(find);
            }

            if (book == null && englishBibleNames != null) {
                book = englishBibleNames.getBook(find);
            }
        }
        return book;
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

    public static BibleBook[] getBooks() {
        return books;
    }

    /**
     * Load up the resources for Bible book and section names.
     */
    private static void initialize() {
        Locale locale = Locale.getDefault();
        bibleNames = new BibleNames(locale);

        // If the locale is not the program's default get it for alternates
        if (!locale.getLanguage().equals(EN_LOCALE.getLanguage())) {
            englishBibleNames = new BibleNames(EN_LOCALE);
        }
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

    /** Localized BibleNames */
    private static BibleNames bibleNames;

    /** English BibleNames, or null when using the program's default locale */
    private static BibleNames englishBibleNames;

    /** The Locale of OSIS Names */
    private static final Locale EN_LOCALE = new Locale("en");

    static {
        for (BibleBook book : BibleBook.values()) {
            osisMap.put(BookName.normalize(book.getOSIS(), EN_LOCALE), book);
        }
        initialize();
    }

}
