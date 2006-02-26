package org.crosswire.jsword.examples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

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
                    }

                    buf = new StringBuffer();
                    buildDocumentOpen(buf, bmd, currentBookName);
                    buildBookOpen(buf, currentBookName);
                }

                if (newBookFound || lastChapter != currentChapter)
                {
                    if (currentChapter != 1)
                    {
                        buildChapterClose(buf);
                    }
                    buildChapterOpen(buf, currentBookName, currentChapter);
                }

                /* Output the verse */
                
                /* TODO(DMS):
                 * If the "raw" verse contains a "preverse" pull it out.
                 * If there were a former preverse then close the "section" div
                 * before outputting it before the verse.
                 */
                boolean foundPreVerse = false;
                String preVerseText = "title"; //$NON-NLS-1$
                if (foundPreVerse)
                {
                    if (inPreVerse)
                    {
                        buildPreVerseClose(buf);
                    }
                    buildPreVerseOpen(buf, preVerseText); //$NON-NLS-1$
                    inPreVerse = true;
                }

                buildVerseOpen(buf, osisID);
                buf.append(raw);
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
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"); //$NON-NLS-1$
        buf.append("<osis"); //$NON-NLS-1$
        buf.append("\n  xmlns=\"http://www.bibletechnologies.net/2003/OSIS/namespace\""); //$NON-NLS-1$
        buf.append("\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");  //$NON-NLS-1$
        buf.append("\n  xsi:schemaLocation=\"http://www.bibletechnologies.net/2003/OSIS/namespace osisCore.2.1.xsd\">"); //$NON-NLS-1$
        buf.append("\n    <osisText osisIDWork=\"").append(bmd.getInitials()).append('"').append(" osisRefWork=\"defaultReferenceScheme\">"); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("\n    <header>"); //$NON-NLS-1$
        buf.append("\n      <work osisWork=\"").append(bmd.getInitials()).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("\n      <title>").append(bmd.getName()).append("</title>"); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("\n      <identifier type=\"OSIS\">Bible.").append(bmd.getInitials()).append("</identifier>"); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("\n      <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        buf.append("\n      <scope>").append(range).append("</scope>"); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("\n    </work>"); //$NON-NLS-1$
        buf.append("\n    <work osisWork=\"defaultReferenceScheme\">"); //$NON-NLS-1$
        buf.append("\n      <refSystem>Bible.KJV</refSystem>"); //$NON-NLS-1$
        buf.append("\n    </work>"); //$NON-NLS-1$
        buf.append("\n  </header>"); //$NON-NLS-1$
        buf.append('\n');

    }

    private void buildDocumentClose(StringBuffer buf)
    {
        buf.append("</osisText>\n</osis>\n"); //$NON-NLS-1$
    }

    private void buildBookOpen(StringBuffer buf, String bookName)
    {
        buf.append("<div type=\"book\" osisID=\"").append(bookName).append("\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
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
        buf.append("<chapter osisID=\"").append(bookName).append('.').append(chapter).append("\">"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void buildPreVerseOpen(StringBuffer buf, String preVerse)
    {
        buf.append("<div type=\"section\"><title>").append(preVerse).append("</title>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void buildPreVerseClose(StringBuffer buf)
    {
        buf.append("</div>\n"); //$NON-NLS-1$
    }

    private void buildVerseOpen(StringBuffer buf, String osisID)
    {
        buf.append("<verse sID=\"").append(osisID).append("\" osisID=\"").append(osisID).append("\"/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private void buildVerseClose(StringBuffer buf, String osisID)
    {
        buf.append("<verse eID=\"").append(osisID).append("\"/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    private void writeDocument(StringBuffer buf, String filename) throws IOException
    {
        Writer writer = new OutputStreamWriter(new FileOutputStream(filename + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
        writer.write(buf.toString());
        writer.close();
    }
}
