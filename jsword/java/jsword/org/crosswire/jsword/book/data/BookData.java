
package org.crosswire.jsword.book.data;

import java.util.Iterator;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.Validator;

import org.crosswire.common.xml.JAXBSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.Verse;
import org.xml.sax.SAXException;

/**
 * Basic section of BookData.
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
public class BookData
{
    /**
     * Ctor
     */
    public BookData(Osis osis)
    {
        this.osis = osis;
    }

    /**
     * Create a BibleData from a SAXEventProvider
     */
    public BookData(SAXEventProvider provider) throws SAXException
    {
        try
        {
            Unmarshaller unm = JAXBUtil.getJAXBContext().createUnmarshaller();
            UnmarshallerHandler unmh = unm.getUnmarshallerHandler();
            provider.provideSAXEvents(unmh);

            osis = (Osis) unmh.getResult();
        }
        catch (JAXBException ex)
        {
            throw new SAXException(ex);
        }
    }

    /**
     * Accessor for the root OSIS element
     */
    public Osis getOsis()
    {
        return osis;
    }

    /**
     * A simplified plain text version of the data in this document with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public String getPlainText()
    {
        StringBuffer buffer = new StringBuffer();
    
        Iterator oit = getOsis().getOsisText().getDiv().iterator();
        while (oit.hasNext())
        {
            Div div = (Div) oit.next();
    
            Iterator dit = div.getContent().iterator();
            while (dit.hasNext())
            {
                Object data = dit.next();
                if (data instanceof Verse)
                {
                    String txt = JAXBUtil.getPlainText((Verse) data);
                    buffer.append(txt);
                }
            }
        }
    
        return buffer.toString().trim();
    }

    /**
     * Check that a BibleData is valid. Currently (probably as a result of a bug
     * in JAXB) this method will always fail.
     * @throws JAXBException
     */
    public void validate() throws JAXBException
    {
        Validator val = JAXBUtil.getJAXBContext().createValidator();
        val.setEventHandler(new ValidationEventHandler()
        {
            public boolean handleEvent(ValidationEvent ev)
            {
                return false;
            }
        });
        val.validateRoot(osis);
    }

    /**
     * Output the current data as a SAX stream.
     * @return A way of posting SAX events
     */
    public SAXEventProvider getSAXEventProvider()
    {
        return new JAXBSAXEventProvider(JAXBUtil.getJAXBContext(), osis);
    }

    /**
     * The root where we read data from
     */
    private Osis osis;
}
