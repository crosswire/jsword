
package org.crosswire.config.servlet;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.crosswire.config.Choice;
import org.crosswire.util.Convert;
import org.crosswire.util.Logger;
import org.crosswire.util.Reporter;

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
     * Custom code for each of the types
     */
    public static Element getHTMLElement(Document doc, String key, Choice choice, String value)
    {
        String type = choice.getType();
        Field field = null;

        Class clazz = (Class) hash.get(type);
        if (clazz != null)
        {
            try
            {
                field = (Field) clazz.newInstance();
            }
            catch (Exception ex)
            {
                log.warning("field type ("+type+") initialization failed:");
                Reporter.informUser(choice, ex);

                if (field == null)
                {
                    log.warning("field type ("+type+") unregistered.");
                    field = new TextField();
                }
            }
        }
        else
        {
            field = new TextField();
        }

        field.setHelpText(choice.getHelpText());
        field.setKey(key);
        field.setDocument(doc);
        field.setOptions(choice.getTypeOptions());
        field.setValue(value);

        return field.getHTMLElement();
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

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.config");

    /**
     * Default hashtable configuration
     */
    static
    {
        hash.put("boolean", BooleanField.class);
        hash.put("text", TextField.class);
        hash.put("action", ActionField.class);
        hash.put("options", OptionsField.class);
        hash.put("number", NumberField.class);
        hash.put("password", PasswordField.class);
        hash.put("file", FileField.class);
        hash.put("color", ColorField.class);
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static abstract class Field
    {
        public void setKey(String key)          { this.key = key; }
        public void setDocument(Document doc)   { this.doc = doc; }
        public void setHelpText(String help)    { this.help = help; }
        public void setValue(String value)      { this.value = value; }
        public void setOptions(Object data)     { this.data = data; }

        /**
         * Get the actual HTML that we can add to a web page.
         */
        public Element getHTMLElement()
        {
            Element ele = doc.createElement("input");

            ele.setAttribute("name", key);
            ele.setAttribute("size", "30");
            ele.setAttribute("value", value);
            ele.setAttribute("title", help);

            return ele;
        }

        /** The Document used to create Elements */
        protected Document doc;

        /** The key to this choice */
        protected String key = "unset";

        /** The value to the choice */
        protected String value = "unset";

        /** The help text to the choice */
        protected String help = "";

        /** The optional data from the choice */
        protected Object data = null;
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class TextField extends Field
    {
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class BooleanField extends Field
    {
        /**
         * Get the actual HTML that we can add to a web page.
         */
        public Element getHTMLElement()
        {
            Element ele = doc.createElement("input");

            ele.setAttribute("type", "checkbox");
            ele.setAttribute("name", key);
            ele.setAttribute("value", "true");
            ele.setAttribute("title", help);

            if (Convert.string2Boolean(value))
                ele.setAttribute("checked", "true");

            return ele;
        }
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class ActionField extends Field
    {
        /**
         * Get the actual HTML that we can add to a web page.
         */
        public Element getHTMLElement()
        {
            Element ele = doc.createElement("input");

            ele.setAttribute("type", "button");
            ele.setAttribute("value", "Custom actions not supportered in HTML");
            ele.setAttribute("disabled", "true");
            ele.setAttribute("title", help);

            return ele;
        }
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class OptionsField extends Field
    {
        /** Get the actual HTML that we can add to a web page. */
        public Element getHTMLElement()
        {
            String[] array = (String[]) data;

            Element ele = doc.createElement("select");
            ele.setAttribute("name", key);
            ele.setAttribute("title", help);

            for (int i=0; i<array.length; i++)
            {
                Element option = doc.createElement("option");
                ele.appendChild(option);
                option.setAttribute("value", array[i]);

                if (value.equals(array[i]))
                    option.setAttribute("selected", "true");

                option.appendChild(doc.createTextNode(array[i]));
            }

            return ele;
        }
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class NumberField extends Field
    {
        /**
         * Get the actual HTML that we can add to a web page.
         */
        public Element getHTMLElement()
        {
            Element ele = doc.createElement("input");

            ele.setAttribute("name", key);
            ele.setAttribute("size", "6");
            ele.setAttribute("value", value);
            ele.setAttribute("title", help);

            return ele;
        }
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class PasswordField extends Field
    {
        /**
         * Get the actual HTML that we can add to a web page.
         */
        public Element getHTMLElement()
        {
            Element ele = doc.createElement("input");

            ele.setAttribute("type", "password");
            ele.setAttribute("name", key);
            ele.setAttribute("size", "10");
            ele.setAttribute("value", value);
            ele.setAttribute("title", help);

            return ele;
        }
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class FileField extends Field
    {
        /**
         * Get the actual HTML that we can add to a web page.
         */
        public Element getHTMLElement()
        {
            Element ele = doc.createElement("input");

            ele.setAttribute("name", key);
            ele.setAttribute("size", "40");
            ele.setAttribute("value", value);
            ele.setAttribute("title", help);

            return ele;
        }
    }

    /**
     * A Field is an HTML representation of a Choice.
     */
    public static class ColorField extends Field
    {
        /** Get the actual HTML that we can add to a web page. */
        public String getHTML()
        {
            StringBuffer retcode = new StringBuffer();

            retcode.append("Red: <select name='"+key+"_red' title='"+help+"'>\n");
            retcode.append("<option value='0'>0</option>\n");
            retcode.append("<option value='1'>1</option>\n");
            retcode.append("<option value='2'>2</option>\n");
            retcode.append("<option value='3'>3</option>\n");
            retcode.append("<option value='4'>4</option>\n");
            retcode.append("<option value='5'>5</option>\n");
            retcode.append("<option value='6'>6</option>\n");
            retcode.append("<option value='7'>7</option>\n");
            retcode.append("<option value='8'>8</option>\n");
            retcode.append("<option value='9'>9</option>\n");
            retcode.append("<option value='10'>10</option>\n");
            retcode.append("<option value='11'>11</option>\n");
            retcode.append("<option value='12'>12</option>\n");
            retcode.append("<option value='13'>13</option>\n");
            retcode.append("<option value='14'>14</option>\n");
            retcode.append("<option value='15'>15</option>\n");
            retcode.append("</select>\n");

            retcode.append(" Green: <select name='"+key+"_green' title='"+help+"'>\n");
            retcode.append("<option value='0'>0</option>\n");
            retcode.append("<option value='1'>1</option>\n");
            retcode.append("<option value='2'>2</option>\n");
            retcode.append("<option value='3'>3</option>\n");
            retcode.append("<option value='4'>4</option>\n");
            retcode.append("<option value='5'>5</option>\n");
            retcode.append("<option value='6'>6</option>\n");
            retcode.append("<option value='7'>7</option>\n");
            retcode.append("<option value='8'>8</option>\n");
            retcode.append("<option value='9'>9</option>\n");
            retcode.append("<option value='10'>10</option>\n");
            retcode.append("<option value='11'>11</option>\n");
            retcode.append("<option value='12'>12</option>\n");
            retcode.append("<option value='13'>13</option>\n");
            retcode.append("<option value='14'>14</option>\n");
            retcode.append("<option value='15'>15</option>\n");
            retcode.append("</select>\n");

            retcode.append(" Blue: <select name='"+key+"_blue' title='"+help+"'>\n");
            retcode.append("<option value='0'>0</option>\n");
            retcode.append("<option value='1'>1</option>\n");
            retcode.append("<option value='2'>2</option>\n");
            retcode.append("<option value='3'>3</option>\n");
            retcode.append("<option value='4'>4</option>\n");
            retcode.append("<option value='5'>5</option>\n");
            retcode.append("<option value='6'>6</option>\n");
            retcode.append("<option value='7'>7</option>\n");
            retcode.append("<option value='8'>8</option>\n");
            retcode.append("<option value='9'>9</option>\n");
            retcode.append("<option value='10'>10</option>\n");
            retcode.append("<option value='11'>11</option>\n");
            retcode.append("<option value='12'>12</option>\n");
            retcode.append("<option value='13'>13</option>\n");
            retcode.append("<option value='14'>14</option>\n");
            retcode.append("<option value='15'>15</option>\n");
            retcode.append("</select>\n");

            return retcode.toString();
        }
    }
}

