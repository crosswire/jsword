
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.Verifier;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;

/**
 * A ComparePane allows you to compare 2 differing version of the Bible
 * verse, by verse.
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
public class ComparePane extends EirPanel
{
    /**
     * Basic Constructor
     */
    public ComparePane()
    {
        jbInit();
    }

    /**
     * Generate the GUI
     */
    private void jbInit()
    {
        cbo_bible1.setModel(mdl_bibles1);
        cbo_bible2.setModel(mdl_bibles2);
        pnl_bibles.setLayout(new BoxLayout(pnl_bibles, BoxLayout.Y_AXIS));
        pnl_bibles.setAlignmentX((float) 0.5);
        pnl_bibles.setBorder(new TitledBorder("Bibles To Compare"));
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { compare(); }
        });
        pnl_bibles.add(cbo_bible1, null);
        pnl_bibles.add(Box.createVerticalStrut(5), null);
        pnl_bibles.add(cbo_bible2, null);

        txt_verses.setText(PassageFactory.getWholeBiblePassage().toString());
        lbl_verses.setText("Verses: ");
        lbl_verses.setDisplayedMnemonic('V');
        lbl_verses.setLabelFor(txt_verses);
        pnl_verses.setLayout(new BorderLayout());
        pnl_verses.add(lbl_verses, BorderLayout.WEST);
        pnl_verses.add(txt_verses, BorderLayout.CENTER);
        txt_words.setToolTipText("[empty] - test no words; * - test all words, text - test all words starting with 'text'");
        lbl_words.setText("Words:  ");
        lbl_words.setDisplayedMnemonic('W');
        lbl_words.setLabelFor(txt_words);
        pnl_words.setLayout(new BorderLayout());
        pnl_words.add(lbl_words, BorderLayout.WEST);
        pnl_words.add(txt_words, BorderLayout.CENTER);
        pnl_using.setBorder(new TitledBorder("Compare Using"));
        pnl_using.setLayout(new BoxLayout(pnl_using, BoxLayout.Y_AXIS));
        pnl_using.add(pnl_verses, null);
        pnl_using.add(pnl_words, null);

        btn_go.setMnemonic('C');
        btn_go.setText("Compare");
        pnl_buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnl_buttons.add(btn_go, null);

        box_top = Box.createVerticalBox();
        box_top.add(pnl_bibles, null);
        box_top.add(pnl_using, null);
        box_top.add(pnl_buttons, null);

        this.setLayout(new BorderLayout());
        this.add(box_top, BorderLayout.NORTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Bible Compare", false);
    }

    /**
     * Actually preform the comparison.
     */
    private void compare()
    {
        Bible bible1 = mdl_bibles1.getSelectedBible();
        Bible bible2 = mdl_bibles2.getSelectedBible();

        if (bible1.equals(bible2))
        {
            if (JOptionPane.showConfirmDialog(this,
                "You are attempting to compare 2 Bibles that are identical.\n" +
                "Do you want to continue?",
                "Compare Identical Bibles?",
                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        BookMetaData version1 = bible1.getMetaData();
        BookMetaData version2 = bible2.getMetaData();

        if (!version1.equals(version2))
        {
            if (JOptionPane.showConfirmDialog(this,
                "You are attempting to compare 2 Bibles that are of different versions.\n" +
                bible1.getMetaData().getName() + " is a " + version1 + "\n" +
                bible2.getMetaData().getName() + " is a " + version2 + "\n" +
                "Do you want to continue?",
                "Compare Differing Versions?",
                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        try
        {
            String words = txt_words.getText();
            String ref_text = txt_verses.getText();
            Passage ref = PassageFactory.createPassage(ref_text);

            words = words.trim();
            if (words.equals("*")) words = "";
            if (words.equals(""))  words = null;

            Verifier ver = new Verifier(bible1, bible2);

            CompareResultsPane results = new CompareResultsPane(ver);
            results.setCheckText(words);
            results.setCheckPassages(ref);
            results.showInFrame(GuiUtil.getFrame(this));
            results.startStop();
        }
        catch (NoSuchVerseException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The first Bible selection combo
     */
    private BiblesComboBoxModel mdl_bibles1 = new BiblesComboBoxModel();

    /**
     * The second Bible selection combo
     */
    private BiblesComboBoxModel mdl_bibles2 = new BiblesComboBoxModel();

    /* GUI Components */
    private Box box_top;
    private JPanel pnl_bibles = new JPanel();
    private JPanel pnl_using = new JPanel();
    private JPanel pnl_verses = new JPanel();
    private JLabel lbl_verses = new JLabel();
    private JTextField txt_verses = new JTextField();
    private JPanel pnl_words = new JPanel();
    private JLabel lbl_words = new JLabel();
    private JTextField txt_words = new JTextField();
    private JComboBox cbo_bible1 = new JComboBox();
    private JComboBox cbo_bible2 = new JComboBox();
    private JPanel pnl_buttons = new JPanel();
    private JButton btn_go = new JButton();
}
