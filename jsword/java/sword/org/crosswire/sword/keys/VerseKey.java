package org.crosswire.sword.keys;

import org.crosswire.sword.mgr.SWLocale;
import org.crosswire.sword.mgr.LocaleMgr;

/**
 *  versekey.h - code for class 'versekey'- a standard Biblical verse key
 *
 * $Id$
 *
 * Copyright 1998 CrossWire Bible Society (http://www.crosswire.org)
 *  CrossWire Bible Society
 *  P. O. Box 2528
 *  Tempe, AZ  85280-2528
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation version 2.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 */
public class VerseKey extends SWKey
{
    public static final char MAXVERSE = 3;
    public static final char MAXCHAPTER = 4;
    public static final char MAXBOOK = 5;

    private VerseKey upperBound = null;
    private VerseKey lowerBound = null;

    class LocaleCache
    {
        String name;
        int abbrevsCnt;
        SWLocale locale;
        LocaleCache()
        {
            name = null;
            abbrevsCnt = 0;
            locale = null;
        }
    }

    Canon.Abbrev[] abbrevs = null;
    int abbrevsCnt;

    int testament; // 0 - Old; 1 - New
    int book;
    int chapter;
    int verse;
    boolean autonorm; // flag for auto normalization
    boolean headings; // flag for headings on/off

    char[] BMAX;
    Canon.Book[][] books;

    public void setText(String ikey)
    {
        super.setText(ikey);
        parse();
    }

    boolean isTraversable()
    {
        return true;
    }

    /**
     *  Initialize static members of VerseKey
     */
    static Canon.Book builtin_books[] = null;
    static char builtin_BMAX[] = { 39, 27 };

    // after canon.h is imported to this file then think about this line
    //  static long offsets[2][2]  = {{VerseKey::otbks, VerseKey::otcps}, {VerseKey::ntbks, VerseKey::ntcps}};
    static LocaleCache localeCache = null;

    /**
     * VerseKey::init - initializes instance of VerseKey
     */
    void init()
    {
        if (builtin_books == null)
            initStatics();

        autonorm = true; // default auto normalization to true
        headings = false; // default display headings option is false
        testament = 0;
        book = 0;
        chapter = 0;
        verse = 0;

        setLocale(LocaleMgr.systemLocaleMgr.getDefaultLocaleName());
    }

    /**
     * VerseKey Constructor - initializes instance of VerseKey
     *
     * ENT:	ikey - base key (will take various forms of 'BOOK CH:VS'.  See
     *		VerseKey::parse for more detailed information)
     */
    public VerseKey()
    {
        this("");
    }

    public VerseKey(SWKey ikey)
    {
        super(ikey);
        init();
        if (ikey != null)
            parse();
    }

    /**
     * VerseKey Constructor - initializes instance of VerseKey
     *
     * ENT:	ikey - text key (will take various forms of 'BOOK CH:VS'.  See
     *		VerseKey::parse for more detailed information)
     */
    VerseKey(String ikey)
    {
        super(ikey);
        init();
        if (ikey != null)
            parse();
    }

    VerseKey(VerseKey k)
    {
        super(k.keytext);
        init();
        autonorm = k.autonorm;
        headings = k.headings;
        testament = k.getTestament();
        book = k.getBook();
        chapter = k.getChapter();
        verse = k.getVerse();
        setLowerBound(k.getLowerBound());
        setUpperBound(k.getUpperBound());
    }

    VerseKey(String min, String max)
    {
        super();
        init();
        setLowerBound(min);
        setUpperBound(max);
        this.position(TOP);
    }

    public Object clone()
    {
        return new VerseKey(this);
    }

    public void setLocale(String name)
    {
        // char []BMAX;
        // Canon.Book books[][];
        boolean useCache = false;

        if (localeCache.name != null)
            useCache = (localeCache.name.equals(name));

        if (!useCache)
        { // if we're setting params for a new locale
            localeCache.name = name;
            localeCache.abbrevsCnt = 0;
        }

        /*
        	SWLocale locale = (useCache) ? localeCache.locale : LocaleMgr.systemLocaleMgr.getLocale(name);
        	localeCache.locale = locale;
        
        	if (locale != null) {
        		locale.getBooks(BMAX, books);
        		setBooks(BMAX, books);
        		setBookAbbrevs(locale.getBookAbbrevs(), localeCache.abbrevsCnt);
        		localeCache.abbrevsCnt = abbrevsCnt;
        	}
        	else {
        		setBooks(builtin_BMAX, builtin_books);
        		setBookAbbrevs(builtin_abbrevs, localeCache.abbrevsCnt);
        		localeCache.abbrevsCnt = abbrevsCnt;
        	}
        */
    }

