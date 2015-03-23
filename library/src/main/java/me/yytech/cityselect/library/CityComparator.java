package me.yytech.cityselect.library;

import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.Comparator;

public class CityComparator implements Comparator<String> {
    @Override
    public int compare(String lhs, String rhs) {
        return PinyinHelper.convertToPinyinString(lhs, " ").compareTo(PinyinHelper.convertToPinyinString(rhs, " "));
    }
}
