package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;

/**
 * Builds a set of tabs from the list of Books returned by a filtered list
 * of Books.
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
public class TabbedBookPane extends JPanel
{
    /**
     * Simple constructor that uses all the Books
     */
    public TabbedBookPane()
    {
        this(BookFilters.getAll());
    }

    /**
     * A filtered section of the books
     */
    public TabbedBookPane(BookFilter filter)
    {
        if (filter == null)
            throw new NullPointerException("filter can not be null. Use default constructor");

        this.filter = filter;
        jbInit();
    }

    /**
     * Initialise the GUI
     */
    private void jbInit()
    {
        addTabs();

        this.setLayout(new BorderLayout());
        this.add(tab_main,  BorderLayout.CENTER);
    }

    /**
     * Add the Books (as filtered) to the tab.
     */
    private void addTabs()
    {
        List books = Books.getBooks(filter);
        for (Iterator it = books.iterator(); it.hasNext();)
        {
            BookMetaData book = (BookMetaData) it.next();
            
            JScrollPane scroll = new JScrollPane();
            JEditorPane editor = new JEditorPane();

            scroll.getViewport().add(editor, null);
            tab_main.add(scroll, book.getInitials());
        }
    }

    private JTabbedPane tab_main = new JTabbedPane();
    private BookFilter filter = null;
}
