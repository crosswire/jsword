package org.crosswire.jsword.book.sword;

import org.apache.commons.lang.enum.Enum;
import org.crosswire.jsword.book.BookType;

/**
 * Data about module types.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class ModuleType extends Enum
{
    public static final ModuleType RAW_TEXT = new ModuleType("RawText", "texts/rawtext", BookType.BIBLE, false);
    public static final ModuleType Z_TEXT = new ModuleType("zText", "texts/ztext", BookType.BIBLE, true);
    public static final ModuleType RAW_COM = new ModuleType("RawCom", "comments/rawcom", BookType.COMMENTARY, false);
    public static final ModuleType Z_COM = new ModuleType("zCom", "comments/zcom", BookType.COMMENTARY, true);
    public static final ModuleType HREF_COM = new ModuleType("HREFCom", "comments/hrefcom", BookType.COMMENTARY, false);
    public static final ModuleType RAW_FILES = new ModuleType("RawFiles", "comments/rawfiles", BookType.COMMENTARY, false);
    public static final ModuleType RAW_LD = new ModuleType("RawLD", "lexdict/rawld", BookType.DICTIONARY, false);
    public static final ModuleType RAW_LD4 = new ModuleType("RawLD4", "lexdict/rawld4", BookType.DICTIONARY, false);
    public static final ModuleType Z_LD = new ModuleType("zLD", "lexdict/zld", BookType.DICTIONARY, true);
    public static final ModuleType RAW_GEN_BOOK = new ModuleType("RawGenBook", "genbook/rawgenbook", null, false);

    /**
     * Find a ModuleType from a name.
     * @param name The name of the ModuleType to look up
     * @return The found ModuleType or null if the name is not found
     */
    public static ModuleType getModuleType(String name)
    {
        return (ModuleType) Enum.getEnum(ModuleType.class, name);
    }

    /**
     * Simple ctor
     */
    public ModuleType(String name, String install, BookType type, boolean compressed)
    {
        super(name);
        this.install = install;
        this.type = type;
        this.compressed = compressed;
    }

    /**
     * Strings for where the modules are installed.
     * @return Returns the install directory for this module type.
     */
    public String getInstallDirectory()
    {
        return install;
    }

    /**
     * Is this a compressed module
     */
    public boolean isCompressed()
    {
        return compressed;
    }

    /**
     * The book type of this module
     */
    public BookType getBookType()
    {
        return type;
    }

    /**
     * Where are modules of this type installed relative to the sword module
     * directory?
     */
    private String install;

    /**
     * Is this a compressed module
     */
    public final boolean compressed;

    /**
     * What booktype is this module
     */
    private BookType type;
}
