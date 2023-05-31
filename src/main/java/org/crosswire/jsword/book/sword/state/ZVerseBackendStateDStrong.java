package org.crosswire.jsword.book.sword.state;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.versification.Testament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZVerseBackendStateDStrong {

    public static void openAndCacheAugmentedFiles(final String path, stepAugmentedBibleTextCache stepCache,
                                                  final Testament testament) {
        if (stepCache == null)
            return;
        try {
            if (stepCache.posInAugFile == stepCacheErrorDoNotUse) // -512 means do not use cache, encounter error previously
                return;
            stepCache.augmentedFileMBB = null;
        }
        catch (Exception e) {
            log.error("openCacheAugmentedfiles cannot access stepCache variable augmentedFileMBB or posInAugFile", e);
            return;
        }
        try {
            String curPath = path;
            if (curPath.charAt(curPath.length() - 1) != '/')
                curPath += "/"; // Sometimes it does not have a "/" slash character at the end so add it if necessary.
            curPath += "STEP_Augment/";
            File augmentedText = new File(curPath + testament.name() + "augmentedText");
            File augmentedTextIdx = new File(curPath + testament.name() + "augmentedIndex.ser");
            if ((augmentedText.canRead() && (augmentedTextIdx.canRead()))) {
                // Reads the object
                FileInputStream fileIn = new FileInputStream(curPath + testament.name() + "augmentedIndex.ser");
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                // Reads the objects
                stepCache.stepAugmentedIndex = (stepAugmentedIndex) objIn.readObject();
                RandomAccessFile file = new RandomAccessFile(augmentedText, "r");
                FileChannel channel = file.getChannel();
                // Read file into mapped buffer
                stepCache.augmentedFileMBB = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                log.info("openAndCacheAugmentedFiles: open augmented files" + curPath);
                stepCache.posInAugFile = stepCacheReady4Use;
            }
        }
        catch (Exception e) {
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = stepCacheErrorDoNotUse;  // Do not use cache
            log.error("openCacheAugmentedfiles cannot open augmentedText or augmentedIndex.ser files", e);
        }
    }

    public static String getVerseFromAugmentedFile(final int ordinal, final IndexStatus status,
                                                   stepAugmentedBibleTextCache stepCache) {
        if ((status == IndexStatus.CREATING) || (stepCache == null))
            return null;
        int lengthOfIndex2Text = 0;
        try {
            if ((stepCache.posInAugFile != stepCacheReady4Use) || (stepCache.stepAugmentedIndex == null) ||
                    (stepCache.augmentedFileMBB == null))
                return null;
            if (stepCache.stepAugmentedIndex.baseIndexDivideBy == 1)
                lengthOfIndex2Text = stepCache.stepAugmentedIndex.baseIndex.length;
            else
                lengthOfIndex2Text = stepCache.stepAugmentedIndex.index2Text.length - 2;
            if (ordinal > lengthOfIndex2Text - 2) {
                System.out.println("index got out of bound");
                return null;
            }
        }
        catch (Exception e) {
            log.error("getVerseFromAugmentedFile cannot access stepCache variables", e);
            return null;
        }
        try {
            int pos = getPosOfOrdinal(ordinal, stepCache.stepAugmentedIndex);
            if (pos == 0) return "";
            int nextPos = 0;
            for (int i = ordinal + 1; ((nextPos == 0) && (i < lengthOfIndex2Text)); i++) {
                nextPos = getPosOfOrdinal(i, stepCache.stepAugmentedIndex); // Look for a next position that is not a 0.
            }
            int length = nextPos - pos;
            if (length < 1) {
                log.error("negative length in getVerseFromAugmentedFile " + length);
                return null;
            }
            byte[] dest = new byte[length];
            stepCache.augmentedFileMBB.position(pos);
            stepCache.augmentedFileMBB.get(dest, 0, length);
            return new String(dest, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = stepCacheErrorDoNotUse;  // -512 means do not use cache
            log.error("getVerseFromAugmentedFile exception", e);
            return null;
        }
    }

    private static int getPosOfOrdinal(final int ordinal, final stepAugmentedIndex stepAugIndex) {
        if (stepAugIndex.baseIndexDivideBy > 1) {
            int posOfIndex = ordinal * 2;
            if ((stepAugIndex.index2Text[posOfIndex] == (byte) 0xff) && (stepAugIndex.index2Text[posOfIndex + 1] == (byte) 0xff))
                return 0;
            int posOfText = ((stepAugIndex.index2Text[posOfIndex + 1] & 0xff) << 8);
            posOfText += stepAugIndex.index2Text[posOfIndex] & 0xff;
            int index4BaseIndex = ordinal / stepAugIndex.baseIndexDivideBy;
            posOfText += stepAugIndex.baseIndex[index4BaseIndex];
            return posOfText;
        }
        else return stepAugIndex.baseIndex[ordinal]; // When the index is not compacted, it only use baseIndex, it does not user index2Text
}

    public static void createAugStrongCache(final int maxOrdinal, final SwordBookMetaData bmd,
                                            stepAugmentedBibleTextCache stepCache,
                                            final Testament testament) {
        if (stepCache == null)
            return;
        try {
            if (stepCache.posInAugFile == stepCacheErrorDoNotUse)
                return;
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = 0;
            stepCache.augFileChannel = null;
        }
        catch (Exception e) {
            log.error("createAugStrongCache cannot access stepCache variables", e);
            return;
        }
        try {
            String augmentedFilePath = bmd.getLocation().getPath();
            if (augmentedFilePath.indexOf("/C:") == 0)
                augmentedFilePath = augmentedFilePath.substring(1);
            augmentedFilePath += "/STEP_Augment";
            Path path = Paths.get(augmentedFilePath);
            if (Files.exists(path)) {
                augmentedFilePath += "/" + testament.name() + "augmentedText";
                System.out.println(("Writing augmented text file at " + augmentedFilePath));

                Set<StandardOpenOption> options = new HashSet<>();
                options.add(StandardOpenOption.CREATE);
                options.add(StandardOpenOption.WRITE);
                options.add(StandardOpenOption.TRUNCATE_EXISTING);
                path = Paths.get(augmentedFilePath);
                stepCache.augFileChannel = FileChannel.open(path, options);
                String header = bmd.getInitials() + "\n";
                ByteBuffer output = null; // Must output something at the beginning of the file.
                output = ByteBuffer.wrap(header.getBytes("UTF-8"));
                stepCache.augFileChannel.write(output);
                stepCache.posInAugFile += output.limit();
                stepCache.stepAugmentedIndex = new stepAugmentedIndex();
                stepCache.stepAugmentedIndex.baseIndex = new int[maxOrdinal + 2];
            }
        }
        catch (Exception e) {
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = stepCacheErrorDoNotUse; // Encounter error, -512 means do not try to open or create cache
            log.error("createAugStrongCache", e);
        }
    }

    public static void addToAugStrongCache(final int ordinal, final String augmentedString,
                                           stepAugmentedBibleTextCache stepCache) {
        if ((stepCache == null) || (augmentedString.length() <= 0))
            return;
        try {
            if ((stepCache.stepAugmentedIndex == null) || (stepCache.augFileChannel == null))
                return;
        }
        catch (Exception e) {
            log.error("addToAugStrongCache cannot access stepCache variables", e);
            return;
        }
        try {
            ByteBuffer output = ByteBuffer.wrap(augmentedString.getBytes("UTF-8"));
            stepCache.augFileChannel.write(output);
            stepCache.stepAugmentedIndex.baseIndex[ordinal] = stepCache.posInAugFile;
            stepCache.posInAugFile += output.limit();
        }
        catch (Exception e) {
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = stepCacheErrorDoNotUse; // Encounter error, -512 means do not try to open or create cache
            log.error("addToAugStrongCache error in output to augmented Strong file", e);
        }
    }

    public static void finalizeAugStrongCache(final SwordBookMetaData bmd, stepAugmentedBibleTextCache stepCache,
                                              final Testament testament) {
        if (stepCache == null)
            return;
        try {
            if ((stepCache.stepAugmentedIndex == null) || (stepCache.augFileChannel == null))
                return;
        }
        catch (Exception e) {
            log.error("finalizeAugStrongCache cannot access stepCache variables", e);
            return;
        }
        try {
            stepCache.augFileChannel.close();
            stepCache.augFileChannel = null;
            int index2LastNonZero = stepCache.stepAugmentedIndex.baseIndex.length - 1;
            for (; ((index2LastNonZero > 0) && (stepCache.stepAugmentedIndex.baseIndex[index2LastNonZero] == 0)); index2LastNonZero--) {
            }
            if (index2LastNonZero < stepCache.stepAugmentedIndex.baseIndex.length - 10) { // If there are lots of zero's at the end, reduce the size.  KJVA has lots of verses in Deutro cannon that has this situation.
                System.out.println("ordIndex2AugmentedFile array reduced from: " + stepCache.stepAugmentedIndex.baseIndex.length + " to " + index2LastNonZero + " elements");
                stepCache.stepAugmentedIndex.baseIndex = Arrays.copyOf(stepCache.stepAugmentedIndex.baseIndex, index2LastNonZero + 2);
            }
            stepCache.stepAugmentedIndex.baseIndex[stepCache.stepAugmentedIndex.baseIndex.length - 1] = stepCache.posInAugFile;
            String augmentedIndexFilePath = bmd.getLocation().getPath();
            if (augmentedIndexFilePath.indexOf("/C:") == 0)
                augmentedIndexFilePath = augmentedIndexFilePath.substring(1);
            augmentedIndexFilePath += "/STEP_Augment/" + testament.name() + "augmentedIndex.ser";
            stepAugmentedIndex stepIndex = compactIndex(stepCache);
            if (stepIndex != null) {
                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(augmentedIndexFilePath);
                ObjectOutputStream out;
                out = new ObjectOutputStream(fileOutputStream);
                out.writeObject(stepIndex);
                out.flush();
                out.close();
                stepCache.posInAugFile = stepCacheReady4Use;
            }
        }
        catch (Exception e) {
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = stepCacheErrorDoNotUse; // Encounter error, -512 means do not try to open or create cache
            log.warn("finalizeAugStrongCache", e);
        }
    }

    private static stepAugmentedIndex compactIndex (stepAugmentedBibleTextCache stepCache ) {
        // baseIndex is an array of int
        // index2Text is an array of short.  There is an element in this array for each ordinal (verse).
        // The sum of the two will give the position in the cached Bible text file for an ordinal
        //
        int max4 = 0;
        int last4 = stepCache.stepAugmentedIndex.baseIndex[1];
        int max8 = 0;
        int last8 = stepCache.stepAugmentedIndex.baseIndex[1];
        int max16 = 0;
        int last16 = stepCache.stepAugmentedIndex.baseIndex[1];
        int max32 = 0;
        int last32 = stepCache.stepAugmentedIndex.baseIndex[1];
        for (int i = 0; i < stepCache.stepAugmentedIndex.baseIndex.length; i++) {
            if ((i % 4 == 0) || (i == stepCache.stepAugmentedIndex.baseIndex.length - 1)) {
                int currentPos = stepCache.stepAugmentedIndex.baseIndex[i];
                if (currentPos == 0) {
                    for (int j = i + 1; j < stepCache.stepAugmentedIndex.baseIndex.length - 1; j++) {
                        if (stepCache.stepAugmentedIndex.baseIndex[j] != 0) {
                            currentPos = stepCache.stepAugmentedIndex.baseIndex[j];
                            break;
                        }
                    }
                }
                if (max4 < (currentPos - last4))
                    max4 = currentPos - last4;
                last4 = currentPos;
                if ((i % 8 == 0) || (i == stepCache.stepAugmentedIndex.baseIndex.length - 1)) {
                    if (max8 < (currentPos - last8))
                        max8 = currentPos - last8;
                    last8 = currentPos;
                    if ((i % 16 == 0) || (i == stepCache.stepAugmentedIndex.baseIndex.length - 1)) {
                        if (max16 < (currentPos - last16)) {
                            max16 = currentPos - last16;
                        }
                        last16 = currentPos;
                        if ((i % 32 == 0) || (i == stepCache.stepAugmentedIndex.baseIndex.length - 1)) {
                            if (max32 < (currentPos - last32))
                                max32 = currentPos - last32;
                            last32 = currentPos;
                        }
                    }
                }
            }
        }
        stepAugmentedIndex newStepIndex = new stepAugmentedIndex();
        newStepIndex.baseIndexDivideBy = 1;
        if (max32 < 65555) newStepIndex.baseIndexDivideBy = 32; // reduce size by 46% would be from 128K to 68K for a Bible with OT and NT
        else if (max16 < 65535) newStepIndex.baseIndexDivideBy = 16; // reduce size by 43%
        else if (max8 < 65535) newStepIndex.baseIndexDivideBy = 8; // reduce size by 37%
        else if (max4 < 65535) newStepIndex.baseIndexDivideBy = 4; // reduce size by 25%
        else {
            stepCache.stepAugmentedIndex.baseIndexDivideBy = 1;
            return stepCache.stepAugmentedIndex; // Cannot compact the index, return original.
        }
        newStepIndex.baseIndex = new int[(stepCache.stepAugmentedIndex.baseIndex.length / newStepIndex.baseIndexDivideBy) + 2];
        newStepIndex.index2Text = new byte[(stepCache.stepAugmentedIndex.baseIndex.length+1) * 2];
        for (int i = 0; i < stepCache.stepAugmentedIndex.baseIndex.length; i++) {
            int j = i / newStepIndex.baseIndexDivideBy;
            if (i % newStepIndex.baseIndexDivideBy == 0) {
                int currentPos = stepCache.stepAugmentedIndex.baseIndex[i];
                if (currentPos == 0) {
                    for (int k  = i + 1; k < stepCache.stepAugmentedIndex.baseIndex.length - 1; k++) {
                        if (stepCache.stepAugmentedIndex.baseIndex[k] != 0) {
                            currentPos = stepCache.stepAugmentedIndex.baseIndex[k];
                            newStepIndex.index2Text[i * 2] = (byte) 0xff;
                            newStepIndex.index2Text[i * 2 + 1] = (byte) 0xff;;
                            break;
                        }
                    }
                }
                else {
                    newStepIndex.index2Text[i*2] = 0;
                    newStepIndex.index2Text[i*2+1] = 0;
                }
                newStepIndex.baseIndex[j] = currentPos;
            }
            else {
                int currentPos = stepCache.stepAugmentedIndex.baseIndex[i];
                int diff;
                if (currentPos == 0) {
                    diff = 65535; // 65535 is FFFF in hex for an int, it means it is zero
                }
                else {
                    diff = currentPos - newStepIndex.baseIndex[j];
                }
                int pos = i * 2;
                newStepIndex.index2Text[pos] = (byte)(diff & 0xff);
                newStepIndex.index2Text[pos+1] = (byte)((diff >> 8) & 0xff);
            }
        }
        // Verify the compact index.  Don't need to run this at run time.
        for (int i = 0; i < stepCache.stepAugmentedIndex.baseIndex.length; i++) {
            int pos = i * 2;
            if ((newStepIndex.index2Text[pos] == (byte) 0xff) && (newStepIndex.index2Text[pos+1] == (byte) 0xff)) {
                if (stepCache.stepAugmentedIndex.baseIndex[i] != 0)
                    System.out.println("did not find zero");
            }
            if (stepCache.stepAugmentedIndex.baseIndex[i] != getPosOfOrdinal(i, newStepIndex))
                System.out.println("did not match");
        }
        stepCache.stepAugmentedIndex = newStepIndex;
        return newStepIndex;
    }
    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ZVerseBackendState.class);

    public static class stepAugmentedBibleTextCache {
        int posInAugFile;
        stepAugmentedIndex stepAugmentedIndex;
        MappedByteBuffer augmentedFileMBB;
        FileChannel augFileChannel;
    }

    public static class stepAugmentedIndex implements java.io.Serializable {
        int baseIndexDivideBy;
        int[] baseIndex;
        byte[] index2Text;
    }

    private static int stepCacheErrorDoNotUse = -512;
    private static int stepCacheReady4Use = -1048;

}
