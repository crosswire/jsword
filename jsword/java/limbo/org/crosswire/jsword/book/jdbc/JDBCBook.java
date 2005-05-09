package org.crosswire.jsword.book.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * JDBCBook implements Bible, and gets the text from a JDBC database.
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JDBCBook extends AbstractPassageBook
{
    /**
     * Simple ctor
     */
    public JDBCBook(BookDriver driver, Properties prop) throws BookException
    {
        BookMetaData bmd = new DefaultBookMetaData(driver, this, prop);
        setBookMetaData(bmd);

        Map props = getProperties();

        // Load the specified JDBC name
        int driver_attempt = 1;
        while (true)
        {
            String property = "JdbcDriver" + driver_attempt; //$NON-NLS-1$
            String drivername = (String) props.get(property);

            try
            {
                Class.forName(drivername);
                break;
            }
            catch (Exception ex)
            {
                log.debug("Failed to load JDBC name: "+driver+" (System Message: "+ex+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            driver_attempt++;
        }

        try
        {
            // Actually connect to the database
            String text_url = (String) props.get("TextURL"); //$NON-NLS-1$
            textCon = DriverManager.getConnection(text_url);

            String concord_url = (String) props.get("ConcordURL"); //$NON-NLS-1$
            concCon = DriverManager.getConnection(concord_url);

            // SQL statements
            String doc_query = (String) props.get("DocQuery"); //$NON-NLS-1$
            docStmt = textCon.prepareStatement(doc_query);

            String ref_query = (String) props.get("RefQuery"); //$NON-NLS-1$
            refStmt = concCon.prepareStatement(ref_query);

            //String verse_query = (String) props.get("VerseQuery");
            //verse_stmt = textcnx.prepareStatement(verse_query);

            String start_query = (String) props.get("StartQuery"); //$NON-NLS-1$
            startStmt = concCon.prepareStatement(start_query);

            wordsQuery = (String) props.get("WordsQuery"); //$NON-NLS-1$
        }
        catch (SQLException ex)
        {
            textCon = null;
            concCon = null;
            docStmt = null;
            refStmt = null;
            //verse_stmt = null;
            startStmt = null;
            wordsQuery = null;

            throw new BookException(Msg.BIBLE_CONNECT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    protected Filter getFilter()
    {
        return FilterFactory.getDefaultFilter();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getText(org.crosswire.jsword.passage.Verse)
     */
    protected String getText(Key key)
    {
        String reply = ""; //$NON-NLS-1$
        ResultSet rs = null;

        Verse verse = KeyUtil.getVerse(key);

        try
        {
            docStmt.setInt(1, verse.getOrdinal());
            docStmt.setInt(2, verse.getOrdinal());
            rs = docStmt.executeQuery();

            while (rs.next())
            {
                String vtext = rs.getString(5);

                // If the verse is empty then we shouldn't add the verse tag
                if (vtext != null && vtext.length() > 0)
                {
                    reply = JDBCBibleUtil.processText(vtext);
                }
            }
        }
        catch (SQLException ex)
        {
            log.fatal("read failed", ex); //$NON-NLS-1$
        }
        finally
        {
            try
            {
                rs.close();
            }
            catch (SQLException ex)
            {
                log.fatal("close() failed", ex); //$NON-NLS-1$
            }
        }

        return reply;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#setText(org.crosswire.jsword.passage.Verse, java.lang.String)
     */
    protected void setText(Verse verse, String text) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#findWord(java.lang.String)
     */
    public Key findWord(String word) throws BookException
    {
        if (word == null)
        {
            return createEmptyKeyList();
        }

        word = JDBCBibleUtil.swapChar(word, '-', '?').toLowerCase();

        try
        {
            Key retcode = createEmptyKeyList();

            refStmt.setString(1, word);
            ResultSet rs = refStmt.executeQuery();
            while (rs.next())
            {
                Verse temp = new Verse(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                retcode.addAll(temp);
            }

            rs.close();

            return retcode;
        }
        catch (NoSuchVerseException ex)
        {
            log.error("word="+word); //$NON-NLS-1$
            assert false : ex;
            return createEmptyKeyList();
        }
        catch (SQLException ex)
        {
            log.error("word="+word); //$NON-NLS-1$
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#getStartsWith(java.lang.String)
     */
    public Collection getStartsWith(String word) throws BookException
    {
        try
        {
            ArrayList output = new ArrayList();

            // word = JDBCBibleUtil.swapChar(word, '\'', '?');
            startStmt.setString(1, word+"%"); //$NON-NLS-1$
            ResultSet rs = startStmt.executeQuery();
            while (rs.next())
            {
                output.add(rs.getString(1));
            }

            rs.close();

            return output;
        }
        catch (SQLException ex)
        {
            log.error("word="+word); //$NON-NLS-1$
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /*
     * Where does this verse come in the Bible. Starting with Gen 1:1 as number 1
     * counting up one per verse and not resetting at each new chapter.
     * @param book The book part of the reference.
     * @param chapter The current chapter
     * @param verse The current verse
     * @return The ordinal number of verses
     * @exception NoSuchVerseException If the reference is illegal
     *
    private int verseOrdinal(int book, int chapter, int verse) throws NoSuchVerseException, SQLException
    {
        int retcode = 0;

        verse_stmt.setInt(1, book);
        verse_stmt.setInt(2, chapter);
        verse_stmt.setInt(3, verse);
        ResultSet rs = verse_stmt.executeQuery();

        if (!rs.next())
        {
            throw new NoSuchVerseException(Msg.BIBLE_LOST);
        }

        retcode = rs.getInt(1);

        rs.close();

        return retcode;
    }
    */

    /**
     * Cached statement for getDocument
     */
    private PreparedStatement docStmt;

    /**
     * Cached statement for findWord
     */
    private PreparedStatement refStmt;

    /**
     * Cached statement for verseOrdinal
     *
    private PreparedStatement verse_stmt;

    /**
     * Cached statement for startsWith
     */
    private PreparedStatement startStmt;

    /**
     * The statment for this is part of the enumeration
     */
    protected String wordsQuery;

    /**
     * The conenction to the text data source
     */
    private Connection textCon;

    /**
     * The conenction to the concordance
     */
    protected Connection concCon;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(JDBCBook.class);

    /**
     * Helper class to enumerate through the words in a version
     */
    static class WordIterator implements Iterator
    {
        /**
         * Create the necessary SQL query
         */
        protected WordIterator(Statement stmt, ResultSet rs) throws BookException
        {
            this.stmt = stmt;
            this.rs = rs;

            moveToNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return more;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
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
            catch (Exception ex)
            {
                log.warn("SQL error in iteration", ex); //$NON-NLS-1$
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
        private void moveToNext() throws BookException
        {
            try
            {
                more = rs.next();

                if (!more)
                {
                    rs.close();
                    stmt.close();
                }
            }
            catch (SQLException ex)
            {
                throw new BookException(Msg.BIBLE_DB, ex);
            }
        }

        /**
         * Are there any more words?
         */
        private boolean more = true;

        /**
         * The local connection
         */
        private Statement stmt = null;

        /**
         * The result from the database
         */
        private ResultSet rs = null;
    }
}