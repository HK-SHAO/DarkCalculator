package com.sf.DarkCalculator;

import com.sf.ExpressionHandler.Expression;
import com.sf.ExpressionHandler.Result;

/**
 * Created by user on 2017/8/5.
 */

public class ExpressionHandler {

    public static String[] calculation(String response) {
        String[] value;
        try {
            Result r = new Expression(response).value();
            boolean isError = r.isFatalError();
            String val = r.val.toString();
            value = new String[]{val, "" + isError};
        } catch (Exception e) {
            value = new String[]{"表达式语法错误", "true"};
        }
        return value;
    }
}
