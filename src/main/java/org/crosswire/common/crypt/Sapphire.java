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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.crypt;

/**
 * The Sapphire II Stream Cipher is a port of Sword's C++ implementation of
 * Michael Paul Johnson's 2 January 1995 public domain cipher. Below is the
 * documentation that he originally provided for it. It has been converted to
 * JavaDoc and the C++ fragment has been removed.
 * 
 * <h1>THE SAPPHIRE II STREAM CIPHER</h1>
 * 
 * <p>
 * The Sapphire II Stream Cipher is designed to have the following properties:
 * </p>
 * <ul>
 * 
 * <li>Be useful for generation of cryptographic check values as well as
 * protecting message privacy.</li>
 * 
 * <li>Accept a variable length key.</li>
 * 
 * <li>Strong enough to justify <i>at least</i> a 64 bit key for balanced
 * security.</li>
 * 
 * <li>Small enough to be built into other applications with several keys active
 * at once.</li>
 * 
 * <li>Key setup fast enough to support frequent key change operations but slow
 * enough to discourage brute force attack on the key.</li>
 * 
 * <li>Fast enough to not significantly impact file read &amp; write operations on
 * most current platforms.</li>
 * 
 * <li>Portable among common computers and efficient in C, C++, and Pascal.</li>
 * 
 * <li>Byte oriented.</li>
 * 
 * <li>Include both ciphertext and plain text feedback (for both optimal data
 * hiding and value in creation of cryptographic check values).</li>
 * 
 * <li>Acceptable performance as a pure pseudorandom number generator without
 * providing a data stream for encryption or decryption.</li>
 * 
 * <li>Allow <i>limited</i> key reuse without serious security degradation.</li>
 * </ul>
 * 
 * <h2>HISTORY AND RELATED CIPHERS</h2>
 * 
 * <p>
 * The Sapphire Stream Cipher is very similar to a cipher I started work on in
 * November 1993. It is also similar in some respects to the alledged RC-4 that
 * was posted to sci.crypt recently. Both operate on the principle of a mutating
 * permutation vector. Alledged RC-4 doesn't include any feedback of ciphertext
 * or plain text, however. This makes it more vulnerable to a known plain text
 * attack, and useless for creation of cryptographic check values. On the other
 * hand, alledged RC-4 is faster.
 * </p>
 * 
 * <p>
 * The Sapphire Stream Cipher is used in the shareware product Quicrypt, which
 * is available at ftp://ftp.csn.net/mpj/qcrypt10.zip and on the Colorado
 * Catacombs BBS (303-772-1062). There are two versions of Quicrypt: the
 * exportable version (with a session key limited to 32 bits but with strong
 * user keys allowed) and the commercial North American version (with a session
 * key of 128 bits). A variant of the Sapphire Stream Cipher is also used in the
 * shareware program Atbash, which has no weakened exportable version.
 * </p>
 * 
 * <p>
 * The Sapphire II Stream Cipher is a modification of the Sapphire Stream Cipher
 * designed to be much more resistant to adaptive chosen plaintext attacks (with
 * reorigination of the cipher allowed). The Sapphire II Stream Cipher is used
 * in an encryption utility called ATBASH2.
 * </p>
 * 
 * 
 * <h2>OVERVIEW</h2>
 * 
 * <p>
 * The Sapphire Stream Cipher is based on a state machine. The state consists of
 * 5 index values and a permutation vector. The permutation vector is simply an
 * array containing a permutation of the numbers from 0 through 255. Four of the
 * bytes in the permutation vector are moved to new locations (which may be the
 * same as the old location) for every byte output. The output byte is a
 * nonlinear function of all 5 of the index values and 8 of the bytes in the
 * permutation vector, thus frustrating attempts to solve for the state
 * variables based on past output. On initialization, the permutation vector
 * (called the cards array in the source code below) is shuffled based on the
 * user key. This shuffling is done in a way that is designed to minimize the
 * bias in the destinations of the bytes in the array. The biggest advantage in
 * this method is not in the elimination of the bias, per se, but in slowing
 * down the process slightly to make brute force attack more expensive.
 * Eliminating the bias (relative to that exhibited by RC-4) is nice, but this
 * advantage is probably of minimal cryptographic value. The index variables are
 * set (somewhat arbitrarily) to the permutation vector elements at locations 1,
 * 3, 5, 7, and a key dependent value (rsum) left over from the shuffling of the
 * permutation vector (cards array).
 * </p>
 * 
 * 
 * <h2>KEY SETUP</h2>
 * 
 * <p>
 * Key setup (illustrated by the function initialize(), below) consists of three
 * parts:
 * </p>
 * <ol>
 * <li>Initialize the index variables.</li>
 * <li>Set the permutation vector to a known state (a simple counting sequence).
 * </li>
 * <li>Starting at the end of the vector, swap each element of the permutation
 * vector with an element indexed somewhere from 0 to the current index (chosen
 * by the function keyrand()).</li>
 * </ol>
 * <p>
 * The keyrand() function returns a value between 0 and some maximum number
 * based on the user's key, the current state of the permutation vector, and an
 * index running sum called rsum. Note that the length of the key is used in
 * keyrand(), too, so that a key like "abcd" will not result in the same
 * permutation as a key like "abcdabcd".
 * </p>
 * 
 * 
 * <h2>ENCRYPTION</h2>
 * 
 * <p>
 * Each encryption involves updating the index values, moving (up to) 4 bytes
 * around in the permutation vector, selecting an output byte, and adding the
 * output byte bitwise modulo-2 (exclusive-or) to the plain text byte to produce
 * the cipher text byte. The index values are incremented by different rules.
 * The index called rotor just increases by one (modulo 256) each time. Ratchet
 * increases by the value in the permutation vector pointed to by rotor.
 * Avalanche increases by the value in the permutation vector pointed to by
 * another byte in the permutation vector pointed to by the last cipher text
 * byte. The last plain text and the last cipher text bytes are also kept as
 * index variables. See the function called encrypt(), below for details.
 * </p>
 * 
 * 
 * <h2>PSUEDORANDOM BYTE GENERATION</h2>
 * 
 * <p>
 * If you want to generate random numbers without encrypting any particular
 * ciphertext, simply encrypt 0. There is still plenty of complexity left in the
 * system to ensure unpredictability (if the key is not known) of the output
 * stream when this simplification is made.
 * </p>
 * 
 * 
 * <h2>DECRYPTION</h2>
 * 
 * <p>
 * Decryption is the same as encryption, except for the obvious swapping of the
 * assignments to last_plain and last_cipher and the return value. See the
 * function decrypt(), below.
 * </p>
 * 
 * 
 * <h2>C++ SOURCE CODE FRAGMENT</h2>
 * 
 * <p>
 * The original implimentation of this cipher was in Object Oriented Pascal, but
 * C++ is available for more platforms.
 * </p>
 * 
 * <h2>GENERATION OF CRYPTOGRAPHIC CHECK VALUES (HASH VALUES)</h2>
 * 
 * <p>
 * For a fast way to generate a cryptographic check value (also called a hash or
 * message integrity check value) of a message of arbitrary length:
 * </p>
 * <ol>
 * <li>Initialize either with a key (for a keyed hash value) or call hash_init
 * with no key (for a public hash value).</li>
 * 
 * <li>Encrypt all of the bytes of the message or file to be hashed. The results
 * of the encryption need not be kept for the hash generation process.
 * (Optionally decrypt encrypted text, here).</li>
 * 
 * <li>Call hash_final, which will further "stir" the permutation vector by
 * encrypting the values from 255 down to 0, then report the results of
 * encrypting 20 zeroes.</li>
 * </ol>
 * 
 * <h2>SECURITY ANALYSIS</h2>
 * 
 * <p>
 * There are several security issues to be considered. Some are easier to
 * analyze than others. The following includes more "hand waving" than
 * mathematical proofs, and looks more like it was written by an engineer than a
 * mathematician. The reader is invited to improve upon or refute the following,
 * as appropriate.
 * </p>
 * 
 * 
 * <h2>KEY LENGTH</h2>
 * 
 * <p>
 * There are really two kinds of user keys to consider: (1) random binary keys,
 * and (2) pass phrases. Analysis of random binary keys is fairly straight
 * forward. Pass phrases tend to have much less entropy per byte, but the
 * analysis made for random binary keys applies to the entropy in the pass
 * phrase. The length limit of the key (255 bytes) is adequate to allow a pass
 * phrase with enough entropy to be considered strong.
 * </p>
 * 
 * <p>
 * To be real generous to a cryptanalyst, assume dedicated Sapphire Stream
 * Cipher cracking hardware. The constant portion of the key scheduling can be
 * done in one cycle. That leaves at least 256 cycles to do the swapping
 * (probably more, because of the intricacies of keyrand(), but we'll ignore
 * that, too, for now). Assume a machine clock of about 256 MegaHertz (fairly
 * generous). That comes to about one key tried per microsecond. On average, you
 * only have to try half of the keys. Also assume that trying the key to see if
 * it works can be pipelined, so that it doesn't add time to the estimate. Based
 * on these assumptions (reasonable for major governments), and rounding to two
 * significant digits, the following key length versus cracking time estimates
 * result:
 * </p>
 * 
 * <pre>
 *     Key length, bits    Time to crack
 *     ----------------    -------------
 *                   32    35 minutes (exportable in qcrypt)
 *                   33    1.2 hours (not exportable in qcrypt)
 *                   40    6.4 days
 *                   56    1,100 years (kind of like DES's key)
 *                   64    290,000 years (good enough for most things)
 *                   80    19 billion years (kind of like Skipjack's key)
 *                  128    5.4E24 years (good enough for the clinically paranoid)
 * </pre>
 * 
 * <p>
 * Naturally, the above estimates can vary by several orders of magnitude based
 * on what you assume for attacker's hardware, budget, and motivation.
 * </p>
 * 
 * <p>
 * In the range listed above, the probability of spare keys (two keys resulting
 * in the same initial permutation vector) is small enough to ignore. The proof
 * is left to the reader.
 * </p>
 * 
 * 
 * <h2>INTERNAL STATE SPACE</h2>
 * 
 * <p>
 * For a stream cipher, internal state space should be at least as big as the
 * number of possible keys to be considered strong. The state associated with
 * the permutation vector alone (256!) constitutes overkill.
 * </p>
 * 
 * 
 * <h2>PREDICTABILITY OF THE STATE</h2>
 * 
 * <p>
 * If you have a history of stream output from initialization (or equivalently,
 * previous known plaintext and ciphertext), then rotor, last_plain, and
 * last_cipher are known to an attacker. The other two index values, flipper and
 * avalanche, cannot be solved for without knowing the contents of parts of the
 * permutation vector that change with each byte encrypted. Solving for the
 * contents of the permutation vector by keeping track of the possible positions
 * of the index variables and possible contents of the permutation vector at
 * each byte position is not possible, since more variables than known values
 * are generated at each iteration. Indeed, fewer index variables and swaps
 * could be used to achieve security, here, if it were not for the hash
 * requirements.
 * </p>
 * 
 * 
 * <h2>CRYPTOGRAPHIC CHECK VALUE</h2>
 * 
 * <p>
 * The change in state altered with each byte encrypted contributes to an
 * avalanche of generated check values that is radically different after a
 * sequence of at least 64 bytes have been encrypted. The suggested way to
 * create a cryptographic check value is to encrypt all of the bytes of a
 * message, then encrypt a sequence of bytes counting down from 255 to 0. A
 * single bit change in a message causes a radical change in the check value
 * generated (about half of the bits change). This is an essential feature of a
 * cryptographic check value.
 * </p>
 * 
 * <p>
 * Another good property of a cryptographic check value is that it is too hard
 * to compute a message that results in a certain check value. In this case, we
 * assume the attacker knows the key and the contents of a message that has the
 * desired check value, and wants to compute a bogus message having the same
 * check value. There are two obvious ways to do this attack. One is to solve
 * for a sequence that will restore the state of the permutation vector and
 * indices back to what it was before the alteration. The other one is the
 * so-called "birthday" attack that is to cryptographic hash functions what
 * brute force is to key search.
 * </p>
 * 
 * <p>
 * To generate a sequence that restores the state of the cipher to what it was
 * before the alteration probably requires at least 256 bytes, since the index
 * "rotor" marches steadily on its cycle, one by one. The values to do this
 * cannot easily be computed, due to the nonlinearity of the feedback, so there
 * would probably have to be lots of trial and error involved. In practical
 * applications, this would leave a gaping block of binary garbage in the middle
 * of a document, and would be quite obvious, so this is not a practical attack,
 * even if you could figure out how to do it (and I haven't). If anyone has a
 * method to solve for such a block of data, though, I would be most interested
 * in finding out what it is. Please email me at
 * &lt;m.p.johnson&#064;ieee.org&gt; if you find one.
 * </p>
 * 
 * <p>
 * The "birthday" attack just uses the birthday paradox to find a message that
 * has the same check value. With a 20 byte check value, you would have to find
 * at least 80 bits to change in the text such that they wouldn't be noticed (a
 * plausible situation), then try the combinations until one matches. 2 to the
 * 80th power is a big number, so this isn't practical either. If this number
 * isn't big enough, you are free to generate a longer check value with this
 * algorithm. Someone who likes 16 byte keys might prefer 32 byte check values
 * for similar stringth.
 * </p>
 * 
 * 
 * <h2>ADAPTIVE CHOSEN PLAIN TEXT ATTACKS</h2>
 * 
 * <p>
 * Let us give the attacker a keyed black box that accepts any input and
 * provides the corresponding output. Let us also provide a signal to the black
 * box that causes it to reoriginate (revert to its initial keyed state) at the
 * attacker's will. Let us also be really generous and provide a free copy of
 * the black box, identical in all respects except that the key is not provided
 * and it is not locked, so the array can be manipulated directly.
 * </p>
 * 
 * <p>
 * Since each byte encrypted only modifies at most 5 of the 256 bytes in the
 * permutation vector, and it is possible to find different sequences of two
 * bytes that leave the five index variables the same, it is possible for the
 * attacker to find sets of chosen plain texts that differ in two bytes, but
 * which have cipher texts that are the same for several of the subsequent
 * bytes. Modeling indicates that as many as ten of the following bytes
 * (although not necessarily the next ten bytes) might match. This information
 * would be useful in determining the structure of the Sapphire Stream Cipher
 * based on a captured, keyed black box. This means that it would not be a good
 * substitute for the Skipjack algorithm in the EES, but we assume that the
 * attacker already knows the algorithm, anyway. This departure from the
 * statistics expected from an ideal stream cipher with feedback doesn't seem to
 * be useful for determining any key bytes or permutation vector bytes, but it
 * is the reason why post-conditioning is required when computing a
 * cryptographic hash with the Sapphire Stream Cipher. Thanks to Bryan G.
 * Olson's &lt;olson&#064;umbc.edu&gt; continued attacks on the Sapphire Stream
 * Cipher, I have come up with the Sapphire II Stream Cipher. Thanks again to
 * Bryan for his valuable help.
 * </p>
 * 
 * <p>
 * Bryan Olson's "differential" attack of the original Sapphire Stream Cipher
 * relies on both of these facts:
 * </p>
 * 
 * <ol>
 * <li>By continual reorigination of a black box containing a keyed version of
 * the Sapphire Stream Cipher, it is possible to find a set of input strings
 * that differ only in the first two (or possibly three) bytes that have
 * identical output after the first three (or possibly four) bytes. The output
 * suffixes so obtained will not contain the values of the permutation vector
 * bytes that <i>differ</i> because of the different initial bytes encrypted.</li>
 * 
 * <li>Because the five index values are initialized to constants that are known
 * by the attacker, most of the locations of the "missing" bytes noted in the
 * above paragraph are known to the attacker (except for those indexed by the
 * ratchet index variable for encryptions after the first byte).</li>
 * </ol>
 * 
 * <p>
 * I have not yet figured out if Bryan's attack on the original Sapphire Stream
 * Cipher had complexity of more or less than the design strength goal of 2^64
 * encryptions, but some conservative estimations I made showed that it could
 * possibly come in significantly less than that. (I would probably have to
 * develop a full practical attack to accurately estimate the complexity more
 * accurately, and I have limited time for that). Fortunately, there is a way to
 * frustrate this type of attack without fully developing it.
 * </p>
 * 
 * <p>
 * Denial of condition 1 above by increased alteration of the state variables is
 * too costly, at least using the methods I tried. For example, doubling the
 * number of index variables and the number of permutation vector items
 * referenced in the output function of the stream cipher provides only doubles
 * the cost of getting the data in item 1, above. This is bad crypto-economics.
 * A better way is to change the output function such that the stream cipher
 * output byte is a combination of two permutation vector bytes instead of one.
 * That means that all possible output values can occur in the differential
 * sequences of item 1, above.
 * </p>
 * 
 * <p>
 * Denial of condition 2 above, is simpler. By making the initial values of the
 * five index variables dependent on the key, Bryan's differential attack is
 * defeated, since the attacker has no idea which elements of the permutation
 * vector were different between data sets, and exhaustive search is too
 * expensive.
 * </p>
 * 
 * 
 * <h2>OTHER HOLES</h2>
 * 
 * <p>
 * Are there any? Take you best shot and let me know if you see any. I offer no
 * challenge text with this algorithm, but you are free to use it without
 * royalties to me if it is any good.
 * </p>
 * 
 * 
 * <h2>CURRENT STATUS</h2>
 * 
 * <p>
 * This is a new (to the public) cipher, and an even newer approach to
 * cryptographic hash generation. Take your best shot at it, and please let me
 * know if you find any weaknesses (proven or suspected) in it. Use it with
 * caution, but it still looks like it fills a need for reasonably strong
 * cryptography with limited resources.
 * </p>
 * 
 * 
 * <h2>LEGAL STUFF</h2>
 * 
 * <p>
 * The intention of this document is to share some research results on an
 * informal basis. You may freely use the algorithm and code listed above as far
 * as I'm concerned, as long as you don't sue me for anything, but there may be
 * other restrictions that I am not aware of to your using it. The C++ code
 * fragment above is just intended to illustrate the algorithm being discussed,
 * and is not a complete application. I understand this document to be
 * Constitutionally protected publication, and not a munition, but don't blame
 * me if it explodes or has toxic side effects.
 * </p>
 * 
 * <pre>
 *                   ___________________________________________________________
 *                  |                                                           |
 *  |\  /| |        | Michael Paul Johnson  Colorado Catacombs BBS 303-772-1062 |
 *  | \/ |o|        | PO Box 1151, Longmont CO 80502-1151 USA      John 3:16-17 |
 *  |    | | /  _   | mpj&#064;csn.org aka mpj&#064;netcom.com m.p.johnson&#064;ieee.org       |
 *  |    |||/  /_\  | ftp://ftp.csn.net/mpj/README.MPJ          CIS: 71331,2332 |
 *  |    |||\  (    | ftp://ftp.netcom.com/pub/mp/mpj/README  -. --- ----- .... |
 *  |    ||| \ \_/  | PGPprint=F2 5E A1 C1 A6 CF EF 71  12 1F 91 92 6A ED AE A9 |
 *                  |___________________________________________________________|
 * </pre>
 * 
 * Regarding this port to Java and not the original code, the following license
 * applies:
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Michael Paul Johnson [ kahunapule at mpj dot cx] Original code
 * @author unascribed Sword's C++ implementation
 * @author DM Smith Java port from Sword's C++ implementation
 */
