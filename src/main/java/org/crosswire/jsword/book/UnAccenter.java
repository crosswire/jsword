package org.crosswire.jsword.book;

/**
 * An object implementing this interface is able to unaccent the texts. If set on the BookData, then the texts are cleansed before parsing
 *
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface UnAccenter {
    /**
    * Unaccents the text give
     * @param accentedForm the form, with potential accents
     * @return the same string as input, but without any accents, cantillation, etc.
     */
    String unaccent(String accentedForm);
}
