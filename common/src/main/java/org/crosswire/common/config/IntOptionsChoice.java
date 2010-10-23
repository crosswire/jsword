/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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
package org.crosswire.common.config;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.jdom.Element;

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class IntOptionsChoice extends AbstractReflectedChoice implements MappedChoice {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#init(org.jdom.Element)
     */
    /* @Override */
    public void init(Element option, ResourceBundle configResources) throws StartupException {
        assert configResources != null;

        super.init(option, configResources);

        String prefix = getKey() + ".alternative.";

        options = new TreeMap();
        Iterator iter = option.getChildren("alternative").iterator();
        while (iter.hasNext()) {
            Element alternative = (Element) iter.next();
            int number = Integer.parseInt(alternative.getAttributeValue("number"));
            String name = configResources.getString(prefix + number);
            options.put(new Integer(number), name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.MappedChoice#getOptions()
     */
    public Map getOptions() {
        return new TreeMap(options);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class getConversionClass() {
        return Integer.TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.config.AbstractReflectedChoice#convertToString(java
     * .lang.Object)
     */
    /* @Override */
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
    /* @Override */
    public Object convertToObject(String orig) {
        // First check to see if this is a number
        try {
            return new Integer(orig);
        } catch (NumberFormatException ex) {
            Iterator iter = options.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) iter.next();
                if (mapEntry.getValue().equals(orig)) {
                    return mapEntry.getKey();
                }
            }
            return new Integer(0);
        }
    }

    private Map options;
}
