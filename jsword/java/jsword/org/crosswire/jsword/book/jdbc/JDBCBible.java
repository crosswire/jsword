
package org.crosswire.jsword.book.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.ProgressListener;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.BookDataListener;
import org.crosswire.jsword.book.data.DataFactory;
import org.crosswire.jsword.book.data.FilterException;
import org.crosswire.jsword.book.data.Filters;
import org.crosswire.jsword.book.local.LocalURLBible;
import org.crosswire.jsword.book.local.LocalURLBibleMetaData;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * JDBCBible implements Bible, and gets the text from a JDBC database.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JDBCBible extends LocalURLBible
{
    /**
     * Startup
     */
    public void init(Bible source, ProgressListener li)
    {
        throw new LogicError();
    }

    /**
     * Startup
     */
    public void init(ProgressListener li)
    {
        LocalURLBibleMetaData lbmd = getLocalURLBibleMetaData();

        // Load the specified JDBC name
        int driver_attempt = 1;
        while (true)
        {
            String property = "JdbcDriver" + driver_attempt;
            String driver = lbmd.getProperty(property);

            try
            {
                Class.forName(driver);
                break;
            }
            catch (Exception ex)
            {
                log.debug("Failed to load JDBC name: "+driver+" (System Message: "+ex+")");
            }

            driver_attempt++;
        }

        try
        {
            // Actually connect to the database
            String text_url = lbmd.getProperty("TextURL");
            text = DriverManager.getConnection(text_url);

            String concord_url = lbmd.getProperty("ConcordURL");
            concord = DriverManager.getConnection(concord_url);

            // SQL statements
            String doc_query = lbmd.getProperty("DocQuery");
            doc_stmt = text.prepareStatement(doc_query);

            String ref_query = lbmd.getProperty("RefQuery");
            ref_stmt = concord.prepareStatement(ref_query);

            String verse_query = lbmd.getProperty("VerseQuery");
            verse_stmt = text.prepareStatement(verse_query);

            String start_query = lbmd.getProperty("StartQuery");
            start_stmt = concord.prepareStatement(start_query);

            words_query = lbmd.getProperty("WordsQuery");
        }
        catch (SQLException ex)
        {
            text = null;
            concord = null;
            doc_stmt = null;
            ref_stmt = null;
            verse_stmt = null;
            start_stmt = null;
            words_query = null;

            log.error("Failed to connect", ex);
        }

        super.init(li);
    }

    /**
     * Create an XML document for the specified Verses
     * @param doc The XML document
     * @param ele The Element to start adding at. null if the doc is empty
     * @param ref The verses to search for
     */
    public BookData getData(Passage ref) throws BookException
    {
        try
        {
            BookDataListener li = DataFactory.getInstance().createBookDataListnener();
            li.startDocument(getBibleMetaData().getInitials());

            Iterator it = ref.rangeIterator();
            while (it.hasNext())
            {
                VerseRange range = (VerseRange) it.next();
                Verse start = range.getStart();
                Verse end = range.getEnd();
                int start_id = BibleInfo.verseOrdinal(start.getRefArray());
                int end_id = BibleInfo.verseOrdinal(end.getRefArray());

                li.startSection(range.getName());

                doc_stmt.setInt(1, start_id);
                doc_stmt.setInt(2, end_id);
                ResultSet rs = doc_stmt.executeQuery();

                while (rs.next())
                {
                    Verse verse = new Verse(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                    rs.getBoolean(4); // ignored, but perhaps we should still be getting things in order?
                    String text = rs.getString(5);
                    if (text == null) text = "";

                    li.startVerse(verse);
                    Filters.PLAIN_TEXT.toOSIS(li, JDBCBibleUtil.processText(text));
                    li.endVerse();
                }

                li.endSection();
                rs.close();
            }

            return li.endDocument();
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
        catch (FilterException ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
        catch (SQLException ex)
        {
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /**
     * Part of the Bible interface - Get the Passage for this word.
     */
    public Passage findPassage(String word) throws BookException
    {
        if (word == null)
            return PassageFactory.createPassage();

        word = JDBCBibleUtil.swapChar(word, '-', '?').toLowerCase();

        try
        {
            Passage retcode = PassageFactory.createPassage();

            ref_stmt.setString(1, word);
            ResultSet rs = ref_stmt.executeQuery();
            while (rs.next())
            {
                Verse temp = new Verse(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                retcode.add(temp);
            }

            rs.close();

            return retcode;
        }
        catch (NoSuchVerseException ex)
        {
            log.warn("word="+word);
            throw new LogicError(ex);
        }
        catch (SQLException ex)
        {
            log.warn("word="+word);
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /**
     * Part of the Bible interface - Get the Passage for this word.
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        try
        {
            ArrayList output = new ArrayList();

            // word = JDBCBibleUtil.swapChar(word, '\'', '?');
            start_stmt.setString(1, word+"%");
            ResultSet rs = start_stmt.executeQuery();
            while (rs.next())
            {
                output.add(rs.getString(1));
            }

            rs.close();

            return output.iterator();
        }
        catch (SQLException ex)
        {
            log.warn("word="+word);
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /**
     * Retrieval: Get a list of the words used by this Version. This is
     * not vital for normal display, however it is very useful for various
     * things, not least of which is new Version generation. However if
     * you are only looking to <i>display</i> from this Bible then you
     * could skip this one.
     * @return The references to the word
     */
    public Iterator listWords() throws BookException
    {
        return new WordIterator();
    }

    /**
     * Where does this verse come in the Bible. Starting with Gen 1:1 as number 1
     * counting up one per verse and not resetting at each new chapter.
     * @param ref An array of 3 ints, book, chapter, verse
     * @return The ordinal number of verses
     * @exception NoSuchVerseException If the reference is illegal
     */
    public int verseOrdinal(int[] ref) throws NoSuchVerseException, SQLException
    {
        if (ref.length != 3)
        {
            throw new NoSuchVerseException(Msg.BIBLE_VERSE);
        }

        return verseOrdinal(ref[0], ref[1], ref[2]);
    }

    /**
     * Where does this verse come in the Bible. Starting with Gen 1:1 as number 1
     * counting up one per verse and not resetting at each new chapter.
     * @param book The book part of the reference.
     * @param chapter The current chapter
     * @param verse The current verse
     * @return The ordinal number of verses
     * @exception NoSuchVerseException If the reference is illegal
     */
    public int verseOrdinal(int book, int chapter, int verse) throws NoSuchVerseException, SQLException
    {
        int retcode = 0;

        verse_stmt.setInt(1, book);
        verse_stmt.setInt(2, book);
        verse_stmt.setInt(3, book);
        ResultSet rs = verse_stmt.executeQuery();

        if (!rs.next())
        {
            throw new NoSuchVerseException(Msg.BIBLE_LOST);
        }

        retcode = rs.getInt(1);

        rs.close();

        return retcode;
    }

    /** Cached statement for getDocument */
    private PreparedStatement doc_stmt;

    /** Cached statement for findWord */
    private PreparedStatement ref_stmt;

    /** Cached statement for verseOrdinal */
    private PreparedStatement verse_stmt;

    /** Cached statement for startsWith */
    private PreparedStatement start_stmt;

    /** The statment for this is part of the enumeration */
    protected String words_query;

    /** The conenction to the text data source */
    private Connection text;

    /** The conenction to the concordance */
    protected Connection concord;

    /** The log stream */
    protected static Logger log = Logger.getLogger(JDBCBible.class);

    /**
     * Helper class to enumerate through the words in a version
     */
    class WordIterator implements Iterator
    {
        /**
         * Create the necessary SQL query
         */
        protected WordIterator() throws BookException
        {
            try
            {
                stmt = concord.createStatement();
                rs = stmt.executeQuery(words_query);

                moveToNext();
            }
            catch (SQLException ex)
            {
                throw new BookException(Msg.BIBLE_DB, ex);
            }
        }

        /**
         * Are there more items in the database?
         */
        public boolean hasNext()
        {
            return more;
        }

        /**
         * Get the next item from the database
         */
        public Object next() throws NoSuchElementException
        {
            try
            {
                String retcode = rs.getString(1);
                moveToNext();

                // If we got a null then have one more go ...
                if (retcode == null)
                {
                    retcode = rs.getString(1);
                    moveToNext();
                }

                return retcode;
            }
            catch (SQLException ex)
            {
                log.warn("SQL error in iteration", ex);
                throw new NoSuchElementException(ex.getMessage());
            }
        }

        /**
         * Not supported
         * @throws UnsupportedOperationException Every time ...
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Check for more. If there are none, shut up shop to be more
         * resource friendly
         */
        private void moveToNext() throws SQLException
        {
            more = rs.next();

            if (!more)
            {
                rs.close();
                stmt.close();
            }
        }

        /** Are there any more words? */
        private boolean more = true;

        /** The local connection */
        private Statement stmt = null;

        /** The result from the database */
        private ResultSet rs = null;
    }
}
