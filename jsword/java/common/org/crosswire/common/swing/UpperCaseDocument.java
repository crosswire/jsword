
package org.crosswire.common.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
* An upper case document simply extends document to make all
* the text entered upper case according to Character.toUpperCase.
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
* @see java.lang.Character#toUpperCase(char)
* @author Joe Walker
*/
public class UpperCaseDocument extends PlainDocument
{
    /**
    * Override insertString to force upper case
    */
    public void insertString(int offs, String str, AttributeSet att) throws BadLocationException
    {
        if (str == null) return;
        char[] upper = str.toCharArray();

        for (int i = 0; i < upper.length; i++)
        {
            upper[i] = Character.toUpperCase(upper[i]);
        }

        super.insertString(offs, new String(upper), att);
    }
}
