
package org.crosswire.jsword.book.data;

import java.util.Enumeration;
import java.util.Vector;

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
* @version D5.I2.T2
*/
public class DefaultBibleData implements BibleData
{
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
    public Enumeration getSectionDatas()
    {
        return sections.elements();
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
        sections.addElement(dsection);
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

        Enumeration en = getSectionDatas();
        while (en.hasMoreElements())
        {
            SectionData section = (SectionData) en.nextElement();
            buffer.append(section.getPlainText());
        }

        return buffer.toString().trim();
    }

    /**
     * The list of Sections
     * @associates <{org.crosswire.jsword.book.data.DefaultSectionData}>
     * @label contains
     * @clientCardinality 1
     * @supplierCardinality 1..*
     */
    private Vector sections = new Vector();

    /** The actual data store */
    private Document doc;

    /** The root of the DOM tree */
    private Element bible;
}
