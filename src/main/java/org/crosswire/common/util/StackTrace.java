/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Unscramble the current stack, and present the data from it to the user in
 * various forms. This code is slightly dodgy in that it makes use of the way
 * exceptions print their stack traces, however it is probably a safe enough
 * assumption for the moment.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class StackTrace {
    /**
     * Generate a stack trace an model it
     */
    public StackTrace() {
        init(new Throwable(), 2);
    }

    /**
     * We already have an Exception that we'd like to model
     * 
     * @param ex
     *            The Exception to model
     */
    public StackTrace(Throwable ex) {
        init(ex, 1);
    }

    /**
     * Create a stack trace of the code at this point
     * 
     * @param exception
     *            The Throwable containing the Stack Trace
     * @param discard
     *            The number of uppermost stack frames to ignore
     */
    private void init(Throwable exception, int discard) {
        StringWriter sout = new StringWriter();
        exception.printStackTrace(new PrintWriter(sout));
        String msg = new String(sout.getBuffer());
        String[] calls = StringUtil.split(msg, "\n\r");

        int total = 0;
        for (int i = 0; i < calls.length - discard; i++) {
            String call = calls[i + discard];

            if (!(call.startsWith("Caused") || call.indexOf("...") >= 0)) {
                total++;
            }
        }

        classNames = new String[total];
        methodNames = new String[total];
        fileNames = new String[total];
        lineNumbers = new int[total];

        int j = 0;
        for (int i = 0; i < calls.length - discard; i++) {
            String call = calls[i + discard];
            boolean oops = false;
            try {
                if (!(call.startsWith("Caused") || call.indexOf("...") >= 0)) {
                    int spcIndex = call.indexOf(' ');
                    int lhsIndex = call.indexOf('(');
                    int clnIndex = call.indexOf(':');
                    int rhsIndex = call.indexOf(')');

                    String fullFn = call.substring(spcIndex + 1, lhsIndex).trim();
                    int lastDot = fullFn.lastIndexOf('.');

                    classNames[j] = fullFn.substring(0, lastDot).replace('/', '.');
                    methodNames[j] = fullFn.substring(lastDot + 1);

                    if (clnIndex != -1 && lhsIndex < clnIndex) {
                        fileNames[j] = call.substring(lhsIndex + 1, clnIndex);
                        lineNumbers[j] = Integer.parseInt(call.substring(clnIndex + 1, rhsIndex));
                    } else {
                        fileNames[j] = call.substring(lhsIndex + 1, rhsIndex);
                        lineNumbers[j] = 0;
                    }
                    j++;
                }
            } catch (NumberFormatException ex) {
                oops = true;
            } catch (StringIndexOutOfBoundsException ex) {
                // For whatever reason, Java 7 under Web Start is throwing this on
                // call.substring(spcIndex + 1, lhsIndex) with a -56 being passed.
                oops = true;
            }
            if (oops) {
                classNames[j] = "ParseError: ";
                methodNames[j] = call;
                fileNames[j] = "Error";
                lineNumbers[j] = 0;
                j++;
            }
        }
    }

    /**
     * How many stack elements are there?
     * 
     * @return the number of stack elements
     */
    public int countStackElements() {
        return methodNames.length;
    }

    /**
     * Get the name of a function
     * 
     * @param level
     *            Number of calling function
     * @return the function name
     */
    public String getFunctionName(int level) {
        return methodNames[level];
    }

    /**
     * Get the name of a function including class name
     * 
     * @param level
     *            Number of calling function
     * @return the full function name
     */
    public String getFullFunctionName(int level) {
        return classNames[level] + '.' + methodNames[level] + "()";
    }

    /**
     * Get the name of a class
     * 
     * @param level
     *            Number of calling function
     * @return the class name
     */
    public String getClassName(int level) {
        return classNames[level];
    }

    /**
     * Get the name of a file
     * 
     * @param level
     *            Number of calling function
     * @return the file name
     */
    public String getFileName(int level) {
        return fileNames[level];
    }

    /**
     * Get the line number within a file
     * 
     * @param level
     *            Number of calling function
     * @return the line number
     */
    public int getLineNumber(int level) {
        return lineNumbers[level];
    }

    /**
     * Get the count of classes
     * 
     * @return the number of classes
     */
    public int getClassCount() {
        return classNames.length;
    }

    /**
     * Get the Class that owns the function
     * 
     * @param level
     *            Number of calling function
     * @return the function owner
     */
    public Class<?> getClass(int level) {
        try {
            return ClassUtil.forName(classNames[level]);
        } catch (ClassNotFoundException ex) {
            assert false : ex;
            return null;
        }
    }

    /**
     * Base class for the real enumeration implementations below
     * @param <T> the type of the object in the stack
     */
    public abstract class AbstractStackIterator<T> implements Iterator<T> {
        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return level < getClassCount();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * @return the level.
         * @throws NoSuchElementException 
         */
        public int getAndIncrementLevel() throws NoSuchElementException {
            return level++;
        }

        /**
         * Are there more stack levels
         */
        private int level;
    }

    /**
     * To iterate over the class names
     * 
     * @return an iterator of class names
     */
    public Iterator<String> getClassNameElements() {
        return new AbstractStackIterator<String>() {
            public String next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return getClassName(getAndIncrementLevel());
            }
        };
    }

    /**
     * To iterate over the function names
     * 
     * @return an iterator of function names
     */
    public Iterator<String> getFunctionNameElements() {
        return new AbstractStackIterator<String>() {
            public String next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return getFunctionName(getAndIncrementLevel());
            }
        };
    }

    /**
     * To iterate over the full function names
     * 
     * @return an iterator of full function names
     */
    public Iterator<String> getFullFunctionNameElements() {
        return new AbstractStackIterator<String>() {
            public String next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return getFullFunctionName(getAndIncrementLevel());
            }
        };
    }

    /**
     * Array containing the class names
     */
    private String[] classNames;

    /**
     * Array containing the method names
     */
    private String[] methodNames;

    /**
     * Array containing the file names
     */
    private String[] fileNames;

    /**
     * Array containing the line numbers
     */
    private int[] lineNumbers;
}
