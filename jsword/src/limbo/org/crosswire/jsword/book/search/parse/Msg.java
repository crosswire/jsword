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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.search.parse;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase
{
    static final Msg ADD_BLANK = new Msg("AddCommandWord.AddBlank"); //$NON-NLS-1$
    static final Msg RIGHT_PARAM = new Msg("PassageRightParamWord.RightParam"); //$NON-NLS-1$
    static final Msg RIGHT_BRACKETS = new Msg("PassageRightParamWord.RightBrackets"); //$NON-NLS-1$
    static final Msg LEFT_PARAM = new Msg("PassageLeftParamWord.LeftParam"); //$NON-NLS-1$
    static final Msg LEFT_BRACKETS = new Msg("PassageLeftParamWord.LeftBrackets"); //$NON-NLS-1$
    static final Msg STARTS_WORD = new Msg("StartsParamWord.StartsWord"); //$NON-NLS-1$
    static final Msg STARTS_BLANK = new Msg("StartsParamWord.StartsBlank"); //$NON-NLS-1$
    static final Msg RETAIN_BLANK = new Msg("RetainCommandWord.RetainBlank"); //$NON-NLS-1$
    static final Msg REMOVE_BLANK = new Msg("RemoveCommandWord.RemoveBlank"); //$NON-NLS-1$
    static final Msg GRAMMAR_WORD = new Msg("GrammarParamWord.GrammarWord"); //$NON-NLS-1$
    static final Msg GRAMMAR_BLANK = new Msg("GrammarParamWord.GrammarBlank"); //$NON-NLS-1$
    static final Msg BLUR_BLANK = new Msg("BlurCommandWord.BlurBlank"); //$NON-NLS-1$
    static final Msg BLUR_FORMAT = new Msg("BlurCommandWord.BlurFormat"); //$NON-NLS-1$
    static final Msg ENGINE_SYNTAX = new Msg("IndexSearcher.EngineSyntax"); //$NON-NLS-1$
    static final Msg ILLEGAL_PASSAGE = new Msg("PassageLeftParamWord.IllegalPassage"); //$NON-NLS-1$
    static final Msg UNMATCHED_ESCAPE = new Msg("CustomTokenizer.UnmatchedEscape"); //$NON-NLS-1$
    static final Msg SINGLE_PARAM = new Msg("PhraseParamWord.SingleParam"); //$NON-NLS-1$
    static final Msg NO_THESAURUS = new Msg("PhraseParamWord.NoThesaurus"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
