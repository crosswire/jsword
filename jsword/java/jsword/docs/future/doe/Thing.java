
package docs.future.doe;

import java.util.List;
import org.crosswire.jsword.passage.Passage;

/**
 * There is a question as to whether these should be concrete or not.
 * @author Joe Walker
 * @stereotype thing 
 */
public class Thing 
{
    public String getName()
    {
        return name;
    }

    public Passage getPassage()
    {
        return ref;
    }

    public void setPassage(Passage ref)
    {
        this.ref = ref;
    }

    public List getAliases()
    {
        return aliases;
    }

    private String name;

    /**
     * @associates <{String}>
     * @label aliases
     * @supplierCardinality 0..*
     * @clientCardinality 0..*
     */
    private List aliases;

    private Passage ref;
}
