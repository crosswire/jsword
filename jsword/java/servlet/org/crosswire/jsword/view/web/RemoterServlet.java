
package org.crosswire.jsword.view.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.remote.Converter;
import org.crosswire.jsword.book.remote.ConverterException;
import org.crosswire.jsword.book.remote.LocalRemoter;
import org.crosswire.jsword.book.remote.RemoteConstants;
import org.crosswire.jsword.book.remote.RemoteMethod;
import org.crosswire.jsword.book.remote.Remoter;
import org.crosswire.jsword.book.remote.RemoterException;
import org.crosswire.jsword.util.Project;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * A quick demo of how easy it is to write new front-ends to JSword.
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
public class RemoterServlet extends HttpServlet
{
    /**
     * @see javax.servlet.Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        Project.init();
        remoter = new LocalRemoter();
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        XMLOutputter output = new XMLOutputter();

        try
        {
            RemoteMethod method = requestToMethod(request);
            Document doc = remoter.execute(method);

            output.output(doc, response.getOutputStream());
        }
        catch (RemoterException ex)
        {
            Reporter.informUser(this, ex);

            try
            {
                Document doc = Converter.convertExceptionToDocument(ex);
                output.output(doc, response.getOutputStream());
            }
            catch (ConverterException ex2)
            {
                throw new ServletException(ex2);
            }
        }
    }

    /**
     * Convert an HttpServletRequest into a RemoteMethod call.
     * This is the inverse of
     * {@link org.crosswire.jsword.book.remote.HttpRemoter#execute(RemoteMethod)}
     */
    public static RemoteMethod requestToMethod(HttpServletRequest request)
    {
        Map params = request.getParameterMap();
        String methodname = request.getParameter(RemoteConstants.METHOD_KEY);
        RemoteMethod method = new RemoteMethod(methodname);

        Iterator it = params.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String[] val = (String[]) params.get(key);
            
            // This is slightly dodgy - we basically ignore the fact that HTTP
            // GET and POST allow multiple values for each key, however since we
            // get to define the interface i.e. what the allowed keys are, I
            // don't see this as a big problem.
            if (val.length > 0)
            {
                method.addParam(key, val[0]);
            }
        }

        return method;
    }

    /**
     * The way we answer any questions asked of us.
     */
    private Remoter remoter;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(RemoterServlet.class);
}