public class Sapphire {

    /**
     * Construct a Sapphire Stream Cipher from a key, possibly null or empty.
     * 
     * @param aKey the cipher key
     */
    public Sapphire(byte[] aKey) {
        byte[] key = aKey;
        if (key == null) {
            key = new byte[0];
        }
        cards = new int[256];
        if (key.length > 0) {
            initialize(key);
        } else {
            hashInit();
        }
    }

    /**
     * Decipher a single byte, presumably the next.
     * 
     * @param b
     *            the next byte to decipher
     * @return the enciphered byte
     */
    public byte cipher(byte b) {
        // Picture a single enigma rotor with 256 positions, rewired
        // on the fly by card-shuffling.

        // This cipher is a variant of one invented and written
        // by Michael Paul Johnson in November, 1993.

        // Shuffle the deck a little more.

        // Convert from a byte to an int, but prevent sign extension.
        // So -16 becomes 240
        int bVal = b & 0xFF;
        ratchet += cards[rotor++];
        // Keep ratchet and rotor in the range of 0-255
        // The C++ code relied upon overflow of an unsigned char
        ratchet &= 0xFF;
        rotor &= 0xFF;
        int swaptemp = cards[lastCipher];
        cards[lastCipher] = cards[ratchet];
        cards[ratchet] = cards[lastPlain];
        cards[lastPlain] = cards[rotor];
        cards[rotor] = swaptemp;
        avalanche += cards[swaptemp];
        // Keep avalanche in the range of 0-255
        avalanche &= 0xFF;

        // Output one byte from the state in such a way as to make it
        // very hard to figure out which one you are looking at.
        lastPlain = bVal ^ cards[(cards[ratchet] + cards[rotor]) & 0xFF] ^ cards[cards[(cards[lastPlain] + cards[lastCipher] + cards[avalanche]) & 0xFF]];

        lastCipher = bVal;

        // Convert back to a byte
        // E.g. 240 becomes -16
        return (byte) lastPlain;
    }

