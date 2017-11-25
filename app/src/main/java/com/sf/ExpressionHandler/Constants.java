package com.sf.ExpressionHandler;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    private static Map<String, String> constants;

    public static Map<String, String> load() {
        if (constants == null) {
            Map map = new HashMap<>();
            map.put("ans", "0");
            map.put("h", "6.62606876E-34");
            map.put("ћ", "1.0545718E-34");
            map.put("c", "299792458");
            map.put("N", "6.0221409E23");
            map.put("R", "8.3144621");
            map.put("k", "1.38064852E-23");
            map.put("G", "6.67E-11");
            map.put("F", "9.64853399E4");
            map.put("γ", "0.5772156649");
            map.put("φ", "0.61803398875");
            map.put("Φ", "2.067833636E-15");
            map.put("me", "9.10938188E-31");
            map.put("mn", "1.67262158E-27");
            map.put("mp", "1.67492716E-27");
            map.put("K", "2.6854520010");
            map.put("false", "0");
            map.put("true", "1");
            constants = map;
        }
        return constants;
    }

    public static void setAns(String value) {
        constants.put("ans", value);
    }
}