
package org.crosswire.common.util;

//import org.crosswire.common.util.event.*;
import junit.framework.TestCase;

/**
 * JUnit Test.
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
public class HelpDeskTest extends TestCase
{
    public HelpDeskTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void test() throws Exception
    {
        /*
        boolean file = FileCaptureListener.getHelpDeskListener();
        boolean log = LogCaptureListener.getHelpDeskListener();
        boolean soi = StdOutCaptureListener.getHelpDeskInformListener();
        boolean sol = StdOutCaptureListener.getHelpDeskLogListener();
        boolean shelf = ExceptionShelf.getHelpDeskListener();
        boolean mail = MailCaptureListener.getHelpDeskListener();
        String fdest = FileCaptureListener.getDefaultOutputFilename();
        String from = MailCaptureListener.getMailFromAddr();
        String server = MailCaptureListener.getMailServer();
        String to = MailCaptureListener.getMailToAddr();

        FileCaptureListener.setHelpDeskListener(true);
        LogCaptureListener.setHelpDeskListener(true);
        StdOutCaptureListener.setHelpDeskInformListener(true);
        StdOutCaptureListener.setHelpDeskLogListener(true);
        MailCaptureListener.setHelpDeskListener(true);
        FileCaptureListener.setDefaultOutputFilename("test.log");
        from = MailCaptureListener.getMailFromAddr("joe@eireneh.com");
        server = MailCaptureListener.getMailServer("mail");
        to = MailCaptureListener.getMailToAddr("joe@eireneh.com");

        String source = "source";
        Class stat = ZTestUtil.class;

        log("HelpDesk.log*()");
        log.fine("Debug Message");
        log.info("Info Message");
        log.warning("Warn Message");
        log.warning("Error Message");
        log.severe("Emerg Message");
        log.logException(new Exception("Exception Message"));
        log.fine("Debug Message");
        log.info("Info Message");
        log.warning("Warn Message");
        log.warning("Error Message");
        log.severe("Emerg Message");
        log.logException(new Exception("Exception Message"));

        FileCaptureListener.setHelpDeskListener(file);
        LogCaptureListener.setHelpDeskListener(log);
        StdOutCaptureListener.setHelpDeskInformListener(soi);
        StdOutCaptureListener.setHelpDeskLogListener(sol);
        MailCaptureListener.setHelpDeskListener(true);
        FileCaptureListener.setDefaultOutputFilename("test.log");
        from = MailCaptureListener.getMailFromAddr("joe@eireneh.com");
        server = MailCaptureListener.getMailServer("mail");
        to = MailCaptureListener.getMailToAddr("joe@eireneh.com");

        FileCaptureListener.setDefaultOutputFilename(fdest);
        */
    }
}
