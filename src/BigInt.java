package com.moti;

import java.util.ArrayList;

/**
 * Represent a big integer
 */
public class BigInt implements Comparable<BigInt> {
    // The first index is the units , the second dozens and so on...
    ArrayList<Integer> _number;
    boolean _isPositive;

    /**
     * Initialize the integer
     * @param number big integer string representation
     * @throws IllegalArgumentException if the string is not represent a number the exception throws
     */
    public BigInt(String number) throws IllegalArgumentException {
        if (!_validNumber(number)) {
            // The string does not represent a number
            throw new IllegalArgumentException("Invalid number");
        }

        // Check if the first digit is '-'
        char firstDigit = number.charAt(0);
        _isPositive = (firstDigit != '-');

        // Save only the digits of the number
        String onlyDigits = number;
        if (firstDigit == '-' || firstDigit == '+') {
            onlyDigits = number.substring(1);
        }

        _number = new ArrayList<Integer>();
        for (char digit : onlyDigits.toCharArray()) {
            _number.add(0, Character.getNumericValue(digit));
        }

        _removeLeadingZeors();
    }

    /**
     * Initialize big integer
     * @param number array of digits
     * @param isPositive true if the number is positive, otherwise false
     */
    private BigInt(ArrayList<Integer> number, boolean isPositive) {
        _number = number;
        _isPositive = isPositive;

        _removeLeadingZeors();
    }

    /**
     * Validate that the string represent a number
     * @param number the string of the number
     * @return true if the string represent number, otherwise false
     */
    private boolean _validNumber(String number) {
        // Validate the first character is a sign or digit
        char firstDigit = number.charAt(0);
        boolean isFirstDigitValid = (firstDigit == '+' || firstDigit == '-' || Character.isDigit(firstDigit));

        return (isFirstDigitValid && number.substring(1).chars().allMatch(Character::isDigit));
    }

    /**
     * Remove zeros from the begging of the
     * number digits list
     */
    private void _removeLeadingZeors() {
        // Iterate the number until that appears a number greater than 0
        for (int i = _number.size() - 1; i >= 0; --i) {
            if (_number.get(i) == 0) {
                // Remove leading zero
                _number.remove(i);
            } else {
                break;
            }
        }

        // If the list is empty so the number is zero
        if (_number.isEmpty()) {
            _number.add(0);
            _isPositive = true;
        }
    }

    /**
     * Get the digit at the requested index
     * @param num the digits list
     * @param index the index of the digit
     * @return if the index is smaller than the number length the digit in the index, else 0
     */
    private static int _getDigit(ArrayList<Integer> num, int index) {
        return index < num.size() ? num.get(index) : 0;
    }

    /**
     * Get the digit of _number at the requested index
     * @param index the index of the digit
     * @return if the index is smaller than the number length the digit in the index, else 0
     */
    private int _getDigit(int index) {
        return _getDigit(_number, index);
    }

