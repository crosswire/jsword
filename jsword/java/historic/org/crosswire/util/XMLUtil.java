
package org.crosswire.util;

import java.io.*;

import org.xml.sax.*;
import org.w3c.dom.*;

import org.crosswire.util.StringUtil;

/**
* The XMLUtil class does general stuff that I need in various places
* to do with XML.
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
* @version D0.I0.T0
*/
public class XMLUtil
{
    /**
    * Basic constructor
    */
    private XMLUtil()
    {
    }

    /**
    * Display a Document in debug mode to the specified writer
    * @param doc The Document to write
    * @param out The stream to write to
    */
    public static void logDocument(Document doc, int level)
    {
        Node node = doc.getDocumentElement();

        recurseNodes(node, null, level, 0);
    }

    /**
    * Display a Document in debug mode to the specified writer
    * @param doc The Document to write
    * @param out The stream to write to
    */
    public static void logDocument(Element start, int level)
    {
        recurseNodes(start, null, level, 0);
    }

    /**
    * Display a Document in debug mode to the specified writer
    * @param doc The Document to write
    * @param out The stream to write to
    */
    public static void printDocument(Document doc, PrintWriter out)
    {
        Node node = doc.getDocumentElement();

        recurseNodes(node, out, -1, 0);
    }

    /**
    * Display a Document in debug mode to the specified writer
    * @param start The Element to start writing at
    * @param out The stream to write to
    */
    public static void printDocument(Element start, PrintWriter out)
    {
        recurseNodes(start, out, -1, 0);
    }

    /**
    * Recurse down a Doument node tree
    * @param node The node to dig into
    * @param out The place to write the text that we find
    * @param depth How far down have we gone?
    */
    private static void recurseNodes(Node node, PrintWriter out, int level, int depth)
    {
        StringBuffer buff = new StringBuffer();

        switch (node.getNodeType())
        {
        case Node.TEXT_NODE:
            String text = node.getNodeValue().trim();
            if (text.length() != 0)
            {
                buff.append(StringUtil.chain(depth*2, '.'));
                buff.append(text);
            }
            break;

        case Node.CDATA_SECTION_NODE:
            buff.append(StringUtil.chain(depth*2, '.'));
            buff.append("<![CDATA[");
            buff.append(node.getNodeValue());
            buff.append("]]>");
            break;

        case Node.COMMENT_NODE:
            buff.append(StringUtil.chain(depth*2, '.'));
            buff.append("<!-- ");
            buff.append(node.getNodeValue());
            buff.append(" -->");
            break;

        case Node.ELEMENT_NODE:
            buff.append(StringUtil.chain(depth*2, '.'));
            buff.append("<");
            buff.append(node.getNodeName());

            // The attributes
            NamedNodeMap map = node.getAttributes();
            if (map != null)
            {
                for (int i=0; i<map.getLength(); i++)
                {
                    buff.append(" ");
                    buff.append(map.item(i).getNodeName());
                    buff.append("='");
                    buff.append(map.item(i).getNodeValue());
                    buff.append("'");
                }
            }

            // Children
            NodeList list = node.getChildNodes();

            if (list == null || list.getLength() == 0)
            {
                buff.append("/>");
                buff.append(StringUtil.getNewline());
            }
            else
            {
                buff.append(">");
                buff.append(StringUtil.getNewline());

                for (int i=0; i<list.getLength(); i++)
                {
                    recurseNodes(list.item(i), out, level, depth+1);
                }

                buff.append(StringUtil.chain(depth*2, '.'));
                buff.append("</");
                buff.append(node.getNodeName());
                buff.append(">");
                buff.append(StringUtil.getNewline());
            }
            break;

        default:
            buff.append(StringUtil.chain(depth*2, ' '));
            buff.append("Not sure what to do with node of type ");
            buff.append(node.getNodeType());
        }

        if (out != null)
        {
            out.println(buff.toString());
            out.flush();
        }
        else
        {
            log.log(level, buff.toString());
        }
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.xml");
}
