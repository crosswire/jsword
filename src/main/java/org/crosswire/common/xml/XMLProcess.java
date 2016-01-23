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

import org.crosswire.common.util.ClassUtil;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Runs an xml parser on an xml file using an xml handler. The default behavior
 * is to check that the xml file is well-formed.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class XMLProcess {

    public XMLProcess() {
        features = new XMLFeatureSet();
    }

    /**
     * @return Returns the features.
     */
    public XMLFeatureSet getFeatures() {
        return features;
    }

    /**
     * Process an xml file according to the arguments.
     * 
     * @param argv the command-line arguments
     */
    public static void main(String[] argv) {
        XMLProcess checker = new XMLProcess();

        // is there anything to do?
        if (argv.length == 0) {
            checker.usage();
            System.exit(1);
        }

        // variables
        String arg = null;

        // process arguments
        for (int i = 0; i < argv.length; i++) {
            arg = argv[i];
            if (arg.charAt(0) == '-') {
                String option = arg.substring(1);
                if ("h".equals(option)) {
                    checker.usage();
                    System.exit(0);
                }
            }
        }

        checker.initialize(argv);
        checker.parse(arg);

    }

    private void initialize(String[] argv) {
        // process arguments
        int i = 0;
        for (i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.charAt(0) == '-') {
                String option = arg.substring(1);
                if ("p".equals(option)) {
                    // get parser name
                    if (++i == argv.length) {
                        System.err.println("error: Missing argument to -p option.");
                    }
                    parserName = argv[i];

                    createParser();
                    continue;
                }
                if ("a".equals(option)) {
                    // get parser name
                    if (++i == argv.length) {
                        System.err.println("error: Missing argument to -a option.");
                    }
                    adapterName = argv[i];

                    createAdapter();
                    continue;
                }
            }
        }

        features.setFeatureStates(argv);
    }

    private void bind() {
        createParser();
        createAdapter();

        // Now that we have a parser and a handler
        // make the parser use them.
        setHandlers();
        features.setFeatures(parser);

    }

    private void createParser() {
        if (parser != null) {
            return;
        }

        try {
            parser = XMLReaderFactory.createXMLReader(parserName);
        } catch (SAXException e) {
            System.err.println("error: Unable to instantiate parser (" + parserName + ")");
        }

    }

    private void createAdapter() {
        if (adapter != null) {
            return;
        }

        try {
            adapter = (XMLHandlerAdapter) ClassUtil.forName(adapterName).newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("error: Unable to instantiate XMLHandlerAdpater (" + adapterName + ")");
        } catch (InstantiationException e) {
            System.err.println("error: Unable to instantiate XMLHandlerAdpater (" + adapterName + ")");
        } catch (IllegalAccessException e) {
            System.err.println("error: Unable to instantiate XMLHandlerAdpater (" + adapterName + ")");
        }

    }

    private void setHandlers() {
        parser.setDTDHandler(adapter);
        parser.setErrorHandler(adapter);
        parser.setContentHandler(adapter);

        try {
            parser.setProperty(DECLARATION_HANDLER_PROPERTY_ID, adapter);
        } catch (SAXException e) {
            e.printStackTrace(System.err);
        }

        try {
            parser.setProperty(LEXICAL_HANDLER_PROPERTY_ID, adapter);
        } catch (SAXException e) {
            e.printStackTrace(System.err);
        }
    }

    public void parse(String xmlFile) {
        bind();
        // parse file
        try {
            System.out.println("Parsing with the following:");
            printActual();
            parser.parse(xmlFile);
            System.out.println("Done: no problems found.");
        } catch (SAXException e) {
            System.err.println("error: Parse error occurred - " + e.getMessage());
            Exception nested = e.getException();
            if (nested != null) {
                nested.printStackTrace(System.err);
            } else {
                e.printStackTrace(System.err);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /** Prints the usage. */
    private void usage() {
        System.err.println("usage: java org.crosswire.common.xml.XMLProcess (options) uri");
        System.err.println();

        System.err.println("options:");
        printUsage();
        System.err.println("  -h          This help screen.");
        System.err.println();

        System.err.println("defaults:");
        printDefaults();
    }

    public void printUsage() {
        System.err.println("  -p name     Select parser by name.");
        System.err.println("  -a name     Select XMLHandlerAdapter by name.");
        features.printUsage();
    }

    public void printDefaults() {
        System.err.println("Parser:     " + DEFAULT_PARSER_NAME);
        System.err.println("Handler:    " + DEFAULT_HANDLER_NAME);
        System.err.println(new XMLFeatureSet().toString());
    }

    public void printActual() {
        System.err.println("Parser:     " + parserName);
        System.err.println("Handler:    " + adapterName);
        System.err.println(new XMLFeatureSet().toString());
    }

    // property ids

    /**
     * Lexical handler property id
     */
    private static final String LEXICAL_HANDLER_PROPERTY_ID = "http://xml.org/sax/properties/lexical-handler";

    /**
     * Declaration handler property id
     */
    private static final String DECLARATION_HANDLER_PROPERTY_ID = "http://xml.org/sax/properties/declaration-handler";

    // default settings

    /** Default parser name. */
    private static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
    private static final String DEFAULT_HANDLER_NAME = "org.crosswire.common.xml.XMLHandlerAdapter";

    private String parserName = DEFAULT_PARSER_NAME;
    private XMLReader parser;
    private String adapterName = DEFAULT_HANDLER_NAME;
    private XMLHandlerAdapter adapter;
    private XMLFeatureSet features;
}
