
package org.crosswire.jsword.book.remote;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.basic.BasicBookMetaData;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.passage.Passage;
import org.jdom.Document;

/**
 * A Biblical source that comes from some form of remoting code. The remoting
 * mechanism is defined by an implementation of RemoteBibleDriver.
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
 * @see org.crosswire.jsword.book.remote.RemoteBibleDriver#getXML(String)
 */
public class RemoteBible implements Bible
{
    /**
     * Basic constructor for a SerBible
     */
    public RemoteBible(RemoteBibleDriver driver, String name, BasicBookMetaData version)
    {
        this.driver = driver;
        this.name = name;
        this.version = version;

        log.debug("Started RemoteBible");
    }

    /**
     * What driver is controlling this Bible?
     * @return A BibleDriver relevant to this Bible
     */
    public BibleDriver getDriver()
    {
        return driver;
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
     * @param ele The elemenet to append to
     * @param ref The verses to search for
     */
    public BibleData getData(Passage ref) throws BookException
    {
        Document doc = driver.getXML("method=getData&bible="+name+"&passage="+ref.getName());
        return new DefaultBibleData();
    }

    /**
     * For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(String word) throws BookException
    {
        return null;
    }

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * @param base The word to base your word array on
     * @return An array of words starting with the base
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        return null;
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
        return null;
    }

    /**
     * @see org.crosswire.jsword.book.Bible#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key arg0) throws BookException
    {
        return null;
    }

    /**
     * @see org.crosswire.jsword.book.Bible#find(java.lang.String)
     */
    public Key find(String arg0) throws BookException
    {
        return null;
    }

    /**
     * The bible driver from which we can request remote services
     */
    private RemoteBibleDriver driver;

    /**
     * The name of this version
     */
    private String name;

    /**
     * The Version of the Bible that this produces
     */
    private BookMetaData version;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(RemoteBible.class);
}
