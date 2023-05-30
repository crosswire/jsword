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
            stepCache.augmentedFileMBB = null;
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
            }
        }
        catch (Exception e) {
            log.warn("openCacheAugmentedfiles ", e);
        }
    }

    public static String getVerseFromAugmentedFile(final int ordinal, final IndexStatus status,
                                                   stepAugmentedBibleTextCache stepCache) {
        if ((status == IndexStatus.CREATING) || (stepCache == null) ||
                (stepCache.stepAugmentedIndex == null) ||
                (ordinal > stepCache.stepAugmentedIndex.index2Text.length - 2) || (stepCache.augmentedFileMBB == null))
            return null;
        try {
            int pos = getPosOfOrdinal(ordinal, stepCache.stepAugmentedIndex);
            if (pos == 0) return "";
            int nextPos = getPosOfOrdinal(ordinal + 1, stepCache.stepAugmentedIndex);
            for (int i = ordinal + 2; ((nextPos == 0) && (i < stepCache.stepAugmentedIndex.index2Text.length)); i++) {
                nextPos = getPosOfOrdinal(i, stepCache.stepAugmentedIndex);
            }
            int length = nextPos - pos;
            if (length < 1) {
                System.out.println("negative length in getVerseFromAugmentedFile " + length);
                return null;
            }
            byte[] dest = new byte[length];
            stepCache.augmentedFileMBB.position(pos);
            stepCache.augmentedFileMBB.get(dest, 0, length);
            return new String(dest, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            stepCache = null;
            log.warn("getVerseFromAugmentedFile exception", e);
            return null;
        }
    }

    private static int getPosOfOrdinal(final int ordinal, final stepAugmentedIndex stepAugIndex) {
        int j = ordinal / stepAugIndex.baseIndexDivideBy;
        int posOfIndex = ordinal * 2;
        if ((stepAugIndex.index2Text[posOfIndex] == (byte) 0xff) && (stepAugIndex.index2Text[posOfIndex+1] == (byte) 0xff))
            return 0;
        int posOfText = ((stepAugIndex.index2Text[posOfIndex+1] & 0xff) << 8);
        posOfText += stepAugIndex.index2Text[posOfIndex] & 0xff;
        posOfText += stepAugIndex.baseIndex[j];
        return posOfText;
    }

    public static void createAugStrongCache(final int maxOrdinal, final SwordBookMetaData bmd,
                                            stepAugmentedBibleTextCache stepCache,
                                            final Testament testament) {
        if (stepCache == null)
            return;
        try {
            stepCache.ordIndex2AugmentedFile = null;
            stepCache.stepAugmentedIndex = null;
            stepCache.augmentedFileMBB = null;
            stepCache.posInAugFile = 0;
            stepCache.augFileChannel = null;
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
                stepCache.ordIndex2AugmentedFile = new int[maxOrdinal + 2];
            }
        }
        catch (Exception e) {
            stepCache = null;
            log.warn("createAugStrongCache", e);
        }
    }

    public static void addToAugStrongCache(final int ordinal, final String augmentedString,
                                           stepAugmentedBibleTextCache stepCache) {
        try {
            if ((stepCache == null) || (stepCache.ordIndex2AugmentedFile == null) || (stepCache.augFileChannel == null) ||
                    (augmentedString.length() <= 0))
                return;
            ByteBuffer output = ByteBuffer.wrap(augmentedString.getBytes("UTF-8"));
            stepCache.augFileChannel.write(output);
            stepCache.ordIndex2AugmentedFile[ordinal] = stepCache.posInAugFile;
            stepCache.posInAugFile += output.limit();
        }
        catch (Exception e) {
            stepCache = null;
            log.warn("addToAugStrongCache", e);
        }
    }

    public static void finalizeAugStrongCache(final SwordBookMetaData bmd, stepAugmentedBibleTextCache step,
                                              final Testament testament) throws IOException {
        try {
            if ((step == null) || (step.ordIndex2AugmentedFile == null) || (step.augFileChannel == null))
                return;
            step.augFileChannel.close();
            step.augFileChannel = null;
            int lastIndex2NonZero = step.ordIndex2AugmentedFile.length - 1;
            for (; ((lastIndex2NonZero > 0) && (step.ordIndex2AugmentedFile[lastIndex2NonZero] == 0)); lastIndex2NonZero--) {
            }
            if (lastIndex2NonZero < step.ordIndex2AugmentedFile.length - 10) { // If there are lots of zero's at the end, reduce the size.  KJVA has lots of verses in Deutro cannon that has this situation.
                System.out.println("ordIndex2AugmentedFile array reduced from: " + step.ordIndex2AugmentedFile.length + " to " + lastIndex2NonZero + " elements");
                step.ordIndex2AugmentedFile = Arrays.copyOf(step.ordIndex2AugmentedFile, lastIndex2NonZero + 2);
            }
            step.ordIndex2AugmentedFile[step.ordIndex2AugmentedFile.length - 1] = step.posInAugFile;
            String augmentedIndexFilePath = bmd.getLocation().getPath();
            if (augmentedIndexFilePath.indexOf("/C:") == 0)
                augmentedIndexFilePath = augmentedIndexFilePath.substring(1);
            augmentedIndexFilePath += "/STEP_Augment/" + testament.name() + "augmentedIndex.ser";
            stepAugmentedIndex stepIndex = compactIndex(step);
            if (stepIndex != null) {
                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(augmentedIndexFilePath);
                ObjectOutputStream out;
                out = new ObjectOutputStream(fileOutputStream);
                out.writeObject(stepIndex);
                out.flush();
                out.close();
            }
        }
        catch (Exception e) {
            step = null;
            log.warn("finalizeAugStrongCache", e);
        }
    }

    private static stepAugmentedIndex compactIndex (stepAugmentedBibleTextCache step ) {
        int max4 = 0;
        int last4 = step.ordIndex2AugmentedFile[1];
        int max8 = 0;
        int last8 = step.ordIndex2AugmentedFile[1];
        int max16 = 0;
        int last16 = step.ordIndex2AugmentedFile[1];
        int max32 = 0;
        int last32 = step.ordIndex2AugmentedFile[1];
        for (int i = 0; i < step.ordIndex2AugmentedFile.length; i++) {
            if ((i % 4 == 0) || (i == step.ordIndex2AugmentedFile.length - 1)) {
                int currentPos = step.ordIndex2AugmentedFile[i];
                if (currentPos == 0) {
                    for (int j = i + 1; j < step.ordIndex2AugmentedFile.length - 1; j++) {
                        if (step.ordIndex2AugmentedFile[j] != 0) {
                            currentPos = step.ordIndex2AugmentedFile[j];
                            break;
                        }
                    }
                }
                if (max4 < (currentPos - last4))
                    max4 = currentPos - last4;
                last4 = currentPos;
                if ((i % 8 == 0) || (i == step.ordIndex2AugmentedFile.length - 1)) {
                    if (max8 < (currentPos - last8))
                        max8 = currentPos - last8;
                    last8 = currentPos;
                    if ((i % 16 == 0) || (i == step.ordIndex2AugmentedFile.length - 1)) {
                        if (max16 < (currentPos - last16)) {
                            max16 = currentPos - last16;
                        }
                        last16 = currentPos;
                        if ((i % 32 == 0) || (i == step.ordIndex2AugmentedFile.length - 1)) {
                            if (max32 < (currentPos - last32))
                                max32 = currentPos - last32;
                            last32 = currentPos;
                        }
                    }
                }
            }
        }
        stepAugmentedIndex stepIndex = new stepAugmentedIndex();
        stepIndex.baseIndexDivideBy = 1;
        if (max32 < 65555) stepIndex.baseIndexDivideBy = 32;
        else if (max16 < 65535) stepIndex.baseIndexDivideBy = 16;
        else if (max8 < 65535) stepIndex.baseIndexDivideBy = 8;
        else if (max4 < 65535) stepIndex.baseIndexDivideBy = 4;
        else {
            log.error("compactIndex, cannot be compacted, Bible text is too large.");
            return null; // Cannot compact the index
        }
        stepIndex.baseIndex = new int[(step.ordIndex2AugmentedFile.length / stepIndex.baseIndexDivideBy) + 2];
        stepIndex.index2Text = new byte[(step.ordIndex2AugmentedFile.length+1) * 2];

        for (int i = 0; i < step.ordIndex2AugmentedFile.length; i++) {
            int j = i / stepIndex.baseIndexDivideBy;
            if (i % stepIndex.baseIndexDivideBy == 0) {
                int currentPos = step.ordIndex2AugmentedFile[i];
                if (currentPos == 0) {
                    for (int k  = i + 1; k < step.ordIndex2AugmentedFile.length - 1; k++) {
                        if (step.ordIndex2AugmentedFile[k] != 0) {
                            currentPos = step.ordIndex2AugmentedFile[k];
                            stepIndex.index2Text[i * 2] = (byte) 0xff;
                            stepIndex.index2Text[i * 2 + 1] = (byte) 0xff;;
                            break;
                        }
                    }
                }
                else {
                    stepIndex.index2Text[i*2] = 0;
                    stepIndex.index2Text[i*2+1] = 0;
                }
                stepIndex.baseIndex[j] = currentPos;
            }
            else {
                int currentPos = step.ordIndex2AugmentedFile[i];
                int diff;
                if (currentPos == 0) {
                    diff = 65535;
                }
                else {
                    diff = currentPos - stepIndex.baseIndex[j];
                }
                int pos = i * 2;
                stepIndex.index2Text[pos] = (byte)(diff & 0xff);
                stepIndex.index2Text[pos+1] = (byte)((diff >> 8) & 0xff);
            }
        }
        // Verify the compact index.  Don't need to run this at run time.
//        for (int i = 0; i < step.ordIndex2AugmentedFile.length; i++) {
//            int j = i / stepIndex.baseIndexDivideBy;
//            int pos = i * 2;
//            if ((stepIndex.index2Text[pos] == (byte) 0xff) && (stepIndex.index2Text[pos+1] == (byte) 0xff)) {
//                if (step.ordIndex2AugmentedFile[i] != 0)
//                    System.out.println("did not find zero");
//            }
//            if (step.ordIndex2AugmentedFile[i] != getPosOfOrdinal(i, stepIndex))
//                System.out.println("did not match");
//        }
        step.ordIndex2AugmentedFile = null;
        return stepIndex;
    }
    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(ZVerseBackendState.class);

    public static class stepAugmentedBibleTextCache {
        int posInAugFile;
        stepAugmentedIndex stepAugmentedIndex;
        int[] ordIndex2AugmentedFile;
        MappedByteBuffer augmentedFileMBB;
        FileChannel augFileChannel;
    }

    public static class stepAugmentedIndex implements java.io.Serializable {
        int baseIndexDivideBy;
        int[] baseIndex;
        byte[] index2Text;
    }

}
