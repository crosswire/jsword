package org.crosswire.jsword.examples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
        StringBuffer buf = null;
        boolean inPreVerse = false;
        

        try
        {
            Key key = bible.getKey(range);

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
                        buildDocumentClose(buf);
                        writeDocument(buf, lastBookName);
                        XMLProcess parser = new XMLProcess();
                        parser.getFeatures().setFeatureStates("-s", "-f", "-va", "-dv"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        parser.parse(lastBookName + ".xml"); //$NON-NLS-1$
                    }

                    buf = new StringBuffer();
                    buildDocumentOpen(buf, bmd, currentBookName);
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
            buildDocumentClose(buf);
            writeDocument(buf, lastBookName);
            new XMLProcess().parse(lastBookName + ".xml"); //$NON-NLS-1$
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

    private void buildDocumentOpen(StringBuffer buf, BookMetaData bmd, String range)
    {
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
        docBuffer.append("\n      <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
//        docBuffer.append("\n      <scope>{2}</scope>"); //$NON-NLS-1$
        docBuffer.append("\n    </work>"); //$NON-NLS-1$
        docBuffer.append("\n    <work osisWork=\"defaultReferenceScheme\">"); //$NON-NLS-1$
        docBuffer.append("\n      <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n    </work>"); //$NON-NLS-1$
        docBuffer.append("\n  </header>"); //$NON-NLS-1$
        docBuffer.append('\n');
        MessageFormat msgFormat = new MessageFormat(docBuffer.toString()); //$NON-NLS-1$
        msgFormat.format(new Object[] { bmd.getInitials(), bmd.getName(), range }, buf, pos);
    }

    private void buildDocumentClose(StringBuffer buf)
    {
        buf.append("</osisText>\n</osis>\n"); //$NON-NLS-1$
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
        MessageFormat msgFormat = new MessageFormat("<verse sID=\"{0}\" osisID=\"{0}\"/>"); //$NON-NLS-1$
        msgFormat.format(new Object[] { osisID }, buf, pos);
    }

    private void buildVerseClose(StringBuffer buf, String osisID)
    {
        MessageFormat msgFormat = new MessageFormat("<verse eID=\"{0}\"/>"); //$NON-NLS-1$
        msgFormat.format(new Object[] { osisID }, buf, pos);
    }
    
    private void writeDocument(StringBuffer buf, String filename) throws IOException
    {
        Writer writer = new OutputStreamWriter(new FileOutputStream(filename + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
        writer.write(buf.toString());
        writer.close();
    }
    private String cleanup(@SuppressWarnings("unused") String osisID, String input)
    {
        // Fix up bad notes
        while (true)
        {
            if (input.contains("note type=\"strongsMarkup\"")) //$NON-NLS-1$
            {
                Matcher badNoteMatcher = badNotePattern.matcher(input);
                if (!badNoteMatcher.find())
                {
                    break;
                }
                String note = badNoteMatcher.group();
                String fixed = note.substring(0, note.length()-2) + '>';
                fixed = fixed.replaceAll("strongsMarkup", "x-strongsMarkup"); //$NON-NLS-1$ //$NON-NLS-2$
                fixed = fixed.replaceAll(" name=\"[^\"]*\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
                fixed = fixed.replaceAll(" date=\"[^\"]*\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
                input = input.replace(note, fixed);
            }
            else
            {
                break;
            }
        }

        // Fix up bad w tags
        Matcher wMatcher = wPattern.matcher(input);
        while (wMatcher.find())
        {
            String whole = wMatcher.group(0);
            String fixed = whole.replaceAll(" (src |w |morph )", " "); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("\\|", " "); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("x-Strongs", "strong"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("x-StrongsMorph", "morph"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("x-Robinson", "robinson"); //$NON-NLS-1$ //$NON-NLS-2$
            fixed = fixed.replaceAll("splitID=\"", "type=\"x-split\" subType=\"x-"); //$NON-NLS-1$ //$NON-NLS-2$
            if ( !whole.equals(fixed))
            {
                input = input.replace(whole, fixed); //$NON-NLS-1$
            }
        }

input = input.replaceAll("\"transChange\"", "\"x-transChange\""); //$NON-NLS-1$ //$NON-NLS-2$
input = input.replaceAll("\"type:", "\"x-"); //$NON-NLS-1$ //$NON-NLS-2$
input = input.replaceAll("<resp\\s[^>]*/>", ""); //$NON-NLS-1$ //$NON-NLS-2$
input = input.replaceAll("changeType=\"", "type=\""); //$NON-NLS-1$ //$NON-NLS-2$
input = input.replaceAll("<p/>", "<lb/>"); //$NON-NLS-1$ //$NON-NLS-2$
if (osisID.equals("Matt.24.38")) //$NON-NLS-1$
{
    input = input.replace("<w src=\"18\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\"><w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-DPF\">that</w></w>", //$NON-NLS-1$
                          "<w src=\"18\" lemma=\"strong:G3739\" morph=\"robinson:R-GSF\"></w><w src=\"7\" lemma=\"strong:G3588\" morph=\"robinson:T-DPF\">that</w>"); //$NON-NLS-1$
}
        return input;
    }
    private static FieldPosition pos = new FieldPosition(0);

    private static String preVerseStart = "<title subtype=\"x-preverse\" type=\"section\">"; //$NON-NLS-1$
    private static String preVerseEnd = "</title>"; //$NON-NLS-1$
    private static Pattern preVerseStartPattern = Pattern.compile(preVerseStart);
    private static Pattern preVerseEndPattern = Pattern.compile(preVerseEnd); //$NON-NLS-1$

    private static String badNote = "<note type=\"[^\"]*\" name=\"[^\"]*\" date=\"[^\"]*\"/>"; //$NON-NLS-1$
    private static Pattern badNotePattern = Pattern.compile(badNote);

    private static String wElement = "<w\\s[^>]*>"; //$NON-NLS-1$
    private static Pattern wPattern = Pattern.compile(wElement);
}
