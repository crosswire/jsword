package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;
import org.crosswire.common.swing.SortedSetListModel;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.OsisUtil;
import org.crosswire.jsword.util.Style;

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
public class TabbedDictionaryPane extends JPanel
{
    /**
     * Simple constructor that uses all the Books
     */
    public TabbedDictionaryPane()
    {
        jbInit();
    }

    /**
     * Initialise the GUI
     */
    private void jbInit()
    {
        addTabs();

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(tab_main, BorderLayout.CENTER);
    }

    /**
     * Add the Books (as filtered) to the tab.
     */
    private void addTabs()
    {
        List books = Books.getBooks(BookFilters.getDictionaries());
        for (Iterator it = books.iterator(); it.hasNext();)
        {
            try
            {
                DictionaryMetaData dmd = (DictionaryMetaData) it.next();
                Dictionary dict = dmd.getDictionary();
                SortedSet entries = dict.getIndex("");

                JEditorPane edit = new JEditorPane();
                edit.setEditable(false);
                edit.setEditorKit(new HTMLEditorKit());
                edit.addHyperlinkListener(new HyperlinkListener()
                {
                    public void hyperlinkUpdate(HyperlinkEvent ev) { link(ev); }
                });

                JScrollPane sedit = new JScrollPane();
                sedit.getViewport().add(edit);
                
                JList list = new JList();
                list.setModel(new SortedSetListModel(entries));
                list.addListSelectionListener(new CustomListSelectionListener(dict, list, edit));

                JScrollPane slist = new JScrollPane();
                slist.getViewport().add(list);
                
                JSplitPane splitter = new JSplitPane();
                splitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitter.add(slist, JSplitPane.TOP);
                splitter.add(sedit, JSplitPane.BOTTOM);
                
                JPanel spacer = new JPanel();
                spacer.setLayout(new BorderLayout());
                spacer.add(splitter, BorderLayout.CENTER);
                spacer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                tab_main.add(spacer, dmd.getInitials());
            }
            catch (BookException ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * PENDING(joe): Do something here
     * @param ev
     */
    private void link(HyperlinkEvent ev)
    {
        log.warn("No listener for "+ev.getURL());
    }

    /**
     * The top level tab set
     */
    private JTabbedPane tab_main = new JTabbedPane();

    /**
     * The stylizer
     */
    private Style style = new Style("swing");

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(PassageInnerPane.class);

    /**
     * So we can update the dictionary display when the list is used
     * @author Joe Walker [joe at eireneh dot com]
     */
    private class CustomListSelectionListener implements ListSelectionListener
    {
        private CustomListSelectionListener(Dictionary dict, JList list, JEditorPane edit)
        {
            this.dict = dict;
            this.list = list;
            this.edit = edit;
        }

        public void valueChanged(ListSelectionEvent ev)
        {
            try
            {
                Key key = (Key) list.getSelectedValue();
                BookData bdata = dict.getData(key);
                SAXEventProvider provider = OsisUtil.getSAXEventProvider(bdata);
                String text = style.applyStyleToString(provider, "simple.xsl");

                edit.setText(text);
                edit.select(0, 0);
            }
            catch (Exception ex)
            {
                Reporter.informUser(list, ex);
            }
        }

        private JEditorPane edit;
        private Dictionary dict;
        private JList list;
    }
}
