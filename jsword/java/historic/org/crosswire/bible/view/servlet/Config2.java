
package com.eireneh.bible.view.servlet;

import java.util.*;

import javax.servlet.http.*;

import org.w3c.dom.*;

import com.eireneh.config.*;
import com.eireneh.config.servlet.*;

import com.eireneh.bible.util.*;
import com.eireneh.bible.control.*;

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
* @see docs.Licence
* @author Joe Walker
*/
public class Config2 extends BaseServlet
{
    /**
    * Enter elements into an XML Document that can be transformed into
    * an HTML document using XSL
    * @param state data about the current request
    * @param node The place to start adding data
    * @param request A description of the request
    */
    public void generateData(State state, Node node, HttpServletRequest request) throws Exception
    {
        Config config = state.getConfig();

        // Where do we want to return to
        String last_path = null;
        Enumeration en = config.getNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            String value = request.getParameter(key);
            if (value == null) value = "";
            config.setLocal(key, value);
            config.localToApplication(false);
        }
        state.saveConfig();

        String caller = request.getParameter("source");
        if (caller == null) caller = "sweb";

        ConfigDocument confdoc = new ConfigDocument(config);
        confdoc.generateReportData(node, caller);
    }
}
