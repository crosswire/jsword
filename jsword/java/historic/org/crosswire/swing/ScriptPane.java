
package org.crosswire.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ibm.bsf.BSFException;
import com.ibm.bsf.BSFManager;
import org.mozilla.javascript.NativeJavaObject;

/**
* ScriptPane is a GUI interface to the IBM BSF library for executing
* arbitary commands in a variety of scripting languages.
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
public class ScriptPane extends EirPanel
{
    /**
    * Basic Constructor
    */
    public ScriptPane()
    {
        jbInit();
        mgr.setDebug(true);
    }

    /**
    * Generate the GUI
    */
    private void jbInit()
    {
        btn_go.setMnemonic('G');
        btn_go.setText("Go");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { execute(); }
        });
        lbl_prompt.setFont(new Font("Dialog", 1, 12));
        lbl_prompt.setText(">");
        lbl_prompt.setLabelFor(txt_command);
        txt_command.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { execute(); }
        });
        pnl_command.setLayout(new BorderLayout(5, 0));
        pnl_command.add(txt_command, BorderLayout.CENTER);
        pnl_command.add(btn_go, BorderLayout.EAST);
        pnl_command.add(lbl_prompt, BorderLayout.WEST);

        txt_results.setColumns(40);
        txt_results.setRows(10);
        scr_results.getViewport().add(txt_results, null);
        src_vars.getViewport().add(tbl_vars, null);
        //spt_results.setOrientation(JSplitPane.VERTICAL_SPLIT);
        //spt_results.add(scr_results, JSplitPane.TOP);
        //spt_results.add(src_vars, JSplitPane.BOTTOM);

        this.setLayout(new BorderLayout(0, 5));
        this.add(scr_results, BorderLayout.CENTER);
        //this.add(spt_results, BorderLayout.CENTER);
        this.add(pnl_command, BorderLayout.SOUTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Script Window", false);
    }

    /**
    * Execute the current command
    */
    public void execute()
    {
        String command = txt_command.getText();
        String language = "javascript";
        String reply = "";

        try
        {
            if (command.startsWith("print "))
            {
                String sub = command.substring(6);
                Object retcode = mgr.eval(language, sub, 0, 0, sub);

                if (retcode instanceof NativeJavaObject)
                {
                    reply = ((NativeJavaObject) retcode).unwrap().toString();
                }
                else
                {
                    reply = retcode.toString();
                }
            }
            else
            {
                mgr.exec(language, command, 0, 0, command);
            }
        }
        catch (BSFException ex)
        {
            reply = ex.getMessage();
            Throwable tex = ex.getTargetException();
            if (tex != null)
                reply += "\n"+tex.getMessage();
        }

        txt_command.setText("");
        txt_results.append("> "+command+"\n");
        if (!reply.equals(""))
            txt_results.append(reply+"\n");
    }

    /**
    * Make an object available to the scripting engine
    * @param name The name under which the object is available
    * @param bean The object itself
    * @param type The type of the object
    */
    public void declareBean(String name, Object bean, Class type) throws Exception
    {
        txt_results.append("declare "+name+"="+bean+"\n");
        mgr.declareBean(name, bean, type);
    }

    /**
    * Stop an object being available to the scripting engine
    * @param name The name under which the object is available
    */
    public void undeclareBean(String name) throws Exception
    {
        txt_results.append("undeclare "+name+"\n");
        mgr.undeclareBean(name);
    }

    /**
    * Is the ">" prompt visible?
    * @param visible The new prompt visibility
    */
    public void setPromptVisible(boolean visible)
    {
        lbl_prompt.setVisible(visible);
    }

    /**
    * Is the ">" prompt visible?
    * @return The current prompt visibility
    */
    public boolean isPromptVisible()
    {
        return lbl_prompt.isVisible();
    }

    /**
    * Is the GO button visible?
    * @param visible The new GO button visibility
    */
    public void setGoVisible(boolean visible)
    {
        btn_go.setVisible(visible);
    }

    /**
    * Is the GO button visible?
    * @return The current GO button visibility
    */
    public boolean isGoVisible()
    {
        return btn_go.isVisible();
    }

    /** The script manager */
    private BSFManager mgr = new BSFManager();

    /* GUI Components */
    private JTextField txt_command = new JTextField();
    private JSplitPane spt_results = new JSplitPane();
    private JScrollPane scr_results = new JScrollPane();
    private JScrollPane src_vars = new JScrollPane();
    private JTextArea txt_results = new JTextArea();
    private JTable tbl_vars = new JTable();
    private JPanel pnl_command = new JPanel();
    private JButton btn_go = new JButton();
    private JLabel lbl_prompt = new JLabel();
}