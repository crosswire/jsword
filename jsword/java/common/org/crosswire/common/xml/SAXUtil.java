package org.crosswire.common.xml;

import org.crosswire.common.util.Logger;
import org.xml.sax.Attributes;

/**
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class SAXUtil
{
    /**
     * Show the attributes of an element as debug
     */
    public static void debugAttributes(Attributes attrs)
    {
        for (int i=0; i<attrs.getLength(); i++)
        {
            log.debug("attr["+i+"]: "+attrs.getQName(i)+"="+attrs.getValue(i));
        }
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(SAXUtil.class);
}
