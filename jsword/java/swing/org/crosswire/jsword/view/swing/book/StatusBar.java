
package org.crosswire.jsword.view.swing.book;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * The status bar provides usefull info to the user as to the current
 * state of the program.
 * <p>We need to think about the stuff to put in here:<ul>
 * <li>A status message. This changes with what the user is pointing at,
 *     so is very similar to tool-tips. Although they are commonly more
 *     instructional.
 * <li>A set of panels that tell you the time/if CAPS is presses and so on
 * </ul>
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
public class StatusBar extends JComponent implements MouseListener
{
    /**
     * Create a new StatusBar
     */
    public StatusBar()
    {
        jbInit();
    }

    /**
     * Init the GUI
     */
    private void jbInit()
    {
        lbl_message.setBorder(BorderFactory.createEtchedBorder());
        lbl_message.setText(DEFAULT);

        lbl_name.setBorder(BorderFactory.createEtchedBorder());
        lbl_name.setText(" JSword ");

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.add(lbl_message, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lbl_name, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * When the mouse points at something that has registered with us
     * to be shown on the statusbar
     */
    public void mouseEntered(MouseEvent ev)
    {
        if (ev.getSource() instanceof AbstractButton)
        {
            AbstractButton button = (AbstractButton) ev.getSource();
            Action action = button.getAction();

            if (action != null)
            {
                Object value = action.getValue(Action.LONG_DESCRIPTION);

                if (value != null)
                {
                    lbl_message.setText(value.toString());
                }
            }
        }
    }

    /**
     * When the mouse no longer points at something that has registered with us
     */
    public void mouseExited(MouseEvent ev)
    {
        lbl_message.setText(DEFAULT);
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     * Ignored
     */
    public void mouseClicked(MouseEvent ev)
    {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * Ignored
     */
    public void mousePressed(MouseEvent ev)
    {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * Ignored
     */
    public void mouseReleased(MouseEvent ev)
    {
    }

    /** The default text */
    private static final String DEFAULT = "Ready ...        ";

    private JLabel lbl_message = new JLabel();
    private JLabel lbl_name = new JLabel();
}
