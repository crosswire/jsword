package org.crosswire.jsword.book.search.parse;

import java.util.Map;

import junit.framework.TestCase;

import org.crosswire.jsword.book.BookException;
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
public class ParserTest extends TestCase
{
    public ParserTest(String s)
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

        // We shouldn't need a SearchableBible here because all of these should
        // fail before any searching is done.
        LocalParser engine = new LocalParser();
        engine.init(null);
        engine.setSearchMap(commands);        

        //try { engine.search(new Search("moses aaron", false)); fail(); } catch (BookException ex) { }
        try { engine.search(new Search("(", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("~", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search(")", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("&", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search(",", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("+", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("-", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("/", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("|", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("sw", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("startswith", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("gr", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        try { engine.search(new Search("grammar", false)); fail(); } catch (BookException ex) { } //$NON-NLS-1$
        //try { engine.search(new Search("moses ( aaron )", false)); fail(); } catch (BookException ex) { }
        //try { engine.search(new Search("moses & ( aaron", false)); fail(); } catch (BookException ex) { }
        //try { engine.search(new Search("moses & ( aaron", false)); fail(); } catch (BookException ex) { }
        //try { engine.search(new Search("( moses ( aaron ) )", false)); fail(); } catch (BookException ex) { }
    }

    public void testBestMatch() throws Exception
    {
        /*
        version = new FileBible();
        commands = Options.getSearchHashtable();
        engine = new Parser(version, commands);

        PassageTally tally = engine.bestMatch("for god so loved the world that he gave his one and only son");
        log.fine(tally.getName(10));
        */
    }
}
