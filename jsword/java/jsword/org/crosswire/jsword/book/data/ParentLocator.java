package org.crosswire.jsword.book.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.Element;

/**
 * A cheat way to find parents of nodes in a JAXB tree.
 * 
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class ParentLocator
{
    /**
     * Create a cache of parent/child relationships.
     */
    public ParentLocator(Element root)
    {
        recurseChildren(root);
    }

    /**
     * What is the parent of the given child
     */
    public Element getParent(Element child)
    {
        return (Element) map.get(child);
    }

    /**
     * Recurse through the children of the given element finding and recording
     * parent/child relationships.
     */
    private void recurseChildren(Element parent)
    {
        Iterator it = JAXBUtil.getList(parent).iterator();
        while (it.hasNext())
        {
            Element child = (Element) it.next();
            recurseChildren(child);

            map.put(child, parent);
        }
    }

    /**
     * The map of parent/child relationships
     */
    private Map map = new HashMap();
}
