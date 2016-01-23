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

import org.crosswire.jsword.JSOtherMsg;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to convert between strings and objects of a type.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class MappedOptionsChoice extends AbstractReflectedChoice implements MappedChoice<Object, Object> {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#init(org.jdom2.Element)
     */
    @Override
    public void init(Element option, ResourceBundle configResources) throws StartupException {
        assert configResources != null;

        super.init(option, configResources);
        Element mapElement = option.getChild("map");
        if (mapElement == null) {
            throw new StartupException(JSOtherMsg.lookupText("Missing {0} element in config.xml", "map"));
        }

        String name = mapElement.getAttributeValue("name");
        Object map = ChoiceFactory.getDataMap().get(name);
        if (map instanceof Map<?, ?>) {
            options = (Map<?, ?>) map;
        } else {
            options = new TreeMap<Object, Object>();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.MappedChoice#getOptions()
     */
    public Map<Object, Object> getOptions() {
        return new TreeMap<Object, Object>(options);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.Choice#getConvertionClass()
     */
    public Class<String> getConversionClass() {
        return String.class;
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
        return orig != null ? orig.toString() : "";
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
        Iterator<?> iter = options.entrySet().iterator();
        Map.Entry<?, ?> mapEntry = null;
        while (iter.hasNext()) {
            mapEntry = (Map.Entry<?, ?>) iter.next();
            if (mapEntry.getValue().toString().equals(orig) || mapEntry.getKey().toString().equals(orig)) {
                return mapEntry.getKey().toString();
            }
        }
        log.warn(JSOtherMsg.lookupText("Ignoring invalid option: {0}", orig));
        return "";
    }

    private Map<?, ?> options;

    /**
     * The log stream
     */
    private static Logger log = LoggerFactory.getLogger(MappedOptionsChoice.class);
}
