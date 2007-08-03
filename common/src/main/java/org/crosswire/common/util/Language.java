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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: Languages.java 1462 2007-07-02 02:32:23Z dmsmith $
 */
package org.crosswire.common.util;

/**
 * A single language, paring an ISO-639 code to a localized representation of the language.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Language implements Comparable
{
    /**
     * A single language defined by an ISO-639 code.
     * If the code is null or empty then it is considered to be DEFAULT_LANG_CODE (that is, English).
     * 
     * @param iso639Code the particular language
     */
    public Language(String iso639Code)
    {
        this.code = iso639Code;
        if (iso639Code == null || iso639Code.length() == 0)
        {
            this.code = Languages.DEFAULT_LANG_CODE;
        }
    }
    /**
     * Determine whether this language is valid.
     * The code is valid if it is in iso639.properties.
     *
     * @return true if the language is valid.
     */
    public boolean isValidLanguage()
    {
        return Languages.isValidLanguage(code);
    }

    /**
     * Get the language code.
     *
     * @return the code for the language
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Get the language name.
     *
     * @return the name of the language
     */
    public String getName()
    {
        if (name == null)
        {
            name = Languages.getLanguage(code);
        }
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return code.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        final Language other = (Language) obj;

        return code.equals(other.code);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        return getName().compareTo(o.toString());
    }

    private String code;
    private String name;
}
