
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.view.swing.event.CommandEvent;
import org.crosswire.jsword.view.swing.event.CommandListener;
import org.crosswire.jsword.view.swing.event.VersionEvent;
import org.crosswire.jsword.view.swing.event.VersionListener;
import org.crosswire.jsword.view.swing.passage.PassageList;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;

/**
 * A quick Swing Bible display pane.
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
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public class PassagePane extends JPanel implements VersionListener, CommandListener
{
    /**
     * Initialize the PassagePane
     */
    public PassagePane()
    {
        try
        {
            version = Bibles.getDefaultBible();
            txt_view.setVersion(version);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }

        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        CustomMouseListener cml = new CustomMouseListener();

        lst_ranges.addMouseListener(cml);
        lst_ranges.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev) { selection(); }
        });
        scr_ranges.getViewport().add(lst_ranges, null);

        menu_view.add(act_list);
        menu_view.addSeparator();
        menu_view.add(act_options);

        txt_view.addMouseListener(cml);

        spt_top.add(scr_ranges, JSplitPane.LEFT);
        // spt_top.add(txt_view, JSplitPane.RIGHT);

        this.setLayout(new BorderLayout());
        this.add(txt_view, BorderLayout.CENTER);
    }

    /**
     * For popup menus
     */
    class CustomMouseListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent ev)
        {
            triggerMenu(ev);
        }
        public void mouseReleased(MouseEvent ev)
        {
            triggerMenu(ev);
        }
        private void triggerMenu(MouseEvent ev)
        {
            if (ev.isPopupTrigger())
            {
                menu_view.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        }
    }

    /**
     * Someone wants us to show in a new version
     */
    public void versionChanged(VersionEvent ev)
    {
        version = ev.getBible();
        txt_view.setVersion(version);

        // refresh the view
        if (view_mode == VIEW_LIST)
        {
            selection();
        }
        else
        {
            try
            {
                Passage ref = lst_ranges.getPassage();
                txt_view.setPassage(ref);
            }
            catch (Throwable ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Someone wants us to display a new passage
     */
    public void commandMade(CommandEvent ev)
    {
        setPassage(ev.getPassage());
    }

    /**
     * Set the passage to be displayed
     */
    public void setPassage(Passage ref)
    {
        try
        {
            lst_ranges.setPassage(ref);
            txt_view.setPassage(ref);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Get the passage being displayed
     */
    public Passage getPassage()
    {
        return lst_ranges.getPassage();
    }

    /**
     * Accessor for the PassageList
     */
    public PassageList getPassageList()
    {
        if (view_mode == VIEW_LIST)
        {
            return lst_ranges;
        }
        else
        {
            return null;
        }
    }

    /**
     * Is the list showing
     */
    public boolean isListVisible()
    {
        return view_mode == VIEW_LIST;
    }

    /**
     * Toggle the list state
     */
    public void toggleList()
    {
        if (view_mode == VIEW_SIMPLE)
        {
            view_mode = VIEW_LIST;

            PassagePane.this.remove(txt_view);
            spt_top.add(txt_view, JSplitPane.RIGHT);
            PassagePane.this.add(spt_top, BorderLayout.CENTER);
        }
        else
        {
            view_mode = VIEW_SIMPLE;

            spt_top.remove(txt_view);
            PassagePane.this.remove(spt_top);
            PassagePane.this.add(txt_view, BorderLayout.CENTER);
        }

        PassagePane.this.validate();
    }

    /**
     * Someone clicked on a value in the list
     */
    private void selection()
    {
        try
        {
            if (view_mode == VIEW_LIST)
            {
                Object[] ranges = lst_ranges.getSelectedValues();

                Passage ref0 = PassageFactory.createPassage();
                for (int i=0; i<ranges.length; i++)
                {
                    ref0.add((VerseRange) ranges[i]);
                }
                txt_view.setPassage(ref0);
            }
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone clicked on view list
     */
    public class ToggleListAction extends EirAbstractAction
    {
        public ToggleListAction()
        {
            super("Toggle List",
                  "/toolbarButtonGraphics/text/AlignJustify16.gif",
                  "/toolbarButtonGraphics/text/AlignJustify24.gif",
                  "Toggles the passage list", "Toggles display of the passage list.",
                  'L', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            toggleList();
        }
    }

    /**
     * Someone clicked on view list
     */
    public class ViewOptionsAction extends EirAbstractAction
    {
        public ViewOptionsAction()
        {
            super("View Options ...",
                  "/toolbarButtonGraphics/general/Properties16.gif",
                  "/toolbarButtonGraphics/general/Properties24.gif",
                  "Display view options", "Display options for configuring the view.",
                  'V', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(PassagePane.this, "Not implemented");
        }
    }

    private String view_mode = VIEW_SIMPLE;
    private static final String VIEW_LIST = "list";
    private static final String VIEW_SIMPLE = "simple";

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.view");

    /** What is being displayed */
    private Bible version = null;
    private PassageList lst_ranges = new PassageList();

    private Action act_list = new ToggleListAction();
    private Action act_options = new ViewOptionsAction();

    private JSplitPane spt_top = new JSplitPane();
    private JScrollPane scr_ranges = new JScrollPane();
    private PassageTabbedPane txt_view = new PassageTabbedPane();
    private JPopupMenu menu_view = new JPopupMenu();
}
