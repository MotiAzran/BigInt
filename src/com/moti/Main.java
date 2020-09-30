package com.moti;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        BigInt num1 = get_number_from_user();
        BigInt num2 = get_number_from_user();

        System.out.printf("%s + %s = %s\n", num1, num2, num1.plus(num2));
        System.out.printf("%s - %s = %s\n", num1, num2, num1.minus(num2));
        System.out.printf("%s * %s = %s\n", num1, num2, num1.multiply(num2));

        try {
            System.out.printf("%s / %s = %s\n", num1, num2, num1.divide(num2));
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static BigInt get_number_from_user() {
        Scanner stdin = new Scanner(System.in);
        BigInt big_num = new BigInt("0");

        boolean is_number_invalid = false;
        do {
            System.out.print("Enter number: ");
            String num = stdin.nextLine();
            try {
                big_num = new BigInt(num);
                is_number_invalid = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                is_number_invalid = true;
            }
        } while(is_number_invalid);

        return big_num;
    }
}
