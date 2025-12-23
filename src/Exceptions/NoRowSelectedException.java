package Exceptions;

public class NoRowSelectedException extends RuntimeException {
    public NoRowSelectedException() {
        super("You have to select at least one row to do this operation.");
    }

    @Override
    public String toString() {
        return "You have to select at least one row to do this operation.";
    }
}
