
package org.crosswire.mail.config;

import org.crosswire.config.choices.AbstractChoice;
import org.crosswire.config.choices.BooleanChoice;
import org.crosswire.config.choices.IntegerChoice;
import org.crosswire.mail.MailCaptureListener;

/**
 * A collection of EMail config options for if we ever do an
 * EMailCaptureListener.
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
 */
public class MailCaptureChoices
{
    /**
     * Do we capture to email?
     */
    public static class CaptureEMailChoice extends BooleanChoice
    {
        public boolean getBoolean()             { return MailCaptureListener.getHelpDeskListener(); }
        public void setBoolean(boolean value)   { MailCaptureListener.setHelpDeskListener(value); }
        public String getHelpText()             { return "Do we copy exceptions to the E-Mail system."; }
        public int priority()                   { return PRIORITY_HIGHEST; }
    }

    /**
     * EMail - the from username
     */
    public static class EMailDebugChoice extends BooleanChoice
    {
        public void setBoolean(boolean value)   { MailCaptureListener.setMailDebug(value); }
        public boolean getBoolean()             { return MailCaptureListener.getMailDebug(); }
        public String getHelpText()             { return "Do we ask the Mail system to print out debug information."; }
    }

    /**
     * EMail - the from address
     */
    public static class EMailFromAddrChoice extends AbstractChoice
    {
        public void setString(String value)     { MailCaptureListener.setMailFromAddr(value); }
        public String getString()               { return MailCaptureListener.getMailFromAddr(); }
        public String getHelpText()             { return "The address used to send E-Mail from."; }
    }

    /**
     * EMail - the to address
     */
    public static class EMailToAddrChoice extends AbstractChoice
    {
        public void setString(String value)     { MailCaptureListener.setMailToAddr(value); }
        public String getString()               { return MailCaptureListener.getMailToAddr(); }
        public String getHelpText()             { return "The address used to send E-Mail to."; }
    }

    /**
     * EMail - the server
     */
    public static class EMailServerChoice extends AbstractChoice
    {
        public void setString(String value)     { MailCaptureListener.setMailServer(value); }
        public String getString()               { return MailCaptureListener.getMailServer(); }
        public String getHelpText()             { return "The mail server DNS name."; }
    }

    /**
     * EMail - the port
     */
    public static class EMailPortChoice extends IntegerChoice
    {
        public void setInt(int value)           { MailCaptureListener.setMailPort(value); }
        public int getInt()                     { return MailCaptureListener.getMailPort(); }
        public String getHelpText()             { return "The mail server port number (usually 25)."; }
    }
}
