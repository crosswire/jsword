package org.crosswire.jsword.book.remote;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Openness;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
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
    private static final String ELEMENT_MESSAGE = "message"; //$NON-NLS-1$
    private static final String ELEMENT_EXCEPTION = "exception"; //$NON-NLS-1$
    private static final String ELEMENT_WORD = "word"; //$NON-NLS-1$
    private static final String ELEMENT_REF = "ref"; //$NON-NLS-1$
    private static final String ELEMENT_ROOT = "root"; //$NON-NLS-1$
    private static final String ELEMENT_TYPE = "type"; //$NON-NLS-1$
    private static final String ELEMENT_LICENCE = "licence"; //$NON-NLS-1$
    private static final String ELEMENT_OPENNESS = "openness"; //$NON-NLS-1$
    private static final String ELEMENT_PUB = "pub"; //$NON-NLS-1$
    private static final String ELEMENT_EDITION = "edition"; //$NON-NLS-1$
    private static final String ELEMENT_NAME = "name"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    private static final String ELEMENT_METADATA = "metadata"; //$NON-NLS-1$

    /**
     * Prevent Instansiation
     */
    private Converter()
    {
    }

    /**
     * Converter for calls to getBookNames().
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
    public static BookMetaData[] convertDocumentToBookMetaDatas(RemoteBookDriver driver, Document doc, Remoter remoter, int speed) throws ConverterException
    {
        try
        {
            Element root = doc.getRootElement();
            List bmds = root.getChildren(ELEMENT_METADATA);
            BookMetaData[] rbmds = new BookMetaData[bmds.size()];
            int i = 0;

            for (Iterator it = bmds.iterator(); it.hasNext();)
            {
                Element bmdele = (Element) it.next();

                String id = bmdele.getAttributeValue(ATTRIBUTE_ID);

                String name = bmdele.getChildTextTrim(ELEMENT_NAME);
                String edition = bmdele.getChildTextTrim(ELEMENT_EDITION);
                String pubstr = bmdele.getChildTextTrim(ELEMENT_PUB);
                String openstr = bmdele.getChildTextTrim(ELEMENT_OPENNESS);
                String licencestr = bmdele.getChildTextTrim(ELEMENT_LICENCE);
                String typestr = bmdele.getChildTextTrim(ELEMENT_TYPE);

                BookType type = BookType.get(typestr);

                Book book = new RemoteBook(remoter, driver, name, type, edition, pubstr, openstr, licencestr, speed);

                BookMetaData bmd = book.getBookMetaData();
                driver.registerID(id, bmd);

                rbmds[i++] = bmd;
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
     * @see Converter#convertDocumentToBookMetaDatas(RemoteBookDriver, Document, Remoter, int)
     */
    public static Document convertBookMetaDatasToDocument(BookMetaData[] bmds, String[] ids)
    {
        assert bmds.length != ids.length;

        Element root = new Element(ELEMENT_ROOT);
        for (int i = 0; i < bmds.length; i++)
        {
            BookMetaData bmd = bmds[i];

            Element bmdele = new Element(ELEMENT_METADATA);
            Element temp = null;

            bmdele.setAttribute(ATTRIBUTE_ID, ids[i]);
            temp = new Element(ELEMENT_NAME);
            temp.addContent(bmd.getName());
            bmdele.addContent(temp);

            temp = new Element(ELEMENT_EDITION);
            temp.addContent(bmd.getEdition());
            bmdele.addContent(temp);

            String pubstr = DefaultBookMetaData.formatPublishedDate(bmd.getFirstPublished());
            if (pubstr != null)
            {
                temp = new Element(ELEMENT_PUB);
                temp.addContent(pubstr);
                bmdele.addContent(temp);
            }

            Openness open = bmd.getOpenness();
            if (open != null)
            {
                temp = new Element(ELEMENT_OPENNESS);
                temp.addContent(open.toString());
                bmdele.addContent(temp);
            }

            if (bmd.getLicence() != null)
            {
                temp = new Element(ELEMENT_LICENCE);
                temp.addContent(bmd.getLicence().toExternalForm());
                bmdele.addContent(temp);
            }

            root.addContent(bmdele);
        }

        return new Document(root);
    }

    /**
     * Converter for calls to findPassage().
     * <p>The XML reply is expected to be in the form: (swapping &lt; and > for readibility)
     * <pre>
     * [root]
     *   [ref]Gen 1:1, Mat 1:1[/ref]
     * [/root]
     * </pre>
     * @param doc The document to convert
     */
    public static Key convertDocumentToKeyList(Document doc, Book book) throws ConverterException
    {
        String refstr = null;

        try
        {
            Element root = doc.getRootElement();
            refstr = root.getChild(ELEMENT_REF).getTextTrim();

            Key key = new DefaultKeyList();
            key.addAll(book.getKey(refstr));
            return key;
        }
        catch (NoSuchKeyException ex)
        {
            throw new ConverterException(Msg.CONVERT_NOVERSE, ex, new Object[] { refstr });
        }
    }

    /**
     * Reverse of convertDocumentToPassage().
     * @see Converter#convertDocumentToKeyList(Document, Book)
     */
    public static Document convertKeyListToDocument(Key key)
    {
        Element root = new Element(ELEMENT_ROOT);
        Element temp = new Element(ELEMENT_REF);
        temp.addContent(key.getName());
        root.addContent(temp);
        return new Document(root);
    }

    /**
     * Converter for calls to getStartsWith().
     * <p>The XML reply is expected to be in the form: (swapping &lt; and > for readibility)
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
    public static Iterator convertDocumentToStartsWith(Document doc)
    {
        List words = new ArrayList();

        Element root = doc.getRootElement();
        List wordeles = root.getChildren(ELEMENT_WORD);

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
    public static Document convertStartsWithToDocument(Iterator it)
    {
        Element root = new Element(ELEMENT_ROOT);

        while (it.hasNext())
        {
            String word = (String) it.next();
            Element temp = new Element(ELEMENT_WORD);
            temp.addContent(word);
            root.addContent(temp);
        }

        return new Document(root);
    }

    /**
     * Throw an exception if this document represents one, do nothing otherwise
     * @param doc The document to test
     */
    public static void testRethrow(Document doc) throws RemoterException, ConverterException
    {
        if (doc.getRootElement().getName().equals(ELEMENT_EXCEPTION))
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
            String message = exce.getChildTextTrim(ELEMENT_MESSAGE);
            typename = exce.getChildTextTrim(ELEMENT_TYPE);

            Class type = Class.forName(typename);
            
            return new RemoterException(Msg.REMOTE_NOSUPPORT, new Object[] { message, type });
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
    public static Document convertExceptionToDocument(Throwable ex)
    {
        Element exce = new Element(ELEMENT_EXCEPTION);
        Element temp = null;

        StringWriter buffer = new StringWriter();
        ex.printStackTrace(new PrintWriter(buffer));

        temp = new Element(ELEMENT_TYPE);
        temp.addContent(ex.getClass().getName());
        exce.addContent(temp);

        temp = new Element(ELEMENT_MESSAGE);
        temp.addContent(ex.getMessage());
        exce.addContent(temp);

        return new Document(exce);
    }
}