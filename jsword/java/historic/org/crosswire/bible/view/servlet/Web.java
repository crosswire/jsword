
package com.eireneh.bible.view.servlet;

import java.net.URL;
import java.io.*;

import javax.servlet.http.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xerces.dom.*;

import com.eireneh.util.*;
import com.eireneh.bible.util.*;
import com.eireneh.bible.control.*;

/**
* The plan is to have the entire web site delivered via XML/XSL.
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
public class Web extends BaseServlet
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
        String page = request.getParameter("page");

        if (page == null || page.equals(""))
            page = "home";

        URL xml_url = NetUtil.lengthenURL(Project.getWebRoot(), page+".xml");
        InputStream xml_in = xml_url.openStream();
        InputSource xml_is = new InputSource(xml_in);

        // Use SAX to add to the given document
        HandlerBase handler = new DOMHandlerBase(node);

        Parser parser = ParserFactory.makeParser(DEFAULT_PARSER_NAME);
        parser.setDocumentHandler(handler);
        parser.setErrorHandler(handler);
        parser.parse(xml_is);
        //XMLUtil.printDocument(doc);

        // DOMParser xml_parser = new DOMParser();
        // xml_parser.parse(xml_is);
    }

    /** Default parser name. */
    private static final String DEFAULT_PARSER_NAME = "com.ibm.xml.parsers.SAXParser";
}
