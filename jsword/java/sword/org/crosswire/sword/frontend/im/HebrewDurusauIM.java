package org.crosswire.sword.frontend.im;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001 CrossWire Bible Society under the terms of the GNU GPL
 * Company:
 * @author Troy A. Griffitts
 * @version 1.0
 */

import java.util.Hashtable;

public class HebrewDurusauIM extends SWInputMethod {

    private Hashtable charMap = new Hashtable();

    public HebrewDurusauIM(String name) {
        super(name);
        init();
    }

    public String translate(char in) {
        String retVal = (String)charMap.get(new Integer(in));
        if (retVal == null)
            retVal = new String() + in;

        return retVal;
    }

    private void init() {
//      '1'        0591   HEBREW ACCENT ETNAHTA
        charMap.put(new Integer('1'), new String(new char[] {0x0591}));

//      '2'        0592   HEBREW ACCENT SEGOL
        charMap.put(new Integer('2'), new String(new char[] {0x0592}));

//      '3'        0593   HEBREW ACCENT SHALSHELET
        charMap.put(new Integer('3'), new String(new char[] {0x0593}));

//      '4'        0594   HEBREW ACCENT ZAQEF QATAN
        charMap.put(new Integer('4'), new String(new char[] {0x0594}));

//      '5'        0595   HEBREW ACCENT ZAQEF GADOL
        charMap.put(new Integer('5'), new String(new char[] {0x0595}));

//      '6'        0596   HEBREW ACCENT TIPEHA
        charMap.put(new Integer('6'), new String(new char[] {0x0596}));

//      '7'        0597   HEBREW ACCENT REVIA
        charMap.put(new Integer('7'), new String(new char[] {0x0597}));

//      '8'        0598   HEBREW ACCENT ZARQA
        charMap.put(new Integer('8'), new String(new char[] {0x0598}));

//      '9'        0599   HEBREW ACCENT PASHTA
        charMap.put(new Integer('9'), new String(new char[] {0x0599}));

//      '10'       059A   HEBREW ACCENT YETIV   //assuming 0
        charMap.put(new Integer('0'), new String(new char[] {0x059A}));

//      '-'        059B   HEBREW ACCENT TEVIR
        charMap.put(new Integer('-'), new String(new char[] {0x059B}));

//      '='        059C   HEBREW ACCENT GERESH
        charMap.put(new Integer('='), new String(new char[] {0x059C}));

//      (shifted keyboard)

//      '!'      059D   HEBREW ACCENT GERESH MUQDAM
        charMap.put(new Integer('!'), new String(new char[] {0x059D}));

//      '@'      059E   HEBREW ACCENT GERSHAYIM
        charMap.put(new Integer('@'), new String(new char[] {0x059E}));

//      '#'      059F   HEBREW ACCENT QARNEY PARA
        charMap.put(new Integer('#'), new String(new char[] {0x059F}));

//      '$'      05A0   HEBREW ACCENT TELISHA GEDOLA
        charMap.put(new Integer('$'), new String(new char[] {0x05A0}));

//      '%'      05A1   HEBREW ACCENT PAZER
        charMap.put(new Integer('%'), new String(new char[] {0x05A1}));

//      '^'      05A3   HEBREW ACCENT MUNAH
        charMap.put(new Integer('^'), new String(new char[] {0x05A3}));

//      '&'      05A4   HEBREW ACCENT MAHAPAKH
        charMap.put(new Integer('&'), new String(new char[] {0x05A4}));

//      '*'      05A5   HEBREW ACCENT MERKHA
        charMap.put(new Integer('*'), new String(new char[] {0x05A5}));

//      '('      05A6   HEBREW ACCENT MERKHA KEFULA
        charMap.put(new Integer('('), new String(new char[] {0x05A6}));

//      ')'      05A7   HEBREW ACCENT DARGA
        charMap.put(new Integer(')'), new String(new char[] {0x05A7}));

//      '_'      05A8   HEBREW ACCENT QADMA
        charMap.put(new Integer('_'), new String(new char[] {0x05A8}));

//      '+'      05A9   HEBREW ACCENT TELISHA QETANA
        charMap.put(new Integer('+'), new String(new char[] {0x05A9}));

//      'Z'      05AA   HEBREW ACCENT YERAH BEN YOMO
        charMap.put(new Integer('Z'), new String(new char[] {0x05AA}));

//      'X'      05AB   HEBREW ACCENT OLE
        charMap.put(new Integer('X'), new String(new char[] {0x05AB}));

//      'C'      05AC   HEBREW ACCENT ILUY
        charMap.put(new Integer('C'), new String(new char[] {0x05AC}));

//      'V'      05AD   HEBREW ACCENT DEHI
        charMap.put(new Integer('V'), new String(new char[] {0x05AD}));

//      'B'      05AE   HEBREW ACCENT ZINOR
        charMap.put(new Integer('B'), new String(new char[] {0x05AE}));

//      "N'      05AF   HEBREW MARK MASORA CIRCLE
        charMap.put(new Integer('N'), new String(new char[] {0x05AF}));

//      'Q'      05B0   HEBREW POINT SHEVA
        charMap.put(new Integer('Q'), new String(new char[] {0x05B0}));

//      'W'      05B1   HEBREW POINT HATAF SEGOL
        charMap.put(new Integer('W'), new String(new char[] {0x05B1}));

//      'E'      05B2   HEBREW POINT HATAF PATAH
        charMap.put(new Integer('E'), new String(new char[] {0x05B2}));

//      'R'      05B3   HEBREW POINT HATAF QAMATS
        charMap.put(new Integer('R'), new String(new char[] {0x05B3}));

//      'T'      05B4   HEBREW POINT HIRIQ
        charMap.put(new Integer('T'), new String(new char[] {0x05B4}));

//      'Y'      05B5   HEBREW POINT TSERE
        charMap.put(new Integer('Y'), new String(new char[] {0x05B5}));

//      'U'      05B6   HEBREW POINT SEGOL
        charMap.put(new Integer('U'), new String(new char[] {0x05B6}));

//      'I'      05B7   HEBREW POINT PATAH
        charMap.put(new Integer('I'), new String(new char[] {0x05B7}));

//      'O'      05B8   HEBREW POINT QAMATS
        charMap.put(new Integer('O'), new String(new char[] {0x05B8}));

//      'P'      05B9   HEBREW POINT HOLAM
        charMap.put(new Integer('P'), new String(new char[] {0x05B9}));

//      'A'      05BB   HEBREW POINT QUBUTS
        charMap.put(new Integer('A'), new String(new char[] {0x05BB}));

//      'S'      05BC   HEBREW POINT DAGESH OR MAPIQ
        charMap.put(new Integer('S'), new String(new char[] {0x05BC}));

//      'D'      05BD   HEBREW POINT METEG
        charMap.put(new Integer('D'), new String(new char[] {0x05BD}));

//      'F'      05BE   HEBREW PUNCTUATION MAQAF
        charMap.put(new Integer('F'), new String(new char[] {0x05BE}));

//      'G'      05BF   HEBREW POINT RAFE
        charMap.put(new Integer('G'), new String(new char[] {0x05BF}));

//      'H'      05C0   HEBREW PUNCTUATION PASEQ
        charMap.put(new Integer('H'), new String(new char[] {0x05C0}));

//      'J'      05C1   HEBREW POINT SHIN DOT
        charMap.put(new Integer('J'), new String(new char[] {0x05C1}));

//      'K'      05C2   HEBREW POINT SIN DOT
        charMap.put(new Integer('K'), new String(new char[] {0x05C2}));

//      'L'      05C3   HEBREW PUNCTUATION SOF PASUQ
        charMap.put(new Integer('L'), new String(new char[] {0x05C3}));

//      'M'      05C4   HEBREW MARK UPPER DOT
        charMap.put(new Integer('M'), new String(new char[] {0x05C4}));

//      (unshifted keyboard)

//      't'     05D0   HEBREW LETTER ALEF
        charMap.put(new Integer('t'), new String(new char[] {0x05D0}));

//      'c'     05D1   HEBREW LETTER BET
        charMap.put(new Integer('c'), new String(new char[] {0x05D1}));

//      'd'     05D2   HEBREW LETTER GIMEL
        charMap.put(new Integer('d'), new String(new char[] {0x05D2}));

//      's'     005D3   HEBREW LETTER DALET
        charMap.put(new Integer('s'), new String(new char[] {0x05D3}));

//      'v'     05D4   HEBREW LETTER HE
        charMap.put(new Integer('v'), new String(new char[] {0x05D4}));

//      'u'     05D5   HEBREW LETTER VAV
        charMap.put(new Integer('u'), new String(new char[] {0x05D5}));

//      'z''    05D6   HEBREW LETTER ZAYIN
        charMap.put(new Integer('z'), new String(new char[] {0x05D6}));

//      'j'     05D7   HEBREW LETTER HET
        charMap.put(new Integer('j'), new String(new char[] {0x05D7}));

//      'y'     005D8   HEBREW LETTER TET
        charMap.put(new Integer('y'), new String(new char[] {0x05D8}));

//      'h'     005D9   HEBREW LETTER YOD
        charMap.put(new Integer('h'), new String(new char[] {0x05D9}));

//      '/'     05DA   HEBREW LETTER FINAL KAF
        charMap.put(new Integer('/'), new String(new char[] {0x05DA}));

//      'l'     05DB   HEBREW LETTER KAF
        charMap.put(new Integer('l'), new String(new char[] {0x05DB}));

//      'k'     05DC   HEBREW LETTER LAMED
        charMap.put(new Integer('k'), new String(new char[] {0x05DC}));

//      't'     05DD   HEBREW LETTER FINAL MEM
        charMap.put(new Integer('t'), new String(new char[] {0x05DD}));

//      'n'     05DE   HEBREW LETTER MEM
        charMap.put(new Integer('n'), new String(new char[] {0x05DE}));

//      'i'     05DF   HEBREW LETTER FINAL NUN
        charMap.put(new Integer('i'), new String(new char[] {0x05DF}));

//      'b'     05E0   HEBREW LETTER NUN
        charMap.put(new Integer('b'), new String(new char[] {0x05E0}));

//      'x'     05E1   HEBREW LETTER SAMEKH
        charMap.put(new Integer('x'), new String(new char[] {0x05E1}));

//      'g'     05E2   HEBREW LETTER AYIN
        charMap.put(new Integer('g'), new String(new char[] {0x05E2}));

//      ';'     05E3   HEBREW LETTER FINAL PE
        charMap.put(new Integer(';'), new String(new char[] {0x05E3}));

//      'p'     05E4   HEBREW LETTER PE
        charMap.put(new Integer('p'), new String(new char[] {0x05E4}));

//      '.'     05E5   HEBREW LETTER FINAL TSADI
        charMap.put(new Integer('.'), new String(new char[] {0x05E5}));

//      'm'     05E6   HEBREW LETTER TSADI
        charMap.put(new Integer('m'), new String(new char[] {0x05E6}));

//      'e'     05E7   HEBREW LETTER QOF
        charMap.put(new Integer('e'), new String(new char[] {0x05E7}));

//      'r'     05E8   HEBREW LETTER RESH
        charMap.put(new Integer('r'), new String(new char[] {0x05E8}));

//      'a'     05E9   HEBREW LETTER SHIN
        charMap.put(new Integer('a'), new String(new char[] {0x05E9}));

//      ','     05EA   HEBREW LETTER TAV
        charMap.put(new Integer(','), new String(new char[] {0x05EA}));

//
//      (shifted keyboard)

//      'M'      05F0   HEBREW LIGATURE YIDDISH DOUBLE VAV
        charMap.put(new Integer('M'), new String(new char[] {0x05F0}));

//      '<'      05F1   HEBREW LIGATURE YIDDISH VAV YOD
        charMap.put(new Integer('<'), new String(new char[] {0x05F1}));

//      '>'      05F2   HEBREW LIGATURE YIDDISH DOUBLE YOD
        charMap.put(new Integer('>'), new String(new char[] {0x05F2}));

//      '?'      05F3   HEBREW PUNCTUATION GERESH
        charMap.put(new Integer('?'), new String(new char[] {0x05F3}));

//      '"'      05F4   HEBREW PUNCTUATION GERSHAYIM
        charMap.put(new Integer('"'), new String(new char[] {0x05F4}));


    }

}
