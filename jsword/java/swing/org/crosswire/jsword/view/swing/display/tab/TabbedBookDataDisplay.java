package org.crosswire.jsword.view.swing.display.tab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkListener;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.JAXBUtil;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;
import org.crosswire.jsword.view.swing.display.scrolled.ScrolledBookDataDisplay;

/**
 * An inner component of Passage pane that can't show the list.
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
public class TabbedBookDataDisplay implements BookDataDisplay
{
    /**
     * What is the max length for a tab title
     */
    private static final int TITLE_LENGTH = 25;

    /**
     * Simple Constructor
     */
    public TabbedBookDataDisplay()
    {
        pnlView = createInnerDisplayPane();

        initialize();

        // NOTE: when we tried dynamic laf update, these needed special treatment
        // There are times when tab_main or pnl_view are not in visible or
        // attached to the main widget hierachy, so when we change L&F the
        // changes do not get propogated through. The solution is to register
        // them with the L&F handler to be altered when the L&F changes.
        //LookAndFeelUtil.addComponentToUpdate(pnlView);
        //LookAndFeelUtil.addComponentToUpdate(tabMain);
    }

    /**
     * Gui creation
     */
    private void initialize()
    {
        tabMain.setTabPlacement(SwingConstants.BOTTOM);
        tabMain.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                tabChanged();
            }
        });

        pnlMain.setLayout(new BorderLayout());
        center = pnlView.getComponent();
        pnlMain.add(center, BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.BookData)
     */
    public void setBookData(BookData data) throws BookException
    {
        // Tabbed view or not we should clear out the old tabs
        tabMain.removeAll();
        displays.clear();
        displays.add(pnlView);

        datas = JAXBUtil.pagenate(data, pagesize * 10);
        tabs = (datas.size() > 1);
        if (tabs)
        {
            BookData first = (BookData) datas.get(0);

            // Create the first tab
            BookDataDisplay pnlNew = createInnerDisplayPane();
            pnlNew.setBookData(first);

            Component display = pnlNew.getComponent();

            tabMain.add(display, JAXBUtil.getTitle(first, TITLE_LENGTH));
            tabMain.add(pnlMore, Msg.MORE);

            setCenterComponent(tabMain);
        }
        else
        {
            JAXBUtil.getTitle(data, 25);

            // Setup the front tab
            pnlView.setBookData(data);

            setCenterComponent(pnlView.getComponent());
        }

        // tabMain.repaint();
    }

    /**
     * Make a new component reside in the center of this panel
     */
    private void setCenterComponent(Component comp)
    {
        // And show it is needed
        if (center != comp)
        {
            pnlMain.remove(center);
            center = comp;
            pnlMain.add(center, BorderLayout.CENTER);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return pnlMain;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#copy()
     */
    public void copy()
    {
        getInnerDisplayPane().copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public synchronized void addHyperlinkListener(HyperlinkListener li)
    {
        // First add to our list of listeners so when we get more event syncs
        // we can add this new listener to the new sync
        List temp = new ArrayList();
        if (hyperlis == null)
        {
            temp.add(li);
            hyperlis = temp;
        }
        else
        {
            temp.addAll(hyperlis);

            if (!temp.contains(li))
            {
                temp.add(li);
                hyperlis = temp;
            }
        }

        // Now go through all the known syncs and add this one in
        for (Iterator it = displays.iterator(); it.hasNext();)
        {
            BookDataDisplay idp = (BookDataDisplay) it.next();
            idp.addHyperlinkListener(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public synchronized void removeHyperlinkListener(HyperlinkListener li)
    {
        // First remove from the list of listeners
        if (hyperlis != null && hyperlis.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(hyperlis);
            temp.remove(li);
            hyperlis = temp;
        }

        // Now remove from all the known syncs
        for (Iterator it = displays.iterator(); it.hasNext();)
        {
            BookDataDisplay idp = (BookDataDisplay) it.next();
            idp.removeHyperlinkListener(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#addMouseListener(java.awt.event.MouseListener)
     */
    public synchronized void addMouseListener(MouseListener li)
    {
        // First add to our list of listeners so when we get more event syncs
        // we can add this new listener to the new sync
        List temp = new ArrayList();
        if (mouselis == null)
        {
            temp.add(li);
            mouselis = temp;
        }
        else
        {
            temp.addAll(mouselis);

            if (!temp.contains(li))
            {
                temp.add(li);
                mouselis = temp;
            }
        }

        // Now go through all the known syncs and add this one in
        for (Iterator it = displays.iterator(); it.hasNext();)
        {
            BookDataDisplay idp = (BookDataDisplay) it.next();
            idp.addMouseListener(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#removeMouseListener(java.awt.event.MouseListener)
     */
    public synchronized void removeMouseListener(MouseListener li)
    {
        // First remove from the list of listeners
        if (mouselis != null && mouselis.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(mouselis);
            temp.remove(li);
            mouselis = temp;
        }
        
        // Now remove from all the known syncs
        for (Iterator it = displays.iterator(); it.hasNext();)
        {
            BookDataDisplay idp = (BookDataDisplay) it.next();
            idp.removeMouseListener(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.display.BookDataDisplay#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return getInnerDisplayPane().getHTMLSource();
    }

    /**
     * Tabs changed, generate some stuff
     */
    protected void tabChanged()
    {
        try
        {
            // This is someone clicking on more isnt it?
            if (tabMain.getSelectedComponent() != pnlMore)
            {
                return;
            }

            // First remove the old more ... tab that the user has just selected
            tabMain.remove(pnlMore);

            // What do we display next
            int countTabs = tabMain.getTabCount();
            BookData next = (BookData) datas.get(countTabs);

            // Create a new tab
            BookDataDisplay pnlNew = createInnerDisplayPane();
            pnlNew.setBookData(next);
            Component display = pnlNew.getComponent();
            tabMain.add(display, JAXBUtil.getTitle(next, TITLE_LENGTH));

            // Do we need a new more tab
            if (countTabs >= datas.size())
            {
                tabMain.add(pnlMore, Msg.MORE);
            }

            // Select the real new tab in place of any more tabs
            tabMain.setSelectedComponent(display);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Accessor for the current TextComponent
     */
    private BookDataDisplay getInnerDisplayPane()
    {
        if (tabs)
        {
            return (BookDataDisplay) tabMain.getSelectedComponent();
        }
        else
        {
            return pnlView;
        }
    }

    /**
     * Tab creation helper
     */
    private synchronized BookDataDisplay createInnerDisplayPane()
    {
        BookDataDisplay display = new ScrolledBookDataDisplay();
        displays.add(display);

        // Add all the known listeners to this new BookDataDisplay
        if (hyperlis != null)
        {
            for (Iterator it = hyperlis.iterator(); it.hasNext();)
            {
                HyperlinkListener li = (HyperlinkListener) it.next();
                display.addHyperlinkListener(li);
            }
        }

        if (mouselis != null)
        {
            for (Iterator it = mouselis.iterator(); it.hasNext();)
            {
                MouseListener li = (MouseListener) it.next();
                display.addMouseListener(li);
            }
        }

        return display;
    }

    /**
     * Accessor for the page size
     */
    public static void setPageSize(int page_size)
    {
        TabbedBookDataDisplay.pagesize = page_size;
    }

    /**
     * Accessor for the page size
     */
    public static int getPageSize()
    {
        return pagesize;
    }

    /**
     * How many verses on a tab.
     * Should this be a static?
     */
    private static int pagesize = 50;

    /**
     * A list of all the HyperlinkListeners
     */
    private transient List hyperlis;

    /**
     * A list of all the MouseListeners
     */
    private transient List mouselis;

    /**
     * Are we using tabs?
     */
    private boolean tabs = false;

    /**
     * If we are using tabs, this is the main view
     */
    private JTabbedPane tabMain = new JTabbedPane();

    /**
     * If we are not using tabs, this is the main view
     */
    private BookDataDisplay pnlView = null;

    /**
     * A list of all the InnerDisplayPanes so we can control listeners
     */
    private List displays = new ArrayList();

    /**
     * The set of BookDatas for each tab
     */
    private List datas = null;

    /**
     * Pointer to whichever of the above is currently in use
     */
    private Component center = null;

    /**
     * Blank thing for the "More..." button
     */
    private JPanel pnlMore = new JPanel();

    /**
     * The top level component
     */
    private JPanel pnlMain = new JPanel();
}
