
package org.crosswire.net;

import java.util.*;
import java.io.*;
import java.net.*;


/**
* A quick way of testing CommsXL
*
* <h2>Enabling HTTPS</h2>
* This could use the HTTPCLient handler from innovation.ch however
* this fails because there are handlers only for http not https
* <code>System.getProperties().setProperty("java.protocol.handler.pkgs", "HTTPClient");</code>
* <br>Normally this fails with:
* <code>java.lang.ClassFormatError: HTTPClient/CookieModule (Invalid start_pc/length in local var table)</code>
* However if you compile with jikes ...
* <code>java.lang.NoClassDefFoundError: HTTPClient/https/Handler (wrong name: sunw/hotjava/protocol/https/Handler)</code>
*
* <p>If you simply include the HotJava jars in the CLASSPATH then URL will
* automatically try to use them. However I think they only work at 1.1
* Using JDK 1.2 without changing your PATH gives:
* <pre>
* javax.net.ssl.SSLException: Received fatal alert: handshake_failure (no cipher suites in common)
*   at sun.security.ssl.SSLSocketImpl.recvAlert(SSLSocketImpl.java:951)
*   at sun.security.ssl.SSLSocketImpl.clearPipeline(SSLSocketImpl.java:684)
*   at sun.security.ssl.SSLSocketImpl.write(SSLSocketImpl.java:437)
*   ...
* </pre>
* <p>Adding the HotJava bin dir to the path (so that we get a look at jsafe.dll
* alters this (slightly) so we get a windows linker error on javai.dll (from 1.1)
* Adding the HotJava/runtime dir to the path too changes the error to:
* <pre>
* java.lang.UnsatisfiedLinkError: initIDs
*   at sun.security.ssl.Handshaker.maybeSetCipherSuite(Handshaker.java:444)
*   at sun.security.ssl.ClientHandshaker.serverHello(ClientHandshaker.java:341)
*   at sun.security.ssl.ClientHandshaker.processMessage(ClientHandshaker.java:119)
*   at sun.security.ssl.Handshaker.process_record(Handshaker.java:256)
*   at sun.security.ssl.SSLSocketImpl.clearPipeline(SSLSocketImpl.java:625)
*   at sun.security.ssl.SSLSocketImpl.write(SSLSocketImpl.java:437)
*   ... (note similarity with above)
* </pre>
* We should maybe add the PlugProv.jar to the CLASSPATH?
*
* <p>This attempts to make us use the SSLava handlers, this works, but it costs $2500
* <code>URL.setURLStreamHandlerFactory(new crysec.https.HttpsURLStreamHandlerFactory());</code>
*
* <p>Adding the JWS2 jars also works, but for free.
*/
public class WebEnvironment
{
    /**
    *
    */
    public WebEnvironment()
    {
    }

    /**
    * Check that the given string exists in the data
    */
    public void snooze(int millis)
    {
        try
        {
            synchronized (this)
            {
                wait(millis);
            }
        }
        catch (InterruptedException ex)
        {
        }
    }

    /**
    * Works out the reply number 404 or something
    *
    *     public void alert(String contents)
    *     {
    *         log.fine(contents);
    *     }
    *
    *     /**
    * Works out the reply number 404 or something
    */
    public WebBrowser getBrowser()
    {
        return new WebBrowser();
    }

    /**
    * Get a number that is garantteed unique in this VM
    */
    public int getInstanceID()
    {
        return getInstanceIDStatic();
    }

    /**
    * Get a number that is garantteed unique in this VM
    */
    private static int getInstanceIDStatic()
    {
        synchronized (sync)
        {
            return id++;
        }
    }

    /**
    * Sets new proxy settings
    */
    public void setProxy(String host, int port)
    {
        if (host == null)
        {
            System.getProperties().remove("proxySet");
            System.getProperties().remove("proxyHost");
            System.getProperties().remove("proxyPort");
        }
        else
        {
            System.getProperties().put("proxySet", "true");
            System.getProperties().put("proxyHost", host);
            System.getProperties().put("proxyPort", ""+port);

            // Maybe we should have a separate API for this?
            System.getProperties().put("ftpProxySet", "true");
            System.getProperties().put("ftpProxyHost", host);
            System.getProperties().put("ftpProxyPort", ""+port);
        }
    }

    /** The object to sync on */
    private static Object sync = new Object();

    /** The next unique id */
    private static int id = 0;
}
