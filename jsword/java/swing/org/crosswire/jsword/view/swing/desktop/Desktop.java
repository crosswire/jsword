package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
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
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.readings.ReadingsBookDriver;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.SidebarPane;
import org.crosswire.jsword.view.swing.book.TitleChangedEvent;
import org.crosswire.jsword.view.swing.book.TitleChangedListener;
import org.crosswire.jsword.view.swing.display.FocusablePart;
import org.crosswire.jsword.view.swing.util.ConfigurableSwingConverter;
import org.crosswire.jsword.view.swing.util.SimpleSwingConverter;
import org.jdom.Document;
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
    // Strings for the names of property files.
    private static final String SPLASH_PROPS = "splash";
    private static final String DISPLAY_PROPS = "display";

    // Strings for hyperlinks
    private static final String BIBLE_PROTOCOL = "bible";
    private static final String DICTIONARY_PROTOCOL = "dict";
    private static final String COMMENTARY_PROTOCOL = "comment";
    private static final String HYPERLINK_STYLE = "Hyperlink";
    private static final String DOUBLE_SLASH = "//";
    private static final String SCROLL_TO_URL = "scrolling to: {0}";

    // Strings for debug messages
    private static final String PRELOAD_THREAD_NAME = "DisplayPreLoader";
    private static final String PRELOAD_FAILED = "View pre-load failed";
    private static final String PRELOAD_FINISHED = "View pre-load finished";
    private static final String EXITING = "desktop main exiting.";

    // Empty String
    private static final String EMPTY_STRING = "";

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

            log.debug(EXITING);
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
        // Calling Project.instance() will set up the project's home directory
        //     ~/.jsword
        // This will set it as a place to look for overrides for
        // ResourceBundles, properties and other resources
        Project project = Project.instance();
        
        LookAndFeelUtil.tweakLookAndFeel();
        Reporter.grabAWTExecptions(true);

        URL predicturl = project.getWritablePropertiesURL(SPLASH_PROPS);
        Splash splash = new Splash(frame, 60000);
        Job startJob = JobManager.createJob(Msg.STARTUP_TITLE.toString(), predicturl, true);
        splash.pack();

        // Create the Desktop Actions
        actions = new DesktopActions(this);
        
        // Initial setup
        frame = new JFrame();

        startJob.setProgress(Msg.STARTUP_CONFIG.toString());
        generateConfig();

        startJob.setProgress(Msg.STARTUP_GENERATE.toString());
        createComponents();

        // GUI setup
        debug();
        init();

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
        startJob.setProgress(Msg.STARTUP_GENERAL_CONFIG.toString());
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

        rdoViewTdi = new JRadioButtonMenuItem(actions.getAction(DesktopActions.TAB_MODE));
        rdoViewMdi = new JRadioButtonMenuItem(actions.getAction(DesktopActions.WINDOW_MODE));

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
        JMenu menuFile = new JMenu(actions.getAction(DesktopActions.FILE));
        menuFile.add(actions.getAction(DesktopActions.NEW_WINDOW)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.CLOSE)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.CLOSE_ALL)).addMouseListener(barStatus);
        menuFile.addSeparator();
        //menuFile.add(actFilePrint).addMouseListener(barStatus);
        //menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.SAVE_AS)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.SAVE_ALL)).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.EXIT)).addMouseListener(barStatus);

        JMenu menuEdit = new JMenu(actions.getAction(DesktopActions.EDIT));
        menuEdit.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        menuEdit.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        menuEdit.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);

        rdoViewTdi.addMouseListener(barStatus);
        rdoViewMdi.addMouseListener(barStatus);
        //chkViewTbar.addMouseListener(barStatus);
        //chkViewTbar.setSelected(viewTool);

        ButtonGroup grpViews = new ButtonGroup();
        grpViews.add(rdoViewMdi);
        grpViews.add(rdoViewTdi);

        JMenu menuView = new JMenu(actions.getAction(DesktopActions.VIEW));
        menuView.add(rdoViewTdi);
        menuView.add(rdoViewMdi);
        //menuView.add(chkViewTbar);
        menuView.addSeparator();
        menuView.add(actions.getAction(DesktopActions.VIEW_GHTML)).addMouseListener(barStatus);
        menuView.add(actions.getAction(DesktopActions.VIEW_HTML)).addMouseListener(barStatus);
        menuView.add(actions.getAction(DesktopActions.VIEW_OSIS)).addMouseListener(barStatus);

        JMenu menuTools = new JMenu(actions.getAction(DesktopActions.TOOLS));
        menuTools.add(actions.getAction(DesktopActions.BLUR1)).addMouseListener(barStatus);
        menuTools.add(actions.getAction(DesktopActions.BLUR5)).addMouseListener(barStatus);
        menuTools.add(actions.getAction(DesktopActions.DELETE_SELECTED)).addMouseListener(barStatus);
        menuTools.addSeparator();
        //menuTools.add(actions.getAction(DesktopActions.GENERATE)).addMouseListener(barStatus);
        //menuTools.add(actions.getAction(DesktopActions.DIFF)).addMouseListener(barStatus);
        //menuTools.addSeparator();
        menuTools.add(actions.getAction(DesktopActions.BOOKS)).addMouseListener(barStatus);
        menuTools.add(actions.getAction(DesktopActions.OPTIONS)).addMouseListener(barStatus);

        JMenu menuHelp = new JMenu(actions.getAction(DesktopActions.HELP));
        menuHelp.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        menuHelp.addSeparator();
        menuHelp.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);

        JMenuBar barMenu = new JMenuBar();
        barMenu.add(menuFile);
        barMenu.add(menuEdit);
        barMenu.add(menuView);
        barMenu.add(menuTools);
        barMenu.add(menuTools);
        barMenu.add(menuHelp);

        //JToolBar pnlTbar = new JToolBar();
        pnlTbar.setRollover(true);

        pnlTbar.add(actions.getAction(DesktopActions.NEW_WINDOW)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        pnlTbar.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        //pnlTbar.add(actions.getAction("Generate")).addMouseListener(barStatus);
        //pnlTbar.add(actions.getAction("Diff")).addMouseListener(barStatus);
        //pnlTbar.addSeparator();
        pnlTbar.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);

        //barBook.addHyperlinkListener(this);
        barSide.addHyperlinkListener(this);

        sptBooks.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptBooks.setOneTouchExpandable(true);
        sptBooks.setDividerLocation(0.9D);
        //sptBooks.add(barBook, JSplitPane.RIGHT);
        sptBooks.add(barSide, JSplitPane.RIGHT);
        sptBooks.add(new JPanel(), JSplitPane.LEFT);
        sptBooks.setResizeWeight(0.9D);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                actions.getAction(DesktopActions.EXIT).actionPerformed(new ActionEvent(this, 0, EMPTY_STRING));
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
                    if (url.charAt(0) == '#')
                    {
                        url = url.substring(1);
                    }
                    Object [] msg = { url };
                    log.debug(MessageFormat.format(SCROLL_TO_URL, msg));
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

                Style style = pane.addStyle(HYPERLINK_STYLE, null);
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
        int match = url.indexOf(':');
        if (match == -1)
        {
            throw new MalformedURLException(Msg.BAD_PROTOCOL_URL.toString(url));
        }

        String protocol = url.substring(0, match);
        String data = url.substring(match+1);
        if (data.startsWith(DOUBLE_SLASH))
        {
            data = data.substring(2);
        }

        if (protocol.equals(BIBLE_PROTOCOL))
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
        else if (protocol.equals(COMMENTARY_PROTOCOL))
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
        else if (protocol.equals(DICTIONARY_PROTOCOL))
        {
            barSide.getDictionaryPane().setWord(data);
        }
        else
        {
            throw new MalformedURLException(Msg.UNKNOWN_PROTOCOL.toString(protocol));
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
        actions.getAction(DesktopActions.CLOSE).setEnabled(enabled);
        actions.getAction(DesktopActions.CLOSE_ALL).setEnabled(enabled);
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
     * Makes the second invocation much faster
     */
    public static void preload()
    {
        final Thread worker = new Thread(PRELOAD_THREAD_NAME)
        {
            public void run()
            {
                URL predicturl = Project.instance().getWritablePropertiesURL(DISPLAY_PROPS);
                Job job = JobManager.createJob(Msg.PRELOAD_TITLE.toString(), predicturl, this, true);
    
                try
                {
                    job.setProgress(Msg.PRELOAD_SETUP.toString());
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
    
                    job.setProgress(Msg.PRELOAD_DATA.toString());
                    BookData data = test.getData(test.getGlobalKeyList().get(0));
                    if (interrupted())
                    {
                        return;
                    }
    
                    job.setProgress(Msg.PRELOAD_PROVIDER.toString());
                    SAXEventProvider provider = data.getSAXEventProvider();
                    if (interrupted())
                    {
                        return;
                    }
    
                    job.setProgress(Msg.PRELOAD_STYLE.toString());
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
                    log.error(PRELOAD_FAILED, ex);
                }
                finally
                {
                    job.done();
                    log.debug(PRELOAD_FINISHED);
                }
            }
        };
    
        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    /**
     * Load the config.xml file
     */
    public void generateConfig() throws IOException, JDOMException
    {
        fillChoiceFactory();

        config = new Config("Desktop Options");
        Document xmlconfig = Project.instance().getDocument("config");
        config.add(xmlconfig);

        config.setProperties(Project.instance().getProperties("desktop"));
        config.localToApplication(true);
    }

    /**
     * Setup the choices so that the options dialog knows what there is to
     * select from.
     */
    protected void fillChoiceFactory()
    {
        refreshBooks();

        // Create the array of readings sets
        ChoiceFactory.getDataMap().put("readings", ReadingsBookDriver.getInstalledReadingsSets());

        // And the array of allowed osis>html converters
        Map converters = ConverterFactory.getKnownConverters();
        Set keys = converters.keySet();
        String[] names = (String[]) keys.toArray(new String[keys.size()]);
        ChoiceFactory.getDataMap().put("converters", names);

        // The choice of simple XSL stylesheets
        SimpleSwingConverter sstyle = new SimpleSwingConverter();
        String[] sstyles = sstyle.getStyles();
        ChoiceFactory.getDataMap().put("swing-styles", sstyles);

        // The choice of configurable XSL stylesheets
        ConfigurableSwingConverter cstyle = new ConfigurableSwingConverter();
        String[] cstyles = cstyle.getStyles();
        ChoiceFactory.getDataMap().put("cswing-styles", cstyles);
    }

    /**
     * Setup the book choices
     */
    protected void refreshBooks()
    {
        // Create the array of Bibles
        String[] bnames = getFullNameArray(BookFilters.getBibles());
        ChoiceFactory.getDataMap().put("biblenames", bnames);

        // Create the array of Commentaries
        String[] cnames = getFullNameArray(BookFilters.getCommentaries());
        ChoiceFactory.getDataMap().put("commentarynames", cnames);

        // Create the array of Dictionaries
        String[] dnames = getFullNameArray(BookFilters.getDictionaries());
        ChoiceFactory.getDataMap().put("dictionarynames", dnames);
    }

    /**
     * Convert a filter into an array of names of Books that pass the filter.
     */
    private String[] getFullNameArray(BookFilter filter)
    {
        List bmds = Books.installed().getBookMetaDatas(filter);
        List names = new ArrayList();

        for (Iterator it = bmds.iterator(); it.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) it.next();
            names.add(bmd.getFullName());
        }

        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * @return The config set that this application uses to configure itself
     */
    public Config getConfig()
    {
        return config;
    }

    /**
     * The configuration engine
     */
    private Config config = null;

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
    protected FocusablePart last;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Desktop.class);

    protected DesktopActions actions;

    /*
     * GUI components
     */
    private JRadioButtonMenuItem rdoViewTdi;
    private JRadioButtonMenuItem rdoViewMdi;

    private JFrame frame;
    private JToolBar pnlTbar;
    private StatusBar barStatus;
    private SidebarPane barSide;
    //private ReferencedPane barBook = null;
    private JSplitPane sptBooks;
}
