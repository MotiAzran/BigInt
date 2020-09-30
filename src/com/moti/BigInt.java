package com.moti;

import java.util.ArrayList;

public class BigInt implements Comparable{
    // The first index is the units , the second dozens and so on...
    ArrayList<Integer> _number;
    boolean _is_positive;

    public BigInt(String number) throws IllegalArgumentException {
        if (!_valid_number(number)) {
            throw new IllegalArgumentException("Invalid number");
        }

        // Check if the first digit is '-'
        char first_digit = number.charAt(0);
        _is_positive = (first_digit != '-');

        // Save only the digits of the number
        String only_digits = number;
        if (first_digit == '-' || first_digit == '+') {
            only_digits = number.substring(1);
        }

        _number = new ArrayList<Integer>();
        for (char digit : only_digits.toCharArray()) {
            _number.add(0, Character.getNumericValue(digit));
        }

        _remove_leading_zores();
    }

    private BigInt(ArrayList<Integer> number, boolean is_plus) {
        _number = number;
        _is_positive = is_plus;

        _remove_leading_zores();
    }

    private boolean _valid_number(String number) {
        // Validate the first character is a sign or digit
        char first_digit = number.charAt(0);
        boolean is_first_digit_valid = first_digit == '+' || first_digit == '-' || Character.isDigit(first_digit);

        return is_first_digit_valid && number.substring(1).chars().allMatch(Character::isDigit);
    }

    private void _remove_leading_zores() {
        // Iterate the number until that appears a number greater than 0
        for (int i = _number.size() - 1; i >= 0; --i) {
            if (_number.get(i) == 0) {
                _number.remove(i);
            } else {
                break;
            }
        }

        // If the list is empty so the number is zero
        if (_number.isEmpty()) {
            _number.add(0);
            _is_positive = true;
        }
    }

    private int _get_digit(int index) {
        return _get_digit(_number, index);
    }

    private static int _get_digit(ArrayList<Integer> num, int index) {
        return index < num.size() ? num.get(index) : 0;
    }

    private static void _borrow(ArrayList<Integer> num, int index) {
        int i = 0;

        for (i = index + 1; i < num.size(); ++i) {
            if (num.get(i) != 0) {
                // A number we can borrow from
                num.set(i, num.get(i) - 1);
                break;
            }
        }

        --i;
        while (i > index) {
            // Set to 9 because all that digits borrowing to the digit after them
            num.set(i, 9);
            --i;
        }
    }

    private boolean _is_greater(BigInt rhs) {
        if (_number.size() > rhs._number.size()) {
            // The number is longer than the other so is bigger
            return true;
        } else if (_number.size() < rhs._number.size()) {
            // The number is shorter than the other so is smaller
            return false;
        }

        for (int i = _number.size() - 1; i >= 0; --i) {
            if (_number.get(i) > rhs._number.get(i)) {
                return true;
            } else if (_number.get(i) < rhs._number.get(i)) {
                return false;
            }
        }

        return false;
    }

    private void _swap_sign() {
        _is_positive = !_is_positive;
    }

    public BigInt plus(BigInt rhs) {
        if (_is_positive && !rhs._is_positive) {
            // x+(-y)=x-y
            return minus(new BigInt(rhs._number, true));
        } else if (!_is_positive && rhs._is_positive) {
            // -x+y=y-x
            return rhs.minus(new BigInt(_number, true));
        } else if (!_is_positive && !rhs._is_positive) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);

            BigInt result = num1.plus(num2);
            result._swap_sign();

