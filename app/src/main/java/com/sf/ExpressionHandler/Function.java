package com.sf.ExpressionHandler;

/**
 * Created by Iraka Crow on 2017/3/25.
 */

class Function {

    // place a function serial here, interval 10 (accept maximum 10 params.)

    static final int EXP = 10;
    static final int LN = 20;
    static final int RE = 30;
    static final int IM = 40;
    static final int SQRT = 50;
    static final int ABS = 60;
    static final int NORM = 70;
    static final int ARG = 80;
    static final int SIN = 90;
    static final int COS = 100;
    static final int TAN = 110;
    static final int ASIN = 120;
    static final int ACOS = 130;
    static final int ATAN = 140;
    static final int GAMMA = 150;
    static final int FLOOR = 160;
    static final int CEIL = 170;
    static final int REG = 180;
    static final int CONJ = 190;
    static final int RAND = 200;
    static final int ROUND = 210;
    static final int DIFF = 220;
    static final int LIMIT = 230;
    static final int EVAL = 240;
    static final int FZERO = 250;
    static final int INTEG = 260;
    static final int SUM = 270;
    static final int PERM = 280;
    static final int COMB = 290;
    static final int PREC = 300;
    static final int BASE = 320;

    static final int CBRT = 330;
    static final int LOG = 340;
    static final int SINH = 350;
    static final int COSH = 360;
    static final int TANH = 370;
    static final int ASINH = 380;
    static final int ACOSH = 390;
    static final int ATANH = 400;
    static final int MAX = 410;
    static final int MIN = 420;
    static final int FACT = 430;
    static final int RECIPR = 440;
    static final int PRIME = 460;
    static final int ISPRIME = 470;
    static final int GCD = 480;
    static final int LCM = 490;
    static final int ISODD = 500;
    static final int LOGAB = 510;
    static final int SIGN = 520;
    static final int RESTART = 530;
    static final int SETCR = 540;
    static final int SETTS = 550;
    static final int RANDINT = 560;
    static final int TODEG = 570;
    static final int TORAD = 580;
    static final int REMN = 590;
    static final int REDUC = 600;
    static final int ROOT = 610;

    static class Serial { // function name - serial struct
        String funcName;
        int funcSerial;
        int exprParamNum; // how many param accept expression(with x) as input

        Serial(String name_, int serial_) {
            funcName = name_;
            funcSerial = serial_;
            exprParamNum = 0;
        }

        Serial(String name_, int serial_, int ePN_) {
            funcName = name_;
            funcSerial = serial_;
            exprParamNum = ePN_;
        }
    }

    // Register name and serial pair
    // Different name may direct to same function
    static final Serial[] funcList = {
            new Serial("exp", EXP),
            new Serial("ln", LN),
            new Serial("re", RE),
            new Serial("im", IM),
            new Serial("sqrt", SQRT),
            new Serial("abs", ABS),
            new Serial("norm", NORM),
            new Serial("arg", ARG),
            new Serial("sin", SIN),
            new Serial("cos", COS),
            new Serial("tan", TAN),
            new Serial("asin", ASIN),
            new Serial("acos", ACOS),
            new Serial("atan", ATAN),
            new Serial("gamma", GAMMA),
            new Serial("floor", FLOOR),
            new Serial("ceil", CEIL),
            new Serial("reg", REG),
            new Serial("conj", CONJ),
            new Serial("rand", RAND),
            new Serial("round", ROUND),
            new Serial("diff", DIFF, 1),
            new Serial("limit", LIMIT, 1),
            new Serial("eval", EVAL, 1),
            new Serial("fzero", FZERO, 1),
            new Serial("integ", INTEG, 1),
            new Serial("sum", SUM, 1),
            new Serial("perm", PERM),
            new Serial("comb", COMB),
            new Serial("setPrec", PREC),
            new Serial("setBase", BASE),

            new Serial("cbrt", CBRT),
            new Serial("log", LOG),
            new Serial("sinh", SINH),
            new Serial("cosh", COSH),
            new Serial("tanh", TANH),
            new Serial("asinh", ASINH),
            new Serial("acosh", ACOSH),
            new Serial("atanh", ATANH),
            new Serial("max", MAX),
            new Serial("min", MIN),
            new Serial("fact", FACT),
            new Serial("recipr", RECIPR),
            new Serial("prime", PRIME),
            new Serial("isPrime", ISPRIME),
            new Serial("gcd", GCD),
            new Serial("lcm", LCM),
            new Serial("isOdd", ISODD),
            new Serial("logab", LOGAB),
            new Serial("sign", SIGN),
            new Serial("reStart", RESTART),
            new Serial("setCR", SETCR),
            new Serial("setTS", SETTS),
            new Serial("randInt", RANDINT),
            new Serial("toDEG", TODEG),
            new Serial("toRAD", TORAD),
            new Serial("remn", REMN),
            new Serial("reduc", REDUC),
            new Serial("root", ROOT),
    };
}
