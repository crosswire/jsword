
package org.crosswire.common.swing;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.crosswire.common.util.LogUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.UserLevel;
import org.crosswire.common.util.event.ReporterEvent;
import org.crosswire.common.util.event.ReporterListener;

/**
 * A simple way of reporting problems to the user
 * This is probably too simplistic for a full-on
 * public app, but it is probably simple enough for
 * us to use here.
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
public class ExceptionPane
{
    /**
     * Show an option pane containing the exception
     * @param parent Something to attach the Dialog to
     * @param ex The Exception to display
     */
    public static void showExceptionDialog(Component comp, Throwable ex)
    {
        String message = LogUtil.getHTMLDescription(ex);

        String[] options;
        if (UserLevel.getGlobalUserLevel() == UserLevel.ADVANCED)
        {
            options = new String[] { "Ok", "More Info ..." };
        }
        else
        {
            options = new String[] { "Ok" };
        }

        int choice = JOptionPane.showOptionDialog(comp, "<html><font size=\"-1\">An error has occured:</font> "+message, "Error",
                                                  JOptionPane.DEFAULT_OPTION,
                                                  JOptionPane.ERROR_MESSAGE,
                                                  null, options, options[0]);

        if (choice == 1)
            DetailedExceptionPane.showExceptionDialog(comp, ex);
    }

    /**
     * You must call setJoinHelpDesk() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskListener(boolean joined)
    {
        if (joined && li == null)
        {
            li = new CustomCaptureListener();
            Reporter.addReporterListener(li);
        }

        if (!joined && li != null)
        {
            Reporter.removeReporterListener(li);
            li = null;
        }
    }

    /**
     * You must call setJoinHelpDesk() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static boolean getHelpDeskListener()
    {
        return (li != null);
    }

    /** The listener that pops up the ExceptionPanes */
    private static CustomCaptureListener li;

    /**
     * The ExceptionPane instance that we add to the Log
     */
    static class CustomCaptureListener implements ReporterListener
    {
        /**
         * Called whenever Reporter.informUser() is passed an Exception
         * @param ev The event describing the Exception
         */
        public void reportException(final ReporterEvent ev)
        {
            // This faf is to ensure that we don't break any SwingThread rules
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    if (ev.getSource() instanceof Component)
                    {
                        ExceptionPane.showExceptionDialog((Component) ev.getSource(),
                                                          ev.getException());
                    }
                    else
                    {
                        ExceptionPane.showExceptionDialog(null, ev.getException());
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
            // This faf is to ensure that we don't break any SwingThread rules
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    if (ev.getSource() instanceof Component)
                    {
                        JOptionPane.showMessageDialog((Component) ev.getSource(),
                                                      ev.getMessage());
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, ev.getMessage());
                    }
                }
            });
        }
    }
}
