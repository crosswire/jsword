
package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * TDI manager of how we layout views.
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
public class TDIViewLayout extends ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public TDIViewLayout(Desktop tools)
    {
        super(tools);
    }

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
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            add(view);
        }

        // ensure we have been registered
        getDesktop().getContentPane().add(tab_main, BorderLayout.CENTER);
        getDesktop().getContentPane().repaint();

        // I'm not sure if this is a bug in swing or should I really
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
        getDesktop().getContentPane().remove(tab_main);
    }

    /**
     * Add a view to the set while visible
     */
    public boolean add(BibleViewPane view)
    {
        String name = view.getTitle();
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
     * Remove a view from the set while visible
     */
    public void update(BibleViewPane view)
    {
        int index = tab_main.indexOfComponent(view);
        tab_main.setTitleAt(index, view.getTitle());
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
