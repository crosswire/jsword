
package org.crosswire.common.config.swing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

import org.crosswire.common.config.Config;
import org.crosswire.common.config.choices.AbstractChoice;
import org.crosswire.common.config.choices.IntegerChoice;
import org.crosswire.common.swing.config.LookAndFeelChoices;
import org.crosswire.common.swing.config.SourcePathChoice;
import org.crosswire.common.util.PropertiesUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.config.UserLevelChoice;

/**
 * Edit the Config of a remote server.
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
public class Remote
{
    /**
     * Program entry point. There is a problem here - The code to get a
     * URL to edit is project specific. Otherwise this would be main()
     * We perhaps ought to have a default package starter (that is allowed
     * to call project specific code) which gets a URL and then calls this
     * @param url The URL of a file to edit.
     */
    public void editRemoteConfig(URL url)
    {
        try
        {
            LookAndFeelChoices plaf_class = new LookAndFeelChoices();
            Config config = new Config("Remote Configuration");
            config.add("Server.Remote Host", new RemoteHostChoice());
            config.add("Server.Remote Port", new RemotePortChoice());
            config.add("Server.Username", new UsernameChoice());
            config.add("Server.Password", new PasswordChoice());
            config.add("Advanced.User Level", new UserLevelChoice());
            config.add("Advanced.Current Look", plaf_class.getCurrentChoice());
            config.add("Advanced.Source Path", new SourcePathChoice());

            // Load the defaults
            config.permanentToLocal(url);
            config.localToApplication(true);

            // Show the dialog
            SwingConfig.setDisplayClass(TabbedConfigPane.class);
            SwingConfig.showDialog(config, null, url);

            Config remote = loadRemote();
            SwingConfig.setDisplayClass(TreeConfigPane.class);
            SwingConfig.showDialog(remote, null, host, port);

            /*
            The line Config.loadRemote() used to say the more specific:
            Config remote = Choices.getEPayConfig();
            remote.permanentToLocal();
            */
        }
        catch (Exception ex)
        {
            Reporter.informUser(Remote.class, ex);
        }

        System.exit(0);
    }

    /** The host to talk to */
    protected String host = "localhost";

    /** The port number to talk on */
    protected int port = 8888;

    /**
     *
     */
    public Config loadRemote() throws IOException
    {
        Config config;
        InputStream in = null;
        Socket sock = new Socket(host, port);
        in = sock.getInputStream();

        try
        {
            ObjectInputStream sin = new ObjectInputStream(in);
            config = (Config) sin.readObject();

            Properties prop = new Properties();
            PropertiesUtil.load(prop, in);
            config.setProperties(prop);
        }
        catch (ClassNotFoundException ex)
        {
            throw new IOException("Serialization Error");
        }

        // Politeness: Send nothing to the server in return.
        PropertiesUtil.save(new Properties(), sock.getOutputStream(), "Dump");
        sock.close();

        return config;
    }

    /**
     * Default host
     */
    class RemoteHostChoice extends AbstractChoice
    {
        public void setString(String value) { host = value; }
        public String getString()           { return host; }
        public String getHelpText()         { return "The remote server to talk to."; }
    }

    /**
     * Default listen port
     */
    class RemotePortChoice extends IntegerChoice
    {
        public void setInt(int value) { port = value; }
        public int getInt()           { return port; }
        public String getHelpText()   { return "The port on the remote server to talk to."; }
    }

    /**
     * Username
     */
    class UsernameChoice extends AbstractChoice
    {
        public void setString(String value) { }
        public String getString()           { return ""; }
        public String getHelpText()         { return "The username on the remote server."; }
    }

    /**
     * Password
     */
    class PasswordChoice extends AbstractChoice
    {
        public void setString(String value) { }
        public String getString()           { return ""; }
        public String getHelpText()         { return "The password on the remote server."; }
        public String getType()             { return "password"; }
        public boolean isSaveable()         { return false; }
    }
}
