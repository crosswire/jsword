
package org.crosswire.jsword.view.swing.desktop;

import java.awt.Component;

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
public class TDIViewLayout extends ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public TDIViewLayout(Desktop tools)
    {
        super(tools);

        tab_main = new JTabbedPane();
        tab_main.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        LookAndFeelUtil.addComponentToUpdate(tab_main);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#getRootComponent()
     */
    public Component getRootComponent()
    {
        return tab_main;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#add(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public boolean add(BibleViewPane view)
    {
        String name = view.getTitle();
        tab_main.add(view, name);

        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#remove(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public boolean remove(BibleViewPane view)
    {
        tab_main.remove(view);

        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#update(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public void updateTitle(BibleViewPane view)
    {
        int index = tab_main.indexOfComponent(view);
        tab_main.setTitleAt(index, view.getTitle());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#getSelected()
     */
    public BibleViewPane getSelected()
    {
        return (BibleViewPane) tab_main.getSelectedComponent();
    }

    private JTabbedPane tab_main;
}
