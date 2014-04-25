package org.crosswire.common.util;

import java.util.regex.Pattern;

/**
 * A set of utilities to help tests run properly
 */
public class PlatformTestUtils {
    /**
     * Simple helper to wrap around the pattern
     * @param result the string to be tested
     * @param prefixPattern the pattern
     * @return true if matches
     */
    public static boolean startsWith(String result, String prefixPattern) {
        return Pattern.compile(prefixPattern).matcher(result).find();
    }
}
