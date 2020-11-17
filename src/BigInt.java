import java.util.ArrayList;

/**
 * Represent a big integer
 */
public class BigInt implements Comparable<BigInt> {
    // The first index is the units , the second dozens and so on...
    ArrayList<Integer> number;
    boolean isPositive;

    /**
     * Initialize the integer
     * @param number big integer string representation
     * @throws IllegalArgumentException if the string is not represent a number the exception throws
     */
    public BigInt(String number) throws IllegalArgumentException {
        if (!isValidNumber(number)) {
            // The string does not represent a number
            throw new IllegalArgumentException("Invalid number");
        }

        isPositive = isPositiveNumber(number);

        this.number = new ArrayList<Integer>();
        for (char digit : number.toCharArray()) {
            if (Character.isDigit(digit)) {
                this.number.add(0, Character.getNumericValue(digit));
            }
        }

        removeLeadingZeroes();
    }

    /**
     * Initialize big integer
     * @param number array of digits
     * @param isPositive true if the number is positive, otherwise false
     * @throws BigIntException if the list contains non digit number
     */
    private BigInt(ArrayList<Integer> number, boolean isPositive) throws BigIntException {
        if (null == number) {
            throw new BigIntException("Got null parameter");
        }

        for (int num : number) {
            if (num >= 10 || num < 0) {
                // number contains non digit
                throw new BigIntException("Got Invalid number");
            }
        }

        this.number = number;
        this.isPositive = isPositive;

        removeLeadingZeroes();
    }

    /**
     * Validate that the string represent a number
     * @param number the string of the number
     * @return true if the string represent number, otherwise false
     */
    private boolean isValidNumber(String number) {
        if (null == number) {
            return false;
        }

        int i = 0;
        for (i = 0; i < number.length(); ++i) {
            if (number.charAt(i) != '+' && number.charAt(i) != '-') {
                if (Character.isDigit(number.charAt(i))) {
                    break;
                }

                // Found a character that isn't a digit or sign
                return false;
            }
        }

        int firstDigitIndex = i;

        if (number.length() == i) {
            // The number has only signs
            return false;
        }

        return (number.substring(firstDigitIndex).chars().allMatch(Character::isDigit));
    }

    private boolean isPositiveNumber(String number) {
        boolean isPositive = true;

        for (int i = 0; i < number.length(); ++i) {
            if (number.charAt(i) == '-') {
                isPositive = !isPositive;
            } else if (number.charAt(i) != '+') {
                // No more signs
                break;
            }
        }

        return isPositive;
    }

    /**
     * Remove zeros from the begging of the
     * number digits list
     */
    private void removeLeadingZeroes() {
        // Iterate the number until that appears a number greater than 0
        while (number.size() > 0 && number.get(number.size() - 1) == 0) {
            number.remove(number.size() - 1);
        }

        // If the list is empty so the number is zero
        if (number.isEmpty()) {
            number.add(0);
            isPositive = true;
        }
    }

    /**
     * Get the digit at the requested index
     * @param num the digits list
     * @param index the index of the digit
     * @return if the index is smaller than the number length the digit in the index, else 0
     */
    private static int getDigit(ArrayList<Integer> num, int index) {
        return index < num.size() ? num.get(index) : 0;
    }

    /**
     * Get the digit of _number at the requested index
     * @param index the index of the digit
     * @return if the index is smaller than the number length the digit in the index, else 0
     */
    private int getDigit(int index) {
        return getDigit(number, index);
    }

    /**
     * Borrow from the next digit
     * @param num the num to do the borrowing on
     * @param index the index of the digit that need borrow
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    private static void borrow(ArrayList<Integer> num, int index) throws BorrowException {
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
    private boolean isGreater(BigInt rhs) {
        if (isPositive && !rhs.isPositive) {
            // rhs is negative and this is positive
            return true;
        } else if (!isPositive && rhs.isPositive) {
            // rhs is positive and this is negative
            return false;
        }
        // The numbers have the same sign

        if (number.size() > rhs.number.size()) {
            // This is longer than the other, so this is bigger if it positive
            return isPositive;
        } else if (number.size() < rhs.number.size()) {
            // This is shorter than the other, so the other is bigger if this positive
            return !isPositive;
        }

        for (int i = number.size() - 1; i >= 0; --i) {
            if (number.get(i) > rhs.number.get(i)) {
                return isPositive;
            } else if (number.get(i) < rhs.number.get(i)) {
                return !isPositive;
            }
        }

        return false;
    }

    /**
     * Swap big integer sign
     */
    private void swapSign() {
        isPositive = !isPositive;
    }

