
package org.crosswire.jsword.book.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;

import org.crosswire.common.util.LogicError;
import org.crosswire.common.xml.JAXBSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.ObjectFactory;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXHandler;
import org.xml.sax.SAXException;

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
    public DefaultBibleData(SAXEventProvider provider) throws SAXException
    {
        try
        {
            SAXHandler handler = new SAXHandler();
            provider.provideSAXEvents(handler);
            this.doc = handler.getDocument();

            bible = doc.getRootElement();

            jc = JAXBContext.newInstance(OsisUtil.OSIS_PACKAGE);
            Unmarshaller unm = jc.createUnmarshaller();
            UnmarshallerHandler unmh = unm.getUnmarshallerHandler();
            provider.provideSAXEvents(unmh);
            osis = (Osis) unmh.getResult();
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }
        catch (IOException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Create a default BibleElement.
     */
    public DefaultBibleData()
    {
        bible = new Element("bible");
        doc = new Document(bible);
            
        try
        {
            jc = JAXBContext.newInstance(OsisUtil.OSIS_PACKAGE);

            osis = ObjectFactory.createOsis();            
            work = ObjectFactory.createWork();

            text = ObjectFactory.createOsisText();
            text.setOsisIDWork("Bible.KJV");
            osis.setOsisText(text);
            
            header = ObjectFactory.createHeader();
            header.getWork().add(work);
            text.setHeader(header);
        }
        catch (JAXBException ex)
        {
            throw new LogicError(ex);
        }

        /*
        Validator val = jc.createValidator();
        val.setEventHandler(new ValidationEventHandler()
        {
            public boolean handleEvent(ValidationEvent ev)
            {
                return false;
            }
        });
        val.validateRoot(osis);
        //*/
    }

    /**
     * Output the current data as a SAX stream.
     * @param handler The Place to post SAX events
     */
    public SAXEventProvider getSAXEventProvider()
    {
        return new JAXBSAXEventProvider(jc, osis);
        //return new JDOMSAXEventProvider(doc);
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
    public SectionData createSectionData(String title) throws BookException
    {
        try
        {
            SectionData section = new DefaultSectionData(this, title);
            if (!(section instanceof DefaultSectionData))
                throw new IllegalArgumentException("Can't mix implementations by adding a " + section.getClass().getName() + " to a " + this.getClass().getName());
            
            DefaultSectionData dsection = (DefaultSectionData) section;
            sections.add(dsection);
            bible.addContent(dsection.getElement());

            text.getDiv().add(section.getDiv());
            return section;
        }
        catch (JAXBException ex)
        {
            throw new BookException("osis_create", ex);
        }
    }

    private Osis osis;
    private Work work;
    private OsisText text;
    private Header header;
    private JAXBContext jc = null;

    /**
     * The list of Sections
     */
    private List sections = new ArrayList();

    /**
     * The actual data store
     */
    private Document doc;

    /**
     * The root of the DOM tree
     */
    private Element bible;
}
