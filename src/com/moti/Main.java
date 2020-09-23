package com.moti;

import java.util.ArrayList;

public class Main {

    public static void foo(ArrayList<Character> a) {
        a.add('c');
    }

    public static void main(String[] args) {
        BigInt num1 = new BigInt("123");
        BigInt num2 = new BigInt("1111");
        BigInt num3 = new BigInt("9000");
        BigInt negative_num1 = new BigInt("-123");
        BigInt negative_num2 = new BigInt("-200");

        System.out.printf("%s - %s = %s\n", num2.toString(), num1.toString(), num2.minus(num1).toString());
        System.out.printf("%s - %s = %s\n", num1.toString(), num2.toString(), num1.minus(num2).toString());

        System.out.printf("%s - %s = %s\n", num3.toString(), num2.toString(), num3.minus(num2).toString());
        System.out.printf("%s - %s = %s\n", num2.toString(), num3.toString(), num2.minus(num3).toString());

        System.out.printf("%s + %s = %s\n", num2.toString(), num1.toString(), num2.plus(num1).toString());
        System.out.printf("%s + %s = %s\n", num1.toString(), num2.toString(), num1.plus(num2).toString());

        System.out.printf("%s + %s = %s\n", num2.toString(), num3.toString(), num2.plus(num3).toString());
        System.out.printf("%s + %s = %s\n", num3.toString(), num2.toString(), num3.plus(num2).toString());

        System.out.printf("%s + %s = %s\n", negative_num1.toString(), num3.toString(), negative_num1.plus(num3).toString());
        System.out.printf("%s + %s = %s\n", num3.toString(), negative_num1.toString(), num3.plus(negative_num1).toString());

        System.out.printf("%s * %s = %s\n", num2.toString(), num3.toString(), num2.multiply(num3).toString());
        System.out.printf("%s * %s = %s\n", num3.toString(), num2.toString(), num3.multiply(num2).toString());

        System.out.printf("%s * %s = %s\n", negative_num1.toString(), num3.toString(), negative_num1.multiply(num3).toString());
        System.out.printf("%s * %s = %s\n", num3.toString(), negative_num1.toString(), num3.multiply(negative_num1).toString());

        BigInt x = new BigInt("6");
        BigInt y = new BigInt("3");
        BigInt z = new BigInt("492");

        System.out.printf("%s / %s = %s\n", x.toString(), y.toString(), x.divide(y).toString());
        System.out.printf("%s / %s = %s\n", z.toString(), negative_num1.toString(), z.divide(negative_num1).toString());
    }
}
