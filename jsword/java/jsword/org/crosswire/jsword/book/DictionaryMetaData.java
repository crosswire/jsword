
package org.crosswire.jsword.book;

/**
 * A specialization of BookMetaData for working with Dictionaries.
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
public interface DictionaryMetaData extends BookMetaData
{
    /**
     * Accessor for the real Dictionary to read data.
     * <p>Note that constructing a Dictionary may well consume system resources
     * far more than the construction of a DictionaryMetaData so you should only
     * get a Dictionary if you intend to use it.
     * <p>This method is here rather than on the BookDriver because we want to
     * avoid user contact with the Drivers where possible.
     * <p>For implementors of DictionaryMetaData - the objects returned by 2
     * successive calls to getDictionary() should be the same (i.e. return true
     * to an == test) unless for some reason the objects are not thread safe.
     * Since Dictionary are read-only once setup thread safety should not be
     * hard.
     */
    public Dictionary getDictionary();
}
