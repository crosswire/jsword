package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.TextViewPanel;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.StringSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
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
    /**
     * Create the actions for the desktop
     * @param d the desktop for which these actions apply
     */
    public DesktopActions(Desktop d)
    {
        desktop = d;
        simplestyle = new SimpleSwingConverter();
        actions = new ActionFactory();
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
        if (action == null || action.length() == 0)
        {
            throw new LogicError("Empty action: No Action Command Key");
        }

        // Instead of cascading if/then/else
        // use reflecton to do a direct lookup and call
        try
        {
            Method doMethod = DesktopActions.class.getDeclaredMethod("do"+action, new Class[] { });
            doMethod.invoke(this, null);
            log.info(action);
        }
        catch (NoSuchMethodException e1)
        {
            log.error("Unknown action: " + action);
        }
        catch (IllegalArgumentException e2)
        {
            log.error("Stupid programmer error", e2);
        }
        catch (IllegalAccessException e3)
        {
            log.error("Stupid programmer error", e3);
        }
        catch (InvocationTargetException e4)
        {
            log.error("Stupid programmer error", e4);
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
                Reporter.informUser(getDesktop().getJFrame(), "No Passage to Save");
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
                Reporter.informUser(getDesktop().getJFrame(), "No Passage to Save");
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
            Reporter.informUser(getDesktop().getJFrame(), "No Passage to Save");
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
        doNothing("Cut");        
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
        doNothing("Paste");        
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
            Key ref = da.getKey();

            if (osis == null || osis.equals("") || ref == null)
            {
                Reporter.informUser(getDesktop().getJFrame(), "No Generated HTML source to view.");
                return;
            }

            SAXEventProvider osissep = new StringSAXEventProvider(osis);
            SAXEventProvider htmlsep = simplestyle.convert(osissep);
            String html = XMLUtil.writeToString(htmlsep);
            
            TextViewPanel viewer = new TextViewPanel(html, "Generated source to " + ref.getName());
            viewer.setEditable(true);
            viewer.showInFrame(getDesktop().getJFrame());
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
            String html = da.getHTMLSource();
            Key ref = da.getKey();

            if (html == null || html.equals("") || ref == null)
            {
                Reporter.informUser(getDesktop().getJFrame(), "No HTML source to view.");
                return;
            }

            TextViewPanel viewer = new TextViewPanel(html, "HTML source to "+ref.getName());
            viewer.setEditable(true);
            viewer.showInFrame(getDesktop().getJFrame());
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
            String html = da.getOSISSource();
            Key key = da.getKey();

            if (html == null || html.equals("") || key == null)
            {
                Reporter.informUser(getDesktop().getJFrame(), "No OSIS source to view.");
                return;
            }

            TextViewPanel viewer = new TextViewPanel(html, "OSIS source to "+key.getName());
            viewer.setEditable(true);
            viewer.showInFrame(getDesktop().getJFrame());
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
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
            ref.blur(amount, PassageConstants.RESTRICT_CHAPTER);
            view.setPassage(ref);
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
        // TODO Auto-generated method stub
        
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
     * A declaration that the action is not implemented.
     * @param action is what is not implemnted
     */
    protected void doNothing(String action)
    {
        log.warn(action + " is not implemented");        
    }

    /**
     * The desktop on which these actions work
     */
    private Desktop desktop;

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
