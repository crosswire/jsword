/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.state.GenBookBackendState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.TreeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backend for General Books.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class GenBookBackend extends AbstractBackend<GenBookBackendState> {
    /**
     * Simple ctor
     * 
     * @param sbmd 
     */
    public GenBookBackend(SwordBookMetaData sbmd) {
        super(sbmd);
        index = new TreeKeyIndex(sbmd);
    }

    public GenBookBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getGenBookBackendState(getBookMetaData());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#contains(org.crosswire.jsword.passage.Key)
     */
    @Override
    public boolean contains(Key key) {
        return getRawTextLength(key) > 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#size(org.crosswire.jsword.passage.Key)
     */
    @Override
    public int getRawTextLength(Key key) {
        try {
            TreeNode node = find(key);

            if (node == null) {
                return 0;
            }

            byte[] userData = node.getUserData();

            // Some entries may be empty.
            if (userData.length == 8) {
                return SwordUtil.decodeLittleEndian32(userData, 4);
            }

            return 0;

        } catch (IOException e) {
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.StatefulFileBackedBackend#readRawContent(org.crosswire.jsword.book.sword.state.OpenFileState, org.crosswire.jsword.passage.Key)
     */
    public String readRawContent(GenBookBackendState state, Key key) throws IOException, BookException {
        TreeNode node = find(key);

        if (node == null) {
            // TRANSLATOR: Error condition: Indicates that something could
            // not be found in the book.
            // {0} is a placeholder for the unknown key.
            // {1} is the short name of the book
            throw new BookException(JSMsg.gettext("No entry for '{0}' in {1}.", key.getName(), getBookMetaData().getInitials()));
        }

        byte[] userData = node.getUserData();

        // Some entries may be empty.
        if (userData.length == 8) {
            int start = SwordUtil.decodeLittleEndian32(userData, 0);
            int size = SwordUtil.decodeLittleEndian32(userData, 4);
            byte[] data = SwordUtil.readRAF(state.getBdtRaf(), start, size);
            decipher(data);
            return SwordUtil.decode(key.getName(), data, getBookMetaData().getBookCharset());
        }

        return "";
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
                    log.error("Could not find {}", name);
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
        BookMetaData bmd = getBookMetaData();
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(GenBookBackendState state, Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(GenBookBackendState rafBook, Key key, String text) throws BookException, IOException {
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
     * The raw index file
     */
    private final TreeKeyIndex index;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(GenBookBackend.class);
}
