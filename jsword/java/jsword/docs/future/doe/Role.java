package docs.future.doe;

import org.crosswire.jsword.passage.Passage;
/**
 * @stereotype role 
 */
public class Role
{
    public Thing getThing()
    {
        return thing;
    }

    public Event getEvent()
    {
        return event;
    }

    public String getRole()
    {
        return role;
    }

    /**
     * @label acted by
     * @supplierCardinality 1 
     */
    private Thing thing;

    /**
     * @label part 
     * @supplierCardinality 1
     */
    private Event event;

    private String role;

    private Passage ref;
}
