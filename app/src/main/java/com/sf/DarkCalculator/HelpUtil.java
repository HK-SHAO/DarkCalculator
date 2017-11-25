package com.sf.DarkCalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2017/8/19.
 */

public class HelpUtil {

    private static Map<String, String> map = null;

    private static void initMap() {
        map = new HashMap<>();
        map.put("sqrt", "sqrt函数返回输入参数的平方根，需要1个参数");
        map.put("cbrt", "cbrt函数返回输入参数的立方根，需要1个参数");
        map.put("root", "root函数返回输入参数的根，需要2个参数，第1个参数为所开方的数，第2个是开方的次数");
        map.put("rand", "rand函数返回一个随机复数，不需要参数，但也可以输入一个使其缩放的参数");
        map.put("randInt", "randInt函数返回一个输入参数指定范围的随机整数，需要2个参数以指定随机整数的范围");
        map.put("lg", "lg函数返回输入参数以10为底输入参数的对数，需要1个参数");
        map.put("ln", "ln函数返回输入参数以自然底数为底输入参数的对数，需要1个参数");
        map.put("log", "log函数返回输入参数的对数，需要2个参数，第1个参数为底数，第2个参数为真数");
        map.put("abs", "abs函数返回输入参数的绝对值，但不支持复数，建议使用norm函数，需要1个参数");
        map.put("min", "min函数返回两个输入参数中最小的参数，需要2个参数");
        map.put("max", "max函数返回两个输入参数中最大的参数，需要2个参数");
        map.put("fact", "fact函数返回输入参数的阶乘，支持大数运算，需要1个参数");
        map.put("sin", "sin函数返回输入参数的正弦，需要1个参数");
        map.put("cos", "cos函数返回输入参数的余弦，需要1个参数");
        map.put("tan", "tan函数返回输入参数的正切，需要1个参数");
        map.put("asin", "asin函数返回输入参数的反正弦，需要1个参数");
        map.put("acos", "acos函数返回输入参数的反余弦，需要1个参数");
        map.put("atan", "atan函数返回输入参数的反正切，需要1个参数");
        map.put("sum", "sum函数返回输入参数累加求和的值，需要3个参数，第1个参数为携带变量x的函数，第2个参数为累加的开始值，第3个参数为累加的结束值");
        map.put("re", "re函数返回输入参数的实部，需要1个参数");
        map.put("im", "im函数返回输入参数的虚部，需要1个参数");
        map.put("arg", "arg函数返回输入参数的辐角，需要1个参数");
        map.put("norm", "norm函数返回输入参数的模长，需要1个参数");
        map.put("reg", "reg函数返回输入参数自身，用于暂时的存储一个值，需要的时候使用reg即可返回寄存的值，需要1个参数");
        map.put("conj", "conj函数返回输入参数的共轭，需要1个参数");
        map.put("diff", "diff函数返回输入参数导函数的值，至少需要2个参数，第1个参数为携带变量x的函数，第2个参数为x的值，如果你需要设置求导方向，你也可以将其输入为第3个参数");
        map.put("lim", "lim函数返回给定函数在某一点或无穷处的极限，至少需要2个参数，你也可以输入3个参数，第1个参数为携带变量x的函数，第2个参数为变量x趋近的值，第3个为求极限的方向值");
        map.put("eval", "eval函数返回给定函数变量x为某个值时函数的值，需要2个参数，第1个参数为携带变量x的函数，第2个参数为变量x的值");
        map.put("fzero", "fzero函数返回给定函数值为0时变量x的值，至少需要1个参数，第1个参数为携带变量x的函数，如果计算时间太长或者计算失败，你可以尝试估算函数值为0时x的值然后输入为第2个参数作为寻找函数值为0时的初始值，这样可以增加运算速度和成功率");
        map.put("integ", "integ函数返回输入参数的定积分，需要3个参数，第1个参数为携带变量x的函数，第2个参数为积分的下限，第3个参数为积分的上限");
        map.put("exp", "exp函数返回以自然底数为底输入参数次幂的值，需要1个参数");
        map.put("gcd", "gcd函数返回输入的两个参数的最大公约数，需要2个参数");
        map.put("lcm", "lcm函数返回输入的两个参数的最小公倍数，需要2个参数");
        map.put("perm", "perm函数返回输入参数的排列数量，需要2个参数，第1个参数为互异元素的数量，第2个参数为取出的数量");
        map.put("comb", "comb函数返回输入参数的组合数量，需要2个参数，第1个参数为互异元素的数量，第2个参数为取出的数量");
        map.put("round", "round函数返回输入参数四舍五入的值，至少需要1个参数，也可以输入2个参数，第1个参数为需要四舍五入的值，第2个是需要保留的小数位");
        map.put("floor", "floor函数返回输入参数向下取整的值，需要1个参数");
        map.put("ceil", "ceil函数返回输入参数向上取整的值，需要1个参数");
        map.put("sign", "sign函数返回输入参数的正负性，若为正数返回1，若为0返回0，若为负数返回-1，需要1个参数");
        map.put("gamma", "gamma函数返回输入参数的欧拉第二积分，需要1个参数");
        map.put("remn", "remn函数返回输入参数的余数，需要2个参数，第1个参数为被除数，第2个参数为余数");
        map.put("prime", "prime函数返回第输入参数个的质数，需要1个参数");
        map.put("isPrime", "isPrime函数返回输入参数是否为质数，需要1个参数");
        map.put("prec", "prec函数用于设置输出的精度，需要1个参数，当没有给参数时将输出的精度设置为默认");
        map.put("base", "base函数用于设置输出的进制，需要1个参数，当没有给参数时将输出的进制设置为默认");
    }

    public static String getFunctionHelp(String key) {
        if (map == null) {
            initMap();
        }
        return map.get(key);
    }
}
