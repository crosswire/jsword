package org.crosswire.jsword.book.sword;

import org.crosswire.common.util.CWProject;

import java.io.File;
import java.net.URI;

/**
 * Captures a file with its associate Meta-level. A meta-level is defined as the place in the hierarchy of conf files.
 * A lower ordinal indicates lower precedence.
 */
public class MetaFile {
    /**
     * The different levels of configuration. The order in this enum is important as it determines the precedence.
     * The higher the ordinal, the higher the precedence (i.e. override).
     */
    public enum Level {
        SWORD {
            @Override
            public File getConfigLocation() {
                //this is not implemented, because the SWORD directory could be in two locations.
                //this should never be used as the config file is always obtainable seen as its lack
                //prevents the use of the module
                return null;
            }
        },
        JSWORD_READ {
            @Override
            public File getConfigLocation() {
                URI[] dirs = CWProject.instance().getProjectResourceDirs();
                if (dirs.length > 1) {
                    return getFile(dirs[1]);
                }
                return null;
            }
        },
        JSWORD_WRITE {
            @Override
            public File getConfigLocation() {
                URI[] dirs = CWProject.instance().getProjectResourceDirs();
                if (dirs.length > 0) {
                    return getFile(dirs[0]);
                }
                return null;
            }
        },
        FRONTEND_READ {
            @Override
            public File getConfigLocation() {
                return getFile(CWProject.instance().getReadableFrontendProjectDir());
            }
        },
        FRONTEND_WRITE {
            @Override
            public File getConfigLocation() {
                return getFile(CWProject.instance().getWriteableFrontendProjectDir());
            }
        };

        /**
         * Safely creates the file location, or null if the parent can't exist
         *
         * @param u the parent URI
         * @return the file representing the config
         */
        private static File getFile(URI u) {
            if (u == null) {
                return null;
            }

            final File parent = new File(u);
            return new File(parent, SwordConstants.DIR_CONF_OVERRIDE);
        }

        public abstract File getConfigLocation();
    }

    /**
     * Wraps around the config file and the level
     *
     * @param file  the conf file
     * @param level the level
     */
    public MetaFile(File file, Level level) {
        this.file = file;
        this.level = level;
    }

    /**
     * @return the conf file location, may not exist
     */
    public File getFile() {
        return file;
    }

    /**
     * @return the meta level, whose ordinal indicates the priority level.
     */
    public Level getLevel() {
        return level;
    }

    private File file;
    private Level level;
}
