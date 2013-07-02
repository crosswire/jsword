package org.crosswire.jsword.versification;

import org.crosswire.common.util.KeyValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chrisburrell
 */
public class FileVersificationMapping {
    //unsure what a typical value would be, so leaving at 16 - best to optimize for memory,
    //than speed upon reading the first time.
    private List<KeyValuePair> pairs = new ArrayList<KeyValuePair>(16);

    /**
     * @param key   the key
     * @param value the value
     */
    public void addProperty(String key, String value) {
        pairs.add(new KeyValuePair(key, value));
    }

    public List<KeyValuePair> getMappings() {
        return this.pairs;
    }
}
