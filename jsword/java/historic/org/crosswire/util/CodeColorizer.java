
package org.crosswire.util;

import java.io.*;
import java.util.*;

/**
 * CodeViewer.java
 *
 * Bill Lynch & Matt Tucker
 * CoolServlets.com, July 1999
 *
 * Any errors or suggested improvements to this servlet can be reported
 * as instructed on Coolservlets.com. We hope you enjoy
 * this program... your comments will encourage further development!
 *
 *    Copyright (C) 1999  Bill Lynch & Matt Tucker
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 */
/**
 * Syntax highlights java by turning it into html.
 *
 * A codeviewer object is created and then keeps state as lines are passed
 * in. Each line passed in as java test, is returned as syntax highlighted
 * html text.
 *
 * Users of the class can set how the java code will be highlighted with setter
 * methods.
 *
 * Only valid java lines should be passed in since the object maintains state
 * and may not handle illegal code gracefully.
 *
 * The actual system is implemented as a series of filters that deal with
 * specific portions of the java code. The filters are as follows:
 *
 *  htmlFilter
 *     |__
 *        multiLineCommentFilter
 *           |___
 *                inlineCommentFilter
 *                   |___
 *                        stringFilter
 *                           |__
 *                               keywordFilter
 */
public class CodeColorizer implements Serializable
{
    /**
    * Set up a CodeColorizer
    */
    public CodeColorizer()
    {
        loadHash();
    }

    /**
    * Now different method of seeing if at end of input stream,
    * closes inputs stream at end.
    */
    public String syntaxHighlight(String line)
    {
        return htmlFilter(line);
    }

    public void setCommentStart(String commentStart)
    {
        this.commentStart = commentStart;
    }

    public void setCommentEnd(String commentEnd)
    {
        this.commentEnd = commentEnd;
    }

    public void setStringStart(String stringStart)
    {
        this.stringStart = stringStart;
    }

    public void setStringEnd(String stringEnd)
    {
        this.stringEnd = stringEnd;
    }

    public void setReservedWordStart(String reservedWordStart)
    {
        this.reservedWordStart = reservedWordStart;
    }

    public void setReservedWordEnd(String reservedWordEnd)
    {
        this.reservedWordEnd = reservedWordEnd;
    }

    public String getCommentStart()
    {
        return commentStart;
    }

    public String getCommentEnd()
    {
        return commentEnd;
    }

    public String getStringStart()
    {
        return stringStart;
    }

    public String getStringEnd()
    {
        return stringEnd;
    }

    public String getReservedWordStart()
    {
        return reservedWordStart;
    }

    public String getReservedWordEnd()
    {
        return reservedWordEnd;
    }

    /**
    * Filter html tags into more benign text.
    */
    private String htmlFilter(String line)
    {
        StringBuffer buf = new StringBuffer();
        if (line == null || line.equals(""))
            return "";

        line = replace(line,"&","&amp;");
        line = replace(line,"<","&lt;");
        line = replace(line,">","&gt;");
        line = replace(line, "\\\\", "&#47;&#47;");
        line = replace(line, "\\\"", "\\&quot;");
        line = replace(line, "'\"'", "'&quot;'");

        return multiLineCommentFilter(line);
    }

