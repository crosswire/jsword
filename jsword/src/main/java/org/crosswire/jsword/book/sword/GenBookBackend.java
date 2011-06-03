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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.TreeKey;

/**
 * Backend for General Books.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class GenBookBackend extends AbstractBackend {
    /**
     * Simple ctor
     */
    public GenBookBackend(SwordBookMetaData sbmd) {
        super(sbmd);
        index = new TreeKeyIndex(sbmd);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock) {
        Activator.activate(index);

        URI path = null;
        try {
            path = getExpandedDataPath();
        } catch (BookException e) {
            Reporter.informUser(this, e);
            return;
        }

        bdtFile = new File(path.getPath() + EXTENSION_BDT);

        if (!bdtFile.canRead()) {
            // TRANSLATOR: Common error condition: The file could not be read. There can be many reasons.
            // {0} is a placeholder for the file.
            Reporter.informUser(this, new BookException(JSMsg.gettext("Error reading {0}", bdtFile.getAbsolutePath())));
            return;
        }

        try {
            bdtRaf = new RandomAccessFile(bdtFile, FileUtil.MODE_READ);
        } catch (IOException ex) {
            log.error("failed to open files", ex);
            bdtRaf = null;
        }
        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock) {
        try {
            if (bdtRaf != null) {
                bdtRaf.close();
            }
        } catch (IOException ex) {
            log.error("failed to close gen book files", ex);
        } finally {
            bdtRaf = null;
        }
        active = false;

        // Also deactivate the index
        Activator.deactivate(index);
    }

    @Override
    public boolean contains(Key key) {
        checkActive();

        try {
            DataPolice.setKey(key);
            return null != find(key);
        } catch (IOException e) {
            return false;
        } finally {
            DataPolice.setKey(null);
        }
    }

    @Override
    public String getRawText(Key key) throws BookException {
        checkActive();

        try {
            DataPolice.setKey(key);
            TreeNode node = find(key);

            if (node == null) {
                // TRANSLATOR: Error condition: Indicates that something could not be found in the book.
                // {0} is a placeholder for the unknown key.
                // {1} is the short name of the book
                throw new BookException(JSMsg.gettext("No entry for '{0}' in {1}.", key.getName(), getBookMetaData().getInitials()));
            }

            byte[] userData = node.getUserData();

            // Some entries may be empty.
            if (userData.length == 8) {
                int start = SwordUtil.decodeLittleEndian32(userData, 0);
                int size = SwordUtil.decodeLittleEndian32(userData, 4);
                byte[] data = SwordUtil.readRAF(bdtRaf, start, size);
                decipher(data);
                return SwordUtil.decode(key.getName(), data, getBookMetaData().getBookCharset());
            }

            return "";
        } catch (IOException e) {
            // TRANSLATOR: Common error condition: The file could not be read. There can be many reasons.
            // {0} is a placeholder for the file.
            throw new BookException(JSMsg.gettext("Error reading {0}", key.getName()), e);
        } finally {
            DataPolice.setKey(null);
        }
    }

    /**
     * Given a Key, find the TreeNode for it.
     * 
     * @param key
     *            The key to use for searching
     * @return the found node, null otherwise
     * @throws IOException
     */
    private TreeNode find(Key key) throws IOException {
        // We need to search from the root, so navigate to the root, saving as
        // we go.
        List<String> path = new ArrayList<String>();
        for (Key parentKey = key; parentKey != null && parentKey.getName().length() > 0; parentKey = parentKey.getParent()) {
            path.add(parentKey.getName());
        }

        TreeNode node = index.getRoot();

        node = index.getFirstChild(node);

        for (int i = path.size() - 1; i >= 0; i--) {
            String name = path.get(i);

            // Search among the siblings for the current level.
            while (node != null && !name.equals(node.getName())) {
                if (node.hasNextSibling()) {
                    node = index.getNextSibling(node);
                } else {
                    log.error("Could not find " + name);
                    node = null;
                }
            }

            // If we have found it but have not exhausted the path
            // we need to get more
            if (node != null && name.equals(node.getName()) && i > 0) {
                node = index.getFirstChild(node);
            }
        }

        // At this point we have either found it, returning it or have not,
        // returning null
        if (node != null && node.getName().equals(key.getName())) {
            return node;
        }

        return null;
    }

    @Override
    public Key readIndex() {
        SwordBookMetaData bmd = getBookMetaData();
        Key reply = new DefaultKeyList(null, bmd.getName());

        try {
            TreeNode node = index.getRoot();
            reply = new TreeKey(node.getName(), null);
            doReadIndex(node, reply);
        } catch (IOException e) {
            log.error("Could not get read GenBook index", e);
        }

        return reply;
    }

    @Override
    public void setAliasKey(Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRawText(Key key, String text) throws BookException, IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * A helper function to recursively read the entire tree.
     * 
     * @param parentNode
     *            the current node whose children are being sought
     * @param parentKey
     * @throws IOException
     */
    private void doReadIndex(TreeNode parentNode, Key parentKey) throws IOException {
        TreeNode currentNode = parentNode;
        if (currentNode.hasChildren()) {
            TreeNode childNode = index.getFirstChild(currentNode);
            do {
                TreeKey childKey = new TreeKey(childNode.getName(), parentKey);
                parentKey.addAll(childKey);

                // Build the tree as deep as possible
                doReadIndex(childNode, childKey);

                if (!childNode.hasNextSibling()) {
                    break;
                }

                childNode = index.getNextSibling(childNode);
            } while (true);
        }
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive() {
        if (!active) {
            Activator.activate(this);
        }
    }

    /**
     * Raw GenBook file extensions
     */
    private static final String EXTENSION_BDT = ".bdt";

    /**
     * The raw data file
     */
    private File bdtFile;

    /**
     * The random access file for the raw data
     */
    private RandomAccessFile bdtRaf;

    /**
     * The raw index file
     */
    private TreeKeyIndex index;
    /**
     * Are we active
     */
    private boolean active;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(GenBookBackend.class);
}
