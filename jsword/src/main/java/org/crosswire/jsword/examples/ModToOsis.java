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
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.OSISUtil;
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
        Books books = Books.installed();
        Book bible = books.getBook(BIBLE_NAME);
        int lastBook = -1;
        int lastChapter = -1;
        StringBuffer buf = null;
        
        try
        {
            Key key = bible.getKey(BIBLE_RANGE);

            // Get a verse iterator
            Iterator iter = key.iterator();
            while (iter.hasNext())
            {
                Verse verse = (Verse) iter.next();
                String raw = bible.getRawData(verse);
                String osisID = verse.getOsisID();

                int currentBook = verse.getBook();
                int currentChapter = verse.getChapter();
                if (lastBook != currentBook)
                {
                    if (lastBook != -1)
                    {
                        if (currentChapter == 1)
                        {
                            buf.append("</").append(OSISUtil.OSIS_ELEMENT_CHAPTER).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        buf.append("</").append(OSISUtil.OSIS_ELEMENT_DIV).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
                        Writer writer = new OutputStreamWriter(new FileOutputStream(BibleInfo.getOSISName(lastBook) + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
                        writer.write(buf.toString());
                        writer.close();
                    }

                    buf = new StringBuffer();

                    buf.append('<').append(OSISUtil.OSIS_ELEMENT_DIV);
                    buf.append(' ');
                    buf.append(OSISUtil.OSIS_ATTR_TYPE).append("=\"").append(OSISUtil.ATTRIBUTE_DIV_BOOK); //$NON-NLS-1$
                    buf.append("\" "); //$NON-NLS-1$
                    buf.append(OSISUtil.OSIS_ATTR_OSISID).append("=\"").append(BibleInfo.getOSISName(currentBook));  //$NON-NLS-1$
                    buf.append("\">\n"); //$NON-NLS-1$
                }

                if (lastBook != currentBook || lastChapter != currentChapter)
                {
                    if (currentChapter != 1)
                    {
                        buf.append("</").append(OSISUtil.OSIS_ELEMENT_CHAPTER).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    buf.append('<').append(OSISUtil.OSIS_ELEMENT_CHAPTER);
                    buf.append(' ');
                    buf.append(OSISUtil.OSIS_ATTR_OSISID).append("=\"").append(BibleInfo.getOSISName(currentBook)).append('.').append(currentChapter);  //$NON-NLS-1$
                    buf.append("\">"); //$NON-NLS-1$
                }

                /* Output the verse */
                buf.append('<').append(OSISUtil.OSIS_ELEMENT_VERSE);
                buf.append(' ');
                buf.append(OSISUtil.OSIS_ATTR_OSISID).append("=\"").append(osisID);  //$NON-NLS-1$
                buf.append("\" "); //$NON-NLS-1$
                buf.append(OSISUtil.OSIS_ATTR_SID).append("=\"").append(osisID); //$NON-NLS-1$
                buf.append("\"/>"); //$NON-NLS-1$
                buf.append(raw);
                buf.append('<').append(OSISUtil.OSIS_ELEMENT_VERSE);
                buf.append(' ');
                buf.append(OSISUtil.OSIS_ATTR_EID).append("=\"").append(osisID); //$NON-NLS-1$
                buf.append("\"/>"); //$NON-NLS-1$               

                if (lastChapter != currentChapter)
                {
                    lastChapter = currentChapter;
                }

                if (lastBook != currentBook)
                {
                    lastBook = currentBook;
                }
            }

            buf.append("</").append(OSISUtil.OSIS_ELEMENT_CHAPTER).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("</").append(OSISUtil.OSIS_ELEMENT_DIV).append(">\n"); //$NON-NLS-1$ //$NON-NLS-2$
            Writer writer = new OutputStreamWriter(new FileOutputStream(BibleInfo.getOSISName(lastBook) + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write(buf.toString());
            writer.close();
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
            // XXX Auto-generated catch block
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            // XXX Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // XXX Auto-generated catch block
            e.printStackTrace();
        }
    }

}
