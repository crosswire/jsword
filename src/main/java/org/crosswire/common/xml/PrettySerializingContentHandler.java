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
package org.crosswire.common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * This class provides for the formatted serialization of a SAX stream to a
 * <code>Writer</code>.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class PrettySerializingContentHandler implements ContentHandler {
    /**
     * A formatting serializer that does not add whitespace to the document.
     * This uses a StringWriter and the toString method will return its content.
     */
    public PrettySerializingContentHandler() {
        this(FormatType.AS_IS);
    }

    /**
     * A formatting serializer that adds whitespace to the document according to
     * the specified <code>FormatType</code>. This uses a StringWriter and the
     * toString method will return its content.
     * 
     * @param theFormat
     *            the formatting to use
     */
    public PrettySerializingContentHandler(FormatType theFormat) {
        this(theFormat, null);
    }

    /**
     * A formatting serializer that adds whitespace to the document according to
     * the specified <code>FormatType</code>. As the document is serialized it
     * is written to the provided <code>Writer</code>.
     * 
     * @param theFormat
     *            the formatting to use
     * @param theWriter
     *            the writer to use
     */
    public PrettySerializingContentHandler(FormatType theFormat, Writer theWriter) {
        formatting = theFormat;
        writer = theWriter == null ? new StringWriter() : theWriter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return writer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() {
        // write("<?xml version=\"1.0\"?>");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     * java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localname, String qname, Attributes attrs) {
        if (depth > 0) {
            handlePending();
        }

        write(getTagStart());
        write(decorateTagName(localname));

        for (int i = 0; i < attrs.getLength(); i++) {
            write(' ');
            write(decorateAttributeName(XMLUtil.getAttributeName(attrs, i)));
            write("='");
            write(decorateAttributeValue(XMLUtil.escape(attrs.getValue(i))));
            write('\'');
        }

        pendingEndTag = true;
        depth++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localname, String qname) {
        depth--;
        // Java cannot display empty tags <tag/> so most of the following is
        // commented out
        if (pendingEndTag) {
            if (formatting.isAnalytic() && depth > 0) {
                emitWhitespace(depth - 1);
            }
            //
            // // Hack alert JTextPane cannot handle <br/>
            //            if (localname.equalsIgnoreCase("br"))
            // {
            write(getTagEnd());
            // }
            // else
            // {
            // write(getEmptyTagEnd());
            // }
        }
        // else
        // {
        if (formatting.isClassic()) {
            emitWhitespace(depth);
        }

        write(getEndTagStart());

        write(decorateTagName(localname));

        if (formatting.isAnalytic()) {
            emitWhitespace(depth);
        }

        write(getTagEnd());
        // }
        pendingEndTag = false;
        lookingForChars = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] chars, int start, int length) {
        if (!lookingForChars) {
            handlePending();
        }

        String s = new String(chars, start, length);
        write(decorateCharacters(s));
        lookingForChars = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] chars, int start, int length) {
        characters(chars, start, length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
     * java.lang.String)
     */
    public void processingInstruction(String target, String data) {
        handlePending();

        write(getPIStart());
        write(target);
        write(' ');
        write(decorateCharacters(data));
        write(getPIEnd());

        if (formatting.isMultiline()) {
            write(getNewline());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) {
    }

    protected String getTagStart() {
        return "<";
    }

    protected String getTagEnd() {
        return ">";
    }

    protected String getEmptyTagEnd() {
        return "/>";
    }

    protected String getEndTagStart() {
        return "</";
    }

    protected String getPIStart() {
        return "<!";
    }

    protected String getPIEnd() {
        return "!>";
    }

    protected String getNewline() {
        return "\n";
    }

    protected String decorateTagName(String tagName) {
        return tagName;
    }

    protected String decorateAttributeName(String attrName) {
        return attrName;
    }

    protected String decorateAttributeValue(String attrValue) {
        return attrValue;
    }

    protected String decorateCharacters(String characters) {
        return characters;
    }

    protected String decorateIndent(int indentLevel) {
        return new String(indentation, 0, indentLevel).intern();
    }

    protected void write(String obj) {
        try {
            writer.write(obj);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    protected void write(char obj) {
        try {
            writer.write(obj);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void handlePending() {
        if (pendingEndTag) {
            pendingEndTag = false;

            if (formatting.isAnalytic()) {
                emitWhitespace(depth);
            }

            write(getTagEnd());

        }
        if (formatting.isClassic()) {
            emitWhitespace(depth);
        }
        lookingForChars = false;
    }

    private void emitWhitespace(int indentLevel) {
        write(getNewline());
        if (formatting.isIndented()) {
            write(decorateIndent(indentLevel));
        }
    }

    /**
     * This allows for rapid output of whitespace.
     */
    private static char[] indentation = {
            '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t',
            '\t', '\t', '\t', '\t', '\t', '\t',
    };

    /**
     * The depth is incremented on each startElement and decremented on each
     * endElement. This is used to output the indentation.
     */
    private int depth;

    /**
     * It is possible that characters(...) will be called for adjacent pieces of
     * text. Often this is due to entities in the text. This will allow for
     * these to be joined back together.
     */
    private boolean lookingForChars;

    /**
     * One of the difficulties in SAX parsing is that it does not retain state.
     * Even for an empty tag, it calls startElement and endElement. This allows
     * for making empty elements to have the empty tag notation: &lt;tag/&gt;.
     */
    private boolean pendingEndTag;

    private FormatType formatting;

    private Writer writer;
}
