
package org.crosswire.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
* A generic class of String utils.
* It would be good if we could put this stuff in java.lang ...
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
* @author Joe Walker
*/
public final class StringUtil
{
    /**
    * Create a debug version of an array by calling toString on all the
    * objects in the array.
    * @param array The array to debug
    * @return The debug version of the array
    */
    public static String toString(Object[] array)
    {
        StringBuffer buff = new StringBuffer("{ ");

        for (int i=0; i<array.length; i++)
        {
            if (i != 0)
                buff.append(", ");

            buff.append(array[i]);
        }

        buff.append(" }");
        return buff.toString();
    }

    /**
    * Returns an null if an empty string was passed in
    * @return a non-blank string.
    */
    public static String nullIfBlank(String param)
    {
        if (param == null) return null;
        return param.equals("") ? null : param;
    }

    /**
    * Returns an empty string if null was passed in
    * @return a valid (non-null) string.
    */
    public static String blankIfNull(String param)
    {
        return param == null ? "" : param;
    }

    /**
    * Returns the newline character
    * @return String containing a single newline.
    */
    public static String getNewline()
    {
        return System.getProperty("line.separator", "\r\n");
    }

    /**
    * Parses a String like "1 2 3 4" into a Byte Array like {1, 2, 3, 4}
    * @param command The string to parse.
    * @return A byte array containing the numbers in the string
    */
    public static byte[] tokenizeByte(String command) throws NumberFormatException
    {
        StringTokenizer tokenize = new StringTokenizer(command);
        byte[] retcode = new byte[tokenize.countTokens()];
        int i = 0;

        while (tokenize.hasMoreTokens())
        {
            String temp = tokenize.nextToken();
            retcode[i++] = (byte) Integer.parseInt(temp);
        }

        return retcode;
    }

    /**
    * Parses a String like "1 2 3 4" into a Byte Array like {1, 2, 3, 4}
    * @param command The string to parse.
    * @return A byte array containing the numbers in the string
    */
    public static short[] tokenizeShort(String command) throws NumberFormatException
    {
        StringTokenizer tokenize = new StringTokenizer(command);
        short[] retcode = new short[tokenize.countTokens()];
        int i = 0;

        while (tokenize.hasMoreTokens())
        {
            String temp = tokenize.nextToken();
            retcode[i++] = new Short(temp).shortValue();
        }

        return retcode;
    }

    /**
    * Take a string and parse it (by the default delimiters)
    * into an Array of Strings.
    * @param command The string to parse.
    * @return The number of items on the stack.
    */
    public static String[] tokenize(String command)
    {
        if (command == null) return null;

        StringTokenizer tokenize = new StringTokenizer(command);
        String[] args = new String[tokenize.countTokens()];
        int argc = 0;

        while (tokenize.hasMoreTokens())
        {
            args[argc++] = tokenize.nextToken();
        }

        return args;
    }

    /**
    * Take a string and parse it into an Array of Strings.
    * @param command The string to parse.
    * @param delim A string containing the spacing characters.
    * @return The string array
    */
    public static String[] tokenize(String command, String delim)
    {
        StringTokenizer tokenize = new StringTokenizer(command, delim);
        String[] args = new String[tokenize.countTokens()];
        int argc = 0;

        while (tokenize.hasMoreTokens())
        {
            args[argc++] = tokenize.nextToken();
        }

        return args;
    }

    /**
    * Take a string and parse it into an Array of Strings.
    * @param command The string to parse.
    * @param delim A string containing the spacing characters.
    * @param escape Ignore the delims that are preceeded by the escape char
    * @return The string array
    */
    public static String[] tokenize(String command, String delim, char escape)
    {
        Vector vec = new Vector();
        int start = 0;
        int curr = 0;

        // Ensure that the command ends with a delimitter.
        command += delim;

        // Skip to the next non-delimitter
        while (curr < command.length() && delim.indexOf(command.charAt(curr)) >= 0)
            curr++;
        start = curr;

        // Read the elements into the Vector
        while (curr < command.length())
        {
            char curchar = command.charAt(curr);

            // if we have an unescaped delimitter
            if (curr > 1 &&
                command.charAt(curr-1) != escape &&
                delim.indexOf(curchar) >= 0)
            {
                vec.addElement(command.substring(start, curr));

                // Skip to the next delimitter
                while (curr < command.length() && delim.indexOf(command.charAt(curr)) >= 0)
                    curr++;

                start = curr;
            }
            else
            {
                curr++;
            }
        }

        // Create a String[]
        String[] retcode = new String[vec.size()];
        int i = 0;
        for (Enumeration en = vec.elements(); en.hasMoreElements();)
        {
            retcode[i++] = (String) en.nextElement();
        }

        return retcode;
    }

