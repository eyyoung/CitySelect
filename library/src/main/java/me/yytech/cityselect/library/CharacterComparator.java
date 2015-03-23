package me.yytech.cityselect.library;

import java.util.Comparator;

public class CharacterComparator implements Comparator<Character> {
    @Override
    public int compare(Character lhs, Character rhs) {
        return lhs.compareTo(rhs);
    }
}
