
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.crosswire.jsword.passage.Passage;

/**
 * A quick Swing Bible display pane.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public class BibleViewPane extends JPanel
{
    /**
     * Simple ctor
     */
    public BibleViewPane()
    {
        jbInit();
    }

    /**
     * Setup the GUI
     */
    private void jbInit()
    {
        // pnl_select.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        pnl_passg.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

        pnl_select.addCommandListener(pnl_passg);
        pnl_select.addVersionListener(pnl_passg);

        this.setLayout(new BorderLayout());
        this.add(pnl_select, BorderLayout.NORTH);
        this.add(pnl_passg, BorderLayout.CENTER);
    }

    /**
     * How has this view been saved
     */
    public String getSavedName()
    {
        if (saved == null)
            return "Untitled "+number;

        return saved.getName();
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return pnl_passg.getPassage();
    }

    /**
     * Accessor for the current passage
     */
    public void setPassage(Passage ref)
    {
        pnl_select.setPassage(ref);
        pnl_passg.setPassage(ref);
    }

    /**
     * Accessor for the PassagePane
     */
    public PassagePane getPassagePane()
    {
        return pnl_passg;
    }

    /**
     * Accessor for the SelectPane
     */
    public SelectPane getSelectPane()
    {
        return pnl_select;
    }

    private int number = base++;
    private File saved = null;
    private SelectPane pnl_select = new SelectPane();
    private PassagePane pnl_passg = new PassagePane();

    private static int base = 1;
}
