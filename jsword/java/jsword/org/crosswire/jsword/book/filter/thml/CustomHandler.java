package org.crosswire.jsword.book.filter.thml;

import java.util.LinkedList;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Element;
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
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class CustomHandler extends DefaultHandler
{
    /**
     * Simple ctor
     */
    public CustomHandler(Element ele)
    {
        stack.addFirst(ele);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException
    {
        Element ele = (Element) stack.getFirst();

        for (int i = 0; i < TAGS.length; i++)
        {
            if (qname.equals(TAGS[i].getTagName()))
            {
                TAGS[i].processTag(ele, attrs);
                return;
            }
        }

        // Some of the THML modules are broken in that they use uppercase
        // element names, which the spec disallows, but we might as well
        // look out for them
        for (int i = 0; i < TAGS.length; i++)
        {
            if (qname.equalsIgnoreCase(TAGS[i].getTagName()))
            {
                DataPolice.report("Wrong case used in element: "+qname); //$NON-NLS-1$
                TAGS[i].processTag(ele, attrs);
                return;
            }
        }

        log.warn("unknown thml element: "+localname+" qname="+qname); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] data, int offset, int length)
    {
        // What we are adding to
        Element current = (Element) stack.getFirst();
        List list = OSISUtil.getList(current); 

        // what we are adding
        String text = new String(data, offset, length);

        // If the last element in the list is a string then we should add
        // this string on to the end of it rather than add a new list item
        // because (probably as an atrifact of the HTML/XSL transform we get
        // a space inserted in the output even when 2 calls to this method
        // split a word.
        if (list.size() > 0)
        {
            Object last = list.get(list.size()-1);
            if (last instanceof String)
            {
                list.remove(list.size()-1);
                text = ((String) last) + text; 
            }
        }

        list.add(text);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localname, String qname)
    {
        // Not sure we need to do anything special
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public void endDocument()
    {
        stack.removeFirst();
    }

    /**
     * The stack of elements that we have created
     */
    private LinkedList stack = new LinkedList();

    /**
     * The known tag types
     */
    private static final Tag[] TAGS = new Tag[]
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

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(CustomHandler.class);
}
