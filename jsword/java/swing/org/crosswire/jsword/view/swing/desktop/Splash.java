package org.crosswire.jsword.view.swing.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.crosswire.common.progress.swing.JobsProgressBar;
import org.crosswire.jsword.util.Project;

/**
 * A Simple splash screen.
 * <p>so start one of these call:
 * <pre>
 * new Splash(getComponent(), 60000);
 * </pre>
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
public class Splash extends JWindow
{
    private static final String SPLASH_FONT = "SanSerif";
    private static final String THUMB_COLOR = "ScrollBar.thumbHighlight";

    /**
     * Create a splash window
     */
    public Splash(Component comp, int wait)
    {
        super(JOptionPane.getFrameForComponent(comp));
        this.wait = wait;

        init();

        new Thread(new CloseRunnable()).start();
    }

    /**
     * Init the graphics
     */
    private void init()
    {
        URL url = getClass().getResource(Msg.SPLASH_IMAGE.toString());
        Icon icon = null;
        if (url != null)
        {
            icon = new ImageIcon(url);
        }

        JLabel lbl_picture = new JLabel();
        lbl_picture.setBackground(Color.black);
        lbl_picture.setOpaque(true);
        lbl_picture.setIcon(icon);
        lbl_picture.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JLabel lbl_info = new JLabel();
        lbl_info.setBackground(Color.black);
        lbl_info.setFont(new Font(SPLASH_FONT, 1, 14));
        lbl_info.setForeground(UIManager.getColor(THUMB_COLOR));
        lbl_info.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        lbl_info.setOpaque(true);
        lbl_info.setHorizontalAlignment(SwingConstants.RIGHT);
        Object [] msg = { Project.instance().getVersion() };
        lbl_info.setText(Msg.VERSION_TITLE.toString(msg));

        JPanel pnl_info = new JPanel();
        JobsProgressBar pnl_jobs = new JobsProgressBar(false);
        pnl_info.setLayout(new BorderLayout(5, 0));
        pnl_info.setBackground(Color.black);
        pnl_info.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        pnl_info.add(lbl_info, BorderLayout.CENTER);
        pnl_info.add(pnl_jobs, BorderLayout.SOUTH);

        this.getContentPane().add(pnl_info, BorderLayout.SOUTH);
        this.getContentPane().add(lbl_picture, BorderLayout.CENTER);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window = lbl_picture.getPreferredSize();
        this.setLocation(screen.width/2 - (window.width/2), screen.height/2 - (window.height/2));

        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent ev)
            {
                close();
            }
        });
        this.pack();
        this.setVisible(true);
    }

    /**
     * Shut up shop
     */
    public void close()
    {
        setVisible(false);
        dispose();
    }

    /**
     * Timer to close the window
     */
    class CloseRunnable implements Runnable
    {
        public void run()
        {
            try
            {
                Thread.sleep(wait);

                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        setVisible(false);
                        dispose();
                    }
                });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    protected final int wait;
}
