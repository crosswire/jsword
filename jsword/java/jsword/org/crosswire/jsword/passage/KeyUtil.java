package org.crosswire.jsword.passage;

import java.util.Iterator;

import org.crosswire.common.util.Logger;

/**
 * .
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
public class KeyUtil
{
    /**
     * Prevent instansiation
     */
    private KeyUtil()
    {
    }

    /**
     * Walk through a tree visiting the nodes and branches in the tree
     * @param key The node tree to walk through
     * @param visitor The visitor to notify whenever a node is found
     */
    public static void visit(Key key, KeyVisitor visitor)
    {
        for (Iterator it = key.iterator(); it.hasNext(); )
        {
            Key subkey = (Key) it.next();
            if (subkey.canHaveChildren())
            {
                visitor.visitBranch(subkey);
                visit(subkey, visitor);
            }
            else
            {
                visitor.visitLeaf(subkey);
            }
        }
    }

    /**
     * Not all keys represent verses, but we ought to be able to get something
     * close to a verse from anything that does verse like work.
     */
    public static Verse getVerse(Key key)
    {
        if (key instanceof Verse)
        {
            return (Verse) key;
        }

        if (key instanceof Passage)
        {
            Passage ref = getPassage(key);
            return ref.getVerseAt(0);
        }

        try
        {
            Verse verse = VerseFactory.fromString(key.getName());
            return verse;
        }
        catch (NoSuchVerseException ex)
        {
            log.warn("Key can't be a verse: " + key.getName()); //$NON-NLS-1$
            return Verse.DEFAULT;
        }
    }

    /**
     * Not all keys represent passages, but we ought to be able to get something
     * close to a passage from anything that does passage like work.
     * If you pass a null key into this method, you get a null Passage out.
     */
    public static Passage getPassage(Key key)
    {
        if (key == null)
        {
            return null;
        }

        if (key instanceof Passage)
        {
            return (Passage) key;
        }

        Key ref = null;
        try
        {
            ref = keyf.getKey(key.getName());
        }
        catch (NoSuchKeyException ex)
        {
            log.warn("Key can't be a passage: " + key.getName()); //$NON-NLS-1$
            ref = keyf.createEmptyKeyList();
        }
        return (Passage) ref;
    }

    /**
     * How we create Passages
     */
    private static KeyFactory keyf = PassageKeyFactory.instance();

    /**
     *  The log stream
     */
    private static final Logger log = Logger.getLogger(KeyUtil.class);
}
