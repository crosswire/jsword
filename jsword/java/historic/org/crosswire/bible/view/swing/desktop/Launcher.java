
package com.eireneh.bible.view.swing.desktop;

import java.util.Hashtable;
import java.awt.*;
import javax.swing.*;

import com.eireneh.swing.*;
import com.eireneh.bible.control.search.*;
import com.eireneh.bible.passage.*;
import com.eireneh.bible.book.*;
import com.eireneh.bible.view.swing.beans.*;

/**
* The Launcher class is a collection of frequent start points. 
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
* @see docs.Licence
* @author Joe Walker
* @version D5.I2.T0
*/
public class Launcher
{
    /**
    * Basic constructor
    */
    public Launcher() throws BookException
    {
        engine = new Engine(Bibles.getDefaultBible());
        match = new Matcher(Bibles.getDefaultBible());
    }

    /**
    * Open a search in a CustomFrame
    * @param query a string containing the reference to display
    * @exception SearchException if there was an error searching
    */
    public void openSearch(String query) throws SearchException
    {
        Study tree = new Study();

        Passage ref = tree.getPassage();
        engine.search(ref, query);

        openScrolledComponent(tree, tree.getTitle());
    }

    /**
    * Open a best match search in a CustomFrame
    * @param query a string containing the reference to display
    * @exception SearchException if there was an error searching
    */
    public void openMatch(String query) throws SearchException
    {
        Study study = new Study();

        Passage ref = match.bestMatch(query);
        study.setPassage(ref);

        openScrolledComponent(study, study.getTitle());
    }

    /**
    * Open a Page in a CustomFrame
    * @param query a string containing the reference to display
    * @exception NoSuchVerseException If the entered verses do not exist
    */
    public void openPassage(String query) throws NoSuchVerseException
    {
        // Page page = new Page(query);
        // openComponent(page, page.getTitle());
    }

    /**
    * Open a Page in a CustomFrame
    * @param ref The reference to display
    */
    public void openPassage(Passage ref)
    {
        // Page page = new Page(ref);
        // openComponent(page, page.getTitle());
    }

    /**
    * Open a Page in a CustomFrame
    */
    private void openScrolledComponent(Component comp, String title)
    {
        CustomFrame frame = new CustomFrame(title);
        frame.getContentPane().add(comp);
        frame.setVisible(true);
    }

    /**
    * Open a Page in a CustomFrame
    */
    private void openComponent(Component comp, String title)
    {
        CustomFrame frame = new CustomFrame(title);
        JScrollPane scroll = new JScrollPane();

        scroll.getViewport().setView(comp);
        frame.getContentPane().add(scroll);
        frame.setVisible(true);
    }

    /** The search engine */
    private Engine engine = null;

    /** The best match engine */
    private Matcher match = null;
}
