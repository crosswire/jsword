
package org.crosswire.jsword.util;

/**
 * UserInfo.
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
public class UserInfo
{
    /**
    * Prevent anyone instansiating us for now.
    */
    private UserInfo()
    {
    }

    /**
    * Accessor for the username
    * @return the current users name
    */
    public static String getUserName()
    {
        return user;
    }

    /**
    * Accessor for the username
    * @param user the current users name
    */
    public static void setUserName(String user)
    {
        UserInfo.user = user;
    }

    /**
    * Accessor for the organization
    * @return the current users organization
    */
    public static String getOrganization()
    {
        return org;
    }

    /**
    * Accessor for the organization
    * @param user the current users organization
    */
    public static void setOrganization(String org)
    {
        UserInfo.org = org;
    }

    /**
    * Accessor for the EMail Address
    * @return the current users EMail Address
    */
    public static String getEMailAddress()
    {
        return email;
    }

    /**
    * Accessor for the EMail Address
    * @param user the current users EMail Address
    */
    public static void setEMailAddress(String email)
    {
        UserInfo.email = email;
    }

    /** The User name */
    private static String user = "";

    /** The Organization */
    private static String org = "";

    /** The EMail */
    private static String email = "";
}
