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
import org.crosswire.jsword.book.Books;
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
    private static final String FIELD_VIEW = "view"; //$NON-NLS-1$
    private static final String FIELD_MATCH = "match"; //$NON-NLS-1$
    private static final String FIELD_SEARCH = "search"; //$NON-NLS-1$

    /**
     * @see javax.servlet.Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        try
        {
            String bookname = config.getInitParameter("book-name"); //$NON-NLS-1$
            book = Books.installed().getBookMetaData(bookname).getBook();
        }
        catch (Exception ex)
        {
            throw new ServletException(Msg.INIT_FAILED.toString(), ex);
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

            String search = request.getParameter(FIELD_SEARCH);
            if (search != null)
            {
                request.setAttribute(FIELD_SEARCH, search);
                key = book.find(new Search(search, false));
            }

            String match = request.getParameter(FIELD_MATCH);
            if (match != null)
            {
                request.setAttribute(FIELD_MATCH, match);
                PassageTally tally = (PassageTally) book.find(new Search(match, true));
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(tallyTrim, PassageConstants.RESTRICT_NONE);
                key = tally;
            }

            String view = request.getParameter(FIELD_VIEW);
            if (view != null)
            {
                request.setAttribute(FIELD_VIEW, view);
                key = PassageFactory.createPassage(view);
            }

            if (key instanceof Passage)
            {
                Passage ref = (Passage) key;

                // Do we need multiple pages
                if (ref.countVerses() > pageSize)
                {
                    Passage waiting = ref.trimVerses(pageSize);

                    // JDK: A deprecation error if you don't, won't build or run on java < 1.4 if you do.
                    //String link = URLEncoder.encode(waiting.getName());
                    String link = URLEncoder.encode(waiting.getName(), "UTF-8"); //$NON-NLS-1$

                    request.setAttribute("next-link", link); //$NON-NLS-1$
                    request.setAttribute("next-name", waiting.getName()); //$NON-NLS-1$
                    request.setAttribute("next-overview", waiting.getOverview()); //$NON-NLS-1$
                }

                BookData data = book.getData(ref);
                SAXEventProvider osissep = data.getSAXEventProvider();
                SAXEventProvider htmlsep = style.convert(osissep);
                String text = XMLUtil.writeToString(htmlsep);

                request.setAttribute("reply", text); //$NON-NLS-1$
            }
        }
        catch (Exception ex)
        {
            log.error("Failed view", ex); //$NON-NLS-1$
            throw new ServletException("Failed view", ex); //$NON-NLS-1$
        }

        getServletContext().getRequestDispatcher("/demo.jsp").forward(request, response); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    private static int tallyTrim = 50;
    private static int pageSize = 150;
    private Book book;
    private SimpleWebConverter style = new SimpleWebConverter();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DemoServlet.class);
}
