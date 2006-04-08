/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: APIExamples.java 1046 2006-03-12 21:31:48 -0500 (Sun, 12 Mar 2006) dmsmith $
 */
package org.crosswire.jsword.examples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Iterator;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.XMLProcess;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.xml.sax.SAXException;

/**
 * Start of a mechanism to extract a Dictionary module to OSIS.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DictToOsis
{
    /**
     * The name of a Bible to find
     */
    private static final String BOOK_NAME = "Thayer"; //$NON-NLS-1$

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new DictToOsis().dump(BOOK_NAME);
    }

    public void dump(String name)
    {
        Books books = Books.installed();
        Book book = books.getBook(name);
        BookMetaData bmd = book.getBookMetaData();
        StringBuffer buf = new StringBuffer();

        try
        {
            Key keys = book.getGlobalKeyList();

            buildDocumentOpen(buf, bmd);

            // Get a verse iterator
            Iterator iter = keys.iterator();
            while (iter.hasNext())
            {
                Key key = (Key) iter.next();
                BookData bdata = book.getData(key);
                SAXEventProvider osissep = bdata.getSAXEventProvider();
                try
                {
                    buildEntryOpen(buf, key.getName(), XMLUtil.writeToString(osissep));
                }
                catch (SAXException e)
                {
                    e.printStackTrace();
                }
            }

            buildDocumentClose(buf);

            Writer writer = new OutputStreamWriter(new FileOutputStream(bmd.getInitials() + ".xml"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
            writer.write(buf.toString());
            writer.close();
            XMLProcess parser = new XMLProcess();
//            parser.getFeatures().setFeatureStates("-s", "-f", "-va", "-dv"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            parser.parse(bmd.getInitials() + ".xml"); //$NON-NLS-1$
        }
        catch (BookException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void buildDocumentOpen(StringBuffer buf, BookMetaData bmd)
    {
        StringBuffer docBuffer = new StringBuffer();
        docBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"); //$NON-NLS-1$
        docBuffer.append("\n<osis"); //$NON-NLS-1$
        docBuffer.append("\n  xmlns=\"http://www.bibletechnologies.net/2003/OSIS/namespace\""); //$NON-NLS-1$
        docBuffer.append("\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");  //$NON-NLS-1$
        docBuffer.append("\n  xsi:schemaLocation=\"http://www.bibletechnologies.net/2003/OSIS/namespace osisCore.2.1.xsd\">"); //$NON-NLS-1$
        docBuffer.append("\n<osisText osisIDWork=\"{0}\" osisRefWork=\"defaultReferenceScheme\" xml:lang=\"en\">"); //$NON-NLS-1$
        docBuffer.append("\n  <header>"); //$NON-NLS-1$
        docBuffer.append("\n    <work osisWork=\"{0}\">"); //$NON-NLS-1$
        docBuffer.append("\n      <title>{1}</title>"); //$NON-NLS-1$
        docBuffer.append("\n      <identifier type=\"OSIS\">Dict.{0}</identifier>"); //$NON-NLS-1$
        docBuffer.append("\n      <refSystem>Dict.{0}</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n    </work>"); //$NON-NLS-1$
        docBuffer.append("\n    <work osisWork=\"defaultReferenceScheme\">"); //$NON-NLS-1$
        docBuffer.append("\n      <refSystem>Dict.{0}</refSystem>"); //$NON-NLS-1$
        docBuffer.append("\n    </work>"); //$NON-NLS-1$
        docBuffer.append("\n  </header>"); //$NON-NLS-1$
        docBuffer.append("\n<div>"); //$NON-NLS-1$
        docBuffer.append('\n');
        MessageFormat msgFormat = new MessageFormat(docBuffer.toString()); //$NON-NLS-1$
        msgFormat.format(new Object[] { bmd.getInitials(), bmd.getName() }, buf, pos);
    }

    private void buildDocumentClose(StringBuffer buf)
    {
        buf.append("</div>\n</osisText>\n</osis>\n"); //$NON-NLS-1$
    }

    private void buildEntryOpen(StringBuffer buf, String entryName, String entryDef)
    {
        if (entryName.indexOf(' ') != -1) //$NON-NLS-1$
        {
            entryName = "x"; //$NON-NLS-1$
        }
        MessageFormat msgFormat = new MessageFormat("<div type=\"entry\" osisID=\"{0}\" canonical=\"true\"><seg type=\"x-form\"><seg type=\"x-orth\">{0}</seg></seg><seg type=\"x-def\">{1}</seg></div>\n"); //$NON-NLS-1$
        msgFormat.format(new Object[] { entryName, entryDef }, buf, pos);
    }

    private static FieldPosition pos = new FieldPosition(0);
}
