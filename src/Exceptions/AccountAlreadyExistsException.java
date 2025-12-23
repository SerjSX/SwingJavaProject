package Exceptions;

public class AccountAlreadyExistsException extends Exception {
    public AccountAlreadyExistsException() {
        super("There is already an account with the same username! Choose another username.");
    }

    @Override
    public String toString() {
        return "There is already an account with the same username! Choose another username.";
    }
}
