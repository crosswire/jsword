/******************************************************************************
 *  StrKey.cpp - code for class 'StrKey'- a standard string key class (used
 *				for modules that index on single strings (eg. cities,
 *				names, words, etc.)
 */

package org.crosswire.sword.keys;


public class StrKey extends SWKey {

/******************************************************************************
 * StrKey Constructor - initializes instance of StrKey
 *
 * ENT:	ikey - text key (word, city, name, etc.)
 */

	StrKey(String ikey) {
		super(ikey);
	}
}
