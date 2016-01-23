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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.common.options;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GetOptions parses an argument list for requested arguments given by an
 * OptionList.<br><br>
 * 
 * This supports short and long options:<br>
 * Short Options have the following characteristics.
 * <ul>
 * <li>A single dash, '-', starts a flag or a flag sequence. An example of a
 * flag is '-c' and a flag sequence is '-xyz'.</li>
 * <li>A flag may have a required argument. The flag may or may not be separated
 * by a space from it's argument. For example, both -fbar and -f bar are
 * acceptable.</li>
 * <li>A flag may have an optional argument. The flag must not be separated by a
 * space from it's optional argument. For example, -fbar is acceptable provides
 * bar as the argument, but -f bar has bar as a non-option argument.</li>
 * <li>These rules can combine. For example, -xyzfoo can be the same as -x -y -z
 * foo</li>
 * <li>If an Option expects an argument, then that argument can have a leading
 * '-'. That is, if -x requires an option then the argument -y can be given as
 * -x-y or -x -y.</li>
 * </ul>
 * 
 * Long Options have the following characteristics:
 * <ul>
 * <li>A double dash '--' starts a single flag. For example --print. Note, a
 * long option is typically descriptive, but can be a single character.</li>
 * <li>An argument may be given in one of two ways --file=filename or --file
 * filename. That is, separated by an '=' sign or whitespace.</li>
 * </ul>
 * Note:
 * <ul>
 * <li>Options can be repeated. What that means is up to the program.</li>
 * <li>The '--' sequence terminates argument processing.</li>
 * <li>A '-' by itself is not a flag.</li>
 * <li>Unrecognized flags are an error.</li>
 * <li>Unrecognized arguments are moved after the processed flags.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class GetOptions {
    public GetOptions(String programName, String[] args, OptionList programOptions) {
        this.programName = programName;
        this.args = args.clone();
        this.programOptions = programOptions;
        // Initially, we have not started to process an argument
        this.nonOptionArgs = new ArrayList<String>();
        this.suppliedOptions = new LinkedHashMap<Option, String>();

        parse();
    }

    /**
     * @return the programName
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * @param programName
     *            the programName to set
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    private void parse() {
        int nargs = args.length;
        int skip = 0;
        for (int i = 0; i < nargs; i += 1 + skip) {
            skip = 0;
            String nextArg = args[i];
            // All options are 2 or more characters long and begin with a '-'.
            // If this is a non-option then note it and advance
            if (nextArg.length() < 2 || nextArg.charAt(0) != '-') {
                nonOptionArgs.add(nextArg);
                continue;
            }

            // If we are at the end of all options, '--', we need to skip this
            // and copy what follows to the end
            if ("--".equals(nextArg)) {
                for (int j = i + 1; j < nargs; j++) {
                    nonOptionArgs.add(args[j]);
                }
                return;
            }

            // At this point we are on a short option, a short option sequence
            // or a long option.
            // Invariant: the length > 1.
            if (nextArg.charAt(1) == '-') {
                // Process a long argument
                // This can be of the form --flag or --flag argument or
                // --flag=argument
                int equalPos = nextArg.indexOf('=');
                String flag = (equalPos != -1) ? nextArg.substring(2, equalPos) : nextArg.substring(2);
                List<Option> opts = programOptions.getLongOptions(flag);
                int count = opts.size();
                if (count == 0) {
                    throw new IllegalArgumentException("Illegal option --" + flag);
                }
                if (count > 1) {
                    throw new IllegalArgumentException("Ambiguous option --" + flag);
                }
                Option option = opts.get(0);
                if (option.getArgumentType().equals(ArgumentType.NO_ARGUMENT)) {
                    // Add option with null argument to options
                    suppliedOptions.put(option, null);
                    continue;
                }
                // An argument is allowed or required
                if (equalPos != -1) {
                    // Add option with argument to options
                    // Check for empty argument
                    String argument = (equalPos + 1 < nextArg.length()) ? nextArg.substring(equalPos + 1) : "";
                    suppliedOptions.put(option, argument);
                    continue;
                }
                // An argument is required, so take the next one.
                if (option.getArgumentType().equals(ArgumentType.REQUIRED_ARGUMENT)) {
                    if (i + 1 < nargs) {
                        // Add option with following argument to options
                        String argument = args[i];
                        skip = 1;
                        suppliedOptions.put(option, argument);
                        continue;
                    }
                    throw new IllegalArgumentException("Option missing required argument");
                }
            } else {
                // Process a short argument or short argument sequence

                // for each letter after the '-'
                int shortSeqSize = nextArg.length();
                for (int j = 1; j < shortSeqSize; j++) {
                    char curChar = nextArg.charAt(j);
                    Option option = programOptions.getShortOption(curChar);
                    if (option == null) {
                        throw new IllegalArgumentException("Illegal option -" + curChar);
                    }
                    if (option.getArgumentType().equals(ArgumentType.NO_ARGUMENT)) {
                        // Add option with null argument to options
                        suppliedOptions.put(option, null);
                        continue;
                    }
                    // This option allows or requires an argument
                    if (j < shortSeqSize) {
                        // since there is stuff that follows the flag, it is the
                        // argument.
                        String argument = nextArg.substring(j + 1);
                        suppliedOptions.put(option, argument);
                        continue;
                    }
                    if (option.getArgumentType().equals(ArgumentType.REQUIRED_ARGUMENT)) {
                        if (i + 1 < nargs) {
                            // Add option with following argument to options
                            String argument = args[i];
                            skip = 1;
                            suppliedOptions.put(option, argument);
                            continue;
                        }
                        throw new IllegalArgumentException("Option missing required argument");
                    }
                }
            }
        }
    }

    /**
     * Swap adjacent blocks in an array.
     * 
     * @param array
     *            The array to modify in place
     * @param firstStart
     *            the index of the start of the first block
     * @param firstEnd
     *            the index of the end of the first block
     * @param secondEnd
     *            the index of the end of the second block. Note: the start of
     *            the second block is firstEnd + 1
     */
    public static void swap(Object[] array, int firstStart, int firstEnd, int secondEnd) {
        // Note: this is currently unused.
        // If we implement the traditional GNU extensions GetOpts interface we
        // will need it.
        // We copy the smaller block to the longer block.

        // The performance of this is linear with respect to the size of the
        // larger block.
        // If the blocks are equal the number of swaps is equal to the "larger"
        // block size otherwise it is one greater.

        // if the first block is smaller we start at the start of both and swap
        // Otherwise we start at the end of both and swap from the end to the
        // start

        // Set variables for the second block to be larger
        int sourcePos = firstStart;
        int destPos = firstEnd + 1;
        int increment = 1;
        int destStop = secondEnd;
        int firstSize = firstEnd - firstStart + 1;
        int secondSize = secondEnd - firstEnd;
        int swapCount = secondSize + 1;

        if (firstSize > secondSize) {
            // first block is bigger or equal
            sourcePos = secondEnd;
            destPos = firstEnd;
            destStop = firstStart;
            increment = -1;
            swapCount = firstSize + 1;
        }

        if (firstSize == secondSize) {
            swapCount--;
        }

        while (swapCount-- > 0) {
            Object temp = array[destPos];
            array[destPos] = array[sourcePos];
            array[sourcePos] = temp;
            if (sourcePos != destStop) {
                sourcePos += increment;
            }
            if (destPos != destStop) {
                destPos += increment;
            }

        }
    }

    // public static void main(String[] args)
    // {
    // String[] a = {"a","b","c","d","e"};
    // swap(a, 0, 2, 4);
    // swap(a, 0, 1, 4);
    // swap(a, 0, 0, 1);
    // swap(a, 0, 0, 1);
    // swap(a, 1, 2, 4);
    // swap(a, 1, 2, 4);
    // }

    private String programName;
    private String[] args;
    private OptionList programOptions;

    /**
     * The position in the array that is currently being studied.
     */
    private List<String> nonOptionArgs;
    private Map<Option, String> suppliedOptions;
}
