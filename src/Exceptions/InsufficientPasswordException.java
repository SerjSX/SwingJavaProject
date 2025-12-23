package Exceptions;

public class InsufficientPasswordException extends Exception {

    public InsufficientPasswordException() {
        super("Please make sure the password entered is 7 characters long, or more, for utmost security");
    }

    @Override
    public String toString() {
        return "Please make sure the password entered is 7 characters long, or more, for utmost security";
    }
}
