
package org.crosswire.common.config.choices;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import org.crosswire.common.config.Choice;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.UserLevel;

/**
 * ClassChoices declares the Choices and actions needed to
 * dynamically change the look and feel (PLAF) and to add new PLAFs
 * without needing to restart.
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
public abstract class ClassChoices implements Serializable
{
    /**
     * Create a ClassChoice
     * @param base The class that all the options must inherit from
     * @param options The array of available options
     */
    public ClassChoices(Class base, Hashtable options) throws ClassNotFoundException
    {
        this.options = options;
        this.base = base;

        // Check that the options all inherit from base
        Enumeration en = options.keys();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            String value = (String) options.get(key);
            Class option = Class.forName(value);

            if (!base.isAssignableFrom(option))
                throw new ClassCastException(option.getName());
        }
    }

    /**
     * All changes to the current Class are piped through this method. In
     * the base class it does nothing, however implementations of this will
     * probaly want to use this hook to report changes to the rest of the
     * program
     * @return The Class chosen in this Field
     */
    protected abstract Class getCurrentClass();

    /**
     * All changes to the current Class are piped through this method. In
     * the base class it does nothing, however implementations of this will
     * probaly want to use this hook to report changes to the rest of the
     * program
     * @param new_current The Class chosen in this Field
     */
    protected abstract void setCurrentClass(Class new_current) throws Exception;

    /**
     * Gets the Choice for the currently selected Class
     * @return The Choice for the current Class
     */
    public Choice getCurrentChoice()
    {
        return new Current();
    }

    /**
     * Gets the Choice for the currently selected Class
     * @return The Choice for the current Class
     */
    public Choice getOptionsChoice()
    {
        return new Options(base);
    }

    /** The available Classes */
    protected Hashtable options;

    /** The available Classes */
    protected Class base;

    /**
     * This defines a Choice interface to display a ComboBox
     * with the available Classes.
     */
    public class Current extends AbstractChoice
    {
        /** Generalized read String from the Properties file */
        public String getString()
        {
            String key = null;
            String value = null;
            Class option = null;

            Class curr = getCurrentClass();

            // We need to find the name of this class
            try
            {
                Enumeration en = options.keys();
                while (en.hasMoreElements())
                {
                    key = (String) en.nextElement();
                    value = (String) options.get(key);
                    option = Class.forName(value);

                    if (curr.equals(option))
                        return key;
                }
            }
            catch (ClassNotFoundException ex)
            {
                Reporter.informUser(this, ex);
            }

            Enumeration en = options.keys();
            return (String) en.nextElement();
        }

        /** Is this an allowed value */
        public void setString(String key) throws Exception
        {
            String class_name = (String) options.get(key);
            setCurrentClass(Class.forName(class_name));
        }

        /** A GUI component */
        public String getType()
        {
            return "options";
        }

        /** A GUI component */
        public Object getTypeOptions()
        {
            String[] retcode = new String[options.size()];
            int i = 0;

            Enumeration en = options.keys();
            while (en.hasMoreElements())
            {
                retcode[i++] = (String) en.nextElement();
            }

            return retcode;
        }

        /** Some help text */
        public String getHelpText()
        {
            return "This is the current implementation.";
        }
    }

    /**
     * This defines a Choice interface to display a table
     * to edit the available Classes
     */
    public class Options extends HashtableChoice
    {
        /** Create an Options Choice */
        public Options(Class base)
        {
            super(base);
        }

        /** Generalized read Object from the Properties file */
        public Hashtable getHashtable()
        {
            return options;
        }

        /** Generalized set Object to the Properties file */
        public void setHashtable(Hashtable data)
        {
            options = data;
        }

        /** Override this to check and note any change */
        public String getType()
        {
            return "hash";
        }

        /** A GUI component */
        public Object getTypeOptions()
        {
            return base;
        }

        /** Some help text */
        public String getHelpText()
        {
            return "The available implementations";
        }

        /**
         * This is not something we'd like to allow basic users to do
         */
        public UserLevel getUserLevel()
        {
            return UserLevel.LEVEL_ADVANCED;
        }
    }
}

