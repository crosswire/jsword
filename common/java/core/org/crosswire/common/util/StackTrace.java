package org.crosswire.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;

/**
 * Unscramble the current stack, and present the data from it to the
 * user in various forms. This code is slightly dodgy in that it
 * makes use of the way exceptions print their stack straces, however
 * it is probably a safe enough assumption for the moment.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
        String[] calls = StringUtils.split(msg, "\n\r"); //$NON-NLS-1$

        class_names = new String[calls.length-disgard];
        method_names = new String[calls.length-disgard];
        file_names = new String[calls.length-disgard];
        line_numbers = new int[calls.length-disgard];

        for (int i=0; i<class_names.length; i++)
        {
            String call = calls[i+disgard];

            try
            {
                int spc_index = call.indexOf(" "); //$NON-NLS-1$
                int lhs_index = call.indexOf("("); //$NON-NLS-1$
                int cln_index = call.indexOf(":"); //$NON-NLS-1$
                int rhs_index = call.indexOf(")"); //$NON-NLS-1$

                String full_fn = call.substring(spc_index+1, lhs_index).trim();
                int last_dot = full_fn.lastIndexOf("."); //$NON-NLS-1$

                class_names[i] = StringUtils.replace(full_fn.substring(0, last_dot), "/", "."); //$NON-NLS-1$ //$NON-NLS-2$
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
                class_names[i] = "ParseError: "; //$NON-NLS-1$
                method_names[i] = call;
                file_names[i] = "Error"; //$NON-NLS-1$
                line_numbers[i] = 0;
            }
        }
    }

    /**
     * How many stack elements are there?
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
        return class_names[level]+"."+method_names[level]+"()"; //$NON-NLS-1$ //$NON-NLS-2$
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
            assert false : ex;
            return null;
        }
    }

    /**
     * Base class for the real enumeration implementations below
     */
    public abstract class AbstractStackEnumeration implements Enumeration
    {
        /**
         * Are there more stack levels
         */
        public boolean hasMoreElements()
        {
            return level<class_names.length;
        }

        /**
         * Are there more stack levels
         */
        protected int level = 0;
    }

    /**
     * To itterate over the class names
     */
    public final Enumeration getClassNameElements()
    {
        return new AbstractStackEnumeration()
        {
            public Object nextElement()
            {
                return getClassName(level++);
            }
        };
    }

    /**
     * To itterate over the function names
     */
    public final Enumeration getFunctionNameElements()
    {
        return new AbstractStackEnumeration()
        {
            public Object nextElement()
            {
                return getFunctionName(level++);
            }
        };
    }

    /**
     * To itterate over the full function names
     */
    public final Enumeration getFullFunctionNameElements()
    {
        return new AbstractStackEnumeration()
        {
            public Object nextElement()
            {
                return getFullFunctionName(level++);
            }
        };
    }

    /**
     * Array containing the class names
     */
    protected String[] class_names;

    /**
     * Array containing the method names
     */
    private String[] method_names;

    /**
     * Array containing the file names
     */
    private String[] file_names;

    /**
     * Array containing the line numbers
     */
    private int[] line_numbers;
}
