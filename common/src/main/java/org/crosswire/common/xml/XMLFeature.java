/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: Converter.java 763 2005-07-27 23:26:43Z dmsmith $
 */
package org.crosswire.common.xml;

import java.io.Serializable;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Wraps an XML Feature. The "known" set of XML Features is found in
 * XMLFeatureSet.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class XMLFeature implements Serializable
{

    /**
     * Construct a feature for xml, setting the initial state
     * 
     * @param name
     * @param control
     * @param initialState
     */
    public XMLFeature(String name, String control, boolean initialState)
    {
        this.name = name;
        this.control = control;
        this.state = initialState;
    }

    /**
     * Construct a feature for xml, setting the initial state set to false.
     * 
     * @param name
     * @param control
     */
    public XMLFeature(String name, String control)
    {
        this(name, control, false);
    }

    /**
     * What state should the feature be set to.
     * @return the state of the feature
     */
    public boolean getState()
    {
        return state;
    }

    /**
     * Establish the state for the contol.
     * 
     * @param newState
     */
    public void setState(boolean newState)
    {
        state = newState;
    }

    public void setFeature(XMLReader parser)
    {
        try
        {
            parser.setFeature(control, state);
        }
        catch (SAXNotRecognizedException e)
        {
            System.err.println("warning: Parser does not recognize feature (" + control + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (SAXNotSupportedException e)
        {
            System.err.println("warning: Parser does not support feature (" + control + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name + ' ' + (state ? "on" : "off"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof XMLFeature))
        {
            return false;
        }

        XMLFeature that = (XMLFeature) obj;

        return this.control.equals(that.control);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return control.hashCode();
    }

    private String name;
    private String control;
    private boolean state;

    /**
     * Serialization UID
     */
    private static final long serialVersionUID = -7136500819356182709L;

}
