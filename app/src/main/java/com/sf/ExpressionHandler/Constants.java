package com.sf.ExpressionHandler;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static List<String[]> constants = load();

    private static List<String[]> load() {
        List list = new ArrayList();
        list.add(new String[]{"ans", "0"});
        list.add(new String[]{"h", "6.62606876E-34"});
        list.add(new String[]{"ћ", "1.0545718E-34"});
        list.add(new String[]{"c", "299792458"});
        list.add(new String[]{"N", "6.0221409E23"});
        list.add(new String[]{"R", "8.3144621"});
        list.add(new String[]{"k", "1.38064852E-23"});
        list.add(new String[]{"G", "6.67E-11"});
        list.add(new String[]{"F", "9.64853399E4"});
        list.add(new String[]{"γ", "0.5772156649"});
        list.add(new String[]{"φ", "0.61803398875"});
        list.add(new String[]{"Φ", "2.067833636E-15"});
        list.add(new String[]{"me", "9.10938188E-31"});
        list.add(new String[]{"mn", "1.67262158E-27"});
        list.add(new String[]{"mp", "1.67492716E-27"});
        return list;
    }
}