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
    public CustomHandler(Book book, Key key)
    {
        this.book = book;
        this.key = key;
        stack = new LinkedList();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    /* @Override */
    public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException
    {
        Element ele = null;

        // If we are looking at the root element
        // then the stack is empty
        if (stack.size() > 0)
        {
            Object top = stack.getFirst();

            if (top instanceof Element) // It might be a text element
            {
                ele = (Element) stack.getFirst();

                // If the element and its descendants are to be ignored
                // then there is a null element on the stack
                if (ele == null)
                {
                    return;
                }
            }
        }

        Tag t = getTag(localname, qname);

        if (t != null)
        {
            stack.addFirst(t.processTag(ele, attrs));
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    /* @Override */
    public void characters(char[] data, int offset, int length)
    {
        // what we are adding
        String text = new String(data, offset, length);

        if (stack.isEmpty())
        {
            stack.addFirst(new Text(text));
            return;
        }

        // What we are adding to
        Element current = (Element) stack.getFirst();

        // If the element and its descendants are to be ignored
        // then there is a null element on the stack
        if (current == null)
        {
            return;
        }

        int size = current.getContentSize();

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
    /* @Override */
    public void endElement(String uri, String localname, String qname)
    {
        if (stack.isEmpty())
        {
            return;
        }
        // When we are done processing an element we need to remove
        // it from the stack so that nothing more is attached to it.
        Element finished = (Element) stack.removeFirst();
        Tag t = getTag(localname, qname);

        if (t != null)
        {
            t.processContent(finished);
        }

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
            t = (Tag) TAG_MAP.get(qname.toLowerCase(Locale.ENGLISH));

            if (t == null)
            {
                log.warn("In " + book.getInitials() + "(" + key.getName() + ") unknown thml element: " + localname + " qname=" + qname); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
            new BigTag(),
            new CenterTag(),
            new CitationTag(),
            new ColTag(),
            new DivTag(),
            new ForeignTag(),
            new FontTag(),
            new HrTag(),
            new ImgTag(),
            new ITag(),
            new LiTag(),
            new NoteTag(),
            new NameTag(),
            new OlTag(),
            new PTag(),
            new PbTag(),
            new RootTag(),
            new RowTag(),
            new ScriptureTag(),
            new ScripRefTag(),
            new SmallTag(),
            new SubTag(),
            new SupTag(),
            new SyncTag(),
            new TableTag(),
            new TdTag(),
            new TermTag(),
            new ThTag(),
            new TrTag(),
            new TtTag(),
            new UTag(),
            new UlTag(),
            new AliasTag("em", new ITag()), //$NON-NLS-1$
            new AliasTag("strong", new BTag()), //$NON-NLS-1$
            new AliasTag("h1", new HTag(1)), //$NON-NLS-1$
            new AliasTag("h2", new HTag(2)), //$NON-NLS-1$
            new AliasTag("h3", new HTag(3)), //$NON-NLS-1$
            new AliasTag("h4", new HTag(4)), //$NON-NLS-1$
            new AliasTag("h5", new HTag(5)), //$NON-NLS-1$
            new AliasTag("h6", new HTag(6)), //$NON-NLS-1$
            new AliasTag("dl", new UlTag()), //$NON-NLS-1$
            new AliasTag("dd", new LiTag()), //$NON-NLS-1$
            new AliasTag("dt", new LiTag()), //$NON-NLS-1$
            new IgnoreTag("span"), //$NON-NLS-1$
            new IgnoreTag("dir"), //$NON-NLS-1$
            new IgnoreTag("pre"), //$NON-NLS-1$
            // all the following are from Webster's Dict
            // Don't know what to do with them
            // They are not ThML!
            new AnonymousTag("def"), //$NON-NLS-1$
            new AnonymousTag("pos"), //$NON-NLS-1$
            new AnonymousTag("hpos"), //$NON-NLS-1$
            new AnonymousTag("org"), //$NON-NLS-1$
            new AnonymousTag("wf"), //$NON-NLS-1$
            new AnonymousTag("cd"), //$NON-NLS-1$
            new AnonymousTag("sd"), //$NON-NLS-1$
            new AnonymousTag("tran"), //$NON-NLS-1$
            new AnonymousTag("itran"), //$NON-NLS-1$
            new AnonymousTag("qpers"), //$NON-NLS-1$
            new AnonymousTag("fract"), //$NON-NLS-1$
            new AnonymousTag("sn"), //$NON-NLS-1$
            new AnonymousTag("singw"), //$NON-NLS-1$
            new AnonymousTag("universbold"), //$NON-NLS-1$
            new AnonymousTag("plw"), //$NON-NLS-1$
            new AnonymousTag("matrix"), //$NON-NLS-1$
            new AnonymousTag("ttitle"), //$NON-NLS-1$
            new AnonymousTag("englishtype"), //$NON-NLS-1$
            new AnonymousTag("figcap"), //$NON-NLS-1$
            new AnonymousTag("extendedtype"), //$NON-NLS-1$
            new AnonymousTag("musfig"), //$NON-NLS-1$
            new AnonymousTag("stageof"), //$NON-NLS-1$
            new AnonymousTag("wns"), //$NON-NLS-1$
            new AnonymousTag("subs"), //$NON-NLS-1$
            new AnonymousTag("sups"), //$NON-NLS-1$
            new AnonymousTag("nonpareiltype"), //$NON-NLS-1$
            new AnonymousTag("gothictype"), //$NON-NLS-1$
            new AnonymousTag("sanserif"), //$NON-NLS-1$
            new AnonymousTag("sansserif"), //$NON-NLS-1$
            new AnonymousTag("headrow"), //$NON-NLS-1$
            new AnonymousTag("figure"), //$NON-NLS-1$
            new AnonymousTag("srow"), //$NON-NLS-1$
            new AnonymousTag("longprimertype"), //$NON-NLS-1$
            new AnonymousTag("greatprimertype"), //$NON-NLS-1$
            new AnonymousTag("est"), //$NON-NLS-1$
            new AnonymousTag("chname"), //$NON-NLS-1$
            new AnonymousTag("miniontype"), //$NON-NLS-1$
            new AnonymousTag("supr"), //$NON-NLS-1$
            new AnonymousTag("sansserif"), //$NON-NLS-1$
            new AnonymousTag("funct"), //$NON-NLS-1$
            new AnonymousTag("item"), //$NON-NLS-1$
            new AnonymousTag("mitem"), //$NON-NLS-1$
            new AnonymousTag("mtable"), //$NON-NLS-1$
            new AnonymousTag("figtitle"), //$NON-NLS-1$
            new AnonymousTag("ct"), //$NON-NLS-1$
            new AnonymousTag("defwf"), //$NON-NLS-1$
            new AnonymousTag("umac"), //$NON-NLS-1$
            new AnonymousTag("pearltype"), //$NON-NLS-1$
            new AnonymousTag("vertical"), //$NON-NLS-1$
            new AnonymousTag("title"), //$NON-NLS-1$
            new AnonymousTag("picatype"), //$NON-NLS-1$
            new AnonymousTag("point18"), //$NON-NLS-1$
            new AnonymousTag("matrix2x5"), //$NON-NLS-1$
            new AnonymousTag("oldenglishtype"), //$NON-NLS-1$
            new AnonymousTag("oldstyletype"), //$NON-NLS-1$
            new AnonymousTag("smpicatype"), //$NON-NLS-1$
            new AnonymousTag("frenchelzevirtype"), //$NON-NLS-1$
            new AnonymousTag("typewritertype"), //$NON-NLS-1$
            new AnonymousTag("scripttype"), //$NON-NLS-1$
            new AnonymousTag("point1"), //$NON-NLS-1$
            new AnonymousTag("point1.5"), //$NON-NLS-1$
            new AnonymousTag("point2"), //$NON-NLS-1$
            new AnonymousTag("point2.5"), //$NON-NLS-1$
            new AnonymousTag("point3"), //$NON-NLS-1$
            new AnonymousTag("point3.5"), //$NON-NLS-1$
            new AnonymousTag("point4"), //$NON-NLS-1$
            new AnonymousTag("point4.5"), //$NON-NLS-1$
            new AnonymousTag("point5"), //$NON-NLS-1$
            new AnonymousTag("point5.5"), //$NON-NLS-1$
            new AnonymousTag("point6"), //$NON-NLS-1$
            new AnonymousTag("point7"), //$NON-NLS-1$
            new AnonymousTag("point8"), //$NON-NLS-1$
            new AnonymousTag("point9"), //$NON-NLS-1$
            new AnonymousTag("point10"), //$NON-NLS-1$
            new AnonymousTag("point11"), //$NON-NLS-1$
            new AnonymousTag("point12"), //$NON-NLS-1$
            new AnonymousTag("point14"), //$NON-NLS-1$
            new AnonymousTag("point16"), //$NON-NLS-1$
            new AnonymousTag("point18"), //$NON-NLS-1$
            new AnonymousTag("point20"), //$NON-NLS-1$
            new AnonymousTag("hw"), //$NON-NLS-1$
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
