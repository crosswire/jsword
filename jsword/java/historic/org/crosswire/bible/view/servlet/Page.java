
package com.eireneh.bible.view.servlet;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.w3c.dom.*;
import org.apache.xerces.dom.DocumentImpl;

import com.eireneh.util.*;
import com.eireneh.bible.passage.*;
import com.eireneh.bible.book.*;
import com.eireneh.bible.util.*;
import com.eireneh.bible.control.*;

/**
 * Central web Task viewer.
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
public class Page extends BaseServlet
{
    /**
     * Create a Bible to read references from
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }

    /**
     * Enter elements into an XML Document that can be transformed into
     * an HTML document using XSL
     * @param state data about the current request
     * @param node The place to start adding data
     * @param request A description of the request
     */
    public void generateData(State state, Node node, HttpServletRequest request) throws Exception
    {
        Document doc = new DocumentImpl();
        TaskFactory factory = state.getTaskFactory();

        // The current query
        Task task = factory.findComboBoxTask(request.getParameter("type"));
        task.setParams(new String[] { request.getParameter("input") });

        // The header
        Element body = doc.createElement("web");
        body.setAttribute("title", "Bible Web View");
        doc.appendChild(body);

        // The Javascript
        createScriptLine(body);

        Element form = doc.createElement("form");
        body.appendChild(form);
        // The getClass() but did not work under JBuilder, and I don't see
        // why we should use the long name here and the alias everywhere else.
        form.setAttribute("action", "spage" /*getClass().getName()*/);
        form.setAttribute("method", "get");
        form.setAttribute("id", "form");
        form.setAttribute("name", "form");

        createEntryLine(form, task.nextTask(), factory);
        createHistoryLine(form, state, task);

        try
        {
            Element main = doc.createElement("line");
            form.appendChild(main);
            main.setAttribute("title", "Results");

            if (task.isTrimmed())
            {
                main.appendChild(doc.createTextNode("There is a limit of "+state.getMaxVerses()+" verses per page. Click GO to see more."));
                main.appendChild(doc.createElement("br"));
            }

            task.calculate();
            task.getResults(main);
        }
        catch (TaskException ex)
        {
            log.log(Level.WARNING, "Error viewing page", ex);
            createErrorLine(form, ex.getMessage());
        }

        // It would be nice to be able to check for a need to do this
        // however DOM is a complete pain. So we can't do:
        // if (query.getType() == QUERY_PASSAGE)

        String name = state.getPageStyleName();
        state.getPageStyle().applyStyle(doc, node, name);
    }

    /**
     * The script line
     * @param base The Element to add the line to
     */
    private void createScriptLine(Element base)
    {
        Document doc = base.getOwnerDocument();
        Element script = doc.createElement("script");
        base.appendChild(script);
        script.appendChild(doc.createTextNode("\nfunction update_input()\n" +
                                              "{\n" +
                                              "  form.type.selectedIndex = form.history.value;\n" +
                                              "  form.input.value = form.history.options[form.history.selectedIndex].text;\n" +
                                              "}\n"
                                              ));
    }

    /**
     * The history lines
     * @param base The Element to add the line to
     * @param next The default next query
     */
    private void createEntryLine(Element base, Task next, TaskFactory factory)
    {
        Document doc = base.getOwnerDocument();
        Element query = doc.createElement("line");
        base.appendChild(query);
        query.setAttribute("title", "Query");

        Element sel = doc.createElement("select");
        query.appendChild(sel);
        sel.setAttribute("name", "type");
        sel.setAttribute("size", "1");
        sel.setAttribute("value", ""+factory.findComboBoxName(next));

        String[] tasks = factory.getComboBoxNames();
        for (int i=0; i<tasks.length; i++)
        {
            Element opt = doc.createElement("option");
            //if (i == 0) opt.setAttribute("selected", "true");
            opt.setAttribute("value", ""+tasks[i]);
            opt.appendChild(doc.createTextNode(tasks[i]));
            sel.appendChild(opt);
        }

        Element input_ele = doc.createElement("input");
        query.appendChild(input_ele);
        input_ele.setAttribute("name", "input");
        input_ele.setAttribute("size", "25");
        input_ele.setAttribute("value", next.getParams()[0]);

        Element submit = doc.createElement("input");
        query.appendChild(submit);
        submit.setAttribute("value", "GO");
        submit.setAttribute("type", "submit");
    }

    /**
     * The history lines
     * @param base The Element to add the line to
     * @param state The state f the current connection
     * @param query The request that we have been passed in the form data
     */
    private void createHistoryLine(Element base, State state, Task task)
    {
        try
        {
            TaskFactory factory = state.getTaskFactory();
            Document doc = base.getOwnerDocument();
            History history = new FileHistory(state, task);

            int hist_list_size = Math.min(history.getHistorySize(), HIST_LIST_MAX);
            if (hist_list_size <= 1)
                return;

            Element line = doc.createElement("line");
            base.appendChild(line);
            line.setAttribute("title", "History");

            Element sel = doc.createElement("select");
            line.appendChild(sel);
            sel.setAttribute("id", "history");
            sel.setAttribute("multiple", "true");
            sel.setAttribute("style", "WIDTH: 512px");
            sel.setAttribute("size", ""+hist_list_size);
            sel.setAttribute("language", "javascript");
            sel.setAttribute("onchange", "return update_input()");

            int count = 0;
            Enumeration en = history.getHistory();
            while (en.hasMoreElements() && count<FileHistory.HIST_MAX)
            {
                Task that = (Task) en.nextElement();
                String name = factory.findComboBoxName(that);

                Element opt = doc.createElement("option");
                sel.appendChild(opt);
                opt.setAttribute("value", ""+name);
                opt.appendChild(doc.createTextNode(that.getParams()[0]));
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            // If something goes wrong then just ignore the history line.
        }
    }

    /**
     * The error line
     * @param base The Element to add the line to
     * @param error The Error message
     */
    private void createErrorLine(Element base, String error) throws BookException
    {
        Document doc = base.getOwnerDocument();
        Element main = doc.createElement("line");
        base.appendChild(main);
        main.setAttribute("title", "Error");

        main.appendChild(doc.createTextNode(error));
    }

    /** The default Passage to view */
    private static final String DEFAULT_PASSAGE = "Gen 1:1-5";

    /** The maximum size of the history gui list */
    private static final int HIST_LIST_MAX = 4;
}
