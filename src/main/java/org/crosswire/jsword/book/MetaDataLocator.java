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
 * © CrossWire Bible Society, 2015 - 2016
 */
package org.crosswire.jsword.book;

import java.io.File;
import java.net.URI;

import org.crosswire.common.util.CWProject;

/**
 * A MetaDataLocator allows one to define where BookMetaData for a Book may be found.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum MetaDataLocator {
    TRANSIENT {
        @Override
        public File getReadLocation() {
            return null;
        }
        @Override
        public File getWriteLocation() {
            return null;
        }
    },
    JSWORD {
        @Override
        public File getReadLocation() {
            URI[] dirs = CWProject.instance().getProjectResourceDirs();
            if (dirs.length > 1) {
                return getFile(dirs[1]);
            }
            return null;
        }
        @Override
        public File getWriteLocation() {
            URI[] dirs = CWProject.instance().getProjectResourceDirs();
            if (dirs.length > 0) {
                return getFile(dirs[0]);
            }
            return null;
        }
    },
    FRONTEND {
        @Override
        public File getReadLocation() {
            return getFile(CWProject.instance().getReadableFrontendProjectDir());
        }
        @Override
        public File getWriteLocation() {
            return getFile(CWProject.instance().getWritableFrontendProjectDir());
        }
    };

    /**
     * Safely creates the file location, or null if the parent can't exist
     *
     * @param u the parent URI
     * @return the file representing the config
     */
    protected static File getFile(URI u) {
        if (u == null) {
            return null;
        }

        final File parent = new File(u);
        final File override = new File(parent, DIR_CONF_OVERRIDE);
        override.mkdirs();
        return override;
    }

    public abstract File getReadLocation();
    public abstract File getWriteLocation();

    /**
     * The configuration directory for overrides
     */
    private static final String DIR_CONF_OVERRIDE = "jsword-mods.d";

}
