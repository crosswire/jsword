
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * SidebarPane builds a panel containing a set of books in tabbed dialogs.
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
public class SidebarPane extends JPanel
{
    /**
     * Simple ctor
     */
    public SidebarPane()
    {
        jbInit();
    }

    /**
     * GUI initializer.
     */
    private void jbInit()
    {
        spt_books.setOrientation(JSplitPane.VERTICAL_SPLIT);
        spt_books.setDividerLocation(150);
        spt_books.add(tab_conc, JSplitPane.TOP);
        spt_books.add(tab_dict, JSplitPane.BOTTOM);

        this.setLayout(new BorderLayout());
        this.add(spt_books,  BorderLayout.CENTER);
    }

    private CommentaryPane tab_conc = new CommentaryPane();
    private JSplitPane spt_books = new JSplitPane();
    private DictionaryPane tab_dict = new DictionaryPane();
}
