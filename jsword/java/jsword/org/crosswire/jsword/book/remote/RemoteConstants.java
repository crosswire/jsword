
package org.crosswire.jsword.book.remote;

/**
 * Some constants so that everyone can agree on the names for various methods.
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
public interface RemoteConstants
{
    public static final String METHOD_KEY = "method";
    public static final String METHOD_GETBIBLES = "getBibles";
    public static final String METHOD_GETDATA = "getData";
    public static final String METHOD_FINDPASSAGE = "findPassage";
    public static final String METHOD_STARTSWITH = "getStartsWith";
    public static final String PARAM_BIBLE = "bible";
    public static final String PARAM_PASSAGE = "passage";
    public static final String PARAM_WORD = "word";
}
