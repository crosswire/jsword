
package docs.future.doe;

import java.util.Iterator;

/**
 * @stereotype factory 
 */
public interface DoeFactory {
    Iterator getThings(String alias);

    Iterator getEvents();
}
