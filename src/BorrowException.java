import java.util.ArrayList;

/**
 * Borrowing exception class
 */
public class BorrowException extends Exception {
    /**
     * Initialize BorrowException
     * @param num the number the exception happened on
     * @param index the index that need borrowing
     */
    BorrowException(ArrayList<Integer> num, int index) {
        super(String.format("Borrowing error: number - %s, index - %d", num, index));
    }
}
