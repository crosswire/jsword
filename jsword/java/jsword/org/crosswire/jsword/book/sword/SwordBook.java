package org.crosswire.jsword.book.sword;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.PassageAbstractBook;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Verse;

/**
 * SwordBook is a base class for all sword type modules.
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
public class SwordBook extends PassageAbstractBook
{
    /**
     * Simple ctor
     */
    protected SwordBook(SwordBookMetaData sbmd, Backend backend)
    {
        setBookMetaData(sbmd);
        initSearchEngine();

        this.sbmd = sbmd;
        this.backend = backend;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        super.activate(lock);

        // We don't need to activate the backend because it should be capable
        // of doing it for itself.
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        super.deactivate(lock);

        Activator.deactivate(backend);
    }

    /**
     * Read the unfiltered data for a given verse
     */
    protected String getText(Verse verse) throws BookException
    {
        String result = backend.getRawText(verse, sbmd.getModuleCharset());

        assert result != null;
        // We often get rogue characters in the source so we have to chop them
        // out. To start with we kill chars less than 32 and 255 and invalid UTF-8
        char [] buffer = result.toCharArray();
        for (int i = 0; i < buffer.length; i++)
        {
            char c = buffer[i];
            if ((c >= 0 && c < 32) || c == 255 || (c >= 127 && c <= 159))
            {
                buffer[i] = ' ';
                log.debug("Verse " + verse + " has bad character " + (int)c + " at position " + i + " in input."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
        return new String(buffer);
	}

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.PassageAbstractBook#setText(org.crosswire.jsword.passage.Verse, java.lang.String)
     */
    protected void setText(Verse verse, String text) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.PassageAbstractBook#getFilter()
     */
    protected Filter getFilter()
    {
        return sbmd.getFilter();
    }

    /**
     * To read the data from the disk
     */
    private Backend backend;

    /**
     * The Sword configuration file
     */
    private SwordBookMetaData sbmd;
    
    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(SwordBook.class);
}
