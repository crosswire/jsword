
package org.crosswire.net;

import java.util.Enumeration;
import java.util.Vector;

import org.crosswire.util.Logger;

/**
*
*/
public class WebBrowser
{
    /**
    * Fetch a URL
    */
    public WebPage getWebPage(String url_str)
    {
        return new WebPage(this, url_str);
    }

    /**
    * Adds a new header
    */
/*
    public void addProxyAuth(String username, String password)
    {
        String plain = username+":"+password;
        String encoded = "Basic "+new sun.misc.BASE64Encoder().encode(plain.getBytes());
        Header header = new Header("Proxy-Authorization", encoded);
        addHeader(header);
    }
*/

    /**
    * Adds a new header
    */
    public void addHeader(String key, String value)
    {
        Header header = new Header(key, value);
        addHeader(header);
    }

    /**
    * Adds a new header
    */
    public void addHeader(Header header)
    {
        headers.addElement(header);
    }

    /**
    * Removes a header
    */
    public void removeHeader(Header header)
    {
        headers.removeElement(header);
    }

    /**
    * An enumeration over all the headers
    */
    public Enumeration headers()
    {
        return headers.elements();
    }

    /**
    * The page headers
    */
    public void printHeaders()
    {
        for (Enumeration en=headers.elements(); en.hasMoreElements(); )
        {
            log.fine(""+en.nextElement());
        }
    }

    /**
    * Adds a new Cookie
    */
    public void addCookie(String key, String value)
    {
        Cookie cookie = new Cookie(key, value);
        addCookie(cookie);
    }

    /**
    * Adds a new Cookie
    */
    public void addCookie(Cookie cookie)
    {
        cookies.addElement(cookie);
        setCookieHeader();
    }

    /**
    * Removes a Cookie
    */
    public void removeCookie(Cookie cookie)
    {
        cookies.removeElement(cookie);
        setCookieHeader();
    }

    /**
    * An enumeration over all the cookies
    */
    public Enumeration cookies()
    {
        return cookies.elements();
    }

    /**
    * The cookies
    */
    public void printCookies()
    {
        for (Enumeration en=cookies.elements(); en.hasMoreElements(); )
        {
            log.fine(""+en.nextElement());
        }
    }

    /**
    * Sort the cookie header out
    */
    private void setCookieHeader()
    {
        // First remove the old cookies
        if (cook_head != null)
        {
            headers.removeElement(cook_head);
            cook_head = null;
        }

        // Create the cookies header
        StringBuffer buffer = new StringBuffer();
        for (Enumeration en=cookies.elements(); en.hasMoreElements(); )
        {
            Cookie cookie = (Cookie) en.nextElement();
            buffer.append(cookie.getName()+"="+cookie.getValue());
            if (en.hasMoreElements())
                buffer.append("; ");
        }

        // Add a cookies header
        if (buffer.length() != 0)
        {
            // There are cookies now
            cook_head = new Header("Cookie", buffer.toString());
            headers.addElement(cook_head);
        }
    }

    /** The set of additional headers */
    private Vector headers = new Vector();

    /** The set of cookies */
    private Vector cookies = new Vector();

    /** The special cookie header */
    private Header cook_head = null;

    /** The log stream */
    protected static Logger log = Logger.getLogger(WebBrowser.class);
}

