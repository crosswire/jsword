
package org.crosswire.jsword.book.jdbc;

import java.net.URL;
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
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.Filters;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.book.local.LocalURLBible;
import org.crosswire.jsword.book.local.LocalURLBibleMetaData;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.Parser;
import org.crosswire.jsword.book.search.ParserFactory;
import org.crosswire.jsword.book.search.SearchEngine;
import org.crosswire.jsword.book.search.SearchEngineFactory;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JDBCBible extends LocalURLBible implements Index
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#init()
     */
    public void init() throws BookException
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
            textcnx = DriverManager.getConnection(text_url);

            String concord_url = lbmd.getProperty("ConcordURL");
            conccnx = DriverManager.getConnection(concord_url);

            // SQL statements
            String doc_query = lbmd.getProperty("DocQuery");
            doc_stmt = textcnx.prepareStatement(doc_query);

            String ref_query = lbmd.getProperty("RefQuery");
            ref_stmt = conccnx.prepareStatement(ref_query);

            String verse_query = lbmd.getProperty("VerseQuery");
            verse_stmt = textcnx.prepareStatement(verse_query);

            String start_query = lbmd.getProperty("StartQuery");
            start_stmt = conccnx.prepareStatement(start_query);

            words_query = lbmd.getProperty("WordsQuery");
        }
        catch (SQLException ex)
        {
            textcnx = null;
            conccnx = null;
            doc_stmt = null;
            ref_stmt = null;
            verse_stmt = null;
            start_stmt = null;
            words_query = null;

            throw new BookException(Msg.BIBLE_CONNECT, ex);
        }

        try
        {
            URL url = lbmd.getURL();
            searcher = SearchEngineFactory.createSearchEngine(this, url);
        }
        catch (BookException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.BIBLE_CONNECT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#activate()
     */
    public void activate()
    {
        searcher.activate();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#deactivate()
     */
    public void deactivate()
    {
        searcher.deactivate();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search search) throws BookException
    {
        try
        {
            Parser parser = ParserFactory.createParser(this);
            return parser.search(search);
        }
        catch (InstantiationException ex)
        {
            throw new BookException(Msg.SEARCH_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#getData(org.crosswire.jsword.passage.Passage)
     */
    public BookData getData(Passage ref) throws BookException
    {
        try
        {
            String osisid = getBibleMetaData().getInitials();
            Osis osis = JAXBUtil.factory().createOsis();
            
            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);
            
            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);
            
            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Bible."+osisid);
            text.setHeader(header);
            
            osis.setOsisText(text);

            Iterator it = ref.rangeIterator(PassageConstants.RESTRICT_CHAPTER);
            while (it.hasNext())
            {
                VerseRange range = (VerseRange) it.next();
                Verse start = range.getStart();
                Verse end = range.getEnd();
                int start_id = BibleInfo.verseOrdinal(start.getRefArray());
                int end_id = BibleInfo.verseOrdinal(end.getRefArray());

                Div div = JAXBUtil.factory().createDiv();
                div.setDivTitle(range.getName());

                text.getDiv().add(div);

                doc_stmt.setInt(1, start_id);
                doc_stmt.setInt(2, end_id);
                ResultSet rs = doc_stmt.executeQuery();

                while (rs.next())
                {
                    Verse verse = new Verse(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                    rs.getBoolean(4); // ignored, but perhaps we should still be getting things in order?
                    String vtext = rs.getString(5);

                    // If the verse is empty then we shouldn't add the verse tag
                    if (vtext != null && vtext.length() > 0)
                    {
                        org.crosswire.jsword.osis.Verse everse = JAXBUtil.factory().createVerse();
                        everse.setOsisID(verse.getBook()+"."+verse.getChapter()+"."+verse.getVerse());
    
                        div.getContent().add(everse);
    
                        String txt = JDBCBibleUtil.processText(vtext);
                        Filters.PLAIN_TEXT.toOSIS(everse, txt);
                    }
                }

                rs.close();
            }
            
            BookData bdata = new BookData(osis);
            return bdata;
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
        catch (SQLException ex)
        {
            throw new BookException(Msg.BIBLE_DB, ex);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /**
     * Part of the Bible interface - Get the Passage for this word.
     */
    public Passage findWord(String word) throws BookException
    {
        if (word == null)
        {
            return PassageFactory.createPassage();
        }

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
        try
        {
            Statement stmt = conccnx.createStatement();
            return new WordIterator(stmt, stmt.executeQuery(words_query));
        }
        catch (SQLException ex)
        {
            throw new BookException(Msg.BIBLE_DB, ex);
        }
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

    /** The search implementation */
    protected SearchEngine searcher;

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
    private Connection textcnx;

    /** The conenction to the concordance */
    protected Connection conccnx;

    /** The log stream */
    protected static final Logger log = Logger.getLogger(JDBCBible.class);

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

        /** Are there any more words? */
        private boolean more = true;

        /** The local connection */
        private Statement stmt = null;

        /** The result from the database */
        private ResultSet rs = null;
    }
}
