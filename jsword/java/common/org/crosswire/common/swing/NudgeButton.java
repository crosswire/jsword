
package org.crosswire.common.swing;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
* A nudge button set based on this dialog -
* even down to passing on edited source.
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
*/
public class NudgeButton extends JPanel
{
    /**
    *
    */
    public NudgeButton()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(up);
        add(down);
    }

    /**
    *
    */
    public void setUpEnabled(boolean active)
    {
        up.setEnabled(active);
    }

    /**
    *
    */
    public void setDownEnabled(boolean active)
    {
        down.setEnabled(active);
    }

    /**
    *
    */
    public boolean getUpEnabled()
    {
        return up.isEnabled();
    }

    /**
    *
    */
    public boolean getDownEnabled()
    {
        return down.isEnabled();
    }

    /**
    *
    */
    public void addUpActionListener(ActionListener al)
    {
        up.addActionListener(al);
    }

    /**
    *
    */
    public void removeUpActionListener(ActionListener al)
    {
        up.removeActionListener(al);
    }

    /**
    *
    */
    public void addDownActionListener(ActionListener al)
    {
        down.addActionListener(al);
    }

    /**
    *
    */
    public void removeDownActionListener(ActionListener al)
    {
        down.removeActionListener(al);
    }

    /** The up button */
    private JButton up = new javax.swing.plaf.basic.BasicArrowButton(SwingConstants.NORTH);

    /** The down button */
    private JButton down = new javax.swing.plaf.basic.BasicArrowButton(SwingConstants.SOUTH);
}
