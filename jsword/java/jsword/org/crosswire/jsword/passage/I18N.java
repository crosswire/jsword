
package org.crosswire.jsword.passage;

import org.crosswire.common.util.I18NBase;

/**
 * Compile safe I18N resource settings.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class I18N extends I18NBase
{
    public static final I18N BOOKS_BOOK = new I18N("Book must be between 1 and 66. (Given {0,number,integer}).");
    public static final I18N BOOKS_SECTION = new I18N("Section must be between 1 and 8. (Given {0,number,integer}).");
    public static final I18N BOOKS_NUMBER = new I18N("The string \"{0}\" can\'t be a book, because it is a number.");
    public static final I18N BOOKS_FIND = new I18N("Can not understand \"{0}\" as a book.");
    public static final I18N BOOKS_BOOKCHAP = new I18N("Book must be between 1 and 66. (Given {0,number,integer}), and Chapter must be valid for this book. (Given {1,number,integer}).");
    public static final I18N BOOKS_ORDINAL = new I18N("Must be 3 parts to the reference.");
    public static final I18N BOOKS_DECODE = new I18N("Ordinal must be between 1 and {0,number,integer}. (Given {1,number,integer}).");
    public static final I18N BOOKS_CHAPTER = new I18N("Chapter should be between 1 and {0,number,integer} for {1} (given {2,number,integer}).");
    public static final I18N BOOKS_VERSE = new I18N("Verse should be between 1 and {0,number,integer} for {1} {2,number,integer} (given {3,number,integer})");
    
    public static final I18N RANGE_PARTS = new I18N("A verse range can\'t have more than 2 parts. (Parts are separated by {0}) Given {1}");
    public static final I18N RANGE_BLURS = new I18N("Illegal blurs");
    public static final I18N RANGE_BLURBOOK = new I18N("Can't blur by book in this context");
    public static final I18N RANGE_BLURNONE = new I18N("Illegal blur mode.");
    public static final I18N RANGE_LOCOUNT = new I18N("Verse count must be >= 1");
    public static final I18N RANGE_HICOUNT = new I18N("Too many verses in range '{0}'. Max is {1,number,integer}, given {2,number,integer}");

    public static final I18N VERSE_PARTS = new I18N("Too many parts to the Verse. (Parts are separated by any of {0})");
    public static final I18N VERSE_PARSE = new I18N("Can not understand {0} as a chapter or verse.");

    public static final I18N ERROR_READONLY = new I18N("Attempt to write to a read-only passage.");
    public static final I18N ERROR_INDEX = new I18N("The given index ({0}) is out of range. (Maximum allowed is {1})");
    public static final I18N ERROR_JOGGER = new I18N("Unknown memory jogger");
    public static final I18N ERROR_CASE = new I18N("Unknown case setting: {0}");
    public static final I18N ERROR_PATCH = new I18N("Use patch=true.");

    public static final I18N PASSAGE_UNKNOWN = new I18N("Unknown passage type.");

    public static final I18N ERROR_BLUR = new I18N("Illegal value for blur restriction");
    public static final I18N ERROR_MIXED = new I18N("MIXED case should only exist with LORD\'s");
    public static final I18N ERROR_BADCASE = new I18N("Case must be 0-3");

    public static final I18N ABSTRACT_CAST = new I18N("Can only use Verses and VerseRanges in this Collection");
    public static final I18N ABSTRACT_TYPE = new I18N("Unknown Passage Type");
    public static final I18N ABSTRACT_INDEX = new I18N("Index out of range (Given {0,number,integer}, Max {1,number,integer}).");

/*
    range_error_blur_book=RESTRICT_BOOK is not supported.
    range_error_blur_mode=Illegal blur mode.
    range_error_null=Verse can not be null
    range_error_blur_negative=Negative blurring is not allowed
    range_error_count=verse_count must be greater than 0
    range_error_size=Starting at {0}, there are only {1,number,integer} verses left in the Bible. {2,number,integer} is too many.
    range_error_patch=patch_up\=false is not supported. See the JavaDoc
*/

    /** Initialise any resource bundles */
    static
    {
        init(I18N.class.getName());
    }

    /** Passthrough ctor */
    private I18N(String name)
    {
        super(name);
    }
}
