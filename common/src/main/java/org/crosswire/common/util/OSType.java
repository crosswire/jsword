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
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

/**
 * Types of Operating Systems for which specialized behavior is needed.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class OSType implements Serializable {
    public static final OSType MAC = new OSType("Mac") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.crosswire.jsword.util.OSType#getUserArea()
         */
        public URI getUserArea() {
            return NetUtil.lengthenURI(getUserHome(), MAC_USER_DATA_AREA);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.util.OSType#getUserAreaFolder(java.lang.String,
         * java.lang.String)
         */
        public URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName) {
            return NetUtil.lengthenURI(getUserArea(), visibleFolderName);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -1575982665011980783L;
    };

    public static final OSType WIN32 = new OSType("Win") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.crosswire.jsword.util.OSType#getUserArea()
         */
        public URI getUserArea() {
            return NetUtil.lengthenURI(getUserHome(), WIN32_USER_DATA_AREA);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.util.OSType#getUserAreaFolder(java.lang.String,
         * java.lang.String)
         */
        public URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName) {
            return NetUtil.lengthenURI(getUserArea(), visibleFolderName);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 2448098399487879399L;
    };

    public static final OSType DEFAULT = new OSType("*nix") //$NON-NLS-1$
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.crosswire.jsword.util.OSType#getUserArea()
         */
        public URI getUserArea() {
            return getUserHome();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.jsword.util.OSType#getUserAreaFolder(java.lang.String,
         * java.lang.String)
         */
        public URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName) {
            return NetUtil.lengthenURI(getUserArea(), hiddenFolderName);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 8260119208395182688L;
    };

    /**
     * Simple ctor
     */
    public OSType(String name) {
        this.name = name;
    }

    /**
     * Get the user area for this OSType.
     * 
     * @return the user area
     */
    public abstract URI getUserArea();

    /**
     * A folder in the user area. This osType will determine which to use in
     * constructing the URI to the folder.
     * 
     * @param hiddenFolderName
     *            is typically a "unix" hidden folder name such as .jsword.
     * @param visibleFolderName
     *            is an visible folder name, such as JSword.
     * 
     * @return the user area folder
     */
    public abstract URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName);

    public static URI getUserHome() {
        return NetUtil.getURI(new File(System.getProperty("user.home"))); //$NON-NLS-1$;
    }

    /**
     * Get an integer representation for this CaseType
     */
    public int toInteger() {
        for (int i = 0; i < VALUES.length; i++) {
            if (equals(VALUES[i])) {
                return i;
            }
        }
        // cannot get here
        assert false;
        return -1;
    }

    /**
     * Get the machine's OSType.
     * 
     * @return the machine's OSType
     */
    public static OSType getOSType() {
        return osType;
    }

    /**
     * Lookup method to convert from a String
     */
    public static OSType fromString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            OSType o = VALUES[i];
            if (name.startsWith(o.name)) {
                return o;
            }
        }
        return DEFAULT;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static OSType fromInteger(int i) {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object
     * methods
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object
     * methods
     * 
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * The name of the type
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve() {
        return VALUES[obj];
    }

    private static final OSType[] VALUES = {
            MAC, WIN32, DEFAULT,
    };

    /**
     * The Windows user settings parent directory
     */
    private static final String WIN32_USER_DATA_AREA = "Application Data"; //$NON-NLS-1$

    /**
     * The Mac user settings parent directory
     */
    private static final String MAC_USER_DATA_AREA = "Library/Application Support"; //$NON-NLS-1$

    /**
     * The machine's osType
     */
    private static OSType osType = fromString(System.getProperty("os.name")); //$NON-NLS-1$

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -3196320305857293885L;
}
