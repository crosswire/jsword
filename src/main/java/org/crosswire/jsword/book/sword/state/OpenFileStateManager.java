/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2013
 *     The copyright to this program is held by it's authors.
 *
 */
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
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * 
 * @author DM Smith
 * @author Chris Burrell
 */
public class OpenFileStateManager {
    /**
     * prevent instantiation
     */
    private OpenFileStateManager() {
        // no op
    }

    public static RawBackendState getRawBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        RawBackendState state = getInstance(metadata);
        if (state == null) {
            state = new RawBackendState(metadata);
        }

        return state;
    }

    public static RawFileBackendState getRawFileBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        RawFileBackendState state = getInstance(metadata);
        if (state == null) {
            state = new RawFileBackendState(metadata);
        }

        return state;
    }

    public static GenBookBackendState getGenBookBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        GenBookBackendState state = getInstance(metadata);
        if (state == null) {
            state = new GenBookBackendState(metadata);
        }
        return state;
    }

    public static RawLDBackendState getRawLDBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        RawLDBackendState state = getInstance(metadata);
        if (state == null) {
            state = new RawLDBackendState(metadata);
        }

        return state;
    }

    public static ZLDBackendState getZLDBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        ZLDBackendState state = getInstance(metadata);
        if (state == null) {
            state = new ZLDBackendState(metadata);
        }

        return state;
    }

    public static ZVerseBackendState getZVerseBackendState(SwordBookMetaData metadata, BlockType blockType) throws BookException {
        ensureNotShuttingDown();

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
        synchronized (OpenFileState.class) {
            if (availableStates == null) {
                availableStates = new ConcurrentLinkedQueue<OpenFileState>();
                metaToStates.put(metadata, availableStates);
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

    /**
     * Shuts down all open files
     */
    public static void shutDown() {
        shuttingDown  = true;
        for (Queue<OpenFileState> e : metaToStates.values()) {
            OpenFileState state = null;
            while ((state = e.poll()) != null) {
                state.releaseResources();
            }
        }
    }

    private static void ensureNotShuttingDown() throws BookException {
        if (shuttingDown) {
            throw new BookException("Unable to read book, application is shutting down.");
        }
    }

    private static volatile Map<SwordBookMetaData, Queue<OpenFileState>> metaToStates = new HashMap<SwordBookMetaData, Queue<OpenFileState>>();
    private static volatile boolean shuttingDown;
}
