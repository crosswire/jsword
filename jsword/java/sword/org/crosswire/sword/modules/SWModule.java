/******************************************************************************
 *  swmodule.cpp -code for base class 'module'.  Module is the basis for all
 *		  types of modules (e.g. texts, commentaries, maps, lexicons,
 *		  etc.)
 */

package org.crosswire.sword.modules;

import org.crosswire.sword.frontend.*;
import org.crosswire.sword.keys.*;
import java.util.*;

public class SWModule {

	static SWDisplay rawdisp = new SWDisplay();
	SWKey key = null;
	int error = 0;
	String entrybuf = null;
	String modname = null;
	String moddesc = null;
	String modtype = null;
	SWDisplay disp = rawdisp;
     boolean terminateSearch = false;

/******************************************************************************
 * SWModule Constructor - Initializes data for instance of SWModule
 *
 * ENT:	imodname - Internal name for module
 *	imoddesc - Name to display to user for module
 *	idisp	 - Display object to use for displaying
 *	imodtype - Type of Module (All modules will be displayed with
 *			others of same type under their modtype heading
 */

	SWModule(String imodname, String imoddesc, SWDisplay idisp, String imodtype) {
		key      = CreateKey();
		entrybuf = null;
		modname  = imodname;
		error    = 0;
		moddesc  = imoddesc;
		modtype  = imodtype;
		disp     = (idisp != null) ? idisp : rawdisp;
	}


/******************************************************************************
 * SWModule::CreateKey - Allocates a key of specific type for module
 *
 * RET:	pointer to allocated key
 */

	SWKey CreateKey() {
		return new SWKey();
	}


/******************************************************************************
 * SWModule::Error - Gets and clears error status
 *
 * RET:	error status
 */

	int getError() {
		int retval = error;

		error = 0;
		return retval;
	}


/******************************************************************************
 * SWModule::Name - Sets/gets module name
 *
 * ENT:	imodname - value which to set modname
 *		[0] - only get
 *
 * RET:	pointer to modname
 */

	String getName() {
		return modname;
	}

	void setName(String imodname) {
		modname = imodname;
	}


/******************************************************************************
 * SWModule::Description - Sets/gets module description
 *
 * ENT:	imoddesc - value which to set moddesc
 *		[0] - only get
 *
 * RET:	pointer to moddesc
 */

	String getDescription() {
		return moddesc;
	}

	void setDescription(String imoddesc) {
		moddesc = imoddesc;
	}

/******************************************************************************
 * SWModule::Type - Sets/gets module type
 *
 * ENT:	imodtype - value which to set modtype
 *		[0] - only get
 *
 * RET:	pointer to modtype
 */

	String getType() {
		return modtype;
	}

	void setType(String imodtype) {
		modtype = imodtype;
	}


/******************************************************************************
 * SWModule::Disp - Sets/gets display driver
 *
 * ENT:	idisp - value which to set disp
 *		[0] - only get
 *
 * RET:	pointer to disp
 */

	SWDisplay getDisplay() {
		return disp;
	}

	void setDisplay(SWDisplay idisp) {
		disp = idisp;
	}


/******************************************************************************
 * SWModule::Display - Calls this modules display object and passes itself
 *
 * RET:	error status
 */

	char display() {
		disp.display(this);
		return 0;
	}


/******************************************************************************
 * SWModule::SetKey - Sets a key to this module for position to a particular
 *			record or set of records
 *
 * ENT:	ikey - key with which to set this module
 *
 * RET:	error status
 */

	char setKey(SWKey ikey) {

		if (!ikey.isPersist()) {		// if we are to keep our own copy
			key = CreateKey();
			key.set(ikey);
		}
		else	key = ikey;		// if we are to just point to an external key

		return 0;
	}

	SWKey getKey() {
		return key;
	}


/******************************************************************************
 * SWModule::KeyText - Sets/gets module KeyText
 *
 * ENT:	ikeytext - value which to set keytext
 *		[0] - only get
 *
 * RET:	pointer to keytext
 */

	String getKeyText() {
		return key.getText();
	}

	void setKeyText(String ikeytext) {
		setKey(new SWKey(ikeytext));
	}


/******************************************************************************
 * SWModule::operator =(POSITION)	- Positions this modules to an entry
 *
 * ENT:	p	- position (e.g. TOP, BOTTOM)
 *
 * RET: *this
 */

	void position(int p) {
		key.position(p);
		error = key.getError();
	}


/******************************************************************************
 * SWModule::operator +=	- Increments module key a number of entries
 *
 * ENT:	increment	- Number of entries to jump forward
 *
 * RET: *this
 */

	void next(int increment) {
		key.next(increment);
		error = key.getError();
	}

	void next() {
		next(1);
	}


/******************************************************************************
 * SWModule::operator -=	- Decrements module key a number of entries
 *
 * ENT:	decrement	- Number of entries to jump backward
 *
 * RET: *this
 */

	void previous(int increment) {
		key.previous(increment);
		error = key.getError();
	}


/******************************************************************************
 * SWModule::Search 	- Searches a module for a string
 *
 * ENT:	istr		- string for which to search
 * 	searchType	- type of search to perform
 *				>=0 - regex
 *				-1  - phrase
 *				-2  - multiword
 * 	flags		- options flags for search
 *
 * RET: listkey set to verses that contain istr
 */

