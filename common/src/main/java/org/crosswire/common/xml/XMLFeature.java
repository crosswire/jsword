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


/**
 * Wraps an XML Feature. The "known" set of XML Features is found in
 * XMLFeatureSet.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum XMLFeature
{
    /** Namespaces feature id */
    NAMESPACES ("http://xml.org/sax/features/namespaces"), //$NON-NLS-1$

    /** Namespace prefixes feature id */
    NAMESPACE_PREFIX ("http://xml.org/sax/features/namespace-prefixes"), //$NON-NLS-1$

    /** Validation feature id */
    VALIDATION ("http://xml.org/sax/features/validation"), //$NON-NLS-1$

    /** Schema validation feature id */
    SCHEMA_VALIDATION ("http://apache.org/xml/features/validation/schema"), //$NON-NLS-1$

    /** Schema full checking feature id */
    SCHEMA_FULL_CHECKING ("http://apache.org/xml/features/validation/schema-full-checking"), //$NON-NLS-1$

    /** Validate schema annotations feature id */
    VALIDATE_ANNOTATIONS ("http://apache.org/xml/features/validate-annotations"), //$NON-NLS-1$

    /** Dynamic validation feature id */
    DYNAMIC_VALIDATION ("http://apache.org/xml/features/validation/dynamic"), //$NON-NLS-1$

    /** Load external DTD feature id */
    LOAD_EXTERNAL_DTD ("http://apache.org/xml/features/nonvalidating/load-external-dtd"), //$NON-NLS-1$

    /** XInclude feature id */
    XINCLUDE ("http://apache.org/xml/features/xinclude"), //$NON-NLS-1$

    /** XInclude fixup base URIs feature id */
    XINCLUDE_FIXUP_BASE_URIS ("http://apache.org/xml/features/xinclude/fixup-base-uris", true), //$NON-NLS-1$

    /** XInclude fixup language feature id */
    XINCLUDE_FIXUP_LANGUAGE ("http://apache.org/xml/features/xinclude/fixup-language", true); //$NON-NLS-1$

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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return (state ? "on  " : "off ") + control; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String control;
    private boolean state;
}
