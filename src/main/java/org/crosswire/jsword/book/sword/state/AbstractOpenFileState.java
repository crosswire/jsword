package org.crosswire.jsword.book.sword.state;


/**
  *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractOpenFileState implements OpenFileState {
    
    /**
     * Allows us to decide whether to release the resources or continue using them
     */
    public void close() {
        OpenFileStateManager.release(this);
    }
}
