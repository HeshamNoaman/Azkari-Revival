package com.dot.arabic;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class ArabicUtilities {
    private static final String FONTS_LOCATION_PATH = "fonts/DroidSansArabic.ttf";
    static Typeface face;

    private static boolean isArabicCharacter(char target) {
        for (int i = 0; i < ArabicReshaper.ARABIC_GLPHIES.length; i++) {
            if (ArabicReshaper.ARABIC_GLPHIES[i][0] == target) {
                return true;
            }
        }
        for (int i2 = 0; i2 < ArabicReshaper.HARAKATE.length; i2++) {
            if (ArabicReshaper.HARAKATE[i2] == target) {
                return true;
            }
        }
        return false;
    }

    private static String[] getWords(String sentence) {
        return sentence != null ? sentence.split("\\s") : new String[0];
    }

    public static boolean hasArabicLetters(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (isArabicCharacter(word.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isArabicWord(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (!isArabicCharacter(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String[] getWordsFromMixedWord(String word) {
        ArrayList<String> finalWords = new ArrayList<>();
        String tempWord = XmlPullParser.NO_NAMESPACE;
        for (int i = 0; i < word.length(); i++) {
            if (isArabicCharacter(word.charAt(i))) {
                if (!tempWord.equals(XmlPullParser.NO_NAMESPACE) && !isArabicWord(tempWord)) {
                    finalWords.add(tempWord);
                    tempWord = new StringBuilder().append(word.charAt(i)).toString();
                } else {
                    tempWord = String.valueOf(tempWord) + word.charAt(i);
                }
            } else if (!tempWord.equals(XmlPullParser.NO_NAMESPACE) && isArabicWord(tempWord)) {
                finalWords.add(tempWord);
                tempWord = new StringBuilder().append(word.charAt(i)).toString();
            } else {
                tempWord = String.valueOf(tempWord) + word.charAt(i);
            }
        }
        String[] theWords = new String[finalWords.size()];
        return (String[]) finalWords.toArray(theWords);
    }

    public static String reshape(String allText) {
        if (allText != null) {
            StringBuffer result = new StringBuffer();
            String[] sentences = allText.split("\n");
            for (String str : sentences) {
                result.append(reshapeSentence(str));
            }
            return result.toString();
        }
        return null;
    }

    public static String reshapeSentence(String sentence) {
        String[] words = getWords(sentence);
        StringBuffer reshapedText = new StringBuffer(XmlPullParser.NO_NAMESPACE);
        for (int i = 0; i < words.length; i++) {
            if (hasArabicLetters(words[i])) {
                if (isArabicWord(words[i])) {
                    ArabicReshaper arabicReshaper = new ArabicReshaper(words[i]);
                    reshapedText.append(arabicReshaper.getReshapedWord());
                } else {
                    String[] mixedWords = getWordsFromMixedWord(words[i]);
                    for (String str : mixedWords) {
                        ArabicReshaper arabicReshaper2 = new ArabicReshaper(str);
                        reshapedText.append(arabicReshaper2.getReshapedWord());
                    }
                }
            } else {
                reshapedText.append(words[i]);
            }
            reshapedText.append(" ");
        }
        return reshapedText.toString();
    }

    public static TextView getArabicEnabledTextView(Context context, TextView targetTextView) {
        if (face == null) {
            face = Typeface.createFromAsset(context.getAssets(), FONTS_LOCATION_PATH);
        }
        targetTextView.setTypeface(face);
        targetTextView.setGravity(5);
        return targetTextView;
    }
}
