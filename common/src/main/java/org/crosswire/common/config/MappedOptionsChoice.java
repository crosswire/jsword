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
 * ID: $Id: IntOptionsChoice.java 1575 2007-07-28 16:18:14Z dmsmith $
 */
package org.crosswire.common.config;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.crosswire.common.util.Logger;
import org.jdom.Element;

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class MappedOptionsChoice extends AbstractReflectedChoice implements MappedChoice {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#init(org.jdom.Element)
     */
    /* @Override */
    public void init(Element option, ResourceBundle configResources) throws StartupException {
        assert configResources != null;

        super.init(option, configResources);
        Element mapElement = option.getChild("map");
        if (mapElement == null) {
            throw new StartupException(Msg.CONFIG_NOMAP);
        }

        String name = mapElement.getAttributeValue("name");
        Object map = ChoiceFactory.getDataMap().get(name);
        if (map instanceof Map) {
            options = (Map) map;
        } else {
            options = new TreeMap();
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
        return String.class;
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
        return orig != null ? orig.toString() : "";
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
        Iterator iter = options.entrySet().iterator();
        Map.Entry mapEntry = null;
        while (iter.hasNext()) {
            mapEntry = (Map.Entry) iter.next();
            if (mapEntry.getValue().toString().equals(orig) || mapEntry.getKey().toString().equals(orig)) {
                return mapEntry.getKey().toString();
            }
        }
        logger.warn(Msg.IGNORE.toString(orig));
        return "";
    }

    private static Logger logger = Logger.getLogger(MappedOptionsChoice.class);
    private Map options;
}