    public void setBooks(char[] iBMAX, Canon.Book[][] ibooks)
    {
        BMAX = iBMAX;
        books = ibooks;
    }

    public void setBookAbbrevs(Canon.Abbrev[] bookAbbrevs, int size)
    {
        abbrevs = bookAbbrevs;
        if (size == 0)
        {
            for (abbrevsCnt = 1; abbrevs[abbrevsCnt].ab != null; abbrevsCnt++)
            {
            }
        }
        else
            abbrevsCnt = size;
    }

    /**
     * VerseKey::initstatics - initializes statics.  Performed only when first
     *						instance on VerseKey (or descendent) is created.
     */
    private void initStatics()
    {
        /*
        	int l1, l2, chaptmp = 0;
        
        	builtin_books[0] = otbooks;
        	builtin_books[1] = ntbooks;
        
        	for (l1 = 0; l1 < 2; l1++) {
        		for (l2 = 0; l2 < builtin_BMAX[l1]; l2++) {
        			builtin_books[l1][l2].versemax = &vm[chaptmp];
        			chaptmp += builtin_books[l1][l2].chapmax;
        		}
        	}
        */
    }

    /**
     * VerseKey::parse - parses keytext into testament|book|chapter|verse
     *
     * RET:	error status
     */
    protected int parse()
    {
        /*
    	testament = 1;
    	book      = 1;
    	chapter   = 1;
    	verse     = 1;
    
    	error     = 0;
    
    	if (keytext) {
    		ListKey tmpListKey = VerseKey::ParseVerseList(keytext);
    		if (tmpListKey.Count()) {
    			SWKey::operator =((const char *)tmpListKey);
    			for (testament = 1; testament < 3; testament++) {
    				for (book = 1; book <= BMAX[testament-1]; book++) {
    					if (!strncmp(keytext, books[testament-1][book-1].name, strlen(books[testament-1][book-1].name)))
    						break;
    				}
    				if (book <= BMAX[testament-1])
    					break;
    			}
    
    			if (testament < 3) {
    				sscanf(&keytext[strlen(books[testament-1][book-1].name)], "%d:%d", &chapter, &verse);
    			}
    			else	error = 1;
    		}
    	}
    	Normalize(1);
    	freshtext();        
        */
        return error;
    }

    /**
     * VerseKey::freshtext - refreshes keytext based on
     *				testament|book|chapter|verse
     */
    void freshtext()
    {
        /*
    	char buf[254];
    	int realtest = testament;
    	int realbook = book;
    
    	if (book < 1) {
    		if (testament < 1)
    			sprintf(buf, "[ Module Heading ]");
    		else sprintf(buf, "[ Testament %d Heading ]", testament);
    	}
    	else {
    		if (realbook > BMAX[realtest-1]) {
    			realbook -= BMAX[realtest-1];
    			realtest++;
    		}
    		sprintf(buf, "%s %d:%d", books[realtest-1][realbook-1].name, chapter, verse);
    	}
    
    	stdstr((char **)&keytext, buf);
        */
    }

    /**
     * VerseKey::getBookAbbrev - Attempts to find a book abbreviation for a buffer
     *
     * ENT:	abbr - key for which to search;
     * RET:	book number or < 0 = not valid
     */
    int getBookAbbrev(String abbr)
    {
        // int loop
        int diff, abLen, min, max, target;

        abbr = abbr.trim().toUpperCase();
        abLen = abbr.length();

        if (abLen > 0)
        {
            min = 0;
            max = abbrevsCnt;
            while (true)
            {
                target = min + ((max - min) / 2);
                diff = abbr.compareTo(abbrevs[target].ab.length() > abLen ? abbrevs[target].ab.substring(1, abLen) : abbrevs[target].ab);
                if ((diff == 0) || (target >= max) || (target <= min))
                    break;
                if (diff > 0)
                    min = target;
                else
                    max = target;
            }
            return (diff == 0) ? abbrevs[target].book : -1;
        }
        else
            return -1;
    }

