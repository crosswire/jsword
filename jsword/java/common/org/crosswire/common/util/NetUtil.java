
package org.crosswire.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
* The NetUtil class looks after general utility stuff around the
* java.net package.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walke
* @author Keith Ralston
*   KR - Modified lengthenURL(URL, String)
* @version D0.I0.T0
*/
public class NetUtil
{
    /**
    * Basic constructor - ensure that we can't be instansiated
    */
    private NetUtil()
    {
    }

    /**
    * If the directory does not exist, create it.
    * Note this currently only works with file: type URLs
    * @param orig The URL to check
    */
    public static void makeDirectory(URL orig) throws MalformedURLException
    {
        if (!orig.getProtocol().equals("file"))
            throw new MalformedURLException("The given URL '"+orig+"' is not a file: URL.");

        File file = new File(orig.getFile());

        // If it is a file, except
        if (file.isFile())
            throw new MalformedURLException("The given URL '"+orig+"' is a file.");

        // Is it already a directory ?
        if (!file.isDirectory())
        {
            file.mkdirs();

            // Did that work?
            if (!file.isDirectory())
                throw new MalformedURLException("The given URL '"+orig+"' could not be created as a directory.");
        }
    }

    /**
    * If the file does not exist, create it.
    * Note this currently only works with file: type URLs
    * @param orig The URL to check
    */
    public static void makeFile(URL orig) throws MalformedURLException, IOException
    {
        if (!orig.getProtocol().equals("file"))
            throw new MalformedURLException("The given URL '"+orig+"' is not a file: URL.");

        File file = new File(orig.getFile());

        // If it is a file, except
        if (file.isDirectory())
            throw new MalformedURLException("The given URL '"+orig+"' is a directory.");

        // Is it already a directory ?
        if (!file.isFile())
        {
            FileOutputStream fout = new FileOutputStream(file);
            fout.close();

            // Did that work?
            if (!file.isFile())
                throw new MalformedURLException("The given URL '"+orig+"' could not be created as a file.");
        }
    }

    /**
    * If there is a file at the other end of this URL return true.
    * Note this currently only works with file: type URLs
    * @param orig The URL to check
    * @return true if the URL points at a file
    */
    public static boolean isFile(URL orig) throws MalformedURLException
    {
        if (!orig.getProtocol().equals("file"))
            throw new MalformedURLException("The given URL '"+orig+"' is not a file: URL.");

        File file = new File(orig.getFile());
        return file.isFile();
    }

    /**
    * If there is a directory at the other end of this URL return true.
    * Note this currently only works with file: type URLs
    * @param orig The URL to check
    * @return true if the URL points at a directory
    */
    public static boolean isDirectory(URL orig) throws MalformedURLException
    {
        if (!orig.getProtocol().equals("file"))
            throw new MalformedURLException("The given URL '"+orig+"' is not a file: URL.");

        File file = new File(orig.getFile());
        return file.isDirectory();
    }

    /**
    * Move a URL from one place to another. Currently this only works for
    * file: URLs, however the interface should not need to change to
    * handle more complex URLs
    * @param old_url The URL to move
    * @param new_url The desitination URL
    */
    public static boolean move(URL old_url, URL new_url) throws IOException
    {
        if (!old_url.getProtocol().equals("file"))
            throw new MalformedURLException("The given source URL '"+old_url+"' is not a file: URL.");

        if (!new_url.getProtocol().equals("file"))
            throw new MalformedURLException("The given destination URL '"+new_url+"' is not a file: URL.");

        File old_file = new File(old_url.getFile());
        File new_file = new File(new_url.getFile());
        return old_file.renameTo(new_file);
    }

    /**
    * Delete a URL. Currently this only works for file: URLs, however
    * the interface should not need to change to handle more complex URLs
    * @param url The URL to delete
    */
    public static boolean delete(URL orig) throws IOException
    {
        if (!orig.getProtocol().equals("file"))
            throw new MalformedURLException("The given URL '"+orig+"' is not a file: URL.");

        File file = new File(orig.getFile());
        return file.delete();
    }

