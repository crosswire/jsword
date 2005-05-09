package org.crosswire.common.swing.desktop;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.desktop.event.TitleChangedEvent;
import org.crosswire.common.swing.desktop.event.TitleChangedListener;
import org.crosswire.common.swing.desktop.event.ViewEvent;
import org.crosswire.common.swing.desktop.event.ViewEventListener;
import org.crosswire.common.util.CallContext;

/**
 *
 * A ViewManager is an abstraction of a desktop that displays views
 * as tabs or sub-windows. All the views are of the same type.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class ViewManager implements Viewable, TitleChangedListener, ViewEventListener
{
    /**
     * Construct a ViewManager.
     */
    public ViewManager(ViewGenerator generator)
    {
        this.generator = generator;
        panel = new JPanel(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        panel.add(getViewLayout().getPanel(), getConstraint());

        // Get the action definitions from the calling class
        contextActions = new ActionFactory(CallContext.instance().getCallingClass(), this);

        tdiView = new JRadioButtonMenuItem(contextActions.getAction(TAB_MODE));
        mdiView = new JRadioButtonMenuItem(contextActions.getAction(WINDOW_MODE));

        ButtonGroup grpViews = new ButtonGroup();
        grpViews.add(mdiView);
        grpViews.add(tdiView);

        if (getViewLayoutType().equals(LayoutType.MDI))
        {
            mdiView.setSelected(true);
        }
        else
        {
            tdiView.setSelected(true);
        }
        addViewEventListener(this);
        addView();
    }

    /**
     * @return the desktop
     */
    public JPanel getDesktop()
    {
        return panel;
    }

    /**
     * @return Returns the mdiView.
     */
    public JRadioButtonMenuItem getMdiView()
    {
        return mdiView;
    }

    /**
     * @return Returns the tdiView.
     */
    public JRadioButtonMenuItem getTdiView()
    {
        return tdiView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#addView(java.awt.Component)
     */
    public void addView(Component component)
    {
        getViewLayout().addView(component);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#closeAll()
     */
    public void closeAll()
    {
        getViewLayout().closeAll();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#closeOthers(java.awt.Component)
     */
    public void closeOthers(Component component)
    {
        getViewLayout().closeOthers(component);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#getView(int)
     */
    public Component getView(int i)
    {
        return getViewLayout().getView(i);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#getViews()
     */
    public Collection getViews()
    {
        return getViewLayout().getViews();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#moveTo(org.crosswire.common.swing.desktop.AbstractViewLayout)
     */
    public void moveTo(AbstractViewLayout other)
    {
        getViewLayout().moveTo(other);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#updateTitle(java.awt.Component)
     */
    public void updateTitle(Component component)
    {
        getViewLayout().updateTitle(component);
    }

    /**
     * Adds a view to the list in this Desktop.
     */
    public Component addView()
    {

        Component view = generator.createView();

        if (view instanceof Titleable)
        {
            ((Titleable) view).addTitleChangedListener(this);
        }

        addView(view);

        return view;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#removeView(java.awt.Component)
     */
    public void removeView(Component view)
    {
        // If it were the last one then clear it.
        if (getViewLayout().getViewCount() == 1)
        {
            if (view instanceof Clearable)
            {
                ((Clearable) view).clear();
            }
            return;
        }

        // This call will generate a ViewEvent and call viewRemoved
        getViewLayout().removeView(view);
    }

    /**
     * Reset a view so that it can be reused. If the Component does not
     * implement Clearable, then nothing is done.
     * @param view the view to be cleared
     */
    public void clear(Component view)
    {
        if (view instanceof Clearable)
        {
            ((Clearable) view).clear();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#iterator()
     */
    public Iterator iterator()
    {
        return getViewLayout().iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#visit(org.crosswire.common.swing.desktop.ViewVisitor)
     */
    public void visit(ViewVisitor visitor)
    {
        getViewLayout().visit(visitor);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#getViewCount()
     */
    public int getViewCount()
    {
        return getViewLayout().getViewCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#getSelected()
     */
    public Component getSelected()
    {
        return getViewLayout().getSelected();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#select(java.awt.Component)
     */
    public void select(Component component)
    {
        getViewLayout().select(component);
    }

    /**
     * Get the initial layout type. This is to be used by "config"
     */
    private static final LayoutType getInitialViewLayoutType()
    {
        if (initial == null)
        {
            initial = LayoutType.TDI;
        }
        return initial;
    }

    /**
     * What is the current layout type?
     */
    private final LayoutType getViewLayoutType()
    {
        if (current == null)
        {
            current = getInitialViewLayoutType();
        }
        return current;
    }

    /**
     * Set the current layout type
     */
    private final void setViewLayoutType(LayoutType newLayoutType)
    {
        current = newLayoutType;
    }

    /**
     * What is the current layout?
     */
    public final AbstractViewLayout getViewLayout()
    {
        return getViewLayoutType().getLayout();
    }

    /**
     * Setup the current view
     */
    public void setLayoutType(LayoutType next)
    {
        // Check if this is a change
        if (getViewLayoutType().equals(next))
        {
            return;
        }

        AbstractViewLayout nextLayout = next.getLayout();
        AbstractViewLayout currentLayout = getViewLayout();
        currentLayout.moveTo(nextLayout);

        panel.remove(currentLayout.getPanel());
        panel.add(nextLayout.getPanel(), getConstraint());
        setViewLayoutType(next);
    }

    /**
     * What is the initial layout state?
     */
    public static int getInitialLayoutType()
    {
        if (initial == null)
        {
            initial = LayoutType.TDI;
        }
        return initial.toInteger();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.ViewEventListener#viewRemoved(org.crosswire.common.swing.desktop.ViewEvent)
     */
    public void viewRemoved(ViewEvent event)
    {
        Object view = event.getSource();
        if (view instanceof Titleable)
        {
            ((Titleable) view).removeTitleChangedListener(this);
        }
    }

    /**
     * What should the initial layout state be?
     */
    public static void setInitialLayoutType(int initialLayout)
    {
        ViewManager.initial = LayoutType.fromInteger(initialLayout);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.TitleChangedListener#titleChanged(org.crosswire.bibledesktop.book.TitleChangedEvent)
     */
    public void titleChanged(TitleChangedEvent ev)
    {
        Component view = (Component) ev.getSource();
        getViewLayout().updateTitle(view);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#addViewEventListener(org.crosswire.common.swing.desktop.event.ViewEventListener)
     */
    public void addViewEventListener(ViewEventListener listener)
    {
        LayoutType.MDI.getLayout().addViewEventListener(listener);
        LayoutType.TDI.getLayout().addViewEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.Viewable#removeViewEventListener(org.crosswire.common.swing.desktop.event.ViewEventListener)
     */
    public void removeViewEventListener(ViewEventListener listener)
    {
        LayoutType.MDI.getLayout().removeViewEventListener(listener);
        LayoutType.TDI.getLayout().removeViewEventListener(listener);
    }

    /**
     * Get a particular action by internal name
     * @param key the internal name for the action
     * @return the action requested or null if it does not exist
     */
    public Action getContextAction(String key)
    {
        return contextActions.getAction(key);
    }

    protected Object getConstraint()
    {
        return gbc;
    }

    /**
     * View the Tabbed Document Interface (TDI) interface.
     */
    public void doTabMode()
    {
        setLayoutType(LayoutType.TDI);
    }

    /**
     * View the Multiple Document/Window Interface (MDI) interface.
     */
    public void doWindowMode()
    {
        setLayoutType(LayoutType.MDI);
    }

    /**
     * For creating a new window.
     */
    public void doNewTab()
    {
        addView();
    }

    /**
     * Close the current passage window.
     */
    public void doCloseView()
    {
        removeView(getSelected());
    }

    /**
     * Close the current passage window.
     */
    public void doClearView()
    {
        clear(getSelected());
    }

    /**
     * Close all the passage windows.
     */
    public void doCloseAllViews()
    {
        closeAll();
    }

    /**
     * Close all other passage windows.
     */
    public void doCloseOtherViews()
    {
        closeOthers(getSelected());
    }

    public static final String TAB_MODE = "TabMode"; //$NON-NLS-1$
    public static final String WINDOW_MODE = "WindowMode"; //$NON-NLS-1$
    public static final String NEW_TAB = "NewTab"; //$NON-NLS-1$
    public static final String CLOSE_VIEW = "CloseView"; //$NON-NLS-1$
    public static final String CLEAR_VIEW = "ClearView"; //$NON-NLS-1$
    public static final String CLOSE_ALL_VIEWS = "CloseAllViews"; //$NON-NLS-1$
    public static final String CLOSE_OTHER_VIEWS = "CloseOtherViews"; //$NON-NLS-1$
    public static final String OPEN = "Open"; //$NON-NLS-1$
    public static final String SAVE = "Save"; //$NON-NLS-1$
    public static final String SAVE_AS = "SaveAs"; //$NON-NLS-1$
    public static final String SAVE_ALL = "SaveAll"; //$NON-NLS-1$

    /**
     * The initial layout state
     */
    private static LayoutType initial;

    private ViewGenerator generator;
    private JPanel panel;

    /**
     * The current way the views are laid out
     */
    private LayoutType current;

    private ActionFactory contextActions;

    /*
     * GUI components
     */
    private JRadioButtonMenuItem tdiView;
    private JRadioButtonMenuItem mdiView;

    /**
     * A shared, reusable constraint that makes its contents
     * grow to fill the area.
     */
    private GridBagConstraints gbc;
}
