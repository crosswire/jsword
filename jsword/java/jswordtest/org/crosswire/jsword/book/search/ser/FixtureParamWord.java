
package org.crosswire.jsword.book.search.ser;

import org.crosswire.common.util.MsgBase;
import org.crosswire.common.util.LogicError;
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
     * @see org.crosswire.jsword.book.search.ser.ParamWord#getWord(org.crosswire.jsword.book.search.ser.Parser)
     */
    public String getWord(Parser engine) throws BookException
    {
        throw new BookException(new MsgBase("Can't get a word from a startswith command"){});
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.ser.ParamWord#getPassage(org.crosswire.jsword.book.search.ser.Parser)
     */
    public Passage getPassage(Parser engine)
    {
        try
        {
            return PassageFactory.createPassage(ref);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError();
        }
    }

    /** The editied up Passage */
    private String ref = null;
}
