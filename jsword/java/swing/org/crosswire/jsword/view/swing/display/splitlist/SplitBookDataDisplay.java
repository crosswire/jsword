package org.crosswire.jsword.view.swing.display.splitlist;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;
import org.crosswire.jsword.view.swing.display.proxy.ProxyBookDataDisplay;
import org.crosswire.jsword.view.swing.display.tab.TabbedBookDataDisplay;
import org.crosswire.jsword.view.swing.passage.PassageGuiUtil;
import org.crosswire.jsword.view.swing.passage.PassageListModel;

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
        mdlPassg.setMode(PassageListModel.LIST_RANGES);
        mdlPassg.setRestriction(PassageConstants.RESTRICT_CHAPTER);

        lstPassg.setModel(mdlPassg);
        lstPassg.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                selection();
            }
        });

        sptPassg.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptPassg.add(scrPassg, JSplitPane.LEFT);
        sptPassg.add(getProxy().getComponent(), JSplitPane.RIGHT);
        sptPassg.setOneTouchExpandable(true);
        sptPassg.setDividerLocation(0.0D);

        scrPassg.getViewport().add(lstPassg);

        pnlMain.setLayout(new BorderLayout());
        pnlMain.add(sptPassg, BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return pnlMain;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.BookData)
     */
    public void setBookData(BookData data) throws BookException
    {
        super.setBookData(data);
        this.data = data;
    }
    
    /**
     * Delete the selected verses
     */
    public void deleteSelected(BibleViewPane view)
    {
        PassageGuiUtil.deleteSelectedVersesFromList(lstPassg);
/*
        Key updated = mdlPassg.getKey();
        Book book = data.getBook();
        setBookData(book.getData(updated));
*/
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
/*
        try
        {
            KeyList selected = PassageGuiUtil.getSelectedKey(lstPassg);
            if (selected.size() == 0)
            {
                setBookData(null);
            }
            else if (selected.size() == 1)
            {
                Book book = data.getBook();
                KeyList context = selected.getContext();
                setBookData(book.getData(context));
            }
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
*/
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
    private JSplitPane sptPassg = new JSplitPane();
    private JScrollPane scrPassg = new JScrollPane();
    private JList lstPassg = new JList();
    private PassageListModel mdlPassg = new PassageListModel();
    private JPanel pnlMain = new JPanel();
}
