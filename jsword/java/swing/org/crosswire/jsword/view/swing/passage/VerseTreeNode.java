
package org.crosswire.jsword.view.swing.passage;

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;

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
public class VerseTreeNode extends ChapterTreeNode
{
    /**
     * This constructor is for when we are really a BookTreeNode
     */
    protected VerseTreeNode(TreeNode parent, int book, int Passage, int verse) throws NoSuchVerseException
    {
        super(parent, book, Passage);
        this.verse = verse;
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    public void setPassage(Passage ref, boolean filter)
    {
        this.ref = ref;
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int i)
    {
        return null; // VerseDisplay thing
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount()
    {
        return 0;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, -1 will be returned.
     */
    public int getIndex(TreeNode node)
    {
        return -1;
    }

    /**
     * How we appear in the Tree
     */
    public String toString()
    {
        return ""+verse;
    }

    /**
     * The current Passage number
     */
    public int getVerse()
    {
        return verse;
    }

    /** The Verse that this node referrs to */
    protected int verse = 0;
}

