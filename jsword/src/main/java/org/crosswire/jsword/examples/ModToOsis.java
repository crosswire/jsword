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
import java.util.Iterator;
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
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

public class ModToOsis
{
    /**
     * The name of a Bible to find
     */
    private static final String BIBLE_NAME = "KJV"; //$NON-NLS-1$
    private static final String BIBLE_RANGE = "Gen-Rev"; //$NON-NLS-1$
    private static final boolean BY_CHAPTER = true;

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new ModToOsis().dump(BIBLE_NAME, BIBLE_RANGE);
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
            Key key = bible.getKey(range);

            openOutputFile(bmd.getInitials(), !BY_CHAPTER);
            buildDocumentOpen(buf, bmd, range, !BY_CHAPTER);
            if (!BY_CHAPTER)
            {
                writeDocument(buf);
            }

            // Get a verse iterator
            Iterator iter = key.iterator();
            while (iter.hasNext())
            {
                Verse verse = (Verse) iter.next();
                String raw = bible.getRawData(verse);
                String osisID = verse.getOsisID();

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
                        buildBookClose(buf);
                        buildDocumentClose(buf, BY_CHAPTER);
                        openOutputFile(lastBookName, BY_CHAPTER);
                        writeDocument(buf);
                        closeOutputFile(BY_CHAPTER);
                    }

                    buf = new StringBuffer();
                    buildDocumentOpen(buf, bmd, currentBookName, BY_CHAPTER);
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
                boolean foundPreVerse = false;
                String preVerseText = ""; //$NON-NLS-1$
                if (raw.contains(preVerseStart))
                {
                    Matcher preVerseStartMatcher = preVerseStartPattern.matcher(raw);
                    if (preVerseStartMatcher.find())
                    {
                        int start = preVerseStartMatcher.start();
                        Matcher preVerseEndMatcher = preVerseEndPattern.matcher(raw);
                        if (preVerseEndMatcher.find(1 + preVerseStartMatcher.end()))
                        {
                            int end = preVerseEndMatcher.end();
                            foundPreVerse = true;
                            preVerseText = raw.substring(start, end);
                            raw = raw.replace(preVerseText, ""); //$NON-NLS-1$
                            preVerseText = preVerseText.substring(preVerseStart.length(), preVerseText.length()-preVerseEnd.length());
                        }
                    }
                }
                if (foundPreVerse)
                {
                    if (inPreVerse)
                    {
                        buildPreVerseClose(buf);
                    }
                    buildPreVerseOpen(buf, cleanup(osisID, preVerseText)); //$NON-NLS-1$
                    inPreVerse = true;
                }

