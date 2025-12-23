package Exceptions;

public class InvalidRegisterInputException extends Exception{

    public InvalidRegisterInputException() {
        super("Make sure you entered all of the information needed [Username, Firstname, Lastname and Password]!");
    }

    @Override
    public String toString() {
        return "Make sure you entered all of the information needed [Username, Firstname, Lastname and Password]!";
    }
}