    /**
     * VerseKey::ParseVerseList - Attempts to parse a buffer into separate
     *				verse entries returned in a ListKey
     *
     * ENT:	buf		- buffer to parse;
     *	defaultKey	- if verse, chap, book, or testament is left off,
     *				pull info from this key (ie. Gen 2:3; 4:5;
     *				Gen would be used when parsing the 4:5 section)
     *	expandRange	- whether or not to expand eg. John 1:10-12 or just
     *				save John 1:10
     *
     * RET:	ListKey reference filled with verse entries contained in buf
     *
     * COMMENT: This code works but wreaks.  Rewrite to make more maintainable.
     */
    public ListKey ParseVerseList(String buf, String defaultKey, boolean expandRange)
    {
        // SWKey textkey;
        ListKey internalListKey = new ListKey();

        /*
    	char book[255];
    	char number[255];
    	char tobook = 0;
    	char tonumber = 0;
    	char lastchar = 0;
    	int chap = -1, verse = -1;
    	int bookno = 0;
    	VerseKey curkey, lBound;
    	int loop;
    	char comma = 0;
    	char dash = 0;
    	const char *orig = buf;
    	ListKey tmpListKey;
    	SWKey tmpDefaultKey = defaultKey;
    	char lastPartial = 0;
    
    	curkey.AutoNormalize(0);
    	tmpListKey << tmpDefaultKey;
    
    	while (*buf) {
    		switch (*buf) {
    		case ':':
    			number[tonumber] = 0;
    			tonumber = 0;
    			if (*number)
    				chap = atoi(number);
    			*number = 0;
    			break;
    
    		case '-':
    		case ',': // on number new verse
    		case ';': // on number new chapter
    			number[tonumber] = 0;
    			tonumber = 0;
    			if (*number) {
    				if (chap >= 0)
    					verse = atoi(number);
    				else	chap = atoi(number);
    			}
    			*number = 0;
    			book[tobook] = 0;
    			tobook = 0;
    			bookno = -1;
    			if (*book) {
    				for (loop = strlen(book) - 1; loop+1; loop--) {
    					if ((isdigit(book[loop])) || (book[loop] == ' ')) {
    						book[loop] = 0;
    						continue;
    					}
    					else {
    						if ((toupper(book[loop])=='F')&&(loop)) {
    							if ((isdigit(book[loop-1])) || (book[loop-1] == ' ') || (toupper(book[loop-1]) == 'F')) {
    								book[loop] = 0;
    								continue;
    							}
    						}
    					}
    					break;
    				}
    				bookno = getBookAbbrev(book);
    			}
    			if (((bookno > -1) || (!*book)) && ((*book) || (chap >= 0) || (verse >= 0))) {
    				char partial = 0;
    				curkey.Verse(1);
    				curkey.Chapter(1);
    				curkey.Book(1);
    
    				if (bookno < 0) {
    					curkey.Testament(VerseKey(tmpListKey).Testament());
    					curkey.Book(VerseKey(tmpListKey).Book());
    				}
    				else {
    					curkey.Testament(1);
    					curkey.Book(bookno);
    				}
    
    				if (((comma)||((verse < 0)&&(bookno < 0)))&&(!lastPartial)) {
    //				if (comma) {
    					curkey.Chapter(VerseKey(tmpListKey).Chapter());
    					curkey.Verse(chap);  // chap because this is the first number captured
    				}
    				else {
    					if (chap >= 0) {
    						curkey.Chapter(chap);
    					}
    					else {
    						partial++;
    						curkey.Chapter(1);
    					}
    					if (verse >= 0) {
    						curkey.Verse(verse);
    					}
    					else {
    						partial++;
    						curkey.Verse(1);
    					}
    				}
    
    				if ((*buf == '-') && (expandRange)) {	// if this is a dash save lowerBound and wait for upper
    					VerseKey newElement;
    					newElement.LowerBound(curkey);
    					newElement = TOP;
    					tmpListKey << newElement;
    				}
    				else {
    					if (!dash) { 	// if last separator was not a dash just add
    						if (expandRange && partial) {
    							VerseKey newElement;
    							newElement.LowerBound(curkey);
    							if (partial > 1)
    								curkey = MAXCHAPTER;
    							if (partial > 0)
    								curkey = MAXVERSE;
    							newElement.UpperBound(curkey);
    							newElement = TOP;
    							tmpListKey << newElement;
    						}
    						else tmpListKey << (const SWKey &)(const SWKey)(const char *)curkey;
    					}
    					else	if (expandRange) {
    						VerseKey *newElement = dynamic_cast<VerseKey *>(tmpListKey.GetElement());
    						if (newElement) {
    							if (partial > 1)
    								curkey = MAXCHAPTER;
    							if (partial > 0)
    								curkey = MAXVERSE;
    							newElement->UpperBound(curkey);
    							*newElement = TOP;
    						}
    					}
    				}
    				lastPartial = partial;
    			}
    			*book = 0;
    			chap = -1;
    			verse = -1;
    			if (*buf == ',')
    				comma = 1;
    			else	comma = 0;
    			if (*buf == '-')
    				dash = 1;
    			else	dash = 0;
    			break;
    		case 10:	// ignore these
    		case 13:
    			break;
    		case '.':
    			if (buf > orig)			// ignore (break) if preceeding char is not a digit
    				if (!isdigit(*(buf-1)))
    					break;
    
    		default:
    			if (isdigit(*buf)) {
    				number[tonumber++] = *buf;
    			}
    			else {
    				switch (*buf) {
    				case ' ':    // ignore these and don't reset number
    				case 'f':
    				case 'F':
    					break;
    				default:
    					number[tonumber] = 0;
    					tonumber = 0;
    					break;
    				}
    			}
    			if (chap == -1)
    				book[tobook++] = toupper(*buf);
    		}
    		lastchar = *buf;
    		buf++;
    	}
    	number[tonumber] = 0;
    	tonumber = 0;
    	if (*number) {
    		if (chap >= 0)
    			verse = atoi(number);
    		else	chap = atoi(number);
    	}
    	*number = 0;
    	book[tobook] = 0;
    	tobook = 0;
    	if (*book) {
    		for (loop = strlen(book) - 1; loop+1; loop--) {
    			if ((isdigit(book[loop])) || (book[loop] == ' ')) {
    				book[loop] = 0;
    				continue;
    			}
    			else {
    				if ((toupper(book[loop])=='F')&&(loop)) {
    					if ((isdigit(book[loop-1])) || (book[loop-1] == ' ') || (toupper(book[loop-1]) == 'F')) {
    						book[loop] = 0;
    						continue;
    					}
    				}
    			}
    			break;
    		}
    		bookno = getBookAbbrev(book);
    	}
    	if (((bookno > -1) || (!*book)) && ((*book) || (chap >= 0) || (verse >= 0))) {
    		char partial = 0;
    		curkey.Verse(1);
    		curkey.Chapter(1);
    		curkey.Book(1);
    
    		if (bookno < 0) {
    			curkey.Testament(VerseKey(tmpListKey).Testament());
    			curkey.Book(VerseKey(tmpListKey).Book());
    		}
    		else {
    			curkey.Testament(1);
    			curkey.Book(bookno);
    		}
    
    		if (((comma)||((verse < 0)&&(bookno < 0)))&&(!lastPartial)) {
    //		if (comma) {
    			curkey.Chapter(VerseKey(tmpListKey).Chapter());
    			curkey.Verse(chap);  // chap because this is the first number captured
    		}
    		else {
    			if (chap >= 0) {
    				curkey.Chapter(chap);
    			}
    			else {
    				partial++;
    				curkey.Chapter(1);
    			}
    			if (verse >= 0) {
    				curkey.Verse(verse);
    			}
    			else {
    				partial++;
    				curkey.Verse(1);
    			}
    		}
    
    		if ((*buf == '-') && (expandRange)) {	// if this is a dash save lowerBound and wait for upper
    			VerseKey newElement;
    			newElement.LowerBound(curkey);
    			newElement = TOP;
    			tmpListKey << newElement;
    		}
    		else {
    			if (!dash) { 	// if last separator was not a dash just add
    				if (expandRange && partial) {
    					VerseKey newElement;
    					newElement.LowerBound(curkey);
    					if (partial > 1)
    						curkey = MAXCHAPTER;
    					if (partial > 0)
    						curkey = MAXVERSE;
    					newElement.UpperBound(curkey);
    					newElement = TOP;
    					tmpListKey << newElement;
    				}
    				else tmpListKey << (const SWKey &)(const SWKey)(const char *)curkey;
    			}
    			else if (expandRange) {
    				VerseKey *newElement = dynamic_cast<VerseKey *>(tmpListKey.GetElement());
    				if (newElement) {
    					if (partial > 1)
    						curkey = MAXCHAPTER;
    					if (partial > 0)
    						curkey = MAXVERSE;
    					newElement->UpperBound(curkey);
    					*newElement = TOP;
    				}
    			}
    		}
    	}
    	*book = 0;
    	tmpListKey = TOP;
    	tmpListKey.Remove();	// remove defaultKey
    	internalListKey = tmpListKey;
    	internalListKey = TOP;	// Align internalListKey to first element before passing back;
        
        */
        return internalListKey;
    }

