
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Search;
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
        // search() and version() rely on this returning only Bibles
        mdl_versn = new BooksComboBoxModel(BookFilters.getBibles());
        jbInit();
    }

    /**
     * Initialize the GUI
     */
    private void jbInit()
    {
        rdo_passg.setSelected(true);
        rdo_passg.setText("Passage Lookup");
        rdo_passg.setMnemonic('P');
        rdo_passg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                lay_cards.show(pnl_cards, PASSAGE);
            }
        });
        rdo_match.setText("Match");
        rdo_match.setMnemonic('M');
        rdo_match.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                lay_cards.show(pnl_cards, MATCH);
            }
        });
        rdo_search.setMnemonic('S');
        rdo_search.setText("Search");
        rdo_search.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                lay_cards.show(pnl_cards, SEARCH);
            }
        });
        pnl_select.setLayout(new FlowLayout(FlowLayout.LEFT));
        cbo_versn.setModel(mdl_versn);
        cbo_versn.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                version();
            }
        });
        pnl_radios.add(rdo_passg, null);
        pnl_radios.add(rdo_search, null);
        pnl_radios.add(rdo_match, null);
        pnl_versn.setLayout(new BorderLayout());
        pnl_versn.add(cbo_versn, BorderLayout.SOUTH);
        pnl_select.setLayout(new BorderLayout());
        pnl_select.add(pnl_radios, BorderLayout.WEST);
        pnl_select.add(pnl_versn, BorderLayout.EAST);
        grp_type.add(rdo_passg);
        grp_type.add(rdo_search);
        grp_type.add(rdo_match);

        lbl_passg.setDisplayedMnemonic('V');
        lbl_passg.setText("View:");
        pnl_passg.setLayout(new GridBagLayout());
        txt_passg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                view();
            }
        });
        btn_dialg.setText("...");
        btn_dialg.setBorder(BorderFactory.createCompoundBorder(txt_passg.getBorder(), btn_dialg.getBorder()));
        btn_dialg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                select();
            }
        });
        btn_passg.setText("Go");
        btn_search.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                passage();
            }
        });

        pnl_passg.add(lbl_passg, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, 5, 0, 2), 0, 0));
        pnl_passg.add(txt_passg, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 2, 2, -1), 0, 0));
        pnl_passg.add(btn_dialg, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, -1, 0, 2), 0, 0));
        pnl_passg.add(btn_passg, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));

        pnl_search.setLayout(new GridBagLayout());
        lbl_search.setDisplayedMnemonic('S');
        lbl_search.setLabelFor(txt_search);
        lbl_search.setText("Search:");
        txt_search.setText("");
        txt_search.setColumns(20);
        txt_search.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                search();
            }
        });
        chk_srestrict.setSelected(false);
        chk_srestrict.setMnemonic('R');
        chk_srestrict.setText("Restrict to:");
        chk_srestrict.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                boolean selected = chk_srestrict.isSelected();
                txt_srestrict.setEnabled(selected);
            }
        });
        txt_srestrict.setEnabled(false);
        txt_srestrict.setText("Gen-Rev");
        btn_search.setText("Go");
        btn_search.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                search();
            }
        });

        pnl_search.add(lbl_search, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        pnl_search.add(txt_search, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 5), 0, 0));
        pnl_search.add(chk_srestrict, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 5, 2), 0, 0));
        pnl_search.add(txt_srestrict, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 2), 0, 0));
        pnl_search.add(btn_search, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));

        pnl_match.setLayout(new GridBagLayout());
        lbl_match.setDisplayedMnemonic('V');
        lbl_match.setLabelFor(txt_match);
        lbl_match.setText("Find Verses Like:");
        txt_match.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                match();
            }
        });
        chk_mrestrict.setText("Restrict to:");
        chk_mrestrict.setMnemonic('R');
        chk_mrestrict.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                boolean selected = chk_mrestrict.isSelected();
                txt_mrestrict.setEnabled(selected);
            }
        });
        txt_mrestrict.setText("Gen-Rev");
        txt_mrestrict.setEnabled(false);
        btn_match.setText("Go");
        btn_match.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                match();
            }
        });

        pnl_match.add(lbl_match, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 2, 2), 0, 0));
        pnl_match.add(txt_match, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 5), 0, 0));
        pnl_match.add(chk_mrestrict, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 5, 2), 0, 0));
        pnl_match.add(txt_mrestrict, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 2), 0, 0));
        pnl_match.add(btn_match, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));

        pnl_cards.setLayout(lay_cards);
        pnl_cards.add(pnl_passg, PASSAGE);
        pnl_cards.add(pnl_search, SEARCH);
        pnl_cards.add(pnl_match, MATCH);

        this.setLayout(new BorderLayout());
        this.add(pnl_select, BorderLayout.NORTH);
        this.add(pnl_cards, BorderLayout.CENTER);
    }

    /**
     * Someone pressed return in the search area
     */
    protected void search()
    {
        try
        {
            String param = txt_search.getText();
            Search search = new Search(param, false);
            if (chk_srestrict.isSelected())
            {
                Passage restrict = PassageFactory.createPassage(txt_srestrict.getText());
                search.setRange(restrict);
            }

            Bible version = (Bible) mdl_versn.getSelectedBookMetaData().getBook();
            Passage ref = version.findPassage(search);

            setPassage(ref);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone pressed return in the search area
     */
    protected void match()
    {
        try
        {
            String param = txt_match.getText();
            Search search = new Search(param, true);
            if (chk_mrestrict.isSelected())
            {
                Passage restrict = PassageFactory.createPassage(txt_mrestrict.getText());
                search.setRange(restrict);
            }

            Bible version = (Bible) mdl_versn.getSelectedBookMetaData().getBook();
            Passage ref = version.findPassage(search);

            // we get PassageTallys for best match searches
            if (ref instanceof PassageTally)
            {
                PassageTally tally = (PassageTally) ref;
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(20);
            }

            setPassage(ref);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone pressed return in the search area
     * PENDING(joe): write
     */
    protected void passage()
    {
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

        fireCommandMade(new CommandEvent(this, ref));

        rdo_passg.setSelected(true);
        txt_passg.grabFocus();
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
     * Someone clicked the "..." button
     */
    protected void select()
    {
        String passg = dlg_select.showInDialog(this, "Select Passage", true, txt_passg.getText());
        txt_passg.setText(passg);
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

    private String PASSAGE = "p";
    private String SEARCH = "s";
    private String MATCH = "m";

    private BooksComboBoxModel mdl_versn = null;
    private transient Vector commandListeners;
    private transient Vector versionListeners;
    private PassageSelectionPane dlg_select = new PassageSelectionPane();
    private JLabel lbl_passg = new JLabel();
    private JPanel pnl_passg = new JPanel();
    private JTextField txt_passg = new JTextField();
    private JComboBox cbo_versn = new JComboBox();
    private JButton btn_dialg = new JButton();
    private JPanel pnl_search = new JPanel();
    private JPanel pnl_match = new JPanel();
    private JLabel lbl_search = new JLabel();
    private JTextField txt_search = new JTextField();
    private JCheckBox chk_srestrict = new JCheckBox();
    private JTextField txt_srestrict = new JTextField();
    private JButton btn_search = new JButton();
    private JLabel lbl_match = new JLabel();
    private JTextField txt_match = new JTextField();
    private JButton btn_match = new JButton();
    private JButton btn_passg = new JButton();
    private JCheckBox chk_mrestrict = new JCheckBox();
    private JTextField txt_mrestrict = new JTextField();
    private JPanel pnl_select = new JPanel();
    private JPanel pnl_radios = new JPanel();
    private JPanel pnl_versn = new JPanel();
    private ButtonGroup grp_type = new ButtonGroup();
    private JRadioButton rdo_match = new JRadioButton();
    private JRadioButton rdo_search = new JRadioButton();
    private JRadioButton rdo_passg = new JRadioButton();
    private JPanel pnl_cards = new JPanel();
    private CardLayout lay_cards = new CardLayout();
}
