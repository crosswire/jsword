
package org.crosswire.net;

/**
* A set of Headers are set from the server to the browser before the
* actual web page data is sent, these headers include meta-information
* about the data including Cookies
* @author Joe Walker
*/
public class Header implements Cloneable
{
    /**
    * Constructs a header with a specified name and value.
    * @param name a string specifying the name of the cookie
    * @param value a string specifying the value of the cookie
    * @see #setValue
    */
    public Header(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    /**
    * Returns the name of the header. You cannot change the name
    * after the header is created.
    * @return a string specifying the header's name
    */
    public String getName()
    {
        return name;
    }

    /**
    * Returns the value of the header.
    * @return a string containing the header's present value
    * @see setValue
    */
    public String getValue()
    {
        return value;
    }

    /**
    * If the name and value of the Header are null then the header is
    * not valid - and should be ignored
    */
    public boolean isValid()
    {
        if (name == null && value == null)
            return false;

        return true;
    }

    /**
    * Does this Header represent a request for a new Cookie
    */
    public boolean isCookie()
    {
        return name != null &&
               value != null &&
               name.equalsIgnoreCase("Set-Cookie");
    }

    /**
    * Get Cookie
    */
    public Cookie getCookie()
    {
        if (!isCookie())
            return null;

        return new Cookie(value);
    }

    /**
    * Returns a text version of this cookie
    */
    public String toString()
    {
        if (name == null) return value;
        if (value == null) return name;

        return name+": "+value;
    }

    /** NAME= ... "$Name" style is reserved */
    private String name;

    /** value of NAME */
    private String value;
}

