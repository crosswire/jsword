
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Filters;
import org.crosswire.jsword.control.search.Engine;
import org.crosswire.jsword.control.search.Matcher;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.view.swing.event.CommandEvent;
import org.crosswire.jsword.view.swing.event.CommandListener;
import org.crosswire.jsword.view.swing.event.VersionEvent;
import org.crosswire.jsword.view.swing.event.VersionListener;

/**
 * Passage Selection area.
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
public class SelectPane extends JPanel
{
    /**
     * General constructor
     */
    public SelectPane()
    {
        // Bother search() and version() rely on this returning only Bibles
        mdl_versn = new BooksComboBoxModel(Filters.getBibles());
        jbInit();
    }

    /**
     * Initialize the GUI
     */
    private void jbInit()
    {
        txt_search.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { search(); }
        });
        pnl_search.setLayout(new BorderLayout(5, 0));
        btn_search.setText("Go");
        btn_search.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { search(); }
        });
        txt_search.setColumns(20);
        pnl_search.add(txt_search, BorderLayout.CENTER);
        pnl_search.add(cbo_search, BorderLayout.WEST);
        pnl_search.add(btn_search, BorderLayout.EAST);

        lbl_passg.setToolTipText("");
        lbl_passg.setText("View:");
        pnl_passg.setLayout(new BorderLayout(5, 0));
        txt_passg.setColumns(20);
        txt_passg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { view(); }
        });
        cbo_versn.setModel(mdl_versn);
        cbo_versn.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev) { version(); }
        });
        pnl_passg.add(txt_passg, BorderLayout.CENTER);
        pnl_passg.add(lbl_passg, BorderLayout.WEST);
        pnl_passg.add(cbo_versn, BorderLayout.EAST);

        this.setLayout(new GridLayout(2, 0, 5, 5));
        this.add(pnl_search, null);
        this.add(pnl_passg, null);
    }

    /**
     * Someone pressed return in the search area
     */
    protected void search()
    {
        try
        {
            String param = txt_search.getText();
            String type = (String) cbo_search.getSelectedItem();
            // This cast is safe because we asked for Bibles in the ctor
            Bible version = (Bible) mdl_versn.getSelectedBookMetaData().getBook();

            if (MATCH.equals(type))
            {
                Matcher match = new Matcher(version);

                PassageTally tally = match.bestMatch(param);
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(20);

                txt_passg.setText(tally.getName());
                fireCommandMade(new CommandEvent(this, tally));
            }
            else
            {
                Engine engine = new Engine(version);

                Passage ref = engine.search(param);
                // ref.trimRanges(20);

                txt_passg.setText(ref.getName());
                fireCommandMade(new CommandEvent(this, ref));
            }

            txt_passg.grabFocus();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The Search string
     */
    public String getSearchString()
    {
        return txt_search.getText();
    }

    /**
     * The exact string in the passage window - without any parsing
     */
    public String getPassageString()
    {
        return txt_passg.getText();
    }

    /**
     * The passage string, post parsing
     */
    public Passage getPassage() throws NoSuchVerseException
    {
        String param = txt_passg.getText();
        return PassageFactory.createPassage(param);
    }

    /**
     * The passage string, post parsing
     */
    public void setPassage(Passage ref)
    {
        txt_passg.setText(ref.getName());
    }

    /**
     * Someone pressed return in the passage area
     */
    protected void view()
    {
        try
        {
            Passage ref = getPassage();
            fireCommandMade(new CommandEvent(this, ref));
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone changed the version combo
     */
    protected void version()
    {
        try
        {
            // This cast is safe because we asked for Bibles in the ctor
            Bible bible = (Bible) mdl_versn.getSelectedBookMetaData().getBook();
            fireVersionChanged(new VersionEvent(this, bible));
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Add a command listener
     */
    public synchronized void removeCommandListener(CommandListener li)
    {
        if (commandListeners != null && commandListeners.contains(li))
        {
            Vector v = (Vector) commandListeners.clone();
            v.removeElement(li);
            commandListeners = v;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void addCommandListener(CommandListener li)
    {
        Vector v = commandListeners == null ? new Vector(2) : (Vector) commandListeners.clone();
        if (!v.contains(li))
        {
            v.addElement(li);
            commandListeners = v;
        }
    }

    /**
     * Add a version listener
     */
    public synchronized void removeVersionListener(VersionListener l)
    {
        if (versionListeners != null && versionListeners.contains(l))
        {
            Vector v = (Vector) versionListeners.clone();
            v.removeElement(l);
            versionListeners = v;
        }
    }

    /**
     * Remove a version listener
     */
    public synchronized void addVersionListener(VersionListener l)
    {
        Vector v = versionListeners == null ? new Vector(2) : (Vector) versionListeners.clone();
        if (!v.contains(l))
        {
            v.addElement(l);
            versionListeners = v;
        }
    }

    /**
     * Inform the command listeners
     */
    protected void fireCommandMade(CommandEvent ev)
    {
        if (commandListeners != null)
        {
            Vector listeners = commandListeners;
            int count = listeners.size();
            for (int i = 0; i < count; i++)
            {
                ((CommandListener) listeners.elementAt(i)).commandMade(ev);
            }
        }
    }

    /**
     * Inform the version listeners
     */
    protected void fireVersionChanged(VersionEvent ev)
    {
        if (versionListeners != null)
        {
            Vector listeners = versionListeners;
            int count = listeners.size();
            for (int i = 0; i < count; i++)
            {
                ((VersionListener) listeners.elementAt(i)).versionChanged(ev);
            }
        }
    }

    private static final String SEARCH = "Search";
    private static final String MATCH = "Match";

    private BooksComboBoxModel mdl_versn = null;
    private JPanel pnl_passg = new JPanel();
    private JComboBox cbo_versn = new JComboBox();
    private JLabel lbl_passg = new JLabel();
    private JTextField txt_passg = new JTextField();
    private JPanel pnl_search = new JPanel();
    private JTextField txt_search = new JTextField();
    private transient Vector commandListeners;
    private transient Vector versionListeners;
    private JComboBox cbo_search = new JComboBox(new Object[] { SEARCH, MATCH });
    private JButton btn_search = new JButton();
}
