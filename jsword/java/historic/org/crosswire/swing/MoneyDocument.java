
package org.crosswire.swing;

import javax.swing.*;
import javax.swing.text.*;

/**
* A money document simply extends document to refuse all non-financial
* data entered. We do not currently do any decimal place checking.
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
* @author Joe Walker
*/
public class MoneyDocument extends PlainDocument
{
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {
        if (str == null) return;

        String current = getText(0, getLength());
        boolean has_dot = current.indexOf('.') == -1 ? false : true;

        char[] addition = str.toCharArray();
        StringBuffer clear = new StringBuffer();

        for (int i = 0; i<addition.length; i++)
        {
            if (Character.isDigit(addition[i])) clear.append(addition[i]);
            if (addition[i] == '.' && !has_dot) clear.append(addition[i]);
        }

        super.insertString(offs, clear.toString(), a);

        /* TODO: Some other time
        String after = getText(0, getLength());
        int dot_pos = after.indexOf('.');

        if (dot_pos != -1)
        {
            // Ensure there are 2 digits after the .
            after = after + "00";
            after = after.substring(0, dot_pos + 2);

            // Ensure there is something before it.
            if (dot_pos == 0) after = "0" + after;
        }
        */
    }
}
