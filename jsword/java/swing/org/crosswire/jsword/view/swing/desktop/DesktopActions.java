package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.crosswire.common.config.swing.ConfigEditorFactory;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.TextViewPanel;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.StringSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.book.BibleViewPane;
import org.crosswire.jsword.view.swing.book.SitesPane;
import org.crosswire.jsword.view.swing.display.FocusablePart;
import org.crosswire.jsword.view.swing.display.splitlist.OuterDisplayPane;
import org.crosswire.jsword.view.swing.util.SimpleSwingConverter;

/**
 * DesktopAction is nothing more than a holder of the behavior
 * of the Desktop. It could easily be member methods in that class.
 * It is here simply to simplify the Desktop class and minimize
 * maintenance cost.
 * 
 * Previously each of the "do" methods was a separate class.
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
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class DesktopActions implements ActionListener
{
    // Enumeration of all the keys to known actions
    public static final String FILE = "File"; //$NON-NLS-1$
    public static final String EDIT = "Edit"; //$NON-NLS-1$
    public static final String VIEW = "View"; //$NON-NLS-1$
    public static final String TOOLS = "Tools"; //$NON-NLS-1$
    public static final String HELP = "Help"; //$NON-NLS-1$
    public static final String NEW_WINDOW = "NewWindow"; //$NON-NLS-1$
    public static final String OPEN = "Open"; //$NON-NLS-1$
    public static final String CLOSE = "Close"; //$NON-NLS-1$
    public static final String CLOSE_ALL = "CloseAll"; //$NON-NLS-1$
    public static final String SAVE = "Save"; //$NON-NLS-1$
    public static final String SAVE_AS = "SaveAs"; //$NON-NLS-1$
    public static final String SAVE_ALL = "SaveAll"; //$NON-NLS-1$
    public static final String EXIT = "Exit"; //$NON-NLS-1$
    public static final String CUT = "Cut"; //$NON-NLS-1$
    public static final String COPY = "Copy"; //$NON-NLS-1$
    public static final String PASTE = "Paste"; //$NON-NLS-1$
    public static final String TAB_MODE = "TabMode"; //$NON-NLS-1$
    public static final String WINDOW_MODE = "WindowMode"; //$NON-NLS-1$
    public static final String VIEW_GHTML = "ViewGHTML"; //$NON-NLS-1$
    public static final String VIEW_HTML = "ViewHTML"; //$NON-NLS-1$
    public static final String VIEW_OSIS = "ViewOSIS"; //$NON-NLS-1$
    public static final String BLUR1 = "Blur1"; //$NON-NLS-1$
    public static final String BLUR5 = "Blur5"; //$NON-NLS-1$
    public static final String DELETE_SELECTED = "DeleteSelected"; //$NON-NLS-1$
    public static final String BOOKS = "Books"; //$NON-NLS-1$
    public static final String OPTIONS = "Options"; //$NON-NLS-1$
    public static final String CONTENTS = "Contents"; //$NON-NLS-1$
    public static final String ABOUT = "About"; //$NON-NLS-1$
    public static final String ABOUT_OK = "AboutOK"; //$NON-NLS-1$

    // Enumeration of error strings used in this class
    private static final String UNKNOWN_ACTION_ERROR = "Unknown action : {0}"; //$NON-NLS-1$
    private static final String UNEXPECTED_ERROR = "Stupid Programmer Error"; //$NON-NLS-1$
    private static final String METHOD_PREFIX = "do"; //$NON-NLS-1$

    /**
     * Create the actions for the desktop
     * @param d the desktop for which these actions apply
     */
    public DesktopActions(Desktop d)
    {
        desktop = d;
        simplestyle = new SimpleSwingConverter();
        actions = DesktopActionFactory.instance();
        actions.addActionListener(this);
    }

    /**
     * Get a particular action by internal name
     * @param key the internal name for the action
     * @return the action requested or null if it does not exist
     */
    public CWAction getAction(String key)
    {
        return actions.getAction(key);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        assert action != null;
        assert action.length() != 0;

        // Instead of cascading if/then/else
        // use reflecton to do a direct lookup and call
        try
        {
            Method doMethod = DesktopActions.class.getDeclaredMethod(METHOD_PREFIX+action, new Class[] { });
            doMethod.invoke(this, null);
            log.info(action);
        }
        catch (NoSuchMethodException e1)
        {
            log.error(MessageFormat.format(UNKNOWN_ACTION_ERROR, new Object[] { action }));
        }
        catch (IllegalArgumentException e2)
        {
            log.error(UNEXPECTED_ERROR, e2);
        }
        catch (IllegalAccessException e3)
        {
            log.error(UNEXPECTED_ERROR, e3);
        }
        catch (InvocationTargetException e4)
        {
            log.error(UNEXPECTED_ERROR, e4);
        }
    }

    /**
     * @return the desktop to which these actions apply
     */
    public Desktop getDesktop()
    {
        return desktop;
    }

    /**
     * @return the Bible installer dialog
     */
    public SitesPane getSites()
    {
        if (sites == null)
        {
            sites = new SitesPane();
        }
        return sites;
    }

    /**
     * For creating a new window.
     */
    protected void doNewWindow()
    {
        BibleViewPane view = new BibleViewPane();

        getDesktop().addBibleViewPane(view);

        view.addHyperlinkListener(getDesktop());
    }

    /**
     * Open a new passage window from a file.
     */
    protected void doOpen()
    {
        try
        {
            BibleViewPane view = getDesktop().getSelectedBibleViewPane();
            view.open();
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * Close the current passage window.
     */
    protected void doClose()
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        getDesktop().removeBibleViewPane(view);
    }

    /**
     * Close all the passage windows.
     */
    protected void doCloseAll()
    {
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            getDesktop().removeBibleViewPane(view);
        }
    }

    /**
     * Save the current passage window.
     */
    protected void doSave()
    {
        try
        {
            BibleViewPane view = getDesktop().getSelectedBibleViewPane();
            if (!view.maySave())
            {
                Reporter.informUser(getDesktop().getJFrame(), Msg.NO_PASSAGE);
                return;
            }

            view.save();
        }
        catch (IOException ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * Save the current passage window under a new name.
     */
    protected void doSaveAs()
    {
        try
        {
            BibleViewPane view = getDesktop().getSelectedBibleViewPane();
            if (!view.maySave())
            {
                Reporter.informUser(getDesktop().getJFrame(), Msg.NO_PASSAGE);
                return;
            }

            view.saveAs();
        }
        catch (IOException ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * Save all the passage windows.
     */
    protected void doSaveAll()
    {
        boolean ok = false;
        
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            if (view.maySave())
            {
                ok = true;
            }
        }
        
        if (!ok)
        {
            Reporter.informUser(getDesktop().getJFrame(), Msg.NO_PASSAGE);
            return;
        }

        it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            try
            {
                BibleViewPane view = (BibleViewPane) it.next();
                view.save();
            }
            catch (IOException ex)
            {
                Reporter.informUser(getDesktop().getJFrame(), ex);
            }
        }
    }

    /**
     * Exits the VM.
     */
    protected void doExit()
    {
        System.exit(0);
    }

    /**
     * Remove the selected text from the "active" display area
     * and put it on the clipboard.
     */
    protected void doCut()
    {
        doNothing(CUT);
    }

    /**
     * Copy the selected text from the "active" display area to the clipboard.
     */
    protected void doCopy()
    {
        FocusablePart da = getDesktop().getDisplayArea();
        da.copy();
    }

    /**
     * Paste the clipboard to the insertion point for the "active" display area.
     */
    protected void doPaste()
    {
        doNothing(PASTE);
    }

    /**
     * View the Tabbed Document Interface (TDI) interface.
     */
    protected void doTabMode()
    {
        getDesktop().setLayoutType(Desktop.LAYOUT_TYPE_TDI);
    }

    /**
     * View the Multiple Document/Window Interface (MDI) interface.
     */
    protected void doWindowMode()
    {
        getDesktop().setLayoutType(Desktop.LAYOUT_TYPE_MDI);
    }

    /**
     * View the generated HTML source to the current window.
     */
    protected void doViewGHTML()
    {
        // BUG: Fix this as it is not the same as what is supplied
        // to the viewer. And it looks terrible.
        try
        {
            FocusablePart da = getDesktop().getDisplayArea();
            String osis = da.getOSISSource();
            String html = null;
            if (osis != null && osis.length() > 0)
            {
                SAXEventProvider osissep = new StringSAXEventProvider(osis);
                SAXEventProvider htmlsep = simplestyle.convert(osissep);
                html = XMLUtil.writeToString(htmlsep);
            }

            showTextViewer(da.getKey(), Msg.GHTML.toString(), html);
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * View the HTML as interpreted by the current window.
     * This HTML will not return the styling present in the viewer.
     * That is all class="" are stripped out.
     * Also you may find additional whitespace added to the original.
     */
    protected void doViewHTML()
    {
        try
        {
            FocusablePart da = getDesktop().getDisplayArea();
            showTextViewer(da.getKey(), Msg.HTML.toString(), da.getHTMLSource());
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * View the OSIS source to the current window.
     */
    protected void doViewOSIS()
    {
        try
        {
            FocusablePart da = getDesktop().getDisplayArea();
            showTextViewer(da.getKey(), Msg.OSIS.toString(), da.getOSISSource());
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * Pop up a TextViewPane or inform user of the problem
     * @param ref
     * @param name
     * @param html
     */
    private void showTextViewer(Key ref, String name, String html)
    {
        if (html == null || html.length() == 0 || ref == null)
        {
            Reporter.informUser(getDesktop().getJFrame(), Msg.SOURCE_MISSING, name);
            return;
        }
        
        TextViewPanel viewer = new TextViewPanel(html, Msg.SOURCE_MISSING.toString(name));
        viewer.setEditable(true);
        viewer.showInFrame(getDesktop().getJFrame());
    }

    /**
     * Blur (expand) the current passage action by one verse on each side.
     * This bound by the boundaries of the Chapter.
     */
    protected void doBlur1()
    {
        doBlur(1);        
    }

    /**
     * Blur (expand) the current passage action by five verses on each side.
     * This bound by the boundaries of the Chapter.
     */
    protected void doBlur5()
    {
       doBlur(5);        
    }

    /**
     * Blur (expand) the current passage action by amount verses on each side.
     * This bound by the boundaries of the Chapter.
     * @param amount The amount of blurring
     */
    protected void doBlur(int amount)
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        if (view != null)
        {
            Passage ref = view.getPassage();
            if (ref != null) {
                ref.blur(amount, PassageConstants.RESTRICT_CHAPTER);
                view.setPassage(ref);
            }
        }
    }
    
    /**
     * Remove the selected verses out of the PassagePane.
     */
    protected void doDeleteSelected()
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        if (view != null)
        {
            OuterDisplayPane odp = view.getPassagePane();
            odp.deleteSelected(view);
        }
    }

    /**
     * Opens the Book installer window (aka a SitesPane)
     */
    protected void doBooks()
    {
        if (sites == null)
        {
            sites = new SitesPane();
        }

        sites.showInDialog(getDesktop().getJFrame());
    }

    /**
     * Opens the Options window
     */
    protected void doOptions()
    {
        try
        {
            desktop.fillChoiceFactory();
            BooksListener cbl = new BooksListener()
            {
                public void bookAdded(BooksEvent ev)
                {
                    desktop.refreshBooks();
                }

                public void bookRemoved(BooksEvent ev)
                {
                    desktop.refreshBooks();
                }
            };
            Books.installed().addBooksListener(cbl);

            URL configUrl = Project.instance().getWritablePropertiesURL("desktop"); //$NON-NLS-1$
            ConfigEditorFactory.showDialog(desktop.getConfig(), desktop.getJFrame(), configUrl);

            Books.installed().removeBooksListener(cbl);
        }
        catch (Exception ex)
        {
            Reporter.informUser(desktop, ex);
        }
    }

    /**
     * For opening a help file.
     */
    protected void doContents()
    {
        JOptionPane.showMessageDialog(getDesktop().getJFrame(), Msg.NO_HELP);
    }

    /**
     * For opening the About window
     */
    protected void doAbout()
    {
        if (atp == null)
        {
            atp = new AboutPane(getDesktop());
        }

        atp.showInDialog(getDesktop().getJFrame());
    }

    /**
     * For opening the About window
     */
    protected void doAboutOK()
    {
        if (atp != null)
        {
            atp.close();
        }
    }

    /**
     * A declaration that the action is not implemented.
     * @param action is what is not implemnted
     */
    protected void doNothing(String action)
    {
        Object[] msg = { getAction(action).getValue(Action.NAME) };
        Reporter.informUser(getDesktop().getJFrame(), Msg.NOT_IMPLEMENTED, msg);
    }

    /**
     * The desktop on which these actions work
     */
    protected Desktop desktop;

    /**
     * The factory for actions that this class works with
     */
    private ActionFactory actions;

    /**
     * The simple stylizer for converting OSIS to HTML
     */
    private Converter simplestyle;

    /**
     * The About window
     */
    private AboutPane atp;

    /**
     * The Book installer window
     */
    private SitesPane sites;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DesktopActions.class);

}
