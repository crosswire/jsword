
package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.log4j.Logger;
import org.crosswire.common.config.Config;
import org.crosswire.common.config.swing.SwingConfig;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.LogPane;
import org.crosswire.common.swing.SystemPropertiesPane;
import org.crosswire.common.swing.config.DisplayExceptionChoice;
import org.crosswire.common.swing.config.LookAndFeelChoices;
import org.crosswire.common.swing.config.ShelfExceptionChoice;
import org.crosswire.common.swing.config.SourcePathChoice;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.config.UserLevelChoice;
import org.crosswire.jsword.book.config.CacheBiblesChoice;
import org.crosswire.jsword.book.config.DriversChoice;
import org.crosswire.jsword.book.raw.config.CacheDataChoice;
import org.crosswire.jsword.book.sword.config.SwordDirChoice;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.ComparePane;
import org.crosswire.jsword.view.swing.book.EirAbstractAction;
import org.crosswire.jsword.view.swing.book.MaintenancePane;
import org.crosswire.jsword.view.swing.book.PassagePane;
import org.crosswire.jsword.view.swing.book.SelectPane;
import org.crosswire.jsword.view.swing.book.Splash;
import org.crosswire.jsword.view.swing.book.StatusBar;
import org.crosswire.jsword.view.swing.passage.PassageList;

