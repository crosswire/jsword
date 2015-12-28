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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2015
 *     The copyright to this program is held by its authors.
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.net.URI;

import org.crosswire.common.util.CWProject;
import org.crosswire.jsword.book.MetaDataLocator;

/**
 * The different levels of configuration. The order in this enum is important as it determines the precedence.
 * The higher the ordinal, the higher the precedence (i.e. override).
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum SwordMetaDataLocator implements MetaDataLocator {
    SWORD {
        @Override
        public File getReadLocation() {
            //this is not implemented, because the SWORD directory could be in two locations.
            //this should never be used as the config file is always obtainable seen as its lack
            //prevents the use of the module
            return null;
        }
        @Override
        public File getWriteLocation() {
            //this is not implemented, because the SWORD directory could be in two locations.
            //this should never be used as the config file is always obtainable seen as its lack
            //prevents the use of the module
            return null;
        }
    },
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
        return new File(parent, SwordConstants.DIR_CONF_OVERRIDE);
    }

    public abstract File getReadLocation();
    public abstract File getWriteLocation();
}
