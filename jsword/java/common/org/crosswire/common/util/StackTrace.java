
package org.crosswire.common.util;

import java.io.*;
import java.util.*;

/**
* Unscramble the current stack, and present the data from it to the
* user in various forms. This code is slightly dodgy in that it
* makes use of the way exceptions print their stack straces, however
* it is probably a safe enough assumption for the moment.
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
public final class StackTrace
{
    /**
    * Generate a stack trace an model it
    */
    public StackTrace()
    {
        init(new Throwable(), 2);
    }

    /**
    * We already have an Exception that we'd like to model
    * @param ex The Exception to model
    */
    public StackTrace(Throwable ex)
    {
        init(ex, 1);
    }

    /**
    * Create a stack trace of the code at this point
    * @param ex The Throwable containing the Stack Trace
    * @param disgard The number of uppermost stack frames to ignore
    */
    private void init(Throwable ex, int disgard)
    {
        StringWriter str = new StringWriter();
        ex.printStackTrace(new PrintWriter(str));
        String msg = new String(str.getBuffer());
        String[] calls = StringUtil.tokenize(msg, "\n\r");

        class_names = new String[calls.length-disgard];
        method_names = new String[calls.length-disgard];
        file_names = new String[calls.length-disgard];
        line_numbers = new int[calls.length-disgard];

        for (int i=0; i<class_names.length; i++)
        {
            String call = calls[i+disgard];

            try
            {
                int spc_index = call.indexOf(" ");
                int lhs_index = call.indexOf("(");
                int cln_index = call.indexOf(":");
                int rhs_index = call.indexOf(")");

                String full_fn = call.substring(spc_index+1, lhs_index).trim();
                int last_dot = full_fn.lastIndexOf(".");

                class_names[i] = StringUtil.swap(full_fn.substring(0, last_dot), "/", ".");
                method_names[i] = full_fn.substring(last_dot+1);

                if (cln_index != -1)
                {
                    file_names[i] = call.substring(lhs_index+1, cln_index);
                    line_numbers[i] = Integer.parseInt(call.substring(cln_index+1, rhs_index));
                }
                else
                {
                    file_names[i] = call.substring(lhs_index+1, rhs_index);
                    line_numbers[i] = 0;
                }
            }
            catch (Exception ex2)
            {
                class_names[i] = "ParseError: ";
                method_names[i] = call;
                file_names[i] = "Error";
                line_numbers[i] = 0;
            }
        }
    }

    /**
    * Get the name of a function
    * @param level Number of calling function
    */
    public final int countStackElements()
    {
        return method_names.length;
    }

    /**
    * Get the name of a function
    * @param level Number of calling function
    */
    public final String getFunctionName(int level)
    {
        return method_names[level];
    }

    /**
    * Get the name of a function including class name
    * @param level Number of calling function
    */
    public final String getFullFunctionName(int level)
    {
        return class_names[level]+"."+method_names[level]+"()";
    }

    /**
    * Get the name of a class
    * @param level Number of calling function
    */
    public final String getClassName(int level)
    {
        return class_names[level];
    }

    /**
    * Get the name of a file
    * @param level Number of calling function
    */
    public final String getFileName(int level)
    {
        return file_names[level];
    }

    /**
    * Get the line number within a file
    * @param level Number of calling function
    */
    public final int getLineNumber(int level)
    {
        return line_numbers[level];
    }

    /**
    * Get the Class that owns the function
    * @param level Number of calling function
    */
    public final Class getClass(int level)
    {
        try
        {
            return Class.forName(class_names[level]);
        }
        catch (ClassNotFoundException ex)
        {
            throw new LogicError();
        }
    }

    /**
    * Base class for the real enumeration implementations below
    */
    public abstract class AbstractStackEnumeration implements Enumeration
    {
        /** Are there more stack levels */
        public boolean hasMoreElements()
        {
            return level<class_names.length;
        }

        /** Are there more stack levels */
        int level = 0;
    }

    /**
    * To itterate over the class names
    */
    public final Enumeration getClassNameElements()
    {
        return new AbstractStackEnumeration()
        {
            public Object nextElement() { return getClassName(level++); }
        };
    }

    /**
    * To itterate over the function names
    */
    public final Enumeration getFunctionNameElements()
    {
        return new AbstractStackEnumeration()
        {
            public Object nextElement() { return getFunctionName(level++); }
        };
    }

    /**
    * To itterate over the full function names
    */
    public final Enumeration getFullFunctionNameElements()
    {
        return new AbstractStackEnumeration()
        {
            public Object nextElement() { return getFullFunctionName(level++); }
        };
    }

    /** Array containing the class names */
    private String[] class_names;

    /** Array containing the method names */
    private String[] method_names;

    /** Array containing the file names */
    private String[] file_names;

    /** Array containing the line numbers */
    private int[] line_numbers;
}
