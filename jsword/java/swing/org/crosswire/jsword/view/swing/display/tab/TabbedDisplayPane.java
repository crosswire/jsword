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

import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;
import org.crosswire.jsword.view.swing.display.FocusablePart;
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
public class TabbedDisplayPane extends JPanel implements FocusablePart
{
    /**
     * Simple Constructor
     */
    public TabbedDisplayPane()
    {
        pnlView = createInnerDisplayPane();

        initialize();

        center = pnlView.getComponent();
        this.add(center, BorderLayout.CENTER);

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

        this.setLayout(new BorderLayout());
    }

    /**
     * Set the version used for lookup
     */
    public synchronized void setBook(Book version)
    {
        this.book = version;
    }

    /**
     * Set the passage being viewed
     */
    public synchronized void setPassage(Passage ref) throws BookException
    {
        this.whole = ref;

        try
        {
            // Tabbed view or not we should clear out the old tabs
            tabMain.removeAll();
            displays.clear();
            displays.add(pnlView);

            // Do we need a tabbed view
            tabs = (ref != null && ref.countVerses() > pagesize);
            if (tabs)
            {
                // Calc the verses to display in this tab
                Passage cut = (Passage) whole.clone();
                waiting = cut.trimVerses(pagesize);

                // Create the tab
                BookDataDisplay pnlNew = createInnerDisplayPane();
                setDisplay(pnlNew, cut);

                Component display = pnlNew.getComponent();
                tabMain.add(display, shortenName(cut.getName()));
                tabMain.add(pnlMore, Msg.MORE);

                setCenterComponent(tabMain);
            }
            else
            {
                setDisplay(pnlView, ref);
                setCenterComponent(pnlView.getComponent());
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        this.repaint();
    }

    /**
     * @param display
     * @param cut
     * @throws BookException
     */
    private void setDisplay(BookDataDisplay display, Passage cut) throws BookException
    {
        if (cut == null || book == null)
        {
            display.setBookData(null);
        }
        else
        {
            BookData data = book.getData(cut);
            display.setBookData(data);
        }
    }

    /**
     * Make a new component reside in the center of this panel
     */
    private void setCenterComponent(Component comp)
    {
        // And show it is needed
        if (center != comp)
        {
            this.remove(center);
            center = comp;
            this.add(center, BorderLayout.CENTER);
        }
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

            // Calculate the new verses to display
            Passage cut = waiting;
            waiting = cut.trimVerses(pagesize);

            // Create a new tab
            BookDataDisplay pnlNew = createInnerDisplayPane();
            setDisplay(pnlNew, cut);

            Component display = pnlNew.getComponent();
            tabMain.add(display, shortenName(cut.getName()));

            // Do we need a new more tab
            if (waiting != null)
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
    public BookDataDisplay getInnerDisplayPane()
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
        BookDataDisplay idp = new ScrolledBookDataDisplay();
        displays.add(idp);

        // Add all the known listeners to this new BookDataDisplay
        if (hyperlis != null)
        {
            for (Iterator it = hyperlis.iterator(); it.hasNext();)
            {
                HyperlinkListener li = (HyperlinkListener) it.next();
                idp.addHyperlinkListener(li);
            }
        }
        if (mouselis != null)
        {
            for (Iterator it = mouselis.iterator(); it.hasNext();)
            {
                MouseListener li = (MouseListener) it.next();
                idp.addMouseListener(li);
            }
        }

        return idp;
    }

    /**
     * Ensure that the tab names are not too long - 25 chars max
     * @param tabname The name to be shortened
     * @return The first 9 chars followed by ... followed by the last 9
     */
    private static String shortenName(String tabname)
    {
        int len = tabname.length();
        if (len > 25)
        {
            tabname = tabname.substring(0, 9) + " ... " + tabname.substring(len - 9, len); //$NON-NLS-1$
        }

        return tabname;
    }

    /**
     * Accessor for the page size
     */
    public static void setPageSize(int page_size)
    {
        TabbedDisplayPane.pagesize = page_size;
    }

    /**
     * Accessor for the page size
     */
    public static int getPageSize()
    {
        return pagesize;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#copy()
     */
    public void copy()
    {
        getInnerDisplayPane().copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
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
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
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

    /**
     * Forward the mouse listener to our child components
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

    /**
     * Forward the mouse listener to our child components
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
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return getInnerDisplayPane().getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return whole;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getOSISSource()
     */
    public String getOSISSource()
    {
        if (whole == null || book == null)
        {
            return ""; //$NON-NLS-1$
        }

        try
        {
            BookData data = book.getData(whole);
            SAXEventProvider provider = data.getSAXEventProvider();
            SerializingContentHandler handler = new SerializingContentHandler(true);
            provider.provideSAXEvents(handler);

            return handler.toString();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return ""; //$NON-NLS-1$
        }
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
     * The passage that we are displaying (in one or more tabs)
     */
    private Passage whole = null;

    /**
     * The verses that we have not created tabs for yet
     */
    private Passage waiting = null;

    /**
     * The version used for display
     */
    private Book book = null;

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
     * Pointer to whichever of the above is currently in use
     */
    private Component center = null;

    /**
     * Blank thing for the "More..." button
     */
    private JPanel pnlMore = new JPanel();
}
