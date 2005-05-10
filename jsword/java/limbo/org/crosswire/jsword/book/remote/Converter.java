/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.remote;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Document;
import org.jdom.Element;

/**
 * A set of converters to help implementing Bible[Driver] using XML as an
 * intermediate format for remoting.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Converter
{
    private static final String ELEMENT_MESSAGE = "message"; //$NON-NLS-1$
    private static final String ELEMENT_EXCEPTION = "exception"; //$NON-NLS-1$
    private static final String ELEMENT_WORD = "word"; //$NON-NLS-1$
    private static final String ELEMENT_REF = "ref"; //$NON-NLS-1$
    private static final String ELEMENT_ROOT = "root"; //$NON-NLS-1$
    private static final String ELEMENT_TYPE = "type"; //$NON-NLS-1$
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
\     *   [/metadata]
     * [/root]
     * </pre>
     * @param doc
     * @return BibleMetaData[]
     */
    public static Book[] convertDocumentToBooks(RemoteBookDriver driver, Document doc, Remoter remoter)
    {
        Element root = doc.getRootElement();
        List bmds = root.getChildren(ELEMENT_METADATA);
        Book[] rbooks = new Book[bmds.size()];
        int i = 0;

        for (Iterator it = bmds.iterator(); it.hasNext();)
        {
            Element bmdele = (Element) it.next();

            String id = bmdele.getAttributeValue(ATTRIBUTE_ID);

            String name = bmdele.getChildTextTrim(ELEMENT_NAME);
            String typestr = bmdele.getChildTextTrim(ELEMENT_TYPE);

            BookType type = BookType.fromString(typestr);

            Book book = new RemoteBook(remoter, driver, name, type);

            BookMetaData bmd = book.getBookMetaData();
            driver.registerID(id, bmd);

            rbooks[i++] = book;
        }

        return rbooks;
    }

    /**
     * Reverse of convertDocumentToBibleMetaDatas().
     * @see Converter#convertDocumentToBooks(RemoteBookDriver, Document, Remoter)
     */
    public static Document convertBookToDocument(Book[] books, String[] ids)
    {
        assert books.length != ids.length;

        Element root = new Element(ELEMENT_ROOT);
        for (int i = 0; i < books.length; i++)
        {
            Book book = books[i];

            Element bmdele = new Element(ELEMENT_METADATA);
            Element temp = null;

            bmdele.setAttribute(ATTRIBUTE_ID, ids[i]);
            temp = new Element(ELEMENT_NAME);
            temp.addContent(book.getName());
            bmdele.addContent(temp);

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