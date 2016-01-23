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
package org.crosswire.common.diff;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * Various Strategies to cleanup a diff list.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class DiffCleanup {
    /**
     * Utility class constructor.
     */
    private DiffCleanup() {
    }

    /**
     * Reduce the number of edits by eliminating semantically trivial
     * equalities.
     * 
     * @param diffs
     *            List of Difference objects
     */
    public static void cleanupSemantic(final List<Difference> diffs) {
        boolean changes = false;
        Stack<Difference> equalities = new Stack<Difference>(); // Stack of indices where equalities are
        // found.
        String lastEquality = null; // Always equal to
        // equalities.lastElement().getText()
        int lengthChangesPre = 0; // Number of characters that changed prior to
        // the equality.
        int lengthChangesPost = 0; // Number of characters that changed after
        // the equality.
        ListIterator<Difference> pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        while (curDiff != null) {
            EditType editType = curDiff.getEditType();
            if (EditType.EQUAL.equals(editType)) {
                // equality found
                equalities.push(curDiff);
                lengthChangesPre = lengthChangesPost;
                lengthChangesPost = 0;
                lastEquality = curDiff.getText();
            } else {
                // an insertion or deletion
                lengthChangesPost += curDiff.getText().length();
                int lastLen = lastEquality != null ? lastEquality.length() : 0;
                if (lastEquality != null && lastLen <= lengthChangesPre && lastLen <= lengthChangesPost) {
                    // position pointer to the element after the one at the end
                    // of the stack
                    while (curDiff != equalities.lastElement()) {
                        curDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Difference(EditType.DELETE, lastEquality));
                    // Insert a corresponding an insert.
                    pointer.add(new Difference(EditType.INSERT, lastEquality));
                    equalities.pop(); // Throw away the equality we just
                    // deleted;
                    if (!equalities.empty()) {
                        // Throw away the previous equality (it needs to be
                        // reevaluated).
                        equalities.pop();
                    }
                    if (equalities.empty()) {
                        // There are no previous equalities, walk back to the
                        // start.
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        // There is a safe equality we can fall back to.
                        curDiff = equalities.lastElement();
                        while (curDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }
                    }

                    lengthChangesPre = 0; // Reset the counters.
                    lengthChangesPost = 0;
                    lastEquality = null;
                    changes = true;
                }
            }
            curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        }

        if (changes) {
            cleanupMerge(diffs);
        }
    }

    /**
     * Reduce the number of edits by eliminating operationally trivial
     * equalities.
     * 
     * @param diffs
     *            List of Difference objects
     */
    public static void cleanupEfficiency(final List<Difference> diffs) {
        if (diffs.isEmpty()) {
            return;
        }

        boolean changes = false;
        Stack<Difference> equalities = new Stack<Difference>(); // Stack of indices where equalities are
        // found.
        String lastEquality = null; // Always equal to
        // equalities.lastElement().getText();
        int preInsert = 0; // Is there an insertion operation before the last
        // equality.
        int preDelete = 0; // Is there an deletion operation before the last
        // equality.
        int postInsert = 0; // Is there an insertion operation after the last
        // equality.
        int postDelete = 0; // Is there an deletion operation after the last
        // equality.

        ListIterator<Difference> pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        Difference safeDiff = curDiff; // The last Difference that is known to
        // be unsplitable.

        while (curDiff != null) {
            EditType editType = curDiff.getEditType();
            if (EditType.EQUAL.equals(editType)) {
                // equality found
                if (curDiff.getText().length() < editCost && (postInsert + postDelete) > 0) {
                    // Candidate found.
                    equalities.push(curDiff);
                    preInsert = postInsert;
                    preDelete = postDelete;
                    lastEquality = curDiff.getText();
                } else {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastEquality = null;
                    safeDiff = curDiff;
                }
                postInsert = 0;
                postDelete = 0;
            } else {
                // an insertion or deletion
                if (EditType.DELETE.equals(editType)) {
                    postDelete = 1;
                } else {
                    postInsert = 1;
                }

                // Five types to be split:
                // <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
                // <ins>A</ins>X<ins>C</ins><del>D</del>
                // <ins>A</ins><del>B</del>X<ins>C</ins>
                // <ins>A</del>X<ins>C</ins><del>D</del>
                // <ins>A</ins><del>B</del>X<del>C</del>
                if (lastEquality != null
                        && (((preInsert + preDelete + postInsert + postDelete) > 0)
                                || ((lastEquality.length() < editCost / 2)
                                        && (preInsert + preDelete + postInsert + postDelete) == 3)))
                {
                    // position pointer to the element after the one at the end
                    // of the stack
                    while (curDiff != equalities.lastElement()) {
                        curDiff = pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Difference(EditType.DELETE, lastEquality));
                    // Insert a corresponding an insert.
                    curDiff = new Difference(EditType.INSERT, lastEquality);
                    pointer.add(curDiff);

                    equalities.pop(); // Throw away the equality we just
                    // deleted;
                    lastEquality = null;
                    if (preInsert == 1 && preDelete == 1) {
                        // No changes made which could affect previous entry,
                        // keep going.
                        postInsert = 1;
                        postDelete = 1;
                        equalities.clear();
                        safeDiff = curDiff;
                    } else {
                        if (!equalities.empty()) {
                            // Throw away the previous equality;
                            equalities.pop();
                        }
                        if (equalities.empty()) {
                            // There are no previous questionable equalities,
                            // walk back to the last known safe diff.
                            curDiff = safeDiff;
                        } else {
                            // There is an equality we can fall back to.
                            curDiff = equalities.lastElement();
                        }
                        while (curDiff != pointer.previous()) {
                            // Intentionally empty loop.
                        }

                        postInsert = 0;
                        postDelete = 0;
                    }
                    changes = true;
                }
            }
            curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        }

        if (changes) {
            cleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections. Merge equalities. Any edit section
     * can move as long as it doesn't cross an equality.
     * 
     * @param diffs
     *            List of Difference objects
     */
    public static void cleanupMerge(final List<Difference> diffs) {
        // Add a dummy entry at the end.
        diffs.add(new Difference(EditType.EQUAL, ""));

        int countDelete = 0;
        int countInsert = 0;
        StringBuilder textDelete = new StringBuilder();
        StringBuilder textInsert = new StringBuilder();

        int commonLength = 0;

        ListIterator<Difference> pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        Difference prevEqual = null;
        while (curDiff != null) {
            EditType editType = curDiff.getEditType();
            if (EditType.INSERT.equals(editType)) {
                countInsert++;
                textInsert.append(curDiff.getText());
                prevEqual = null;
            } else if (EditType.DELETE.equals(editType)) {
                countDelete++;
                textDelete.append(curDiff.getText());
                prevEqual = null;
            } else if (EditType.EQUAL.equals(editType)) {
                // Upon reaching an equality, check for prior redundancies.
                if (countDelete != 0 || countInsert != 0) {
                    // Delete the offending records.
                    pointer.previous(); // Reverse direction.
                    while (countDelete-- > 0) {
                        pointer.previous();
                        pointer.remove();
                    }
                    while (countInsert-- > 0) {
                        pointer.previous();
                        pointer.remove();
                    }

                    if (countDelete != 0 && countInsert != 0) {
                        // Factor out any common prefixes.
                        commonLength = Commonality.prefix(textInsert.toString(), textDelete.toString());
                        if (commonLength > 0) {
                            if (pointer.hasPrevious()) {
                                curDiff = pointer.previous();
                                assert EditType.EQUAL.equals(curDiff.getEditType()) : "Previous diff should have been an equality.";
                                curDiff.appendText(textInsert.substring(0, commonLength));
                                pointer.next();
                            } else {
                                pointer.add(new Difference(EditType.EQUAL, textInsert.substring(0, commonLength)));
                            }
                            textInsert.replace(0, textInsert.length(), textInsert.substring(commonLength));
                            textDelete.replace(0, textDelete.length(), textDelete.substring(commonLength));
                        }

                        // Factor out any common suffixes.
                        commonLength = Commonality.suffix(textInsert.toString(), textDelete.toString());
                        if (commonLength > 0) {
                            curDiff = pointer.next();
                            curDiff.prependText(textInsert.substring(textInsert.length() - commonLength));
                            textInsert.replace(0, textInsert.length(), textInsert.substring(0, textInsert.length() - commonLength));
                            textDelete.replace(0, textDelete.length(), textDelete.substring(0, textDelete.length() - commonLength));
                            pointer.previous();
                        }
                    }

                    // Insert the merged records.
                    if (textDelete.length() != 0) {
                        pointer.add(new Difference(EditType.DELETE, textDelete.toString()));
                    }

                    if (textInsert.length() != 0) {
                        pointer.add(new Difference(EditType.INSERT, textInsert.toString()));
                    }

                    // Step forward to the equality.
                    curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
                } else if (prevEqual != null) {
                    // Merge this equality with the previous one.
                    prevEqual.appendText(curDiff.getText());
                    pointer.remove();
                    curDiff = pointer.previous();
                    pointer.next(); // Forward direction
                }

                countInsert = 0;
                countDelete = 0;
                textDelete.delete(0, textDelete.length());
                textInsert.delete(0, textInsert.length());
                prevEqual = curDiff;
            }
            curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        }

        Difference lastDiff = diffs.get(diffs.size() - 1);
        if (lastDiff.getText().length() == 0) {
            diffs.remove(diffs.size() - 1); // Remove the dummy entry at the
            // end.
        }
    }

    /**
     * Set the edit cost for efficiency
     * 
     * @param newEditCost the edit cost
     */
    public static void setEditCost(int newEditCost) {
        editCost = newEditCost;
    }

    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    private static final int EDIT_COST = 4;
    private static int editCost = EDIT_COST;
}
