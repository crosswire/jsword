/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.File;
import java.net.URI;

/**
 * Types of Operating Systems for which specialized behavior is needed.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum OSType {
    MAC  ("Mac") {
        @Override
        public URI getUserArea() {
            return NetUtil.lengthenURI(getUserHome(), MAC_USER_DATA_AREA);
        }

        @Override
        public URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName) {
            return NetUtil.lengthenURI(getUserArea(), visibleFolderName);
        }
    },

    WIN32  ("Win") {
        @Override
        public URI getUserArea() {
            return NetUtil.lengthenURI(getUserHome(), WIN32_USER_DATA_AREA);
        }

        @Override
        public URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName) {
            return NetUtil.lengthenURI(getUserArea(), visibleFolderName);
        }
    },

    DEFAULT ("*nix") {
        @Override
        public URI getUserArea() {
            return getUserHome();
        }

        @Override
        public URI getUserAreaFolder(String hiddenFolderName, String visibleFolderName) {
            return NetUtil.lengthenURI(getUserArea(), hiddenFolderName);
        }
    };

    /**
     * Simple ctor
     * 
     * @param name the name of the OS
     */
    OSType(String name) {
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

    /**
     * @return the URI of the user home
     */
    public static URI getUserHome() {
        return NetUtil.getURI(new File(System.getProperty("user.home")));
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
     * Lookup method to convert from a String to an OSType
     * 
     * @param name the name of an OSType
     * @return the OSType or DEFAULT
     */
    public static OSType fromString(String name) {
        for (OSType v : values()) {
            if (name.startsWith(v.name)) {
                return v;
            }
        }

        return DEFAULT;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * The name of the type
     */
    private String name;

    /**
     * The Windows user settings parent directory
     */
    private static final String WIN32_USER_DATA_AREA = "Application Data";

    /**
     * The Mac user settings parent directory
     */
    private static final String MAC_USER_DATA_AREA = "Library/Application Support";

    /**
     * The machine's osType
     */
    private static OSType osType = fromString(System.getProperty("os.name"));
}
