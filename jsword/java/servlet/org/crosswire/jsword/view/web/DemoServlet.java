package org.crosswire.jsword.view.web;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;

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
 * @see gnu.gpl.Licence
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
            book = Defaults.getBibleMetaData().getBook();
        }
        catch (Exception ex)
        {
            throw new ServletException("Failed to initialize", ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            Key key = null;

            String search = request.getParameter("search");
            if (search != null)
            {
                request.setAttribute("search", search);
                key = book.find(new Search(search, false));
            }

            String match = request.getParameter("match");
            if (match != null)
            {
                request.setAttribute("match", match);
                PassageTally tally = (PassageTally) book.find(new Search(match, true));
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(tally_trim, PassageConstants.RESTRICT_NONE);
                key = tally;
            }

            String view = request.getParameter("view");
            if (view != null)
            {
                request.setAttribute("view", view);
                key = PassageFactory.createPassage(view);
            }

            if (key instanceof Passage)
            {
                Passage ref = (Passage) key;

                // Do we need multiple pages
                if (ref.countVerses() > page_size)
                {
                    Passage waiting = ref.trimVerses(page_size);

                    // JDK: A deprecation error if you don't, won't build or run on java < 1.4 if you do.
                    //String link = URLEncoder.encode(waiting.getName());
                    String link = URLEncoder.encode(waiting.getName(), "UTF-8");

                    request.setAttribute("next-link", link);
                    request.setAttribute("next-name", waiting.getName());
                    request.setAttribute("next-overview", waiting.getOverview());
                }

                BookData data = book.getData(ref);
                SAXEventProvider osissep = data.getSAXEventProvider();
                SAXEventProvider htmlsep = style.convert(osissep);
                String text = XMLUtil.writeToString(htmlsep);

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

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    private static int tally_trim = 50;
    private static int page_size = 150;
    private Book book;
    private SimpleWebConverter style = new SimpleWebConverter();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DemoServlet.class);
}
