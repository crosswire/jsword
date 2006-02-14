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
 * ID: $Id$
 */
package org.crosswire.jsword.book.filter.thml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.DataPolice;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * To convert SAX events into OSIS events.
 *
 * <p>I used the THML ref page:
 * <a href="http://www.ccel.org/ThML/ThML1.04.htm">http://www.ccel.org/ThML/ThML1.04.htm</a>
 * to work out what the tags meant.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CustomHandler extends DefaultHandler
{
    /**
     * Simple ctor
     */
    public CustomHandler()
    {
        stack = new LinkedList();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException
    {
        Element ele = null;

        // If we are looking at the root element
        // then the stack is empty
        if (stack.size() > 0)
        {
            ele = (Element) stack.getFirst();
            // If the element and its descendants are to be ignored
            // then there is a null element on the stack
            if (ele == null)
            {
                return;
            }
        }

        Tag t = getTag(localname, qname);

        stack.addFirst(t.processTag(ele, attrs));
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] data, int offset, int length)
    {
        // What we are adding to
        Element current = (Element) stack.getFirst();

        // If the element and its descendants are to be ignored
        // then there is a null element on the stack
        if (current == null)
        {
            return;
        }

        int size = current.getContentSize();

        // what we are adding
        String text = 
            new String(data, offset, length);
        // If the last element in the list is a string then we should add
        // this string on to the end of it rather than add a new list item
        // because (probably as an atrifact of the HTML/XSL transform we get
        // a space inserted in the output even when 2 calls to this method
        // split a word.
        if (size > 0)
        {
            Content last = current.getContent(size - 1);
            if (last instanceof Text)
            {
                current.removeContent(size - 1);
                text = ((Text) last).getText() + text;
            }
        }

        current.addContent(new Text(text));
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localname, String qname)
    {
        // When we are done processing an element we need to remove
        // it from the stack so that nothing more is attached to it.
        Element finished = (Element) stack.removeFirst();
        Tag t = getTag(localname, qname);
        t.processContent(finished);

        // If it was the last element then it was the root element
        // so save it
        if (stack.size() == 0)
        {
            rootElement = finished;
        }
    }

    public Element getRootElement()
    {
        return rootElement;
    }

    private Tag getTag(String localname, String qname)
    {
        Tag t = (Tag) TAG_MAP.get(qname);
    
        // Some of the THML books are broken in that they use uppercase
        // element names, which the spec disallows, but we might as well
        // look out for them
        if (t == null)
        {
            t = (Tag) TAG_MAP.get(qname.toLowerCase());
    
            if (t == null)
            {
                log.warn("unknown thml element: " + localname + " qname=" + qname); //$NON-NLS-1$ //$NON-NLS-2$
                return t;
            }
    
            DataPolice.report("Wrong case used in thml element: " + qname); //$NON-NLS-1$
        }
        return t;
    }

    /**
     * When the document is parsed,
     * this is the last element popped off the stack.
     */
    private Element rootElement;

    /**
     * The stack of elements that we have created
     */
    private LinkedList stack;

    /**
     * The known tag types
     */
    private static final Map TAG_MAP = new HashMap();

    static {
        Tag[] tags = new Tag[]
        {
            new ATag(),
            new BlockquoteTag(),
            new BrTag(),
            new BTag(),
            new CenterTag(),
            new CitationTag(),
            new DivTag(),
            new ForeignTag(),
            new FontTag(),
            new HrTag(),
            new ITag(),
            new LiTag(),
            new NoteTag(),
            new NameTag(),
            new OlTag(),
            new PTag(),
            new PbTag(),
            new RootTag(),
            new ScriptureTag(),
            new ScripRefTag(),
            new SmallTag(),
            new SupTag(),
            new SyncTag(),
            new TableTag(),
            new TdTag(),
            new TermTag(),
            new ThTag(),
            new TrTag(),
            new UTag(),
            new UlTag(),
            new AliasTag("h1", new BTag()), //$NON-NLS-1$
            new AliasTag("h2", new BTag()), //$NON-NLS-1$
            new AliasTag("h3", new BTag()), //$NON-NLS-1$
            new AliasTag("h4", new BTag()), //$NON-NLS-1$
            new AliasTag("dl", new UlTag()), //$NON-NLS-1$
            new AliasTag("dd", new LiTag()), //$NON-NLS-1$
            new AliasTag("dt", new LiTag()), //$NON-NLS-1$
            new IgnoreTag("img"), //$NON-NLS-1$
            new IgnoreTag("span"), //$NON-NLS-1$
            new IgnoreTag("dir"), //$NON-NLS-1$
            new IgnoreTag("pre"), //$NON-NLS-1$
        };
        for (int i = 0; i < tags.length; i++)
        {
            Tag t = tags[i];
            String tagName = t.getTagName();
            TAG_MAP.put(tagName, t);
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(CustomHandler.class);
}
