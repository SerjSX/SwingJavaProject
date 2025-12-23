package Exceptions;

public class NoInvoicesToShowException extends Exception {

    public NoInvoicesToShowException() {
        super("There are no invoices to show!");
    }

    @Override
    public String toString() {
        return "There are no invoices to show!";
    }
}
