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


/**
 * Wraps an XML Feature. The "known" set of XML Features is found in
 * XMLFeatureSet.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class XMLFeature implements Serializable, Comparable
{
    /** Namespaces feature id */
    public static final XMLFeature NAMESPACES = new XMLFeature("http://xml.org/sax/features/namespaces"); //$NON-NLS-1$

    /** Namespace prefixes feature id */
    public static final XMLFeature NAMESPACE_PREFIX = new XMLFeature("http://xml.org/sax/features/namespace-prefixes"); //$NON-NLS-1$

    /** Validation feature id */
    public static final XMLFeature VALIDATION = new XMLFeature("http://xml.org/sax/features/validation"); //$NON-NLS-1$

    /** Schema validation feature id */
    public static final XMLFeature SCHEMA_VALIDATION = new XMLFeature("http://apache.org/xml/features/validation/schema"); //$NON-NLS-1$

    /** Schema full checking feature id */
    public static final XMLFeature SCHEMA_FULL_CHECKING = new XMLFeature("http://apache.org/xml/features/validation/schema-full-checking"); //$NON-NLS-1$

    /** Validate schema annotations feature id */
    public static final XMLFeature VALIDATE_ANNOTATIONS = new XMLFeature("http://apache.org/xml/features/validate-annotations"); //$NON-NLS-1$

    /** Dynamic validation feature id */
    public static final XMLFeature DYNAMIC_VALIDATION = new XMLFeature("http://apache.org/xml/features/validation/dynamic"); //$NON-NLS-1$

    /** Load external DTD feature id */
    public static final XMLFeature LOAD_EXTERNAL_DTD = new XMLFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd"); //$NON-NLS-1$

    /** XInclude feature id */
    public static final XMLFeature XINCLUDE = new XMLFeature("http://apache.org/xml/features/xinclude"); //$NON-NLS-1$

    /** XInclude fixup base URIs feature id */
    public static final XMLFeature XINCLUDE_FIXUP_BASE_URIS = new XMLFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true); //$NON-NLS-1$

    /** XInclude fixup language feature id */
    public static final XMLFeature XINCLUDE_FIXUP_LANGUAGE = new XMLFeature("http://apache.org/xml/features/xinclude/fixup-language", true); //$NON-NLS-1$

    /**
     * Construct a feature for xml, setting the initial state
     * 
     * @param control
     * @param initialState
     */
    private XMLFeature(String control, boolean initialState)
    {
        this.control = control;
        this.state = initialState;
    }

    /**
     * Construct a feature for xml, setting the initial state set to false.
     * 
     * @param control
     */
    private XMLFeature(String control)
    {
        this(control, false);
    }

    /**
     * @return the control associated with this feature
     */
    public String getControl()
    {
        return control;
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
     * Lookup method to convert from a String
     */
    public static XMLFeature fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            XMLFeature o = VALUES[i];
            if (o.control.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static XMLFeature fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return (state ? "on  " : "off ") + control; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        XMLFeature feature = (XMLFeature) o;
        return this.control.compareTo(feature.control);
    }

    private String control;
    private boolean state;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final XMLFeature[] VALUES =
    {
        NAMESPACES,
        NAMESPACE_PREFIX,
        VALIDATION,
        SCHEMA_VALIDATION,
        SCHEMA_FULL_CHECKING,
        VALIDATE_ANNOTATIONS,
        DYNAMIC_VALIDATION,
        LOAD_EXTERNAL_DTD,
        XINCLUDE,
        XINCLUDE_FIXUP_BASE_URIS,
        XINCLUDE_FIXUP_LANGUAGE
    };

    /**
     * Serialization UID
     */
    private static final long serialVersionUID = -1972881391399216524L;

}
