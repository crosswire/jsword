/******************************************************************************
 *  listkey.cpp - code for base class 'ListKey'.  ListKey is the basis for all
 *				types of keys that have lists of specified indexes
 *				(e.g. a list of verses, place, etc.)
 */

package org.crosswire.sword.keys;

import java.util.List;
import java.util.ArrayList;

public class ListKey extends SWKey {

	protected int arraypos;
	protected List array;


/******************************************************************************
 * ListKey Constructor - initializes instance of ListKey
 *
 * ENT:	ikey - text key
 */

	public ListKey() {
		this(""); //$NON-NLS-1$
	}

	public ListKey(String ikey) {
		super(ikey);
		ClearList();
	}


	public boolean traversable() { return true; }


	public ListKey(ListKey k) {
		super(k.keytext);
		arraypos = k.arraypos;
		array = new ArrayList();
		for (int i = 0; i < array.size(); i++)
			array.add(((SWKey)k.array.get(i)).clone());
	}


	public Object clone() {
		return new ListKey(this);
	}


	public void ClearList() {
		array.clear();
		arraypos  = 0;
	}


/******************************************************************************
 * ListKey::operator = Equates this ListKey to another ListKey object
 *
 * ENT:	ikey - other ListKey object
 */

	public void set(ListKey ikey) {
		ClearList();

		arraypos = ikey.arraypos;
		for (int i = 0; i < array.size(); i++)
			array.add(((SWKey)ikey.array.get(i)).clone());

		setToElement(0);
	}


/******************************************************************************
 * ListKey::operator << - Adds an element to the list
 */

	public void add(SWKey ikey) {
		array.add(ikey.clone());
		setToElement(array.size()-1);
	}


/******************************************************************************
 * ListKey::operator =(POSITION)	- Positions this key
 *
 * ENT:	p	- position
 *
 * RET:	*this
 */

	public void position(int p) {
		switch (p) {
		case TOP:
			setToElement(0);
			break;
		case BOTTOM:
			setToElement(array.size()-1);
			break;
		}
	}


/******************************************************************************
 * ListKey::operator += - Increments a number of elements
 */

	public void next(int increment) {
		if (increment < 0) {
			previous(increment*-1);
		}
		else {
			getError();		// clear error
			for (; (increment != 0  && (getError() == 0)); increment--) {
				if (arraypos < array.size()) {
					((SWKey)array.get(arraypos)).next();
					if (((SWKey)array.get(arraypos)).getError() != 0) {
						setToElement(arraypos+1);
					}
					else setText(((SWKey)array.get(arraypos)).getText());
				}
				else error = KEYERR_OUTOFBOUNDS;
			}
		}
	}


/******************************************************************************
 * ListKey::operator -= - Decrements a number of elements
 */

	public void previous(int decrement) {
		if (decrement < 0) {
			next(decrement*-1);
		}
		else {
			getError();		// clear error
			for (; ((decrement != 0) && (getError() == 0)); decrement--) {
				if (arraypos > -1) {
					((SWKey)array.get(arraypos)).previous();
					if (((SWKey)array.get(arraypos)).getError() != 0) {
						setToElement(arraypos-1, BOTTOM);
					}
					else setText(((SWKey)array.get(arraypos)).getText());
				}
				else error = KEYERR_OUTOFBOUNDS;
			}
		}
	}


/******************************************************************************
 * ListKey::Count	- Returns number of elements in list
 */

	public int count() {
		return array.size();
	}


/******************************************************************************
 * ListKey::setToElement	- Sets key to element number
 *
 * ENT:	ielement	- element number to set to
 *
 * RET:	error status
 */

	public int setToElement(int ielement, int pos) {
		arraypos = ielement;
		if (arraypos >= array.size()) {
			arraypos = (array.size()>0) ? array.size() - 1:0;
			error = KEYERR_OUTOFBOUNDS;
		}
		else {
			if (arraypos < 0) {
				arraypos = 0;
				error = KEYERR_OUTOFBOUNDS;
			}
			else {
				error = 0;
			}
		}

		if (array.size() > 1) {
			((SWKey)array.get(arraypos)).position(pos);
			setText(((SWKey)array.get(arraypos)).getText());
		}
		else setText(""); //$NON-NLS-1$

		return error;
	}

	public int setToElement(int ielement) {
		return setToElement(ielement, TOP);
	}

/******************************************************************************
 * ListKey::Remove	- Removes current element from list
 */

	void remove() {
		if ((arraypos > -1) && (arraypos < array.size())) {
			if (arraypos < array.size() - 1)
				array.remove(arraypos);

			setToElement((arraypos > 0) ? arraypos-1 : 0);
		}
	}
}
