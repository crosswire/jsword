package org.crosswire.common.config.swing;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
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
public class Msg extends MsgBase
{
    static final Msg CONFIG_SAVE_FAILED = new Msg("Could not save config file: {0}"); //$NON-NLS-1$
    static final Msg PROPERTIES = new Msg(" Properties"); //$NON-NLS-1$
    static final Msg NO = new Msg("No"); //$NON-NLS-1$
    static final Msg YES = new Msg("Yes"); //$NON-NLS-1$
    static final Msg HELP = new Msg("Help"); //$NON-NLS-1$
    static final Msg SELECT_FONT = new Msg("Select Font"); //$NON-NLS-1$
    static final Msg APPLY = new Msg("Apply"); //$NON-NLS-1$
    static final Msg CANCEL = new Msg("Cancel"); //$NON-NLS-1$
    static final Msg OK = new Msg("OK"); //$NON-NLS-1$
    static final Msg EDIT = new Msg("Edit"); //$NON-NLS-1$
    static final Msg BROWSE = new Msg("Browse"); //$NON-NLS-1$
    static final Msg UPDATE = new Msg("Update"); //$NON-NLS-1$
    static final Msg REMOVE = new Msg("Remove"); //$NON-NLS-1$
    static final Msg ADD = new Msg("Add"); //$NON-NLS-1$
    static final Msg CLASS = new Msg("Class"); //$NON-NLS-1$
    static final Msg NAME = new Msg("Name"); //$NON-NLS-1$
    static final Msg ERROR = new Msg("Error"); //$NON-NLS-1$
    static final Msg PATH_EDITOR = new Msg("Path Editor"); //$NON-NLS-1$
    static final Msg BASIC = new Msg("Basic"); //$NON-NLS-1$
    static final Msg NEXT = new Msg("Next"); //$NON-NLS-1$
    static final Msg FINISH = new Msg("Finish"); //$NON-NLS-1$
    static final Msg BACK = new Msg("Back"); //$NON-NLS-1$
    static final Msg COMPONENT_EDITOR = new Msg("Component Editor"); //$NON-NLS-1$
    static final Msg NEW_CLASS = new Msg("New Class"); //$NON-NLS-1$
    static final Msg EDIT_CLASS = new Msg("Edit Class"); //$NON-NLS-1$
    static final Msg SELECT_SUBNODE = new Msg("Select a sub-node in the tree for more options"); //$NON-NLS-1$
    static final Msg CLASS_NOT_FOUND = new Msg("A class named {0} could not be found."); //$NON-NLS-1$
    static final Msg BAD_SUPERCLASS = new Msg("The class {0} does not inherit from {1}. Instansiation failed."); //$NON-NLS-1$
    static final Msg NO_OPTIONS = new Msg("No Options Set"); //$NON-NLS-1$
    static final Msg PROPERTIES_POSN = new Msg(" Properties ({0} out of {1})"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
