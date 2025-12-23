package Exceptions;

public class InsufficientInputException extends Exception {

    public InsufficientInputException() {
        super("Please make sure all of the values entered have a length of 3 or longer.");
    }

    @Override
    public String toString() {
        return "Please make sure all of the values entered have a length of 3 or longer.";
    }
}
