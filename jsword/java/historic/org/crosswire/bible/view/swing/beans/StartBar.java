
package com.eireneh.bible.view.swing.beans;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.eireneh.swing.*;
import com.eireneh.util.*;
import com.eireneh.bible.view.swing.desktop.*;

/**
 * The StartBar class is a starting point to get people to functionality
 * as fast a possible. It is probably not a beginner tool, but I bet it is
 * an almost invaluable power user tool.
 * TODO: I don't like the interaction between this class and Launcher. But
 * it needs thought and I'm not working on this package much at the
 * moment.
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
 * @see docs.Licence
 * @author Joe Walker
 * @version D0.I0.T0
 */
public class StartBar extends JComponent
{
    /**
     * Basic constructor
     */
    public StartBar()
    {
        launcher = B.getLauncher();

        CustomListener action = new CustomListener();

        chooser.addKeyListener(action);
        chooser.setSelectedIndex(REFERENCE);
        input.setEditable(true);
        input.addKeyListener(action);
        go.addActionListener(action);

        setLayout(new BorderLayout());
        add("West", chooser);
        add("Center", input);
        add("East", go);
    }

    /**
     * Sets the GO button on or off.
     * I guess from using IE5 which added this option over IE4 that some
     * users don't get the press return thing.
     * @param show The desired state of the GO button
     */
    public void setGoShown(boolean show)
    {
        if (show && !this.go_shown) add("East", go);
        if (!show && this.go_shown) remove(go);

        this.go_shown = show;
    }

    /**
     * Is the GO button on or off.
     * @return The state of the GO button
     */
    public boolean getGoShown()
    {
        return go_shown;
    }

    /** The types of search available */
    private static final String[] SEARCHES =
    {
        "Question",
        "Match",
        "Passage",
        "Search",
    };

    /**
     * An search has been triggered
     */
    private void go()
    {
        try
        {
            String query = input.getText();

            switch (chooser.getSelectedIndex())
            {
            case REFERENCE:
                launcher.openPassage(query);
                break;

            case SEARCH:
                launcher.openSearch(query);
                break;

            case QUESTION:
            case MATCH:
                launcher.openMatch(query);

            default:
                throw new Exception("Illegal query type.");
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /** The question enum. Make sure this maches with SEARCHES */
    private static final int QUESTION = 0;

    /** The match enum. Make sure this maches with SEARCHES */
    private static final int MATCH = 1;

    /** The reference enum. Make sure this maches with SEARCHES */
    private static final int REFERENCE = 2;

    /** The search enum. Make sure this maches with SEARCHES */
    private static final int SEARCH = 3;

    /** The start point */
    private Launcher launcher;

    /** Is the GO button shown */
    private boolean go_shown = true;

    /** The type of search */
    private JComboBox chooser = new JComboBox(SEARCHES);

    /** The search text */
    private JTextField input = new JTextField();

    /** The go button */
    private JButton go = new JButton("GO");

    /**
     * For when someone presses return or clicks on GO
     */
    class CustomListener implements ActionListener, KeyListener
    {
        public void actionPerformed(ActionEvent ev) { go(); }
        public void keyTyped(KeyEvent ev)           { }
        public void keyPressed(KeyEvent ev)         { }
        public void keyReleased(KeyEvent ev)        { if (ev.getKeyChar() == '\n') go(); }
    }
}
