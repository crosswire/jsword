
package org.crosswire.jsword.book.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Verse;

/**
 * A SectionData contains a list of references, and a note that
 * describes them. We can also override the version settting on the bible
 * element here.
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
public class DefaultSectionData implements SectionData
{
    /**
     * The most simple constructor just takes a title, and a reference to
     * the BibleData that we are a part of
     * @param doc The BibleData that we are a part of
     * @param title The title of this section
     */
    public DefaultSectionData(BibleData doc, String title)
    {
        this(doc, title, null);
    }

    /**
     * A constructor that allows us to set up a default version as well as
     * a title, and a reference to the BibleData that we are a part of
     * @param doc The BibleData that we are a part of
     * @param title The title of this section
     * @param version A default version for the refs that we contain
     */
    public DefaultSectionData(BibleData doc, String title, String version)
    {
        this.doc = doc;

        section = new Element("section");
        if (version != null) section.setAttribute("version", version);
        section.setAttribute("title", title);

        // doc.getElement().addContent(section);
    }

    /**
     * Accessor for our parent Element
     * @return The parent BibleData
     */
    public BibleData getParent()
    {
        return doc;
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
        return section;
    }

    /**
     * This is an accessor for the list of references (verses) that we
     * hold
     * @return The list of RefDatas
     */
    public Iterator getRefDatas()
    {
        return refs.iterator();
    }

    /**
     * Get a reference to the real W3C Document.
     * @param verse The reference marker
     * @param para True if this is the start of a new section
     */
    public void addRefData(RefData ref) throws BookException
    {
        if (!(ref instanceof DefaultRefData))
            throw new IllegalArgumentException("Can't mix implementations by adding a " + ref.getClass().getName() + " to a " + this.getClass().getName());

        DefaultRefData dref = (DefaultRefData) ref;
        refs.add(dref);
        section.addContent(dref.getElement());
    }

    /**
     * Get a reference to the real W3C Document.
     * @param verse The reference marker
     * @param para True if this is the start of a new section
     */
    public RefData createRefData(Verse verse, boolean para) throws BookException
    {
        RefData ref = new DefaultRefData(this, verse, para);
        addRefData(ref);
        return ref;
    }

    /**
     * A simplified plain text version of the data in this verse with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public String getPlainText()
    {
        StringBuffer buffer = new StringBuffer();

        Iterator it = getRefDatas();
        while (it.hasNext())
        {
            RefData ref = (RefData) it.next();
            buffer.append(ref.getPlainText());
        }

        return buffer.toString();
    }

    /**
     * The document that we are a part of
     * @label document
     */
    private BibleData doc;

    /**
     * The list of references
     * @associates <{org.crosswire.jsword.book.data.DefaultRefData}>
     * @label contains
     * @clientCardinality 1
     * @supplierCardinality 1..*
     */
    private List refs = new ArrayList();

    /** The actual Element that we wrap */
    private Element section;
}