    /**
     * Destroy the key and state information in RAM.
     */
    public void burn() {
        // Destroy the key and state information in RAM.
        for (int i = 0; i < 256; i++) {
            cards[i] = 0;
        }
        rotor = 0;
        ratchet = 0;
        avalanche = 0;
        lastPlain = 0;
        lastCipher = 0;
    }

    /**
     * @param hash the destination
     */
    public void hashFinal(byte[] hash) { // Destination
        for (int i = 255; i >= 0; i--) {
            cipher((byte) i);
        }
        for (int i = 0; i < hash.length; i++) {
            hash[i] = cipher((byte) 0);
        }
    }

    /**
     * Initializes the cards array to be deterministically random based upon the
     * key.
     * <p>
     * Key size may be up to 256 bytes. Pass phrases may be used directly, with
     * longer length compensating for the low entropy expected in such keys.
     * Alternatively, shorter keys hashed from a pass phrase or generated
     * randomly may be used. For random keys, lengths of from 4 to 16 bytes are
     * recommended, depending on how secure you want this to be.
     * </p>
     * 
     * @param key
     *            used to initialize the cipher engine.
     */
    private void initialize(byte[] key) {

        // Start with cards all in order, one of each.
        for (int i = 0; i < 256; i++) {
            cards[i] = i;
        }

        // Swap the card at each position with some other card.
        int swaptemp;
        int toswap = 0;
        keypos = 0; // Start with first byte of user key.
        rsum = 0;
        for (int i = 255; i >= 0; i--) {
            toswap = keyrand(i, key);
            swaptemp = cards[i];
            cards[i] = cards[toswap];
            cards[toswap] = swaptemp;
        }

        // Initialize the indices and data dependencies.
        // Indices are set to different values instead of all 0
        // to reduce what is known about the state of the cards
        // when the first byte is emitted.
        rotor = cards[1];
        ratchet = cards[3];
        avalanche = cards[5];
        lastPlain = cards[7];
        lastCipher = cards[rsum];

        // ensure that these have no useful values to those that snoop
        toswap = 0;
        swaptemp = toswap;
        rsum = swaptemp;
        keypos = rsum;
    }

