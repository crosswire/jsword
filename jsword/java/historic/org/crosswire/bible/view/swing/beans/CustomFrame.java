
package com.eireneh.bible.view.swing.beans;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.eireneh.swing.*;
import com.eireneh.bible.view.swing.desktop.*;

/**
* The CustomFrame class is a JFrame or a JInternalFrame customized for
* this program. I would like to have an 'externalize' option, but I'm
* not sure where to put it at the moment. 
* For this reason this class will not inherit from any other Frames
* (becuase that could stop it being a different sort of Frame later)
* but it will expose a very Frame like interface.
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
* @version D5.I2.T0
*/
public class CustomFrame
{
    /**
    * Basic constructor
    */
    public CustomFrame()
    {
        this.desktop = B.getDesktop();
        this.frame = new JInternalFrame("", true, true, true, true);

        frame.setBounds(10, 10, 210, 110);
        
        desktop.add(frame, JLayeredPane.PALETTE_LAYER);
    }

    /**
    * Create a titled Frame
    */
    public CustomFrame(String title)
    {
        this();
        setTitle(title);
    }

    /**
    * Returns the content pane -- the container that holds the components
    * parented by the root pane.
    * @return The Container for children
    */
    public Container getContentPane()
    {
        return frame.getContentPane();
    }

    /**
    * Set the title of this window
    * @param title The new window title
    */
    public void setTitle(String title)
    {
        frame.setTitle(title);
    }

    /**
    * Set the view status of this window
    * @param visibility true/false for the view setting
    */
    public void setVisible(boolean visibility)
    {
        frame.setVisible(visibility);
    }

    /** The window to which we are attached as an internal Frame */
    private JDesktopPane desktop;

    /** The Frame that we proxy to */
    private JInternalFrame frame;
}
