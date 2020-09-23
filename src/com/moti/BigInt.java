package com.moti;

import java.util.ArrayList;

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

        _number = new ArrayList<Integer>();
        for (char digit : only_digits.toCharArray()) {
            _number.add(0, Character.getNumericValue(digit));
        }

        _remove_leading_zores();

        if (_number.get(0) == 0) {
            _is_plus = true;
        }
    }

    private BigInt(ArrayList<Integer> number, boolean is_plus) {
        _number = number;
        _is_plus = is_plus;

        _remove_leading_zores();

        if (_number.get(0) == 0) {
            _is_plus = true;
        }
    }

    private boolean _valid_number(String number) {
        char first_digit = number.charAt(0);
        boolean is_first_digit_valid = first_digit == '+' || first_digit == '-' || Character.isDigit(first_digit);

        return is_first_digit_valid && number.substring(1).chars().allMatch(Character::isDigit);
    }

    private int _get_digit(int index) {
        return index < _number.size() ? _number.get(index) : 0;
    }

    private static int _get_digit(ArrayList<Integer> num, int index) {
        return index < num.size() ? num.get(index) : 0;
    }

    private static void _borrow(ArrayList<Integer> num, int index) {
        int i = 0;

        for (i = index + 1; i < num.size(); ++i) {
            if (num.get(i) != 0) {
                num.set(i, num.get(i) - 1);
                break;
            }
        }

        --i;
        while (i > index) {
            num.set(i, 9);
            --i;
        }
    }

    private void _remove_leading_zores() {
        for (int i = _number.size() - 1; i >= 0; --i) {
            if (_number.get(i) == 0) {
                _number.remove(i);
            } else {
                break;
            }
        }

        if (_number.isEmpty()) {
            _number.add(0);
            _is_plus = true;
        }
    }

    private boolean _is_greater(BigInt rhs) {
        if (_number.size() > rhs._number.size()) {
            return true;
        } else if (_number.size() < rhs._number.size()) {
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
        _is_plus = !_is_plus;
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

            BigInt result = num1.plus(num2);
            result._swap_sign();

            return result;
        }

        // x+y
        int length = Math.max(_number.size(), rhs._number.size());

        int digit_sum = 0;
        ArrayList<Integer> sum = new ArrayList<Integer>();
        for (int i = 0; i < length; ++i) {
            digit_sum += _get_digit(i) + rhs._get_digit(i);

            sum.add(digit_sum % 10);
            digit_sum = Math.floorDiv(digit_sum, 10);
        }

        if (0 != digit_sum) {
            sum.add(1);
        }

        return new BigInt(sum, true);
    }

    public BigInt minus(BigInt rhs) {
        if (!_is_plus && rhs._is_plus) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);

            BigInt result = num1.minus(num2);
            result._swap_sign();

            return result;
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

        ArrayList<Integer> bigger, smaller;
        boolean is_plus;
        if (_is_greater(rhs)) {
            bigger = (ArrayList<Integer>)_number.clone();
            smaller = (ArrayList<Integer>)rhs._number.clone();
            is_plus = true;
        } else {
            bigger = (ArrayList<Integer>)rhs._number.clone();
            smaller = (ArrayList<Integer>)_number.clone();
            is_plus = false;
        }

        ArrayList<Integer> diff = new ArrayList<Integer>();
        int digit_diff = 0;
        for (int i = 0; i < length; ++i) {
            int subtracting_current_digit = _get_digit(bigger, i);
            int subtracted_current_digit = _get_digit(smaller, i);

            digit_diff = subtracting_current_digit - subtracted_current_digit;
            if (0 > digit_diff) {
                _borrow(bigger, i);
                digit_diff += 10;
            }

            diff.add(digit_diff);
        }

        return new BigInt(diff, is_plus);
    }

    public BigInt multiply(BigInt rhs) {
        boolean is_plus = _is_plus == rhs._is_plus;

        BigInt product = new BigInt("0");
        ArrayList<Integer> digit_product = new ArrayList<>();
        for (int i = 0; i < _number.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                digit_product.add(0);
            }

            int carry = 0;
            for (int j = 0; j < rhs._number.size(); ++j) {
                int prod = (_number.get(i) * rhs._number.get(j)) + carry;
                digit_product.add(prod % 10);
                carry = Math.floorDiv(prod, 10);
            }

            if (carry != 0) {
                digit_product.add(carry);
            }

            product = product.plus(new BigInt(digit_product, true));
            digit_product.clear();
        }

        product._is_plus = is_plus;

        return product;
    }

    public BigInt divide(BigInt rhs) throws ArithmeticException {
        if (rhs.equals(new BigInt("0")))
        {
            throw new ArithmeticException("Zero division");
        }

        boolean is_plus = _is_plus == rhs._is_plus;

        BigInt quotient = new BigInt("0");
        BigInt orig_divisor = new BigInt(rhs._number, true);
        BigInt divisor = new BigInt(rhs._number, true);
        while (0 >= divisor.compareTo(this)) {
            quotient = quotient.plus(new BigInt("1"));
            divisor = divisor.plus(orig_divisor);
        }

        quotient._is_plus = is_plus;

        return quotient;
    }

    @Override
    public String toString() {
        String num = new String();
        if (!_is_plus)
        {
            num = num.concat("-");
        }

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

        return this.minus(num)._number.get(0) == 0;
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

        if (equals(num)) {
            return 0;
        }

        return minus(num)._is_plus ? 1 : -1;
    }
}