    /**
    * Take a string and parse it taking note of string qualifiers
    * into an Array of Strings.
    * @param command The string to parse.
    * @param delim A string containing the spacing characters.
    * @param qual A string containing the text qualifiers (like " or ')
    * @return The string array
    */
    public static String[] tokenize(String command, String delim, String qual)
    {
        Vector vec = new Vector();
        char quoted = 0;
        int start = 0;
        int curr = 0;

        // Ensure that the command ends with a delimitter.
        command += delim;

        // Skip to the next non-delimitter
        while (curr < command.length() && delim.indexOf(command.charAt(curr)) >= 0)
            curr++;
        start = curr;

        // Read the elements into the Vector
        while (curr < command.length())
        {
            char curchar = command.charAt(curr);

            // if in quotes and this is an end quote
            if (quoted != 0 && curchar == quoted)
            {
                quoted = 0;
                curr++;
            }
            // This is a start quote
            else if (qual.indexOf(curchar) >= 0)
            {
                quoted = curchar;
                curr++;
            }
            // if we have a delimitter
            else if (quoted == 0 && delim.indexOf(curchar) >= 0)
            {
                vec.addElement(command.substring(start, curr));

                // Skip to the next delimitter
                while (curr < command.length() && delim.indexOf(command.charAt(curr)) >= 0)
                    curr++;

                start = curr;
            }
            else
            {
                curr++;
            }
        }

        // Create a String[]
        String[] retcode = new String[vec.size()];
        int i = 0;
        for (Enumeration en = vec.elements(); en.hasMoreElements();)
        {
            String next = (String) en.nextElement();
            retcode[i++] = removeChars(next, qual);
        }

        return retcode;
    }

    /**
    * Escapes certain characters from an original string.
    * @param orig The string to process
    */
    public static String escape(String orig)
    {
        StringBuffer buffer = new StringBuffer(orig.length()*2);

        for (int x=0; x<orig.length(); )
        {
            char current = orig.charAt(x++);
            switch (current)
            {
            case '\\':
                buffer.append('\\');
                buffer.append('\\');
                continue;
            case '\t':
                buffer.append('\\');
                buffer.append('t');
                continue;
            case '\n':
                buffer.append('\\');
                buffer.append('n');
                continue;
            case '\r':
                buffer.append('\\');
                buffer.append('r');
                continue;
            case '\f':
                buffer.append('\\');
                buffer.append('f');
                continue;
            default:
                if ((current < 20) || (current > 127))
                {
                    buffer.append('\\');
                    buffer.append('u');
                    buffer.append(toHexString(current));
                }
                else
                {
                    if ("=: \t\r\n\f#!".indexOf(current) != -1)
                        buffer.append('\\');
                    buffer.append(current);
                }
            }
        }

        return buffer.toString();
    }

    /**
    * Escapes certain characters from an original string.
    * @param orig The string to process
    */
    public static String[] escape(String[] orig)
    {
        String[] retcode = new String[orig.length];
        for (int i=0; i<orig.length; i++)
        {
            retcode[i] = escape(orig[i]);
        }
        return retcode;
    }