    /**
     * Add rhs to this big integer
     * @param rhs the big integer to add
     * @return the sum of the two big integers
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt plus(BigInt rhs) throws BigIntException {
        if (null == rhs) {
            throw new BigIntException("null parameter");
        }

        if (isPositive && !rhs.isPositive) {
            // x+(-y)=x-y
            return minus(new BigInt(rhs.number, true));
        } else if (!isPositive && !rhs.isPositive) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(number, true);
            BigInt num2 = new BigInt(rhs.number, true);

            BigInt result = num1.plus(num2);
            result.swapSign();

            return result;
        } else if (!isPositive && rhs.isPositive) {
            // -x+y=y-x
            return rhs.minus(new BigInt(number, true));
        }

        // x+y
        int length = Math.max(number.size(), rhs.number.size());

        int digitSum = 0;
        ArrayList<Integer> sum = new ArrayList<Integer>();
        for (int i = 0; i < length; ++i) {
            // Calculate the digits sum including the carry from the last sum
            digitSum += getDigit(i) + rhs.getDigit(i);

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
    public BigInt minus(BigInt rhs) throws BigIntException {
        if (null == rhs) {
            throw new BigIntException("null parameter");
        }

        if (!isPositive && rhs.isPositive) {
            // -x-y=-(x+y)
            BigInt num1 = new BigInt(number, true);
            BigInt num2 = new BigInt(rhs.number, true);

            BigInt result = num1.plus(num2);
            result.swapSign();

            return result;
        } else if (!isPositive && !rhs.isPositive) {
            // -x-(-y)=y-x
            BigInt num1 = new BigInt(number, true);
            BigInt num2 = new BigInt(rhs.number, true);

            return num2.minus(num1);
        } else if (isPositive && !rhs.isPositive) {
            // x-(-y)=x+y
            return plus(new BigInt(rhs.number, true));
        }

        // x-y
        int length = Math.max(number.size(), rhs.number.size());

        // Set the bigger and the smaller number
        ArrayList<Integer> bigger, smaller;
        boolean isDiffPositive = isGreater(rhs);
        if (isDiffPositive) {
            // this is greater
            bigger = new ArrayList<Integer>(number);
            smaller = new ArrayList<Integer>(rhs.number);
        } else {
            // rhs is greater
            bigger = new ArrayList<Integer>(rhs.number);
            smaller = new ArrayList<Integer>(number);
        }

        ArrayList<Integer> diff = new ArrayList<Integer>();
        int digit_diff = 0;
        for (int i = 0; i < length; ++i) {
            int subtracting_current_digit = getDigit(bigger, i);
            int subtracted_current_digit = getDigit(smaller, i);

            digit_diff = subtracting_current_digit - subtracted_current_digit;
            if (0 > digit_diff) {
                // The subtracted is bigger so we need to borrow
                borrow(bigger, i);
                digit_diff += 10;
            }

            diff.add(digit_diff);
        }

        return new BigInt(diff, isDiffPositive);
    }

    /**
     * Multiply two big integers
     * @param rhs the big integer to multiply with this
     * @return the product of this and rhs
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt multiply(BigInt rhs) throws BigIntException {
        if (null == rhs) {
            throw new BigIntException("null parameter");
        }

        BigInt product = new BigInt(new ArrayList<Integer>(), true);
        ArrayList<Integer> digitProduct = new ArrayList<>();
        for (int i = 0; i < number.size(); ++i) {
            // Add zero's to the digit product
            for (int j = 0; j < i; ++j) {
                digitProduct.add(0);
            }

            // calculate the digit product
            int prod = 0;
            for (int j = 0; j < rhs.number.size(); ++j) {
                prod += (number.get(i) * rhs.number.get(j));
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

        product.isPositive = (isPositive == rhs.isPositive);

        return product;
    }

    /**
     * Divide this by rhs
     * @param rhs the number to divide by
     * @return the quotient of this and rhs
     * @throws ArithmeticException in case of zero division the exception is thrown
     * @throws BorrowException if the borrowing failed the exception is thrown
     */
    public BigInt divide(BigInt rhs) throws ArithmeticException, BigIntException {
        if (null == rhs) {
            throw new BigIntException("null parameter");
        }

        if (rhs.equals(new BigInt("0"))) {
            // The rhs is 0
            throw new ArithmeticException("Zero division");
        }

        BigInt absNumerator = new BigInt(number, true);
        BigInt quotient = new BigInt(new ArrayList<Integer>(), true);
        BigInt origDenominator = new BigInt(rhs.number, true);
        BigInt denominator = new BigInt(rhs.number, true);
        while (0 >= denominator.compareTo(absNumerator)) {
            quotient = quotient.plus(new BigInt("1"));
            denominator = denominator.plus(origDenominator);
        }

        quotient.isPositive = (isPositive == rhs.isPositive);

        return quotient;
    }

    /**
     * Get string representation of this big integer
     * @return string representation of this big integer
     */
    @Override
    public String toString() {
        String num = "";

        if (!isPositive) {
            // Add '-' if the number is negative
            num = num.concat("-");
        }

        // Add all the digits in reverse order
        for (int i = number.size() - 1; i >= 0; --i) {
            num = num.concat(number.get(i).toString());
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
        } else if (null == other) {
            return false;
        }

        if (!(other instanceof BigInt)) {
            // other is not a BigInt
            return false;
        }

        BigInt num = (BigInt)other;

        return (num.isPositive == isPositive && number.equals(num.number));
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

        return (isGreater(otherNum) ? 1 : -1);
    }
}
