package Exceptions;

public class InvalidInvoiceInputException extends Exception {

    public InvalidInvoiceInputException() {
        super("Make sure you have entered all of the client's information and currencies without the | character.");
    }

    @Override
    public String toString() {
        return "Make sure you have entered all of the client's information and currencies without the | character.";
    }
}
