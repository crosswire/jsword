
package org.crosswire.jsword.view.cli;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * A Quick test GUI to see if we want to re-introduce a script pane.
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
public class BSFGui extends JPanel
{
    /**
     * Start point
     */
    public static void main(String[] args)
    {
        JFrame frame = BSFGui.showInFrame("Test GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 
     */
    private static JFrame showInFrame(String title)
    {
        BSFGui gui = new BSFGui(title);

        JFrame frame = new JFrame();
        frame.setContentPane(gui);
        frame.setSize(246, 183);
        frame.setTitle(gui.getTitle());
        frame.setVisible(true);

        return frame;
    }

    /**
     * Simple ctor
     */
    public BSFGui(String title)
    {
        this.title = title;
        jbInit();
    }

    /**
     * Initialise the GUI
     */
    private void jbInit()
    {
        txtcmd.setColumns(10);
        txtcmd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                exec();
            }
        });

        lblcmd.setText(" > ");

        pnlcmd.setLayout(new BorderLayout());
        pnlcmd.add(lblcmd, BorderLayout.WEST);
        pnlcmd.add(txtcmd, BorderLayout.CENTER);

        txtresult.setEditable(false);
        scrresult.setViewportView(txtresult);

        pnlentry.setLayout(new BorderLayout(5, 5));
        pnlentry.add(scrresult, BorderLayout.CENTER);
        pnlentry.add(pnlcmd, BorderLayout.NORTH);
        pnlentry.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        scrvars.setViewportView(lstvars);

        pnlvars.setLayout(new BorderLayout());
        pnlvars.add(scrvars, BorderLayout.CENTER);
        pnlvars.setSize(234, 153);
        pnlvars.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        sptmain.setLeftComponent(pnlentry);
        sptmain.setRightComponent(pnlvars);
        sptmain.setDividerLocation(150);

        this.setLayout(new BorderLayout());
        this.add(sptmain, BorderLayout.CENTER);
    }

    /**
     * Execute the next command
     */
    protected void exec()
    {
        Object reply = null;

        String command = txtcmd.getText();
        txtcmd.setText("");
        System.out.println(command);

        Context cx = Context.enter();
        try
        {
            Scriptable scope = cx.initStandardObjects(null);

            Scriptable jsout = Context.toObject(System.out, scope);
            scope.put("out", scope, jsout);

            reply = cx.evaluateString(scope, command, "<cmd>", 1, jsout);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
            reply = ex;
        }
        finally
        {
            Context.exit();
        }
        
        String results = txtresult.getText();
        results = results + "\n" + Context.toString(reply);
        txtresult.setText(results);
    }

    /**
     * Accessor for the window/script title
     */
    private String getTitle()
    {
        return title;
    }

    private String title;
    private JSplitPane sptmain = new JSplitPane();
    private JPanel pnlentry = new JPanel();
    private JScrollPane scrvars = new JScrollPane();
    private JList lstvars = new JList();
    private JScrollPane scrresult = new JScrollPane();
    private JPanel pnlcmd = new JPanel();
    private JLabel lblcmd = new JLabel();
    private JTextField txtcmd = new JTextField();
    private JTextArea txtresult = new JTextArea();
    private JPanel pnlvars = new JPanel();
}
