/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.MessageFormat;

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
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class DictToOsis {
    /**
     * The name of a Bible to find
     */
    private static final String BOOK_NAME = "WebstersDict";

    /**
     * @param args
     * @throws BookException 
     * @throws IOException 
     */
    public static void main(String[] args) throws BookException, IOException {
        new DictToOsis().dump(BOOK_NAME);
    }

    public void dump(String name) throws BookException, IOException {
        Books books = Books.installed();
        Book book = books.getBook(name);
        BookMetaData bmd = book.getBookMetaData();
        StringBuffer buf = new StringBuffer();

        Key keys = book.getGlobalKeyList();

        buildDocumentOpen(buf, bmd);

        // Get a verse iterator
        for (Key key : keys) {
            BookData bdata = new BookData(book, key);
            SAXEventProvider osissep = bdata.getSAXEventProvider();
            try {
                buildEntryOpen(buf, key.getName(), XMLUtil.writeToString(osissep));
            } catch (SAXException e) {
                e.printStackTrace(System.err);
            }
        }

        buildDocumentClose(buf);

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(bmd.getInitials() + ".xml"), "UTF-8");
            writer.write(buf.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        XMLProcess parser = new XMLProcess();
        // parser.getFeatures().setFeatureStates("-s", "-f", "-va", "-dv");
        parser.parse(bmd.getInitials() + ".xml");
    }

    private void buildDocumentOpen(StringBuffer buf, BookMetaData bmd) {
        MessageFormat msgFormat = new MessageFormat(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<osis\n  xmlns=\"http://www.bibletechnologies.net/2003/OSIS/namespace\"\n  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n  xsi:schemaLocation=\"http://www.bibletechnologies.net/2003/OSIS/namespace osisCore.2.1.xsd\">\n<osisText osisIDWork=\"{0}\" osisRefWork=\"defaultReferenceScheme\" xml:lang=\"en\">\n  <header>\n    <work osisWork=\"{0}\">\n      <title>{1}</title>\n      <identifier type=\"OSIS\">Dict.{0}</identifier>\n      <refSystem>Dict.{0}</refSystem>\n    </work>\n    <work osisWork=\"defaultReferenceScheme\">\n      <refSystem>Dict.{0}</refSystem>\n    </work>\n  </header>\n<div>\n");
        msgFormat.format(new Object[] {
                bmd.getInitials(), bmd.getName()
        }, buf, pos);
    }

    private void buildDocumentClose(StringBuffer buf) {
        buf.append("</div>\n</osisText>\n</osis>\n");
    }

    private void buildEntryOpen(StringBuffer buf, String entryName, String entryDef) {
        String tmp = entryName;
        if (tmp.indexOf(' ') != -1) {
            tmp = "x";
        }
        MessageFormat msgFormat = new MessageFormat(
                "<div type=\"entry\" osisID=\"{0}\" canonical=\"true\"><seg type=\"x-form\"><seg type=\"x-orth\">{0}</seg></seg><seg type=\"x-def\">{1}</seg></div>\n");
        msgFormat.format(new Object[] {
                tmp, entryDef
        }, buf, pos);
    }

    private static FieldPosition pos = new FieldPosition(0);
}
