
package org.crosswire.jsword.book.filter.osis;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.JAXBUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.osis.P;
import org.xml.sax.InputSource;

/**
 * Filter to convert an OSIS XML string to OSIS format.
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
public class OSISFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.filter.BookDataListener, java.lang.String)
     */
    public void toOSIS(Element ele, String plain) throws FilterException
    {
        if (unm == null)
        {
            try
            {
                unm = JAXBUtil.getJAXBContext().createUnmarshaller();
                unm.setEventHandler(new CustomValidationEventHandler());
            }
            catch (JAXBException ex)
            {
                throw new FilterException(Msg.OSIS_INIT, ex);
            }
        }

        try
        {
            parse(ele, plain);
        }
        catch (Exception ex1)
        {
            DataPolice.report("parse original failed: "+ex1.getMessage());
            DataPolice.report("  while parsing: "+forOutput(plain));

            // Attempt to fix broken entities, that could be the least damage
            // way to fix a broken input string
            String cropped = XMLUtil.cleanAllEntities(plain);

            try
            {
                parse(ele, cropped);
            }
            catch (Exception ex2)
            {
                DataPolice.report("parse cropped failed: "+ex2.getMessage());
                DataPolice.report("  while parsing: "+forOutput(cropped));
                
                // So just try to strip out all XML looking things
                String shawn = XMLUtil.cleanAllTags(cropped);

                try
                {
                    parse(ele, shawn);
                }
                catch (Exception ex3)
                {
                    DataPolice.report("parse shawn failed: "+ex3.getMessage());
                    DataPolice.report("  while parsing: "+forOutput(shawn));

                    try
                    {
                        P p = JAXBUtil.factory().createP();
                        List list = JAXBUtil.getList(ele);
                        list.add(p);
                        list.add(plain);
                    }
                    catch (Exception ex4)
                    {
                        log.warn("no way. say it ain't so!", ex4);
                    }
                }
            }
        }
    }

    /**
     * If the string is invalid then we might want to have more than one
     * crack at parsing it
     */
    private void parse(Element ele, String plain) throws JAXBException
    {
        // create a root element to house our document fragment
        StringReader in = new StringReader("<div>"+plain+"</div>");
        InputSource is = new InputSource(in);

        Element data = (Element) unm.unmarshal(is);

        // data is the div we added above so the input was a well formed
        // XML so we need to add the content of the div and not the div
        // itself

        List host = JAXBUtil.getList(ele);
        List content = JAXBUtil.getList(data);

        host.addAll(content);
    }

    /**
     * Cut up the input data so it is OK to output in an error log
     */
    private String forOutput(String data)
    {
        String chopped = StringUtils.left(data, 50);
        return chopped;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(OSISFilter.class);

    /**
     * The JAXB unmarshaller
     */
    private Unmarshaller unm = null;

    /**
     * Catcher for all the Unmarshallers warnings
     */
    private static class CustomValidationEventHandler implements ValidationEventHandler
    {
        /* (non-Javadoc)
         * @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent)
         */
        public boolean handleEvent(ValidationEvent ev)
        {
            DataPolice.report("OSIS parse error: "+ev.getMessage()+" More information is available.");
            return true;
        }
    }
}
