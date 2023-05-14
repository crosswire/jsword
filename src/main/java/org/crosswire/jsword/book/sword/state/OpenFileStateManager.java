/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.BlockType;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * In order to prevent memory leaks (OpenFileStates might be quite heavy as they do some internal caching of file data..
 * In order to avoid many file references piling up in memory, we implement a background cleaning thread which will clean
 * up redundant keys every so often.
 *
 *
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * 
 * @author DM Smith
 * @author Chris Burrell
 */
public final class OpenFileStateManager {

    // ordinal for OT is from 0 to 24114 (ESV) or 0 to 24182 (OHB).  When the 15th bit is on, the 14th bit is not on.
    // ordinal for NT is subtracted by 24116 (first ordinal for the book of Matthew).
    // ordinal for Revelation 22:21 is 32361
    // Therefore the NT range is from 0 to about 8245.  In that number range, 15th bit will never be on for an NT ordinal.

    public static class OrdinalStrongArray implements Serializable {
        public int[] OHBOrdinal; // 24182
        public int[] OTRSVOrdinal; // 24114
        public int[] NTRSVOrdinal; // 8245
        public byte[] augStrong;
        public short[] strongsWithAugments;
        public byte[] defaultAugment;
        public int numOfGreekStrongWithAugments;
    }
    /**
     * prevent instantiation
     */
    private OpenFileStateManager(final int cleanupIntervalSeconds, final int maxExpiry) {
        // no op
        scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }

        });
        this.monitoringThread = this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                // check the state of the maps and queues... The queues may have too much in them and that will in turn max out
                // the heap.
                long currentTime = System.currentTimeMillis();

                for (Queue<OpenFileState> e : OpenFileStateManager.this.metaToStates.values()) {
                    for (Iterator<OpenFileState> iterator = e.iterator(); iterator.hasNext(); ) {
                        final OpenFileState state = iterator.next();
                        if (state.getLastAccess() + maxExpiry * 1000 < currentTime) {
                            //release resources
                            state.releaseResources();

                            //remove from the queues
                            iterator.remove();
                        }
                    }
                }
            }
        }, 0, cleanupIntervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Allow the caller to initialise with their own settings. Should the OpenFileStateManager already be initialised
     * a no-op will occur. No need for double-checked locking here
     */
    public static synchronized void init(final int cleanupIntervalSeconds, final int maxExpiry) {
        if(manager == null) {
            manager = new OpenFileStateManager(cleanupIntervalSeconds, maxExpiry);
        } else {
            // already intialised
            LOGGER.warn("The OpenFileStateManager has already been initialised, potentially with its default settings. The following values were ignored: cleanUpInterval [{}], maxExpiry=[{}]", cleanupIntervalSeconds, maxExpiry);
        }

    }

    public static synchronized void addOrdinalStrong(OrdinalStrongArray osArray) {
        OpenFileStateManager.osArray = osArray;
//        for (int i = 0; i < OpenFileStateManager.osArray.OHBOrdinal.length; i++) {
//            int beginPos = OpenFileStateManager.osArray.OHBOrdinal[i];
//            if (beginPos == 0) continue;
//            int endPos = 0;
//            for (int j = i + 1; ((j < OpenFileStateManager.osArray.OHBOrdinal.length) && (endPos == 0)); j++) {
//                endPos = OpenFileStateManager.osArray.OHBOrdinal[j];
//            }
//            if (endPos == 0) {
//                for (int k = 0; ((k < OpenFileStateManager.osArray.NTRSVOrdinal.length) && (endPos == 0)); k++) {
//                    endPos = OpenFileStateManager.osArray.NTRSVOrdinal[k];
//                }
//            }
//            byte[] b = new byte[endPos - beginPos];
//            int count = 0;
//            for (int j = beginPos; j < endPos; j++) {
//                b[count] = OpenFileStateManager.osArray.augStrong[j];
//                count++;
//            }
//            System.out.println("OT: " + i + " " + new String(b));
//        }
//        for (int i = 0; i < OpenFileStateManager.osArray.NTRSVOrdinal.length; i++) {
//            int beginPos = OpenFileStateManager.osArray.NTRSVOrdinal[i];
//            if (beginPos == 0) continue;
//            int endPos = 0;
//            for (int j = i + 1; ((j < OpenFileStateManager.osArray.NTRSVOrdinal.length) && (endPos == 0)); j++) {
//                endPos = OpenFileStateManager.osArray.NTRSVOrdinal[j];
//            }
//            if (endPos == 0) {
//                endPos = OpenFileStateManager.osArray.augStrong.length;
//            }
//            byte[] b = new byte[endPos - beginPos];
//            int count = 0;
//            for (int j = beginPos; j < endPos; j++) {
//                b[count] = OpenFileStateManager.osArray.augStrong[j];
//                count++;
//            }
//            System.out.println("NT: " + i + " " + new String(b));
//        }
//        for (int i = 0; i < OpenFileStateManager.osArray.OTRSVOrdinal.length; i++) {
//            System.out.println("OT RSV: " + i + " " + OpenFileStateManager.osArray.OTRSVOrdinal[i]);
//        }
//        System.out.println("numOfGreek " + OpenFileStateManager.osArray.numOfGreekStrongWithAugments);
//        for (int i = 0; i < OpenFileStateManager.osArray.strongsWithAugments.length; i++) {
//            System.out.println("Strong: " + OpenFileStateManager.osArray.strongsWithAugments[i] + " " + OpenFileStateManager.osArray.defaultAugment[i]);
//        }
    }

    /**
     * Singleton instance method to return the one and only Open File State Manager
     * @return
     */
    public static OpenFileStateManager instance() {
        if(manager == null) {
            synchronized (OpenFileStateManager.class) {
                init(60, 60);
            }
        }
        return manager;
    }

    public RawBackendState getRawBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        RawBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new RawBackendState(metadata);
        }

        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public RawFileBackendState getRawFileBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        RawFileBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new RawFileBackendState(metadata);
        }

        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public GenBookBackendState getGenBookBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        GenBookBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new GenBookBackendState(metadata);
        }

        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public RawLDBackendState getRawLDBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        RawLDBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new RawLDBackendState(metadata);
        }

        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public ZLDBackendState getZLDBackendState(SwordBookMetaData metadata) throws BookException {
        ensureNotShuttingDown();

        ZLDBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new ZLDBackendState(metadata);
        }

        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    public ZVerseBackendState getZVerseBackendState(SwordBookMetaData metadata, BlockType blockType) throws BookException {
        ensureNotShuttingDown();

        ZVerseBackendState state = getInstance(metadata);
        if (state == null) {
            LOGGER.trace("Initializing: {}", metadata.getInitials());
            return new ZVerseBackendState(metadata, blockType);
        }

        LOGGER.trace("Reusing: {}", metadata.getInitials());
        return state;
    }

    @SuppressWarnings("unchecked")
    private <T extends OpenFileState> T getInstance(SwordBookMetaData metadata) {
        Queue<OpenFileState> availableStates = getQueueForMeta(metadata);

        final T state = (T) availableStates.poll();

        //while not strictly necessary, the documentation suggests that iterating through the collection
        //gives you a snapshot at some point in time, though not necessarily consistent, so just in case this remains
        //in access of the iterator() functionality, we update the last access date to avoid it being destroyed while we
        //use it
        if(state != null) {
            state.setLastAccess(System.currentTimeMillis());
        }
        return state;
    }

    private Queue<OpenFileState> getQueueForMeta(SwordBookMetaData metadata) {
        Queue<OpenFileState> availableStates = metaToStates.get(metadata);
        if (availableStates == null) {
            synchronized (OpenFileState.class) {
                availableStates = new ConcurrentLinkedQueue<OpenFileState>();
                metaToStates.put(metadata, availableStates);
            }
        }
        return availableStates;
    }

    public void release(OpenFileState fileState) {
        if (fileState == null) {
            // can't release anything. JSword has failed to open a file state,
            // and a finally block is trying to close this
            return;
        }

        fileState.setLastAccess(System.currentTimeMillis());

        // instead of releasing, we add to our queue
        SwordBookMetaData bmd = fileState.getBookMetaData();
        Queue<OpenFileState> queueForMeta = getQueueForMeta(bmd);
        LOGGER.trace("Offering to releasing: {}", bmd.getInitials());
        boolean offered = queueForMeta.offer(fileState);

        // ignore if we couldn't offer to the queue
        if (!offered) {
            LOGGER.trace("Released: {}", bmd.getInitials());
            fileState.releaseResources();
        }
    }

    /**
     * Shuts down all open files
     */
    public void shutDown() {
        shuttingDown = true;
        this.monitoringThread.cancel(true);
        this.scheduledExecutorService.shutdownNow();
        for (Queue<OpenFileState> e : metaToStates.values()) {
            OpenFileState state = null;
            while ((state = e.poll()) != null) {
                state.releaseResources();
            }
        }
    }

    private void ensureNotShuttingDown() throws BookException {
        if (shuttingDown) {
            throw new BookException("Unable to read book, application is shutting down.");
        }
    }

    private final ScheduledFuture<?> monitoringThread;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Map<SwordBookMetaData, Queue<OpenFileState>> metaToStates = new HashMap<SwordBookMetaData, Queue<OpenFileState>>();
    private volatile boolean shuttingDown;

    private static volatile OpenFileStateManager manager;
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFileStateManager.class);
    public static OrdinalStrongArray osArray;
}
