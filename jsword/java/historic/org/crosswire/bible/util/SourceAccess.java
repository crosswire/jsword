
package com.eireneh.bible.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import com.eireneh.util.*;

/**
* SourceAccess give some access to the source to an application so that
* it can be viewed. Initially it is only needed as a servlet viewer,
* maybe one day there is scope for an editor/compiler add-in.
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
* @see docs.Licence
* @author Joe Walker
* @version D0.I0.T0
*/
public class SourceAccess
{
    /**
    * Create relevant data about a query
    */
    private SourceAccess(String orig_name) throws MalformedURLException, IOException
    {
        if (orig_name == null)
            orig_name = "";

        this.orig_name = orig_name;

        // Ensure that the source string is of the correct format
        // We nee to remove the extension because we swap chars
        // around below to allow flexibility in entering classes
        file_name = orig_name;
        if (file_name.endsWith(".java"))
        {
            file_name = file_name.substring(0, file_name.length()-5);
            query = FILE;
        }
        if (file_name.endsWith(".class"))
        {
            file_name = file_name.substring(0, file_name.length()-6);
            query = FILE;
        }

        // Change all separators for file separators for this platform
        file_name = file_name.replace('\\', File.separatorChar);
        file_name = file_name.replace('/', File.separatorChar);
        file_name = file_name.replace('.', File.separatorChar);
        if (file_name.endsWith(""+File.separatorChar))
            file_name = file_name.substring(0, file_name.length()-1);

        // Security: Stop them going back up the tree
        file_name = StringUtil.swap(file_name, "..", "");

        // Having worked out the file_name, the java_name uses dots
        java_name = file_name.replace(File.separatorChar, '.');

        // So are we reading from a zip or a filesystem?
        source = Project.getSourceRoot();
        if (source.getFile().endsWith(".zip") || source.getFile().endsWith(".jar"))
        {
            // Zip files use / as a separator.
            file_name = file_name.replace(File.separatorChar, '/');
            zip = new ZipFile(source.getFile());

            // It is possible to have a package Foo and a package
            // called Foo.java, we need to distinguish between them so we check
            // that we didn't start with a .java extension
            if (query != FILE)
            {
                if (file_name.equals(""))
                {
                    query = PACKAGE;
                }
                else
                {
                    ZipEntry entry = zip.getEntry(file_name+"/");
                    if (entry != null && entry.isDirectory())   query = PACKAGE;
                    else                                        query = INVALID;
                }
            }

            // If it is not a directory we still need to check that
            // there is an existing java file
            if (query != PACKAGE)
            {
                ZipEntry entry = zip.getEntry(file_name+".java");
                if (entry != null && !entry.isDirectory())  query = FILE;
                else                                        query = INVALID;
            }
        }
        else
        {
            // It is possible to have a package Foo and a package
            // called Foo.java, we need to distinguish between them so we check
            // that we didn't start with a .java extension
            if (query != FILE)
            {
                URL dir = NetUtil.lengthenURL(source, file_name);
                if (NetUtil.isDirectory(dir))    query = PACKAGE;
                else                                query = INVALID;
            }

            // If it is not a directory we still need to check that
            // there is an existing java file
            if (query != PACKAGE)
            {
                URL file = NetUtil.lengthenURL(source, file_name+".java");
                if (NetUtil.isFile(file))    query = FILE;
                else                            query = INVALID;
            }
        }
    }

    /**
    * What type of thing are we looking at - a source file, a package
    * or nothing.
    * @return One of INVALID, FILE, or PACKAGE
    */
    public int getType()
    {
        return query;
    }

    /**
    * The name of the Java class or package that we are looking at
    * The separator is '.' and there are no '.java' prefixes
    */
    public String getJavaName()
    {
        return java_name;
    }

    /**
    * The name of the Java class or package that we are looking at
    * The separator is '.' and there are no '.java' prefixes
    */
    public String getTitle()
    {
        switch (query)
        {
        case FILE:
            return "Source: "+java_name;

        case PACKAGE:
            if (java_name.equals(""))
                return "Default Package";
            else
                return "Package: "+java_name;

        case INVALID:
            return "Error: "+java_name;
        }

        return "Error";
    }

    /**
    * A String containing a URL where we can find the JavaDoc for this
    * file or package
    * @return The JavaDoc link
    */
    public String getJavaDocLink()
    {
        // Change the name to have slashes for the web
        String link_str = java_name.replace('.', '/');

        switch (query)
        {
        case FILE:
            return javadoc_base + link_str + ".html";

        case PACKAGE:
            if (link_str.equals(""))
                return javadoc_base + "overview-summary.html";
            else
                return javadoc_base + link_str + "/package-summary.html";

        default:
            return javadoc_base;
        }
    }

    /**
    * Get a BufferedReader with which to read the source file. If
    * this fails, it is probably becuase it is being called from an
    * incorrect state, if should only be called, (an will only work)
    * if getType() == FILE
    * @return A reader on the source file
    */
    public BufferedReader getSource() throws MalformedURLException, IOException
    {
        InputStream in = null;

        // So are we reading from a zip or a filesystem?
        if (zip != null)
        {
            ZipEntry entry = zip.getEntry(file_name+".java");
            if (entry == null) return null;
            in = zip.getInputStream(entry);
        }
        else
        {
            URL file = NetUtil.lengthenURL(source, file_name+".java");
            if (!NetUtil.isFile(file)) return null;
            in = file.openStream();
        }

        return new BufferedReader(new InputStreamReader(in));
    }

