package org.crosswire.common.util;

/**
 * This various different levels that people can log at.
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
public class Level
{
    /** Log level: emergency */
    public static final int SEVERE = 1;

    /** Log level: warning */
    public static final int WARNING = 2;

    /** Log level: info */
    public static final int CONFIG = 3;

    /** Log level: debug */
    public static final int INFO = 4;

    /** Log level: debug */
    public static final int FINE = 5;

    /** Log level: debug */
    public static final int FINER = 6;

    /** Log level: debug */
    public static final int FINEST = 7;
}