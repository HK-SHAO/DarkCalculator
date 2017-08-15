package com.sf.ExpressionHandler;

/**
 * Created by user on 2017/8/7.
 */

public class BigFactorial {

    public static void carry(int[] bit, int pos) {
        int i, carray = 0;
        for (i = 0; i <= pos; i++)//从0到pos逐位检查是否需要进位
        {
            bit[i] += carray;//累加进位
            if (bit[i] <= 9)     //小于9不进位
            {
                carray = 0;
            } else if (bit[i] > 9 && i < pos)//大于9，但不是最高位
            {
                carray = bit[i] / 10;//保存进位值
                bit[i] = bit[i] % 10;//得到该位的一位数
            } else if (bit[i] > 9 && i >= pos)//大于9，且是最高位
            {
                while (bit[i] > 9)//循环向前进位
                {
                    carray = bit[i] / 10;//计算进位值
                    bit[i] = bit[i] % 10;//当前的第一位数
                    i++;
                    bit[i] = carray;//在下一位保存进位值
                }
            }
        }
    }

    public static String calc(int bigInteger) {
        int pos = 0;//
        int digit;//数据长度
        int a, b;
        double sum = 0;//阶乘位数
        for (a = 1; a <= bigInteger; a++)//计算阶乘位数
        {
            sum += Math.log10(a);
        }
        digit = (int) sum + 1;//数据长度

        int[] fact = new int[digit];//初始化一个数组
        fact[0] = 1;//设个位为 1

        for (a = 2; a <= bigInteger; a++)//将2^bigInteger逐个与原来的积相乘
        {
            for (b = digit - 1; b >= 0; b--)//查找最高位{}
            {
                if (fact[b] != 0) {
                    pos = b;//记录最高位
                    break;
                }
            }

            for (b = 0; b <= pos; b++) {
                fact[b] *= a;//每一位与i乘
            }
            carry(fact, pos);
        }

        for (b = digit - 1; b >= 0; b--) {
            if (fact[b] != 0) {
                pos = b;//记录最高位
                break;
            }
        }

        StringBuffer sb = new StringBuffer();
        for (a = pos; a >= 0; a--)//输出计算结果
        {
            sb.append(fact[a]);
        }
        return sb.toString();
    }
}
