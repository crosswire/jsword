/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
package org.crosswire.jsword.book.filter.osis;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Key;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * Filter to convert an OSIS XML string to OSIS format.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OSISFilter implements Filter
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
            ele = parse(XMLUtil.cleanAllEntities(plain));
        }
        catch (Exception ex1)
        {
            DataPolice.report("Parse failed: " + ex1.getMessage() + //$NON-NLS-1$
                              "\non: " + plain); //$NON-NLS-1$

            // So just try to strip out all XML looking things
            String shawn = XMLUtil.cleanAllTags(plain);

            try
            {
                ele = parse(shawn);
            }
            catch (Exception ex2)
            {
                log.warn("Could not fix it by cleaning tags: " + ex2.getMessage()); //$NON-NLS-1$

                try
                {
                    ele = OSISUtil.factory().createP();
                    ele.addContent(plain);
                }
                catch (Exception ex4)
                {
                    log.warn("no way. say it ain't so! " + ex4.getMessage()); //$NON-NLS-1$
                }
            }
        }
        finally
        {
            if (ele == null)
            {
                ele = OSISUtil.factory().createP();
            }
            // Make sure that other places don't report this problem
            DataPolice.setKey(null);
        }
        return ele.removeContent();
    }

    /**
     * If the string is invalid then we might want to have more than one
     * crack at parsing it
     */
    private Element parse(String plain) throws JDOMException, IOException
    {
        // create a root element to house our document fragment
        StringReader in = new StringReader("<div>" + plain + "</div>"); //$NON-NLS-1$ //$NON-NLS-2$
        InputSource is = new InputSource(in);

        Document doc = builder.build(is);
        Element div = doc.getRootElement();

        // data is the div we added above so the input was a well formed
        // XML so we need to add the content of the div and not the div
        // itself

//        List data = div.removeContent();
        return div;
    }

    /**
     * The JDOM parser
     */
    private SAXBuilder builder = new SAXBuilder();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(OSISFilter.class);
}
