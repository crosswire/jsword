
package org.crosswire.jsword.map.model;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.control.search.Matcher;
import org.crosswire.jsword.control.search.SearchException;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * LinkArray contains a set of link chapters for each chapter in the Bible.
 * It is similar to a central margin reference data set, except that it works
 * with chapters and not verses and every chapter is linked to a constant
 * number of others, and the links have strengths.
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
public class LinkArray implements Serializable
{
    /**
     * Basic constructor
     * @param bible The source of Bible data
     */
    public LinkArray(Bible bible) throws NoSuchVerseException, BookException, SearchException
    {
        this.bible = bible;
        engine = new Matcher(bible);

        links = new Link[Books.booksInBible()+1][][];

        for (int b=1; b<=Books.booksInBible(); b++)
        {
            links[b] = new Link[Books.chaptersInBook(b)+1][];
        }
    }

    /**
     * Save link data to XML as a stream.
     */
    public void load(Reader out) throws IOException
    {
        try
        {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(out);
            Element root = doc.getRootElement();
            fromXML(root);
        }
        catch (JDOMException ex)
        {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Save link data to XML as a stream.
     */
    public void save(Writer out) throws IOException
    {
        Element root = toXML();
        Document doc = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent(2);
        outputter.setNewlines(true);
        outputter.output(doc, out);
    }

    /**
     * Generate links from an XML representation.
     * @param root The root 'links' element
     */
    public void fromXML(Element elinks) throws JDOMException
    {
        if (!elinks.getName().equals("links"))
            throw new JDOMException("root element is not called 'links'");

        List ebs = elinks.getChildren("book");
        Iterator bit = ebs.iterator();
        while (bit.hasNext())
        {
            Element eb = (Element) bit.next();
            int b = Integer.parseInt(eb.getAttributeValue("num"));
            
            List ecs = eb.getChildren("chapter");
            Iterator cit = ecs.iterator();
            while (cit.hasNext())
            {
                Element ec = (Element) cit.next();
                int c = Integer.parseInt(ec.getAttributeValue("num"));

                List ls = new ArrayList();

                List els = ec.getChildren("link");
                Iterator lit = els.iterator();
                while (lit.hasNext())
                {
                    Element el = (Element) lit.next();
                    int db = Integer.parseInt(el.getAttributeValue("book"));
                    int dc = Integer.parseInt(el.getAttributeValue("chapter"));
                    int dr = Integer.parseInt(el.getAttributeValue("rating"));
                    Link l = new Link(db, dc, dr);
                    ls.add(l);
                }

                links[b][c] = (Link[]) ls.toArray(new Link[ls.size()]);
            }
        }
    }

    /**
     * Save link data to XML as a JDOM tree.
     */
    public Element toXML()
    {
        Element elinks = new Element("links");

        try
        {
            for (int b=1; b<=Books.booksInBible(); b++)
            {
                Element eb = new Element("book");
                eb.setAttribute("num", ""+b);
                eb.setAttribute("name", Books.getShortBookName(b));
                elinks.addContent(eb);

                for (int c=1; c<=Books.chaptersInBook(b); c++)
                {
                    Element ec = new Element("chapter");
                    ec.setAttribute("num", ""+c);
                    eb.addContent(ec);
                    Link[] export = links[b][c];
                    for (int i=0; export!=null && i<export.length; i++)
                    {
                        Link l = export[i];
                        int dbook = l.getDestinationBook();
                        int dchap = l.getDestinationChapter();

                        Verse start = new Verse(dbook, dchap, 1);
                        Verse end = new Verse(dbook, dchap, Books.versesInChapter(dbook, dchap));
                        VerseRange chap = new VerseRange(start, end);

                        Element el = new Element("link");
                        el.setAttribute("book", ""+dbook);
                        el.setAttribute("chapter", ""+dchap);
                        el.setAttribute("name", chap.getName());
                        el.setAttribute("rating", ""+l.getStrength());
                        ec.addContent(el);
                    }
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }

        return elinks;
    }

    /**
     * Fill up the link cache
     */
    public void cacheAll() throws NoSuchVerseException, BookException, SearchException
    {
        // Create the array of Nodes
        for (int b=1; b<=Books.booksInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                getLinks(b, c);
            }
        }
    }

    /**
     * Turn a PassageTally into an array of links.
     * @param verse The verse to get a link for
     * @return The array of links for the specified verse
     */
    public Link[] getLinks(int b, int c)
    {
        if (links[b][c] != null)
            return links[b][c];

        try
        {
            PassageTally total = new PassageTally();
            total.setOrdering(PassageTally.ORDER_TALLY);
            
            for (int v=1; v<=Books.versesInChapter(b, c); v++)
            {
                Verse find = new Verse(b, c, v);
                Passage ref = PassageFactory.createPassage();
                ref.add(find);

                BibleData data = bible.getData(ref);
                String text = data.getPlainText();   
                PassageTally temp = engine.bestMatch(text);
                temp.setOrdering(PassageTally.ORDER_TALLY);
                total.addAll(temp);
            }

            int chff = Books.chaptersInBook(b);
            int vsff = Books.versesInChapter(b, chff);
            Verse start = new Verse(b, 1, 1);
            Verse end = new Verse(b, chff, vsff);
            VerseRange book = new VerseRange(start, end);

            total.remove(book);
            total.trimVerses(LINKS_PER_CHAPTER);
            scrunchTally(total);

            // Create the links for the tally
            links[b][c] = new Link[total.countVerses()];
            for (int i=0; i<links[b][c].length; i++)
            {
                Verse loop = total.getVerseAt(i);
                int strength = total.getTallyOf(loop);
                links[b][c][i] = new Link(loop.getBook(), loop.getChapter(), strength);
            }
            
            log.debug("Generated links for: book="+b+" chapter="+c+" #links="+links[b][c].length);

            return links[b][c];
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * What is the average index for a given match. This is a measure of
     * how good the nest match algorithm is. The closer to zero the better
     * @return The average match index
     */
    public float getMatchScore()
    {
        if (linked == 0)
            return -1;

        return ((float) (100 * miss_total)) / linked;
    }

    /**
     * Take a tally and move all the link strengths in and chapter to the first
     * verse in the chapter.
     */
    public void scrunchTally(PassageTally tally) throws NoSuchVerseException
    {
        for (int b=1; b<=Books.booksInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                Verse start = new Verse(b, c, 1);
                Verse end = new Verse(b, c, Books.versesInChapter(b, c));
                VerseRange chapter = new VerseRange(start, end);
                
                int chaptotal = 0;

                for (int v=1; v<=Books.versesInChapter(b, c); v++)
                {
                    chaptotal += tally.getTallyOf(new Verse(b, c, v));
                }
                
                tally.remove(chapter);
                tally.add(start, chaptotal);

                if (chaptotal > PassageTally.MAX_TALLY)
                    System.out.println("truncated chaptotal: "+chaptotal);
            }
        }        
    }

    /**
     * Debug for an array of Links
     */
    private static String debug(Link[] set)
    {
        StringBuffer buff = new StringBuffer();

        for (int i=0; i<set.length; i++)
        {
            if (i != 0)
                buff.append(", ");

            buff.append(set[i].toString());
        }

        return buff.toString();
    }

    /** To make serialization work across new versions */
    static final long serialVersionUID = -2354670272946948354L;

    /** The total miss mark */
    private transient int miss_total = 0;

    /** The number of verses checked */
    private transient int linked = 0;

    /** The Bible that we search in */
    private transient Bible bible;

    /** The thing we use to generate matches */
    private transient Matcher engine;

    /** The link data */
    private Link[][][] links;

    /** The number of links we record for each chapter */
    public static final int LINKS_PER_CHAPTER = 200;

    /** The log stream */
    protected static Logger log = Logger.getLogger(LinkArray.class);
}
