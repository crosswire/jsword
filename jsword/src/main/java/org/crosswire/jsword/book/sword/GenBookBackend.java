/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * Backend for General Books. These are currently not supported.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class GenBookBackend extends AbstractBackend
{
    /**
     * Simple ctor
     */
    public GenBookBackend(SwordBookMetaData sbmd) throws BookException
    {
        super(sbmd);
        String path = getExpandedDataPath();
        bdtFile = new File(path + EXTENSION_BDT);

        if (!bdtFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { bdtFile.getAbsolutePath() });
        }

    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            bdtRaf = new RandomAccessFile(bdtFile, FileUtil.MODE_READ);
        }
        catch (IOException ex)
        {
            log.error("failed to open files", ex); //$NON-NLS-1$
            bdtRaf = null;
        }
        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        try
        {
            if (bdtRaf != null)
            {
                bdtRaf.close();
            }
        }
        catch (IOException ex)
        {
            log.error("failed to close gen book files", ex); //$NON-NLS-1$
        }
        finally
        {
            bdtRaf = null;
        }
        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    /* @Override */
    public String getRawText(Key key) throws BookException
    {
        checkActive();
        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#readIndex()
     */
    /* @Override */
    public Key readIndex()
    {
        SwordBookMetaData bmd = getBookMetaData();
        Key reply = new DefaultKeyList(null, bmd.getName());

        return reply;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isSupported()
     */
    /* @Override */
    public boolean isSupported()
    {
        return true;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * Raw GenBook file extensions
     */
    private static final String EXTENSION_BDT = ".bdt"; //$NON-NLS-1$

    /**
     * The raw data file
     */
    private File bdtFile;

    /**
     * The random access file for the raw data
     */
    private RandomAccessFile bdtRaf;

    /**
     * Are we active
     */
    private boolean active;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(GenBookBackend.class);
}
