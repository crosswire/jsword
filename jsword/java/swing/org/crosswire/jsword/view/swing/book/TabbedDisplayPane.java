
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkListener;
import javax.xml.transform.TransformerException;

import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.passage.Passage;
import org.xml.sax.SAXException;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class TabbedDisplayPane extends JPanel implements DisplayArea
{
    /**
     * Simple Constructor
     */
    public TabbedDisplayPane()
    {
        idps.add(pnl_view);

        jbInit();

        // There are times when tab_main or pnl_view are not in visible or
        // attached to the main widget hierachy, so when we change L&F the
        // changes do not get propogated through. The solution is to register
        // them with the L&F handler to be altered when the L&F changes.
        LookAndFeelUtil.addComponentToUpdate(pnl_view);
        LookAndFeelUtil.addComponentToUpdate(tab_main);
    }

    /**
     * Gui creation
     */
    private void jbInit()
    {
        tab_main.setTabPlacement(SwingConstants.BOTTOM);
        tab_main.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                newTab();
            }
        });

        this.setLayout(new BorderLayout());
        this.add(pnl_view, BorderLayout.CENTER);
        center = pnl_view;
    }

    /**
     * Set the version used for lookup
     */
    public synchronized void setVersion(Bible version)
    {
        this.version = version;

        // Now go through all the known views and set the version
        for (Iterator it = idps.iterator(); it.hasNext();)
        {
            InnerDisplayPane idp = (InnerDisplayPane) it.next();
            idp.setVersion(version);
        }
    }

    /**
     * Set the passage being viewed
     */
    public synchronized void setPassage(Passage ref) throws IOException, SAXException, BookException, TransformerException
    {
        this.whole = ref;

        try
        {
            // Tabbed view or not we should clear out the old tabs
            tab_main.removeAll();

            idps.clear();
            idps.add(pnl_view);

            // Do we need a tabbed view
            if (ref != null && ref.countVerses() > page_size)
            {
                tabs = true;

                // Calc the verses to display in this tab
                Passage cut = (Passage) whole.clone();
                waiting = cut.trimVerses(page_size);

                // Create the tab
                InnerDisplayPane pnl_new = createInnerDisplayPane(cut);
                tab_main.add(pnl_new, shortenName(cut.getName()));
                tab_main.add(pnl_more, "More ...");

                // And show it is needed
                if (center != tab_main)
                {
                    this.remove(center);
                    this.add(tab_main, BorderLayout.CENTER);
                    center = tab_main;
                }
            }
            else
            {
                tabs = false;

                // Setup the front tab
                pnl_view.setPassage(ref);

                // And show it if needed
                if (center != pnl_view)
                {
                    this.remove(center);
                    this.add(pnl_view, BorderLayout.CENTER);
                    center = pnl_view;
                }
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        this.repaint();
    }

    /**
     * Tabs changed, generate some stuff
     */
    protected void newTab()
    {
        try
        {
            // This is someone clicking on more isnt it?
            if (tab_main.getSelectedComponent() != pnl_more)
            {
                return;
            }

            // First remove the old more ... tab that the user has just selected
            tab_main.remove(pnl_more);

            // Calculate the new verses to display
            Passage cut = waiting;
            waiting = cut.trimVerses(page_size);

            // Create a new tab
            InnerDisplayPane pnl_new = createInnerDisplayPane(cut);
            tab_main.add(pnl_new, shortenName(cut.getName()));

            // Do we need a new more tab
            if (waiting != null)
            {
                tab_main.add(pnl_more, "More ...");
            }

            // Select the real new tab in place of any more tabs
            tab_main.setSelectedComponent(pnl_new);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Accessor for the current TextComponent
     */
    public InnerDisplayPane getInnerDisplayPane()
    {
        if (tabs)
        {
            return (InnerDisplayPane) tab_main.getSelectedComponent();
        }
        else
        {
            return pnl_view;
        }
    }

    /**
     * Tab creation helper
     */
    private synchronized InnerDisplayPane createInnerDisplayPane(Passage cut) throws IOException, SAXException, BookException, TransformerException
    {
        InnerDisplayPane idp = new InnerDisplayPane();
        idp.setVersion(version);
        idp.setPassage(cut);

        idps.add(idp);

        // Add all the known listeners to this new InnerDisplayPane
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
            tabname = tabname.substring(0, 9) + " ... " + tabname.substring(len - 9, len);
        }

        return tabname;
    }

    /**
     * Accessor for the page size
     */
    public static void setPageSize(int page_size)
    {
        TabbedDisplayPane.page_size = page_size;
    }

    /**
     * Accessor for the page size
     */
    public static int getPageSize()
    {
        return page_size;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#cut()
     */
    public void cut()
    {
        getInnerDisplayPane().cut();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#copy()
     */
    public void copy()
    {
        getInnerDisplayPane().copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#paste()
     */
    public void paste()
    {
        getInnerDisplayPane().paste();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#addHyperlinkListener(javax.swing.event.HyperlinkListener)
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
        for (Iterator it = idps.iterator(); it.hasNext();)
        {
            InnerDisplayPane idp = (InnerDisplayPane) it.next();
            idp.addHyperlinkListener(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
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
        for (Iterator it = idps.iterator(); it.hasNext();)
        {
            InnerDisplayPane idp = (InnerDisplayPane) it.next();
            idp.removeHyperlinkListener(li);
        }
    }

    /**
     * Forward the mouse listener to our child components
     */
    public synchronized void removeMouseListener(MouseListener li)
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
        for (Iterator it = idps.iterator(); it.hasNext();)
        {
            InnerDisplayPane idp = (InnerDisplayPane) it.next();
            idp.addMouseListener(li);
        }
    }

    /**
     * Forward the mouse listener to our child components
     */
    public synchronized void addMouseListener(MouseListener li)
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
        for (Iterator it = idps.iterator(); it.hasNext();)
        {
            InnerDisplayPane idp = (InnerDisplayPane) it.next();
            idp.removeMouseListener(li);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getOSISSource()
     */
    public String getOSISSource()
    {
        return getInnerDisplayPane().getOSISSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return getInnerDisplayPane().getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.DisplayArea#getKey()
     */
    public Key getKey()
    {
        return getInnerDisplayPane().getKey();
    }

    /**
     * How many verses on a tab.
     * Should this be a static?
     */
    private static int page_size = 50;

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
    private Bible version = null;

    /**
     * Are we using tabs?
     */
    private boolean tabs = false;

    /**
     * If we are using tabs, this is the main view
     */
    private JTabbedPane tab_main = new JTabbedPane();

    /**
     * If we are not using tabs, this is the main view
     */
    private InnerDisplayPane pnl_view = new InnerDisplayPane();

    /**
     * A list of all the InnerDisplayPanes so we can control listeners
     */
    private List idps = new ArrayList();

    /**
     * Pointer to whichever of the above is currently in use
     */
    private Component center = null;

    /**
     * Blank thing for the "More..." button
     */
    private JPanel pnl_more = new JPanel();
}

