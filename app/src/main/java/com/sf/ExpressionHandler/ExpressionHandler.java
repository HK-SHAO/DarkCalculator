package com.sf.ExpressionHandler;

/**
 * Created by user on 2017/8/5.
 */

public class ExpressionHandler {

    private static Expression expression = null;

    public static String[] calculation(String response) {
        try {
            expression = new Expression(response);
            Result result = expression.value();
            expression = null;
            String val = result.val.toString();
            switch (result.getError()) {
                case 0:
                    return new String[]{val, "false"};
                case 1:
                    return new String[]{val, "true"};
                case 2:
                    return new String[]{"已强制停止运算", "false"};
                case 3:
                    return new String[]{"函数不支持复数", "true"};
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"未知错误", "true"};
        }
    }

    public static void stop() {
        if (expression != null) {
            expression.stopEvaluation();
            expression = null;
        }
    }
}
