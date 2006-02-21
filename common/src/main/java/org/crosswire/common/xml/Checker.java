/*
 * This is based on Checker.
 * @author Andy Clark, IBM
 * @author Arnaud Le Hors, IBM
 * 
 * Copyright 2000-2002,2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
 * ID: $Id: Converter.java 763 2005-07-27 23:26:43Z dmsmith $
 */
package org.crosswire.common.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Checks a XML document for problems, reporting line and offset.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Checker extends DefaultHandler implements ContentHandler, DTDHandler, ErrorHandler, DeclHandler, LexicalHandler
{

    // feature ids

    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces"; //$NON-NLS-1$

    /**
     * Namespace prefixes feature id
     * (http://xml.org/sax/features/namespace-prefixes).
     */
    protected static final String NAMESPACE_PREFIXES_FEATURE_ID = "http://xml.org/sax/features/namespace-prefixes"; //$NON-NLS-1$

    /** Validation feature id (http://xml.org/sax/features/validation). */
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation"; //$NON-NLS-1$

    /**
     * Schema validation feature id
     * (http://apache.org/xml/features/validation/schema).
     */
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema"; //$NON-NLS-1$

    /**
     * Schema full checking feature id
     * (http://apache.org/xml/features/validation/schema-full-checking).
     */
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking"; //$NON-NLS-1$

    /**
     * Validate schema annotations feature id
     * (http://apache.org/xml/features/validate-annotations)
     */
    protected static final String VALIDATE_ANNOTATIONS_ID = "http://apache.org/xml/features/validate-annotations"; //$NON-NLS-1$

    /**
     * Dynamic validation feature id
     * (http://apache.org/xml/features/validation/dynamic).
     */
    protected static final String DYNAMIC_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/dynamic"; //$NON-NLS-1$

    /**
     * Load external DTD feature id
     * (http://apache.org/xml/features/nonvalidating/load-external-dtd).
     */
    protected static final String LOAD_EXTERNAL_DTD_FEATURE_ID = "http://apache.org/xml/features/nonvalidating/load-external-dtd"; //$NON-NLS-1$

    /** XInclude feature id (http://apache.org/xml/features/xinclude). */
    protected static final String XINCLUDE_FEATURE_ID = "http://apache.org/xml/features/xinclude"; //$NON-NLS-1$

    /**
     * XInclude fixup base URIs feature id
     * (http://apache.org/xml/features/xinclude/fixup-base-uris).
     */
    protected static final String XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID = "http://apache.org/xml/features/xinclude/fixup-base-uris"; //$NON-NLS-1$

    /**
     * XInclude fixup language feature id
     * (http://apache.org/xml/features/xinclude/fixup-language).
     */
    protected static final String XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID = "http://apache.org/xml/features/xinclude/fixup-language"; //$NON-NLS-1$

    // property ids

    /**
     * Lexical handler property id
     * (http://xml.org/sax/properties/lexical-handler).
     */
    protected static final String LEXICAL_HANDLER_PROPERTY_ID = "http://xml.org/sax/properties/lexical-handler"; //$NON-NLS-1$

    // default settings

    /** Default parser name. */
    protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser"; //$NON-NLS-1$

    /** Default namespaces support (true). */
    protected static final boolean DEFAULT_NAMESPACES = true;

    /** Default namespace prefixes (false). */
    protected static final boolean DEFAULT_NAMESPACE_PREFIXES = false;

    /** Default validation support (false). */
    protected static final boolean DEFAULT_VALIDATION = false;

    /** Default load external DTD (true). */
    protected static final boolean DEFAULT_LOAD_EXTERNAL_DTD = true;

    /** Default Schema validation support (false). */
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

    /** Default Schema full checking support (false). */
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;

    /** Default validate schema annotations (false). */
    protected static final boolean DEFAULT_VALIDATE_ANNOTATIONS = false;

    /** Default dynamic validation support (false). */
    protected static final boolean DEFAULT_DYNAMIC_VALIDATION = false;

    /** Default XInclude processing support (false). */
    protected static final boolean DEFAULT_XINCLUDE = false;

    /** Default XInclude fixup base URIs support (true). */
    protected static final boolean DEFAULT_XINCLUDE_FIXUP_BASE_URIS = true;

    /** Default XInclude fixup language support (true). */
    protected static final boolean DEFAULT_XINCLUDE_FIXUP_LANGUAGE = true;

    /** Print writer. */
    protected PrintWriter fOut;

    /** Indent level. */
    protected int fIndent;

    /** Default constructor. */
    public Checker()
    {
        setOutput(new PrintWriter(System.out));
    }

    /** Sets the output stream for printing. */
    public void setOutput(OutputStream stream, String encoding) throws UnsupportedEncodingException
    {
        if (encoding == null)
        {
            encoding = "UTF8"; //$NON-NLS-1$
        }

        Writer writer = new OutputStreamWriter(stream, encoding);
        fOut = new PrintWriter(writer);
    }

    /** Sets the output writer. */
    public void setOutput(Writer writer)
    {
        fOut = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
    }

    /** Set document locator. */
    public void setDocumentLocator(Locator locator)
    {
        printIndent();
        fOut.print("setDocumentLocator("); //$NON-NLS-1$
        fOut.print("locator="); //$NON-NLS-1$
        fOut.print(locator);
        fOut.println(')');
        fOut.flush();
    }

    /** Start document. */
    public void startDocument() throws SAXException
    {
        fIndent = 0;
        printIndent();
        fOut.println("startDocument()"); //$NON-NLS-1$
        fOut.flush();
        fIndent++ ;
    }

    /** Processing instruction. */
    public void processingInstruction(String target, String data) throws SAXException
    {
        printIndent();
        fOut.print("processingInstruction("); //$NON-NLS-1$
        fOut.print("target="); //$NON-NLS-1$
        printQuotedString(target);
        fOut.print(',');
        fOut.print("data="); //$NON-NLS-1$
        printQuotedString(data);
        fOut.println(')');
        fOut.flush();
    }

    /** Characters. */
    public void characters(char[] ch, int offset, int length) throws SAXException
    {
        printIndent();
        fOut.print("characters("); //$NON-NLS-1$
        fOut.print("text="); //$NON-NLS-1$
        printQuotedString(ch, offset, length);
        fOut.println(')');
        fOut.flush();
    }

    /** Ignorable whitespace. */
    public void ignorableWhitespace(char[] ch, int offset, int length) throws SAXException
    {
        printIndent();
        fOut.print("ignorableWhitespace("); //$NON-NLS-1$
        fOut.print("text="); //$NON-NLS-1$
        printQuotedString(ch, offset, length);
        fOut.println(')');
        fOut.flush();
    }

    /** End document. */
    public void endDocument() throws SAXException
    {
        fIndent-- ;
        printIndent();
        fOut.println("endDocument()"); //$NON-NLS-1$
        fOut.flush();
    }

    /** Start prefix mapping. */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        printIndent();
        fOut.print("startPrefixMapping("); //$NON-NLS-1$
        fOut.print("prefix="); //$NON-NLS-1$
        printQuotedString(prefix);
        fOut.print(',');
        fOut.print("uri="); //$NON-NLS-1$
        printQuotedString(uri);
        fOut.println(')');
        fOut.flush();
    }

    /** Start element. */
    public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException
    {
        printIndent();
        fOut.print("startElement("); //$NON-NLS-1$
        fOut.print("uri="); //$NON-NLS-1$
        printQuotedString(uri);
        fOut.print(',');
        fOut.print("localName="); //$NON-NLS-1$
        printQuotedString(localName);
        fOut.print(',');
        fOut.print("qname="); //$NON-NLS-1$
        printQuotedString(qname);
        fOut.print(',');
        fOut.print("attributes="); //$NON-NLS-1$
        if (attributes == null)
        {
            fOut.println("null"); //$NON-NLS-1$
        }
        else
        {
            fOut.print('{');
            int length = attributes.getLength();
            for (int i = 0; i < length; i++)
            {
                if (i > 0)
                {
                    fOut.print(',');
                }
                String attrLocalName = attributes.getLocalName(i);
                String attrQName = attributes.getQName(i);
                String attrURI = attributes.getURI(i);
                String attrType = attributes.getType(i);
                String attrValue = attributes.getValue(i);
                fOut.print('{');
                fOut.print("uri="); //$NON-NLS-1$
                printQuotedString(attrURI);
                fOut.print(',');
                fOut.print("localName="); //$NON-NLS-1$
                printQuotedString(attrLocalName);
                fOut.print(',');
                fOut.print("qname="); //$NON-NLS-1$
                printQuotedString(attrQName);
                fOut.print(',');
                fOut.print("type="); //$NON-NLS-1$
                printQuotedString(attrType);
                fOut.print(',');
                fOut.print("value="); //$NON-NLS-1$
                printQuotedString(attrValue);
                fOut.print('}');
            }
            fOut.print('}');
        }
        fOut.println(')');
        fOut.flush();
        fIndent++ ;
    }

    /** End element. */
    public void endElement(String uri, String localName, String qname) throws SAXException
    {
        fIndent-- ;
        printIndent();
        fOut.print("endElement("); //$NON-NLS-1$
        fOut.print("uri="); //$NON-NLS-1$
        printQuotedString(uri);
        fOut.print(',');
        fOut.print("localName="); //$NON-NLS-1$
        printQuotedString(localName);
        fOut.print(',');
        fOut.print("qname="); //$NON-NLS-1$
        printQuotedString(qname);
        fOut.println(')');
        fOut.flush();
    }

    /** End prefix mapping. */
    public void endPrefixMapping(String prefix) throws SAXException
    {
        printIndent();
        fOut.print("endPrefixMapping("); //$NON-NLS-1$
        fOut.print("prefix="); //$NON-NLS-1$
        printQuotedString(prefix);
        fOut.println(')');
        fOut.flush();
    }

    /** Skipped entity. */
    public void skippedEntity(String name) throws SAXException
    {
        printIndent();
        fOut.print("skippedEntity("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.println(')');
        fOut.flush();
    }

    /** Notation declaration. */
    public void notationDecl(String name, String publicId, String systemId) throws SAXException
    {
        printIndent();
        fOut.print("notationDecl("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.print(',');
        fOut.print("publicId="); //$NON-NLS-1$
        printQuotedString(publicId);
        fOut.print(',');
        fOut.print("systemId="); //$NON-NLS-1$
        printQuotedString(systemId);
        fOut.println(')');
        fOut.flush();
    }

    /** Unparsed entity declaration. */
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException
    {
        printIndent();
        fOut.print("unparsedEntityDecl("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.print(',');
        fOut.print("publicId="); //$NON-NLS-1$
        printQuotedString(publicId);
        fOut.print(',');
        fOut.print("systemId="); //$NON-NLS-1$
        printQuotedString(systemId);
        fOut.print(',');
        fOut.print("notationName="); //$NON-NLS-1$
        printQuotedString(notationName);
        fOut.println(')');
        fOut.flush();
    }

    /** Start DTD. */
    public void startDTD(String name, String publicId, String systemId) throws SAXException
    {
        printIndent();
        fOut.print("startDTD("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.print(',');
        fOut.print("publicId="); //$NON-NLS-1$
        printQuotedString(publicId);
        fOut.print(',');
        fOut.print("systemId="); //$NON-NLS-1$
        printQuotedString(systemId);
        fOut.println(')');
        fOut.flush();
        fIndent++ ;
    }

    /** Start entity. */
    public void startEntity(String name) throws SAXException
    {
        printIndent();
        fOut.print("startEntity("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.println(')');
        fOut.flush();
        fIndent++ ;
    }

    /** Start CDATA section. */
    public void startCDATA() throws SAXException
    {
        printIndent();
        fOut.println("startCDATA()"); //$NON-NLS-1$
        fOut.flush();
        fIndent++ ;
    }

    /** End CDATA section. */
    public void endCDATA() throws SAXException
    {
        fIndent-- ;
        printIndent();
        fOut.println("endCDATA()"); //$NON-NLS-1$
        fOut.flush();
    }

    /** Comment. */
    public void comment(char[] ch, int offset, int length) throws SAXException
    {
        printIndent();
        fOut.print("comment("); //$NON-NLS-1$
        fOut.print("text="); //$NON-NLS-1$
        printQuotedString(ch, offset, length);
        fOut.println(')');
        fOut.flush();
    }

    /** End entity. */
    public void endEntity(String name) throws SAXException
    {
        fIndent-- ;
        printIndent();
        fOut.print("endEntity("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.println(')');
    }

    /** End DTD. */
    public void endDTD() throws SAXException
    {
        fIndent-- ;
        printIndent();
        fOut.println("endDTD()"); //$NON-NLS-1$
        fOut.flush();
    }

    /** Element declaration. */
    public void elementDecl(String name, String contentModel) throws SAXException
    {
        printIndent();
        fOut.print("elementDecl("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.print(',');
        fOut.print("contentModel="); //$NON-NLS-1$
        printQuotedString(contentModel);
        fOut.println(')');
        fOut.flush();
    }

    /** Attribute declaration. */
    public void attributeDecl(String elementName, String attributeName, String type, String valueDefault, String value) throws SAXException
    {
        printIndent();
        fOut.print("attributeDecl("); //$NON-NLS-1$
        fOut.print("elementName="); //$NON-NLS-1$
        printQuotedString(elementName);
        fOut.print(',');
        fOut.print("attributeName="); //$NON-NLS-1$
        printQuotedString(attributeName);
        fOut.print(',');
        fOut.print("type="); //$NON-NLS-1$
        printQuotedString(type);
        fOut.print(',');
        fOut.print("valueDefault="); //$NON-NLS-1$
        printQuotedString(valueDefault);
        fOut.print(',');
        fOut.print("value="); //$NON-NLS-1$
        printQuotedString(value);
        fOut.println(')');
        fOut.flush();
    }

    /** Internal entity declaration. */
    public void internalEntityDecl(String name, String text) throws SAXException
    {
        printIndent();
        fOut.print("internalEntityDecl("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.print(',');
        fOut.print("text="); //$NON-NLS-1$
        printQuotedString(text);
        fOut.println(')');
        fOut.flush();
    }

    /** External entity declaration. */
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException
    {
        printIndent();
        fOut.print("externalEntityDecl("); //$NON-NLS-1$
        fOut.print("name="); //$NON-NLS-1$
        printQuotedString(name);
        fOut.print(',');
        fOut.print("publicId="); //$NON-NLS-1$
        printQuotedString(publicId);
        fOut.print(',');
        fOut.print("systemId="); //$NON-NLS-1$
        printQuotedString(systemId);
        fOut.println(')');
        fOut.flush();
    }

    /** Warning. */
    public void warning(SAXParseException ex) throws SAXException
    {
        printError("Warning", ex); //$NON-NLS-1$
    }

    /** Error. */
    public void error(SAXParseException ex) throws SAXException
    {
        printError("Error", ex); //$NON-NLS-1$
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException
    {
        printError("Fatal Error", ex); //$NON-NLS-1$
        throw ex;
    }

    /** Print quoted string. */
    protected void printQuotedString(String s)
    {
        if (s == null)
        {
            fOut.print("null"); //$NON-NLS-1$
            return;
        }

        fOut.print('"');
        int length = s.length();
        for (int i = 0; i < length; i++ )
        {
            char c = s.charAt(i);
            normalizeAndPrint(c);
        }
        fOut.print('"');
    }

    /** Print quoted string. */
    protected void printQuotedString(char[] ch, int offset, int length)
    {
        fOut.print('"');
        for (int i = 0; i < length; i++ )
        {
            normalizeAndPrint(ch[offset + i]);
        }
        fOut.print('"');
    }

    /** Normalize and print. */
    protected void normalizeAndPrint(char c)
    {
        switch (c)
        {
            case '\n':
            {
                fOut.print("\\n"); //$NON-NLS-1$
                break;
            }
            case '\r':
            {
                fOut.print("\\r"); //$NON-NLS-1$
                break;
            }
            case '\t':
            {
                fOut.print("\\t"); //$NON-NLS-1$
                break;
            }
            case '\\':
            {
                fOut.print("\\\\"); //$NON-NLS-1$
                break;
            }
            case '"':
            {
                fOut.print("\\\""); //$NON-NLS-1$
                break;
            }
            default:
            {
                fOut.print(c);
            }
        }
    }

    /** Prints the error message. */
    protected void printError(String type, SAXParseException ex)
    {
        System.err.print("["); //$NON-NLS-1$
        System.err.print(type);
        System.err.print("] "); //$NON-NLS-1$
        String systemId = ex.getSystemId();
        if (systemId != null)
        {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
            {
                systemId = systemId.substring(index + 1);
            }
            System.err.print(systemId);
        }
        System.err.print(':');
        System.err.print(ex.getLineNumber());
        System.err.print(':');
        System.err.print(ex.getColumnNumber());
        System.err.print(": "); //$NON-NLS-1$
        System.err.print(ex.getMessage());
        System.err.println();
        System.err.flush();

    }

    /** Prints the indent. */
    protected void printIndent()
    {
        for (int i = 0; i < fIndent; i++ )
        {
            fOut.print(' ');
        }
    }

    /** Main. */
    public static void main(String[] argv) throws Exception
    {

        // is there anything to do?
        if (argv.length == 0)
        {
            printUsage();
            System.exit(1);
        }

        // variables
        Checker tracer = new Checker();
        XMLReader parser = null;
        boolean namespaces = DEFAULT_NAMESPACES;
        boolean namespacePrefixes = DEFAULT_NAMESPACE_PREFIXES;
        boolean validation = DEFAULT_VALIDATION;
        boolean externalDTD = DEFAULT_LOAD_EXTERNAL_DTD;
        boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
        boolean validateAnnotations = DEFAULT_VALIDATE_ANNOTATIONS;
        boolean dynamicValidation = DEFAULT_DYNAMIC_VALIDATION;
        boolean xincludeProcessing = DEFAULT_XINCLUDE;
        boolean xincludeFixupBaseURIs = DEFAULT_XINCLUDE_FIXUP_BASE_URIS;
        boolean xincludeFixupLanguage = DEFAULT_XINCLUDE_FIXUP_LANGUAGE;

        // process arguments
        for (int i = 0; i < argv.length; i++ )
        {
            String arg = argv[i];
            if (arg.startsWith("-")) { //$NON-NLS-1$
                String option = arg.substring(1);
                if (option.equals("p")) { //$NON-NLS-1$
                    // get parser name
                    if ( ++i == argv.length)
                    {
                        System.err.println("error: Missing argument to -p option."); //$NON-NLS-1$
                    }
                    String parserName = argv[i];

                    // create parser
                    try
                    {
                        parser = XMLReaderFactory.createXMLReader(parserName);
                    }
                    catch (Exception ex)
                    {
                        parser = null;
                        System.err.println("error: Unable to instantiate parser (" + parserName + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    continue;
                }
                if (option.equalsIgnoreCase("n")) //$NON-NLS-1$
                {
                    namespaces = option.equals("n"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("np")) //$NON-NLS-1$
                {
                    namespacePrefixes = option.equals("np"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("v")) //$NON-NLS-1$
                {
                    validation = option.equals("v"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xd")) //$NON-NLS-1$
                {
                    externalDTD = option.equals("xd"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("s")) //$NON-NLS-1$
                {
                    schemaValidation = option.equals("s"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("f")) //$NON-NLS-1$
                {
                    schemaFullChecking = option.equals("f"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("va")) //$NON-NLS-1$
                {
                    validateAnnotations = option.equals("va"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("dv")) //$NON-NLS-1$
                {
                    dynamicValidation = option.equals("dv"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xi")) //$NON-NLS-1$
                {
                    xincludeProcessing = option.equals("xi"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xb")) //$NON-NLS-1$
                {
                    xincludeFixupBaseURIs = option.equals("xb"); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xl")) //$NON-NLS-1$
                {
                    xincludeFixupLanguage = option.equals("xl"); //$NON-NLS-1$
                    continue;
                }
                if (option.equals("h")) //$NON-NLS-1$
                {
                    printUsage();
                    continue;
                }
            }

            // use default parser?
            if (parser == null)
            {
                // create parser
                try
                {
                    parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
                }
                catch (Exception e)
                {
                    System.err.println("error: Unable to instantiate parser (" + DEFAULT_PARSER_NAME + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                    continue;
                }
            }

            // set parser features
            try
            {
                parser.setFeature(NAMESPACES_FEATURE_ID, namespaces);
            }
            catch (SAXException e)
            {
                System.err.println("warning: Parser does not support feature (" + NAMESPACES_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(NAMESPACE_PREFIXES_FEATURE_ID, namespacePrefixes);
            }
            catch (SAXException e)
            {
                System.err.println("warning: Parser does not support feature (" + NAMESPACE_PREFIXES_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(VALIDATION_FEATURE_ID, validation);
            }
            catch (SAXException e)
            {
                System.err.println("warning: Parser does not support feature (" + VALIDATION_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(LOAD_EXTERNAL_DTD_FEATURE_ID, externalDTD);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + LOAD_EXTERNAL_DTD_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + LOAD_EXTERNAL_DTD_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + SCHEMA_VALIDATION_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + SCHEMA_VALIDATION_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(VALIDATE_ANNOTATIONS_ID, validateAnnotations);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + VALIDATE_ANNOTATIONS_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + VALIDATE_ANNOTATIONS_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, dynamicValidation);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + DYNAMIC_VALIDATION_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + DYNAMIC_VALIDATION_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(XINCLUDE_FEATURE_ID, xincludeProcessing);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + XINCLUDE_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + XINCLUDE_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID, xincludeFixupBaseURIs);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + XINCLUDE_FIXUP_BASE_URIS_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            try
            {
                parser.setFeature(XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID, xincludeFixupLanguage);
            }
            catch (SAXNotRecognizedException e)
            {
                System.err.println("warning: Parser does not recognize feature (" + XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (SAXNotSupportedException e)
            {
                System.err.println("warning: Parser does not support feature (" + XINCLUDE_FIXUP_LANGUAGE_FEATURE_ID + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // set handlers
            parser.setDTDHandler(tracer);
            parser.setErrorHandler(tracer);
            parser.setContentHandler(tracer);
            try
            {
                parser.setProperty("http://xml.org/sax/properties/declaration-handler", tracer); //$NON-NLS-1$
            }
            catch (SAXException e)
            {
                e.printStackTrace(System.err);
            }
            try
            {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", tracer); //$NON-NLS-1$
            }
            catch (SAXException e)
            {
                e.printStackTrace(System.err);
            }

            // parse file
            try
            {
                parser.parse(arg);
            }
            catch (SAXParseException e)
            {
                // ignore
            }
            catch (Exception e)
            {
                System.err.println("error: Parse error occurred - " + e.getMessage()); //$NON-NLS-1$
                if (e instanceof SAXException)
                {
                    Exception nested = ((SAXException) e).getException();
                    if (nested != null)
                    {
                        e = nested;
                    }
                }
                e.printStackTrace(System.err);
            }
        }

    }

    /** Prints the usage. */
    private static void printUsage()
    {

        System.err.println("usage: java sax.DocumentTracer (options) uri ..."); //$NON-NLS-1$
        System.err.println();

        System.err.println("options:"); //$NON-NLS-1$
        System.err.println("  -p name     Select parser by name."); //$NON-NLS-1$
        System.err.println("  -n  | -N    Turn on/off namespace processing."); //$NON-NLS-1$
        System.err.println("  -np | -NP   Turn on/off namespace prefixes."); //$NON-NLS-1$
        System.err.println("              NOTE: Requires use of -n."); //$NON-NLS-1$
        System.err.println("  -v  | -V    Turn on/off validation."); //$NON-NLS-1$
        System.err.println("  -xd | -XD   Turn on/off loading of external DTDs."); //$NON-NLS-1$
        System.err.println("              NOTE: Always on when -v in use and not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -s  | -S    Turn on/off Schema validation support."); //$NON-NLS-1$
        System.err.println("              NOTE: Not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -f  | -F    Turn on/off Schema full checking."); //$NON-NLS-1$
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -va | -VA   Turn on/off validation of schema annotations."); //$NON-NLS-1$
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -dv | -DV   Turn on/off dynamic validation."); //$NON-NLS-1$
        System.err.println("              NOTE: Not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -xi | -XI   Turn on/off XInclude processing."); //$NON-NLS-1$
        System.err.println("              NOTE: Not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -xb | -XB   Turn on/off base URI fixup during XInclude processing."); //$NON-NLS-1$
        System.err.println("              NOTE: Requires use of -xi and not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -xl | -XL   Turn on/off language fixup during XInclude processing."); //$NON-NLS-1$
        System.err.println("              NOTE: Requires use of -xi and not supported by all parsers."); //$NON-NLS-1$
        System.err.println("  -h          This help screen."); //$NON-NLS-1$
        System.err.println();

        System.err.println("defaults:"); //$NON-NLS-1$
        System.err.println("  Parser:     " + DEFAULT_PARSER_NAME); //$NON-NLS-1$
        System.err.print("  Namespaces: "); //$NON-NLS-1$
        System.err.println(DEFAULT_NAMESPACES ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  Prefixes:   "); //$NON-NLS-1$
        System.err.println(DEFAULT_NAMESPACE_PREFIXES ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  Validation: "); //$NON-NLS-1$
        System.err.println(DEFAULT_VALIDATION ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  Load External DTD: "); //$NON-NLS-1$
        System.err.println(DEFAULT_LOAD_EXTERNAL_DTD ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  Schema:     "); //$NON-NLS-1$
        System.err.println(DEFAULT_SCHEMA_VALIDATION ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  Schema full checking:     "); //$NON-NLS-1$
        System.err.println(DEFAULT_SCHEMA_FULL_CHECKING ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  Dynamic:    "); //$NON-NLS-1$
        System.err.println(DEFAULT_DYNAMIC_VALIDATION ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  XInclude:   "); //$NON-NLS-1$
        System.err.println(DEFAULT_XINCLUDE ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  XInclude base URI fixup:  "); //$NON-NLS-1$
        System.err.println(DEFAULT_XINCLUDE_FIXUP_BASE_URIS ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.print("  XInclude language fixup:  "); //$NON-NLS-1$
        System.err.println(DEFAULT_XINCLUDE_FIXUP_LANGUAGE ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$

    }
}
