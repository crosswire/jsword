
package org.crosswire.jsword.book.data.jaxb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.Validator;

import org.crosswire.common.xml.JAXBSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Osis;

/**
 * Basic section of JAXBBookData.
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
public class JAXBBookData implements BookData
{
    /**
     * We only want to be created by JAXBUtil
     * @see JAXBUtil
     */
    protected JAXBBookData()
    {
    }

    /**
     * Check that a BibleData is valid. Currently (probably as a result of a bug
     * in JAXB) this method will always fail.
     * @param bdata The BibleData to check
     * @throws JAXBException
     */
    public static void validate(JAXBBookData bdata) throws JAXBException
    {
        Validator val = JAXBUtil.getJAXBContext().createValidator();
        val.setEventHandler(new ValidationEventHandler()
        {
            public boolean handleEvent(ValidationEvent ev)
            {
                return false;
            }
        });
        val.validateRoot(bdata.osis);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookData#getSAXEventProvider()
     */
    public SAXEventProvider getSAXEventProvider()
    {
        return new JAXBSAXEventProvider(JAXBUtil.getJAXBContext(), osis);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookData#getSectionDatas()
     */
    public Iterator getSectionDatas()
    {
        List reply = new ArrayList();
        
        for (Iterator dit = osis.getOsisText().getDiv().iterator(); dit.hasNext();)
        {
            JAXBSectionData sdata = new JAXBSectionData();
            sdata.div = (Div) dit.next();
            reply.add(sdata);
        }
    
        return reply.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.BookData#getPlainText()
     */
    public String getPlainText()
    {
        StringBuffer buffer = new StringBuffer();
    
        Iterator it = getSectionDatas();
        while (it.hasNext())
        {
            JAXBSectionData sdata = (JAXBSectionData) it.next();
            buffer.append(sdata.getPlainText());
        }
    
        return buffer.toString().trim();
    }

    /**
     * The root where we read data from
     */
    protected Osis osis;
}
