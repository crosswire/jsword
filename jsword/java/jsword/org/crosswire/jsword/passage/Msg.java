package org.crosswire.jsword.passage;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Msg extends MsgBase
{
    static final Msg TALLY_ERROR_ENUM = new Msg("nextElement() has not been called yet.");
    static final Msg TALLY_ERROR_ORDER = new Msg("Ordering must be one of ORDER_BIBLICAL or ORDER_TALLY");

    static final Msg PASSAGE_READONLY = new Msg("Can't alter a read-only passage");

    static final Msg ABSTRACT_VERSE_SINGULAR = new Msg("verse in");
    static final Msg ABSTRACT_VERSE_PLURAL = new Msg("verses in");
    static final Msg ABSTRACT_BOOK_SINGULAR = new Msg("book");
    static final Msg ABSTRACT_BOOK_PLURAL = new Msg("books");

    static final Msg BOOKS_BOOK = new Msg("Book must be between 1 and 66. (Given {0,number,integer}).");
    static final Msg BOOKS_SECTION = new Msg("Section must be between 1 and 8. (Given {0,number,integer}).");
    static final Msg BOOKS_NUMBER = new Msg("The string \"{0}\" can\'t be a book, because it is a number.");
    static final Msg BOOKS_FIND = new Msg("Can not understand \"{0}\" as a book.");
    static final Msg BOOKS_BOOKCHAP = new Msg("Book must be between 1 and 66. (Given {0,number,integer}), and Chapter must be valid for this book. (Given {1,number,integer}).");
    static final Msg BOOKS_ORDINAL = new Msg("Must be 3 parts to the reference.");
    static final Msg BOOKS_DECODE = new Msg("Ordinal must be between 1 and {0,number,integer}. (Given {1,number,integer}).");
    static final Msg BOOKS_CHAPTER = new Msg("Chapter should be between 1 and {0,number,integer} for {1} (given {2,number,integer}).");
    static final Msg BOOKS_VERSE = new Msg("Verse should be between 1 and {0,number,integer} for {1} {2,number,integer} (given {3,number,integer})");
    
    static final Msg RANGE_PARTS = new Msg("A verse range can\'t have more than 2 parts. (Parts are separated by {0}) Given {1}");
    static final Msg RANGE_BLURS = new Msg("Illegal blurs");
    static final Msg RANGE_BLURBOOK = new Msg("Can't blur by book in this context");
    static final Msg RANGE_BLURNONE = new Msg("Illegal blur mode.");
    static final Msg RANGE_LOCOUNT = new Msg("Verse count must be >= 1");
    static final Msg RANGE_HICOUNT = new Msg("Too many verses in range {0}. Max is {1,number,integer}, given {2,number,integer}");
    static final Msg RANGE_BADCHAPTER = new Msg("Cant understand {0} as a chapter.");

    static final Msg VERSE_PARTS = new Msg("Too many parts to the Verse. (Parts are separated by any of {0})");
    static final Msg VERSE_PARSE = new Msg("Can not understand {0} as a chapter or verse.");

    static final Msg ERROR_READONLY = new Msg("Attempt to write to a read-only passage.");
    static final Msg ERROR_INDEX = new Msg("The given index ({0}) is out of range. (Maximum allowed is {1})");
    static final Msg ERROR_JOGGER = new Msg("Unknown memory jogger");
    static final Msg ERROR_CASE = new Msg("Unknown case setting: {0}");
    static final Msg ERROR_PATCH = new Msg("Use patch=true.");

    static final Msg PASSAGE_UNKNOWN = new Msg("Unknown passage type.");

    static final Msg ERROR_LOGIC = new Msg("Logic Error");
    static final Msg ERROR_BLUR = new Msg("Illegal value for blur restriction");
    static final Msg ERROR_MIXED = new Msg("MIXED case should only exist with LORD\'s");
    static final Msg ERROR_BADCASE = new Msg("Case must be 0-3");

    static final Msg ABSTRACT_CAST = new Msg("Can only use Verses and VerseRanges in this Collection");
    static final Msg ABSTRACT_TYPE = new Msg("Unknown Passage Type");
    static final Msg ABSTRACT_INDEX = new Msg("Index out of range (Given {0,number,integer}, Max {1,number,integer}).");

    static final Msg KEYLIST_READONLY = new Msg("Can't alter a read-only key list");

    /*
    range_error_blur_book=RESTRICT_BOOK is not supported.
    range_error_blur_mode=Illegal blur mode.
    range_error_null=Verse can not be null
    range_error_blur_negative=Negative blurring is not allowed
    range_error_count=verse_count must be greater than 0
    range_error_size=Starting at {0}, there are only {1,number,integer} verses left in the Bible. {2,number,integer} is too many.
    range_error_patch=patch_up\=false is not supported. See the JavaDoc
    */

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
