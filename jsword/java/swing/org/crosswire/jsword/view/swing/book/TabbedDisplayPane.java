
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.xml.transform.TransformerException;

import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
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
public class TabbedDisplayPane extends JPanel
{
    /**
     * Simple Constructor
     */
    public TabbedDisplayPane()
    {
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
        tab_main.setTabPlacement(JTabbedPane.BOTTOM);
        tab_main.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                newTab(ev);
            }
        });

        this.setLayout(new BorderLayout());
        this.add(pnl_view, BorderLayout.CENTER);
        center = pnl_view;
    }

    /**
     * Set the version used for lookup
     */
    public void setVersion(Bible version)
    {
        this.version = version;
        pnl_view.setVersion(version);
    }

    /**
     * Set the passage being viewed
     */
    public void setPassage(Passage ref) throws IOException, SAXException, BookException, TransformerException
    {
        this.whole = ref;

        try
        {
            // Tabbed view or not we should clear out the old tabs
            tab_main.removeAll();

            // Do we need a tabbed view
            if (ref != null && ref.countVerses() > page_size)
            {
                tabs = true;

                // Calc the verses to display in this tab
                Passage cut = (Passage) whole.clone();
                waiting = cut.trimVerses(page_size);
                String tabname = cut.getName();
                int len = tabname.length();
                if (len > 25)
                {
                    tabname = tabname.substring(0, 9)
                            + " ... "
                            + tabname.substring(len-9, len);
                }

                // Create the tab
                InnerDisplayPane pnl_new = new InnerDisplayPane();
                pnl_new.setVersion(version);
                pnl_new.setPassage(cut);
                tab_main.add(pnl_new, tabname);
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
    protected void newTab(ChangeEvent ev)
    {
        try
        {
            // This is someone clicking on more isnt it?
            if (tab_main.getSelectedComponent() != pnl_more)
                return;

            // First remove the old more ... tab that the user has just selected
            tab_main.remove(pnl_more);

            // Calculate the new verses to display
            Passage cut = waiting;
            waiting = cut.trimVerses(page_size);
            String tabname = cut.getName();
            int len = tabname.length();
            if (len > 25)
            {
                tabname = tabname.substring(0, 9)
                        + " ... "
                        + tabname.substring(len-9, len);
            }

            // Create a new tab
            InnerDisplayPane pnl_new = new InnerDisplayPane();
            pnl_new.setVersion(version);
            pnl_new.setPassage(cut);
            tab_main.add(pnl_new, tabname);

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
    public JTextComponent getJTextComponent()
    {
        if (tabs)
        {
            return pnl_view.getJTextComponent();
        }
        else
        {
            InnerDisplayPane view = (InnerDisplayPane) tab_main.getSelectedComponent();
            return view.getJTextComponent();
        }
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

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    {
        pnl_view.removeMouseListener(li);
        tab_main.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    {
        pnl_view.addMouseListener(li);
        tab_main.addMouseListener(li);
    }

    // Should this be a static?
    private static int page_size = 50;

    /** What is being displayed */
    private Passage whole = null;
    private Passage waiting = null;
    private Bible version = null;
    private boolean tabs = false;

    private JTabbedPane tab_main = new JTabbedPane();
    private JPanel pnl_more = new JPanel();
    private InnerDisplayPane pnl_view = new InnerDisplayPane();
    private Component center = null;
}
