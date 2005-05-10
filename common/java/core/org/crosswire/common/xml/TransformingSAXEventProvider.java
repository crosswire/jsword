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
package org.crosswire.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
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
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
        TemplateInfo tinfo = (TemplateInfo) txers.get(xslurl);

        // But check it is up to date
        if (tinfo != null)
        {
            if (modtime > tinfo.getModtime())
            {
                txers.remove(xslurl);
                tinfo = null;
                log.debug("updated style, re-caching. xsl=" + xslurl.toExternalForm()); //$NON-NLS-1$
            }
        }

        if (tinfo == null)
        {
            log.debug("generating templates for " + xslurl.toExternalForm()); //$NON-NLS-1$

            InputStream xsl_in = xslurl.openStream();
            Templates templates = transfact.newTemplates(new StreamSource(xsl_in));

            tinfo = new TemplateInfo(templates, modtime);

            txers.put(xslurl, tinfo);
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

            for (Iterator it = outputs.keySet().iterator(); it.hasNext(); )
            {
                String key = (String) it.next();
                String val = getOutputProperty(key);
                transformer.setOutputProperty(key, val);
            }

            for (Iterator it = params.keySet().iterator(); it.hasNext(); )
            {
                String key = (String) it.next();
                Object val = params.get(key);
                transformer.setParameter(key, val);
            }

            if (errors != null)
            {
                transformer.setErrorListener(errors);
            }

            if (resolver != null)
            {
                transformer.setURIResolver(resolver);
            }

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
     * @see Transformer#getErrorListener()
     */
    public ErrorListener getErrorListener()
    {
        return errors;
    }

    /**
     * @see Transformer#setErrorListener(javax.xml.transform.ErrorListener)
     */
    public void setErrorListener(ErrorListener errors) throws IllegalArgumentException
    {
        this.errors = errors;
    }

    /**
     * @see Transformer#getURIResolver()
     */
    public URIResolver getURIResolver()
    {
        return resolver;
    }

    /**
     * @see Transformer#setURIResolver(javax.xml.transform.URIResolver)
     */
    public void setURIResolver(URIResolver resolver)
    {
        this.resolver = resolver;
    }

    /**
     * @see Transformer#getOutputProperties()
     */
    public Properties getOutputProperties()
    {
        return outputs;
    }

    /**
     * @see Transformer#setOutputProperties(java.util.Properties)
     */
    public void setOutputProperties(Properties outputs) throws IllegalArgumentException
    {
        this.outputs = outputs;
    }

    /**
     * @see Transformer#getOutputProperty(java.lang.String)
     */
    public String getOutputProperty(String name) throws IllegalArgumentException
    {
        return outputs.getProperty(name);
    }

    /**
     * @see Transformer#setOutputProperty(java.lang.String, java.lang.String)
     */
    public void setOutputProperty(String name, String value) throws IllegalArgumentException
    {
        outputs.setProperty(name, value);
    }

    /**
     * @see Transformer#clearParameters()
     */
    public void clearParameters()
    {
        params.clear();
    }

    /**
     * @see Transformer#getParameter(java.lang.String)
     */
    public Object getParameter(String name)
    {
        return params.get(name);
    }

    /**
     * @see Transformer#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value)
    {
        params.put(name, value);
    }

    /**
     * The remembered ErrorListener because the transformer has not been created
     */
    private ErrorListener errors;

    /**
     * The remembered URIResolver because the transformer has not been created
     */
    private URIResolver resolver;

    /**
     * The remembered OutputProperties because the transformer has not been created
     */
    private Properties outputs = new Properties();

    /**
     * The remembered Parameters because the transformer has not been created
     */
    private Map params = new HashMap();

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
    private static class TemplateInfo
    {
        /**
         * Simple ctor
         */
        public TemplateInfo(Templates templates, long modtime)
        {
            this.templates = templates;
            this.modtime = modtime;
        }

        /**
         * The transformer
         */
        Templates getTemplates()
        {
            return templates;
        }

        /**
         * The modtime of the xsl file
         */
        long getModtime()
        {
            return modtime;
        }

        private Templates templates;
        private long modtime;
    }
}
