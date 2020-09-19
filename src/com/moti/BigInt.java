package com.moti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BigInt implements Comparable{
    // The first index is the units , the second dozens and so on...
    ArrayList<Integer> _number;
    boolean _is_plus;

    public BigInt(String number) throws IllegalArgumentException {
        if (!_valid_number(number)) {
            throw new IllegalArgumentException("Invalid number");
        }

        char first_digit = number.charAt(0);
        _is_plus = (first_digit != '-');

        String only_digits = number;
        if (first_digit == '-' || first_digit == '+') {
            only_digits = number.substring(1);
        }

        for (char digit : only_digits.toCharArray()) {
            _number.add(0, Integer.parseInt(Character.toString(digit)));
        }
    }

    private BigInt(ArrayList<Integer> number, boolean is_plus) {
        _number = number;
        _is_plus = is_plus;
    }

    private boolean _valid_number(String number) {
        char first_digit = number.charAt(0);
        boolean is_first_digit_valid = first_digit == '+' || first_digit == '-' || Character.isDigit(first_digit);

        return is_first_digit_valid && number.substring(1).chars().allMatch(Character::isDigit);
    }

    private int _get_digit(int index) {
        return index < _number.size() ? _number.get(index) : 0;
    }

    public BigInt plus(BigInt rhs) {
        if (_is_plus && !rhs._is_plus) {
            // x-y
            return minus(new BigInt(rhs._number, true));
        } else if (!_is_plus && rhs._is_plus) {
            // -x+y=y-x
            return rhs.minus(new BigInt(_number, true));
        } else if (!_is_plus && !rhs._is_plus) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);
            return new BigInt(num1.plus(num2)._number, false);
        }

        // x+y
        int length = Math.max(_number.size(), rhs._number.size());

        int digit_sum = 0;
        ArrayList<Integer> sum = new ArrayList<Integer>();
        for (int i = 0; i < length; ++i) {
            digit_sum += _get_digit(i) + rhs._get_digit(i);

            sum.add(0, digit_sum % 10);
            digit_sum = (int)Math.floor(digit_sum / 10.0);
        }

        if (0 != digit_sum) {
            sum.add(0, 1);
        }

        return new BigInt(sum, true);
    }

    public BigInt minus(BigInt rhs) {
        // -x-y=-(x+y)
        if (!_is_plus && rhs._is_plus) {
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);
            return new BigInt(num1.minus(num2)._number, false);
        } else if (_is_plus && !rhs._is_plus) {
            // x-(-y)=x+y
            return plus(new BigInt(rhs._number, true));
        } else if (!_is_plus && !rhs._is_plus) {
            // -x-(-y)=y-x
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);
            return num2.minus(num1);
        }

        // x-y
        int length = Math.max(_number.size(), rhs._number.size());

        ArrayList<Integer> diff = new ArrayList<Integer>();
        int digit_diff = 0;
        for (int i = 0; i < length; ++i) {
            int subtracting_current_digit = _get_digit(i);
            int subtracted_current_digit = rhs._get_digit(i);

            digit_diff += subtracting_current_digit - subtracted_current_digit;
            if (digit_diff < 0) {
                diff.add(0, digit_diff + 10);
                digit_diff = -1;
            } else {
                diff.add(0, digit_diff % 10);
                digit_diff = (int) Math.floor(digit_diff / 10.0);
            }
        }

        return new BigInt(diff, digit_diff < 0);
    }

    public BigInt multiply(BigInt rhs) {

    }

    public BigInt divide(BigInt rhs) {

    }

    @Override
    public String toString() {
        ArrayList<Character> num = new ArrayList<>();
        num = (ArrayList<Character>)_number.clone();
        Collections.reverse(num);

        return num.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            // Same instance passed as parameter
            return true;
        }

        if (!(other instanceof BigInt)) {
            // other is not a BigInt
            return false;
        }

        BigInt num = (BigInt)other;

        return this.minus(num)._number.toString().equals("0");
    }

    @Override
    public int compareTo(Object other) throws IllegalArgumentException {
        if (this == other) {
            // Same instance passed as parameter
            return 0;
        }

        if (!(other instanceof BigInt)) {
            // other is not a BigInt
            throw new IllegalArgumentException();
        }

        BigInt num = (BigInt)other;

        if (this.minus(num)._number.get(0).equals('-')) {
            return -1;
        }

        return this.equals(num) ? 0 : 1;
    }
}