	ListKey search(String istr, int searchType, int flags, SWKey scope) {
		SWKey savekey = null;
		SWKey searchkey = null;
	//	regex_t preg;
		SWKey textkey = new SWKey();
		List words = null;
		String wordBuf = null;
		int wordCount = 0;
		String sres;
		ListKey listKey = new ListKey();

		terminateSearch = false;

		if (!key.isPersist()) {
			savekey = CreateKey();
			savekey.set(key);
		}
		else	savekey = key;

		searchkey = (SWKey)((scope != null)?scope.clone():(key.isPersist())?key.clone():null);
		if (searchkey != null) {
			searchkey.setPersist(true);
			setKey(searchkey);
		}

		position(SWKey.TOP);
	/*
		if (searchType >= 0) {
			flags |=searchType|REG_NOSUB|REG_EXTENDED;
			regcomp(&preg, istr, flags);
		}
	*/

		if (searchType == -2) {
			wordBuf = istr;
			words = new ArrayList();
	/*
			words.put(strtok(wordBuf, " ");
			while (words[wordCount]) {
				wordCount++;
				if (wordCount == allocWords) {
					allocWords+=10;
					words = (char **)realloc(words, sizeof(char *)*allocWords);
				}
				words[wordCount] = strtok(NULL, " ");
			}
	*/
		}




		while ((getError()!=0) && !terminateSearch) {
			if (searchType >= 0) {
	/*
				if (!regexec(&preg,  StripText(), 0, 0, 0)) {
					textkey = KeyText();
					listkey << textkey;
				}
	*/
			}
			else {
				if (searchType == -1) {
	//				sres = ((flags & REG_ICASE) == REG_ICASE) ? stristr(StripText(), istr) : strstr(StripText(), istr);
					int offset = ((flags & 1) == 1) ? stripText().indexOf(istr) : stripText().toUpperCase().indexOf(istr.toUpperCase());
					if (offset > -1) {
							textkey = new SWKey(getKeyText());
							listKey.add(textkey);
					}
				}
				if (searchType == -2) {
					int i;
					for (i = 0; i < wordCount; i++) {
                              int offset = ((flags & 1) == 1) ? stripText().indexOf((String)words.get(i)) : stripText().toUpperCase().indexOf(((String)words.get(i)).toUpperCase());
						if (offset < 0)
							break;
					}
					if (i == wordCount) {
						textkey = new SWKey(getKeyText());
						listKey.add(textkey);
					}
				}
			}
			next();
		}
	//	if (searchType >= 0)
	//		regfree(&preg);

	//	if (searchType == -2) {
	//		free(words);
	//		free(wordBuf);
	//	}

		setKey(savekey);

		listKey.position(SWKey.TOP);
		return listKey;
	}


/******************************************************************************
 * SWModule::StripText() 	- calls all stripfilters on current text
 *
 * ENT:	buf	- buf to massage instead of this modules current text
 * 	len	- max len of buf
 *
 * RET: this module's text at specified key location massaged by Strip filters
 */

	String stripText(String buf, int len) {
	//	FilterList::iterator it;

		if (buf == null)
			buf = getText();

	/*
		for (it = optionfilters.begin(); it != optionfilters.end(); it++) {
			(*it)->ProcessText(buf, len);
		}
		for (it = stripfilters.begin(); it != stripfilters.end(); it++) {
			(*it)->ProcessText(buf, len);
		}
	*/
		return buf;
	}

	String stripText() {
		return stripText(null, -1);
	}

/******************************************************************************
 * SWModule::RenderText 	- calls all renderfilters on current text
 *
 * ENT:	buf	- buffer to Render instead of current module position
 *
 * RET: listkey set to verses that contain istr
 */

	String renderText(String buf, int len) {
	//	FilterList::iterator it;

		if (buf == null)
			buf = getText();

	/*
		for (it = optionfilters.begin(); it != optionfilters.end(); it++) {
			(*it)->ProcessText(buf, len);
		}
		for (it = renderfilters.begin(); it != renderfilters.end(); it++) {
			(*it)->ProcessText(buf, len);
		}
	*/
		return buf;
	}

	String renderText() {
		return renderText(null, -1);
	}

/******************************************************************************
 * SWModule::RenderText 	- calls all renderfilters on current text
 *
 * ENT:	tmpKey	- key to use to grab text
 *
 * RET: this module's text at specified key location massaged by RenderFilers
 */

	String renderText(SWKey tmpKey) {
		SWKey savekey;
		String retVal;

		if (!key.isPersist()) {
			savekey = CreateKey();
			savekey.set(key);
		}
		else	savekey = key;

		setKey(tmpKey);

		retVal = renderText();

		setKey(savekey);

		return retVal;
	}


/******************************************************************************
 * SWModule::StripText 	- calls all StripTextFilters on current text
 *
 * ENT:	tmpKey	- key to use to grab text
 *
 * RET: this module's text at specified key location massaged by Strip filters
 */

	String stripText(SWKey tmpKey) {
		SWKey savekey;
		String retVal;

		if (key.isPersist()) {
			savekey = CreateKey();
			savekey.set(key);
		}
		else	savekey = key;

		setKey(tmpKey);

		retVal = stripText();

		setKey(savekey);

		return retVal;
	}

	public String getText() {
		return "";
	}
}
