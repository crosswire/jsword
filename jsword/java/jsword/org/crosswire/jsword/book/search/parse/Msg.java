package org.crosswire.jsword.book.search.parse;

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
class Msg extends MsgBase
{
    static final Msg ADD_BLANK = new Msg("ADD_BLANK"); //$NON-NLS-1$

    static final Msg MISSED = new Msg("MISSED"); //$NON-NLS-1$
    static final Msg RIGHT_PARAM = new Msg("RIGHT_PARAM"); //$NON-NLS-1$
    static final Msg RIGHT_BRACKETS = new Msg("RIGHT_BRACKETS"); //$NON-NLS-1$
    static final Msg LEFT_PARAM = new Msg("LEFT_PARAM"); //$NON-NLS-1$
    static final Msg LEFT_BRACKETS = new Msg("LEFT_BRACKETS"); //$NON-NLS-1$
    static final Msg STARTS_WORD = new Msg("STARTS_WORD"); //$NON-NLS-1$
    static final Msg STARTS_BLANK = new Msg("STARTS_BLANK"); //$NON-NLS-1$
    static final Msg STARTS_OTHER = new Msg("STARTS_OTHER"); //$NON-NLS-1$
    static final Msg RETAIN_BLANK = new Msg("RETAIN_BLANK"); //$NON-NLS-1$
    static final Msg REMOVE_BLANK = new Msg("REMOVE_BLANK"); //$NON-NLS-1$
    static final Msg GRAMMAR_WORD = new Msg("GRAMMAR_WORD"); //$NON-NLS-1$
    static final Msg GRAMMAR_BLANK = new Msg("GRAMMAR_BLANK"); //$NON-NLS-1$
    static final Msg GRAMMER_OTHER = new Msg("GRAMMER_OTHER"); //$NON-NLS-1$
    static final Msg DEFAULT_OTHER = new Msg("DEFAULT_OTHER"); //$NON-NLS-1$
    static final Msg BLUR_BLANK = new Msg("BLUR_BLANK"); //$NON-NLS-1$
    static final Msg BLUR_FORMAT = new Msg("BLUR_FORMAT"); //$NON-NLS-1$
    static final Msg ENGINE_SYNTAX = new Msg("ENGINE_SYNTAX"); //$NON-NLS-1$
    static final Msg ILLEGAL_PASSAGE = new Msg("ILLEGAL_PASSAGE"); //$NON-NLS-1$
    static final Msg UNMATCHED_ESCAPE = new Msg("UNMATCHED_ESCAPE"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}