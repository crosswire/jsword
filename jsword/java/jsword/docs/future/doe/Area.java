package docs.future.doe;

import java.util.List;

/**
 * @stereotype description
 */
public class Area extends Location
{
    /**
     * @associates <{docs.future.doe.Point}>
     * @label bounded by
     * @supplierCardinality 0..*
     */
    private List outline;
}
