package org.crosswire.jsword.view.swing.reference;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.crosswire.common.util.IteratorEnumeration;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

/**
 * The root node in the ReferencePane Tree model.
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
public class ReferenceRootTreeNode implements TreeNode
{
    /**
     * Simple ctor
     */
    public ReferenceRootTreeNode(ReferenceTreeModel model)
    {
        this.model = model;        
        Books.installed().addBooksListener(new CustomBooksListener());

        this.filter = null;
        books = Books.installed().getBookMetaDatas();
    }

    /**
     * Simple ctor
     */
    public ReferenceRootTreeNode(ReferenceTreeModel model, BookFilter filter)
    {
        this.model = model;
        Books.installed().addBooksListener(new CustomBooksListener());

        this.filter = filter;
        books = Books.installed().getBookMetaDatas(filter);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Reference Works";
    }

    /**
     * Set the filter for this TreeNode
     */
    public void setFilter(BookFilter filter)
    {
        this.filter = filter;
        books = Books.installed().getBookMetaDatas(filter);

        model.fireTreeStructureChanged(filter, new Object[] { this }, new int[0], null);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount()
    {
        return books.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children()
    {
        return new IteratorEnumeration(books.iterator());
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(int childIndex)
    {
        BookMetaData bmd = (BookMetaData) books.get(childIndex);
        return new ReferenceBookTreeNode(model, this, bmd);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node)
    {
        ReferenceBookTreeNode refnode = (ReferenceBookTreeNode) node;
        BookMetaData bmd = refnode.getBookMetaData();
        Book book = bmd.getBook();
        return books.indexOf(book);
    }

    /**
     * The current list of books
     */
    protected List books = new ArrayList();

    /**
     * the filter that we apply to books before we display them
     */
    protected BookFilter filter;

    /**
     * The place that we report changes to
     */
    private ReferenceTreeModel model;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(ReferenceRootTreeNode.class);

    /**
     * So we can keep track of how many books there are
     */
    private final class CustomBooksListener implements BooksListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev)
        {
            BookMetaData bmd = ev.getBookMetaData();

            if (filter.test(bmd))
            {
                boolean changed = books.add(bmd);
                if (!changed)
                {
                    log.error("added a book from an event but our filtered book list did not change");
                }
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev)
        {
            BookMetaData bmd = ev.getBookMetaData();

            if (filter.test(bmd))
            {
                boolean changed = books.remove(bmd);
                if (!changed)
                {
                    log.error("removed a book from an event but our filtered book list did not change");
                }
            }
        }
    }
}
