/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License, version 2 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 */
package org.crosswire.jsword.xml;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;

import javax.xml.transform.TransformerException;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.URIFilter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;

/**
 * Turn XML from a Bible into HTML according to a Display style.
 * 
 * @see gnu.gpl.License for license details.
 * @author Joe Walker
 */
public class ConfigurableConverter implements Converter {
    /**
     * Get an array of the available style names for a given subject. Different
     * subjects are available for different contexts. For example - for
     * insertion into a web page we might want to use a set that had complex
     * HTML, or IE/NS specific HTML, where as a JFC HTMLDocument needs simpler
     * HTML - and special tags like the starting &lt;HTML> tags.
     * <p>
     * If the protocol of the URL of the current directory is not file then we
     * can't use File.list to get the contents of the directory. This will
     * happen if this is being run as an applet. When we start doing that then
     * we will need to think up something smarter here. Until then we just
     * return a zero length array.
     * 
     * @return An array of available style names
     */
    public String[] getStyles() {
        try {
            URL index = ResourceUtil.getResource(NetUtil.INDEX_FILE);
            return NetUtil.listByIndexFile(NetUtil.toURI(index), new XSLTFilter());
        } catch (IOException ex) {
            return new String[0];
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.Converter#convert(org.crosswire.common.xml.SAXEventProvider)
     */
    @Override
    public SAXEventProvider convert(SAXEventProvider xmlsep) throws TransformerException {
        try {
            URL xslurl = ResourceUtil.getResource(style);

            TransformingSAXEventProvider tsep = new TransformingSAXEventProvider(NetUtil.toURI(xslurl), xmlsep);
            // We used to do:
            // tsep.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // however for various reasons, now we don't but nothing seems to be
            // broken ...
            return tsep;
        } catch (MissingResourceException ex) {
            throw new TransformerException(ex);
        }
    }

    /**
     * Accessor for the transforming stylesheet we are using
     * 
     * @return the XSL stylesheet to use
     */
    public static String getResourceName() {
        return style;
    }

    /**
     * Accessor for the transforming stylesheet we are using
     * 
     * @param style the XSL stylesheet to use
     */
    public static void setResourceName(String style) {
        ConfigurableConverter.style = style;
    }

    /**
     *
     */
    static final class XSLTFilter implements URIFilter {
        /* (non-Javadoc)
         * @see org.crosswire.common.util.URLFilter#accept(java.lang.String)
         */
        @Override
        public boolean accept(String name) {
            return name.endsWith(FileUtil.EXTENSION_XSLT);
        }
    }

    /**
     * The default transforming stylesheet we are using
     */
    private static String style = "html5.xsl";
}
