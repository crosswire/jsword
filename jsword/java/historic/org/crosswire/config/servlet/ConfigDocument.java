
package org.crosswire.config.servlet;

import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.crosswire.config.Choice;
import org.crosswire.config.Config;
import org.crosswire.util.StringUtil;

/**
* The simplest possible servlet.
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
public class ConfigDocument
{
    /**
    * Configure the ConfigDocument with a set of Choices
    * @param config The set of Choices
    */
    public ConfigDocument(Config config)
    {
        this.config = config;
    }

    /**
    * Create a Document from the set of Choices
    * @return The XML Document
    */
    public void generateQuestionData(Node base, String caller, String answer)
    {
        Document doc = base.getOwnerDocument();
        Element ele;

        // Body start
        Element body = doc.createElement("web");
        if (base != null) base.appendChild(body);
        else              doc.appendChild(body);
        body.setAttribute("title", "Configuration");

        // Form start
        Element form = doc.createElement("form");
        form.setAttribute("action", answer);
        form.setAttribute("method", "post");
        form.setAttribute("id", "form");
        form.setAttribute("name", "form");
        body.appendChild(form);
        ele = doc.createElement("input");
        ele.setAttribute("type", "hidden");
        ele.setAttribute("name", "source");
        ele.setAttribute("value", caller);
        form.appendChild(ele);

        // Loop for each Choice
        String last_path = null;
        Element line = null;
        Element table = null;

        Enumeration en = config.getNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            Choice choice = config.getChoice(key);
            String value = config.getLocal(key);
            String path = "";

            int last_dot = key.lastIndexOf('.');
            if (last_dot != -1) path = key.substring(0, last_dot);
            String name = key.substring(last_dot+1);

            if (!path.equals(last_path))
            {
                line = doc.createElement("line");
                form.appendChild(line);
                line.setAttribute("title", StringUtil.swap(path, ".", ": "));

                table = doc.createElement("table");
                line.appendChild(table);

                last_path = path;
            }

            Element row = doc.createElement("tr");
            table.appendChild(row);

            ele = doc.createElement("td");
            row.appendChild(ele);
            ele.setAttribute("align", "right");
            ele.appendChild(doc.createTextNode(name+": "));

            ele = doc.createElement("td");
            row.appendChild(ele);
            ele.appendChild(FieldMap.getHTMLElement(doc, key, choice, value));
        }

        line = doc.createElement("line");
        form.appendChild(line);
        line.setAttribute("title", "Actions");

        line.appendChild(doc.createElement("br"));
        ele = doc.createElement("input");
        ele.setAttribute("type", "submit");
        ele.setAttribute("value", "OK");
        line.appendChild(ele);
        ele = doc.createElement("input");
        ele.setAttribute("type", "reset");
        ele.setAttribute("value", "Reset");
        line.appendChild(ele);
    }

    /**
    * Create a Document from the set of Choices
    */
    public void generateReportData(Node base, String dest)
    {
        Document doc = base.getOwnerDocument();
        Element ele;

        // Body start
        Element body = doc.createElement("web");
        if (base != null) base.appendChild(body);
        else              doc.appendChild(body);
        body.setAttribute("title", "Updated Configuration");

        // Loop for each Choice
        String last_path = null;
        Enumeration en = config.getNames();
        Element line = null;
        Element table = null;

        line = doc.createElement("line");
        body.appendChild(line);
        line.setAttribute("title", "Settings");

        table = doc.createElement("table");
        line.appendChild(table);

        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            Choice choice = config.getChoice(key);
            String value = config.getLocal(key);
            String path = "";

            int last_dot = key.lastIndexOf('.');
            if (last_dot != -1) path = key.substring(0, last_dot);
            String name = key.substring(last_dot+1);

            Element row = doc.createElement("tr");
            table.appendChild(row);

            ele = doc.createElement("td");
            row.appendChild(ele);
            ele.appendChild(doc.createTextNode(StringUtil.swap(name, ".", " ")+": "));

            ele = doc.createElement("td");
            row.appendChild(ele);
            ele.appendChild(doc.createTextNode(value));
        }

        line = doc.createElement("line");
        body.appendChild(line);
        line.setAttribute("title", "Actions");

        line.appendChild(doc.createElement("br"));

        line.appendChild(doc.createTextNode("If these settings are OK you can "));

        ele = doc.createElement("a");
        line.appendChild(ele);
        ele.setAttribute("href", dest);
        ele.appendChild(doc.createTextNode("carry on with whatever you were doing"));

        line.appendChild(doc.createTextNode(" or you can go back to "));

        ele = doc.createElement("a");
        line.appendChild(ele);
        ele.setAttribute("href", "Config1");
        ele.appendChild(doc.createTextNode("the Configuration Page"));
    }

    /** The set of Choices */
    private Config config;
}
