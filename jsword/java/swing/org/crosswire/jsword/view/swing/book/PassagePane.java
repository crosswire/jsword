
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.view.swing.event.CommandEvent;
import org.crosswire.jsword.view.swing.event.CommandListener;
import org.crosswire.jsword.view.swing.event.VersionEvent;
import org.crosswire.jsword.view.swing.event.VersionListener;
import org.crosswire.jsword.view.swing.passage.PassageList;

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
public class PassagePane extends JPanel implements VersionListener, CommandListener
{
    /**
     * Initialize the PassagePane
     */
    public PassagePane()
    {
        try
        {
            version = Defaults.getBibleMetaData().getBible();
            txt_passg.setVersion(version);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }

        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        lst_passg.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev) { selection(); }
        });

        spt_passg.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        spt_passg.add(scr_passg, JSplitPane.LEFT);
        spt_passg.add(txt_passg, JSplitPane.RIGHT);
        spt_passg.setOneTouchExpandable(true);
        spt_passg.setDividerLocation(0.0D);

        scr_passg.getViewport().add(lst_passg);

        this.setLayout(new BorderLayout());
        this.add(spt_passg, BorderLayout.CENTER);
    }

    /**
     * Someone wants us to show in a new version
     */
    public void versionChanged(VersionEvent ev)
    {
        version = ev.getBible();
        txt_passg.setVersion(version);

        // refresh the view
        selection();
    }

    /**
     * Someone wants us to display a new passage
     */
    public void commandMade(CommandEvent ev)
    {
        setPassage(ev.getPassage());
    }

    /**
     * Set the passage to be displayed
     */
    public void setPassage(Passage ref)
    {
        try
        {
            lst_passg.setPassage(ref);
            txt_passg.setPassage(ref);
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Get the passage being displayed
     */
    public Passage getPassage()
    {
        return lst_passg.getPassage();
    }

    /**
     * Accessor for the PassageList
     */
    public PassageList getPassageList()
    {
        return lst_passg;
    }

    /**
     * Accessor for the current TextComponent
     */
    public JTextComponent getJTextComponent()
    {
        return txt_passg.getJTextComponent();
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            Object[] ranges = lst_passg.getSelectedValues();

            // if there was a single selection then show the whole chapter
            if (ranges.length == 1)
            {
                VerseRange range = (VerseRange) ranges[0];

                Passage ref = PassageFactory.createPassage();
                ref.add(range);
                ref.blur(1000, Passage.RESTRICT_CHAPTER);

                txt_passg.setPassage(ref);
            }
            else
            {
                Passage ref = PassageFactory.createPassage();
                for (int i=0; i<ranges.length; i++)
                {
                    ref.add((VerseRange) ranges[i]);
                }

            }
        }
        catch (Throwable ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(PassagePane.class);

    /** What is being displayed */
    private Bible version = null;

    private JSplitPane spt_passg = new JSplitPane();
    private JScrollPane scr_passg = new JScrollPane();
    private PassageTabbedPane txt_passg = new PassageTabbedPane();
    private PassageList lst_passg = new PassageList();
}