    /**
     * VerseKey::LowerBound	- sets / gets the lower boundary for this key
     */
    public VerseKey getLowerBound()
    {
        return lowerBound;
    }

    public void setLowerBound(SWKey lb)
    {
        setLowerBound(lb.getText());
    }

    public void setLowerBound(String lb)
    {
        if (lowerBound == null)
            initBounds();

        lowerBound.setText(lb);
        lowerBound.normalize();
    }

    /**
     * VerseKey::UpperBound	- sets / gets the upper boundary for this key
     */
    public VerseKey getUpperBound()
    {
        return upperBound;
    }

    public void setUpperBound(SWKey ub)
    {
        setUpperBound(ub.getText());
    }

    public void setUpperBound(String ub)
    {
        if (upperBound == null)
            initBounds();

        // need to set upperbound parsing to resolve to max verse/chap if not specified
        upperBound.setText(ub);
        if (upperBound.compareTo(lowerBound) < 0)
            upperBound.set(lowerBound);
        upperBound.normalize();

        /*
        // until we have a proper method to resolve max verse/chap use this kludge
    	int len = strlen(ub);
    	bool alpha = false;
    	bool versespec = false;
    	bool chapspec = false;
    	for (int i = 0; i < len; i++) {
    		if (isalpha(ub[i]))
    			alpha = true;
    		if (ub[i] == ':')	// if we have a : we assume verse spec
    			versespec = true;
    		if ((isdigit(ub[i])) && (alpha))	// if digit after alpha assume chap spec
    			chapspec = true;
    	}
    	if (!chapspec)
    		*upperBound = MAXCHAPTER;
    	if (!versespec)
    		*upperBound = MAXVERSE;
            
        // -- end kludge
        */
    }

