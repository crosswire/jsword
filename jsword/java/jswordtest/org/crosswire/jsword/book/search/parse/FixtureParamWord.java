
package org.crosswire.jsword.book.search.parse;

import org.crosswire.common.util.MsgBase;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * A test SearchParamWord
 * @author Joe Walker
 */
class FixtureParamWord implements ParamWord
{
    /**
     * Setup the Passage to edit
     * @param ref The Passage to edit
     */
    public FixtureParamWord(String ref)
    {
        this.ref = ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire.jsword.book.search.parse.Parser)
     */
    public String getWord(LocalParser engine) throws BookException
    {
        throw new BookException(new MsgBase("Can't get a word from a startswith command"){}); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getPassage(org.crosswire.jsword.book.search.parse.Parser)
     */
    public Passage getPassage(LocalParser engine)
    {
        try
        {
            return PassageFactory.createPassage(ref);
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return PassageFactory.createPassage();
        }
    }

    /**
     * The editied up Passage
     */
    private String ref = null;
}
