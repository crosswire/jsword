/******************************************************************************
 *  swdisp.cpp  - code for base class 'swdisp'.  swdisp is the basis for all
 *		  types of displays (e.g. raw textout, curses, xwindow, etc.)
 */

package org.crosswire.sword.frontend;

import org.crosswire.sword.modules.SWModule;

public class SWDisplay {


/******************************************************************************
 * SWDisplay::Display - casts a module to a character pointer and displays it to
 *			raw output (overriden for different display types and
 *			module types if necessary)
 *
 * ENT:	imodule - module to display
 *
 * RET:	error status
 */

	public char display(SWModule imodule) {
		System.out.println(imodule.getText());
		return 0;
	}
}
