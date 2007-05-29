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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */

package org.crosswire.common.compress;


/**
 * The LZSS compression is a port of code as implemented for STEP.
 * The following information gives the history of this implementation.
 * 
 * <p>Compression Info, 10-11-95<br/>
 * Jeff Wheeler</p>
 * 
 * <h2><u>Source of Algorithm</u></h2>
 * 
 * <p>The compression algorithms used here are based upon the algorithms developed and published by Haruhiko Okumura in a paper entitled "Data Compression Algorithms of LARC and LHarc."  This paper discusses three compression algorithms, LSZZ, LZARI, and LZHUF.  LZSS is described as the "first" of these, and is described as providing moderate compression with good speed.  LZARI is described as an improved LZSS, a combination of the LZSS algorithm with adaptive arithmetic compression.  It is described as being slower than LZSS but with better compression.  LZHUF (the basis of the common LHA compression program) was included in the paper, however, a free usage license was not included.</p> 
 * 
 * <p>The following are copies of the statements included at the beginning of each source code listing that was supplied in the working paper.</p>
 * 
 *         <blockquote>LZSS, dated 4/6/89, marked as "Use, distribute and
 *         modify this program freely."</blockquote>
 * 
 *         <blockquote>LZARI, dated 4/7/89, marked as "Use, distribute and
 *         modify this program freely."</blockquote>
 * 
 *         <blockquote>LZHUF, dated 11/20/88, written by Haruyasu Yoshizaki,
 *         translated by Haruhiko Okumura on 4/7/89.  Not
 *         expressly marked as redistributable or modifiable.</blockquote>
 * 
 * <p>Since both LZSS and LZARI are marked as "use, distribute and modify freely" we have felt at liberty basing our compression algorithm on either of these.</p>
 * 
 * <h2><u>Selection of Algorithm</u></h2>
 * 
 * <p>Working samples of three possible compression algorithms are supplied in Okumura's paper.  Which should be used?</p>
 * 
 * <p>LZSS is the fastest at decompression, but does not generated as small a compressed file as the other methods. The other two methods provided, perhaps, a 15% improvement in compression.  Or, put another way, on a 100K file, LZSS might compress it to 50K while the others might approach 40-45K.  For STEP purposes, it was decided that decoding speed was of more importance than tighter compression. For these reasons, the first compression algorithm implemented is the LZSS algorithm.</p>
 * 
 * <h2><u>About LZSS Encoding</u></h2>
 * 
 * <p>(adapted from Haruhiko Okumura's paper)</p>
 * 
 * <p>This scheme was proposed by Ziv and Lempel [1].  A slightly modified version is described by Storer and Szymanski [2]. An implementation using a binary tree has been proposed by Bell [3].</p>
 * 
 * The algorithm is quite simple.<br/>
 * <ol>
 * <li>Keep a ring buffer which initially contains all space characters.</li>
 * <li>Read several letters from the file to the buffer.</li>
 * <li>Search the buffer for the longest string that matches the letters just read, and send its length and position into the buffer.</li>
 * </ol>
 * 
 * <p>If the ring buffer is 4096 bytes, the position can be stored in 12 bits.  If the length is represented in 4 bits, the <position, length> pair is two bytes long.  If the longest match is no more than two characters, then just one character is sent without encoding.  The process starts again with the next character.  An extra bit is sent each time to tell the decoder whether the next item is a character of a <position, length> pair.</p>
 * 
 * <p>
 * [1] J. Ziv and A. Lempel, IEEE Transactions IT-23, 337-343 (1977).<br/>
 * [2] J. A. Storer and T. G. Szymanski, J. ACM, 29, 928-951 (1982).<br/>
 * [3] T.C. Gell, IEEE Transactions COM-34, 1176-1182 (1986).
 * </p>
 * 
 * Regarding this port to Java and not the original code, the following license
 * applies:
 *
 * @see gnu.lgpl.License for license details.<br/>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class LZSS
{
    /**
     * Create an LZSS that is capable of transforming the input.
     * 
     * @param input to compress or uncompress.
     */
    public LZSS(byte[] input)
    {
        readBuffer = input;
    }

    /**
     * Encodes the input stream into the output stream.
     * 
     * @return the encoded result
     */
   public byte[] encode()
   {
       short i;                        // an iterator
       int r;                          // node number in the binary tree
       short s;                        // position in the ring buffer
       int len;                        // len of initial string
       int lastMatchLength;            // length of last match
       int codeBufPos;                 // position in the output buffer
       byte[] codeBuff = new byte[17]; // the output buffer
       byte mask;                      // bit mask for byte 0 of out readBuffer
       byte c;                         // character read from string

       // Start with a clean tree.
       initTree();

       // code_buf[0] works as eight flags.  A "1" represents that the
       // unit is an unencoded letter (1 byte), and a "0" represents
       // that the next unit is a <position,length> pair (2 bytes).
       //
       // code_buf[1..16] stores eight units of code.  Since the best
       // we can do is store eight <position,length> pairs, at most 16 
       // bytes are needed to store this.
       //
       // This is why the maximum size of the code buffer is 17 bytes.
       codeBuff[0] = 0;
       codeBufPos = 1;

       // Mask iterates over the 8 bits in the code buffer.  The first
       // character ends up being stored in the low bit.
       //
       //  bit   8   7   6   5   4   3   2   1
       //        |                           |
       //        |             first sequence in code buffer
       //        |
       //      last sequence in code buffer        
       mask = 1;

       s = 0;
       r = RING_SIZE - MAX_STORE_LENGTH;

       // Initialize the ring buffer with spaces...

       // Note that the last MAX_STORE_LENGTH bytes of the ring buffer are not filled.
       // This is because those MAX_STORE_LENGTH bytes will be filled in immediately
       // with bytes from the input stream.
       for (i = 0; i < r; i++)
       {
           ringBuffer[i] = ' ';
       }
       
       // Read MAX_STORE_LENGTH bytes into the last MAX_STORE_LENGTH bytes of the ring buffer.
       //
       // This function loads the buffer with X characters and returns
       // the actual amount loaded.
       len = getBytes(ringBuffer, r, MAX_STORE_LENGTH);

       // Make sure there is something to be compressed.
       if (len == 0)
       {
           return new byte[0];
       }

       // Insert the MAX_STORE_LENGTH strings, each of which begins with one or more
       // 'space' characters.  Note the order in which these strings
       // are inserted.  This way, degenerate trees will be less likely
       // to occur.
       for (i = 1; i <= MAX_STORE_LENGTH; i++)
       {
           insertNode((short) (r - i));
       }

       // Finally, insert the whole string just read.  The
       // member variables match_length and match_position are set.
       insertNode((short) r);

       // Now that we're preloaded, continue till done.
       do
       {

           // matchLength may be spuriously long near the end of text.
           if (matchLength > len)
           {
               matchLength = len;
           }

           // Is it cheaper to store this as a single character?  If so, make it so.
           if (matchLength < THRESHOLD)
           {
               // Send one character.  Remember that code_buf[0] is the
               // set of flags for the next eight items.
               matchLength = 1;     
               codeBuff[0] |= mask;  
               codeBuff[codeBufPos++] = ringBuffer[r];
           }
           else
           {
               // Otherwise, we do indeed have a string that can be stored
               // compressed to save space.

               // The next 16 bits need to contain the position (12 bits)
               // and the length (4 bits).
               codeBuff[codeBufPos++] = (byte) matchPosition;
               codeBuff[codeBufPos++] = (byte) (((matchPosition >> 4) & 0xf0) | (matchLength - THRESHOLD));
           }

           // Shift the mask one bit to the left so that it will be ready
           // to store the new bit.
           mask = (byte) (mask << 1);

           // If the mask is now 0, then we know that we have a full set
           // of flags and items in the code buffer.  These need to be
           // output.
           if (mask == 0)
           {
               // code_buf is the buffer of characters to be output.
               // code_buf_pos is the number of characters it contains.
               sendBytes(codeBuff, codeBufPos);

               // Reset for next buffer...
               codeBuff[0] = 0;
               codeBufPos = 1;
               mask = 1;
           }

           lastMatchLength = matchLength;

           // Delete old strings and read new bytes...
           for (i = 0; i < lastMatchLength; i++)
           {

               // Get next character...
               try
               {
                   c = getByte();
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                   break;
               }

               // Delete "old strings"
               deleteNode(s);

               // Put this character into the ring buffer.
               //          
               // The original comment here says "If the position is near
               // the end of the buffer, extend the buffer to make
               // string comparison easier."
               //
               // That's a little misleading, because the "end" of the 
               // buffer is really what we consider to be the "beginning"
               // of the buffer, that is, positions 0 through MAX_STORE_LENGTH.
               //
               // The idea is that the front end of the buffer is duplicated
               // into the back end so that when you're looking at characters
               // at the back end of the buffer, you can index ahead (beyond
               // the normal end of the buffer) and see the characters
               // that are at the front end of the buffer wihtout having
               // to adjust the index.
               //
               // That is...
               //
               //      1234xxxxxxxxxxxxxxxxxxxxxxxxxxxxx1234
               //      |                               |  |
               //      position 0          end of buffer  |
               //                                         |
               //                  duplicate of front of buffer
               ringBuffer[s] = c;

               if (s < MAX_STORE_LENGTH - 1)
               {
                   ringBuffer[s + RING_SIZE] = c;
               }

               // Increment the position, and wrap around when we're at
               // the end.  Note that this relies on RING_SIZE being a power of 2.
               s = (short) ((s + 1) & (RING_SIZE - 1));
               r = (short) ((r + 1) & (RING_SIZE - 1));

               // Register the string that is found in 
               // ringBuffer[r..r + MAX_STORE_LENGTH - 1].
               insertNode((short) r);
           }

           // If we didn't quit because we hit the last_match_length,
           // then we must have quit because we ran out of characters
           // to process.
           while (i++ < lastMatchLength)
           {                              
               deleteNode(s);

               s = (short) ((s + 1) & (RING_SIZE - 1));
               r = (short) ((r + 1) & (RING_SIZE - 1));

               // Note that len hitting 0 is the key that causes the
               // do...while() to terminate.  This is the only place
               // within the loop that len is modified.
               //
               // Its original value is MAX_STORE_LENGTH (or a number less than MAX_STORE_LENGTH for
               // short strings).
               if (--len != 0)
               {
                   insertNode((short) r);       /* buffer may not be empty. */
               }
           }

           // End of do...while() loop.  Continue processing until there
           // are no more characters to be compressed.  The variable
           // "len" is used to signal this condition.
       }
       while (len > 0);

       // There could still be something in the output buffer.  Send it now.
       if (codeBufPos > 1)
       {
           // code_buf is the encoded string to send.
           // code_buf_ptr is the number of characters.
           sendBytes(codeBuff, codeBufPos);
       }

       return writeBuffer;
   }

   /**
    * Decode the input stream into the output stream.
    * 
    * @return the decoded result
    */
   public byte[] decode()
   {
       byte[] c = new byte[MAX_STORE_LENGTH];     // an array of chars
       byte flags;                               // 8 bits of flags

       // Initialize the ring buffer with a common string.
       //
       // Note that the last MAX_STORE_LENGTH bytes of the ring buffer are not filled.
       // r is a nodeNumber
       int r = RING_SIZE - MAX_STORE_LENGTH;
       for (int i = 0; i < r; i++)
       {
           ringBuffer[i] = ' ';
       }

       flags = 0;
       int flagCount = 0;                     // which flag we're on

       while (true)
       {

           // If there are more bits of interest in this flag, then
           // shift that next interesting bit into the 1's position.
           //
           // If this flag has been exhausted, the next byte must be a flag.
           if (flagCount > 0)
           {
               flags = (byte) (flags >> 1);
               flagCount--;
           }
           else
           {
               // Next byte must be a flag.
               if (!hasMoreToRead())
               {
                   break;
               }

               flags = getByte();

               // Set the flag counter.  While at first it might appear
               // that this should be an 8 since there are 8 bits in the
               // flag, it should really be a 7 because the shift must
               // be performed 7 times in order to see all 8 bits.
               flagCount = 7;
           }

           // If the low order bit of the flag is now set, then we know
           // that the next byte is a single, unencoded character.
           if ((flags & 1) != 0)
           {
               if (getBytes(c, 1) != 1)
               {
                   break;
               }

               if (sendBytes(c, 1) != 1)
               {
                   break;
               }

               // Add to buffer, and increment to next spot. Wrap at end.
               ringBuffer[r] = c[0];
               r = (short) ((r + 1) & (RING_SIZE - 1));
           }
           else
           {
               // Otherwise, we know that the next two bytes are a
               // <position,length> pair.  The position is in 12 bits and
               // the length is in 4 bits.

               // Original code:
               //  if ((i = getc(infile)) == EOF)
               //      break;
               //  if ((j = getc(infile)) == EOF)
               //      break;
               //  i |= ((j & 0xf0) << 4);    
               //  j = (j & 0x0f) + THRESHOLD;
               //
               // I've modified this to only make one input call, and
               // have changed the variable names to something more
               // obvious.

               if (getBytes(c, 2) != 2)
               {
                   break;
               }

               // Convert these two characters into the position and
               // length in the ringBuffer.  Note that the length is always at least
               // THRESHOLD, which is why we're able to get a length
               // of 18 out of only 4 bits.
               int pos = (short) (c[0] | ((c[1] & 0xF0) << 4));
               int len = (short) ((c[1] & 0x0F) + THRESHOLD);

               // There are now "len" characters at position "pos" in
               // the ring buffer that can be pulled out.  Note that
               // len is never more than MAX_STORE_LENGTH.
               for (int k = 0; k < len; k++)
               {
                   c[k] = ringBuffer[(pos + k) & (RING_SIZE - 1)];

                   // Add to buffer, and increment to next spot. Wrap at end.
                   ringBuffer[r] = c[k];
                   r = (short) ((r + 1) & (RING_SIZE - 1));
               }

               // Add the "len" characters to the output stream.
               if (sendBytes(c, len) != len)
               {
                   break;
               }
           }
       }
       return writeBuffer;
   }

   /**
     * Initializes the tree nodes to "empty" states.
     */
    private void initTree()
    {
        // For i = 0 to RING_SIZE - 1, rightSon[i] and leftSon[i] will be the right
        // and left children of node i.  These nodes need not be
        // initialized.  However, for debugging purposes, it is nice to
        // have them initialized.  Since this is only used for compression
        // (not decompression), I don't mind spending the time to do it.
        //
        // For the same range of i, dad[i] is the parent of node i.
        // These are initialized to a known value that can represent
        // a "not used" state.
        for (int i = 0; i < RING_SIZE; i++)
        {
            leftSon[i] = NOT_USED;
            rightSon[i] = NOT_USED;
            dad[i] = NOT_USED;
        }

        // For i = 0 to 255, rightSon[RING_SIZE + i + 1] is the root of the tree
        // for strings that begin with the character i.  This is why
        // the right child array is larger than the left child array.
        // These are also initialzied to a "not used" state.
        //
        // Note that there are 256 of these, one for each of the possible
        // 256 characters.
        for (int i = RING_SIZE + 1; i <= (RING_SIZE + 256); i++)
        {
            rightSon[i] = NOT_USED;
        }

    }

    /**
     * Inserts a string from the ring buffer into one of the trees.
     * It loads the match position and length member variables
     * for the longest match.
     * 
     * <p>The string to be inserted is identified by the parameter Pos,
     * A full MAX_STORE_LENGTH bytes are inserted.  So, ringBuffer[Pos ... Pos+MAX_STORE_LENGTH-1]
     * are inserted.</p>
     * 
     * <p>If the matched length is exactly MAX_STORE_LENGTH, then an old node is removed
     * in favor of the new one (because the old one will be deleted
     * sooner).</p>
     * 
     * @param pos plays a dual role.  It is used as both a position
     * in the ring buffer and also as a tree node.  ringBuffer[Pos]
     * defines a character that is used to identify a tree node.
     */
    private void insertNode(short pos)
    {
        assert pos >= 0;
        assert pos < RING_SIZE;

        int cmp = 1;
        short key = pos;

        // The last 256 entries in rightSon contain the root nodes for
        // strings that begin with a letter.  Get an index for the
        // first letter in this string.
        short p = (short) (RING_SIZE + 1 + ringBuffer[key]);

        // Set the left and right tree nodes for this position to "not used."
        leftSon[pos] = NOT_USED;
        rightSon[pos] = NOT_USED;

        // Haven't matched anything yet.
        matchLength = 0;

        while (true)
        {
            if (cmp >= 0)
            {
                if (rightSon[p] != NOT_USED)
                {
                    p = rightSon[p];
                }
                else
                {
                    rightSon[p] = pos;
                    dad[pos] = p;
                    return;
                }
            }
            else
            {
                if (leftSon[p] != NOT_USED)
                {
                    p = leftSon[p];
                }
                else
                {
                    leftSon[p] = pos;
                    dad[pos] = p;
                    return;
                }
            }

            // Should we go to the right or the left to look for the
            // next match?
            short i = 0;
            for (i = 1; i < MAX_STORE_LENGTH; i++)
            {
                cmp = ringBuffer[key + i] - ringBuffer[p + i];
                if (cmp != 0)
                {
                    break;
                }
            }

            if (i > matchLength)
            {
                matchPosition = p;
                matchLength = i;

                if (i >= MAX_STORE_LENGTH)
                {
                    break;
                }
            }
        }

        dad[pos] = dad[p];
        leftSon[pos] = leftSon[p];
        rightSon[pos] = rightSon[p];

        dad[leftSon[p]] = pos;
        dad[rightSon[p]] = pos;

        if (rightSon[dad[p]] == p)
        {
            rightSon[dad[p]] = pos;
        }
        else
        {
            leftSon[dad[p]] = pos;
        }

        // Remove "p"
        dad[p] = NOT_USED;
    }

    /**
     * Remove a node from the tree.
     * 
     * @param node the node to remove
     */
    private void deleteNode(short node)
    {
        assert node >= 0;
        assert node < (RING_SIZE+1);

        short q;

        if (dad[node] == NOT_USED)
        {
            // not in tree, nothing to do
            return;
        }

        if (rightSon[node] == NOT_USED)
        {
            q = leftSon[node];
        }
        else if (leftSon[node] == NOT_USED)
        {
            q = rightSon[node];
        }
        else
        {
            q = leftSon[node];
            if (rightSon[q] != NOT_USED)
            {
                do
                {
                    q = rightSon[q];
                }
                while (rightSon[q] != NOT_USED);

                rightSon[dad[q]] = leftSon[q];
                dad[leftSon[q]] = dad[q];
                leftSon[q] = leftSon[node];
                dad[leftSon[node]] = q;
            }

            rightSon[q] = rightSon[node];
            dad[rightSon[node]] = q;
        }

        dad[q] = dad[node];

        if (rightSon[dad[node]] == node)
        {
            rightSon[dad[node]] = q;
        }
        else
        {
            leftSon[dad[node]] = q;
        }

        dad[node] = NOT_USED;
    }

    /**
     * Fill a buffer with some bytes from the input stream.
     * 
     * @param readBuffer the buffer to fill
     * @param start the position in the buffer to start filling
     * @param count the number of bytes to get
     * @return the number of bytes added to the buffer
     */
    private int getBytes(byte[] ibuf, int start, int len)
    {
        int slen = readBuffer.length;
        int realLen = (((slen - readOffset) > len) ? len : slen - readOffset);
        if (realLen > 0)
        {
            System.arraycopy(readBuffer, readOffset, ibuf, start, realLen);
            readOffset += realLen;
        }

        return realLen;
    }

    /**
     * Fill a buffer with some bytes from the input stream.
     * 
     * @param readBuffer the buffer to fill
     * @param count the number of bytes to get
     * @return the number of bytes added to the buffer
     */
    private int getBytes(byte[] ibuf, int len)
    {
        int slen = readBuffer.length;
        int realLen = (((slen - readOffset) > len) ? len : slen - readOffset);
        if (realLen > 0)
        {
            System.arraycopy(readBuffer, readOffset, ibuf, 0, realLen);
            readOffset += realLen;
        }

        return realLen;
    }
        
    /**
     * Return whether there are more bytes to read.
     * 
     * @return whether there are more bytes to read.
     */
    private boolean hasMoreToRead()
    {
        return readOffset < readBuffer.length;
    }

    /**
     * Get the next byte from the stream.
     * 
     * @return the the next byte from the stream
     */
    private byte getByte()
    {
        return readBuffer[readOffset++];
    }

    private int sendBytes(byte[] ibuf, int len)
    {
        // Make sure the buffer is more than big enough
        writeBuffer = ensureCapacity(writeBuffer, writeOffset, len);

        // Copy the new contents
        System.arraycopy(ibuf, 0, writeBuffer, writeOffset, len);
        writeOffset += len;

        return len;
    }

    private byte[] ensureCapacity(byte[] input, int currentPosition, int length)
    {
        // Make sure the buffer is more than big enough
        if (input != null)
        {
            int inputLength = readBuffer.length;
            if ((currentPosition + length) > inputLength)
            {
                int biggerLength = currentPosition + length + 1024;
                byte[] biggerBuf = new byte[biggerLength];
                System.arraycopy(readBuffer, 0, biggerBuf, 0, inputLength);
                for (int i = inputLength; i < biggerLength; i++)
                {
                    biggerBuf[i] = '\0';
                }
                return biggerBuf;
            }
            return input;
        }

        return new byte[length + 1024];
    }

    /**
     * This is the size of the ring buffer.  It is set to 4K. 
     * It is important to note that a position within the ring buffer requires 12 bits.  
     */
    private static final int RING_SIZE = 4096;

    /**
     * This is the maximum length of a character sequence that can be taken from the ring buffer.
     * It is set to 18.  Note that a length must be 3 before it is worthwhile to store a
     * position/length pair, so the length can be encoded in only 4 bits.
     * Or, put yet another way, it is not necessary to encode a length of 0-18, it is necessary
     * to encode a length of 3-18, which requires 4 bits.
     * <p>Note that the 12 bits used to store the position and the 4 bits
     * used to store the length equal a total of 16 bits, or 2 bytes.</p>
     */
    private static final int MAX_STORE_LENGTH = 18;

    /** 
     * It takes 2 bytes to store an offset and a length.
     * If a character sequence only requires 1 or 2 characters to store uncompressed,
     * then it is better to store it uncompressed than as an offset into the ring buffer.
     */
    private static final int THRESHOLD = 3;

    /**
     * Used to mark nodes as not used.
     */
    private static final int NOT_USED = RING_SIZE;

    /**
     * A text buffer.  It contains "nodes" of
     * uncompressed text that can be indexed by position.  That is,
     * a substring of the ring buffer can be indexed by a position
     * and a length.  When decoding, the compressed text may contain
     * a position in the ring buffer and a count of the number of
     * bytes from the ring buffer that are to be moved into the
     * uncompressed buffer.  
     *
     * <p>This ring buffer is not maintained as part of the compressed
     * text.  Instead, it is reconstructed dynamically.  That is,
     * it starts out empty and gets built as the text is decompressed.</p>
     *
     * <p>The ring buffer contain RING_SIZE bytes, with an additional MAX_STORE_LENGTH - 1 bytes
     * to facilitate string comparison.</p>
     */
    private byte[] ringBuffer = new byte[RING_SIZE + MAX_STORE_LENGTH - 1];

    /**
     * The position in the ring buffer. Used by insertNode.
     */
    private short matchPosition;

    /**
     * The number of characters in the ring buffer at matchPosition that match a given string. Used by insertNode.
     */
    private int matchLength;

    /**
     * leftSon, rightSon, and dad are the Japanese way of referring to
     * a tree structure.  The dad is the parent and it has a right and
     * left son (child).
     *
     * <p>For i = 0 to RING_SIZE-1, rightSon[i] and leftSon[i] will be the right 
     * and left children of node i.</p>
     *
     * <p>For i = 0 to RING_SIZE-1, dad[i] is the parent of node i.</p>
     *
     * <p>For i = 0 to 255, rightSon[RING_SIZE + i + 1] is the root of the tree for 
     * strings that begin with the character i.  Note that this requires 
     * one byte characters.</p>
     *
     * <p>These nodes store values of 0...(RING_SIZE-1).  Memory requirements
     * can be reduces by using 2-byte integers instead of full 4-byte
     * integers (for 32-bit applications).  Therefore, these are 
     * defined as "shorts."</p>
     */
    private short[] dad = new short[RING_SIZE + 1];
    private short[] leftSon = new short[RING_SIZE + 1];
    private short[] rightSon = new short[RING_SIZE + 257];

    /**
     * The buffer to get or send, when uncompressed.
     */
    private byte[] readBuffer;

    /**
     * The current offset into readBuffer.
     */
    private int readOffset;

    /**
     * The buffer to get or send, when compressed.
     */
    private byte[] writeBuffer;

    /**
     * The current offset into writeBuffer.
     */
    private int writeOffset;
}
