/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Key;
import org.jdom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Filter to convert THML to OSIS format.
 * 
 * <p>I used the THML ref page:
 * <a href="http://www.ccel.org/ThML/ThML1.04.htm">http://www.ccel.org/ThML/ThML1.04.htm</a>
 * to work out what the tags meant.
 * 
 * LATER(joe): check nesting on these THML elements
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class THMLFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.filter.BookDataListener, java.lang.String)
     */
    public List toOSIS(Key key, String plain)
    {
        DataPolice.setKey(key);
        Element ele = null;
        try
        {
            ele = parse(plain);
        }
        catch (Exception ex1)
        {
            DataPolice.report("Parse failed: " + ex1.getMessage() + //$NON-NLS-1$
                              "\non: " + plain); //$NON-NLS-1$

            // Attempt to fix broken entities, that could be a low damage
            // way to fix a broken input string
            String cropped = XMLUtil.cleanAllEntities(plain);

            try
            {
                ele = parse(cropped);
            }
            catch (Exception ex2)
            {
                log.warn("Could not fix it by cleaning entities: " + ex2.getMessage()); //$NON-NLS-1$

                // So just try to strip out all XML looking things
                String shawn = XMLUtil.cleanAllTags(cropped);

                try
                {
                    ele = parse(shawn);
                }
                catch (Exception ex3)
                {
                    log.warn("Could not fix it by cleaning tags: " + ex3.getMessage()); //$NON-NLS-1$

                    ele = OSISUtil.factory().createP();
                    ele.addContent(plain);
                }
            }
        }
        finally
        {
            if (ele == null)
            {
                ele = OSISUtil.factory().createP();
                ele.addContent(plain);
            }
            // Make sure that other places don't report this problem
            DataPolice.setKey(null);
        }
        return ele.removeContent();
    }

    /**
     * Parse a string by creating a StringReader and all the other gubbins.
     */
    private Element parse(String toparse) throws FactoryConfigurationError, ParserConfigurationException, SAXException, IOException
    {
        // We need to create a root element to house our document fragment
        StringReader in = new StringReader("<" + RootTag.TAG_ROOT + ">" + toparse + "</" + RootTag.TAG_ROOT + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        InputSource is = new InputSource(in);

        SAXParser parser = spf.newSAXParser();
        CustomHandler handler = new CustomHandler();

        parser.parse(is, handler);
        return handler.getRootElement();
    }

    /**
     * The SAX parser factory
     */
    private SAXParserFactory spf = SAXParserFactory.newInstance();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(THMLFilter.class);
}
