
package org.crosswire.mail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.crosswire.util.Level;
import org.crosswire.util.Logger;
import org.crosswire.util.Reporter;
import org.crosswire.util.event.ReporterEvent;
import org.crosswire.util.event.ReporterListener;

/**
 * This class listens to Log captures and copies them to a
 * mail server.
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
public class MailCaptureListener implements ReporterListener
{
    /**
     * Called whenever log.capture() is passed an Exception
     * @param ev Object describing the exception
     */
    public void reportException(ReporterEvent ev)
    {
        // Create a string from the exception
        StringWriter str = new StringWriter();
        ev.getException().printStackTrace(new PrintWriter(str));
        String message = new String(str.getBuffer());

        sendMail("Exception from "+ev.getSourceName(), message);
    }

    /**
     * Called whenever log.capture() is passed an Exception
     * @param ev Object describing the exception
     */
    public void reportMessage(ReporterEvent ev)
    {
        sendMail("Message from "+ev.getSourceName(), ev.getMessage());
    }

    /**
     * Called whenever log.capture() is passed an Exception
     * @param ev Object describing the exception
     */
    public void sendMail(String subject, String body)
    {
        // create some properties and get the default Session
        Properties props = new Properties();
        props.put("mail.smtp.host", mail_server);
        props.put("mail.smtp.port", ""+mail_port);

        Session session = Session.getDefaultInstance(props, null);

        if (mail_debug)
        {
            props.put("mail.debug", "true");
            session.setDebug(mail_debug);
        }

        try
        {
            // Create the basic message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mail_fromaddr));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail_toaddr, false));
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setHeader("X-Mailer", "MailCaptureListener");
            msg.setText(body /*default charset*/);

            /*
            // Create and fill the second message part
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(message, "us-ascii");

            // Create the Multipart and its parts to it
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);

            // Add the Multipart to the message
            msg.setContent(mp);
            */

            // Send the message
            Transport.send(msg);
        }
        catch (MessagingException ex)
        {
            // log.fine("Warning failure in MailException reporting. Turning Mail reporting off.");
            //if (ex instanceof MessagingException)
            {
                printMessagingException(ex);
                throw new IllegalAccessError("Mail system failed. See system output for details.");
            }
            /*
            else
            {
                log.log(Level.INFO, "Failure during reporting", ex);
            }

            // Turn ourselves off and then throw again.
            // Is this a good idea? What if there is more than one thing that
            // handles Exceptions? Hmmmm.
            setHelpDeskListener(false);
            log.capture(this, new Exception("Mail system failed. See system output for details. Disabling Mail."));
            */
        }
    }

    /**
     * You must call getHelpDeskListener() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskListener(boolean joined)
    {
        if (joined && li == null)
        {
            li = new MailCaptureListener();
            Reporter.addReporterListener(li);
        }

        if (!joined && li != null)
        {
            Reporter.removeReporterListener(li);
            li = null;
        }
    }

    /**
     * Get the listening status
     */
    public static boolean getHelpDeskListener()
    {
        return (li != null);
    }

    /**
     * Some debug for when we get an exception
     * @param out The stream to write some debug to
     * @param ex The MessagingException to print
     */
    private static void printMessagingException(Exception ex)
    {
        log.log(Level.INFO, "Mail Exception", ex);

        if (ex instanceof SendFailedException)
        {
            SendFailedException sfex = (SendFailedException) ex;

            Address[] invalid = sfex.getInvalidAddresses();
            if (invalid != null)
            {
                log.info("Invalid Addresses:");
                for (int i=0; i<invalid.length; i++)
                    log.info("  "+invalid[i]);
            }

            Address[] unsent = sfex.getValidUnsentAddresses();
            if (unsent != null)
            {
                log.info("Unsent Addresses");
                for (int i=0; i<unsent.length; i++)
                    log.info("  "+unsent[i]);
            }

            Address[] sent = sfex.getValidSentAddresses();
            if (sent != null)
            {
                log.info("Sent Addresses");
                for (int i=0; i<sent.length; i++)
                    log.info("  "+sent[i]);
            }
        }

        if (ex instanceof MessagingException)
        {
            printMessagingException(((MessagingException) ex).getNextException());
        }
    }

    /**
     * E-Mail - the from username
     */
    public static boolean getMailDebug()
    {
        return mail_debug;
    }

    /**
     * E-Mail - the from username
     */
    public static void setMailDebug(boolean value)
    {
        mail_debug = value;
    }

    /**
     * E-Mail - the from address
     */
    public static String getMailFromAddr()
    {
        return mail_fromaddr;
    }

    /**
     * E-Mail - the from address
     */
    public static void setMailFromAddr(String value)
    {
        mail_fromaddr = value;
    }

    /**
     * E-Mail - the to address
     */
    public static String getMailToAddr()
    {
        return mail_toaddr;
    }

    /**
     * E-Mail - the to address
     */
    public static void setMailToAddr(String value)
    {
        mail_toaddr = value;
    }

    /**
     * E-Mail - the server name
     */
    public static String getMailServer()
    {
        return mail_server;
    }

    /**
     * E-Mail - the server name
     */
    public static void setMailServer(String value)
    {
        mail_server = value;
    }

    /**
     * E-Mail - the server port
     */
    public static int getMailPort()
    {
        return mail_port;
    }

    /**
     * E-Mail - the server port
     */
    public static void setMailPort(int value)
    {
        mail_port = value;
    }

    /** E-Mail - the from username */
    private static boolean mail_debug = false;

    /** E-Mail - the from address */
    private static String mail_fromaddr = "source@nowhere.com";

    /** E-Mail - the to address */
    private static String mail_toaddr = "dest@nowhere.com";

    /** E-Mail - the server name */
    private static String mail_server = "smtp";

    /** E-Mail - the server port */
    private static int mail_port = 25;

    /** The listener that pops up the ExceptionPanes */
    private static MailCaptureListener li = null;

    /** The log stream */
    protected static Logger log = Logger.getLogger(MailCaptureListener.class);
}
