package com.sf.ExpressionHandler;

import com.sf.DarkCalculator.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Expression {
    private String text;
    private int[] br; // Bracket Depth
    private int[] lastLB; // last left bracket of the same level
    private int[] nextFS; // next functional symbol of the same level
    private int[] commaCnt; // for each '(', how many comma belongs to it?
    private int[] funcSer; // the function interpreted
    private int brDiff; // the difference of ( and )

    // Cache interpretation result
    private class SymbolCachePair {
        static final int SYMBOL_NUM = 0; // may cache value
        static final int SYMBOL_ADD = 1;
        static final int SYMBOL_POS = 2;
        static final int SYMBOL_SUB = 3;
        static final int SYMBOL_NEG = 4;
        static final int SYMBOL_MUL = 5;
        static final int SYMBOL_DIV = 6;
        static final int SYMBOL_MUL_OMIT = 7;
        static final int SYMBOL_POW = 8;
        static final int SYMBOL_SQRT = 9;
        static final int SYMBOL_CONST = 10; // may cache value
        static final int SYMBOL_FUNC = 11;
        static final int SYMBOL_VAR = 12; // Temporarily not used
        static final int SYMBOL_BRACKET = 13;
        static final int SYMBOL_FACT = 14;

        int end_pos;
        int symbol;
        int symbol_pos;
        Complex cachedValue;

        SymbolCachePair(int end_pos_, int symbol_, int symbol_pos_, Complex cachedValue_) {
            end_pos = end_pos_;
            symbol = symbol_;
            symbol_pos = symbol_pos_;
            cachedValue = cachedValue_;
        }
    }

    private class SymbolCache {
        List<SymbolCachePair> list;

        SymbolCache() {
            list = new ArrayList<>();
        }

        void submit(int end_pos_, int symbol_, int symbol_pos_) {
            list.add(new SymbolCachePair(end_pos_, symbol_, symbol_pos_, new Complex()));
            //Log.i("expression","Submit pos="+symbol_pos_+" type="+symbol_);
        }

        void submit(int end_pos_, int symbol_, Complex cachedValue_) {
            list.add(new SymbolCachePair(end_pos_, symbol_, -1, cachedValue_));
            //Log.i("expression","Submit val="+cachedValue_+" type="+symbol_);
        }

        SymbolCachePair checkCache(int end_pos) {
            for (int i = 0; i < list.size(); i++) {
                SymbolCachePair pair = list.get(i);
                if (pair.end_pos == end_pos) {
                    return pair;
                }
            }
            return null;
        }
    }

    private SymbolCache[] interpretResult;

    private volatile boolean isWorking;

    private static final String mathOperator = "+-*•/^√";
    private static Complex memValue = new Complex(); // for memory function

    public Expression(String s) {
        text = s;
        br = new int[s.length() + 1];
        lastLB = new int[s.length() + 1];
        nextFS = new int[s.length() + 1];
        commaCnt = new int[s.length() + 1];
        brDiff = 0;

        int[] symbolStack = new int[s.length() + 1]; // a position stack of all left brackets
        int[] lastSymbol = new int[s.length() + 1]; // what's the position of the last symbol ?

        int top = -1;

        br[0] = 0;
        for (int i = 0; i < s.length(); i++) {
            lastLB[i] = -1;
            nextFS[i] = -1;
            commaCnt[i] = 0;
            char c = s.charAt(i);
            if (i > 0) {
                br[i] = br[i - 1];
                if (s.charAt(i - 1) == '(') br[i]++;
                if (c == ')') br[i]--;
            }

            if (c == '(') { // push
                top++;
                symbolStack[top] = i;
                lastLB[i] = i;
                lastSymbol[top] = i;
                brDiff++;
            }
            if (c == ',' && top >= 0) { // record
                lastLB[i] = symbolStack[top];
                commaCnt[symbolStack[top]]++;
                nextFS[lastSymbol[top]] = i;
                lastSymbol[top] = i;
            }
            if (c == ')') {
                if (top >= 0) { // pop
                    lastLB[i] = symbolStack[top];
                    nextFS[lastSymbol[top]] = i;
                    top--;
                }
                brDiff--;
            }
        }
    }

    private void initCache() { // init cache for evaluation
        funcSer = new int[text.length()];
        interpretResult = new SymbolCache[text.length()];
        for (int i = 0; i < interpretResult.length; i++) {
            interpretResult[i] = new SymbolCache();
            funcSer[i] = -1;
        }
    }

    private boolean isOperator(char c) {
        return mathOperator.indexOf(c) != -1;
    }

    // only used when text[p]=='+'/'-' && p>0
    private boolean isAddSubSymbol(int p) {
        if (p == 0) return false;

        char cj = text.charAt(p);
        if (!(cj == '+' || cj == '-')) {
            return false;
        }

        cj = text.charAt(p - 1);
        if (isOperator(cj) || cj == 'E') {
            return false;
        }
        if (ParseNumber.isBaseSymbol(cj)) { // a pos/neg symbol in scientific notation under certain base
            int pos;
            for (pos = p + 1; pos < text.length(); pos++) {
                cj = text.charAt(pos);
                if (!(cj >= '0' && cj <= '9')) { // not a decimal number
                    break;
                }
            }
            if (pos == text.length()) { // parsed to an end
                return false;
            }
            if (pos == p + 1) { // '+/-' directly followed by non-integer symbol
                return true;
            }
            if (ParseNumber.isBaseSymbol(cj) || (cj >= 'A' && cj <= 'F') || cj == '.') { // part of another notation
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean isOmitMult(int p) {
        if (p == 0) return false;

        char ci = text.charAt(p);
        char cj = text.charAt(p - 1);

        boolean iscjPreSymbol = (cj == ')' || cj == '∞' || cj == 'π' || cj == '°' || cj == '!' || cj == '%');
        boolean iscjNumber = (cj >= '0' && cj <= '9' || cj == '.');
        boolean iscjBase = ParseNumber.isBaseSymbol(cj);
        boolean iscjFunc = (cj >= 'a' && cj <= 'z');
        boolean isciNumber = (ci >= '0' && ci <= '9' || ci == '.');

        if ((ci >= 'a' && ci <= 'z' || ci == '(') && (iscjNumber || iscjPreSymbol || iscjBase))
            return true;
        else if ((isciNumber) && (iscjPreSymbol || iscjFunc))
            return true;
        else if ((ci == '∞' || ci == 'π' || ci == '°' || ci == '%' || ci == 'Γ' || ci == '√' || ci == '!') && (iscjNumber || iscjPreSymbol || iscjBase || iscjFunc))
            return true;
        else
            return false;
    }

    // 0+NaN*I is never possible during a calculation
    // and is so used as "No Variable X provided" sign
    public Result value(int l, int r, Complex vX) {
        if (!isWorking) return new Result(2);

        if (l > r) {
            return new Result(1).setAnswer("表达式语法错误");
        }

        // Check if result cached
        SymbolCachePair pair = interpretResult[l].checkCache(r);
        if (pair != null) { // cached result
            Result r1, r2;
            switch (pair.symbol) { // No fatal error now
                case SymbolCachePair.SYMBOL_CONST:
                case SymbolCachePair.SYMBOL_NUM:
                    return new Result(pair.cachedValue);
                case SymbolCachePair.SYMBOL_ADD:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.add(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_SUB:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.sub(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_POS:
                    return value(l + 1, r, vX);
                case SymbolCachePair.SYMBOL_NEG:
                    r1 = value(l + 1, r, vX);
                    return new Result(Complex.inv(r1.val));
                case SymbolCachePair.SYMBOL_MUL:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.mul(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_DIV:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.div(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_MUL_OMIT:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos, r, vX); // Attention to the pos!
                    return new Result(Complex.mul(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_POW:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    r2 = value(pair.symbol_pos + 1, r, vX);
                    return new Result(Complex.pow(r1.val, r2.val));
                case SymbolCachePair.SYMBOL_SQRT:
                    r1 = value(l + 1, r, vX);
                    return new Result(Complex.sqrt(r1.val));
                case SymbolCachePair.SYMBOL_FUNC:
                    return funcValue(l, r, vX);
                case SymbolCachePair.SYMBOL_BRACKET:
                    return value(l + 1, r - 1, vX);
                case SymbolCachePair.SYMBOL_FACT:
                    r1 = value(l, pair.symbol_pos - 1, vX);
                    return fact(r1.val);
            }
        }

        // Interpret expression
        String s = text.substring(l, r + 1);

        // Variable
        if (s.equals("x") && (vX.isValid() || vX.isNaN())) return new Result(vX); // variable X
        if (s.equals("reg")) return new Result(memValue); // reg value

        // omit space and enter
        if (text.charAt(l) == ' ' || text.charAt(l) == '\n' || text.charAt(l) == '\r')
            return value(l + 1, r, vX);
        if (text.charAt(r) == ' ' || text.charAt(r) == '\n' || text.charAt(r) == '\r')
            return value(l, r - 1, vX);

        /*======================= Below this line, string will be parsed only once ========================*/

        { // Constants
            Complex complexConst = null;
            if (s.equals("e")) complexConst = Complex.E; // constant e
            else if (s.equals("π")) complexConst = Complex.PI; // constant pi
            else if (s.equals("i")) complexConst = Complex.I; // constant i
            else if (s.equals("∞")) complexConst = Complex.Inf; // constant Infinity
            else if (s.equals("°")) complexConst = new Complex(Math.PI / 180); // degree value
            else if (s.equals("%")) complexConst = new Complex(0.01); // percent value
            else {
                String constValue = Constants.load().get(s);
                if (constValue != null) {
                    complexConst = new Complex(constValue);
                }
            }

            if (complexConst != null) {
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_CONST, complexConst);
                return new Result(complexConst);
            }
        }

        // Number parsing
        try {
            // Forbid default real values and char e as a operator
            // Forbid default notations
            if (s.indexOf('e') >= 0 || s.indexOf('I') >= 0 || s.indexOf('N') >= 0 ||
                    s.indexOf('X') >= 0 || s.indexOf('P') >= 0 || s.indexOf('x') >= 0 || s.indexOf('p') >= 0) {
                throw new NumberFormatException();
            }

            try { // try parse decimal double
                if (s.indexOf('D') >= 0 || s.indexOf('F') >= 0) { // forbid double and float sign
                    throw new NumberFormatException();
                }

                double v = Double.parseDouble(s);
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_NUM, new Complex(v));
                return new Result(new Complex(v));
            } catch (NumberFormatException e) { // try parse double under a base
                // Not a valid dec Double
                double v = ParseNumber.parse(s);
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_NUM, new Complex(v));
                return new Result(new Complex(v));
            }
        } catch (NumberFormatException e) {
            // Not a valid Number
        }

        char ci;
        // Addition and Subtraction
        for (int i = r; i > l; i--) {
            ci = text.charAt(i);
            // Only ONE of the following long boolean expression will be calculated
            if (br[i] == br[l] && isAddSubSymbol(i)) {
                if (ci == '+') {
                    interpretResult[l].submit(r, SymbolCachePair.SYMBOL_ADD, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError()) return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError()) return r2;
                    return new Result(Complex.add(r1.val, r2.val));
                } else if (ci == '-') {
                    interpretResult[l].submit(r, SymbolCachePair.SYMBOL_SUB, i);
                    Result r1 = value(l, i - 1, vX);
                    if (r1.isFatalError()) return r1;
                    Result r2 = value(i + 1, r, vX);
                    if (r2.isFatalError()) return r2;
                    return new Result(Complex.sub(r1.val, r2.val));
                }
            }
        }

        // Unary operator: positive and negative
        if (text.charAt(l) == '+') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_POS, -1);
            return value(l + 1, r, vX);
        } else if (text.charAt(l) == '-') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_NEG, -1);
            Result r1 = value(l + 1, r, vX);
            if (r1.isFatalError()) return r1;
            return new Result(Complex.inv(r1.val));
        }

        // Multiplication and Division
        for (int i = r; i > l; i--) {
            if (br[i] == br[l]) {
                ci = text.charAt(i);
                Result r1, r2;
                switch (ci) {
                    case '*':
                    case '•':
                        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_MUL, i);
                        r1 = value(l, i - 1, vX);
                        if (r1.isFatalError()) return r1;
                        r2 = value(i + 1, r, vX);
                        if (r2.isFatalError()) return r2;
                        return new Result(Complex.mul(r1.val, r2.val));
                    case '/':
                        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_DIV, i);
                        r1 = value(l, i - 1, vX);
                        if (r1.isFatalError()) return r1;
                        r2 = value(i + 1, r, vX);
                        if (r2.isFatalError()) return r2;
                        return new Result(Complex.div(r1.val, r2.val));
                    case '!':
                        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_FACT, i);
                        r1 = value(l, i - 1, vX);
                        if (r1.isFatalError()) return r1;
                        if (i != r)
                            return new Result(1).setAnswer("无法计算 “" + s + "”");
                        if (r1.val.re % 1 != 0 || r1.val.re < 0)
                            return new Result(1).setAnswer("阶乘只能作用于自然数");
                        return fact(r1.val);
                    default:
                        if (isOmitMult(i)) { // * symbol omission
                            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_MUL_OMIT, i);
                            r1 = value(l, i - 1, vX);
                            if (r1.isFatalError()) return r1;
                            r2 = value(i, r, vX);
                            if (r2.isFatalError()) return r2;
                            return new Result(Complex.mul(r1.val, r2.val));
                        }
                }
            }
        }

        // Power (priority right->left)
        for (int i = l; i <= r; i++)
            if (br[i] == br[l] && text.charAt(i) == '^') {
                interpretResult[l].submit(r, SymbolCachePair.SYMBOL_POW, i);
                Result r1 = value(l, i - 1, vX);
                if (r1.isFatalError()) return r1;
                Result r2 = value(i + 1, r, vX);
                if (r2.isFatalError()) return r2;
                return new Result(Complex.pow(r1.val, r2.val));
            }

        // Sqrt symbol
        if (text.charAt(l) == '√') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_SQRT, -1);
            Result r1 = value(l + 1, r, vX);
            if (r1.isFatalError()) return r1;
            return new Result(Complex.sqrt(r1.val));
        }

        // Brackets
        if (text.charAt(r) != ')')
            return new Result(1).setAnswer("无法计算 “" + s + "”");
        if (text.charAt(l) == '(') {
            interpretResult[l].submit(r, SymbolCachePair.SYMBOL_BRACKET, -1);
            return value(l + 1, r - 1, vX);
        }

        // parse function
        interpretResult[l].submit(r, SymbolCachePair.SYMBOL_FUNC, -1);
        return funcValue(l, r, vX);
    }

    private Result funcValue(int l, int r, Complex vX) {
        String s = text.substring(l, r + 1);

        // Functions
        int listPos; // the Position in Function class
        int funcID; // the ID in Function class
        int paramNum; // how many params in the function
        int leftBr; // where's the left bracket
        int exprParamNum; // how many params requires functional input

        if (funcSer[l] < 0) { // not searched in list yet
            for (int i = 0; i < Function.funcList.length; i++) {
                if (s.startsWith(Function.funcList[i].funcName + "(")) {
                    //Log.i("expression","parse "+s);
                    funcSer[l] = i; // found
                    break;
                }
            }
        }

        listPos = funcSer[l];

        // Not found
        if (listPos < 0)
            return new Result(1).setAnswer("没有函数 “" + s.substring(0, s.length()) + "”");

        funcID = Function.funcList[listPos].funcSerial;
        leftBr = l + Function.funcList[listPos].funcName.length();
        exprParamNum = Function.funcList[listPos].exprParamNum;
        if (text.charAt(leftBr + 1) == ')') {
            paramNum = 0;
        } else {
            paramNum = commaCnt[leftBr] + 1;
        }

        // Too many param.
        if (paramNum > 9)
            return new Result(1).setAnswer("函数 “" + Function.funcList[listPos].funcName + "” 参数错误");

        // Calculate each param. value
        Complex[] val = new Complex[10];
        if (paramNum > 0) {
            for (int p = leftBr, i = 0; nextFS[p] >= 0; p = nextFS[p], i++) {
                if (i >= exprParamNum) {
                    int resl = p + 1;
                    int resr = nextFS[p] - 1;
                    Result res = value(resl, resr, vX);
                    if (res.isFatalError())
                        return res.setAnswer("函数 “" + Function.funcList[listPos].funcName + "” 参数无效");
                    val[i] = res.val;
                }
            }
        }

        int funcJump = funcID + paramNum;
        switch (funcJump) {
            case Function.ROOT + 2:
                return new Result(Complex.pow(val[0], Complex.div(new Complex(1), val[1])));
            case Function.REMN + 2:
                if (val[0].im != 0 || val[1].im != 0)
                    return new Result(3);
                return new Result(new Complex(val[0].re % val[1].re));
            case Function.RANDINT:
                return new Result(new Complex(Math.round(Math.random())));
            case Function.RANDINT + 1:
                if (val[0].im != 0)
                    return new Result(3);
                return new Result(new Complex(Math.round(Math.random() * val[0].re)));
            case Function.RANDINT + 2:
                if (val[0].im != 0 || val[1].im != 0)
                    return new Result(3);
                return new Result(new Complex(Math.round(val[0].re + Math.random() * (val[1].re - val[0].re))));
            case Function.SIGN + 1:
                if (val[0].im != 0)
                    return new Result(3);
                return new Result(new Complex(Math.signum(val[0].re)));
            case Function.LCM + 2:
                if (val[0].re % 1 != 0 || val[1].re % 1 != 0 || val[0].re <= 0 || val[1].re <= 0)
                    return new Result(1).setAnswer("参数必须是正整数");
                return lcm(val[0], val[1]);
            case Function.GCD + 2:
                if (val[0].re % 1 != 0 || val[1].re % 1 != 0 || val[0].re <= 0 || val[1].re <= 0)
                    return new Result(1).setAnswer("参数必须是正整数");
                return gcd(val[0], val[1]);
            case Function.ISPRIME + 1:
                if (val[0].re % 1 != 0 || val[0].re <= 0)
                    return new Result(1).setAnswer("参数必须是正整数");
                return new Result(isPrime(val[0]));
            case Function.PRIME + 1:
                if (val[0].re % 1 != 0 || val[0].re <= 0)
                    return new Result(1).setAnswer("寻找质数的参数必须是正整数");
                return prime(val[0]);
            case Function.FACT + 1:
                if (val[0].re % 1 != 0 || val[0].re < 0)
                    return new Result(1).setAnswer("阶乘函数的参数必须是自然数");
                return fact(val[0]);
            case Function.MAX + 2:
                return new Result(Complex.max(val[0], val[1]));
            case Function.MIN + 2:
                return new Result(Complex.min(val[0], val[1]));
            case Function.LG + 1:
                return new Result(Complex.log(val[0]));
            case Function.LOG + 2:
                return new Result(Complex.logab(val[0], val[1]));
            case Function.CBRT + 1:
                return new Result(Complex.cbrt(val[0]));


            case Function.EXP + 1:
                return new Result(Complex.exp(val[0]));
            case Function.LN + 1:
                return new Result(Complex.ln(val[0]));
            case Function.RE + 1:
                return new Result(new Complex(val[0].re));
            case Function.IM + 1:
                return new Result(new Complex(val[0].im));
            case Function.SQRT + 1:
                return new Result(Complex.sqrt(val[0]));
            case Function.ABS + 1:
                return new Result(val[0].abs());
            case Function.NORM + 1:
                return new Result(val[0].norm());
            case Function.ARG + 1:
                return new Result(val[0].arg());
            case Function.SIN + 1:
                return new Result(Complex.sin(val[0]));
            case Function.COS + 1:
                return new Result(Complex.cos(val[0]));
            case Function.TAN + 1:
                return new Result(Complex.tan(val[0]));
            case Function.ASIN + 1:
                return new Result(Complex.arcsin(val[0]));
            case Function.ACOS + 1:
                return new Result(Complex.arccos(val[0]));
            case Function.ATAN + 1:
                return new Result(Complex.arctan(val[0]));
            case Function.GAMMA + 1:
                return new Result(Complex.gamma(val[0]));
            case Function.FLOOR + 1:
                return new Result(new Complex(Math.floor(val[0].re), Math.floor(val[0].im)));
            case Function.CEIL + 1:
                return new Result(new Complex(Math.ceil(val[0].re), Math.ceil(val[0].im)));
            case Function.REG:
                return new Result(memValue);
            case Function.REG + 1:
                memValue = val[0];
                return new Result(val[0]);
            case Function.CONJ + 1:
                return new Result(new Complex(val[0].re, -val[0].im));
            case Function.RAND:
                return new Result(new Complex(Math.random(), Math.random()));
            case Function.RAND + 1:
                return new Result(new Complex(val[0].re * Math.random(), val[0].im * Math.random()));
            case Function.ROUND + 1:
                return new Result(new Complex(Math.round(val[0].re), Math.round(val[0].im)));
            case Function.ROUND + 2:
                double precRnd = Math.round(val[1].re);
                if (precRnd < 0)
                    return new Result(1).setAnswer("设置的精度过低");
                if (precRnd > 15)
                    return new Result(1).setAnswer("设置的精度过高");
                double ratio = Math.pow(10, precRnd);
                return new Result(new Complex(Math.round(val[0].re * ratio) / ratio, Math.round(val[0].im * ratio) / ratio));
            case Function.DIFF + 2:
                return diff(leftBr + 1, nextFS[leftBr] - 1, val[1]);
            case Function.DIFF + 3:
                return diff(leftBr + 1, nextFS[leftBr] - 1, val[1], val[2]);
            case Function.LIMIT + 2:
                return limit(leftBr + 1, nextFS[leftBr] - 1, val[1]);
            case Function.LIMIT + 3:
                return limit(leftBr + 1, nextFS[leftBr] - 1, val[1], val[2]);
            case Function.EVAL + 2:
                return value(leftBr + 1, nextFS[leftBr] - 1, val[1]);
            case Function.FZERO + 1:
                return solve(leftBr + 1, nextFS[leftBr] - 1, new Complex(-1 + Math.random() * 2, -1 + Math.random() * 2));
            case Function.FZERO + 2:
                return solve(leftBr + 1, nextFS[leftBr] - 1, val[1]);
            case Function.INTEG + 3:
                return integrate(leftBr + 1, nextFS[leftBr] - 1, val[1], val[2]);
            case Function.SUM + 3:
                return sum(leftBr + 1, nextFS[leftBr] - 1, val[1], val[2]);
            case Function.PERM + 2:
                return new Result(perm(val[0], val[1]));
            case Function.COMB + 2:
                return new Result(comb(val[0], val[1]));
            case Function.PREC:
                Result.setBase(Result.base);
                return new Result(0).setAnswer("精度设置为 " + Result.precision + " 位小数");
            case Function.PREC + 1:
                if (val[0].im != 0)
                    return new Result(3);
                int prec = (int) Math.round(val[0].re);
                if (prec < 0)
                    return new Result(1).setVal(new Complex(1)).setAnswer("精度过低");
                if (prec > Result.maxPrecision)
                    return new Result(1).setVal(new Complex(1))
                            .setAnswer("设置的精度过高，最大精度是 " + Result.maxPrecision + " 位小数");
                Result.precision = prec;
                return new Result(0).setAnswer("精度设置为 " + prec + " 位小数");
            case Function.BASE:
                Result.setBase(10);
                return new Result(0).setAnswer("输出进制被设置为 " + 10 + " 进制，" + "精度为 " + Result.precision + " 位小数");
            case Function.BASE + 1:
                if (val[0].im != 0)
                    return new Result(3);
                int base = (int) Math.round(val[0].re);
                if (!(base >= 2 && base <= 10 || base == 12 || base == 16))
                    return new Result(1).setVal(new Complex(1)).setAnswer("函数的参数无效");
                Result.setBase(base);
                return new Result(0).setAnswer("输出进制被设置为 " + base + " 进制，" + "精度为 " + Result.precision + " 位小数");
        }
        return new Result(1).setAnswer("函数 “" + Function.funcList[listPos].funcName + "” 参数错误");
    }

    public Result value() { // Entrance !
        isWorking = true;
        isIntegOverTolerance = false;
        isDiffOverTolerance = false;
        if (brDiff != 0) {
            return new Result(1).setAnswer("括号不匹配");
        }

        // Start working
        // 0+NaNi id a sign for "No Variable X provided" initially
        initCache();
        Result res = value(0, text.length() - 1, new Complex(0, Double.NaN));
        return res;
    }

    // 3 point diff()
    Result diff3(int l, int r, Complex x0, Complex delta) {
        Result rn = value(l, r, Complex.sub(x0, delta));
        if (rn.isFatalError()) return rn;
        if (!rn.val.isValid()) return new Result(-1);
        Result rp = value(l, r, Complex.add(x0, delta));
        if (rp.isFatalError()) return rp;

        Complex dv = Complex.div(Complex.sub(rp.val, rn.val), new Complex(delta.re * 2, delta.im * 2));
        return new Result(dv);
    }

    // 5 point diff()
    Result diff5(int l, int r, Complex x0, Complex delta) {
        Result r1 = diff3(l, r, x0, delta);
        if (r1.isFatalError()) return r1;
        if (!r1.val.isValid()) return new Result(-1);
        Result r2 = diff3(l, r, x0, new Complex(delta.re * 2, delta.im * 2));
        if (r2.isFatalError()) return r2;

        Complex dv = Complex.div(new Complex(r1.val.re * 4 - r2.val.re, r1.val.im * 4 - r2.val.im), new Complex(3));
        return new Result(dv);
    }

    // general diff()
    private boolean isDiffOverTolerance = false; // only checked for once, reduce data traffic

    Result diff(int l, int r, Complex x0) {

        final int sect = 8;
        final double sectAngle = Math.PI / sect;
        final double TOL = 1E-5;
        Complex[] dirDer = new Complex[sect];
        Complex dsum = new Complex(0);
        double dvar = 0;
        for (int i = 0; i < sect; i++) { // find directions in [0,pi)
            Complex delta = new Complex(Math.cos(i * sectAngle) * TOL, Math.sin(i * sectAngle) * TOL);
            Result rv = diff5(l, r, x0, delta);
            if (rv.isFatalError()) return rv;
            dirDer[i] = rv.val;
            dsum = Complex.add(dsum, rv.val);
        }
        dsum.re /= sect;
        dsum.im /= sect;
        for (int i = 0; i < sect; i++) {
            dvar += Complex.sub(dirDer[i], dsum).norm2();
        }
        Result res = new Result(dsum);
        if (!isDiffOverTolerance && dvar > TOL) {
            isDiffOverTolerance = true;
            res.append("某些情况下函数失效");
        }
        return res;
    }

    // directional diff
    Result diff(int l, int r, Complex x0, Complex dir) {
        if (dir.re == 0 && dir.im == 0 || !dir.isFinite())
            return new Result(1).setAnswer("无效的方向值");

        final double TOL = 1E-5;

        double norm = dir.norm().re;
        Complex delta = new Complex(dir.re / norm * TOL, dir.im / norm * TOL);
        Result rv = diff5(l, r, x0, delta);
        if (rv.isFatalError()) return rv;
        return new Result(rv.val);
    }

    // solve under condition
    Result solve(int l, int r, Complex x0, Complex M, int iter) {
        // Improved Secant Method, avoid second derivative
        Complex x1 = x0;
        Result res1 = value(l, r, x1);
        if (res1.isFatalError()) return res1;
        Complex v1 = res1.val;
        Complex r1 = Complex.div(v1, diff(l, r, x1).val);
        if (r1.isNaN()) {
            return new Result(1).setAnswer("无效的初始值");
        }
        // 1 step Newton Method Iteration
        Complex x2 = Complex.sub(x1, r1);
        Complex v2 = value(l, r, x2).val;
        if (r1.norm2() < 1E-20 && v2.norm2() < 1E-20) { // 1E-10 precision
            return new Result(x2);
        }

        Complex r2 = Complex.div(v2, diff(l, r, x2, r1).val); // use dir diff to speed up
        if (r2.isNaN()) {
            return new Result(1).setAnswer("无效的初始值");
        }

        Complex x3;
        Result root = new Result(0);
        List<Complex> histRes = new ArrayList<>();
        double minDe = 1E200;
        int minPos = -1;
        int overErrorRangeCount = 0;

        for (int i = 0; i <= iter; i++) { // normally no more than 20 iter., but for eq. such as x^.2, more is needed.

            if (!isWorking) return new Result(2);

            Complex d1 = Complex.mul(Complex.sub(x2, x1), r2);
            Complex d2 = Complex.sub(r2, r1);


            x3 = Complex.mul(Complex.sub(x2, Complex.div(d1, d2)), M); // Relaxation Param.

            Complex deltaX = Complex.sub(x2, x3);
            double deltaE = deltaX.norm2();

            histRes.add(x3);
            if (i > 0) {

                if (deltaE < minDe) {
                    minDe = deltaE;
                    minPos = i;
                    overErrorRangeCount = 0;
                } else {
                    overErrorRangeCount++;
                }

                if (!x3.isFinite() || overErrorRangeCount > 20) {
                    // return the result
                    Complex res = histRes.get(minPos);
                    if (minDe > 1E-20 || v2.norm2() > 1E-18) {
                        root.append("某些情况下函数失效");
                    } else {
                        root = new Result(res);
                    }
                    break;
                }

            }

            v2 = value(l, r, x3).val;
            x1 = x2;
            x2 = x3;
            r1 = r2;
            r2 = Complex.div(v2, diff(l, r, x3).val); // use dir diff to speed up
            if (r2.isNaN()) { // Math Error
                if (M.re == 1.0) { // 1.0 is a safely expressed double
                    root = new Result(1).setAnswer("无效的迭代器，某些情况下函数失效");
                }
                break;
            }
        }

        return root;
    }

    // general solve
    Result solve(int l, int r, Complex x0) { // auto solver
        Result rp;
        for (double M = 1.0; M > 0.05; M *= 0.7) {
            if (!isWorking) return new Result(2);

            rp = solve(l, r, x0, new Complex(M), (int) Math.round(1500 / Math.sqrt(M)));
            if (rp.isFatalError()) return rp;
            if (rp.val.isValid() && !rp.val.isNaN()) {
                return rp;
            }
            if (rp.getError() == -1) { // Initial Value Error
                break;
            }
            if (M == 1.0) {
                rp.append("尝试 Under-Relaxation 方法");
            }
        }

        return new Result(1).setAnswer("寻找函数零点 " + text.substring(l, r + 1) + " 失败");
    }

    // 15 nodes Gauss Quadrature
    private static final double[] gaussNodes15 = new double[]{ // G15 Nodes
            0.000000000000000000000000000000000e+00,
            2.011940939974345223006283033945962e-01,
            3.941513470775633698972073709810455e-01,
            5.709721726085388475372267372539106e-01,
            7.244177313601700474161860546139380e-01,
            8.482065834104272162006483207742169e-01,
            9.372733924007059043077589477102095e-01,
            9.879925180204854284895657185866126e-01
    };

    private static final double[] gaussWeights15 = new double[]{
            2.025782419255612728806201999675193e-01,
            1.984314853271115764561183264438393e-01,
            1.861610000155622110268005618664228e-01,
            1.662692058169939335532008604812088e-01,
            1.395706779261543144478047945110283e-01,
            1.071592204671719350118695466858693e-01,
            7.036604748810812470926741645066734e-02,
            3.075324199611726835462839357720442e-02
    };

    Complex gaussIntegrate15(int l, int r, Complex x0, Complex x2) { // G15
        Complex lenH = new Complex(x2.re - x0.re, x2.im - x0.im);
        Complex halfH = new Complex(lenH.re / 2, lenH.im / 2);

        Complex t0 = new Complex((x0.re + x2.re) / 2, (x0.im + x2.im) / 2);
        Complex[] tp = new Complex[7];
        Complex[] tn = new Complex[7];

        for (int i = 0; i < 7; i++) {
            tp[i] = new Complex(t0.re + gaussNodes15[i + 1] * halfH.re, t0.im + gaussNodes15[i + 1] * halfH.im);
            tn[i] = new Complex(t0.re - gaussNodes15[i + 1] * halfH.re, t0.im - gaussNodes15[i + 1] * halfH.im);
        }

        t0 = value(l, r, t0).val;
        Complex sum = new Complex(t0.re * gaussWeights15[0], t0.im * gaussWeights15[0]);
        for (int i = 0; i < 7; i++) {
            tp[i] = value(l, r, tp[i]).val;
            tn[i] = value(l, r, tn[i]).val;
            sum = Complex.add(sum, new Complex(
                    (tp[i].re + tn[i].re) * gaussWeights15[i + 1],
                    (tp[i].im + tn[i].im) * gaussWeights15[i + 1]
            ));
        }

        sum = Complex.mul(sum, halfH);

        return sum;
    }

    // integrate from x0 to x2, auto step-length
    private boolean isIntegOverTolerance = false; // only checked for once, reduce data traffic
    private static final double infIntegrateStepRatio = Math.E * Math.E;

    Result adaptiveIntegrate(int l, int r, Complex x0, Complex x2, Complex lastSum, double TOL, int depth) {

        if (!isWorking) return new Result(2);

        Complex x1;

        if (Double.isInfinite(x0.re)) {
            return new Result(1).setVal(new Complex(0));
        }

        if (Double.isInfinite(x2.re)) {
            double aRe = Math.abs(x0.re);
            double newRe;
            if (aRe <= 1E5) { // Avoid precision loss due to large number
                double logNextPoint = aRe * 2;
                newRe = (logNextPoint < 1 ? Math.exp(logNextPoint) : infIntegrateStepRatio * aRe);
            } else
                return new Result(-1).setVal(new Complex(0));

            //System.out.println("New Point: "+x0.toString()+" ~ "+x2.toString());
            if (x2.re > 0) { // POS Inf
                x1 = new Complex(newRe, x0.im);
            } else { // NEG Inf
                x1 = new Complex(-newRe, x0.im);
            }
        } else {
            x1 = new Complex((x0.re + x2.re) / 2, (x0.im + x2.im) / 2);
        }

        Complex sAB, sAC, sCB, sABnew, abbr;

        sAB = lastSum;
        sAC = gaussIntegrate15(l, r, x0, x1);
        sCB = gaussIntegrate15(l, r, x1, x2);
        sABnew = new Complex(0);
        if (sAC.isFinite()) sABnew = Complex.add(sABnew, sAC);
        if (sCB.isFinite()) sABnew = Complex.add(sABnew, sCB);
        abbr = Complex.sub(sAB, sABnew);

        if (abbr.isValid() && abbr.norm2() < 200 * TOL) { // Under precision limit
            return new Result(sABnew);
        }

        if (depth >= 20) { // Max iteration
            Result r1 = new Result(sABnew);
            if (!isIntegOverTolerance && abbr.norm().re > 1E3 * TOL) {
                isIntegOverTolerance = true;
                r1.append("某些情况下函数失效");
            }

            return r1;
        }

        sAC = adaptiveIntegrate(l, r, x0, x1, sAC, TOL / 4, depth + 1).val;
        sCB = adaptiveIntegrate(l, r, x1, x2, sCB, TOL / 4, depth + 1).val;
        sABnew = Complex.add(sAC, sCB);

        return new Result(sABnew);
    }

    // general quadrature
    Result integrate(int l, int r, Complex x0, Complex x2) {
        if (x0.isNaN()) {
            return new Result(1).setAnswer("无效的下界");
        }
        if (x2.isNaN()) {
            return new Result(1).setAnswer("无效的上界");
        }
        Result check = value(l, r, x0); // Check for syntax error. Better solution ?
        if (check.isFatalError()) return check;

        if (Double.isInfinite(x0.re)) {
            if (Double.isInfinite(x2.re)) { // Add middle point
                double iim = ((Complex.isDoubleFinite(x0.im) ? x0.im : 0) + (Complex.isDoubleFinite(x2.im) ? x2.im : 0)) / 2;

                Result r1 = integrate(l, r, x0, new Complex(0, iim));
                Result r2 = integrate(l, r, new Complex(0, iim), x2);
                return new Result(Complex.add(r1.val, r2.val));
            } else {
                Result r1 = integrate(l, r, x2, x0);
                return new Result(new Complex(-r1.val.re, -r1.val.im));
            }
        }

        double TOL = 1E-8; // the precision expected, 8 digits

        Complex sAB = gaussIntegrate15(l, r, x0, x2);
        return adaptiveIntegrate(l, r, x0, x2, sAB, TOL * TOL, 0);
    }

    // summation
    Result sum(int l, int r, Complex start, Complex end) {
        double ds = start.re;
        double de = end.re;
        boolean isInfiniteSummation = (Double.isInfinite(ds) || Double.isInfinite(de));

        if (de < ds) {
            return new Result(1).setAnswer("上界小于下界");
        }

        Complex sum = new Complex(0);
        Complex v = new Complex(0);
        final double TOL2 = 1E-16; // 8 digits expected
        final int maxBoundCnt = 1000;
        int boundCnt = 0;
        int cnt = 0;

        double ratio = (end.im - start.im) / (de - ds);
        if (!Complex.isDoubleFinite(ratio)) {
            return new Result(1).setAnswer("无法运算求和的路径");
        }
        for (v.re = ds; v.re <= de; v.re += 1, cnt++) {
            if (!isWorking) return new Result(2);

            v.im = (v.re - ds) * ratio + start.im;
            Result res = value(l, r, v);
            if (res.isFatalError()) {
                return res;
            }
            if (!res.val.isFinite()) {
                return new Result(sum).append("求和时发现错误 x=" + v.toString() + " ，求和可能不是有限的");
            }

            if (isInfiniteSummation) {
                if (res.val.norm2() < TOL2) {
                    boundCnt++;
                } else {
                    boundCnt = 0;
                }
                if (boundCnt > maxBoundCnt) {
                    break;
                }
            }

            sum = Complex.add(sum, res.val);
        }

        return new Result(sum);
    }

    // permutation & combination
    private Complex permIter(Complex n_, Complex m_) { // Gamma(n+1)/Gamma(m+1)
        Complex n, m;
        Complex ans = new Complex(1);
        n = n_;
        m = m_;

        while (true) { // adapted from iteration
            if (n.re > 1 && m.re > 1) {
                if (n.re - m.re >= 1) {
                    ans = Complex.mul(new Complex(n.re), ans);
                    n.re -= 1;
                } else if (m.re - n.re >= 1) {
                    ans = Complex.div(ans, new Complex(m.re));
                    m.re -= 1;
                } else {
                    ans = Complex.mul(new Complex(n.re / m.re), ans);
                    n.re -= 1;
                    m.re -= 1;
                }
            } else if (n.re == m.re && n.im == m.im) {
                break;
            } else {
                Complex af = Complex.div(Complex.gamma(new Complex(n.re + 1, n.im)), Complex.gamma(new Complex(m.re + 1, m.im)));
                ans = Complex.mul(af, ans);
                break;
            }
            if (!ans.isFinite()) { // invalid value occurred, no need to continue
                break;
            }
            if (!isWorking) return new Complex().error(2);
        }

        return ans;
    }

    public Complex perm(Complex n, Complex r) {
        return permIter(n, Complex.sub(n, r));
    }

    private Complex combIter(Complex n_, Complex m_) { // Gamma(n+1)/Gamma(m+1)/Gamma(n-m+1)

        Complex n, m;
        Complex ans = new Complex(1);
        n = n_;
        m = m_;

        while (true) { // adapted from iteration
            if (n.re > 1 && m.re > 1) {
                ans = Complex.mul(new Complex(n.re / m.re), ans);
                n.re -= 1;
                m.re -= 1;
            } else {
                Complex af = Complex.div(perm(n, m), Complex.gamma(new Complex(m.re + 1, m.im)));
                ans = Complex.mul(af, ans);
                break;
            }
            if (!ans.isFinite()) { // invalid value occurred, no need to continue
                break;
            }
            if (!isWorking) return new Complex().error(2);
        }

        return ans;
    }

    public Complex comb(Complex n, Complex r) {
        return combIter(n, r);
    }

    // limit (Lagrange interpolation)
    private static final Complex par2p = new Complex((1 + Math.sqrt(2)) / 4);
    private static final Complex par2n = new Complex((1 - Math.sqrt(2)) / 4);
    private static final Complex hRatio = new Complex(1 + Math.sqrt(2));

    private Result limitH(int l, int r, Complex x0, Complex h) {

        Complex f0;
        boolean finiteLimit = x0.isFinite();

        Complex x1, x2, x3, x4;

        if (finiteLimit) {
            x1 = Complex.add(x0, h);
            x2 = Complex.add(x0, Complex.mul(h, hRatio));
            x3 = Complex.sub(x0, h);
            x4 = Complex.sub(x0, Complex.mul(h, hRatio));
        } else {
            double norm2 = h.norm2();
            x1 = new Complex(h.re / norm2, h.im / norm2);
            x2 = new Complex(x1.re * 2, x1.im * 2);
            x3 = new Complex(x1.re * 3, x1.im * 3);
            x4 = new Complex(x1.re * 4, x1.im * 4);
        }

        Result r1 = value(l, r, x1);
        if (r1.isFatalError()) return r1;
        Result r2 = value(l, r, x2);
        if (r2.isFatalError()) return r2;
        Result r3 = value(l, r, x3);
        if (r3.isFatalError()) return r3;
        Result r4 = value(l, r, x4);
        if (r4.isFatalError()) return r4;

        Complex f1 = r1.val;
        Complex f2 = r2.val;
        Complex f3 = r3.val;
        Complex f4 = r4.val;

        if (finiteLimit) {
            Complex f13 = Complex.mul(Complex.add(f1, f3), par2p);
            Complex f24 = Complex.mul(Complex.add(f2, f4), par2n);
            f0 = Complex.add(f13, f24);
        } else {
            f0 = new Complex(
                    (-f1.re + 24 * f2.re - 81 * f3.re + 64 * f4.re) / 6,
                    (-f1.im + 24 * f2.im - 81 * f3.im + 64 * f4.im) / 6
            );
        }

        return new Result(f0);
    }

    // general limit
    public Result limit(int l, int r, Complex x0) {

        // Responsibility recharge
        if (Double.isInfinite(x0.re)) { // to real infinity
            return limit(l, r, x0, new Complex(x0.re > 0 ? 1 : -1));
        }
        if (Double.isInfinite(x0.im)) { // to imaginary infinity
            return limit(l, r, x0, new Complex(0, x0.im > 0 ? 1 : -1));
        }

        final int sect = 8;
        final double sectAngle = Math.PI / sect;
        Complex[] limitRes = new Complex[sect];
        Complex limitSum = new Complex(0);
        double limitVar = 0;
        int validSect = 0;

        for (int i = 0; i < sect; i++) { // find directions in [0,pi)

            List<Complex> histRes = new ArrayList<>();
            double minDe = 1E200;
            int minPos = -1;

            int cnt = 0;
            int overErrorRangeCount = 0;
            double h;
            for (h = 1E-1; h >= 1E-10; h *= 0.9, cnt++) {
                Complex delta = new Complex(Math.cos(i * sectAngle) * h, Math.sin(i * sectAngle) * h);
                Result resR = limitH(l, r, x0, delta);
                if (resR.isFatalError()) return new Result(1).setAnswer("未找到极限");
                Complex res = resR.val;

                if (cnt > 0) {
                    double e = Complex.sub(res, histRes.get(cnt - 1)).norm().re;

                    if (e < minDe) {
                        overErrorRangeCount = 0;
                        minDe = e;
                        minPos = cnt;
                    } else { // e >= minDe or e is NaN!
                        overErrorRangeCount++;
                    }
                    if (overErrorRangeCount > 20) {
                        break;
                    }
                }

                histRes.add(res);
            }

            if (minDe > 1E-5) { // didn't found ?
                new Result(-1).setAnswer("函数可能没有收敛");
            } else { // found
                Complex minRes = histRes.get(minPos - 1);
                limitSum = Complex.add(limitSum, minRes);
                limitRes[validSect] = minRes;
                validSect++;
            }
        }

        if (validSect == 0) return new Result(1).setAnswer("未找到极限");

        limitSum.re /= validSect;
        limitSum.im /= validSect;
        for (int i = 0; i < validSect; i++) {
            limitVar += Complex.sub(limitRes[i], limitSum).norm2();
        }

        Result res = new Result(limitSum);
        if (limitVar > 1E-5) {
            res.append("函数可能没有收敛到预期精度");
        }

        return res;
    }

    // directional limit
    public Result limit(int l, int r, Complex x0, Complex dir) {
        if (dir.re == 0 && dir.im == 0 || !dir.isFinite())
            return new Result(1).setAnswer("无效的方向值");


        List<Complex> histRes = new ArrayList<>();
        double minDe = 1E200;
        int minPos = -1;
        double norm = dir.norm().re;

        int cnt = 0;
        int overErrorRangeCount = 0;
        double h;
        for (h = 1E-1; h >= 1E-10; h *= 0.9, cnt++) {
            Complex delta = new Complex(dir.re / norm * h, dir.im / norm * h);
            Result resR = limitH(l, r, x0, delta);
            if (resR.isFatalError()) return new Result(1).setAnswer("未找到极限");
            Complex res = resR.val;

            if (cnt > 0) {
                double e = Complex.sub(res, histRes.get(cnt - 1)).norm().re;

                if (e < minDe) {
                    overErrorRangeCount = 0;
                    minDe = e;
                    minPos = cnt;
                } else { // e >= minDe or e is NaN!
                    overErrorRangeCount++;
                }
                if (overErrorRangeCount > 20) {
                    break;
                }

            }
            histRes.add(res);
        }

        if (minPos < 1) {
            return new Result(1).setAnswer("函数在给定点上可能没有收敛");
        }
        Complex minRes = histRes.get(minPos - 1);

        if (minDe > 1E-5) { // didn't found ?
            return new Result(1).setAnswer("函数在给定点上可能没有收敛");
        } else { // found
            return new Result(minRes);
        }
    }

    private Result gcd(Complex c, Complex c2) {
        if (c.im != 0 || c2.im != 0)
            return new Result(3);
        double x = c.re, y = c2.re;
        while (x != y) {
            if (!isWorking) return new Result(2);
            if (x > y) x = x - y;
            else y = y - x;
        }
        return new Result(new Complex(x));
    }

    private Result lcm(Complex c, Complex c2) {
        if (c.im != 0 || c2.im != 0)
            return new Result(3);
        double x = c.re, y = c2.re;
        Result gcd = gcd(c, c2);
        if (gcd.getError() == 2)
            return gcd;
        else
            return new Result(new Complex((x * y / gcd.val.re)));
    }

    private Complex isPrime(Complex c) {
        if (c.im != 0)
            return new Complex().error(3);
        double x = c.re;
        if (x == 1) return new Complex(false);

        for (int i = 2; i <= Math.sqrt(x); i++) {
            if (x % i == 0)
                return new Complex(false);
        }
        return new Complex(true);
    }

    private Result prime(Complex c) {
        if (c.im != 0)
            return new Result(3);
        double i = 2, j = 1, n = c.re;
        while (true) {
            if (!isWorking) return new Result(2);
            j = j + 1;
            if (j > i / j) {
                n--;
                if (n == 0)
                    break;
                j = 1;
            }
            if (i % j == 0) {
                i++;
                j = 1;
            }
        }
        return new Result(new Complex(i));
    }

    private void carry(int[] bit, int pos) {
        int i, carray = 0;
        for (i = 0; i <= pos; i++) {
            bit[i] += carray;
            if (bit[i] <= 9) {
                carray = 0;
            } else if (bit[i] > 9 && i < pos) {
                carray = bit[i] / 10;
                bit[i] = bit[i] % 10;
            } else if (bit[i] > 9 && i >= pos) {
                while (bit[i] > 9) {
                    carray = bit[i] / 10;
                    bit[i] = bit[i] % 10;
                    i++;
                    bit[i] = carray;
                }
            }
        }
    }

    private Result fact(Complex c) {
        if (c.im != 0)
            return new Result(3);
        int bigInteger = (int) c.re;
        int pos = 0;
        int digit;
        int a, b;
        double sum = 0;
        for (a = 1; a <= bigInteger; a++) {
            if (!isWorking) return new Result(2);
            sum += Math.log10(a);
        }
        digit = (int) sum + 1;

        int[] fact = new int[digit];
        fact[0] = 1;

        for (a = 2; a <= bigInteger; a++) {
            for (b = digit - 1; b >= 0; b--) {
                if (fact[b] != 0) {
                    pos = b;
                    break;
                }
            }

            for (b = 0; b <= pos; b++) {
                if (!isWorking) return new Result(2);
                fact[b] *= a;
            }
            carry(fact, pos);
        }

        for (b = digit - 1; b >= 0; b--) {
            if (fact[b] != 0) {
                pos = b;
                break;
            }
        }

        StringBuffer sb = new StringBuffer();
        for (a = pos; a >= 0; a--) {
            if (!isWorking) return new Result(2);
            sb.append(fact[a]);
        }
        return new Result(new Complex(sb.toString()));
    }

    public void stopEvaluation() {
        isWorking = false;
    }
}
