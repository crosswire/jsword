
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
* A quick Swing Bible display pane.
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
* @version D5.I2.T0
*/
public class Display extends JPanel
{
    /**
    * Create a basic swing display
    */
    public Display()
    {
        jbInit();
    }

    /**
    * Initialize the gui components
    */
    private void jbInit()
    {
        cbo_type.addItem("View");
        cbo_type.addItem("Match");
        cbo_type.addItem("Search");
        cbo_type.addItem("Help");
        btn_go.setText("GO");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                view();
            }
        });
        txt_query.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                if (ev.getKeyCode() == KeyEvent.VK_ENTER)
                    view();
            }
        });

        pnl_top.setLayout(new BorderLayout(5, 0));
        pnl_top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnl_top.add(btn_go, BorderLayout.EAST);
        pnl_top.add(txt_query, BorderLayout.CENTER);
        pnl_top.add(cbo_type, BorderLayout.WEST);

        scr_results.getViewport().add(txt_results, null);

        this.setLayout(new BorderLayout());
        this.add(pnl_top, BorderLayout.NORTH);
        this.add(scr_results, BorderLayout.CENTER);
    }

    /**
    * When someone clicks on the GO button
    */
    public void view()
    {

    }

    /** The top panel */
    JPanel pnl_top = new JPanel();

    /** The results scroller */
    JScrollPane scr_results = new JScrollPane();

    /** The query chooser */
    JComboBox cbo_type = new JComboBox();

    /** The query entry box */
    JTextField txt_query = new JTextField();

    /** The GO button */
    JButton btn_go = new JButton();

    /** The results */
    JTextArea txt_results = new JTextArea();
}
