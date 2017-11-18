package com.sf.ExpressionHandler;

public class Result {
    public Complex val = new Complex(0);
    private int err;
    public static int precision = 10;
    public static int base = 10;
    public static int maxPrecision = 15;

    public Result(Complex v) {
        val = v;
        err = v.err;
    }

    public Result(int err_) {
        val = new Complex(Double.NaN, Double.NaN);
        err = err_;
    }

    public Result setAnswer(String answer) {
        val.setAnswer(answer);
        //append(answer);
        return this;
    }

    public Result append(String name) {
        //Temporarily not used
        return this;
    }

    public Result setVal(Complex v_) {
        val = v_;
        return this;
    }

    public static void setBase(int base_) { // set the radix as base_
        base = base_;
        precision = (int) Math.floor(35 * Math.log(2) / Math.log(base_));
        maxPrecision = (int) Math.floor(52 * Math.log(2) / Math.log(base_));
    }

    public boolean isFatalError() {
        return this.err > 0;
    }

    public int getError() {
        return this.err;
    }
}
