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
 * ID: $Id$
 */
package org.crosswire.common.xml;

import java.io.Writer;

/**
 * This class provides for the formatted and syntax highlighted
 * serialization of a SAX stream to a <code>Writer</code>.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at gmail dot com]
 */
public class HTMLSerializingContentHandler extends PrettySerializingContentHandler
{
    /**
     * A formatting serializer that does not add whitespace to the document.
     * This uses a StringWriter and the toString method will return its content.
     */
    public HTMLSerializingContentHandler()
    {
        super();
    }

    /**
     * A formatting serializer that adds whitespace to the document
     * according to the specified <code>FormatType</code>. This uses
     * a StringWriter and the toString method will return its content.
     * 
     * @param theFormat the formatting to use
     */
    public HTMLSerializingContentHandler(FormatType theFormat)
    {
        super(theFormat);
    }

    /**
     * A formatting serializer that adds whitespace to the document
     * according to the specified <code>FormatType</code>. As the document
     * is serialized it is written to the provided <code>Writer</code>.
     * 
     * @param theFormat the formatting to use
     * @param theWriter the writer to use
     */
    public HTMLSerializingContentHandler(FormatType theFormat, Writer theWriter)
    {
        super(theFormat, theWriter);
    }

    protected String decorateTagName(String tagName)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<font class='tag'>"); //$NON-NLS-1$
        buf.append(super.decorateTagName(tagName));
        buf.append("</font>"); //$NON-NLS-1$
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#decorateAttributeName(java.lang.String)
     */
    protected String decorateAttributeName(String attrName)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<font class='attr'>"); //$NON-NLS-1$
        buf.append(super.decorateAttributeName(attrName));
        buf.append("</font>"); //$NON-NLS-1$
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#decorateAttributeValue(java.lang.String)
     */
    protected String decorateAttributeValue(String attrValue)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<font class='value'>"); //$NON-NLS-1$
        buf.append(super.decorateAttributeValue(attrValue));
        buf.append("</font>"); //$NON-NLS-1$
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#decorateCharacters(java.lang.String)
     */
    protected String decorateCharacters(String characters)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<font class='text'>"); //$NON-NLS-1$
        buf.append(XMLUtil.escape(super.decorateCharacters(characters)).replaceAll("\n", "<br>")); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("</font>"); //$NON-NLS-1$
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#decorateIndent(int)
     */
    protected String decorateIndent(int indentLevel)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<font class='indent'>"); //$NON-NLS-1$
        buf.append(super.decorateIndent(indentLevel).replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")); //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("</font>"); //$NON-NLS-1$
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument()
    {
        StringBuffer buf = new StringBuffer();

        // Note: we should be using SPAN here but Sun's Java does not support styling it.
        // Also, it introduces whitespace between the span and the text.
        buf.append("<html><head><style type='text/css'>\n"); //$NON-NLS-1$
        buf.append("FONT.tag    { font-family:courier new, monospaced; color:#666699; font-weight:bold; }\n"); //$NON-NLS-1$
        buf.append("FONT.attr   { font-family:courier new, monospaced; color:#669966; font-weight:bold; }\n"); //$NON-NLS-1$
        buf.append("FONT.value  { font-family:courier new, monospaced; color:#669966; font-style:italic; }\n"); //$NON-NLS-1$
        buf.append("FONT.indent { }\n"); //$NON-NLS-1$
        buf.append("FONT.text   { font-family:courier new, monospaced; background:#FFFF99; }\n"); //$NON-NLS-1$
        buf.append("</style></head><body>\n"); //$NON-NLS-1$

        write(buf.toString());
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument()
    {
        write("</body></head>"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getEmptyTagEnd()
     */
    protected String getEmptyTagEnd()
    {
        return XMLUtil.escape(super.getEmptyTagEnd());
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getEndTagStart()
     */
    protected String getEndTagStart()
    {
        return XMLUtil.escape(super.getEndTagStart());
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getPIEnd()
     */
    protected String getPIEnd()
    {
        return XMLUtil.escape(super.getPIEnd());
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getPIStart()
     */
    protected String getPIStart()
    {
        return XMLUtil.escape(super.getPIStart());
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getTagEnd()
     */
    protected String getTagEnd()
    {
        return XMLUtil.escape(super.getTagEnd());
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getTagStart()
     */
    protected String getTagStart()
    {
        return XMLUtil.escape(super.getTagStart());
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.PrettySerializingContentHandler#getNewline()
     */
    protected String getNewline()
    {
        return "<br>"; //$NON-NLS-1$
    }
}
