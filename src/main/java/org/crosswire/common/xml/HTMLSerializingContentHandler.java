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

import java.io.Writer;

/**
 * This class provides for the formatted and syntax highlighted serialization of
 * a SAX stream to a <code>Writer</code>.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class HTMLSerializingContentHandler extends PrettySerializingContentHandler {
    /**
     * A formatting serializer that does not add whitespace to the document.
     * This uses a StringWriter and the toString method will return its content.
     */
    public HTMLSerializingContentHandler() {
        super();
    }

    /**
     * A formatting serializer that adds whitespace to the document according to
     * the specified <code>FormatType</code>. This uses a StringWriter and the
     * toString method will return its content.
     * 
     * @param theFormat
     *            the formatting to use
     */
    public HTMLSerializingContentHandler(FormatType theFormat) {
        super(theFormat);
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
    public HTMLSerializingContentHandler(FormatType theFormat, Writer theWriter) {
        super(theFormat, theWriter);
    }

    @Override
    protected String decorateTagName(String tagName) {
        StringBuilder buf = new StringBuilder(50);
        buf.append("<font class='tag'>");
        buf.append(super.decorateTagName(tagName));
        buf.append("</font>");
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.crosswire.common.xml.PrettySerializingContentHandler#
     * decorateAttributeName(java.lang.String)
     */
    @Override
    protected String decorateAttributeName(String attrName) {
        StringBuilder buf = new StringBuilder(50);
        buf.append("<font class='attr'>");
        buf.append(super.decorateAttributeName(attrName));
        buf.append("</font>");
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.crosswire.common.xml.PrettySerializingContentHandler#
     * decorateAttributeValue(java.lang.String)
     */
    @Override
    protected String decorateAttributeValue(String attrValue) {
        StringBuilder buf = new StringBuilder(50);
        buf.append("<font class='value'>");
        buf.append(super.decorateAttributeValue(attrValue));
        buf.append("</font>");
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#decorateCharacters
     * (java.lang.String)
     */
    @Override
    protected String decorateCharacters(String characters) {
        StringBuilder buf = new StringBuilder(50);
        buf.append("<font class='text'>");
        buf.append(XMLUtil.escape(super.decorateCharacters(characters)).replaceAll("\n", "<br>"));
        buf.append("</font>");
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#decorateIndent
     * (int)
     */
    @Override
    protected String decorateIndent(int indentLevel) {
        StringBuilder buf = new StringBuilder(100);
        buf.append("<font class='indent'>");
        buf.append(super.decorateIndent(indentLevel).replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
        buf.append("</font>");
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    @Override
    public void startDocument() {
        // Note: we should be using SPAN here but Sun's Java does not support
        // styling it.
        // Also, it introduces whitespace between the span and the text.
        write("<html><head><style type='text/css'>\nFONT.tag    { font-family:courier new, monospaced; color:#666699; font-weight:bold; }\nFONT.attr   { font-family:courier new, monospaced; color:#669966; font-weight:bold; }\nFONT.value  { font-family:courier new, monospaced; color:#669966; font-style:italic; }\nFONT.indent { }\nFONT.text   { font-family:courier new, monospaced; background:#FFFF99; }\n</style></head><body>\n");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() {
        write("</body></head>");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#getEmptyTagEnd()
     */
    @Override
    protected String getEmptyTagEnd() {
        return XMLUtil.escape(super.getEmptyTagEnd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#getEndTagStart()
     */
    @Override
    protected String getEndTagStart() {
        return XMLUtil.escape(super.getEndTagStart());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getPIEnd()
     */
    @Override
    protected String getPIEnd() {
        return XMLUtil.escape(super.getPIEnd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#getPIStart()
     */
    @Override
    protected String getPIStart() {
        return XMLUtil.escape(super.getPIStart());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getTagEnd()
     */
    @Override
    protected String getTagEnd() {
        return XMLUtil.escape(super.getTagEnd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#getTagStart()
     */
    @Override
    protected String getTagStart() {
        return XMLUtil.escape(super.getTagStart());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.xml.PrettySerializingContentHandler#getNewline()
     */
    @Override
    protected String getNewline() {
        return "<br>";
    }
}
