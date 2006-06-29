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
 * ID: $Id: APIExamples.java 1046 2006-03-12 21:31:48 -0500 (Sun, 12 Mar 2006) dmsmith $
 */
package org.crosswire.jsword.examples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;

import org.crosswire.common.util.StringUtil;
import org.crosswire.common.xml.XMLProcess;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;

/**
 * Start of a mechanism to extract a Bible module to OSIS.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BibleToOsis
{
    /**
     * The name of a Bible to find
     */
    private static final String BIBLE_NAME = "KJV"; //$NON-NLS-1$
    private static final String BIBLE_RANGE = "Gen-Rev"; //$NON-NLS-1$
    private static final boolean BY_BOOK = false;

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new BibleToOsis().dump(BIBLE_NAME, BIBLE_RANGE);
    }

    public void dump(String name, String range)
    {
        Books books = Books.installed();
        Book bible = books.getBook(name);
        BookMetaData bmd = bible.getBookMetaData();
        String lastBookName = ""; //$NON-NLS-1$
        int lastChapter = -1;
        StringBuffer buf = new StringBuffer();
        boolean inPreVerse = false;

        try
        {
            Key keys = bible.getKey(range);

            openOutputFile(bmd.getInitials(), !BY_BOOK);
            buildDocumentOpen(buf, bmd, range, !BY_BOOK);
            if (!BY_BOOK)
            {
                writeDocument(buf);
            }

            // Get a verse iterator
            Iterator iter = keys.iterator();
            while (iter.hasNext())
            {
                Verse verse = (Verse) iter.next();
                String raw = bible.getRawData(verse);
                String osisID = verse.getOsisID();
                Verse v = null;

                try
                {
                    v = VerseFactory.fromString(osisID);
                }
                catch (NoSuchVerseException e)
                {
                    // does not happen
                }

                String currentBookName = BibleInfo.getOSISName(verse.getBook());
                int currentChapter = verse.getChapter();

                boolean newBookFound = !lastBookName.equals(currentBookName);

                if (newBookFound)
                {
                    if (lastBookName.length() > 0)
                    {
                        if (currentChapter == 1)
                        {
                            if (inPreVerse)
                            {
                                buildPreVerseClose(buf);
                                inPreVerse = false;
                            }
                            buildChapterClose(buf);
                        }
                        buildBookClose(buf, lastBookName);
                        buildDocumentClose(buf, BY_BOOK);
                        openOutputFile(lastBookName, BY_BOOK);
                        writeDocument(buf);
                        closeOutputFile(BY_BOOK);
                    }

                    buf = new StringBuffer();
                    buildDocumentOpen(buf, bmd, currentBookName, BY_BOOK);
                    buildBookOpen(buf, currentBookName);
                }

                if (newBookFound || lastChapter != currentChapter)
                {
                    if (currentChapter != 1)
                    {
                        if (inPreVerse)
                        {
                            buildPreVerseClose(buf);
                            inPreVerse = false;
                        }
                        buildChapterClose(buf);
                    }
                    buildChapterOpen(buf, currentBookName, currentChapter);
                }

                /* Output the verse */

                /*
                 * If the "raw" verse contains a "preverse" pull it out.
                 * If there were a former preverse then close the "section" div
                 * before outputting it before the verse.
                 */
                if (osisID.equals("Ps.132.1")) //$NON-NLS-1$
                {
                    raw = replace(raw, "<w lemma=\"x-Strongs:H07892\">A Song</w> <w lemma=\"x-Strongs:H04609\">of degrees</w>. ", //$NON-NLS-1$
                                      "<title type=\"psalm\"><w lemma=\"x-Strongs:H07892\">A Song</w> <w lemma=\"x-Strongs:H04609\">of degrees</w>.</title>"); //$NON-NLS-1$
                }

                boolean foundPreVerse = false;
                String preVerseText = ""; //$NON-NLS-1$
                if (raw.indexOf(preVerseStart) != -1)
                {
                    Matcher matcher = preVersePattern.matcher(raw);
                    StringBuffer rawbuf = new StringBuffer();
                    if (matcher.find())
                    {
                        foundPreVerse = true;
                        preVerseText = matcher.group(1);
                        matcher.appendReplacement(rawbuf, ""); //$NON-NLS-1$
                    }
                    matcher.appendTail(rawbuf);
                    raw = rawbuf.toString();
                }

                boolean foundPsalmTitle = false;
                String psalmTitleText = ""; //$NON-NLS-1$
                if (raw.indexOf(psalmTitleStart) != -1)
                {
                    Matcher matcher = psalmTitlePattern.matcher(raw);
                    StringBuffer rawbuf = new StringBuffer();
                    if (matcher.find())
                    {
                        foundPsalmTitle = true;
                        psalmTitleText = matcher.group(1);
                        matcher.appendReplacement(rawbuf, ""); //$NON-NLS-1$
                    }
                    matcher.appendTail(rawbuf);
                    raw = rawbuf.toString();
                }

                if (foundPsalmTitle)
                {
                    buildPsalmTitle(buf, cleanup(osisID, psalmTitleText, false)); 
                }

                if (foundPreVerse && !preVerseText.equals(psalmTitleText))
                {
                    if (inPreVerse)
                    {
                        buildPreVerseClose(buf);
                    }
                    buildPreVerseOpen(buf, cleanup(osisID, preVerseText, false)); 
                    inPreVerse = true;
                }

                // There is a bug in the KJV where NT book titles are at the end of the prior book
                // And they contain junk!
                if (SwordConstants.getTestament(v) == SwordConstants.TESTAMENT_NEW)
                {
                    if (raw.indexOf("<title") != -1) //$NON-NLS-1$
                    {
                        Pattern p = Pattern.compile("<title.*?>(.*?)</title>"); //$NON-NLS-1$
                        Matcher matcher = p.matcher(raw);
                        StringBuffer rawbuf = new StringBuffer();
                        while (matcher.find())
                        {
                            matcher.appendReplacement(rawbuf, ""); //$NON-NLS-1$
                        }
                        matcher.appendTail(rawbuf);
                        raw = rawbuf.toString();
                    }
                }

                buildVerseOpen(buf, osisID);
                buf.append(cleanup(osisID, raw, true));
                buildVerseClose(buf, osisID);

                lastChapter = currentChapter;
                lastBookName = currentBookName;
            }

            // Close everything that is open
            if (inPreVerse)
            {
                buildPreVerseClose(buf);
                inPreVerse = false;
            }

            buildChapterClose(buf);
            buildBookClose(buf, lastBookName);
            buildDocumentClose(buf, true);
            openOutputFile(lastBookName, BY_BOOK);
            writeDocument(buf);
            closeOutputFile(true);
        }
        catch (BookException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchVerseException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchKeyException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Convert a string containing URL escape sequences to UTF-8.
     * This is needed because the KJV strongsMarkup notes contain URL escapes.
     * This routine is borrowed from http://www.w3.org/International/unescape.java
     */
    public static String unescape(String s)
    {
        StringBuffer sbuf = new StringBuffer();
        int l = s.length();
        int ch = -1;
        int b = 0;
        int sumb = 0;
        int i = 0;
        int more = -1;
        for (i = 0; i < l; i++)
        {
            /* Get next byte b from URL segment s */
            ch = s.charAt(i);
            switch (ch)
            {
              case '%':
                ch = s.charAt(++i);
                int hb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                ch = s.charAt(++i);
                int lb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                b = (hb << 4) | lb;
                break;
              case '+':
                b = ' ';
                break;
              default:
                b = ch;
            }
            /* Decode byte b as UTF-8, sumb collects incomplete chars */
            if ((b & 0xc0) == 0x80)
            { // 10xxxxxx (continuation byte)
                sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
                if (--more == 0)
                {
                    sbuf.append((char) sumb); // Add char to sbuf
                }
            }
            else if ((b & 0x80) == 0x00)
            { // 0xxxxxxx (yields 7 bits)
                sbuf.append((char) b); // Store in sbuf
            }
            else if ((b & 0xe0) == 0xc0)
            {
                // 110xxxxx (yields 5 bits)
                sumb = b & 0x1f;
                more = 1; // Expect 1 more byte
            }
            else if ((b & 0xf0) == 0xe0)
            {
                // 1110xxxx (yields 4 bits)
                sumb = b & 0x0f;
                more = 2; // Expect 2 more bytes
            }
            else if ((b & 0xf8) == 0xf0)
            {
                // 11110xxx (yields 3 bits)
                sumb = b & 0x07;
                more = 3; // Expect 3 more bytes
            }
            else if ((b & 0xfc) == 0xf8)
            {
                // 111110xx (yields 2 bits)
                sumb = b & 0x03;
                more = 4; // Expect 4 more bytes
            }
            else /* if ((b & 0xfe) == 0xfc) */
            {
                // 1111110x (yields 1 bit)
                sumb = b & 0x01;
                more = 5; // Expect 5 more bytes
            }
            /* We don't test if the UTF-8 encoding is well-formed */
        }
        return sbuf.toString();
    }

    private void buildDocumentOpen(StringBuffer buf, BookMetaData bmd, String range, boolean force)
    {
        if (!force)
        {
            return;
        }

        StringBuffer docBuffer = new StringBuffer();
        docBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"); //$NON-NLS-1$
        docBuffer.append("\n<osis"); //$NON-NLS-1$
        docBuffer.append("\n  xmlns=\"http://www.bibletechnologies.net/2003/OSIS/namespace\""); //$NON-NLS-1$
        docBuffer.append("\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");  //$NON-NLS-1$
        docBuffer.append("\n  xsi:schemaLocation=\"http://www.bibletechnologies.net/2003/OSIS/namespace osisCore.2.1.xsd\">"); //$NON-NLS-1$
        docBuffer.append("\n<osisText osisIDWork=\"{0}\" osisRefWork=\"defaultReferenceScheme\" xml:lang=\"en\">"); //$NON-NLS-1$
        docBuffer.append("\n<header>"); //$NON-NLS-1$
        docBuffer.append("\n  <work osisWork=\"{0}\">"); //$NON-NLS-1$
        docBuffer.append("\n    <title>{1}</title>"); //$NON-NLS-1$
        docBuffer.append("\n    <identifier type=\"OSIS\">Bible.{0}</identifier>"); //$NON-NLS-1$
        docBuffer.append("\n    <scope>{2}</scope>"); //$NON-NLS-1$
        docBuffer.append("\n    <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n  </work>"); //$NON-NLS-1$
        docBuffer.append("\n  <work osisWork=\"defaultReferenceScheme\">"); //$NON-NLS-1$
        docBuffer.append("\n    <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n  </work>"); //$NON-NLS-1$
        docBuffer.append("\n  <work osisWork=\"strong\">"); //$NON-NLS-1$
        docBuffer.append("\n    <refSystem>Dict.Strongs</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n  </work>"); //$NON-NLS-1$
        docBuffer.append("\n  <work osisWork=\"robinson\">"); //$NON-NLS-1$
        docBuffer.append("\n    <refSystem>Dict.Robinsons</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n  </work>"); //$NON-NLS-1$
        docBuffer.append("\n  <work osisWork=\"strongMorph\">"); //$NON-NLS-1$
        docBuffer.append("\n    <refSystem>Dict.strongMorph</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n  </work>"); //$NON-NLS-1$
        docBuffer.append("\n</header>"); //$NON-NLS-1$
        docBuffer.append('\n');
        MessageFormat msgFormat = new MessageFormat(docBuffer.toString()); 
        msgFormat.format(new Object[] { bmd.getInitials(), bmd.getName(), range }, buf, pos);
    }

    private void buildDocumentClose(StringBuffer buf, boolean force)
    {
        if (force)
        {
            buf.append("</osisText>\n</osis>\n"); //$NON-NLS-1$
        }
    }

    private void buildBookOpen(StringBuffer buf, String bookName)
    {
        System.err.println("processing " + bookName); //$NON-NLS-1$
        MessageFormat msgFormat = new MessageFormat("<div type=\"book\" osisID=\"{0}\" canonical=\"true\">\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] { bookName}, buf, pos);

        MessageFormat titleFormat = new MessageFormat("<title type=\"main\">{0}</title>\n"); //$NON-NLS-1$
        String title = (String) bookTitles.get(bookName);
        if (title != null)
        {
            titleFormat.format(new Object[] { title }, buf, pos);
        }
    }

    private void buildBookClose(StringBuffer buf, String bookName)
    {
        String colophon = (String) colophons.get(bookName);
        if (colophon != null)
        {
            buf.append(colophon);
        }
        buf.append("</div>\n"); //$NON-NLS-1$
    }

    private void buildChapterClose(StringBuffer buf)
    {
        buf.append("</chapter>\n"); //$NON-NLS-1$
    }

    private void buildChapterOpen(StringBuffer buf, String bookName, int chapter)
    {
        if (bookName.equals("Obad") || //$NON-NLS-1$
            bookName.equals("Phlm") || //$NON-NLS-1$
            bookName.equals("2John") || //$NON-NLS-1$
            bookName.equals("3John") || //$NON-NLS-1$
            bookName.equals("Jude")) //$NON-NLS-1$
        {
            MessageFormat msgSingleFormat = new MessageFormat("<chapter osisID=\"{0}.{1}\" chapterTitle=\"{2} {1}.\">\n"); //$NON-NLS-1$
            msgSingleFormat.format(new Object[] { bookName, new Integer(chapter) }, buf, pos);
            return;
        }

        String chapterName = "CHAPTER"; //$NON-NLS-1$
        if (bookName.equals("Ps")) //$NON-NLS-1$
        {
            chapterName = "PSALM"; //$NON-NLS-1$
        }

        MessageFormat msgFormat = new MessageFormat("<chapter osisID=\"{0}.{1}\" chapterTitle=\"{2} {1}.\">\n<title type=\"chapter\">{2} {1}.</title>\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] { bookName, new Integer(chapter), chapterName }, buf, pos);
    }

    private void buildPsalmTitle(StringBuffer buf, String psalmTitle)
    {
        MessageFormat msgFormat = new MessageFormat("<title type=\"psalm\" canonical=\"true\">{0}</title>"); //$NON-NLS-1$
        msgFormat.format(new Object[] { psalmTitle }, buf, pos);
    }

//    private void buildPsalmAcrostic(StringBuffer buf, String psalmTitle)
//    {
//        MessageFormat msgFormat = new MessageFormat("<title type=\"acrostic\" canonical=\"true\">{0}</title>"); //$NON-NLS-1$
//        msgFormat.format(new Object[] { psalmTitle }, buf, pos);
//    }

    private void buildPreVerseOpen(StringBuffer buf, String preVerse)
    {
        MessageFormat msgFormat = new MessageFormat("<div type=\"section\" canonical=\"true\"><title canonical=\"true\">{0}</title>"); //$NON-NLS-1$
        msgFormat.format(new Object[] { preVerse }, buf, pos);
    }

    private void buildPreVerseClose(StringBuffer buf)
    {
        buf.append("</div>\n"); //$NON-NLS-1$
    }

    private void buildVerseOpen(StringBuffer buf, String osisID)
    {
//        MessageFormat msgFormat = new MessageFormat("<verse sID=\"{0}\" osisID=\"{0}\"/>"); //$NON-NLS-1$
        MessageFormat msgFormat = new MessageFormat("<verse osisID=\"{0}\">"); //$NON-NLS-1$
        msgFormat.format(new Object[] { osisID }, buf, pos);
    }

    private void buildVerseClose(StringBuffer buf, String osisID)
    {
//        MessageFormat msgFormat = new MessageFormat("<verse eID=\"{0}\"/>"); //$NON-NLS-1$
        MessageFormat msgFormat = new MessageFormat("</verse>\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] { osisID }, buf, pos);
    }

    private void openOutputFile(String newFilename, boolean open) throws IOException
    {
        if (open)
        {
            filename = newFilename;
            writer = new OutputStreamWriter(new FileOutputStream(filename + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void writeDocument(StringBuffer buf) throws IOException
    {
        writer.write(buf.toString());
    }

    private void closeOutputFile(boolean close) throws IOException
    {
        if (close)
        {
            writer.close();
            parse();
        }
    }

    private void parse()
    {
        XMLProcess parser = new XMLProcess();
        parser.getFeatures().setFeatureStates(new String[] {"-s", "-f", "-va", "-dv"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        parser.parse(filename + ".xml"); //$NON-NLS-1$
    }

    private String cleanup(String osisID, String input, boolean inVerse)
    {
        String acrostic = (String) acrostics.get(osisID);
        if (acrostic != null)
        {
            MessageFormat msgFormat = new MessageFormat("<title type=\"acrostic\" canonical=\"true\">{0}</title>"); //$NON-NLS-1$
            input = input.replaceFirst((String) hebLetters.get(osisID), ""); //$NON-NLS-1$ 
            input = msgFormat.format(new Object[] { acrostic }) + input;
        }

        // Fix up bad notes
        if (input.indexOf("note type=\"strongsMarkup\"") != -1) //$NON-NLS-1$
        {
            MessageFormat noteCleanupFormat = new MessageFormat("<note type=\"x-strongsMarkup\" resp=\"{0} {1}\">{2}</note>"); //$NON-NLS-1$
            Matcher matcher = badNotePattern.matcher(input);
            StringBuffer buf = new StringBuffer();
            while (matcher.find())
            {
                String content = XMLUtil.escape(unescape(matcher.group(4)));
                String replacement = noteCleanupFormat.format(new Object[] { matcher.group(2), matcher.group(3), content});
                matcher.appendReplacement(buf, replacement);
            }
            matcher.appendTail(buf);
            input = buf.toString();
        }

        if (input.indexOf("<resp") != -1) //$NON-NLS-1$
        {
            Matcher matcher = respPattern.matcher(input);
            StringBuffer buf = new StringBuffer();
            while (matcher.find())
            {
                matcher.appendReplacement(buf, "<milestone type=\"x-strongsMarkup\" resp=\"$1 $2\"/>"); //$NON-NLS-1$
            }
            matcher.appendTail(buf);
            input = buf.toString();
        }

        // Add in missing w
        if (osisID.equals("1Cor.16.24")) //$NON-NLS-1$
        {
            input += "<w src=\"15\" lemma=\"strong:G575\" morph=\"robinson:PREP\"></w><w src=\"11\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w><w src=\"12\" lemma=\"strong:G2881\" morph=\"robinson:A-APM\"></w>"; //$NON-NLS-1$
        }

        if (osisID.equals("2Cor.13.14")) //$NON-NLS-1$
        {
            input += "<w src=\"26\" lemma=\"strong:G575\" morph=\"robinson:PREP\"></w><w src=\"22\" lemma=\"strongs:G4314\" morph=\"robinson:PREP\"></w>"; //$NON-NLS-1$
        }

        if (osisID.equals("1Thess.1.8")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"25\" lemma=\"strong:G4314\" morph=\"robinson:PREP\">to</w> <w src=\"26 27\" lemma=\"strong:G3588 strong:G2316\" morph=\"robinson:T-ASM robinson:N-ASM\">God</w>-<w src=\"25\" lemma=\"strong:G4314\" morph=\"robinson:PREP\" type=\"x-split\" subType=\"x-36\">ward</w>", //$NON-NLS-1$
                                  "<w src=\"25 26 27\" lemma=\"strong:G4314 strong:G3588 strong:G2316\" morph=\"robinson:PREP robinson:T-ASM robinson:N-ASM\">to God-ward</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("1Thess.5.28")) //$NON-NLS-1$
        {
            input += "<w src=\"11\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w><w src=\"12\" lemma=\"strong:G2331\" morph=\"robinson:N-APM\"></w>"; //$NON-NLS-1$
        }

        Set before = new TreeSet();

        // Fix up bad w tags
        Matcher wMatcher = wPattern.matcher(input);
        while (wMatcher.find())
        {
            String whole = wMatcher.group(0);
            String fixed = whole.replaceAll(" (src|w|morph)(?=[^=])", " "); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("\\|", " "); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("x-Strongs", "strong"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("x-Robinson", "robinson"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("split(ID|id)=\"", "type=\"x-split\" subType=\"x-"); //$NON-NLS-1$ //$NON-NLS-2$
            if (!whole.equals(fixed))
            {
                input = replace(input, whole, fixed); 
            }

            Matcher srcMatcher = srcPattern.matcher(fixed);
            while (srcMatcher.find())
            {
                Integer src = new Integer(srcMatcher.group(1));
                before.add(src);
            }
        }

        // Check input to make sure that there are no gaps.
        // That every number from 1 to n is present, where n is the largest for the verse.
        int i = 1;
        Iterator iter = before.iterator();
        while (iter.hasNext())
        {
            Integer number = (Integer) iter.next();
            int j = number.intValue();
            if (i != j)
            {
                for ( ; i < j; i++)
                {
                    System.out.println(osisID + " missing src=" + i); //$NON-NLS-1$
                }
            }
            i++;
        }

        input = input.replaceAll("changeType=\"", "type=\""); //$NON-NLS-1$ //$NON-NLS-2$

        if (osisID.startsWith("Ps")) //$NON-NLS-1$
        {
            Matcher matcher = transChangeSegPattern.matcher(input);
            StringBuffer buf = null;
            while (matcher.find())
            {
                if (buf == null)
                {
                    buf = new StringBuffer();
                }
                matcher.appendReplacement(buf, "<transChange type=\"added\">$1</transChange>"); //$NON-NLS-1$
            }
            if (buf != null)
            {
                matcher.appendTail(buf);
                input = buf.toString();
            }
        }
        input = input.replaceAll("\"transChange\"", "\"x-transChange\""); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("type:", "x-"); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll(" type=\"x-yhwh\"", ""); //$NON-NLS-1$ //$NON-NLS-2$

//        if (input.indexOf(transSegStart) != -1)
//        {
//            Matcher transSegStartMatcher = transSegStartPattern.matcher(input);
//            if (transSegStartMatcher.find())
//            {
//                int start = transSegStartMatcher.start();
//                Matcher transSegEndMatcher = transSegEndPattern.matcher(input);
//                if (transSegEndMatcher.find(1 + transSegStartMatcher.end()))
//                {
//                    int end = transSegEndMatcher.end();
//                    String transSegText = input.substring(start, end);
//                    transSegText = transSegText.substring(transSegStart.length(), transSegText.length() - transSegEnd.length());
////                    if (transSegText.indexOf('<') != -1 || transSegText.indexOf('>') != -1)
//                    {
//                        System.out.println(osisID + " found transseg " + transSegText + "\n\t" + orig); //$NON-NLS-1$ //$NON-NLS-2$
//                    }
//                }
//            }
//        }

        input = input.replaceAll("x-StudyNote", "study"); //$NON-NLS-1$ //$NON-NLS-2$

//        // normalize paragraph markers and move them from the end of a verse to the beginning of the next
//        input = input.replaceAll("<milestone type=\"x-p\"\\s*/>", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
//        String beforeChange = input;
//        input = input.replaceAll("<p/>(?=<note)", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
//        input = input.replaceAll("<p/>(?=<milestone type=\"x-strongsMarkup\")", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
//        input = input.replaceAll("<p/>(?=$)", "<milestone type=\"x-p\" subType=\"x-end\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
//        if (!beforeChange.equals(input) || osisID.equals("Matt.2.10")) //$NON-NLS-1$
//        {
//            System.err.println(osisID + "\nbefore: " + beforeChange + "\nafter: " + input); //$NON-NLS-1$ //$NON-NLS-2$
//        }
////        input = input.replaceAll("<p/>", "<milestone type=\"x-extra-p\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
//        if (input.indexOf("<milestone type=\"x-p\" marker=\"\u00B6\"/>") != -1) //$NON-NLS-1$
//        {
//            input = input.replaceAll("<milestone type=\"x-p\" marker=\"\u00B6\"/>", ""); //$NON-NLS-1$ //$NON-NLS-2$
//            moveP = true;
////            System.err.println(osisID + " remove \u00b6"); //$NON-NLS-1$
//        }
//        else if (moveP && inVerse)
//        {
//            input = "<milestone type=\"x-p\" marker=\"\u00B6\"/>" + input; //$NON-NLS-1$
//            moveP = false;
//        }
//
        // # is used in a note for a greek strong's #
        input = input.replace('#', 'G');
        // used in a note as a quotation mark at the beginning of a word. i.e. `not'
        input = input.replace('`', '\'');
        // used in notes as a space
        input = input.replace('_', ' ');
        // used in notes to indicate italics. These are incomplete GBF codes.
        input = input.replaceAll("[{][Ff][iI][}]", ""); //$NON-NLS-1$ //$NON-NLS-2$
        // found an email address in a note
        input = replace(input, "@hotmail.", "at hotmail dot "); //$NON-NLS-1$ //$NON-NLS-2$

        if (osisID.equals("Exod.32.32")) //$NON-NLS-1$
        {
            input = replace(input, "<w morph=\"strongMorph:TH8798\" lemma=\"strong:H04229\">--; ", //$NON-NLS-1$
                                  "\u2015; <w morph=\"strongMorph:TH8798\" lemma=\"strong:H04229\">"); //$NON-NLS-1$
        }

        if (osisID.equals("Ezek.26.16")) //$NON-NLS-1$
        {
            input = replace(input, "\\pa", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.5.30")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G846\" morph=\"robinson:P-ASF\">if</w>", //$NON-NLS-1$
                                  "<w src=\"10\" lemma=\"strong:G846\" morph=\"robinson:P-ASF\">it</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.16.17")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G920\" morph=\"robinson:ARAM\">Bar</w><w src=\"11\" lemma=\"strong:G920\" morph=\"robinson:ARAM\">jona</w>", //$NON-NLS-1$
                                  "<w src=\"10 11\" lemma=\"strong:G920\" morph=\"robinson:ARAM\">Bar\u2013jona</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.24.38")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"18\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\"><w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-DPF\">that</w></w>", //$NON-NLS-1$
                                  "<w src=\"18\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\"></w><w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-DPF\">that</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.18.28")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"19\" lemma=\"strong:G4155\" morph=\"robinson:V-IAI-3S\">and took <transChange type=\"added\">him</transChange> by the throat</w>", //$NON-NLS-1$
                                  "<w src=\"19\" lemma=\"strong:G4155\" morph=\"robinson:V-IAI-3S\">and took <seg type=\"x-transChange\" subType=\"x-added\">him</seg> by the throat</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.21.28")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"5\" lemma=\"strong:G444\" morph=\"robinson:N-NSM\">A <transChange type=\"added\">certain</transChange> man</w>", //$NON-NLS-1$
                                  "<w src=\"5\" lemma=\"strong:G444\" morph=\"robinson:N-NSM\">A <seg type=\"x-transChange\" subType=\"x-added\">certain</seg> man</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.21.31")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9\" lemma=\"strong:G3962\" morph=\"robinson:N-GSM\">of <transChange type=\"added\">his</transChange> father</w>", //$NON-NLS-1$
                                  "<w src=\"9\" lemma=\"strong:G3962\" morph=\"robinson:N-GSM\">of <seg type=\"x-transChange\" subType=\"x-added\">his</seg> father</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.22.6")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G5195\" morph=\"robinson:V-AAI-3P\">and entreated <transChange type=\"added\">them</transChange> spitefully</w>", //$NON-NLS-1$
                                  "<w src=\"8\" lemma=\"strong:G5195\" morph=\"robinson:V-AAI-3P\">and entreated <seg type=\"x-transChange\" subType=\"x-added\">them</seg> spitefully</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.23.4")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"19\" lemma=\"strong:G2309\" morph=\"robinson:V-PAI-3P\">they <transChange type=\"added\">themselves</transChange> will</w>", //$NON-NLS-1$
                                  "<w src=\"19\" lemma=\"strong:G2309\" morph=\"robinson:V-PAI-3P\">they <seg type=\"x-transChange\" subType=\"x-added\">themselves</seg> will</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.25.37")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"17\" lemma=\"strong:G4222\" morph=\"robinson:V-AAI-1P\">gave <transChange type=\"added\">thee</transChange> drink</w>", //$NON-NLS-1$
                                  "<w src=\"17\" lemma=\"strong:G4222\" morph=\"robinson:V-AAI-1P\">gave <seg type=\"x-transChange\" subType=\"x-added\">thee</seg> drink</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.25.38")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"7\" lemma=\"strong:G4863\" morph=\"robinson:V-2AAI-1P\">took <transChange type=\"added\">thee</transChange> in</w>", //$NON-NLS-1$
                                  "<w src=\"7\" lemma=\"strong:G4863\" morph=\"robinson:V-2AAI-1P\">took <seg type=\"x-transChange\" subType=\"x-added\">thee</seg> in</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.17")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"5\" lemma=\"strong:G106\" morph=\"robinson:A-GPN\">of the <transChange type=\"added\">feast of</transChange> unleavened bread</w>", //$NON-NLS-1$
                                  "<w src=\"5\" lemma=\"strong:G106\" morph=\"robinson:A-GPN\">of the <seg type=\"x-transChange\" subType=\"x-added\">feast of</seg> unleavened bread</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.45")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"15\" lemma=\"strong:G373\" morph=\"robinson:V-PMI-2P\">take <transChange type=\"added\">your</transChange> rest</w>", //$NON-NLS-1$
                                  "<w src=\"15\" lemma=\"strong:G373\" morph=\"robinson:V-PMI-2P\">take <seg type=\"x-transChange\" subType=\"x-added\">your</seg> rest</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.67")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G4474\" morph=\"robinson:V-AAI-3P\">smote <transChange type=\"added\">him</transChange> with the palms of their hands</w>", //$NON-NLS-1$
                                  "<w src=\"12\" lemma=\"strong:G4474\" morph=\"robinson:V-AAI-3P\">smote <seg type=\"x-transChange\" subType=\"x-added\">him</seg> with the palms of their hands</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.69")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"9\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">and</w></w>", //$NON-NLS-1$
                                  "<w src=\"2 9\" lemma=\"strong:G1161 strong:G2532\" morph=\"robinson:CONJ\">and</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.10.27")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\"><w src=\"9\" lemma=\"strong:G102\" morph=\"robinson:A-NSN\">it is</transChange> impossible</w>", //$NON-NLS-1$
                                  "<transChange type=\"added\">it is</transChange> <w src=\"9\" lemma=\"strong:G102\" morph=\"robinson:A-NSN\">impossible</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.9.36")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"13\" lemma=\"strong:G4601\" morph=\"robinson:V-AAI-3P\">kept <transChange type=\"added\">it</transChange> close</w>", //$NON-NLS-1$
                                  "<w src=\"13\" lemma=\"strong:G4601\" morph=\"robinson:V-AAI-3P\">kept <seg type=\"x-transChange\" subType=\"x-added\">it</seg> close</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.11.19")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G1544\" morph=\"robinson:V-PAI-3P\" type=\"x-split\" subType=\"x-21\">cast <transChange type=\"added\">them</transChange> out</w>", //$NON-NLS-1$
                                  "<w src=\"14\" lemma=\"strong:G1544\" morph=\"robinson:V-PAI-3P\" type=\"x-split\" subType=\"x-21\">cast <seg type=\"x-transChange\" subType=\"x-added\">them</seg> out</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.12.13")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S\"><w src=\"9\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAM-2S\">speak</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 9\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S robinson:V-2AAM-2S\">speak</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.14.21")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"26\" lemma=\"strong:G3588\" morph=\"robinson:T-GSF\"><w src=\"22\" lemma=\"strong:G3588\" morph=\"robinson:T-APF\"></w></w>", //$NON-NLS-1$
                                  "<w src=\"26 22\" lemma=\"strong:G3588\" morph=\"robinson:T-GSF robinson:T-APF\"></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.14.25")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S\"></w><w src=\"5\" lemma=\"strong:G4183\" morph=\"robinson:A-NPM\"></w><w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
            input = replace(input, "An</w>d</w> <w morph=\"robinson:V-INI-3P\" src=\"1\" lemma=\"strong:G4848\">there went</w>", //$NON-NLS-1$
                                  "And </w> <w morph=\"robinson:V-INI-3P\" src=\"1\" lemma=\"strong:G4848\" type=\"x-split\" subType=\"x-1\">there went</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:A-NPM\" src=\"5\" lemma=\"strong:G4183\">g<w src=\"4\" lemma=\"strong:G3793\" morph=\"robinson:N-NPM\">reat</w> <w morph=\"robinson:N-NPM\" src=\"4\" lemma=\"strong:G3793\">mu</w>ltitud<w src=\"1\" lemma=\"strong:G4848\" morph=\"robinson:V-INI-3P\"></w>e<w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-DSM\">s</w> </w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:A-NPM\" src=\"5\" lemma=\"strong:G4183\">great</w> <w morph=\"robinson:N-NPM\" src=\"4\" lemma=\"strong:G3793\">multitudes</w> "); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:P-DSM\" src=\"3\" lemma=\"strong:G846\">w<w src=\"6\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">ith</w> him<w src=\"7\" lemma=\"strong:G4762\" morph=\"robinson:V-2APP-NSM\"></w>: <w morph=\"robinson:CONJ\" src=\"6\" lemma=\"strong:G2532\">an</w>d<w src=\"10\" lemma=\"strong:G846\" morph=\"robinson:P-APM\"></w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-INI-3P\" src=\"1\" lemma=\"strong:G4848\" type=\"x-split\" subType=\"x-1\">with</w> <w morph=\"robinson:P-DSM\" src=\"3\" lemma=\"strong:G846\">him</w>: <w morph=\"robinson:CONJ\" src=\"6\" lemma=\"strong:G2532\">and</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:V-2APP-NSM\" src=\"7\" lemma=\"strong:G4762\">he turned<w src=\"9\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-2APP-NSM\" src=\"7\" lemma=\"strong:G4762\">he turned</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.14.35")) //$NON-NLS-1$
        {
            input = replace(input, "<w morph=\"robinson:CONJ\" src=\"1\" lemma=\"strong:G3777\"><w src=\"1\" lemma=\"strong:G3777\" morph=\"robinson:CONJ\"></w>neither</w>", //$NON-NLS-1$
                                  "<w src=\"1\" lemma=\"strong:G3777\" morph=\"robinson:CONJ\">neither</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:V-PAM-3S\" src=\"16\" lemma=\"strong:G191\">l<w src=\"16\" lemma=\"strong:G191\" morph=\"robinson:V-PAM-3S\"></w>et him hear</w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-PAM-3S\" src=\"16\" lemma=\"strong:G191\">let him hear</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"2\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"></w><w src=\"3\" lemma=\"strong:G1093\" morph=\"robinson:N-ASF\"></w><w src=\"4\" lemma=\"strong:G3777\" morph=\"robinson:CONJ\"></w><w src=\"5\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"></w><w src=\"6\" lemma=\"strong:G2874\" morph=\"robinson:N-ASF\"></w><w src=\"7\" lemma=\"strong:G2111\" morph=\"robinson:A-NSN\"></w><w src=\"8\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\"></w><w src=\"9\" lemma=\"strong:G1854\" morph=\"robinson:ADV\"></w><w src=\"10\" lemma=\"strong:G906\" morph=\"robinson:V-PAI-3P\"></w><w src=\"11\" lemma=\"strong:G846\" morph=\"robinson:P-ASN\"></w><w src=\"12\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\"></w><w src=\"13\" lemma=\"strong:G2192\" morph=\"robinson:V-PAP-NSM\"></w><w src=\"14\" lemma=\"strong:G3775\" morph=\"robinson:N-APN\"></w><w src=\"15\" lemma=\"strong:G191\" morph=\"robinson:V-PAN\"></w>", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.15.24")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"16\" lemma=\"strong:G756\" morph=\"robinson:V-ADI-3P\"></w><w src=\"14\" lemma=\"strong:G2147\" morph=\"robinson:V-API-3S\"></w><w src=\"9\" lemma=\"strong:G326\" morph=\"robinson:V-AAI-3S\"></w><w src=\"3\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\"></w><w morph=\"robinson:T-NSM\" src=\"3\" lemma=\"strong:G3588\"></w><w morph=\"robinson:CONJ\" src=\"1\" lemma=\"strong:G3754\"></w>F", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
            input = replace(input, "<w src=\"2\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">or</w> </w>", //$NON-NLS-1$
                                  "<w src=\"1\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">For</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:D-NSM\" src=\"2\" lemma=\"strong:G3778\">th<w src=\"5\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\">i<w src=\"4\" lemma=\"strong:G5207\" morph=\"robinson:N-NSM\"></w>s</w> </w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:D-NSM\" src=\"2\" lemma=\"strong:G3778\">this</w><w morph=\"robinson:T-NSM\" src=\"3\" lemma=\"strong:G3588\"></w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:P-1GS\" src=\"5\" lemma=\"strong:G3450\">my<w src=\"7\" lemma=\"strong:G2258\" morph=\"robinson:V-IXI-3S\"></w> <w src=\"6\" lemma=\"strong:G3498\" morph=\"robinson:A-NSM\"></w><w morph=\"robinson:N-NSM\" src=\"4\" lemma=\"strong:G5207\">son</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-1GS\" src=\"5\" lemma=\"strong:G3450\">my</w> <w morph=\"robinson:N-NSM\" src=\"4\" lemma=\"strong:G5207\">son</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"8\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\"><w morph=\"robinson:V-IXI-3S\" src=\"7\" lemma=\"strong:G2258\"><w src=\"10\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">wa</w></w>s</w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-IXI-3S\" src=\"7\" lemma=\"strong:G2258\">was</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:V-AAI-3S\" src=\"9\" lemma=\"strong:G326\">is a<w src=\"12\" lemma=\"strong:G2258\" morph=\"robinson:V-IXI-3S\">li<w src=\"11\" lemma=\"strong:G622\" morph=\"robinson:V-2RAP-NSM\"></w>ve a</w>ga<w src=\"13\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">i<w src=\"15\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">n</w></w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-AAI-3S\" src=\"9\" lemma=\"strong:G326\">is alive again</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:V-IXI-3S\" src=\"12\" lemma=\"strong:G2258\">he was</w> <w morph=\"robinson:CONJ\" src=\"10\" lemma=\"strong:G2532\"></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:CONJ\" src=\"10\" lemma=\"strong:G2532\"></w><w morph=\"robinson:V-IXI-3S\" src=\"12\" lemma=\"strong:G2258\">he was</w> "); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:V-API-3S\" src=\"14\" lemma=\"strong:G2147\">i<w src=\"17\" lemma=\"strong:G2165\" morph=\"robinson:V-PPN\">s found</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-API-3S\" src=\"14\" lemma=\"strong:G2147\">is found</w>"); //$NON-NLS-1$
            input = "<q who=\"Jesus\">" + input; //$NON-NLS-1$
        }

        if (osisID.equals("Luke.19.30")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"16\" lemma=\"strong:G4455\" morph=\"robinson:ADV\">never</w> <w src=\"15\" lemma=\"strong:G3762\" morph=\"robinson:A-NSM\"><w src=\"17\" lemma=\"strong:G444\" morph=\"robinson:N-GPM\">man</w></w>", //$NON-NLS-1$
                                  "<w src=\"15 16\" lemma=\"strong:G3762 strong:G4455\" morph=\"robinson:A-NSM robinson:ADV\">never</w> <w src=\"17\" lemma=\"strong:G444\" morph=\"robinson:N-GPM\">man</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("John.5.36")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G2491\" morph=\"robinson:N-GSM\">than <transChange type=\"added\">that</transChange> of John</w>", //$NON-NLS-1$
                                  "<w src=\"8\" lemma=\"strong:G2491\" morph=\"robinson:N-GSM\">than <seg type=\"x-transChange\" subType=\"x-added\">that</seg> of John</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Acts.17.25")) //$NON-NLS-1$
        {
            input = replace(input, "<w morph=\"robinson:A-APN\" src=\"16\" lemma=\"strong:G3956\"><w src=\"15\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things;</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:A-APN\" src=\"15 16\" lemma=\"strong:G3956\">all things;</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Acts.26.3")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"18\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\"><w morph=\"robinson:P-1GS\" src=\"19\" lemma=\"strong:G3450\">me</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\">me</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("2Cor.5.4")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"18\" lemma=\"strong:G3588\" morph=\"robinson:T-NSN\">might be <w src=\"22\" lemma=\"strong:G2222\" morph=\"robinson:N-GSF\"></w>swallowed up</w>", //$NON-NLS-1$
                                  "<w src=\"18\" lemma=\"strong:G3588\" morph=\"robinson:T-NSN\"></w><w src=\"22\" lemma=\"strong:G2222\" morph=\"robinson:N-GSF\">might be swallowed up</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("1Thess.1.6")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"17\" lemma=\"strong:G4151\" morph=\"robinson:N-GSN\"><w src=\"18\" lemma=\"strong:G40\" morph=\"robinson:A-GSN\">of the Holy</w> Ghost</w>", //$NON-NLS-1$
                                  "<w src=\"18\" lemma=\"strong:G40\" morph=\"robinson:A-GSN\">of the Holy</w> <w src=\"17\" lemma=\"strong:G4151\" morph=\"robinson:N-GSN\">Ghost</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("2Thess.1.11")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G2443\" morph=\"robinson:CONJ\">th<w src=\"2\" lemma=\"strong:G3739\" morph=\"robinson:R-ASN\"></w>at</w>", //$NON-NLS-1$
                                  "<w src=\"2 8\" lemma=\"strong:G3739 strong:G2443\" morph=\"robinson:R-ASN robinson:CONJ\">that</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Phlm.1.15")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"><w src=\"3\" lemma=\"strong:G1223\" morph=\"robinson:PREP\">for</w></w>", //$NON-NLS-1$
                                  "<w src=\"3 6\" lemma=\"strong:G1223 strong:G4314\" morph=\"robinson:PREP\">for</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.8.6")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G4537\" morph=\"robinson:V-AAS-3P\"><w morph=\"robinson:V-AAS-3P\" src=\"13\" lemma=\"strong:G4537\">sound.</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G4537\" morph=\"robinson:V-AAS-3P\">sound.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.11.1")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"19\" lemma=\"strong:G3588\" morph=\"robinson:T-APM\"><w morph=\"robinson:T-APM\" src=\"23\" lemma=\"strong:G3588\">them that</w></w>", //$NON-NLS-1$
                                  "<w src=\"19 23\" lemma=\"strong:G3588\" morph=\"robinson:T-APM\">them that</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:V-PAP-APM\" src=\"24\" lemma=\"strong:G4352\"><w src=\"20\" lemma=\"strong:G4352\" morph=\"robinson:V-PAP-APM\">worship</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-PAP-APM\" src=\"20 24\" lemma=\"strong:G4352\">worship</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:P-DSM\" src=\"26\" lemma=\"strong:G846\"><w src=\"22\" lemma=\"strong:G846\" morph=\"robinson:P-DSM\">therein.</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-DSM\" src=\"22 26\" lemma=\"strong:G846\">therein.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.11.4")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"15\" lemma=\"strong:G2476\" morph=\"robinson:V-RAP-NPF\"><w morph=\"robinson:V-RAP-NPF\" src=\"16\" lemma=\"strong:G2476\">standing</w></w>", //$NON-NLS-1$
                                  "<w src=\"15 16\" lemma=\"strong:G2476\" morph=\"robinson:V-RAP-NPF\">standing</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.11.14")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G5035\" morph=\"robinson:ADV\"><w morph=\"robinson:ADV\" src=\"13\" lemma=\"strong:G5035\">quickly.</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G5035\" morph=\"robinson:ADV\">quickly.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.14.7")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"32\" lemma=\"strong:G5204\" morph=\"robinson:N-GPN\"><w morph=\"robinson:N-GPN\" src=\"33\" lemma=\"strong:G5204\">of waters.</w></w>", //$NON-NLS-1$
                                  "<w src=\"32 33\" lemma=\"strong:G5204\" morph=\"robinson:N-GPN\">of waters.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.14.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w morph=\"robinson:P-GSF\" src=\"42\" lemma=\"strong:G846\"><w src=\"40\" lemma=\"strong:G846\" morph=\"robinson:P-GSF\">her</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-GSF\" src=\"40 42\" lemma=\"strong:G846\">her</w>"); //$NON-NLS-1$
            input = replace(input, "<w morph=\"robinson:N-NPF\" src=\"41\" lemma=\"strong:G4718\"><w src=\"39\" lemma=\"strong:G4718\" morph=\"robinson:N-NPF\">grapes</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:N-NPF\" src=\"39 41\" lemma=\"strong:G4718\">grapes</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.19.14")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"16\" lemma=\"strong:G2513\" morph=\"robinson:A-ASN\"><w morph=\"robinson:A-ASN\" src=\"17\" lemma=\"strong:G2513\">clean.</w></w>", //$NON-NLS-1$
                                  "<w src=\"16 17\" lemma=\"strong:G2513\" morph=\"robinson:A-ASN\">clean.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.19.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"28\" lemma=\"strong:G3173\" morph=\"robinson:A-GPM\"><w morph=\"robinson:A-GPM\" src=\"29\" lemma=\"strong:G3173\">great.</w></w>", //$NON-NLS-1$
                                  "<w src=\"28 29\" lemma=\"strong:G3173\" morph=\"robinson:A-GPM\">great.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.21.13")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"16\" lemma=\"strong:G5140\" morph=\"robinson:A-NPM\"><w morph=\"robinson:A-NPM\" src=\"17\" lemma=\"strong:G5140\">three</w></w>", //$NON-NLS-1$
                                  "<w src=\"16 17\" lemma=\"strong:G5140\" morph=\"robinson:A-NPM\">three</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.1.9")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"20\" morph=\"robinson:N-ASM\" lemma=\"strong:G2446\"><w src=\"19\" lemma=\"strong:G2446\" morph=\"robinson:N-ASM\">Jordan</w></w>.", //$NON-NLS-1$
                                  "<w src=\"20\" morph=\"robinson:N-ASM\" lemma=\"strong:G2446\">Jordan</w>.<w src=\"19\" lemma=\"strong:G2446\" morph=\"robinson:N-ASM\"></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.1.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w morph=\"robinson:V-AAI-3P\" type=\"x-split\" subType=\"x-10\" src=\"9\" lemma=\"strong:G190\"><w src=\"7\" lemma=\"strong:G190\" morph=\"robinson:V-AAI-3P\">and followed</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-AAI-3P\" type=\"x-split\" subType=\"x-10\" src=\"9\" lemma=\"strong:G190\">and followed</w><w src=\"7\" lemma=\"strong:G190\" morph=\"robinson:V-AAI-3P\"></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.1.38")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"><w src=\"13\" lemma=\"strong:G5124\" morph=\"robinson:D-ASN\">therefore</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G1519 strong:G5124\" morph=\"robinson:PREP robinson:D-ASN\">therefore</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.4")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"19\" lemma=\"strong:G1909\" morph=\"robinson:PREP\"><w src=\"20\" lemma=\"strong:G3739\" morph=\"robinson:R-DSM\">wherein</w></w>", //$NON-NLS-1$
                                  "<w src=\"19 20\" lemma=\"strong:G1909 strong:G3739\" morph=\"robinson:PREP robinson:R-DSM\">wherein</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.7")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"11\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"10 11\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"16\" lemma=\"strong:G1223\" morph=\"robinson:PREP\"><w src=\"17\" lemma=\"strong:G5101\" morph=\"robinson:I-ASN\">Why</w></w>", //$NON-NLS-1$
                                  "<w src=\"16 17\" lemma=\"strong:G1223 strong:G5101\" morph=\"robinson:PREP robinson:I-ASN\">Why</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.19")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"6\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"7\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3P\">Can</w></w>", //$NON-NLS-1$
                                  "<w src=\"6 7\" lemma=\"strong:G3361 strong:G1410\" morph=\"robinson:PRT-N robinson:V-PNI-3P\">Can</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"12\" lemma=\"strong:G1722\" morph=\"robinson:PREP\"><w src=\"13\" lemma=\"strong:G3739\" morph=\"robinson:R-DSM\">while</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G1722 strong:G3739\" morph=\"robinson:PREP robinson:R-DSM\">while</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"21\" lemma=\"strong:G5550\" morph=\"robinson:N-ASM\"><w src=\"20\" lemma=\"strong:G3745\" morph=\"robinson:K-ASM\">as long as</w></w>", //$NON-NLS-1$
                                  "<w src=\"21 20\" lemma=\"strong:G5550 strong:G3745\" morph=\"robinson:N-ASM robinson:K-ASM\">as long as</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.21")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"11\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"10\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">else</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"11 10 12\" lemma=\"strong:G1161 strong:G1487 strong:G3361\" morph=\"robinson:CONJ robinson:COND robinson:PRT-N\">else</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.22")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"10\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"11\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">else</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"9 10 11\" lemma=\"strong:G1487 strong:G1161 strong:G3361\" morph=\"robinson:COND robinson:CONJ robinson:PRT-N\">else</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.23")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"17\" lemma=\"strong:G4160\" morph=\"robinson:V-PAN\"><w src=\"18\" lemma=\"strong:G5089\" morph=\"robinson:V-PAP-NPM\">to pluck</w></w>", //$NON-NLS-1$
                                  "<w src=\"17 18\" lemma=\"strong:G4160 strong:G5089\" morph=\"robinson:V-PAN robinson:V-PAP-NPM\">to pluck</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.26")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G740\" morph=\"robinson:N-APM\"><w src=\"16\" lemma=\"strong:G4286\" morph=\"robinson:N-GSF\">the shewbread</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 16\" lemma=\"strong:G740 strong:G4286\" morph=\"robinson:N-APM robinson:N-GSF\">the shewbread</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"22\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"23\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"22 23\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.9")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"13\" lemma=\"strong:G2443\" morph=\"robinson:CONJ\">lest</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 13\" lemma=\"strong:G3361 strong:G2443\" morph=\"robinson:PRT-N robinson:CONJ\">lest</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.16")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he <w src=\"6\" lemma=\"strong:G4074\" morph=\"robinson:N-ASM\">surnamed</w></w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he</w> <w src=\"6\" lemma=\"strong:G4074\" morph=\"robinson:N-ASM\">surnamed</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.17")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"13\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he <w src=\"15\" lemma=\"strong:G3686\" morph=\"robinson:N-APN\">surnamed</w></w>", //$NON-NLS-1$
                                  "<w src=\"13\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he</w> <w src=\"15\" lemma=\"strong:G3686\" morph=\"robinson:N-APN\">surnamed</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.24")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\"><w src=\"7\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w></w>", //$NON-NLS-1$
                                  "<w src=\"8 7\" lemma=\"strong:G1410 strong:G3756\" morph=\"robinson:V-PNI-3S robinson:PRT-N\">cannot</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.25")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\"><w src=\"7\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w></w>", //$NON-NLS-1$
                                  "<w src=\"8 7\" lemma=\"strong:G1410 strong:G3756\" morph=\"robinson:V-PNI-3S robinson:PRT-N\">cannot</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.26")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"11\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\">he <w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w></w>", //$NON-NLS-1$
                                  "<w src=\"11\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\">he</w> <w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.27")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G1437\" morph=\"robinson:COND\"><w src=\"15\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">except</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G1437 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">except</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.29")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"13\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"><w src=\"15\" lemma=\"strong:G165\" morph=\"robinson:N-ASM\">never</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"10 13 15\" lemma=\"strong:G3756 strong:G1519 strong:G165\" morph=\"robinson:PRT-N robinson:PREP robinson:N-ASM\">never</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"7\" lemma=\"strong:G4151\" morph=\"robinson:N-ASN\"><w src=\"9\" lemma=\"strong:G40\" morph=\"robinson:A-ASN\">the Holy</w> Ghost</w>", //$NON-NLS-1$
                                  "<w src=\"9\" lemma=\"strong:G40\" morph=\"robinson:A-ASN\">the Holy</w> <w src=\"7\" lemma=\"strong:G4151\" morph=\"robinson:N-ASN\">Ghost</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.35")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.4")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"6\" lemma=\"strong:G3739\" morph=\"robinson:R-NSN\"><w src=\"7\" lemma=\"strong:G3303\" morph=\"robinson:PRT\">some</w></w>", //$NON-NLS-1$
                                  "<w src=\"6 7\" lemma=\"strong:G3739 strong:G3303\" morph=\"robinson:R-NSN robinson:PRT\">some</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.20")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"22\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\"><w src=\"26\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">and</w></w>", //$NON-NLS-1$
                                  "<w src=\"22 26\" lemma=\"strong:G2532 strong:G2532\" morph=\"robinson:CONJ robinson:CONJ\">and</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.22")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"4\" lemma=\"strong:G5100\" morph=\"robinson:X-NSN\">nothing</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 4\" lemma=\"strong:G3756 strong:G5100\" morph=\"robinson:PRT-N robinson:X-NSN\">nothing</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"14\" lemma=\"strong:G2443\" morph=\"robinson:CONJ\"><w src=\"15\" lemma=\"strong:G1519\" morph=\"robinson:PREP\">that</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G2443 strong:G1519\" morph=\"robinson:CONJ robinson:PREP\">that</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.34")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\">to</w> <w src=\"12\" lemma=\"strong:G846\" morph=\"robinson:P-GSM\">his</w> <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\" type=\"x-split\" subType=\"x-15\">disciples</w>.</w>", //$NON-NLS-1$
                                  "<w src=\"14\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things</w> <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\">to</w> <w src=\"12\" lemma=\"strong:G846\" morph=\"robinson:P-GSM\">his</w> <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\" type=\"x-split\" subType=\"x-15\">disciples</w>."); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.5.29")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G4983\" morph=\"robinson:N-DSN\">in <transChange type=\"added\">her</transChange> body</w>", //$NON-NLS-1$
                                  "<w src=\"12\" lemma=\"strong:G4983\" morph=\"robinson:N-DSN\">in <seg type=\"x-transChange\" subType=\"x-added\">her</seg> body</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.1")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"11\" lemma=\"strong:G5101\" morph=\"robinson:I-ASN\"><w src=\"9\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">nothing</w></w>", //$NON-NLS-1$
                                  "<w src=\"11 9\" lemma=\"strong:G5101 strong:G3361\" morph=\"robinson:I-ASN robinson:PRT-N\">nothing</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.2")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"14\" lemma=\"strong:G5101\" morph=\"robinson:I-ASN\">nothing</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 14\" lemma=\"strong:G3756 strong:G5101\" morph=\"robinson:PRT-N robinson:I-ASN\">nothing</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.14")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"7\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"6\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"5\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">neither</w></w></w></w>", //$NON-NLS-1$
                                  "<w src=\"10 7 6 5\" lemma=\"strong:G3756 strong:G3361 strong:G1487 strong:G2532\" morph=\"robinson:PRT-N robinson:PRT-N robinson:COND robinson:CONJ\">neither</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.24")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G3708\" morph=\"robinson:V-PAI-1S\"><w src=\"4\" lemma=\"strong:G991\" morph=\"robinson:V-PAI-1S\">I see</w></w>", //$NON-NLS-1$
                                  "<w src=\"10 4\" lemma=\"strong:G3708 strong:G991\" morph=\"robinson:V-PAI-1S robinson:V-PAI-1S\">I see</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"7\" lemma=\"strong:G3754\" morph=\"robinson:CONJ\"><w src=\"8\" lemma=\"strong:G5613\" morph=\"robinson:ADV\">as</w></w>", //$NON-NLS-1$
                                  "<w src=\"7 8\" lemma=\"strong:G3754 strong:G5613\" morph=\"robinson:CONJ robinson:ADV\">as</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.35")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">whosoever</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"13\" lemma=\"strong:G302\" morph=\"robinson:PRT\"><w src=\"11\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"13 11\" lemma=\"strong:G302 strong:G3739\" morph=\"robinson:PRT robinson:R-NSM\">whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.38")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">Whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">Whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.1")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"15\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G3756 strong:G3361\" morph=\"robinson:PRT-N robinson:PRT-N\">not</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"18\" lemma=\"strong:G2193\" morph=\"robinson:CONJ\"><w src=\"19\" lemma=\"strong:G302\" morph=\"robinson:PRT\">till</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G2193 strong:G302\" morph=\"robinson:CONJ robinson:PRT\">till</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.9")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"19\" lemma=\"strong:G3588\" morph=\"robinson:T-GSM\"></w><w src=\17\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\"></w><w src=\"5\" lemma=\"strong:G3588\" morph=\"robinson:T-GSN\"></w>", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
            input = replace(input, "<w src=\"14\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"15\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"16\" lemma=\"strong:G3752\" morph=\"robinson:CONJ\">till</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15 16\" lemma=\"strong:G1487 strong:G3361 strong:G3752\" morph=\"robinson:COND robinson:PRT-N robinson:CONJ\">till</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"1\" lemma=\"strong:G2597\" morph=\"robinson:V-PAP-GPM\">as <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-GPM\">they</w> came down</w>", //$NON-NLS-1$
                                  "<w src=\"1\" lemma=\"strong:G2597\" morph=\"robinson:V-PAP-GPM\">as</w> <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-GPM\">they came down</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2\" lemma=\"strong:G3699\" morph=\"robinson:ADV\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">wheresoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"2 3\" lemma=\"strong:G3699 strong:G302\" morph=\"robinson:ADV robinson:PRT\">wheresoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.19")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9\" lemma=\"strong:G2193\" morph=\"robinson:CONJ\"><w src=\"10\" lemma=\"strong:G4219\" morph=\"robinson:PRT-I\">how long</w></w>", //$NON-NLS-1$
                                  "<w src=\"9 10\" lemma=\"strong:G2193 strong:G4219\" morph=\"robinson:CONJ robinson:PRT-I\">how long</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"14\" lemma=\"strong:G2193\" morph=\"robinson:CONJ\"><w src=\"15\" lemma=\"strong:G4219\" morph=\"robinson:PRT-I\">how long</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G2193 strong:G4219\" morph=\"robinson:CONJ robinson:PRT-I\">how long</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.25")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\" type=\"x-split\" subType=\"x-32\">When</w></w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\" type=\"x-split\" subType=\"x-32\">When</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.26")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G3004\" morph=\"robinson:V-PAN\"><w src=\"15\" lemma=\"strong:G3754\" morph=\"robinson:CONJ\">said</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G3004 strong:G3754\" morph=\"robinson:V-PAN robinson:CONJ\">said</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.28")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2\" lemma=\"strong:G1525\" morph=\"robinson:V-2AAP-ASM\">when <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-ASM\">he</w> was come <w src=\"4\" lemma=\"strong:G1519\" morph=\"robinson:PREP\">into</w></w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G1525\" morph=\"robinson:V-2AAP-ASM\">when</w> <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-ASM\">he was come</w> <w src=\"4\" lemma=\"strong:G1519\" morph=\"robinson:PREP\">into</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"12\" lemma=\"strong:G2398\" morph=\"robinson:A-ASF\"><w src=\"11\" lemma=\"strong:G2596\" morph=\"robinson:PREP\">privately</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 11\" lemma=\"strong:G2398 strong:G2596\" morph=\"robinson:A-ASF robinson:PREP\">privately</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"11\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"11 12\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.29")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"11\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"11 12\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.37")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"2\" lemma=\"strong:G1437\" morph=\"robinson:COND\">Whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 2\" lemma=\"strong:G3739 strong:G1437\" morph=\"robinson:R-NSM robinson:COND\">Whosoever</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"15\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"16\" lemma=\"strong:G1437\" morph=\"robinson:COND\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"15 16\" lemma=\"strong:G3739 strong:G1437\" morph=\"robinson:R-NSM robinson:COND\">whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.41")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">whosoever</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"18\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"19\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G3756 strong:G3361\" morph=\"robinson:PRT-N robinson:PRT-N\">not</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.42")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\"><w src=\"2\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"3 2\" lemma=\"strong:G302 strong:G3739\" morph=\"robinson:PRT robinson:R-NSM\">whosoever</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"12\" lemma=\"strong:G2570\" morph=\"robinson:A-NSN\"><w src=\"15\" lemma=\"strong:G3123\" morph=\"robinson:ADV\">better</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 15\" lemma=\"strong:G2570 strong:G3123\" morph=\"robinson:A-NSN robinson:ADV\">better</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"18\" lemma=\"strong:G3037\" morph=\"robinson:N-NSM\"><w src=\"19\" lemma=\"strong:G3457\" morph=\"robinson:A-NSM\">a millstone</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G3037 strong:G3457\" morph=\"robinson:N-NSM robinson:A-NSM\">a millstone</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.50")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9\" lemma=\"strong:G1096\" morph=\"robinson:V-2ADS-3S\"><w src=\"8\" lemma=\"strong:G358\" morph=\"robinson:A-NSN\">have lost his saltness</w></w>", //$NON-NLS-1$
                                  "<w src=\"9 8\" lemma=\"strong:G1096 strong:G358\" morph=\"robinson:V-2ADS-3S robinson:A-NSN\">have lost his saltness</w>"); //$NON-NLS-1$
            input = replace(input, "<w src=\"10\" lemma=\"strong:G1722\" morph=\"robinson:PREP\"><w src=\"11\" lemma=\"strong:G5101\" morph=\"robinson:I-DSN\">wherewith</w></w>", //$NON-NLS-1$
                                  "<w src=\"10 11\" lemma=\"strong:G1722 strong:G5101\" morph=\"robinson:PREP robinson:I-DSN\">wherewith</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.10.4")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G630\" morph=\"robinson:V-AAN\">to put <transChange type=\"added\">her</transChange> away</w>", //$NON-NLS-1$
                                  "<w src=\"10\" lemma=\"strong:G630\" morph=\"robinson:V-AAN\">to put <seg type=\"x-transChange\" subType=\"x-added\">her</seg> away</w>"); //$NON-NLS-1$
        }


        Set split = new TreeSet();
        Set dup = new TreeSet();
        Set after = new HashSet();
        Map wMap = new HashMap();

        wMatcher = wPattern.matcher(input);
        while (wMatcher.find())
        {
            String whole = wMatcher.group();
            Matcher srcMatcher = srcPattern.matcher(whole);
            while (srcMatcher.find())
            {
                String[] numbers = StringUtil.split(srcMatcher.group(1), ' ');
                for (int j = 0; j < numbers.length; j++)
                {
                    Integer src = new Integer(numbers[j]);
                    if (!wMap.containsKey(src))
                    {
                        wMap.put(src, whole);
                    }

                    // If the src number occurs on more than one w element
                    // then all those w elements must have a split id.
                    if (whole.indexOf("x-split") != -1) //$NON-NLS-1$
                    {
                        split.add(src);
                    }

                    if (after.contains(src))
                    {
                        dup.add(src);
                    }
                    after.add(src);
                }
            }
        }

        // For each G3588 <w> element that is empty
        // Take its src attribute and grab its morph which should be
        // of the form T-xxx.
        // Find the  element with src+1, if it is N-xxx, then
        // merge this to that element, removing the G3588 one.
        iter = wMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String definiteArticle = entry.getValue() + "</w>"; //$NON-NLS-1$
            if (!(input.indexOf(definiteArticle) != -1 && definiteArticle.indexOf("G3588") != -1)) //$NON-NLS-1$
            {
                continue;
            }

            Matcher morphTMatcher = morphTPattern.matcher(definiteArticle);
            if (!morphTMatcher.find())
            {
                continue;
            }

            String tType = morphTMatcher.group(1);
            Integer here = (Integer) entry.getKey();
            Integer next = new Integer(here.intValue() + 1);
            String found = (String) wMap.get(next);
            if (found == null)
            {
                continue;
            }

            Matcher morphNMatcher = morphNPattern.matcher(found);
            if (!morphNMatcher.find())
            {
                continue;
            }

            String nType = morphNMatcher.group(1);
            if (tType.equals(nType) && input.indexOf("src=\"" + next + "\"") != -1) //$NON-NLS-1$ //$NON-NLS-2$
            {
                String changed = found;
                changed = replace(changed, "src=\"", "src=\"" + here + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                changed = replace(changed, "lemma=\"", "lemma=\"strong:G3588 "); //$NON-NLS-1$ //$NON-NLS-2$
                changed = replace(changed, "morph=\"", "morph=\"robinson:T-" + tType + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                input = replace(input, definiteArticle, ""); //$NON-NLS-1$
                input = replace(input, found, changed);
            }
        }

        dup.removeAll(split);

        if (!dup.isEmpty())
        {
            System.err.println(osisID + " duplicate src=" + dup); //$NON-NLS-1$
        }

        if (!before.equals(after))
        {
            before.removeAll(after);
            System.err.println(osisID + ": Problems with w src attribute. Missing: " + before); //$NON-NLS-1$
        }

//        if (osisID.equals("Ps.60.1")) //$NON-NLS-1$
//        {
//            System.err.println(osisID + ':' + input);
//        }

        input = fixApostrophe(osisID, input);
        input = fixPunctuation(osisID, input);
        input = fixDivineName(osisID, input);
        input = fixSpelling(osisID, input);
        input = fixTransChange(osisID, input);
        input = fixHyphenatedNames(osisID, input);
        input = fixInscriptions(osisID, input);
        input = fixParagraphs(osisID, input, inVerse);
        input = fixNotes(osisID, input);
        return input;
    }

    private String fixApostrophe(String osisID, String input)
    {
        Verse v = null;

        try
        {
            v = VerseFactory.fromString(osisID);
        }
        catch (NoSuchVerseException e)
        {
            return input;
        }

        if (input.indexOf('\'') == -1)
        {
            return input;
        }

        input = a1Pattern.matcher(input).replaceAll("$1 $2"); //$NON-NLS-1$

        input = a2Pattern.matcher(input).replaceAll("$1S$2"); //$NON-NLS-1$

        input = a3Pattern.matcher(input).replaceAll("$1s$2"); //$NON-NLS-1$

        input = a4Pattern.matcher(input).replaceAll("$1$2</w>"); //$NON-NLS-1$

        // for the ot only
        input = a5Pattern.matcher(input).replaceAll("$1S "); //$NON-NLS-1$

        // for the ot only
        if (SwordConstants.getTestament(v) == SwordConstants.TESTAMENT_OLD)
        {
            input = a6Pattern.matcher(input).replaceAll("$1s "); //$NON-NLS-1$
        }

        input = a7Pattern.matcher(input).replaceAll("$1S $2"); //$NON-NLS-1$

        if (SwordConstants.getTestament(v) == SwordConstants.TESTAMENT_OLD)
        {
            input = a8Pattern.matcher(input).replaceAll("$1s $2"); //$NON-NLS-1$
        }

        input = a9Pattern.matcher(input).replaceAll("$1'</w> <"); //$NON-NLS-1$

        input = a10Pattern.matcher(input).replaceAll("$1'</w> $2"); //$NON-NLS-1$

        input = a11Pattern.matcher(input).replaceAll("$2</w> $1"); //$NON-NLS-1$

        input = a12Pattern.matcher(input).replaceAll("'</w> "); //$NON-NLS-1$

        input = a13Pattern.matcher(input).replaceAll("'</w> "); //$NON-NLS-1$

        input = a14Pattern.matcher(input).replaceAll("$1s</w>$2"); //$NON-NLS-1$

        input = a15Pattern.matcher(input).replaceAll("$1s "); //$NON-NLS-1$

        if (osisID.equals("Isa.59.5") || osisID.equals("Isa.11.8")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = a16Pattern.matcher(input).replaceAll("$1 "); //$NON-NLS-1$
        }

        input = a17Pattern.matcher(input).replaceAll("$1s$2"); //$NON-NLS-1$

//        matcher = axPattern.matcher(input);
//        while (matcher.find())
//        {
//            System.err.println(osisID + " LOOK ' in :" + matcher.group()); //$NON-NLS-1$
//        }

//        if (changed)
//        {
//            System.err.println(osisID + ": " + input); //$NON-NLS-1$
//        }

        return input;
    }

    private String fixPunctuation(String osisID, String input)
    {
        input = input.replaceAll("<milestone type=\"line\"/>", "<lb/>"); //$NON-NLS-1$ //$NON-NLS-2$

        if (osisID.equals("Lev.2.3") || osisID.equals("Lev.2.10")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "sons'", "sons':"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Lev.7.31")) //$NON-NLS-1$
        {
            input = replace(input, "sons'", "sons'."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Lev.24.9") || osisID.equals("Ezek.46.16")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "sons'", "sons';"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Sam.6.9")) //$NON-NLS-1$
        {
            input = replace(input, "us:", "us;"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Kgs.12.16")) //$NON-NLS-1$
        {
            input = replace(input, "priests'", "priests'."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (osisID.equals("Isa.30.14")) //$NON-NLS-1$
        {
            input = replace(input, "..:", "...:"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        input = w1Pattern.matcher(input).replaceAll("$1"); //$NON-NLS-1$
        input = w4Pattern.matcher(input).replaceAll(" "); //$NON-NLS-1$
        input = w5Pattern.matcher(input).replaceAll("$2$1"); //$NON-NLS-1$
        input = w6Pattern.matcher(input).replaceAll("$2$1"); //$NON-NLS-1$
        input = w2Pattern.matcher(input).replaceAll(") "); //$NON-NLS-1$
        input = w3Pattern.matcher(input).replaceAll(" ("); //$NON-NLS-1$

        input = input.replaceAll("\\s+</q>", "</q>"); //$NON-NLS-1$ //$NON-NLS-2$

        // strip trailing spaces
        int length = input.length();
        int here = length;
        while (input.charAt(here - 1) == ' ')
        {
            here--;
        }

        if (here < length)
        {
            input = input.substring(0, here);
        }

        input = w7Pattern.matcher(input).replaceAll("$2$1"); //$NON-NLS-1$
        input = w8Pattern.matcher(input).replaceAll("$1"); //$NON-NLS-1$
        input = w9Pattern.matcher(input).replaceAll("$2$1"); //$NON-NLS-1$
        input = w10Pattern.matcher(input).replaceAll("$1"); //$NON-NLS-1$
        input = w11Pattern.matcher(input).replaceAll("$1"); //$NON-NLS-1$
        input = w12Pattern.matcher(input).replaceAll("$1"); //$NON-NLS-1$

        // strip leading spaces
        here = 0;
        while (input.charAt(here) == ' ')
        {
            here++;
        }

        if (here > 0)
        {
            input = input.substring(here);
//            System.err.println(osisID + " remove " + here + " leading spaces"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        input = wnPattern.matcher(input).replaceAll(" "); //$NON-NLS-1$

        input = p1Pattern.matcher(input).replaceAll("\u2026"); //$NON-NLS-1$

        if (osisID.equals("Matt.15.39")) //$NON-NLS-1$
        {
            input = replace(input, "Magdala</w>,", "Magdala</w>."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.26.56")) //$NON-NLS-1$
        {
            input = replace(input, ".</q>", ".</q> "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.2.1")) //$NON-NLS-1$
        {
            input = replace(input, ",", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.8.47")) //$NON-NLS-1$
        {
            input = replace(input, " <w src=\"24", ", <w src=\"24"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.12.49")) //$NON-NLS-1$
        {
            input = replace(input, "will I</w>", "will I</w>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.15.24")) //$NON-NLS-1$
        {
            input = replace(input, "For</w>", "For</w> "); //$NON-NLS-1$ //$NON-NLS-2$
            input = replace(input, "this</w>", "this</w> "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.17.37")) //$NON-NLS-1$
        {
            input = replace(input, " is</w>,", "</w> <transChange type=\"added\">is</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.2.17")) //$NON-NLS-1$
        {
            input = replace(input, "God</w>,<w", "God</w>, <w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.5.21")) //$NON-NLS-1$
        {
            input = replace(input, "Israel</w>", "Israel</w>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

//        if (osisID.equals("Acts.2.1")) //$NON-NLS-1$
//        {
//            System.err.println(osisID + ':' + input);
//            input = replace(input, ",", ""); //$NON-NLS-1$ //$NON-NLS-2$
//        }

        if (osisID.equals("Acts.16.40")) //$NON-NLS-1$
        {
            input = replace(input, "Lydia</w>", "Lydia</w>:"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.24.25")) //$NON-NLS-1$
        {
            input = replace(input, "come</w>", "come</w>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.15.27")) //$NON-NLS-1$
        {
            input = replace(input, "</w>,", "</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return input;
    }

    private String fixSpelling(String osisID, String input)
    {
        if (osisID.equals("Matt.5.10")) //$NON-NLS-1$
        {
            input = replace(input, "righteousness", "righteousness'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.26.39")) //$NON-NLS-1$
        {
            input = replace(input, "farther", "further"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.27.3")) //$NON-NLS-1$
        {
            input = replace(input, "betrayeth", "betrayed"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.1.19")) //$NON-NLS-1$
        {
            input = replace(input, "farther", "further"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.12.36")) //$NON-NLS-1$
        {
            input = input.replaceFirst("Lord", "<seg><divineName>Lord</divineName></seg>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.14.43")) //$NON-NLS-1$
        {
            input = replace(input, "priest", "priests"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.7.25")) //$NON-NLS-1$
        {
            input = replace(input, "kings", "kings'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.23.32")) //$NON-NLS-1$
        {
            input = replace(input, "others", "other"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.19.18")) //$NON-NLS-1$
        {
            input = replace(input, "others", "other"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.20.27")) //$NON-NLS-1$
        {
            input = input.replaceFirst("reach", "Reach"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.21.11")) //$NON-NLS-1$
        {
            input = replace(input, "and hundred", "an hundred"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.11.12") || osisID.equals("Acts.11.28") || osisID.equals("1John.5.8")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            input = replace(input, "spirit", "Spirit"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.16.23")) //$NON-NLS-1$
        {
            input = replace(input, "jailer", "jailor"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.28.15")) //$NON-NLS-1$
        {
            input = replace(input, "Forum", "forum"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.4.15")) //$NON-NLS-1$
        {
            input = replace(input, "instructers", "instructors"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.4.19") || osisID.equals("Rom.9.9") || osisID.equals("1Pet.3.6")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            input = replace(input, "Sarah", "Sara"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.15.58")) //$NON-NLS-1$
        {
            input = replace(input, "unmovable", "unmoveable"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.9.13")) //$NON-NLS-1$
        {
            input = replace(input, "into", "unto"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Gal.4.30")) //$NON-NLS-1$
        {
            input = replace(input, "free woman", "freewoman"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Phil.2.25")) //$NON-NLS-1$
        {
            input = replace(input, "fellow soldier", "fellowsoldier"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Col.4.11")) //$NON-NLS-1$
        {
            input = replace(input, "fellow workers", "fellowworkers"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.1.13")) //$NON-NLS-1$
        {
            input = replace(input, "times", "time"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Pet.2.6")) //$NON-NLS-1$
        {
            input = replace(input, "Gomorrah", "Gomorrha"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Pet.2.13")) //$NON-NLS-1$
        {
            input = replace(input, "daytime", "day time"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rev.2.6") || osisID.equals("Rev.2.15")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "Nicolaitanes", "Nicolaitans"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Acts.9.33") || osisID.equals("Acts.9.34")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "Aeneas", "\u00C6neas"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("John.3.23")) //$NON-NLS-1$
        {
            input = replace(input, "Aenon", "\u00C6non"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.2.14") || osisID.equals("Luke.6.15")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "Alphaeus", "Alph\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Matt.27.57") || osisID.equals("Luke.23.51")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "Arimathaea", "Arimath\u00E6a"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Mark.10.46")) //$NON-NLS-1$
        {
            input = replace(input, "Bartimaeus", "Bartim\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (!osisID.equals("Matt.22.17") && //$NON-NLS-1$
            !osisID.equals("Phil.4.22") && //$NON-NLS-1$
            !osisID.equals("Matt.16.13")) //$NON-NLS-1$
        {
            input = replace(input, "Caesar", "C\u00E6sar"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.7.4")) //$NON-NLS-1$ 
        {
            input = replace(input, "Chaldaeans", "Chald\u00E6ans"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Mark") || //$NON-NLS-1$
            osisID.startsWith("Luke") || //$NON-NLS-1$
            osisID.startsWith("John") || //$NON-NLS-1$
            osisID.startsWith("Acts")) //$NON-NLS-1$
        {
            input = replace(input, "Judaea", "Jud\u00E6a"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (!osisID.equals("Mark.14.70")) //$NON-NLS-1$ 
        {
            input = replace(input, "Galilaean", "Galil\u00E6an"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.15.16")) //$NON-NLS-1$ 
        {
            input = replace(input, "Praetorium", "Pr\u00E6torium"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.10.46")) //$NON-NLS-1$ 
        {
            input = replace(input, "Timaeus", "Tim\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Luke.19")) //$NON-NLS-1$ 
        {
            input = replace(input, "Zacchaeus", "Zacch\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return input;
    }

    private String fixTransChange(String osisID, String input)
    {
        if (osisID.equals("Matt.2.6")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"4\" lemma=\"strong:G1093\" morph=\"robinson:N-VSF\">in ", "<transChange type=\"added\">in</transChange> <w src=\"4\" lemma=\"strong:G1093\" morph=\"robinson:N-VSF\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.5.30")) //$NON-NLS-1$
        {
            input = replace(input, "cast it</w>", "cast</w> <transChange type=\"added\">it</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.8.13")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"11\" lemma=\"strong:G1096\" morph=\"robinson:V-AOM-3S\">so ", "<transChange type=\"added\">so</transChange> <w src=\"11\" lemma=\"strong:G1096\" morph=\"robinson:V-AOM-3S\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.20.11")) //$NON-NLS-1$
        {
            input = replace(input, " it</w>", "</w> <transChange type=\"added\">it</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.24.26")) //$NON-NLS-1$
        {
            input = input.replaceFirst("<w src=\"9\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">he is</w>", "<transChange type=\"added\">he is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceFirst("<transChange type=\"added\">he is</transChange>", "<w src=\"9\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">he is</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.24.32")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"21\" lemma=\"strong:G1451\" morph=\"robinson:ADV\">is ", "<transChange type=\"added\">is</transChange> <w src=\"21\" lemma=\"strong:G1451\" morph=\"robinson:ADV\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.4.11")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"20\" lemma=\"strong:G3956\" morph=\"robinson:A-NPN\">all these</w> <transChange type=\"added\">things</transChange>", "<w src=\"20\" lemma=\"strong:G3956\" morph=\"robinson:A-NPN\" type=\"x-split\" subType=\"x-20\">all</w> <transChange type=\"added\">these</transChange> <w src=\"20\" lemma=\"strong:G3956\" morph=\"robinson:A-NPN\" type=\"x-split\" subType=\"x-20\">things</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.12.30")) //$NON-NLS-1$
        {
            input = replace(input, " is</w>", "</w> <transChange type=\"added\">is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (osisID.equals("Luke.4.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"3\" lemma=\"strong:G1909\" morph=\"robinson:PREP\">is ", "<transChange type=\"added\">is</transChange> <w src=\"3\" lemma=\"strong:G1909\" morph=\"robinson:PREP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.11.27")) //$NON-NLS-1$
        {
            input = replace(input, " is</w> <w src=\"18", "</w> <transChange type=\"added\">is</transChange> <w src=\"18"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.11.31")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"31\" lemma=\"strong:G5602\" morph=\"robinson:ADV\">is ", "<transChange type=\"added\">is</transChange> <w src=\"31\" lemma=\"strong:G5602\" morph=\"robinson:ADV\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.17.37")) //$NON-NLS-1$
        {
            input = replace(input, " is</w>,", "</w> <transChange type=\"added\">is</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.18.1")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\"><w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\">", "<w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w><transChange type=\"added\">"); //$NON-NLS-1$ //$NON-NLS-2$
            input = replace(input, "</w>,</transChange>", "</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.21.34")) //$NON-NLS-1$
        {
            input = replace(input, " so</w>", "</w> <transChange type=\"added\">so</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.10.38")) //$NON-NLS-1$
        {
            input = replace(input, "</w> is <w", "</w> <transChange type=\"added\">is</transChange> <w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.13.13")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G1510\" morph=\"robinson:V-PXI-1S\">so ", "<transChange type=\"added\">so</transChange> <w src=\"12\" lemma=\"strong:G1510\" morph=\"robinson:V-PXI-1S\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.14.2")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w> <w src=\"11\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">so</w>", "<w src=\"11\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"></w><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w> <transChange type=\"added\">so</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.15.18")) //$NON-NLS-1$
        {
            input = input.replaceFirst("<w src=\"12\" lemma=\"strong:G3404\" morph=\"robinson:V-RAI-3S\">it hated</w>", "<transChange type=\"added\">it hated</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceFirst("<transChange type=\"added\">it hated</transChange>", "<w src=\"12\" lemma=\"strong:G3404\" morph=\"robinson:V-RAI-3S\">it hated</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.6.9")) //$NON-NLS-1$
        {
            input = input.replaceFirst("<w src=\"6 7\" lemma=\"strong:G3588 strong:G4864\" morph=\"robinson:T-GSF robinson:N-GSF\">the synagogue</w>", "<transChange type=\"added\">the synagogue</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceFirst("<transChange type=\"added\">the synagogue</transChange>", "<w src=\"6 7\" lemma=\"strong:G3588 strong:G4864\" morph=\"robinson:T-GSF robinson:N-GSF\">the synagogue</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.10.2")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G2152\" morph=\"robinson:A-NSM\">A ", "<transChange type=\"added\">A</transChange> <w src=\"1\" lemma=\"strong:G2152\" morph=\"robinson:A-NSM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.16.11")) //$NON-NLS-1$
        {
            input = replace(input, " day</w>", "</w> <transChange type=\"added\">day</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.19.19")) //$NON-NLS-1$
        {
            input = replace(input, "found it</w>", "found</w> <transChange type=\"added\">it</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.22.3")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9 10\" lemma=\"strong:G3588 strong:G2791\" morph=\"robinson:T-GSF robinson:N-GSF\">a city ", "<transChange type=\"added\">a city</transChange> <w src=\"9 10\" lemma=\"strong:G3588 strong:G2791\" morph=\"robinson:T-GSF robinson:N-GSF\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.6.5")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\">in the likeness of his</transChange>", "<transChange type=\"added\">in the likeness</transChange> of <transChange type=\"added\">his</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.12.2")) //$NON-NLS-1$
        {
            input = replace(input, " is</w>", "</w> <transChange type=\"added\">is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.10.20")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\">I say</transChange>", "I <transChange type=\"added\">say</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.11.26")) //$NON-NLS-1$
        {
            input = replace(input, "</w><transChange type=\"added\">this</transChange>", "this</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.11.27")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10 11\" lemma=\"strong:G3588 strong:G4221\" morph=\"robinson:T-ASN robinson:N-ASN\">this ", "<transChange type=\"added\">this</transChange> <w src=\"10 11\" lemma=\"strong:G3588 strong:G4221\" morph=\"robinson:T-ASN robinson:N-ASN\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.15.10")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\">which was bestowed</transChange>", "which <transChange type=\"added\">was bestowed</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.1.2")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2\" lemma=\"strong:G5213\" morph=\"robinson:P-2DP\">be ", "<transChange type=\"added\">be</transChange> <w src=\"2\" lemma=\"strong:G5213\" morph=\"robinson:P-2DP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.2.6")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"6\" lemma=\"strong:G3778\" morph=\"robinson:D-NSF\">is ", "<transChange type=\"added\">is</transChange> <w src=\"6\" lemma=\"strong:G3778\" morph=\"robinson:D-NSF\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.8.18")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">is ", "<transChange type=\"added\">is</transChange> <w src=\"10\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.11.9")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"28\" lemma=\"strong:G5083\" morph=\"robinson:V-FAI-1S\">so ", "<transChange type=\"added\">so</transChange> <w src=\"28\" lemma=\"strong:G5083\" morph=\"robinson:V-FAI-1S\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Eph.5.9")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"6\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">is ", "<transChange type=\"added\">is</transChange> <w src=\"6\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Tim.3.11")) //$NON-NLS-1$
        {
            input = replace(input, "</w> be <w", "</w> <transChange type=\"added\">be</transChange> <w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Phlm.1.1")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\">our</w> <w src=\"8\" lemma=\"strong:G80\" morph=\"robinson:N-NSM\">", "<transChange type=\"added\">our</transChange> <w src=\"7 8\" lemma=\"strong:G3588 strong:G80\" morph=\"robinson:T-NSM robinson:N-NSM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.10.23")) //$NON-NLS-1$
        {
            input = replace(input, "he is</w>", "he</w> <transChange type=\"added\">is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.12.1")) //$NON-NLS-1$
        {
            input = replace(input, "</w> us,", "</w> <transChange type=\"added\">us</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.12.19")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\">which</transChange> <w src=\"7\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\">voice</w>", "<w src=\"7\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\">which</w> <transChange type=\"added\">voice</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Jas.2.16")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"10\" lemma=\"strong:G2328\" morph=\"robinson:V-PEM-2P\">be ye warmed</w>", "<w src=\"10\" lemma=\"strong:G2328\" morph=\"robinson:V-PEM-2P\" type=\"x-split\" subType=\"x-10\">be</w> <transChange type=\"added\">ye</transChange> <w src=\"10\" lemma=\"strong:G2328\" morph=\"robinson:V-PEM-2P\" type=\"x-split\" subType=\"x-10\">warmed</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1John.2.23")) //$NON-NLS-1$
        {
            input = replace(input, "(<transChange type=\"added\">", "<transChange type=\"added\">("); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1John.5.19")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"1\" lemma=\"strong:G1492\" morph=\"robinson:V-RAI-1P\">And ", "<transChange type=\"added\">And</transChange> <w src=\"1\" lemma=\"strong:G1492\" morph=\"robinson:V-RAI-1P\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1John.5.20")) //$NON-NLS-1$
        {
            input = replace(input, "<transChange type=\"added\"><w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">And</w></transChange>", "<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">And</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rev.22.2")) //$NON-NLS-1$
        {
            input = replace(input, "</transChange> <w src=\"15\" lemma=\"strong:G2590\" morph=\"robinson:N-APM\">of ", " of</transChange> <w src=\"15\" lemma=\"strong:G2590\" morph=\"robinson:N-APM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rev.22.7")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"5\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\">is ", "<transChange type=\"added\">is</transChange> <w src=\"5\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return input;
    }

    private String fixHyphenatedNames(String osisID, String input)
    {
        Verse v = null;

        try
        {
            v = VerseFactory.fromString(osisID);
        }
        catch (NoSuchVerseException e)
        {
            // does not happen
        }

//        if (osisID.equals("Gen.49.17")) //$NON-NLS-1$
//        {
//            input = replace(input, "arrow-snake", "arrow\u2010snake"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Exod.18.19") || osisID.equals("2Cor.3.4") || osisID.equals("1Thess.1.8")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        {
////            System.err.println(osisID + ':' + input);
//            input = replace(input, "God-ward", "God\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("1Sam.19.4")) //$NON-NLS-1$
//        {
//            input = replace(input, "thee-ward", "thee\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Ps.49.5") || osisID.equals("Eph.1.19") || osisID.equals("2Pet.3.9")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        {
//            input = replace(input, "us-ward", "us\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Song.5.10")) //$NON-NLS-1$
//        {
//            input = replace(input, "standard-bearer", "standard\u2010bearer"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Jer.22.14")) //$NON-NLS-1$
//        {
//            input = replace(input, "through-aired", "through\u2010aired"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Acts.13.6")) //$NON-NLS-1$
//        {
//            input = replace(input, "Bar-jesus", "Bar\u2010jesus"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Rom.8.17")) //$NON-NLS-1$
//        {
//            input = replace(input, "joint-heirs", "joint\u2010heirs"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("2Cor.1.12") || osisID.equals("2Cor.13.3")) //$NON-NLS-1$ //$NON-NLS-2$
//        {
//            input = replace(input, "you-ward", "you\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (osisID.equals("Eph.3.2")) //$NON-NLS-1$
//        {
//            input = replace(input, "youward", "you\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
//        }

        if (osisID.equals("Eph.3.2")) //$NON-NLS-1$
        {
            input = replace(input, "youward", "you-ward"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Confirmed
        if (input.indexOf("Abednego") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbednego\\b", "Abed\u2013nego"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abelbethmaachah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelbethmaachah\\b", "Abel\u2013beth\u2013maachah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abelmaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelmaim\\b", "Abel\u2013maim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abelmeholah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelmeholah\\b", "Abel\u2013meholah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abelmizraim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelmizraim\\b", "Abel\u2013mizraim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abelshittim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelshittim\\b", "Abel\u2013shittim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abialbon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbialbon\\b", "Abi\u2013albon"); //$NON-NLS-1$ //$NON-NLS-2$
        if ((osisID.equals("Judg.6.34") || //$NON-NLS-1$
             osisID.equals("Judg.8.2") || //$NON-NLS-1$
             osisID.equals("1Chr.11.28") || //$NON-NLS-1$
             osisID.equals("1Chr.27.12") //$NON-NLS-1$
             ) && input.indexOf("Abiezer") != -1) //$NON-NLS-1$
        {
            input = input.replaceAll("\\bAbiezer\\b", "Abi\u2013ezer"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (input.indexOf("Abiezrites") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbiezrites\\b", "Abi\u2013ezrites"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Abiezrite") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAbiezrite\\b", "Abi\u2013ezrite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Adonibezek") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAdonibezek\\b", "Adoni\u2013bezek"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Adonizedek") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAdonizedek\\b", "Adoni\u2013zedek"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Allonbachuth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAllonbachuth\\b", "Allon\u2013bachuth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Almondiblathaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAlmondiblathaim\\b", "Almon\u2013diblathaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ashdothpisgah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAshdothpisgah\\b", "Ashdoth\u2013pisgah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Atarothadar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAtarothadar\\b", "Ataroth\u2013adar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Atarothaddar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAtarothaddar\\b", "Ataroth\u2013addar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Aznothtabor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAznothtabor\\b", "Aznoth\u2013tabor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalathbeer") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalathbeer\\b", "Baalath\u2013beer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalberith") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalberith\\b", "Baal\u2013berith"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalgad") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalgad\\b", "Baal\u2013gad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalhamon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhamon\\b", "Baal\u2013hamon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalhanan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhanan\\b", "Baal\u2013hanan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalhazor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhazor\\b", "Baal\u2013hazor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalhermon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhermon\\b", "Baal\u2013hermon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalmeon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalmeon\\b", "Baal\u2013meon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalpeor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalpeor\\b", "Baal\u2013peor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalperazim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalperazim\\b", "Baal\u2013perazim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalshalisha") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalshalisha\\b", "Baal\u2013shalisha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baaltamar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaaltamar\\b", "Baal\u2013tamar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalzebub") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalzebub\\b", "Baal\u2013zebub"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Baalzephon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalzephon\\b", "Baal\u2013zephon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bamothbaal") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBamothbaal\\b", "Bamoth\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bashanhavothjair") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBashanhavothjair\\b", "Bashan\u2013havoth\u2013jair"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bathrabbim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBathrabbim\\b", "Bath\u2013rabbim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bathsheba") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBathsheba\\b", "Bath\u2013sheba"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bathshua") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBathshua\\b", "Bath\u2013shua"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Beerelim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBeerelim\\b", "Beer\u2013elim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Beerlahairoi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBeerlahairoi\\b", "Beer\u2013lahai\u2013roi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Beersheba") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBeersheba\\b", "Beer\u2013sheba"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Beeshterah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBeeshterah\\b", "Beesh\u2013terah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benammi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenammi\\b", "Ben\u2013ammi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Beneberak") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBeneberak\\b", "Bene\u2013berak"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benejaakan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenejaakan\\b", "Bene\u2013jaakan"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.indexOf("Benhadad") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhadad\\b", "Ben\u2013hadad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benhail") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhail\\b", "Ben\u2013hail"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benhanan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhanan\\b", "Ben\u2013hanan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benoni") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenoni\\b", "Ben\u2013oni"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benzoheth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenzoheth\\b", "Ben\u2013zoheth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Berodachbaladan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBerodachbaladan\\b", "Berodach\u2013baladan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethanath") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethanath\\b", "Beth\u2013anath"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethanoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethanoth\\b", "Beth\u2013anoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Betharabah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBetharabah\\b", "Beth\u2013arabah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Betharam") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBetharam\\b", "Beth\u2013aram"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Betharbel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBetharbel\\b", "Beth\u2013arbel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethaven") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethaven\\b", "Beth\u2013aven"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethazmaveth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethazmaveth\\b", "Beth\u2013azmaveth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethbaalmeon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethbaalmeon\\b", "Beth\u2013baal\u2013meon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethbarah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethbarah\\b", "Beth\u2013barah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethbirei") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethbirei\\b", "Beth\u2013birei"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethcar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethcar\\b", "Beth\u2013car"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethdagon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethdagon\\b", "Beth\u2013dagon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethdiblathaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethdiblathaim\\b", "Beth\u2013diblathaim"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
        if (input.indexOf("Bethel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethel\\b", "Beth\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.indexOf("Bethemek") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethemek\\b", "Beth\u2013emek"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethezel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethezel\\b", "Beth\u2013ezel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethgader") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethgader\\b", "Beth\u2013gader"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethgamul") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethgamul\\b", "Beth\u2013gamul"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethhaccerem") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhaccerem\\b", "Beth\u2013haccerem"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethharan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethharan\\b", "Beth\u2013haran"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethhoglah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhoglah\\b", "Beth\u2013hoglah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethhogla") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhogla\\b", "Beth\u2013hogla"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethhoron") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhoron\\b", "Beth\u2013horon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethjeshimoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethjeshimoth\\b", "Beth\u2013jeshimoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethjesimoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethjesimoth\\b", "Beth\u2013jesimoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethlebaoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlebaoth\\b", "Beth\u2013lebaoth"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
        if (input.indexOf("Bethlehemjudah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlehemjudah\\b", "Beth\u2013lehem\u2013judah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethlehem") != -1 && SwordConstants.getTestament(v) == SwordConstants.TESTAMENT_OLD) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlehem\\b", "Beth\u2013lehem"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.indexOf("Bethmaachah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethmaachah\\b", "Beth\u2013maachah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethmarcaboth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethmarcaboth\\b", "Beth\u2013marcaboth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethmeon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethmeon\\b", "Beth\u2013meon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethnimrah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethnimrah\\b", "Beth\u2013nimrah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethpalet") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethpalet\\b", "Beth\u2013palet"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethpazzez") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethpazzez\\b", "Beth\u2013pazzez"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethpeor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethpeor\\b", "Beth\u2013peor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethphelet") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethphelet\\b", "Beth\u2013phelet"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethrapha") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethrapha\\b", "Beth\u2013rapha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethrehob") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethrehob\\b", "Beth\u2013rehob"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethshan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshan\\b", "Beth\u2013shan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethshean") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshean\\b", "Beth\u2013shean"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethshemesh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshemesh\\b", "Beth\u2013shemesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethshemite") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshemite\\b", "Beth\u2013shemite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethshittah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshittah\\b", "Beth\u2013shittah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethtappuah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethtappuah\\b", "Beth\u2013tappuah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethzur") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethzur\\b", "Beth\u2013zur"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Calebephratah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bCalebephratah\\b", "Caleb\u2013ephratah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Chepharhaammonai") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bChepharhaammonai\\b", "Chephar\u2013haammonai"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Chislothtabor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bChislothtabor\\b", "Chisloth\u2013tabor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Chorashan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bChorashan\\b", "Chor\u2013ashan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Chushanrishathaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bChushanrishathaim\\b", "Chushan\u2013rishathaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Colhozeh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bColhozeh\\b", "Col\u2013hozeh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Danjaan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bDanjaan\\b", "Dan\u2013jaan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Dibongad") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bDibongad\\b", "Dibon\u2013gad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ebedmelech") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEbedmelech\\b", "Ebed\u2013melech"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ebenezer") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEbenezer\\b", "Eben\u2013ezer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Elbethel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bElbethel\\b", "El\u2013beth\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
        if (input.indexOf("Elelohe-Israel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bElelohe-Israel\\b", "El\u2013elohe\u2013Israel"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.indexOf("Elonbethhanan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bElonbethhanan\\b", "Elon\u2013beth\u2013hanan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Elparan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bElparan\\b", "El\u2013paran"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Eneglaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEneglaim\\b", "En\u2013eglaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Engannim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEngannim\\b", "En\u2013gannim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Engedi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEngedi\\b", "En\u2013gedi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enhaddah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnhaddah\\b", "En\u2013haddah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enhakkore") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnhakkore\\b", "En\u2013hakkore"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enhazor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnhazor\\b", "En\u2013hazor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enmishpat") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnmishpat\\b", "En\u2013mishpat"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enrimmon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnrimmon\\b", "En\u2013rimmon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enrogel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnrogel\\b", "En\u2013rogel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Enshemesh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEnshemesh\\b", "En\u2013shemesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Entappuah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEntappuah\\b", "En\u2013tappuah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ephesdammim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEphesdammim\\b", "Ephes\u2013dammim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Esarhaddon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEsarhaddon\\b", "Esar\u2013haddon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Eshbaal") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEshbaal\\b", "Esh\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Evilmerodach") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEvilmerodach\\b", "Evil\u2013merodach"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Eziongaber") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEziongaber\\b", "Ezion\u2013gaber"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Eziongeber") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bEziongeber\\b", "Ezion\u2013geber"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Gathhepher") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bGathhepher\\b", "Gath\u2013hepher"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Gathrimmon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bGathrimmon\\b", "Gath\u2013rimmon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Gibeah-haaraloth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bGibeah-haaraloth\\b", "Gibeah\u2013haaraloth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Gittahhepher") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bGittahhepher\\b", "Gittah\u2013hepher"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Gurbaal") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bGurbaal\\b", "Gur\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hamathzobah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHamathzobah\\b", "Hamath\u2013zobah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hammothdor") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHammothdor\\b", "Hammoth\u2013dor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hamongog") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHamongog\\b", "Hamon\u2013gog"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Havothjair") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHavothjair\\b", "Havoth\u2013jair"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazaraddar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazaraddar\\b", "Hazar\u2013addar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazarenan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarenan\\b", "Hazar\u2013enan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazargaddah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazargaddah\\b", "Hazar\u2013gaddah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazarhatticon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarhatticon\\b", "Hazar\u2013hatticon"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
//        if (input.indexOf("Hazarmaveth") != -1) //$NON-NLS-1$
//          input = input.replaceAll("\\bHazarmaveth\\b", "Hazar\u2013maveth"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.indexOf("Hazarshual") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarshual\\b", "Hazar\u2013shual"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazarsusah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarsusah\\b", "Hazar\u2013susah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazarsusim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarsusim\\b", "Hazar\u2013susim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazazontamar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazazontamar\\b", "Hazazon\u2013tamar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hazezontamar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHazezontamar\\b", "Hazezon\u2013tamar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Helkathhazzurim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHelkathhazzurim\\b", "Helkath\u2013hazzurim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hephzibah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHephzibah\\b", "Hephzi\u2013bah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Horhagidgad") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHorhagidgad\\b", "Hor\u2013hagidgad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ichabod") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIchabod\\b", "I\u2013chabod"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ijeabarim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIjeabarim\\b", "Ije\u2013abarim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Irnahash") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIrnahash\\b", "Ir\u2013nahash"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Irshemesh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIrshemesh\\b", "Ir\u2013shemesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ishbibenob") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIshbibenob\\b", "Ishbi\u2013benob"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ishbosheth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIshbosheth\\b", "Ish\u2013bosheth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ishtob") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIshtob\\b", "Ish\u2013tob"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ittahkazin") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bIttahkazin\\b", "Ittah\u2013kazin"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jaareoregim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJaareoregim\\b", "Jaare\u2013oregim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jabeshgilead") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJabeshgilead\\b", "Jabesh\u2013gilead"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jashubilehem") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJashubilehem\\b", "Jashubi\u2013lehem"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jegarsahadutha") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJegarsahadutha\\b", "Jegar\u2013sahadutha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jehovahjireh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahjireh\\b", "Jehovah\u2013jireh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jehovahnissi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahnissi\\b", "Jehovah\u2013nissi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jehovahshalom") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahshalom\\b", "Jehovah\u2013shalom"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jiphthahel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJiphthahel\\b", "Jiphthah\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jushabhesed") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJushabhesed\\b", "Jushab\u2013hesed"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kadeshbarnea") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKadeshbarnea\\b", "Kadesh\u2013barnea"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kedeshnaphtali") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKedeshnaphtali\\b", "Kedesh\u2013naphtali"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kerenhappuch") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKerenhappuch\\b", "Keren\u2013happuch"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kibrothhattaavah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKibrothhattaavah\\b", "Kibroth\u2013hattaavah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirharaseth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirharaseth\\b", "Kir\u2013haraseth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirhareseth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirhareseth\\b", "Kir\u2013hareseth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirharesh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirharesh\\b", "Kir\u2013haresh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirheres") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirheres\\b", "Kir\u2013heres"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjatharba") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjatharba\\b", "Kirjath\u2013arba"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjatharim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjatharim\\b", "Kirjath\u2013arim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjathbaal") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathbaal\\b", "Kirjath\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjathhuzoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathhuzoth\\b", "Kirjath\u2013huzoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjathjearim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathjearim\\b", "Kirjath\u2013jearim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjathsannah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathsannah\\b", "Kirjath\u2013sannah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Kirjathsepher") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathsepher\\b", "Kirjath\u2013sepher"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Lahairoi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bLahairoi\\b", "Lahai\u2013roi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Loammi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bLoammi\\b", "Lo\u2013ammi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Lodebar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bLodebar\\b", "Lo\u2013debar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Loruhamah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bLoruhamah\\b", "Lo\u2013ruhamah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Maalehacrabbim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMaalehacrabbim\\b", "Maaleh\u2013acrabbim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Magormissabib") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMagormissabib\\b", "Magor\u2013missabib"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Mahanehdan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMahanehdan\\b", "Mahaneh\u2013dan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Mahershalalhashbaz") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMahershalalhashbaz\\b", "Maher\u2013shalal\u2013hash\u2013baz"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Maher-shalal-hash-baz") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMaher-shalal-hash-baz\\b", "Maher\u2013shalal\u2013hash\u2013baz"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Malchishua") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMalchishua\\b", "Malchi\u2013shua"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Mejarkon") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMejarkon\\b", "Me\u2013jarkon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Melchishua") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMelchishua\\b", "Melchi\u2013shua"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Meribah-Kadesh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribah-Kadesh\\b", "Meribah\u2013Kadesh"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
        if (input.indexOf("Meribaal") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribaal\\b", "Meri\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Meribbaal") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribbaal\\b", "Merib\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.indexOf("Merodachbaladan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMerodachbaladan\\b", "Merodach\u2013baladan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Methegammah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMethegammah\\b", "Metheg\u2013ammah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Migdalel") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMigdalel\\b", "Migdal\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Migdalgad") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMigdalgad\\b", "Migdal\u2013gad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Misrephothmaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMisrephothmaim\\b", "Misrephoth\u2013maim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Moreshethgath") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMoreshethgath\\b", "Moresheth\u2013gath"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Nathanmelech") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bNathanmelech\\b", "Nathan\u2013melech"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Nebuzaradan") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bNebuzaradan\\b", "Nebuzar\u2013adan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Nergalsharezer") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bNergalsharezer\\b", "Nergal\u2013sharezer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Obededom") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bObededom\\b", "Obed\u2013edom"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Padanaram") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPadanaram\\b", "Padan\u2013aram"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pahathmoab") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPahathmoab\\b", "Pahath\u2013moab"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pasdammim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPasdammim\\b", "Pas\u2013dammim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Perezuzzah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPerezuzzah\\b", "Perez\u2013uzzah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Perezuzza") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPerezuzza\\b", "Perez\u2013uzza"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pharaohhophra") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPharaohhophra\\b", "Pharaoh\u2013hophra"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pharaohnechoh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPharaohnechoh\\b", "Pharaoh\u2013nechoh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pharaohnecho") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPharaohnecho\\b", "Pharaoh\u2013necho"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pibeseth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPibeseth\\b", "Pi\u2013beseth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Pihahiroth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPihahiroth\\b", "Pi\u2013hahiroth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Potipherah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bPotipherah\\b", "Poti\u2013pherah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Rabsaris") != -1 && !osisID.equals("2Kgs.18.17")) //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("\\bRabsaris\\b", "Rab\u2013saris"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Rabshakeh") != -1 && osisID.startsWith("2Kgs")) //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("\\bRabshakeh\\b", "Rab\u2013shakeh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ramathaimzophim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRamathaimzophim\\b", "Ramathaim\u2013zophim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ramathlehi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRamathlehi\\b", "Ramath\u2013lehi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ramathmizpeh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRamathmizpeh\\b", "Ramath\u2013mizpeh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ramothgilead") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRamothgilead\\b", "Ramoth\u2013gilead"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Regemmelech") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRegemmelech\\b", "Regem\u2013melech"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Remmonmethoar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRemmonmethoar\\b", "Remmon\u2013methoar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Rimmonparez") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRimmonparez\\b", "Rimmon\u2013parez"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Romamtiezer") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRomamtiezer\\b", "Romamti\u2013ezer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Ruhamah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRuhamah\\b", "Ru\u2013hamah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Samgarnebo") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bSamgarnebo\\b", "Samgar\u2013nebo"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Selahammahlekoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bSelahammahlekoth\\b", "Sela\u2013hammahlekoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Shearjashub") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bShearjashub\\b", "Shear\u2013jashub"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Shetharboznai") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bShetharboznai\\b", "Shethar\u2013boznai"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Shihorlibnath") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bShihorlibnath\\b", "Shihor\u2013libnath"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Shimronmeron") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bShimronmeron\\b", "Shimron\u2013meron"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Succothbenoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bSuccothbenoth\\b", "Succoth\u2013benoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Syriadamascus") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bSyriadamascus\\b", "Syria\u2013damascus"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Syriamaachah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bSyriamaachah\\b", "Syria\u2013maachah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Taanathshiloh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTaanathshiloh\\b", "Taanath\u2013shiloh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Tahtimhodshi") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTahtimhodshi\\b", "Tahtim\u2013hodshi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Telabib") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTelabib\\b", "Tel\u2013abib"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Telharesha") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTelharesha\\b", "Tel\u2013haresha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Telharsa") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTelharsa\\b", "Tel\u2013harsa"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Telmelah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTelmelah\\b", "Tel\u2013melah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Tiglathpileser") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTiglathpileser\\b", "Tiglath\u2013pileser"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Tilgathpilneser") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTilgathpilneser\\b", "Tilgath\u2013pilneser"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Timnathheres") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTimnathheres\\b", "Timnath\u2013heres"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Timnathserah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTimnathserah\\b", "Timnath\u2013serah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Tobadonijah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTobadonijah\\b", "Tob\u2013adonijah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Tubalcain") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bTubalcain\\b", "Tubal\u2013cain"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Uzzensherah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bUzzensherah\\b", "Uzzen\u2013sherah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Zarethshahar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bZarethshahar\\b", "Zareth\u2013shahar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Zaphnathpaaneah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bZaphnathpaaneah\\b", "Zaphnath\u2013paaneah"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
        if (input.indexOf("Altaschith") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAltaschith\\b", "Al\u2013taschith"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Aramnaharaim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAramnaharaim\\b", "Aram\u2013naharaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Aramzobah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bAramzobah\\b", "Aram\u2013zobah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jonathelemrechokim") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJonathelemrechokim\\b", "Jonath\u2013elem\u2013rechokim"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed and are in margin notes
        if (input.indexOf("Bathshuah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBathshuah\\b", "Bath\u2013shuah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benabinadab") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenabinadab\\b", "Ben\u2013abinadab"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bendekar") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBendekar\\b", "Ben\u2013dekar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bengeber") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBengeber\\b", "Ben\u2013geber"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benhesed") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhesed\\b", "Ben\u2013hesed"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Benhur") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhur\\b", "Ben\u2013hur"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Betheden") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBetheden\\b", "Beth\u2013eden"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Hatsihammenuchoth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bHatsihammenuchoth\\b", "Hatsi\u2013ham\u2013menuchoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jehovahshammah") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahshammah\\b", "Jehovah\u2013shammah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Jehovahtsidkenu") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahtsidkenu\\b", "Jehovah\u2013tsidkenu"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Meribahkadesh") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribahkadesh\\b", "Meribah\u2013kadesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Shoshannimeduth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bShoshannimeduth\\b", "Shoshannim\u2013eduth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Shushaneduth") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bShushaneduth\\b", "Shushan\u2013eduth"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed.
//        if (input.indexOf("Amminadib") != -1) //$NON-NLS-1$
//            input = input.replaceAll("\\bAmminadib\\b", "Ammi\u2013nadib"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethelite") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethelite\\b", "Beth\u2013elite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Bethlehemite") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlehemite\\b", "Beth\u2013lehemite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.indexOf("Rabmag") != -1) //$NON-NLS-1$
            input = input.replaceAll("\\bRabmag\\b", "Rab\u2013mag"); //$NON-NLS-1$ //$NON-NLS-2$
//        if (input.indexOf("Endor") != -1) //$NON-NLS-1$
//            input = input.replaceAll("\\bEndor\\b", "En\u2013dor"); //$NON-NLS-1$ //$NON-NLS-2$

        return input;
    }

    private String fixDivineName(String osisID, String input)
    {
        if (input.indexOf("divineName") != -1) //$NON-NLS-1$
        {
            input = divineNamePattern.matcher(input).replaceAll(divineNameReplace);
        }

        if (input.indexOf("H03069") != -1) //$NON-NLS-1$
        {
            input = dna1Pattern.matcher(input).replaceAll(dna1Replace);
        }

        if (input.indexOf("H03050") != -1) //$NON-NLS-1$
        {
            input = dna2Pattern.matcher(input).replaceAll(dna1Replace);
        }

//        input = dna3Pattern.matcher(input).replaceAll(dna2Replace);
//        input = dna4Pattern.matcher(input).replaceAll(dna2Replace);

        input = dn1Pattern.matcher(input).replaceAll(dn1Replace);
        input = dn2Pattern.matcher(input).replaceAll(dn2Replace);
        input = dn3Pattern.matcher(input).replaceAll(dn3Replace);
        input = dn4Pattern.matcher(input).replaceAll(dn4Replace);
        input = dn5Pattern.matcher(input).replaceAll(dn5Replace);

//        if (osisID.equals("Exod.3.14")) //$NON-NLS-1$
//        {
//            input = replace(input, "<w morph=\"strongMorph:TH8799\" lemma=\"strong:H01961\">I AM</w> <w morph=\"strongMorph:TH8799\" lemma=\"strong:H01961\">THAT I AM</w>", //$NON-NLS-1$
//                                  "<divineName><w morph=\"strongMorph:TH8799\" lemma=\"strong:H01961\">I am</w> <w morph=\"strongMorph:TH8799\" lemma=\"strong:H01961\">that I am</w></divineName>"); //$NON-NLS-1$
//            input = replace(input, "I AM", "<seg><divineName>I am</divineName></seg>"); //$NON-NLS-1$ //$NON-NLS-2$
//        }

//        if (osisID.equals("Matt.1.21") || //$NON-NLS-1$
//            osisID.equals("Matt.1.25") || //$NON-NLS-1$
//            osisID.equals("Luke.1.31") || //$NON-NLS-1$
//            osisID.equals("Luke.2.21")) //$NON-NLS-1$
//        {
//            input = replace(input, "JESUS", "<seg><divineName type=\"x-jesus\">Jesus</divineName></seg>"); //$NON-NLS-1$ //$NON-NLS-2$
//        }

        if (osisID.equals("Matt.22.44")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"2 3\" lemma=\"strong:G3588 strong:G2962\" morph=\"robinson:T-NSM robinson:N-NSM\">The LORD</w>", //$NON-NLS-1$
                                  "<w src=\"2 3\" lemma=\"strong:G3588 strong:G2962\" morph=\"robinson:T-NSM robinson:N-NSM\">The <seg><divineName>Lord</divineName></seg></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.20.42")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9 10\" lemma=\"strong:G3588 strong:G2962\" morph=\"robinson:T-NSM robinson:N-NSM\">The LORD</w>", //$NON-NLS-1$
                                  "<w src=\"9 10\" lemma=\"strong:G3588 strong:G2962\" morph=\"robinson:T-NSM robinson:N-NSM\">The <seg><divineName>Lord</divineName></seg></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Acts.2.34")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"12 13\" lemma=\"strong:G3588 strong:G2962\" morph=\"robinson:T-NSM robinson:N-NSM\">The LORD</w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G3588 strong:G2962\" morph=\"robinson:T-NSM robinson:N-NSM\">The <seg><divineName>Lord</divineName></seg></w>"); //$NON-NLS-1$
        }

        input = dn6Pattern.matcher(input).replaceAll(dn6Replace);
        input = dn7Pattern.matcher(input).replaceAll(dn7Replace);
        input = dn8Pattern.matcher(input).replaceAll(dn8Replace);
        input = dn9Pattern.matcher(input).replaceAll(dn9Replace);
        input = dn10Pattern.matcher(input).replaceAll(dn10Replace);
//        input = dn11Pattern.matcher(input).replaceAll(dn11Replace);

        if (osisID.equals("Deut.28.58")) //$NON-NLS-1$
        {
            input = replace(input, "<divineName>God</divineName>", //$NON-NLS-1$
                                   "GOD"); //$NON-NLS-1$
        }

        if (osisID.equals("Jer.23.6") || osisID.equals("Jer.33.16")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "<divineName>Lord</divineName>", //$NON-NLS-1$
                                   "<seg><divineName>Lord</divineName></seg>"); //$NON-NLS-1$
        }

        return input;
    }

    private String fixInscriptions(String osisID, String input)
    {

        if (osisID.equals("Exod.28.36") || osisID.equals("Exod.39.30")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = replace(input, "<w lemma=\"strong:H06944\">HOLINESS</w> <w lemma=\"strong:H03068\">TO THE <seg><divineName>Lord</divineName></seg></w>", //$NON-NLS-1$
                                  "<inscription><w lemma=\"strong:H06944\">HOLINESS</w> <w lemma=\"strong:H03068\">TO THE <seg><divineName>Lord</divineName></seg></w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Dan.5.25")) //$NON-NLS-1$ 
        {
            input = replace(input, "<w morph=\"strongMorph:TH8752\" lemma=\"strong:H04484\">MENE</w>, <w morph=\"strongMorph:TH8752\" lemma=\"strong:H04484\">MENE</w>, <w morph=\"strongMorph:TH8752\" lemma=\"strong:H08625\">TEKEL</w>, <w morph=\"strongMorph:TH8751\" lemma=\"strong:H06537\">UPHARSIN</w>", //$NON-NLS-1$
                                  "<inscription><w morph=\"strongMorph:TH8752\" lemma=\"strong:H04484\">MENE</w>, <w morph=\"strongMorph:TH8752\" lemma=\"strong:H04484\">MENE</w>, <w morph=\"strongMorph:TH8752\" lemma=\"strong:H08625\">TEKEL</w>, <w morph=\"strongMorph:TH8751\" lemma=\"strong:H06537\">UPHARSIN</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Dan.5.26")) //$NON-NLS-1$ 
        {
            input = replace(input, "<w morph=\"strongMorph:TH8752\" lemma=\"strong:H04484\">MENE</w>", //$NON-NLS-1$
                                  "<inscription><w morph=\"strongMorph:TH8752\" lemma=\"strong:H04484\">MENE</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Dan.5.27")) //$NON-NLS-1$ 
        {
            input = replace(input, "<w morph=\"strongMorph:TH8752\" lemma=\"strong:H08625\">TEKEL</w>", //$NON-NLS-1$
                                  "<inscription><w morph=\"strongMorph:TH8752\" lemma=\"strong:H08625\">TEKEL</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Dan.5.28")) //$NON-NLS-1$ 
        {
            input = replace(input, "<w morph=\"strongMorph:TH8752\" lemma=\"strong:H06537\">PERES</w>", //$NON-NLS-1$
                                  "<inscription><w morph=\"strongMorph:TH8752\" lemma=\"strong:H06537\">PERES</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Zech.14.20")) //$NON-NLS-1$
        {
            input = replace(input, "<w lemma=\"strong:H06944\">HOLINESS</w> <w lemma=\"strong:H03068\">UNTO THE <seg><divineName>Lord</divineName></seg></w>", //$NON-NLS-1$
                                  "<inscription><w lemma=\"strong:H06944\">HOLINESS</w> <w lemma=\"strong:H03068\">UNTO THE <seg><divineName>Lord</divineName></seg></w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.27.37")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"11\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">THIS</w> <w src=\"12\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">IS</w> <w src=\"13\" lemma=\"strong:G2424\" morph=\"robinson:N-NSM\">JESUS</w> <w src=\"14 15\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"16\" lemma=\"strong:G3588\" morph=\"robinson:T-GPM\"></w><w src=\"17\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w>", //$NON-NLS-1$
                                  "<inscription><w src=\"11\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">THIS</w> <w src=\"12\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">IS</w> <w src=\"13\" lemma=\"strong:G2424\" morph=\"robinson:N-NSM\">JESUS</w> <w src=\"14 15\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"16\" lemma=\"strong:G3588\" morph=\"robinson:T-GPM\"></w><w src=\"17\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.23.38")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">THIS</w> <w src=\"15\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">IS</w> <w src=\"16 17\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"19\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w>", //$NON-NLS-1$
                                  "<inscription><w src=\"14\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">THIS</w> <w src=\"15\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">IS</w> <w src=\"16 17\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"19\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("John.19.19")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"15\" lemma=\"strong:G2424\" morph=\"robinson:N-NSM\">JESUS</w> <w src=\"16 17\" lemma=\"strong:G3588 strong:G3480\" morph=\"robinson:T-NSM robinson:N-NSM\">OF NAZARETH</w> <w src=\"18 19\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"21\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w>", //$NON-NLS-1$
                                  "<inscription><w src=\"15\" lemma=\"strong:G2424\" morph=\"robinson:N-NSM\">JESUS</w> <w src=\"16 17\" lemma=\"strong:G3588 strong:G3480\" morph=\"robinson:T-NSM robinson:N-NSM\">OF NAZARETH</w> <w src=\"18 19\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"21\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w></inscription>"); //$NON-NLS-1$
        }

        // The next undoes an incorrect application of divineName
        if (osisID.equals("Acts.17.23")) //$NON-NLS-1$ 
        {
            input = replace(input, "<w src=\"14\" lemma=\"strong:G57\" morph=\"robinson:A-DSM\">TO THE UNKNOWN</w> <w src=\"15\" lemma=\"strong:G2316\" morph=\"robinson:N-DSM\"><divineName>God</divineName></w>", //$NON-NLS-1$
                                  "<inscription><w src=\"14\" lemma=\"strong:G57\" morph=\"robinson:A-DSM\">TO THE UNKNOWN</w> <w src=\"15\" lemma=\"strong:G2316\" morph=\"robinson:N-DSM\">GOD</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.19.16")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"13\" lemma=\"strong:G1125\" morph=\"robinson:V-RPP-ASN\">KING</w> <w src=\"14\" lemma=\"strong:G935\" morph=\"robinson:N-NSM\">OF KINGS</w>, <w src=\"15\" lemma=\"strong:G935\" morph=\"robinson:N-GPM\">AND</w> <w src=\"16\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\"><divineName>Lord</divineName></w> <w src=\"17\" lemma=\"strong:G2962\" morph=\"robinson:N-NSM\">OF <divineName>Lord</divineName>S</w>", //$NON-NLS-1$
                                  "<inscription><w src=\"13\" lemma=\"strong:G1125\" morph=\"robinson:V-RPP-ASN\">KING</w> <w src=\"14\" lemma=\"strong:G935\" morph=\"robinson:N-NSM\">OF KINGS</w>, <w src=\"15\" lemma=\"strong:G935\" morph=\"robinson:N-GPM\">AND</w> <w src=\"16\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">LORD</w> <w src=\"17\" lemma=\"strong:G2962\" morph=\"robinson:N-NSM\">OF LORDS</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.15.26")) //$NON-NLS-1$
        {
            input = replace(input, "<w src=\"9 10\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"12\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w>", //$NON-NLS-1$
                                  "<inscription><w src=\"9 10\" lemma=\"strong:G3588 strong:G935\" morph=\"robinson:T-NSM robinson:N-NSM\">THE KING</w> <w src=\"12\" lemma=\"strong:G2453\" morph=\"robinson:A-GPM\">OF THE JEWS</w></inscription>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.17.5")) //$NON-NLS-1$ 
        {
            input = replace(input, "<w src=\"8\" lemma=\"strong:G3466\" morph=\"robinson:N-NSN\">MYSTERY</w>, <w src=\"9\" lemma=\"strong:G897\" morph=\"robinson:N-NSF\">BABYLON</w> <w src=\"11\" lemma=\"strong:G3173\" morph=\"robinson:A-NSF\">THE GREAT</w>, <w src=\"12 13\" lemma=\"strong:G3588 strong:G3384\" morph=\"robinson:T-NSF robinson:N-NSF\">THE MOTHER</w> <w src=\"14 15\" lemma=\"strong:G3588 strong:G4204\" morph=\"robinson:T-GPF robinson:N-GPF\">OF HARLOTS</w> <w src=\"16\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">AND</w> <w src=\"17 18\" lemma=\"strong:G3588 strong:G946\" morph=\"robinson:T-GPN robinson:N-GPN\">ABOMINATIONS</w> <w src=\"19 20\" lemma=\"strong:G3588 strong:G1093\" morph=\"robinson:T-GSF robinson:N-GSF\">OF THE EARTH</w>", //$NON-NLS-1$
                                  "<inscription><w src=\"8\" lemma=\"strong:G3466\" morph=\"robinson:N-NSN\">MYSTERY</w>, <w src=\"9\" lemma=\"strong:G897\" morph=\"robinson:N-NSF\">BABYLON</w> <w src=\"11\" lemma=\"strong:G3173\" morph=\"robinson:A-NSF\">THE GREAT</w>, <w src=\"12 13\" lemma=\"strong:G3588 strong:G3384\" morph=\"robinson:T-NSF robinson:N-NSF\">THE MOTHER</w> <w src=\"14 15\" lemma=\"strong:G3588 strong:G4204\" morph=\"robinson:T-GPF robinson:N-GPF\">OF HARLOTS</w> <w src=\"16\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">AND</w> <w src=\"17 18\" lemma=\"strong:G3588 strong:G946\" morph=\"robinson:T-GPN robinson:N-GPN\">ABOMINATIONS</w> <w src=\"19 20\" lemma=\"strong:G3588 strong:G1093\" morph=\"robinson:T-GSF robinson:N-GSF\">OF THE EARTH</w></inscription>"); //$NON-NLS-1$
        }

        return input;
    }

    private String fixParagraphs(String osisID, String input, boolean inVerse)
    {
        if (input.indexOf("<p/>") != -1) //$NON-NLS-1$
        {
            input = replace(input, "<p/></transChange>", "</transChange><p/>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = replace(input, "<p/></q>", "</q><p/>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("<p/>\\s+", "<p/>"); //$NON-NLS-1$ //$NON-NLS-2$

            Matcher matcher = pPattern.matcher(input);
            while (matcher.find())
            {
                input = matcher.replaceFirst("$2<p/>"); //$NON-NLS-1$
                matcher.reset(input);
            }

            // normalize paragraph markers
            input = input.replaceAll("(<p/>\\s*)+", "<p/>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("<p/>(?=<note)", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("<p/>(?=<milestone type=\"x-strongsMarkup\")", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("<p/>(?=$)", "<milestone type=\"x-p\" subType=\"x-end\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("<p/>", "<milestone type=\"x-extra-p\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        input = input.replaceAll("<milestone type=\"x-p\"\\s*/>", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$

        boolean seenP = false;

        //  And move them from the end of a verse to the beginning of the next
        if (input.indexOf("<milestone type=\"x-p\" marker=\"\u00B6\"/>") != -1) //$NON-NLS-1$
        {
            input = input.replaceAll("<milestone type=\"x-p\" marker=\"\u00B6\"/>", ""); //$NON-NLS-1$ //$NON-NLS-2$
            moveP = true;
        }
        else if (moveP && inVerse)
        {
            Verse v = null;

            try
            {
                v = VerseFactory.fromString(osisID);
            }
            catch (NoSuchVerseException e)
            {
                // does not happen
            }

            if (v.getVerse() != 1)
            {
                if (paragraphs.contains(osisID))
                {
                    input = "<milestone type=\"x-p\" marker=\"\u00B6\"/>" + input; //$NON-NLS-1$
                }
                else
                {
                    input = "<milestone type=\"x-extra-p\"/>" + input; //$NON-NLS-1$
                }
            }
            seenP = true;
            moveP = false;
        }

        if (inVerse && paragraphs.contains(osisID) && !seenP)
        {
            input = "<milestone type=\"x-p\" subType=\"x-added\" marker=\"\u00B6\"/>" + input; //$NON-NLS-1$
        }

//        if (v.getBook() >= BibleInfo.Names.MATTHEW)
//        {
//            if (input.indexOf("x-p") != -1) //$NON-NLS-1$
//            {
//                System.err.println(osisID + ": NT paragraph: " + input); //$NON-NLS-1$
//            }
//        }

        return input;
    }

    private String fixNotes(String osisID, String input)
    {
        String original = input;
        if (osisID.equals("Deut.3.17")) //$NON-NLS-1$
            input = replace(input, "Ashdoth\u2013pisgah;", "Ashdoth\u2013pisgah:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Josh.15.3")) //$NON-NLS-1$
            input = replace(input, "Maalehacrebbim", "Maaleh\u2013acrabbim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Judg.2.9")) //$NON-NLS-1$
            input = replace(input, "Timnathhares", "Timnath\u2013heres"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("2Sam.19.6")) //$NON-NLS-1$
            input = replace(input, "in\u2026:", "In\u2026:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("1Kgs.5.18")) //$NON-NLS-1$
            input = replace(input, "stonesquares", "stonesquarers"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("1Kgs.6.38")) //$NON-NLS-1$
            input = replace(input, "through out", "throughout"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Neh.7.70")) //$NON-NLS-1$
            input = replace(input, "the Tirshatha", "The Tirshatha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.40.12")) //$NON-NLS-1$
            input = replace(input, "falleth", "faileth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.65.3")) //$NON-NLS-1$
            input = replace(input, "iniquities:", "Iniquities:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.73.22")) //$NON-NLS-1$
            input = replace(input, "before Heb. with", "before: Heb. with"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.78.31")) //$NON-NLS-1$
            input = replace(input, "Chosen\u2026:", "chosen\u2026:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.89.33")) //$NON-NLS-1$
            input = replace(input, "to fall", "to fail"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.103.8")) //$NON-NLS-1$
            input = replace(input, "plentious", "plenteous"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.106.27")) //$NON-NLS-1$
            input = replace(input, "to overthrow:", "To overthrow:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ps.106.27")) //$NON-NLS-1$
            input = replace(input, "to make them", "To make them"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Prov.18.8")) //$NON-NLS-1$
            input = replace(input, "most\u2026:", "innermost\u2026:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Prov.24.18")) //$NON-NLS-1$
            input = replace(input, "displeaseth\u2026:", "displease\u2026:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Prov.31.5")) //$NON-NLS-1$
            input = replace(input, "prevert:", "pervert:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Eccl.5.8")) //$NON-NLS-1$
            input = replace(input, "at the\u2026 Heb.", "at the\u2026: Heb."); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Eccl.5.18")) //$NON-NLS-1$
            input = replace(input, "It is good\u2026", "it is good\u2026"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Isa.8.9")) //$NON-NLS-1$
            input = replace(input, "people and:", "people, and:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Isa.11.8")) //$NON-NLS-1$
            input = replace(input, "cockatrice' or", "cockatrice': or"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Isa.34.14")) //$NON-NLS-1$
            input = replace(input, "The wild\u2026desert", "The wild beasts of the desert"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Isa.34.14")) //$NON-NLS-1$
            input = replace(input, "the wild\u2026island", "the wild beasts of the island"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Isa.59.5")) //$NON-NLS-1$
            input = replace(input, "cockatrice' or", "cockatrice': or"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Jer.50.16")) //$NON-NLS-1$
            input = replace(input, "sickle; or, scythe", "sickle: or, scythe"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ezek.43.15")) //$NON-NLS-1$
            input = replace(input, "the altar (first)", "the altar shall"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ezek.43.15")) //$NON-NLS-1$
            input = replace(input, "the altar (second)", "the altar and"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Ezek.48.23")) //$NON-NLS-1$
            input = replace(input, "A portion:", "a portion:"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Mic.5.4")) //$NON-NLS-1$
            input = replace(input, "feed or, rule", "feed: or, rule"); //$NON-NLS-1$ //$NON-NLS-2$
        if (osisID.equals("Zeph.1.2")) //$NON-NLS-1$
            input = replace(input, "I will\u2026; Heb.", "I will\u2026: Heb."); //$NON-NLS-1$ //$NON-NLS-2$
//        if (!original.equals(input))
//            System.err.println(osisID + ':' + input);
        return input;
    }

    private String replace(String input, String pattern, String replacement)
    {
        int patternLength = pattern.length();
        if (patternLength == 0)
        {
            throw new IllegalArgumentException("Pattern cannot be empty."); //$NON-NLS-1$
        }

         StringBuffer result = new StringBuffer();
         int start = 0;
         for (int match = input.indexOf(pattern, start); match >= 0; match = input.indexOf(pattern, start))
         {
           // add everything from the last match up to this one
           result.append(input.substring(start, match));
           //add the replacement
           result.append(replacement);

           // The next start is just after where the old match was
           start = match + patternLength;
         }

         // append the remainder
         result.append(input.substring(start));
         return result.toString();
    }

    private static FieldPosition pos = new FieldPosition(0);

    private static String preVerseStart = "<title subtype=\"x-preverse\" type=\"section\">"; //$NON-NLS-1$
    private static String preVerseElement = "<title subtype=\"x-preverse\" type=\"section\">(.*?)</title>"; //$NON-NLS-1$
    private static Pattern preVersePattern = Pattern.compile(preVerseElement);
//    private static String preVerseEnd = "</title>"; //$NON-NLS-1$
//    private static Pattern preVerseStartPattern = Pattern.compile(preVerseStart);
//    private static Pattern preVerseEndPattern = Pattern.compile(preVerseEnd); //$NON-NLS-1$

    private static String psalmTitleStart = "<title type=\"psalm\">"; //$NON-NLS-1$
    private static String psalmTitleElement = "<title type=\"psalm\">(.*?)</title>"; //$NON-NLS-1$
    private static Pattern psalmTitlePattern = Pattern.compile(psalmTitleElement);
//    private static String psalmTitleEnd = "</title>"; //$NON-NLS-1$
//    private static Pattern psalmTitleStartPattern = Pattern.compile(psalmTitleStart);
//    private static Pattern psalmTitleEndPattern = Pattern.compile(psalmTitleEnd);

    private static String divineNameElement = "(<divineName.*?>)(.*?)(LORD'S|LORD|GOD|JEHOVAH)(.*?)(</divineName>)"; //$NON-NLS-1$
    private static String divineNameReplace = "$2<seg><divineName>$3</divineName></seg>$4"; //$NON-NLS-1$
    private static Pattern divineNamePattern = Pattern.compile(divineNameElement);
    private static Pattern dna1Pattern = Pattern.compile("(<w\\s+lemma=\"strong:H03069\"\\s*>.*?)(GOD|LORD|LORD'S)(.*?</w>)"); //$NON-NLS-1$
    private static Pattern dna2Pattern = Pattern.compile("(<w\\s+lemma=\"strong:H03050\"\\s*>.*?)(GOD|LORD|LORD'S|JAH)(.*?</w>)"); //$NON-NLS-1$
    private static String dna1Replace = "$1<seg><divineName>$2</divineName></seg>$3"; //$NON-NLS-1$
//    private static Pattern dna3Pattern = Pattern.compile("(<transChange.*?>)(.*?)(GOD|LORD|LORD'S|JAH)(.*?)(</transChange>)"); //$NON-NLS-1$
//    private static Pattern dna4Pattern = Pattern.compile("(<note.*?>)(.*?)(GOD|LORD|LORD'S|JAH|JEHOVAH)(.*?)(</note>)"); //$NON-NLS-1$
//    private static String dna2Replace = "$1$2<divineName>$3</divineName>$4$5"; //$NON-NLS-1$
    private static Pattern dn1Pattern = Pattern.compile("<divineName>LORD</divineName>"); //$NON-NLS-1$
    private static String dn1Replace = "<divineName>Lord</divineName>"; //$NON-NLS-1$
    private static Pattern dn2Pattern = Pattern.compile("<divineName>LORD'S</divineName>"); //$NON-NLS-1$
    private static String dn2Replace = "<divineName>Lord's</divineName>"; //$NON-NLS-1$
    private static Pattern dn3Pattern = Pattern.compile("<divineName>GOD</divineName>"); //$NON-NLS-1$
    private static String dn3Replace = "<divineName>God</divineName>"; //$NON-NLS-1$
    private static Pattern dn4Pattern = Pattern.compile("<divineName>JEHOVAH</divineName>"); //$NON-NLS-1$
    private static String dn4Replace = "<divineName>Jehovah</divineName>"; //$NON-NLS-1$
    private static Pattern dn5Pattern = Pattern.compile("<divineName>JAH</divineName>"); //$NON-NLS-1$
    private static String dn5Replace = "<divineName>Jah</divineName>"; //$NON-NLS-1$
    private static Pattern dn6Pattern = Pattern.compile("LORD'S"); //$NON-NLS-1$
    private static String dn6Replace = "<divineName>Lord's</divineName>"; //$NON-NLS-1$
    private static Pattern dn7Pattern = Pattern.compile("LORD"); //$NON-NLS-1$
    private static String dn7Replace = "<divineName>Lord</divineName>"; //$NON-NLS-1$
    private static Pattern dn8Pattern = Pattern.compile("GOD"); //$NON-NLS-1$
    private static String dn8Replace = "<divineName>God</divineName>"; //$NON-NLS-1$
    private static Pattern dn9Pattern = Pattern.compile("JEHOVAH"); //$NON-NLS-1$
    private static String dn9Replace = "<divineName>Jehovah</divineName>"; //$NON-NLS-1$
    private static Pattern dn10Pattern = Pattern.compile("JAH"); //$NON-NLS-1$
    private static String dn10Replace = "<divineName>Jah</divineName>"; //$NON-NLS-1$
//    private static Pattern dn11Pattern = Pattern.compile("(BRANCH)"); //$NON-NLS-1$
//    private static String dn11Replace = "<seg><divineName>Branch</divineName></seg>"; //$NON-NLS-1$

    private static String transChangeSeg = "<seg type=\"transChange\" subType=\"type:added\">([^<]*)</seg>"; //$NON-NLS-1$
    private static Pattern transChangeSegPattern = Pattern.compile(transChangeSeg);

    private static String badNote = "<note type=\"[^\"]*\" (name=\"([^\"]*)\" date=\"([^\"]*)\"/)>([^<]*)</note>"; //$NON-NLS-1$
    private static Pattern badNotePattern = Pattern.compile(badNote);

    private static String respElement = "<resp.*?name=\"(.*?)\".*?date=\"(.*?)\".*?>"; //$NON-NLS-1$
    private static Pattern respPattern = Pattern.compile(respElement);

    private static Pattern pPattern = Pattern.compile("(<p/>\\s*)+(<w[^>]+></w>)\\s*"); //$NON-NLS-1$

    private static String wElement = "<w\\s[^>]*>"; //$NON-NLS-1$
    private static Pattern wPattern = Pattern.compile(wElement);
    private static Pattern srcPattern = Pattern.compile("src=\"([^\"]*)\""); //$NON-NLS-1$
    private static Pattern morphNPattern = Pattern.compile("morph=\"robinson:N-([^\"]*)\""); //$NON-NLS-1$
    private static Pattern morphTPattern = Pattern.compile("morph=\"robinson:T-([^\"]*)\""); //$NON-NLS-1$

    private static Pattern a1Pattern = Pattern.compile("(\\w+s')(\\w\\w+)"); //$NON-NLS-1$
    private static Pattern a2Pattern = Pattern.compile("(LORD')(</w>)"); //$NON-NLS-1$
    private static Pattern a3Pattern = Pattern.compile("(\\w*[^s]')(</w>|</note>|</seg>|</transChange>)"); //$NON-NLS-1$
    private static Pattern a4Pattern = Pattern.compile("(\\w*[^s])</w>('s)"); //$NON-NLS-1$
    private static Pattern a5Pattern = Pattern.compile("(LORD') "); //$NON-NLS-1$
    private static Pattern a6Pattern = Pattern.compile("(\\w*[^s>]') "); //$NON-NLS-1$
    private static Pattern a7Pattern = Pattern.compile("(LORD')([.:])"); //$NON-NLS-1$
    private static Pattern a8Pattern = Pattern.compile("(\\w+[^s>]')([.:])"); //$NON-NLS-1$
    private static Pattern a9Pattern = Pattern.compile("(\\w+)</w>'<"); //$NON-NLS-1$
    private static Pattern a10Pattern = Pattern.compile("(\\w+)</w>'(s\\w+)"); //$NON-NLS-1$
    private static Pattern a11Pattern = Pattern.compile("</w>(<w[^>]*>)('s?) "); //$NON-NLS-1$
    private static Pattern a12Pattern = Pattern.compile("</w>' "); //$NON-NLS-1$
    private static Pattern a13Pattern = Pattern.compile("</w>'"); //$NON-NLS-1$
    private static Pattern a14Pattern = Pattern.compile("(\\w+[^Ss]')</w>(.)"); //$NON-NLS-1$
    private static Pattern a15Pattern = Pattern.compile("(husband') "); //$NON-NLS-1$
    private static Pattern a16Pattern = Pattern.compile("(cockatrice')s"); //$NON-NLS-1$
    private static Pattern a17Pattern = Pattern.compile("(ass')([^s])"); //$NON-NLS-1$
//    private static Pattern axPattern = Pattern.compile(".....[sS]'[^sS< \\.].........."); //$NON-NLS-1$

    private static Pattern w1Pattern = Pattern.compile("\\s([,;:.?!])"); //$NON-NLS-1$
    private static Pattern w4Pattern = Pattern.compile("[\n\r\t]"); //$NON-NLS-1$
    private static Pattern w5Pattern = Pattern.compile("([!\"#$%&()*+,-./:;=?@^_`{|}~ ]+)(</w>|</transChange>)"); //$NON-NLS-1$
    private static Pattern w6Pattern = Pattern.compile("(<w\\s[^>]*>|<transChange\\s[^>]*>)([!\"#$%&'()*+,-./:;=?@^_`{|}~ ]+)"); //$NON-NLS-1$
    private static Pattern w7Pattern = Pattern.compile("(<w\\s[^>]*></w>)([!\"#$%&'()*+,-./:;=?@^_`{|}~ ]+)"); //$NON-NLS-1$
    private static Pattern w2Pattern = Pattern.compile("\\s\\)"); //$NON-NLS-1$
    private static Pattern w3Pattern = Pattern.compile("\\(\\s"); //$NON-NLS-1$
    private static Pattern w8Pattern = Pattern.compile("(<milestone type=\"x-p\" marker=\"\u00B6\"/>)\\s+"); //$NON-NLS-1$
    private static Pattern w9Pattern = Pattern.compile("(<title\\s[^>]*>)\\s+"); //$NON-NLS-1$
    private static Pattern w10Pattern = Pattern.compile("(</title>) "); //$NON-NLS-1$
    private static Pattern w11Pattern = Pattern.compile(" (<note)"); //$NON-NLS-1$
    private static Pattern w12Pattern = Pattern.compile("\\s+(</note>)"); //$NON-NLS-1$
    private static Pattern wnPattern = Pattern.compile("\\s\\s+"); //$NON-NLS-1$
    private static Pattern p1Pattern = Pattern.compile("\\.\\.\\."); //$NON-NLS-1$

    private static Map bookTitles = new HashMap();

    static {
        bookTitles.put("Gen", "THE FIRST BOOK OF MOSES CALLED GENESIS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Exod", "THE SECOND BOOK OF MOSES CALLED EXODUS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Lev", "THE THIRD BOOK OF MOSES CALLED LEVITICUS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Num", "THE FOURTH BOOK OF MOSES CALLED NUMBERS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Deut", "THE FIFTH BOOK OF MOSES CALLED DEUTERONOMY"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Josh", "THE BOOK OF JOSHUA"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Judg", "THE BOOK OF JUDGES"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Ruth", "THE BOOK OF RUTH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Sam", "THE FIRST BOOK OF SAMUEL OTHERWISE CALLED THE FIRST BOOK OF THE KINGS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Sam", "THE SECOND BOOK OF SAMUEL OTHERWISE CALLED THE SECOND BOOK OF THE KINGS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Kgs", "THE FIRST BOOK OF THE KINGS COMMONLY CALLED THE THIRD BOOK OF THE KINGS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Kgs", "THE SECOND BOOK OF THE KINGS COMMONLY CALLED THE FOURTH BOOK OF THE KINGS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Chr", "THE FIRST BOOK OF THE CHRONICLES"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Chr", "THE SECOND BOOK OF THE CHRONICLES"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Ezra", "EZRA"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Neh", "THE BOOK OF NEHEMIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Esth", "THE BOOK OF ESTHER"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Job", "THE BOOK OF JOB"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Ps", "THE BOOK OF PSALMS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Prov", "THE PROVERBS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Eccl", "ECCLESIASTES OR, THE PREACHER"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Song", "THE SONG OF SOLOMON"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Isa", "THE BOOK OF THE PROPHET ISAIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Jer", "THE BOOK OF THE PROPHET JEREMIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Lam", "THE LAMENTATIONS OF JEREMIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Ezek", "THE BOOK OF THE PROPHET EZEKIEL"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Dan", "THE BOOK OF DANIEL"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Hos", "HOSEA"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Joel", "JOEL"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Amos", "AMOS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Obad", "OBADIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Jonah", "JONAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Mic", "MICAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Nah", "NAHUM"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Hab", "HABAKKUK"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Zeph", "ZEPHANIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Hag", "HAGGAI"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Zech", "ZECHARIAH"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Mal", "MALACHI"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Matt", "THE GOSPEL ACCORDING TO <abbr expansion=\"Saint\">ST.</abbr> MATTHEW"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Mark", "THE GOSPEL ACCORDING TO <abbr expansion=\"Saint\">ST.</abbr> MARK"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Luke", "THE GOSPEL ACCORDING TO <abbr expansion=\"Saint\">ST.</abbr> LUKE"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("John", "THE GOSPEL ACCORDING TO <abbr expansion=\"Saint\">ST.</abbr> JOHN"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Acts", "THE ACTS OF THE APOSTLES"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Rom", "THE EPISTLE OF PAUL THE APOSTLE TO THE ROMANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Cor", "THE FIRST EPISTLE OF PAUL THE APOSTLE TO THE CORINTHIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Cor", "THE SECOND EPISTLE OF PAUL THE APOSTLE TO THE CORINTHIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Gal", "THE EPISTLE OF PAUL THE APOSTLE TO THE GALATIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Eph", "THE EPISTLE OF PAUL THE APOSTLE TO THE EPHESIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Phil", "THE EPISTLE OF PAUL THE APOSTLE TO THE PHILIPPIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Col", "THE EPISTLE OF PAUL THE APOSTLE TO THE COLOSSIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Thess", "THE FIRST EPISTLE OF PAUL THE APOSTLE TO THE THESSALONIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Thess", "THE SECOND EPISTLE OF PAUL THE APOSTLE TO THE THESSALONIANS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Tim", "THE FIRST EPISTLE OF PAUL THE APOSTLE TO TIMOTHY"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Tim", "THE SECOND EPISTLE OF PAUL THE APOSTLE TO TIMOTHY"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Titus", "THE EPISTLE OF PAUL THE APOSTLE TO TITUS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Phlm", "THE EPISTLE OF PAUL THE APOSTLE TO PHILEMON"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Heb", "THE EPISTLE OF PAUL THE APOSTLE TO THE HEBREWS"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Jas", "THE GENERAL EPISTLE OF JAMES"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1Pet", "THE FIRST EPISTLE GENERAL OF PETER"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2Pet", "THE SECOND EPISTLE GENERAL OF PETER"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("1John", "THE FIRST EPISTLE GENERAL OF JOHN"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("2John", "THE SECOND EPISTLE OF JOHN"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("3John", "THE THIRD EPISTLE OF JOHN"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Jude", "THE GENERAL EPISTLE OF JUDE"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Rev", "THE REVELATION OF <abbr expansion=\"Saint\">ST.</abbr> JOHN THE DIVINE"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static Map colophons = new HashMap();

    static {
        colophons.put("Rom", "<div type=\"colophon\" osisID=\"Rom.c\">Written to the Romans from Corinthus, <transChange type=\"added\">and sent</transChange> by Phebe servant of the church at Cenchrea.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("1Cor", "<div type=\"colophon\" osisID=\"1Cor.c\">The first <transChange type=\"added\">epistle</transChange> to the Corinthians was written from Philippi by Stephanas, and Fortunatus, and Achaicus, and Timotheus.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("2Cor", "<div type=\"colophon\" osisID=\"2Cor.c\">The second <transChange type=\"added\">epistle</transChange> to the Corinthians was written from Philippi, <transChange type=\"added\">a city</transChange> of Macedonia, by Titus and Lucas.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Gal", "<div type=\"colophon\" osisID=\"Gal.c\">Unto the Galatians written from Rome.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Eph", "<div type=\"colophon\" osisID=\"Eph.c\">Written from Rome unto the Ephesians by Tychicus.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Phil", "<div type=\"colophon\" osisID=\"Phil.c\">It was written to the Philippians from Rome by Epaphroditus.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Col", "<div type=\"colophon\" osisID=\"Col.c\">Written from Rome to the Colossians by Tychicus and Onesimus.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("1Thess", "<div type=\"colophon\" osisID=\"1Thess.c\">The first <transChange type=\"added\">epistle</transChange> unto the Thessalonians was written from Athens.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("2Thess", "<div type=\"colophon\" osisID=\"2Thess.c\">The second <transChange type=\"added\">epistle</transChange> to the Thessalonians was written from Athens.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("1Tim", "<div type=\"colophon\" osisID=\"1Tim.c\">The first to Timothy was written from Laodicea, which is the chiefest city of Phrygia Pacatiana.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("2Tim", "<div type=\"colophon\" osisID=\"2Tim.c\">The second <transChange type=\"added\">epistle</transChange> unto Timotheus, ordained the first bishop of the church of the Ephesians, was written from Rome, when Paul was brought before Nero the second time.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Titus", "<div type=\"colophon\" osisID=\"Titus.c\">It was written to Titus, ordained the first bishop of the church of the Cretians, from Nicopolis of Macedonia.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Phlm", "<div type=\"colophon\" osisID=\"Phlm.c\">Written from Rome to Philemon, by Onesimus, a servant.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
        colophons.put("Heb", "<div type=\"colophon\" osisID=\"Heb.c\">Written to the Hebrews from Italy by Timothy.</div>\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static Map acrostics = new HashMap();
    private static Map hebLetters = new HashMap();

    static {
        acrostics.put("Ps.119.1", "<foreign n=\"\u05D0\">ALEPH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.9", "<foreign n=\"\u05D1\">BETH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.17", "<foreign n=\"\u05D2\">GIMEL.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.25", "<foreign n=\"\u05D3\">DALETH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.33", "<foreign n=\"\u05D4\">HE.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.41", "<foreign n=\"\u05D5\">VAU.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.49", "<foreign n=\"\u05D6\">ZAIN.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.57", "<foreign n=\"\u05D7\">CHETH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.65", "<foreign n=\"\u05D8\">TETH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.73", "<foreign n=\"\u05D9\">JOD.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.81", "<foreign n=\"\u05DB\">CAPH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.89", "<foreign n=\"\u05DC\">LAMED.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.97", "<foreign n=\"\u05DE\">MEM.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.105", "<foreign n=\"\u05E0\">NUN.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.113", "<foreign n=\"\u05E1\">SAMECH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.121", "<foreign n=\"\u05E2\">AIN.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.129", "<foreign n=\"\u05E4\">PE.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.137", "<foreign n=\"\u05E6\">TZADDI.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.145", "<foreign n=\"\u05E7\">KOPH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.153", "<foreign n=\"\u05E8\">RESH.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.161", "<foreign n=\"\u05E9\">SCHIN.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.169", "<foreign n=\"\u05Ea\">TAU.</foreign>"); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.1", "ALEPH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.9", "BETH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.17", "GIMEL. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.25", "DALETH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.33", "HE. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.41", "VAU. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.49", "ZAIN. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.57", "CHETH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.65", "TETH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.73", "JOD. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.81", "CAPH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.89", "LAMED. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.97", "MEM. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.105", "NUN. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.113", "SAMECH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.121", "AIN. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.129", "PE. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.137", "TZADDI. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.145", "KOPH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.153", "RESH. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.161", "SCHIN. "); //$NON-NLS-1$ //$NON-NLS-2$
        hebLetters.put("Ps.119.169", "TAU. "); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private boolean moveP;
    private static Set paragraphs = new HashSet();

    static {
        paragraphs.add("Gen.1.6"); //$NON-NLS-1$
        paragraphs.add("Gen.1.9"); //$NON-NLS-1$
        paragraphs.add("Gen.1.14"); //$NON-NLS-1$
        paragraphs.add("Gen.1.24"); //$NON-NLS-1$
        paragraphs.add("Gen.1.26"); //$NON-NLS-1$
        paragraphs.add("Gen.1.29"); //$NON-NLS-1$
        paragraphs.add("Gen.2.4"); //$NON-NLS-1$
        paragraphs.add("Gen.2.8"); //$NON-NLS-1$
        paragraphs.add("Gen.2.18"); //$NON-NLS-1$
        paragraphs.add("Gen.3.22"); //$NON-NLS-1$
        paragraphs.add("Gen.4.9"); //$NON-NLS-1$
        paragraphs.add("Gen.4.16"); //$NON-NLS-1$
        paragraphs.add("Gen.4.19"); //$NON-NLS-1$
        paragraphs.add("Gen.4.25"); //$NON-NLS-1$
        paragraphs.add("Gen.5.3"); //$NON-NLS-1$
        paragraphs.add("Gen.5.9"); //$NON-NLS-1$
        paragraphs.add("Gen.5.12"); //$NON-NLS-1$
        paragraphs.add("Gen.5.15"); //$NON-NLS-1$
        paragraphs.add("Gen.5.18"); //$NON-NLS-1$
        paragraphs.add("Gen.5.21"); //$NON-NLS-1$
        paragraphs.add("Gen.5.28"); //$NON-NLS-1$
        paragraphs.add("Gen.6.5"); //$NON-NLS-1$
        paragraphs.add("Gen.6.9"); //$NON-NLS-1$
        paragraphs.add("Gen.6.14"); //$NON-NLS-1$
        paragraphs.add("Gen.7.7"); //$NON-NLS-1$
        paragraphs.add("Gen.7.11"); //$NON-NLS-1$
        paragraphs.add("Gen.8.6"); //$NON-NLS-1$
        paragraphs.add("Gen.8.13"); //$NON-NLS-1$
        paragraphs.add("Gen.8.15"); //$NON-NLS-1$
        paragraphs.add("Gen.8.20"); //$NON-NLS-1$
        paragraphs.add("Gen.9.8"); //$NON-NLS-1$
        paragraphs.add("Gen.9.18"); //$NON-NLS-1$
        paragraphs.add("Gen.9.28"); //$NON-NLS-1$
        paragraphs.add("Gen.10.6"); //$NON-NLS-1$
        paragraphs.add("Gen.10.15"); //$NON-NLS-1$
        paragraphs.add("Gen.10.21"); //$NON-NLS-1$
        paragraphs.add("Gen.11.10"); //$NON-NLS-1$
        paragraphs.add("Gen.11.27"); //$NON-NLS-1$
        paragraphs.add("Gen.12.6"); //$NON-NLS-1$
        paragraphs.add("Gen.12.10"); //$NON-NLS-1$
        paragraphs.add("Gen.12.14"); //$NON-NLS-1$
        paragraphs.add("Gen.13.5"); //$NON-NLS-1$
        paragraphs.add("Gen.13.14"); //$NON-NLS-1$
        paragraphs.add("Gen.14.13"); //$NON-NLS-1$
        paragraphs.add("Gen.14.17"); //$NON-NLS-1$
        paragraphs.add("Gen.16.4"); //$NON-NLS-1$
        paragraphs.add("Gen.16.7"); //$NON-NLS-1$
        paragraphs.add("Gen.16.15"); //$NON-NLS-1$
        paragraphs.add("Gen.17.9"); //$NON-NLS-1$
        paragraphs.add("Gen.17.15"); //$NON-NLS-1$
        paragraphs.add("Gen.17.23"); //$NON-NLS-1$
        paragraphs.add("Gen.18.9"); //$NON-NLS-1$
        paragraphs.add("Gen.18.16"); //$NON-NLS-1$
        paragraphs.add("Gen.18.23"); //$NON-NLS-1$
        paragraphs.add("Gen.19.4"); //$NON-NLS-1$
        paragraphs.add("Gen.19.12"); //$NON-NLS-1$
        paragraphs.add("Gen.19.15"); //$NON-NLS-1$
        paragraphs.add("Gen.19.17"); //$NON-NLS-1$
        paragraphs.add("Gen.19.23"); //$NON-NLS-1$
        paragraphs.add("Gen.19.26"); //$NON-NLS-1$
        paragraphs.add("Gen.19.27"); //$NON-NLS-1$
        paragraphs.add("Gen.19.29"); //$NON-NLS-1$
        paragraphs.add("Gen.19.30"); //$NON-NLS-1$
        paragraphs.add("Gen.20.17"); //$NON-NLS-1$
        paragraphs.add("Gen.21.6"); //$NON-NLS-1$
        paragraphs.add("Gen.21.9"); //$NON-NLS-1$
        paragraphs.add("Gen.21.12"); //$NON-NLS-1$
        paragraphs.add("Gen.21.22"); //$NON-NLS-1$
        paragraphs.add("Gen.21.33"); //$NON-NLS-1$
        paragraphs.add("Gen.22.3"); //$NON-NLS-1$
        paragraphs.add("Gen.22.15"); //$NON-NLS-1$
        paragraphs.add("Gen.22.20"); //$NON-NLS-1$
        paragraphs.add("Gen.23.3"); //$NON-NLS-1$
        paragraphs.add("Gen.23.17"); //$NON-NLS-1$
        paragraphs.add("Gen.24.7"); //$NON-NLS-1$
        paragraphs.add("Gen.24.10"); //$NON-NLS-1$
        paragraphs.add("Gen.24.15"); //$NON-NLS-1$
        paragraphs.add("Gen.24.29"); //$NON-NLS-1$
        paragraphs.add("Gen.24.32"); //$NON-NLS-1$
        paragraphs.add("Gen.24.61"); //$NON-NLS-1$
        paragraphs.add("Gen.25.5"); //$NON-NLS-1$
        paragraphs.add("Gen.25.11"); //$NON-NLS-1$
        paragraphs.add("Gen.25.12"); //$NON-NLS-1$
        paragraphs.add("Gen.25.19"); //$NON-NLS-1$
        paragraphs.add("Gen.25.24"); //$NON-NLS-1$
        paragraphs.add("Gen.25.29"); //$NON-NLS-1$
        paragraphs.add("Gen.26.6"); //$NON-NLS-1$
        paragraphs.add("Gen.26.17"); //$NON-NLS-1$
        paragraphs.add("Gen.26.26"); //$NON-NLS-1$
        paragraphs.add("Gen.26.34"); //$NON-NLS-1$
        paragraphs.add("Gen.27.6"); //$NON-NLS-1$
        paragraphs.add("Gen.27.18"); //$NON-NLS-1$
        paragraphs.add("Gen.27.30"); //$NON-NLS-1$
        paragraphs.add("Gen.27.41"); //$NON-NLS-1$
        paragraphs.add("Gen.28.6"); //$NON-NLS-1$
        paragraphs.add("Gen.28.10"); //$NON-NLS-1$
        paragraphs.add("Gen.28.16"); //$NON-NLS-1$
        paragraphs.add("Gen.29.9"); //$NON-NLS-1$
        paragraphs.add("Gen.29.15"); //$NON-NLS-1$
        paragraphs.add("Gen.29.21"); //$NON-NLS-1$
        paragraphs.add("Gen.29.31"); //$NON-NLS-1$
        paragraphs.add("Gen.30.14"); //$NON-NLS-1$
        paragraphs.add("Gen.30.22"); //$NON-NLS-1$
        paragraphs.add("Gen.30.25"); //$NON-NLS-1$
        paragraphs.add("Gen.30.37"); //$NON-NLS-1$
        paragraphs.add("Gen.31.17"); //$NON-NLS-1$
        paragraphs.add("Gen.31.25"); //$NON-NLS-1$
        paragraphs.add("Gen.31.36"); //$NON-NLS-1$
        paragraphs.add("Gen.31.43"); //$NON-NLS-1$
        paragraphs.add("Gen.32.6"); //$NON-NLS-1$
        paragraphs.add("Gen.32.9"); //$NON-NLS-1$
        paragraphs.add("Gen.32.13"); //$NON-NLS-1$
        paragraphs.add("Gen.32.24"); //$NON-NLS-1$
        paragraphs.add("Gen.33.16"); //$NON-NLS-1$
        paragraphs.add("Gen.33.18"); //$NON-NLS-1$
        paragraphs.add("Gen.34.6"); //$NON-NLS-1$
        paragraphs.add("Gen.34.20"); //$NON-NLS-1$
        paragraphs.add("Gen.34.25"); //$NON-NLS-1$
        paragraphs.add("Gen.35.6"); //$NON-NLS-1$
        paragraphs.add("Gen.35.9"); //$NON-NLS-1$
        paragraphs.add("Gen.35.16"); //$NON-NLS-1$
        paragraphs.add("Gen.35.21"); //$NON-NLS-1$
        paragraphs.add("Gen.35.27"); //$NON-NLS-1$
        paragraphs.add("Gen.36.9"); //$NON-NLS-1$
        paragraphs.add("Gen.36.14"); //$NON-NLS-1$
        paragraphs.add("Gen.36.15"); //$NON-NLS-1$
        paragraphs.add("Gen.36.17"); //$NON-NLS-1$
        paragraphs.add("Gen.36.18"); //$NON-NLS-1$
        paragraphs.add("Gen.36.20"); //$NON-NLS-1$
        paragraphs.add("Gen.36.31"); //$NON-NLS-1$
        paragraphs.add("Gen.37.5"); //$NON-NLS-1$
        paragraphs.add("Gen.37.9"); //$NON-NLS-1$
        paragraphs.add("Gen.37.12"); //$NON-NLS-1$
        paragraphs.add("Gen.37.15"); //$NON-NLS-1$
        paragraphs.add("Gen.37.23"); //$NON-NLS-1$
        paragraphs.add("Gen.37.29"); //$NON-NLS-1$
        paragraphs.add("Gen.38.12"); //$NON-NLS-1$
        paragraphs.add("Gen.38.24"); //$NON-NLS-1$
        paragraphs.add("Gen.38.27"); //$NON-NLS-1$
        paragraphs.add("Gen.39.7"); //$NON-NLS-1$
        paragraphs.add("Gen.39.21"); //$NON-NLS-1$
        paragraphs.add("Gen.40.5"); //$NON-NLS-1$
        paragraphs.add("Gen.40.20"); //$NON-NLS-1$
        paragraphs.add("Gen.41.9"); //$NON-NLS-1$
        paragraphs.add("Gen.41.14"); //$NON-NLS-1$
        paragraphs.add("Gen.41.25"); //$NON-NLS-1$
        paragraphs.add("Gen.41.37"); //$NON-NLS-1$
        paragraphs.add("Gen.41.46"); //$NON-NLS-1$
        paragraphs.add("Gen.41.53"); //$NON-NLS-1$
        paragraphs.add("Gen.42.3"); //$NON-NLS-1$
        paragraphs.add("Gen.42.21"); //$NON-NLS-1$
        paragraphs.add("Gen.42.25"); //$NON-NLS-1$
        paragraphs.add("Gen.42.29"); //$NON-NLS-1$
        paragraphs.add("Gen.42.35"); //$NON-NLS-1$
        paragraphs.add("Gen.43.15"); //$NON-NLS-1$
        paragraphs.add("Gen.43.26"); //$NON-NLS-1$
        paragraphs.add("Gen.44.6"); //$NON-NLS-1$
        paragraphs.add("Gen.44.14"); //$NON-NLS-1$
        paragraphs.add("Gen.44.18"); //$NON-NLS-1$
        paragraphs.add("Gen.45.16"); //$NON-NLS-1$
        paragraphs.add("Gen.45.25"); //$NON-NLS-1$
        paragraphs.add("Gen.46.8"); //$NON-NLS-1$
        paragraphs.add("Gen.46.10"); //$NON-NLS-1$
        paragraphs.add("Gen.46.11"); //$NON-NLS-1$
        paragraphs.add("Gen.46.12"); //$NON-NLS-1$
        paragraphs.add("Gen.46.13"); //$NON-NLS-1$
        paragraphs.add("Gen.46.14"); //$NON-NLS-1$
        paragraphs.add("Gen.46.16"); //$NON-NLS-1$
        paragraphs.add("Gen.46.17"); //$NON-NLS-1$
        paragraphs.add("Gen.46.20"); //$NON-NLS-1$
        paragraphs.add("Gen.46.21"); //$NON-NLS-1$
        paragraphs.add("Gen.46.23"); //$NON-NLS-1$
        paragraphs.add("Gen.46.24"); //$NON-NLS-1$
        paragraphs.add("Gen.46.28"); //$NON-NLS-1$
        paragraphs.add("Gen.47.11"); //$NON-NLS-1$
        paragraphs.add("Gen.47.13"); //$NON-NLS-1$
        paragraphs.add("Gen.47.27"); //$NON-NLS-1$
        paragraphs.add("Gen.48.5"); //$NON-NLS-1$
        paragraphs.add("Gen.48.15"); //$NON-NLS-1$
        paragraphs.add("Gen.49.3"); //$NON-NLS-1$
        paragraphs.add("Gen.49.5"); //$NON-NLS-1$
        paragraphs.add("Gen.49.8"); //$NON-NLS-1$
        paragraphs.add("Gen.49.13"); //$NON-NLS-1$
        paragraphs.add("Gen.49.14"); //$NON-NLS-1$
        paragraphs.add("Gen.49.16"); //$NON-NLS-1$
        paragraphs.add("Gen.49.19"); //$NON-NLS-1$
        paragraphs.add("Gen.49.20"); //$NON-NLS-1$
        paragraphs.add("Gen.49.21"); //$NON-NLS-1$
        paragraphs.add("Gen.49.22"); //$NON-NLS-1$
        paragraphs.add("Gen.49.27"); //$NON-NLS-1$
        paragraphs.add("Gen.49.28"); //$NON-NLS-1$
        paragraphs.add("Gen.50.7"); //$NON-NLS-1$
        paragraphs.add("Gen.50.14"); //$NON-NLS-1$
        paragraphs.add("Gen.50.15"); //$NON-NLS-1$
        paragraphs.add("Gen.50.22"); //$NON-NLS-1$
        paragraphs.add("Exod.1.7"); //$NON-NLS-1$
        paragraphs.add("Exod.1.15"); //$NON-NLS-1$
        paragraphs.add("Exod.2.5"); //$NON-NLS-1$
        paragraphs.add("Exod.2.11"); //$NON-NLS-1$
        paragraphs.add("Exod.2.23"); //$NON-NLS-1$
        paragraphs.add("Exod.3.7"); //$NON-NLS-1$
        paragraphs.add("Exod.3.11"); //$NON-NLS-1$
        paragraphs.add("Exod.3.19"); //$NON-NLS-1$
        paragraphs.add("Exod.4.6"); //$NON-NLS-1$
        paragraphs.add("Exod.4.10"); //$NON-NLS-1$
        paragraphs.add("Exod.4.18"); //$NON-NLS-1$
        paragraphs.add("Exod.4.24"); //$NON-NLS-1$
        paragraphs.add("Exod.4.27"); //$NON-NLS-1$
        paragraphs.add("Exod.4.29"); //$NON-NLS-1$
        paragraphs.add("Exod.5.10"); //$NON-NLS-1$
        paragraphs.add("Exod.5.15"); //$NON-NLS-1$
        paragraphs.add("Exod.5.20"); //$NON-NLS-1$
        paragraphs.add("Exod.6.9"); //$NON-NLS-1$
        paragraphs.add("Exod.6.14"); //$NON-NLS-1$
        paragraphs.add("Exod.6.16"); //$NON-NLS-1$
        paragraphs.add("Exod.6.21"); //$NON-NLS-1$
        paragraphs.add("Exod.6.28"); //$NON-NLS-1$
        paragraphs.add("Exod.7.8"); //$NON-NLS-1$
        paragraphs.add("Exod.7.10"); //$NON-NLS-1$
        paragraphs.add("Exod.7.14"); //$NON-NLS-1$
        paragraphs.add("Exod.7.19"); //$NON-NLS-1$
        paragraphs.add("Exod.8.5"); //$NON-NLS-1$
        paragraphs.add("Exod.8.8"); //$NON-NLS-1$
        paragraphs.add("Exod.8.16"); //$NON-NLS-1$
        paragraphs.add("Exod.8.20"); //$NON-NLS-1$
        paragraphs.add("Exod.8.25"); //$NON-NLS-1$
        paragraphs.add("Exod.9.8"); //$NON-NLS-1$
        paragraphs.add("Exod.9.13"); //$NON-NLS-1$
        paragraphs.add("Exod.9.22"); //$NON-NLS-1$
        paragraphs.add("Exod.9.27"); //$NON-NLS-1$
        paragraphs.add("Exod.10.12"); //$NON-NLS-1$
        paragraphs.add("Exod.10.16"); //$NON-NLS-1$
        paragraphs.add("Exod.10.21"); //$NON-NLS-1$
        paragraphs.add("Exod.10.24"); //$NON-NLS-1$
        paragraphs.add("Exod.10.27"); //$NON-NLS-1$
        paragraphs.add("Exod.12.3"); //$NON-NLS-1$
        paragraphs.add("Exod.12.11"); //$NON-NLS-1$
        paragraphs.add("Exod.12.18"); //$NON-NLS-1$
        paragraphs.add("Exod.12.21"); //$NON-NLS-1$
        paragraphs.add("Exod.12.29"); //$NON-NLS-1$
        paragraphs.add("Exod.12.31"); //$NON-NLS-1$
        paragraphs.add("Exod.12.37"); //$NON-NLS-1$
        paragraphs.add("Exod.12.40"); //$NON-NLS-1$
        paragraphs.add("Exod.12.43"); //$NON-NLS-1$
        paragraphs.add("Exod.13.3"); //$NON-NLS-1$
        paragraphs.add("Exod.13.5"); //$NON-NLS-1$
        paragraphs.add("Exod.13.8"); //$NON-NLS-1$
        paragraphs.add("Exod.13.11"); //$NON-NLS-1$
        paragraphs.add("Exod.13.14"); //$NON-NLS-1$
        paragraphs.add("Exod.13.17"); //$NON-NLS-1$
        paragraphs.add("Exod.13.20"); //$NON-NLS-1$
        paragraphs.add("Exod.14.5"); //$NON-NLS-1$
        paragraphs.add("Exod.14.10"); //$NON-NLS-1$
        paragraphs.add("Exod.14.13"); //$NON-NLS-1$
        paragraphs.add("Exod.14.15"); //$NON-NLS-1$
        paragraphs.add("Exod.14.19"); //$NON-NLS-1$
        paragraphs.add("Exod.14.23"); //$NON-NLS-1$
        paragraphs.add("Exod.14.26"); //$NON-NLS-1$
        paragraphs.add("Exod.15.20"); //$NON-NLS-1$
        paragraphs.add("Exod.15.23"); //$NON-NLS-1$
        paragraphs.add("Exod.15.27"); //$NON-NLS-1$
        paragraphs.add("Exod.16.4"); //$NON-NLS-1$
        paragraphs.add("Exod.16.9"); //$NON-NLS-1$
        paragraphs.add("Exod.16.11"); //$NON-NLS-1$
        paragraphs.add("Exod.16.16"); //$NON-NLS-1$
        paragraphs.add("Exod.16.22"); //$NON-NLS-1$
        paragraphs.add("Exod.16.27"); //$NON-NLS-1$
        paragraphs.add("Exod.16.32"); //$NON-NLS-1$
        paragraphs.add("Exod.17.8"); //$NON-NLS-1$
        paragraphs.add("Exod.18.7"); //$NON-NLS-1$
        paragraphs.add("Exod.18.13"); //$NON-NLS-1$
        paragraphs.add("Exod.18.27"); //$NON-NLS-1$
        paragraphs.add("Exod.19.7"); //$NON-NLS-1$
        paragraphs.add("Exod.19.10"); //$NON-NLS-1$
        paragraphs.add("Exod.19.14"); //$NON-NLS-1$
        paragraphs.add("Exod.19.16"); //$NON-NLS-1$
        paragraphs.add("Exod.20.12"); //$NON-NLS-1$
        paragraphs.add("Exod.20.18"); //$NON-NLS-1$
        paragraphs.add("Exod.20.22"); //$NON-NLS-1$
        paragraphs.add("Exod.20.24"); //$NON-NLS-1$
        paragraphs.add("Exod.21.7"); //$NON-NLS-1$
        paragraphs.add("Exod.21.12"); //$NON-NLS-1$
        paragraphs.add("Exod.21.15"); //$NON-NLS-1$
        paragraphs.add("Exod.21.16"); //$NON-NLS-1$
        paragraphs.add("Exod.21.17"); //$NON-NLS-1$
        paragraphs.add("Exod.21.18"); //$NON-NLS-1$
        paragraphs.add("Exod.21.20"); //$NON-NLS-1$
        paragraphs.add("Exod.21.22"); //$NON-NLS-1$
        paragraphs.add("Exod.21.26"); //$NON-NLS-1$
        paragraphs.add("Exod.21.28"); //$NON-NLS-1$
        paragraphs.add("Exod.21.33"); //$NON-NLS-1$
        paragraphs.add("Exod.21.35"); //$NON-NLS-1$
        paragraphs.add("Exod.22.2"); //$NON-NLS-1$
        paragraphs.add("Exod.22.5"); //$NON-NLS-1$
        paragraphs.add("Exod.22.6"); //$NON-NLS-1$
        paragraphs.add("Exod.22.7"); //$NON-NLS-1$
        paragraphs.add("Exod.22.14"); //$NON-NLS-1$
        paragraphs.add("Exod.22.16"); //$NON-NLS-1$
        paragraphs.add("Exod.22.18"); //$NON-NLS-1$
        paragraphs.add("Exod.22.19"); //$NON-NLS-1$
        paragraphs.add("Exod.22.20"); //$NON-NLS-1$
        paragraphs.add("Exod.22.21"); //$NON-NLS-1$
        paragraphs.add("Exod.22.22"); //$NON-NLS-1$
        paragraphs.add("Exod.22.25"); //$NON-NLS-1$
        paragraphs.add("Exod.22.28"); //$NON-NLS-1$
        paragraphs.add("Exod.22.29"); //$NON-NLS-1$
        paragraphs.add("Exod.22.31"); //$NON-NLS-1$
        paragraphs.add("Exod.23.2"); //$NON-NLS-1$
        paragraphs.add("Exod.23.3"); //$NON-NLS-1$
        paragraphs.add("Exod.23.4"); //$NON-NLS-1$
        paragraphs.add("Exod.23.8"); //$NON-NLS-1$
        paragraphs.add("Exod.23.9"); //$NON-NLS-1$
        paragraphs.add("Exod.23.14"); //$NON-NLS-1$
        paragraphs.add("Exod.23.20"); //$NON-NLS-1$
        paragraphs.add("Exod.23.26"); //$NON-NLS-1$
        paragraphs.add("Exod.24.3"); //$NON-NLS-1$
        paragraphs.add("Exod.24.9"); //$NON-NLS-1$
        paragraphs.add("Exod.24.12"); //$NON-NLS-1$
        paragraphs.add("Exod.25.10"); //$NON-NLS-1$
        paragraphs.add("Exod.25.23"); //$NON-NLS-1$
        paragraphs.add("Exod.25.31"); //$NON-NLS-1$
        paragraphs.add("Exod.26.7"); //$NON-NLS-1$
        paragraphs.add("Exod.26.15"); //$NON-NLS-1$
        paragraphs.add("Exod.26.26"); //$NON-NLS-1$
        paragraphs.add("Exod.26.31"); //$NON-NLS-1$
        paragraphs.add("Exod.26.33"); //$NON-NLS-1$
        paragraphs.add("Exod.27.9"); //$NON-NLS-1$
        paragraphs.add("Exod.27.12"); //$NON-NLS-1$
        paragraphs.add("Exod.27.16"); //$NON-NLS-1$
        paragraphs.add("Exod.27.18"); //$NON-NLS-1$
        paragraphs.add("Exod.27.20"); //$NON-NLS-1$
        paragraphs.add("Exod.28.6"); //$NON-NLS-1$
        paragraphs.add("Exod.28.13"); //$NON-NLS-1$
        paragraphs.add("Exod.28.15"); //$NON-NLS-1$
        paragraphs.add("Exod.28.22"); //$NON-NLS-1$
        paragraphs.add("Exod.28.26"); //$NON-NLS-1$
        paragraphs.add("Exod.28.30"); //$NON-NLS-1$
        paragraphs.add("Exod.28.31"); //$NON-NLS-1$
        paragraphs.add("Exod.28.33"); //$NON-NLS-1$
        paragraphs.add("Exod.28.36"); //$NON-NLS-1$
        paragraphs.add("Exod.28.39"); //$NON-NLS-1$
        paragraphs.add("Exod.28.40"); //$NON-NLS-1$
        paragraphs.add("Exod.29.15"); //$NON-NLS-1$
        paragraphs.add("Exod.29.19"); //$NON-NLS-1$
        paragraphs.add("Exod.29.29"); //$NON-NLS-1$
        paragraphs.add("Exod.29.31"); //$NON-NLS-1$
        paragraphs.add("Exod.29.38"); //$NON-NLS-1$
        paragraphs.add("Exod.29.45"); //$NON-NLS-1$
        paragraphs.add("Exod.30.11"); //$NON-NLS-1$
        paragraphs.add("Exod.30.17"); //$NON-NLS-1$
        paragraphs.add("Exod.30.22"); //$NON-NLS-1$
        paragraphs.add("Exod.30.34"); //$NON-NLS-1$
        paragraphs.add("Exod.31.12"); //$NON-NLS-1$
        paragraphs.add("Exod.31.18"); //$NON-NLS-1$
        paragraphs.add("Exod.32.7"); //$NON-NLS-1$
        paragraphs.add("Exod.32.15"); //$NON-NLS-1$
        paragraphs.add("Exod.32.19"); //$NON-NLS-1$
        paragraphs.add("Exod.32.25"); //$NON-NLS-1$
        paragraphs.add("Exod.32.30"); //$NON-NLS-1$
        paragraphs.add("Exod.33.4"); //$NON-NLS-1$
        paragraphs.add("Exod.33.12"); //$NON-NLS-1$
        paragraphs.add("Exod.34.4"); //$NON-NLS-1$
        paragraphs.add("Exod.34.10"); //$NON-NLS-1$
        paragraphs.add("Exod.34.18"); //$NON-NLS-1$
        paragraphs.add("Exod.34.21"); //$NON-NLS-1$
        paragraphs.add("Exod.34.22"); //$NON-NLS-1$
        paragraphs.add("Exod.34.23"); //$NON-NLS-1$
        paragraphs.add("Exod.34.29"); //$NON-NLS-1$
        paragraphs.add("Exod.35.4"); //$NON-NLS-1$
        paragraphs.add("Exod.35.20"); //$NON-NLS-1$
        paragraphs.add("Exod.35.30"); //$NON-NLS-1$
        paragraphs.add("Exod.36.5"); //$NON-NLS-1$
        paragraphs.add("Exod.36.8"); //$NON-NLS-1$
        paragraphs.add("Exod.36.14"); //$NON-NLS-1$
        paragraphs.add("Exod.36.20"); //$NON-NLS-1$
        paragraphs.add("Exod.36.31"); //$NON-NLS-1$
        paragraphs.add("Exod.36.35"); //$NON-NLS-1$
        paragraphs.add("Exod.36.37"); //$NON-NLS-1$
        paragraphs.add("Exod.37.6"); //$NON-NLS-1$
        paragraphs.add("Exod.37.10"); //$NON-NLS-1$
        paragraphs.add("Exod.37.17"); //$NON-NLS-1$
        paragraphs.add("Exod.37.25"); //$NON-NLS-1$
        paragraphs.add("Exod.37.29"); //$NON-NLS-1$
        paragraphs.add("Exod.38.8"); //$NON-NLS-1$
        paragraphs.add("Exod.38.9"); //$NON-NLS-1$
        paragraphs.add("Exod.38.21"); //$NON-NLS-1$
        paragraphs.add("Exod.39.6"); //$NON-NLS-1$
        paragraphs.add("Exod.39.8"); //$NON-NLS-1$
        paragraphs.add("Exod.39.22"); //$NON-NLS-1$
        paragraphs.add("Exod.39.27"); //$NON-NLS-1$
        paragraphs.add("Exod.39.30"); //$NON-NLS-1$
        paragraphs.add("Exod.39.32"); //$NON-NLS-1$
        paragraphs.add("Exod.39.33"); //$NON-NLS-1$
        paragraphs.add("Exod.40.17"); //$NON-NLS-1$
        paragraphs.add("Exod.40.20"); //$NON-NLS-1$
        paragraphs.add("Exod.40.22"); //$NON-NLS-1$
        paragraphs.add("Exod.40.24"); //$NON-NLS-1$
        paragraphs.add("Exod.40.26"); //$NON-NLS-1$
        paragraphs.add("Exod.40.28"); //$NON-NLS-1$
        paragraphs.add("Exod.40.30"); //$NON-NLS-1$
        paragraphs.add("Exod.40.34"); //$NON-NLS-1$
        paragraphs.add("Lev.1.10"); //$NON-NLS-1$
        paragraphs.add("Lev.1.14"); //$NON-NLS-1$
        paragraphs.add("Lev.2.4"); //$NON-NLS-1$
        paragraphs.add("Lev.2.5"); //$NON-NLS-1$
        paragraphs.add("Lev.2.7"); //$NON-NLS-1$
        paragraphs.add("Lev.2.12"); //$NON-NLS-1$
        paragraphs.add("Lev.3.6"); //$NON-NLS-1$
        paragraphs.add("Lev.3.12"); //$NON-NLS-1$
        paragraphs.add("Lev.4.13"); //$NON-NLS-1$
        paragraphs.add("Lev.4.22"); //$NON-NLS-1$
        paragraphs.add("Lev.4.27"); //$NON-NLS-1$
        paragraphs.add("Lev.5.11"); //$NON-NLS-1$
        paragraphs.add("Lev.5.14"); //$NON-NLS-1$
        paragraphs.add("Lev.5.17"); //$NON-NLS-1$
        paragraphs.add("Lev.6.8"); //$NON-NLS-1$
        paragraphs.add("Lev.6.14"); //$NON-NLS-1$
        paragraphs.add("Lev.6.19"); //$NON-NLS-1$
        paragraphs.add("Lev.6.24"); //$NON-NLS-1$
        paragraphs.add("Lev.7.22"); //$NON-NLS-1$
        paragraphs.add("Lev.7.28"); //$NON-NLS-1$
        paragraphs.add("Lev.7.35"); //$NON-NLS-1$
        paragraphs.add("Lev.8.18"); //$NON-NLS-1$
        paragraphs.add("Lev.8.22"); //$NON-NLS-1$
        paragraphs.add("Lev.8.31"); //$NON-NLS-1$
        paragraphs.add("Lev.9.5"); //$NON-NLS-1$
        paragraphs.add("Lev.9.8"); //$NON-NLS-1$
        paragraphs.add("Lev.9.15"); //$NON-NLS-1$
        paragraphs.add("Lev.10.8"); //$NON-NLS-1$
        paragraphs.add("Lev.10.12"); //$NON-NLS-1$
        paragraphs.add("Lev.10.16"); //$NON-NLS-1$
        paragraphs.add("Lev.11.9"); //$NON-NLS-1$
        paragraphs.add("Lev.11.13"); //$NON-NLS-1$
        paragraphs.add("Lev.11.29"); //$NON-NLS-1$
        paragraphs.add("Lev.13.9"); //$NON-NLS-1$
        paragraphs.add("Lev.13.18"); //$NON-NLS-1$
        paragraphs.add("Lev.13.24"); //$NON-NLS-1$
        paragraphs.add("Lev.13.29"); //$NON-NLS-1$
        paragraphs.add("Lev.13.38"); //$NON-NLS-1$
        paragraphs.add("Lev.13.47"); //$NON-NLS-1$
        paragraphs.add("Lev.14.33"); //$NON-NLS-1$
        paragraphs.add("Lev.15.19"); //$NON-NLS-1$
        paragraphs.add("Lev.16.15"); //$NON-NLS-1$
        paragraphs.add("Lev.16.20"); //$NON-NLS-1$
        paragraphs.add("Lev.16.29"); //$NON-NLS-1$
        paragraphs.add("Lev.17.8"); //$NON-NLS-1$
        paragraphs.add("Lev.17.10"); //$NON-NLS-1$
        paragraphs.add("Lev.18.6"); //$NON-NLS-1$
        paragraphs.add("Lev.19.3"); //$NON-NLS-1$
        paragraphs.add("Lev.19.4"); //$NON-NLS-1$
        paragraphs.add("Lev.19.5"); //$NON-NLS-1$
        paragraphs.add("Lev.19.9"); //$NON-NLS-1$
        paragraphs.add("Lev.19.11"); //$NON-NLS-1$
        paragraphs.add("Lev.19.12"); //$NON-NLS-1$
        paragraphs.add("Lev.19.13"); //$NON-NLS-1$
        paragraphs.add("Lev.19.14"); //$NON-NLS-1$
        paragraphs.add("Lev.19.15"); //$NON-NLS-1$
        paragraphs.add("Lev.19.16"); //$NON-NLS-1$
        paragraphs.add("Lev.19.17"); //$NON-NLS-1$
        paragraphs.add("Lev.19.18"); //$NON-NLS-1$
        paragraphs.add("Lev.19.19"); //$NON-NLS-1$
        paragraphs.add("Lev.19.20"); //$NON-NLS-1$
        paragraphs.add("Lev.19.23"); //$NON-NLS-1$
        paragraphs.add("Lev.19.26"); //$NON-NLS-1$
        paragraphs.add("Lev.19.29"); //$NON-NLS-1$
        paragraphs.add("Lev.19.30"); //$NON-NLS-1$
        paragraphs.add("Lev.19.31"); //$NON-NLS-1$
        paragraphs.add("Lev.19.32"); //$NON-NLS-1$
        paragraphs.add("Lev.19.33"); //$NON-NLS-1$
        paragraphs.add("Lev.19.35"); //$NON-NLS-1$
        paragraphs.add("Lev.20.6"); //$NON-NLS-1$
        paragraphs.add("Lev.20.7"); //$NON-NLS-1$
        paragraphs.add("Lev.20.9"); //$NON-NLS-1$
        paragraphs.add("Lev.20.10"); //$NON-NLS-1$
        paragraphs.add("Lev.20.22"); //$NON-NLS-1$
        paragraphs.add("Lev.20.27"); //$NON-NLS-1$
        paragraphs.add("Lev.21.9"); //$NON-NLS-1$
        paragraphs.add("Lev.21.16"); //$NON-NLS-1$
        paragraphs.add("Lev.22.14"); //$NON-NLS-1$
        paragraphs.add("Lev.22.17"); //$NON-NLS-1$
        paragraphs.add("Lev.22.26"); //$NON-NLS-1$
        paragraphs.add("Lev.23.4"); //$NON-NLS-1$
        paragraphs.add("Lev.23.9"); //$NON-NLS-1$
        paragraphs.add("Lev.23.15"); //$NON-NLS-1$
        paragraphs.add("Lev.23.22"); //$NON-NLS-1$
        paragraphs.add("Lev.23.23"); //$NON-NLS-1$
        paragraphs.add("Lev.23.26"); //$NON-NLS-1$
        paragraphs.add("Lev.23.33"); //$NON-NLS-1$
        paragraphs.add("Lev.24.5"); //$NON-NLS-1$
        paragraphs.add("Lev.24.10"); //$NON-NLS-1$
        paragraphs.add("Lev.24.17"); //$NON-NLS-1$
        paragraphs.add("Lev.24.23"); //$NON-NLS-1$
        paragraphs.add("Lev.25.8"); //$NON-NLS-1$
        paragraphs.add("Lev.25.18"); //$NON-NLS-1$
        paragraphs.add("Lev.25.23"); //$NON-NLS-1$
        paragraphs.add("Lev.25.25"); //$NON-NLS-1$
        paragraphs.add("Lev.25.35"); //$NON-NLS-1$
        paragraphs.add("Lev.25.39"); //$NON-NLS-1$
        paragraphs.add("Lev.25.47"); //$NON-NLS-1$
        paragraphs.add("Lev.26.2"); //$NON-NLS-1$
        paragraphs.add("Lev.26.3"); //$NON-NLS-1$
        paragraphs.add("Lev.26.14"); //$NON-NLS-1$
        paragraphs.add("Lev.26.21"); //$NON-NLS-1$
        paragraphs.add("Lev.27.14"); //$NON-NLS-1$
        paragraphs.add("Lev.27.26"); //$NON-NLS-1$
        paragraphs.add("Num.1.5"); //$NON-NLS-1$
        paragraphs.add("Num.1.17"); //$NON-NLS-1$
        paragraphs.add("Num.1.22"); //$NON-NLS-1$
        paragraphs.add("Num.1.24"); //$NON-NLS-1$
        paragraphs.add("Num.1.26"); //$NON-NLS-1$
        paragraphs.add("Num.1.28"); //$NON-NLS-1$
        paragraphs.add("Num.1.30"); //$NON-NLS-1$
        paragraphs.add("Num.1.32"); //$NON-NLS-1$
        paragraphs.add("Num.1.34"); //$NON-NLS-1$
        paragraphs.add("Num.1.36"); //$NON-NLS-1$
        paragraphs.add("Num.1.38"); //$NON-NLS-1$
        paragraphs.add("Num.1.40"); //$NON-NLS-1$
        paragraphs.add("Num.1.42"); //$NON-NLS-1$
        paragraphs.add("Num.1.47"); //$NON-NLS-1$
        paragraphs.add("Num.2.10"); //$NON-NLS-1$
        paragraphs.add("Num.2.17"); //$NON-NLS-1$
        paragraphs.add("Num.2.18"); //$NON-NLS-1$
        paragraphs.add("Num.2.25"); //$NON-NLS-1$
        paragraphs.add("Num.2.29"); //$NON-NLS-1$
        paragraphs.add("Num.2.32"); //$NON-NLS-1$
        paragraphs.add("Num.3.5"); //$NON-NLS-1$
        paragraphs.add("Num.3.14"); //$NON-NLS-1$
        paragraphs.add("Num.3.27"); //$NON-NLS-1$
        paragraphs.add("Num.3.33"); //$NON-NLS-1$
        paragraphs.add("Num.3.38"); //$NON-NLS-1$
        paragraphs.add("Num.3.40"); //$NON-NLS-1$
        paragraphs.add("Num.3.44"); //$NON-NLS-1$
        paragraphs.add("Num.4.5"); //$NON-NLS-1$
        paragraphs.add("Num.4.16"); //$NON-NLS-1$
        paragraphs.add("Num.4.17"); //$NON-NLS-1$
        paragraphs.add("Num.4.21"); //$NON-NLS-1$
        paragraphs.add("Num.4.29"); //$NON-NLS-1$
        paragraphs.add("Num.4.34"); //$NON-NLS-1$
        paragraphs.add("Num.4.42"); //$NON-NLS-1$
        paragraphs.add("Num.5.5"); //$NON-NLS-1$
        paragraphs.add("Num.5.11"); //$NON-NLS-1$
        paragraphs.add("Num.6.13"); //$NON-NLS-1$
        paragraphs.add("Num.6.22"); //$NON-NLS-1$
        paragraphs.add("Num.7.10"); //$NON-NLS-1$
        paragraphs.add("Num.7.12"); //$NON-NLS-1$
        paragraphs.add("Num.7.18"); //$NON-NLS-1$
        paragraphs.add("Num.7.24"); //$NON-NLS-1$
        paragraphs.add("Num.7.30"); //$NON-NLS-1$
        paragraphs.add("Num.7.36"); //$NON-NLS-1$
        paragraphs.add("Num.7.42"); //$NON-NLS-1$
        paragraphs.add("Num.7.48"); //$NON-NLS-1$
        paragraphs.add("Num.7.54"); //$NON-NLS-1$
        paragraphs.add("Num.7.60"); //$NON-NLS-1$
        paragraphs.add("Num.7.66"); //$NON-NLS-1$
        paragraphs.add("Num.7.72"); //$NON-NLS-1$
        paragraphs.add("Num.7.78"); //$NON-NLS-1$
        paragraphs.add("Num.8.5"); //$NON-NLS-1$
        paragraphs.add("Num.8.23"); //$NON-NLS-1$
        paragraphs.add("Num.9.6"); //$NON-NLS-1$
        paragraphs.add("Num.9.9"); //$NON-NLS-1$
        paragraphs.add("Num.9.15"); //$NON-NLS-1$
        paragraphs.add("Num.10.11"); //$NON-NLS-1$
        paragraphs.add("Num.10.14"); //$NON-NLS-1$
        paragraphs.add("Num.10.18"); //$NON-NLS-1$
        paragraphs.add("Num.10.22"); //$NON-NLS-1$
        paragraphs.add("Num.10.25"); //$NON-NLS-1$
        paragraphs.add("Num.10.29"); //$NON-NLS-1$
        paragraphs.add("Num.10.33"); //$NON-NLS-1$
        paragraphs.add("Num.11.4"); //$NON-NLS-1$
        paragraphs.add("Num.11.10"); //$NON-NLS-1$
        paragraphs.add("Num.11.16"); //$NON-NLS-1$
        paragraphs.add("Num.11.24"); //$NON-NLS-1$
        paragraphs.add("Num.11.31"); //$NON-NLS-1$
        paragraphs.add("Num.12.14"); //$NON-NLS-1$
        paragraphs.add("Num.13.17"); //$NON-NLS-1$
        paragraphs.add("Num.13.21"); //$NON-NLS-1$
        paragraphs.add("Num.13.26"); //$NON-NLS-1$
        paragraphs.add("Num.14.6"); //$NON-NLS-1$
        paragraphs.add("Num.14.11"); //$NON-NLS-1$
        paragraphs.add("Num.14.13"); //$NON-NLS-1$
        paragraphs.add("Num.14.15"); //$NON-NLS-1$
        paragraphs.add("Num.14.26"); //$NON-NLS-1$
        paragraphs.add("Num.14.40"); //$NON-NLS-1$
        paragraphs.add("Num.15.17"); //$NON-NLS-1$
        paragraphs.add("Num.15.22"); //$NON-NLS-1$
        paragraphs.add("Num.15.27"); //$NON-NLS-1$
        paragraphs.add("Num.15.30"); //$NON-NLS-1$
        paragraphs.add("Num.15.32"); //$NON-NLS-1$
        paragraphs.add("Num.15.37"); //$NON-NLS-1$
        paragraphs.add("Num.16.12"); //$NON-NLS-1$
        paragraphs.add("Num.16.23"); //$NON-NLS-1$
        paragraphs.add("Num.16.31"); //$NON-NLS-1$
        paragraphs.add("Num.16.36"); //$NON-NLS-1$
        paragraphs.add("Num.16.41"); //$NON-NLS-1$
        paragraphs.add("Num.16.44"); //$NON-NLS-1$
        paragraphs.add("Num.16.46"); //$NON-NLS-1$
        paragraphs.add("Num.17.6"); //$NON-NLS-1$
        paragraphs.add("Num.17.10"); //$NON-NLS-1$
        paragraphs.add("Num.18.8"); //$NON-NLS-1$
        paragraphs.add("Num.18.20"); //$NON-NLS-1$
        paragraphs.add("Num.18.25"); //$NON-NLS-1$
        paragraphs.add("Num.19.11"); //$NON-NLS-1$
        paragraphs.add("Num.20.7"); //$NON-NLS-1$
        paragraphs.add("Num.20.12"); //$NON-NLS-1$
        paragraphs.add("Num.20.14"); //$NON-NLS-1$
        paragraphs.add("Num.20.22"); //$NON-NLS-1$
        paragraphs.add("Num.21.4"); //$NON-NLS-1$
        paragraphs.add("Num.21.7"); //$NON-NLS-1$
        paragraphs.add("Num.21.10"); //$NON-NLS-1$
        paragraphs.add("Num.21.12"); //$NON-NLS-1$
        paragraphs.add("Num.21.17"); //$NON-NLS-1$
        paragraphs.add("Num.21.21"); //$NON-NLS-1$
        paragraphs.add("Num.21.31"); //$NON-NLS-1$
        paragraphs.add("Num.21.33"); //$NON-NLS-1$
        paragraphs.add("Num.22.2"); //$NON-NLS-1$
        paragraphs.add("Num.22.15"); //$NON-NLS-1$
        paragraphs.add("Num.22.22"); //$NON-NLS-1$
        paragraphs.add("Num.22.36"); //$NON-NLS-1$
        paragraphs.add("Num.23.14"); //$NON-NLS-1$
        paragraphs.add("Num.23.25"); //$NON-NLS-1$
        paragraphs.add("Num.23.27"); //$NON-NLS-1$
        paragraphs.add("Num.24.10"); //$NON-NLS-1$
        paragraphs.add("Num.24.15"); //$NON-NLS-1$
        paragraphs.add("Num.24.20"); //$NON-NLS-1$
        paragraphs.add("Num.25.6"); //$NON-NLS-1$
        paragraphs.add("Num.25.10"); //$NON-NLS-1$
        paragraphs.add("Num.25.16"); //$NON-NLS-1$
        paragraphs.add("Num.26.5"); //$NON-NLS-1$
        paragraphs.add("Num.26.12"); //$NON-NLS-1$
        paragraphs.add("Num.26.15"); //$NON-NLS-1$
        paragraphs.add("Num.26.19"); //$NON-NLS-1$
        paragraphs.add("Num.26.23"); //$NON-NLS-1$
        paragraphs.add("Num.26.26"); //$NON-NLS-1$
        paragraphs.add("Num.26.28"); //$NON-NLS-1$
        paragraphs.add("Num.26.33"); //$NON-NLS-1$
        paragraphs.add("Num.26.35"); //$NON-NLS-1$
        paragraphs.add("Num.26.38"); //$NON-NLS-1$
        paragraphs.add("Num.26.42"); //$NON-NLS-1$
        paragraphs.add("Num.26.44"); //$NON-NLS-1$
        paragraphs.add("Num.26.48"); //$NON-NLS-1$
        paragraphs.add("Num.26.52"); //$NON-NLS-1$
        paragraphs.add("Num.26.57"); //$NON-NLS-1$
        paragraphs.add("Num.26.63"); //$NON-NLS-1$
        paragraphs.add("Num.27.6"); //$NON-NLS-1$
        paragraphs.add("Num.27.12"); //$NON-NLS-1$
        paragraphs.add("Num.27.15"); //$NON-NLS-1$
        paragraphs.add("Num.27.18"); //$NON-NLS-1$
        paragraphs.add("Num.28.9"); //$NON-NLS-1$
        paragraphs.add("Num.28.11"); //$NON-NLS-1$
        paragraphs.add("Num.28.26"); //$NON-NLS-1$
        paragraphs.add("Num.29.7"); //$NON-NLS-1$
        paragraphs.add("Num.29.12"); //$NON-NLS-1$
        paragraphs.add("Num.29.17"); //$NON-NLS-1$
        paragraphs.add("Num.29.20"); //$NON-NLS-1$
        paragraphs.add("Num.29.23"); //$NON-NLS-1$
        paragraphs.add("Num.29.26"); //$NON-NLS-1$
        paragraphs.add("Num.29.29"); //$NON-NLS-1$
        paragraphs.add("Num.29.32"); //$NON-NLS-1$
        paragraphs.add("Num.29.35"); //$NON-NLS-1$
        paragraphs.add("Num.31.13"); //$NON-NLS-1$
        paragraphs.add("Num.31.21"); //$NON-NLS-1$
        paragraphs.add("Num.31.25"); //$NON-NLS-1$
        paragraphs.add("Num.31.48"); //$NON-NLS-1$
        paragraphs.add("Num.32.6"); //$NON-NLS-1$
        paragraphs.add("Num.32.16"); //$NON-NLS-1$
        paragraphs.add("Num.32.20"); //$NON-NLS-1$
        paragraphs.add("Num.32.34"); //$NON-NLS-1$
        paragraphs.add("Num.33.50"); //$NON-NLS-1$
        paragraphs.add("Num.34.9"); //$NON-NLS-1$
        paragraphs.add("Num.35.9"); //$NON-NLS-1$
        paragraphs.add("Deut.1.9"); //$NON-NLS-1$
        paragraphs.add("Deut.1.19"); //$NON-NLS-1$
        paragraphs.add("Deut.1.22"); //$NON-NLS-1$
        paragraphs.add("Deut.2.16"); //$NON-NLS-1$
        paragraphs.add("Deut.2.24"); //$NON-NLS-1$
        paragraphs.add("Deut.2.26"); //$NON-NLS-1$
        paragraphs.add("Deut.3.18"); //$NON-NLS-1$
        paragraphs.add("Deut.3.21"); //$NON-NLS-1$
        paragraphs.add("Deut.4.14"); //$NON-NLS-1$
        paragraphs.add("Deut.4.25"); //$NON-NLS-1$
        paragraphs.add("Deut.4.41"); //$NON-NLS-1$
        paragraphs.add("Deut.4.44"); //$NON-NLS-1$
        paragraphs.add("Deut.5.6"); //$NON-NLS-1$
        paragraphs.add("Deut.5.16"); //$NON-NLS-1$
        paragraphs.add("Deut.5.22"); //$NON-NLS-1$
        paragraphs.add("Deut.6.3"); //$NON-NLS-1$
        paragraphs.add("Deut.6.16"); //$NON-NLS-1$
        paragraphs.add("Deut.7.12"); //$NON-NLS-1$
        paragraphs.add("Deut.9.7"); //$NON-NLS-1$
        paragraphs.add("Deut.10.6"); //$NON-NLS-1$
        paragraphs.add("Deut.10.8"); //$NON-NLS-1$
        paragraphs.add("Deut.10.12"); //$NON-NLS-1$
        paragraphs.add("Deut.11.10"); //$NON-NLS-1$
        paragraphs.add("Deut.11.13"); //$NON-NLS-1$
        paragraphs.add("Deut.11.18"); //$NON-NLS-1$
        paragraphs.add("Deut.11.22"); //$NON-NLS-1$
        paragraphs.add("Deut.11.26"); //$NON-NLS-1$
        paragraphs.add("Deut.12.17"); //$NON-NLS-1$
        paragraphs.add("Deut.12.20"); //$NON-NLS-1$
        paragraphs.add("Deut.12.29"); //$NON-NLS-1$
        paragraphs.add("Deut.13.6"); //$NON-NLS-1$
        paragraphs.add("Deut.13.12"); //$NON-NLS-1$
        paragraphs.add("Deut.14.3"); //$NON-NLS-1$
        paragraphs.add("Deut.14.9"); //$NON-NLS-1$
        paragraphs.add("Deut.14.11"); //$NON-NLS-1$
        paragraphs.add("Deut.14.21"); //$NON-NLS-1$
        paragraphs.add("Deut.14.28"); //$NON-NLS-1$
        paragraphs.add("Deut.15.7"); //$NON-NLS-1$
        paragraphs.add("Deut.15.12"); //$NON-NLS-1$
        paragraphs.add("Deut.15.19"); //$NON-NLS-1$
        paragraphs.add("Deut.16.9"); //$NON-NLS-1$
        paragraphs.add("Deut.16.13"); //$NON-NLS-1$
        paragraphs.add("Deut.16.16"); //$NON-NLS-1$
        paragraphs.add("Deut.16.18"); //$NON-NLS-1$
        paragraphs.add("Deut.16.21"); //$NON-NLS-1$
        paragraphs.add("Deut.17.2"); //$NON-NLS-1$
        paragraphs.add("Deut.17.8"); //$NON-NLS-1$
        paragraphs.add("Deut.17.14"); //$NON-NLS-1$
        paragraphs.add("Deut.18.3"); //$NON-NLS-1$
        paragraphs.add("Deut.18.6"); //$NON-NLS-1$
        paragraphs.add("Deut.18.9"); //$NON-NLS-1$
        paragraphs.add("Deut.18.15"); //$NON-NLS-1$
        paragraphs.add("Deut.19.4"); //$NON-NLS-1$
        paragraphs.add("Deut.19.11"); //$NON-NLS-1$
        paragraphs.add("Deut.19.14"); //$NON-NLS-1$
        paragraphs.add("Deut.19.15"); //$NON-NLS-1$
        paragraphs.add("Deut.19.16"); //$NON-NLS-1$
        paragraphs.add("Deut.20.5"); //$NON-NLS-1$
        paragraphs.add("Deut.20.10"); //$NON-NLS-1$
        paragraphs.add("Deut.20.19"); //$NON-NLS-1$
        paragraphs.add("Deut.21.10"); //$NON-NLS-1$
        paragraphs.add("Deut.21.15"); //$NON-NLS-1$
        paragraphs.add("Deut.21.18"); //$NON-NLS-1$
        paragraphs.add("Deut.21.22"); //$NON-NLS-1$
        paragraphs.add("Deut.22.4"); //$NON-NLS-1$
        paragraphs.add("Deut.22.5"); //$NON-NLS-1$
        paragraphs.add("Deut.22.6"); //$NON-NLS-1$
        paragraphs.add("Deut.22.8"); //$NON-NLS-1$
        paragraphs.add("Deut.22.9"); //$NON-NLS-1$
        paragraphs.add("Deut.22.10"); //$NON-NLS-1$
        paragraphs.add("Deut.22.11"); //$NON-NLS-1$
        paragraphs.add("Deut.22.12"); //$NON-NLS-1$
        paragraphs.add("Deut.22.13"); //$NON-NLS-1$
        paragraphs.add("Deut.22.22"); //$NON-NLS-1$
        paragraphs.add("Deut.22.23"); //$NON-NLS-1$
        paragraphs.add("Deut.22.25"); //$NON-NLS-1$
        paragraphs.add("Deut.22.28"); //$NON-NLS-1$
        paragraphs.add("Deut.22.30"); //$NON-NLS-1$
        paragraphs.add("Deut.23.7"); //$NON-NLS-1$
        paragraphs.add("Deut.23.9"); //$NON-NLS-1$
        paragraphs.add("Deut.23.10"); //$NON-NLS-1$
        paragraphs.add("Deut.23.12"); //$NON-NLS-1$
        paragraphs.add("Deut.23.15"); //$NON-NLS-1$
        paragraphs.add("Deut.23.17"); //$NON-NLS-1$
        paragraphs.add("Deut.23.19"); //$NON-NLS-1$
        paragraphs.add("Deut.23.21"); //$NON-NLS-1$
        paragraphs.add("Deut.23.24"); //$NON-NLS-1$
        paragraphs.add("Deut.24.5"); //$NON-NLS-1$
        paragraphs.add("Deut.24.6"); //$NON-NLS-1$
        paragraphs.add("Deut.24.7"); //$NON-NLS-1$
        paragraphs.add("Deut.24.8"); //$NON-NLS-1$
        paragraphs.add("Deut.24.10"); //$NON-NLS-1$
        paragraphs.add("Deut.24.14"); //$NON-NLS-1$
        paragraphs.add("Deut.24.17"); //$NON-NLS-1$
        paragraphs.add("Deut.24.19"); //$NON-NLS-1$
        paragraphs.add("Deut.25.4"); //$NON-NLS-1$
        paragraphs.add("Deut.25.5"); //$NON-NLS-1$
        paragraphs.add("Deut.25.11"); //$NON-NLS-1$
        paragraphs.add("Deut.25.13"); //$NON-NLS-1$
        paragraphs.add("Deut.25.17"); //$NON-NLS-1$
        paragraphs.add("Deut.26.12"); //$NON-NLS-1$
        paragraphs.add("Deut.26.16"); //$NON-NLS-1$
        paragraphs.add("Deut.27.9"); //$NON-NLS-1$
        paragraphs.add("Deut.27.11"); //$NON-NLS-1$
        paragraphs.add("Deut.27.14"); //$NON-NLS-1$
        paragraphs.add("Deut.28.15"); //$NON-NLS-1$
        paragraphs.add("Deut.29.2"); //$NON-NLS-1$
        paragraphs.add("Deut.29.10"); //$NON-NLS-1$
        paragraphs.add("Deut.30.11"); //$NON-NLS-1$
        paragraphs.add("Deut.30.15"); //$NON-NLS-1$
        paragraphs.add("Deut.31.7"); //$NON-NLS-1$
        paragraphs.add("Deut.31.9"); //$NON-NLS-1$
        paragraphs.add("Deut.31.14"); //$NON-NLS-1$
        paragraphs.add("Deut.31.16"); //$NON-NLS-1$
        paragraphs.add("Deut.31.22"); //$NON-NLS-1$
        paragraphs.add("Deut.31.24"); //$NON-NLS-1$
        paragraphs.add("Deut.31.28"); //$NON-NLS-1$
        paragraphs.add("Deut.32.7"); //$NON-NLS-1$
        paragraphs.add("Deut.32.15"); //$NON-NLS-1$
        paragraphs.add("Deut.32.44"); //$NON-NLS-1$
        paragraphs.add("Deut.33.6"); //$NON-NLS-1$
        paragraphs.add("Deut.33.7"); //$NON-NLS-1$
        paragraphs.add("Deut.33.8"); //$NON-NLS-1$
        paragraphs.add("Deut.33.12"); //$NON-NLS-1$
        paragraphs.add("Deut.33.13"); //$NON-NLS-1$
        paragraphs.add("Deut.33.18"); //$NON-NLS-1$
        paragraphs.add("Deut.33.20"); //$NON-NLS-1$
        paragraphs.add("Deut.33.22"); //$NON-NLS-1$
        paragraphs.add("Deut.33.23"); //$NON-NLS-1$
        paragraphs.add("Deut.33.24"); //$NON-NLS-1$
        paragraphs.add("Deut.33.26"); //$NON-NLS-1$
        paragraphs.add("Deut.34.5"); //$NON-NLS-1$
        paragraphs.add("Deut.34.7"); //$NON-NLS-1$
        paragraphs.add("Deut.34.8"); //$NON-NLS-1$
        paragraphs.add("Deut.34.9"); //$NON-NLS-1$
        paragraphs.add("Deut.34.10"); //$NON-NLS-1$
        paragraphs.add("Josh.1.10"); //$NON-NLS-1$
        paragraphs.add("Josh.1.12"); //$NON-NLS-1$
        paragraphs.add("Josh.1.16"); //$NON-NLS-1$
        paragraphs.add("Josh.2.8"); //$NON-NLS-1$
        paragraphs.add("Josh.2.23"); //$NON-NLS-1$
        paragraphs.add("Josh.3.7"); //$NON-NLS-1$
        paragraphs.add("Josh.3.9"); //$NON-NLS-1$
        paragraphs.add("Josh.3.14"); //$NON-NLS-1$
        paragraphs.add("Josh.4.10"); //$NON-NLS-1$
        paragraphs.add("Josh.4.14"); //$NON-NLS-1$
        paragraphs.add("Josh.4.19"); //$NON-NLS-1$
        paragraphs.add("Josh.5.2"); //$NON-NLS-1$
        paragraphs.add("Josh.5.10"); //$NON-NLS-1$
        paragraphs.add("Josh.5.12"); //$NON-NLS-1$
        paragraphs.add("Josh.5.13"); //$NON-NLS-1$
        paragraphs.add("Josh.6.6"); //$NON-NLS-1$
        paragraphs.add("Josh.6.8"); //$NON-NLS-1$
        paragraphs.add("Josh.6.9"); //$NON-NLS-1$
        paragraphs.add("Josh.6.12"); //$NON-NLS-1$
        paragraphs.add("Josh.6.17"); //$NON-NLS-1$
        paragraphs.add("Josh.6.26"); //$NON-NLS-1$
        paragraphs.add("Josh.7.6"); //$NON-NLS-1$
        paragraphs.add("Josh.7.10"); //$NON-NLS-1$
        paragraphs.add("Josh.7.16"); //$NON-NLS-1$
        paragraphs.add("Josh.7.22"); //$NON-NLS-1$
        paragraphs.add("Josh.8.3"); //$NON-NLS-1$
        paragraphs.add("Josh.8.9"); //$NON-NLS-1$
        paragraphs.add("Josh.8.14"); //$NON-NLS-1$
        paragraphs.add("Josh.8.30"); //$NON-NLS-1$
        paragraphs.add("Josh.8.32"); //$NON-NLS-1$
        paragraphs.add("Josh.9.3"); //$NON-NLS-1$
        paragraphs.add("Josh.9.16"); //$NON-NLS-1$
        paragraphs.add("Josh.9.22"); //$NON-NLS-1$
        paragraphs.add("Josh.10.6"); //$NON-NLS-1$
        paragraphs.add("Josh.10.8"); //$NON-NLS-1$
        paragraphs.add("Josh.10.12"); //$NON-NLS-1$
        paragraphs.add("Josh.10.15"); //$NON-NLS-1$
        paragraphs.add("Josh.10.28"); //$NON-NLS-1$
        paragraphs.add("Josh.10.31"); //$NON-NLS-1$
        paragraphs.add("Josh.10.33"); //$NON-NLS-1$
        paragraphs.add("Josh.10.34"); //$NON-NLS-1$
        paragraphs.add("Josh.10.38"); //$NON-NLS-1$
        paragraphs.add("Josh.10.40"); //$NON-NLS-1$
        paragraphs.add("Josh.11.6"); //$NON-NLS-1$
        paragraphs.add("Josh.11.10"); //$NON-NLS-1$
        paragraphs.add("Josh.11.15"); //$NON-NLS-1$
        paragraphs.add("Josh.11.21"); //$NON-NLS-1$
        paragraphs.add("Josh.12.4"); //$NON-NLS-1$
        paragraphs.add("Josh.12.7"); //$NON-NLS-1$
        paragraphs.add("Josh.12.9"); //$NON-NLS-1$
        paragraphs.add("Josh.13.15"); //$NON-NLS-1$
        paragraphs.add("Josh.13.22"); //$NON-NLS-1$
        paragraphs.add("Josh.13.29"); //$NON-NLS-1$
        paragraphs.add("Josh.14.6"); //$NON-NLS-1$
        paragraphs.add("Josh.15.13"); //$NON-NLS-1$
        paragraphs.add("Josh.15.16"); //$NON-NLS-1$
        paragraphs.add("Josh.15.48"); //$NON-NLS-1$
        paragraphs.add("Josh.15.63"); //$NON-NLS-1$
        paragraphs.add("Josh.16.5"); //$NON-NLS-1$
        paragraphs.add("Josh.17.3"); //$NON-NLS-1$
        paragraphs.add("Josh.17.7"); //$NON-NLS-1$
        paragraphs.add("Josh.18.8"); //$NON-NLS-1$
        paragraphs.add("Josh.18.10"); //$NON-NLS-1$
        paragraphs.add("Josh.18.11"); //$NON-NLS-1$
        paragraphs.add("Josh.19.10"); //$NON-NLS-1$
        paragraphs.add("Josh.19.17"); //$NON-NLS-1$
        paragraphs.add("Josh.19.24"); //$NON-NLS-1$
        paragraphs.add("Josh.19.32"); //$NON-NLS-1$
        paragraphs.add("Josh.19.40"); //$NON-NLS-1$
        paragraphs.add("Josh.19.49"); //$NON-NLS-1$
        paragraphs.add("Josh.20.7"); //$NON-NLS-1$
        paragraphs.add("Josh.21.9"); //$NON-NLS-1$
        paragraphs.add("Josh.21.13"); //$NON-NLS-1$
        paragraphs.add("Josh.21.20"); //$NON-NLS-1$
        paragraphs.add("Josh.21.27"); //$NON-NLS-1$
        paragraphs.add("Josh.21.34"); //$NON-NLS-1$
        paragraphs.add("Josh.21.43"); //$NON-NLS-1$
        paragraphs.add("Josh.22.7"); //$NON-NLS-1$
        paragraphs.add("Josh.22.9"); //$NON-NLS-1$
        paragraphs.add("Josh.22.10"); //$NON-NLS-1$
        paragraphs.add("Josh.22.11"); //$NON-NLS-1$
        paragraphs.add("Josh.22.15"); //$NON-NLS-1$
        paragraphs.add("Josh.22.21"); //$NON-NLS-1$
        paragraphs.add("Josh.22.30"); //$NON-NLS-1$
        paragraphs.add("Josh.22.32"); //$NON-NLS-1$
        paragraphs.add("Josh.24.14"); //$NON-NLS-1$
        paragraphs.add("Josh.24.26"); //$NON-NLS-1$
        paragraphs.add("Josh.24.29"); //$NON-NLS-1$
        paragraphs.add("Josh.24.32"); //$NON-NLS-1$
        paragraphs.add("Judg.1.9"); //$NON-NLS-1$
        paragraphs.add("Judg.1.16"); //$NON-NLS-1$
        paragraphs.add("Judg.1.22"); //$NON-NLS-1$
        paragraphs.add("Judg.1.27"); //$NON-NLS-1$
        paragraphs.add("Judg.1.29"); //$NON-NLS-1$
        paragraphs.add("Judg.1.30"); //$NON-NLS-1$
        paragraphs.add("Judg.1.31"); //$NON-NLS-1$
        paragraphs.add("Judg.1.33"); //$NON-NLS-1$
        paragraphs.add("Judg.2.6"); //$NON-NLS-1$
        paragraphs.add("Judg.2.11"); //$NON-NLS-1$
        paragraphs.add("Judg.2.14"); //$NON-NLS-1$
        paragraphs.add("Judg.2.16"); //$NON-NLS-1$
        paragraphs.add("Judg.2.20"); //$NON-NLS-1$
        paragraphs.add("Judg.3.5"); //$NON-NLS-1$
        paragraphs.add("Judg.3.8"); //$NON-NLS-1$
        paragraphs.add("Judg.3.12"); //$NON-NLS-1$
        paragraphs.add("Judg.3.31"); //$NON-NLS-1$
        paragraphs.add("Judg.4.4"); //$NON-NLS-1$
        paragraphs.add("Judg.4.10"); //$NON-NLS-1$
        paragraphs.add("Judg.4.18"); //$NON-NLS-1$
        paragraphs.add("Judg.6.7"); //$NON-NLS-1$
        paragraphs.add("Judg.6.11"); //$NON-NLS-1$
        paragraphs.add("Judg.6.19"); //$NON-NLS-1$
        paragraphs.add("Judg.6.21"); //$NON-NLS-1$
        paragraphs.add("Judg.6.25"); //$NON-NLS-1$
        paragraphs.add("Judg.6.28"); //$NON-NLS-1$
        paragraphs.add("Judg.6.33"); //$NON-NLS-1$
        paragraphs.add("Judg.6.36"); //$NON-NLS-1$
        paragraphs.add("Judg.7.9"); //$NON-NLS-1$
        paragraphs.add("Judg.7.15"); //$NON-NLS-1$
        paragraphs.add("Judg.7.19"); //$NON-NLS-1$
        paragraphs.add("Judg.7.24"); //$NON-NLS-1$
        paragraphs.add("Judg.8.4"); //$NON-NLS-1$
        paragraphs.add("Judg.8.6"); //$NON-NLS-1$
        paragraphs.add("Judg.8.8"); //$NON-NLS-1$
        paragraphs.add("Judg.8.10"); //$NON-NLS-1$
        paragraphs.add("Judg.8.11"); //$NON-NLS-1$
        paragraphs.add("Judg.8.13"); //$NON-NLS-1$
        paragraphs.add("Judg.8.18"); //$NON-NLS-1$
        paragraphs.add("Judg.8.22"); //$NON-NLS-1$
        paragraphs.add("Judg.8.24"); //$NON-NLS-1$
        paragraphs.add("Judg.8.28"); //$NON-NLS-1$
        paragraphs.add("Judg.8.29"); //$NON-NLS-1$
        paragraphs.add("Judg.8.32"); //$NON-NLS-1$
        paragraphs.add("Judg.9.7"); //$NON-NLS-1$
        paragraphs.add("Judg.9.22"); //$NON-NLS-1$
        paragraphs.add("Judg.9.30"); //$NON-NLS-1$
        paragraphs.add("Judg.9.34"); //$NON-NLS-1$
        paragraphs.add("Judg.9.46"); //$NON-NLS-1$
        paragraphs.add("Judg.9.50"); //$NON-NLS-1$
        paragraphs.add("Judg.9.56"); //$NON-NLS-1$
        paragraphs.add("Judg.10.3"); //$NON-NLS-1$
        paragraphs.add("Judg.10.6"); //$NON-NLS-1$
        paragraphs.add("Judg.10.10"); //$NON-NLS-1$
        paragraphs.add("Judg.10.15"); //$NON-NLS-1$
        paragraphs.add("Judg.11.4"); //$NON-NLS-1$
        paragraphs.add("Judg.11.12"); //$NON-NLS-1$
        paragraphs.add("Judg.11.29"); //$NON-NLS-1$
        paragraphs.add("Judg.11.32"); //$NON-NLS-1$
        paragraphs.add("Judg.11.34"); //$NON-NLS-1$
        paragraphs.add("Judg.12.8"); //$NON-NLS-1$
        paragraphs.add("Judg.12.11"); //$NON-NLS-1$
        paragraphs.add("Judg.12.13"); //$NON-NLS-1$
        paragraphs.add("Judg.13.2"); //$NON-NLS-1$
        paragraphs.add("Judg.13.6"); //$NON-NLS-1$
        paragraphs.add("Judg.13.8"); //$NON-NLS-1$
        paragraphs.add("Judg.13.15"); //$NON-NLS-1$
        paragraphs.add("Judg.13.24"); //$NON-NLS-1$
        paragraphs.add("Judg.14.5"); //$NON-NLS-1$
        paragraphs.add("Judg.14.8"); //$NON-NLS-1$
        paragraphs.add("Judg.14.10"); //$NON-NLS-1$
        paragraphs.add("Judg.14.12"); //$NON-NLS-1$
        paragraphs.add("Judg.14.19"); //$NON-NLS-1$
        paragraphs.add("Judg.15.3"); //$NON-NLS-1$
        paragraphs.add("Judg.15.6"); //$NON-NLS-1$
        paragraphs.add("Judg.15.7"); //$NON-NLS-1$
        paragraphs.add("Judg.15.9"); //$NON-NLS-1$
        paragraphs.add("Judg.15.14"); //$NON-NLS-1$
        paragraphs.add("Judg.15.18"); //$NON-NLS-1$
        paragraphs.add("Judg.16.4"); //$NON-NLS-1$
        paragraphs.add("Judg.16.6"); //$NON-NLS-1$
        paragraphs.add("Judg.16.15"); //$NON-NLS-1$
        paragraphs.add("Judg.16.21"); //$NON-NLS-1$
        paragraphs.add("Judg.17.7"); //$NON-NLS-1$
        paragraphs.add("Judg.18.7"); //$NON-NLS-1$
        paragraphs.add("Judg.18.11"); //$NON-NLS-1$
        paragraphs.add("Judg.18.14"); //$NON-NLS-1$
        paragraphs.add("Judg.18.22"); //$NON-NLS-1$
        paragraphs.add("Judg.18.30"); //$NON-NLS-1$
        paragraphs.add("Judg.19.5"); //$NON-NLS-1$
        paragraphs.add("Judg.19.16"); //$NON-NLS-1$
        paragraphs.add("Judg.19.22"); //$NON-NLS-1$
        paragraphs.add("Judg.19.29"); //$NON-NLS-1$
        paragraphs.add("Judg.20.8"); //$NON-NLS-1$
        paragraphs.add("Judg.20.12"); //$NON-NLS-1$
        paragraphs.add("Judg.20.18"); //$NON-NLS-1$
        paragraphs.add("Judg.20.26"); //$NON-NLS-1$
        paragraphs.add("Judg.21.8"); //$NON-NLS-1$
        paragraphs.add("Judg.21.16"); //$NON-NLS-1$
        paragraphs.add("Ruth.1.6"); //$NON-NLS-1$
        paragraphs.add("Ruth.1.19"); //$NON-NLS-1$
        paragraphs.add("Ruth.2.4"); //$NON-NLS-1$
        paragraphs.add("Ruth.2.18"); //$NON-NLS-1$
        paragraphs.add("Ruth.3.6"); //$NON-NLS-1$
        paragraphs.add("Ruth.3.8"); //$NON-NLS-1$
        paragraphs.add("Ruth.3.14"); //$NON-NLS-1$
        paragraphs.add("Ruth.4.6"); //$NON-NLS-1$
        paragraphs.add("Ruth.4.9"); //$NON-NLS-1$
        paragraphs.add("Ruth.4.13"); //$NON-NLS-1$
        paragraphs.add("Ruth.4.18"); //$NON-NLS-1$
        paragraphs.add("1Sam.1.4"); //$NON-NLS-1$
        paragraphs.add("1Sam.1.9"); //$NON-NLS-1$
        paragraphs.add("1Sam.1.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.1.24"); //$NON-NLS-1$
        paragraphs.add("1Sam.2.12"); //$NON-NLS-1$
        paragraphs.add("1Sam.2.18"); //$NON-NLS-1$
        paragraphs.add("1Sam.2.20"); //$NON-NLS-1$
        paragraphs.add("1Sam.2.22"); //$NON-NLS-1$
        paragraphs.add("1Sam.2.27"); //$NON-NLS-1$
        paragraphs.add("1Sam.3.11"); //$NON-NLS-1$
        paragraphs.add("1Sam.3.15"); //$NON-NLS-1$
        paragraphs.add("1Sam.3.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.4.3"); //$NON-NLS-1$
        paragraphs.add("1Sam.4.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.4.12"); //$NON-NLS-1$
        paragraphs.add("1Sam.4.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.5.3"); //$NON-NLS-1$
        paragraphs.add("1Sam.5.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.6.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.6.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.6.21"); //$NON-NLS-1$
        paragraphs.add("1Sam.7.3"); //$NON-NLS-1$
        paragraphs.add("1Sam.7.9"); //$NON-NLS-1$
        paragraphs.add("1Sam.7.13"); //$NON-NLS-1$
        paragraphs.add("1Sam.8.6"); //$NON-NLS-1$
        paragraphs.add("1Sam.8.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.8.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.9.11"); //$NON-NLS-1$
        paragraphs.add("1Sam.9.15"); //$NON-NLS-1$
        paragraphs.add("1Sam.9.25"); //$NON-NLS-1$
        paragraphs.add("1Sam.10.9"); //$NON-NLS-1$
        paragraphs.add("1Sam.10.14"); //$NON-NLS-1$
        paragraphs.add("1Sam.10.17"); //$NON-NLS-1$
        paragraphs.add("1Sam.10.26"); //$NON-NLS-1$
        paragraphs.add("1Sam.11.4"); //$NON-NLS-1$
        paragraphs.add("1Sam.11.12"); //$NON-NLS-1$
        paragraphs.add("1Sam.12.6"); //$NON-NLS-1$
        paragraphs.add("1Sam.12.16"); //$NON-NLS-1$
        paragraphs.add("1Sam.12.20"); //$NON-NLS-1$
        paragraphs.add("1Sam.13.5"); //$NON-NLS-1$
        paragraphs.add("1Sam.13.8"); //$NON-NLS-1$
        paragraphs.add("1Sam.13.11"); //$NON-NLS-1$
        paragraphs.add("1Sam.13.17"); //$NON-NLS-1$
        paragraphs.add("1Sam.13.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.14.4"); //$NON-NLS-1$
        paragraphs.add("1Sam.14.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.14.24"); //$NON-NLS-1$
        paragraphs.add("1Sam.14.33"); //$NON-NLS-1$
        paragraphs.add("1Sam.14.36"); //$NON-NLS-1$
        paragraphs.add("1Sam.14.47"); //$NON-NLS-1$
        paragraphs.add("1Sam.15.6"); //$NON-NLS-1$
        paragraphs.add("1Sam.15.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.15.24"); //$NON-NLS-1$
        paragraphs.add("1Sam.15.32"); //$NON-NLS-1$
        paragraphs.add("1Sam.15.34"); //$NON-NLS-1$
        paragraphs.add("1Sam.16.6"); //$NON-NLS-1$
        paragraphs.add("1Sam.16.14"); //$NON-NLS-1$
        paragraphs.add("1Sam.16.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.4"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.12"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.20"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.28"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.30"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.32"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.38"); //$NON-NLS-1$
        paragraphs.add("1Sam.17.55"); //$NON-NLS-1$
        paragraphs.add("1Sam.18.5"); //$NON-NLS-1$
        paragraphs.add("1Sam.18.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.18.12"); //$NON-NLS-1$
        paragraphs.add("1Sam.18.17"); //$NON-NLS-1$
        paragraphs.add("1Sam.18.22"); //$NON-NLS-1$
        paragraphs.add("1Sam.18.28"); //$NON-NLS-1$
        paragraphs.add("1Sam.19.4"); //$NON-NLS-1$
        paragraphs.add("1Sam.19.8"); //$NON-NLS-1$
        paragraphs.add("1Sam.19.12"); //$NON-NLS-1$
        paragraphs.add("1Sam.19.18"); //$NON-NLS-1$
        paragraphs.add("1Sam.20.11"); //$NON-NLS-1$
        paragraphs.add("1Sam.20.24"); //$NON-NLS-1$
        paragraphs.add("1Sam.20.35"); //$NON-NLS-1$
        paragraphs.add("1Sam.20.41"); //$NON-NLS-1$
        paragraphs.add("1Sam.21.8"); //$NON-NLS-1$
        paragraphs.add("1Sam.21.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.22.3"); //$NON-NLS-1$
        paragraphs.add("1Sam.22.5"); //$NON-NLS-1$
        paragraphs.add("1Sam.22.6"); //$NON-NLS-1$
        paragraphs.add("1Sam.22.9"); //$NON-NLS-1$
        paragraphs.add("1Sam.22.17"); //$NON-NLS-1$
        paragraphs.add("1Sam.22.20"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.7"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.9"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.13"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.16"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.19"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.27"); //$NON-NLS-1$
        paragraphs.add("1Sam.23.29"); //$NON-NLS-1$
        paragraphs.add("1Sam.24.9"); //$NON-NLS-1$
        paragraphs.add("1Sam.24.16"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.4"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.10"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.14"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.18"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.32"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.36"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.39"); //$NON-NLS-1$
        paragraphs.add("1Sam.25.44"); //$NON-NLS-1$
        paragraphs.add("1Sam.26.5"); //$NON-NLS-1$
        paragraphs.add("1Sam.26.13"); //$NON-NLS-1$
        paragraphs.add("1Sam.26.21"); //$NON-NLS-1$
        paragraphs.add("1Sam.27.5"); //$NON-NLS-1$
        paragraphs.add("1Sam.27.8"); //$NON-NLS-1$
        paragraphs.add("1Sam.28.3"); //$NON-NLS-1$
        paragraphs.add("1Sam.28.7"); //$NON-NLS-1$
        paragraphs.add("1Sam.28.15"); //$NON-NLS-1$
        paragraphs.add("1Sam.28.21"); //$NON-NLS-1$
        paragraphs.add("1Sam.29.6"); //$NON-NLS-1$
        paragraphs.add("1Sam.29.8"); //$NON-NLS-1$
        paragraphs.add("1Sam.30.3"); //$NON-NLS-1$
        paragraphs.add("1Sam.30.11"); //$NON-NLS-1$
        paragraphs.add("1Sam.30.16"); //$NON-NLS-1$
        paragraphs.add("1Sam.30.21"); //$NON-NLS-1$
        paragraphs.add("1Sam.30.26"); //$NON-NLS-1$
        paragraphs.add("1Sam.31.7"); //$NON-NLS-1$
        paragraphs.add("1Sam.31.11"); //$NON-NLS-1$
        paragraphs.add("2Sam.1.13"); //$NON-NLS-1$
        paragraphs.add("2Sam.1.17"); //$NON-NLS-1$
        paragraphs.add("2Sam.2.5"); //$NON-NLS-1$
        paragraphs.add("2Sam.2.8"); //$NON-NLS-1$
        paragraphs.add("2Sam.2.12"); //$NON-NLS-1$
        paragraphs.add("2Sam.2.18"); //$NON-NLS-1$
        paragraphs.add("2Sam.2.25"); //$NON-NLS-1$
        paragraphs.add("2Sam.2.32"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.2"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.12"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.13"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.17"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.22"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.28"); //$NON-NLS-1$
        paragraphs.add("2Sam.3.31"); //$NON-NLS-1$
        paragraphs.add("2Sam.4.9"); //$NON-NLS-1$
        paragraphs.add("2Sam.5.4"); //$NON-NLS-1$
        paragraphs.add("2Sam.5.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.5.11"); //$NON-NLS-1$
        paragraphs.add("2Sam.5.13"); //$NON-NLS-1$
        paragraphs.add("2Sam.5.17"); //$NON-NLS-1$
        paragraphs.add("2Sam.5.22"); //$NON-NLS-1$
        paragraphs.add("2Sam.6.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.6.12"); //$NON-NLS-1$
        paragraphs.add("2Sam.6.17"); //$NON-NLS-1$
        paragraphs.add("2Sam.6.20"); //$NON-NLS-1$
        paragraphs.add("2Sam.7.4"); //$NON-NLS-1$
        paragraphs.add("2Sam.7.12"); //$NON-NLS-1$
        paragraphs.add("2Sam.7.18"); //$NON-NLS-1$
        paragraphs.add("2Sam.8.3"); //$NON-NLS-1$
        paragraphs.add("2Sam.8.9"); //$NON-NLS-1$
        paragraphs.add("2Sam.8.14"); //$NON-NLS-1$
        paragraphs.add("2Sam.9.5"); //$NON-NLS-1$
        paragraphs.add("2Sam.9.7"); //$NON-NLS-1$
        paragraphs.add("2Sam.9.9"); //$NON-NLS-1$
        paragraphs.add("2Sam.10.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.10.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.11.2"); //$NON-NLS-1$
        paragraphs.add("2Sam.11.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.11.14"); //$NON-NLS-1$
        paragraphs.add("2Sam.11.18"); //$NON-NLS-1$
        paragraphs.add("2Sam.11.22"); //$NON-NLS-1$
        paragraphs.add("2Sam.11.26"); //$NON-NLS-1$
        paragraphs.add("2Sam.12.7"); //$NON-NLS-1$
        paragraphs.add("2Sam.12.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.12.24"); //$NON-NLS-1$
        paragraphs.add("2Sam.12.26"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.19"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.21"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.23"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.28"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.30"); //$NON-NLS-1$
        paragraphs.add("2Sam.13.37"); //$NON-NLS-1$
        paragraphs.add("2Sam.14.4"); //$NON-NLS-1$
        paragraphs.add("2Sam.14.21"); //$NON-NLS-1$
        paragraphs.add("2Sam.14.25"); //$NON-NLS-1$
        paragraphs.add("2Sam.14.28"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.7"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.10"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.13"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.19"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.24"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.30"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.31"); //$NON-NLS-1$
        paragraphs.add("2Sam.15.32"); //$NON-NLS-1$
        paragraphs.add("2Sam.16.5"); //$NON-NLS-1$
        paragraphs.add("2Sam.16.9"); //$NON-NLS-1$
        paragraphs.add("2Sam.16.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.16.20"); //$NON-NLS-1$
        paragraphs.add("2Sam.17.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.17.23"); //$NON-NLS-1$
        paragraphs.add("2Sam.17.25"); //$NON-NLS-1$
        paragraphs.add("2Sam.17.27"); //$NON-NLS-1$
        paragraphs.add("2Sam.18.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.18.9"); //$NON-NLS-1$
        paragraphs.add("2Sam.18.18"); //$NON-NLS-1$
        paragraphs.add("2Sam.18.19"); //$NON-NLS-1$
        paragraphs.add("2Sam.18.33"); //$NON-NLS-1$
        paragraphs.add("2Sam.19.9"); //$NON-NLS-1$
        paragraphs.add("2Sam.19.11"); //$NON-NLS-1$
        paragraphs.add("2Sam.19.16"); //$NON-NLS-1$
        paragraphs.add("2Sam.19.24"); //$NON-NLS-1$
        paragraphs.add("2Sam.19.31"); //$NON-NLS-1$
        paragraphs.add("2Sam.19.41"); //$NON-NLS-1$
        paragraphs.add("2Sam.20.3"); //$NON-NLS-1$
        paragraphs.add("2Sam.20.4"); //$NON-NLS-1$
        paragraphs.add("2Sam.20.14"); //$NON-NLS-1$
        paragraphs.add("2Sam.20.16"); //$NON-NLS-1$
        paragraphs.add("2Sam.20.23"); //$NON-NLS-1$
        paragraphs.add("2Sam.21.10"); //$NON-NLS-1$
        paragraphs.add("2Sam.21.12"); //$NON-NLS-1$
        paragraphs.add("2Sam.21.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.23.6"); //$NON-NLS-1$
        paragraphs.add("2Sam.23.8"); //$NON-NLS-1$
        paragraphs.add("2Sam.24.5"); //$NON-NLS-1$
        paragraphs.add("2Sam.24.10"); //$NON-NLS-1$
        paragraphs.add("2Sam.24.15"); //$NON-NLS-1$
        paragraphs.add("2Sam.24.18"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.5"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.11"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.15"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.22"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.28"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.32"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.41"); //$NON-NLS-1$
        paragraphs.add("1Kgs.1.50"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.12"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.13"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.19"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.26"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.28"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.35"); //$NON-NLS-1$
        paragraphs.add("1Kgs.2.36"); //$NON-NLS-1$
        paragraphs.add("1Kgs.3.5"); //$NON-NLS-1$
        paragraphs.add("1Kgs.3.16"); //$NON-NLS-1$
        paragraphs.add("1Kgs.4.7"); //$NON-NLS-1$
        paragraphs.add("1Kgs.4.20"); //$NON-NLS-1$
        paragraphs.add("1Kgs.4.22"); //$NON-NLS-1$
        paragraphs.add("1Kgs.4.26"); //$NON-NLS-1$
        paragraphs.add("1Kgs.4.29"); //$NON-NLS-1$
        paragraphs.add("1Kgs.5.7"); //$NON-NLS-1$
        paragraphs.add("1Kgs.5.13"); //$NON-NLS-1$
        paragraphs.add("1Kgs.6.5"); //$NON-NLS-1$
        paragraphs.add("1Kgs.6.11"); //$NON-NLS-1$
        paragraphs.add("1Kgs.6.23"); //$NON-NLS-1$
        paragraphs.add("1Kgs.6.31"); //$NON-NLS-1$
        paragraphs.add("1Kgs.6.36"); //$NON-NLS-1$
        paragraphs.add("1Kgs.6.37"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.2"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.6"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.7"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.8"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.13"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.23"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.27"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.38"); //$NON-NLS-1$
        paragraphs.add("1Kgs.7.40"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.12"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.22"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.31"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.33"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.35"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.37"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.44"); //$NON-NLS-1$
        paragraphs.add("1Kgs.8.62"); //$NON-NLS-1$
        paragraphs.add("1Kgs.9.10"); //$NON-NLS-1$
        paragraphs.add("1Kgs.9.15"); //$NON-NLS-1$
        paragraphs.add("1Kgs.9.24"); //$NON-NLS-1$
        paragraphs.add("1Kgs.9.25"); //$NON-NLS-1$
        paragraphs.add("1Kgs.9.26"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.14"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.16"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.18"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.21"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.24"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.26"); //$NON-NLS-1$
        paragraphs.add("1Kgs.10.28"); //$NON-NLS-1$
        paragraphs.add("1Kgs.11.9"); //$NON-NLS-1$
        paragraphs.add("1Kgs.11.14"); //$NON-NLS-1$
        paragraphs.add("1Kgs.11.23"); //$NON-NLS-1$
        paragraphs.add("1Kgs.11.26"); //$NON-NLS-1$
        paragraphs.add("1Kgs.11.41"); //$NON-NLS-1$
        paragraphs.add("1Kgs.12.6"); //$NON-NLS-1$
        paragraphs.add("1Kgs.12.12"); //$NON-NLS-1$
        paragraphs.add("1Kgs.12.16"); //$NON-NLS-1$
        paragraphs.add("1Kgs.12.21"); //$NON-NLS-1$
        paragraphs.add("1Kgs.12.25"); //$NON-NLS-1$
        paragraphs.add("1Kgs.13.11"); //$NON-NLS-1$
        paragraphs.add("1Kgs.13.20"); //$NON-NLS-1$
        paragraphs.add("1Kgs.13.23"); //$NON-NLS-1$
        paragraphs.add("1Kgs.13.33"); //$NON-NLS-1$
        paragraphs.add("1Kgs.14.5"); //$NON-NLS-1$
        paragraphs.add("1Kgs.14.17"); //$NON-NLS-1$
        paragraphs.add("1Kgs.14.21"); //$NON-NLS-1$
        paragraphs.add("1Kgs.14.25"); //$NON-NLS-1$
        paragraphs.add("1Kgs.14.29"); //$NON-NLS-1$
        paragraphs.add("1Kgs.15.9"); //$NON-NLS-1$
        paragraphs.add("1Kgs.15.16"); //$NON-NLS-1$
        paragraphs.add("1Kgs.15.25"); //$NON-NLS-1$
        paragraphs.add("1Kgs.15.27"); //$NON-NLS-1$
        paragraphs.add("1Kgs.15.31"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.8"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.11"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.15"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.21"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.23"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.25"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.29"); //$NON-NLS-1$
        paragraphs.add("1Kgs.16.34"); //$NON-NLS-1$
        paragraphs.add("1Kgs.17.8"); //$NON-NLS-1$
        paragraphs.add("1Kgs.17.17"); //$NON-NLS-1$
        paragraphs.add("1Kgs.17.24"); //$NON-NLS-1$
        paragraphs.add("1Kgs.18.7"); //$NON-NLS-1$
        paragraphs.add("1Kgs.18.17"); //$NON-NLS-1$
        paragraphs.add("1Kgs.18.41"); //$NON-NLS-1$
        paragraphs.add("1Kgs.19.4"); //$NON-NLS-1$
        paragraphs.add("1Kgs.19.9"); //$NON-NLS-1$
        paragraphs.add("1Kgs.19.19"); //$NON-NLS-1$
        paragraphs.add("1Kgs.20.13"); //$NON-NLS-1$
        paragraphs.add("1Kgs.20.22"); //$NON-NLS-1$
        paragraphs.add("1Kgs.20.28"); //$NON-NLS-1$
        paragraphs.add("1Kgs.20.31"); //$NON-NLS-1$
        paragraphs.add("1Kgs.20.35"); //$NON-NLS-1$
        paragraphs.add("1Kgs.21.5"); //$NON-NLS-1$
        paragraphs.add("1Kgs.21.15"); //$NON-NLS-1$
        paragraphs.add("1Kgs.21.17"); //$NON-NLS-1$
        paragraphs.add("1Kgs.21.25"); //$NON-NLS-1$
        paragraphs.add("1Kgs.22.15"); //$NON-NLS-1$
        paragraphs.add("1Kgs.22.37"); //$NON-NLS-1$
        paragraphs.add("1Kgs.22.41"); //$NON-NLS-1$
        paragraphs.add("1Kgs.22.50"); //$NON-NLS-1$
        paragraphs.add("1Kgs.22.51"); //$NON-NLS-1$
        paragraphs.add("2Kgs.1.5"); //$NON-NLS-1$
        paragraphs.add("2Kgs.1.13"); //$NON-NLS-1$
        paragraphs.add("2Kgs.1.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.2.9"); //$NON-NLS-1$
        paragraphs.add("2Kgs.2.12"); //$NON-NLS-1$
        paragraphs.add("2Kgs.2.16"); //$NON-NLS-1$
        paragraphs.add("2Kgs.2.19"); //$NON-NLS-1$
        paragraphs.add("2Kgs.2.23"); //$NON-NLS-1$
        paragraphs.add("2Kgs.3.4"); //$NON-NLS-1$
        paragraphs.add("2Kgs.3.6"); //$NON-NLS-1$
        paragraphs.add("2Kgs.3.21"); //$NON-NLS-1$
        paragraphs.add("2Kgs.3.26"); //$NON-NLS-1$
        paragraphs.add("2Kgs.4.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.4.18"); //$NON-NLS-1$
        paragraphs.add("2Kgs.4.38"); //$NON-NLS-1$
        paragraphs.add("2Kgs.4.42"); //$NON-NLS-1$
        paragraphs.add("2Kgs.5.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.5.15"); //$NON-NLS-1$
        paragraphs.add("2Kgs.5.20"); //$NON-NLS-1$
        paragraphs.add("2Kgs.6.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.6.13"); //$NON-NLS-1$
        paragraphs.add("2Kgs.6.19"); //$NON-NLS-1$
        paragraphs.add("2Kgs.6.24"); //$NON-NLS-1$
        paragraphs.add("2Kgs.6.30"); //$NON-NLS-1$
        paragraphs.add("2Kgs.7.3"); //$NON-NLS-1$
        paragraphs.add("2Kgs.7.12"); //$NON-NLS-1$
        paragraphs.add("2Kgs.7.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.8.7"); //$NON-NLS-1$
        paragraphs.add("2Kgs.8.16"); //$NON-NLS-1$
        paragraphs.add("2Kgs.8.20"); //$NON-NLS-1$
        paragraphs.add("2Kgs.8.25"); //$NON-NLS-1$
        paragraphs.add("2Kgs.8.28"); //$NON-NLS-1$
        paragraphs.add("2Kgs.9.4"); //$NON-NLS-1$
        paragraphs.add("2Kgs.9.11"); //$NON-NLS-1$
        paragraphs.add("2Kgs.9.27"); //$NON-NLS-1$
        paragraphs.add("2Kgs.9.30"); //$NON-NLS-1$
        paragraphs.add("2Kgs.10.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.10.12"); //$NON-NLS-1$
        paragraphs.add("2Kgs.10.15"); //$NON-NLS-1$
        paragraphs.add("2Kgs.10.18"); //$NON-NLS-1$
        paragraphs.add("2Kgs.10.29"); //$NON-NLS-1$
        paragraphs.add("2Kgs.10.32"); //$NON-NLS-1$
        paragraphs.add("2Kgs.11.4"); //$NON-NLS-1$
        paragraphs.add("2Kgs.11.13"); //$NON-NLS-1$
        paragraphs.add("2Kgs.11.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.12.4"); //$NON-NLS-1$
        paragraphs.add("2Kgs.12.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.12.19"); //$NON-NLS-1$
        paragraphs.add("2Kgs.13.3"); //$NON-NLS-1$
        paragraphs.add("2Kgs.13.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.13.10"); //$NON-NLS-1$
        paragraphs.add("2Kgs.13.14"); //$NON-NLS-1$
        paragraphs.add("2Kgs.13.20"); //$NON-NLS-1$
        paragraphs.add("2Kgs.13.22"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.5"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.15"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.21"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.23"); //$NON-NLS-1$
        paragraphs.add("2Kgs.14.28"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.5"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.13"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.16"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.21"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.23"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.27"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.32"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.35"); //$NON-NLS-1$
        paragraphs.add("2Kgs.15.36"); //$NON-NLS-1$
        paragraphs.add("2Kgs.16.5"); //$NON-NLS-1$
        paragraphs.add("2Kgs.16.10"); //$NON-NLS-1$
        paragraphs.add("2Kgs.16.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.16.19"); //$NON-NLS-1$
        paragraphs.add("2Kgs.17.3"); //$NON-NLS-1$
        paragraphs.add("2Kgs.17.5"); //$NON-NLS-1$
        paragraphs.add("2Kgs.17.6"); //$NON-NLS-1$
        paragraphs.add("2Kgs.17.24"); //$NON-NLS-1$
        paragraphs.add("2Kgs.18.4"); //$NON-NLS-1$
        paragraphs.add("2Kgs.18.9"); //$NON-NLS-1$
        paragraphs.add("2Kgs.18.13"); //$NON-NLS-1$
        paragraphs.add("2Kgs.18.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.19.6"); //$NON-NLS-1$
        paragraphs.add("2Kgs.19.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.19.14"); //$NON-NLS-1$
        paragraphs.add("2Kgs.19.20"); //$NON-NLS-1$
        paragraphs.add("2Kgs.19.35"); //$NON-NLS-1$
        paragraphs.add("2Kgs.20.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.20.12"); //$NON-NLS-1$
        paragraphs.add("2Kgs.20.14"); //$NON-NLS-1$
        paragraphs.add("2Kgs.20.20"); //$NON-NLS-1$
        paragraphs.add("2Kgs.21.10"); //$NON-NLS-1$
        paragraphs.add("2Kgs.21.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.21.19"); //$NON-NLS-1$
        paragraphs.add("2Kgs.21.23"); //$NON-NLS-1$
        paragraphs.add("2Kgs.22.3"); //$NON-NLS-1$
        paragraphs.add("2Kgs.22.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.22.15"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.3"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.15"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.21"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.24"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.26"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.29"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.31"); //$NON-NLS-1$
        paragraphs.add("2Kgs.23.36"); //$NON-NLS-1$
        paragraphs.add("2Kgs.24.5"); //$NON-NLS-1$
        paragraphs.add("2Kgs.24.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.24.10"); //$NON-NLS-1$
        paragraphs.add("2Kgs.24.17"); //$NON-NLS-1$
        paragraphs.add("2Kgs.25.4"); //$NON-NLS-1$
        paragraphs.add("2Kgs.25.8"); //$NON-NLS-1$
        paragraphs.add("2Kgs.25.18"); //$NON-NLS-1$
        paragraphs.add("2Kgs.25.22"); //$NON-NLS-1$
        paragraphs.add("2Kgs.25.27"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.5"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.8"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.17"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.24"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.29"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.32"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.35"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.43"); //$NON-NLS-1$
        paragraphs.add("1Chr.1.51"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.3"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.13"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.18"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.21"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.25"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.34"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.42"); //$NON-NLS-1$
        paragraphs.add("1Chr.2.50"); //$NON-NLS-1$
        paragraphs.add("1Chr.3.10"); //$NON-NLS-1$
        paragraphs.add("1Chr.3.17"); //$NON-NLS-1$
        paragraphs.add("1Chr.4.5"); //$NON-NLS-1$
        paragraphs.add("1Chr.4.9"); //$NON-NLS-1$
        paragraphs.add("1Chr.4.11"); //$NON-NLS-1$
        paragraphs.add("1Chr.4.21"); //$NON-NLS-1$
        paragraphs.add("1Chr.4.24"); //$NON-NLS-1$
        paragraphs.add("1Chr.4.39"); //$NON-NLS-1$
        paragraphs.add("1Chr.5.11"); //$NON-NLS-1$
        paragraphs.add("1Chr.5.18"); //$NON-NLS-1$
        paragraphs.add("1Chr.5.23"); //$NON-NLS-1$
        paragraphs.add("1Chr.5.25"); //$NON-NLS-1$
        paragraphs.add("1Chr.6.4"); //$NON-NLS-1$
        paragraphs.add("1Chr.6.16"); //$NON-NLS-1$
        paragraphs.add("1Chr.6.49"); //$NON-NLS-1$
        paragraphs.add("1Chr.6.54"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.6"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.13"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.14"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.20"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.21"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.23"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.28"); //$NON-NLS-1$
        paragraphs.add("1Chr.7.30"); //$NON-NLS-1$
        paragraphs.add("1Chr.8.33"); //$NON-NLS-1$
        paragraphs.add("1Chr.9.2"); //$NON-NLS-1$
        paragraphs.add("1Chr.9.10"); //$NON-NLS-1$
        paragraphs.add("1Chr.9.27"); //$NON-NLS-1$
        paragraphs.add("1Chr.9.35"); //$NON-NLS-1$
        paragraphs.add("1Chr.10.8"); //$NON-NLS-1$
        paragraphs.add("1Chr.10.11"); //$NON-NLS-1$
        paragraphs.add("1Chr.10.13"); //$NON-NLS-1$
        paragraphs.add("1Chr.11.4"); //$NON-NLS-1$
        paragraphs.add("1Chr.11.10"); //$NON-NLS-1$
        paragraphs.add("1Chr.11.15"); //$NON-NLS-1$
        paragraphs.add("1Chr.11.20"); //$NON-NLS-1$
        paragraphs.add("1Chr.11.26"); //$NON-NLS-1$
        paragraphs.add("1Chr.12.23"); //$NON-NLS-1$
        paragraphs.add("1Chr.13.9"); //$NON-NLS-1$
        paragraphs.add("1Chr.14.3"); //$NON-NLS-1$
        paragraphs.add("1Chr.14.8"); //$NON-NLS-1$
        paragraphs.add("1Chr.15.25"); //$NON-NLS-1$
        paragraphs.add("1Chr.15.29"); //$NON-NLS-1$
        paragraphs.add("1Chr.16.4"); //$NON-NLS-1$
        paragraphs.add("1Chr.16.7"); //$NON-NLS-1$
        paragraphs.add("1Chr.16.37"); //$NON-NLS-1$
        paragraphs.add("1Chr.17.3"); //$NON-NLS-1$
        paragraphs.add("1Chr.17.11"); //$NON-NLS-1$
        paragraphs.add("1Chr.17.16"); //$NON-NLS-1$
        paragraphs.add("1Chr.18.3"); //$NON-NLS-1$
        paragraphs.add("1Chr.18.9"); //$NON-NLS-1$
        paragraphs.add("1Chr.18.11"); //$NON-NLS-1$
        paragraphs.add("1Chr.18.13"); //$NON-NLS-1$
        paragraphs.add("1Chr.18.14"); //$NON-NLS-1$
        paragraphs.add("1Chr.19.6"); //$NON-NLS-1$
        paragraphs.add("1Chr.19.16"); //$NON-NLS-1$
        paragraphs.add("1Chr.20.4"); //$NON-NLS-1$
        paragraphs.add("1Chr.21.5"); //$NON-NLS-1$
        paragraphs.add("1Chr.21.9"); //$NON-NLS-1$
        paragraphs.add("1Chr.21.14"); //$NON-NLS-1$
        paragraphs.add("1Chr.21.18"); //$NON-NLS-1$
        paragraphs.add("1Chr.21.28"); //$NON-NLS-1$
        paragraphs.add("1Chr.22.6"); //$NON-NLS-1$
        paragraphs.add("1Chr.22.17"); //$NON-NLS-1$
        paragraphs.add("1Chr.23.2"); //$NON-NLS-1$
        paragraphs.add("1Chr.23.7"); //$NON-NLS-1$
        paragraphs.add("1Chr.23.12"); //$NON-NLS-1$
        paragraphs.add("1Chr.23.21"); //$NON-NLS-1$
        paragraphs.add("1Chr.23.24"); //$NON-NLS-1$
        paragraphs.add("1Chr.24.20"); //$NON-NLS-1$
        paragraphs.add("1Chr.24.27"); //$NON-NLS-1$
        paragraphs.add("1Chr.25.8"); //$NON-NLS-1$
        paragraphs.add("1Chr.26.13"); //$NON-NLS-1$
        paragraphs.add("1Chr.26.20"); //$NON-NLS-1$
        paragraphs.add("1Chr.26.29"); //$NON-NLS-1$
        paragraphs.add("1Chr.27.16"); //$NON-NLS-1$
        paragraphs.add("1Chr.27.23"); //$NON-NLS-1$
        paragraphs.add("1Chr.27.25"); //$NON-NLS-1$
        paragraphs.add("1Chr.28.9"); //$NON-NLS-1$
        paragraphs.add("1Chr.28.11"); //$NON-NLS-1$
        paragraphs.add("1Chr.29.6"); //$NON-NLS-1$
        paragraphs.add("1Chr.29.10"); //$NON-NLS-1$
        paragraphs.add("1Chr.29.20"); //$NON-NLS-1$
        paragraphs.add("1Chr.29.26"); //$NON-NLS-1$
        paragraphs.add("2Chr.1.7"); //$NON-NLS-1$
        paragraphs.add("2Chr.1.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.2.3"); //$NON-NLS-1$
        paragraphs.add("2Chr.2.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.2.17"); //$NON-NLS-1$
        paragraphs.add("2Chr.3.3"); //$NON-NLS-1$
        paragraphs.add("2Chr.3.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.3.14"); //$NON-NLS-1$
        paragraphs.add("2Chr.4.2"); //$NON-NLS-1$
        paragraphs.add("2Chr.4.6"); //$NON-NLS-1$
        paragraphs.add("2Chr.4.9"); //$NON-NLS-1$
        paragraphs.add("2Chr.4.19"); //$NON-NLS-1$
        paragraphs.add("2Chr.5.2"); //$NON-NLS-1$
        paragraphs.add("2Chr.5.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.6.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.6.22"); //$NON-NLS-1$
        paragraphs.add("2Chr.6.24"); //$NON-NLS-1$
        paragraphs.add("2Chr.6.26"); //$NON-NLS-1$
        paragraphs.add("2Chr.6.28"); //$NON-NLS-1$
        paragraphs.add("2Chr.6.32"); //$NON-NLS-1$
        paragraphs.add("2Chr.7.4"); //$NON-NLS-1$
        paragraphs.add("2Chr.7.8"); //$NON-NLS-1$
        paragraphs.add("2Chr.7.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.8.7"); //$NON-NLS-1$
        paragraphs.add("2Chr.8.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.8.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.8.14"); //$NON-NLS-1$
        paragraphs.add("2Chr.8.17"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.15"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.20"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.23"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.25"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.26"); //$NON-NLS-1$
        paragraphs.add("2Chr.9.29"); //$NON-NLS-1$
        paragraphs.add("2Chr.10.6"); //$NON-NLS-1$
        paragraphs.add("2Chr.10.16"); //$NON-NLS-1$
        paragraphs.add("2Chr.11.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.11.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.11.18"); //$NON-NLS-1$
        paragraphs.add("2Chr.12.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.12.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.13.4"); //$NON-NLS-1$
        paragraphs.add("2Chr.13.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.13.21"); //$NON-NLS-1$
        paragraphs.add("2Chr.14.6"); //$NON-NLS-1$
        paragraphs.add("2Chr.14.9"); //$NON-NLS-1$
        paragraphs.add("2Chr.15.16"); //$NON-NLS-1$
        paragraphs.add("2Chr.15.18"); //$NON-NLS-1$
        paragraphs.add("2Chr.16.7"); //$NON-NLS-1$
        paragraphs.add("2Chr.16.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.16.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.17.7"); //$NON-NLS-1$
        paragraphs.add("2Chr.17.10"); //$NON-NLS-1$
        paragraphs.add("2Chr.17.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.18.4"); //$NON-NLS-1$
        paragraphs.add("2Chr.19.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.19.8"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.14"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.20"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.22"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.26"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.31"); //$NON-NLS-1$
        paragraphs.add("2Chr.20.35"); //$NON-NLS-1$
        paragraphs.add("2Chr.21.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.21.8"); //$NON-NLS-1$
        paragraphs.add("2Chr.21.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.21.16"); //$NON-NLS-1$
        paragraphs.add("2Chr.21.18"); //$NON-NLS-1$
        paragraphs.add("2Chr.22.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.22.10"); //$NON-NLS-1$
        paragraphs.add("2Chr.23.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.23.16"); //$NON-NLS-1$
        paragraphs.add("2Chr.24.4"); //$NON-NLS-1$
        paragraphs.add("2Chr.24.15"); //$NON-NLS-1$
        paragraphs.add("2Chr.24.23"); //$NON-NLS-1$
        paragraphs.add("2Chr.24.27"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.3"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.14"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.17"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.25"); //$NON-NLS-1$
        paragraphs.add("2Chr.25.27"); //$NON-NLS-1$
        paragraphs.add("2Chr.26.16"); //$NON-NLS-1$
        paragraphs.add("2Chr.26.22"); //$NON-NLS-1$
        paragraphs.add("2Chr.27.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.27.7"); //$NON-NLS-1$
        paragraphs.add("2Chr.27.9"); //$NON-NLS-1$
        paragraphs.add("2Chr.28.6"); //$NON-NLS-1$
        paragraphs.add("2Chr.28.16"); //$NON-NLS-1$
        paragraphs.add("2Chr.28.22"); //$NON-NLS-1$
        paragraphs.add("2Chr.28.26"); //$NON-NLS-1$
        paragraphs.add("2Chr.29.3"); //$NON-NLS-1$
        paragraphs.add("2Chr.29.12"); //$NON-NLS-1$
        paragraphs.add("2Chr.29.20"); //$NON-NLS-1$
        paragraphs.add("2Chr.30.13"); //$NON-NLS-1$
        paragraphs.add("2Chr.30.27"); //$NON-NLS-1$
        paragraphs.add("2Chr.31.2"); //$NON-NLS-1$
        paragraphs.add("2Chr.31.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.31.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.31.20"); //$NON-NLS-1$
        paragraphs.add("2Chr.32.9"); //$NON-NLS-1$
        paragraphs.add("2Chr.32.21"); //$NON-NLS-1$
        paragraphs.add("2Chr.32.24"); //$NON-NLS-1$
        paragraphs.add("2Chr.32.27"); //$NON-NLS-1$
        paragraphs.add("2Chr.32.31"); //$NON-NLS-1$
        paragraphs.add("2Chr.32.32"); //$NON-NLS-1$
        paragraphs.add("2Chr.33.3"); //$NON-NLS-1$
        paragraphs.add("2Chr.33.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.33.18"); //$NON-NLS-1$
        paragraphs.add("2Chr.33.20"); //$NON-NLS-1$
        paragraphs.add("2Chr.33.21"); //$NON-NLS-1$
        paragraphs.add("2Chr.33.25"); //$NON-NLS-1$
        paragraphs.add("2Chr.34.3"); //$NON-NLS-1$
        paragraphs.add("2Chr.34.8"); //$NON-NLS-1$
        paragraphs.add("2Chr.34.14"); //$NON-NLS-1$
        paragraphs.add("2Chr.34.23"); //$NON-NLS-1$
        paragraphs.add("2Chr.34.29"); //$NON-NLS-1$
        paragraphs.add("2Chr.35.20"); //$NON-NLS-1$
        paragraphs.add("2Chr.35.25"); //$NON-NLS-1$
        paragraphs.add("2Chr.36.5"); //$NON-NLS-1$
        paragraphs.add("2Chr.36.9"); //$NON-NLS-1$
        paragraphs.add("2Chr.36.11"); //$NON-NLS-1$
        paragraphs.add("2Chr.36.14"); //$NON-NLS-1$
        paragraphs.add("2Chr.36.22"); //$NON-NLS-1$
        paragraphs.add("Ezra.1.5"); //$NON-NLS-1$
        paragraphs.add("Ezra.1.7"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.36"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.40"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.41"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.42"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.43"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.55"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.61"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.64"); //$NON-NLS-1$
        paragraphs.add("Ezra.2.68"); //$NON-NLS-1$
        paragraphs.add("Ezra.3.8"); //$NON-NLS-1$
        paragraphs.add("Ezra.4.7"); //$NON-NLS-1$
        paragraphs.add("Ezra.4.11"); //$NON-NLS-1$
        paragraphs.add("Ezra.4.17"); //$NON-NLS-1$
        paragraphs.add("Ezra.4.23"); //$NON-NLS-1$
        paragraphs.add("Ezra.5.3"); //$NON-NLS-1$
        paragraphs.add("Ezra.5.6"); //$NON-NLS-1$
        paragraphs.add("Ezra.6.13"); //$NON-NLS-1$
        paragraphs.add("Ezra.6.16"); //$NON-NLS-1$
        paragraphs.add("Ezra.7.11"); //$NON-NLS-1$
        paragraphs.add("Ezra.7.27"); //$NON-NLS-1$
        paragraphs.add("Ezra.8.15"); //$NON-NLS-1$
        paragraphs.add("Ezra.8.21"); //$NON-NLS-1$
        paragraphs.add("Ezra.8.24"); //$NON-NLS-1$
        paragraphs.add("Ezra.8.31"); //$NON-NLS-1$
        paragraphs.add("Ezra.8.33"); //$NON-NLS-1$
        paragraphs.add("Ezra.8.36"); //$NON-NLS-1$
        paragraphs.add("Ezra.9.5"); //$NON-NLS-1$
        paragraphs.add("Ezra.10.6"); //$NON-NLS-1$
        paragraphs.add("Ezra.10.9"); //$NON-NLS-1$
        paragraphs.add("Ezra.10.15"); //$NON-NLS-1$
        paragraphs.add("Ezra.10.18"); //$NON-NLS-1$
        paragraphs.add("Neh.1.4"); //$NON-NLS-1$
        paragraphs.add("Neh.2.9"); //$NON-NLS-1$
        paragraphs.add("Neh.2.12"); //$NON-NLS-1$
        paragraphs.add("Neh.2.17"); //$NON-NLS-1$
        paragraphs.add("Neh.4.7"); //$NON-NLS-1$
        paragraphs.add("Neh.4.13"); //$NON-NLS-1$
        paragraphs.add("Neh.4.19"); //$NON-NLS-1$
        paragraphs.add("Neh.5.6"); //$NON-NLS-1$
        paragraphs.add("Neh.5.14"); //$NON-NLS-1$
        paragraphs.add("Neh.6.15"); //$NON-NLS-1$
        paragraphs.add("Neh.6.17"); //$NON-NLS-1$
        paragraphs.add("Neh.7.5"); //$NON-NLS-1$
        paragraphs.add("Neh.7.39"); //$NON-NLS-1$
        paragraphs.add("Neh.7.43"); //$NON-NLS-1$
        paragraphs.add("Neh.7.44"); //$NON-NLS-1$
        paragraphs.add("Neh.7.45"); //$NON-NLS-1$
        paragraphs.add("Neh.7.46"); //$NON-NLS-1$
        paragraphs.add("Neh.7.57"); //$NON-NLS-1$
        paragraphs.add("Neh.7.63"); //$NON-NLS-1$
        paragraphs.add("Neh.7.66"); //$NON-NLS-1$
        paragraphs.add("Neh.7.70"); //$NON-NLS-1$
        paragraphs.add("Neh.8.9"); //$NON-NLS-1$
        paragraphs.add("Neh.8.13"); //$NON-NLS-1$
        paragraphs.add("Neh.8.16"); //$NON-NLS-1$
        paragraphs.add("Neh.9.4"); //$NON-NLS-1$
        paragraphs.add("Neh.10.28"); //$NON-NLS-1$
        paragraphs.add("Neh.11.3"); //$NON-NLS-1$
        paragraphs.add("Neh.11.20"); //$NON-NLS-1$
        paragraphs.add("Neh.12.10"); //$NON-NLS-1$
        paragraphs.add("Neh.12.22"); //$NON-NLS-1$
        paragraphs.add("Neh.12.27"); //$NON-NLS-1$
        paragraphs.add("Neh.12.44"); //$NON-NLS-1$
        paragraphs.add("Neh.13.4"); //$NON-NLS-1$
        paragraphs.add("Neh.13.10"); //$NON-NLS-1$
        paragraphs.add("Neh.13.15"); //$NON-NLS-1$
        paragraphs.add("Neh.13.23"); //$NON-NLS-1$
        paragraphs.add("Esth.1.10"); //$NON-NLS-1$
        paragraphs.add("Esth.1.13"); //$NON-NLS-1$
        paragraphs.add("Esth.2.5"); //$NON-NLS-1$
        paragraphs.add("Esth.2.8"); //$NON-NLS-1$
        paragraphs.add("Esth.2.12"); //$NON-NLS-1$
        paragraphs.add("Esth.2.15"); //$NON-NLS-1$
        paragraphs.add("Esth.2.21"); //$NON-NLS-1$
        paragraphs.add("Esth.3.7"); //$NON-NLS-1$
        paragraphs.add("Esth.3.8"); //$NON-NLS-1$
        paragraphs.add("Esth.4.4"); //$NON-NLS-1$
        paragraphs.add("Esth.4.10"); //$NON-NLS-1$
        paragraphs.add("Esth.4.15"); //$NON-NLS-1$
        paragraphs.add("Esth.5.6"); //$NON-NLS-1$
        paragraphs.add("Esth.5.9"); //$NON-NLS-1$
        paragraphs.add("Esth.5.14"); //$NON-NLS-1$
        paragraphs.add("Esth.6.4"); //$NON-NLS-1$
        paragraphs.add("Esth.6.12"); //$NON-NLS-1$
        paragraphs.add("Esth.7.5"); //$NON-NLS-1$
        paragraphs.add("Esth.7.7"); //$NON-NLS-1$
        paragraphs.add("Esth.8.3"); //$NON-NLS-1$
        paragraphs.add("Esth.8.7"); //$NON-NLS-1$
        paragraphs.add("Esth.8.15"); //$NON-NLS-1$
        paragraphs.add("Esth.9.12"); //$NON-NLS-1$
        paragraphs.add("Esth.9.20"); //$NON-NLS-1$
        paragraphs.add("Job.1.6"); //$NON-NLS-1$
        paragraphs.add("Job.1.13"); //$NON-NLS-1$
        paragraphs.add("Job.2.7"); //$NON-NLS-1$
        paragraphs.add("Job.2.9"); //$NON-NLS-1$
        paragraphs.add("Job.2.11"); //$NON-NLS-1$
        paragraphs.add("Job.40.3"); //$NON-NLS-1$
        paragraphs.add("Job.40.6"); //$NON-NLS-1$
        paragraphs.add("Job.40.15"); //$NON-NLS-1$
        paragraphs.add("Job.42.7"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.9"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.17"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.25"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.33"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.41"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.49"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.57"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.65"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.73"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.81"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.89"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.97"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.105"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.113"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.121"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.129"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.137"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.145"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.153"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.161"); //$NON-NLS-1$
        //paragraphs.add("Ps.119.169"); //$NON-NLS-1$
        paragraphs.add("Prov.1.7"); //$NON-NLS-1$
        paragraphs.add("Prov.1.10"); //$NON-NLS-1$
        paragraphs.add("Prov.1.20"); //$NON-NLS-1$
        paragraphs.add("Prov.1.24"); //$NON-NLS-1$
        paragraphs.add("Prov.2.10"); //$NON-NLS-1$
        paragraphs.add("Prov.3.5"); //$NON-NLS-1$
        paragraphs.add("Prov.3.7"); //$NON-NLS-1$
        paragraphs.add("Prov.3.11"); //$NON-NLS-1$
        paragraphs.add("Prov.3.13"); //$NON-NLS-1$
        paragraphs.add("Prov.3.21"); //$NON-NLS-1$
        paragraphs.add("Prov.3.27"); //$NON-NLS-1$
        paragraphs.add("Prov.3.30"); //$NON-NLS-1$
        paragraphs.add("Prov.3.31"); //$NON-NLS-1$
        paragraphs.add("Prov.3.33"); //$NON-NLS-1$
        paragraphs.add("Prov.4.14"); //$NON-NLS-1$
        paragraphs.add("Prov.4.20"); //$NON-NLS-1$
        paragraphs.add("Prov.4.23"); //$NON-NLS-1$
        paragraphs.add("Prov.5.3"); //$NON-NLS-1$
        paragraphs.add("Prov.5.15"); //$NON-NLS-1$
        paragraphs.add("Prov.5.22"); //$NON-NLS-1$
        paragraphs.add("Prov.6.6"); //$NON-NLS-1$
        paragraphs.add("Prov.6.12"); //$NON-NLS-1$
        paragraphs.add("Prov.6.16"); //$NON-NLS-1$
        paragraphs.add("Prov.6.20"); //$NON-NLS-1$
        paragraphs.add("Prov.7.6"); //$NON-NLS-1$
        paragraphs.add("Prov.7.24"); //$NON-NLS-1$
        paragraphs.add("Prov.9.13"); //$NON-NLS-1$
        paragraphs.add("Prov.31.10"); //$NON-NLS-1$
        paragraphs.add("Eccl.1.12"); //$NON-NLS-1$
        paragraphs.add("Eccl.2.12"); //$NON-NLS-1$
        paragraphs.add("Eccl.2.18"); //$NON-NLS-1$
        paragraphs.add("Eccl.2.24"); //$NON-NLS-1$
        paragraphs.add("Eccl.3.16"); //$NON-NLS-1$
        paragraphs.add("Eccl.4.4"); //$NON-NLS-1$
        paragraphs.add("Eccl.4.7"); //$NON-NLS-1$
        paragraphs.add("Eccl.4.9"); //$NON-NLS-1$
        paragraphs.add("Eccl.4.13"); //$NON-NLS-1$
        paragraphs.add("Eccl.5.8"); //$NON-NLS-1$
        paragraphs.add("Eccl.5.9"); //$NON-NLS-1$
        paragraphs.add("Eccl.5.18"); //$NON-NLS-1$
        paragraphs.add("Eccl.6.3"); //$NON-NLS-1$
        paragraphs.add("Eccl.6.6"); //$NON-NLS-1$
        paragraphs.add("Eccl.6.9"); //$NON-NLS-1$
        paragraphs.add("Eccl.6.11"); //$NON-NLS-1$
        paragraphs.add("Eccl.7.2"); //$NON-NLS-1$
        paragraphs.add("Eccl.7.7"); //$NON-NLS-1$
        paragraphs.add("Eccl.7.11"); //$NON-NLS-1$
        paragraphs.add("Eccl.7.23"); //$NON-NLS-1$
        paragraphs.add("Eccl.8.6"); //$NON-NLS-1$
        paragraphs.add("Eccl.8.12"); //$NON-NLS-1$
        paragraphs.add("Eccl.8.16"); //$NON-NLS-1$
        paragraphs.add("Eccl.9.4"); //$NON-NLS-1$
        paragraphs.add("Eccl.9.7"); //$NON-NLS-1$
        paragraphs.add("Eccl.9.11"); //$NON-NLS-1$
        paragraphs.add("Eccl.9.13"); //$NON-NLS-1$
        paragraphs.add("Eccl.10.16"); //$NON-NLS-1$
        paragraphs.add("Eccl.10.18"); //$NON-NLS-1$
        paragraphs.add("Eccl.10.19"); //$NON-NLS-1$
        paragraphs.add("Eccl.10.20"); //$NON-NLS-1$
        paragraphs.add("Eccl.11.7"); //$NON-NLS-1$
        paragraphs.add("Eccl.11.9"); //$NON-NLS-1$
        paragraphs.add("Eccl.12.8"); //$NON-NLS-1$
        paragraphs.add("Eccl.12.13"); //$NON-NLS-1$
        paragraphs.add("Song.1.8"); //$NON-NLS-1$
        paragraphs.add("Song.1.12"); //$NON-NLS-1$
        paragraphs.add("Song.2.8"); //$NON-NLS-1$
        paragraphs.add("Song.2.14"); //$NON-NLS-1$
        paragraphs.add("Song.2.16"); //$NON-NLS-1$
        paragraphs.add("Song.3.6"); //$NON-NLS-1$
        paragraphs.add("Song.4.8"); //$NON-NLS-1$
        paragraphs.add("Song.4.16"); //$NON-NLS-1$
        paragraphs.add("Song.5.2"); //$NON-NLS-1$
        paragraphs.add("Song.5.9"); //$NON-NLS-1$
        paragraphs.add("Song.6.4"); //$NON-NLS-1$
        paragraphs.add("Song.6.10"); //$NON-NLS-1$
        paragraphs.add("Song.7.10"); //$NON-NLS-1$
        paragraphs.add("Song.8.6"); //$NON-NLS-1$
        paragraphs.add("Song.8.8"); //$NON-NLS-1$
        paragraphs.add("Song.8.14"); //$NON-NLS-1$
        paragraphs.add("Isa.1.5"); //$NON-NLS-1$
        paragraphs.add("Isa.1.10"); //$NON-NLS-1$
        paragraphs.add("Isa.1.16"); //$NON-NLS-1$
        paragraphs.add("Isa.1.21"); //$NON-NLS-1$
        paragraphs.add("Isa.1.25"); //$NON-NLS-1$
        paragraphs.add("Isa.1.28"); //$NON-NLS-1$
        paragraphs.add("Isa.2.6"); //$NON-NLS-1$
        paragraphs.add("Isa.2.10"); //$NON-NLS-1$
        paragraphs.add("Isa.3.9"); //$NON-NLS-1$
        paragraphs.add("Isa.3.12"); //$NON-NLS-1$
        paragraphs.add("Isa.3.16"); //$NON-NLS-1$
        paragraphs.add("Isa.5.8"); //$NON-NLS-1$
        paragraphs.add("Isa.5.11"); //$NON-NLS-1$
        paragraphs.add("Isa.5.13"); //$NON-NLS-1$
        paragraphs.add("Isa.5.20"); //$NON-NLS-1$
        paragraphs.add("Isa.5.26"); //$NON-NLS-1$
        paragraphs.add("Isa.6.5"); //$NON-NLS-1$
        paragraphs.add("Isa.6.9"); //$NON-NLS-1$
        paragraphs.add("Isa.6.13"); //$NON-NLS-1$
        paragraphs.add("Isa.7.10"); //$NON-NLS-1$
        paragraphs.add("Isa.7.17"); //$NON-NLS-1$
        paragraphs.add("Isa.8.5"); //$NON-NLS-1$
        paragraphs.add("Isa.8.9"); //$NON-NLS-1$
        paragraphs.add("Isa.8.11"); //$NON-NLS-1$
        paragraphs.add("Isa.8.19"); //$NON-NLS-1$
        paragraphs.add("Isa.9.8"); //$NON-NLS-1$
        paragraphs.add("Isa.9.13"); //$NON-NLS-1$
        paragraphs.add("Isa.9.18"); //$NON-NLS-1$
        paragraphs.add("Isa.10.5"); //$NON-NLS-1$
        paragraphs.add("Isa.10.20"); //$NON-NLS-1$
        paragraphs.add("Isa.10.24"); //$NON-NLS-1$
        paragraphs.add("Isa.11.10"); //$NON-NLS-1$
        paragraphs.add("Isa.13.6"); //$NON-NLS-1$
        paragraphs.add("Isa.13.19"); //$NON-NLS-1$
        paragraphs.add("Isa.14.4"); //$NON-NLS-1$
        paragraphs.add("Isa.14.24"); //$NON-NLS-1$
        paragraphs.add("Isa.14.29"); //$NON-NLS-1$
        paragraphs.add("Isa.16.6"); //$NON-NLS-1$
        paragraphs.add("Isa.16.9"); //$NON-NLS-1$
        paragraphs.add("Isa.16.12"); //$NON-NLS-1$
        paragraphs.add("Isa.17.6"); //$NON-NLS-1$
        paragraphs.add("Isa.17.9"); //$NON-NLS-1$
        paragraphs.add("Isa.17.12"); //$NON-NLS-1$
        paragraphs.add("Isa.18.7"); //$NON-NLS-1$
        paragraphs.add("Isa.19.11"); //$NON-NLS-1$
        paragraphs.add("Isa.19.18"); //$NON-NLS-1$
        paragraphs.add("Isa.19.23"); //$NON-NLS-1$
        paragraphs.add("Isa.21.11"); //$NON-NLS-1$
        paragraphs.add("Isa.21.13"); //$NON-NLS-1$
        paragraphs.add("Isa.22.8"); //$NON-NLS-1$
        paragraphs.add("Isa.22.15"); //$NON-NLS-1$
        paragraphs.add("Isa.22.20"); //$NON-NLS-1$
        paragraphs.add("Isa.23.17"); //$NON-NLS-1$
        paragraphs.add("Isa.24.13"); //$NON-NLS-1$
        paragraphs.add("Isa.24.16"); //$NON-NLS-1$
        paragraphs.add("Isa.25.6"); //$NON-NLS-1$
        paragraphs.add("Isa.25.9"); //$NON-NLS-1$
        paragraphs.add("Isa.26.5"); //$NON-NLS-1$
        paragraphs.add("Isa.26.12"); //$NON-NLS-1$
        paragraphs.add("Isa.26.20"); //$NON-NLS-1$
        paragraphs.add("Isa.27.7"); //$NON-NLS-1$
        paragraphs.add("Isa.27.12"); //$NON-NLS-1$
        paragraphs.add("Isa.28.5"); //$NON-NLS-1$
        paragraphs.add("Isa.28.7"); //$NON-NLS-1$
        paragraphs.add("Isa.28.9"); //$NON-NLS-1$
        paragraphs.add("Isa.28.14"); //$NON-NLS-1$
        paragraphs.add("Isa.28.16"); //$NON-NLS-1$
        paragraphs.add("Isa.28.18"); //$NON-NLS-1$
        paragraphs.add("Isa.28.23"); //$NON-NLS-1$
        paragraphs.add("Isa.29.7"); //$NON-NLS-1$
        paragraphs.add("Isa.29.9"); //$NON-NLS-1$
        paragraphs.add("Isa.29.13"); //$NON-NLS-1$
        paragraphs.add("Isa.29.18"); //$NON-NLS-1$
        paragraphs.add("Isa.30.8"); //$NON-NLS-1$
        paragraphs.add("Isa.30.18"); //$NON-NLS-1$
        paragraphs.add("Isa.30.27"); //$NON-NLS-1$
        paragraphs.add("Isa.31.6"); //$NON-NLS-1$
        paragraphs.add("Isa.31.8"); //$NON-NLS-1$
        paragraphs.add("Isa.32.9"); //$NON-NLS-1$
        paragraphs.add("Isa.33.13"); //$NON-NLS-1$
        paragraphs.add("Isa.34.11"); //$NON-NLS-1$
        paragraphs.add("Isa.34.16"); //$NON-NLS-1$
        paragraphs.add("Isa.35.3"); //$NON-NLS-1$
        paragraphs.add("Isa.36.4"); //$NON-NLS-1$
        paragraphs.add("Isa.36.11"); //$NON-NLS-1$
        paragraphs.add("Isa.36.12"); //$NON-NLS-1$
        paragraphs.add("Isa.36.22"); //$NON-NLS-1$
        paragraphs.add("Isa.37.6"); //$NON-NLS-1$
        paragraphs.add("Isa.37.8"); //$NON-NLS-1$
        paragraphs.add("Isa.37.14"); //$NON-NLS-1$
        paragraphs.add("Isa.37.21"); //$NON-NLS-1$
        paragraphs.add("Isa.37.37"); //$NON-NLS-1$
        paragraphs.add("Isa.38.4"); //$NON-NLS-1$
        paragraphs.add("Isa.38.9"); //$NON-NLS-1$
        paragraphs.add("Isa.39.3"); //$NON-NLS-1$
        paragraphs.add("Isa.40.3"); //$NON-NLS-1$
        paragraphs.add("Isa.40.9"); //$NON-NLS-1$
        paragraphs.add("Isa.40.12"); //$NON-NLS-1$
        paragraphs.add("Isa.40.18"); //$NON-NLS-1$
        paragraphs.add("Isa.40.28"); //$NON-NLS-1$
        paragraphs.add("Isa.41.10"); //$NON-NLS-1$
        paragraphs.add("Isa.42.5"); //$NON-NLS-1$
        paragraphs.add("Isa.42.17"); //$NON-NLS-1$
        paragraphs.add("Isa.43.8"); //$NON-NLS-1$
        paragraphs.add("Isa.43.14"); //$NON-NLS-1$
        paragraphs.add("Isa.43.18"); //$NON-NLS-1$
        paragraphs.add("Isa.43.22"); //$NON-NLS-1$
        paragraphs.add("Isa.44.9"); //$NON-NLS-1$
        paragraphs.add("Isa.44.21"); //$NON-NLS-1$
        paragraphs.add("Isa.45.5"); //$NON-NLS-1$
        paragraphs.add("Isa.45.20"); //$NON-NLS-1$
        paragraphs.add("Isa.46.3"); //$NON-NLS-1$
        paragraphs.add("Isa.46.5"); //$NON-NLS-1$
        paragraphs.add("Isa.46.12"); //$NON-NLS-1$
        paragraphs.add("Isa.47.6"); //$NON-NLS-1$
        paragraphs.add("Isa.47.7"); //$NON-NLS-1$
        paragraphs.add("Isa.47.10"); //$NON-NLS-1$
        paragraphs.add("Isa.47.11"); //$NON-NLS-1$
        paragraphs.add("Isa.48.9"); //$NON-NLS-1$
        paragraphs.add("Isa.48.12"); //$NON-NLS-1$
        paragraphs.add("Isa.48.16"); //$NON-NLS-1$
        paragraphs.add("Isa.48.20"); //$NON-NLS-1$
        paragraphs.add("Isa.49.5"); //$NON-NLS-1$
        paragraphs.add("Isa.49.13"); //$NON-NLS-1$
        paragraphs.add("Isa.49.18"); //$NON-NLS-1$
        paragraphs.add("Isa.49.24"); //$NON-NLS-1$
        paragraphs.add("Isa.50.5"); //$NON-NLS-1$
        paragraphs.add("Isa.50.7"); //$NON-NLS-1$
        paragraphs.add("Isa.50.10"); //$NON-NLS-1$
        paragraphs.add("Isa.51.4"); //$NON-NLS-1$
        paragraphs.add("Isa.51.7"); //$NON-NLS-1$
        paragraphs.add("Isa.51.9"); //$NON-NLS-1$
        paragraphs.add("Isa.51.17"); //$NON-NLS-1$
        paragraphs.add("Isa.51.21"); //$NON-NLS-1$
        paragraphs.add("Isa.52.7"); //$NON-NLS-1$
        paragraphs.add("Isa.52.9"); //$NON-NLS-1$
        paragraphs.add("Isa.52.11"); //$NON-NLS-1$
        paragraphs.add("Isa.52.13"); //$NON-NLS-1$
        paragraphs.add("Isa.53.4"); //$NON-NLS-1$
        paragraphs.add("Isa.53.10"); //$NON-NLS-1$
        paragraphs.add("Isa.54.11"); //$NON-NLS-1$
        paragraphs.add("Isa.54.17"); //$NON-NLS-1$
        paragraphs.add("Isa.55.6"); //$NON-NLS-1$
        paragraphs.add("Isa.55.8"); //$NON-NLS-1$
        paragraphs.add("Isa.56.3"); //$NON-NLS-1$
        paragraphs.add("Isa.56.9"); //$NON-NLS-1$
        paragraphs.add("Isa.57.3"); //$NON-NLS-1$
        paragraphs.add("Isa.57.13"); //$NON-NLS-1$
        paragraphs.add("Isa.58.3"); //$NON-NLS-1$
        paragraphs.add("Isa.58.8"); //$NON-NLS-1$
        paragraphs.add("Isa.58.13"); //$NON-NLS-1$
        paragraphs.add("Isa.59.9"); //$NON-NLS-1$
        paragraphs.add("Isa.59.16"); //$NON-NLS-1$
        paragraphs.add("Isa.59.20"); //$NON-NLS-1$
        paragraphs.add("Isa.61.4"); //$NON-NLS-1$
        paragraphs.add("Isa.61.7"); //$NON-NLS-1$
        paragraphs.add("Isa.62.5"); //$NON-NLS-1$
        paragraphs.add("Isa.62.10"); //$NON-NLS-1$
        paragraphs.add("Isa.63.7"); //$NON-NLS-1$
        paragraphs.add("Isa.63.10"); //$NON-NLS-1$
        paragraphs.add("Isa.63.15"); //$NON-NLS-1$
        paragraphs.add("Isa.63.17"); //$NON-NLS-1$
        paragraphs.add("Isa.64.9"); //$NON-NLS-1$
        paragraphs.add("Isa.65.8"); //$NON-NLS-1$
        paragraphs.add("Isa.65.11"); //$NON-NLS-1$
        paragraphs.add("Isa.65.17"); //$NON-NLS-1$
        paragraphs.add("Isa.66.5"); //$NON-NLS-1$
        paragraphs.add("Jer.1.7"); //$NON-NLS-1$
        paragraphs.add("Jer.1.11"); //$NON-NLS-1$
        paragraphs.add("Jer.1.17"); //$NON-NLS-1$
        paragraphs.add("Jer.2.5"); //$NON-NLS-1$
        paragraphs.add("Jer.2.9"); //$NON-NLS-1$
        paragraphs.add("Jer.2.14"); //$NON-NLS-1$
        paragraphs.add("Jer.2.20"); //$NON-NLS-1$
        paragraphs.add("Jer.2.31"); //$NON-NLS-1$
        paragraphs.add("Jer.3.6"); //$NON-NLS-1$
        paragraphs.add("Jer.3.12"); //$NON-NLS-1$
        paragraphs.add("Jer.3.20"); //$NON-NLS-1$
        paragraphs.add("Jer.4.3"); //$NON-NLS-1$
        paragraphs.add("Jer.4.19"); //$NON-NLS-1$
        paragraphs.add("Jer.5.7"); //$NON-NLS-1$
        paragraphs.add("Jer.5.10"); //$NON-NLS-1$
        paragraphs.add("Jer.5.19"); //$NON-NLS-1$
        paragraphs.add("Jer.5.25"); //$NON-NLS-1$
        paragraphs.add("Jer.5.30"); //$NON-NLS-1$
        paragraphs.add("Jer.6.6"); //$NON-NLS-1$
        paragraphs.add("Jer.6.9"); //$NON-NLS-1$
        paragraphs.add("Jer.6.18"); //$NON-NLS-1$
        paragraphs.add("Jer.6.26"); //$NON-NLS-1$
        paragraphs.add("Jer.7.8"); //$NON-NLS-1$
        paragraphs.add("Jer.7.17"); //$NON-NLS-1$
        paragraphs.add("Jer.7.21"); //$NON-NLS-1$
        paragraphs.add("Jer.7.29"); //$NON-NLS-1$
        paragraphs.add("Jer.7.32"); //$NON-NLS-1$
        paragraphs.add("Jer.8.4"); //$NON-NLS-1$
        paragraphs.add("Jer.8.13"); //$NON-NLS-1$
        paragraphs.add("Jer.8.18"); //$NON-NLS-1$
        paragraphs.add("Jer.9.9"); //$NON-NLS-1$
        paragraphs.add("Jer.9.12"); //$NON-NLS-1$
        paragraphs.add("Jer.9.17"); //$NON-NLS-1$
        paragraphs.add("Jer.9.23"); //$NON-NLS-1$
        paragraphs.add("Jer.9.25"); //$NON-NLS-1$
        paragraphs.add("Jer.10.17"); //$NON-NLS-1$
        paragraphs.add("Jer.10.19"); //$NON-NLS-1$
        paragraphs.add("Jer.10.23"); //$NON-NLS-1$
        paragraphs.add("Jer.11.11"); //$NON-NLS-1$
        paragraphs.add("Jer.11.18"); //$NON-NLS-1$
        paragraphs.add("Jer.12.5"); //$NON-NLS-1$
        paragraphs.add("Jer.12.7"); //$NON-NLS-1$
        paragraphs.add("Jer.12.14"); //$NON-NLS-1$
        paragraphs.add("Jer.13.12"); //$NON-NLS-1$
        paragraphs.add("Jer.13.15"); //$NON-NLS-1$
        paragraphs.add("Jer.13.22"); //$NON-NLS-1$
        paragraphs.add("Jer.14.7"); //$NON-NLS-1$
        paragraphs.add("Jer.14.10"); //$NON-NLS-1$
        paragraphs.add("Jer.14.13"); //$NON-NLS-1$
        paragraphs.add("Jer.14.17"); //$NON-NLS-1$
        paragraphs.add("Jer.15.10"); //$NON-NLS-1$
        paragraphs.add("Jer.15.15"); //$NON-NLS-1$
        paragraphs.add("Jer.15.19"); //$NON-NLS-1$
        paragraphs.add("Jer.16.10"); //$NON-NLS-1$
        paragraphs.add("Jer.16.14"); //$NON-NLS-1$
        paragraphs.add("Jer.16.16"); //$NON-NLS-1$
        paragraphs.add("Jer.17.5"); //$NON-NLS-1$
        paragraphs.add("Jer.17.9"); //$NON-NLS-1$
        paragraphs.add("Jer.17.12"); //$NON-NLS-1$
        paragraphs.add("Jer.17.15"); //$NON-NLS-1$
        paragraphs.add("Jer.17.19"); //$NON-NLS-1$
        paragraphs.add("Jer.18.11"); //$NON-NLS-1$
        paragraphs.add("Jer.18.18"); //$NON-NLS-1$
        paragraphs.add("Jer.20.7"); //$NON-NLS-1$
        paragraphs.add("Jer.20.10"); //$NON-NLS-1$
        paragraphs.add("Jer.20.14"); //$NON-NLS-1$
        paragraphs.add("Jer.21.3"); //$NON-NLS-1$
        paragraphs.add("Jer.21.8"); //$NON-NLS-1$
        paragraphs.add("Jer.21.11"); //$NON-NLS-1$
        paragraphs.add("Jer.22.10"); //$NON-NLS-1$
        paragraphs.add("Jer.22.13"); //$NON-NLS-1$
        paragraphs.add("Jer.22.20"); //$NON-NLS-1$
        paragraphs.add("Jer.23.5"); //$NON-NLS-1$
        paragraphs.add("Jer.23.9"); //$NON-NLS-1$
        paragraphs.add("Jer.23.33"); //$NON-NLS-1$
        paragraphs.add("Jer.24.4"); //$NON-NLS-1$
        paragraphs.add("Jer.24.8"); //$NON-NLS-1$
        paragraphs.add("Jer.25.8"); //$NON-NLS-1$
        paragraphs.add("Jer.25.12"); //$NON-NLS-1$
        paragraphs.add("Jer.25.15"); //$NON-NLS-1$
        paragraphs.add("Jer.25.34"); //$NON-NLS-1$
        paragraphs.add("Jer.26.8"); //$NON-NLS-1$
        paragraphs.add("Jer.26.10"); //$NON-NLS-1$
        paragraphs.add("Jer.26.12"); //$NON-NLS-1$
        paragraphs.add("Jer.26.16"); //$NON-NLS-1$
        paragraphs.add("Jer.27.12"); //$NON-NLS-1$
        paragraphs.add("Jer.27.19"); //$NON-NLS-1$
        paragraphs.add("Jer.28.5"); //$NON-NLS-1$
        paragraphs.add("Jer.28.10"); //$NON-NLS-1$
        paragraphs.add("Jer.28.12"); //$NON-NLS-1$
        paragraphs.add("Jer.28.15"); //$NON-NLS-1$
        paragraphs.add("Jer.29.8"); //$NON-NLS-1$
        paragraphs.add("Jer.29.10"); //$NON-NLS-1$
        paragraphs.add("Jer.29.15"); //$NON-NLS-1$
        paragraphs.add("Jer.29.20"); //$NON-NLS-1$
        paragraphs.add("Jer.29.24"); //$NON-NLS-1$
        paragraphs.add("Jer.29.30"); //$NON-NLS-1$
        paragraphs.add("Jer.30.4"); //$NON-NLS-1$
        paragraphs.add("Jer.30.10"); //$NON-NLS-1$
        paragraphs.add("Jer.30.18"); //$NON-NLS-1$
        paragraphs.add("Jer.31.10"); //$NON-NLS-1$
        paragraphs.add("Jer.31.15"); //$NON-NLS-1$
        paragraphs.add("Jer.31.18"); //$NON-NLS-1$
        paragraphs.add("Jer.31.22"); //$NON-NLS-1$
        paragraphs.add("Jer.31.27"); //$NON-NLS-1$
        paragraphs.add("Jer.31.31"); //$NON-NLS-1$
        paragraphs.add("Jer.31.35"); //$NON-NLS-1$
        paragraphs.add("Jer.31.38"); //$NON-NLS-1$
        paragraphs.add("Jer.32.6"); //$NON-NLS-1$
        paragraphs.add("Jer.32.13"); //$NON-NLS-1$
        paragraphs.add("Jer.32.16"); //$NON-NLS-1$
        paragraphs.add("Jer.32.26"); //$NON-NLS-1$
        paragraphs.add("Jer.32.36"); //$NON-NLS-1$
        paragraphs.add("Jer.33.9"); //$NON-NLS-1$
        paragraphs.add("Jer.33.15"); //$NON-NLS-1$
        paragraphs.add("Jer.33.17"); //$NON-NLS-1$
        paragraphs.add("Jer.33.19"); //$NON-NLS-1$
        paragraphs.add("Jer.34.8"); //$NON-NLS-1$
        paragraphs.add("Jer.34.12"); //$NON-NLS-1$
        paragraphs.add("Jer.35.12"); //$NON-NLS-1$
        paragraphs.add("Jer.35.18"); //$NON-NLS-1$
        paragraphs.add("Jer.36.11"); //$NON-NLS-1$
        paragraphs.add("Jer.36.20"); //$NON-NLS-1$
        paragraphs.add("Jer.36.27"); //$NON-NLS-1$
        paragraphs.add("Jer.36.32"); //$NON-NLS-1$
        paragraphs.add("Jer.37.6"); //$NON-NLS-1$
        paragraphs.add("Jer.37.11"); //$NON-NLS-1$
        paragraphs.add("Jer.37.16"); //$NON-NLS-1$
        paragraphs.add("Jer.38.7"); //$NON-NLS-1$
        paragraphs.add("Jer.38.14"); //$NON-NLS-1$
        paragraphs.add("Jer.38.24"); //$NON-NLS-1$
        paragraphs.add("Jer.39.4"); //$NON-NLS-1$
        paragraphs.add("Jer.39.8"); //$NON-NLS-1$
        paragraphs.add("Jer.39.11"); //$NON-NLS-1$
        paragraphs.add("Jer.39.15"); //$NON-NLS-1$
        paragraphs.add("Jer.40.7"); //$NON-NLS-1$
        paragraphs.add("Jer.40.13"); //$NON-NLS-1$
        paragraphs.add("Jer.41.11"); //$NON-NLS-1$
        paragraphs.add("Jer.42.7"); //$NON-NLS-1$
        paragraphs.add("Jer.42.13"); //$NON-NLS-1$
        paragraphs.add("Jer.42.19"); //$NON-NLS-1$
        paragraphs.add("Jer.43.8"); //$NON-NLS-1$
        paragraphs.add("Jer.44.11"); //$NON-NLS-1$
        paragraphs.add("Jer.44.15"); //$NON-NLS-1$
        paragraphs.add("Jer.44.20"); //$NON-NLS-1$
        paragraphs.add("Jer.44.29"); //$NON-NLS-1$
        paragraphs.add("Jer.45.4"); //$NON-NLS-1$
        paragraphs.add("Jer.46.13"); //$NON-NLS-1$
        paragraphs.add("Jer.46.27"); //$NON-NLS-1$
        paragraphs.add("Jer.48.7"); //$NON-NLS-1$
        paragraphs.add("Jer.48.11"); //$NON-NLS-1$
        paragraphs.add("Jer.48.14"); //$NON-NLS-1$
        paragraphs.add("Jer.48.26"); //$NON-NLS-1$
        paragraphs.add("Jer.48.47"); //$NON-NLS-1$
        paragraphs.add("Jer.49.7"); //$NON-NLS-1$
        paragraphs.add("Jer.49.23"); //$NON-NLS-1$
        paragraphs.add("Jer.49.28"); //$NON-NLS-1$
        paragraphs.add("Jer.49.30"); //$NON-NLS-1$
        paragraphs.add("Jer.49.34"); //$NON-NLS-1$
        paragraphs.add("Jer.49.39"); //$NON-NLS-1$
        paragraphs.add("Jer.50.4"); //$NON-NLS-1$
        paragraphs.add("Jer.50.9"); //$NON-NLS-1$
        paragraphs.add("Jer.50.17"); //$NON-NLS-1$
        paragraphs.add("Jer.50.21"); //$NON-NLS-1$
        paragraphs.add("Jer.50.33"); //$NON-NLS-1$
        paragraphs.add("Jer.50.35"); //$NON-NLS-1$
        paragraphs.add("Jer.51.59"); //$NON-NLS-1$
        paragraphs.add("Jer.52.4"); //$NON-NLS-1$
        paragraphs.add("Jer.52.8"); //$NON-NLS-1$
        paragraphs.add("Jer.52.12"); //$NON-NLS-1$
        paragraphs.add("Jer.52.24"); //$NON-NLS-1$
        paragraphs.add("Jer.52.31"); //$NON-NLS-1$
        paragraphs.add("Lam.1.12"); //$NON-NLS-1$
        paragraphs.add("Lam.1.18"); //$NON-NLS-1$
        paragraphs.add("Lam.2.20"); //$NON-NLS-1$
        paragraphs.add("Lam.3.22"); //$NON-NLS-1$
        paragraphs.add("Lam.3.37"); //$NON-NLS-1$
        paragraphs.add("Lam.3.55"); //$NON-NLS-1$
        paragraphs.add("Lam.3.64"); //$NON-NLS-1$
        paragraphs.add("Lam.4.13"); //$NON-NLS-1$
        paragraphs.add("Lam.4.21"); //$NON-NLS-1$
        paragraphs.add("Lam.4.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.1.4"); //$NON-NLS-1$
        paragraphs.add("Ezek.1.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.1.26"); //$NON-NLS-1$
        paragraphs.add("Ezek.2.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.2.9"); //$NON-NLS-1$
        paragraphs.add("Ezek.3.4"); //$NON-NLS-1$
        paragraphs.add("Ezek.3.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.3.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.4.9"); //$NON-NLS-1$
        paragraphs.add("Ezek.5.5"); //$NON-NLS-1$
        paragraphs.add("Ezek.5.12"); //$NON-NLS-1$
        paragraphs.add("Ezek.6.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.6.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.7.16"); //$NON-NLS-1$
        paragraphs.add("Ezek.7.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.7.23"); //$NON-NLS-1$
        paragraphs.add("Ezek.8.5"); //$NON-NLS-1$
        paragraphs.add("Ezek.8.7"); //$NON-NLS-1$
        paragraphs.add("Ezek.8.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.8.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.8.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.9.5"); //$NON-NLS-1$
        paragraphs.add("Ezek.9.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.10.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.11.4"); //$NON-NLS-1$
        paragraphs.add("Ezek.11.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.11.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.11.24"); //$NON-NLS-1$
        paragraphs.add("Ezek.12.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.12.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.12.21"); //$NON-NLS-1$
        paragraphs.add("Ezek.12.26"); //$NON-NLS-1$
        paragraphs.add("Ezek.13.10"); //$NON-NLS-1$
        paragraphs.add("Ezek.13.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.14.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.14.12"); //$NON-NLS-1$
        paragraphs.add("Ezek.14.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.14.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.14.19"); //$NON-NLS-1$
        paragraphs.add("Ezek.14.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.15.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.16.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.16.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.16.35"); //$NON-NLS-1$
        paragraphs.add("Ezek.16.44"); //$NON-NLS-1$
        paragraphs.add("Ezek.16.60"); //$NON-NLS-1$
        paragraphs.add("Ezek.17.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.17.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.5"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.10"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.14"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.19"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.24"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.25"); //$NON-NLS-1$
        paragraphs.add("Ezek.18.31"); //$NON-NLS-1$
        paragraphs.add("Ezek.19.10"); //$NON-NLS-1$
        paragraphs.add("Ezek.20.5"); //$NON-NLS-1$
        paragraphs.add("Ezek.20.10"); //$NON-NLS-1$
        paragraphs.add("Ezek.20.27"); //$NON-NLS-1$
        paragraphs.add("Ezek.20.33"); //$NON-NLS-1$
        paragraphs.add("Ezek.20.45"); //$NON-NLS-1$
        paragraphs.add("Ezek.21.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.21.18"); //$NON-NLS-1$
        paragraphs.add("Ezek.21.25"); //$NON-NLS-1$
        paragraphs.add("Ezek.21.28"); //$NON-NLS-1$
        paragraphs.add("Ezek.22.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.22.23"); //$NON-NLS-1$
        paragraphs.add("Ezek.23.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.23.36"); //$NON-NLS-1$
        paragraphs.add("Ezek.23.45"); //$NON-NLS-1$
        paragraphs.add("Ezek.24.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.24.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.24.19"); //$NON-NLS-1$
        paragraphs.add("Ezek.25.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.25.12"); //$NON-NLS-1$
        paragraphs.add("Ezek.25.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.26.7"); //$NON-NLS-1$
        paragraphs.add("Ezek.26.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.27.26"); //$NON-NLS-1$
        paragraphs.add("Ezek.28.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.28.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.28.24"); //$NON-NLS-1$
        paragraphs.add("Ezek.29.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.29.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.29.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.29.21"); //$NON-NLS-1$
        paragraphs.add("Ezek.30.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.31.3"); //$NON-NLS-1$
        paragraphs.add("Ezek.31.10"); //$NON-NLS-1$
        paragraphs.add("Ezek.31.18"); //$NON-NLS-1$
        paragraphs.add("Ezek.32.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.32.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.33.7"); //$NON-NLS-1$
        paragraphs.add("Ezek.33.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.33.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.33.21"); //$NON-NLS-1$
        paragraphs.add("Ezek.33.30"); //$NON-NLS-1$
        paragraphs.add("Ezek.34.7"); //$NON-NLS-1$
        paragraphs.add("Ezek.34.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.34.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.36.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.36.16"); //$NON-NLS-1$
        paragraphs.add("Ezek.36.21"); //$NON-NLS-1$
        paragraphs.add("Ezek.36.25"); //$NON-NLS-1$
        paragraphs.add("Ezek.37.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.37.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.37.18"); //$NON-NLS-1$
        paragraphs.add("Ezek.37.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.38.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.38.14"); //$NON-NLS-1$
        paragraphs.add("Ezek.39.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.39.11"); //$NON-NLS-1$
        paragraphs.add("Ezek.39.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.39.23"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.20"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.24"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.32"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.35"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.39"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.44"); //$NON-NLS-1$
        paragraphs.add("Ezek.40.48"); //$NON-NLS-1$
        paragraphs.add("Ezek.42.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.42.19"); //$NON-NLS-1$
        paragraphs.add("Ezek.43.7"); //$NON-NLS-1$
        paragraphs.add("Ezek.43.10"); //$NON-NLS-1$
        paragraphs.add("Ezek.43.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.43.18"); //$NON-NLS-1$
        paragraphs.add("Ezek.44.4"); //$NON-NLS-1$
        paragraphs.add("Ezek.44.9"); //$NON-NLS-1$
        paragraphs.add("Ezek.44.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.44.17"); //$NON-NLS-1$
        paragraphs.add("Ezek.45.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.45.7"); //$NON-NLS-1$
        paragraphs.add("Ezek.45.9"); //$NON-NLS-1$
        paragraphs.add("Ezek.46.9"); //$NON-NLS-1$
        paragraphs.add("Ezek.46.16"); //$NON-NLS-1$
        paragraphs.add("Ezek.46.19"); //$NON-NLS-1$
        paragraphs.add("Ezek.47.6"); //$NON-NLS-1$
        paragraphs.add("Ezek.47.13"); //$NON-NLS-1$
        paragraphs.add("Ezek.47.22"); //$NON-NLS-1$
        paragraphs.add("Ezek.48.8"); //$NON-NLS-1$
        paragraphs.add("Ezek.48.15"); //$NON-NLS-1$
        paragraphs.add("Ezek.48.21"); //$NON-NLS-1$
        paragraphs.add("Ezek.48.30"); //$NON-NLS-1$
        paragraphs.add("Dan.1.3"); //$NON-NLS-1$
        paragraphs.add("Dan.1.8"); //$NON-NLS-1$
        paragraphs.add("Dan.1.17"); //$NON-NLS-1$
        paragraphs.add("Dan.2.10"); //$NON-NLS-1$
        paragraphs.add("Dan.2.14"); //$NON-NLS-1$
        paragraphs.add("Dan.2.19"); //$NON-NLS-1$
        paragraphs.add("Dan.2.24"); //$NON-NLS-1$
        paragraphs.add("Dan.2.31"); //$NON-NLS-1$
        paragraphs.add("Dan.2.36"); //$NON-NLS-1$
        paragraphs.add("Dan.2.46"); //$NON-NLS-1$
        paragraphs.add("Dan.3.8"); //$NON-NLS-1$
        paragraphs.add("Dan.3.13"); //$NON-NLS-1$
        paragraphs.add("Dan.3.19"); //$NON-NLS-1$
        paragraphs.add("Dan.3.26"); //$NON-NLS-1$
        paragraphs.add("Dan.4.4"); //$NON-NLS-1$
        paragraphs.add("Dan.4.8"); //$NON-NLS-1$
        paragraphs.add("Dan.4.19"); //$NON-NLS-1$
        paragraphs.add("Dan.4.28"); //$NON-NLS-1$
        paragraphs.add("Dan.5.5"); //$NON-NLS-1$
        paragraphs.add("Dan.5.10"); //$NON-NLS-1$
        paragraphs.add("Dan.5.17"); //$NON-NLS-1$
        paragraphs.add("Dan.5.25"); //$NON-NLS-1$
        paragraphs.add("Dan.5.30"); //$NON-NLS-1$
        paragraphs.add("Dan.6.4"); //$NON-NLS-1$
        paragraphs.add("Dan.6.10"); //$NON-NLS-1$
        paragraphs.add("Dan.6.18"); //$NON-NLS-1$
        paragraphs.add("Dan.6.24"); //$NON-NLS-1$
        paragraphs.add("Dan.6.25"); //$NON-NLS-1$
        paragraphs.add("Dan.7.9"); //$NON-NLS-1$
        paragraphs.add("Dan.7.15"); //$NON-NLS-1$
        paragraphs.add("Dan.8.13"); //$NON-NLS-1$
        paragraphs.add("Dan.8.15"); //$NON-NLS-1$
        paragraphs.add("Dan.9.3"); //$NON-NLS-1$
        paragraphs.add("Dan.9.16"); //$NON-NLS-1$
        paragraphs.add("Dan.9.20"); //$NON-NLS-1$
        paragraphs.add("Dan.10.10"); //$NON-NLS-1$
        paragraphs.add("Dan.11.5"); //$NON-NLS-1$
        paragraphs.add("Dan.11.30"); //$NON-NLS-1$
        paragraphs.add("Dan.12.5"); //$NON-NLS-1$
        paragraphs.add("Hos.1.6"); //$NON-NLS-1$
        paragraphs.add("Hos.1.8"); //$NON-NLS-1$
        paragraphs.add("Hos.1.10"); //$NON-NLS-1$
        paragraphs.add("Hos.2.6"); //$NON-NLS-1$
        paragraphs.add("Hos.2.14"); //$NON-NLS-1$
        paragraphs.add("Hos.4.6"); //$NON-NLS-1$
        paragraphs.add("Hos.4.12"); //$NON-NLS-1$
        paragraphs.add("Hos.4.15"); //$NON-NLS-1$
        paragraphs.add("Hos.5.15"); //$NON-NLS-1$
        paragraphs.add("Hos.6.4"); //$NON-NLS-1$
        paragraphs.add("Hos.7.11"); //$NON-NLS-1$
        paragraphs.add("Hos.8.5"); //$NON-NLS-1$
        paragraphs.add("Hos.11.5"); //$NON-NLS-1$
        paragraphs.add("Hos.12.3"); //$NON-NLS-1$
        paragraphs.add("Hos.12.7"); //$NON-NLS-1$
        paragraphs.add("Hos.13.5"); //$NON-NLS-1$
        paragraphs.add("Hos.13.9"); //$NON-NLS-1$
        paragraphs.add("Hos.13.15"); //$NON-NLS-1$
        paragraphs.add("Hos.14.4"); //$NON-NLS-1$
        paragraphs.add("Joel.1.8"); //$NON-NLS-1$
        paragraphs.add("Joel.1.14"); //$NON-NLS-1$
        paragraphs.add("Joel.2.12"); //$NON-NLS-1$
        paragraphs.add("Joel.2.15"); //$NON-NLS-1$
        paragraphs.add("Joel.2.18"); //$NON-NLS-1$
        paragraphs.add("Joel.2.21"); //$NON-NLS-1$
        paragraphs.add("Joel.2.28"); //$NON-NLS-1$
        paragraphs.add("Joel.3.9"); //$NON-NLS-1$
        paragraphs.add("Joel.3.18"); //$NON-NLS-1$
        paragraphs.add("Amos.1.6"); //$NON-NLS-1$
        paragraphs.add("Amos.1.9"); //$NON-NLS-1$
        paragraphs.add("Amos.1.11"); //$NON-NLS-1$
        paragraphs.add("Amos.1.13"); //$NON-NLS-1$
        paragraphs.add("Amos.2.4"); //$NON-NLS-1$
        paragraphs.add("Amos.2.6"); //$NON-NLS-1$
        paragraphs.add("Amos.2.9"); //$NON-NLS-1$
        paragraphs.add("Amos.3.9"); //$NON-NLS-1$
        paragraphs.add("Amos.4.4"); //$NON-NLS-1$
        paragraphs.add("Amos.4.6"); //$NON-NLS-1$
        paragraphs.add("Amos.5.4"); //$NON-NLS-1$
        paragraphs.add("Amos.5.21"); //$NON-NLS-1$
        paragraphs.add("Amos.6.7"); //$NON-NLS-1$
        paragraphs.add("Amos.6.12"); //$NON-NLS-1$
        paragraphs.add("Amos.7.4"); //$NON-NLS-1$
        paragraphs.add("Amos.7.7"); //$NON-NLS-1$
        paragraphs.add("Amos.7.10"); //$NON-NLS-1$
        paragraphs.add("Amos.7.14"); //$NON-NLS-1$
        paragraphs.add("Amos.7.16"); //$NON-NLS-1$
        paragraphs.add("Amos.8.4"); //$NON-NLS-1$
        paragraphs.add("Amos.8.11"); //$NON-NLS-1$
        paragraphs.add("Amos.9.11"); //$NON-NLS-1$
        paragraphs.add("Obad.1.3"); //$NON-NLS-1$
        paragraphs.add("Obad.1.10"); //$NON-NLS-1$
        paragraphs.add("Obad.1.17"); //$NON-NLS-1$
        paragraphs.add("Jonah.1.4"); //$NON-NLS-1$
        paragraphs.add("Jonah.1.11"); //$NON-NLS-1$
        paragraphs.add("Jonah.1.17"); //$NON-NLS-1$
        paragraphs.add("Jonah.2.10"); //$NON-NLS-1$
        paragraphs.add("Jonah.3.5"); //$NON-NLS-1$
        paragraphs.add("Jonah.3.10"); //$NON-NLS-1$
        paragraphs.add("Jonah.4.4"); //$NON-NLS-1$
        paragraphs.add("Mic.1.10"); //$NON-NLS-1$
        paragraphs.add("Mic.2.4"); //$NON-NLS-1$
        paragraphs.add("Mic.2.7"); //$NON-NLS-1$
        paragraphs.add("Mic.2.12"); //$NON-NLS-1$
        paragraphs.add("Mic.3.5"); //$NON-NLS-1$
        paragraphs.add("Mic.3.8"); //$NON-NLS-1$
        paragraphs.add("Mic.4.3"); //$NON-NLS-1$
        paragraphs.add("Mic.4.8"); //$NON-NLS-1$
        paragraphs.add("Mic.4.11"); //$NON-NLS-1$
        paragraphs.add("Mic.5.4"); //$NON-NLS-1$
        paragraphs.add("Mic.5.8"); //$NON-NLS-1$
        paragraphs.add("Mic.6.6"); //$NON-NLS-1$
        paragraphs.add("Mic.6.10"); //$NON-NLS-1$
        paragraphs.add("Mic.6.16"); //$NON-NLS-1$
        paragraphs.add("Mic.7.3"); //$NON-NLS-1$
        paragraphs.add("Mic.7.5"); //$NON-NLS-1$
        paragraphs.add("Mic.7.8"); //$NON-NLS-1$
        paragraphs.add("Mic.7.14"); //$NON-NLS-1$
        paragraphs.add("Mic.7.16"); //$NON-NLS-1$
        paragraphs.add("Hab.1.5"); //$NON-NLS-1$
        paragraphs.add("Hab.1.12"); //$NON-NLS-1$
        paragraphs.add("Hab.2.5"); //$NON-NLS-1$
        paragraphs.add("Hab.2.9"); //$NON-NLS-1$
        paragraphs.add("Hab.2.12"); //$NON-NLS-1$
        paragraphs.add("Hab.2.15"); //$NON-NLS-1$
        paragraphs.add("Hab.2.18"); //$NON-NLS-1$
        paragraphs.add("Hab.3.17"); //$NON-NLS-1$
        paragraphs.add("Zeph.2.4"); //$NON-NLS-1$
        paragraphs.add("Zeph.2.8"); //$NON-NLS-1$
        paragraphs.add("Zeph.2.12"); //$NON-NLS-1$
        paragraphs.add("Zeph.3.8"); //$NON-NLS-1$
        paragraphs.add("Zeph.3.14"); //$NON-NLS-1$
        paragraphs.add("Hag.1.7"); //$NON-NLS-1$
        paragraphs.add("Hag.1.12"); //$NON-NLS-1$
        paragraphs.add("Hag.2.10"); //$NON-NLS-1$
        paragraphs.add("Hag.2.20"); //$NON-NLS-1$
        paragraphs.add("Zech.1.7"); //$NON-NLS-1$
        paragraphs.add("Zech.1.12"); //$NON-NLS-1$
        paragraphs.add("Zech.1.18"); //$NON-NLS-1$
        paragraphs.add("Zech.2.6"); //$NON-NLS-1$
        paragraphs.add("Zech.2.10"); //$NON-NLS-1$
        paragraphs.add("Zech.4.11"); //$NON-NLS-1$
        paragraphs.add("Zech.5.5"); //$NON-NLS-1$
        paragraphs.add("Zech.6.9"); //$NON-NLS-1$
        paragraphs.add("Zech.7.4"); //$NON-NLS-1$
        paragraphs.add("Zech.7.8"); //$NON-NLS-1$
        paragraphs.add("Zech.8.9"); //$NON-NLS-1$
        paragraphs.add("Zech.8.16"); //$NON-NLS-1$
        paragraphs.add("Zech.8.18"); //$NON-NLS-1$
        paragraphs.add("Zech.9.9"); //$NON-NLS-1$
        paragraphs.add("Zech.9.12"); //$NON-NLS-1$
        paragraphs.add("Zech.10.5"); //$NON-NLS-1$
        paragraphs.add("Zech.11.3"); //$NON-NLS-1$
        paragraphs.add("Zech.11.10"); //$NON-NLS-1$
        paragraphs.add("Zech.11.15"); //$NON-NLS-1$
        paragraphs.add("Zech.12.3"); //$NON-NLS-1$
        paragraphs.add("Zech.12.6"); //$NON-NLS-1$
        paragraphs.add("Zech.12.9"); //$NON-NLS-1$
        paragraphs.add("Zech.13.2"); //$NON-NLS-1$
        paragraphs.add("Zech.13.7"); //$NON-NLS-1$
        paragraphs.add("Zech.14.4"); //$NON-NLS-1$
        paragraphs.add("Zech.14.12"); //$NON-NLS-1$
        paragraphs.add("Zech.14.16"); //$NON-NLS-1$
        paragraphs.add("Zech.14.20"); //$NON-NLS-1$
        paragraphs.add("Mal.1.6"); //$NON-NLS-1$
        paragraphs.add("Mal.1.12"); //$NON-NLS-1$
        paragraphs.add("Mal.2.11"); //$NON-NLS-1$
        paragraphs.add("Mal.2.14"); //$NON-NLS-1$
        paragraphs.add("Mal.2.17"); //$NON-NLS-1$
        paragraphs.add("Mal.3.7"); //$NON-NLS-1$
        paragraphs.add("Mal.3.8"); //$NON-NLS-1$
        paragraphs.add("Mal.3.13"); //$NON-NLS-1$
        paragraphs.add("Mal.3.16"); //$NON-NLS-1$
        paragraphs.add("Mal.4.2"); //$NON-NLS-1$
        paragraphs.add("Mal.4.4"); //$NON-NLS-1$
        paragraphs.add("Mal.4.5"); //$NON-NLS-1$
        paragraphs.add("Matt.1.18"); //$NON-NLS-1$
        paragraphs.add("Matt.2.11"); //$NON-NLS-1$
        paragraphs.add("Matt.2.16"); //$NON-NLS-1$
        paragraphs.add("Matt.2.19"); //$NON-NLS-1$
        paragraphs.add("Matt.3.7"); //$NON-NLS-1$
        paragraphs.add("Matt.3.13"); //$NON-NLS-1$
        paragraphs.add("Matt.4.12"); //$NON-NLS-1$
        paragraphs.add("Matt.4.17"); //$NON-NLS-1$
        paragraphs.add("Matt.4.18"); //$NON-NLS-1$
        paragraphs.add("Matt.4.23"); //$NON-NLS-1$
        paragraphs.add("Matt.5.13"); //$NON-NLS-1$
        paragraphs.add("Matt.5.17"); //$NON-NLS-1$
        paragraphs.add("Matt.5.21"); //$NON-NLS-1$
        paragraphs.add("Matt.5.27"); //$NON-NLS-1$
        paragraphs.add("Matt.5.33"); //$NON-NLS-1$
        paragraphs.add("Matt.5.38"); //$NON-NLS-1$
        paragraphs.add("Matt.5.43"); //$NON-NLS-1$
        paragraphs.add("Matt.6.5"); //$NON-NLS-1$
        paragraphs.add("Matt.6.16"); //$NON-NLS-1$
        paragraphs.add("Matt.6.19"); //$NON-NLS-1$
        paragraphs.add("Matt.6.24"); //$NON-NLS-1$
        paragraphs.add("Matt.7.6"); //$NON-NLS-1$
        paragraphs.add("Matt.7.7"); //$NON-NLS-1$
        paragraphs.add("Matt.7.13"); //$NON-NLS-1$
        paragraphs.add("Matt.7.15"); //$NON-NLS-1$
        paragraphs.add("Matt.7.21"); //$NON-NLS-1$
        paragraphs.add("Matt.7.24"); //$NON-NLS-1$
        paragraphs.add("Matt.8.5"); //$NON-NLS-1$
        paragraphs.add("Matt.8.14"); //$NON-NLS-1$
        paragraphs.add("Matt.8.16"); //$NON-NLS-1$
        paragraphs.add("Matt.8.18"); //$NON-NLS-1$
        paragraphs.add("Matt.8.23"); //$NON-NLS-1$
        paragraphs.add("Matt.8.28"); //$NON-NLS-1$
        paragraphs.add("Matt.9.9"); //$NON-NLS-1$
        paragraphs.add("Matt.9.10"); //$NON-NLS-1$
        paragraphs.add("Matt.9.14"); //$NON-NLS-1$
        paragraphs.add("Matt.9.18"); //$NON-NLS-1$
        paragraphs.add("Matt.9.20"); //$NON-NLS-1$
        paragraphs.add("Matt.9.27"); //$NON-NLS-1$
        paragraphs.add("Matt.9.32"); //$NON-NLS-1$
        paragraphs.add("Matt.9.36"); //$NON-NLS-1$
        paragraphs.add("Matt.10.16"); //$NON-NLS-1$
        paragraphs.add("Matt.10.40"); //$NON-NLS-1$
        paragraphs.add("Matt.11.7"); //$NON-NLS-1$
        paragraphs.add("Matt.11.16"); //$NON-NLS-1$
        paragraphs.add("Matt.11.20"); //$NON-NLS-1$
        paragraphs.add("Matt.11.25"); //$NON-NLS-1$
        paragraphs.add("Matt.11.28"); //$NON-NLS-1$
        paragraphs.add("Matt.12.10"); //$NON-NLS-1$
        paragraphs.add("Matt.12.14"); //$NON-NLS-1$
        paragraphs.add("Matt.12.22"); //$NON-NLS-1$
        paragraphs.add("Matt.12.31"); //$NON-NLS-1$
        paragraphs.add("Matt.12.38"); //$NON-NLS-1$
        paragraphs.add("Matt.12.46"); //$NON-NLS-1$
        paragraphs.add("Matt.13.18"); //$NON-NLS-1$
        paragraphs.add("Matt.13.24"); //$NON-NLS-1$
        paragraphs.add("Matt.13.31"); //$NON-NLS-1$
        paragraphs.add("Matt.13.33"); //$NON-NLS-1$
        paragraphs.add("Matt.13.44"); //$NON-NLS-1$
        paragraphs.add("Matt.13.45"); //$NON-NLS-1$
        paragraphs.add("Matt.13.47"); //$NON-NLS-1$
        paragraphs.add("Matt.13.53"); //$NON-NLS-1$
        paragraphs.add("Matt.14.3"); //$NON-NLS-1$
        paragraphs.add("Matt.14.13"); //$NON-NLS-1$
        paragraphs.add("Matt.14.15"); //$NON-NLS-1$
        paragraphs.add("Matt.14.22"); //$NON-NLS-1$
        paragraphs.add("Matt.14.34"); //$NON-NLS-1$
        paragraphs.add("Matt.15.10"); //$NON-NLS-1$
        paragraphs.add("Matt.15.21"); //$NON-NLS-1$
        paragraphs.add("Matt.15.32"); //$NON-NLS-1$
        paragraphs.add("Matt.16.6"); //$NON-NLS-1$
        paragraphs.add("Matt.16.13"); //$NON-NLS-1$
        paragraphs.add("Matt.16.21"); //$NON-NLS-1$
        paragraphs.add("Matt.16.24"); //$NON-NLS-1$
        paragraphs.add("Matt.17.14"); //$NON-NLS-1$
        paragraphs.add("Matt.17.22"); //$NON-NLS-1$
        paragraphs.add("Matt.17.24"); //$NON-NLS-1$
        paragraphs.add("Matt.18.7"); //$NON-NLS-1$
        paragraphs.add("Matt.18.15"); //$NON-NLS-1$
        paragraphs.add("Matt.18.21"); //$NON-NLS-1$
        paragraphs.add("Matt.18.23"); //$NON-NLS-1$
        paragraphs.add("Matt.19.3"); //$NON-NLS-1$
        paragraphs.add("Matt.19.10"); //$NON-NLS-1$
        paragraphs.add("Matt.19.13"); //$NON-NLS-1$
        paragraphs.add("Matt.19.16"); //$NON-NLS-1$
        paragraphs.add("Matt.19.23"); //$NON-NLS-1$
        paragraphs.add("Matt.19.27"); //$NON-NLS-1$
        paragraphs.add("Matt.20.17"); //$NON-NLS-1$
        paragraphs.add("Matt.20.20"); //$NON-NLS-1$
        paragraphs.add("Matt.20.30"); //$NON-NLS-1$
        paragraphs.add("Matt.21.12"); //$NON-NLS-1$
        paragraphs.add("Matt.21.17"); //$NON-NLS-1$
        paragraphs.add("Matt.21.23"); //$NON-NLS-1$
        paragraphs.add("Matt.21.28"); //$NON-NLS-1$
        paragraphs.add("Matt.21.33"); //$NON-NLS-1$
        paragraphs.add("Matt.22.11"); //$NON-NLS-1$
        paragraphs.add("Matt.22.15"); //$NON-NLS-1$
        paragraphs.add("Matt.22.23"); //$NON-NLS-1$
        paragraphs.add("Matt.22.34"); //$NON-NLS-1$
        paragraphs.add("Matt.22.41"); //$NON-NLS-1$
        paragraphs.add("Matt.23.13"); //$NON-NLS-1$
        paragraphs.add("Matt.23.34"); //$NON-NLS-1$
        paragraphs.add("Matt.24.3"); //$NON-NLS-1$
        paragraphs.add("Matt.24.29"); //$NON-NLS-1$
        paragraphs.add("Matt.24.36"); //$NON-NLS-1$
        paragraphs.add("Matt.24.42"); //$NON-NLS-1$
        paragraphs.add("Matt.25.14"); //$NON-NLS-1$
        paragraphs.add("Matt.25.31"); //$NON-NLS-1$
        paragraphs.add("Matt.26.6"); //$NON-NLS-1$
        paragraphs.add("Matt.26.14"); //$NON-NLS-1$
        paragraphs.add("Matt.26.17"); //$NON-NLS-1$
        paragraphs.add("Matt.26.26"); //$NON-NLS-1$
        paragraphs.add("Matt.26.36"); //$NON-NLS-1$
        paragraphs.add("Matt.26.47"); //$NON-NLS-1$
        paragraphs.add("Matt.26.57"); //$NON-NLS-1$
        paragraphs.add("Matt.26.69"); //$NON-NLS-1$
        paragraphs.add("Matt.27.3"); //$NON-NLS-1$
        paragraphs.add("Matt.27.19"); //$NON-NLS-1$
        paragraphs.add("Matt.27.24"); //$NON-NLS-1$
        paragraphs.add("Matt.27.26"); //$NON-NLS-1$
        paragraphs.add("Matt.27.29"); //$NON-NLS-1$
        paragraphs.add("Matt.27.34"); //$NON-NLS-1$
        paragraphs.add("Matt.27.39"); //$NON-NLS-1$
        paragraphs.add("Matt.27.50"); //$NON-NLS-1$
        paragraphs.add("Matt.27.62"); //$NON-NLS-1$
        paragraphs.add("Matt.28.9"); //$NON-NLS-1$
        paragraphs.add("Matt.28.11"); //$NON-NLS-1$
        paragraphs.add("Matt.28.16"); //$NON-NLS-1$
        paragraphs.add("Matt.28.19"); //$NON-NLS-1$
        paragraphs.add("Mark.3.22"); //$NON-NLS-1$
        paragraphs.add("Mark.3.31"); //$NON-NLS-1$
        paragraphs.add("Mark.4.14"); //$NON-NLS-1$
        paragraphs.add("Mark.4.21"); //$NON-NLS-1$
        paragraphs.add("Mark.4.26"); //$NON-NLS-1$
        paragraphs.add("Mark.4.30"); //$NON-NLS-1$
        paragraphs.add("Mark.6.7"); //$NON-NLS-1$
        paragraphs.add("Mark.7.14"); //$NON-NLS-1$
        paragraphs.add("Mark.7.24"); //$NON-NLS-1$
        paragraphs.add("Mark.7.31"); //$NON-NLS-1$
        paragraphs.add("Mark.8.10"); //$NON-NLS-1$
        paragraphs.add("Mark.8.14"); //$NON-NLS-1$
        paragraphs.add("Mark.8.22"); //$NON-NLS-1$
        paragraphs.add("Mark.8.27"); //$NON-NLS-1$
        paragraphs.add("Mark.8.34"); //$NON-NLS-1$
        paragraphs.add("Mark.9.2"); //$NON-NLS-1$
        paragraphs.add("Mark.9.11"); //$NON-NLS-1$
        paragraphs.add("Mark.9.14"); //$NON-NLS-1$
        paragraphs.add("Mark.9.30"); //$NON-NLS-1$
        paragraphs.add("Mark.9.33"); //$NON-NLS-1$
        paragraphs.add("Mark.9.38"); //$NON-NLS-1$
        paragraphs.add("Mark.10.2"); //$NON-NLS-1$
        paragraphs.add("Mark.10.13"); //$NON-NLS-1$
        paragraphs.add("Mark.10.17"); //$NON-NLS-1$
        paragraphs.add("Mark.10.23"); //$NON-NLS-1$
        paragraphs.add("Mark.10.28"); //$NON-NLS-1$
        paragraphs.add("Mark.10.32"); //$NON-NLS-1$
        paragraphs.add("Mark.10.35"); //$NON-NLS-1$
        paragraphs.add("Mark.10.46"); //$NON-NLS-1$
        paragraphs.add("Mark.11.12"); //$NON-NLS-1$
        paragraphs.add("Mark.11.15"); //$NON-NLS-1$
        paragraphs.add("Mark.11.20"); //$NON-NLS-1$
        paragraphs.add("Mark.11.27"); //$NON-NLS-1$
        paragraphs.add("Mark.12.13"); //$NON-NLS-1$
        paragraphs.add("Mark.12.18"); //$NON-NLS-1$
        paragraphs.add("Mark.12.28"); //$NON-NLS-1$
        paragraphs.add("Mark.12.35"); //$NON-NLS-1$
        paragraphs.add("Mark.12.38"); //$NON-NLS-1$
        paragraphs.add("Mark.12.41"); //$NON-NLS-1$
        paragraphs.add("Mark.13.9"); //$NON-NLS-1$
        paragraphs.add("Mark.13.14"); //$NON-NLS-1$
        paragraphs.add("Mark.13.24"); //$NON-NLS-1$
        paragraphs.add("Mark.13.32"); //$NON-NLS-1$
        paragraphs.add("Mark.14.3"); //$NON-NLS-1$
        paragraphs.add("Mark.14.10"); //$NON-NLS-1$
        paragraphs.add("Mark.14.12"); //$NON-NLS-1$
        paragraphs.add("Mark.14.22"); //$NON-NLS-1$
        paragraphs.add("Mark.14.26"); //$NON-NLS-1$
        paragraphs.add("Mark.14.43"); //$NON-NLS-1$
        paragraphs.add("Mark.14.46"); //$NON-NLS-1$
        paragraphs.add("Mark.14.53"); //$NON-NLS-1$
        paragraphs.add("Mark.14.66"); //$NON-NLS-1$
        paragraphs.add("Mark.15.15"); //$NON-NLS-1$
        paragraphs.add("Mark.15.39"); //$NON-NLS-1$
        paragraphs.add("Mark.15.42"); //$NON-NLS-1$
        paragraphs.add("Mark.16.9"); //$NON-NLS-1$
        paragraphs.add("Mark.16.12"); //$NON-NLS-1$
        paragraphs.add("Mark.16.14"); //$NON-NLS-1$
        paragraphs.add("Mark.16.19"); //$NON-NLS-1$
        paragraphs.add("Luke.1.5"); //$NON-NLS-1$
        paragraphs.add("Luke.4.14"); //$NON-NLS-1$
        paragraphs.add("Luke.4.16"); //$NON-NLS-1$
        paragraphs.add("Luke.4.33"); //$NON-NLS-1$
        paragraphs.add("Luke.4.38"); //$NON-NLS-1$
        paragraphs.add("Luke.4.40"); //$NON-NLS-1$
        paragraphs.add("Luke.5.12"); //$NON-NLS-1$
        paragraphs.add("Luke.5.16"); //$NON-NLS-1$
        paragraphs.add("Luke.5.18"); //$NON-NLS-1$
        paragraphs.add("Luke.5.27"); //$NON-NLS-1$
        paragraphs.add("Luke.5.33"); //$NON-NLS-1$
        paragraphs.add("Luke.5.36"); //$NON-NLS-1$
        paragraphs.add("Luke.6.13"); //$NON-NLS-1$
        paragraphs.add("Luke.6.17"); //$NON-NLS-1$
        paragraphs.add("Luke.6.20"); //$NON-NLS-1$
        paragraphs.add("Luke.6.27"); //$NON-NLS-1$
        paragraphs.add("Luke.6.46"); //$NON-NLS-1$
        paragraphs.add("Luke.7.11"); //$NON-NLS-1$
        paragraphs.add("Luke.7.19"); //$NON-NLS-1$
        paragraphs.add("Luke.7.24"); //$NON-NLS-1$
        paragraphs.add("Luke.7.31"); //$NON-NLS-1$
        paragraphs.add("Luke.7.36"); //$NON-NLS-1$
        paragraphs.add("Luke.8.4"); //$NON-NLS-1$
        paragraphs.add("Luke.8.16"); //$NON-NLS-1$
        paragraphs.add("Luke.8.19"); //$NON-NLS-1$
        paragraphs.add("Luke.8.22"); //$NON-NLS-1$
        paragraphs.add("Luke.8.26"); //$NON-NLS-1$
        paragraphs.add("Luke.8.37"); //$NON-NLS-1$
        paragraphs.add("Luke.8.41"); //$NON-NLS-1$
        paragraphs.add("Luke.8.43"); //$NON-NLS-1$
        paragraphs.add("Luke.8.49"); //$NON-NLS-1$
        paragraphs.add("Luke.9.7"); //$NON-NLS-1$
        paragraphs.add("Luke.9.10"); //$NON-NLS-1$
        paragraphs.add("Luke.9.18"); //$NON-NLS-1$
        paragraphs.add("Luke.9.23"); //$NON-NLS-1$
        paragraphs.add("Luke.9.28"); //$NON-NLS-1$
        paragraphs.add("Luke.9.37"); //$NON-NLS-1$
        paragraphs.add("Luke.9.43"); //$NON-NLS-1$
        paragraphs.add("Luke.9.46"); //$NON-NLS-1$
        paragraphs.add("Luke.9.49"); //$NON-NLS-1$
        paragraphs.add("Luke.9.51"); //$NON-NLS-1$
        paragraphs.add("Luke.9.57"); //$NON-NLS-1$
        paragraphs.add("Luke.10.17"); //$NON-NLS-1$
        paragraphs.add("Luke.10.21"); //$NON-NLS-1$
        paragraphs.add("Luke.10.23"); //$NON-NLS-1$
        paragraphs.add("Luke.10.25"); //$NON-NLS-1$
        paragraphs.add("Luke.10.38"); //$NON-NLS-1$
        paragraphs.add("Luke.11.14"); //$NON-NLS-1$
        paragraphs.add("Luke.11.27"); //$NON-NLS-1$
        paragraphs.add("Luke.11.29"); //$NON-NLS-1$
        paragraphs.add("Luke.11.37"); //$NON-NLS-1$
        paragraphs.add("Luke.11.45"); //$NON-NLS-1$
        paragraphs.add("Luke.12.13"); //$NON-NLS-1$
        paragraphs.add("Luke.12.22"); //$NON-NLS-1$
        paragraphs.add("Luke.12.31"); //$NON-NLS-1$
        paragraphs.add("Luke.12.41"); //$NON-NLS-1$
        paragraphs.add("Luke.12.49"); //$NON-NLS-1$
        paragraphs.add("Luke.12.54"); //$NON-NLS-1$
        paragraphs.add("Luke.12.58"); //$NON-NLS-1$
        paragraphs.add("Luke.13.6"); //$NON-NLS-1$
        paragraphs.add("Luke.13.11"); //$NON-NLS-1$
        paragraphs.add("Luke.13.18"); //$NON-NLS-1$
        paragraphs.add("Luke.13.24"); //$NON-NLS-1$
        paragraphs.add("Luke.13.31"); //$NON-NLS-1$
        paragraphs.add("Luke.14.7"); //$NON-NLS-1$
        paragraphs.add("Luke.14.12"); //$NON-NLS-1$
        paragraphs.add("Luke.14.15"); //$NON-NLS-1$
        paragraphs.add("Luke.14.25"); //$NON-NLS-1$
        paragraphs.add("Luke.14.34"); //$NON-NLS-1$
        paragraphs.add("Luke.15.3"); //$NON-NLS-1$
        paragraphs.add("Luke.15.8"); //$NON-NLS-1$
        paragraphs.add("Luke.15.11"); //$NON-NLS-1$
        paragraphs.add("Luke.16.13"); //$NON-NLS-1$
        paragraphs.add("Luke.16.19"); //$NON-NLS-1$
        paragraphs.add("Luke.17.3"); //$NON-NLS-1$
        paragraphs.add("Luke.17.11"); //$NON-NLS-1$
        paragraphs.add("Luke.17.20"); //$NON-NLS-1$
        paragraphs.add("Luke.18.31"); //$NON-NLS-1$
        paragraphs.add("Luke.18.35"); //$NON-NLS-1$
        paragraphs.add("Luke.19.28"); //$NON-NLS-1$
        paragraphs.add("Luke.19.41"); //$NON-NLS-1$
        paragraphs.add("Luke.20.19"); //$NON-NLS-1$
        paragraphs.add("Luke.20.27"); //$NON-NLS-1$
        paragraphs.add("Luke.20.39"); //$NON-NLS-1$
        paragraphs.add("Luke.20.45"); //$NON-NLS-1$
        paragraphs.add("Luke.21.5"); //$NON-NLS-1$
        paragraphs.add("Luke.21.25"); //$NON-NLS-1$
        paragraphs.add("Luke.21.34"); //$NON-NLS-1$
        paragraphs.add("Luke.22.3"); //$NON-NLS-1$
        paragraphs.add("Luke.22.7"); //$NON-NLS-1$
        paragraphs.add("Luke.22.19"); //$NON-NLS-1$
        paragraphs.add("Luke.22.21"); //$NON-NLS-1$
        paragraphs.add("Luke.22.24"); //$NON-NLS-1$
        paragraphs.add("Luke.22.31"); //$NON-NLS-1$
        paragraphs.add("Luke.22.39"); //$NON-NLS-1$
        paragraphs.add("Luke.22.47"); //$NON-NLS-1$
        paragraphs.add("Luke.22.50"); //$NON-NLS-1$
        paragraphs.add("Luke.22.54"); //$NON-NLS-1$
        paragraphs.add("Luke.22.63"); //$NON-NLS-1$
        paragraphs.add("Luke.22.66"); //$NON-NLS-1$
        paragraphs.add("Luke.23.8"); //$NON-NLS-1$
        paragraphs.add("Luke.23.12"); //$NON-NLS-1$
        paragraphs.add("Luke.23.13"); //$NON-NLS-1$
        paragraphs.add("Luke.23.27"); //$NON-NLS-1$
        paragraphs.add("Luke.23.34"); //$NON-NLS-1$
        paragraphs.add("Luke.23.39"); //$NON-NLS-1$
        paragraphs.add("Luke.23.46"); //$NON-NLS-1$
        paragraphs.add("Luke.23.50"); //$NON-NLS-1$
        paragraphs.add("Luke.24.13"); //$NON-NLS-1$
        paragraphs.add("Luke.24.36"); //$NON-NLS-1$
        paragraphs.add("Luke.24.49"); //$NON-NLS-1$
        paragraphs.add("Luke.24.50"); //$NON-NLS-1$
        paragraphs.add("John.1.6"); //$NON-NLS-1$
        paragraphs.add("John.1.15"); //$NON-NLS-1$
        paragraphs.add("John.1.19"); //$NON-NLS-1$
        paragraphs.add("John.1.29"); //$NON-NLS-1$
        paragraphs.add("John.1.35"); //$NON-NLS-1$
        paragraphs.add("John.1.43"); //$NON-NLS-1$
        paragraphs.add("John.2.12"); //$NON-NLS-1$
        paragraphs.add("John.2.13"); //$NON-NLS-1$
        paragraphs.add("John.2.18"); //$NON-NLS-1$
        paragraphs.add("John.2.23"); //$NON-NLS-1$
        paragraphs.add("John.3.14"); //$NON-NLS-1$
        paragraphs.add("John.3.16"); //$NON-NLS-1$
        paragraphs.add("John.3.18"); //$NON-NLS-1$
        paragraphs.add("John.3.22"); //$NON-NLS-1$
        paragraphs.add("John.3.23"); //$NON-NLS-1$
        paragraphs.add("John.3.25"); //$NON-NLS-1$
        paragraphs.add("John.4.27"); //$NON-NLS-1$
        paragraphs.add("John.4.31"); //$NON-NLS-1$
        paragraphs.add("John.4.39"); //$NON-NLS-1$
        paragraphs.add("John.4.43"); //$NON-NLS-1$
        paragraphs.add("John.5.10"); //$NON-NLS-1$
        paragraphs.add("John.5.17"); //$NON-NLS-1$
        paragraphs.add("John.5.32"); //$NON-NLS-1$
        paragraphs.add("John.5.36"); //$NON-NLS-1$
        paragraphs.add("John.5.39"); //$NON-NLS-1$
        paragraphs.add("John.6.5"); //$NON-NLS-1$
        paragraphs.add("John.6.15"); //$NON-NLS-1$
        paragraphs.add("John.6.22"); //$NON-NLS-1$
        paragraphs.add("John.6.66"); //$NON-NLS-1$
        paragraphs.add("John.7.10"); //$NON-NLS-1$
        paragraphs.add("John.7.14"); //$NON-NLS-1$
        paragraphs.add("John.7.32"); //$NON-NLS-1$
        paragraphs.add("John.7.40"); //$NON-NLS-1$
        paragraphs.add("John.7.45"); //$NON-NLS-1$
        paragraphs.add("John.8.12"); //$NON-NLS-1$
        paragraphs.add("John.8.33"); //$NON-NLS-1$
        paragraphs.add("John.9.8"); //$NON-NLS-1$
        paragraphs.add("John.9.13"); //$NON-NLS-1$
        paragraphs.add("John.9.39"); //$NON-NLS-1$
        paragraphs.add("John.10.19"); //$NON-NLS-1$
        paragraphs.add("John.10.22"); //$NON-NLS-1$
        paragraphs.add("John.11.47"); //$NON-NLS-1$
        paragraphs.add("John.11.55"); //$NON-NLS-1$
        paragraphs.add("John.12.10"); //$NON-NLS-1$
        paragraphs.add("John.12.12"); //$NON-NLS-1$
        paragraphs.add("John.12.20"); //$NON-NLS-1$
        paragraphs.add("John.12.23"); //$NON-NLS-1$
        paragraphs.add("John.12.37"); //$NON-NLS-1$
        paragraphs.add("John.12.42"); //$NON-NLS-1$
        paragraphs.add("John.12.44"); //$NON-NLS-1$
        paragraphs.add("John.13.18"); //$NON-NLS-1$
        paragraphs.add("John.13.31"); //$NON-NLS-1$
        paragraphs.add("John.13.36"); //$NON-NLS-1$
        paragraphs.add("John.14.15"); //$NON-NLS-1$
        paragraphs.add("John.18.15"); //$NON-NLS-1$
        paragraphs.add("John.18.19"); //$NON-NLS-1$
        paragraphs.add("John.18.28"); //$NON-NLS-1$
        paragraphs.add("John.19.8"); //$NON-NLS-1$
        paragraphs.add("John.19.13"); //$NON-NLS-1$
        paragraphs.add("John.19.19"); //$NON-NLS-1$
        paragraphs.add("John.19.23"); //$NON-NLS-1$
        paragraphs.add("John.19.25"); //$NON-NLS-1$
        paragraphs.add("John.19.28"); //$NON-NLS-1$
        paragraphs.add("John.19.38"); //$NON-NLS-1$
        paragraphs.add("John.20.11"); //$NON-NLS-1$
        paragraphs.add("John.20.19"); //$NON-NLS-1$
        paragraphs.add("John.20.24"); //$NON-NLS-1$
        paragraphs.add("John.20.26"); //$NON-NLS-1$
        paragraphs.add("John.20.30"); //$NON-NLS-1$
        paragraphs.add("John.21.15"); //$NON-NLS-1$
        paragraphs.add("Acts.1.15"); //$NON-NLS-1$
        paragraphs.add("Acts.2.14"); //$NON-NLS-1$
        paragraphs.add("Acts.2.37"); //$NON-NLS-1$
        paragraphs.add("Acts.2.41"); //$NON-NLS-1$
        paragraphs.add("Acts.3.12"); //$NON-NLS-1$
        paragraphs.add("Acts.3.19"); //$NON-NLS-1$
        paragraphs.add("Acts.4.5"); //$NON-NLS-1$
        paragraphs.add("Acts.4.13"); //$NON-NLS-1$
        paragraphs.add("Acts.4.23"); //$NON-NLS-1$
        paragraphs.add("Acts.4.31"); //$NON-NLS-1$
        paragraphs.add("Acts.5.12"); //$NON-NLS-1$
        paragraphs.add("Acts.5.17"); //$NON-NLS-1$
        paragraphs.add("Acts.5.29"); //$NON-NLS-1$
        paragraphs.add("Acts.5.33"); //$NON-NLS-1$
        paragraphs.add("Acts.5.41"); //$NON-NLS-1$
        paragraphs.add("Acts.6.5"); //$NON-NLS-1$
        paragraphs.add("Acts.6.9"); //$NON-NLS-1$
        paragraphs.add("Acts.7.37"); //$NON-NLS-1$
        paragraphs.add("Acts.7.51"); //$NON-NLS-1$
        paragraphs.add("Acts.7.54"); //$NON-NLS-1$
        paragraphs.add("Acts.9.10"); //$NON-NLS-1$
        paragraphs.add("Acts.9.23"); //$NON-NLS-1$
        paragraphs.add("Acts.9.32"); //$NON-NLS-1$
        paragraphs.add("Acts.9.36"); //$NON-NLS-1$
        paragraphs.add("Acts.10.9"); //$NON-NLS-1$
        paragraphs.add("Acts.10.19"); //$NON-NLS-1$
        paragraphs.add("Acts.10.34"); //$NON-NLS-1$
        paragraphs.add("Acts.10.44"); //$NON-NLS-1$
        paragraphs.add("Acts.11.19"); //$NON-NLS-1$
        paragraphs.add("Acts.11.22"); //$NON-NLS-1$
        paragraphs.add("Acts.11.27"); //$NON-NLS-1$
        paragraphs.add("Acts.12.20"); //$NON-NLS-1$
        paragraphs.add("Acts.12.24"); //$NON-NLS-1$
        paragraphs.add("Acts.13.4"); //$NON-NLS-1$
        paragraphs.add("Acts.13.14"); //$NON-NLS-1$
        paragraphs.add("Acts.13.38"); //$NON-NLS-1$
        paragraphs.add("Acts.13.44"); //$NON-NLS-1$
        paragraphs.add("Acts.14.8"); //$NON-NLS-1$
        paragraphs.add("Acts.14.19"); //$NON-NLS-1$
        paragraphs.add("Acts.15.6"); //$NON-NLS-1$
        paragraphs.add("Acts.15.12"); //$NON-NLS-1$
        paragraphs.add("Acts.15.13"); //$NON-NLS-1$
        paragraphs.add("Acts.15.36"); //$NON-NLS-1$
        paragraphs.add("Acts.16.14"); //$NON-NLS-1$
        paragraphs.add("Acts.16.16"); //$NON-NLS-1$
        paragraphs.add("Acts.16.19"); //$NON-NLS-1$
        paragraphs.add("Acts.16.25"); //$NON-NLS-1$
        paragraphs.add("Acts.17.5"); //$NON-NLS-1$
        paragraphs.add("Acts.17.10"); //$NON-NLS-1$
        paragraphs.add("Acts.17.16"); //$NON-NLS-1$
        paragraphs.add("Acts.17.22"); //$NON-NLS-1$
        paragraphs.add("Acts.17.32"); //$NON-NLS-1$
        paragraphs.add("Acts.18.7"); //$NON-NLS-1$
        paragraphs.add("Acts.18.12"); //$NON-NLS-1$
        paragraphs.add("Acts.18.18"); //$NON-NLS-1$
        paragraphs.add("Acts.18.24"); //$NON-NLS-1$
        paragraphs.add("Acts.19.13"); //$NON-NLS-1$
        paragraphs.add("Acts.19.21"); //$NON-NLS-1$
        paragraphs.add("Acts.20.13"); //$NON-NLS-1$
        paragraphs.add("Acts.20.17"); //$NON-NLS-1$
        paragraphs.add("Acts.20.28"); //$NON-NLS-1$
        paragraphs.add("Acts.20.36"); //$NON-NLS-1$
    }

    private Writer writer;
    private String filename;
}
