
package org.crosswire.common.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A numeric document simply extends document to refuse all non-numeric
 * data entered according to Character.isDigit.
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
 * @see java.lang.Character#isDigit(char)
 */
public class NumericDocument extends PlainDocument
{
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {
        if (str == null) return;

        char[] upper = str.toCharArray();
        StringBuffer clear = new StringBuffer();

        for (int i = 0; i<upper.length; i++)
        {
            if (Character.isDigit(upper[i])) clear.append(upper[i]);
        }

        super.insertString(offs, clear.toString(), a);
    }
}
