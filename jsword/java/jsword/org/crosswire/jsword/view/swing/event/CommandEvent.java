
package org.crosswire.jsword.view.swing.event;

import java.util.EventObject;

import org.crosswire.jsword.passage.Passage;

/**
 * A CommandEvent happens whenever a user makes a command.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @see Bible
 * @see ProgressListener
 * @author Joe Walker
 * @version $Id$
 */
public class CommandEvent extends EventObject
{
    /**
     * For when a command has been made
     * @param source The thing that started this off
     * @param param The parameter to this event
     */
    public CommandEvent(Object source, Passage ref)
    {
        super(source);

        this.ref = ref;
    }

    /**
     * Get the type of command
     * @return The type of command
     */
    public Passage getPassage()
    {
        return ref;
    }

    /**
     * The parameter to the command
     */
    private Passage ref;
}
