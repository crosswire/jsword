
package org.crosswire.common.config.swing;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.crosswire.common.config.swing.fields.ActionField;
import org.crosswire.common.config.swing.fields.BooleanField;
import org.crosswire.common.config.swing.fields.ColorField;
import org.crosswire.common.config.swing.fields.FileField;
import org.crosswire.common.config.swing.fields.FontButtonField;
import org.crosswire.common.config.swing.fields.FontField;
import org.crosswire.common.config.swing.fields.HashtableField;
import org.crosswire.common.config.swing.fields.NumberField;
import org.crosswire.common.config.swing.fields.OptionsField;
import org.crosswire.common.config.swing.fields.PasswordField;
import org.crosswire.common.config.swing.fields.StringArrayField;
import org.crosswire.common.config.swing.fields.TextField;
import org.crosswire.common.config.swing.fields.TextViewField;
import org.crosswire.common.util.Reporter;

/**
 * This class provides mapping between Choice types and Fields.
 * There is an argument that this class should be a properties file
 * however the practical advantages of compile time type-checking and
 * make simplicity, overweigh the possible re-use gains of a
 * properties file.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 */
public class FieldMap
{
    /**
     * Get a field from a string
     * @param the configuration type
     * @return The best Field that matches
     */
    public static Field getField(String type, Object data)
    {
        Field field = null;

        try
        {
            Class clazz = (Class) hash.get(type);
            if (clazz == null)
            {
                log.warn("field type ("+type+") unregistered.");
                field = new TextField();
                field.setOptions(data);
            }
            else
            {
                field = (Field) clazz.newInstance();
                field.setOptions(data);
            }
        }
        catch (Exception ex)
        {
            log.warn("field type ("+type+") initialization failed:");
            Reporter.informUser(type, ex);

            if (field == null)
            {
                log.warn("field type ("+type+") unregistered.");
                field = new TextField();
                field.setOptions(data);
            }
        }

        return field;
    }

    /**
     * We configure the FieldMap by access to the Hashtable that holds
     * the string/Field mapping.
     * @return The configuration Hashtable
     */
    public static Hashtable getHashtable()
    {
        return hash;
    }

    /** The configuration table */
    private static Hashtable hash = new Hashtable();

    /**
     * Default hashtable configuration
     */
    static
    {
        hash.put("text", TextField.class);
        hash.put("action", ActionField.class);
        hash.put("boolean", BooleanField.class);
        hash.put("number", NumberField.class);
        hash.put("options", OptionsField.class);
        hash.put("hash", HashtableField.class);
        hash.put("array", StringArrayField.class);
        hash.put("password", PasswordField.class);

        hash.put("color", ColorField.class);
        hash.put("file", FileField.class);
        hash.put("font", FontField.class);
        hash.put("fontb", FontButtonField.class);
        hash.put("file", TextViewField.class);
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(FieldMap.class);
}

