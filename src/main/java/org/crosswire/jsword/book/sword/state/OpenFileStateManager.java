package org.crosswire.jsword.book.sword.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.BlockType;
import org.crosswire.jsword.book.sword.SwordBookMetaData;

/**
 * Manages the creation and re-distribution of open file states. This increases
 * performance as more often than not, the same file state may be used. For
 * example we may be carrying out a contains() operation followed by a read to
 * disk for a particular key
 * 
 * Each {@link SwordBookMetaData} has a corresponding a file state which is
 * different to another. Furthermore, concurrent accesses cannot share this file
 * state as the {@link OpenFileState} records where in the file it is, for
 * reading several verses together for example. As a result, we want to key a
 * lookup by {@link SwordBookMetaData}, which then gives us a pool of available
 * file states... We create some more if none are available.
 * 
 * We may want to set a maximum to prevent leaking resources on heavy concurrent
 * usage. However, at the current time, with single thread access, we are
 * bounded to having 1 open file per module installed, which should be acceptable across platforms.
 */
public class OpenFileStateManager {
    private static volatile Map<SwordBookMetaData,Queue<OpenFileState>> metaToStates = new HashMap<SwordBookMetaData,Queue<OpenFileState>>();

    /**
     * prevent instantiation
     */
    private OpenFileStateManager() {
        // no op
    }

    public static RawBackendState getRawBackendState(SwordBookMetaData metadata) {
        RawBackendState state = getInstance(metadata);
        if (state == null) {
            state = new RawBackendState(metadata);
        }

        return state;
    }

    public static RawFileBackendState getRawFileBackendState(SwordBookMetaData metadata) {
        RawFileBackendState state = getInstance(metadata);
        if (state == null) {
            state = new RawFileBackendState(metadata);
        }

        return state;
    }

    public static GenBookBackendState getGenBookBackendState(SwordBookMetaData metadata) {
        GenBookBackendState state = getInstance(metadata);
        if (state == null) {
            state = new GenBookBackendState(metadata);
        }
        return state;
    }

    public static RawLDBackendState getRawLDBackendState(SwordBookMetaData metadata) throws BookException {
        RawLDBackendState state = getInstance(metadata);
        if (state == null) {
            state = new RawLDBackendState(metadata);
        }

        return state;
    }

    public static ZLDBackendState getZLDBackendState(SwordBookMetaData metadata) throws BookException {
        ZLDBackendState state = getInstance(metadata);
        if (state == null) {
            state = new ZLDBackendState(metadata);
        }

        return state;
    }

    public static ZVerseBackendState getZVerseBackendState(SwordBookMetaData metadata, BlockType blockType) throws BookException {
        ZVerseBackendState state = getInstance(metadata);
        if (state == null) {
            state = new ZVerseBackendState(metadata, blockType);
        }

        return state;
    }

    @SuppressWarnings("unchecked")
    private static <T extends OpenFileState> T getInstance(SwordBookMetaData metadata) {
        Queue<OpenFileState> availableStates = getQueueForMeta(metadata);

        return (T) availableStates.poll();
    }

    private static Queue<OpenFileState> getQueueForMeta(SwordBookMetaData metadata) {
        Queue<OpenFileState> availableStates = metaToStates.get(metadata);
        if (availableStates == null) {
            synchronized (OpenFileState.class) {
                if (availableStates == null) {
                    availableStates = new ConcurrentLinkedQueue<OpenFileState>();
                    metaToStates.put(metadata, availableStates);
                }
            }
        }
        return availableStates;
    }

    public static void release(OpenFileState fileState)  {
        // instead of releasing, we add to our queue
        Queue<OpenFileState> queueForMeta = getQueueForMeta(fileState.getBookMetaData());
        boolean offered = queueForMeta.offer(fileState);

        // ignore if we couldn't offer to the queue
        if (!offered) {
            fileState.releaseResources();
        }
    }
}