    /**
    * UnEscapes certain characters from an original string.
    * @param orig The string to process
    */
    public static String unescape(String orig)
    {
        int len = orig.length();
        StringBuffer buffer = new StringBuffer(len);

        for (int x=0; x<len; )
        {
            char current = orig.charAt(x++);
            if (current == '\\')
            {
                current = orig.charAt(x++);
                if (current == 'u')
                {
                    // Read the xxxx
                    int value=0;
                    for (int i=0; i<4; i++)
                    {
                        current = orig.charAt(x++);
                        switch (current)
                        {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            value = (value << 4) + current - '0';
                            break;

                        case 'a': case 'b': case 'c':
                        case 'd': case 'e': case 'f':
                            value = (value << 4) + 10 + current - 'a';
                            break;

                        case 'A': case 'B': case 'C':
                        case 'D': case 'E': case 'F':
                            value = (value << 4) + 10 + current - 'A';
                            break;

                        default:
                            throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }

                    buffer.append((char)value);
                }
                else
                {
                    if (current == 't') current = '\t';
                    else if (current == 'r') current = '\r';
                    else if (current == 'n') current = '\n';
                    else if (current == 'f') current = '\f';

                    buffer.append(current);
                }
            }
            else
            {
                buffer.append(current);
            }
        }

        return buffer.toString();
    }

    /**
    * UnEscapes certain characters from an original string.
    * @param orig The string to process
    */
    public static String[] unescape(String[] orig)
    {
        String[] retcode = new String[orig.length];
        for (int i=0; i<orig.length; i++)
        {
            retcode[i] = unescape(orig[i]);
        }
        return retcode;
    }

    /**
    * Search and replace in a String
    * @param orig The string to parse.
    * @param x The String to remove
    * @param y The String to replace it with
    * @return The string with the char replaced
    */
    public static String swap(String orig, String x, String y)
    {
        StringBuffer retcode = new StringBuffer();

        int start = 0;
        int current = 0;

        while (true)
        {
            current = orig.indexOf(x, start+1);
            if (current == -1) break;

            retcode.append(orig.substring(start, current));
            retcode.append(y);

            start = current + x.length();
        }

        retcode.append(orig.substring(start, orig.length()));

        return ""+retcode;
    }

    /**
    * Removes a character from a String
    * @param orig The string to parse.
    * @param x The char to remove
    * @return The string with the char removed
    */
    public static String removeChars(String orig, String x)
    {
        String retcode = "";
        for (int i=0; i<orig.length(); i++)
        {
            if (x.indexOf(orig.charAt(i)) < 0)
                retcode += orig.charAt(i);
        }
        return retcode;
    }

    /**
    * Removes a character from a String
    * @param orig The string to parse.
    * @param x The char to remove
    * @return The string with the char removed
    */
    public static String removeChar(String orig, char x)
    {
        StringBuffer retcode = new StringBuffer(orig.length());
        for (int i=0; i<orig.length(); i++)
        {
            if (orig.charAt(i) != x)
                retcode.append(orig.charAt(i));
        }
        return retcode.toString();
    }

    /**
    * Take a String array and create a String out of all elements
    * @param arr The Array of Strings to use as the source.
    * @param sep A string to place between the array elements. Can be "".
    * @return The String with all the stuff added together.
    */
    public static String cat(String[] arr, String sep)
    {
        String retcode = "";

        // Check for blank input array
        if (arr.length == 0)
            return "";

        // Do the adding together
        for (int i=0; i<arr.length; i++)
        {
            retcode += arr[i];
            if (i != arr.length-1) retcode += sep;
        }

        return retcode;
    }

    /**
    * Take a String array and create a String out of some elements.
    * EG:<pre>
    *   String[] arr = "a", "b", "c", "d", "e";
    *   StringUtil.cat(arr, 2, "-")) = "c-d-e"
    * </pre>
    * @param arr The Array of Strings to use as the source.
    * @param start The first element in the array to be used.
    * @param sep A string to place between the array elements. Can be "".
    * @return The String with all the stuff added together.
    */
    public static String cat(String[] arr, int start, String sep)
    {
        return cat(arr, start, arr.length-1, sep);
    }

    /**
    * Take a String array and create a String out of some elements.
    * EG:<pre>
    *   String[] arr = "a", "b", "c", "d", "e";
    *   StringUtil.cat(arr, 2, 3, "-")) = "c-d"
    * </pre>
    * @param arr The Array of Strings to use as the source.
    * @param start The first element in the array to be used.
    * @param end The last elemenet in the array to be used.
    * @param sep A string to place between the array elements. Can be "".
    * @return The String with all the stuff added together.
    */
    public static String cat(String[] arr, int start, int end, String sep)
    {
        String retcode = "";
        int dirn = 1;

        // Check the limits
        if (start > arr.length || end >= arr.length)
        {
            throw new IllegalArgumentException();
        }

        // Check for blank input array
        if (arr.length == 0)
            return "";

        // Are we going forwards or backwards?
        if (start > end) dirn = -1;

        // Do the adding together
        for (int i=start; i<=end; i+=dirn)
        {
            retcode += arr[i];
            if (i != end) retcode += sep;
        }

        return retcode;
    }

    /**
    * Run String.trim() on all the elements of this array.
    * @param cmd The array of Strings to be trimmed
    * @return The same array, with trim() called on all its elements
    */
    public static String[] trim(String[] cmd)
    {
        for (int i=0; i<cmd.length; i++)
        {
            cmd[i] = cmd[i].trim();
        }

        return cmd;
    }

    /**
    * Reduce the size of the array by dropping element zero
    * @param cmd The array to be narrowed
    * @return A new array which is a narrowed version of the original
    */
    public static String[] shift(String[] cmd)
    {
        String[] cmd2 = new String[cmd.length-1];
        System.arraycopy(cmd, 1, cmd2, 0, cmd2.length);
        return cmd2;
    }

    /**
    * Strips the text between a pair of delimitters. For example:
    * <code>chop("123(456)789", "(", ")") = "123789"</code>
    * Delimiters currently do not nest. So:
    * <code>chop("12(34(56)78)9", "(", ")") = Exception</code>
    */
    public static String chop(String orig, String start_delim, String end_delim)
    {
        while (true)
        {
            int next_start = orig.indexOf(start_delim);
            int next_end = orig.indexOf(end_delim);

            if (next_start == -1)
            {
                if (next_end == -1) break;
                else throw new IllegalArgumentException("Unmatched or nested delimitters");
            }
            if (next_end == -1)
                throw new IllegalArgumentException("Unmatched or nested delimitters");

            orig = orig.substring(0, next_start) +
                   orig.substring(next_end+end_delim.length());
        }

        return orig;
    }

    /**
    * This method reads an InputStream <b>In its entirety</b>, and passes
    * The text back as a string. If you are reading from a source that can
    * block then be preapred for a long wait for this to return.
    * @param in The Stream to read from.
    * @return A string containing all the text from the Stream.
    */
    public static String read(Reader in) throws IOException
    {
        StringBuffer retcode = new StringBuffer();
        String line = "";
        BufferedReader din = new BufferedReader(in);

        while (true)
        {
            line = din.readLine();

            if (line == null)
                break;

            retcode.append(line);
            retcode.append(NEWLINE);
        }

        return retcode.toString();
    }

    /**
    * Turns a Vector of Strings into an array of Strings
    * @param vec The vector to transform
    * @return The new array
    */
    public static String[] getStringArray(Vector vec)
    {
        String[] retcode = new String[vec.size()];

        // Do the adding together
        for (int i=0; i<vec.size(); i++)
        {
            retcode[i] = (String) vec.elementAt(i);
        }

        return retcode;
    }

    /**
    * Turns an array of Objects into an array of Strings
    * @param objs The array to transform
    * @return The new array
    */
    public static String[] getStringArray(Object[] objs)
    {
        String[] retcode = new String[objs.length];

        // Do the adding together
        for (int i=0; i<objs.length; i++)
        {
            retcode[i] = objs[i].toString();
        }

        return retcode;
    }

    /**
    * Create a string consisting of num chars
    * @param num The number of characters to return
    * @param ch The character to duplicate
    */
    public static String chain(int num, char ch)
    {
        String retcode = "";

        for (int i=0; i<num; i++)
            retcode += ch;

        return retcode;
    }

    /**
    * Create a string consisting of num chars
    * @param num The number of characters to return
    * @param ch The String to duplicate
    */
    public static String chain(int num, String str)
    {
        String retcode = "";

        for (int i=0; i<=(num/str.length()); i++)
            retcode += str;

        return retcode.substring(0, num);
    }

    /**
    * Ensure a string is of a fixed length by truncating it or
    * by adding spaces untill it is.
    * @param str The string to check
    * @param len The number of characters needed
    */
    public static String setLength(String str, int len)
    {
        int diff = len - str.length();

        if (diff == 0)  return str;
        if (diff < 0)   return str.substring(0, len);
        else            return str+chain(diff, ' ');
    }

    /**
    * Count the instances of a character in a string
    * @param str The string to search
    * @param ch The char to search for
    * @return The number of instances
    */
    public static int countInstancesOf(String str, char ch)
    {
        int retcode = 0;
        int pos = -1;

        do
        {
            pos = str.indexOf(ch, pos+1);
            if (pos == -1) break;
            retcode++;
        }
        while (true);

        return retcode;
    }

    /**
    * Helper for parsing Integers
    */
    public static int parseInt(String str, int defaultval)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException ex)
        {
            return defaultval;
        }
    }

    /**
    * This function creates a readable title from a
    * variable name type input. For example calling:
    *   StringUtil.createTitle("one_two") = "One Two"
    *   StringUtil.createTitle("oneTwo") = "One Two"
    */
    public static String createTitle(String variable)
    {
        StringBuffer retcode = new StringBuffer();
        boolean lastlower = false;
        boolean lastspace = true;

        for (int i=0; i<variable.length(); i++)
        {
            char c = variable.charAt(i);

            if (lastlower && Character.isUpperCase(c) && !lastspace)
            {
                retcode.append(' ');
            }

            lastlower = !Character.isUpperCase(c);

            if (lastspace)              c = Character.toUpperCase(c);
            if (c == '_')               c = ' ';
            if (!lastspace || c != ' ') retcode.append(c);

            lastspace = (c == ' ');
        }

        return ""+retcode;
    }

    /**
    * For example getInitials("Java DataBase Connectivity") = "JDC" and
    * getInitials("Church of England") = "CoE".
    * @param The phrase from which to get the initial letters.
    * @return The initial letters in the given words.
    */
    public static String getInitials(String words)
    {
        String[] worda = tokenize(words);

        StringBuffer retcode = new StringBuffer();
        for (int i=0; i<worda.length; i++)
        {
            retcode.append(worda[i].charAt(0));
        }

        return retcode.toString();
    }

    /**
    * For example getCapitals("Java DataBase Connectivity") = "JDBC" and
    * getCapitals("Church of England") = "CE".
    * A character is tested for capitalness using Character.isUpperCase
    * and not Character.isUpperCase, which seems to be less correct from
    * what I can see of the docs but works!
    * @param The phrase from which to get the capital letters.
    * @return The capital letters in the given words.
    * @see #getInitials(String)
    */
    public static String getCapitals(String words)
    {
        StringBuffer retcode = new StringBuffer();

        for (int i=0; i<words.length(); i++)
        {
            char c = words.charAt(i);
            if (Character.isUpperCase(c))
                retcode.append(c);
        }

        return retcode.toString();
    }

    /**
    * This function creates a Java style name from a
    * variable name type input. For example calling:
    *   StringUtil.createTitle("one_two") = "OneTwo"
    *   StringUtil.createTitle("oneTwo") = "OneTwo"
    */
    public static String createJavaName(String variable)
    {
        StringBuffer retcode = new StringBuffer();
        boolean newword = true;

        for (int i=0; i<variable.length(); i++)
        {
            char c = variable.charAt(i);

            if (Character.isLetterOrDigit(c))
            {
                if (newword)    retcode.append(Character.toUpperCase(c));
                else            retcode.append(c);
            }

            newword = !Character.isLetter(c);
        }

        return ""+retcode;
    }

    /**
    * This function creates a Java Class/Package name from a file name
    * and a classpath. For example:
    * StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.class", "C:\\src")
    *   = "fred.Bing"
    */
    public static String fileNameToJavaPackage(String filename, String classpath)
    {
        // Get all of the individual paths
        String paths[] = tokenize(classpath, File.pathSeparator);

        // Remove path prefix
        if (File.pathSeparator.equals("/"))
        {
            // Unix
            for (int i=0; i<paths.length; i++)
            {
                if (filename.startsWith(paths[i]))
                {
                    filename = filename.substring(paths[i].length());
                }
            }
        }
        else
        {
            // Windows
            for (int i=0; i<paths.length; i++)
            {
                if (filename.toUpperCase().startsWith(paths[i].toUpperCase()))
                {
                    filename = filename.substring(paths[i].length());
                }
            }

            // In case no matches, remove the drive letter
            // This is only needed for windows
            int colon = filename.indexOf(":");
            if (colon != -1)
                filename = filename.substring(colon+1);
        }

        // remove any starting filename separators
        if (filename.startsWith(""+File.separatorChar))
            filename = filename.substring(1);

        // remove trailing type names
        if (filename.endsWith(".class"))
            filename = filename.substring(0, filename.length()-6);

        if (filename.endsWith(".java"))
            filename = filename.substring(0, filename.length()-5);

        return swap(filename, File.separator, ".");
    }

    /**
    * This function creates a Java Package name from a
    * file name and the system default classpath. For example:
    *   StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.class") = "fred.Bing"
    * given a classpath of "C:\\src"
    */
    public static String fileNameToJavaPackage(String filename)
    {
        String classpath = System.getProperty("java.class.path", "");
        return fileNameToJavaPackage(filename, classpath);
    }

    /**
    * This function find the first matching filename for a Java class
    * file from the classpath, if none is found it returns null.
    */
    public static String findClasspathEntry(String classname, String classpath)
    {
        String full = null;

        String paths[] = tokenize(classpath, File.pathSeparator);
        for (int i=0; i<paths.length; i++)
        {
            // Search the jar
            if (paths[i].endsWith(".zip") || paths[i].endsWith(".jar"))
            {
                try
                {
                    String file_name = swap(classname, ".", "/") + ".class";
                    ZipFile zip = new ZipFile(paths[i]);
                    ZipEntry entry = zip.getEntry(file_name);

                    if (entry != null && !entry.isDirectory())
                    {
                        if (full != null && !full.equals(file_name))
                            log.warn("Warning duplicate "+classname+" found: "+full+" and "+paths[i]);
                        else
                            full = paths[i];
                    }
                }
                catch (IOException ex)
                {
                    // If that zip file failed, then ignore it and more on.
                }
            }
            else
            {
                // Search for the file
                String extra = swap(classname, ".", File.separator);

                if (!paths[i].endsWith(File.separator))
                    paths[i] = paths[i] + File.separator;

                String file_name = paths[i] + extra + ".class";

                if (new File(file_name).isFile())
                {
                    if (full != null && !full.equals(file_name))
                        log.warn("Warning duplicate "+classname+" found: "+full+" and "+paths[i]);
                    else
                        full = paths[i];
                }
            }
        }

        return full;
    }

    /**
    * This function find the first matching filename for a Java class
    * file from the classpath, if none is found it returns null.
    */
    public static String findClasspathEntry(String classname)
    {
        String classpath = System.getProperty("java.class.path", "");
        return findClasspathEntry(classname, classpath);
    }

    /**
    * Get a URL from a class name. If there are any problems then we
    * return null, and we don't throw an Exception.
    */
    public static URL getLocalURL(String name)
    {
        try
        {
            ClassLoader loader = StringUtil.class.getClassLoader();

            String us_leaf = StringUtil.swap(StringUtil.class.getName(), ".", "/") + ".class";
            String us_full = loader.getResource(us_leaf).toString();
            String base = us_full.substring(0, us_full.length()-us_leaf.length());

            return new URL(base + name);
        }
        catch (Exception ex)
        {
            // Reporter.informUser(StringUtil.class, ex);
            return null;
        }
    }

    /**
    * Convert a nibble to a hex character
    * @param nibble the nibble to convert.
    */
    public static char toHexChar(byte nibble)
    {
        return hexDigit[(nibble & 0xF)];
    }

    /**
    * Convert a int to a hex string
    * @param nibble the nibble to convert.
    */
    public static String toHexString(int i)
    {
        /*
        // This gives a StringIndexOutOfBoundsException on setCharAt(0, ...);
        // Huh?

        StringBuffer retcode = new StringBuffer(4);

        retcode.setCharAt(0, toHexChar((byte) (i >> 12)));
        retcode.setCharAt(1, toHexChar((byte) (i >> 8)));
        retcode.setCharAt(2, toHexChar((byte) (i >> 4)));
        retcode.setCharAt(3, toHexChar((byte) (i >> 0)));
        */

        StringBuffer retcode = new StringBuffer();

        retcode.append(toHexChar((byte) (i >> 12)));
        retcode.append(toHexChar((byte) (i >> 8)));
        retcode.append(toHexChar((byte) (i >> 4)));
        retcode.append(toHexChar((byte) (i >> 0)));

        return retcode.toString();
    }

    /** A table of hex digits */
    private static final char[] hexDigit =
    {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
    };

    /** The newline character */
    public static final String NEWLINE = System.getProperty("line.separator", "\r\n");

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.util");
}
