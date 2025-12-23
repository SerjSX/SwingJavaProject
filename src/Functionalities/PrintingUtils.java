package Functionalities;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.text.MessageFormat;

/**
 * NOTE: I could not test this since I was getting "no printing service available".
 */
public final class PrintingUtils {

    private PrintingUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Prints any Swing component (JInternalFrame, JPanel, etc.) with automatic scaling.
     * Opens a print dialog for the user to select printer and settings.
     *
     * @param component The component to print
     * @param parentForDialog Parent component for showing dialog messages (can be null)
     * @return true if printing was successful, false otherwise
     */
    public static boolean printComponent(Component component, Component parentForDialog) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Print Document");

        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Scale component to fit on the page
                double scaleX = pageFormat.getImageableWidth() / component.getWidth();
                double scaleY = pageFormat.getImageableHeight() / component.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2d.scale(scale, scale);

                // Print the component
                component.printAll(graphics);

                return PAGE_EXISTS;
            }
        });

        boolean doPrint = printerJob.printDialog();
        if (!doPrint) {
            return false;
        }

        try {
            printerJob.print();
            if (parentForDialog != null) {
                JOptionPane.showMessageDialog(parentForDialog,
                    "Document printed successfully!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (PrinterException ex) {
            if (parentForDialog != null) {
                JOptionPane.showMessageDialog(parentForDialog,
                    "Error printing document: " + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    /**
     * Prints a JTable using the built-in JTable printing functionality.
     * Includes header and footer with page numbers.
     *
     * @param table The JTable to print
     * @param headerText Header text to display at the top of each page (can be null)
     * @param parentForDialog Parent component for showing dialog messages (can be null)
     * @return true if printing was successful, false otherwise
     */
    public static boolean printTable(JTable table, String headerText, Component parentForDialog) {
        try {
            MessageFormat header = headerText != null ? new MessageFormat(headerText) : null;
            MessageFormat footer = new MessageFormat("Page {0,number,integer}");

            table.print(JTable.PrintMode.FIT_WIDTH, header, footer);

            if (parentForDialog != null) {
                JOptionPane.showMessageDialog(parentForDialog,
                    "Table printed successfully!",
                    "Print Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (PrinterException ex) {
            if (parentForDialog != null) {
                JOptionPane.showMessageDialog(parentForDialog,
                    "Error printing table: " + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                ex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Prints a JTable without showing success/error dialogs.
     * Useful when you want to handle messages separately.
     *
     * @param table The JTable to print
     * @param headerText Header text to display at the top of each page (can be null)
     * @return true if printing was successful, false otherwise
     */
    public static boolean printTableQuietly(JTable table, String headerText) {
        return printTable(table, headerText, null);
    }

    /**
     * Prints a component without showing success/error dialogs.
     * Useful when you want to handle messages separately.
     *
     * @param component The component to print
     * @return true if printing was successful, false otherwise
     */
    public static boolean printComponentQuietly(Component component) {
        return printComponent(component, null);
    }
}

