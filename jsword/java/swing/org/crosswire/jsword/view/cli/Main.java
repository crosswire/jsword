
package org.crosswire.jsword.view.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.util.Project;
import org.jdom.JDOMException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

/**
 * Quick helper for writing scripts to control JSword.
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
public class Main
{
    /**
     * 
     */
    public static void main(String[] args) throws MalformedURLException, JDOMException, IOException
    {
        Logger.stopLogging();

        Reader in = new InputStreamReader(System.in);
        Writer out = new PrintWriter(System.out);

        Main main = new Main("<console>");

        main.parse(in, out);
    }

    /**
     * 
     */
    public Main(String title) throws MalformedURLException, JDOMException, IOException
    {
        this.title = title;

        cx = Context.enter();
        scope = cx.initStandardObjects(null);

        Scriptable jsout = Context.toObject(System.out, scope);
        scope.put("out", scope, jsout);

        Scriptable jsmodel = Context.toObject(new Model(), scope);
        scope.put("sw", scope, jsmodel);

        // At some stage we should call Context.exit();
    }

    /**
     * 
     */
    public void parse(Reader in, Writer out) throws IOException
    {
        BufferedReader cin = new BufferedReader(in);
        PrintWriter pout = new PrintWriter(out);

        pout.println("JSword CLI. Version "+Project.resource().getVersion());

        int linenum = 1;
        while (true)
        {
            System.err.flush();
            out.flush();

            String line = cin.readLine();
            if (line == null)
            {
                break;
            }

            String reply = "";
            try
            {
                reply = parse(line, linenum++);
            }
            catch (JavaScriptException ex)
            {
                Object th = ex.getValue();
                if (th instanceof Throwable)
                {
                    handle((Throwable) th);
                }
                else
                {
                    handle(ex);
                }
            }
            catch (Throwable ex)
            {
                reply = handle(ex);
            }

            pout.println(reply);
        }
    }

    /**
     * 
     */
    private String handle(Throwable ex)
    {
        ex.printStackTrace();

        return ex.toString();
    }

    /**
     * 
     */
    private String parse(String command, int linenum) throws JavaScriptException
    {
        Object reply = cx.evaluateString(scope, command, title, linenum, null);        
        return Context.toString(reply);
    }

    private Context cx;
    private Scriptable scope;
    private String title;
}
