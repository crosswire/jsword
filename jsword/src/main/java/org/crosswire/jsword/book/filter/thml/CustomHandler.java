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
import java.util.Locale;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.passage.Key;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * To convert SAX events into OSIS events.
 * 
 * <p>
 * I used the THML ref page: <a
 * href="http://www.ccel.org/ThML/ThML1.04.htm">http
 * ://www.ccel.org/ThML/ThML1.04.htm</a> to work out what the tags meant.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CustomHandler extends DefaultHandler {
    /**
     * Simple ctor
     */
    public CustomHandler(Book book, Key key) {
        this.book = book;
        this.key = key;
        stack = new LinkedList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    /* @Override */
    public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException {
        Element ele = null;

        // If we are looking at the root element
        // then the stack is empty
        if (!stack.isEmpty()) {
            Object top = stack.getFirst();

            // If the element and its descendants are to be ignored
            // then there is a null element on the stack
            if (top == null) {
                return;
            }

            if (top instanceof Element) // It might be a text element
            {
                ele = (Element) top;
            }
        }

        Tag t = getTag(localname, qname);

        if (t != null) {
            stack.addFirst(t.processTag(ele, attrs));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    /* @Override */
    public void characters(char[] data, int offset, int length) {
        // what we are adding
        String text = new String(data, offset, length);

        if (stack.isEmpty()) {
            stack.addFirst(new Text(text));
            return;
        }

        // What we are adding to
        Content top = (Content) stack.getFirst();

        // If the element and its descendants are to be ignored
        // then there is a null element on the stack
        if (top == null) {
            return;
        }

        if (top instanceof Text) {
            ((Text) top).append(text);
            return;
        }

        if (top instanceof Element) {
            Element current = (Element) top;

            int size = current.getContentSize();

            // If the last element in the list is a string then we should add
            // this string on to the end of it rather than add a new list item
            // because (probably as an artifact of the HTML/XSL transform we get
            // a space inserted in the output even when 2 calls to this method
            // split a word.
            if (size > 0) {
                Content last = current.getContent(size - 1);
                if (last instanceof Text) {
                    ((Text) last).append(text);
                    return;
                }
            }
            current.addContent(new Text(text));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    /* @Override */
    public void endElement(String uri, String localname, String qname) {
        if (stack.isEmpty()) {
            return;
        }
        // When we are done processing an element we need to remove
        // it from the stack so that nothing more is attached to it.
        Content top = (Content) stack.removeFirst();
        if (top instanceof Element) {
            Element finished = (Element) top;
            Tag t = getTag(localname, qname);

            if (t != null) {
                t.processContent(finished);
            }

            // If it was the last element then it was the root element
            // so save it
            if (stack.isEmpty()) {
                rootElement = finished;
            }
        }
    }

    public Element getRootElement() {
        return rootElement;
    }

    private Tag getTag(String localname, String qname) {
        Tag t = (Tag) TAG_MAP.get(qname);

        // Some of the THML books are broken in that they use uppercase
        // element names, which the spec disallows, but we might as well
        // look out for them
        if (t == null) {
            t = (Tag) TAG_MAP.get(qname.toLowerCase(Locale.ENGLISH));

            if (t == null) {
                log.warn("In " + book.getInitials() + "(" + key.getName() + ") unknown thml element: " + localname + " qname=" + qname); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

                // Report on it only once and make sure the content is output.
                t = new AnonymousTag(qname);
                TAG_MAP.put(qname, t);
                return t;
            }

            DataPolice.report("In " + book.getInitials() + "(" + key.getName() + ") Wrong case used in thml element: " + qname); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return t;
    }

    /**
     * The book containing the data.
     */
    private Book book;

    /**
     * The key for the data.
     */
    private Key key;

    /**
     * When the document is parsed, this is the last element popped off the
     * stack.
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
        /*
         * ThML is based upon Voyager XHTML and all Voyager elements are
         * allowed. However not all elements make sense.
         */
        Tag[] tags = new Tag[] {
                // The following are defined in Voyager xhtml 4.0
                new ATag(), new AbbrTag(), new AliasTag("acronym", new AbbrTag()), //$NON-NLS-1$
                new AnonymousTag("address"), //$NON-NLS-1$
                new SkipTag("applet"), //$NON-NLS-1$
                new SkipTag("area"), //$NON-NLS-1$
                new BTag(), new SkipTag("base"), //$NON-NLS-1$
                new SkipTag("basefont"), //$NON-NLS-1$
                new IgnoreTag("bdo"), //$NON-NLS-1$
                new BigTag(), new BlockquoteTag(), new IgnoreTag("body"), //$NON-NLS-1$
                new BrTag(), new SkipTag("button"), //$NON-NLS-1$
                new AnonymousTag("caption"), //$NON-NLS-1$
                new CenterTag(), new AnonymousTag("cite"), //$NON-NLS-1$
                new AnonymousTag("code"), //$NON-NLS-1$
                new SkipTag("col"), //$NON-NLS-1$
                new SkipTag("colgroup"), //$NON-NLS-1$
                new AliasTag("dd", new LiTag()), //$NON-NLS-1$
                new AnonymousTag("del"), //$NON-NLS-1$
                new AnonymousTag("dfn"), //$NON-NLS-1$
                new DivTag(), new AliasTag("dl", new UlTag()), //$NON-NLS-1$
                new AliasTag("dt", new LiTag()), //$NON-NLS-1$
                new AliasTag("em", new ITag()), //$NON-NLS-1$
                new IgnoreTag("fieldset"), //$NON-NLS-1$
                new FontTag(), new SkipTag("form"), //$NON-NLS-1$
                new SkipTag("frame"), //$NON-NLS-1$
                new SkipTag("frameset"), //$NON-NLS-1$
                new AliasTag("h1", new HTag(1)), //$NON-NLS-1$
                new AliasTag("h2", new HTag(2)), //$NON-NLS-1$
                new AliasTag("h3", new HTag(3)), //$NON-NLS-1$
                new AliasTag("h4", new HTag(4)), //$NON-NLS-1$
                new AliasTag("h5", new HTag(5)), //$NON-NLS-1$
                new AliasTag("h6", new HTag(6)), //$NON-NLS-1$
                new SkipTag("head"), //$NON-NLS-1$
                new HrTag(), new IgnoreTag("html"), //$NON-NLS-1$
                new IgnoreTag("frameset"), //$NON-NLS-1$
                new ITag(), new SkipTag("iframe"), //$NON-NLS-1$
                new ImgTag(), new SkipTag("input"), //$NON-NLS-1$
                new AnonymousTag("ins"), //$NON-NLS-1$
                new AnonymousTag("kbd"), //$NON-NLS-1$
                new AnonymousTag("label"), //$NON-NLS-1$
                new AnonymousTag("legend"), //$NON-NLS-1$
                new LiTag(), new SkipTag("link"), //$NON-NLS-1$
                new SkipTag("map"), //$NON-NLS-1$
                new SkipTag("meta"), //$NON-NLS-1$
                new SkipTag("noscript"), //$NON-NLS-1$
                new SkipTag("object"), //$NON-NLS-1$
                new OlTag(), new SkipTag("optgroup"), //$NON-NLS-1$
                new SkipTag("option"), //$NON-NLS-1$
                new PTag(), new SkipTag("param"), //$NON-NLS-1$
                new IgnoreTag("pre"), //$NON-NLS-1$
                new QTag(), new RootTag(), new STag(), new AnonymousTag("samp"), //$NON-NLS-1$
                new SkipTag("script"), //$NON-NLS-1$
                new SkipTag("select"), //$NON-NLS-1$
                new SmallTag(), new IgnoreTag("span"), //$NON-NLS-1$
                new AliasTag("strong", new BTag()), //$NON-NLS-1$
                new SkipTag("style"), //$NON-NLS-1$
                new SubTag(), new SupTag(), new SyncTag(), new TableTag(), new IgnoreTag("tbody"), //$NON-NLS-1$
                new TdTag(), new IgnoreTag("tfoot"), //$NON-NLS-1$
                new SkipTag("textarea"), //$NON-NLS-1$
                new SkipTag("title"), //$NON-NLS-1$
                new IgnoreTag("thead"), //$NON-NLS-1$
                new ThTag(), new TrTag(), new TtTag(), new UTag(), new UlTag(), new AnonymousTag("var"), //$NON-NLS-1$

                // ThML adds the following to Voyager
                // Note: hymn.mod is not here nor are additional head&DC
                // elements
                new AnonymousTag("added"), //$NON-NLS-1$
                new AnonymousTag("attr"), //$NON-NLS-1$
                new AnonymousTag("argument"), //$NON-NLS-1$
                new CitationTag(), new AnonymousTag("date"), //$NON-NLS-1$
                new AnonymousTag("deleted"), //$NON-NLS-1$
                new AnonymousTag("def"), //$NON-NLS-1$
                new AliasTag("div1", new DivTag(1)), //$NON-NLS-1$
                new AliasTag("div2", new DivTag(2)), //$NON-NLS-1$
                new AliasTag("div3", new DivTag(3)), //$NON-NLS-1$
                new AliasTag("div4", new DivTag(4)), //$NON-NLS-1$
                new AliasTag("div5", new DivTag(5)), //$NON-NLS-1$
                new AliasTag("div6", new DivTag(6)), //$NON-NLS-1$
                new ForeignTag(), new AnonymousTag("index"), //$NON-NLS-1$
                new AnonymousTag("insertIndex"), //$NON-NLS-1$
                new AnonymousTag("glossary"), //$NON-NLS-1$
                new NoteTag(), new NameTag(), new PbTag(), new AnonymousTag("scripCom"), //$NON-NLS-1$
                new AnonymousTag("scripContext"), //$NON-NLS-1$
                new ScripRefTag(), new ScriptureTag(), new TermTag(), new AnonymousTag("unclear"), //$NON-NLS-1$
                new VerseTag(),
        };
        for (int i = 0; i < tags.length; i++) {
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
