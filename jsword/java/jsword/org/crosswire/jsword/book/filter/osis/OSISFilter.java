package org.crosswire.jsword.book.filter.osis;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.book.filter.FilterUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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
    public List toOSIS(String plain) throws FilterException
    {
        Element ele = OSISUtil.factory().createDiv();

        try
        {
            parse(ele, plain);
        }
        catch (Exception ex1)
        {
            DataPolice.report("parse (1) original failed: " + ex1.getMessage()); //$NON-NLS-1$
            DataPolice.report("  while parsing: " + FilterUtil.forOutput(plain)); //$NON-NLS-1$

            // Attempt to fix broken entities, that could be a low damage
            // way to fix a broken input string
            String cropped = XMLUtil.cleanAllEntities(plain);

            try
            {
                parse(ele, cropped);
            }
            catch (Exception ex2)
            {
                DataPolice.report("parse (2) cropped failed: " + ex2.getMessage()); //$NON-NLS-1$
                DataPolice.report("  while parsing: " + FilterUtil.forOutput(cropped)); //$NON-NLS-1$

                // So just try to strip out all XML looking things
                String shawn = XMLUtil.cleanAllTags(cropped);

                try
                {
                    parse(ele, shawn);
                }
                catch (Exception ex3)
                {
                    DataPolice.report("parse (3) shawn failed: " + ex3.getMessage()); //$NON-NLS-1$
                    DataPolice.report("  while parsing: " + FilterUtil.forOutput(shawn)); //$NON-NLS-1$

                    try
                    {
                        Element p = OSISUtil.factory().createP();
                        ele.addContent(p);
                        p.addContent(plain);
                    }
                    catch (Exception ex4)
                    {
                        log.warn("no way. say it ain't so!", ex4); //$NON-NLS-1$
                    }
                }
            }
        }
        return ele.removeContent();
    }

    /**
     * If the string is invalid then we might want to have more than one
     * crack at parsing it
     */
    private void parse(Element ele, String plain) throws JDOMException, IOException
    {
        // create a root element to house our document fragment
        StringReader in = new StringReader("<div>"+plain+"</div>"); //$NON-NLS-1$ //$NON-NLS-2$
        InputSource is = new InputSource(in);

        Document doc = builder.build(is);
        Element div = doc.getRootElement();

        // data is the div we added above so the input was a well formed
        // XML so we need to add the content of the div and not the div
        // itself

        List data = div.removeContent();
        ele.addContent(data);
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
