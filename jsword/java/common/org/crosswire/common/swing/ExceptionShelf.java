
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.crosswire.common.util.LogUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.event.ReporterEvent;
import org.crosswire.common.util.event.ReporterListener;

/**
 * This is broken.
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
public class ExceptionShelf extends JPanel
{
    /**
     *
     */
    public ExceptionShelf()
    {
        toggle.addUpActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { previousException(); }
        });
        toggle.addDownActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { nextException(); }
        });

        shelf.setLayout(card);

        setLayout(new BorderLayout());

        add(shelf, BorderLayout.CENTER);
        add(toggle, BorderLayout.EAST);

        addPanel(NO_PROBLEMS);
        shelves.addElement(this);

        setToggles();
    }

    /**
     *
     */
    public void close()
    {
        shelves.removeElement(this);
    }

    /**
     * Add an exception to the list of things that have gone wrong.
     * This method looks after the internal data structures but leaves the
     * gui work to the private addPanel() method
     */
    public void addException(Throwable ex)
    {
        // Error checking
        if (ex == null)
            throw new NullPointerException();

        // Get rid of the 'no problems' panel if it exists
        if (total == 0)
            removePanel(NO_PROBLEMS);

        // Add in the new panel
        JPanel panel = createExceptionPanel(ex);
        total++;
        exceptions.addElement(ex);
        panels.addElement(panel);
        addPanel(panel);
        setToggles();
    }

    /**
     * Remove an exception from the list of things that have gone wrong.
     * This method looks after the internal data structures but leaves the
     * gui work to the private addPanel() method
     */
    public void removeException(Throwable ex)
    {
        // Error checking
        if (ex == null)
            throw new NullPointerException();

        // Remove the old panel
        int index = exceptions.indexOf(ex);
        if (index == -1)
            return;

        removePanel((JPanel) panels.elementAt(index));
        total--;
        exceptions.removeElementAt(index);
        panels.removeElementAt(index);
        setToggles();

        // Add in the 'no problems' panel if we are empty
        if (total == 0)
        {
            addPanel(NO_PROBLEMS);
            card.first(shelf);
        }
    }

    /**
     * Show the previous Exception in the list. If there are no more
     * Exceptions to view we do nothing.
     */
    protected void previousException()
    {
        if (current < 1) return;

        current--;
        card.previous(shelf);
        // log.fine("Moving to previous. Now down to current="+current);

        setToggles();
    }

    /**
     * Show the next Exception in the list. If there are no more
     * Exceptions to view we do nothing.
     */
    protected void nextException()
    {
        if (current > (total-1)) return;

        current++;
        card.next(shelf);
        // log.fine("Moving to next. Now up to current="+current);

        setToggles();
    }

    /**
     *
     */
    private void setToggles()
    {
        toggle.setUpEnabled(current > 0);
        toggle.setDownEnabled(current < (total-1));
    }

    /**
     *
     */
    protected void reporter()
    {
        if (total == 0)
        {
            JOptionPane.showMessageDialog(this,
                    "No detected problems.",
                    "Status",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            DetailedExceptionPane.showExceptionDialog(this,
                    (Throwable) exceptions.elementAt(current));
        }
    }

    /**
     *
     */
    protected void remover()
    {
        removeException((Throwable) exceptions.elementAt(current));
    }

    /**
     *
     */
    private void addPanel(JPanel panel)
    {
        String key = ""+panel.hashCode();
        shelf.add(panel, key);

        card.last(shelf);
        current = total-1;
        // log.fine("Added panel, now current="+current+" total="+total);
    }

    /**
     *
     */
    private void removePanel(JPanel panel)
    {
        int index = panels.indexOf(panel);
        // log.fine("Removing tab at index="+index);

        if (index != -1)
        {
            if (index == current && index == 0)
            {
                nextException();
                current--;
            }

            if (index == current && index != 0)
            {
                previousException();
            }
        }

        shelf.remove(panel);
        shelf.repaint();
    }

    /**
     *
     */
    private JPanel createExceptionPanel(Throwable ex)
    {
        JPanel retcode = new JPanel();
        JButton remove = new JButton("Remove");
        JButton report = new JButton();

        if (small == null)
        {
            Font norm = report.getFont();
            small = new Font(norm.getName(), norm.getStyle(), norm.getSize()-2);
        }

        if (ex == null)
        {
            report.setText("No Problems");
            report.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            remove.setEnabled(false);
        }
        else
        {
            report.setText("<html>"+LogUtil.getHTMLDescription(ex));
            report.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
            remove.setEnabled(true);
        }

        report.setFocusPainted(false);
        report.setHorizontalAlignment(JButton.LEFT);

        remove.setFocusPainted(false);
        // remove.setBorderPainted(false);
        // remove.setBorder(BorderFactory.createEmptyBorder());
        remove.setFont(small);
        remove.setVerticalAlignment(JButton.TOP);
        remove.setVerticalAlignment(JButton.BOTTOM);
        remove.setMargin(new Insets(0, 3, 3, 3));

        report.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { reporter(); }
        });
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { remover(); }
        });

        retcode.setLayout(new BorderLayout());
        retcode.add(report, BorderLayout.CENTER);
        retcode.add(remove, BorderLayout.EAST);

        return retcode;
    }

    /** The set of panels reporting on the errors */
    private Vector panels = new Vector();

    /** The shelf scroller */
    private NudgeButton toggle = new NudgeButton();

    /** The layout for the shelf */
    private CardLayout card = new CardLayout();

    /** The card currently being displayed */
    private int current = 0;

    /** The scrolled panel to which we can add stuff */
    private JPanel shelf = new JPanel();

    /** The no problems exception */
    private final JPanel NO_PROBLEMS = createExceptionPanel(null);

    /** The current number of cards */
    private int total = 0;

    /** The set of known errors */
    private Vector exceptions = new Vector();

    /**
     * You must call setHelpDeskListener() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskListener(boolean action)
    {
        if (action && !joined)
        {
            Reporter.addReporterListener(li);
        }

        if (!action && joined)
        {
            Reporter.removeReporterListener(li);
        }

        joined = action;
    }

    /**
    * Get the listening status
    */
    public static boolean getHelpDeskListener()
    {
        return (li != null);
    }

    /** The listener that pops up the ExceptionPanes */
    private static ShelfCaptureListener li = new ShelfCaptureListener();

    /** All the ExceptionShelves that we know about */
    protected static Vector shelves = new Vector();

    /** The font for the remove button */
    private static Font small;

    /** Are we in the list of listeners */
    private static boolean joined = false;

    /**
     * A class to listen to Exceptions
     */
    static class ShelfCaptureListener implements ReporterListener
    {
        /**
         * Called whenever Reporter.informUser() is passed an Exception
         * @param ev The event describing the Exception
         */
        public void reportException(final ReporterEvent ev)
        {
            SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    Enumeration en = shelves.elements();
                    while (en.hasMoreElements())
                    {
                        ExceptionShelf es = (ExceptionShelf) en.nextElement();
                        es.addException(ev.getException());
                    }
                }
            });
        }

        /**
         * Called whenever Reporter.informUser() is passed a message
         * @param ev The event describing the message
         */
        public void reportMessage(final ReporterEvent ev)
        {
            SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    Enumeration en = shelves.elements();
                    while (en.hasMoreElements())
                    {
                        ExceptionShelf es = (ExceptionShelf) en.nextElement();
                        es.addException(new Exception(ev.getMessage()));
                    }
                }
            });
        }
    }
}
