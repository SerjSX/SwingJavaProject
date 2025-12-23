package Exceptions;

public class InvalidVatException extends Exception {

    public InvalidVatException() {
        super("Make sure the VAT value entered is valid, NOT empty, and in this format: NN%, for example: 11%");
    }

    @Override
    public String toString() {
        return "Make sure the VAT value entered is valid, NOT empty, and in this format: NN%, for example: 11%";
    }
}