    /**
    * Filter out multiLine comments. State is kept with a private boolean
    * variable.
    */
    private String multiLineCommentFilter(String line)
    {
        if (line == null || line.equals(""))
            return "";

        StringBuffer buf = new StringBuffer();
        int index;

        // First, check for the end of a multi-line comment.
        if (inMultiLineComment &&
            (index = line.indexOf("*/")) > -1 &&
            !isInsideString(line,index))
        {
            inMultiLineComment = false;

            buf.append(line.substring(0,index));
            buf.append("*/").append(commentEnd);
            if (line.length() > index+2)
                buf.append(inlineCommentFilter(line.substring(index+2)));

            return buf.toString();
        }
        else if (inMultiLineComment)
        {
            // If there was no end detected and we're currently in a multi-line
            // comment, we don't want to do anymore work, so return line.

            return line;
        }
        else if ((index = line.indexOf("/*")) > -1 &&
                 !isInsideString(line,index))
        {
            // We're not currently in a comment, so check to see if the start
            // of a multi-line comment is in this line.
            inMultiLineComment = true;

            // Return result of other filters + everything after the start
            // of the multiline comment. We need to pass the through the
            // to the multiLineComment filter again in case the comment ends
            // on the same line.
            buf.append(inlineCommentFilter(line.substring(0,index)));
            buf.append(commentStart).append("/*");
            buf.append(multiLineCommentFilter(line.substring(index+2)));

            return buf.toString();
        }
        else
        {
            // Otherwise, no useful multi-line comment information was found so
            // pass the line down to the next filter for processesing.

            return inlineCommentFilter(line);
        }
    }

    /**
    * Filter inline comments from a line and formats them properly.
    * One problem we'll have to solve here: comments contained in a string
    * should be ignored... this is also true of the multiline comments. So,
    * we could either ignore the problem, or implement a function called
    * something like isInsideString(line, index) where index points to
    * some point in the line that we need to check... started doing this
    * function below.
    */
    private String inlineCommentFilter(String line)
    {
        if (line == null || line.equals(""))
            return "";

        StringBuffer buf = new StringBuffer();
        int index;

        if ((index = line.indexOf("//")) > -1 &&
            !isInsideString(line,index))
        {
            buf.append(stringFilter(line.substring(0,index)));
            buf.append(commentStart);
            buf.append(line.substring(index));
            buf.append(commentEnd);
        }
        else
        {
            buf.append(stringFilter(line));
        }

        return buf.toString();
    }

    /**
    * Filters strings from a line of text and formats them properly.
    */
    private String stringFilter(String line)
    {
        if (line == null || line.equals(""))
            return "";

        if (line.indexOf("\"") <= -1)
            return keywordFilter(line);

        StringBuffer buf = new StringBuffer();
        int start = 0;
        int startStringIndex = -1;
        int endStringIndex = -1;
        int tempIndex;

        // Keep moving through String characters until we want to stop...
        while ((tempIndex = line.indexOf("\"")) > -1)
        {
            // We found the beginning of a string
            if (startStringIndex == -1)
            {
                startStringIndex = 0;
                buf.append(stringFilter(line.substring(start,tempIndex)));
                buf.append(stringStart).append("\"");
                line = line.substring(tempIndex+1);
            }
            else
            {
                //Must be at the end
                startStringIndex = -1;
                endStringIndex = tempIndex;
                buf.append(line.substring(0,endStringIndex+1));
                buf.append(stringEnd);
                line = line.substring(endStringIndex+1);
            }
        }

        buf.append( keywordFilter(line));

        return buf.toString();
    }

    /**
    * Filters keywords from a line of text and formats them properly.
    */
    private String keywordFilter(String line)
    {
        if (line == null || line.equals(""))
            return "";

        StringBuffer buf = new StringBuffer();

        //HashMap usedReservedWords = new HashMap();
        Hashtable usedReservedWords = new Hashtable();

        int i = 0;
        int startAt = 0;
        char ch;
        StringBuffer temp = new StringBuffer();

        while (i < line.length())
        {
            temp.setLength(0);
            ch = line.charAt(i);
            startAt = i;

            while (i<line.length() &&
                   ((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122)))
            {
                temp.append(ch);
                i++;

                if (i < line.length())
                    ch = line.charAt(i);
            }

            String tempString = temp.toString();
            if (reservedWords.containsKey(tempString) && !usedReservedWords.containsKey(tempString))
            {
                usedReservedWords.put(tempString,tempString);
                line = replace (line, tempString, (reservedWordStart+tempString+reservedWordEnd));
                i += (reservedWordStart.length() + reservedWordEnd.length());
            }
            else
            {
                i++;
            }
        }
        buf.append(line);
        return buf.toString();
    }

