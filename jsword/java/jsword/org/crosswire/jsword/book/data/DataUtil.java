
package org.crosswire.jsword.book.data;

import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * The DataUtil class provide utility functions for the various Data classes.
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
 * @version D2.I0.T0
 */
public class DataUtil
{
    /**
    * Ensure we can not be instansiated
    */
    private DataUtil()
    {
    }

    /**
    * This is a helper method to detect an attribute of para=true in the
    * first ref node in the document. Now this code looks very badly
    * written - it is very deeply nested. 8 levels of indentation is
    * enough to give any code analysis tool a fit. However I think it is
    * such simple code, and the alternative does not actually make it any
    * simpler?...
    * @param doc The document to search
    * @return True if this ref contains a new paragraph
    */
    public static boolean isNewPara(BibleData doc)
    {
        // This should be the <bible> node
        Element bible = doc.getDocument().getRootElement();

        // Loop through the <bible> node
        Iterator sit = bible.getChildren("section").iterator();
        while (sit.hasNext())
        {
            Element section = (Element) sit.next();
            Iterator rit = section.getChildren("ref").iterator();
            while (rit.hasNext())
            {
                Element ref = (Element) rit.next();
                Iterator ait = ref.getAttributes().iterator();
                while (ait.hasNext())
                {
                    Attribute attr = (Attribute) ait.next();
                    if (attr.getName().equals("para"))
                    {
                        if (attr.getValue().equals("true"))
                            return true;
                        else
                            return false;
                    }
                }
            }
        }

        return false;
    }
}
