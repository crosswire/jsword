
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.crosswire.common.swing.ComponentAbstractAction;
import org.crosswire.jsword.util.Project;

/**
 * A Simple splash screen.
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
public class Splash extends JWindow
{
    /**
     * Create a splash window
     */
    public Splash(Component comp, int wait)
    {
        super(JOptionPane.getFrameForComponent(comp));
        this.wait = wait;

        jbInit();

        new Thread(new CloseRunnable()).start();
    }

    /**
     * init the graphics
     */
    private void jbInit()
    {
        URL url = getClass().getResource("/org/crosswire/jsword/view/resource/splash.png");
        if (url != null)
        {
            icon = new ImageIcon(url);
        }

        lbl_picture.setBackground(Color.black);
        lbl_picture.setOpaque(true);
        lbl_picture.setIcon(icon);
        lbl_picture.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));

        lbl_info.setBackground(Color.black);
        lbl_info.setFont(new java.awt.Font("SansSerif", 1, 14));
        lbl_info.setForeground(UIManager.getColor("ScrollBar.thumbHighlight"));
        lbl_info.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        lbl_info.setOpaque(true);
        lbl_info.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl_info.setText("Version "+Project.getVersion());

        prg_info.setString("Loading ...");
        prg_info.setStringPainted(true);

        pnl_info.setLayout(new BorderLayout(5,0));
        pnl_info.setBackground(Color.black);
        pnl_info.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        pnl_info.add(lbl_info, BorderLayout.NORTH);
        pnl_info.add(prg_info, BorderLayout.CENTER);

        this.getContentPane().add(pnl_info, BorderLayout.SOUTH);
        this.getContentPane().add(lbl_picture, BorderLayout.CENTER);
        this.pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window = lbl_picture.getPreferredSize();
        this.setLocation(screen.width/2 - (window.width/2), screen.height/2 - (window.height/2));

        this.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent ev) { setVisible(false); dispose(); }
        });
        this.setVisible(true);
    }

    /**
     * set progress bar
     */
    public void setProgress(final int percent, final String message)
    {
        try
        {
            Runnable setter = new Runnable()
            {
                public void run()
                {
                    prg_info.setValue(percent);
                    prg_info.setString(message);
                }
            };

            if (SwingUtilities.isEventDispatchThread())
            {
                setter.run();
            }
            else
            {
                SwingUtilities.invokeAndWait(setter);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Create an 'open' Action
     */
    public static Action createOpenAction(Component parent)
    {
        return new OpenAction(parent);
    }

    /**
     * An Action to open a new Splash
     */
    public static class OpenAction extends ComponentAbstractAction
    {
        public OpenAction(Component comp)
        {
            super(comp,
                  "About ...",
                  "/toolbarButtonGraphics/general/About16.gif",
                  "/toolbarButtonGraphics/general/About24.gif",
                  "About this program", "Display details about this program.",
                  'A', null);
        }
    
        public void actionPerformed(ActionEvent ev)
        {
            Splash splash = new Splash(getComponent(), 60000);
            splash.setProgress(100, "");
        }
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

    private final int wait;
    private Icon icon;
    private JPanel pnl_info = new JPanel();
    private JLabel lbl_picture = new JLabel();
    private JProgressBar prg_info = new JProgressBar();
    private JLabel lbl_info = new JLabel();
}
