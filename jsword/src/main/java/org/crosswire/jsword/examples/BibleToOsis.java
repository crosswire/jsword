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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.common.xml.XMLProcess;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * Start of a mechanism to extract a Bible module to OSIS.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BibleToOsis {
    /**
     * The name of a Bible to find
     */
    private static final String BIBLE_NAME = "KJV"; //$NON-NLS-1$
    private static final String BIBLE_RANGE = "Gen-Rev"; //$NON-NLS-1$
    private static final boolean BY_BOOK = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new BibleToOsis().dump(BIBLE_NAME, BIBLE_RANGE);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void dump(String name, String range) throws NoSuchKeyException, IOException, BookException {
        Books books = Books.installed();
        Book bible = books.getBook(name);
        BookMetaData bmd = bible.getBookMetaData();
        String lastBookName = ""; //$NON-NLS-1$
        int lastChapter = -1;
        StringBuffer buf = new StringBuffer();
        boolean inPreVerse = false;

        Key keys = bible.getKey(range);

        openOutputFile(bmd.getInitials(), !BY_BOOK);
        buildDocumentOpen(buf, bmd, range, !BY_BOOK);
        if (!BY_BOOK) {
            writeDocument(buf);
        }

        // Get a verse iterator
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            Verse verse = (Verse) iter.next();
            String raw = bible.getRawText(verse);
            String osisID = verse.getOsisID();
            String currentBookName = BibleInfo.getOSISName(verse.getBook());
            int currentChapter = verse.getChapter();

            boolean newBookFound = !lastBookName.equals(currentBookName);

            if (newBookFound) {
                if (lastBookName.length() > 0) {
                    if (currentChapter == 1) {
                        if (inPreVerse) {
                            buildPreVerseClose(buf);
                            inPreVerse = false;
                        }
                        buildChapterClose(buf);
                    }
                    buildBookClose(buf);
                    buildDocumentClose(buf, BY_BOOK);
                    openOutputFile(lastBookName, BY_BOOK);
                    writeDocument(buf);
                    closeOutputFile(BY_BOOK);
                }

                buf = new StringBuffer();
                buildDocumentOpen(buf, bmd, currentBookName, BY_BOOK);
                buildBookOpen(buf, currentBookName);
            }

            if (newBookFound || lastChapter != currentChapter) {
                if (currentChapter != 1) {
                    if (inPreVerse) {
                        buildPreVerseClose(buf);
                        inPreVerse = false;
                    }
                    buildChapterClose(buf);
                }
                buildChapterOpen(buf, currentBookName, currentChapter);
            }

            /* Output the verse */

            boolean foundPreVerse = false;
            String preVerseText = ""; //$NON-NLS-1$
            if (raw.indexOf(preVerseStart) != -1) {
                Matcher matcher = preVersePattern.matcher(raw);
                StringBuffer rawbuf = new StringBuffer();
                if (matcher.find()) {
                    foundPreVerse = true;
                    preVerseText = matcher.group(1);
                    matcher.appendReplacement(rawbuf, ""); //$NON-NLS-1$
                }
                matcher.appendTail(rawbuf);
                raw = rawbuf.toString();
            }

            boolean foundPsalmTitle = false;
            String psalmTitleText = ""; //$NON-NLS-1$
            if (raw.indexOf(psalmTitleStart) != -1) {
                Matcher matcher = psalmTitlePattern.matcher(raw);
                StringBuffer rawbuf = new StringBuffer();
                if (matcher.find()) {
                    foundPsalmTitle = true;
                    psalmTitleText = matcher.group(1);
                    matcher.appendReplacement(rawbuf, ""); //$NON-NLS-1$
                }
                matcher.appendTail(rawbuf);
                raw = rawbuf.toString();
            }

            if (foundPsalmTitle) {
                buildPsalmTitle(buf, psalmTitleText);
            }

            if (foundPreVerse && !preVerseText.equals(psalmTitleText)) {
                if (inPreVerse) {
                    buildPreVerseClose(buf);
                }
                buildPreVerseOpen(buf, preVerseText);
                inPreVerse = true;
            }

            buildVerseOpen(buf, osisID);
            buf.append(raw);
            buildVerseClose(buf, osisID);

            lastChapter = currentChapter;
            lastBookName = currentBookName;
        }

        // Close everything that is open
        if (inPreVerse) {
            buildPreVerseClose(buf);
            inPreVerse = false;
        }

        buildChapterClose(buf);
        buildBookClose(buf);
        buildDocumentClose(buf, true);
        openOutputFile(lastBookName, BY_BOOK);
        writeDocument(buf);
        closeOutputFile(true);
    }

    private void buildDocumentOpen(StringBuffer buf, BookMetaData bmd, String range, boolean force) {
        if (!force) {
            return;
        }

        MessageFormat msgFormat = new MessageFormat(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<osis\n  xmlns=\"http://www.bibletechnologies.net/2003/OSIS/namespace\"\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n  xsi:schemaLocation=\"http://www.bibletechnologies.net/2003/OSIS/namespace http://www.bibletechnologies.net/osisCore.2.1.1.xsd\">\n<osisText osisIDWork=\"{0}\" osisRefWork=\"defaultReferenceScheme\" xml:lang=\"en\">\n<header>\n  <work osisWork=\"{0}\">\n    <title>{1}</title>\n    <identifier type=\"OSIS\">Bible.{0}</identifier>\n    <scope>{2}</scope>\n    <refSystem>Bible.KJV</refSystem>\n  </work>\n  <work osisWork=\"defaultReferenceScheme\">\n    <refSystem>Bible.KJV</refSystem>\n  </work>\n  <work osisWork=\"strong\">\n    <refSystem>Dict.Strongs</refSystem>\n  </work>\n  <work osisWork=\"robinson\">\n    <refSystem>Dict.Robinsons</refSystem>\n  </work>\n  <work osisWork=\"strongMorph\">\n    <refSystem>Dict.strongMorph</refSystem>\n  </work>\n</header>\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] {
                bmd.getInitials(), bmd.getName(), range
        }, buf, pos);
    }

    private void buildDocumentClose(StringBuffer buf, boolean force) {
        if (force) {
            buf.append("</osisText>\n</osis>\n"); //$NON-NLS-1$
        }
    }

    private void buildBookOpen(StringBuffer buf, String bookName) {
        System.err.println("processing " + bookName); //$NON-NLS-1$
        MessageFormat msgFormat = new MessageFormat("<div type=\"book\" osisID=\"{0}\" canonical=\"true\">\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] {
            bookName
        }, buf, pos);
    }

    private void buildBookClose(StringBuffer buf) {
        buf.append("</div>\n"); //$NON-NLS-1$
    }

    private void buildChapterClose(StringBuffer buf) {
        buf.append("</chapter>\n"); //$NON-NLS-1$
    }

    private void buildChapterOpen(StringBuffer buf, String bookName, int chapter) {
        MessageFormat msgFormat = new MessageFormat("<chapter osisID=\"{0}.{1}\" chapterTitle=\"{2} {1}.\">\n"); //$NON-NLS-1$
        if ("Obad".equals(bookName) || //$NON-NLS-1$
                "Phlm".equals(bookName) || //$NON-NLS-1$
                "2John".equals(bookName) || //$NON-NLS-1$
                "3John".equals(bookName) || //$NON-NLS-1$
                "Jude".equals(bookName)) //$NON-NLS-1$
        {
            return;
        }

        String chapterName = "CHAPTER"; //$NON-NLS-1$
        if ("Ps".equals(bookName)) //$NON-NLS-1$
        {
            chapterName = "PSALM"; //$NON-NLS-1$
        }

        msgFormat.format(new Object[] {
                bookName, new Integer(chapter), chapterName
        }, buf, pos);
    }

    private void buildPsalmTitle(StringBuffer buf, String psalmTitle) {
        MessageFormat msgFormat = new MessageFormat("<title type=\"psalm\" canonical=\"true\">{0}</title>"); //$NON-NLS-1$
        msgFormat.format(new Object[] {
            psalmTitle
        }, buf, pos);
    }

    // private void buildPsalmAcrostic(StringBuffer buf, String psalmTitle)
    // {
    //        MessageFormat msgFormat = new MessageFormat("<title type=\"acrostic\" canonical=\"true\">{0}</title>"); //$NON-NLS-1$
    // msgFormat.format(new Object[] { psalmTitle }, buf, pos);
    // }

    private void buildPreVerseOpen(StringBuffer buf, String preVerse) {
        MessageFormat msgFormat = new MessageFormat("<div type=\"section\" canonical=\"true\"><title canonical=\"true\">{0}</title>"); //$NON-NLS-1$
        msgFormat.format(new Object[] {
            preVerse
        }, buf, pos);
    }

    private void buildPreVerseClose(StringBuffer buf) {
        buf.append("</div>\n"); //$NON-NLS-1$
    }

    private void buildVerseOpen(StringBuffer buf, String osisID) {
        //        MessageFormat msgFormat = new MessageFormat("<verse sID=\"{0}\" osisID=\"{0}\"/>"); //$NON-NLS-1$
        MessageFormat msgFormat = new MessageFormat("<verse osisID=\"{0}\">"); //$NON-NLS-1$
        msgFormat.format(new Object[] {
            osisID
        }, buf, pos);
    }

    private void buildVerseClose(StringBuffer buf, String osisID) {
        //        MessageFormat msgFormat = new MessageFormat("<verse eID=\"{0}\"/>"); //$NON-NLS-1$
        MessageFormat msgFormat = new MessageFormat("</verse>\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] {
            osisID
        }, buf, pos);
    }

    private void openOutputFile(String newFilename, boolean open) throws IOException {
        if (open) {
            filename = newFilename;
            writer = new OutputStreamWriter(new FileOutputStream(filename + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void writeDocument(StringBuffer buf) throws IOException {
        writer.write(buf.toString());
    }

    private void closeOutputFile(boolean close) throws IOException {
        if (close) {
            writer.close();
            parse();
        }
    }

    private void parse() {
        XMLProcess parser = new XMLProcess();
        parser.getFeatures().setFeatureStates(new String[] {
                "-s", "-f", "-va", "-dv"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        parser.parse(filename + ".xml"); //$NON-NLS-1$
    }

    private static FieldPosition pos = new FieldPosition(0);

    private static String preVerseStart = "<title subtype=\"x-preverse\" type=\"section\">"; //$NON-NLS-1$
    private static String preVerseElement = "<title subtype=\"x-preverse\" type=\"section\">(.*?)</title>"; //$NON-NLS-1$
    private static Pattern preVersePattern = Pattern.compile(preVerseElement);
    //    private static String preVerseEnd = "</title>"; //$NON-NLS-1$
    // private static Pattern preVerseStartPattern =
    // Pattern.compile(preVerseStart);
    //    private static Pattern preVerseEndPattern = Pattern.compile(preVerseEnd); //$NON-NLS-1$

    private static String psalmTitleStart = "<title type=\"psalm\">"; //$NON-NLS-1$
    private static String psalmTitleElement = "<title type=\"psalm\">(.*?)</title>"; //$NON-NLS-1$
    private static Pattern psalmTitlePattern = Pattern.compile(psalmTitleElement);
    //    private static String psalmTitleEnd = "</title>"; //$NON-NLS-1$
    // private static Pattern psalmTitleStartPattern =
    // Pattern.compile(psalmTitleStart);
    // private static Pattern psalmTitleEndPattern =
    // Pattern.compile(psalmTitleEnd);

    private Writer writer;
    private String filename;
}
