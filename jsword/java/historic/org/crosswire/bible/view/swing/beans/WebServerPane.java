
package com.eireneh.bible.view.swing.beans;

import java.awt.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

// import org.apache.tomcat.shell.*;

import com.eireneh.util.*;
import com.eireneh.swing.*;
import com.eireneh.bible.util.Project;

/**
 * A Simple pane that contains the Apache Java web server for testing
 * purposes
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
 */
public class WebServerPane extends EirPanel
{
    /**
     * Basic Constructor
     */
    public WebServerPane()
    {
        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        chk_started.setMnemonic('W');
        chk_started.setText("Web Server Running");
        chk_started.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev) { startStop(); }
        });

        lay_state.setAlignment(FlowLayout.LEFT);
        pnl_state.setLayout(lay_state);
        pnl_state.setBorder(new TitledBorder("Web Server State"));
        pnl_state.add(chk_started, null);

        txt_results.setColumns(30);
        txt_results.setRows(10);
        scr_results.getViewport().add(txt_results, null);

        this.setLayout(new BorderLayout());
        this.add(scr_results, BorderLayout.CENTER);
        this.add(pnl_state, BorderLayout.NORTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Web Server", false);
    }

    /**
     * When someone toggles the state of the internal web server
     */
    private void startStop()
    {
        // if we are not started but should be
        if (chk_started.isSelected() && work == null)
        {
            work = new Thread(new Runnable() {
                public void run()
                {
                    try
                    {
                        URL url = NetUtil.lengthenURL(Project.getConfigRoot(), "server.xml");
                        String file = url.getFile().replace('\\', '/');

                        throw new Exception("Since tomcat 3.2 changed the embedded start system, this has been broken");
                        /*
                        Startup start = new Startup();
                        start.configure(new String[] { "-config", file });
                        */
                    }
                    catch (Exception ex)
                    {
                        Reporter.informUser(this, ex);
                    }
                }
            });
            work.start();
        }

        // if we are started but shouldn't be
        if (!chk_started.isSelected() && work != null)
        {
            /*
            Shutdown.main(new String[0]);
            work = null;
            */
        }
    }

    /** The web server thread */
    private Thread work = null;

    /* GUI Components */
    private JScrollPane scr_results = new JScrollPane();
    private JTextArea txt_results = new JTextArea();
    private JPanel pnl_state = new JPanel();
    private JCheckBox chk_started = new JCheckBox();
    private FlowLayout lay_state = new FlowLayout();
}