    /**
    *  Replace...
    *  I made it use a stringBuffer... hope it still works :)
    */
    private String replace(String line, String oldString, String newString)
    {
        int i=0;

        while ((i = line.indexOf(oldString, i)) >= 0)
        {
            line = (new StringBuffer().append(line.substring(0,i)).append(newString).append(line.substring(i+oldString.length()))).toString();
            i += newString.length();
        }

        return line;
    }

    /**
    * Checks to see if some position in a line is between String start and
    * ending characters. Not yet used in code or fully working :)
    */
    private boolean isInsideString(String line, int position)
    {
        if (line.indexOf("\"") < 0)
            return false;

        int index;
        String left = line.substring(0,position);
        String right = line.substring(position);
        int leftCount = 0;
        int rightCount = 0;

        while ((index = left.indexOf("\"")) > -1)
        {
            leftCount ++;
            left = left.substring(index+1);
        }

        while ((index = right.indexOf("\"")) > -1)
        {
            rightCount ++;
            right = right.substring(index+1);
        }

        if (rightCount % 2 != 0 && leftCount % 2 != 0)
            return true;
        else
            return false;
    }

    private void loadHash()
    {
        reservedWords.put("abstract", "abstract");
        reservedWords.put("do", "do");
        reservedWords.put("inner", "inner");
        reservedWords.put("public", "public");
        reservedWords.put("var", "var");
        reservedWords.put("boolean", "boolean");
        reservedWords.put("continue", "continue");
        reservedWords.put("int", "int");
        reservedWords.put("return", "return");
        reservedWords.put( "void", "void");
        reservedWords.put("break", "break");
        reservedWords.put("else", "else");
        reservedWords.put("interface", "interface");
        reservedWords.put("short", "short");
        reservedWords.put("volatile", "volatile");
        reservedWords.put("byvalue", "byvalue");
        reservedWords.put("extends", "extends");
        reservedWords.put("long", "long");
        reservedWords.put("static", "static");
        reservedWords.put("while", "while");
        reservedWords.put("case", "case");
        reservedWords.put("final", "final");
        reservedWords.put("naive", "naive");
        reservedWords.put("super", "super");
        reservedWords.put("transient", "transient");
        reservedWords.put("cast", "cast");
        reservedWords.put("float", "float");
        reservedWords.put("new", "new");
        reservedWords.put("rest", "rest");
        reservedWords.put("catch", "catch");
        reservedWords.put("for", "for");
        reservedWords.put("null", "null");
        reservedWords.put("synchronized", "synchronized");
        reservedWords.put("char", "char");
        reservedWords.put("finally", "finally");
        reservedWords.put("operator", "operator");
        reservedWords.put("this", "this");
        reservedWords.put("class", "class");
        reservedWords.put("generic", "generic");
        reservedWords.put("outer", "outer");
        reservedWords.put("switch", "switch");
        reservedWords.put("const", "const");
        reservedWords.put("goto", "goto");
        reservedWords.put("package", "package");
        reservedWords.put("throw", "throw");
        reservedWords.put("double", "double");
        reservedWords.put("if", "if");
        reservedWords.put("private", "private");
        reservedWords.put("true", "true");
        reservedWords.put("default", "default");
        reservedWords.put("import", "import");
        reservedWords.put("protected", "protected");
        reservedWords.put("try", "try");
    }

    void writeObject(ObjectOutputStream oos) throws IOException
    {
        oos.defaultWriteObject();
    }

    void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
    {
        ois.defaultReadObject();
    }

    private BufferedReader in;
    private StringBuffer out;
    //private HashMap reservedWords = new HashMap();
    private Hashtable reservedWords = new Hashtable();
    private boolean inMultiLineComment = false;
    private String backgroundColor = "#ffffff";
    private String commentStart = "<font color='#009900'>";
    private String commentEnd = "</font>";
    private String stringStart = "<font color='#0000aa'>";
    private String stringEnd = "</font>";
    private String reservedWordStart = "<b>";
    private String reservedWordEnd = "</b>";
}