    /**
    * Get a BufferedReader with which to read package.html. If
    * this fails, it is probably becuase it is being called from an
    * incorrect state, if should only be called, (an will only work)
    * if getType() == PACKAGE
    * @return A reader on the package.html
    */
    public BufferedReader getPackageDoc() throws MalformedURLException, IOException
    {
        InputStream in = null;

        // So are we reading from a zip or a filesystem?
        if (zip != null)
        {
            String search;
            if (file_name.equals(""))   search = "overview.html";
            else                        search = file_name+"/"+"package.html";

            ZipEntry entry = zip.getEntry(search);
            if (entry == null) return null;
            in = zip.getInputStream(entry);
        }
        else
        {
            String doc_file;
            if (file_name.equals(""))   doc_file = "overview.html";
            else                        doc_file = "package.html";

            URL file = NetUtil.lengthenURL(source, file_name, doc_file);
            if (!NetUtil.isFile(file)) return null;
            in = file.openStream();
        }

        return new BufferedReader(new InputStreamReader(in));
    }

    /**
    * Get an array of the packages in this package. If
    * this fails, it is probably becuase it is being called from an
    * incorrect state, if should only be called, (an will only work)
    * if getType() == PACKAGE
    * @return An array of items in this package
    */
    public Vector getPackageChildren() throws MalformedURLException, IOException
    {
        Vector retcode = new Vector();
        findChildren();

        for (int i=0; i<contents.length; i++)
        {
            if (zip != null)
            {
                String search;
                if (file_name.equals(""))   search = contents[i]+"/";
                else                        search = file_name+"/"+contents[i]+"/";

                ZipEntry entry = zip.getEntry(search);
                if (entry != null && entry.isDirectory())
                    retcode.addElement(contents[i]);
            }
            else
            {
                File sub = new File(file, contents[i]);
                if (sub.isDirectory())
                    retcode.addElement(contents[i]);
            }
        }

        return retcode;
    }

    /**
    * Get an array of the files in this package. If
    * this fails, it is probably becuase it is being called from an
    * incorrect state, if should only be called, (an will only work)
    * if getType() == PACKAGE
    * @return An array of items in this package
    */
    public Vector getSourceChildren() throws MalformedURLException, IOException
    {
        Vector retcode = new Vector();
        findChildren();

        for (int i=0; i<contents.length; i++)
        {
            if (zip != null)
            {
                String search;
                if (file_name.equals(""))   search = contents[i];
                else                        search = file_name+"/"+contents[i];

                ZipEntry entry = zip.getEntry(search);
                if (entry != null && !entry.isDirectory() && contents[i].endsWith(".java"))
                    retcode.addElement(contents[i]);
            }
            else
            {
                File sub = new File(file, contents[i]);
                if (sub.isFile() && contents[i].endsWith(".java"))
                    retcode.addElement(contents[i]);
            }
        }

        return retcode;
    }

    /**
    * Fill in the array of files in this package
    */
    private void findChildren() throws MalformedURLException, IOException
    {
        if (contents != null) return;

        if (zip != null)
        {
            Vector vector = new Vector();

            // Grab the matches into a Vector
            int count = 0;
            Enumeration en = zip.entries();
            while (en.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry) en.nextElement();
                String name = entry.getName();

                // Do we have something that is down below us in the
                // directory hierachy somewhere?
                if (file_name.equals("") || name.startsWith(file_name))
                {
                    // Chop off the beginning path that we know about
                    if (!file_name.equals(""))
                        name = name.substring(file_name.length()+1);

                    // This is a grandchild if there is still any more slashes,
                    // unless there is only one, and it is at the end
                    if (name.indexOf("/") == -1 ||
                        (name.indexOf("/") == name.length()-1))
                    {
                        vector.addElement(name);
                    }
                }
            }

            // Turn the vector into an array, and chop off any trailing /s
            contents = new String[vector.size()];
            for (int i=0; i<contents.length; i++)
            {
                contents[i] = (String) vector.elementAt(i);
                if (contents[i].endsWith("/"))
                    contents[i] = contents[i].substring(0, contents[i].length()-1);
            }
        }
        else
        {
            URL full = NetUtil.lengthenURL(Project.getSourceRoot(), file_name);

            // The contents
            if (full.getProtocol().equals("file"))
            {
                file = new File(full.getFile());
                contents = file.list();
            }
            else
            {
                contents = new String[0];
            }
        }
    }

    /**
    * Accessor for the JavaDoc base URL
    * @param link The base for the JavaDoc package
    */
    public static void setJavaDocBase(String link)
    {
        javadoc_base = link;
        if (!javadoc_base.endsWith("/"))
            javadoc_base += "/";
    }

    /**
    * Accessor for the JavaDoc base URL
    * @return The base for the JavaDoc package
    */
    public static String getJavaDocBase()
    {
        return javadoc_base;
    }

    /** Is is an invalid request */
    public static final int INVALID = 0;

    /** Is is a file that we have to display */
    public static final int FILE = 1;

    /** Is is a package that we have to display */
    public static final int PACKAGE = 2;

    /** The source string */
    private static String javadoc_base = "";

    /** The source root URL */
    private URL source;

    /** The zip file (if the source is a zip) */
    private ZipFile zip;

    /** An array of all the files in this package - filled in by findChildren() */
    private String[] contents;

    /** The File that points at this package - filled in by findChildren() */
    private File file;

    /** The original query */
    private int query;

    /** The original query */
    private String orig_name;

    /** The original query */
    private String file_name;

    /** The original query */
    private String java_name;
}
