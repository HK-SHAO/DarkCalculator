package com.sf.ExpressionHandler;

import android.text.TextUtils;

public class Complex {
    public static Complex E = new Complex(Math.E);
    public static Complex PI = new Complex(Math.PI);
    public static Complex I = new Complex(0, 1);
    public static Complex Inf = new Complex(Double.POSITIVE_INFINITY);

    public int err = 0;
    public double re;
    public double im;

    private String answer = "";

    public Complex(double re_, double im_) {
        re = re_;
        im = im_;
    }

    public Complex(double re_) {
        re = re_;
        im = 0;
    }

    public Complex(String answer_) {
        answer = answer_;
        try {
            re = Double.parseDouble(answer);
        } catch (Exception e) {
            re = Double.NaN;
        }
        im = 0;
    }

    public Complex(String answer_, Complex c) {
        answer = answer_;
        re = c.re;
        im = c.im;
    }

    public Complex(boolean b) {
        re = b ? 1 : 0;
        im = 0;
        answer = b ? "true" : "false";
    }

    public Complex() {
        re = Double.NaN;
        im = Double.NaN;
    }

    public Complex error(int err) {
        this.err = err;
        return this;
    }

    public void setAnswer(String str) {
        this.answer = str;
    }

    public static Complex add(Complex a, Complex b) {
        return new Complex(a.re + b.re, a.im + b.im);
    }

    public static Complex sub(Complex a, Complex b) {
        return new Complex(a.re - b.re, a.im - b.im);
    }

    public static Complex inv(Complex a) {
        return new Complex(-a.re, -a.im);
    }

    public static Complex mul(Complex a, Complex b) {
        return new Complex(
                a.re * b.re - a.im * b.im,
                a.re * b.im + a.im * b.re
        );
    }

    public Complex abs() {
        if (im != 0)
            return new Complex().error(3);
        return new Complex(Math.abs(re));
    }

    public double norm2() {
        if (Double.isInfinite(re) || Double.isInfinite(im))
            return Double.POSITIVE_INFINITY;
        return re * re + im * im;
    }

    public Complex norm() {
        return new Complex(Math.hypot(re, im));
    }

    public Complex arg() {
        // deal with the difference between 0 and -0
        // but better solution shall be discussed
        if (im == 0) {
            im = 0; // -0 -> 0
            if (re == 0) // the arg of 0 is not determined
                return new Complex(Double.NaN);
        }
        return new Complex(Math.atan2(im, re));
    }

    public boolean isNaN() {
        return Double.isNaN(re); // im is not cared
    }

    public boolean isValid() { // finite complex or Complex Infinity
        return !(isDoubleFinite(re) && Double.isNaN(im));
    }

    public static boolean isDoubleFinite(double d) {
        return !(Double.isNaN(d) || Double.isInfinite(d));
    }

    public boolean isFinite() {
        return Complex.isDoubleFinite(re) && Complex.isDoubleFinite(im);
    }

    public static Complex div(Complex a, Complex b) {
        double aNorm = a.norm().re;
        double bNorm = b.norm().re;
        if (aNorm > 0 && bNorm == 0) return Inf; // pInf==nInf in complex field?
        if (Double.isInfinite(bNorm) && Complex.isDoubleFinite(aNorm)) return new Complex(0);
        double ure = b.re / bNorm; // prevent overflow on a.re*b.re
        double uim = b.im / bNorm;
        double re = (a.re * ure + a.im * uim) / bNorm; // prevent overflow on bnorm^2
        double im = (a.im * ure - a.re * uim) / bNorm;
        return new Complex(re, im);
    }

    public static Complex pow(Complex a, Complex b) {
        if (a.re == 0 && a.im == 0) { // special treatment for 0
            if (b.re > 0) return new Complex(0);
            else if (b.re < 0 && b.im == 0) return Complex.Inf;
            else return new Complex();
        }
        if (a.norm().re < 1 && b.re == Double.POSITIVE_INFINITY) { // special treatment for inf
            return new Complex(0);
        }
        if (a.norm().re > 1 && b.re == Double.NEGATIVE_INFINITY) { // special treatment for -inf
            return new Complex(0);
        }

        return Complex.exp(Complex.mul(b, Complex.ln(a)));
    }

    private static String doubleToString(double d) {
        if (Double.isNaN(d)) {
            return "nan";
        }
        if (Double.isInfinite(d)) {
            return d > 0 ? "∞" : "-∞";
        }

        if (Result.base == 10 && Result.precision == Result.maxPrecision) {
            return Double.toString(d);
        }

        return ParseNumber.toBaseString(d, Result.base, Result.precision);
    }

