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
package org.crosswire.common.xml;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * A set of useful XML Features
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class XMLFeatureSet {

    /**
     * An XMLFeatureSet with default settings.
     */
    public XMLFeatureSet() {
        features = new TreeMap<String, XMLFeatureState>();
        states = new TreeMap<XMLFeature, String>();
        features.put("n", new XMLFeatureState(XMLFeature.NAMESPACES, true));
        features.put("np", new XMLFeatureState(XMLFeature.NAMESPACE_PREFIX));
        features.put("v", new XMLFeatureState(XMLFeature.VALIDATION));
        features.put("xd", new XMLFeatureState(XMLFeature.LOAD_EXTERNAL_DTD, true));
        features.put("s", new XMLFeatureState(XMLFeature.SCHEMA_VALIDATION));
        features.put("f", new XMLFeatureState(XMLFeature.SCHEMA_FULL_CHECKING));
        features.put("va", new XMLFeatureState(XMLFeature.VALIDATE_ANNOTATIONS));
        features.put("dv", new XMLFeatureState(XMLFeature.DYNAMIC_VALIDATION));
        features.put("xi", new XMLFeatureState(XMLFeature.XINCLUDE));
        features.put("xb", new XMLFeatureState(XMLFeature.XINCLUDE_FIXUP_BASE_URIS, true));
        features.put("xl", new XMLFeatureState(XMLFeature.XINCLUDE_FIXUP_LANGUAGE, true));

        for (Map.Entry<String, XMLFeatureState> entry : features.entrySet()) {
            states.put(entry.getValue().getFeature(), entry.getKey());
        }
    }

    /**
     * Set the state of an XMLFeture in this set.
     * 
     * @param feature the XMLFeature to set
     * @param state whether the feature is on or off
     */
    public void setFeatureState(XMLFeature feature, boolean state) {
        features.get(states.get(feature)).setState(state);
    }

    /**
     * Allow for XMLFeatures to be set from command line.
     * 
     * @param argv the specification of XMLFeatures to turn on or off
     * @see #printUsage See printUsage for details
     */
    public void setFeatureStates(String[] argv) {
        // process arguments
        for (int i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.charAt(0) == '-') {
                String option = arg.substring(1);
                String key = option.toLowerCase(Locale.ENGLISH);
                XMLFeatureState feature = features.get(key);
                if (feature != null) {
                    feature.setState(option.equals(key));
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append('\n');
        for (XMLFeatureState state : features.values()) {
            buf.append(state.getFeature().toString()).append('\n');
        }
        return buf.toString();
    }

    /**
     * Prints the usage.
     */
    public void printUsage() {
        System.err.println("XML Feature Set options:");
        System.err.println("  -n  | -N    Turn on/off namespace processing.");
        System.err.println("  -np | -NP   Turn on/off namespace prefixes.");
        System.err.println("              NOTE: Requires use of -n.");
        System.err.println("  -v  | -V    Turn on/off validation.");
        System.err.println("  -xd | -XD   Turn on/off loading of external DTDs.");
        System.err.println("              NOTE: Always on when -v in use and not supported by all parsers.");
        System.err.println("  -s  | -S    Turn on/off Schema validation support.");
        System.err.println("              NOTE: Not supported by all parsers.");
        System.err.println("  -f  | -F    Turn on/off Schema full checking.");
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers.");
        System.err.println("  -va | -VA   Turn on/off validation of schema annotations.");
        System.err.println("              NOTE: Requires use of -s and not supported by all parsers.");
        System.err.println("  -dv | -DV   Turn on/off dynamic validation.");
        System.err.println("              NOTE: Not supported by all parsers.");
        System.err.println("  -xi | -XI   Turn on/off XInclude processing.");
        System.err.println("              NOTE: Not supported by all parsers.");
        System.err.println("  -xb | -XB   Turn on/off base URI fixup during XInclude processing.");
        System.err.println("              NOTE: Requires use of -xi and not supported by all parsers.");
        System.err.println("  -xl | -XL   Turn on/off language fixup during XInclude processing.");
        System.err.println("              NOTE: Requires use of -xi and not supported by all parsers.");
    }

    /**
     * Set this XMLFeatureSets state onto an XMLReader.
     * 
     * @param parser the XMLReader
     */
    public void setFeatures(XMLReader parser) {
        for (XMLFeatureState state : features.values()) {
            state.setFeature(parser);
        }
    }

    /**
     * A holder of the boolean state for a feature.
     */
    private static class XMLFeatureState {
        /**
         * Bind a state to an XMLFeature.
         * 
         * @param feature the XMLFeature
         * @param state whether that XMLFeature is on or off
         */
        XMLFeatureState(XMLFeature feature, boolean state) {
            this.feature = feature;
            this.state = state;
        }

        /**
         * An XMLFeature that is turned off.
         * @param feature the XMLFeature
         */
        XMLFeatureState(XMLFeature feature) {
            this(feature, false);
        }

        /**
         * @return Returns the feature.
         */
        public XMLFeature getFeature() {
            return feature;
        }

        /**
         * Set the new state
         * 
         * @param newState whether the feature is on or off
         */
        public void setState(boolean newState) {
            state = newState;
        }

        /**
         * Set the control state on the parser.
         * 
         * @param parser The parser on which to set this feature
         */
        public void setFeature(XMLReader parser) {
            String control = feature.getControl();
            try {
                parser.setFeature(control, state);
            } catch (SAXNotRecognizedException e) {
                System.err.println("warning: Parser does not recognize feature (" + control + ")");
            } catch (SAXNotSupportedException e) {
                System.err.println("warning: Parser does not support feature (" + control + ")");
            }
        }

        private boolean state;
        private XMLFeature feature;
    }

    private Map<String, XMLFeatureState> features;
    private Map<XMLFeature, String> states;
}
