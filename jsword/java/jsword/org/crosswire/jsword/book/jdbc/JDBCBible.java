
package org.crosswire.jsword.book.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ArrayList;

import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.basic.VersionFactory;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.book.data.RefData;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;

/**
 * JDBCBible implements Bible, and gets the text from a JDBC database.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public class JDBCBible extends AbstractBible
{
    /**
     * Connect to the Database
     * @param name The name of the Bible
     * @param props The settings for this instance
     */
    public JDBCBible(String name, Properties prop) throws BookException
    {
        this.name = name;
        this.prop = prop;

        // Load the specified JDBC driver
        int driver_attempt = 1;
        while (true)
        {
            String property = "JdbcDriver" + driver_attempt;
            String driver = prop.getProperty(property);

            if (driver == null)
                throw new BookException("jdbc_bible_load",
                    new Object[] { new Integer(driver_attempt-1) });

            try
            {
                Class.forName(driver);
                break;
            }
            catch (Exception ex)
            {
                log.fine("Failed to load JDBC driver: "+
                                  driver+" (System Message: "+ex+")");
            }

            driver_attempt++;
        }

        try
        {
            // The Version
            String version_name = prop.getProperty("Version");
            version = VersionFactory.getVersion(version_name);

            // Actually connect to the database
            String text_url = prop.getProperty("TextURL");
            text = DriverManager.getConnection(text_url);

            String concord_url = prop.getProperty("ConcordURL");
            concord = DriverManager.getConnection(concord_url);

            // SQL statements
            String doc_query = prop.getProperty("DocQuery");
            doc_stmt = text.prepareStatement(doc_query);

            String ref_query = prop.getProperty("RefQuery");
            ref_stmt = concord.prepareStatement(ref_query);

            String verse_query = prop.getProperty("VerseQuery");
            verse_stmt = text.prepareStatement(verse_query);

            String start_query = prop.getProperty("StartQuery");
            start_stmt = concord.prepareStatement(start_query);

            words_query = prop.getProperty("WordsQuery");
        }
        catch (SQLException ex)
        {
            throw new BookException("jdbc_bible_connect", ex);
        }
    }

    /**
     * What driver is controlling this Bible?
     * @return A BibleDriver relevant to this Bible
     */
    public BibleDriver getDriver()
    {
        return JDBCBibleDriver.driver;
    }

    /**
     * Meta-Information: What name can I use to get this Bible in a call
     * to Bibles.getBible(name);
     * @return The name of this Bible
     */
    public String getName()
    {
        return name;
    }

    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getMetaData()
    {
        return version;
    }

    /**
     * Create an XML document for the specified Verses
     * @param doc The XML document
     * @param ele The Element to start adding at. null if the doc is empty
     * @param ref The verses to search for
     */
    public BibleData getData(Passage ref) throws BookException
    {
        BibleData doc = new DefaultBibleData();

        try
        {
            Iterator it = ref.rangeIterator();
            while (it.hasNext())
            {
                VerseRange range = (VerseRange) it.next();
                Verse start = range.getStart();
                Verse end = range.getEnd();
                int start_id = Books.verseOrdinal(start.getRefArray());
                int end_id = Books.verseOrdinal(end.getRefArray());

                SectionData section = doc.createSectionData(range.getName(), "AV");

                doc_stmt.setInt(1, start_id);
                doc_stmt.setInt(2, end_id);
                ResultSet rs = doc_stmt.executeQuery();

                while (rs.next())
                {
                    Verse verse = new Verse(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                    boolean para = rs.getBoolean(4);
                    String text = rs.getString(5);
                    if (text == null) text = "";

                    RefData vref = section.createRefData(verse, para);
                    vref.setPlainText(JDBCBibleUtil.processText(text));
                }

                rs.close();
            }

            return doc;
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
        catch (SQLException ex)
        {
            throw new BookException("jdbc_bible_db", ex);
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
            log.warning("word="+word);
            throw new LogicError(ex);
        }
        catch (SQLException ex)
        {
            log.warning("word="+word);
            throw new BookException("jdbc_bible_db", ex);
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
            log.warning("word="+word);
            throw new BookException("jdbc_bible_db", ex);
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
            throw new NoSuchVerseException("jdbc_bible_verse");

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
            throw new NoSuchVerseException("jdbc_bible_lost");

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

    /** The properties file */
    private Properties prop;

    /** The name of this version */
    private String name;

    /** The Version of the Bible that this produces */
    private BookMetaData version;

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.book");

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
                throw new BookException("jdbc_bible_db", ex);
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
                throw new NoSuchElementException("Database Error. System message: "+ex);
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
