
package org.crosswire.jsword.control.search;

import java.util.Map;

import junit.framework.TestCase;

import org.crosswire.jsword.book.Defaults;

/**
 * JUnit Test.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
        Map commands = SearchDefault.getMap();
        Engine engine = new Engine(Defaults.getBibleMetaData().getBible(), commands);
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
