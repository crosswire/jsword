
package org.crosswire.jsword.control.search;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.crosswire.jsword.book.Bibles;

/**
 * JUnit Test.
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
public class TestEngine extends TestCase
{
    public TestEngine(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testSearch() throws Exception
    {
        Hashtable commands = SearchDefault.getHashtable();
        Engine engine = new Engine(Bibles.getDefaultBible(), commands);
            try { engine.search("moses aaron"); fail(); } catch (SearchException ex) { }
        try { engine.search("("); fail(); } catch (SearchException ex) { }
        try { engine.search("~"); fail(); } catch (SearchException ex) { }
        try { engine.search(")"); fail(); } catch (SearchException ex) { }
        try { engine.search("&"); fail(); } catch (SearchException ex) { }
        try { engine.search(","); fail(); } catch (SearchException ex) { }
        try { engine.search("+"); fail(); } catch (SearchException ex) { }
        try { engine.search("-"); fail(); } catch (SearchException ex) { }
        try { engine.search("/"); fail(); } catch (SearchException ex) { }
        try { engine.search("|"); fail(); } catch (SearchException ex) { }
        try { engine.search("sw"); fail(); } catch (SearchException ex) { }
        try { engine.search("startswith"); fail(); } catch (SearchException ex) { }
        try { engine.search("gr"); fail(); } catch (SearchException ex) { }
        try { engine.search("grammar"); fail(); } catch (SearchException ex) { }
        try { engine.search("moses ( aaron )"); fail(); } catch (SearchException ex) { }
        try { engine.search("moses & ( aaron"); fail(); } catch (SearchException ex) { }
        try { engine.search("moses & ( aaron"); fail(); } catch (SearchException ex) { }
        try { engine.search("( moses ( aaron ) )"); fail(); } catch (SearchException ex) { }
    }

    public void testBestMatch() throws Exception
    {
        /*
        version = new FileBible();
        commands = Options.getSearchHashtable();
        engine = new Engine(version, commands);

        PassageTally tally = engine.bestMatch("for god so loved the world that he gave his one and only son");
        log.fine(tally.getName(10));
        */
    }
}
