package org.crosswire.jsword.book.sword;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Histogram;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Element;

/**
 * A ConfigEntry holds the value(s) for an entry of ConfigEntryType.
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
 * @see gnu.gpl.Licence
 * @author DM Smith [ dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class ConfigEntry
{

    /**
     * Create a ConfigEntry whose type is not certain
     * and whose value is not known.
     * @param moduleName the internal name of the module
     * @param aName the name of the ConfigEntry.
     */
    public ConfigEntry(String moduleName, String aName)
    {
        internal = moduleName;
        name = aName;
        type = ConfigEntryType.fromString(aName);
    }

    /**
     * Create a ConfigEntry directly with an initial value.
     * @param moduleName the internal name of the module
     * @param aType the kind of ConfigEntry
     * @param aValue the initial value for the ConfigEntry
     */
    public ConfigEntry(String moduleName, ConfigEntryType aType, String aValue)
    {
        internal = moduleName;
        name = aType.getName();
        type = aType;
        addValue(aValue);
    }

    /**
     * Get the key of this ConfigEntry
     */
    public String getName()
    {
        if (type != null)
        {
            return type.getName();
        }
        return name;
    }

    /**
     * Get the type of this ConfigEntry
     */
    public ConfigEntryType getType()
    {
        return type;
    }

    /**
     * Determines whether the string is allowed. For some config entries, 
     * the value is expected to be one of a group, for others the format is defined.
     * 
     * @param aValue
     * @return true if the string is allowed
     */
    public boolean isAllowed(String aValue)
    {
        if (type != null)
        {
            return type.isAllowed(aValue);
        }
        return true;
    }

    /**
     * RTF is allowed in a few config entries.
     * @return true if rtf is allowed
     */
    public boolean allowsRTF()
    {
        if (type != null)
        {
            return type.allowsRTF();
        }
        return true;
    }
    
    /**
     * While most fields are single line or single value, some allow continuation.
     * A continuation mark is a backslash at the end of a line. It is not to be followed by whitespace.
     * @return true if continuation is allowed
     */
    public boolean allowsContinuation()
    {
        if (type != null)
        {
            return type.allowsContinuation();
        }
        return true;
    }

    /**
     * Some keys can repeat. When this happens each is a single value pick from a list of choices.
     * @return true if this ConfigEntryType can occur more than once
     */
    public boolean mayRepeat()
    {
        if (type != null)
        {
            return type.mayRepeat();
        }
        return true;
    }
    
    /**
     * 
     */
    public boolean reportDetails()
    {
        if (type != null)
        {
            return type.reportDetails();
        }
        return true;
    }
    /**
     * Determine whether this config entry is supported.
     * @return true if this ConfigEntry has a type.
     */
    public boolean isSupported()
    {
        return type != null;
    }

    /**
     * Get the value(s) of this ConfigEntry.
     * If mayRepeat() == true then it returns a List.
     * Otherwise it returns a string.
     * @return a list, value or null.
     */
    public Object getValue()
    {
        if (value != null)
        {
            return value;
        }
        if (values != null)
        {
            return values;
        }
        return type.getDefault();
    }

    /**
     * Add a value to the list of values for this ConfigEntry
     */
    public void addValue(String aValue)
    {
        String confEntryName = getName();
        aValue = handleRTF(aValue);
        if (mayRepeat())
        {
            if (values == null)
            {
                histogram.increment(confEntryName);
                values = new ArrayList();
            }
            if (reportDetails())
            {
                histogram.increment(confEntryName + '.' + aValue);
            }
            if (!isAllowed(aValue))
            {
                log.info("Ignoring unknown config value for " + confEntryName + " in " + internal + ": " + aValue); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                return;
            }
            values.add(aValue);
        }
        else
        {
            if (value != null)
            {
                log.info("Ignoring unexpected additional entry for " + confEntryName + " in " + internal + ": " + aValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$                
            }
            else
            {
                histogram.increment(confEntryName);
                if (type.hasChoices())
                {
                    histogram.increment(confEntryName + '.' + aValue);
                }
                if (!isAllowed(aValue))
                {
                    log.info("Ignoring unknown config value for " + confEntryName + " in " + internal + ": " + aValue); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
                    return;
                }
                value = aValue;
            }
        }
    }
    
    public Element toOSIS()
    {
        OSISUtil.ObjectFactory factory = OSISUtil.factory();
        
        Element rowEle = factory.createRow();

        Element nameEle = factory.createCell();
        Element hiEle = factory.createHI();
        hiEle.setAttribute("rend", "bold"); //$NON-NLS-1$ //$NON-NLS-2$
        nameEle.addContent(hiEle);
        Element valueElement = factory.createCell();
        rowEle.addContent(nameEle);
        rowEle.addContent(valueElement);
        
        // I18N(DMS): use name to lookup translation. 
        hiEle.addContent(getName());

        if (value != null)
        {
            String expandedValue = XMLUtil.escape(value);
            if (allowsContinuation() || allowsRTF())
            {
                valueElement.addContent(processLines(factory, expandedValue));
            }
            else
            {
                valueElement.addContent(expandedValue);
            }
        }

        if (values != null)
        {
            Element listEle = factory.createLG();
            valueElement.addContent(listEle);

            Iterator iter = values.iterator();
            while (iter.hasNext())
            {
                Element itemEle = factory.createL();
                listEle.addContent(itemEle);
                itemEle.addContent(XMLUtil.escape(iter.next().toString()));
            }
        }
        return rowEle;
    }

    public static void resetStatistics()
    {
        histogram.clear();
    }

    public static void dumpStatistics()
    {
        // Uncomment the following line to produce statistics
        //System.out.println(histogram.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        ConfigEntry that = (ConfigEntry) obj;
        return that.getName().equals(this.getName());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }

    private String handleRTF(String aValue)
    {
        String copy = aValue;
        // This method is a hack! It could be made much nicer.

        // strip \pard
        copy = copy.replaceAll("\\\\pard ?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // replace rtf newlines
        copy = copy.replaceAll("\\\\pa[er] ?", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

        // strip whatever \qc is.
        copy = copy.replaceAll("\\\\qc ?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // strip bold and italic
        copy = copy.replaceAll("\\\\[bi]0? ?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // strip unicode characters
        copy = copy.replaceAll("\\\\u-?[0-9]{4,6}+\\?", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // strip { and } which are found in {\i text }
        copy = copy.replaceAll("[{}]", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // This check is here rather than at the top, so we can find the problems
        // and fix the source.
        if (!allowsRTF())
        {
            if (!copy.equals(aValue))
            {
                log.info("Ignoring unexpected RTF for " + getName() + " in " + internal + ": " + aValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            return aValue;
        }

        return copy;
    }
    
    private List processLines(OSISUtil.ObjectFactory factory, String aValue)
    {
        List list = new ArrayList();
        String [] lines = StringUtil.splitAll(aValue, '\n');
        for (int i = 0; i < lines.length; i++)
        {
            Element lineElement = factory.createL();
            lineElement.addContent(lines[i]);
            list.add(lineElement);
        }
        return list;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ConfigEntry.class);
    /**
     * A histogram for debugging.
     */
    private static Histogram histogram = new Histogram();

    private ConfigEntryType type;
    private String internal;
    private String name;
    private List values;
    private String value;
}
