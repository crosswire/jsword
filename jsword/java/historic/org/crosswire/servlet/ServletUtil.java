
package org.crosswire.servlet;

import javax.servlet.http.*;

/**
* Some generic functions to help Servlet writing.
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
public class ServletUtil
{
    /**
    * You can create an instance of this class to do reference counting
    */
    public ServletUtil()
    {
    }

    /**
    * Called at the start of any service call. The average service method
    * (or a <code>doGet()</code> or <code>doPost()</code>) method should
    * do something like this:<pre>
    *   public void service()
    *   {
    *       sutil.startService();
    *       // stuff
    *       sutil.endService();
    *   }
    * </pre>
    *
    * If there is any lengthy processing to be done in this method then
    * something like this is a good idea:<pre>
    *       while (inProgress() && sutil.isAlive())
    *       {
    *           // lengthy stuff
    *       }
    * </pre>
    *
    * In the destroy method you should do stuff like this:<pre>
    *   public void destroy()
    *   {
    *       try
    *       {
    *           die();
    *           // shutdown;
    *       }
    *       catch (InterruptedException ex)
    *       {
    *           log(ex);
    *       }
    *   }
    * </pre>
    */
    public void startService()
    {
        counter++;
    }

    /**
    * Called at the end of any service call
    */
    public void endService()
    {
        counter--;

        synchronized (this)
        {
            notify();
        }
    }

    /**
    * Check to see if destroy has been called. Service methods should
    * call this before any lengthy operations, and back down if there
    * is a problem.
    */
    public boolean isAlive()
    {
        return !dying;
    }

    /**
    * This method first sets a flag to inform any service methods that
    * ask, that we are dying, and then waits for them all to finish
    * before returning.
    */
    public void die() throws InterruptedException
    {
        dying = true;

        while (counter > 0)
        {
            synchronized (this)
            {
                wait();
            }
        }
    }

    /** The number of instances noted */
    private int counter = 0;

    /** The number of instances noted */
    private boolean dying = false;
}
