
package org.crosswire.jsword.passage.config;

import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.common.config.choices.BooleanChoice;
import org.crosswire.common.config.choices.OptionsChoice;

/**
* The Choices for configuring the Passage package.
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
* @version D0.I0.T0
*/
public class PassageChoices
{
    /**
    * Ensure that this class is not newed
    */
    private PassageChoices()
    {
    }

    /**
    * Accessor for if persistent naming is used
    */
    public static class PersistentNamingChoice extends BooleanChoice
    {
        public void setBoolean(boolean value)       { PassageUtil.setPersistentNaming(value); }
        public boolean getBoolean()                 { return PassageUtil.isPersistentNaming(); }
        public String getHelpText()                 { return "True if the passage editor re-write the references to conform to its notation."; }
    }

    /**
    * How blurs are restricted
    */
    public static class BlurRestrictionChoice extends OptionsChoice
    {
        public BlurRestrictionChoice()   { super(PassageUtil.getBlurRestrictions()); }
        public void setInt(int value)    { PassageUtil.setBlurRestriction(value); }
        public int getInt()              { return PassageUtil.getBlurRestriction(); }
        public String getHelpText()      { return "What is the default blurring - Blur across chapter boundaries or not."; }
    }

    /**
    * The passage case
    */
    public static class DisplayCaseChoice extends OptionsChoice
    {
        public DisplayCaseChoice()       { super(PassageUtil.getCases()); }
        public void setInt(int value)    { Books.setCase(value); }
        public int getInt()              { return Books.getCase(); }
        public String getHelpText()      { return "What case should we use to display the references."; }
    }
}
