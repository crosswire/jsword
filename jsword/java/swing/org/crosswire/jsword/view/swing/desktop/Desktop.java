
package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.swing.CustomAWTExceptionHandler;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.swing.SystemPropertiesPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.AdvancedToolsPane;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.DisplayArea;
import org.crosswire.jsword.view.swing.book.InnerDisplayPane;
import org.crosswire.jsword.view.swing.book.SidebarPane;
import org.crosswire.jsword.view.swing.book.Splash;
import org.crosswire.jsword.view.swing.book.StatusBar;
import org.crosswire.jsword.view.swing.book.TitleChangedEvent;
import org.crosswire.jsword.view.swing.book.TitleChangedListener;
import org.jdom.JDOMException;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class Desktop extends JFrame implements TitleChangedListener, HyperlinkListener
{
    /**
     * Central start point.
     * @param args The command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            Desktop desktop = new Desktop();
            desktop.pack();
            GuiUtil.centerWindow(desktop);
            desktop.toFront();
            desktop.setVisible(true);

            log.debug("desktop main exiting.");
        }
        catch (Throwable ex)
        {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            ex.printStackTrace();
            ExceptionPane.showExceptionDialog(null, ex);
        }
    }

    /**
     * Construct a Desktop.
     */
    public Desktop() throws BookException, IOException, JDOMException
    {
        URL predicturl = Project.resource().getWritablePropertiesURL("splash");
        Splash splash = new Splash(this, 60000);
        startjob = JobManager.createJob("Startup", predicturl, true);
        splash.pack();

        // Initial setup
        startjob.setProgress("Setting-up config");
        act_tools_options = new OptionsAction(this);
        CustomAWTExceptionHandler.setParentComponent(this);

        startjob.setProgress("Loading Configuration System");
        act_tools_options.createConfig();

        startjob.setProgress("Loading Stored Settings");
        act_tools_options.loadConfig();

        startjob.setProgress("Generating Components");
        createComponents();

        // GUI setup
        init();
        accelerateMenu(bar_menu);

        if (initial == LAYOUT_TYPE_MDI)
        {
            rdo_view_mdi.setSelected(true);
        }
        if (initial == LAYOUT_TYPE_TDI)
        {
            rdo_view_tdi.setSelected(true);
        }

        // Sort out the current ViewLayout. We need to reset current to be
        // initial because the config system may well have changed initial
        current = initial;
        ensureAvailableBibleViewPane();

        // Configuration
        startjob.setProgress("General configuration");
        LookAndFeelUtil.addComponentToUpdate(this);

        // Keep track of the selected DisplayArea
        FocusManager.getCurrentManager().addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent ev)
            {
                DisplayArea da = recurseDisplayArea();
                if (da != null)
                {
                    last = da;
                }
            }
        });
        last = recurseDisplayArea();

        // Preload the PassageInnerPane for faster initial view
        InnerDisplayPane.preload();

        startjob.done();
        splash.close();

        this.pack();
    }

    /**
     * Call all the constructors
     */
    private void createComponents()
    {
        layouts = new ViewLayout[2];
        layouts[LAYOUT_TYPE_TDI] = new TDIViewLayout();
        layouts[LAYOUT_TYPE_MDI] = new MDIViewLayout();
        
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
        act_edit_blur1 = new BlurAction(this, 1, PassageConstants.RESTRICT_CHAPTER);
        act_edit_blur5 = new BlurAction(this, 5, PassageConstants.RESTRICT_CHAPTER);
        
        act_view_tdi = new ViewTDIAction(this);
        act_view_mdi = new ViewMDIAction(this);
        act_view_tbar = new ViewToolBarAction(this);
        act_view_sbar = new ViewStatusBarAction(this);
        act_view_html = new ViewSourceHTMLAction(this);
        act_view_osis = new ViewSourceOSISAction(this);
        
        act_list_delete = new ListDeleteAction(this);
        
        //act_tools_generate = GeneratorPane.createOpenAction(this);
        //act_tools_diff = ComparePane.createOpenAction(this);
        
        act_help_contents = new HelpContentsAction(this);
        act_help_system = SystemPropertiesPane.createOpenAction(this);
        act_help_about = Splash.createOpenAction(this);
        act_help_log = AdvancedToolsPane.createOpenAction(this);
        act_help_debug = new DebugAction(this);
        
        rdo_view_tdi = new JRadioButtonMenuItem(act_view_tdi);
        rdo_view_mdi = new JRadioButtonMenuItem(act_view_mdi);
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
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void init()
    {
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
        chk_view_tbar.addMouseListener(bar_status);
        chk_view_sbar.addMouseListener(bar_status);
        chk_view_tbar.setSelected(view_status);
        chk_view_sbar.setSelected(view_tool);
        grp_views.add(rdo_view_mdi);
        grp_views.add(rdo_view_tdi);

        menu_view.setText("View");
        menu_view.setMnemonic('V');
        menu_view.add(rdo_view_tdi);
        menu_view.add(rdo_view_mdi);
        menu_view.addSeparator();
        menu_view.add(chk_view_tbar);
        menu_view.add(chk_view_sbar);
        menu_view.addSeparator();
        menu_view.add(act_view_html).addMouseListener(bar_status);
        menu_view.add(act_view_osis).addMouseListener(bar_status);

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

        bar_menu.add(menu_file);
        bar_menu.add(menu_edit);
        bar_menu.add(menu_view);
        bar_menu.add(menu_tools);
        bar_menu.add(menu_tools);
        bar_menu.add(menu_help);

        // JToolBar.setRollover(boolean) is not supported in JDK1.3, instead we use reflection
        // to find out whether the method is available, if so call it.
        try
        {
            Class cl = pnl_tbar.getClass();
            Method method = cl.getMethod("setRollover", new Class[] { Boolean.TYPE });
            method.invoke(pnl_tbar, new Object[] { Boolean.TRUE });
        }
        catch (NoSuchMethodException ex)
        {
            log.debug("Assume 1.3 JVM since: "+ex);
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

        bar_side.addHyperlinkListener(this);

        spt_books.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        spt_books.setOneTouchExpandable(true);
        spt_books.setDividerLocation(0.9D);
        spt_books.add(bar_side, JSplitPane.RIGHT);
        spt_books.add(new JPanel(), JSplitPane.LEFT);
        spt_books.setResizeWeight(0.9D);

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
    }

    /**
     * Adds BibleViewPane to the list in this Desktop.
     */
    public void addBibleViewPane(BibleViewPane view)
    {
        view.addTitleChangedListener(this);
        view.addHyperlinkListener(this);
        views.add(view);

        getViewLayout().add(view);

        setLayoutComponent(getViewLayout().getRootComponent());
        getViewLayout().getSelected().adjustFocus(); 
    }

    /**
     * Removes BibleViewPane from the list in this Desktop.
     */
    public void removeBibleViewPane(BibleViewPane view)
    {
        view.removeTitleChangedListener(this);
        view.removeHyperlinkListener(this);
        views.remove(view);

        getViewLayout().remove(view);

        // Just in case that was the last one
        ensureAvailableBibleViewPane();

        setLayoutComponent(getViewLayout().getRootComponent());
        //getViewLayout().getSelected().adjustFocus(); 
    }

    /**
     * Iterate through the list of views
     */
    public Iterator iterateBibleViewPanes()
    {
        return views.iterator();
    }

    /**
     * How many BibleViewPanes are there currently?
     */
    public int countBibleViewPanes()
    {
        return views.size();
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
     * Find the currently highlighted DisplayArea
     */
    public DisplayArea getDisplayArea()
    {
        DisplayArea da = recurseDisplayArea();
        if (da != null)
        {
            return da;
        }

        return last;
    }

    /**
     * Get the currently selected component and the walk up the component tree
     * trying to find a component that implements DisplayArea
     */
    protected DisplayArea recurseDisplayArea()
    {
        Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        
        // So we've got the current component, we now need to walk up the tree
        // to find something that we recognise.
        while (comp != null)
        {
            if (comp instanceof DisplayArea)
            {
                return (DisplayArea) comp;
            }
        
            comp = comp.getParent();
        }

        return null;
    }

    /**
     * What is the current layout?
     */
    private final ViewLayout getViewLayout()
    {
        return layouts[current];
    }

    /**
     * Setup the current view
     */
    public void setLayoutType(int next)
    {
        // Check this is a change
        if (current == next)
        {
            return;
        }

        // Go through the views removing them from the layout
        Iterator it = iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            getViewLayout().remove(view);
        }

        current = next;

        // Go through the views adding them to the layout SDIViewLayout may well add
        // a view, in which case the view needs to be set already so this must come
        // last.
        it = iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            getViewLayout().add(view);
        }

        // Allow the current BibleViewPane to set the focus in the right place
        setLayoutComponent(getViewLayout().getRootComponent());
        getViewLayout().getSelected().adjustFocus();
    }

    /**
     * For the use of the various Layout components to update the UI with
     * their Layout component.
     */
    private void setLayoutComponent(Component next)
    {
        Component leftcurr = spt_books.getLeftComponent();
        if (leftcurr == next)
        {
            return;
        }

        if (leftcurr != null)
        {
            // Not sure why we have to use a number in place of
            // the JSplitPane.LEFT string constant.
            
            // And not sure that we need to do this at all.
            //spt_books.remove(1/*JSplitPane.LEFT*/);
        }

        spt_books.add(next, JSplitPane.LEFT);
    }

    /**
     * If there are no current BibleViewPanes then add one in.
     * final because the ctor calls this method
     */
    private final void ensureAvailableBibleViewPane()
    {
        // If there are no views in the pool, create one
        if (!iterateBibleViewPanes().hasNext())
        {
            BibleViewPane view = new BibleViewPane();
            addBibleViewPane(view);
        }
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

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        try
        {
            bar_status.hyperlinkUpdate(ev);

            HyperlinkEvent.EventType type = ev.getEventType();
            if (type == HyperlinkEvent.EventType.ACTIVATED)
            {
                openHyperlink(ev.getDescription());
            }
        }
        catch (MalformedURLException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Create a new view showing the contents of the given hyperlink
     */
    public void openHyperlink(String url) throws MalformedURLException
    {
        int match = url.indexOf(":");
        if (match == -1)
        {
            throw new MalformedURLException("missing : in "+url);
        }

        String protocol = url.substring(0, match);
        String data = url.substring(match+1);

        if (protocol.equals("bible"))
        {
            try
            {
                Passage ref = PassageFactory.createPassage(data);
                BibleViewPane view = new BibleViewPane();

                addBibleViewPane(view);

                view.addHyperlinkListener(this);
                view.setPassage(ref);
            }
            catch (NoSuchVerseException ex)
            {
                Reporter.informUser(this, ex);
            }
        }
        else if (protocol.equals("comment"))
        {
            try
            {
                Passage ref = PassageFactory.createPassage(data);
    
                bar_side.getCommentaryPane().setPassage(ref);
            }
            catch (NoSuchVerseException ex)
            {
                Reporter.informUser(this, ex);
            }
        }
        else if (protocol.equals("dict"))
        {
            bar_side.getDictionaryPane().setWord(data);
        }
        else
        {
            throw new MalformedURLException("unknown protocol "+protocol);
        }
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
     * @param enabled The enabled state
     */
    public void setCloseEnabled(boolean enabled)
    {
        act_file_close.setEnabled(enabled);
        act_file_closeall.setEnabled(enabled);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.TitleChangedListener#titleChanged(org.crosswire.jsword.view.swing.book.TitleChangedEvent)
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
                if (item == null)
                {
                    log.warn("null item at "+j+" when getMenuCount()="+menu.getItemCount());
                    continue;
                }

                Action action = item.getAction();
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

    /**
     * Tabbed document interface
     */
    protected static final int LAYOUT_TYPE_TDI = 0;

    /**
     * Multiple document interface
     */
    protected static final int LAYOUT_TYPE_MDI = 1;

    /**
     * The initial layout state
     */
    private static int initial = LAYOUT_TYPE_TDI;

    /**
     * The array of valid layouts
     */
    protected ViewLayout[] layouts; 

    /**
     * The current way the views are layed out
     */
    private int current = initial;

    /**
     * The list of BibleViewPanes being viewed in tdi and mdi workspaces
     */
    private List views = new ArrayList();

    /**
     * is the status bar visible
     */
    private boolean view_status = true;

    /**
     * is the toolbar visible
     */
    private boolean view_tool = true;

    /**
     * The last selected DisplayArea
     */
    protected DisplayArea last = null;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Desktop.class);

    private Job startjob = null;
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

    private Action act_view_tdi = null;
    private Action act_view_mdi = null;
    private Action act_view_tbar = null;
    private Action act_view_sbar = null;

    private Action act_view_html = null;
    private Action act_view_osis = null;

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
