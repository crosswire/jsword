package org.crosswire.jsword.book.filter.gbf;

import java.util.LinkedList;

import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

/**
 * Tag syntax: word&lt;WTxxxx>.
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
public class StrongsMorphTagBuilder implements TagBuilder
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang.String)
     */
    public Tag createTag(final String tagname)
    {
        if (!tagname.startsWith("WT")) //$NON-NLS-1$
        {
            return null;
        }

        return new Tag()
        {
            public void updateOsisStack(LinkedList stack)
            {
                String name = tagname.trim();

                Element ele = (Element) stack.get(0);
                int size = ele.getContentSize();
                if (size == 0)
                {
                    DataPolice.report("No content to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                int lastIndex = size - 1;
                Content prevObj = ele.getContent(lastIndex);
                Element word = null;

                if (prevObj instanceof Text)
                {
                    word = OSISUtil.factory().createW();
                    ele.removeContent(prevObj);
                    word.addContent(prevObj);
                    ele.addContent(word);
                }
                else if (prevObj instanceof Element)
                {
                    word = (Element) prevObj;
                }
                else
                {
                    DataPolice.report("No words to attach word to: <" + name + ">."); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                String existingMorph = word.getAttributeValue(OSISUtil.ATTRIBUTE_WORD_MORPH);
                StringBuffer newMorph = new StringBuffer();

                if (existingMorph != null && existingMorph.length() > 0)
                {
                    newMorph.append(existingMorph).append('|');
                }
                newMorph.append(OSISUtil.MORPH_STRONGS).append(name.substring(2));
                word.setAttribute(OSISUtil.ATTRIBUTE_WORD_MORPH, newMorph.toString());
            }
        };
    }
}
