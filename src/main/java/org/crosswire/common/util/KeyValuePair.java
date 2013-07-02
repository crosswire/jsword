package org.crosswire.common.util;

/**
 * Encapsulates the key and value together.
 * @author chrisburrell
 */
public class KeyValuePair {
    private String key;
    private String value;

    /**
     * @param key the key
     * @param value the value
     */
   public KeyValuePair(String key, String value) {
       this.key = key;
       this.value = value;
   }


    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
