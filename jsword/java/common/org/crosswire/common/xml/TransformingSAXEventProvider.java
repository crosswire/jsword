package org.crosswire.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A SAXEventProvider that gets its output data from an XSL stylesheet and
 * another SAXEventProvider (supplying input XML).
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
public class TransformingSAXEventProvider implements SAXEventProvider
{
    /**
     * Simple ctor
     */
    public TransformingSAXEventProvider(URL xslurl, SAXEventProvider xmlsep)
    {
        this.xslurl = xslurl;
        this.xmlsep = xmlsep;
    }

    /**
     * Compile the XSL or retrieve it from the cache
     */
    private TemplateInfo getTemplateInfo() throws IOException, TransformerConfigurationException
    {
        long modtime = NetUtil.getLastModified(xslurl);

        // we may have one cached
        TemplateInfo tinfo = null;
        if (cache)
        {
            tinfo = (TemplateInfo) txers.get(xslurl);

            // But check it is up to date        
            if (tinfo != null)
            {
                if (modtime > tinfo.getModtime())
                {
                    txers.remove(xslurl);
                    tinfo = null;
                    log.debug("updated style, re-caching. xsl=" + xslurl.toExternalForm());
                }
            }
        }

        if (tinfo == null)
        {
            log.debug("generating templates for " + xslurl.toExternalForm());

            InputStream xsl_in = xslurl.openStream();
            Templates templates = transfact.newTemplates(new StreamSource(xsl_in));

            tinfo = new TemplateInfo(templates, modtime);
            if (cache)
            {
                txers.put(xslurl, tinfo);
            }
        }

        return tinfo;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.SAXEventProvider#provideSAXEvents(org.xml.sax.ContentHandler)
     */
    public void provideSAXEvents(ContentHandler handler) throws SAXException
    {
        try
        {
            Source src_in = new SAXSource(new SAXEventProviderXMLReader(xmlsep), new SAXEventProviderInputSource());
    
            TemplateInfo tinfo = getTemplateInfo();
    
            SAXResult res_out = new SAXResult(handler);
    
            Transformer transformer = tinfo.getTemplates().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(src_in, res_out);
        }
        catch (IOException ex)
        {
            throw new SAXException(ex);
        }
        catch (TransformerConfigurationException ex)
        {
            throw new SAXException(ex);
        }
        catch (TransformerException ex)
        {
            throw new SAXException(ex);
        }
    }

    /**
     * Returns the transformer cache status.
     * @return boolean
     */
    public static boolean isCache()
    {
        return cache;
    }

    /**
     * Sets the transformer cache status.
     * @param cache The status to set
     */
    public static void setCache(boolean cache)
    {
        TransformingSAXEventProvider.cache = cache;
        if (!cache)
        {
            txers.clear();
        }
    }

    /**
     * The XSL stylesheet
     */
    private URL xslurl;

    /**
     * The XML input source
     */
    private SAXEventProvider xmlsep;

    /**
     * How we get the transformer objects
     */
    private TransformerFactory transfact = TransformerFactory.newInstance();
    
    /**
     * Do we cache the transformers - speed vs devt ease trade off
     */
    private static boolean cache = true;

    /**
     * A cache of transformers
     */
    private static Map txers = new HashMap();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(TransformingSAXEventProvider.class);

    /**
     * A simple struct to link modification times to Templates objects
     */
    class TemplateInfo
    {
        /**
         * Simple ctor
         */
        public TemplateInfo(Templates templates, long modtime)
        {
            super();
            this.templates = templates;
            this.modtime = modtime;
        }

        /**
         * 
         */
        Templates getTemplates()
        {
            return templates;
        }

        /**
         * 
         */
        long getModtime()
        {
            return modtime;
        }

        private Templates templates;
        private long modtime;
    }
}
