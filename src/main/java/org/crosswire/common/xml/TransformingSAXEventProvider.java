/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
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

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A SAXEventProvider that gets its output data from an XSL stylesheet and
 * another SAXEventProvider (supplying input XML).
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class TransformingSAXEventProvider extends Transformer implements SAXEventProvider {
    /**
     * Simple ctor
     * 
     * @param xsluri the URI of XSL
     * @param xmlsep a SAX Event Provider for an XML document
     */
    public TransformingSAXEventProvider(URI xsluri, SAXEventProvider xmlsep) {
        this.xsluri = xsluri;
        this.xmlsep = xmlsep;
        this.outputs = new Properties();
        this.params = new HashMap<String, Object>();
    }

    /**
     * Compile the XSL or retrieve it from the cache
     * 
     * @return the template information
     * @throws TransformerConfigurationException when there is a problem with configuring the transformer
     * @throws IOException when there is an I/O error
     */
    private TemplateInfo getTemplateInfo() throws TransformerConfigurationException, IOException {
        // we may have one cached
        TemplateInfo tinfo = txers.get(xsluri);

        long modtime = -1;
        if (TransformingSAXEventProvider.developmentMode) {
            if (tinfo != null) {
                modtime = NetUtil.getLastModified(xsluri);

                // But check it is up to date
                if (modtime > tinfo.getModtime()) {
                    txers.remove(xsluri);
                    tinfo = null;
                    log.debug("updated style, re-caching. xsl={}", xsluri);
                }
            }
        }

        if (tinfo == null) {
            log.debug("generating templates for {}", xsluri);

            InputStream xslStream = null;
            try {
                xslStream = NetUtil.getInputStream(xsluri);
                if (transfact == null) {
                    transfact = TransformerFactory.newInstance();
                }
                Templates templates = transfact.newTemplates(new StreamSource(xslStream));

                if (modtime == -1) {
                    modtime = NetUtil.getLastModified(xsluri);
                }

                tinfo = new TemplateInfo(templates, modtime);

                txers.put(xsluri, tinfo);
            } finally {
                IOUtil.close(xslStream);
            }
        }

        return tinfo;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#transform(javax.xml.transform.Source, javax.xml.transform.Result)
     */
    @Override
    public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
        TemplateInfo tinfo;
        try {
            tinfo = getTemplateInfo();
        } catch (IOException e) {
            throw new TransformerException(e);
        }

        Transformer transformer = tinfo.getTemplates().newTransformer();

        for (Object obj : outputs.keySet()) {
            String key = (String) obj;
            String val = getOutputProperty(key);
            transformer.setOutputProperty(key, val);
        }

        for (String key : params.keySet()) {
            Object val = params.get(key);
            transformer.setParameter(key, val);
        }

        if (errors != null) {
            transformer.setErrorListener(errors);
        }

        if (resolver != null) {
            transformer.setURIResolver(resolver);
        }

        transformer.transform(xmlSource, outputTarget);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.SAXEventProvider#provideSAXEvents(org.xml.sax.ContentHandler)
     */
    public void provideSAXEvents(ContentHandler handler) throws SAXException {
        try {
            Source xmlSource = new SAXSource(new SAXEventProviderXMLReader(xmlsep), new SAXEventProviderInputSource());

            SAXResult outputTarget = new SAXResult(handler);

            transform(xmlSource, outputTarget);
        } catch (TransformerException ex) {
            throw new SAXException(ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#getErrorListener()
     */
    @Override
    public ErrorListener getErrorListener() {
        return errors;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#setErrorListener(javax.xml.transform.ErrorListener)
     */
    @Override
    public void setErrorListener(ErrorListener errors) throws IllegalArgumentException {
        this.errors = errors;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#getURIResolver()
     */
    @Override
    public URIResolver getURIResolver() {
        return resolver;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#setURIResolver(javax.xml.transform.URIResolver)
     */
    @Override
    public void setURIResolver(URIResolver resolver) {
        this.resolver = resolver;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#getOutputProperties()
     */
    @Override
    public Properties getOutputProperties() {
        return outputs;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#setOutputProperties(java.util.Properties)
     */
    @Override
    public void setOutputProperties(Properties outputs) throws IllegalArgumentException {
        this.outputs = outputs;
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#getOutputProperty(java.lang.String)
     */
    @Override
    public String getOutputProperty(String name) throws IllegalArgumentException {
        return outputs.getProperty(name);
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#setOutputProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setOutputProperty(String name, String value) throws IllegalArgumentException {
        outputs.setProperty(name, value);
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#clearParameters()
     */
    @Override
    public void clearParameters() {
        params.clear();
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#getParameter(java.lang.String)
     */
    @Override
    public Object getParameter(String name) {
        return params.get(name);
    }

    /* (non-Javadoc)
     * @see javax.xml.transform.Transformer#setParameter(java.lang.String, java.lang.Object)
     */
    @Override
    public void setParameter(String name, Object value) {
        params.put(name, value);
    }

    /**
     * In development mode the style sheet is checked for modifications before use and if so, it is recompiled.
     * 
     * @param developmentMode the developmentMode to set
     */
    public static void setDevelopmentMode(boolean developmentMode) {
        TransformingSAXEventProvider.developmentMode = developmentMode;
    }

    /**
     * In development mode the style sheet is checked for modifications before use and if so, it is recompiled.
     * 
     * @return the developmentMode
     */
    public static boolean isDevelopmentMode() {
        return developmentMode;
    }

    /**
     * A simple class to link modification times to Templates objects
     */
    private static class TemplateInfo {
        /**
         * Simple ctor
         * 
         * @param templates the compiled XSL
         * @param modtime the time the XSL was last modified
         */
        TemplateInfo(Templates templates, long modtime) {
            this.templates = templates;
            this.modtime = modtime;
        }

        /**
         * The transformer
         * 
         * @return the compiled XSL
         */
        Templates getTemplates() {
            return templates;
        }

        /**
         * The time the xsl file was last modified
         * 
         * @return when the xsl was last modified.
         */
        long getModtime() {
            return modtime;
        }

        private Templates templates;
        private long modtime;
    }

    /**
     * In development mode the style sheet is checked for modifications before use and if so, it is recompiled.
     */
    private static boolean developmentMode;

    /**
     * The remembered ErrorListener because the transformer has not been created
     */
    private ErrorListener errors;

    /**
     * The remembered URIResolver because the transformer has not been created
     */
    private URIResolver resolver;

    /**
     * The remembered OutputProperties because the transformer has not been
     * created
     */
    private Properties outputs;

    /**
     * The remembered Parameters because the transformer has not been created
     */
    private Map<String, Object> params;

    /**
     * The XSL stylesheet
     */
    private URI xsluri;

    /**
     * The XML input source
     */
    private SAXEventProvider xmlsep;

    /**
     * How we get the transformer objects
     */
    private TransformerFactory transfact;

    /**
     * A cache of transformers
     */
    private static Map<URI, TemplateInfo> txers = new HashMap<URI, TemplateInfo>();

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(TransformingSAXEventProvider.class);
}
