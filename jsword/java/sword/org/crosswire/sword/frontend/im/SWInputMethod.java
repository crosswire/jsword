package org.crosswire.sword.frontend.im;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001 CrossWire Bible Society under the terms of the GNU GPL
 * Company:
 * @author Troy A. Griffitts
 * @version 1.0
 */

public abstract class SWInputMethod {

    private String name = "InputMethod"; //$NON-NLS-1$
    int state = 0;

    public SWInputMethod(String name) {
        this.name = name;
    }

    public abstract String translate(char in);

    protected void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void clearState() {
        state = 0;
    }

    public String toString() {
        return name;
    }
}