    /*
     * VerseKey::LowerBound	- sets / gets the lower boundary for this key
     *    
    VerseKey &VerseKey::LowerBound() const
    {
    	if (!lowerBound)
    		initBounds();
    
    	return (*lowerBound);
    }
    */
    
    /*
     * VerseKey::UpperBound	- sets / gets the upper boundary for this key
     *
    VerseKey &VerseKey::UpperBound() const
    {
    	if (!upperBound)
    		initBounds();
    
    	return (*upperBound);
    }
    */

    /**
     * VerseKey::ClearBounds	- clears bounds for this VerseKey
     */
    public void clearBounds()
    {
        initBounds();
    }

    protected void initBounds()
    {
        if (upperBound == null)
        {
            upperBound = new VerseKey();
            upperBound.setAutoNormalize(false);
            upperBound.setHeadings(true);
        }
        if (lowerBound == null)
        {
            lowerBound = new VerseKey();
            lowerBound.setAutoNormalize(false);
            lowerBound.setHeadings(true);
        }

        lowerBound.setTestament(0);
        lowerBound.setBook(0);
        lowerBound.setChapter(0);
        lowerBound.setVerse(0);

        upperBound.setTestament(2);
        upperBound.setBook(BMAX[1]);
        upperBound.setChapter(books[1][BMAX[1] - 1].chapmax);
        upperBound.setVerse(books[1][BMAX[1] - 1].versemax[upperBound.getChapter() - 1]);
    }

    /*
     * VerseKey::operator = - Equates this VerseKey to another VerseKey
     *
    SWKey &VerseKey::operator =(const VerseKey &ikey)
    {
    	SWKey::operator =(ikey);
    
    	parse();
    
    	return *this;
    }
    */

    /*
     * VerseKey::operator = - Equates this VerseKey to another SWKey
     *
    SWKey &VerseKey::operator =(const SWKey &ikey)
    {
    	SWKey::operator =(ikey);
    
    	parse();
    
    	return *this;
    }    
    */

    /*
     * VerseKey::operator char * - refreshes keytext before returning if cast to
     *				a (char *) is requested
     *
    VerseKey::operator const char *() const
    {
    	freshtext();
    	return keytext;
    }
    */