                buildVerseOpen(buf, osisID);
                buf.append(cleanup(osisID, raw));
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
            buildBookClose(buf);
            buildDocumentClose(buf, true);
            openOutputFile(lastBookName, BY_CHAPTER);
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
        int b, sumb = 0;
        for (int i = 0, more = -1; i < l; i++ )
        {
            /* Get next byte b from URL segment s */
            switch (ch = s.charAt(i))
            {
              case '%':
                ch = s.charAt( ++i);
                int hb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                ch = s.charAt( ++i);
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
                if ( --more == 0)
                {
                    sbuf.append((char) sumb); // Add char to sbuf
                }
            }
            else if ((b & 0x80) == 0x00)
            { // 0xxxxxxx (yields 7 bits)
                sbuf.append((char) b); // Store in sbuf
            }
            else if ((b & 0xe0) == 0xc0)
            { // 110xxxxx (yields 5 bits)
                sumb = b & 0x1f;
                more = 1; // Expect 1 more byte
            }
            else if ((b & 0xf0) == 0xe0)
            { // 1110xxxx (yields 4 bits)
                sumb = b & 0x0f;
                more = 2; // Expect 2 more bytes
            }
            else if ((b & 0xf8) == 0xf0)
            { // 11110xxx (yields 3 bits)
                sumb = b & 0x07;
                more = 3; // Expect 3 more bytes
            }
            else if ((b & 0xfc) == 0xf8)
            { // 111110xx (yields 2 bits)
                sumb = b & 0x03;
                more = 4; // Expect 4 more bytes
            }
            else
            /* if ((b & 0xfe) == 0xfc) */{ // 1111110x (yields 1 bit)
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
        docBuffer.append("\n    <osisText osisIDWork=\"{0}\" osisRefWork=\"defaultReferenceScheme\" xml:lang=\"en\">"); //$NON-NLS-1$
        docBuffer.append("\n    <header>"); //$NON-NLS-1$
        docBuffer.append("\n      <work osisWork=\"{0}\">"); //$NON-NLS-1$
        docBuffer.append("\n      <title>{1}</title>"); //$NON-NLS-1$
        docBuffer.append("\n      <identifier type=\"OSIS\">Bible.{0}</identifier>"); //$NON-NLS-1$
        docBuffer.append("\n      <scope>{2}</scope>"); //$NON-NLS-1$
        docBuffer.append("\n      <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n    </work>"); //$NON-NLS-1$
        docBuffer.append("\n    <work osisWork=\"defaultReferenceScheme\">"); //$NON-NLS-1$
        docBuffer.append("\n      <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n    </work>"); //$NON-NLS-1$
        docBuffer.append("\n  </header>"); //$NON-NLS-1$
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
        MessageFormat msgFormat = new MessageFormat("<div type=\"book\" osisID=\"{0}\">\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] { bookName}, buf, pos);
    }

    private void buildBookClose(StringBuffer buf)
    {
        buf.append("</div>\n"); //$NON-NLS-1$
    }

    private void buildChapterClose(StringBuffer buf)
    {
        buf.append("</chapter>\n"); //$NON-NLS-1$
    }

    private void buildChapterOpen(StringBuffer buf, String bookName, int chapter)
    {
        MessageFormat msgFormat = new MessageFormat("<chapter osisID=\"{0}.{1}\">\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] { bookName, new Integer(chapter)}, buf, pos);
    }

    private void buildPreVerseOpen(StringBuffer buf, String preVerse)
    {
        MessageFormat msgFormat = new MessageFormat("<div type=\"section\"><title>{0}</title>"); //$NON-NLS-1$
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
        MessageFormat msgFormat = new MessageFormat("</verse>"); //$NON-NLS-1$
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

    private String cleanup(String osisID, String input)
    {
        // Fix up bad notes
        MessageFormat noteCleanupFormat = new MessageFormat("<note type=\"x-strongsMarkup\" resp=\"{0} {1}\">{2}</note>"); //$NON-NLS-1$
        while (true)
        {
            if (input.contains("note type=\"strongsMarkup\"")) //$NON-NLS-1$
            {
                Matcher badNoteMatcher = badNotePattern.matcher(input);
                if (!badNoteMatcher.find())
                {
                    System.err.println("This was unexpected!"); //$NON-NLS-1$
                    break;
                }
                input = input.replace(badNoteMatcher.group(),
                                      noteCleanupFormat.format(new Object[] { badNoteMatcher.group(2), badNoteMatcher.group(3), XMLUtil.escape(unescape(badNoteMatcher.group(4)))}));
            }
            else
            {
                break;
            }
        }

        MessageFormat respCleanupFormat = new MessageFormat("<milestone type=\"x-strongsMarkup\" resp=\"{0} {1}\"/>"); //$NON-NLS-1$
        while (true)
        {
            if (input.contains("<resp")) //$NON-NLS-1$
            {
                Matcher respMatcher = respPattern.matcher(input);
                if (!respMatcher.find())
                {
                    System.err.println("This was unexpected!"); //$NON-NLS-1$
                    break;
                }
                String resp = respMatcher.group();

                Matcher nameDateMatcher = nameDatePattern.matcher(resp);
                if (!nameDateMatcher.find())
                {
                    System.err.println("This was unexpected!"); //$NON-NLS-1$
                    break;
                }
                String fixed = respCleanupFormat.format(new Object[] { nameDateMatcher.group(1), nameDateMatcher.group(2)});
                input = input.replace(resp, fixed);
            }
            else
            {
                break;
            }
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
            fixed = fixed.replaceAll("x-StrongsMorph", "morph"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("x-Robinson", "robinson"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("split(ID|id)=\"", "type=\"x-split\" subType=\"x-"); //$NON-NLS-1$ //$NON-NLS-2$
            if ( !whole.equals(fixed))
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
                for (; i < j; i++)
                {
                    System.out.println(osisID + " missing src=" + i); //$NON-NLS-1$
                }
            }
            i++;
        }

        input = input.replaceAll("\"transChange\"", "\"x-transChange\""); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("\"type:", "\"x-"); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("changeType=\"", "type=\""); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("<p/>", "<lb/><lb/><milestone type=\"x-p\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("x-StudyNote", "study"); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll("\\s*</q>", "</q>"); //$NON-NLS-1$ //$NON-NLS-2$
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
            input = input.replace("</q>", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
                        if (split.contains(src))
                        {
                            System.err.println(osisID + " split for src=" + src); //$NON-NLS-1$
                        }
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

        for (Map.Entry<Integer, String> entry: wMap.entrySet())
        {
            if (entry.getValue().contains("G3588")) //$NON-NLS-1$
            {
                
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
        return input;
    }

    private static FieldPosition pos = new FieldPosition(0);

    private static String preVerseStart = "<title subtype=\"x-preverse\" type=\"section\">"; //$NON-NLS-1$
    private static String preVerseEnd = "</title>"; //$NON-NLS-1$
    private static Pattern preVerseStartPattern = Pattern.compile(preVerseStart);
    private static Pattern preVerseEndPattern = Pattern.compile(preVerseEnd); //$NON-NLS-1$

    private static String badNote = "<note type=\"[^\"]*\" (name=\"([^\"]*)\" date=\"([^\"]*)\"/)>([^<]*)</note>"; //$NON-NLS-1$
    private static Pattern badNotePattern = Pattern.compile(badNote);

    private static String respElement = "<resp\\s[^>]*>"; //$NON-NLS-1$
    private static Pattern respPattern = Pattern.compile(respElement);

    private static String wElement = "<w\\s[^>]*>"; //$NON-NLS-1$
    private static Pattern wPattern = Pattern.compile(wElement);
    private static Pattern srcPattern = Pattern.compile("src=\"([^\"]*)\""); //$NON-NLS-1$

    private static String nameDate = "type=\"strongsMarkup\"[ ]+name=\"([^\"]*)\"[ ]+date=\"([^\"]*)\""; //$NON-NLS-1$
    private static Pattern nameDatePattern = Pattern.compile(nameDate);
    
    private Writer writer;
    private String filename;
}
