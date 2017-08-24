package com.sf.DarkCalculator;

import com.sf.ExpressionHandler.Expression;
import com.sf.ExpressionHandler.Result;

/**
 * Created by user on 2017/8/5.
 */

public class ExpressionHandler {

    private static Expression expression = null;

    public static String[] calculation(String response) {
        String[] value;
        try {
            expression = new Expression(response);
            Result result = expression.value();
            boolean isError = result.isFatalError();
            String val = result.val.toString();
            value = new String[]{val, "" + isError};
        } catch (Exception e) {
            e.printStackTrace();
            value = new String[]{"···", "true"};
        }
        expression = null;
        return value;
    }

    public static void stop() {
        if (expression != null) {
            expression.stopEvaluation();
            expression = null;
        }
    }
}
