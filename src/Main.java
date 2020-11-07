package com.moti;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        BigInt num1 = getNumberFromUser();
        BigInt num2 = getNumberFromUser();

        try {
            // Print number relations
            System.out.printf("%s + %s = %s\n", num1, num2, num1.plus(num2));
            System.out.printf("%s - %s = %s\n", num1, num2, num1.minus(num2));
            System.out.printf("%s * %s = %s\n", num1, num2, num1.multiply(num2));

            try {
                System.out.printf("%s / %s = %s\n", num1, num2, num1.divide(num2));
            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (BorrowException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static BigInt getNumberFromUser() {
        Scanner stdin = new Scanner(System.in);
        BigInt bigNum = new BigInt("0");

        boolean isNumberInvalid = false;
        // Get the number from the user until he entered valid number
        do {
            // Get string number from user
            System.out.print("Enter number: ");
            String num = stdin.nextLine();
            try {
                // Try to initialize the big integer
                bigNum = new BigInt(num);
                isNumberInvalid = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                isNumberInvalid = true;
            }
        } while(isNumberInvalid);

        return bigNum;
    }
}
