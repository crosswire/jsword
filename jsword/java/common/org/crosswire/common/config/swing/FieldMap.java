
package org.crosswire.common.config.swing;

import java.awt.Font;
import java.io.File;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.crosswire.common.config.Choice;
import org.crosswire.common.config.DirectoryChoice;
import org.crosswire.common.config.MultipleChoice;
import org.crosswire.common.util.Reporter;

/**
 * This class provides mapping between Choice types and Fields.
 * There is an argument that this class should be a properties file
 * however the practical advantages of compile time type-checking and
 * make simplicity, overweigh the possible re-use gains of a
 * properties file.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class FieldMap
{
    /**
     * Get a field from a string
     * @param the configuration type
     * @return The best Field that matches
     */
    public static Field getField(Choice type)
    {
        Field field = null;

        try
        {
            // We need to treat instances of MultipleChoice differently
            // because they are always OptionsFields whatever their underlying
            // type is.
            if (type instanceof MultipleChoice)
            {
                field = new OptionsField();
            }
            else
            {
                Class clazz = (Class) hash.get(type.getConvertionClass());
                if (clazz != null)
                {
                    field = (Field) clazz.newInstance();
                }
                else
                {
                    log.warn("field type ("+type+") unregistered.");
                    field = new TextField();
                }
            }
        }
        catch (Exception ex)
        {
            log.warn("field type ("+type+") initialization failed:", ex);
            Reporter.informUser(type, ex);

            if (field == null)
            {
                log.warn("field type ("+type+") unregistered.");
                field = new TextField();
            }
        }

        field.setChoice(type);
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
        hash.put(String.class, TextField.class);
        hash.put(Boolean.TYPE, BooleanField.class);
        hash.put(Integer.TYPE, NumberField.class);
        //hash.put("hash", MapField.class);
        //hash.put("array", StringArrayField.class);
        //hash.put("password", PasswordField.class);

        //hash.put("color", ColorField.class);
        hash.put(File.class, FileField.class);
        hash.put(DirectoryChoice.class, FileField.class);
        hash.put(String[].class, FileField.class);
        hash.put(Font.class, FontField.class);
        //hash.put("file", TextViewField.class);
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(FieldMap.class);
}

