
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
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
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.OSISUtil;
import org.crosswire.jsword.util.Style;

/**
 * Builds a panel on which all the Dictionaries and their entries are visible.
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
public class DictionaryPane extends JPanel
{
    /**
     * Setup the GUI 
     */
    public DictionaryPane()
    {
        jbInit();
    }

    /**
     * GUI initialiser
     */
    private void jbInit()
    {
        lstcomments.setVisibleRowCount(4);
        lstcomments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstcomments.setModel(mdl_comments);
        lstcomments.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newDictionary();
            }
        });
        scrcomments.setViewportView(lstcomments);

        lstentries.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newEntry();
            }
        });
        screntries.setViewportView(lstentries);

        txtdisplay.setEditable(false);
        txtdisplay.setEditorKit(new HTMLEditorKit());
        txtdisplay.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent ev)
            {
                link(ev);
            }
        });
        scrdisplay.setViewportView(txtdisplay);

        sptmain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        sptmain.setTopComponent(screntries);
        sptmain.setBottomComponent(scrdisplay);

        this.setLayout(new BorderLayout(5, 5));
        this.add(scrcomments, BorderLayout.NORTH);
        this.add(sptmain, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Called when someone selects a new Dictionary
     */
    protected void newDictionary()
    {
        SortedSet set;

        try
        {
            Object selected = lstcomments.getSelectedValue();
            DictionaryMetaData cmd = (DictionaryMetaData) selected;
            dict = cmd.getDictionary();
            set = dict.getIndex("");
        }
        catch (BookException ex)
        {
            Reporter.informUser(this, ex);
            set = new TreeSet();
        }

        SortedSetListModel model = new SortedSetListModel(set);
        lstentries.setModel(model);
    }

    /**
     * Called when someone selects a new entry from the current dictionary
     */
    protected void newEntry()
    {
        try
        {
            Key key = (Key) lstentries.getSelectedValue();
            BookData bdata = dict.getData(key);
            SAXEventProvider provider = OSISUtil.getSAXEventProvider(bdata);
            String text = style.applyStyleToString(provider, "simple.xsl");

            txtdisplay.setText(text);
            txtdisplay.select(0, 0);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Called when someone clicks on a hyperlink from the current dictionary
     */
    protected void link(HyperlinkEvent ev)
    {
        log.warn("No listener for "+ev.getURL());
    }

    /**
     * The stylizer
     */
    protected Style style = new Style("swing");

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(DictionaryPane.class);

    private BookFilter filter = BookFilters.getDictionaries();
    private BooksComboBoxModel mdl_comments = new BooksComboBoxModel(filter);
    private Dictionary dict = null;

    private JScrollPane scrcomments = new JScrollPane();
    private JList lstcomments = new JList();
    private JSplitPane sptmain = new JSplitPane();
    private JScrollPane screntries = new JScrollPane();
    private JScrollPane scrdisplay =new JScrollPane();
    private JTextPane txtdisplay = new JTextPane();;
    private JList lstentries = new JList();
}
