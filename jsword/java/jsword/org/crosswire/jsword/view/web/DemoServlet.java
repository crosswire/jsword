
package org.crosswire.jsword.view.web;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.control.search.Engine;
import org.crosswire.jsword.control.search.Matcher;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.style.Style;
import org.jdom.Document;

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
public class DemoServlet extends HttpServlet
{
    /**
     * @see javax.servlet.Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        try
        {
            Project.init();
            version = Bibles.getDefaultBible();
            engine = new Engine(version);
            matcher = new Matcher(version);
        }
        catch (BookException ex)
        {
            throw new ServletException("Failed to initialize", ex);
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            Passage ref = null;
    
            String search = request.getParameter("search");
            if (search != null)
            {
                request.setAttribute("search", search);
                ref = engine.search(search);
            }

            String match = request.getParameter("match");
            if (match != null)
            {
                request.setAttribute("match", match);
                PassageTally tally = matcher.bestMatch(match);
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(tally_trim);
                ref = tally;
            }

            String view = request.getParameter("view");
            if (view != null)
            {
                request.setAttribute("view", view);
                ref = PassageFactory.createPassage(view);
            }
    
            if (ref != null)
            {
                // Do we need multiple pages
                if (ref.countVerses() > page_size)
                {
                    Passage waiting = ref.trimVerses(page_size);
                    String link = URLEncoder.encode(waiting.getName(), "UTF-8");
                    request.setAttribute("next-link", link);
                    request.setAttribute("next-name", waiting.getName());
                    request.setAttribute("next-overview", waiting.getOverview());
                }
    
                BibleData data = version.getData(ref);
                Document xml = data.getDocument();
        
                Document output = style.applyStyle(xml, "Simple");
                String text = style.getString(output);
    
                request.setAttribute("reply", text);
            }
        }
        catch (Exception ex)
        {
            log.error("Failed view", ex);
            throw new ServletException("Failed view", ex);
        }

        getServletContext().getRequestDispatcher("/demo.jsp").forward(request, response);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    private static int tally_trim = 50;
    private static int page_size = 150;
    private Bible version;
    private Engine engine;
    private Matcher matcher;
    private Style style = new Style("web");

    /** The log stream */
    protected static Logger log = Logger.getLogger(DemoServlet.class);
}
