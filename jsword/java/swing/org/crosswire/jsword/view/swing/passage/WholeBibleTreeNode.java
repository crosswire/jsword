
package org.crosswire.jsword.view.swing.passage;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A PassageTreeNode extends TreeNode to Model a Passage.
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
public class WholeBibleTreeNode implements TreeNode
{
    /**
     * The starrt point for all WholeBibleTreeNodes.
     */
    public static WholeBibleTreeNode getRootNode()
    {
        return new WholeBibleTreeNode(null, VerseRange.getWholeBibleVerseRange(), LEVEL_BIBLE);
    }

    /**
     * We could do some caching here if needs be.
     */
    protected static WholeBibleTreeNode getNode(TreeNode parent, int b, int c, int v)
    {
        try
        {
            Verse start = null;
            Verse end = null;
            int level;

            if (b == -1)
            {
                throw new LogicError();
            } 
            else if (c == -1)
            {
                level = LEVEL_BOOK;
                int ec = BibleInfo.chaptersInBook(b);
                int ev = BibleInfo.versesInChapter(b, ec);
                start = new Verse(b, 1, 1);
                end = new Verse(b, ec, ev);
            }
            else if (v == -1)
            {
                level = LEVEL_CHAPTER;
                int ev = BibleInfo.versesInChapter(b, c);
                start = new Verse(b, c, 1);
                end = new Verse(b, c, ev);
            }
            else
            {
                level = LEVEL_VERSE;
                start = new Verse(b, c, v);
                end = start;
            }

            VerseRange range = new VerseRange(start, end);
            return new WholeBibleTreeNode(parent, range, level);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    private WholeBibleTreeNode(TreeNode parent, VerseRange range, int level)
    {
        if (parent != null)
        {
            this.parent = parent;
        }
        else
        {
            this.parent = this;
        }

        this.range = range;
        this.level = level;
    }

    /**
     * The current Passage number
     */
    public VerseRange getVerseRange()
    {
        return range;
    }

    /**
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent()
    {
        return parent;
    }

    /**
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren()
    {
        return level != LEVEL_VERSE;
    }

    /**
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf()
    {
        return level == LEVEL_VERSE;
    }

    /**
     * How we appear in the Tree
     */
    public String toString()
    {
        try
        {
            switch (level)
            {
            case LEVEL_BIBLE:
                return "The Bible";

            case LEVEL_BOOK:
                return BibleInfo.getLongBookName(range.getStart().getBook());

            case LEVEL_CHAPTER:
                return Integer.toString(range.getStart().getChapter());

            case LEVEL_VERSE:
                return Integer.toString(range.getStart().getVerse());

            default:
                return "ERROR";
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int i)
    {
        switch (level)
        {
        case LEVEL_BIBLE:
            return WholeBibleTreeNode.getNode(this, i + 1, -1, -1);

        case LEVEL_BOOK:
            return WholeBibleTreeNode.getNode(this, range.getStart().getBook(), i + 1, -1);

        case LEVEL_CHAPTER:
            return WholeBibleTreeNode.getNode(this, range.getStart().getBook(), range.getStart().getChapter(), i + 1);

        case LEVEL_VERSE:
            return null;

        default:
            return null;
        }
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount()
    {
        try
        {
            switch (level)
            {
            case LEVEL_BIBLE:
                return BibleInfo.booksInBible();
    
            case LEVEL_BOOK:
                return BibleInfo.chaptersInBook(range.getStart().getBook());
    
            case LEVEL_CHAPTER:
                return BibleInfo.versesInChapter(range.getStart().getBook(), range.getStart().getChapter());
    
            case LEVEL_VERSE:
                return 0;
    
            default:
                return 0;
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, -1 will be returned.
     */
    public int getIndex(TreeNode node)
    {
        if (!(node instanceof WholeBibleTreeNode))
            return -1;

        WholeBibleTreeNode vnode = (WholeBibleTreeNode) node;

        switch (level)
        {
        case LEVEL_BIBLE:
            return vnode.getVerseRange().getStart().getBook() - 1;

        case LEVEL_BOOK:
            return vnode.getVerseRange().getStart().getChapter() - 1;

        case LEVEL_CHAPTER:
            return vnode.getVerseRange().getStart().getVerse() - 1;

        case LEVEL_VERSE:
            return -1;

        default:
            return -1;
        }
    }

    /**
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children()
    {
        return new WholeBibleEnumeration();
    }

    /**
     * Iterate over the Books
     */
    public class WholeBibleEnumeration implements Enumeration
    {
        public boolean hasMoreElements()
        {
            return count < getChildCount();
        }

        public Object nextElement()
        {
            count++;
            return getChildAt(count);
        }
        
        private int count = 0;
    }

    protected static final int LEVEL_BIBLE = 0;
    protected static final int LEVEL_BOOK = 1;
    protected static final int LEVEL_CHAPTER = 2;
    protected static final int LEVEL_VERSE = 3;

    /** The range that this node refers to */
    protected VerseRange range;

    /** Our parent tree node */
    private TreeNode parent;

    /** The level of this node one of: LEVEL_[BIBLE|BOOK|CHAPTER|VERSE] */
    private int level;
}
