
package org.crosswire.jsword.view.swing.script;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
public class BSFGui
{
    public static void main(String[] args)
    {
        BSFGui aBSFGui = new BSFGui();
        JFrame jframe = aBSFGui.getFrmmain();

        jframe.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(1);
            }
        });
        jframe.setVisible(true);
    }

    public BSFGui()
    {
    }

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
     * This method initializes jSplitPane
     * 
     * @return JSplitPane
     */
    private JSplitPane getSptmain()
    {
        if (sptmain == null)
        {
            sptmain = new JSplitPane();
            sptmain.setLeftComponent(getPnlentry());
            sptmain.setRightComponent(getPnlvars());
            sptmain.setDividerLocation(150);
        }
        return sptmain;
    }
    /**
     * This method initializes jPanel1
     * 
     * @return JPanel
     */
    private JPanel getPnlentry()
    {
        if (pnlentry == null)
        {
            pnlentry = new JPanel();
            pnlentry.setLayout(new BorderLayout(5, 5));
            pnlentry.add(getScrresult(), BorderLayout.CENTER);
            pnlentry.add(getPnlcmd(), BorderLayout.NORTH);
            pnlentry.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        return pnlentry;
    }
    /**
     * This method initializes jScrollPane
     * 
     * @return JScrollPane
     */
    private JScrollPane getScrvars()
    {
        if (scrvars == null)
        {
            scrvars = new JScrollPane();
            scrvars.setViewportView(getLstvars());
        }
        return scrvars;
    }
    /**
     * This method initializes jList
     * 
     * @return JList
     */
    private JList getLstvars()
    {
        if (lstvars == null)
        {
            lstvars = new JList();
        }
        return lstvars;
    }
    /**
     * This method initializes jScrollPane1
     * 
     * @return JScrollPane
     */
    private JScrollPane getScrresult()
    {
        if (scrresult == null)
        {
            scrresult = new JScrollPane();
            scrresult.setViewportView(getTxtresult());
        }
        return scrresult;
    }
    /**
     * This method initializes jPanel2
     * 
     * @return JPanel
     */
    private JPanel getPnlcmd()
    {
        if (pnlcmd == null)
        {
            pnlcmd = new JPanel();
            pnlcmd.setLayout(new BorderLayout());
            pnlcmd.add(getLblcmd(), BorderLayout.WEST);
            pnlcmd.add(getTxtcmd(), BorderLayout.CENTER);
        }
        return pnlcmd;
    }
    /**
     * This method initializes jLabel
     * 
     * @return JLabel
     */
    private JLabel getLblcmd()
    {
        if (lblcmd == null)
        {
            lblcmd = new JLabel();
            lblcmd.setText(" > ");
        }
        return lblcmd;
    }
    /**
     * This method initializes jTextField
     * 
     * @return JTextField
     */
    private JTextField getTxtcmd()
    {
        if (txtcmd == null)
        {
            txtcmd = new JTextField();
            txtcmd.setColumns(10);
            txtcmd.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    exec();
                }
            });
        }
        return txtcmd;
    }

    /**
     * This method initializes jTextArea
     * 
     * @return JTextArea
     */
    private JTextArea getTxtresult()
    {
        if (txtresult == null)
        {
            txtresult = new JTextArea();
            txtresult.setEditable(false);
        }
        return txtresult;
    }

    /**
     * This method initializes jPanel
     * 
     * @return JPanel
     */
    private JPanel getPnlvars()
    {
        if (pnlvars == null)
        {
            pnlvars = new JPanel();
            pnlvars.setLayout(new BorderLayout());
            pnlvars.add(getScrvars(), BorderLayout.CENTER);
            pnlvars.setSize(234, 153);
            pnlvars.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        return pnlvars;
    }
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPnlmain()
    {
        if (pnlmain == null)
        {
            pnlmain = new JPanel();
            pnlmain.setLayout(new BorderLayout());
            pnlmain.add(getSptmain(), BorderLayout.CENTER);
        }
        return pnlmain;
    }
    /**
     * This method initializes jFrame
     * 
     * @return javax.swing.JFrame
     */
    private JFrame getFrmmain()
    {
        if (frmmain == null)
        {
            frmmain = new JFrame();
            frmmain.setContentPane(getPnlmain());
            frmmain.setSize(246, 183);
        }
        return frmmain;
    }

    private JSplitPane sptmain = null;
    private JPanel pnlentry = null;
    private JScrollPane scrvars = null;
    private JList lstvars = null;
    private JScrollPane scrresult = null;
    private JPanel pnlcmd = null;
    private JLabel lblcmd = null;
    private JTextField txtcmd = null;
    private JTextArea txtresult = null;
    private JPanel pnlvars = null;
    private JFrame frmmain = null;
    private JPanel pnlmain = null;
}
