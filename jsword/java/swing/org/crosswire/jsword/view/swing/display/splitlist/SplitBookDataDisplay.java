package org.crosswire.jsword.view.swing.display.splitlist;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.view.swing.book.KeyTreeModel;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;
import org.crosswire.jsword.view.swing.display.proxy.ProxyBookDataDisplay;
import org.crosswire.jsword.view.swing.display.tab.TabbedBookDataDisplay;
import org.crosswire.jsword.view.swing.passage.PassageGuiUtil;

/**
 * A quick Swing Bible display pane.
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
public class SplitBookDataDisplay extends ProxyBookDataDisplay implements BookDataDisplay
{
    /**
     * Initialize the OuterDisplayPane
     */
    public SplitBookDataDisplay()
    {
        super(new TabbedBookDataDisplay());

        initialize();
    }

    /**
     * Create the GUI
     */
    private void initialize()
    {
        tree.setModel(model);
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                selection();
            }
        });

        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.add(scroll, JSplitPane.LEFT);
        split.add(getProxy().getComponent(), JSplitPane.RIGHT);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(0.0D);

        scroll.getViewport().add(tree);

        main.setLayout(new BorderLayout());
        main.add(split, BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return main;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.BookData)
     */
    public void setBookData(BookData data) throws BookException
    {
        this.data = data;
        model = new KeyTreeModel(data.getKey());

        super.setBookData(data);
    }
    
    /**
     * Delete the selected verses
     */
    public void deleteSelected() throws BookException
    {
        PassageGuiUtil.deleteSelectedVersesFromTree(tree);

        Key updated = model.getKey();
        Book book = data.getBook();
        setBookData(book.getData(updated));
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            KeyList selected = PassageGuiUtil.getSelectedKeys(tree);

            if (selected.size() == 0)
            {
                setBookData(null);
            }
            /*
            else if (selected.size() == 1)
            {
                Book book = data.getBook();
                KeyList context = selected.getContext();
                setBookData(book.getData(context));
            }
            */
            else
            {
                Book book = data.getBook();
                setBookData(book.getData(selected));
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * What are we currently viewing?
     */
    private BookData data = null;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(SplitBookDataDisplay.class);

    /*
     * GUI Components
     */
    private JSplitPane split = new JSplitPane();
    private JScrollPane scroll = new JScrollPane();
    private JTree tree = new JTree();
    private KeyTreeModel model = null;
    private JPanel main = new JPanel();
}
