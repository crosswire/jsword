
package org.crosswire.jsword.view.swing.desktop;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.crosswire.common.swing.LookAndFeelUtil;
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
public class TDIViewLayout implements ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public TDIViewLayout()
    {
        tab_main.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        LookAndFeelUtil.addComponentToUpdate(tab_main);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#getRootComponent()
     */
    public Component getRootComponent()
    {
        if (views.size() == 1)
        {
            return (Component) views.get(0);
        }
        else
        {
            return tab_main;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#add(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public void add(BibleViewPane view)
    {
        switch (views.size())
        {
        case 0:
            // Don't add the view to tab_main
            break;

        case 1:
            // We used to be in SDI mode, but we are about to go into TDI mode
            // (when getRootComponent() is called is a few secs. So we need
            // to construct tab_main properly
            BibleViewPane first = (BibleViewPane) views.get(0); 
            tab_main.add(first, first.getTitle());
            tab_main.add(view, view.getTitle());
            tab_main.setSelectedComponent(view);
            break;

        default:
            // So we are well into tabbed mode
            tab_main.add(view, view.getTitle());
            tab_main.setSelectedComponent(view);
            break;
        }

        views.add(view);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#remove(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public void remove(BibleViewPane view)
    {
        if (views.size() == 2)
        {
            // remove both tabs, because 0 will be reparented
            tab_main.removeTabAt(0);
        }

        tab_main.remove(view);

        views.remove(view);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#update(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public void updateTitle(BibleViewPane view)
    {
        if (views.size() > 1)
        {
            int index = tab_main.indexOfComponent(view);
            tab_main.setTitleAt(index, view.getTitle());
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#getSelected()
     */
    public BibleViewPane getSelected()
    {
        if (views.size() == 1)
        {
            return (BibleViewPane) views.get(0);
        }
        else
        {
            return (BibleViewPane) tab_main.getSelectedComponent();
        }
    }

    /**
     * The list of views. We maintain this separately so we don't have
     * a dependency on Desktop, which has caused loops before
     */
    private List views = new ArrayList();

    /**
     * The tabbed view pane
     */
    private JTabbedPane tab_main = new JTabbedPane();
}
