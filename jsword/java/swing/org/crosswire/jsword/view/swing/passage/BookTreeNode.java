
package org.crosswire.jsword.view.swing.passage;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.common.util.LogicError;

/**
 * PassageTableModel.
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
public class BookTreeNode extends BibleTreeNode
{
    /**
     * This constructor is for when we are really a BookTreeNode
     */
    protected BookTreeNode(TreeNode parent, int book) throws NoSuchVerseException
    {
        this.parent = parent;
        this.book = book;

        kids = new ChapterTreeNode[BibleInfo.chaptersInBook(book)];
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    public void setPassage(Passage ref, boolean filter)
    {
        this.ref = ref;

        if (filter)
        {
            try
            {
                kids = new ChapterTreeNode[ref.chaptersInPassage(book)];

                int current_Passage = 0;
                int Passage_count = 0;

                Iterator it = ref.verseIterator();
                while (it.hasNext())
                {
                    Verse verse = (Verse) it.next();

                    if ((book == 0 || verse.getBook() == book)
                        && current_Passage != verse.getChapter())
                    {
                        current_Passage = verse.getChapter();

                        ChapterTreeNode node = new ChapterTreeNode(this, book, current_Passage);
                        node.setPassage(ref, true);
                        kids[Passage_count++] = node;
                    }
                }
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
        }
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int i)
    {
        try
        {
            if (kids[i] != null) return kids[i];

            ChapterTreeNode node = new ChapterTreeNode(this, book, i+1);
            node.setPassage(ref, false);
            kids[i] = node;

            return kids[i];
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent()
    {
        return parent;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node)
    {
        if (!(node instanceof ChapterTreeNode))
            return -1;

        ChapterTreeNode chap = (ChapterTreeNode) node;
        return chap.getChapter();
    }

    /**
     * How we appear in the Tree
     */
    public String toString()
    {
        try
        {
            String book_name = BibleInfo.getLongBookName(book);
            if (ref == null) return book_name;

            int chapters = ref.chaptersInPassage(book);
            if (chapters == 0) return book_name;

            return book_name + " (" + chapters + ")";
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * The current book number (Genesis=1)
     */
    public int getBook()
    {
        return book;
    }

    /** The Book that this node referrs to */
    protected int book = 0;

    /** The base of this tree */
    protected TreeNode parent = null;
}

