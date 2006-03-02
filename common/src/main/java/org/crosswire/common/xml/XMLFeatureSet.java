/*
 * This is inspired by DocumentChecker.
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

import org.xml.sax.XMLReader;

/**
 * A set of useful XML Features
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class XMLFeatureSet
{

    public void setFeatures(XMLReader parser, String[] argv)
    {
        // process arguments
        for (int i = 0; i < argv.length; i++)
        {
            String arg = argv[i];
            if (arg.startsWith("-")) //$NON-NLS-1$
            {
                String option = arg.substring(1);
                if (option.equalsIgnoreCase("n")) //$NON-NLS-1$
                {
                    namespacesFeature.setState(option.equals("n")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("np")) //$NON-NLS-1$
                {
                    namespacePrefixFeature.setState(option.equals("np")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("v")) //$NON-NLS-1$
                {
                    validationFeature.setState(option.equals("v")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xd")) //$NON-NLS-1$
                {
                    loadExternalDTDFeature.setState(option.equals("xd")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("s")) //$NON-NLS-1$
                {
                    schemaValidationFeature.setState(option.equals("s")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("f")) //$NON-NLS-1$
                {
                    schemaFullCheckingFeature.setState(option.equals("f")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("va")) //$NON-NLS-1$
                {
                    validateAnnotationsFeature.setState(option.equals("va")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("dv")) //$NON-NLS-1$
                {
                    dynamicValidationFeature.setState(option.equals("dv")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xi")) //$NON-NLS-1$
                {
                    xincludeFeature.setState(option.equals("xi")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xb")) //$NON-NLS-1$
                {
                    xincludeFixupBaseURIsFeature.setState(option.equals("xb")); //$NON-NLS-1$
                    continue;
                }
                if (option.equalsIgnoreCase("xl")) //$NON-NLS-1$
                {
                    xincludeFixupLanguageFeature.setState(option.equals("xl")); //$NON-NLS-1$
                    continue;
                }
                if (option.equals("h")) //$NON-NLS-1$
                {
                    printUsage();
                    continue;
                }
            }
        }

        setFeatures(parser);
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append('\n');
        buf.append(namespacesFeature.toString()).append('\n');
        buf.append(namespacePrefixFeature.toString()).append('\n');
        buf.append(validationFeature.toString()).append('\n');
        buf.append(schemaValidationFeature.toString()).append('\n');
        buf.append(schemaFullCheckingFeature.toString()).append('\n');
        buf.append(validateAnnotationsFeature.toString()).append('\n');
        buf.append(dynamicValidationFeature.toString()).append('\n');
        buf.append(loadExternalDTDFeature.toString()).append('\n');
        buf.append(xincludeFeature.toString()).append('\n');
        buf.append(xincludeFixupBaseURIsFeature.toString()).append('\n');
        buf.append(xincludeFixupLanguageFeature.toString());
        return buf.toString();
    }

    /** Prints the usage. */
    public void printUsage()
    {
        System.err.println("XML Feature Set options:"); //$NON-NLS-1$
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
        System.err.println();

        System.err.println("defaults:"); //$NON-NLS-1$
        System.err.println(new XMLFeatureSet().toString());
    }

    private void setFeatures(XMLReader parser)
    {
        namespacesFeature.setFeature(parser);
        namespacePrefixFeature.setFeature(parser);
        validationFeature.setFeature(parser);
        schemaValidationFeature.setFeature(parser);
        schemaFullCheckingFeature.setFeature(parser);
        validateAnnotationsFeature.setFeature(parser);
        dynamicValidationFeature.setFeature(parser);
        loadExternalDTDFeature.setFeature(parser);
        xincludeFeature.setFeature(parser);
        xincludeFixupBaseURIsFeature.setFeature(parser);
        xincludeFixupLanguageFeature.setFeature(parser);
    }

    /**
     * Namespaces feature id
     */
    private XMLFeature namespacesFeature =
        new XMLFeature("Namespaces", "http://xml.org/sax/features/namespaces", true); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Namespace prefixes feature id
     */
    private XMLFeature namespacePrefixFeature =
        new XMLFeature("Prefixes", "http://xml.org/sax/features/namespace-prefixes"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Validation feature id
     */
    private XMLFeature validationFeature =
        new XMLFeature("Validation", "http://xml.org/sax/features/validation"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Schema validation feature id
     */
    private XMLFeature schemaValidationFeature =
        new XMLFeature("Schema", "http://apache.org/xml/features/validation/schema"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Schema full checking feature id
     */
    private XMLFeature schemaFullCheckingFeature =
        new XMLFeature("Schema full checking", "http://apache.org/xml/features/validation/schema-full-checking"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Validate schema annotations feature id
     */
    private XMLFeature validateAnnotationsFeature =
        new XMLFeature("Validate Annotations", "http://apache.org/xml/features/validate-annotations"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Dynamic validation feature id
     */
    private XMLFeature dynamicValidationFeature =
        new XMLFeature("Dynamic Validation", "http://apache.org/xml/features/validation/dynamic"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Load external DTD feature id
     */
    private XMLFeature loadExternalDTDFeature =
        new XMLFeature("Load External DTD", "http://apache.org/xml/features/nonvalidating/load-external-dtd", true); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * XInclude feature id
     */
    private XMLFeature xincludeFeature =
        new XMLFeature("XInclude", "http://apache.org/xml/features/xinclude"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * XInclude fixup base URIs feature id
     */
    private XMLFeature xincludeFixupBaseURIsFeature =
        new XMLFeature("XInclude base URI fixup", "http://apache.org/xml/features/xinclude/fixup-base-uris", true); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * XInclude fixup language feature id
     */
    private XMLFeature xincludeFixupLanguageFeature =
        new XMLFeature("XInclude language fixup", "http://apache.org/xml/features/xinclude/fixup-language", true); //$NON-NLS-1$ //$NON-NLS-2$

}
