
package org.crosswire.common.config.choices;

/**
* OptionsChoice.
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
public abstract class OptionsChoice extends AbstractChoice
{
    /**
    * Construct an Options Choice
    */
    public OptionsChoice(String[] options)
    {
        setOptions(options);
    }

    /**
    * Generalized read boolean from the Properties file
    * @return Found boolean or the default value
    */
    public void setOptions(String[] options)
    {
        this.options = options;

        /*
        if (editor != null)
        {
            editor.removeAllItems();
            for (int i=0; i<options.length; i++)
                editor.addItem(options[i]);
        }
        */
    }

    /**
    * Generalized read integer from the Properties file
    * @return Found int or the default value
    */
    public abstract int getInt();

    /**
    * Generalized set integer to the Properties file
    * @param value The value to enter
    */
    public abstract void setInt(int value);

    /**
    * Generalized read boolean from the Properties file
    * @return Found boolean or the default value
    */
    public String getString()
    {
        return options[getInt()];
    }

    /**
    * Generalized set boolean to the Properties file
    * @param data The value to enter
    */
    public void setString(String data)
    {
        for (int i=0; i<options.length; i++)
        {
            if (options[i].equals(data))
            {
                setInt(i);
                return;
            }
        }

        throw new IllegalArgumentException(data);
    }

    /**
    *
    */
    public String getType()
    {
        return "options";
    }

    /**
    *
    */
    public Object getTypeOptions()
    {
        return options;
    }

    /** The editor */
    //protected transient OptionsField editor;

    /** The allowed options */
    protected String[] options;
}