    /**
    * Utility to strip a string from the end of a URL.
    * @param orig The URL to strip
    * @param strip The text to strip from the end of the URL
    * @return The stripped URL
    * @exception MalformedURLException If the URL does not end in the given text
    */
    public static URL shortenURL(URL orig, String strip) throws MalformedURLException
    {
        String file = orig.getFile();
        if (file.endsWith("/"))  file = file.substring(0, file.length()-1);
        if (file.endsWith("\\")) file = file.substring(0, file.length()-1);

        String test = file.substring(file.length() - strip.length());

        if (!test.equals(strip))
            throw new MalformedURLException("The URL '"+orig+"' does not end in '"+strip+"'");

        String new_file = file.substring(0, file.length() - strip.length());

        return new URL(orig.getProtocol(),
                       orig.getHost(),
                       orig.getPort(),
                       new_file);
    }

    /**
    * Utility to add a string to the end of a URL.
    * @param orig The URL to strip
    * @param extra The text to add to the end of the URL
    * @return The stripped URL
    * @exception MalformedURLException If the URL is not valid
    */
    public static URL lengthenURL(URL orig, String extra) throws MalformedURLException
    {
        if (orig.getProtocol().equals("file"))
        {
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile()+
/*KR modification*/        (orig.toString().endsWith(File.separator)?"":File.separator+extra));
        }
        else
        {
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile()+"/"+extra);
        }
    }

    /**
    * Utility to add a string to the end of a URL.
    * @param orig The URL to strip
    * @param extra1 The text to add to the end of the URL
    * @param extra2 The next bit of text to add to the end of the URL
    * @return The stripped URL
    * @exception MalformedURLException If the URL is not valid
    */
    public static URL lengthenURL(URL orig, String extra1, String extra2) throws MalformedURLException
    {
        if (orig.getProtocol().equals("file"))
        {
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile()+File.separator+extra1+File.separator+extra2);
        }
        else
        {
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile()+"/"+extra1+"/"+extra2);
        }
    }

    /**
    * Utility to add a string to the end of a URL.
    * @param orig The URL to strip
    * @param extra1 The text to add to the end of the URL
    * @param extra2 The next bit of text to add to the end of the URL
    * @param extra3 The next bit of text to add to the end of the URL
    * @return The stripped URL
    * @exception MalformedURLException If the URL is not valid
    */
    public static URL lengthenURL(URL orig, String extra1, String extra2, String extra3) throws MalformedURLException
    {
        if (orig.getProtocol().equals("file"))
        {
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile()+
                                File.separator+extra1+
                                File.separator+extra2+
                                File.separator+extra3);
        }
        else
        {
            return new URL(orig.getProtocol(),
                           orig.getHost(),
                           orig.getPort(),
                           orig.getFile()+"/"+extra1+"/"+extra2+"/"+extra3);
        }
    }

    /**
    * Attempt to obtain an OutputStream from a URL. The simple case will
    * just call url.openConnection().getOutputStream(), however in some
    * JVMs (MS at least this fails where new FileOutputStream(url) works.
    * So if openConnection().getOutputStream() fails and the protocol is
    * file, then the alternate version is used.
    * @param url The URL to attempt to write to
    * @return An OutputStream connection
    */
    public static OutputStream getOutputStream(URL url) throws IOException
    {
        return getOutputStream(url, false);
    }

    /**
    * Attempt to obtain an OutputStream from a URL. The simple case will
    * just call url.openConnection().getOutputStream(), however in some
    * JVMs (MS at least this fails where new FileOutputStream(url) works.
    * So if openConnection().getOutputStream() fails and the protocol is
    * file, then the alternate version is used.
    * @param url The URL to attempt to write to
    * @param append Do we write to the end of the file instead of the beginning
    * @return An OutputStream connection
    */
    public static OutputStream getOutputStream(URL url, boolean append) throws IOException
    {
        // We favour the FileOutputStream method here because append
        // is not well defined for the openConnection method

        if (url.getProtocol().equals("file"))
        {
            return new FileOutputStream(url.getFile(), append);
        }
        else
        {
            return url.openConnection().getOutputStream();
        }
    }
}
