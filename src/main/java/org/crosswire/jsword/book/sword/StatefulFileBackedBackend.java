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
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.passage.Key;

/**
 * Indicates that there is a stateful backend
 *
 *
 * @param <T> The type of the OpenFileState that this class extends.
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface StatefulFileBackedBackend<T extends OpenFileState> {
    /**
     * Initialises the state required to read from files, specific to each
     * different backend
     * 
     * @return the state that has been initialised
     * @throws BookException
     */
     T initState() throws BookException;

    /**
     * 
     * @param state
     *            the state object containing all the open random access files
     * @param key
     *            the verse that is sought
     * @return the raw text
     * @throws BookException 
     * @throws IOException
     *             something went wrong when reading the verse
     */
     String readRawContent(T state, Key key) throws BookException, IOException;

     /**
      * Set the text allotted for the given verse
      * 
      * @param state
      *            TODO
      * @param key
      *            The key to set text to
      * @param text
      *            The text to be set for key
      * 
      * @throws BookException
      *             If the data can not be set.
      * @throws IOException
      *             If the module data path could not be created.
      */
     void setRawText(T state, Key key, String text) throws BookException, IOException;

     /**
      * Sets alias for a comment on a verse range I.e. setRawText() was for verse
      * range Gen.1.1-3 then setAliasKey should be called for Gen.1.1.2 and
      * Gen.1.1.3
      * 
      * @param state 
      *            the open file state
      * @param alias
      *            Alias Key
      * @param source
      *            Source Key
      * @throws IOException
      *             Exception when anything goes wrong on writing the alias
      */
     void setAliasKey(T state, Key alias, Key source) throws IOException;
}
