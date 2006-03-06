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
            XMLProcess parser = new XMLProcess();
            parser.getFeatures().setFeatureStates("-s", "-f", "-va", "-dv"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            parser.parse(lastBookName + ".xml"); //$NON-NLS-1$
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

    private String cleanup(String osisID, String input)
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
        if (osisID.equals("Mark.10.27")) //$NON-NLS-1$
        {
            input = input.replace("<transChange type=\"added\"><w src=\"9\" lemma=\"strong:G102\" morph=\"robinson:A-NSN\">it is</transChange> impossible</w>", //$NON-NLS-1$
                                  "<transChange type=\"added\">it is</transChange> <w src=\"9\" lemma=\"strong:G102\" morph=\"robinson:A-NSN\">impossible</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Luke.15.24")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"2\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">or</w> </w>", //$NON-NLS-1$
                                  "<w src=\"2\" lemma=\"strong:G3778\" morph=\"robinson:D-NSM\">or</w> "); //$NON-NLS-1$
            input = input.replace("</q>", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (osisID.equals("John.5.36")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"8\" lemma=\"strong:G2491\" morph=\"robinson:N-GSM\">than <transChange type=\"added\">that</transChange> of John</w>", //$NON-NLS-1$
                                  "<w src=\"8\" lemma=\"strong:G2491\" morph=\"robinson:N-GSM\">than <seg type=\"x-transChange\" subType=\"x-added\">that</seg> of John</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Acts.17.25")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"robinson:A-APN\" src=\"16\" lemma=\"strong:G3956\"><w src=\"15\" lemma=\"strong:G3956\" morph=\"robinson:A-APN\">all things;</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:A-APN\" src=\"15, 16\" lemma=\"strong:G3956\">all things;</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Acts.26.3")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"18\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\"><w morph=\"robinson:P-1GS\" src=\"19\" lemma=\"strong:G3450\">me</w></w>", //$NON-NLS-1$
                                  "<w src=\"18, 19\" lemma=\"strong:G3450\" morph=\"robinson:P-1GS\">me</w>"); //$NON-NLS-1$
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
                                  "<w src=\"2, 8\" lemma=\"strong:G3739 strong:G2443\" morph=\"robinson:R-ASN robinson:CONJ\">that</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Phlm.1.15")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"6\" lemma=\"strong:G4314\" morph=\"robinson:PREP\"><w src=\"3\" lemma=\"strong:G1223\" morph=\"robinson:PREP\">for</w></w>", //$NON-NLS-1$
                                  "<w src=\"3, 6\" lemma=\"strong:G1223 strong:G4314\" morph=\"robinson:PREP\">for</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.8.6")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G4537\" morph=\"robinson:V-AAS-3P\"><w morph=\"robinson:V-AAS-3P\" src=\"13\" lemma=\"strong:G4537\">sound.</w></w>", //$NON-NLS-1$
                                  "<w src=\"12, 13\" lemma=\"strong:G4537\" morph=\"robinson:V-AAS-3P\">sound.</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.11.1")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"19\" lemma=\"strong:G3588\" morph=\"robinson:T-APM\"><w morph=\"robinson:T-APM\" src=\"23\" lemma=\"strong:G3588\">them that</w></w>", //$NON-NLS-1$
                                  "<w src=\"19, 23\" lemma=\"strong:G3588\" morph=\"robinson:T-APM\">them that</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:V-PAP-APM\" src=\"24\" lemma=\"strong:G4352\"><w src=\"20\" lemma=\"strong:G4352\" morph=\"robinson:V-PAP-APM\">worship</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:V-PAP-APM\" src=\"20, 24\" lemma=\"strong:G4352\">worship</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:P-DSM\" src=\"26\" lemma=\"strong:G846\"><w src=\"22\" lemma=\"strong:G846\" morph=\"robinson:P-DSM\">therein.</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-DSM\" src=\"22, 26\" lemma=\"strong:G846\">therein.</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.11.4")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"15\" lemma=\"strong:G2476\" morph=\"robinson:V-RAP-NPF\"><w morph=\"robinson:V-RAP-NPF\" src=\"16\" lemma=\"strong:G2476\">standing</w></w>", //$NON-NLS-1$
                                  "<w src=\"15, 16\" lemma=\"strong:G2476\" morph=\"robinson:V-RAP-NPF\">standing</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.11.14")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"12\" lemma=\"strong:G5035\" morph=\"robinson:ADV\"><w morph=\"robinson:ADV\" src=\"13\" lemma=\"strong:G5035\">quickly.</w></w>", //$NON-NLS-1$
                                  "<w src=\"12, 13\" lemma=\"strong:G5035\" morph=\"robinson:ADV\">quickly.</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.14.7")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"32\" lemma=\"strong:G5204\" morph=\"robinson:N-GPN\"><w morph=\"robinson:N-GPN\" src=\"33\" lemma=\"strong:G5204\">of waters.</w></w>", //$NON-NLS-1$
                                  "<w src=\"32, 33\" lemma=\"strong:G5204\" morph=\"robinson:N-GPN\">of waters.</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.14.18")) //$NON-NLS-1$
        {
            input = input.replace("<w morph=\"robinson:P-GSF\" src=\"42\" lemma=\"strong:G846\"><w src=\"40\" lemma=\"strong:G846\" morph=\"robinson:P-GSF\">her</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:P-GSF\" src=\"40, 42\" lemma=\"strong:G846\">her</w>"); //$NON-NLS-1$
            input = input.replace("<w morph=\"robinson:N-NPF\" src=\"41\" lemma=\"strong:G4718\"><w src=\"39\" lemma=\"strong:G4718\" morph=\"robinson:N-NPF\">grapes</w></w>", //$NON-NLS-1$
                                  "<w morph=\"robinson:N-NPF\" src=\"39, 41\" lemma=\"strong:G4718\">grapes</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.19.14")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G2513\" morph=\"robinson:A-ASN\"><w morph=\"robinson:A-ASN\" src=\"17\" lemma=\"strong:G2513\">clean.</w></w>", //$NON-NLS-1$
                                  "<w src=\"16, 17\" lemma=\"strong:G2513\" morph=\"robinson:A-ASN\">clean.</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.19.18")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"28\" lemma=\"strong:G3173\" morph=\"robinson:A-GPM\"><w morph=\"robinson:A-GPM\" src=\"29\" lemma=\"strong:G3173\">great.</w></w>", //$NON-NLS-1$
                                  "<w src=\"28, 29\" lemma=\"strong:G3173\" morph=\"robinson:A-GPM\">great.</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Rev.21.13")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"16\" lemma=\"strong:G5140\" morph=\"robinson:A-NPM\"><w morph=\"robinson:A-NPM\" src=\"17\" lemma=\"strong:G5140\">three</w></w>", //$NON-NLS-1$
                                  "<w src=\"16, 17\" lemma=\"strong:G5140\" morph=\"robinson:A-NPM\">three</w>"); //$NON-NLS-1$
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
                                  "<w src=\"2, 9\" lemma=\"strong:G1161 strong:G2532\" morph=\"robinson:CONJ\">and</w>"); //$NON-NLS-1$
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
                                  "<w src=\"1\" lemma=\"strong:G2036\" morph=\"robinson:V-2AAI-3S\">speak</w>"); //$NON-NLS-1$
        }
        if (osisID.equals("Luke.14.21")) //$NON-NLS-1$
        {
            input = input.replace("<w src=\"26\" lemma=\"strong:G3588\" morph=\"robinson:T-GSF\"><w src=\"22\" lemma=\"strong:G3588\" morph=\"robinson:T-APF\"></w></w>", //$NON-NLS-1$
                                  "<w src=\"26\" lemma=\"strong:G3588\" morph=\"robinson:T-GSF\"></w>"); //$NON-NLS-1$
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
        if (osisID.equals("")) //$NON-NLS-1$
        {
            input = input.replace("", //$NON-NLS-1$
                                  ""); //$NON-NLS-1$
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