    /*
     * VerseKey::operator =(POSITION)	- Positions this key
     *
     * ENT:	p	- position
     *
     * RET:	*this
     *
    SWKey &VerseKey::operator =(POSITION p)
    {
    	switch (p) {
    	case POS_TOP:
    		testament = LowerBound().Testament();
    		book      = LowerBound().Book();
    		chapter   = LowerBound().Chapter();
    		verse     = LowerBound().Verse();
    		break;
    	case POS_BOTTOM:
    		testament = UpperBound().Testament();
    		book      = UpperBound().Book();
    		chapter   = UpperBound().Chapter();
    		verse     = UpperBound().Verse();
    		break;
    	case POS_MAXVERSE:
    		Normalize();
    		verse     = books[testament-1][book-1].versemax[chapter-1];
    		break;
    	case POS_MAXCHAPTER:
    		verse     = 1;
    		Normalize();
    		chapter   = books[testament-1][book-1].chapmax;
    		break;
    	}
    	Normalize(1);
    	Error();	// clear error from normalize
    	return *this;
    }
    */

    /*
     * VerseKey::operator +=	- Increments key a number of verses
     *
     * ENT:	increment	- Number of verses to jump forward
     *
     * RET: *this
     *
    SWKey &VerseKey::operator += (int increment)
    {
    	char ierror = 0;
    	Index(Index() + increment);
    	while ((!verse) && (!headings) && (!ierror)) {
    		Index(Index() + 1);
    		ierror = Error();
    	}
    
    	error = (ierror) ? ierror : error;
    	return *this;
    }
    */
    
    /*
     * VerseKey::operator -=	- Decrements key a number of verses
     *
     * ENT:	decrement	- Number of verses to jump backward
     *
     * RET: *this
     *
    SWKey &VerseKey::operator -= (int decrement)
    {
    	char ierror = 0;
    
    	Index(Index() - decrement);
    	while ((!verse) && (!headings) && (!ierror)) {
    		Index(Index() - 1);
    		ierror = Error();
    	}
    	if ((ierror) && (!headings))
    		(*this)++;
    
    	error = (ierror) ? ierror : error;
    	return *this;
    }
    */

    /**
     * VerseKey::Normalize	- checks limits and normalizes if necessary (e.g.
     *				Matthew 29:47 = Mark 2:2).  If last verse is
     *				exceeded, key is set to last Book CH:VS
     * RET: *this
     */
    public void normalize()
    {
        normalize(false);
    }

    public void normalize(boolean autocheck)
    {
        error = 0;

        if ((autocheck) && (!autonorm)) // only normalize if we were explicitely called or if autonorm is turned on
            return;

        if ((headings) && (verse == 0)) // this is cheeze and temporary until deciding what actions should be taken.
            return; // so headings should only be turned on when positioning with Index() or incrementors

        while ((testament < 3) && (testament > 0))
        {

            if (book > BMAX[testament - 1])
            {
                book -= BMAX[testament - 1];
                testament++;
                continue;
            }

            if (book < 1)
            {
                if (--testament > 0)
                {
                    book += BMAX[testament - 1];
                }
                continue;
            }

            if (chapter > books[testament - 1][book - 1].chapmax)
            {
                chapter -= books[testament - 1][book - 1].chapmax;
                book++;
                continue;
            }

            if (chapter < 1)
            {
                if (--book > 0)
                {
                    chapter += books[testament - 1][book - 1].chapmax;
                }
                else
                {
                    if (testament > 1)
                    {
                        chapter += books[0][BMAX[0] - 1].chapmax;
                    }
                }
                continue;
            }

            if (verse > books[testament - 1][book - 1].versemax[chapter - 1])
            { // -1 because e.g chapter 1 of Matthew is books[1][0].versemax[0]
                verse -= books[testament - 1][book - 1].versemax[chapter++ -1];
                continue;
            }

            if (verse < 1)
            {
                if (--chapter > 0)
                {
                    verse += books[testament - 1][book - 1].versemax[chapter - 1];
                }
                else
                {
                    if (book > 1)
                    {
                        verse += books[testament - 1][book - 2].versemax[books[testament - 1][book - 2].chapmax - 1];
                    }
                    else
                    {
                        if (testament > 1)
                        {
                            verse += books[0][BMAX[0] - 1].versemax[books[0][BMAX[0] - 1].chapmax - 1];
                        }
                    }
                }
                continue;
            }

            break; // If we've made it this far (all failure checks continue) we're ok
        }

        if (testament > 2)
        {
            testament = 2;
            book = BMAX[testament - 1];
            chapter = books[testament - 1][book - 1].chapmax;
            verse = books[testament - 1][book - 1].versemax[chapter - 1];
            error = KEYERR_OUTOFBOUNDS;
        }

        if (testament < 1)
        {
            error = ((!headings) || (testament < 0) || (book < 0)) ? KEYERR_OUTOFBOUNDS : 0;
            testament = ((headings) ? 0 : 1);
            book = ((headings) ? 0 : 1);
            chapter = ((headings) ? 0 : 1);
            verse = ((headings) ? 0 : 1);
        }
        if (getUpperBound().compareTo(this) < 0)
        {
            set(getUpperBound());
            error = KEYERR_OUTOFBOUNDS;
        }
        if (getLowerBound().compareTo(this) > 0)
        {
            set(getLowerBound());
            error = KEYERR_OUTOFBOUNDS;
        }
    }

