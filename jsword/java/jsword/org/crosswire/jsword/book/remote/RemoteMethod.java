
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RemoteMethod is a simple way to encapsulate the name and parameters of a
 * remote method call.
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
public class RemoteMethod
{
    /**
     * Constructor RemoteMethod.
     * @param string
     */
    public RemoteMethod(String methodname)
    {
        this.methodname = methodname;
    }

    /**
     * Accessor for the method name
     * @return String
     */
    public String getMethodName()
    {
        return methodname;
    }

    /**
     * Accessor for a parameter
     */
    public String getParameter(String key)
    {
        return (String) params.get(key);
    }

    /**
     * Accessor for the keys
     */
    public Iterator getParameterKeys()
    {
        return params.keySet().iterator();
    }

    /**
     * Add a parameter to this method call.
     * The slightly unorthodox return allows us to chain calls to addParam()
     * one behind the other,
     * @param name The parameter name
     * @param value The value to assign to this parameter
     * @return this to allow addParam chains
     */
    public RemoteMethod addParam(String name, String value)
    {
        params.put(name, value);
        return this;
    }

    /**
     * Remove all the parameters
     */
    public void clearParams()
    {
        params = new HashMap();
    }

    /**
     * The parameters
     */
    private Map params = new HashMap();

    /**
     * The method name
     */
    private String methodname;
}
