
package org.crosswire.jsword.view.swing.desktop;

import java.awt.Component;

import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * SDI manager of how we layout views.
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
public class SDIViewLayout extends ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public SDIViewLayout(Desktop tools)
    {
        super(tools);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#getRootComponent()
     */
    public Component getRootComponent()
    {
        return getSelected();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#add(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public boolean add(BibleViewPane view)
    {
        // if there are already views then we dont need any more in SDI view
        if (getDesktop().iterateBibleViewPanes().hasNext())
            return false;

        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#remove(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public boolean remove(BibleViewPane view)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#update(org.crosswire.jsword.view.swing.book.BibleViewPane)
     */
    public void updateTitle(BibleViewPane view)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.desktop.ViewLayout#getSelected()
     */
    public BibleViewPane getSelected()
    {
        getDesktop().ensureAvailableBibleViewPane();

        // Assume that there will always be one, because we don't let them be
        // deleted, and we ensure that there is at least 1 before we start.
        return (BibleViewPane) getDesktop().iterateBibleViewPanes().next();
    }
}
