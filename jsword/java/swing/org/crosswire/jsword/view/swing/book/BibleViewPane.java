
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.view.swing.event.*;

/**
 * A quick Swing Bible display pane.
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
        pnl_select.addCommandListener(new CommandListener()
        {
            public void commandMade(CommandEvent ev)
            {
                if (saved == null)
                    fireTitleChanged(new TitleChangedEvent(BibleViewPane.this, getTitle()));
            }
        });
        pnl_passg.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

        // pnl_select.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        pnl_select.addCommandListener(pnl_passg);
        pnl_select.addVersionListener(pnl_passg);

        this.setLayout(new BorderLayout());
        this.add(pnl_select, BorderLayout.NORTH);
        this.add(pnl_passg, BorderLayout.CENTER);
    }

    /**
     * How has this view been saved
     */
    public String getTitle()
    {
        if (saved == null)
            return getDefaultName();

        return saved.getName();
    }

    /**
     *
     */
    public String getDefaultName()
    {
        String deft = pnl_select.getSearchString();

        if (deft == null | deft.trim().length() == 0)
        {
            deft = pnl_select.getPassageString();

            if (deft == null | deft.trim().length() == 0)
                deft = "Untitled "+number;
        }

        return StringUtil.shorten(deft, shortlen);
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

    /**
     * Add a listener to the list
     * @param li
     */
    public synchronized void addTitleChangedListener(TitleChangedListener li)
    {
        Vector v = listeners == null ? new Vector(2) : (Vector) listeners.clone();
        if (!v.contains(li))
        {
            v.addElement(li);
            listeners = v;
        }
    }

    /**
     * Remote a listener from the list
     * @param li
     */
    public synchronized void removeTitleChangedListener(TitleChangedListener li)
    {
        if (listeners != null && listeners.contains(li))
        {
            Vector temp = (Vector) listeners.clone();
            temp.removeElement(li);
            listeners = temp;
        }
    }

    /**
     * Inform the listeners that a title has changed
     * @param ev
     */
    protected void fireTitleChanged(TitleChangedEvent ev)
    {
        if (listeners != null)
        {
            Vector temp = listeners;
            int count = temp.size();
            for (int i = 0; i < count; i++)
            {
                ((TitleChangedListener) temp.elementAt(i)).titleChanged(ev);
            }
        }
    }

    /**
     * Returns the shortlen.
     * @return int
     */
    public static int getShortlen()
    {
        return shortlen;
    }

    /**
     * Sets the shortlen.
     * @param shortlen The shortlen to set
     */
    public static void setShortlen(int shortlen)
    {
        BibleViewPane.shortlen = shortlen;
    }

    private static int shortlen = 30;
    private int number = base++;
    private File saved = null;
    private SelectPane pnl_select = new SelectPane();
    private PassagePane pnl_passg = new PassagePane();
    private transient Vector listeners;
    private static int base = 1;
}
