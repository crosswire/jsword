
package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.crosswire.common.swing.CustomAWTExceptionHandler;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LogPane;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.swing.SystemPropertiesPane;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.InnerDisplayPane;
import org.crosswire.jsword.view.swing.book.SidebarPane;
import org.crosswire.jsword.view.swing.book.Splash;
import org.crosswire.jsword.view.swing.book.StatusBar;
import org.crosswire.jsword.view.swing.book.TitleChangedEvent;
import org.crosswire.jsword.view.swing.book.TitleChangedListener;

/**
 * A container for various tools, particularly the BibleGenerator and
 * the Tester. These tools are generally only of use to developers, and
 * not to end users.
 *
 * <p>2 Things to think about, if you change the LaF when you have run
 * some tests already, then the window can grow quite a lot. Also do we
 * want to disable the Exit button if work is going on?</p>
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class Desktop extends JFrame implements TitleChangedListener
{
    /**
     * Central start point.
     * @param args The command line arguments
     */
    public static void main(String[] args)
    {
        Desktop desktop = new Desktop();
        desktop.pack();
        GuiUtil.centerWindow(desktop);
        desktop.setVisible(true);
        
        log.debug("desktop main exiting.");
    }

    /**
     * Construct a Desktop.
     */
    public Desktop()
    {
        try
        {
            splash = new Splash(this, 60000);

            // Initial setup
            splash.setProgress("Creating GUI : Setting-up config");
            act_tools_options = new OptionsAction(this);
            CustomAWTExceptionHandler.setParentComponent(this);

            splash.setProgress("Creating GUI : Loading Configuration System");
            act_tools_options.createConfig();

            splash.setProgress("Creating GUI : Loading Stored Settings");
            act_tools_options.loadConfig();

            splash.setProgress("Creating GUI : Generating Components");
            layouts = new ViewLayout[3];
            layouts[LAYOUT_TYPE_SDI] = new SDIViewLayout(this);
            layouts[LAYOUT_TYPE_TDI] = new TDIViewLayout(this);
            layouts[LAYOUT_TYPE_MDI] = new MDIViewLayout(this);

            act_file_new = new FileNewAction(this);
            act_file_open = new FileOpenAction(this);
            act_file_save = new FileSaveAction(this);
            act_file_saveas = new FileSaveAsAction(this);
            act_file_saveall = new FileSaveAllAction(this);
            act_file_close = new FileCloseAction(this);
            act_file_closeall = new FileCloseAllAction(this);
            act_file_print = new FilePrintAction(this);
            act_file_exit = new ExitAction(this);

            act_edit_cut = new EditCutAction(this);
            act_edit_copy = new EditCopyAction(this);
            act_edit_paste = new EditPasteAction(this);
            act_edit_blur1 = new BlurAction(this, 1, Passage.RESTRICT_CHAPTER);
            act_edit_blur5 = new BlurAction(this, 5, Passage.RESTRICT_CHAPTER);

            act_view_sdi = new ViewSDIAction(this);
            act_view_tdi = new ViewTDIAction(this);
            act_view_mdi = new ViewMDIAction(this);
            act_view_tbar = new ViewToolBarAction(this);
            act_view_sbar = new ViewStatusBarAction(this);

            act_list_delete = new ListDeleteAction(this);

            //act_tools_generate = GeneratorPane.createOpenAction(this);
            //act_tools_diff = ComparePane.createOpenAction(this);

            act_help_contents = new HelpContentsAction(this);
            act_help_system = SystemPropertiesPane.createOpenAction(this);
            act_help_about = Splash.createOpenAction(this);
            act_help_log = LogPane.createOpenAction(this);
            act_help_debug = new DebugAction(this);

            rdo_view_tdi = new JRadioButtonMenuItem(act_view_tdi);
            rdo_view_mdi = new JRadioButtonMenuItem(act_view_mdi);
            rdo_view_sdi = new JRadioButtonMenuItem(act_view_sdi);
            chk_view_sbar = new JCheckBoxMenuItem(act_view_sbar);
            chk_view_tbar = new JCheckBoxMenuItem(act_view_tbar);

            bar_menu = new JMenuBar();
            menu_file = new JMenu();
            menu_edit = new JMenu();
            menu_view = new JMenu();
            menu_tools = new JMenu();
            menu_help = new JMenu();

            grp_views = new ButtonGroup();
            pnl_tbar = new JToolBar();
            bar_status = new StatusBar();
            bar_side = new SidebarPane();
            spt_books = new JSplitPane();

            // GUI setup
            jbInit();

            // Sort out the current ViewLayout. We need to reset current to be
            // initial because the config system may well have changed initial
            ensureAvailableBibleViewPane();
            current = initial;
            BibleViewPane view = (BibleViewPane) iterateBibleViewPanes().next();
            layouts[current].add(view);
            Component comp = layouts[current].getRootComponent();
            spt_books.add(comp, JSplitPane.LEFT);
            layouts[current].getSelected().adjustFocus(); 

            // Preload the PassageInnerPane for faster initial view
            splash.setProgress("Creating GUI : Preloading view system");
            InnerDisplayPane.preload();

            // Configuration
            splash.setProgress("General configuration");
            LookAndFeelUtil.addComponentToUpdate(this);

            splash.done();
        }
        catch (Exception ex)
        {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            ex.printStackTrace();
            ExceptionPane.showExceptionDialog(this, ex);
        }
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void jbInit()
    {
        splash.setProgress("Creating GUI : Laying out menus");
        menu_file.setText("File");
        menu_file.setMnemonic('F');
        menu_file.add(act_file_new).addMouseListener(bar_status);
        menu_file.add(act_file_open).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_close).addMouseListener(bar_status);
        menu_file.add(act_file_closeall).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_print).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_save).addMouseListener(bar_status);
        menu_file.add(act_file_saveas).addMouseListener(bar_status);
        menu_file.add(act_file_saveall).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_exit).addMouseListener(bar_status);

        menu_edit.setText("Edit");
        menu_edit.setMnemonic('E');
        menu_edit.add(act_edit_cut).addMouseListener(bar_status);
        menu_edit.add(act_edit_copy).addMouseListener(bar_status);
        menu_edit.add(act_edit_paste).addMouseListener(bar_status);

        rdo_view_tdi.addMouseListener(bar_status);
        rdo_view_mdi.addMouseListener(bar_status);
        rdo_view_sdi.addMouseListener(bar_status);
        chk_view_tbar.addMouseListener(bar_status);
        chk_view_sbar.addMouseListener(bar_status);
        rdo_view_sdi.setSelected(true);
        chk_view_tbar.setSelected(view_status);
        chk_view_sbar.setSelected(view_tool);
        grp_views.add(rdo_view_mdi);
        grp_views.add(rdo_view_sdi);
        grp_views.add(rdo_view_tdi);

        menu_view.setText("View");
        menu_view.setMnemonic('V');
        menu_view.add(rdo_view_sdi);
        menu_view.add(rdo_view_tdi);
        menu_view.add(rdo_view_mdi);
        menu_view.addSeparator();
        menu_view.add(chk_view_tbar);
        menu_view.add(chk_view_sbar);

        menu_tools.setText("Tools");
        menu_tools.setMnemonic('T');
        menu_tools.add(act_edit_blur1).addMouseListener(bar_status);
        menu_tools.add(act_edit_blur5).addMouseListener(bar_status);
        menu_tools.add(act_list_delete).addMouseListener(bar_status);
        menu_tools.addSeparator();
        //menu_tools.add(act_tools_generate).addMouseListener(bar_status);
        //menu_tools.add(act_tools_diff).addMouseListener(bar_status);
        //menu_tools.addSeparator();
        menu_tools.add(act_tools_options).addMouseListener(bar_status);

        menu_help.setText("Help");
        menu_help.setMnemonic('H');
        menu_help.add(act_help_contents).addMouseListener(bar_status);
        menu_help.addSeparator();
        menu_help.add(act_help_system).addMouseListener(bar_status);
        menu_help.add(act_help_log).addMouseListener(bar_status);
        menu_help.addSeparator();
        menu_help.add(act_help_about).addMouseListener(bar_status);
        menu_help.addSeparator();
        menu_help.add(act_help_debug).addMouseListener(bar_status);

        splash.setProgress("Creating GUI : Toolbars");
        bar_menu.add(menu_file);
        bar_menu.add(menu_edit);
        bar_menu.add(menu_view);
        bar_menu.add(menu_tools);
        bar_menu.add(menu_tools);
        bar_menu.add(menu_help);

        // JToolBar.setRollover(boolean) is not supported in jdk1.3, instead we use reflection
        // to find out whether the method is available, if so call it.
        try
        {
            Class cl = pnl_tbar.getClass();
            Method meth = cl.getMethod("setRollover", new Class[] { Boolean.TYPE });
            meth.invoke(pnl_tbar, new Object[] { Boolean.TRUE });
        }
        catch (NoSuchMethodException ex)
        {
            // we have a java < 1.4 user
        }
        catch (Exception ex)
        {
            // we don't expect this one, print a stack trace
            ex.printStackTrace();
        }

        pnl_tbar.add(act_file_new).addMouseListener(bar_status);
        pnl_tbar.add(act_file_open).addMouseListener(bar_status);
        pnl_tbar.add(act_file_save).addMouseListener(bar_status);
        pnl_tbar.addSeparator();
        pnl_tbar.add(act_edit_cut).addMouseListener(bar_status);
        pnl_tbar.add(act_edit_copy).addMouseListener(bar_status);
        pnl_tbar.add(act_edit_paste).addMouseListener(bar_status);
        pnl_tbar.addSeparator();
        //pnl_tbar.add(act_tools_generate).addMouseListener(bar_status);
        //pnl_tbar.add(act_tools_diff).addMouseListener(bar_status);
        //pnl_tbar.addSeparator();
        pnl_tbar.add(act_help_contents).addMouseListener(bar_status);
        pnl_tbar.add(act_help_system).addMouseListener(bar_status);
        pnl_tbar.add(act_help_log).addMouseListener(bar_status);
        pnl_tbar.add(act_help_about).addMouseListener(bar_status);

        splash.setProgress("Creating GUI : Main Window");
        spt_books.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        spt_books.setOneTouchExpandable(true);
        spt_books.setDividerLocation(1.0D);
        spt_books.add(bar_side, JSplitPane.RIGHT);
        spt_books.setResizeWeight(1.0D);

        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                act_file_exit.actionPerformed(null);
            }
        });
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(pnl_tbar, BorderLayout.NORTH);
        this.getContentPane().add(bar_status, BorderLayout.SOUTH);
        this.getContentPane().add(spt_books, BorderLayout.CENTER);
        this.setJMenuBar(bar_menu);

        this.setEnabled(true);
        this.setTitle("JSword");

        accelerateMenu(bar_menu);
    }

    /**
     * Adds BibleViewPane to the list in this Desktop.
     */
    public boolean addBibleViewPane(BibleViewPane view)
    {
        view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (getViewLayout().add(view))
        {
            view.addTitleChangedListener(this);
            views.add(view);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes BibleViewPane from the list in this Desktop.
     */
    public boolean removeBibleViewPane(BibleViewPane view)
    {
        // From FileCloseAllAction
        if (getViewLayout().remove(view))
        {
            views.remove(view);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Iterate through the list of views
     */
    public Iterator iterateBibleViewPanes()
    {
        return views.iterator();
    }

    /**
     * Find the selected BibleViewPane.
     * @return BibleViewPane
     */
    public BibleViewPane getSelectedBibleViewPane()
    {
        return getViewLayout().getSelected();
    }

    /**
     * Returns the view_status.
     * @return boolean
     */
    public boolean isStatusBarVisible()
    {
        return view_status;
    }

    /**
     * Sets the view_status.
     * @param view_status The view_status to set
     */
    public void setStatusBarVisible(boolean view_status)
    {
        bar_status.setVisible(true);
        this.view_status = view_status;
    }

    /**
     * Returns the view_tool.
     * @return boolean
     */
    public boolean isToolbarVisible()
    {
        return view_tool;
    }

    /**
     * Sets the view_tool.
     * @param view_tool The view_tool to set
     */
    public void setToolbarVisible(boolean view_tool)
    {
        pnl_tbar.setVisible(true);
        this.view_tool = view_tool;
    }

    /**
     * Are the close buttons enabled?
     * @param The enabled state
     */
    public void setCloseEnabled(boolean b)
    {
        act_file_close.setEnabled(false);
        act_file_closeall.setEnabled(false);
    }

    /**
     * A Select pane is telling us that it has changed, and we might want to
     * update the BibleViewPane and the ViewLayout to reflect any potentially
     * new titles
     */
    public void titleChanged(TitleChangedEvent ev)
    {
        BibleViewPane bvp = (BibleViewPane) ev.getSource();
        getViewLayout().updateTitle(bvp);
    }

    /**
     * Run down the menus adding the accelerators
     */
    private void accelerateMenu(JMenuBar menubar)
    {
        for (int i = 0; i < menubar.getMenuCount(); i++)
        {
            JMenu menu = menubar.getMenu(i);
            for (int j = 0; j < menu.getItemCount(); j++)
            {
                JMenuItem item = menu.getItem(j);
                if (item instanceof AbstractButton)
                {
                    AbstractButton button = (AbstractButton) item;
                    Action action = button.getAction();

                    if (action != null)
                    {
                        KeyStroke accel = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
                        if (accel != null)
                        {
                            item.setAccelerator(accel);
                        }
                    }
                }
            }
        }
    }

    /**
     * If there are no current BibleViewPane then add one in.
     * final because the ctor calls this method
     */
    public final void ensureAvailableBibleViewPane()
    {
        // If there are no views in the pool, create one
        if (!iterateBibleViewPanes().hasNext())
        {
            BibleViewPane view = new BibleViewPane();
            addBibleViewPane(view);
        }
    }

    /**
     * What is the current layout?
     */
    private ViewLayout getViewLayout()
    {
        return layouts[current];
    }

    /**
     * Setup the current view
     */
    public void setLayoutType(int next)
    {
        // Check this is a change
        if (this.current == next)
            return;

        // Go through the views removing them from the layout
        Iterator it = iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            layouts[current].remove(view);
        }
        Component comp = layouts[current].getRootComponent();
        spt_books.remove(comp);

        // set the new layout to be current
        this.current = next;

        // Go through the views adding them to the layout
        // SDIViewLayout may well add a view, in which case the view needs to
        // be set already so this must come last.
        it = iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            layouts[current].add(view);
        }
        comp = layouts[current].getRootComponent();
        spt_books.add(comp, JSplitPane.LEFT);

        // Allow the current BibleViewPane to set the focus in the right place
        layouts[current].getSelected().adjustFocus(); 
    }

    /**
     * What is the initial layout state?
     */
    public static int getInitialLayoutType()
    {
        return initial;
    }

    /**
     * What should the initial layout state be?
     */
    public static void setInitialLayoutType(int initial)
    {
        Desktop.initial = initial;
    }

    /** Single document interface */
    protected static final int LAYOUT_TYPE_SDI = 0;

    /** Tabbed document interface */
    protected static final int LAYOUT_TYPE_TDI = 1;

    /** Multiple document interface */
    protected static final int LAYOUT_TYPE_MDI = 2;

    /** The initial layout state */
    private static int initial = LAYOUT_TYPE_SDI;

    /** The array of valid layouts */
    protected ViewLayout[] layouts; 

    /** The current way the views are layed out */
    private int current = initial;

    /** The list of BibleViewPanes being viewed in tdi and mdi workspaces */
    private List views = new ArrayList();

    /** is the status bar visible */
    private boolean view_status = true;

    /** is the toolbar visible */
    private boolean view_tool = true;

    /** The log stream */
    private static final Logger log = Logger.getLogger(Desktop.class);

    /* GUI components */
    private Splash splash = null;
    private Action act_file_new = null;
    private Action act_file_open = null;
    private Action act_file_save = null;
    private Action act_file_saveas = null;
    private Action act_file_saveall = null;
    private Action act_file_close = null;
    private Action act_file_closeall = null;
    private Action act_file_print = null;
    protected Action act_file_exit = null;

    private Action act_edit_cut = null;
    private Action act_edit_copy = null;
    private Action act_edit_paste = null;
    private Action act_edit_blur1 = null;
    private Action act_edit_blur5 = null;

    private Action act_view_sdi = null;
    private Action act_view_tdi = null;
    private Action act_view_mdi = null;
    private Action act_view_tbar = null;
    private Action act_view_sbar = null;

    private Action act_list_delete = null;

    //private Action act_tools_generate = null;
    //private Action act_tools_diff = null;
    private OptionsAction act_tools_options = null;

    private Action act_help_contents = null;
    private Action act_help_system = null;
    private Action act_help_about = null;
    private Action act_help_log = null;
    private Action act_help_debug = null;

    private JRadioButtonMenuItem rdo_view_tdi = null;
    private JRadioButtonMenuItem rdo_view_mdi = null;
    private JRadioButtonMenuItem rdo_view_sdi = null;
    private JCheckBoxMenuItem chk_view_sbar = null;
    private JCheckBoxMenuItem chk_view_tbar = null;

    private JMenuBar bar_menu = null;
    private JMenu menu_file = null;
    private JMenu menu_edit = null;
    private JMenu menu_view = null;
    private JMenu menu_tools = null;
    private JMenu menu_help = null;

    private ButtonGroup grp_views = null;
    private JToolBar pnl_tbar = null;
    private StatusBar bar_status = null;
    private SidebarPane bar_side = null;
    private JSplitPane spt_books = null;
}
