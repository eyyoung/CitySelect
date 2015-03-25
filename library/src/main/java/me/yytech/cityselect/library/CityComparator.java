package me.yytech.cityselect.library;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

public class CityComparator implements Comparator<String> {
    public int compare(String key1, String key2) {
        for (int i = 0; i < key1.length() && i < key2.length(); i++) {
            int codePoint1 = key1.charAt(i);
            int codePoint2 = key2.charAt(i);
            if (Character.isSupplementaryCodePoint(codePoint1)
                    || Character.isSupplementaryCodePoint(codePoint2)) {
                i++;
            }
            if (codePoint1 != codePoint2) {
                if (Character.isSupplementaryCodePoint(codePoint1)
                        || Character.isSupplementaryCodePoint(codePoint2)) {
                    return codePoint1 - codePoint2;
                }
                String pinyin1 = pinyin((char) codePoint1);
                String pinyin2 = pinyin((char) codePoint2);
                if (pinyin1 != null && pinyin2 != null) {
                    if (!pinyin1.equals(pinyin2)) {
                        return pinyin1.compareTo(pinyin2);
                    }
                } else {
                    return codePoint1 - codePoint2;
                }
            }
        }
        return key1.length() - key2.length();
    }


    private String pinyin(char c) {

        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);

        if (pinyins == null) {

            return null;

        }

        return pinyins[0];

    }
}
