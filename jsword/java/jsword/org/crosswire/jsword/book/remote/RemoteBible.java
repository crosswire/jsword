
package org.crosswire.jsword.book.remote;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.passage.NoSuchVerseException;
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
public class RemoteBible extends AbstractBible
{
    /**
     * Basic constructor for a SerBible
     */
    public RemoteBible(RemoteBibleDriver driver, RemoteBibleMetaData rbmd)
    {
        this.driver = driver;
        this.rbmd = rbmd;

        log.debug("Started RemoteBible");
    }

    /**
     * Meta-Information: What version of the Bible is this?.
     * @return A Version for this Bible
     */
    public BibleMetaData getBibleMetaData()
    {
        return rbmd;
    }

    /**
     * Create an XML document for the specified Verses
     * @param doc The XML document
     * @param ele The elemenet to append to
     * @param ref The verses to search for
     */
    public BibleData getData(Passage ref) throws BookException
    {
        try
        {
            RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_GETDATA);
            method.addParam(RemoteConstants.PARAM_BIBLE, rbmd.getID());
            method.addParam(RemoteConstants.PARAM_PASSAGE, ref.getName());
            Document doc = driver.getRemoter().execute(method);

            return new DefaultBibleData(doc);
        }
        catch (RemoterException ex)
        {
            throw new BookException("remoting failure", ex);
        }
    }

    /**
     * For a given word find a list of references to it.
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(String word) throws BookException
    {
        try
        {
            RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_FINDPASSAGE);
            method.addParam(RemoteConstants.PARAM_BIBLE, rbmd.getID());
            method.addParam(RemoteConstants.PARAM_WORD, word);
            Document doc = driver.getRemoter().execute(method);

            return Converter.convertDocumentToPassage(doc);
        }
        catch (NoSuchVerseException ex)
        {
            throw new BookException("parse exception", ex);
        }
        catch (RemoterException ex)
        {
            throw new BookException("remoting failure", ex);
        }
    }

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string.
     * For example calling: <code>getStartsWith("love")</code>
     * will return something like: { "love", "loves", "lover", "lovely", ... }
     * @param base The word to base your word array on
     * @return An array of words starting with the base
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        try
        {
            RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_STARTSWITH);
            method.addParam(RemoteConstants.PARAM_BIBLE, rbmd.getID());
            method.addParam(RemoteConstants.PARAM_WORD, word);
            Document doc = driver.getRemoter().execute(method);

            return Converter.convertDocumentToStartsWith(doc);
        }
        catch (RemoterException ex)
        {
            throw new BookException("remoting failure", ex);
        }
    }

    /**
     * The bible driver from which we can request remote services
     */
    private RemoteBibleDriver driver;

    /**
     * The Version of the Bible that this produces
     */
    private RemoteBibleMetaData rbmd;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(RemoteBible.class);
}
