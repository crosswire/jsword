
package org.crosswire.jsword.book.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Some helper classes to aid Document creation, and to hide all the
 * DOM/ProjectX/XML4J specific bits.
 * <p>The requirements for Document handling are these:<pre>
 *   - Do not force users of this or other packages to use org.w3c.dom
 *   - Model the current DTD.
 * </pre>
 *
 * <p>This is the root of a set of Element type objects to help creating
 * a DOM according to our DTD. In general it is the job of each Parent to
 * add child Elements to itself. In general you should use the parent to
 * create a child because it is easier.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class DefaultBibleData implements BibleData
{
    /**
     * Constructor DefaultBibleData.
     * @param doc
     */
    public DefaultBibleData(Document doc)
    {
        this.bible = doc.getRootElement();
        this.doc = doc;
    }

    /**
     * Create a default BibleElement.
     */
    public DefaultBibleData()
    {
        bible = new Element("bible");
        doc = new Document(bible);
    }

    /**
     * Get a reference to the real W3C Document.
     * @return The Document
     */
    public Document getDocument()
    {
        return doc;
    }

    /**
     * This is an accessor for the root &lt;bible> Element
     * @return The &lt;bible> Element
     */
    public Element getElement()
    {
        return bible;
    }

    /**
     * This is an enumeration through all the sections in this Document.
     * Each of the sections will be able to give a list of the Verses
     * that it contains.
     * @return The list of sections
     */
    public Iterator getSectionDatas()
    {
        return sections.iterator();
    }

    /**
     * Start a new section
     * @param title The heading for this section
     * @param version The Bible string
     */
    public void addSectionData(SectionData section)
    {
        if (!(section instanceof DefaultSectionData))
            throw new IllegalArgumentException("Can't mix implementations by adding a " + section.getClass().getName() + " to a " + this.getClass().getName());

        DefaultSectionData dsection = (DefaultSectionData) section;
        sections.add(dsection);
        bible.addContent(dsection.getElement());
    }

    /**
     * Start a new section
     * @param title The heading for this section
     * @param version The Bible string
     */
    public SectionData createSectionData(String title)
    {
        SectionData section = new DefaultSectionData(this, title);
        addSectionData(section);
        return section;
    }

    /**
     * Start a new section
     * @param title The heading for this section
     * @param version The Bible string
     */
    public SectionData createSectionData(String title, String version)
    {
        SectionData section = new DefaultSectionData(this, title, version);
        addSectionData(section);
        return section;
    }

    /**
     * A simplified plain text version of the data in this verse with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public String getPlainText()
    {
        StringBuffer buffer = new StringBuffer();

        Iterator it = getSectionDatas();
        while (it.hasNext())
        {
            SectionData section = (SectionData) it.next();
            buffer.append(section.getPlainText());
        }

        return buffer.toString().trim();
    }

    /**
     * The list of Sections
     */
    private List sections = new ArrayList();

    /** The actual data store */
    private Document doc;

    /** The root of the DOM tree */
    private Element bible;
}
