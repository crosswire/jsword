
package org.crosswire.jsword.book.sword;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Verse;

/**
 * A backend file reader
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
public class CompressedSwordCommentary extends SwordCommentary
{
    /**
     * Simple ctor
     */
    public CompressedSwordCommentary(SwordCommentaryMetaData data, SwordConfig config)
    {
        super(data, config);
        CompressedSwordCommentary.log.warn("No support for commentary type: DRIVER_Z_COM in "+config.getName()+" desire="+(++CompressedSwordCommentary.desire_zcom));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.SwordCommentary#getText(org.crosswire.jsword.passage.Verse)
     */
    public String getText(Verse verse) throws BookException
    {
        // PENDING(joe): support DRIVER_Z_COM
        return "";
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(CompressedSwordCommentary.class);

    /**
     * So we know wha the demand for this is
     */
    private static int desire_zcom = 0;
}
