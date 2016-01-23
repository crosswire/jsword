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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.jsword.bridge;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.crosswire.common.util.IniSection;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.SwordBookMetaData;

/**
 * Parses the Sword conf and reports errors to the console.
 * Optionally will normalize the conf.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class ConfParser {

    public ConfParser(final String confFile) {
        conf = confFile;
    }
    public void parse() throws IOException {
        File confFile = new File(conf);
        if (!confFile.exists()) {
            System.err.println("File does not exist: " + conf);
            return;
        }
        IniSection config = new IniSection();

        // So called Latin-1 is the default, but most are UTF-8
        config.load(confFile, "UTF-8");
        String encoding = config.get("Encoding");
        if (encoding == null || "Latin-1".equalsIgnoreCase(encoding)) {
            config.clear();
            config.load(confFile, "WINDOWS-1252");
        }
        SwordBookMetaData.report(config);
        SwordBookMetaData.normalize(new PrintWriter(System.out), config, CANONICAL_ORDER);
    }

    /**
     * 
     * 
     * @param args The list of conf files to check.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            return;
        }
        ConfParser parser = new ConfParser(args[0]);
        try {
            parser.parse();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void usage() {
        System.err.println("Usage: ConfParser /full/path/to/xyz.conf");
    }

    private String conf;
    private static final String[] CANONICAL_ORDER = {
            SwordBookMetaData.KEY_ABBREVIATION, // Uncommon, but renames [xyz] for non-latin languages
            SwordBookMetaData.KEY_DESCRIPTION, // Always
            SwordBookMetaData.KEY_DATA_PATH, // Always
            BookMetaData.KEY_CATEGORY, // Non-bibles
            SwordBookMetaData.KEY_MINIMUM_VERSION, // Should be the last released version of the SWORD engine
            BookMetaData.KEY_LANG, // Should always be used
            SwordBookMetaData.KEY_DIRECTION, // Defaults to ltor, here because of Lang
            SwordBookMetaData.KEY_ENCODING, // Default is Latin-1
            SwordBookMetaData.KEY_SOURCE_TYPE, // Always
            SwordBookMetaData.KEY_MOD_DRV, // Always
            SwordBookMetaData.KEY_COMPRESS_TYPE, // When compressed
            SwordBookMetaData.KEY_BLOCK_TYPE, // When compressed
            SwordBookMetaData.KEY_BLOCK_COUNT, // Compressed Dictionary, not common
            BookMetaData.KEY_VERSIFICATION, // Bibles and Commentaries, defaults to KJV
            SwordBookMetaData.KEY_OSIS_VERSION, // Informational, for SourceType=OSIS
            SwordBookMetaData.KEY_OSIS_Q_TO_TICK, // Bibles, obsolete, not common
            SwordBookMetaData.KEY_KEY_TYPE,      // For GenBook Bibles, not used
            SwordBookMetaData.KEY_DISPLAY_LEVEL, // GenBook, not common
            SwordBookMetaData.KEY_FEATURE, // SWORD Engine
            SwordBookMetaData.KEY_GLOBAL_OPTION_FILTER, // SWORD Engine toggles
            SwordBookMetaData.KEY_SIGLUM1, // For OSISGlosses
            SwordBookMetaData.KEY_SIGLUM2,
            SwordBookMetaData.KEY_SIGLUM3,
            SwordBookMetaData.KEY_SIGLUM4,
            SwordBookMetaData.KEY_SIGLUM5,
            SwordBookMetaData.KEY_GLOSSARY_FROM, // For Glossaries
            SwordBookMetaData.KEY_GLOSSARY_TO, // For Glossaries
            BookMetaData.KEY_FONT, // Rare
            BookMetaData.KEY_SCOPE, // Suggested, present in 1
            BookMetaData.KEY_BOOKLIST, // JSword only
            SwordBookMetaData.KEY_LCSH,
            SwordBookMetaData.KEY_SWORD_VERSION_DATE, // Date of last change
            SwordBookMetaData.KEY_VERSION, // Module version
            SwordBookMetaData.KEY_HISTORY, // Module history
            SwordBookMetaData.KEY_OBSOLETES,
            // General info
            SwordBookMetaData.KEY_DISTRIBUTION_LICENSE, // Always
            SwordBookMetaData.KEY_DISTRIBUTION_SOURCE,
            SwordBookMetaData.KEY_DISTRIBUTION_NOTES,
            SwordBookMetaData.KEY_TEXT_SOURCE,
            SwordBookMetaData.KEY_SHORT_PROMO,
            SwordBookMetaData.KEY_COPYRIGHT_DATE,
            SwordBookMetaData.KEY_SHORT_COPYRIGHT,
            SwordBookMetaData.KEY_ABOUT, // Always
            SwordBookMetaData.KEY_COPYRIGHT,
            SwordBookMetaData.KEY_COPYRIGHT_HOLDER,
            SwordBookMetaData.KEY_COPYRIGHT_CONTACT_NAME,
            SwordBookMetaData.KEY_COPYRIGHT_CONTACT_ADDRESS,
            SwordBookMetaData.KEY_COPYRIGHT_CONTACT_EMAIL,
            SwordBookMetaData.KEY_COPYRIGHT_CONTACT_NOTES,
            SwordBookMetaData.KEY_COPYRIGHT_NOTES,
            // Kept last as it is maintained there by external program
            SwordBookMetaData.KEY_INSTALL_SIZE
    };

}
