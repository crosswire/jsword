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
            for (Key key : keys)
            {
                Verse verse = (Verse) key;
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
                    raw = raw.replace("<w lemma=\"x-Strongs:H07892\">A Song</w> <w lemma=\"x-Strongs:H04609\">of degrees</w>. ", //$NON-NLS-1$
                                      "<title type=\"psalm\"><w lemma=\"x-Strongs:H07892\">A Song</w> <w lemma=\"x-Strongs:H04609\">of degrees</w>.</title>"); //$NON-NLS-1$
                }

                boolean foundPreVerse = false;
                String preVerseText = ""; //$NON-NLS-1$
                if (raw.contains(preVerseStart))
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
                if (raw.contains(psalmTitleStart))
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
                    buildPsalmTitle(buf, cleanup(osisID, psalmTitleText, false)); //$NON-NLS-1$
                }

                if (foundPreVerse && !preVerseText.equals(psalmTitleText))
                {
                    if (inPreVerse)
                    {
                        buildPreVerseClose(buf);
                    }
                    buildPreVerseOpen(buf, cleanup(osisID, preVerseText, false)); //$NON-NLS-1$
                    inPreVerse = true;
                }

                // There is a bug in the KJV where NT book titles are at the end of the prior book
                // And they contain junk!
                if (SwordConstants.getTestament(v) == SwordConstants.TESTAMENT_NEW)
                {
                    if (raw.contains("<title")) //$NON-NLS-1$
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
        MessageFormat msgFormat = new MessageFormat(docBuffer.toString()); //$NON-NLS-1$
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
        String title = bookTitles.get(bookName);
        if (title != null)
        {
            titleFormat.format(new Object[] { title }, buf, pos);
        }
    }

    private void buildBookClose(StringBuffer buf, String bookName)
    {
        String colophon = colophons.get(bookName);
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
        parser.getFeatures().setFeatureStates("-s", "-f", "-va", "-dv"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        parser.parse(filename + ".xml"); //$NON-NLS-1$
    }

    private String cleanup(String osisID, String input, boolean inVerse)
    {
        String acrostic = acrostics.get(osisID);
        if (acrostic != null)
        {
            MessageFormat msgFormat = new MessageFormat("<title type=\"acrostic\" canonical=\"true\">{0}</title>"); //$NON-NLS-1$
            input = input.replaceFirst(acrostic, ""); //$NON-NLS-1$ //$NON-NLS-2$
            input = msgFormat.format(new Object[] { acrostic }) + input;
        }

        // Fix up bad notes
        if (input.contains("note type=\"strongsMarkup\"")) //$NON-NLS-1$
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

        if (input.contains("<resp")) //$NON-NLS-1$
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

        if (osisID.equals("1Thess.5.28")) //$NON-NLS-1$
        {
            input += "<w src=\"11\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w><w src=\"12\" lemma=\"strong:G2331\" morph=\"robinson:N-APM\"></w>"; //$NON-NLS-1$
        }
 
        Set<Integer> before = new TreeSet<Integer>();

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
                input = input.replace(whole, fixed); //$NON-NLS-1$
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
        for (Integer number : before)
        {
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

//        if (input.contains(transSegStart))
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

        // normalize paragraph markers and move them from the end of a verse to the beginning of the next
        input = input.replaceAll("<milestone type=\"x-p\"\\s*/>", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("<p/>", "<milestone type=\"x-p\" marker=\"\u00B6\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("<milestone type=\"x-p\" marker=\"\u00B6\"/>")) //$NON-NLS-1$
        {
            input = input.replaceAll("<milestone type=\"x-p\" marker=\"\u00B6\"/>", ""); //$NON-NLS-1$ //$NON-NLS-2$
            moveP = true;
//            System.err.println(osisID + " remove \u00b6"); //$NON-NLS-1$
        }
        else if (moveP && inVerse)
        {
            input = "<milestone type=\"x-p\" marker=\"\u00B6\"/>" + input; //$NON-NLS-1$
            moveP = false;
        }

        // # is used in a note for a greek strong's #
        input = input.replace('#', 'G');
        // used in a note as a quotation mark at the beginning of a word. i.e. `not'
        input = input.replace('`', '\'');
        // used in notes as a space
        input = input.replace('_', ' ');
        // used in notes to indicate italics. These are incomplete GBF codes.
        input = input.replaceAll("[{][Ff][iI][}]", ""); //$NON-NLS-1$ //$NON-NLS-2$
        // found an email address in a note
        input = input.replace("@hotmail.", "at hotmail dot "); //$NON-NLS-1$ //$NON-NLS-2$

        if (osisID.equals("Exod.32.32")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"strongMorph:TH8798\" lemma=\"strong:H04229\">--; ", //$NON-NLS-1$
                                  "\u2015; <w morph=\"strongMorph:TH8798\" lemma=\"strong:H04229\">"); //$NON-NLS-1$
        }

        if (osisID.equals("Ezek.26.16")) //$NON-NLS-1$
        {
            input = input.replace("\\pa", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.5.30")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G846\" morph=\"robinson:P-ASF\">if</w>", //$NON-NLS-1$
                                  "<w src=\"10\" lemma=\"strong:G846\" morph=\"robinson:P-ASF\">it</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.16.17")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G920\" morph=\"robinson:ARAM\">Bar</w><w src=\"11\" lemma=\"strong:G920\" morph=\"robinson:ARAM\">jona</w>", //$NON-NLS-1$
                                  "<w src=\"10 11\" lemma=\"strong:G920\" morph=\"robinson:ARAM\">Bar\u2013jona</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.24.38")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"18\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\"><w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-DPF\">that</w></w>", //$NON-NLS-1$
                                  "<w src=\"18\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\"></w><w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-DPF\">that</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.18.28")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"19\" lemma=\"strong:G4155\" morph=\"robinson:V-IAI-3S\">and took <transChange type=\"added\">him</transChange> by the throat</w>", //$NON-NLS-1$
                                  "<w src=\"19\" lemma=\"strong:G4155\" morph=\"robinson:V-IAI-3S\">and took <seg type=\"x-transChange\" subType=\"x-added\">him</seg> by the throat</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.21.28")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"5\" lemma=\"strong:G444\" morph=\"robinson:N-NSM\">A <transChange type=\"added\">certain</transChange> man</w>", //$NON-NLS-1$
                                  "<w src=\"5\" lemma=\"strong:G444\" morph=\"robinson:N-NSM\">A <seg type=\"x-transChange\" subType=\"x-added\">certain</seg> man</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.21.31")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"9\" lemma=\"strong:G3962\" morph=\"robinson:N-GSM\">of <transChange type=\"added\">his</transChange> father</w>", //$NON-NLS-1$
                                  "<w src=\"9\" lemma=\"strong:G3962\" morph=\"robinson:N-GSM\">of <seg type=\"x-transChange\" subType=\"x-added\">his</seg> father</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.22.6")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G5195\" morph=\"robinson:V-AAI-3P\">and entreated <transChange type=\"added\">them</transChange> spitefully</w>", //$NON-NLS-1$
                                  "<w src=\"8\" lemma=\"strong:G5195\" morph=\"robinson:V-AAI-3P\">and entreated <seg type=\"x-transChange\" subType=\"x-added\">them</seg> spitefully</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.23.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"19\" lemma=\"strong:G2309\" morph=\"robinson:V-PAI-3P\">they <transChange type=\"added\">themselves</transChange> will</w>", //$NON-NLS-1$
                                  "<w src=\"19\" lemma=\"strong:G2309\" morph=\"robinson:V-PAI-3P\">they <seg type=\"x-transChange\" subType=\"x-added\">themselves</seg> will</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.25.37")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"17\" lemma=\"strong:G4222\" morph=\"robinson:V-AAI-1P\">gave <transChange type=\"added\">thee</transChange> drink</w>", //$NON-NLS-1$
                                  "<w src=\"17\" lemma=\"strong:G4222\" morph=\"robinson:V-AAI-1P\">gave <seg type=\"x-transChange\" subType=\"x-added\">thee</seg> drink</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.25.38")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"7\" lemma=\"strong:G4863\" morph=\"robinson:V-2AAI-1P\">took <transChange type=\"added\">thee</transChange> in</w>", //$NON-NLS-1$
                                  "<w src=\"7\" lemma=\"strong:G4863\" morph=\"robinson:V-2AAI-1P\">took <seg type=\"x-transChange\" subType=\"x-added\">thee</seg> in</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.17")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"5\" lemma=\"strong:G106\" morph=\"robinson:A-GPN\">of the <transChange type=\"added\">feast of</transChange> unleavened bread</w>", //$NON-NLS-1$
                                  "<w src=\"5\" lemma=\"strong:G106\" morph=\"robinson:A-GPN\">of the <seg type=\"x-transChange\" subType=\"x-added\">feast of</seg> unleavened bread</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.45")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"15\" lemma=\"strong:G373\" morph=\"robinson:V-PMI-2P\">take <transChange type=\"added\">your</transChange> rest</w>", //$NON-NLS-1$
                                  "<w src=\"15\" lemma=\"strong:G373\" morph=\"robinson:V-PMI-2P\">take <seg type=\"x-transChange\" subType=\"x-added\">your</seg> rest</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.67")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G4474\" morph=\"robinson:V-AAI-3P\">smote <transChange type=\"added\">him</transChange> with the palms of their hands</w>", //$NON-NLS-1$
                                  "<w src=\"12\" lemma=\"strong:G4474\" morph=\"robinson:V-AAI-3P\">smote <seg type=\"x-transChange\" subType=\"x-added\">him</seg> with the palms of their hands</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Matt.26.69")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"9\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">and</w></w>", //$NON-NLS-1$
                                  "<w src=\"2 9\" lemma=\"strong:G1161 strong:G2532\" morph=\"robinson:CONJ\">and</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.10.27")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\"><w src=\"9\" lemma=\"strong:G102\" morph=\"robinson:A-NSN\">it is</transChange> impossible</w>", //$NON-NLS-1$
                                  "<transChange type=\"added\">it is</transChange> <w src=\"9\" lemma=\"strong:G102\" morph=\"robinson:A-NSN\">impossible</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.9.36")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"13\" lemma=\"strong:G4601\" morph=\"robinson:V-AAI-3P\">kept <transChange type=\"added\">it</transChange> close</w>", //$NON-NLS-1$
                                  "<w src=\"13\" lemma=\"strong:G4601\" morph=\"robinson:V-AAI-3P\">kept <seg type=\"x-transChange\" subType=\"x-added\">it</seg> close</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.11.19")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G1544\" morph=\"robinson:V-PAI-3P\" type=\"x-split\" subType=\"x-21\">cast <transChange type=\"added\">them</transChange> out</w>", //$NON-NLS-1$
                                  "<w src=\"14\" lemma=\"strong:G1544\" morph=\"robinson:V-PAI-3P\" type=\"x-split\" subType=\"x-21\">cast <seg type=\"x-transChange\" subType=\"x-added\">them</seg> out</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.12.13")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S\"><w src=\"9\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAM-2S\">speak</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 9\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S robinson:V-2AAM-2S\">speak</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.14.21")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"26\" lemma=\"strong:G3588\" morph=\"robinson:T-GSF\"><w src=\"22\" lemma=\"strong:G3588\" morph=\"robinson:T-APF\"></w></w>", //$NON-NLS-1$
                                  "<w src=\"26 22\" lemma=\"strong:G3588\" morph=\"robinson:T-GSF robinson:T-APF\"></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.14.25")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S\"></w><w src=\"5\" lemma=\"strong:G4183\" morph=\"robinson:A-NPM\"></w><w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
            input = input.replace("An</w>d</w> <w morph=\"robinson:V-INI-3P\" src=\"1\" lemma=\"strong:G4848\">there went</w>", //$NON-NLS-1$
                                  "And </w> <w morph=\"robinson:V-INI-3P\" src=\"1\" lemma=\"strong:G4848\" type=\"x-split\" subType=\"x-1\">there went</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:A-NPM\" src=\"5\" lemma=\"strong:G4183\">g<w src=\"4\" lemma=\"strong:G3793\" morph=\"robinson:N-NPM\">reat</w> <w morph=\"robinson:N-NPM\" src=\"4\" lemma=\"strong:G3793\">mu</w>ltitud<w src=\"1\" lemma=\"strong:G4848\" morph=\"robinson:V-INI-3P\"></w>e<w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-DSM\">s</w> </w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:A-NPM\" src=\"5\" lemma=\"strong:G4183\">great</w> <w morph=\"robinson:N-NPM\" src=\"4\" lemma=\"strong:G3793\">multitudes</w> "); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:P-DSM\" src=\"3\" lemma=\"strong:G846\">w<w src=\"6\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">ith</w> him<w src=\"7\" lemma=\"strong:G4762\" morph=\"robinson:V-2APP-NSM\"></w>: <w morph=\"robinson:CONJ\" src=\"6\" lemma=\"strong:G2532\">an</w>d<w src=\"10\" lemma=\"strong:G846\" morph=\"robinson:P-APM\"></w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-INI-3P\" src=\"1\" lemma=\"strong:G4848\" type=\"x-split\" subType=\"x-1\">with</w> <w morph=\"robinson:P-DSM\" src=\"3\" lemma=\"strong:G846\">him</w>: <w morph=\"robinson:CONJ\" src=\"6\" lemma=\"strong:G2532\">and</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-2APP-NSM\" src=\"7\" lemma=\"strong:G4762\">he turned<w src=\"9\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-2APP-NSM\" src=\"7\" lemma=\"strong:G4762\">he turned</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.14.35")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"robinson:CONJ\" src=\"1\" lemma=\"strong:G3777\"><w src=\"1\" lemma=\"strong:G3777\" morph=\"robinson:CONJ\"></w>neither</w>", //$NON-NLS-1$
                                  "<w src=\"1\" lemma=\"strong:G3777\" morph=\"robinson:CONJ\">neither</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-PAM-3S\" src=\"16\" lemma=\"strong:G191\">l<w src=\"16\" lemma=\"strong:G191\" morph=\"robinson:V-PAM-3S\"></w>et him hear</w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-PAM-3S\" src=\"16\" lemma=\"strong:G191\">let him hear</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"2\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"></w><w src=\"3\" lemma=\"strong:G1093\" morph=\"robinson:N-ASF\"></w><w src=\"4\" lemma=\"strong:G3777\" morph=\"robinson:CONJ\"></w><w src=\"5\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"></w><w src=\"6\" lemma=\"strong:G2874\" morph=\"robinson:N-ASF\"></w><w src=\"7\" lemma=\"strong:G2111\" morph=\"robinson:A-NSN\"></w><w src=\"8\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\"></w><w src=\"9\" lemma=\"strong:G1854\" morph=\"robinson:ADV\"></w><w src=\"10\" lemma=\"strong:G906\" morph=\"robinson:V-PAI-3P\"></w><w src=\"11\" lemma=\"strong:G846\" morph=\"robinson:P-ASN\"></w><w src=\"12\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\"></w><w src=\"13\" lemma=\"strong:G2192\" morph=\"robinson:V-PAP-NSM\"></w><w src=\"14\" lemma=\"strong:G3775\" morph=\"robinson:N-APN\"></w><w src=\"15\" lemma=\"strong:G191\" morph=\"robinson:V-PAN\"></w>", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
        }

        if (osisID.equals("Luke.15.24")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G756\" morph=\"robinson:V-ADI-3P\"></w><w src=\"14\" lemma=\"strong:G2147\" morph=\"robinson:V-API-3S\"></w><w src=\"9\" lemma=\"strong:G326\" morph=\"robinson:V-AAI-3S\"></w><w src=\"3\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\"></w><w morph=\"robinson:T-NSM\" src=\"3\" lemma=\"strong:G3588\"></w><w morph=\"robinson:CONJ\" src=\"1\" lemma=\"strong:G3754\"></w>F", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
            input = input.replace("<w src=\"2\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">or</w> </w>", //$NON-NLS-1$
                                  "<w src=\"1\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">For</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:D-NSM\" src=\"2\" lemma=\"strong:G3778\">th<w src=\"5\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\">i<w src=\"4\" lemma=\"strong:G5207\" morph=\"robinson:N-NSM\"></w>s</w> </w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:D-NSM\" src=\"2\" lemma=\"strong:G3778\">this</w><w morph=\"robinson:T-NSM\" src=\"3\" lemma=\"strong:G3588\"></w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:P-1GS\" src=\"5\" lemma=\"strong:G3450\">my<w src=\"7\" lemma=\"strong:G2258\" morph=\"robinson:V-IXI-3S\"></w> <w src=\"6\" lemma=\"strong:G3498\" morph=\"robinson:A-NSM\"></w><w morph=\"robinson:N-NSM\" src=\"4\" lemma=\"strong:G5207\">son</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-1GS\" src=\"5\" lemma=\"strong:G3450\">my</w> <w morph=\"robinson:N-NSM\" src=\"4\" lemma=\"strong:G5207\">son</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"8\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\"><w morph=\"robinson:V-IXI-3S\" src=\"7\" lemma=\"strong:G2258\"><w src=\"10\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">wa</w></w>s</w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-IXI-3S\" src=\"7\" lemma=\"strong:G2258\">was</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-AAI-3S\" src=\"9\" lemma=\"strong:G326\">is a<w src=\"12\" lemma=\"strong:G2258\" morph=\"robinson:V-IXI-3S\">li<w src=\"11\" lemma=\"strong:G622\" morph=\"robinson:V-2RAP-NSM\"></w>ve a</w>ga<w src=\"13\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">i<w src=\"15\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">n</w></w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-AAI-3S\" src=\"9\" lemma=\"strong:G326\">is alive again</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-IXI-3S\" src=\"12\" lemma=\"strong:G2258\">he was</w> <w morph=\"robinson:CONJ\" src=\"10\" lemma=\"strong:G2532\"></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:CONJ\" src=\"10\" lemma=\"strong:G2532\"></w><w morph=\"robinson:V-IXI-3S\" src=\"12\" lemma=\"strong:G2258\">he was</w> "); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-API-3S\" src=\"14\" lemma=\"strong:G2147\">i<w src=\"17\" lemma=\"strong:G2165\" morph=\"robinson:V-PPN\">s found</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-API-3S\" src=\"14\" lemma=\"strong:G2147\">is found</w>"); //$NON-NLS-1$
            input = "<q who=\"Jesus\">" + input; //$NON-NLS-1$
        }

        if (osisID.equals("Luke.19.30")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G4455\" morph=\"robinson:ADV\">never</w> <w src=\"15\" lemma=\"strong:G3762\" morph=\"robinson:A-NSM\"><w src=\"17\" lemma=\"strong:G444\" morph=\"robinson:N-GPM\">man</w></w>", //$NON-NLS-1$
                                  "<w src=\"15 16\" lemma=\"strong:G3762 strong:G4455\" morph=\"robinson:A-NSM robinson:ADV\">never</w> <w src=\"17\" lemma=\"strong:G444\" morph=\"robinson:N-GPM\">man</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("John.5.36")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G2491\" morph=\"robinson:N-GSM\">than <transChange type=\"added\">that</transChange> of John</w>", //$NON-NLS-1$
                                  "<w src=\"8\" lemma=\"strong:G2491\" morph=\"robinson:N-GSM\">than <seg type=\"x-transChange\" subType=\"x-added\">that</seg> of John</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Acts.17.25")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"robinson:A-APN\" src=\"16\" lemma=\"strong:G3956\"><w src=\"15\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things;</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:A-APN\" src=\"15 16\" lemma=\"strong:G3956\">all things;</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Acts.26.3")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"18\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\"><w morph=\"robinson:P-1GS\" src=\"19\" lemma=\"strong:G3450\">me</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\">me</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("2Cor.5.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"18\" lemma=\"strong:G3588\" morph=\"robinson:T-NSN\">might be <w src=\"22\" lemma=\"strong:G2222\" morph=\"robinson:N-GSF\"></w>swallowed up</w>", //$NON-NLS-1$
                                  "<w src=\"18\" lemma=\"strong:G3588\" morph=\"robinson:T-NSN\"></w><w src=\"22\" lemma=\"strong:G2222\" morph=\"robinson:N-GSF\">might be swallowed up</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("1Thess.1.6")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"17\" lemma=\"strong:G4151\" morph=\"robinson:N-GSN\"><w src=\"18\" lemma=\"strong:G40\" morph=\"robinson:A-GSN\">of the Holy</w> Ghost</w>", //$NON-NLS-1$
                                  "<w src=\"18\" lemma=\"strong:G40\" morph=\"robinson:A-GSN\">of the Holy</w> <w src=\"17\" lemma=\"strong:G4151\" morph=\"robinson:N-GSN\">Ghost</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("2Thess.1.11")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G2443\" morph=\"robinson:CONJ\">th<w src=\"2\" lemma=\"strong:G3739\" morph=\"robinson:R-ASN\"></w>at</w>", //$NON-NLS-1$
                                  "<w src=\"2 8\" lemma=\"strong:G3739 strong:G2443\" morph=\"robinson:R-ASN robinson:CONJ\">that</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Phlm.1.15")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"><w src=\"3\" lemma=\"strong:G1223\" morph=\"robinson:PREP\">for</w></w>", //$NON-NLS-1$
                                  "<w src=\"3 6\" lemma=\"strong:G1223 strong:G4314\" morph=\"robinson:PREP\">for</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.8.6")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G4537\" morph=\"robinson:V-AAS-3P\"><w morph=\"robinson:V-AAS-3P\" src=\"13\" lemma=\"strong:G4537\">sound.</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G4537\" morph=\"robinson:V-AAS-3P\">sound.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.11.1")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"19\" lemma=\"strong:G3588\" morph=\"robinson:T-APM\"><w morph=\"robinson:T-APM\" src=\"23\" lemma=\"strong:G3588\">them that</w></w>", //$NON-NLS-1$
                                  "<w src=\"19 23\" lemma=\"strong:G3588\" morph=\"robinson:T-APM\">them that</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-PAP-APM\" src=\"24\" lemma=\"strong:G4352\"><w src=\"20\" lemma=\"strong:G4352\" morph=\"robinson:V-PAP-APM\">worship</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-PAP-APM\" src=\"20 24\" lemma=\"strong:G4352\">worship</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:P-DSM\" src=\"26\" lemma=\"strong:G846\"><w src=\"22\" lemma=\"strong:G846\" morph=\"robinson:P-DSM\">therein.</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-DSM\" src=\"22 26\" lemma=\"strong:G846\">therein.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.11.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"15\" lemma=\"strong:G2476\" morph=\"robinson:V-RAP-NPF\"><w morph=\"robinson:V-RAP-NPF\" src=\"16\" lemma=\"strong:G2476\">standing</w></w>", //$NON-NLS-1$
                                  "<w src=\"15 16\" lemma=\"strong:G2476\" morph=\"robinson:V-RAP-NPF\">standing</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.11.14")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G5035\" morph=\"robinson:ADV\"><w morph=\"robinson:ADV\" src=\"13\" lemma=\"strong:G5035\">quickly.</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G5035\" morph=\"robinson:ADV\">quickly.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.14.7")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"32\" lemma=\"strong:G5204\" morph=\"robinson:N-GPN\"><w morph=\"robinson:N-GPN\" src=\"33\" lemma=\"strong:G5204\">of waters.</w></w>", //$NON-NLS-1$
                                  "<w src=\"32 33\" lemma=\"strong:G5204\" morph=\"robinson:N-GPN\">of waters.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.14.18")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"robinson:P-GSF\" src=\"42\" lemma=\"strong:G846\"><w src=\"40\" lemma=\"strong:G846\" morph=\"robinson:P-GSF\">her</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-GSF\" src=\"40 42\" lemma=\"strong:G846\">her</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:N-NPF\" src=\"41\" lemma=\"strong:G4718\"><w src=\"39\" lemma=\"strong:G4718\" morph=\"robinson:N-NPF\">grapes</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:N-NPF\" src=\"39 41\" lemma=\"strong:G4718\">grapes</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.19.14")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G2513\" morph=\"robinson:A-ASN\"><w morph=\"robinson:A-ASN\" src=\"17\" lemma=\"strong:G2513\">clean.</w></w>", //$NON-NLS-1$
                                  "<w src=\"16 17\" lemma=\"strong:G2513\" morph=\"robinson:A-ASN\">clean.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.19.18")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"28\" lemma=\"strong:G3173\" morph=\"robinson:A-GPM\"><w morph=\"robinson:A-GPM\" src=\"29\" lemma=\"strong:G3173\">great.</w></w>", //$NON-NLS-1$
                                  "<w src=\"28 29\" lemma=\"strong:G3173\" morph=\"robinson:A-GPM\">great.</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Rev.21.13")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G5140\" morph=\"robinson:A-NPM\"><w morph=\"robinson:A-NPM\" src=\"17\" lemma=\"strong:G5140\">three</w></w>", //$NON-NLS-1$
                                  "<w src=\"16 17\" lemma=\"strong:G5140\" morph=\"robinson:A-NPM\">three</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.1.9")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"20\" morph=\"robinson:N-ASM\" lemma=\"strong:G2446\"><w src=\"19\" lemma=\"strong:G2446\" morph=\"robinson:N-ASM\">Jordan</w></w>.", //$NON-NLS-1$
                                  "<w src=\"20\" morph=\"robinson:N-ASM\" lemma=\"strong:G2446\">Jordan</w>.<w src=\"19\" lemma=\"strong:G2446\" morph=\"robinson:N-ASM\"></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.1.18")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"robinson:V-AAI-3P\" type=\"x-split\" subType=\"x-10\" src=\"9\" lemma=\"strong:G190\"><w src=\"7\" lemma=\"strong:G190\" morph=\"robinson:V-AAI-3P\">and followed</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-AAI-3P\" type=\"x-split\" subType=\"x-10\" src=\"9\" lemma=\"strong:G190\">and followed</w><w src=\"7\" lemma=\"strong:G190\" morph=\"robinson:V-AAI-3P\"></w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.1.38")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"><w src=\"13\" lemma=\"strong:G5124\" morph=\"robinson:D-ASN\">therefore</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G1519 strong:G5124\" morph=\"robinson:PREP robinson:D-ASN\">therefore</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"19\" lemma=\"strong:G1909\" morph=\"robinson:PREP\"><w src=\"20\" lemma=\"strong:G3739\" morph=\"robinson:R-DSM\">wherein</w></w>", //$NON-NLS-1$
                                  "<w src=\"19 20\" lemma=\"strong:G1909 strong:G3739\" morph=\"robinson:PREP robinson:R-DSM\">wherein</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.7")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"11\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"10 11\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.18")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G1223\" morph=\"robinson:PREP\"><w src=\"17\" lemma=\"strong:G5101\" morph=\"robinson:I-ASN\">Why</w></w>", //$NON-NLS-1$
                                  "<w src=\"16 17\" lemma=\"strong:G1223 strong:G5101\" morph=\"robinson:PREP robinson:I-ASN\">Why</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.19")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"6\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"7\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3P\">Can</w></w>", //$NON-NLS-1$
                                  "<w src=\"6 7\" lemma=\"strong:G3361 strong:G1410\" morph=\"robinson:PRT-N robinson:V-PNI-3P\">Can</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"12\" lemma=\"strong:G1722\" morph=\"robinson:PREP\"><w src=\"13\" lemma=\"strong:G3739\" morph=\"robinson:R-DSM\">while</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 13\" lemma=\"strong:G1722 strong:G3739\" morph=\"robinson:PREP robinson:R-DSM\">while</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"21\" lemma=\"strong:G5550\" morph=\"robinson:N-ASM\"><w src=\"20\" lemma=\"strong:G3745\" morph=\"robinson:K-ASM\">as long as</w></w>", //$NON-NLS-1$
                                  "<w src=\"21 20\" lemma=\"strong:G5550 strong:G3745\" morph=\"robinson:N-ASM robinson:K-ASM\">as long as</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.21")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"11\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"10\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">else</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"11 10 12\" lemma=\"strong:G1161 strong:G1487 strong:G3361\" morph=\"robinson:CONJ robinson:COND robinson:PRT-N\">else</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.22")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"9\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"10\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"11\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">else</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"9 10 11\" lemma=\"strong:G1487 strong:G1161 strong:G3361\" morph=\"robinson:COND robinson:CONJ robinson:PRT-N\">else</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.23")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"17\" lemma=\"strong:G4160\" morph=\"robinson:V-PAN\"><w src=\"18\" lemma=\"strong:G5089\" morph=\"robinson:V-PAP-NPM\">to pluck</w></w>", //$NON-NLS-1$
                                  "<w src=\"17 18\" lemma=\"strong:G4160 strong:G5089\" morph=\"robinson:V-PAN robinson:V-PAP-NPM\">to pluck</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.2.26")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G740\" morph=\"robinson:N-APM\"><w src=\"16\" lemma=\"strong:G4286\" morph=\"robinson:N-GSF\">the shewbread</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 16\" lemma=\"strong:G740 strong:G4286\" morph=\"robinson:N-APM robinson:N-GSF\">the shewbread</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"22\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"23\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"22 23\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.9")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"13\" lemma=\"strong:G2443\" morph=\"robinson:CONJ\">lest</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 13\" lemma=\"strong:G3361 strong:G2443\" morph=\"robinson:PRT-N robinson:CONJ\">lest</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.16")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he <w src=\"6\" lemma=\"strong:G4074\" morph=\"robinson:N-ASM\">surnamed</w></w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he</w> <w src=\"6\" lemma=\"strong:G4074\" morph=\"robinson:N-ASM\">surnamed</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.17")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"13\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he <w src=\"15\" lemma=\"strong:G3686\" morph=\"robinson:N-APN\">surnamed</w></w>", //$NON-NLS-1$
                                  "<w src=\"13\" lemma=\"strong:G2007\" morph=\"robinson:V-AAI-3S\">he</w> <w src=\"15\" lemma=\"strong:G3686\" morph=\"robinson:N-APN\">surnamed</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.24")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\"><w src=\"7\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w></w>", //$NON-NLS-1$
                                  "<w src=\"8 7\" lemma=\"strong:G1410 strong:G3756\" morph=\"robinson:V-PNI-3S robinson:PRT-N\">cannot</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.25")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\"><w src=\"7\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w></w>", //$NON-NLS-1$
                                  "<w src=\"8 7\" lemma=\"strong:G1410 strong:G3756\" morph=\"robinson:V-PNI-3S robinson:PRT-N\">cannot</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.26")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"11\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\">he <w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w></w>", //$NON-NLS-1$
                                  "<w src=\"11\" lemma=\"strong:G1410\" morph=\"robinson:V-PNI-3S\">he</w> <w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\">cannot</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.27")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G1437\" morph=\"robinson:COND\"><w src=\"15\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">except</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G1437 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">except</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.29")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"13\" lemma=\"strong:G1519\" morph=\"robinson:PREP\"><w src=\"15\" lemma=\"strong:G165\" morph=\"robinson:N-ASM\">never</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"10 13 15\" lemma=\"strong:G3756 strong:G1519 strong:G165\" morph=\"robinson:PRT-N robinson:PREP robinson:N-ASM\">never</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"7\" lemma=\"strong:G4151\" morph=\"robinson:N-ASN\"><w src=\"9\" lemma=\"strong:G40\" morph=\"robinson:A-ASN\">the Holy</w> Ghost</w>", //$NON-NLS-1$
                                  "<w src=\"9\" lemma=\"strong:G40\" morph=\"robinson:A-ASN\">the Holy</w> <w src=\"7\" lemma=\"strong:G4151\" morph=\"robinson:N-ASN\">Ghost</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.3.35")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"6\" lemma=\"strong:G3739\" morph=\"robinson:R-NSN\"><w src=\"7\" lemma=\"strong:G3303\" morph=\"robinson:PRT\">some</w></w>", //$NON-NLS-1$
                                  "<w src=\"6 7\" lemma=\"strong:G3739 strong:G3303\" morph=\"robinson:R-NSN robinson:PRT\">some</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.20")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"22\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\"><w src=\"26\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">and</w></w>", //$NON-NLS-1$
                                  "<w src=\"22 26\" lemma=\"strong:G2532 strong:G2532\" morph=\"robinson:CONJ robinson:CONJ\">and</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.22")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"4\" lemma=\"strong:G5100\" morph=\"robinson:X-NSN\">nothing</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 4\" lemma=\"strong:G3756 strong:G5100\" morph=\"robinson:PRT-N robinson:X-NSN\">nothing</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"14\" lemma=\"strong:G2443\" morph=\"robinson:CONJ\"><w src=\"15\" lemma=\"strong:G1519\" morph=\"robinson:PREP\">that</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G2443 strong:G1519\" morph=\"robinson:CONJ robinson:PREP\">that</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.4.34")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\">to</w> <w src=\"12\" lemma=\"strong:G846\" morph=\"robinson:P-GSM\">his</w> <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\" type=\"x-split\" subType=\"x-15\">disciples</w>.</w>", //$NON-NLS-1$
                                  "<w src=\"14\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things</w> <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\">to</w> <w src=\"12\" lemma=\"strong:G846\" morph=\"robinson:P-GSM\">his</w> <w src=\"11\" lemma=\"strong:G3101\" morph=\"robinson:N-DPM\" type=\"x-split\" subType=\"x-15\">disciples</w>."); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.5.29")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G4983\" morph=\"robinson:N-DSN\">in <transChange type=\"added\">her</transChange> body</w>", //$NON-NLS-1$
                                  "<w src=\"12\" lemma=\"strong:G4983\" morph=\"robinson:N-DSN\">in <seg type=\"x-transChange\" subType=\"x-added\">her</seg> body</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.1")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"11\" lemma=\"strong:G5101\" morph=\"robinson:I-ASN\"><w src=\"9\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">nothing</w></w>", //$NON-NLS-1$
                                  "<w src=\"11 9\" lemma=\"strong:G5101 strong:G3361\" morph=\"robinson:I-ASN robinson:PRT-N\">nothing</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.2")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"14\" lemma=\"strong:G5101\" morph=\"robinson:I-ASN\">nothing</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 14\" lemma=\"strong:G3756 strong:G5101\" morph=\"robinson:PRT-N robinson:I-ASN\">nothing</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.14")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"7\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"6\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"5\" lemma=\"strong:G2532\" morph=\"robinson:CONJ\">neither</w></w></w></w>", //$NON-NLS-1$
                                  "<w src=\"10 7 6 5\" lemma=\"strong:G3756 strong:G3361 strong:G1487 strong:G2532\" morph=\"robinson:PRT-N robinson:PRT-N robinson:COND robinson:CONJ\">neither</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.24")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G3708\" morph=\"robinson:V-PAI-1S\"><w src=\"4\" lemma=\"strong:G991\" morph=\"robinson:V-PAI-1S\">I see</w></w>", //$NON-NLS-1$
                                  "<w src=\"10 4\" lemma=\"strong:G3708 strong:G991\" morph=\"robinson:V-PAI-1S robinson:V-PAI-1S\">I see</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"7\" lemma=\"strong:G3754\" morph=\"robinson:CONJ\"><w src=\"8\" lemma=\"strong:G5613\" morph=\"robinson:ADV\">as</w></w>", //$NON-NLS-1$
                                  "<w src=\"7 8\" lemma=\"strong:G3754 strong:G5613\" morph=\"robinson:CONJ robinson:ADV\">as</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.35")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">whosoever</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"13\" lemma=\"strong:G302\" morph=\"robinson:PRT\"><w src=\"11\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"13 11\" lemma=\"strong:G302 strong:G3739\" morph=\"robinson:PRT robinson:R-NSM\">whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.8.38")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">Whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">Whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.1")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"15\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G3756 strong:G3361\" morph=\"robinson:PRT-N robinson:PRT-N\">not</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"18\" lemma=\"strong:G2193\" morph=\"robinson:CONJ\"><w src=\"19\" lemma=\"strong:G302\" morph=\"robinson:PRT\">till</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G2193 strong:G302\" morph=\"robinson:CONJ robinson:PRT\">till</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.9")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"19\" lemma=\"strong:G3588\" morph=\"robinson:T-GSM\"></w><w src=\17\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\"></w><w src=\"5\" lemma=\"strong:G3588\" morph=\"robinson:T-GSN\"></w>", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
            input = input.replace("<w src=\"14\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"15\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\"><w src=\"16\" lemma=\"strong:G3752\" morph=\"robinson:CONJ\">till</w></w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15 16\" lemma=\"strong:G1487 strong:G3361 strong:G3752\" morph=\"robinson:COND robinson:PRT-N robinson:CONJ\">till</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"1\" lemma=\"strong:G2597\" morph=\"robinson:V-PAP-GPM\">as <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-GPM\">they</w> came down</w>", //$NON-NLS-1$
                                  "<w src=\"1\" lemma=\"strong:G2597\" morph=\"robinson:V-PAP-GPM\">as</w> <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-GPM\">they came down</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.18")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G3699\" morph=\"robinson:ADV\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">wheresoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"2 3\" lemma=\"strong:G3699 strong:G302\" morph=\"robinson:ADV robinson:PRT\">wheresoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.19")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"9\" lemma=\"strong:G2193\" morph=\"robinson:CONJ\"><w src=\"10\" lemma=\"strong:G4219\" morph=\"robinson:PRT-I\">how long</w></w>", //$NON-NLS-1$
                                  "<w src=\"9 10\" lemma=\"strong:G2193 strong:G4219\" morph=\"robinson:CONJ robinson:PRT-I\">how long</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"14\" lemma=\"strong:G2193\" morph=\"robinson:CONJ\"><w src=\"15\" lemma=\"strong:G4219\" morph=\"robinson:PRT-I\">how long</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G2193 strong:G4219\" morph=\"robinson:CONJ robinson:PRT-I\">how long</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.25")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"><w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\" type=\"x-split\" subType=\"x-32\">When</w></w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\" type=\"x-split\" subType=\"x-32\">When</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.26")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"14\" lemma=\"strong:G3004\" morph=\"robinson:V-PAN\"><w src=\"15\" lemma=\"strong:G3754\" morph=\"robinson:CONJ\">said</w></w>", //$NON-NLS-1$
                                  "<w src=\"14 15\" lemma=\"strong:G3004 strong:G3754\" morph=\"robinson:V-PAN robinson:CONJ\">said</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.28")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G1525\" morph=\"robinson:V-2AAP-ASM\">when <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-ASM\">he</w> was come <w src=\"4\" lemma=\"strong:G1519\" morph=\"robinson:PREP\">into</w></w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G1525\" morph=\"robinson:V-2AAP-ASM\">when</w> <w src=\"3\" lemma=\"strong:G846\" morph=\"robinson:P-ASM\">he was come</w> <w src=\"4\" lemma=\"strong:G1519\" morph=\"robinson:PREP\">into</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"12\" lemma=\"strong:G2398\" morph=\"robinson:A-ASF\"><w src=\"11\" lemma=\"strong:G2596\" morph=\"robinson:PREP\">privately</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 11\" lemma=\"strong:G2398 strong:G2596\" morph=\"robinson:A-ASF robinson:PREP\">privately</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"11\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"11 12\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.29")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"11\" lemma=\"strong:G1487\" morph=\"robinson:COND\"><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">but</w></w>", //$NON-NLS-1$
                                  "<w src=\"11 12\" lemma=\"strong:G1487 strong:G3361\" morph=\"robinson:COND robinson:PRT-N\">but</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.37")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"2\" lemma=\"strong:G1437\" morph=\"robinson:COND\">Whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 2\" lemma=\"strong:G3739 strong:G1437\" morph=\"robinson:R-NSM robinson:COND\">Whosoever</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"15\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"16\" lemma=\"strong:G1437\" morph=\"robinson:COND\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"15 16\" lemma=\"strong:G3739 strong:G1437\" morph=\"robinson:R-NSM robinson:COND\">whosoever</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.41")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\"><w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"1 3\" lemma=\"strong:G3739 strong:G302\" morph=\"robinson:R-NSM robinson:PRT\">whosoever</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"18\" lemma=\"strong:G3756\" morph=\"robinson:PRT-N\"><w src=\"19\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G3756 strong:G3361\" morph=\"robinson:PRT-N robinson:PRT-N\">not</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.42")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"3\" lemma=\"strong:G302\" morph=\"robinson:PRT\"><w src=\"2\" lemma=\"strong:G3739\" morph=\"robinson:R-NSM\">whosoever</w></w>", //$NON-NLS-1$
                                  "<w src=\"3 2\" lemma=\"strong:G302 strong:G3739\" morph=\"robinson:PRT robinson:R-NSM\">whosoever</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"12\" lemma=\"strong:G2570\" morph=\"robinson:A-NSN\"><w src=\"15\" lemma=\"strong:G3123\" morph=\"robinson:ADV\">better</w></w>", //$NON-NLS-1$
                                  "<w src=\"12 15\" lemma=\"strong:G2570 strong:G3123\" morph=\"robinson:A-NSN robinson:ADV\">better</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"18\" lemma=\"strong:G3037\" morph=\"robinson:N-NSM\"><w src=\"19\" lemma=\"strong:G3457\" morph=\"robinson:A-NSM\">a millstone</w></w>", //$NON-NLS-1$
                                  "<w src=\"18 19\" lemma=\"strong:G3037 strong:G3457\" morph=\"robinson:N-NSM robinson:A-NSM\">a millstone</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.9.50")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"9\" lemma=\"strong:G1096\" morph=\"robinson:V-2ADS-3S\"><w src=\"8\" lemma=\"strong:G358\" morph=\"robinson:A-NSN\">have lost his saltness</w></w>", //$NON-NLS-1$
                                  "<w src=\"9 8\" lemma=\"strong:G1096 strong:G358\" morph=\"robinson:V-2ADS-3S robinson:A-NSN\">have lost his saltness</w>"); //$NON-NLS-1$
            input = input.replace("<w src=\"10\" lemma=\"strong:G1722\" morph=\"robinson:PREP\"><w src=\"11\" lemma=\"strong:G5101\" morph=\"robinson:I-DSN\">wherewith</w></w>", //$NON-NLS-1$
                                  "<w src=\"10 11\" lemma=\"strong:G1722 strong:G5101\" morph=\"robinson:PREP robinson:I-DSN\">wherewith</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("Mark.10.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G630\" morph=\"robinson:V-AAN\">to put <transChange type=\"added\">her</transChange> away</w>", //$NON-NLS-1$
                                  "<w src=\"10\" lemma=\"strong:G630\" morph=\"robinson:V-AAN\">to put <seg type=\"x-transChange\" subType=\"x-added\">her</seg> away</w>"); //$NON-NLS-1$
        }

        if (osisID.equals("")) //$NON-NLS-1$
        {
            input = input.replace("", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
        }

        Set<Integer> split = new TreeSet<Integer>();
        Set<Integer> dup = new TreeSet<Integer>();
        Set<Integer> after = new HashSet<Integer>();
        Map<Integer, String> wMap = new HashMap<Integer, String>();

        wMatcher = wPattern.matcher(input);
        while (wMatcher.find())
        {
            String whole = wMatcher.group();
            Matcher srcMatcher = srcPattern.matcher(whole);
            while (srcMatcher.find())
            {
                String[] numbers = StringUtil.split(srcMatcher.group(1), ' ');
                for (String n : numbers)
                {
                    Integer src = new Integer(n);
                    if (!wMap.containsKey(src))
                    {
                        wMap.put(src, whole);
                    }

                    // If the src number occurs on more than one w element
                    // then all those w elements must have a split id.
                    if (whole.contains("x-split")) //$NON-NLS-1$
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
        // Find the <w> element with src+1, if it is N-xxx, then
        // merge this to that element, removing the G3588 one.
        for (Map.Entry<Integer, String> entry : wMap.entrySet())
        {
            String definiteArticle = entry.getValue() + "</w>"; //$NON-NLS-1$
            if (!(input.contains(definiteArticle) && definiteArticle.contains("G3588"))) //$NON-NLS-1$
            {
                continue;
            }

            Matcher morphTMatcher = morphTPattern.matcher(definiteArticle);
            if (!morphTMatcher.find())
            {
                continue;
            }

            String tType = morphTMatcher.group(1);
            Integer here = entry.getKey();
            Integer next = new Integer(here.intValue() + 1);
            String found = wMap.get(next);
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
            if (tType.equals(nType) && input.contains("src=\"" + next + "\"")) //$NON-NLS-1$ //$NON-NLS-2$
            {
                String changed = found;
                changed = changed.replace("src=\"", "src=\"" + here + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                changed = changed.replace("lemma=\"", "lemma=\"strong:G3588 "); //$NON-NLS-1$ //$NON-NLS-2$
                changed = changed.replace("morph=\"", "morph=\"robinson:T-" + tType + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                input = input.replace(definiteArticle, ""); //$NON-NLS-1$
                input = input.replace(found, changed);
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
        input = fixApostrophe(osisID, input);
        input = fixPunctuation(osisID, input);
        input = fixDivineName(osisID, input);
        input = fixSpelling(osisID, input);
        input = fixTransChange(osisID, input);
        input = fixHyphenatedNames(osisID, input);
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
        if (osisID.equals("Lev.2.3") || osisID.equals("Lev.2.10")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("sons'", "sons':"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Lev.7.31")) //$NON-NLS-1$
        {
            input = input.replace("sons'", "sons'."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Lev.24.9") || osisID.equals("Ezek.46.16")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("sons'", "sons';"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Sam.6.9")) //$NON-NLS-1$
        {
            input = input.replace("us:", "us;"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Kgs.12.16")) //$NON-NLS-1$
        {
            input = input.replace("priests'", "priests'."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (osisID.equals("Isa.30.14")) //$NON-NLS-1$
        {
            input = input.replace("..:", "...:"); //$NON-NLS-1$ //$NON-NLS-2$
        }

//        if (osisID.equals("Ps.119.9")) //$NON-NLS-1$
//        {
//            System.err.println(osisID + ':' + input);
//        }

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
            input = input.replace("Magdala</w>,", "Magdala</w>."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.26.56")) //$NON-NLS-1$
        {
            input = input.replace(".</q>", ".</q> "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.2.1")) //$NON-NLS-1$
        {
            input = input.replace(",", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.8.47")) //$NON-NLS-1$
        {
            input = input.replace(" <w src=\"24", ", <w src=\"24"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.12.49")) //$NON-NLS-1$
        {
            input = input.replace("will I</w>", "will I</w>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.15.24")) //$NON-NLS-1$
        {
            input = input.replace("For</w>", "For</w> "); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replace("this</w>", "this</w> "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.17.37")) //$NON-NLS-1$
        {
            input = input.replace(" is</w>,", "</w> <transChange type=\"added\">is</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.2.17")) //$NON-NLS-1$
        {
            input = input.replace("God</w>,<w", "God</w>, <w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.5.21")) //$NON-NLS-1$
        {
            input = input.replace("Israel</w>", "Israel</w>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

//        if (osisID.equals("Acts.2.1")) //$NON-NLS-1$
//        {
//            System.err.println(osisID + ':' + input);
//            input = input.replace(",", ""); //$NON-NLS-1$ //$NON-NLS-2$
//        }

        if (osisID.equals("Acts.16.40")) //$NON-NLS-1$
        {
            input = input.replace("Lydia</w>", "Lydia</w>:"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.24.25")) //$NON-NLS-1$
        {
            input = input.replace("come</w>", "come</w>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.15.27")) //$NON-NLS-1$
        {
            input = input.replace("</w>,", "</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return input;
    }

    private String fixSpelling(String osisID, String input)
    {
        if (osisID.equals("Matt.5.10")) //$NON-NLS-1$
        {
            input = input.replace("righteousness", "righteousness'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.26.39")) //$NON-NLS-1$
        {
            input = input.replace("farther", "further"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.27.3")) //$NON-NLS-1$
        {
            input = input.replace("betrayeth", "betrayed"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.1.19")) //$NON-NLS-1$
        {
            input = input.replace("farther", "further"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.12.36")) //$NON-NLS-1$
        {
            input = input.replaceFirst("Lord", "<seg><divineName>LORD</divineName></seg>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.14.43")) //$NON-NLS-1$
        {
            input = input.replace("priest", "priests"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.7.25")) //$NON-NLS-1$
        {
            input = input.replace("kings", "kings'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.23.32")) //$NON-NLS-1$
        {
            input = input.replace("others", "other"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.19.18")) //$NON-NLS-1$
        {
            input = input.replace("others", "other"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.20.27")) //$NON-NLS-1$
        {
            input = input.replaceFirst("reach", "Reach"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.21.11")) //$NON-NLS-1$
        {
            input = input.replace("and hundred", "an hundred"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.11.12") || osisID.equals("Acts.11.28") || osisID.equals("1John.5.8")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            input = input.replace("spirit", "Spirit"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.16.23")) //$NON-NLS-1$
        {
            input = input.replace("jailer", "jailor"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.28.15")) //$NON-NLS-1$
        {
            input = input.replace("Forum", "forum"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.4.15")) //$NON-NLS-1$
        {
            input = input.replace("instructers", "instructors"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.4.19") || osisID.equals("Rom.9.9") || osisID.equals("1Pet.3.6")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            input = input.replace("Sarah", "Sara"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.15.58")) //$NON-NLS-1$
        {
            input = input.replace("unmovable", "unmoveable"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.9.13")) //$NON-NLS-1$
        {
            input = input.replace("into", "unto"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Gal.4.30")) //$NON-NLS-1$
        {
            input = input.replace("free woman", "freewoman"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Phil.2.25")) //$NON-NLS-1$
        {
            input = input.replace("fellow soldier", "fellowsoldier"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Col.4.11")) //$NON-NLS-1$
        {
            input = input.replace("fellow workers", "fellowworkers"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.1.13")) //$NON-NLS-1$
        {
            input = input.replace("times", "time"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Pet.2.6")) //$NON-NLS-1$
        {
            input = input.replace("Gomorrah", "Gomorrha"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Pet.2.13")) //$NON-NLS-1$
        {
            input = input.replace("daytime", "day time"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rev.2.6") || osisID.equals("Rev.2.15")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Nicolaitanes", "Nicolaitans"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Acts.9.33") || osisID.equals("Acts.9.34")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Aeneas", "\u00C6neas"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("John.3.23")) //$NON-NLS-1$
        {
            input = input.replace("Aenon", "\u00C6non"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.2.14") || osisID.equals("Luke.6.15")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Alphaeus", "Alph\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Matt.27.57") || osisID.equals("Luke.23.51")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Arimathaea", "Arimath\u00E6a"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Mark.10.46")) //$NON-NLS-1$
        {
            input = input.replace("Bartimaeus", "Bartim\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (!osisID.equals("Matt.22.17") && //$NON-NLS-1$
            !osisID.equals("Phil.4.22") && //$NON-NLS-1$
            !osisID.equals("Matt.16.13")) //$NON-NLS-1$
        {
            input = input.replace("Caesar", "C\u00E6sar"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.7.4")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Chaldaeans", "Chald\u00E6ans"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Mark") || //$NON-NLS-1$
            osisID.startsWith("Luke") || //$NON-NLS-1$
            osisID.startsWith("John") || //$NON-NLS-1$
            osisID.startsWith("Acts")) //$NON-NLS-1$
        {
            input = input.replace("Judaea", "Jud\u00E6a"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (!osisID.equals("Mark.14.70")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Galilaean", "Galil\u00E6an"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.15.16")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Praetorium", "Pr\u00E6torium"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.10.46")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Timaeus", "Tim\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.startsWith("Luke.19")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("Zacchaeus", "Zacch\u00E6us"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return input;
    }

    private String fixTransChange(String osisID, String input)
    {
        if (osisID.equals("Matt.2.6")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"4\" lemma=\"strong:G1093\" morph=\"robinson:N-VSF\">in ", "<transChange type=\"added\">in</transChange> <w src=\"4\" lemma=\"strong:G1093\" morph=\"robinson:N-VSF\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.5.30")) //$NON-NLS-1$
        {
            input = input.replace("cast it</w>", "cast</w> <transChange type=\"added\">it</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.8.13")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"11\" lemma=\"strong:G1096\" morph=\"robinson:V-AOM-3S\">so ", "<transChange type=\"added\">so</transChange> <w src=\"11\" lemma=\"strong:G1096\" morph=\"robinson:V-AOM-3S\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.20.11")) //$NON-NLS-1$
        {
            input = input.replace(" it</w>", "</w> <transChange type=\"added\">it</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.24.26")) //$NON-NLS-1$
        {
            input = input.replaceFirst("<w src=\"9\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">he is</w>", "<transChange type=\"added\">he is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceFirst("<transChange type=\"added\">he is</transChange>", "<w src=\"9\" lemma=\"strong:G2076\" morph=\"robinson:V-PXI-3S\">he is</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Matt.24.32")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"21\" lemma=\"strong:G1451\" morph=\"robinson:ADV\">is ", "<transChange type=\"added\">is</transChange> <w src=\"21\" lemma=\"strong:G1451\" morph=\"robinson:ADV\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.4.11")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"20\" lemma=\"strong:G3956\" morph=\"robinson:A-NPN\">all these</w> <transChange type=\"added\">things</transChange>", "<w src=\"20\" lemma=\"strong:G3956\" morph=\"robinson:A-NPN\" type=\"x-split\" subType=\"x-20\">all</w> <transChange type=\"added\">these</transChange> <w src=\"20\" lemma=\"strong:G3956\" morph=\"robinson:A-NPN\" type=\"x-split\" subType=\"x-20\">things</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Mark.12.30")) //$NON-NLS-1$
        {
            input = input.replace(" is</w>", "</w> <transChange type=\"added\">is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (osisID.equals("Luke.4.18")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"3\" lemma=\"strong:G1909\" morph=\"robinson:PREP\">is ", "<transChange type=\"added\">is</transChange> <w src=\"3\" lemma=\"strong:G1909\" morph=\"robinson:PREP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.11.27")) //$NON-NLS-1$
        {
            input = input.replace(" is</w> <w src=\"18", "</w> <transChange type=\"added\">is</transChange> <w src=\"18"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.11.31")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"31\" lemma=\"strong:G5602\" morph=\"robinson:ADV\">is ", "<transChange type=\"added\">is</transChange> <w src=\"31\" lemma=\"strong:G5602\" morph=\"robinson:ADV\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.17.37")) //$NON-NLS-1$
        {
            input = input.replace(" is</w>,", "</w> <transChange type=\"added\">is</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.18.1")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\"><w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\">", "<w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"></w><transChange type=\"added\">"); //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replace("</w>,</transChange>", "</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Luke.21.34")) //$NON-NLS-1$
        {
            input = input.replace(" so</w>", "</w> <transChange type=\"added\">so</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.10.38")) //$NON-NLS-1$
        {
            input = input.replace("</w> is <w", "</w> <transChange type=\"added\">is</transChange> <w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.13.13")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G1510\" morph=\"robinson:V-PXI-1S\">so ", "<transChange type=\"added\">so</transChange> <w src=\"12\" lemma=\"strong:G1510\" morph=\"robinson:V-PXI-1S\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("John.14.2")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w> <w src=\"11\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">so</w>", "<w src=\"11\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\"></w><w src=\"12\" lemma=\"strong:G3361\" morph=\"robinson:PRT-N\">not</w> <transChange type=\"added\">so</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
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
            input = input.replace("<w src=\"1\" lemma=\"strong:G2152\" morph=\"robinson:A-NSM\">A ", "<transChange type=\"added\">A </transChange> <w src=\"1\" lemma=\"strong:G2152\" morph=\"robinson:A-NSM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.16.11")) //$NON-NLS-1$
        {
            input = input.replace(" day</w>", "</w> <transChange type=\"added\">day</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.19.19")) //$NON-NLS-1$
        {
            input = input.replace("found it</w>", "found</w> <transChange type=\"added\">it</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.22.3")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"9 10\" lemma=\"strong:G3588 strong:G2791\" morph=\"robinson:T-GSF robinson:N-GSF\">a city ", "<transChange type=\"added\">a city</transChange> <w src=\"9 10\" lemma=\"strong:G3588 strong:G2791\" morph=\"robinson:T-GSF robinson:N-GSF\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.6.5")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\">in the likeness of his</transChange>", "<transChange type=\"added\">in the likeness</transChange> of <transChange type=\"added\">his</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.12.2")) //$NON-NLS-1$
        {
            input = input.replace(" is</w>", "</w> <transChange type=\"added\">is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.10.20")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\">I say</transChange>", "I <transChange type=\"added\">say</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.11.26")) //$NON-NLS-1$
        {
            input = input.replace("</w><transChange type=\"added\">this</transChange>", "this</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.11.27")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10 11\" lemma=\"strong:G3588 strong:G4221\" morph=\"robinson:T-ASN robinson:N-ASN\">this ", "<transChange type=\"added\">this</transChange> <w src=\"10 11\" lemma=\"strong:G3588 strong:G4221\" morph=\"robinson:T-ASN robinson:N-ASN\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Cor.15.10")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\">which was bestowed</transChange>", "which <transChange type=\"added\">was bestowed</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.1.2")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G5213\" morph=\"robinson:P-2DP\">be ", "<transChange type=\"added\">be</transChange> <w src=\"2\" lemma=\"strong:G5213\" morph=\"robinson:P-2DP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.2.6")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"6\" lemma=\"strong:G3778\" morph=\"robinson:D-NSF\">is ", "<transChange type=\"added\">is</transChange> <w src=\"6\" lemma=\"strong:G3778\" morph=\"robinson:D-NSF\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.8.18")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">is ", "<transChange type=\"added\">is</transChange> <w src=\"10\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.11.9")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"28\" lemma=\"strong:G5083\" morph=\"robinson:V-FAI-1S\">so ", "<transChange type=\"added\">so</transChange> <w src=\"28\" lemma=\"strong:G5083\" morph=\"robinson:V-FAI-1S\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Eph.5.9")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"6\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">is ", "<transChange type=\"added\">is</transChange> <w src=\"6\" lemma=\"strong:G1722\" morph=\"robinson:PREP\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Tim.3.11")) //$NON-NLS-1$
        {
            input = input.replace("</w> be <w", "</w> <transChange type=\"added\">be</transChange> <w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Phlm.1.1")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\">our</w> <w src=\"8\" lemma=\"strong:G80\" morph=\"robinson:N-NSM\">", "<transChange type=\"added\">our</transChange> <w src=\"7 8\" lemma=\"strong:G3588 strong:G80\" morph=\"robinson:T-NSM robinson:N-NSM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.10.23")) //$NON-NLS-1$
        {
            input = input.replace("he is</w>", "he</w> <transChange type=\"added\">is</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.12.1")) //$NON-NLS-1$
        {
            input = input.replace("</w> us,", "</w> <transChange type=\"added\">us</transChange>,"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Heb.12.19")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\">which</transChange> <w src=\"7\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\">voice</w>", "<w src=\"7\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\">which</w> <transChange type=\"added\">voice</transChange>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Jas.2.16")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"10\" lemma=\"strong:G2328\" morph=\"robinson:V-PEM-2P\">be ye warmed</w>", "<w src=\"10\" lemma=\"strong:G2328\" morph=\"robinson:V-PEM-2P\" type=\"x-split\" subType=\"x-10\">be</w> <transChange type=\"added\">ye</transChange> <w src=\"10\" lemma=\"strong:G2328\" morph=\"robinson:V-PEM-2P\" type=\"x-split\" subType=\"x-10\">warmed</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1John.2.23")) //$NON-NLS-1$
        {
            input = input.replace("(<transChange type=\"added\">", "<transChange type=\"added\">("); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1John.5.19")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"1\" lemma=\"strong:G1492\" morph=\"robinson:V-RAI-1P\">And ", "<transChange type=\"added\">And</transChange> <w src=\"1\" lemma=\"strong:G1492\" morph=\"robinson:V-RAI-1P\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1John.5.20")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\"><w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">And</w></transChange>", "<w src=\"2\" lemma=\"strong:G1161\" morph=\"robinson:CONJ\">And</w>"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rev.22.2")) //$NON-NLS-1$
        {
            input = input.replace("</transChange> <w src=\"15\" lemma=\"strong:G2590\" morph=\"robinson:N-APM\">of ", " of</transChange> <w src=\"15\" lemma=\"strong:G2590\" morph=\"robinson:N-APM\">"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rev.22.7")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"5\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\">is ", "<transChange type=\"added\">is</transChange> <w src=\"5\" lemma=\"strong:G3588\" morph=\"robinson:T-NSM\">"); //$NON-NLS-1$ //$NON-NLS-2$
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

        if (osisID.equals("Gen.49.17")) //$NON-NLS-1$
        {
            input = input.replace("arrow-snake", "arrow\u2010snake"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Exod.18.19") || osisID.equals("2Cor.3.4")) //$NON-NLS-1$ //$NON-NLS-2$
        {
//            System.err.println(osisID + ':' + input);
            input = input.replace("God-ward", "God\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Sam.19.4")) //$NON-NLS-1$
        {
            input = input.replace("thee-ward", "thee\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Ps.49.5") || osisID.equals("Eph.1.19") || osisID.equals("2Pet.3.9")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            input = input.replace("us-ward", "us\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Song.5.10")) //$NON-NLS-1$
        {
            input = input.replace("standard-bearer", "standard\u2010bearer"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Jer.22.14")) //$NON-NLS-1$
        {
            input = input.replace("through-aired", "through\u2010aired"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Acts.13.6")) //$NON-NLS-1$
        {
            input = input.replace("Bar-jesus", "Bar\u2010jesus"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Rom.8.17")) //$NON-NLS-1$
        {
            input = input.replace("joint-heirs", "joint\u2010heirs"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("2Cor.1.12") || osisID.equals("2Cor.13.3")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            input = input.replace("you-ward", "you\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("Eph.3.2")) //$NON-NLS-1$
        {
            input = input.replace("youward", "you\u2010ward"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (osisID.equals("1Thess.1.8")) //$NON-NLS-1$
        {
            input = input.replace("</w>-<w", "</w>\u2010<w"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // The following are confirmed
        if (input.contains("Abednego")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbednego\\b", "Abed\u2013nego"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abelbethmaachah")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelbethmaachah\\b", "Abel\u2013beth\u2013maachah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abelmaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelmaim\\b", "Abel\u2013maim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abelmeholah")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelmeholah\\b", "Abel\u2013meholah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abelmizraim")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelmizraim\\b", "Abel\u2013mizraim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abelshittim")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbelshittim\\b", "Abel\u2013shittim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abialbon")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbialbon\\b", "Abi\u2013albon"); //$NON-NLS-1$ //$NON-NLS-2$
        if ((osisID.equals("Judg.6.34") || //$NON-NLS-1$
             osisID.equals("Judg.8.2") || //$NON-NLS-1$
             osisID.equals("1Chr.11.28") || //$NON-NLS-1$
             osisID.equals("1Chr.27.12") //$NON-NLS-1$
             )&& input.contains("Abiezer")) //$NON-NLS-1$
        {
            input = input.replaceAll("\\bAbiezer\\b", "Abi\u2013ezer"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (input.contains("Abiezrites")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbiezrites\\b", "Abi\u2013ezrites"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Abiezrite")) //$NON-NLS-1$
            input = input.replaceAll("\\bAbiezrite\\b", "Abi\u2013ezrite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Adonibezek")) //$NON-NLS-1$
            input = input.replaceAll("\\bAdonibezek\\b", "Adoni\u2013bezek"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Adonizedek")) //$NON-NLS-1$
            input = input.replaceAll("\\bAdonizedek\\b", "Adoni\u2013zedek"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Allonbachuth")) //$NON-NLS-1$
            input = input.replaceAll("\\bAllonbachuth\\b", "Allon\u2013bachuth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Almondiblathaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bAlmondiblathaim\\b", "Almon\u2013diblathaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ashdothpisgah")) //$NON-NLS-1$
            input = input.replaceAll("\\bAshdothpisgah\\b", "Ashdoth\u2013pisgah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Atarothadar")) //$NON-NLS-1$
            input = input.replaceAll("\\bAtarothadar\\b", "Ataroth\u2013adar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Atarothaddar")) //$NON-NLS-1$
            input = input.replaceAll("\\bAtarothaddar\\b", "Ataroth\u2013addar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Aznothtabor")) //$NON-NLS-1$
            input = input.replaceAll("\\bAznothtabor\\b", "Aznoth\u2013tabor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalathbeer")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalathbeer\\b", "Baalath\u2013beer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalberith")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalberith\\b", "Baal\u2013berith"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalgad")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalgad\\b", "Baal\u2013gad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalhamon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhamon\\b", "Baal\u2013hamon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalhanan")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhanan\\b", "Baal\u2013hanan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalhazor")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhazor\\b", "Baal\u2013hazor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalhermon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalhermon\\b", "Baal\u2013hermon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalmeon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalmeon\\b", "Baal\u2013meon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalpeor")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalpeor\\b", "Baal\u2013peor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalperazim")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalperazim\\b", "Baal\u2013perazim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalshalisha")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalshalisha\\b", "Baal\u2013shalisha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baaltamar")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaaltamar\\b", "Baal\u2013tamar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalzebub")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalzebub\\b", "Baal\u2013zebub"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Baalzephon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBaalzephon\\b", "Baal\u2013zephon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bamothbaal")) //$NON-NLS-1$
            input = input.replaceAll("\\bBamothbaal\\b", "Bamoth\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bashanhavothjair")) //$NON-NLS-1$
            input = input.replaceAll("\\bBashanhavothjair\\b", "Bashan\u2013havoth\u2013jair"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bathrabbim")) //$NON-NLS-1$
            input = input.replaceAll("\\bBathrabbim\\b", "Bath\u2013rabbim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bathsheba")) //$NON-NLS-1$
            input = input.replaceAll("\\bBathsheba\\b", "Bath\u2013sheba"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bathshua")) //$NON-NLS-1$
            input = input.replaceAll("\\bBathshua\\b", "Bath\u2013shua"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Beerelim")) //$NON-NLS-1$
            input = input.replaceAll("\\bBeerelim\\b", "Beer\u2013elim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Beerlahairoi")) //$NON-NLS-1$
            input = input.replaceAll("\\bBeerlahairoi\\b", "Beer\u2013lahai\u2013roi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Beersheba")) //$NON-NLS-1$
            input = input.replaceAll("\\bBeersheba\\b", "Beer\u2013sheba"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Beeshterah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBeeshterah\\b", "Beesh\u2013terah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benammi")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenammi\\b", "Ben\u2013ammi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Beneberak")) //$NON-NLS-1$
            input = input.replaceAll("\\bBeneberak\\b", "Bene\u2013berak"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benejaakan")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenejaakan\\b", "Bene\u2013jaakan"); //$NON-NLS-1$ //$NON-NLS-2$

        // Unconfirmed
        if (input.contains("Benhadad")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhadad\\b", "Ben\u2013hadad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benhail")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhail\\b", "Ben\u2013hail"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benhanan")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhanan\\b", "Ben\u2013hanan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benoni")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenoni\\b", "Ben\u2013oni"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benzoheth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenzoheth\\b", "Ben\u2013zoheth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Berodachbaladan")) //$NON-NLS-1$
            input = input.replaceAll("\\bBerodachbaladan\\b", "Berodach\u2013baladan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethanath")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethanath\\b", "Beth\u2013anath"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethanoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethanoth\\b", "Beth\u2013anoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Betharabah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBetharabah\\b", "Beth\u2013arabah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Betharam")) //$NON-NLS-1$
            input = input.replaceAll("\\bBetharam\\b", "Beth\u2013aram"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Betharbel")) //$NON-NLS-1$
            input = input.replaceAll("\\bBetharbel\\b", "Beth\u2013arbel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethaven")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethaven\\b", "Beth\u2013aven"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethazmaveth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethazmaveth\\b", "Beth\u2013azmaveth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethbaalmeon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethbaalmeon\\b", "Beth\u2013baal\u2013meon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethbarah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethbarah\\b", "Beth\u2013barah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethbirei")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethbirei\\b", "Beth\u2013birei"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethcar")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethcar\\b", "Beth\u2013car"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethdagon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethdagon\\b", "Beth\u2013dagon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethdiblathaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethdiblathaim\\b", "Beth\u2013diblathaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethel")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethel\\b", "Beth\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethemek")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethemek\\b", "Beth\u2013emek"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethezel")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethezel\\b", "Beth\u2013ezel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethgader")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethgader\\b", "Beth\u2013gader"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethgamul")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethgamul\\b", "Beth\u2013gamul"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethhaccerem")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhaccerem\\b", "Beth\u2013haccerem"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethharan")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethharan\\b", "Beth\u2013haran"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethhoglah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhoglah\\b", "Beth\u2013hoglah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethhogla")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhogla\\b", "Beth\u2013hogla"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethhoron")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethhoron\\b", "Beth\u2013horon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethjeshimoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethjeshimoth\\b", "Beth\u2013jeshimoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethjesimoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethjesimoth\\b", "Beth\u2013jesimoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethlebaoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlebaoth\\b", "Beth\u2013lebaoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethlehemjudah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlehemjudah\\b", "Beth\u2013lehem\u2013judah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethlehem") && SwordConstants.getTestament(v) == SwordConstants.TESTAMENT_OLD) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlehem\\b", "Beth\u2013lehem"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethmaachah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethmaachah\\b", "Beth\u2013maachah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethmarcaboth")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethmarcaboth\\b", "Beth\u2013marcaboth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethmeon")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethmeon\\b", "Beth\u2013meon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethnimrah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethnimrah\\b", "Beth\u2013nimrah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethpalet")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethpalet\\b", "Beth\u2013palet"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethpazzez")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethpazzez\\b", "Beth\u2013pazzez"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethpeor")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethpeor\\b", "Beth\u2013peor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethphelet")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethphelet\\b", "Beth\u2013phelet"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethrapha")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethrapha\\b", "Beth\u2013rapha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethrehob")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethrehob\\b", "Beth\u2013rehob"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethshan")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshan\\b", "Beth\u2013shan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethshean")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshean\\b", "Beth\u2013shean"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethshemesh")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshemesh\\b", "Beth\u2013shemesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethshemite")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshemite\\b", "Beth\u2013shemite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethshittah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethshittah\\b", "Beth\u2013shittah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethtappuah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethtappuah\\b", "Beth\u2013tappuah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethzur")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethzur\\b", "Beth\u2013zur"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Calebephratah")) //$NON-NLS-1$
            input = input.replaceAll("\\bCalebephratah\\b", "Caleb\u2013ephratah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Chepharhaammonai")) //$NON-NLS-1$
            input = input.replaceAll("\\bChepharhaammonai\\b", "Chephar\u2013haammonai"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Chislothtabor")) //$NON-NLS-1$
            input = input.replaceAll("\\bChislothtabor\\b", "Chisloth\u2013tabor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Chorashan")) //$NON-NLS-1$
            input = input.replaceAll("\\bChorashan\\b", "Chor\u2013ashan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Chushanrishathaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bChushanrishathaim\\b", "Chushan\u2013rishathaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Colhozeh")) //$NON-NLS-1$
            input = input.replaceAll("\\bColhozeh\\b", "Col\u2013hozeh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Danjaan")) //$NON-NLS-1$
            input = input.replaceAll("\\bDanjaan\\b", "Dan\u2013jaan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Dibongad")) //$NON-NLS-1$
            input = input.replaceAll("\\bDibongad\\b", "Dibon\u2013gad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ebedmelech")) //$NON-NLS-1$
            input = input.replaceAll("\\bEbedmelech\\b", "Ebed\u2013melech"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ebenezer")) //$NON-NLS-1$
            input = input.replaceAll("\\bEbenezer\\b", "Eben\u2013ezer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Elbethel")) //$NON-NLS-1$
            input = input.replaceAll("\\bElbethel\\b", "El\u2013beth\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Elelohe-Israel")) //$NON-NLS-1$
            input = input.replaceAll("\\bElelohe-Israel\\b", "El\u2013elohe\u2013Israel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Elonbethhanan")) //$NON-NLS-1$
            input = input.replaceAll("\\bElonbethhanan\\b", "Elon\u2013beth\u2013hanan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Elparan")) //$NON-NLS-1$
            input = input.replaceAll("\\bElparan\\b", "El\u2013paran"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Eneglaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bEneglaim\\b", "En\u2013eglaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Engannim")) //$NON-NLS-1$
            input = input.replaceAll("\\bEngannim\\b", "En\u2013gannim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Engedi")) //$NON-NLS-1$
            input = input.replaceAll("\\bEngedi\\b", "En\u2013gedi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enhaddah")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnhaddah\\b", "En\u2013haddah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enhakkore")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnhakkore\\b", "En\u2013hakkore"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enhazor")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnhazor\\b", "En\u2013hazor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enmishpat")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnmishpat\\b", "En\u2013mishpat"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enrimmon")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnrimmon\\b", "En\u2013rimmon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enrogel")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnrogel\\b", "En\u2013rogel"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Enshemesh")) //$NON-NLS-1$
            input = input.replaceAll("\\bEnshemesh\\b", "En\u2013shemesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Entappuah")) //$NON-NLS-1$
            input = input.replaceAll("\\bEntappuah\\b", "En\u2013tappuah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ephesdammim")) //$NON-NLS-1$
            input = input.replaceAll("\\bEphesdammim\\b", "Ephes\u2013dammim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Esarhaddon")) //$NON-NLS-1$
            input = input.replaceAll("\\bEsarhaddon\\b", "Esar\u2013haddon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Eshbaal")) //$NON-NLS-1$
            input = input.replaceAll("\\bEshbaal\\b", "Esh\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Evilmerodach")) //$NON-NLS-1$
            input = input.replaceAll("\\bEvilmerodach\\b", "Evil\u2013merodach"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Eziongaber")) //$NON-NLS-1$
            input = input.replaceAll("\\bEziongaber\\b", "Ezion\u2013gaber"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Eziongeber")) //$NON-NLS-1$
            input = input.replaceAll("\\bEziongeber\\b", "Ezion\u2013geber"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Gathhepher")) //$NON-NLS-1$
            input = input.replaceAll("\\bGathhepher\\b", "Gath\u2013hepher"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Gathrimmon")) //$NON-NLS-1$
            input = input.replaceAll("\\bGathrimmon\\b", "Gath\u2013rimmon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Gibeah-haaraloth")) //$NON-NLS-1$
            input = input.replaceAll("\\bGibeah-haaraloth\\b", "Gibeah\u2013haaraloth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Gittahhepher")) //$NON-NLS-1$
            input = input.replaceAll("\\bGittahhepher\\b", "Gittah\u2013hepher"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Gurbaal")) //$NON-NLS-1$
            input = input.replaceAll("\\bGurbaal\\b", "Gur\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hamathzobah")) //$NON-NLS-1$
            input = input.replaceAll("\\bHamathzobah\\b", "Hamath\u2013zobah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hammothdor")) //$NON-NLS-1$
            input = input.replaceAll("\\bHammothdor\\b", "Hammoth\u2013dor"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hamongog")) //$NON-NLS-1$
            input = input.replaceAll("\\bHamongog\\b", "Hamon\u2013gog"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Havothjair")) //$NON-NLS-1$
            input = input.replaceAll("\\bHavothjair\\b", "Havoth\u2013jair"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazaraddar")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazaraddar\\b", "Hazar\u2013addar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazarenan")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarenan\\b", "Hazar\u2013enan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazargaddah")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazargaddah\\b", "Hazar\u2013gaddah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazarhatticon")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarhatticon\\b", "Hazar\u2013hatticon"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
//        if (input.contains("Hazarmaveth")) //$NON-NLS-1$
//          input = input.replaceAll("\\bHazarmaveth\\b", "Hazar\u2013maveth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazarshual")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarshual\\b", "Hazar\u2013shual"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazarsusah")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarsusah\\b", "Hazar\u2013susah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazarsusim")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazarsusim\\b", "Hazar\u2013susim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazazontamar")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazazontamar\\b", "Hazazon\u2013tamar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hazezontamar")) //$NON-NLS-1$
            input = input.replaceAll("\\bHazezontamar\\b", "Hazezon\u2013tamar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Helkathhazzurim")) //$NON-NLS-1$
            input = input.replaceAll("\\bHelkathhazzurim\\b", "Helkath\u2013hazzurim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hephzibah")) //$NON-NLS-1$
            input = input.replaceAll("\\bHephzibah\\b", "Hephzi\u2013bah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Horhagidgad")) //$NON-NLS-1$
            input = input.replaceAll("\\bHorhagidgad\\b", "Hor\u2013hagidgad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ichabod")) //$NON-NLS-1$
            input = input.replaceAll("\\bIchabod\\b", "I\u2013chabod"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ijeabarim")) //$NON-NLS-1$
            input = input.replaceAll("\\bIjeabarim\\b", "Ije\u2013abarim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Irnahash")) //$NON-NLS-1$
            input = input.replaceAll("\\bIrnahash\\b", "Ir\u2013nahash"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Irshemesh")) //$NON-NLS-1$
            input = input.replaceAll("\\bIrshemesh\\b", "Ir\u2013shemesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ishbibenob")) //$NON-NLS-1$
            input = input.replaceAll("\\bIshbibenob\\b", "Ishbi\u2013benob"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ishbosheth")) //$NON-NLS-1$
            input = input.replaceAll("\\bIshbosheth\\b", "Ish\u2013bosheth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ishtob")) //$NON-NLS-1$
            input = input.replaceAll("\\bIshtob\\b", "Ish\u2013tob"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ittahkazin")) //$NON-NLS-1$
            input = input.replaceAll("\\bIttahkazin\\b", "Ittah\u2013kazin"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jaareoregim")) //$NON-NLS-1$
            input = input.replaceAll("\\bJaareoregim\\b", "Jaare\u2013oregim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jabeshgilead")) //$NON-NLS-1$
            input = input.replaceAll("\\bJabeshgilead\\b", "Jabesh\u2013gilead"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jashubilehem")) //$NON-NLS-1$
            input = input.replaceAll("\\bJashubilehem\\b", "Jashubi\u2013lehem"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jegarsahadutha")) //$NON-NLS-1$
            input = input.replaceAll("\\bJegarsahadutha\\b", "Jegar\u2013sahadutha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jehovahjireh")) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahjireh\\b", "Jehovah\u2013jireh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jehovahnissi")) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahnissi\\b", "Jehovah\u2013nissi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jehovahshalom")) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahshalom\\b", "Jehovah\u2013shalom"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jiphthahel")) //$NON-NLS-1$
            input = input.replaceAll("\\bJiphthahel\\b", "Jiphthah\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jushabhesed")) //$NON-NLS-1$
            input = input.replaceAll("\\bJushabhesed\\b", "Jushab\u2013hesed"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kadeshbarnea")) //$NON-NLS-1$
            input = input.replaceAll("\\bKadeshbarnea\\b", "Kadesh\u2013barnea"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kedeshnaphtali")) //$NON-NLS-1$
            input = input.replaceAll("\\bKedeshnaphtali\\b", "Kedesh\u2013naphtali"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kerenhappuch")) //$NON-NLS-1$
            input = input.replaceAll("\\bKerenhappuch\\b", "Keren\u2013happuch"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kibrothhattaavah")) //$NON-NLS-1$
            input = input.replaceAll("\\bKibrothhattaavah\\b", "Kibroth\u2013hattaavah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirharaseth")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirharaseth\\b", "Kir\u2013haraseth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirhareseth")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirhareseth\\b", "Kir\u2013hareseth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirharesh")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirharesh\\b", "Kir\u2013haresh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirheres")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirheres\\b", "Kir\u2013heres"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjatharba")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjatharba\\b", "Kirjath\u2013arba"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjatharim")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjatharim\\b", "Kirjath\u2013arim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjathbaal")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathbaal\\b", "Kirjath\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjathhuzoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathhuzoth\\b", "Kirjath\u2013huzoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjathjearim")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathjearim\\b", "Kirjath\u2013jearim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjathsannah")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathsannah\\b", "Kirjath\u2013sannah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Kirjathsepher")) //$NON-NLS-1$
            input = input.replaceAll("\\bKirjathsepher\\b", "Kirjath\u2013sepher"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Lahairoi")) //$NON-NLS-1$
            input = input.replaceAll("\\bLahairoi\\b", "Lahai\u2013roi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Loammi")) //$NON-NLS-1$
            input = input.replaceAll("\\bLoammi\\b", "Lo\u2013ammi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Lodebar")) //$NON-NLS-1$
            input = input.replaceAll("\\bLodebar\\b", "Lo\u2013debar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Loruhamah")) //$NON-NLS-1$
            input = input.replaceAll("\\bLoruhamah\\b", "Lo\u2013ruhamah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Maalehacrabbim")) //$NON-NLS-1$
            input = input.replaceAll("\\bMaalehacrabbim\\b", "Maaleh\u2013acrabbim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Magormissabib")) //$NON-NLS-1$
            input = input.replaceAll("\\bMagormissabib\\b", "Magor\u2013missabib"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Mahanehdan")) //$NON-NLS-1$
            input = input.replaceAll("\\bMahanehdan\\b", "Mahaneh\u2013dan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Mahershalalhashbaz")) //$NON-NLS-1$
            input = input.replaceAll("\\bMahershalalhashbaz\\b", "Maher\u2013shalal\u2013hash\u2013baz"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Maher–shalal–hash–baz")) //$NON-NLS-1$
            input = input.replaceAll("\\bMaher–shalal–hash–baz\\b", "Maher\u2013shalal\u2013hash\u2013baz"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Malchishua")) //$NON-NLS-1$
            input = input.replaceAll("\\bMalchishua\\b", "Malchi\u2013shua"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Mejarkon")) //$NON-NLS-1$
            input = input.replaceAll("\\bMejarkon\\b", "Me\u2013jarkon"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Melchishua")) //$NON-NLS-1$
            input = input.replaceAll("\\bMelchishua\\b", "Melchi\u2013shua"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Meribah-Kadesh")) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribah-Kadesh\\b", "Meribah\u2013Kadesh"); //$NON-NLS-1$ //$NON-NLS-2$

        // Confirmed
        if (input.contains("Meribaal")) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribaal\\b", "Meri\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Meribbaal")) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribbaal\\b", "Merib\u2013baal"); //$NON-NLS-1$ //$NON-NLS-2$

        if (input.contains("Merodachbaladan")) //$NON-NLS-1$
            input = input.replaceAll("\\bMerodachbaladan\\b", "Merodach\u2013baladan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Methegammah")) //$NON-NLS-1$
            input = input.replaceAll("\\bMethegammah\\b", "Metheg\u2013ammah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Migdalel")) //$NON-NLS-1$
            input = input.replaceAll("\\bMigdalel\\b", "Migdal\u2013el"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Migdalgad")) //$NON-NLS-1$
            input = input.replaceAll("\\bMigdalgad\\b", "Migdal\u2013gad"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Misrephothmaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bMisrephothmaim\\b", "Misrephoth\u2013maim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Moreshethgath")) //$NON-NLS-1$
            input = input.replaceAll("\\bMoreshethgath\\b", "Moresheth\u2013gath"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Nathanmelech")) //$NON-NLS-1$
            input = input.replaceAll("\\bNathanmelech\\b", "Nathan\u2013melech"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Nebuzaradan")) //$NON-NLS-1$
            input = input.replaceAll("\\bNebuzaradan\\b", "Nebuzar\u2013adan"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Nergalsharezer")) //$NON-NLS-1$
            input = input.replaceAll("\\bNergalsharezer\\b", "Nergal\u2013sharezer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Obededom")) //$NON-NLS-1$
            input = input.replaceAll("\\bObededom\\b", "Obed\u2013edom"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Padanaram")) //$NON-NLS-1$
            input = input.replaceAll("\\bPadanaram\\b", "Padan\u2013aram"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pahathmoab")) //$NON-NLS-1$
            input = input.replaceAll("\\bPahathmoab\\b", "Pahath\u2013moab"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pasdammim")) //$NON-NLS-1$
            input = input.replaceAll("\\bPasdammim\\b", "Pas\u2013dammim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Perezuzzah")) //$NON-NLS-1$
            input = input.replaceAll("\\bPerezuzzah\\b", "Perez\u2013uzzah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Perezuzza")) //$NON-NLS-1$
            input = input.replaceAll("\\bPerezuzza\\b", "Perez\u2013uzza"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pharaohhophra")) //$NON-NLS-1$
            input = input.replaceAll("\\bPharaohhophra\\b", "Pharaoh\u2013hophra"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pharaohnechoh")) //$NON-NLS-1$
            input = input.replaceAll("\\bPharaohnechoh\\b", "Pharaoh\u2013nechoh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pharaohnecho")) //$NON-NLS-1$
            input = input.replaceAll("\\bPharaohnecho\\b", "Pharaoh\u2013necho"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pibeseth")) //$NON-NLS-1$
            input = input.replaceAll("\\bPibeseth\\b", "Pi\u2013beseth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Pihahiroth")) //$NON-NLS-1$
            input = input.replaceAll("\\bPihahiroth\\b", "Pi\u2013hahiroth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Potipherah")) //$NON-NLS-1$
            input = input.replaceAll("\\bPotipherah\\b", "Poti\u2013pherah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Rabsaris") && !osisID.equals("2Kgs.18.17")) //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("\\bRabsaris\\b", "Rab\u2013saris"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Rabshakeh") && osisID.startsWith("2Kgs")) //$NON-NLS-1$ //$NON-NLS-2$
            input = input.replaceAll("\\bRabshakeh\\b", "Rab\u2013shakeh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ramathaimzophim")) //$NON-NLS-1$
            input = input.replaceAll("\\bRamathaimzophim\\b", "Ramathaim\u2013zophim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ramathlehi")) //$NON-NLS-1$
            input = input.replaceAll("\\bRamathlehi\\b", "Ramath\u2013lehi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ramathmizpeh")) //$NON-NLS-1$
            input = input.replaceAll("\\bRamathmizpeh\\b", "Ramath\u2013mizpeh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ramothgilead")) //$NON-NLS-1$
            input = input.replaceAll("\\bRamothgilead\\b", "Ramoth\u2013gilead"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Regemmelech")) //$NON-NLS-1$
            input = input.replaceAll("\\bRegemmelech\\b", "Regem\u2013melech"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Remmonmethoar")) //$NON-NLS-1$
            input = input.replaceAll("\\bRemmonmethoar\\b", "Remmon\u2013methoar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Rimmonparez")) //$NON-NLS-1$
            input = input.replaceAll("\\bRimmonparez\\b", "Rimmon\u2013parez"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Romamtiezer")) //$NON-NLS-1$
            input = input.replaceAll("\\bRomamtiezer\\b", "Romamti\u2013ezer"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Ruhamah")) //$NON-NLS-1$
            input = input.replaceAll("\\bRuhamah\\b", "Ru\u2013hamah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Samgarnebo")) //$NON-NLS-1$
            input = input.replaceAll("\\bSamgarnebo\\b", "Samgar\u2013nebo"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Selahammahlekoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bSelahammahlekoth\\b", "Sela\u2013hammahlekoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Shearjashub")) //$NON-NLS-1$
            input = input.replaceAll("\\bShearjashub\\b", "Shear\u2013jashub"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Shetharboznai")) //$NON-NLS-1$
            input = input.replaceAll("\\bShetharboznai\\b", "Shethar\u2013boznai"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Shihorlibnath")) //$NON-NLS-1$
            input = input.replaceAll("\\bShihorlibnath\\b", "Shihor\u2013libnath"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Shimronmeron")) //$NON-NLS-1$
            input = input.replaceAll("\\bShimronmeron\\b", "Shimron\u2013meron"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Succothbenoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bSuccothbenoth\\b", "Succoth\u2013benoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Syriadamascus")) //$NON-NLS-1$
            input = input.replaceAll("\\bSyriadamascus\\b", "Syria\u2013damascus"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Syriamaachah")) //$NON-NLS-1$
            input = input.replaceAll("\\bSyriamaachah\\b", "Syria\u2013maachah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Taanathshiloh")) //$NON-NLS-1$
            input = input.replaceAll("\\bTaanathshiloh\\b", "Taanath\u2013shiloh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Tahtimhodshi")) //$NON-NLS-1$
            input = input.replaceAll("\\bTahtimhodshi\\b", "Tahtim\u2013hodshi"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Telabib")) //$NON-NLS-1$
            input = input.replaceAll("\\bTelabib\\b", "Tel\u2013abib"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Telharesha")) //$NON-NLS-1$
            input = input.replaceAll("\\bTelharesha\\b", "Tel\u2013haresha"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Telharsa")) //$NON-NLS-1$
            input = input.replaceAll("\\bTelharsa\\b", "Tel\u2013harsa"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Telmelah")) //$NON-NLS-1$
            input = input.replaceAll("\\bTelmelah\\b", "Tel\u2013melah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Tiglathpileser")) //$NON-NLS-1$
            input = input.replaceAll("\\bTiglathpileser\\b", "Tiglath\u2013pileser"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Tilgathpilneser")) //$NON-NLS-1$
            input = input.replaceAll("\\bTilgathpilneser\\b", "Tilgath\u2013pilneser"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Timnathheres")) //$NON-NLS-1$
            input = input.replaceAll("\\bTimnathheres\\b", "Timnath\u2013heres"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Timnathserah")) //$NON-NLS-1$
            input = input.replaceAll("\\bTimnathserah\\b", "Timnath\u2013serah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Tobadonijah")) //$NON-NLS-1$
            input = input.replaceAll("\\bTobadonijah\\b", "Tob\u2013adonijah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Tubalcain")) //$NON-NLS-1$
            input = input.replaceAll("\\bTubalcain\\b", "Tubal\u2013cain"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Uzzensherah")) //$NON-NLS-1$
            input = input.replaceAll("\\bUzzensherah\\b", "Uzzen\u2013sherah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Zarethshahar")) //$NON-NLS-1$
            input = input.replaceAll("\\bZarethshahar\\b", "Zareth\u2013shahar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Zaphnathpaaneah")) //$NON-NLS-1$
            input = input.replaceAll("\\bZaphnathpaaneah\\b", "Zaphnath\u2013paaneah"); //$NON-NLS-1$ //$NON-NLS-2$

        // The following are confirmed
        if (input.contains("Altaschith")) //$NON-NLS-1$
            input = input.replaceAll("\\bAltaschith\\b", "Al\u2013taschith"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Aramnaharaim")) //$NON-NLS-1$
            input = input.replaceAll("\\bAramnaharaim\\b", "Aramnaharaim"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Aramzobah")) //$NON-NLS-1$
            input = input.replaceAll("\\bAramzobah\\b", ""); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jonathelemrechokim")) //$NON-NLS-1$
            input = input.replaceAll("\\bJonathelemrechokim\\b", "Jonath\u2013elem\u2013rechokim"); //$NON-NLS-1$ //$NON-NLS-2$

        // This next block are unconfirmed and are in margin notes
        if (input.contains("Bathshuah")) //$NON-NLS-1$
            input = input.replaceAll("\\bBathshuah\\b", "Bath\u2013shuah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benabinadab")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenabinadab\\b", "Ben\u2013abinadab"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bendekar")) //$NON-NLS-1$
            input = input.replaceAll("\\bBendekar\\b", "Ben\u2013dekar"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bengeber")) //$NON-NLS-1$
            input = input.replaceAll("\\bBengeber\\b", "Ben\u2013geber"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benhesed")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhesed\\b", "Ben\u2013hesed"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Benhur")) //$NON-NLS-1$
            input = input.replaceAll("\\bBenhur\\b", "Ben\u2013hur"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Betheden")) //$NON-NLS-1$
            input = input.replaceAll("\\bBetheden\\b", "Beth\u2013eden"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Hatsihammenuchoth")) //$NON-NLS-1$
            input = input.replaceAll("\\bHatsihammenuchoth\\b", "Hatsi\u2013ham\u2013menuchoth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jehovahshammah")) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahshammah\\b", "Jehovah\u2013shammah"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Jehovahtsidkenu")) //$NON-NLS-1$
            input = input.replaceAll("\\bJehovahtsidkenu\\b", "Jehovaht\u2013sidkenu"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Meribahkadesh")) //$NON-NLS-1$
            input = input.replaceAll("\\bMeribahkadesh\\b", "Meribah\u2013kadesh"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Shoshannimeduth")) //$NON-NLS-1$
            input = input.replaceAll("\\bShoshannimeduth\\b", "Shoshannim\u2013eduth"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Shushaneduth")) //$NON-NLS-1$
            input = input.replaceAll("\\bShushaneduth\\b", "Shushan\u2013eduth"); //$NON-NLS-1$ //$NON-NLS-2$

        // The following are confirmed.
//        if (input.contains("Amminadib")) //$NON-NLS-1$
//            input = input.replaceAll("\\bAmminadib\\b", "Ammi\u2013nadib"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethelite")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethelite\\b", "Beth\u2013elite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Bethlehemite")) //$NON-NLS-1$
            input = input.replaceAll("\\bBethlehemite\\b", "Beth\u2013lehemite"); //$NON-NLS-1$ //$NON-NLS-2$
        if (input.contains("Rabmag")) //$NON-NLS-1$
            input = input.replaceAll("\\bRabmag\\b", "Rab\u2013mag"); //$NON-NLS-1$ //$NON-NLS-2$
//        if (input.contains("Endor")) //$NON-NLS-1$
//            input = input.replaceAll("\\bEndor\\b", "En\u2013dor"); //$NON-NLS-1$ //$NON-NLS-2$
                        
        return input;
    }

    private String fixDivineName(String osisID, String input)
    {
        if (input.contains("divineName")) //$NON-NLS-1$
        {
            StringBuffer buf = new StringBuffer();
            Matcher matcher = divineNamePattern.matcher(input);
            while (matcher.find())
            {
                matcher.appendReplacement(buf, divineNameReplace);
//                if (osisID.equals("Gen.4.15"))
//                {
//                    System.err.println(osisID + ':' + buf);
//                }
            }
            matcher.appendTail(buf);
//            if (osisID.equals("Gen.4.15"))
//            {
//                System.err.println(osisID + ':' + buf);
//            }
            input = buf.toString();
        }

        if (input.contains("H03069")) //$NON-NLS-1$
        {
            StringBuffer buf = new StringBuffer();
            Matcher matcher = divineNameAnalysisPattern.matcher(input);
            while (matcher.find())
            {
                matcher.appendReplacement(buf, divineGodNameReplace);
            }            
            matcher.appendTail(buf);
            input = buf.toString();
        }

        input = dn1Pattern.matcher(input).replaceAll(dn1Replace);

        input = dn2Pattern.matcher(input).replaceAll(dn2Replace);

        return input;
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

    private static String divineNameElement = "(<divineName.*?>)(.*?)(LORD'|LORD'S|LORD|GOD|JEHOVAH)(.*?)(</divineName>)"; //$NON-NLS-1$
    private static String divineNameReplace = "$2<seg><divineName>$3</divineName></seg>$4"; //$NON-NLS-1$
    private static Pattern divineNamePattern = Pattern.compile(divineNameElement);
    private static Pattern divineNameAnalysisPattern = Pattern.compile("(<w\\s+lemma=\"strong:H03069\"\\s*>.*?)(GOD|LORD)(.*?</w>)"); //$NON-NLS-1$
    private static String divineGodNameReplace = "$1<seg><divineName>$2</divineName></seg>$3"; //$NON-NLS-1$
    private static Pattern dn1Pattern = Pattern.compile("<divineName>LORD</divineName>"); //$NON-NLS-1$
    private static String dn1Replace = "<divineName type=\"x-yhwh\">Lord</divineName>"; //$NON-NLS-1$
    private static Pattern dn2Pattern = Pattern.compile("<divineName>GOD</divineName>"); //$NON-NLS-1$
    private static String dn2Replace = "<divineName type=\"x-yhwh\">God</divineName>"; //$NON-NLS-1$

    private static String transChangeSeg = "<seg type=\"transChange\" subType=\"type:added\">([^<]*)</seg>"; //$NON-NLS-1$
    private static Pattern transChangeSegPattern = Pattern.compile(transChangeSeg);

    private static String badNote = "<note type=\"[^\"]*\" (name=\"([^\"]*)\" date=\"([^\"]*)\"/)>([^<]*)</note>"; //$NON-NLS-1$
    private static Pattern badNotePattern = Pattern.compile(badNote);

    private static String respElement = "<resp.*?name=\"(.*?)\".*?date=\"(.*?)\".*?>"; //$NON-NLS-1$
    private static Pattern respPattern = Pattern.compile(respElement);

    private static String wElement = "<w\\s[^>]*>"; //$NON-NLS-1$
    private static Pattern wPattern = Pattern.compile(wElement);
    private static Pattern srcPattern = Pattern.compile("src=\"([^\"]*)\""); //$NON-NLS-1$
    private static Pattern morphNPattern = Pattern.compile("morph=\"robinson:N-([^\"]*)\""); //$NON-NLS-1$
    private static Pattern morphTPattern = Pattern.compile("morph=\"robinson:T-([^\"]*)\""); //$NON-NLS-1$

    private static String nameDate = "type=\"strongsMarkup\"[ ]+name=\"([^\"]*)\"[ ]+date=\"([^\"]*)\""; //$NON-NLS-1$
    private static Pattern nameDatePattern = Pattern.compile(nameDate);

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
    private static Pattern wnPattern = Pattern.compile("\\s\\s+"); //$NON-NLS-1$
    private static Pattern p1Pattern = Pattern.compile("\\.\\.\\."); //$NON-NLS-1$

    private static Pattern hyphenatedNamePattern = Pattern.compile("\\w+[a-z](-)\\w\\w+"); //$NON-NLS-1$
    
    private static Map<String, String> bookTitles = new HashMap<String, String>();
    
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
        bookTitles.put("Matt", "THE GOSPEL ACCORDING TO ST. MATTHEW"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Mark", "THE GOSPEL ACCORDING TO ST. MARK"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("Luke", "THE GOSPEL ACCORDING TO ST. LUKE"); //$NON-NLS-1$ //$NON-NLS-2$
        bookTitles.put("John", "THE GOSPEL ACCORDING TO ST. JOHN"); //$NON-NLS-1$ //$NON-NLS-2$
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
        bookTitles.put("Rev", "THE REVELATION OF ST. JOHN THE DIVINE"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static Map<String, String> colophons = new HashMap<String, String>();
    
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

    private static Map<String, String> acrostics = new HashMap<String, String>();
    
    static {
        acrostics.put("Ps.119.1", "ALEPH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.9", "BETH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.17", "GIMEL."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.25", "DALETH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.33", "HE."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.41", "VAU."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.49", "ZAIN."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.57", "CHETH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.65", "TETH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.73", "JOD."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.81", "CAPH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.89", "LAMED."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.97", "MEM."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.105", "NUN."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.113", "SAMECH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.121", "AIN."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.129", "PE."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.137", "TZADDI."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.145", "KOPH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.153", "RESH."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.161", "SCHIN."); //$NON-NLS-1$ //$NON-NLS-2$
        acrostics.put("Ps.119.169", "TAU."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private boolean moveP = false;

    private Writer writer;
    private String filename;
}
