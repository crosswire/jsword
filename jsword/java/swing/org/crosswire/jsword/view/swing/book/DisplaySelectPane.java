package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class DisplaySelectPane extends JPanel
{
    /**
     * General constructor
     */
    public DisplaySelectPane()
    {
        // search() and version() rely on this returning only Bibles
        mdlVersn = new BooksComboBoxModel(BookFilters.getBibles());
        initialize();
    }

    /**
     * Initialize the GUI
     */
    private void initialize()
    {
        rdoPassg.setSelected(true);
        rdoPassg.setText("Passage Lookup");
        rdoPassg.setMnemonic('P');
        rdoPassg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                layCards.show(pnlCards, PASSAGE);
                adjustFocus();
            }
        });
        rdoMatch.setText("Match");
        rdoMatch.setMnemonic('M');
        rdoMatch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                layCards.show(pnlCards, MATCH);
                adjustFocus();
            }
        });
        rdoSearch.setMnemonic('S');
        rdoSearch.setText("Search");
        rdoSearch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                layCards.show(pnlCards, SEARCH);
                adjustFocus();
            }
        });
        pnlSelect.setLayout(new FlowLayout(FlowLayout.LEFT));
        cboVersn.setModel(mdlVersn);
        cboVersn.setRenderer(new BookListCellRenderer());
        cboVersn.setPrototypeDisplayValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        Dimension min = cboVersn.getMinimumSize();
        min.width = 100;
        cboVersn.setMinimumSize(min);
        cboVersn.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                changeVersion();
            }
        });
        pnlRadios.add(rdoPassg, null);
        pnlRadios.add(rdoSearch, null);
        pnlRadios.add(rdoMatch, null);
        pnlVersn.setLayout(new BorderLayout());
        pnlVersn.add(cboVersn, BorderLayout.SOUTH);
        pnlSelect.setLayout(new BorderLayout());
        pnlSelect.add(pnlRadios, BorderLayout.WEST);
        pnlSelect.add(pnlVersn, BorderLayout.EAST);
        grpType.add(rdoPassg);
        grpType.add(rdoSearch);
        grpType.add(rdoMatch);

        lblPassg.setDisplayedMnemonic('W');
        lblPassg.setText("View:");
        pnlPassg.setLayout(new GridBagLayout());
        txtPassg.setToolTipText("Enter a passage to display. Press CTRL+ENTER or press the ... button for a Passage selection window.");
        txtPassg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doPassageAction();
            }
        });
        txtPassg.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                if (ev.getKeyChar() == '\n' && ev.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
                {
                    showSelectDialog();
                }
            }
        });
        btnDialg.setText("...");
        btnDialg.setBorder(BorderFactory.createCompoundBorder(txtPassg.getBorder(), btnDialg.getBorder()));
        btnDialg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                showSelectDialog();
            }
        });
        btnPassg.setText("Go");
        btnPassg.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doPassageAction();
            }
        });

        pnlPassg.add(lblPassg, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, 5, 0, 2), 0, 0));
        pnlPassg.add(txtPassg, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 2, 2, -1), 0, 0));
        pnlPassg.add(btnDialg, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, -1, 0, 2), 0, 0));
        pnlPassg.add(btnPassg, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));

        pnlSearch.setLayout(new GridBagLayout());
        lblSearch.setDisplayedMnemonic('S');
        lblSearch.setLabelFor(txtSearch);
        lblSearch.setText("Search:");
        txtSearch.setText("");
        txtSearch.setColumns(20);
        txtSearch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doSearchAction();
            }
        });
        chkSRestrict.setSelected(false);
        chkSRestrict.setMnemonic('R');
        chkSRestrict.setText("Restrict to:");
        chkSRestrict.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                boolean selected = chkSRestrict.isSelected();
                txtSRestrict.setEnabled(selected);
            }
        });
        txtSRestrict.setEnabled(false);
        txtSRestrict.setText("Gen-Rev");
        txtSRestrict.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doSearchAction();
            }
        });
        btnSearch.setText("Go");
        btnSearch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doSearchAction();
            }
        });

        pnlSearch.add(lblSearch, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        pnlSearch.add(txtSearch, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 5), 0, 0));
        pnlSearch.add(chkSRestrict, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 5, 2), 0, 0));
        pnlSearch.add(txtSRestrict, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 2), 0, 0));
        pnlSearch.add(btnSearch, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));

        pnlMatch.setLayout(new GridBagLayout());
        lblMatch.setDisplayedMnemonic('V');
        lblMatch.setLabelFor(txtMatch);
        lblMatch.setText("Find Verses Like:");
        txtMatch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doMatchAction();
            }
        });
        chkMRestrict.setText("Restrict to:");
        chkMRestrict.setMnemonic('R');
        chkMRestrict.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                boolean selected = chkMRestrict.isSelected();
                txtMRestrict.setEnabled(selected);
            }
        });
        txtMRestrict.setText("Gen-Rev");
        txtMRestrict.setEnabled(false);
        txtMRestrict.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doMatchAction();
            }
        });
        btnMatch.setText("Go");
        btnMatch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doMatchAction();
            }
        });

        pnlMatch.add(lblMatch, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 2, 2), 0, 0));
        pnlMatch.add(txtMatch, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 5), 0, 0));
        pnlMatch.add(chkMRestrict, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 5, 2), 0, 0));
        pnlMatch.add(txtMRestrict, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 2), 0, 0));
        pnlMatch.add(btnMatch, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 20, 5, 20), 0, 0));

        pnlCards.setLayout(layCards);
        pnlCards.add(pnlPassg, PASSAGE);
        pnlCards.add(pnlSearch, SEARCH);
        pnlCards.add(pnlMatch, MATCH);

        this.setLayout(new BorderLayout());
        this.add(pnlSelect, BorderLayout.NORTH);
        this.add(pnlCards, BorderLayout.CENTER);
    }

    /**
     * Someone pressed return in the search area
     */
    protected void doSearchAction()
    {
        try
        {
            String param = txtSearch.getText();
            Search search = new Search(param, false);
            if (chkSRestrict.isSelected())
            {
                Passage restrict = PassageFactory.createPassage(txtSRestrict.getText());
                search.setRestriction(restrict);
            }

            Book book = mdlVersn.getSelectedBookMetaData().getBook();
            Key key = book.find(search);

            txtPassg.setText(key.getName());

            setDefaultName(param);
            updateDisplay();
            setCurrentAction(PASSAGE);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone pressed return in the search area
     */
    protected void doMatchAction()
    {
        try
        {
            String param = txtMatch.getText();
            Search search = new Search(param, true);
            if (chkMRestrict.isSelected())
            {
                Passage restrict = PassageFactory.createPassage(txtMRestrict.getText());
                search.setRestriction(restrict);
            }

            Book version = mdlVersn.getSelectedBookMetaData().getBook();
            Key key = version.find(search);

            // we get PassageTallys for best match searches
            if (key instanceof PassageTally)
            {
                PassageTally tally = (PassageTally) key;
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(20, PassageConstants.RESTRICT_NONE);
            }

            txtPassg.setText(key.getName());

            setDefaultName(param);
            updateDisplay();
            setCurrentAction(PASSAGE);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone pressed return in the passage area
     */
    protected void doPassageAction()
    {
        setDefaultName(txtPassg.getText());
        updateDisplay();
    }

    /**
     * Sync the viewed passage with the passage text box
     */
    private void updateDisplay()
    {
        try
        {
            Book bible = mdlVersn.getSelectedBookMetaData().getBook();
            Passage ref = getPassage();

            fireCommandMade(new DisplaySelectEvent(this, ref, bible));
        }
        catch (NoSuchVerseException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error finding verse", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * What is the currently displayed action?
     * @param action one of the constants PASSAGE, SEARCH or MATCH;
     */
    private void setCurrentAction(String action)
    {
        layCards.show(pnlCards, action);

        if (action == PASSAGE)
        {
            rdoPassg.setSelected(true);
        }
        else if (action == SEARCH)
        {
            rdoSearch.setSelected(true);
        }
        else if (action == MATCH)
        {
            rdoMatch.setSelected(true);
        }
        else
        {
            throw new IllegalArgumentException("action is not PASSAGE, SEARCH or MATCH");
        }
        
        adjustFocus();
    }

    /**
     * Set the focus to the right initial component
     */
    public void adjustFocus()
    {
        if (rdoPassg.isSelected())
        {
            txtPassg.grabFocus();
        }
        else if (rdoSearch.isSelected())
        {
            txtSearch.grabFocus();
        }
        else if (rdoMatch.isSelected())
        {
            txtMatch.grabFocus();
        }
    }

    /**
     * Accessor for the default name
     */
    public String getDefaultName()
    {
        return title;
    }

    /**
     * Sets the default name
     */
    public void setDefaultName(String title)
    {
        this.title = title;
    }

    /**
     * The passage string, post parsing
     */
    public Passage getPassage() throws NoSuchVerseException
    {
        String param = txtPassg.getText();
        return PassageFactory.createPassage(param);
    }

    /**
     * The passage string, post parsing
     */
    public void setPassage(Passage ref)
    {
        txtPassg.setText(ref.getName());

        doPassageAction();
    }

    /**
     * Someone changed the version combo
     */
    protected void changeVersion()
    {
        try
        {
            // This cast is safe because we asked for Bibles in the ctor
            Book book = mdlVersn.getSelectedBookMetaData().getBook();
            Passage ref = getPassage();

            fireVersionChanged(new DisplaySelectEvent(this, ref, book));
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone clicked the "..." button
     */
    protected void showSelectDialog()
    {
        String passg = dlgSelect.showInDialog(this, "Select Passage", true, txtPassg.getText());
        txtPassg.setText(passg);
        doPassageAction();
    }

    /**
     * Add a command listener
     */
    public synchronized void addCommandListener(DisplaySelectListener li)
    {
        List temp = new ArrayList(2);

        if (listeners != null)
        {
            temp.addAll(listeners);
        }

        if (!temp.contains(li))
        {
            temp.add(li);
            listeners = temp;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void removeCommandListener(DisplaySelectListener li)
    {
        if (listeners != null && listeners.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(listeners);

            temp.remove(li);
            listeners = temp;
        }
    }

    /**
     * Inform the command listeners
     */
    protected void fireCommandMade(DisplaySelectEvent ev)
    {
        if (listeners != null)
        {
            for (int i=0; i<listeners.size(); i++)
            {
                DisplaySelectListener li = (DisplaySelectListener) listeners.get(i); 
                li.passageSelected(ev);
            }
        }
    }

    /**
     * Inform the version listeners
     */
    protected void fireVersionChanged(DisplaySelectEvent ev)
    {
        if (listeners != null)
        {
            int count = listeners.size();
            for (int i = 0; i < count; i++)
            {
                ((DisplaySelectListener) listeners.get(i)).bookChosen(ev);
            }
        }
    }

    public static final String PASSAGE = "p";
    public static final String SEARCH = "s";
    public static final String MATCH = "m";

    private static int base = 1;

    private String title = "Untitled " + (base++);

    private transient List listeners;

    private BooksComboBoxModel mdlVersn = null;
    private PassageSelectionPane dlgSelect = new PassageSelectionPane();
    private JLabel lblPassg = new JLabel();
    private JPanel pnlPassg = new JPanel();
    private JTextField txtPassg = new JTextField();
    private JComboBox cboVersn = new JComboBox();
    private JButton btnDialg = new JButton();
    private JPanel pnlSearch = new JPanel();
    private JPanel pnlMatch = new JPanel();
    private JLabel lblSearch = new JLabel();
    private JTextField txtSearch = new JTextField();
    protected JCheckBox chkSRestrict = new JCheckBox();
    protected JTextField txtSRestrict = new JTextField();
    private JButton btnSearch = new JButton();
    private JLabel lblMatch = new JLabel();
    private JTextField txtMatch = new JTextField();
    private JButton btnMatch = new JButton();
    private JButton btnPassg = new JButton();
    protected JCheckBox chkMRestrict = new JCheckBox();
    protected JTextField txtMRestrict = new JTextField();
    private JPanel pnlSelect = new JPanel();
    private JPanel pnlRadios = new JPanel();
    private JPanel pnlVersn = new JPanel();
    private ButtonGroup grpType = new ButtonGroup();
    private JRadioButton rdoMatch = new JRadioButton();
    private JRadioButton rdoSearch = new JRadioButton();
    private JRadioButton rdoPassg = new JRadioButton();
    protected JPanel pnlCards = new JPanel();
    protected CardLayout layCards = new CardLayout();
}
