package org.crosswire.sword.frontend.im;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001 CrossWire Bible Society under the terms of the GNU GPL
 * Company:
 * @author Troy A. Griffitts
 * @version 1.0
 */

public class NullIM extends SWInputMethod {

    public NullIM(String name) {
        super(name);
    }

    public String translate(char ch) {
        return new String() + ch;
    }
}
