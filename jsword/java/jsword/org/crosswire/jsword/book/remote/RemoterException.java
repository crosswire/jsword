
package org.crosswire.jsword.book.remote;

/**
 * For use in Remoter calls that fail.
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
public class RemoterException extends Exception
{
    /**
     * Constructor RemoterException.
     */
    public RemoterException(String message)
    {
        super(message);
    }

    /**
     * Constructor RemoterException.
     */
    public RemoterException(String message, Class original_type)
    {
        super(message);
        this.original_type = original_type;
    }

    /**
     * Constructor RemoterException.
     */
    public RemoterException(String string, Throwable cause)
    {
        super(string);
        this.cause = cause;
    }

    /**
     * Constructor RemoterException.
     */
    public RemoterException(Throwable cause)
    {
        this.cause = cause;
    }

    /**
     * Accessor for the cause of this exception
     */
    public Throwable getCause()
    {
        return cause;
    }

    /**
     * Accessor for the original type
     */
    public Class getOriginalType()
    {
        return original_type;
    }

    /**
     * The cause of this exception
     */
    private Throwable cause;

    /**
     * The original type of that caused this
     */
    private Class original_type;
}