/**
 * A container for various tools, particularly the BibleGenerator and
 * the Tester. These tools are generally only of use to developers, and
 * not to end users.
 *
 * <p>2 Things to think about, if you change the LaF when you have run
 * some tests already, then the window can grow quite a lot. Also do we
 * want to disable the Exit button if work is going on?</p>
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
public class Tools extends JFrame
{
    public Tools()
    {
        this("");
    }

    /**
     * Construct a ShedFrame.
     */
    public Tools(String prop_base)
    {
        try
        {
            splash = new Splash(this, 60000);

            // Initial setup
            splash.setProgress(10, "Project initialization");
            Project.init(prop_base);

            // Configuration
            splash.setProgress(20, "General configuration");
            LookAndFeelChoices.addWindow(this);

            LookAndFeelChoices plaf_class = new LookAndFeelChoices();
            config = new Config("Tool Shed Options");
            config.add("Bibles.Cache Versions", new CacheBiblesChoice());
            config.add("Bibles.Raw.Cache Data", new CacheDataChoice());
            config.add("Bibles.Sword.Base Directory", new SwordDirChoice());

            config.add("Looks.Current Look", plaf_class.getCurrentChoice());
            config.add("Looks.Available Looks", plaf_class.getOptionsChoice());

            config.add("Reports.Exceptions to Dialog Box", new DisplayExceptionChoice());
            config.add("Reports.Exceptions to Log Window", new ShelfExceptionChoice());

            config.add("Advanced.Source Path", new SourcePathChoice());
            config.add("Advanced.User Level", new UserLevelChoice());
            config.add("Advanced.Available Drivers", new DriversChoice());

            config.setProperties(Project.resource().getProperties("Tools"));
            config.localToApplication(true);

            // GUI setup
            splash.setProgress(50, "Creating GUI");
            jbInit();
            setViewLayout(VIEW_SDI);

            // Create a default view for the tdi and mdi schemes
            splash.setProgress(90, "Other initialization");

            // Script pane setup
            /*
            pnl_script.declareBean("tools", this, this.getClass());
            pnl_script.declareBean("views", views, views.getClass());
            String[] names = Bibles.getBibleNames();
            for (int i=0; i<names.length; i++)
            {
                String varname = names[i].toLowerCase();
                varname = varname.replace(' ', '_');
                varname = varname.replace('-', '_');

                try
                {
                    Bible bible = Bibles.getBible(names[i]);
                    pnl_script.declareBean(varname, bible, bible.getClass());
                }
                catch (Exception ex)
                {
                    Reporter.informUser(this, ex);
                }
            }
            */

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
        pnl_maint = new MaintenancePane();
        //pnl_bench = new BenchmarkPane();
        pnl_comp = new ComparePane();
        //pnl_tester = new TesterPane();
        //pnl_script = new ScriptPane();
        pnl_props = new SystemPropertiesPane();

        splash.setProgress(52, "Creating GUI : Menus");
        menu_file.setText("File");
        menu_file.setMnemonic('F');
        menu_file.add(act_file_new).addMouseListener(bar_status);
        menu_file.add(act_file_open).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_save).addMouseListener(bar_status);
        menu_file.add(act_file_saveas).addMouseListener(bar_status);
        menu_file.add(act_file_saveall).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_close).addMouseListener(bar_status);
        menu_file.add(act_file_closeall).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_print).addMouseListener(bar_status);
        menu_file.addSeparator();
        menu_file.add(act_file_restart).addMouseListener(bar_status);
        menu_file.add(act_file_exit).addMouseListener(bar_status);

        splash.setProgress(55, "Creating GUI : Menus");
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

        splash.setProgress(57, "Creating GUI : Menus");
        menu_view.setText("View");
        menu_view.setMnemonic('V');
        menu_view.add(rdo_view_sdi);
        menu_view.add(rdo_view_tdi);
        menu_view.add(rdo_view_mdi);
        menu_view.addSeparator();
        menu_view.add(chk_view_tbar);
        menu_view.add(chk_view_sbar);

        splash.setProgress(60, "Creating GUI : Menus");
        menu_list.setText("List");
        menu_list.setMnemonic('L');
        menu_list.add(act_list_toggle).addMouseListener(bar_status);
        menu_list.addSeparator();
        menu_list.add(act_edit_blur1).addMouseListener(bar_status);
        menu_list.add(act_edit_blur5).addMouseListener(bar_status);
        menu_list.addSeparator();
        menu_list.add(act_list_delete).addMouseListener(bar_status);

        splash.setProgress(62, "Creating GUI : Menus");
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

        splash.setProgress(65, "Creating GUI : Menus");
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

        splash.setProgress(67, "Creating GUI : Toolbars");
        bar_menu.add(menu_file);
        bar_menu.add(menu_edit);
        bar_menu.add(menu_view);
        bar_menu.add(menu_list);
        bar_menu.add(menu_tools);
        bar_menu.add(menu_help);

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

        splash.setProgress(70, "Creating GUI : Actions");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev) { act_file_exit.actionPerformed(null); }
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
        for (int i=0; i<menubar.getMenuCount(); i++)
        {
            JMenu menu = menubar.getMenu(i);
            for (int j=0; j<menu.getItemCount(); j++)
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
     * Setup the current view
     */
    public void setViewLayout(ViewLayout newLayout)
    {
        if (newLayout == layout)
            return;

        if (layout != null)
            layout.postDisplay();
        newLayout.preDisplay();

        layout = newLayout;
    }

    /**
     * For creating a new window
     */
    public class DebugAction extends EirAbstractAction
    {
        public DebugAction()
        {
            super("Debug",
                  null,
                  null,
                  "Debug", "Debug",
                  'G', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            debug();
        }
    }

    /**
     * Some debug action that we can configure
     */
    protected void debug()
    {
        System.out.println("\nViews:");
        int i = 0;
        Iterator it = views.iterator();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            System.out.println(""+(i++)+": "+view.getSavedName()+" "+view.toString());
        }
    }

    /**
     * For creating a new window
     */
    public class FileNewAction extends EirAbstractAction
    {
        public FileNewAction()
        {
            super("New Window",
                  "/toolbarButtonGraphics/general/New16.gif",
                  "/toolbarButtonGraphics/general/New24.gif",
                  "New Window", "Open a new Bible View window",
                  'N', KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            BibleViewPane view = new BibleViewPane();
            view.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

            if (!layout.add(view))
            {
                JOptionPane.showMessageDialog(Tools.this, "You can't add windows in this view.\nYou must switch to MDI or TDI to view multiple passages.");
                return;
            }

            views.add(view);
        }
    }

    /**
     * open a new passage window from a file
     */
    public class FileOpenAction extends EirAbstractAction
    {
        public FileOpenAction()
        {
            super("Open ...",
                  "/toolbarButtonGraphics/general/Open16.gif",
                  "/toolbarButtonGraphics/general/Open24.gif",
                  "Open Passage", "Open a saved passage.",
                  'O', KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            if (layout == VIEW_SDI)
            {
                JOptionPane.showMessageDialog(Tools.this, "You must switch to MDI or TDI to open a passage window.");
                return;
            }

            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * save the current passage window
     */
    public class FileSaveAction extends EirAbstractAction
    {
        public FileSaveAction()
        {
            super("Save ...",
                  "/toolbarButtonGraphics/general/Save16.gif",
                  "/toolbarButtonGraphics/general/Save24.gif",
                  "Save Passage", "Save the current passage.",
                  'S', KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * save the current passage window under a new name
     */
    public class FileSaveAsAction extends EirAbstractAction
    {
        public FileSaveAsAction()
        {
            super("Save As ...",
                  "/toolbarButtonGraphics/general/SaveAs16.gif",
                  "/toolbarButtonGraphics/general/SaveAs24.gif",
                  "Save Passage As", "Save the current passage under a different name.",
                  'A', KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * save all the passage windows
     */
    public class FileSaveAllAction extends EirAbstractAction
    {
        public FileSaveAllAction()
        {
            super("Save All",
                  "/toolbarButtonGraphics/general/SaveAll16.gif",
                  "/toolbarButtonGraphics/general/SaveAll24.gif",
                  "Save All Passages", "Save all the passages.",
                  'A', KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * close the current passage window
     */
    public class FileCloseAction extends EirAbstractAction
    {
        public FileCloseAction()
        {
            super("Close",
                  null,
                  null,
                  "Close Passages", "Close the current passage.",
                  'C', KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            BibleViewPane view = layout.getSelected();
            if (!layout.remove(view))
            {
                JOptionPane.showMessageDialog(Tools.this, "You can't remove a passage in this view.\nYou must switch to MDI or TDI to close a passage window.");
            }
            else
            {
                views.remove(view);

                // Warning: this is also done in MDIView when a user closes an
                // internal frame by clicking on the 'x'
            }
        }
    }

    /**
     * close all the passage windows
     */
    public class FileCloseAllAction extends EirAbstractAction
    {
        public FileCloseAllAction()
        {
            super("Close All",
                  null,
                  null,
                  "Close All Passages", "Close all the passages.",
                  'L', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            if (layout == VIEW_SDI)
            {
                JOptionPane.showMessageDialog(Tools.this, "You must switch to MDI or TDI to close a passage window.");
                return;
            }

            Iterator it = views.iterator();
            while (it.hasNext())
            {
                BibleViewPane view = (BibleViewPane) it.next();
                layout.remove(view);
            }
        }
    }

    /**
     * print the current passage window
     */
    public class FilePrintAction extends EirAbstractAction
    {
        public FilePrintAction()
        {
            super("Print ...",
                  "/toolbarButtonGraphics/general/Print16.gif",
                  "/toolbarButtonGraphics/general/Print24.gif",
                  "Print Passage", "Print the current passage.",
                  'P', KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * Action from clicking on the restart button. Forks and then Exits the VM.
     */
    public class RestartAction extends EirAbstractAction
    {
        public RestartAction()
        {
            super("Restart",
                  "/toolbarButtonGraphics/general/Refresh16.gif",
                  "/toolbarButtonGraphics/general/Refresh24.gif",
                  "Restart", "Restart the Tools application.",
                  'R', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            try
            {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("java dtools");

                System.exit(0);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Action from clicking on the exit button. Exits the VM.
     * Note the actionPerformed() method is called with a null ActionEvent
     * when the window is closing, so do not use it.
     */
    public class ExitAction extends EirAbstractAction
    {
        public ExitAction()
        {
            super("Exit",
                  null,
                  null,
                  "Exit", "Exit the Tools application.",
                  'X', KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            System.exit(0);
        }
    }

    /**
     * cut action
     */
    public class EditCutAction extends EirAbstractAction
    {
        public EditCutAction()
        {
            super("Cut",
                  "/toolbarButtonGraphics/general/Cut16.gif",
                  "/toolbarButtonGraphics/general/Cut24.gif",
                  "Cut", "Cut the selection.",
                  'U', KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_MASK, false));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * copy action
     */
    public class EditCopyAction extends EirAbstractAction
    {
        public EditCopyAction()
        {
            super("Copy",
                  "/toolbarButtonGraphics/general/Copy16.gif",
                  "/toolbarButtonGraphics/general/Copy24.gif",
                  "Copy", "Copy the selection.",
                  'C', KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK, false));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * paste action
     */
    public class EditPasteAction extends EirAbstractAction
    {
        public EditPasteAction()
        {
            super("Paste",
                  "/toolbarButtonGraphics/general/Paste16.gif",
                  "/toolbarButtonGraphics/general/Paste24.gif",
                  "Paste", "Paste the selection.",
                  'P', KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.ALT_MASK, false));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Not implemented");
        }
    }

    /**
     * View the SDI interface
     */
    public class ViewSDIAction extends EirAbstractAction
    {
        public ViewSDIAction()
        {
            super("View SDI",
                  null,
                  null,
                  "View SDI", "View using a single document",
                  'S', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            setViewLayout(VIEW_SDI);
        }
    }

    /**
     * View the TDI interface
     */
    public class ViewTDIAction extends EirAbstractAction
    {
        public ViewTDIAction()
        {
            super("View TDI",
                  null,
                  null,
                  "View TDI", "View using a tab layout",
                  'T', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            setViewLayout(VIEW_TDI);
        }
    }

    /**
     * View the MDI interface
     */
    public class ViewMDIAction extends EirAbstractAction
    {
        public ViewMDIAction()
        {
            super("View MDI",
                  null,
                  null,
                  "View MDI", "View using a window layout",
                  'M', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            setViewLayout(VIEW_MDI);
        }
    }

    /**
     * Toggle the status bar. Don't add this action to the toolbar because
     * the toggling will break.
     */
    public class ViewStatusBarAction extends EirAbstractAction
    {
        public ViewStatusBarAction()
        {
            super("Status Bar",
                  "/toolbarButtonGraphics/general/AlignBottom16.gif",
                  "/toolbarButtonGraphics/general/AlignBottom24.gif",
                  "Toggle Status Bar", "Toggle the display of the status bar.",
                  'A', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            if (view_status)
            {
                bar_status.setVisible(false);
                view_status = false;
            }
            else
            {
                bar_status.setVisible(true);
                view_status = true;
            }
        }
    }

    /**
     * Toggle the tool bar. Don't add this action to the toolbar because
     * the toggling will break.
     */
    public class ViewToolBarAction extends EirAbstractAction
    {
        public ViewToolBarAction()
        {
            super("Toolbar",
                  "/toolbarButtonGraphics/general/AlignTop16.gif",
                  "/toolbarButtonGraphics/general/AlignTop24.gif",
                  "Toggle Toolbar", "Toggle the display of the toolbar.",
                  'A', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            if (view_tool)
            {
                pnl_tbar.setVisible(false);
                view_tool = false;
            }
            else
            {
                pnl_tbar.setVisible(true);
                view_tool = true;
            }
        }
    }

    /**
     * Show hide the list
     */
    public class ListToggleAction extends EirAbstractAction
    {
        public ListToggleAction()
        {
            super("Toggle List",
                  "/toolbarButtonGraphics/text/AlignJustify16.gif",
                  "/toolbarButtonGraphics/text/AlignJustify24.gif",
                  "Toggles the passage list", "Toggles display of the passage list.",
                  'T', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            BibleViewPane view = layout.getSelected();
            if (view != null)
            {
                view.getPassagePane().toggleList();
            }
        }
    }

    /**
     * Show hide the list
     */
    public class ListDeleteAction extends EirAbstractAction
    {
        public ListDeleteAction()
        {
            super("Delete Selected",
                  "/toolbarButtonGraphics/general/Remove16.gif",
                  "/toolbarButtonGraphics/general/Remove24.gif",
                  "Delete selected verses", "Deleted the selected verses in the current verse list.",
                  'D', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            BibleViewPane view = layout.getSelected();
            if (view != null)
            {
                PassagePane ppane = view.getPassagePane();
                if (ppane.isListVisible())
                {
                    PassageList plist = ppane.getPassageList();
                    plist.deleteSelected();

                    // Update the text box
                    Passage ref = plist.getPassage();
                    SelectPane psel = view.getSelectPane();
                    psel.setPassage(ref);
                }
            }
        }
    }

    /**
     * Blur the current passage action
     */
    public class BlurAction extends EirAbstractAction
    {
        public BlurAction(int amount, int restrict)
        {
            super("Blur by "+amount+" verse",
                  null,
                  null,
                  "Blur passage by "+amount+" verse", "Blur the current passage by "+amount+" verse.",
                  '0'+(char)amount, null);

            this.amount = amount;
            this.restrict = restrict;
        }
        public void actionPerformed(ActionEvent ev)
        {
            BibleViewPane view = layout.getSelected();
            if (view != null)
            {
                Passage ref = view.getPassage();
                ref.blur(amount, restrict);
                view.setPassage(ref);
            }
        }
        private int amount;
        private int restrict;
    }

    /**
     * benchmark
     *
    public class BenchmarkAction extends EirAbstractAction
    {
        public BenchmarkAction()
        {
            super("Benchmark ...",
                  "/toolbarButtonGraphics/media/Movie16.gif",
                  "/toolbarButtonGraphics/media/Movie24.gif",
                  "Becnhmark", "Run a benchmark test.",
                  'B', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_bench.showInDialog(Tools.this);
        }
    }
    */

    /**
     * The version generation tool
     */
    public class GenerateAction extends EirAbstractAction
    {
        public GenerateAction()
        {
            super("Generate ...",
                  "/toolbarButtonGraphics/development/BeanAdd16.gif",
                  "/toolbarButtonGraphics/development/BeanAdd24.gif",
                  "Generate", "Generate a new version file set.",
                  'G', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_maint.showInDialog(Tools.this);
        }
    }

    /**
     * Show the unit test dialog
     *
    public class TestAction extends EirAbstractAction
    {
        public TestAction()
        {
            super("Unit Test ...",
                  "/toolbarButtonGraphics/development/Host16.gif",
                  "/toolbarButtonGraphics/development/Host24.gif",
                  "Unit Test", "Run a set of unit tests.",
                  'U', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_tester.showInDialog(Tools.this);
        }
    }
    */

    /**
     * Show the script pane
     *
    public class ScriptAction extends EirAbstractAction
    {
        public ScriptAction()
        {
            super("Scripting ...",
                  "/toolbarButtonGraphics/development/Applet16.gif",
                  "/toolbarButtonGraphics/development/Applet24.gif",
                  "Scripting", "Run some commands in a scripting language.",
                  'S', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_script.showInDialog(Tools.this);
        }
    }
    */

    /**
     * The Bible diff tool
     */
    public class DiffAction extends EirAbstractAction
    {
        public DiffAction()
        {
            super("Version Compare ...",
                  "/toolbarButtonGraphics/media/Pause16.gif",
                  "/toolbarButtonGraphics/media/Pause24.gif",
                  "Compare Versions", "Compare text produced by different drivers.",
                  'S', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_comp.showInDialog(Tools.this);
        }
    }

    /**
     * Action from clicking on the options button. Opens a config dialog.
     */
    public class OptionsAction extends EirAbstractAction
    {
        public OptionsAction()
        {
            super("Options ...",
                  "/toolbarButtonGraphics/general/Properties16.gif",
                  "/toolbarButtonGraphics/general/Properties24.gif",
                  "Options", "Alter system settings.",
                  'O', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            try
            {
                // SwingConfig.setDisplayClass(TreeConfigPane.class);
                URL config_url = Project.resource().getPropertiesURL("Tools");
                SwingConfig.showDialog(config, Tools.this, config_url);
            }
            catch (Exception ex)
            {
                Reporter.informUser(Tools.this, ex);
            }
        }
    }

    /**
     * For opening a help file
     */
    public class HelpContentsAction extends EirAbstractAction
    {
        public HelpContentsAction()
        {
            super("Contents ...",
                  "/toolbarButtonGraphics/general/Help16.gif",
                  "/toolbarButtonGraphics/general/Help24.gif",
                  "Help", "Help file contents.",
                  'C', KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false));
        }
        public void actionPerformed(ActionEvent ev)
        {
            JOptionPane.showMessageDialog(Tools.this, "Um. Help, yes that would require me to write some.\nErrr. Sorry.");
        }
    }

    /**
     * Show the system information dialog
     */
    public class SysInfoAction extends EirAbstractAction
    {
        public SysInfoAction()
        {
            super("System Information ...",
                  "/toolbarButtonGraphics/general/Information16.gif",
                  "/toolbarButtonGraphics/general/Information24.gif",
                  "System Information", "Display system configuration information.",
                  'I', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_props.showInDialog(Tools.this);
        }
    }

    /**
     * Show the error log window
     */
    public class ErrorLogAction extends EirAbstractAction
    {
        public ErrorLogAction()
        {
            super("Problem History ...",
                  "/toolbarButtonGraphics/general/History16.gif",
                  "/toolbarButtonGraphics/general/History24.gif",
                  "Problem History", "Display list of captured problems.",
                  'P', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            pnl_log.showInDialog(Tools.this);
        }
    }

    /**
     * Show the error log window
     */
    public class AboutAction extends EirAbstractAction
    {
        public AboutAction()
        {
            super("About ...",
                  "/toolbarButtonGraphics/general/About16.gif",
                  "/toolbarButtonGraphics/general/About24.gif",
                  "About this program", "Display details about this program.",
                  'A', null);
        }
        public void actionPerformed(ActionEvent ev)
        {
            Splash splash = new Splash(Tools.this, 60000);
            splash.setProgress(100, "");
        }
    }

    /**
     * Abstract manager of how we layout views
     */
    public abstract class ViewLayout
    {
        /**
         * Prepare any data structures needed before we are made live
         */
        public abstract void preDisplay();

        /**
         * Undo any data structures needed for live
         */
        public abstract void postDisplay();

        /**
         * Add a view to the set while visible
         */
        public abstract boolean add(BibleViewPane view);

        /**
         * Remove a view from the set while visible
         */
        public abstract boolean remove(BibleViewPane view);

        /**
         * While visible, which is the current pane
         */
        public abstract BibleViewPane getSelected();
    }

    /**
     * SDI manager of how we layout views
     */
    public class SDIViewLayout extends ViewLayout
    {
        /**
         * Prepare any data structures needed before we are made live
         */
        public void preDisplay()
        {
            // If there are no views in the pool, create one
            if (views.isEmpty())
            {
                BibleViewPane view = new BibleViewPane();
                view.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

                views.add(view);
            }

            Tools.this.getContentPane().add(getSelected(), BorderLayout.CENTER);
            Tools.this.getContentPane().repaint();

            act_file_close.setEnabled(false);
            act_file_closeall.setEnabled(false);
        }

        /**
         * Undo any data structures needed for live
         */
        public void postDisplay()
        {
            Tools.this.getContentPane().remove(getSelected());

            act_file_close.setEnabled(true);
            act_file_closeall.setEnabled(true);
        }

        /**
         * Add a view to the set while visible
         */
        public boolean add(BibleViewPane view)
        {
            return false;
        }

        /**
         * Remove a view from the set while visible
         */
        public boolean remove(BibleViewPane view)
        {
            return false;
        }

        /**
         * While visible, which is the current pane
         */
        public BibleViewPane getSelected()
        {
            return (BibleViewPane) views.get(0);
        }
    }

    /**
     * TDI manager of how we layout views
     */
    public class TDIViewLayout extends ViewLayout
    {
        /**
         * Prepare any data structures needed before we are made live
         */
        public void preDisplay()
        {
            if (tab_main == null)
            {
                tab_main = new JTabbedPane();
                tab_main.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
            }

            // Setup
            Iterator it = views.iterator();
            while (it.hasNext())
            {
                BibleViewPane view = (BibleViewPane) it.next();
                add(view);
            }

            // ensure we have been registered
            Tools.this.getContentPane().add(tab_main, BorderLayout.CENTER);
            Tools.this.getContentPane().repaint();

            // TODO: Work out if this is a bug in swing or should I really
            // be doing this to make the first tab be painted ...
            if (getSelected() != null)
                getSelected().setVisible(true);
        }

        /**
         * Undo any data structures needed for live
         */
        public void postDisplay()
        {
            tab_main.removeAll();
            Tools.this.getContentPane().remove(tab_main);
        }

        /**
         * Add a view to the set while visible
         */
        public boolean add(BibleViewPane view)
        {
            String name = view.getSavedName();
            tab_main.add(view, name);

            return true;
        }

        /**
         * Remove a view from the set while visible
         */
        public boolean remove(BibleViewPane view)
        {
            tab_main.remove(view);

            return true;
        }

        /**
         * While visible, which is the current pane
         */
        public BibleViewPane getSelected()
        {
            return (BibleViewPane) tab_main.getSelectedComponent();
        }

        private JTabbedPane tab_main;
    }

    /**
     * MDI manager of how we layout views
     */
    public class MDIViewLayout extends ViewLayout
    {
        /**
         * Prepare any data structures needed before we are made live
         */
        public void preDisplay()
        {
            // setup
            Iterator it = views.iterator();
            while (it.hasNext())
            {
                BibleViewPane view = (BibleViewPane) it.next();
                add(view);
            }

            // ensure we have been registered
            Tools.this.getContentPane().add(mdi_main, BorderLayout.CENTER);
            Tools.this.getContentPane().repaint();
        }

        /**
         * Undo any data structures needed for live
         */
        public void postDisplay()
        {
            // remove the old frames
            JInternalFrame[] frames = mdi_main.getAllFrames();
            for (int i=0; i<frames.length;i++)
            {
                mdi_main.remove(frames[i]);
            }

            Tools.this.getContentPane().remove(mdi_main);
        }

        /**
         * Add a view to the set while visible
         */
        public boolean add(BibleViewPane view)
        {
            String name = view.getSavedName();

            JInternalFrame iframe = new JInternalFrame(name, true, true, true, true);
            iframe.getContentPane().add(view);

            mdi_main.add(iframe/*, JLayeredPane.PALETTE_LAYER*/);

            //iframe.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
            iframe.addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosed(InternalFrameEvent ev)
                {
                    JInternalFrame iframe = ev.getInternalFrame();
                    views.remove(iframe.getContentPane().getComponent(0));
                }
            });
            iframe.setVisible(true);
            iframe.pack();

            return true;
        }

        /**
         * Remove a view from the set while visible
         */
        public boolean remove(BibleViewPane view)
        {
            JInternalFrame iframe = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, view);
            iframe.dispose();

            return true;
        }

        /**
         * While visible, which is the current pane
         */
        public BibleViewPane getSelected()
        {
            JInternalFrame frame = mdi_main.getSelectedFrame();

            if (frame == null)
                return null;

            Component comp = frame.getContentPane().getComponent(0);
            return (BibleViewPane) comp;
        }

        private JDesktopPane mdi_main = new JDesktopPane();
    }

    protected final ViewLayout VIEW_SDI = new SDIViewLayout();
    protected final ViewLayout VIEW_MDI = new MDIViewLayout();
    protected final ViewLayout VIEW_TDI = new TDIViewLayout();

    /** The current way the views are layed out */
    protected ViewLayout layout = null;

    /** The list of BibleViewPanes being viewed in tdi and mdi workspaces */
    protected List views = new ArrayList();

    /** The version generation tool */
    protected MaintenancePane pnl_maint = null;

    /** The benchmarking tool */
    //protected BenchmarkPane pnl_bench = null;

    /** The Bible comparison tool */
    protected ComparePane pnl_comp = null;

    /** The test tool */
    //protected TesterPane pnl_tester = null;

    /** The scripting interface to BSF */
    //protected ScriptPane pnl_script = null;

    /** The properties pane */
    protected LogPane pnl_log = new LogPane();

    /** The properties pane */
    protected SystemPropertiesPane pnl_props = null;

    /** is the status bar visible */
    protected boolean view_status = true;

    /** is the toolbar visible */
    protected boolean view_tool = true;

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.view");

    protected Config config = null;

    protected Action act_file_new = new FileNewAction();
    protected Action act_file_open = new FileOpenAction();
    protected Action act_file_save = new FileSaveAction();
    protected Action act_file_saveas = new FileSaveAsAction();
    protected Action act_file_saveall = new FileSaveAllAction();
    protected Action act_file_close = new FileCloseAction();
    protected Action act_file_closeall = new FileCloseAllAction();
    protected Action act_file_print = new FilePrintAction();
    protected Action act_file_restart = new RestartAction();
    protected Action act_file_exit = new ExitAction();

    protected Action act_edit_cut = new EditCutAction();
    protected Action act_edit_copy = new EditCopyAction();
    protected Action act_edit_paste = new EditPasteAction();
    protected Action act_edit_blur1 = new BlurAction(1, Passage.RESTRICT_CHAPTER);
    protected Action act_edit_blur5 = new BlurAction(5, Passage.RESTRICT_CHAPTER);

    protected Action act_view_sdi = new ViewSDIAction();
    protected Action act_view_tdi = new ViewTDIAction();
    protected Action act_view_mdi = new ViewMDIAction();
    protected Action act_view_tbar = new ViewToolBarAction();
    protected Action act_view_sbar = new ViewStatusBarAction();

    protected Action act_list_toggle = new ListToggleAction();
    protected Action act_list_delete = new ListDeleteAction();

    //protected Action act_tools_bench = new BenchmarkAction();
    protected Action act_tools_generate = new GenerateAction();
    //protected Action act_tools_script = new ScriptAction();
    //protected Action act_tools_test = new TestAction();
    protected Action act_tools_diff = new DiffAction();
    protected Action act_tools_options = new OptionsAction();

    protected Action act_help_contents = new HelpContentsAction();
    protected Action act_help_system = new SysInfoAction();
    protected Action act_help_about = new AboutAction();
    protected Action act_help_log = new ErrorLogAction();
    protected Action act_help_debug = new DebugAction();

    protected JRadioButtonMenuItem rdo_view_tdi = new JRadioButtonMenuItem(act_view_tdi);
    protected JRadioButtonMenuItem rdo_view_mdi = new JRadioButtonMenuItem(act_view_mdi);
    protected JRadioButtonMenuItem rdo_view_sdi = new JRadioButtonMenuItem(act_view_sdi);
    protected JCheckBoxMenuItem chk_view_sbar = new JCheckBoxMenuItem(act_view_sbar);
    protected JCheckBoxMenuItem chk_view_tbar = new JCheckBoxMenuItem(act_view_tbar);

    private JMenuBar bar_menu = new JMenuBar();
    private JMenu menu_file = new JMenu();
    private JMenu menu_edit = new JMenu();
    private JMenu menu_view = new JMenu();
    private JMenu menu_list = new JMenu();
    private JMenu menu_tools = new JMenu();
    private JMenu menu_help = new JMenu();

    private ButtonGroup grp_views = new ButtonGroup();

    // private JPanel pnl_main = new JPanel();
    // private CardLayout lay_main = new CardLayout();
    protected JToolBar pnl_tbar = new JToolBar();
    protected StatusBar bar_status = new StatusBar();
    protected Splash splash = null;
}
