
package com.eireneh.bible.view.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.dom.DocumentImpl;

import com.eireneh.util.*;
import com.eireneh.mail.*;
import com.eireneh.config.*;
import com.eireneh.config.servlet.*;
import com.eireneh.util.config.*;
import com.eireneh.mail.config.*;

import com.eireneh.bible.util.*;
import com.eireneh.bible.util.config.*;
import com.eireneh.bible.control.*;
import com.eireneh.bible.book.config.*;
import com.eireneh.bible.book.raw.config.*;
import com.eireneh.bible.book.sword.config.*;

/**
 * The base servlet class that makes it easier for other servets and
 * promotes the use of XML.
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
public abstract class BaseServlet extends HttpServlet
{
    /**
     * Get the Config class to set up up
     */
    public static Config getConfig()
    {
        Config config = new Config("Servlet");

        config.add("Bibles.Cache-Versions", new CacheBiblesChoice());
        config.add("Bibles.Raw.Cache-Data", new CacheDataChoice());
        config.add("Bibles.Sword.Base Directory", new SwordDirChoice());

        config.add("Mail.To", new MailCaptureChoices.EMailToAddrChoice());
        config.add("Mail.From", new MailCaptureChoices.EMailFromAddrChoice());
        config.add("Mail.Server", new MailCaptureChoices.EMailServerChoice());

        config.add("Capture.Mail", new MailCaptureChoices.CaptureEMailChoice());
        config.add("Capture.Log", new LogCaptureChoice());
        config.add("Capture.StdOut", new StdOutCaptureInformChoice());

        config.add("Log.File", new FileChoice());
        config.add("Log.StdOut", new StdOutCaptureLogChoice());

        config.add("Source.JavaDoc", new JavaDocLinkChoice());

        config.add("Advanced.Available Drivers", new DriversChoice());

        return config;
    }

    /**
     * Create a Bible to read references from
     */
    public void init(ServletConfig config) throws ServletException, UnavailableException
    {
        super.init(config);

        try
        {
            ServletContext context = config.getServletContext();
            String init0 = (String) context.getAttribute("init");
            log.fine("init0="+init0);

            Enumeration en = config.getInitParameterNames();
            while (en.hasMoreElements())
            {
                String key = (String) en.nextElement();
                String val = config.getInitParameter(key);
                log.fine(key+"="+val);
            }

            String init = config.getInitParameter("init");
            com.eireneh.bible.util.Project.init(init);

            URL config_url = NetUtil.lengthenURL(Project.getConfigRoot(), "Servlet.properties");

            Config conf = getConfig();
            conf.permanentToLocal(config_url);
            conf.localToApplication(true);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(BaseServlet.class, ex);
        }

        log.fine("init called on "+getClass().getName());
    }

    /**
     * Some basic info about what is going on
     */
    public String getServletInfo()
    {
        return "Project B";
    }

    /**
     * Enter elements into an XML Document that can be transformed into
     * an HTML document using XSL
     * @param state data about the current request
     * @param node The place to start adding data
     * @param request A description of the request
     */
    public abstract void generateData(State state, Node node, HttpServletRequest request) throws Exception;

    /**
     * Respond to a GET request
     * @param request A description of the request
     * @param response Data on the reply
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        State state = new CookieState(request, response);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try
        {
            logAccess(request);

            Document doc = new DocumentImpl();
            generateData(state, doc, request);

            String name = state.getWebStyleName();
            out.println(state.getWebStyle().applyStyleString(doc, name));

            if (state.isDebugging())
                debug(request, response, state, out);
        }
        catch (Throwable ex)
        {
            if (ex instanceof ThreadDeath)
                throw (ThreadDeath) ex;

            logError(request, ex);

            out.println("<html><body>");

            // The exception
            out.println("<h3>Exception</h3>");
            out.println("<pre>");
            ex.printStackTrace(out);
            if (ex instanceof SAXParseException)
            {
                SAXParseException spex = (SAXParseException) ex;
                out.println("Location: line="+spex.getLineNumber()+", col="+spex.getColumnNumber());
                out.println("Public ID="+spex.getSystemId()+" System ID="+spex.getSystemId());
            }
            out.println("</pre>");

            debug(request, response, state, out);
        }
    }

    /**
     * Respond to a POST request
     * @param request A description of the request
     * @param response Data on the reply
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }

    /**
     * Debug output to the web page
     */
    private void debug(HttpServletRequest request, HttpServletResponse response, State state, PrintWriter out) throws IOException
    {
        // The cookies
        out.println("<h3>Cookies</h3>");
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++)
        {
            out.println(cookies[i].getName()+"="+cookies[i].getValue()+"<br>");
        }

        // The headers
        out.println("<h3>Headers</h3>");
        Enumeration en = request.getHeaderNames();
        while (en.hasMoreElements())
        {
            String name = (String) en.nextElement();
            out.println(name+"="+request.getHeader(name)+"<br>");
        }

        // The parameters
        out.println("<h3>Parameters</h3>");
        en = request.getParameterNames();
        while (en.hasMoreElements())
        {
            String name = (String) en.nextElement();
            out.println(name+"="+request.getParameter(name)+"<br>");
        }

        // The request info
        out.println("<h3>Request Info</h3>");
        out.println("getMethod="+request.getMethod()+"<br>");
        out.println("getRequestURI="+request.getRequestURI()+"<br>");
        out.println("getProtocol="+request.getProtocol()+"<br>");
        out.println("getPathInfo="+request.getPathInfo()+"<br>");
        out.println("getRemoteAddr="+request.getRemoteAddr()+"<br>");
        out.println("getRemoteUser="+request.getRemoteUser()+"<br>");

        // The cookie state
        out.println("<h3>Cookie Properties</h3>");
        out.println("<pre>");
        out.println(state.getDebugString());
        out.println("</pre>");
        out.println("</body></html>");
    }

    /**
     * Log an access
     * @param state data about the current request
     */
    public void logAccess(HttpServletRequest request) throws IOException
    {
        log.info(request.getRemoteAddr() + "\t" +
                 request.getRequestURI() + "\t" +
                 request.getHeader("Referer"));
    }

    /**
     * Log an error
     * @param state data about the current request
     */
    public void logError(HttpServletRequest request, Throwable ex) throws IOException
    {
        logAccess(request);
        Reporter.informUser(this, ex);
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(BaseServlet.class);
}
