
package org.crosswire.jsword.util.config;

import org.crosswire.jsword.util.UserInfo;
import org.crosswire.common.config.choices.AbstractChoice;

/**
* The Choices for configuring the Bible package.
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
* @version D0.I0.T0
*/
public class RemoteChoices
{
    /**
    * Ensure that this class is not newed
    */
    private RemoteChoices()
    {
    }

    /**
    * The Username
    */
    public static class UsernameChoice extends AbstractChoice
    {
        public void setString(String value)         { UserInfo.setUserName(value); }
        public String getString()                   { return UserInfo.getUserName(); }
        public String getHelpText()                 { return "Your full name"; }
    }

    /**
    * The Organization
    */
    public static class OrganizationChoice extends AbstractChoice
    {
        public void setString(String value)         { UserInfo.setOrganization(value); }
        public String getString()                   { return UserInfo.getOrganization(); }
        public String getHelpText()                 { return "Your organization"; }
    }

    /**
    * The EMail Address
    */
    public static class EMailAddressChoice extends AbstractChoice
    {
        public void setString(String value)         { UserInfo.setEMailAddress(value); }
        public String getString()                   { return UserInfo.getEMailAddress(); }
        public String getHelpText()                 { return "Your E-Mail address"; }
    }
}