    /**
     * VerseKey::Testament - Gets testament
     *
     * RET:	value of testament
     */
    public int getTestament()
    {
        return testament;
    }

    /**
     * VerseKey::Testament - Sets/gets testament
     *
     * ENT:	itestament - value which to set testament
     */
    public void setTestament(int itestament)
    {
        testament = itestament;
        normalize(true);
    }

    /**
     * VerseKey::Book - Gets book
     *
     * RET:	value of book
     */
    public int getBook()
    {
        return book;
    }

    /**
     * VerseKey::Book - Sets/gets book
     *
     * ENT:	ibook - value which to set book
     */
    public void setBook(int ibook)
    {
        setChapter(1);
        book = ibook;
        normalize(true);
    }

    /**
     * VerseKey::Chapter - Gets chapter
     *
     * RET:	value of chapter
     */
    public int getChapter()
    {
        return chapter;
    }

    /**
     * VerseKey::Chapter - Sets/gets chapter
     *
     * ENT:	ichapter - value which to set chapter
     *
     */
    public void setChapter(int ichapter)
    {
        setVerse(1);
        chapter = ichapter;
        normalize(true);
    }

    /**
     * VerseKey::Verse - Gets verse
     *
     * RET:	value of verse
     */
    public int getVerse()
    {
        return verse;
    }

    /**
     * VerseKey::Verse - Sets/gets verse
     *
     * ENT:	iverse - value which to set verse
     *
     */
    public void setVerse(int iverse)
    {
        verse = iverse;
        normalize(true);
    }

    /**
     * VerseKey::AutoNormalize - Sets/gets flag that tells VerseKey to auto-
     *				matically normalize itself when modified
     *
     * ENT:	iautonorm - value which to set autonorm
     *		[MAXPOS(char)] - only get
     *
     * RET:	if unchanged ->          value of autonorm
     *		if   changed -> previous value of autonorm
     */
    public boolean isAutoNormalize()
    {
        return autonorm;
    }

    public void setAutoNormalize(boolean iautonorm)
    {
        autonorm = iautonorm;
        normalize(true);
    }

    /**
     * VerseKey::Headings - Sets/gets flag that tells VerseKey to include
     *					chap/book/testmnt/module headings
     *
     * ENT:	iheadings - value which to set headings
     *		[MAXPOS(char)] - only get
     *
     * RET:	if unchanged ->          value of headings
     *		if   changed -> previous value of headings
     */
    public boolean isHeadings()
    {
        return headings;
    }

    public void setHeadings(boolean iheadings)
    {
        headings = iheadings;
        normalize(true);
    }

    /*
     * VerseKey::findindex - binary search to find the index closest, but less
     *						than the given value.
     *
     * ENT:	array	- long * to array to search
     *		size		- number of elements in the array
     *		value	- value to find
     *
     * RET:	the index into the array that is less than but closest to value
     *
    int VerseKey::findindex(long *array, int size, long value)
    {
    	int lbound, ubound, tval;
    
    	lbound = 0;
    	ubound = size - 1;
    	while ((ubound - lbound) > 1) {
    		tval = lbound + (ubound-lbound)/2;
    		if (array[tval] <= value)
    			lbound = tval;
    		else ubound = tval;
    	}
    	return (array[ubound] <= value) ? ubound : lbound;
    }
    */

    /*
     * VerseKey::Index - Gets index based upon current verse
     *
     * RET:	offset
     *
    long VerseKey::Index() const
    {
    	long  offset;
    
    	if (!testament) { // if we want module heading
    		offset = 0;
    		verse  = 0;
    	}
    	else {
    		if (!book)
    			chapter = 0;
    		if (!chapter)
    			verse   = 0;
    
    		offset = offsets[testament-1][0][book];
    		offset = offsets[testament-1][1][(int)offset + chapter];
    		if (!(offset|verse)) // if we have a testament but nothing else.
    			offset = 1;
    	}
    	return (offset + verse);
    }
    */
    
