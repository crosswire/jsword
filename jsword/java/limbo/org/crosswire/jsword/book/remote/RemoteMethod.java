/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RemoteMethod is a simple way to encapsulate the name and parameters of a
 * remote method call.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RemoteMethod
{
    /**
     * Constructor RemoteMethod.
     */
    public RemoteMethod(MethodName methodname)
    {
        this.methodname = methodname;
    }

    /**
     * Accessor for the method name
     * @return String
     */
    public MethodName getMethodName()
    {
        return methodname;
    }

    /**
     * Accessor for a parameter
     */
    public String getParameter(ParamName key)
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
    public RemoteMethod addParam(ParamName name, String value)
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
    private MethodName methodname;
}
