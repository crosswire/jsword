
package org.crosswire.jsword.view.swing.desktop;

import java.awt.Component;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * MDI manager of how we layout views.
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
public class MDIViewLayout extends ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public MDIViewLayout(Desktop tools)
    {
        super(tools);
    }

    /**
     * Prepare any data structures needed before we are made live
     */
    public void preDisplay()
    {
        // setup
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            add(view);
        }

        // ensure we have been registered
        getDesktop().setViewComponent(mdi_main);
    }

    /**
     * Undo any data structures needed for live
     */
    public void postDisplay()
    {
        // remove the old frames
        JInternalFrame[] frames = mdi_main.getAllFrames();
        for (int i=0; i<frames.length;i++)
        {
            mdi_main.remove(frames[i]);
        }

        getDesktop().unsetViewComponent(mdi_main);
    }

    /**
     * Add a view to the set while visible
     */
    public boolean add(BibleViewPane view)
    {
        String name = view.getTitle();

        JInternalFrame iframe = new JInternalFrame(name, true, true, true, true);
        iframe.getContentPane().add(view);

        mdi_main.add(iframe/*, JLayeredPane.PALETTE_LAYER*/);

        //iframe.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        /*
        iframe.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosed(InternalFrameEvent ev)
            {
                JInternalFrame iframe = ev.getInternalFrame();
                BibleViewPane view = (BibleViewPane) iframe.getContentPane().getComponent(0);
            }
        });
        */
        iframe.setVisible(true);
        iframe.pack();

        return true;
    }

    /**
     * Remove a view from the set while visible
     */
    public boolean remove(BibleViewPane view)
    {
        JInternalFrame iframe = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, view);
        iframe.dispose();

        return true;
    }

    /**
     * Remove a view from the set while visible
     */
    public void update(BibleViewPane view)
    {
        JInternalFrame iframe = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, view);
        iframe.setTitle(view.getTitle());
    }

    /**
     * While visible, which is the current pane
     */
    public BibleViewPane getSelected()
    {
        JInternalFrame frame = mdi_main.getSelectedFrame();

        if (frame == null)
            return null;

        Component comp = frame.getContentPane().getComponent(0);
        return (BibleViewPane) comp;
    }

    private JDesktopPane mdi_main = new JDesktopPane();
}
