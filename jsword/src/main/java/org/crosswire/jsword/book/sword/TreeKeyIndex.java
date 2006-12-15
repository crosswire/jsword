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
 * ID: $Id: SwordUtil.java 1169 2006-10-19 17:48:21 -0400 (Thu, 19 Oct 2006) dmsmith $
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * TreeKeyIndex reads Sword index files that are path based.
 * Paths are of the form /a/b/c, and can be of any depth.
 * The ultimate output of a TreeKeyIndex is the offset and
 * length of a chunk of data in another file that can be read.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class TreeKeyIndex implements Activatable
{
    /**
     * Simple ctor
     * @throws BookException 
     */
    public TreeKeyIndex(SwordBookMetaData sbmd) throws BookException
    {
        bmd = sbmd;

        String path = getExpandedDataPath();

        idxFile = new File(path + EXTENSION_INDEX);
        datFile = new File(path + EXTENSION_DATA);

        if (!idxFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { idxFile.getAbsolutePath() });
        }

        if (!datFile.canRead())
        {
            throw new BookException(Msg.READ_FAIL, new Object[] { datFile.getAbsolutePath() });
        }

    }

    /**
     * @return the root TreeNode for the module.
     * @throws IOException
     */
    public TreeNode getRoot() throws IOException
    {
        return getTreeNode(getOffset(0));
    }

    /**
     * Get the parent of the TreeNode.
     * @param node the node being worked upon
     * @return the parent node
     * @throws IOException
     */
    public TreeNode getParent(TreeNode node) throws IOException
    {
        return getTreeNode(getOffset(node.getParent()));
    }

    /**
     * Get the first child of the TreeNode.
     * @param node the node being worked upon
     * @return the first child node
     * @throws IOException
     */
    public TreeNode getFirstChild(TreeNode node) throws IOException
    {
        return getTreeNode(getOffset(node.getFirstChild()));
    }

    /**
     * Get the next sibling of the TreeNode.
     * @param node the node being worked upon
     * @return the next sibling node
     * @throws IOException
     */
    public TreeNode getNextSibling(TreeNode node) throws IOException
    {
        return getTreeNode(getOffset(node.getNextSibling()));
    }

    /**
     * The idx file contains offsets into the dat file.
     * @param index the record id
     * @return an offset into the dat file
     * @throws IOException
     */
    private int getOffset(int index) throws IOException
    {
        if (index == -1)
        {
            return -1;
        }

        byte[] buffer = SwordUtil.readRAF(idxRaf, index, 4);
        return SwordUtil.decodeLittleEndian32(buffer, 0);
    }

    /**
     * Given an offset get the TreeNode from the dat file.
     * @param offset start of a TreeNode record in the dat file.
     * @return the TreeNode
     * @throws IOException
     */
    private TreeNode getTreeNode(int offset) throws IOException
    {
        TreeNode node = new TreeNode(offset);

        if (offset == -1)
        {
            return node;
        }

        byte[] buffer = SwordUtil.readRAF(datRaf, offset, 12);
        node.setParent(SwordUtil.decodeLittleEndian32(buffer, 0));
        node.setNextSibling(SwordUtil.decodeLittleEndian32(buffer, 4));
        node.setFirstChild(SwordUtil.decodeLittleEndian32(buffer, 8));

        buffer = SwordUtil.readUntilRAF(datRaf, (byte) 0);
        int size = buffer.length;
        if (buffer[size-1] == 0)
        {
            size--;
        }

        Key key = new DefaultKeyList(null, bmd.getName());
        node.setName(SwordUtil.decode(key, buffer, size, bmd.getBookCharset()));

        buffer = SwordUtil.readNextRAF(datRaf, 2);
        int userDataSize = SwordUtil.decodeLittleEndian16(buffer, 0);
        if (userDataSize > 0)
        {
            node.setUserData(SwordUtil.readNextRAF(datRaf, userDataSize));
        }

        return node;        
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            idxRaf = new RandomAccessFile(idxFile, FileUtil.MODE_READ);
            datRaf = new RandomAccessFile(datFile, FileUtil.MODE_READ);
        }
        catch (IOException ex)
        {
            log.error("failed to open files", ex); //$NON-NLS-1$
            idxRaf = null;
            datRaf = null;
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
            if (idxRaf != null)
            {
                idxRaf.close();
            }
            if (datRaf != null)
            {
                datRaf.close();
            }
        }
        catch (IOException ex)
        {
            log.error("failed to close nt files", ex); //$NON-NLS-1$
        }
        finally
        {
            idxRaf = null;
            datRaf = null;
        }
        active = false;
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

    private String getExpandedDataPath() throws BookException
    {
        URL loc = NetUtil.lengthenURL(bmd.getLibrary(), bmd.getProperty(ConfigEntryType.DATA_PATH));

        if (loc == null)
        {
            throw new BookException(Msg.MISSING_FILE);
        }

        return new File(loc.getFile()).getAbsolutePath();
    }

    private static final String EXTENSION_INDEX = ".idx"; //$NON-NLS-1$
    private static final String EXTENSION_DATA = ".dat"; //$NON-NLS-1$

    private SwordBookMetaData bmd;
    private File idxFile;
    private File datFile;
    private RandomAccessFile idxRaf;
    private RandomAccessFile datRaf;
    private boolean active;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(TreeKeyIndex.class);
}