            return result;
        }

        // x+y
        int length = Math.max(_number.size(), rhs._number.size());

        int digit_sum = 0;
        ArrayList<Integer> sum = new ArrayList<Integer>();
        for (int i = 0; i < length; ++i) {
            // Calculate the digits sum including the carry from the last sum
            digit_sum += _get_digit(i) + rhs._get_digit(i);

            sum.add(digit_sum % 10);
            digit_sum = Math.floorDiv(digit_sum, 10);
        }

        // We got a carry at the end so we need to add another '1' digit
        if (0 != digit_sum) {
            sum.add(1);
        }

        return new BigInt(sum, true);
    }

    public BigInt minus(BigInt rhs) {
        if (!_is_positive && rhs._is_positive) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);

            BigInt result = num1.minus(num2);
            result._swap_sign();

            return result;
        } else if (_is_positive && !rhs._is_positive) {
            // x-(-y)=x+y
            return plus(new BigInt(rhs._number, true));
        } else if (!_is_positive && !rhs._is_positive) {
            // -x-(-y)=y-x
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);

            return num2.minus(num1);
        }

        // x-y
        int length = Math.max(_number.size(), rhs._number.size());

        // Set the bigger and the smaller number
        ArrayList<Integer> bigger, smaller;
        boolean is_plus;
        if (_is_greater(rhs)) {
            bigger = new ArrayList<Integer>(_number);
            smaller = new ArrayList<Integer>(rhs._number);
            is_plus = true;
        } else {
            bigger = new ArrayList<Integer>(rhs._number);
            smaller = new ArrayList<Integer>(_number);
            is_plus = false;
        }

        ArrayList<Integer> diff = new ArrayList<Integer>();
        int digit_diff = 0;
        for (int i = 0; i < length; ++i) {
            int subtracting_current_digit = _get_digit(bigger, i);
            int subtracted_current_digit = _get_digit(smaller, i);

            // subtract the digits
            digit_diff = subtracting_current_digit - subtracted_current_digit;
            if (0 > digit_diff) {
                // The subtracted is bigger so we need to borrow
                _borrow(bigger, i);
                digit_diff += 10;
            }

            diff.add(digit_diff);
        }

        return new BigInt(diff, is_plus);
    }

    public BigInt multiply(BigInt rhs) {
        BigInt product = new BigInt(new ArrayList<Integer>(), true);
        ArrayList<Integer> digit_product = new ArrayList<>();
        for (int i = 0; i < _number.size(); ++i) {
            // Add zero's to the digit product
            for (int j = 0; j < i; ++j) {
                digit_product.add(0);
            }

            // calculate the digit product
            int prod = 0;
            for (int j = 0; j < rhs._number.size(); ++j) {
                prod += (_number.get(i) * rhs._number.get(j));
                digit_product.add(prod % 10);
                prod = Math.floorDiv(prod, 10);
            }

            if (prod != 0) {
                digit_product.add(prod);
            }

            // Sum the digit product to the number product
            product = product.plus(new BigInt(digit_product, true));
            digit_product.clear();
        }

        product._is_positive = (_is_positive == rhs._is_positive);

        return product;
    }

    public BigInt divide(BigInt rhs) throws ArithmeticException {
        if (rhs.equals(new BigInt("0")))
        {
            throw new ArithmeticException("Zero division");
        }

        BigInt abs_divider = new BigInt(_number, true);

        BigInt quotient = new BigInt(new ArrayList<Integer>(), true);
        BigInt orig_divisor = new BigInt(rhs._number, true);
        BigInt divisor = new BigInt(rhs._number, true);
        // Run until the divisor is bigger or equal to the divider
        while (0 >= divisor.compareTo(abs_divider)) {
            // Increase the quotient by 1
            quotient = quotient.plus(new BigInt("1"));
            // Increase the divisor
            divisor = divisor.plus(orig_divisor);
        }

        quotient._is_positive = (_is_positive == rhs._is_positive);

        return quotient;
    }

    @Override
    public String toString() {
        String num = new String();
        if (!_is_positive)
        {
            // Add '-' if the number is negative
            num = num.concat("-");
        }

        // Add all the digits in reverse order
        for (int i = _number.size() - 1; i >= 0; --i)
        {
            num = num.concat(_number.get(i).toString());
        }

        return num;
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
        BigInt diff = this.minus(num);

        // If the difference has more than one digit it not 0
        if (diff._number.size() > 1) {
            return false;
        }

        // Return if this - other == 0
        return diff._number.get(0) == 0;
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

        // Calculate number difference
        BigInt diff = minus(num);

        // If the difference is 0 the numbers are equals
        if (diff._number.size() == 1 && diff._number.get(0) == 0) {
            return 0;
        }

        return diff._is_positive ? 1 : -1;
    }
}
