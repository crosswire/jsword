package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URI;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.BlockType;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.book.sword.SwordUtil;
import org.crosswire.jsword.versification.Testament;

/**
 * Stores the random access files required for processing the passage request
 * 
 * The caller is required to close to correctly free resources and avoid File
 * pointer leaks
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ZLDBackendStateTODO implements OpenFileState {
    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ZLDBackendStateTODO.class);
    private static final String EXTENSION_Z_INDEX = ".zdx";
    private static final String EXTENSION_Z_DATA = ".zdt";

    private static final int ZDX_ENTRY_SIZE = 8;
    private static final int BLOCK_ENTRY_COUNT = 4;
    private static final int BLOCK_ENTRY_SIZE = 8;
    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * Flags whether there are open files or not
     */
    private transient boolean active;

    /**
     * The compressed index.
     */
    private transient File zdxFile;

    /**
     * The compressed index random access file.
     */
    private transient RandomAccessFile zdxRaf;

    /**
     * The compressed text.
     */
    private transient File zdtFile;

    /**
     * The compressed text random access file.
     */
    private transient RandomAccessFile zdtRaf;

    /**
     * The index of the block that is cached.
     */
    private transient long lastBlockNum;

    /**
     * The cache for a read of a compressed block.
     */
    private transient byte[] lastUncompressed;

    public ZLDBackendStateTODO(SwordBookMetaData bookMetaData, BlockType blockType) throws BookException {
        
        
    }

    public void close() {
//        IOUtil.close(ntIdxRaf);
//        IOUtil.close(ntTextRaf);
//        IOUtil.close(ntCompRaf);
//        IOUtil.close(otIdxRaf);
//        IOUtil.close(otTextRaf);
//        IOUtil.close(otCompRaf);
//        ntIdxRaf = null;
//        ntTextRaf = null;
//        ntCompRaf = null;
//        otIdxRaf = null;
//        otTextRaf = null;
//        otCompRaf = null;
    }

}
