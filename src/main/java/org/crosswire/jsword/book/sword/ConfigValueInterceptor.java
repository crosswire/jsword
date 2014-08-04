package org.crosswire.jsword.book.sword;

/**
 * Intercepts values from the configuration before these are widely distributed to the rest of the application.
 */
public interface ConfigValueInterceptor {
    /**
     * Intercepts a value before distribution to the rest of the JSword library
     * @param bookName the initials of the book that is being intercepted
     * @param configEntryType the configuration entry type, describing which field is being accessed
     * @param value the value to be intercepted
     * @return the new value, if different
     */
    Object intercept(String bookName, ConfigEntryType configEntryType, Object value);
}
