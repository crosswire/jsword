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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.config;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.jdom2.Element;

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class IntOptionsChoice extends AbstractReflectedChoice implements MappedChoice<Integer, String> {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#init(org.jdom2.Element)
     */
    @Override
    public void init(Element option, ResourceBundle configResources) throws StartupException {
        assert configResources != null;

        super.init(option, configResources);

        String prefix = getKey() + ".alternative.";

        options = new TreeMap<Integer, String>();
        Iterator<Element> iter = option.getChildren("alternative").iterator();
        while (iter.hasNext()) {
            Element alternative = iter.next();
            int number = Integer.parseInt(alternative.getAttributeValue("number"));
            String name = configResources.getString(prefix + number);
            options.put(Integer.valueOf(number), name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.MappedChoice#getOptions()
     */
    public Map<Integer, String> getOptions() {
        return new TreeMap<Integer, String>(options);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class<Integer> getConversionClass() {
        return Integer.TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.config.AbstractReflectedChoice#convertToString(java
     * .lang.Object)
     */
    @Override
    public String convertToString(Object orig) {
        return orig.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.config.AbstractReflectedChoice#convertToObject(java
     * .lang.String)
     */
    @Override
    public Object convertToObject(String orig) {
        // First check to see if this is a number
        try {
            return Integer.valueOf(orig);
        } catch (NumberFormatException ex) {
            for (Map.Entry<Integer, String> mapEntry : options.entrySet()) {
                if (mapEntry.getValue().equals(orig)) {
                    return mapEntry.getKey();
                }
            }
            return Integer.valueOf(0);
        }
    }

    private Map<Integer, String> options;
}