    /**
     * Initialize non-keyed hash computation.
     */
    private void hashInit() {

        // Initialize the indices and data dependencies.
        rotor = 1;
        ratchet = 3;
        avalanche = 5;
        lastPlain = 7;
        lastCipher = 11;

        // Start with cards all in inverse order.

        int j = 255;
        for (int i = 0; i < 256; i++) {
            cards[i] = j--;
        }
    }

    private int keyrand(int limit, byte[] key) {
        int u; // Value from 0 to limit to return.

        if (limit == 0) {
            return 0; // Avoid divide by zero error.
        }

        int retryLimiter = 0; // No infinite loops allowed.

        // Fill mask with enough bits to cover the desired range.
        int mask = 1;
        while (mask < limit) {
            mask = (mask << 1) + 1;
        }

        do {
            // Convert a byte from the key to an int, but prevent sign
            // extension.
            // So -16 becomes 240
            // Also keep rsum in the range of 0-255
            // The C++ code relied upon overflow of an unsigned char
            rsum = (cards[rsum] + (key[keypos++] & 0xFF)) & 0xFF;

            if (keypos >= key.length) {
                keypos = 0; // Recycle the user key.
                rsum += key.length; // key "aaaa" != key "aaaaaaaa"
                rsum &= 0xFF;
            }

            u = mask & rsum;

            if (++retryLimiter > 11) {
                u %= limit; // Prevent very rare long loops.
            }
        } while (u > limit);
        return u;
    }

    private int[] cards;
    private int rotor;
    private int ratchet;
    private int avalanche;
    private int lastPlain;
    private int lastCipher;
    private int keypos;
    private int rsum;
}
