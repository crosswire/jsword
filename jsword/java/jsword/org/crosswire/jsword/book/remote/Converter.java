
package org.crosswire.jsword.book.remote;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * A set of converters to help implementing Bible[Driver] using XML as an
 * intermediate format for remoting.
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
public class Converter
{
    /**
     * Converter for calls to getBibleNames().
     * <p>The XML reply is expected to be in the form: (swapping ] and > for readibility)
     * <pre>
     * [root]
     *   [metadata id="uid"]
     *     [name]King James Version[/name]
     *     [edition][/edition]
     *     [initials]KJV[/initials]
     *     [pub]1900[/pub]
     *     [openness]PD[/openness]
     *   [/metadata]
     * [/root]
     * </pre>
     * @param doc
     * @return BibleMetaData[]
     */
    public static RemoteBibleMetaData[] convertDocumentToBibleMetaDatas(BookDriver driver, Document doc, Remoter remoter, int speed) throws ConverterException
    {
        try
        {
            Element root = doc.getRootElement();
            List bmds = root.getChildren("metadata");
            RemoteBibleMetaData[] rbmds = new RemoteBibleMetaData[bmds.size()];
            int i = 0;
            
            for (Iterator it = bmds.iterator(); it.hasNext();)
            {
                Element bmdele = (Element) it.next();
            
                String id = bmdele.getAttributeValue("id");
                String name = bmdele.getChildTextTrim("name");
                String edition = bmdele.getChildTextTrim("edition");
                String initials = bmdele.getChildTextTrim("initials");
                String pub = bmdele.getChildTextTrim("pub");
                String open = bmdele.getChildTextTrim("openness");
                String licence = bmdele.getChildTextTrim("licence");
            
                rbmds[i++] = new RemoteBibleMetaData(driver, remoter, id, name, edition, initials, pub, open, licence, speed);
            }
            
            return rbmds;
        }
        catch (MalformedURLException ex)
        {
            throw new ConverterException(Msg.CONVERT_BMD, ex);
        }
        catch (ParseException ex)
        {
            throw new ConverterException(Msg.CONVERT_BMD, ex);
        }
    }

    /**
     * Reverse of convertDocumentToBibleMetaDatas().
     * @see Converter#convertDocumentToBibleMetaDatas(BookDriver, Document, Remoter, int)
     */
    public static Document convertBibleMetaDatasToDocument(BibleMetaData[] bmds, String[] ids) throws ConverterException
    {
        if (bmds.length != ids.length)
        {
            throw new LogicError();
        }

        Element root = new Element("root");
        for (int i = 0; i < bmds.length; i++)
        {
            BibleMetaData bmd = bmds[i];

            Element bmdele = new Element("metadata");

            bmdele.setAttribute("id", ids[i]);
            bmdele.addContent(new Element("name").addContent(bmd.getName()));
            bmdele.addContent(new Element("edition").addContent(bmd.getEdition()));
            bmdele.addContent(new Element("initials").addContent(bmd.getInitials()));

            String pubstr = AbstractBookMetaData.formatPublishedDate(bmd.getFirstPublished());
            if (pubstr != null)
            {
                bmdele.addContent(new Element("pub").addContent(pubstr));
            }

            Openness open = bmd.getOpenness();
            if (open != null)
            {
                bmdele.addContent(new Element("openness").addContent(open.toString()));
            }

            if (bmd.getLicence() != null)
            {
                bmdele.addContent(new Element("licence").addContent(bmd.getLicence().toExternalForm()));
            }

            root.addContent(bmdele);
        }

        return new Document(root);
    }

    /**
     * Converter for calls to findPassage().
     * <p>The XML reply is expected to be in the form: (swapping ] and > for readibility)
     * <pre>
     * [root]
     *   [ref]Gen 1:1, Mat 1:1[/ref]
     * [/root]
     * </pre>
     * @param doc
     * @return Passage
     * @throws NoSuchVerseException
     */
    public static Passage convertDocumentToPassage(Document doc) throws ConverterException
    {
        String refstr = null;

        try
        {
            Element root = doc.getRootElement();
            refstr = root.getChild("ref").getTextTrim();
            
            return PassageFactory.createPassage(refstr);
        }
        catch (NoSuchVerseException ex)
        {
            throw new ConverterException(Msg.CONVERT_NOVERSE, ex, new Object[] { refstr });
        }
    }

    /**
     * Reverse of convertDocumentToPassage().
     * @see Converter#convertDocumentToPassage(Document)
     */
    public static Document convertPassageToDocument(Passage ref) throws ConverterException
    {
        Element root = new Element("root");
        root.addContent(new Element("ref").addContent(ref.getName()));
        return new Document(root);
    }

    /**
     * Converter for calls to getStartsWith().
     * <p>The XML reply is expected to be in the form: (swapping ] and > for readibility)
     * <pre>
     * [root]
     *   [word]love[/word]
     *   [word]loves[/word]
     *   ...
     * [/root]
     * </pre>
     * @param doc
     * @return Iterator
     */
    public static Iterator convertDocumentToStartsWith(Document doc) throws ConverterException
    {
        List words = new ArrayList();

        Element root = doc.getRootElement();
        List wordeles = root.getChildren("word");

        Iterator it = wordeles.iterator();
        while (it.hasNext())
        {
            Element wordele = (Element) it.next();
            words.add(wordele.getTextTrim());
        }

        return words.iterator();
    }

    /**
     * Reverse of convertDocumentToStartsWith().
     * @see Converter#convertDocumentToStartsWith(Document)
     */
    public static Document convertStartsWithToDocument(Iterator it) throws ConverterException
    {
        Element root = new Element("root");
        while (it.hasNext())
        {
            String word = (String) it.next();
            root.addContent(new Element("word").addContent(word));
        }
        return new Document(root);
    }

    /**
     * Throw an exception if this document represents one, do nothing otherwise
     * @param doc The document to test
     */
    public static void testRethrow(Document doc) throws RemoterException, ConverterException
    {
        if (doc.getRootElement().getName().equals("exception"))
        {
            throw convertDocumentToException(doc);
        }
    }

    /**
     * Converter for Exceptions.
     * Right now this simply creates a RemoterException with the message from
     * the original, however we could concevably re-create the original.
     * <p>The XML reply is expected to be in the form: (swapping ] and > for readibility)
     * <pre>
     * [exception]
     *   [type]java.lang.NullPointerException[/type]
     *   [message]bust[/message]
     *   [trace]...[/trace]
     * [/exception]
     * </pre>
     */
    public static RemoterException convertDocumentToException(Document doc) throws ConverterException
    {
        String typename = null;

        try
        {
            Element exce = doc.getRootElement();
            String message = exce.getChildTextTrim("message");
            typename = exce.getChildTextTrim("type");
            
            Class type = Class.forName(typename);
            
            return new RemoterException(message, type);
        }
        catch (ClassNotFoundException ex)
        {
            throw new ConverterException(Msg.CONVERT_NOCLASS, ex, new Object[] { typename });
        }
    }

    /**
     * Reverse of convertDocumentToException().
     * @see Converter#convertDocumentToException(Document)
     */
    public static Document convertExceptionToDocument(Throwable ex) throws ConverterException
    {
        Element exce = new Element("exception");

        StringWriter buffer = new StringWriter();
        ex.printStackTrace(new PrintWriter(buffer));

        exce.addContent(new Element("type").addContent(ex.getClass().getName()));
        exce.addContent(new Element("message").addContent(ex.getMessage()));

        return new Document(exce);
    }
}
