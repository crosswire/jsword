package org.crosswire.jsword.view.web;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;

/**
 * Turn XML from a Bible into HTML according to a Display style.
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
public class SimpleWebConverter implements Converter
{
    /* (non-Javadoc)
     * @see org.crosswire.common.xml.Converter#convert(org.crosswire.common.xml.SAXEventProvider)
     */
    public SAXEventProvider convert(SAXEventProvider xmlsep) throws TransformerException
    {
        try
        {
            URL xslurl = ResourceUtil.getResource("xsl/web/simple.xsl");
            return new TransformingSAXEventProvider(xslurl, xmlsep);
        }
        catch (MalformedURLException ex)
        {
            throw new TransformerException(ex);
        }
    }
}
