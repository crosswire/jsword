package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.swing.BackportUtil;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.SidebarPane;
import org.crosswire.jsword.view.swing.book.SitesPane;
import org.crosswire.jsword.view.swing.book.TitleChangedEvent;
import org.crosswire.jsword.view.swing.book.TitleChangedListener;
import org.crosswire.jsword.view.swing.display.FocusablePart;
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
public class Desktop implements TitleChangedListener, HyperlinkListener
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
            desktop.getJFrame().pack();
            GuiUtil.centerWindow(desktop.getJFrame());
            desktop.getJFrame().toFront();
            desktop.getJFrame().setVisible(true);

            log.debug("desktop main exiting.");
        }
        catch (Exception ex)
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
    public Desktop() throws IOException, JDOMException
    {
        LookAndFeelUtil.tweakLookAndFeel();
        Reporter.grabAWTExecptions(true);

        URL predicturl = Project.instance().getWritablePropertiesURL("splash");
        Splash splash = new Splash(frame, 60000);
        startJob = JobManager.createJob("Startup", predicturl, true);
        splash.pack();

        // Initial setup
        frame = new JFrame();

        startJob.setProgress("Setting-up config");
        actToolsOptions = new OptionsAction(this);

        startJob.setProgress("Loading Configuration System");
        actToolsOptions.createConfig();

        startJob.setProgress("Loading Stored Settings");
        actToolsOptions.loadConfig();

        startJob.setProgress("Generating Components");
        createComponents();

        // GUI setup
        debug();
        init();

        accelerateMenu(barMenu);

        if (initial == LAYOUT_TYPE_MDI)
        {
            rdoViewMdi.setSelected(true);
        }
        if (initial == LAYOUT_TYPE_TDI)
        {
            rdoViewTdi.setSelected(true);
        }

        // Sort out the current ViewLayout. We need to reset current to be
        // initial because the config system may well have changed initial
        current = initial;
        ensureAvailableBibleViewPane();

        // Configuration
        startJob.setProgress("General configuration");
        // NOTE: when we tried dynamic laf update, frame needed special treatment
        //LookAndFeelUtil.addComponentToUpdate(frame);

        // Keep track of the selected FocusablePart
        FocusManager.getCurrentManager().addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent ev)
            {
                FocusablePart da = recurseDisplayArea();
                if (da != null)
                {
                    last = da;
                }
            }
        });

        // And setup the initial display area, by getting the first
        // BibleViewPane and asking it for a PassagePane.
        // According to the iterator contract hasNext has to be called before next
        Iterator iter = iterateBibleViewPanes();
        iter.hasNext();
        last = ((BibleViewPane) iter.next()).getPassagePane();

        // Preload the PassageInnerPane for faster initial view
        Desktop.preload();

        startJob.done();
        splash.close();

        frame.pack();
    }

    /**
     * Sometimes we need to make some changes to debug the GUI.
     */
    private void debug()
    {
        //this.getContentPane().addContainerListener(new DebugContainerListener());

        //javax.swing.RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        //((javax.swing.JComponent) getContentPane()).setDebugGraphicsOptions(javax.swing.DebugGraphics.LOG_OPTION);
    }

    /**
     * Call all the constructors
     */
    private void createComponents()
    {
        layouts = new ViewLayout[2];
        layouts[LAYOUT_TYPE_TDI] = new TDIViewLayout();
        layouts[LAYOUT_TYPE_MDI] = new MDIViewLayout();
        
        actFileNew = new FileNewAction(this);
        actFileOpen = new FileOpenAction(this);
        actFileSave = new FileSaveAction(this);
        actFileSaveAs = new FileSaveAsAction(this);
        actFileSaveAll = new FileSaveAllAction(this);
        actFileClose = new FileCloseAction(this);
        actFileCloseAll = new FileCloseAllAction(this);
        //actFilePrint = new FilePrintAction(this);
        actFileExit = new ExitAction(this);
        
        actEditCut = new EditCutAction(this);
        actEditCopy = new EditCopyAction(this);
        actEditPaste = new EditPasteAction(this);
        actEditBlur1 = new BlurAction(this, 1, PassageConstants.RESTRICT_CHAPTER);
        actEditBlur5 = new BlurAction(this, 5, PassageConstants.RESTRICT_CHAPTER);
        
        actViewTdi = new ViewTDIAction(this);
        actViewMdi = new ViewMDIAction(this);
        //actViewTbar = new ViewToolBarAction(this);
        actViewGhtml = new ViewSourceGHTMLAction(this);
        actViewVhtml = new ViewSourceHTMLAction(this);
        actViewOsis = new ViewSourceOSISAction(this);

        actListDelete = new ListDeleteAction(this);

        //actToolsGenerate = GeneratorPane.createOpenAction(this);
        //actToolsDiff = ComparePane.createOpenAction(this);
        actToolsSites = SitesPane.createOpenAction(frame);

        actHelpContents = new HelpContentsAction(this);
        actHelpAbout = AboutPane.createOpenAction(this);

        rdoViewTdi = new JRadioButtonMenuItem(actViewTdi);
        rdoViewMdi = new JRadioButtonMenuItem(actViewMdi);
        //chkViewTbar = new JCheckBoxMenuItem(actViewTbar);

        barMenu = new JMenuBar();
        menuFile = new JMenu();
        menuEdit = new JMenu();
        menuView = new JMenu();
        menuTools = new JMenu();
        menuHelp = new JMenu();

        grpViews = new ButtonGroup();
        pnlTbar = new JToolBar();
        barStatus = new StatusBar();
        barSide = new SidebarPane();
        //barBook = new ReferencedPane();
        sptBooks = new JSplitPane();
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void init()
    {
        menuFile.setText("File");
        menuFile.setMnemonic('F');
        menuFile.add(actFileNew).addMouseListener(barStatus);
        menuFile.add(actFileOpen).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actFileClose).addMouseListener(barStatus);
        menuFile.add(actFileCloseAll).addMouseListener(barStatus);
        menuFile.addSeparator();
        //menu_file.add(actFilePrint).addMouseListener(bar_status);
        //menu_file.addSeparator();
        menuFile.add(actFileSave).addMouseListener(barStatus);
        menuFile.add(actFileSaveAs).addMouseListener(barStatus);
        menuFile.add(actFileSaveAll).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actFileExit).addMouseListener(barStatus);

        menuEdit.setText("Edit");
        menuEdit.setMnemonic('E');
        menuEdit.add(actEditCut).addMouseListener(barStatus);
        menuEdit.add(actEditCopy).addMouseListener(barStatus);
        menuEdit.add(actEditPaste).addMouseListener(barStatus);

        rdoViewTdi.addMouseListener(barStatus);
        rdoViewMdi.addMouseListener(barStatus);
        //chkViewTbar.addMouseListener(bar_status);
        //chkViewTbar.setSelected(view_tool);
        grpViews.add(rdoViewMdi);
        grpViews.add(rdoViewTdi);

        menuView.setText("View");
        menuView.setMnemonic('V');
        menuView.add(rdoViewTdi);
        menuView.add(rdoViewMdi);
        //menu_view.add(chkViewTbar);
        menuView.addSeparator();
        menuView.add(actViewGhtml).addMouseListener(barStatus);
        menuView.add(actViewVhtml).addMouseListener(barStatus);
        menuView.add(actViewOsis).addMouseListener(barStatus);

        menuTools.setText("Tools");
        menuTools.setMnemonic('T');
        menuTools.add(actEditBlur1).addMouseListener(barStatus);
        menuTools.add(actEditBlur5).addMouseListener(barStatus);
        menuTools.add(actListDelete).addMouseListener(barStatus);
        menuTools.addSeparator();
        //menu_tools.add(actToolsGenerate).addMouseListener(bar_status);
        //menu_tools.add(actToolsDiff).addMouseListener(bar_status);
        //menu_tools.addSeparator();
        menuTools.add(actToolsSites).addMouseListener(barStatus);
        menuTools.add(actToolsOptions).addMouseListener(barStatus);

        menuHelp.setText("Help");
        menuHelp.setMnemonic('H');
        menuHelp.add(actHelpContents).addMouseListener(barStatus);
        menuHelp.addSeparator();
        menuHelp.add(actHelpAbout).addMouseListener(barStatus);

        barMenu.add(menuFile);
        barMenu.add(menuEdit);
        barMenu.add(menuView);
        barMenu.add(menuTools);
        barMenu.add(menuTools);
        barMenu.add(menuHelp);

        BackportUtil.setRollover(pnlTbar, true);

        pnlTbar.add(actFileNew).addMouseListener(barStatus);
        pnlTbar.add(actFileOpen).addMouseListener(barStatus);
        pnlTbar.add(actFileSave).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        pnlTbar.add(actEditCut).addMouseListener(barStatus);
        pnlTbar.add(actEditCopy).addMouseListener(barStatus);
        pnlTbar.add(actEditPaste).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        //pnl_tbar.add(actToolsGenerate).addMouseListener(bar_status);
        //pnl_tbar.add(actToolsDiff).addMouseListener(bar_status);
        //pnl_tbar.addSeparator();
        pnlTbar.add(actHelpContents).addMouseListener(barStatus);
        pnlTbar.add(actHelpAbout).addMouseListener(barStatus);

        //barBook.addHyperlinkListener(this);
        barSide.addHyperlinkListener(this);

        sptBooks.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptBooks.setOneTouchExpandable(true);
        sptBooks.setDividerLocation(0.9D);
        //spt_books.add(barBook, JSplitPane.RIGHT);
        sptBooks.add(barSide, JSplitPane.RIGHT);
        sptBooks.add(new JPanel(), JSplitPane.LEFT);
        sptBooks.setResizeWeight(0.9D);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                actFileExit.actionPerformed(null);
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pnlTbar, BorderLayout.NORTH);
        frame.getContentPane().add(barStatus, BorderLayout.SOUTH);
        frame.getContentPane().add(sptBooks, BorderLayout.CENTER);
        frame.setJMenuBar(barMenu);

        frame.setEnabled(true);
        frame.setTitle(Project.instance().getName());
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
     * Iterate through a copied list of views
     */
    public Iterator iterateBibleViewPanes()
    {
        Collection copy = new ArrayList(views);
        return copy.iterator();
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
     * Find the currently highlighted FocusablePart
     */
    public FocusablePart getDisplayArea()
    {
        FocusablePart da = recurseDisplayArea();
        if (da != null)
        {
            return da;
        }

        return last;
    }

    /**
     * Get the currently selected component and the walk up the component tree
     * trying to find a component that implements FocusablePart
     */
    protected FocusablePart recurseDisplayArea()
    {
        Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

        // So we've got the current component, we now need to walk up the tree
        // to find something that we recognize.
        while (comp != null)
        {
            if (comp instanceof FocusablePart)
            {
                return (FocusablePart) comp;
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
        Component leftcurr = sptBooks.getLeftComponent();
        if (leftcurr == next)
        {
            return;
        }

        /*
        if (leftcurr != null)
        {
            // Not sure why we have to use a number in place of
            // the JSplitPane.LEFT string constant.
            // And not sure that we need to do this at all.
            //spt_books.remove(1);
        }
        */

        sptBooks.add(next, JSplitPane.LEFT);
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
        if (initial != LAYOUT_TYPE_TDI && initial != LAYOUT_TYPE_MDI)
        {
            throw new IllegalArgumentException();
        }

        Desktop.initial = initial;
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        try
        {
            barStatus.hyperlinkUpdate(ev);

            HyperlinkEvent.EventType type = ev.getEventType();
            JTextPane pane = (JTextPane) ev.getSource();

            if (type == HyperlinkEvent.EventType.ACTIVATED)
            {
                String url = ev.getDescription();
                if (url.indexOf(':') == -1)
                {
                    // So there is no protocol, this must be relative to the current
                    // in which case we assume that it is an in page reference.
                    // We ignore the frame case (example code within JEditorPane
                    // JavaDoc).
                    if (url.startsWith("#"))
                    {
                        url = url.substring(1);
                    }
                    log.debug("scrolling to: "+url);
                    BackportUtil.scrollToReference(url, pane);
                }
                else
                {
                    // Fully formed, so we open a new window
                    openHyperlink(ev.getDescription());
                }
            }
            else
            {
                // Must be either an enter or an exit event
                // simulate a link rollover effect, a CSS style not supported in JDK 1.4
                Element textElement = ev.getSourceElement();

                // Focus is needed to decorate Enter and Leave events
                pane.grabFocus();

                int start = textElement.getStartOffset();
                int length = textElement.getEndOffset() - start;

                Style style = pane.addStyle("HyperLink", null);
                StyleConstants.setUnderline(style, type == HyperlinkEvent.EventType.ENTERED);
                StyledDocument doc = pane.getStyledDocument();
                doc.setCharacterAttributes(start, length, style, false);
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
        if (data.startsWith("//"))
        {
            data = data.substring(2);
        }

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
                barSide.getCommentaryPane().setPassage(ref);
            }
            catch (NoSuchVerseException ex)
            {
                Reporter.informUser(this, ex);
            }
        }
        else if (protocol.equals("dict"))
        {
            barSide.getDictionaryPane().setWord(data);
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
        return viewStatus;
    }

    /**
     * Sets the view_status.
     * @param view_status The view_status to set
     */
    public void setStatusBarVisible(boolean view_status)
    {
        barStatus.setVisible(true);
        this.viewStatus = view_status;
    }

    /**
     * Returns the view_tool.
     * @return boolean
     */
    public boolean isToolbarVisible()
    {
        return viewTool;
    }

    /**
     * Sets the view_tool.
     * @param view_tool The view_tool to set
     */
    public void setToolbarVisible(boolean view_tool)
    {
        pnlTbar.setVisible(true);
        this.viewTool = view_tool;
    }

    /**
     * Are the close buttons enabled?
     * @param enabled The enabled state
     */
    public void setCloseEnabled(boolean enabled)
    {
        actFileClose.setEnabled(enabled);
        actFileCloseAll.setEnabled(enabled);
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
     * Accessor for the main desktop Frame
     */
    public JFrame getJFrame()
    {
        return frame;
    }

    /**
     * Run down the menus adding the accelerators
     */
    private void accelerateMenu(JMenuBar menubar)
    {
        for (int i = 0; i < menubar.getMenuCount(); i++)
        {
            JMenu menu = menubar.getMenu(i);
            for (int j = 0; j < menu.getMenuComponentCount(); j++)
            {
                Component comp = menu.getMenuComponent(j);
                if (comp instanceof JMenuItem)
                {
                    JMenuItem item = (JMenuItem) comp;
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
                else
                {
                    // Just in case we start getting things we could do something with
                    if (!(comp instanceof JPopupMenu.Separator))
                    {
                        log.warn("Non JMenuItem, class="+comp.getClass().getName());
                    }
                }
            }
        }
    }

    /**
     * Makes the second invocation much faster
     */
    public static void preload()
    {
        final Thread worker = new Thread("DisplayPreLoader")
        {
            public void run()
            {
                URL predicturl = Project.instance().getWritablePropertiesURL("display");
                Job job = JobManager.createJob("Display Pre-load", predicturl, this, true);
    
                try
                {
                    job.setProgress("Setup");
                    List booklist = Books.installed().getBookMetaDatas();
                    if (booklist.size() == 0)
                    {
                        return;
                    }
    
                    Book test = ((BookMetaData) booklist.get(0)).getBook();
                    if (interrupted())
                    {
                        return;
                    }
    
                    job.setProgress("Getting initial data");
                    BookData data = test.getData(test.getGlobalKeyList().get(0));
                    if (interrupted())
                    {
                        return;
                    }
    
                    job.setProgress("Getting event provider");
                    SAXEventProvider provider = data.getSAXEventProvider();
                    if (interrupted())
                    {
                        return;
                    }
    
                    job.setProgress("Compiling stylesheet");
                    Converter converter = ConverterFactory.getConverter();
                    converter.convert(provider);
                    if (interrupted())
                    {
                        return;
                    }
                }
                catch (Exception ex)
                {
                    job.ignoreTimings();
                    log.error("View pre-load failed", ex);
                }
                finally
                {
                    job.done();
                    log.debug("View pre-load finished");
                }
            }
        };
    
        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
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
     * The current way the views are laid out
     */
    private int current = initial;

    /**
     * The list of BibleViewPanes being viewed in tdi and mdi workspaces
     */
    private List views = new ArrayList();

    /**
     * is the status bar visible
     */
    private boolean viewStatus = true;

    /**
     * is the toolbar visible
     */
    private boolean viewTool = true;

    /**
     * The last selected FocusablePart
     */
    protected FocusablePart last = null;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Desktop.class);

    /*
     * GUI components
     */
    private Job startJob = null;
    private Action actFileNew = null;
    private Action actFileOpen = null;
    private Action actFileSave = null;
    private Action actFileSaveAs = null;
    private Action actFileSaveAll = null;
    private Action actFileClose = null;
    private Action actFileCloseAll = null;
    //private Action actFilePrint = null;
    protected Action actFileExit = null;

    private Action actEditCut = null;
    private Action actEditCopy = null;
    private Action actEditPaste = null;
    private Action actEditBlur1 = null;
    private Action actEditBlur5 = null;

    private Action actViewTdi = null;
    private Action actViewMdi = null;
    //private Action actViewTbar = null;

    private Action actViewGhtml = null;
    private Action actViewVhtml = null;
    private Action actViewOsis = null;

    private Action actListDelete = null;

    //private Action actToolsGenerate = null;
    //private Action actToolsDiff = null;
    private OptionsAction actToolsOptions = null;
    private Action actToolsSites = null;

    private Action actHelpContents = null;
    private Action actHelpAbout = null;
    //private Action actHelpDebug = null;

    private JRadioButtonMenuItem rdoViewTdi = null;
    private JRadioButtonMenuItem rdoViewMdi = null;
    //private JCheckBoxMenuItem chkViewTbar = null;

    private JMenuBar barMenu = null;
    private JMenu menuFile = null;
    private JMenu menuEdit = null;
    private JMenu menuView = null;
    private JMenu menuTools = null;
    private JMenu menuHelp = null;

    private JFrame frame = null;
    private ButtonGroup grpViews = null;
    private JToolBar pnlTbar = null;
    private StatusBar barStatus = null;
    private SidebarPane barSide = null;
    //private ReferencedPane barBook = null;
    private JSplitPane sptBooks = null;
}
