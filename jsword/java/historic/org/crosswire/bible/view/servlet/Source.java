
package com.eireneh.bible.view.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.eireneh.util.*;
import com.eireneh.bible.util.*;
import com.eireneh.bible.control.*;

/**
 * A servlet that displays the source code to an application.
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
public class Source extends BaseServlet
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
        Document doc = node.getOwnerDocument();

        // The sourcecode to look at
        String orig_name = request.getParameter("java");

        SourceAccess access = new SourceAccess(orig_name);

        // The main document heading
        Element web = doc.createElement("web");
        web.setAttribute("title", access.getTitle());
        node.appendChild(web);

        // Hierachy line
        Element line = doc.createElement("line");
        line.setAttribute("title", "You are here");
        web.appendChild(line);

        Element para = doc.createElement("p");
        line.appendChild(para);

        Element link = doc.createElement("a");
        para.appendChild(link);
        link.setAttribute("href", "ssource?java=");
        link.appendChild(doc.createTextNode("START"));

        String[] pkgs = StringUtil.tokenize(access.getJavaName(), ".");
        String links = "";
        for (int i=0; i<pkgs.length; i++)
        {
            para.appendChild(doc.createTextNode(" -> "));
            if (i != 0) links += ".";
            links += pkgs[i];

            link = doc.createElement("a");
            para.appendChild(link);
            link.setAttribute("href", "ssource?java="+links);
            link.appendChild(doc.createTextNode(pkgs[i]));
        }

        // Now we know what we are dealing with:
        switch (access.getType())
        {
        case SourceAccess.FILE:
            // The header
            line = doc.createElement("line");
            line.setAttribute("title", "Source");
            web.appendChild(line);

            // The contents
            para = doc.createElement("pre");
            line.appendChild(para);

            // Read and colourize the file
            CodeColorizer ccol = new CodeColorizer();
            StringBuffer buffer = new StringBuffer();
            BufferedReader din = access.getSource();
            while (true)
            {
                String temp = din.readLine();
                if (temp == null)
                    break;

                buffer.append(ccol.syntaxHighlight(temp));
                buffer.append(StringUtil.NEWLINE);
            }

            try
            {
                // Attempt to treat is as XHTML
                HandlerBase handler = new DOMHandlerBase(para);
                InputSource xml_is = new InputSource(new StringReader("<file>"+buffer.toString()+"</file>"));
                Parser parser = ParserFactory.makeParser(DEFAULT_PARSER_NAME);
                parser.setDocumentHandler(handler);
                parser.setErrorHandler(handler);
                parser.parse(xml_is);
            }
            catch (Exception ex)
            {
                // This isn't pretty, but at least it will get an answer out
                Reporter.informUser(this, ex);
                para.appendChild(doc.createTextNode(buffer.toString()));
            }
            break;

        case SourceAccess.PACKAGE:
            // The package.html file
            line = doc.createElement("line");
            line.setAttribute("title", "Package Documentation");
            web.appendChild(line);
            try
            {
                HandlerBase handler = new DOMHandlerBase(line);
                BufferedReader bin = access.getPackageDoc();

                if (bin != null)
                {
                    InputSource xml_is = new InputSource(bin);

                    Parser parser = ParserFactory.makeParser(DEFAULT_PARSER_NAME);
                    parser.setDocumentHandler(handler);
                    parser.setErrorHandler(handler);
                    parser.parse(xml_is);
                }
                else
                {
                    para = doc.createElement("p");
                    line.appendChild(para);
                    para.appendChild(doc.createTextNode("None"));
                }
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);

                para = doc.createElement("p");
                line.appendChild(para);
                para.appendChild(doc.createTextNode("Not loadable"));
            }

            // The list of sub packages
            line = doc.createElement("line");
            line.setAttribute("title", "Sub-Packages");
            web.appendChild(line);
            para = doc.createElement("ul");
            line.appendChild(para);

            Vector sub = access.getPackageChildren();
            if (sub.size() != 0)
            {
                for (int i=0; i<sub.size(); i++)
                {
                    Element item = doc.createElement("li");
                    para.appendChild(item);

                    link = doc.createElement("a");
                    item.appendChild(link);
                    if (access.getJavaName().equals(""))
                        link.setAttribute("href", "ssource?java="+sub.elementAt(i));
                    else
                        link.setAttribute("href", "ssource?java="+access.getJavaName()+"."+sub.elementAt(i));
                    link.appendChild(doc.createTextNode((String) sub.elementAt(i)));
                }
            }
            else
            {
                Element item = doc.createElement("li");
                para.appendChild(item);
                item.appendChild(doc.createTextNode("None"));
            }

            // The list of classes
            line = doc.createElement("line");
            line.setAttribute("title", "Classes");
            web.appendChild(line);
            para = doc.createElement("ul");
            line.appendChild(para);

            sub = access.getSourceChildren();
            if (sub.size() != 0)
            {
                for (int i=0; i<sub.size(); i++)
                {
                    Element item = doc.createElement("li");
                    para.appendChild(item);

                    link = doc.createElement("a");
                    item.appendChild(link);
                    if (access.getJavaName().equals(""))
                        link.setAttribute("href", "ssource?java="+sub.elementAt(i));
                    else
                        link.setAttribute("href", "ssource?java="+access.getJavaName()+"."+sub.elementAt(i));
                    link.appendChild(doc.createTextNode((String) sub.elementAt(i)));
                }
            }
            else
            {
                Element item = doc.createElement("li");
                para.appendChild(item);
                item.appendChild(doc.createTextNode("None"));
            }
            break;

        case SourceAccess.INVALID:
            Reporter.informUser(this, new Exception("Not Found: java_name="+access.getJavaName()+" orig_name="+orig_name));

            // The header
            line = doc.createElement("line");
            line.setAttribute("title", "Not Found: "+access.getJavaName());
            web.appendChild(line);

            // The contents
            para = doc.createElement("p");
            line.appendChild(para);
            para.appendChild(doc.createTextNode("The request: "+orig_name+" was not found."));
            break;
        }

        // Notes
        line = doc.createElement("line");
        line.setAttribute("title", "About");
        web.appendChild(line);

        para = doc.createElement("p");
        line.appendChild(para);
        para.appendChild(doc.createTextNode("This environment is designed to view source code information not JavaDoc. "));
        para.appendChild(doc.createTextNode("A full set of JavaDoc web pages is available "));
        link = doc.createElement("a");
        para.appendChild(link);
        link.setAttribute("href", access.getJavaDocLink());
        link.appendChild(doc.createTextNode("here"));
        para.appendChild(doc.createTextNode(". (However a full JavaDoc upload is 15Mb, so expect it to be out of date, and have broken links)"));
    }

    /** Default parser name. */
    private static final String DEFAULT_PARSER_NAME = "com.ibm.xml.parsers.SAXParser";
}
