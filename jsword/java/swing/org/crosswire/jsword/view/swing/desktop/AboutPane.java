package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.crosswire.common.progress.swing.JobsViewPane;
import org.crosswire.common.swing.ExceptionShelf;
import org.crosswire.common.swing.MapTableModel;
import org.crosswire.jsword.util.Project;

/**
 * AboutPane is a window that contains various advanced user tools in
 * one place.
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
public class AboutPane
{
    private static final String SPLASH_FONT = "SanSerif";
    
    /**
     * Basic constructor
     */
    public AboutPane(Desktop desktop)
    {
        jbInit(desktop);
    }

    /**
     * Build the GUI components
     */
    private void jbInit(Desktop desktop)
    {
        URL url = getClass().getResource(Msg.SPLASH_IMAGE.toString());
        Icon icon = null;
        if (url != null)
        {
            icon = new ImageIcon(url);
        }

        JLabel lbl_picture = new JLabel();
        lbl_picture.setIcon(icon);
        lbl_picture.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lbl_picture.setHorizontalAlignment(SwingConstants.CENTER);
        lbl_picture.setVerticalAlignment(SwingConstants.CENTER);

        JLabel lbl_info = new JLabel();
        lbl_info.setFont(new Font(SPLASH_FONT, 1, 14));
        lbl_info.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        lbl_info.setOpaque(true);
        lbl_info.setHorizontalAlignment(SwingConstants.RIGHT);
        Object[] msg = { Project.instance().getVersion() };
        lbl_info.setText(Msg.VERSION_TITLE.toString(msg));

        JTabbedPane tab_main = new JTabbedPane();

        // TODO: Turn into an ActionFactory action
        JButton btn_ok = new JButton();
        btn_ok.setText("OK");
        btn_ok.setMnemonic('O');
        btn_ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });

        JPanel pnl_buttons = new JPanel();
        pnl_buttons.add(btn_ok);

        pnl_main = new JPanel();
        pnl_main.setLayout(new BorderLayout(5, 5));
        pnl_main.add(pnl_buttons, BorderLayout.SOUTH);
        pnl_main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnl_main.add(tab_main, BorderLayout.CENTER);

        // Create and add the splash tab
        JPanel pnl_splash = new JPanel();
        pnl_splash.setLayout(new BorderLayout(5, 0));
        pnl_splash.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnl_splash.add(lbl_picture, BorderLayout.CENTER);
        pnl_splash.add(lbl_info, BorderLayout.SOUTH);
        tab_main.add(pnl_splash, Project.instance().getName());

        if (advanced)
        {
            // create and add the Exception shelf
            ExceptionShelf pnl_shelf = new ExceptionShelf();
            JPanel pnl_hshelf = new JPanel();
            pnl_hshelf.setLayout(new BorderLayout());
            pnl_hshelf.add(pnl_shelf, BorderLayout.NORTH);
            pnl_hshelf.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tab_main.add(pnl_hshelf, Msg.ERROR_TAB_TITLE.toString());

            // create and add the System Properties tab
            JTable tbl_props = new JTable();
            MapTableModel mdl_props = new MapTableModel(System.getProperties());
            tbl_props.setModel(mdl_props);

            JScrollPane scr_props = new JScrollPane();
            scr_props.setPreferredSize(new Dimension(500, 300));
            scr_props.getViewport().add(tbl_props);

            JPanel pnl_props = new JPanel();
            pnl_props.setLayout(new BorderLayout());
            pnl_props.add(scr_props, BorderLayout.CENTER);
            pnl_props.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tab_main.add(pnl_props, Msg.SYSTEM_PROPS_TAB_TITLE.toString());

            // create and add the Tasks tab
            JobsViewPane pnl_jobs = new JobsViewPane();
            tab_main.add(pnl_jobs, Msg.TASK_TAB_TITLE.toString());

            if (debugging)
            {
                // create and add the Debug tab
                //tab_main.add(pnl_logs, "Logs");
                DebugPane pnl_debug = new DebugPane(desktop);
                tab_main.add(pnl_debug, Msg.DEBUG_TAB_TITLE.toString());
            }
        }
    }

    /**
     * Close this dialog
     */
    protected void close()
    {
        if (dlg_main != null)
        {
            dlg_main.dispose();
            dlg_main = null;
        }
    }

    /**
     * A method to be exposed by our children
     * @param parent The component to which to attach the new dialog
     */
    public void showInDialog(Component parent)
    {
        dlg_main = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlg_main.getContentPane().add(pnl_main);
        Object[] msg = { Project.instance().getName() };
        dlg_main.setTitle(Msg.ABOUT_TITLE.toString(msg));
        dlg_main.setModal(true);
        dlg_main.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                close();
            }
        });
        dlg_main.pack();
        dlg_main.setLocationRelativeTo(parent);
        dlg_main.setVisible(true);
    }

    /**
     * Is the debug tab visible?
     */
    public synchronized static boolean isDebugging()
    {
        return debugging;
    }

    /**
     * Set the visibility of the debug tab?
     */
    public static void setDebugging(boolean debugging)
    {
        AboutPane.debugging = debugging;
    }

    /**
     * @return Returns whether the window should show an advanced view.
     */
    public static synchronized boolean isAdvanced()
    {
        return advanced;
    }

    /**
     * @param advanced Turn on an advanced view of the window.
     */
    public static synchronized void setAdvanced(boolean advanced)
    {
        AboutPane.advanced = advanced;
    }

    private static boolean advanced = true; // TODO: Add to config.xml
    private static boolean debugging;
    private JDialog dlg_main;
    private JPanel pnl_main;
}
