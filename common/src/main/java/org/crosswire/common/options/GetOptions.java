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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */

package org.crosswire.common.options;

/**
 * GetOptions parses an argument list for requested arguments given by an
 * OptionList.<br/>
 * 
 * This supports short and long options:<br/>
 * Short Options have the following characteristics.
 * <ul>
 * <li>A single dash, '-', starts a flag or a flag sequence. An example of a
 * flag is '-c' and a flag sequence is '-xyz'.</li>
 * <li>A flag may have a required argument. The flag may or may not be
 * separated by a space from it's argument. For example, both -fbar and -f bar
 * are acceptable.</li>
 * <li>A flag may have an optional argument. The flag must not be separated by
 * a space from it's optional argument. For example, -fbar is acceptable
 * provides bar as the argument, but -f bar has bar as a non-option argument.</li>
 * <li>These rules can combine. For example, -xyzfoo can be the same as -x -y
 * -z foo</li>
 * <li>If an Option expects an argument, then that argument can have a leading
 * '-'. That is, if -x requires an option then the argument -y can be given as
 * -x-y or -x -y.</li>
 * </ul>
 * 
 * Long Options have the following characteristics:
 * <ul>
 * <li>A double dash '--' starts a single flag. For example --print. Note, a
 * long option is typically descriptive, but can be a single character.</li>
 * <li>An argument may be given in one of two ways --file=filename or --file
 * filename. That is, separated by an '=' sign or whitespace.</li>
 * <li>
 * <ul>
 * Note:
 * <ul>
 * <li>Options can be repeated. What that means is up to the program.</li>
 * <li>The '--' sequence terminates argument processing.</li>
 * <li>A '-' by itself is not a flag.</li>
 * <li>Unrecognized flags are an error.</li>
 * <li>Unrecognized arguments are moved after the processed flags.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class GetOptions
{
    public GetOptions(String programName, String[] args, OptionList options)
    {
        this.programName = programName;
        this.args = args;
        this.options = options;
    }

    private String     programName;
    private String[]   args;
    private OptionList options;
}
