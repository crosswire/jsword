package org.crosswire.jsword.book.search.parse;

import org.crosswire.common.util.MsgBase;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;

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
    public KeyList getKeyList(LocalParser engine)
    {
        try
        {
            Index index = engine.getIndex();
            KeyList keylist = index.findWord(null);
            Key key = index.getKey(ref);
            keylist.add(key);

            return keylist;
        }
        catch (Exception ex)
        {
            assert false : ex;
            try
            {
                return engine.getIndex().findWord(null);
            }
            catch (BookException ex2)
            {
                assert false : ex2;
                return null;
            }
        }
    }

    /**
     * The editied up Passage
     */
    private String ref = null;
}