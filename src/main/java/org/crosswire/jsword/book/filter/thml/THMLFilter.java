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
package org.crosswire.jsword.book.filter.thml;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Filter to convert THML to OSIS format.
 *
 * <p>
 * I used the THML ref page: <a
 * href="http://www.ccel.org/ThML/ThML1.04.htm">http
 * ://www.ccel.org/ThML/ThML1.04.htm</a> to work out what the tags meant.
 *
 * LATER(joe): check nesting on these THML elements
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class THMLFilter implements SourceFilter {
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public List<Content> toOSIS(Book book, Key key, String plain) {
        Element ele = cleanParse(book, key, plain);

        if (ele == null) {
            if (error instanceof SAXParseException) {
                SAXParseException spe = (SAXParseException) error;
                int colNumber = spe.getColumnNumber();
                int start = Math.max(0, colNumber - 40);
                int stop = Math.min(finalInput.length(), colNumber + 40);
                int here = stop - start;
                log.warn("Could not fix {}({}) by {}: Error here({},{},{}): {}",
                         book.getInitials(),
                         key.getName(),
                         errorMessage,
                         Integer.toString(colNumber),
                         Integer.toString(finalInput.length()),
                         Integer.toString(here),
                         finalInput.substring(start, stop));
            } else {
                log.warn("Could not fix {}({}) by {}: {}",
                         book.getInitials(),
                         key.getName(),
                         errorMessage,
                         error.getMessage());
            }
            ele = OSISUtil.factory().createP();
        }

        return ele.removeContent();
    }

    @Override
    public THMLFilter clone() {
        THMLFilter clone = null;
        try {
            clone = (THMLFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    private Element cleanParse(Book book, Key key, String plain) {
        // So just try to strip out all XML looking things
        String clean = XMLUtil.cleanAllEntities(plain);
        Element ele = parse(book, key, clean, "cleaning entities");

        if (ele == null) {
            ele = cleanText(book, key, clean);
        }

        return ele;
    }

    private Element cleanText(Book book, Key key, String plain) {
        // So just try to strip out all XML looking things
        String clean = XMLUtil.cleanAllCharacters(plain);
        Element ele = parse(book, key, clean, "cleaning text");

        if (ele == null) {
            ele = parse(book, key, XMLUtil.closeEmptyTags(clean), "closing empty tags");
        }

        if (ele == null) {
            ele = cleanTags(book, key, clean);
        }

        return ele;
    }

    private Element cleanTags(Book book, Key key, String plain) {
        // So just try to strip out all XML looking things
        String clean = XMLUtil.cleanAllTags(plain);
        return parse(book, key, clean, "cleaning tags");
    }

    private Element parse(Book book, Key key, String plain, String failMessage) {
        Exception ex = null;
        // We need to create a root element to house our document fragment
        // 15 for the tags we add
        StringBuilder buf = new StringBuilder(15 + plain.length());
        buf.append('<').append(RootTag.TAG_ROOT).append('>').append(plain).append("</").append(RootTag.TAG_ROOT).append('>');
        finalInput = buf.toString();
        try {
            StringReader in = new StringReader(finalInput);
            InputSource is = new InputSource(in);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            CustomHandler handler = new CustomHandler(book, key);

            parser.parse(is, handler);
            return handler.getRootElement();
        } catch (SAXParseException e) {
            ex = e;
        } catch (SAXException e) {
            ex = e;
        } catch (IOException e) {
            ex = e;
        } catch (ParserConfigurationException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            // JDOM has a few exceptions which are all derived from this.
            ex = e;
        }

        errorMessage = failMessage;
        error = ex;
        return null;
    }

    private String errorMessage;
    private Exception error;
    private String finalInput;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(THMLFilter.class);
}
