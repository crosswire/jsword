
package org.crosswire.jsword.book.remote;

import org.apache.log4j.Logger;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.OsisUtil;
import org.crosswire.jsword.passage.Passage;
import org.jdom.Document;
import org.xml.sax.SAXException;

/**
 * A Biblical source that comes from some form of remoting code.
 * 
 * The remoting mechanism is defined by an implementation of RemoteBibleDriver.
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
public class RemoteBible extends AbstractBible
{
    /**
     * Basic constructor for a SerBible
     */
    public RemoteBible(Remoter remoter, RemoteBibleMetaData rbmd)
    {
        this.remoter = remoter;
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

            Document doc = remoter.execute(method);
            SAXEventProvider provider = new JDOMSAXEventProvider(doc);

            return OsisUtil.createBibleData(provider);
        }
        catch (RemoterException ex)
        {
            throw new BookException("remoting failure", ex);
        }
        catch (SAXException ex)
        {
            throw new BookException("remoting failure", ex);
        }
    }

    /**
     * For a given word find a list of references to it.
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(Search word) throws BookException
    {
        try
        {
            RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_FINDPASSAGE);
            method.addParam(RemoteConstants.PARAM_BIBLE, rbmd.getID());
            method.addParam(RemoteConstants.PARAM_WORD, word.getMatch());
            Document doc = remoter.execute(method);

            return Converter.convertDocumentToPassage(doc);
        }
        catch (ConverterException ex)
        {
            throw new BookException("parse exception", ex);
        }
        catch (RemoterException ex)
        {
            throw new BookException("remoting failure", ex);
        }
    }

    /**
     * So we can request remote services
     */
    private Remoter remoter;

    /**
     * The Version of the Bible that this produces
     */
    private RemoteBibleMetaData rbmd;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(RemoteBible.class);
}