    public String toString() {
        if (!TextUtils.isEmpty(answer))
            return answer;
        double threshold = (Result.precision < Result.maxPrecision ? Math.pow(Result.base, -Result.precision) : 0);
        if (Double.isNaN(im) && Double.isInfinite(re)) {
            answer = (re > 0 ? "∞" : "-∞");
        } else if (Math.abs(re) > threshold || Double.isNaN(re)) { // re to be shown.
            answer += doubleToString(re);

            if (isDoubleFinite(im)) {
                if (Math.abs(im) > threshold) {
                    answer += (im > 0 ? "+" : "-");
                    if (Math.abs(Math.abs(im) - 1) > threshold) {
                        answer += doubleToString(Math.abs(im));
                    }
                    answer += "i";
                }
            } else { // inf or nan
                answer += (im < 0 ? "" : "+"); // +inf/nan -> +
                answer += doubleToString(im) + "*i";
            }
        } else {
            if (isDoubleFinite(im)) {
                if (Math.abs(im) > threshold) {
                    answer += (im > 0 ? "" : "-");
                    if (Math.abs(Math.abs(im) - 1) > threshold) {
                        answer += doubleToString(Math.abs(im));
                    }
                    answer += "i";
                } else { // Nothing
                    answer += "0";
                }
            } else { // inf nan
                answer += doubleToString(im) + "*i";
            }
        }
        return answer;
    }

    //======================= Functions ============================

    public static Complex logab(Complex c, Complex c2) {
        return div(ln(c2), ln(c));
    }

    public static Complex max(Complex c, Complex c2) {
        if (c.im != 0 || c2.im != 0)
            return new Complex().error(3);
        return new Complex(Math.max(c.re, c2.re));
    }

    public static Complex min(Complex c, Complex c2) {
        if (c.im != 0 || c2.im != 0)
            return new Complex().error(3);
        return new Complex(Math.min(c.re, c2.re));
    }

    public static Complex ln(Complex c) {
        return new Complex(Math.log(c.norm().re), c.arg().re);
    }

    public static Complex exp(Complex c) {
        if (c.re == Double.NEGATIVE_INFINITY)
            return new Complex(0);
        double norm = Math.exp(c.re);
        return new Complex(norm * Math.cos(c.im), norm * Math.sin(c.im));
    }

    public static Complex log(Complex c) {
        return div(ln(c), ln(new Complex(10)));
    }

    public static Complex sqrt(Complex c) {
        double norm = c.norm().re;
        if (norm == 0) return new Complex(0);
        double cosArg = c.re / norm; // invalid for 0
        double sind2 = Math.sqrt((1 - cosArg) / 2);
        double cosd2 = Math.sqrt((1 + cosArg) / 2);
        if (c.im < 0) sind2 = -sind2;
        norm = Math.sqrt(norm);
        return new Complex(norm * cosd2, norm * sind2);
    }

    public static Complex cbrt(Complex c) {
        return pow(c, div(new Complex(1), new Complex(3)));
    }

    public static Complex sin(Complex c) {
        double eip = Math.exp(c.im);
        double ein = Math.exp(-c.im);
        return new Complex((eip + ein) * Math.sin(c.re) / 2, (eip - ein) * Math.cos(c.re) / 2);
    }

    public static Complex cos(Complex c) {
        double eip = Math.exp(c.im);
        double ein = Math.exp(-c.im);
        return new Complex((eip + ein) * Math.cos(c.re) / 2, (ein - eip) * Math.sin(c.re) / 2);
    }

    public static Complex tan(Complex c) {
        //return Complex.div(Complex.sin(c),Complex.cos(c)); // not precise enough
        double re2 = c.re * 2;
        double im2 = c.im * 2;

        double eip2 = Math.exp(im2);
        double ein2 = Math.exp(-im2);
        double sinhi2 = (eip2 - ein2) / 2;
        double coshi2 = (eip2 + ein2) / 2;

        if (Double.isInfinite(coshi2)) { // Special case
            return new Complex(0, c.im > 0 ? 1 : -1);
        }

        double ratio = Math.cos(re2) + coshi2;
        double resRe = Math.sin(re2) / ratio;
        double resIm = sinhi2 / ratio;
        return new Complex(resRe, resIm);
    }

    public static Complex arcsin(Complex c) {
        Complex v = Complex.add(Complex.mul(c, I), Complex.sqrt(Complex.sub(new Complex(1), Complex.mul(c, c))));
        return Complex.mul(new Complex(0, -1), Complex.ln(v));
    }

