
package org.crosswire.jsword.view.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
* An AWT Bible display pane.
* TODO: Re-write this using Tasks
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
* @see <{Licence}>
* @author Joe Walker
* @version D9.I9.T6
*/
public class Display extends Panel
{
    /**
    * Create a basic swing display
    */
    public Display()
    {
        jbInit();
    }

    /**
    * Initialize the gui components
    */
    private void jbInit()
    {
        cbo_type.addItem("View");
        cbo_type.addItem("Match");
        cbo_type.addItem("Search");
        cbo_type.addItem("Help");

        btn_go.setLabel("GO");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                view();
            }
        });
        txt_query.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                if (ev.getKeyCode() == KeyEvent.VK_ENTER)
                    view();
            }
        });

        pnl_top.setLayout(new BorderLayout());
        pnl_top.add(cbo_type, BorderLayout.WEST);
        pnl_top.add(txt_query, BorderLayout.CENTER);
        pnl_top.add(btn_go, BorderLayout.EAST);

        txt_results.setEditable(false);
        scr_results.add(txt_results, null);

        this.setLayout(new BorderLayout());
        this.add(scr_results, BorderLayout.CENTER);
        this.add(pnl_top, BorderLayout.NORTH);
    }

    /**
    * When someone clicks on the GO button
    */
    public void view()
    {
        String input = txt_query.getText();

        String next_type = OPTIONS[QUERY_PASSAGE];
        String next_input = DEFAULT_PASSAGE;
        Passage ref = null;
        Passage remainder = null;
        //String error = null;
        String results = "";

        try
        {
            Bible version = Bibles.getDefaultBible();

            switch (cbo_type.getSelectedIndex())
            {
            case QUERY_PASSAGE:
            case QUERY_FAST_PASS:
                ref = PassageFactory.createPassage(input);
                remainder = ref.trimVerses(VERSES_MAX);
                next_input = remainder == null ? ref.getName() : remainder.getName();

                // clean compile fixes
                next_input = next_input;
                next_type = next_type;

                BibleData data = version.getData(ref);
                // @todo: Invoke stylizer
                results += "todo:"+data.toString();
                break;

            case QUERY_MATCH:
                // To Do
                break;

            case QUERY_SEARCH:
                // To Do
                break;

            case QUERY_HELP:
                txt_results.setText(HELP_MSG);
                break;
            }
        }
        catch (Exception ex)
        {
            log.info("Failure", ex);
            txt_results.setText(ex.getMessage());
        }
    }

    /** The top panel */
    protected Panel pnl_top = new Panel();

    /** The query chooser */
    protected Choice cbo_type = new Choice();

    /** The query entry box */
    protected TextField txt_query = new TextField();

    /** The GO button */
    protected Button btn_go = new Button();

    /** The results */
    protected TextArea txt_results = new TextArea();

    /** The results scroller */
    protected ScrollPane scr_results = new ScrollPane();

    /** The log stream */
    protected static Logger log = Logger.getLogger(Display.class);

    /** The default Passage to view */
    private static final String DEFAULT_PASSAGE = "Gen 1:1-5";

    /** The Available options */
    private static final String[] OPTIONS =
    {
        "View Passage",
        "Best Match Search",
        "Power Search",
        "Quick Passage",
        "Help",
    };

    /** Query of type passage - must match the index below */
    private static final int QUERY_PASSAGE = 0;

    /** Query of type best match - must match the index below */
    private static final int QUERY_MATCH = 1;

    /** Query of type search - must match the index below */
    private static final int QUERY_SEARCH = 2;

    /** Query of type search - must match the index below */
    private static final int QUERY_FAST_PASS = 3;

    /** Query of type help - must match the index below */
    private static final int QUERY_HELP = 4;

    /** The maximum number of verses to fetch at a time */
    private static final int VERSES_MAX = 35;

    /** The Help string */
    protected static final String HELP_MSG =
        "Welcome: There are 3 different types of query you can execute on this server.\n " +
        "View Passage: Selecting this option and then entering a passage\n" +
        "description will enable you to see the Biblical text of the specified verses.\n " +
        "The parser is quite advanced, so the following will work: 'gen 1', 'Jude 2',\n "+
        "'MAT 1:3,4,7-8,5-6'\n\n" +
        "Best Match Search: This attempts to find a passage the matches that text that\n "+
        "you entered. Note the implementation of this is mediocre right now. There are\n "+
        "several tweaks in the pipeline that will make this much better. So expect slow\n "+
        "and imperfect matches. God willing it will get better.\n\n" +
        "Power Search: This allows you to search for a word or words. You can use a\n "+
        "searches syntax like the OnLine Bible e.g. 'aaron & moses' searches for Aaron\n "+
        "and Moses, and 'aaron | moses' finds verses with Aaron or Moses.\n\n" +
        "Quick Passage: This is similar to 'View Passage'\n "+
        "with the emphasis here on speed. View Passage provides customizable views, linkingv "+
        "technology, and so on. Quick Passage just gives you the text.\n\n" +
        "Help: Gets back to this message.\n\n" +
        "The Config screen allows you to select a Bible view style.\n "+
        "(Amongst other stuff like the current version, and the web page style)\n "+
        "This option shows off the power of using XSL as a template language. At the timev "+
        "of writing there are 2 styles. 'Oxford' looks like my old AV, uses ¶ marks to\n "+
        "denote new paragraphs, uses a serifed font. 'Modern' uses different fonts and\n "+
        "colours, includes a small graphic (which adds nothing other than proof that we "+
        "can do graphics) and even links - Try clicking on the verse numbers.\n";
}
