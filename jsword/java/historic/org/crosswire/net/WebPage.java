
package org.crosswire.net;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;

// import java.a*t.*;
// import java.a*t.event.*;
// import javax.sw*ng.*;
// import javax.sw*ng.text.*;
// import javax.sw*ng.text.html.*;
// import sunw.hotjava.bean.*;
import org.crosswire.util.Logger;
import org.crosswire.util.StringUtil;

/**
* Represents a single web page.
* @author Joe Walker
*/
public class WebPage
{
    /**
    * Create a URLData object from a URL string
    * @param browser The browser that created us, from which we get headers
    * @param url_str The URL to visit
    */
    public WebPage(WebBrowser browser, String url_str)
    {
        try
        {
            this.url = new URL(url_str);
            this.browser = browser;

            // Create a connection
            URLConnection cnx = url.openConnection();
            cnx.setDoOutput(true);
            cnx.setUseCaches(false);
            cnx.setAllowUserInteraction(false);

            // Add custom headers
            for (Enumeration en=browser.headers(); en.hasMoreElements(); )
            {
                Header header = (Header) en.nextElement();
                cnx.setRequestProperty(header.getName(), header.getValue());
            }

            // They all should be http connections ...
            if (cnx instanceof HttpURLConnection)
            {
                HttpURLConnection hcnx = (HttpURLConnection) cnx;

                code = hcnx.getResponseCode();

                // Read the headers
                int i = 0;
                while (true)
                {
                    Header header = new Header(cnx.getHeaderFieldKey(i), cnx.getHeaderField(i));
                    if (!header.isValid()) break;

                    // Add new cookies to the browser
                    if (header.isCookie())
                    {
                        Cookie cookie = header.getCookie();
                        browser.addCookie(cookie);
                        cookies.addElement(cookie);
                    }

                    headers.addElement(header);
                    i++;
                }
            }

            // Read the actual page data
            Reader in = new InputStreamReader(cnx.getInputStream());
            data = StringUtil.read(in);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
            if (ex instanceof ThreadDeath) throw (ThreadDeath) ex;
            data = null;
        }
    }

    /**
    * Check that the given string exists in the data
    */
    public boolean contains(String test)
    {
        return data.indexOf(test) != -1;
    }

    /**
    * The page headers
    */
    public void printHeaders()
    {
        for (Enumeration en=headers.elements(); en.hasMoreElements(); )
        {
            System.out.println(""+en.nextElement());
        }
    }

    /**
    * The page data
    */
    public void printContent()
    {
        System.out.println(data);
    }

    /*
    * The page data
    *
    public void displayContent()
    {
        try
        {
            if (mode == 0)
            {
                HTMLDocument html = new HTMLDocument();
                HTMLEditorKit editor = new HTMLEditorKit();

                try
                {
                    // Shove the page data into a stream
                    // And the read it into a Document
                    editor.read(new StringReader(data), html, 0);
                }
                catch (Throwable ex)
                {
                    if (ex instanceof ThreadDeath) throw (ThreadDeath) ex;
                    // The HTML component regularly throws up without any good reason
                    // ignore him and he will go away.
                }

                // Create the GUI components
                JFrame frame = new JFrame(url.toString());
                JTextPane text = new JTextPane(html);
                JScrollPane scroll = new JScrollPane(text);

                // Display them
                scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add("Center", scroll);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                try
                {
                    frame.pack();
                }
                catch (Throwable ex)
                {
                    if (ex instanceof ThreadDeath) throw (ThreadDeath) ex;
                    // The HTML component regularly throws up without any good reason
                    // ignore him and he will go away.
                }

                frame.setVisible(true);
            }
            else
            {
                /*
                HotJavaBrowserBean hjb = new HotJavaBrowserBean();
                StringReader sin = new StringReader(data);
                hjb.setDocumentSource(sin);

                final Frame frame = new Frame(url.toString());

                // Display them
                frame.setLayout(new BorderLayout());
                frame.add("Center", hjb);

                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent ev)
                    {
                        frame.setVisible(false);
                        frame.dispose();
                    }
                });

                //frame.pack();
                frame.setVisible(true);
                *
            }
        }
        catch (Throwable ex)
        {
            if (ex instanceof ThreadDeath) throw (ThreadDeath) ex;
            // System.out.println(Level.INFO, "Failure", ex);
        }
    }
    */

    /**
    * The page headers
    */
    public void printNewCookies()
    {
        for (Enumeration en=cookies.elements(); en.hasMoreElements(); )
        {
            System.out.println(en.nextElement().toString());
        }
    }

    /**
    * Works out the reply number 404 or something
    * @return The reply status
    */
    public int getStatus()
    {
        return code;
    }

    /**
    * Gets the page contents
    * @return The page contents
    */
    public String getPageContents()
    {
        return data;
    }

    /** How are the pages displayed */
    public static int mode = 1;

    /** The page headers */
    private Vector headers = new Vector();

    /** The new cookies delivered with this page */
    private Vector cookies = new Vector();

    /** The page source */
    private String data;

    /** The response code */
    private int code = -1;

    /** The URL we went after in the first place */
    private URL url;

    /** The Web browser we were grabbed from */
    private WebBrowser browser;

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.swing");
}
