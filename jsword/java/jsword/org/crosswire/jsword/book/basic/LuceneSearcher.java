
package org.crosswire.jsword.book.basic;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.events.ProgressListener;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;

/**
 * Implement the Searcher using Lucene as the search engine.
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
public class LuceneSearcher implements Searcher
{

    /**
     * Constructor for LuceneSearcher.
     */
    public LuceneSearcher(Bible bible, URL url, ProgressListener li) throws IOException
    {
        // An index is created by opening an IndexWriter with the
        // create argument set to true.
        IndexWriter writer = new IndexWriter(NetUtil.getAsFile(url), new SimpleAnalyzer(), false);

        // We create a Document with two Fields, one which contains
        // the file path, and one the file's contents.
        Document doc = new Document();
        doc.add(Field.UnIndexed("path", "somefile"));
        doc.add(Field.Text("body", new FileReader("somefile")));
    
        writer.addDocument(doc);

        writer.close();

        searcher = new IndexSearcher(NetUtil.getAsFile(url).getCanonicalPath());
    }

    /**
     * @see org.crosswire.jsword.book.basic.Searcher#getStartsWith(java.lang.String)
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        return null;
    }

    /**
     * @see org.crosswire.jsword.book.basic.Searcher#findPassage(java.lang.String)
     */
    public Passage findPassage(String word) throws BookException
    {
        try
        {
            Query query = QueryParser.parse(word, "body", new SimpleAnalyzer());
            Hits hits = searcher.search(query);
            
            PassageTally tally = new PassageTally();
            for (int i = 0; i < hits.length(); i++)
            {
                Verse verse = new Verse(hits.doc(i).get("path"));
                int score = (int) (hits.score(i) * 100);
                tally.add(verse, score);
            };
            
            return tally;
        }
        catch (Exception ex)
        {
            throw new BookException("search_failed", ex);
        }
    }

    /**
     * Remove all the files that make up this index.
     */
    public void delete() throws BookException
    {
        // write this
    }

    private org.apache.lucene.search.Searcher searcher;
}