    /*
     * VerseKey::Index - Gets index based upon current verse
     *
     * RET:	offset
     *
    long VerseKey::NewIndex() const
    {
    	static long otMaxIndex = offsets[0][1][(int)offsets[0][0][BMAX[0]] + books[0][BMAX[0]].chapmax];
    	return ((testament-1) * otMaxIndex) + Index();
    }
    */
    
    /*
     * VerseKey::Index - Sets index based upon current verse
     *
     * ENT:	iindex - value to set index to
     *
     * RET:	offset
     *
    long VerseKey::Index(long iindex)
    {
    	long  offset;
    
    // This is the dirty stuff --------------------------------------------
    
    	if (!testament)
    		testament = 1;
    
    	if (iindex < 1) {				// if (-) or module heading
    		if (testament < 2) {
    			if (iindex < 0) {
    				testament = 0;  // previously we changed 0 -> 1
    				error     = KEYERR_OUTOFBOUNDS;
    			}
    			else testament = 0;		// we want module heading
    		}
    		else {
    			testament--;
    			iindex = (offsets[testament-1][1][offsize[testament-1][1]-1] + books[testament-1][BMAX[testament-1]-1].versemax[books[testament-1][BMAX[testament-1]-1].chapmax-1]) + iindex; // What a doozy! ((offset of last chapter + number of verses in the last chapter) + iindex)
    		}
    	}
    
    // --------------------------------------------------------------------
    
    
    	if (testament) {
    		if ((!error) && (iindex)) {
    			offset  = findindex(offsets[testament-1][1], offsize[testament-1][1], iindex);
    			verse   = iindex - offsets[testament-1][1][offset];
    			book    = findindex(offsets[testament-1][0], offsize[testament-1][0], offset);
    			chapter = offset - offsets[testament-1][0][VerseKey::book];
    			verse   = (chapter) ? verse : 0;  // funny check. if we are index=1 (testmt header) all gets set to 0 exept verse.  Don't know why.  Fix if you figure out.  Think its in the offsets table.
    			if (verse) {		// only check if -1 won't give negative
    				if (verse > books[testament-1][book-1].versemax[chapter-1]) {
    					if (testament > 1) {
    						verse = books[testament-1][book-1].versemax[chapter-1];
    						error = KEYERR_OUTOFBOUNDS;
    					}
    					else {
    						testament++;
    						Index(verse - books[testament-2][book-1].versemax[chapter-1]);
    					}
    				}
    			}
    		}
    	}
    	if (_compare(UpperBound()) > 0) {
    		*this = UpperBound();
    		error = KEYERR_OUTOFBOUNDS;
    	}
    	if (_compare(LowerBound()) < 0) {
    		*this = LowerBound();
    		error = KEYERR_OUTOFBOUNDS;
    	}
    	return Index();
    }
    */
    
    /*
     * VerseKey::compare	- Compares another SWKey object
     *
     * ENT:	ikey - key to compare with this one
     *
     * RET:	>0 if this versekey is greater than compare versekey
     *	<0 <
     *	 0 =
     *
    int VerseKey::compare(const SWKey &ikey)
    {
    	VerseKey ivkey = (const char *)ikey;
    	return _compare(ivkey);
    }

    /*
     * VerseKey::_compare	- Compares another VerseKey object
     *
     * ENT:	ikey - key to compare with this one
     *
     * RET:	>0 if this versekey is greater than compare versekey
     *	<0 <
     *	 0 =
     *
    int VerseKey::_compare(const VerseKey &ivkey)
    {
    	long keyval1 = 0;
    	long keyval2 = 0;
    
    	keyval1 += Testament() * 1000000000;
    	keyval2 += ivkey.Testament() * 1000000000;
    	keyval1 += Book() * 1000000;
    	keyval2 += ivkey.Book() * 1000000;
    	keyval1 += Chapter() * 1000;
    	keyval2 += ivkey.Chapter() * 1000;
    	keyval1 += Verse();
    	keyval2 += ivkey.Verse();
    	keyval1 -= keyval2;
    	keyval1 = (keyval1) ? ((keyval1 > 0) ? 1 : -1) :0; // -1 | 0 | 1
    	return keyval1;
    }
    */
}
