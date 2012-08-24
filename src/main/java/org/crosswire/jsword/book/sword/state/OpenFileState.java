package org.crosswire.jsword.book.sword.state;

import java.io.Closeable;

/**
 * Marker interface for objects holding open files that should be freed up upon finishing
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public interface OpenFileState extends Closeable {


}
