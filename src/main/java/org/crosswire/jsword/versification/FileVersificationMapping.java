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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.versification;

import org.crosswire.common.config.ConfigException;
import org.crosswire.common.util.KeyValuePair;
import org.crosswire.common.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This reads a file up front and creates the key value pairs. Because
 * we're not quite using the 'properties' file definition because we allow
 * duplicate keys on either side of the '=' sign, we need to do the processing ourselves.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Chris Burrell
 */
public class FileVersificationMapping {
    //unsure what a typical value would be, so leaving at 16 - best to optimize for memory,
    //than speed upon reading the first time.
    private List<KeyValuePair> pairs = new ArrayList<KeyValuePair>(16);

    /**
     * Allow a default initialising if someone wants to create a mapping file dynamically.
     */
    public FileVersificationMapping() {
        //no work to do.
    }

    /**
     * @param versification the name of the versification maps to the expected .properties file
     * @throws IOException     error reading the mapping files
     * @throws ConfigException error parsing the contents of the file
     */
    public FileVersificationMapping(Versification versification) throws IOException, ConfigException {
        //TODO(CJB): deal with Missing Resource Exceptions
        InputStream s = ResourceUtil.getResourceAsStream(getClass(), versification.getName() + ".properties");
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(s));
        String line;
        while ((line = lineReader.readLine()) != null) {
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }

            int firstEqual = line.indexOf('=');
            if (firstEqual == -1) {
                this.addProperty(line, null);
            } else {
                this.addProperty(line.substring(0, firstEqual), line.substring(firstEqual + 1));
            }
        }
    }

    /**
     * @param key   the key
     * @param value the value
     */
    public void addProperty(String key, String value) {
        pairs.add(new KeyValuePair(key, value));
    }

    public List<KeyValuePair> getMappings() {
        return this.pairs;
    }
}