    /**
     * Borrow from the next digit
     * @param num the num to do the borrowing on
     * @param index the index of the digit that need borrow
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    private static void _borrow(ArrayList<Integer> num, int index) throws BorrowException {
        int i = 0;

        // Search for digit to borrow from
        for (i = index + 1; i < num.size(); ++i) {
            if (num.get(i) != 0) {
                // A digit we can borrow from
                num.set(i, num.get(i) - 1);
                break;
            }
        }

        if (num.size() == i) {
            // We can't borrow
            throw new BorrowException(num, index);
        }

        --i;
        while (i > index) {
            // Set to 9 because all that digits borrowing to the digit after them
            num.set(i, 9);
            --i;
        }
    }

    /**
     * Checks if the number is greater from other number
     * @param rhs the number to compare
     * @return true if this is greater than rhs, else false
     */
    private boolean _isGreater(BigInt rhs) {
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

    /**
     * Swap big integer sign
     */
    private void _swapSign() {
        _isPositive = !_isPositive;
    }

    /**
     * Add rhs to this big integer
     * @param rhs the big integer to add
     * @return the sum of the two big integers
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt plus(BigInt rhs) throws BorrowException {
        if (_isPositive && !rhs._isPositive) {
            // x+(-y)=x-y
            return minus(new BigInt(rhs._number, true));
        } else if (!_isPositive && rhs._isPositive) {
            // -x+y=y-x
            return rhs.minus(new BigInt(_number, true));
        } else if (!_isPositive && !rhs._isPositive) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);

            BigInt result = num1.plus(num2);
            result._swapSign();

            return result;
        }

        // x+y
        int length = Math.max(_number.size(), rhs._number.size());

        int digitSum = 0;
        ArrayList<Integer> sum = new ArrayList<Integer>();
        for (int i = 0; i < length; ++i) {
            // Calculate the digits sum including the carry from the last sum
            digitSum += _getDigit(i) + rhs._getDigit(i);

            sum.add(digitSum % 10);
            digitSum = Math.floorDiv(digitSum, 10);
        }

        // We got a carry at the end so we need to add another '1' digit
        if (0 != digitSum) {
            sum.add(1);
        }

        return new BigInt(sum, true);
    }

    /**
     * Reduce rhs from this big integer
     * @param rhs the big integer to reduce
     * @return the difference of this and rhs
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt minus(BigInt rhs) throws BorrowException {
        if (!_isPositive && rhs._isPositive) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(_number, true);
            BigInt num2 = new BigInt(rhs._number, true);

            BigInt result = num1.plus(num2);
            result._swapSign();

            return result;
        } else if (_isPositive && !rhs._isPositive) {
            // x-(-y)=x+y
            return plus(new BigInt(rhs._number, true));
        } else if (!_isPositive && !rhs._isPositive) {
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
        if (_isGreater(rhs)) {
            // this is greater
            bigger = new ArrayList<Integer>(_number);
            smaller = new ArrayList<Integer>(rhs._number);
            is_plus = true;
        } else {
            // rhs is greater
            bigger = new ArrayList<Integer>(rhs._number);
            smaller = new ArrayList<Integer>(_number);
            is_plus = false;
        }

        ArrayList<Integer> diff = new ArrayList<Integer>();
        int digit_diff = 0;
        for (int i = 0; i < length; ++i) {
            int subtracting_current_digit = _getDigit(bigger, i);
            int subtracted_current_digit = _getDigit(smaller, i);

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

    /**
     * Multiply two big integers
     * @param rhs the big integer to multiply with this
     * @return the product of this and rhs
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt multiply(BigInt rhs) throws BorrowException {
        BigInt product = new BigInt(new ArrayList<Integer>(), true);
        ArrayList<Integer> digitProduct = new ArrayList<>();
        for (int i = 0; i < _number.size(); ++i) {
            // Add zero's to the digit product
            for (int j = 0; j < i; ++j) {
                digitProduct.add(0);
            }

            // calculate the digit product
            int prod = 0;
            for (int j = 0; j < rhs._number.size(); ++j) {
                prod += (_number.get(i) * rhs._number.get(j));
                digitProduct.add(prod % 10);
                prod = Math.floorDiv(prod, 10);
            }

            if (prod != 0) {
                digitProduct.add(prod);
            }

            // Sum the digit product to the number product
            product = product.plus(new BigInt(digitProduct, true));
            digitProduct.clear();
        }

        product._isPositive = (_isPositive == rhs._isPositive);

        return product;
    }

    /**
     * Divide this by rhs
     * @param rhs the number to divide by
     * @return the quotient of this and rhs
     * @throws ArithmeticException in case of zero division the exception is thrown
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt divide(BigInt rhs) throws ArithmeticException, BorrowException {
        if (rhs.equals(new BigInt("0")))
        {
            // The rhs is 0
            throw new ArithmeticException("Zero division");
        }

        BigInt absDivider = new BigInt(_number, true);
        BigInt quotient = new BigInt(new ArrayList<Integer>(), true);
        BigInt origDivisor = new BigInt(rhs._number, true);
        BigInt divisor = new BigInt(rhs._number, true);
        // Run until the divisor is bigger or equal to the divider
        while (0 >= divisor.compareTo(absDivider)) {
            // Increase the quotient by 1
            quotient = quotient.plus(new BigInt("1"));
            // Increase the divisor
            divisor = divisor.plus(origDivisor);
        }

        quotient._isPositive = (_isPositive == rhs._isPositive);

        return quotient;
    }

    /**
     * Get string representation of this big integer
     * @return string representation of this big integer
     */
    @Override
    public String toString() {
        String num = "";

        if (!_isPositive)
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

    /**
     * Checks if two big integers are equal
     * @param other the big integer to compare
     * @return true if the object are equal, otherwise false
     */
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

        return (num._isPositive == _isPositive && _number.equals(num._number));
    }

    /**
     * Compare this to otherNum
     * @param otherNum big integer to compare
     * @return 0 if the numbers are equal, 1 if this
     *          is greater than otherNum, else -1
     */
    @Override
    public int compareTo(BigInt otherNum) {
        if (equals(otherNum)) {
            // The number are equals
            return 0;
        }

        return (_isGreater(otherNum) ? 1 : -1);
    }
}
