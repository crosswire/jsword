
package org.crosswire.jsword.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.xml.sax.SAXException;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id: Style.java,v 1.5 2002/10/08 21:36:16 joe Exp $
 */
public class Style
{
    /**
     * Create a Style processor with a subject, to narrow down the list
     * of available style sheets
     * @param subject The subject to search for styles for
     */
    public Style(String subject)
    {
        this.subject = subject;

        // @TODO: Handle this error
        try
        {
            docbuild = docfact.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex)
        {
            docbuild = null;
        }
    }

    /**
     * The the given name OK?
     * @param subject The style subject to be tested
     * @return true If the subject will transform OK
     */
    public boolean isValidName(String name)
    {
        try
        {
            InputStream in = Project.resource().getStyleInputStream(subject, name);
            return (in != null);
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * Get an array of the available style names for a given subject.
     * Different subjects are available for different contexts. For
     * example - for insertion into a web page we might want to use a set
     * that had complex HTML, or IE/NS specific HTML, where as a JFC
     * HTMLDocument needs simpler HTML - and special tags like the
     * starting &lt;HTML> tags.
     * <p>If the protocol of the URL of the current directory is not file
     * then we can't use File.list to get the contents of the directory.
     * This will happen if this is being run as an applet. When we start
     * doing that then we will need to think up something smarter here.
     * Until then we just return a zero length array.
     * @return An array of available style names
     */
    public String[] getStyles()
    {
        try
        {
            return Project.resource().getStyles(subject);
        }
        catch (Exception ex)
        {
            return new String[0];
        }
    }

    /**
     * Get a default style - for when we are instructed to apply a
     * style to a document, but that style does not exist.
     */
    public String getDefaultStyle()
    {
        String[] styles = getStyles();
        if (styles.length == 0)
            return null;

        return styles[0];
    }

    /**
     * Reading and writing an XML document stored in a file.
     */
    public Document applyStyle(Document doc_in, String style) throws IOException, SAXException, TransformerException
    {
        // Load the xml document
        JDOMSource src_in = new JDOMSource(doc_in);

        // html output
        JDOMResult res_out = new JDOMResult();

        Transformer transformer = (Transformer) txers.get(style);
        if (transformer == null)
        {
            // Load the xsl document
            InputStream xsl_in = Project.resource().getStyleInputStream(subject, style);
            if (xsl_in == null)
                throw new IOException("Resource not found subject="+subject+" style="+style);

            transformer = transfact.newTransformer(new StreamSource(xsl_in));
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }

        transformer.transform(src_in, res_out);

        return res_out.getDocument();
    }

    /**
     * Convenience method to create a String from a JDOM document
     */
    public String getString(Document doc) throws IOException
    {
        StringWriter html_writer = new StringWriter();
        outputter.output(doc, html_writer);

        String output = html_writer.toString();

        // For some reason the new TRaX stuff leaves the
        // <?xml version="1.0" encoding="UTF-8"?> string in the result.
        if (output.startsWith("<?xml"))
        {
            int close = output.indexOf("?>");
            if (close != -1)
            {
                output = output.substring(close+2);
                log.debug("no effect from setting "+OutputKeys.OMIT_XML_DECLARATION);
            }
        }

        return output;
    }

    private DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();
    private DocumentBuilder docbuild = null;
    private TransformerFactory transfact = TransformerFactory.newInstance();
    private XMLOutputter outputter = new XMLOutputter();

    /** A cache of transfotmers */
    private Map txers = new HashMap();

    /** The current subject */
    private String subject;

    /** Do we log.fine() the documents as we format them? */
    private static final boolean debug = false;

    /** The extension for an XSL file */
    public static final String XSL_EXTENSION = ".xsl";

    /** The log stream */
    protected static Logger log = Logger.getLogger(Style.class);
}

