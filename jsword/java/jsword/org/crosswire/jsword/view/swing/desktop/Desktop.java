
package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LogPane;
import org.crosswire.common.swing.SystemPropertiesPane;
import org.crosswire.common.swing.config.LookAndFeelChoices;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.ComparePane;
import org.crosswire.jsword.view.swing.book.GeneratorPane;
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
        Desktop shed;

        if (args.length > 0)
            shed = new Desktop(args[0]);
        else
            shed = new Desktop();

        shed.pack();
        GuiUtil.centerWindow(shed);
        shed.setVisible(true);
    }

    /**
     * simple ctor
     */
    public Desktop()
    {
        this("");
    }

    /**
     * Construct a Desktop.
     */
    public Desktop(String prop_base)
    {
        try
        {
            splash = new Splash(this, 60000);

            // Initial setup
            splash.setProgress(5, "Project initialization");
            Project.init(prop_base);

            splash.setProgress(10, "Creating GUI : Ctors (Layouts)");
            VIEW_SDI = new SDIViewLayout(this);
            VIEW_MDI = new MDIViewLayout(this);
            VIEW_TDI = new TDIViewLayout(this);

            splash.setProgress(11, "Creating GUI : Ctors (File Menu)");
            act_file_new = new FileNewAction(this);
            act_file_open = new FileOpenAction(this);
            act_file_save = new FileSaveAction(this);
            act_file_saveas = new FileSaveAsAction(this);
            act_file_saveall = new FileSaveAllAction(this);
            act_file_close = new FileCloseAction(this);
            act_file_closeall = new FileCloseAllAction(this);
            act_file_print = new FilePrintAction(this);
            act_file_exit = new ExitAction(this);

            splash.setProgress(12, "Creating GUI : Ctors (Edit Menu)");
            act_edit_cut = new EditCutAction(this);
            act_edit_copy = new EditCopyAction(this);
            act_edit_paste = new EditPasteAction(this);
            act_edit_blur1 = new BlurAction(this, 1, Passage.RESTRICT_CHAPTER);
            act_edit_blur5 = new BlurAction(this, 5, Passage.RESTRICT_CHAPTER);

            splash.setProgress(13, "Creating GUI : Ctors (View Menu)");
            act_view_sdi = new ViewSDIAction(this);
            act_view_tdi = new ViewTDIAction(this);
            act_view_mdi = new ViewMDIAction(this);
            act_view_tbar = new ViewToolBarAction(this);
            act_view_sbar = new ViewStatusBarAction(this);

            splash.setProgress(14, "Creating GUI : Ctors (List Menu)");
            act_list_toggle = new ListToggleAction(this);
            act_list_delete = new ListDeleteAction(this);

            splash.setProgress(15, "Creating GUI : Ctors (Tools Menu)");
            act_tools_generate = GeneratorPane.createOpenAction(this);
            act_tools_diff = ComparePane.createOpenAction(this);
            act_tools_options = new OptionsAction(this);

            splash.setProgress(16, "Creating GUI : Ctors (Help Menu)");
            act_help_contents = new HelpContentsAction(this);
            act_help_system = SystemPropertiesPane.createOpenAction(this);
            act_help_about = Splash.createOpenAction(this);
            act_help_log = LogPane.createOpenAction(this);
            act_help_debug = new DebugAction(this);

            splash.setProgress(17, "Creating GUI : Ctors (View Buttons)");
            rdo_view_tdi = new JRadioButtonMenuItem(act_view_tdi);
            rdo_view_mdi = new JRadioButtonMenuItem(act_view_mdi);
            rdo_view_sdi = new JRadioButtonMenuItem(act_view_sdi);
            chk_view_sbar = new JCheckBoxMenuItem(act_view_sbar);
            chk_view_tbar = new JCheckBoxMenuItem(act_view_tbar);

            splash.setProgress(18, "Creating GUI : Ctors (Menu Bar)");
            bar_menu = new JMenuBar();
            menu_file = new JMenu();
            menu_edit = new JMenu();
            menu_view = new JMenu();
            menu_list = new JMenu();
            menu_tools = new JMenu();
            menu_help = new JMenu();

            splash.setProgress(19, "Creating GUI : Ctors (Other Components)");
            grp_views = new ButtonGroup();
            pnl_tbar = new JToolBar();
            bar_status = new StatusBar();

            // GUI setup
            splash.setProgress(20, "Creating GUI : Init");
            jbInit();
            setViewLayout(VIEW_SDI);

            // Configuration
            splash.setProgress(80, "General configuration");
            LookAndFeelChoices.addWindow(this);

            // Create a default view for the tdi and mdi schemes
            splash.setProgress(90, "Creating config");
            act_tools_options.createConfig();
            splash.setProgress(95, "Loading config");
            act_tools_options.loadConfig();

            splash.setProgress(100, "Done");
            splash.setVisible(false);
            splash.dispose();
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
        splash.setProgress(22, "Creating GUI : Menus");
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

        splash.setProgress(25, "Creating GUI : Menus");
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

        splash.setProgress(27, "Creating GUI : Menus");
        menu_view.setText("View");
        menu_view.setMnemonic('V');
        menu_view.add(rdo_view_sdi);
        menu_view.add(rdo_view_tdi);
        menu_view.add(rdo_view_mdi);
        menu_view.addSeparator();
        menu_view.add(chk_view_tbar);
        menu_view.add(chk_view_sbar);

        splash.setProgress(30, "Creating GUI : Menus");
        menu_list.setText("List");
        menu_list.setMnemonic('L');
        menu_list.add(act_list_toggle).addMouseListener(bar_status);
        menu_list.addSeparator();
        menu_list.add(act_edit_blur1).addMouseListener(bar_status);
        menu_list.add(act_edit_blur5).addMouseListener(bar_status);
        menu_list.addSeparator();
        menu_list.add(act_list_delete).addMouseListener(bar_status);

        splash.setProgress(32, "Creating GUI : Menus");
        menu_tools.setText("Tools");
        menu_tools.setMnemonic('T');
        //menu_tools.add(act_tools_bench).addMouseListener(bar_status);
        menu_tools.add(act_tools_generate).addMouseListener(bar_status);
        menu_tools.add(act_tools_diff).addMouseListener(bar_status);
        menu_tools.addSeparator();
        //menu_tools.add(act_tools_test).addMouseListener(bar_status);
        //menu_tools.add(act_tools_script).addMouseListener(bar_status);
        menu_tools.addSeparator();
        menu_tools.add(act_tools_options).addMouseListener(bar_status);

        splash.setProgress(35, "Creating GUI : Menus");
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

        splash.setProgress(37, "Creating GUI : Toolbars");
        bar_menu.add(menu_file);
        bar_menu.add(menu_edit);
        bar_menu.add(menu_view);
        bar_menu.add(menu_list);
        bar_menu.add(menu_tools);
        bar_menu.add(menu_help);

        pnl_tbar.setRollover(true);
        pnl_tbar.add(act_file_new).addMouseListener(bar_status);
        pnl_tbar.add(act_file_open).addMouseListener(bar_status);
        pnl_tbar.add(act_file_save).addMouseListener(bar_status);
        pnl_tbar.addSeparator();
        pnl_tbar.add(act_edit_cut).addMouseListener(bar_status);
        pnl_tbar.add(act_edit_copy).addMouseListener(bar_status);
        pnl_tbar.add(act_edit_paste).addMouseListener(bar_status);
        pnl_tbar.addSeparator();
        //pnl_tbar.add(act_tools_bench).addMouseListener(bar_status);
        pnl_tbar.add(act_tools_generate).addMouseListener(bar_status);
        pnl_tbar.add(act_tools_diff).addMouseListener(bar_status);
        //pnl_tbar.add(act_tools_test).addMouseListener(bar_status);
        //pnl_tbar.add(act_tools_script).addMouseListener(bar_status);
        pnl_tbar.addSeparator();
        pnl_tbar.add(act_help_contents).addMouseListener(bar_status);
        pnl_tbar.add(act_help_system).addMouseListener(bar_status);
        pnl_tbar.add(act_help_log).addMouseListener(bar_status);
        pnl_tbar.add(act_help_about).addMouseListener(bar_status);

        splash.setProgress(40, "Creating GUI : Actions");
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
        this.setJMenuBar(bar_menu);

        this.setEnabled(true);
        this.setTitle("Project B");

        accelerateMenu(bar_menu);
    }

    /**
     * Run down the menus adding the accelerators
     */
    public void accelerateMenu(JMenuBar menubar)
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
     * A Select pane is telling us that it has changed, and we might want to
     * update the BibleViewPane and the ViewLayout to reflect any potentially
     * new titles
     */
    public void titleChanged(TitleChangedEvent ev)
    {
        layout.update((BibleViewPane) ev.getSource());
    }

    /**
     * Adds BibleViewPane to the list in this Desktop.
     */
    public boolean addBibleViewPane(BibleViewPane view)
    {
        view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (layout.add(view))
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
        if (layout.remove(view))
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
        return layout.getSelected();
    }

    /**
     * Setup the current view
     */
    public void setViewLayout(ViewLayout newLayout)
    {
        if (newLayout == layout)
            return;

        if (layout != null)
            layout.postDisplay();

        layout = newLayout;

        // SDIViewLayout may well add a view, in which case the view needs to
        // be set already so this must come last.
        layout.preDisplay();
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

    protected ViewLayout VIEW_SDI = null;
    protected ViewLayout VIEW_MDI = null;
    protected ViewLayout VIEW_TDI = null;

    /** The splash screen */
    private Splash splash = null;

    /** The current way the views are layed out */
    private ViewLayout layout = null;

    /** The list of BibleViewPanes being viewed in tdi and mdi workspaces */
    private List views = new ArrayList();

    /** is the status bar visible */
    private boolean view_status = true;

    /** is the toolbar visible */
    private boolean view_tool = true;

    /** The log stream */
    private static Logger log = Logger.getLogger(Desktop.class);

    private Action act_file_new = null;
    private Action act_file_open = null;
    private Action act_file_save = null;
    private Action act_file_saveas = null;
    private Action act_file_saveall = null;
    protected Action act_file_close = null;
    protected Action act_file_closeall = null;
    private Action act_file_print = null;
    private Action act_file_exit = null;

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

    private Action act_list_toggle = null;
    private Action act_list_delete = null;

    private Action act_tools_generate = null;
    private Action act_tools_diff = null;
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
    private JMenu menu_list = null;
    private JMenu menu_tools = null;
    private JMenu menu_help = null;

    private ButtonGroup grp_views = null;
    private JToolBar pnl_tbar = null;
    private StatusBar bar_status = null;
}
