package Exceptions;

public class EmptyTableRowException extends Exception {

    public EmptyTableRowException() {
        super("Make sure all of the details of the items are entered in the table properly as integer values.");
    }

    @Override
    public String toString() {
        return "Make sure all of the details of the items are entered in the table properly as integer values.";
    }
}