    public static Complex arccos(Complex c) {
        Complex v = Complex.add(c, Complex.sqrt(Complex.sub(Complex.mul(c, c), new Complex(1))));
        return Complex.mul(new Complex(0, -1), Complex.ln(v));
    }

    public static Complex arctan(Complex c) {
        if (c.re == Double.POSITIVE_INFINITY) return new Complex(Math.PI / 2);
        if (c.re == Double.NEGATIVE_INFINITY) return new Complex(Math.PI / 2);

        Complex c1 = new Complex(1 - c.im, c.re);
        Complex c2 = new Complex(1 + c.im, -c.re);
        double re_ = (c1.arg().re - c2.arg().re) / 2;
        double im_ = (Math.log(c2.norm().re) - Math.log(c1.norm().re)) / 2;
        return new Complex(re_, im_);
    }

    private static double[] gammaP = { // constants for Lanczos approximation
            676.5203681218851, -1259.1392167224028, 771.32342877765313,
            -176.61502916214059, 12.507343278686905, -0.13857109526572012,
            9.9843695780195716E-6, 1.5056327351493116E-7
    };
    private static double[] gammaT = { // constants for Taylor approximation
            -0.57721566490153286, 0.9890559953279725, 0.9074790760808862,
            0.9817280868344002, 0.9819950689031453, 0.9931491146212761
    };

    public static Complex gamma(Complex c) { // Lanczos approximation + Taylor series

        if (c.re == Double.POSITIVE_INFINITY && c.im == 0) return Complex.Inf;
        //if(c.re==Double.NEGATIVE_INFINITY)return new Complex();

        Complex result;

        if (c.re < -310) { // guarantee result in double field
            if (c.re == Double.NEGATIVE_INFINITY) {
                if (c.im == 0)
                    result = new Complex();
                else
                    result = new Complex(0);

            } else if (c.re == Math.floor(c.re) && c.im == 0) {
                result = Complex.Inf;
            } else {
                result = new Complex(0);
            }
        } else if (c.re < -0.5) { // negative x complex plane
            int k = (int) Math.floor(-c.re) + 1;
            result = Complex.gamma(new Complex(c.re + k, c.im));
            for (int i = k - 1; i >= 0; i--) { // reversed order, prevent 0/0 -> NaN
                if (!result.isFinite()) break;
                result = Complex.div(result, new Complex(c.re + i, c.im));
            }
        } else if (c.re > 142) { // big numbers
            double kd = Math.ceil(c.re - 142);
            long k = (long) kd;
            result = Complex.gamma(new Complex(c.re - kd, c.im));
            if (result.re != 0 || result.im != 0) {
                for (long i = 1; i <= k; i++) {
                    if (!result.isFinite()) break;
                    result = Complex.mul(result, new Complex(c.re - i, c.im));
                }
            }
        } else if (Math.abs(c.re) < 1E-3 && Math.abs(c.im) < 1E-2) { // Taylor series, deal with value REALLY near the pole 0
            result = new Complex(0);
            for (int i = gammaT.length - 1; i >= 0; i--) {
                result = Complex.mul(result, c);
                result = new Complex(result.re + gammaT[i], result.im);
            }
            result = Complex.add(result, Complex.div(new Complex(1), c));
        } else if (c.re < 0.5 && Math.abs(c.im) <= 220) { // Reflection formula(more precise), deal with value near the pole 0
            Complex sZ = Complex.sin(Complex.mul(Complex.PI, c));
            Complex gZ = Complex.gamma(Complex.sub(new Complex(1), c));
            //Log.i("Gamma","sZ="+sZ+" gZ="+gZ);
            result = Complex.div(Complex.PI, Complex.mul(sZ, gZ));
        } else {
            Complex z = new Complex(c.re - 1, c.im);
            Complex x = new Complex(0.99999999999980993);

            for (int i = 0; i < gammaP.length; i++) {
                Complex dn = new Complex(z.re + i + 1, z.im);
                x = Complex.add(x, Complex.div(new Complex(gammaP[i]), dn));
            }

            Complex t = new Complex(z.re + gammaP.length - 0.5, z.im);
            result = Complex.exp(Complex.mul(new Complex(z.re + 0.5, z.im), Complex.ln(t)));
            result = Complex.mul(new Complex(Math.sqrt(2 * Math.PI)), result);
            result = Complex.mul(Complex.exp(Complex.inv(t)), result);
            result = Complex.mul(result, x);
        }
        if (Double.isInfinite(result.re) && !Complex.isDoubleFinite(result.im))
            result.im = Double.NaN;
        return result;
    }


}
