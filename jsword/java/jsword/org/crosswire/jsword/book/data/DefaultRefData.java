
package org.crosswire.jsword.book.data;

import java.io.StringReader;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Verse;

/**
* A VerseData represents a Verse that exists inside a BibleData.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
* @version D5.I5.T0
*/
public class DefaultRefData implements RefData
{
    /**
    * Create a RefData
    * @param doc The BibleData that we are a part of
    * @param title The title of this section
    * @param para Is this verse at the start of a new paragraph
    * @param xml The XML text that need to be parse and added in
    */
    public DefaultRefData(SectionData section, Verse verse, boolean para) throws BookException
    {
        this.section = section;
        this.doc = section.getParent();
        this.verse = verse;
        this.xml = xml;
        this.para = para;

        this.ref = new Element("ref");

        ref.setAttribute("b", "" + verse.getBook());
        ref.setAttribute("c", "" + verse.getChapter());
        ref.setAttribute("v", "" + verse.getVerse());
        if (para) ref.setAttribute("para", "" + para);
    }

    /**
    * Accessor for our parent Element
    * @return The parent SectionElement
    */
    public SectionData getParent()
    {
        return section;
    }

    /**
    * Accessor for the Element that we are wrapping
    * @return The Element that we wrap
    */
    public Document getDocument()
    {
        return doc.getDocument();
    }

    /**
    * Accessor for the Element that we are wrapping
    * @return The Element that we wrap
    */
    public Element getElement()
    {
        return ref;
    }

    /**
    * Get the verse that this element contains
    * @param The verse
    */
    public Verse getVerse()
    {
        return verse;
    }

    /**
    * Add some plain text to the verse
    */
    public void setPlainText(String text)
    {
        Element it = new Element("it");
        it.addContent(text);

        ref.addContent(it);
    }

    /**
    * Add XML markup to the verse
    * @param xml The markup to add
    */
    public void setXMLText(String xml) throws SAXException
    {
        // First we remove the old ref node:
        Element dad = ref.getParent();
        dad.removeContent(ref);

        // We need to ensure that we have a single root node to be the
        // Document node in the parse
        String insert =
        "<ref " +
        " b='" + verse.getBook() + "'" +
        " c='" + verse.getChapter() + "'" +
        " v='" + verse.getVerse() + "'" +
        (para ? " para='true'" : "") +
        "><it>" +
        xml +
        "</it></ref>";

        // TODO: Not sure about this it needs testing since we killed the SAX
        // parse section. We might need to get the last child of the root element
        try
        {
            StringReader in = new StringReader(insert);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            ref = doc.getRootElement();
        }
        catch (JDOMException ex)
        {
            throw new SAXException("Rare in-memory IO error. System message: " + ex);
        }
    }

    /**
    * A simplified plain text version of the data in this verse with all
    * the markup stripped out.
    * @return The Bible text without markup
    */
    public String getPlainText()
    {
        StringBuffer buffer = new StringBuffer();
        getText(buffer);
        return buffer.toString();
    }

    /**
    * A simplified plain text version of the data in this verse with all
    * the markup stripped out.
    * @return The Bible text without markup
    */
    public String getXMLText()
    {
        return xml;
    }

    /**
    * A simplified plain text version of the data in this verse with all
    * the markup stripped out.
    * @return The Bible text without markup
    */
    public void getText(StringBuffer buffer)
    {
        recurseNodes(ref, buffer, 0);
    }

    /**
    * Recurse down a Doument node tree
    * @param node The node to dig into
    * @param text The place to store the text that we find
    * @param depth How far down have we gone? This is only needed if we
    *              want to uncomment the debug
    */
    private static void recurseNodes(Element node, StringBuffer text, int depth)
    {
        text.append(node.getText());

        Iterator it = node.getChildren("it").iterator();
        while (it.hasNext())
        {
            Element sub = (Element) it.next();
            recurseNodes(sub, text, depth + 1);
        }
    }

    /**
     * The document that we are a part of
     * @label document
     */
    private BibleData doc;

    /** The Verse that we model */
    private Verse verse;

    /** Is this verse the start of a new paragraph */
    private boolean para;

    /** The current verse */
    private Element ref;

    /**
     * The parent section
     * @label parent
     */
    private SectionData section;

    /** The formatted text */
    private String xml;
}
