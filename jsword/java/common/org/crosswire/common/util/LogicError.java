
package org.crosswire.common.util;

/**
 * LogicError is an unchecked Exception that tells us something went
 * unexpectedly wrong - something that logic says cant happen.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LogicError extends RuntimeException
{
    /**
     * Logic Errors should never happen so there is no need for an error
     * message as a description describes how this happened.
     */
    public LogicError()
    {
    }

    /**
     * Logic Errors should never happen so there is no need for an error
     * message as a description describes how this happened.
     */
    public LogicError(String message)
    {
        super(message);
    }

    /**
     * Logic Errors should never happen. This passes on the presumably
     * checked Exception
     */
    public LogicError(Throwable ex)
    {
        super(ex.getMessage());
        this.ex = ex;
    }

    /** The error that caused this exception */
    private Throwable ex = null;
}
