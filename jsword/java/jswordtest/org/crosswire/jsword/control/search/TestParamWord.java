
package org.crosswire.jsword.control.search;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
* A test SearchParamWord
* @author Joe Walker
*/
class TestParamWord implements ParamWord
{
    /**
     * Setup the Passage to edit
     * @param ref The Passage to edit
     */
    public TestParamWord(String ref)
    {
        this.ref = ref;
    }

    /** Get a word for something else to word on */
    public String getWord(Engine engine) throws SearchException
    {
        throw new SearchException("Can't get a word from a startswith command");
    }

    /** Get a Passage for something else to word on */
    public Passage getPassage(Engine engine)
    {
        try
        {
            return PassageFactory.createPassage(ref);
        }
        catch (NoSuchVerseException ex)
        {
            throw new Error("Logic Error");
        }
    }

    /** The editied up Passage */
    private String ref = null;
}
