package com.dot.arabic;

import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class ArabicReshaper {
    private String _returnString;
    public static char DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_MDD = 1570;
    public static char DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_HAMAZA = 1571;
    public static char DEFINED_CHARACTERS_ORGINAL_ALF_LOWER_HAMAZA = 1573;
    public static char DEFINED_CHARACTERS_ORGINAL_ALF = 1575;
    public static char DEFINED_CHARACTERS_ORGINAL_LAM = 1604;
    public static char[][] LAM_ALEF_GLPHIES = {new char[]{15270, 65270, 65269}, new char[]{15271, 65272, 65271}, new char[]{1575, 65276, 65275}, new char[]{1573, 65274, 65273}};
    public static char[] HARAKATE = {1536, 1537, 1538, 1539, 1542, 1543, 1544, 1545, 1546, 1547, 1549, 1550, 1552, 1553, 1554, 1555, 1556, 1557, 1558, 1559, 1560, 1561, 1562, 1563, 1566, 1567, 1569, 1595, 1596, 1597, 1598, 1599, 1600, 1611, 1612, 1613, 1614, 1615, 1616, 1617, 1618, 1619, 1620, 1621, 1622, 1623, 1624, 1625, 1626, 1627, 1628, 1629, 1630, 1632, 1642, 1643, 1644, 1647, 1648, 1650, 1748, 1749, 1750, 1751, 1752, 1753, 1754, 1755, 1756, 1759, 1760, 1761, 1762, 1763, 1764, 1765, 1766, 1767, 1768, 1769, 1770, 1771, 1772, 1773, 1774, 1775, 1750, 1751, 1752, 1753, 1754, 1755, 1756, 1757, 1758, 1759, 1776, 1789, 65136, 65137, 65138, 65139, 65140, 65141, 65142, 65143, 65144, 65145, 65146, 65147, 65148, 65149, 65150, 65151, 64606, 64607, 64608, 64609, 64610, 64611, 778};
    public static char[][] ARABIC_GLPHIES = {new char[]{1570, 65153, 65153, 65154, 65154, 2}, new char[]{1571, 65154, 65155, 65156, 65156, 2}, new char[]{1572, 65157, 65157, 65158, 65158, 2}, new char[]{1573, 65159, 65159, 65160, 65160, 2}, new char[]{1574, 65161, 65163, 65164, 65162, 4}, new char[]{1575, 1575, 1575, 65166, 65166, 2}, new char[]{1576, 65167, 65169, 65170, 65168, 4}, new char[]{1577, 65171, 65171, 65172, 65172, 2}, new char[]{1578, 65173, 65175, 65176, 65174, 4}, new char[]{1579, 65177, 65179, 65180, 65178, 4}, new char[]{1580, 65181, 65183, 65184, 65182, 4}, new char[]{1581, 65185, 65187, 65188, 65186, 4}, new char[]{1582, 65189, 65191, 65192, 65190, 4}, new char[]{1583, 65193, 65193, 65194, 65194, 2}, new char[]{1584, 65195, 65195, 65196, 65196, 2}, new char[]{1585, 65197, 65197, 65198, 65198, 2}, new char[]{1586, 65199, 65199, 65200, 65200, 2}, new char[]{1587, 65201, 65203, 65204, 65202, 4}, new char[]{1588, 65205, 65207, 65208, 65206, 4}, new char[]{1589, 65209, 65211, 65212, 65210, 4}, new char[]{1590, 65213, 65215, 65216, 65214, 4}, new char[]{1591, 65217, 65219, 65218, 65220, 4}, new char[]{1592, 65221, 65223, 65222, 65222, 4}, new char[]{1593, 65225, 65227, 65228, 65226, 4}, new char[]{1594, 65229, 65231, 65232, 65230, 4}, new char[]{1601, 65233, 65235, 65236, 65234, 4}, new char[]{1602, 65237, 65239, 65240, 65238, 4}, new char[]{1603, 65241, 65243, 65244, 65242, 4}, new char[]{1604, 65245, 65247, 65248, 65246, 4}, new char[]{1605, 65249, 65251, 65252, 65250, 4}, new char[]{1606, 65253, 65255, 65256, 65254, 4}, new char[]{1607, 65257, 65259, 65260, 65258, 4}, new char[]{1608, 65261, 65261, 65262, 65262, 2}, new char[]{1609, 65263, 65263, 65264, 65264, 2}, new char[]{1649, 1649, 1649, 64337, 64337, 2}, new char[]{1610, 65265, 65267, 65268, 65266, 4}, new char[]{1646, 64484, 64488, 64489, 64485, 4}, new char[]{1649, 1649, 1649, 64337, 64337, 2}, new char[]{1706, 64398, 64400, 64401, 64399, 4}, new char[]{1729, 64422, 64424, 64425, 64423, 4}, new char[]{1764, 1764, 1764, 1764, 65262, 2}};

    public String getReshapedWord() {
        return this._returnString;
    }

    private char getReshapedGlphy(char target, int location) {
        for (int n = 0; n < ARABIC_GLPHIES.length; n++) {
            if (ARABIC_GLPHIES[n][0] == target) {
                return ARABIC_GLPHIES[n][location];
            }
        }
        return target;
    }

    private int getGlphyType(char target) {
        for (int n = 0; n < ARABIC_GLPHIES.length; n++) {
            if (ARABIC_GLPHIES[n][0] == target) {
                return ARABIC_GLPHIES[n][5];
            }
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isHaraka(char target) {
        for (int n = 0; n < HARAKATE.length; n++) {
            if (HARAKATE[n] == target) {
                return true;
            }
        }
        return false;
    }

    private String replaceLamAlef(String unshapedWord) {
        char lamAlef;
        int wordLength = unshapedWord.length();
        char[] wordLetters = new char[wordLength];
        unshapedWord.getChars(0, wordLength, wordLetters, 0);
        char letterBefore = 0;
        for (int index = 0; index < wordLetters.length - 1; index++) {
            if (!isHaraka(wordLetters[index]) && DEFINED_CHARACTERS_ORGINAL_LAM != wordLetters[index]) {
                letterBefore = wordLetters[index];
            }
            if (DEFINED_CHARACTERS_ORGINAL_LAM == wordLetters[index]) {
                char candidateLam = wordLetters[index];
                int lamPosition = index;
                int harakaPosition = lamPosition + 1;
                while (harakaPosition < wordLetters.length && isHaraka(wordLetters[harakaPosition])) {
                    harakaPosition++;
                }
                if (harakaPosition < wordLetters.length) {
                    if (lamPosition > 0 && getGlphyType(letterBefore) > 2) {
                        lamAlef = getLamAlef(wordLetters[harakaPosition], candidateLam, false);
                    } else {
                        lamAlef = getLamAlef(wordLetters[harakaPosition], candidateLam, true);
                    }
                    if (lamAlef != 0) {
                        wordLetters[lamPosition] = lamAlef;
                        wordLetters[harakaPosition] = ' ';
                    }
                }
            }
        }
        return new String(wordLetters).replaceAll(" ", XmlPullParser.NO_NAMESPACE).trim();
    }

    private char getLamAlef(char candidateAlef, char candidateLam, boolean isEndOfWord) {
        int shiftRate = 1;
        char reshapedLamAlef = 0;
        if (isEndOfWord) {
            shiftRate = 1 + 1;
        }
        if (DEFINED_CHARACTERS_ORGINAL_LAM != candidateLam) {
            return (char) 0;
        }
        if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_MDD) {
            reshapedLamAlef = LAM_ALEF_GLPHIES[0][shiftRate];
        }
        if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_HAMAZA) {
            reshapedLamAlef = LAM_ALEF_GLPHIES[1][shiftRate];
        }
        if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF_LOWER_HAMAZA) {
            reshapedLamAlef = LAM_ALEF_GLPHIES[3][shiftRate];
        }
        if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF) {
            char reshapedLamAlef2 = LAM_ALEF_GLPHIES[2][shiftRate];
            return reshapedLamAlef2;
        }
        return reshapedLamAlef;
    }

    public ArabicReshaper(String unshapedWord) {
        this._returnString = XmlPullParser.NO_NAMESPACE;
        DecomposedWord decomposedWord = new DecomposedWord(replaceLamAlef(unshapedWord));
        if (decomposedWord.stripedRegularLetters.length > 0) {
            this._returnString = reshapeIt(new String(decomposedWord.stripedRegularLetters));
        }
        this._returnString = decomposedWord.reconstructWord(this._returnString);
    }

    class DecomposedWord {
        int[] harakatesPositions;
        int[] lettersPositions;
        char[] stripedHarakates;
        char[] stripedRegularLetters;

        DecomposedWord(String unshapedWord) {
            int wordLength = unshapedWord.length();
            int harakatesCount = 0;
            for (int index = 0; index < wordLength; index++) {
                if (ArabicReshaper.this.isHaraka(unshapedWord.charAt(index))) {
                    harakatesCount++;
                }
            }
            this.harakatesPositions = new int[harakatesCount];
            this.stripedHarakates = new char[harakatesCount];
            this.lettersPositions = new int[wordLength - harakatesCount];
            this.stripedRegularLetters = new char[wordLength - harakatesCount];
            int harakatesCount2 = 0;
            int letterCount = 0;
            for (int index2 = 0; index2 < unshapedWord.length(); index2++) {
                if (ArabicReshaper.this.isHaraka(unshapedWord.charAt(index2))) {
                    this.harakatesPositions[harakatesCount2] = index2;
                    this.stripedHarakates[harakatesCount2] = unshapedWord.charAt(index2);
                    harakatesCount2++;
                } else {
                    this.lettersPositions[letterCount] = index2;
                    this.stripedRegularLetters[letterCount] = unshapedWord.charAt(index2);
                    letterCount++;
                }
            }
        }

        String reconstructWord(String reshapedWord) {
            char[] wordWithHarakates = new char[reshapedWord.length() + this.stripedHarakates.length];
            for (int index = 0; index < this.lettersPositions.length; index++) {
                wordWithHarakates[this.lettersPositions[index]] = reshapedWord.charAt(index);
            }
            for (int index2 = 0; index2 < this.harakatesPositions.length; index2++) {
                wordWithHarakates[this.harakatesPositions[index2]] = this.stripedHarakates[index2];
            }
            return new String(wordWithHarakates);
        }
    }

    public String reshapeIt(String unshapedWord) {
        StringBuffer reshapedWord = new StringBuffer(XmlPullParser.NO_NAMESPACE);
        int wordLength = unshapedWord.length();
        char[] wordLetters = new char[wordLength];
        unshapedWord.getChars(0, wordLength, wordLetters, 0);
        reshapedWord.append(getReshapedGlphy(wordLetters[0], 2));
        for (int i = 1; i < wordLength - 1; i++) {
            int beforeLast = i - 1;
            if (getGlphyType(wordLetters[beforeLast]) == 2) {
                reshapedWord.append(getReshapedGlphy(wordLetters[i], 2));
            } else {
                reshapedWord.append(getReshapedGlphy(wordLetters[i], 3));
            }
        }
        if (wordLength >= 2) {
            if (getGlphyType(wordLetters[wordLength - 2]) == 2) {
                reshapedWord.append(getReshapedGlphy(wordLetters[wordLength - 1], 1));
            } else {
                reshapedWord.append(getReshapedGlphy(wordLetters[wordLength - 1], 4));
            }
        }
        return reshapedWord.toString();
    }
}
