
package com.eireneh.bible.view.servlet;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.*;

import com.eireneh.util.*;
import com.eireneh.config.choices.*;
import com.eireneh.config.*;

import com.eireneh.bible.util.*;
import com.eireneh.bible.book.*;
import com.eireneh.bible.control.*;

/**
* The State class takes a Cookie and maintains some state on the current
* user and their preferences.
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
* @see docs.Licence
* @author Joe Walker
*/
public class CookieState extends State
{
    /**
    * Create a State object from the data about the incomming request
    * @param request A description of the request
    * @param response Data on the reply
    */
    public CookieState(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;

        // Find the id of the person
        Cookie[] cookies = request.getCookies();
        for (int i=0; i<cookies.length; i++)
        {
            if (cookies[i].getName().equals("id"))
            {
                id = cookies[i].getValue();
                break;
            }
        }

        // New user or no ID. Create new ID
        if (id == null)
        {
            id = ""+System.currentTimeMillis();
            Cookie cooked = new Cookie("id", id);
            cooked.setMaxAge(MAX_AGE);
            response.addCookie(cooked);

            log.fine("Created config cookie id="+id+" ip="+request.getRemoteAddr());
        }

        initConfig();
        loadConfig();
    }

    /**
    * Save any changes to the Config back to disk
    */
    public void saveConfig() throws IOException
    {
        URL url = NetUtil.lengthenURL(Project.getCookiesRoot(), id+".properties");
        config.localToPermanent(url);
    }

    /**
     * Load an the config with settings. This method should not fail so
     * if for some reason there is an error we should just use some
     * defaults and carry on (posibly with a note to the Log)
     */
    public void loadConfig()
    {
        try
        {
            URL url = NetUtil.lengthenURL(Project.getCookiesRoot(), id+".properties");
            config.permanentToLocal(url);
            config.localToApplication(true);
        }
        catch (Exception ex)
        {
            // prop = new Properties();
            // Ignore they've probably just not changed their settings
        }
    }

    /** Cookie Max Age */
    public static final int MAX_AGE = 60 * 60 * 24 * 365 * 10;

    /** The http request data */
    private HttpServletRequest request;

    /** The http response data */
    private HttpServletResponse response;

    /** The log stream */
    protected static Logger log = Logger.getLogger(CookieState.class);
}
