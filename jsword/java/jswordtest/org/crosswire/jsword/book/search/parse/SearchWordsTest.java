
package org.crosswire.jsword.book.search.parse;

import java.util.Map;

import junit.framework.TestCase;

import org.crosswire.jsword.book.Search;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class SearchWordsTest extends TestCase
{
    public SearchWordsTest(String s)
    {
        super(s);
    }

    private LocalParser engine = null;

    protected void setUp() throws Exception
    {
        Map commands = SearchDefault.getMap();

        commands.put("t1", new FixtureParamWord("Rut 2"));
        commands.put("t2", new FixtureParamWord("Deu 28-1Sa 1:1"));
        commands.put("t3", new FixtureParamWord("Mar 2:3"));

        // We shouldn't need a SearchableBible here because all of these should
        // complete without any searching being done.
        engine = new LocalParser();
        engine.init(null);
        engine.setSearchMap(commands);        
    }

    protected void tearDown() throws Exception
    {
    }

    public void testAddCommandWord() throws Exception
    {
        assertEquals(engine.search(new Search("t1", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t2", false)).getName(), "Deu 28:1-1Sa 1:1");
        assertEquals(engine.search(new Search("/t1", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("/t2", false)).getName(), "Deu 28:1-1Sa 1:1");
        assertEquals(engine.search(new Search("|t1", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("|t2", false)).getName(), "Deu 28:1-1Sa 1:1");
    }

    public void testRetainCommandWord() throws Exception
    {
        assertEquals(engine.search(new Search("t2&t1", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t1&t2", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t2+t1", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t1+t2", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t2,t1", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t1,t2", false)).getName(), "Rut 2");
    }

    public void testRemoveCommandWord() throws Exception
    {
        assertEquals(engine.search(new Search("t2-t1", false)).getName(), "Deu 28-Rut 1, Rut 3:1-1Sa 1:1");
        assertEquals(engine.search(new Search("t1-t2", false)).getName(), "");
    }

    public void testBlurCommandWord() throws Exception
    {
        assertEquals(engine.search(new Search("t3 ~1", false)).getName(), "Mar 2:2-4");
        assertEquals(engine.search(new Search("t3 ~2", false)).getName(), "Mar 2:1-5");
        assertEquals(engine.search(new Search("t3 ~3", false)).getName(), "Mar 2:1-6");
        assertEquals(engine.search(new Search("t3 ~4", false)).getName(), "Mar 2:1-7");
        assertEquals(engine.search(new Search("t3 ~5", false)).getName(), "Mar 2:1-8");
    }

    public void testStartsParamWord() throws Exception
    {
        //assertEquals(engine.search(new Search("startswith joshu", false)), engine.search(new Search("joshua", false)));
        //assertEquals(engine.search(new Search("sw joshu", false)), engine.search(new Search("joshua", false)));
    }

    public void testSubXParamWord() throws Exception
    {
        assertEquals(engine.search(new Search("t3 / ( t2 )", false)).getName(), "Deu 28:1-1Sa 1:1, Mar 2:3");
        assertEquals(engine.search(new Search("t3/(t2)", false)).getName(), "Deu 28:1-1Sa 1:1, Mar 2:3");
        assertEquals(engine.search(new Search("t1 & t2 | t3", false)).getName(), "Rut 2, Mar 2:3");
        assertEquals(engine.search(new Search("t1 & t2 - t3", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("( t1 & t2 ) | t3", false)).getName(), "Rut 2, Mar 2:3");
        assertEquals(engine.search(new Search("t1 & ( t2 | t3 )", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t1 & ( t2 | t3 ) & ( t3 | t2 )", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t1&(t2|t3)&(t3|t2)", false)).getName(), "Rut 2");
        assertEquals(engine.search(new Search("t1&(t2|(t3))&(t3|t2)", false)).getName(), "Rut 2");
    }

    public void testGetAlternatives() throws Exception
    {
        /*
        GrammarParamWord grammar = new GrammarParamWord();
        String[] temp = grammar.getAlternatives("joseph");
        assertEquals(temp[0], "joseph");
        assertEquals(temp[1], "joseph's");
        assertEquals(temp.length, 2);
        temp = grammar.getAlternatives("joseph's");
        assertEquals(temp[0], "joseph");
        assertEquals(temp[1], "joseph's");
        assertEquals(temp.length, 2);
        */
    }

    public void testUpdatePassage() throws Exception
    {
        //assertEquals(engine.search(new Search("grammar joseph", false)), engine.search(new Search("joseph / joseph's", false)));
        //assertEquals(engine.search(new Search("new Search(gr joseph", false)), engine.search(new Search("joseph / joseph's", false)));
    }
}
