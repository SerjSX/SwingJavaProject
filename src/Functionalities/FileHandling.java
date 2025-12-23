package Functionalities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileHandling {

    // Used to write invoice to file. This adds the invoice data to the log and the invoice items to the correct folder.
    public static void writeInvoiceToFile(String username, String[] invoiceData, String currency, int invoiceId, DefaultTableModel tableModel, JInternalFrame frame) {

        try {
            createInvoiceLog(username, invoiceId, currency, invoiceData);

            createInvoiceOutput(username, invoiceId, tableModel);

            JOptionPane.showMessageDialog(frame, "Successfully added Invoice #" + invoiceId);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(frame, "An error occurred when trying to save Invoice #" + invoiceId);
        }


    }

    // Creates the invoice log which stores in UserData/AccountInvoiceTracks
    private static void createInvoiceLog(String username, int invoiceId, String currency, String[] invoiceData) throws IOException {
        String pathToLog = "src/UserData/AccountInvoiceTracks/" + username + ".txt";
        Path pathToLogCheck = Paths.get(pathToLog);

        Files.createDirectories(pathToLogCheck.getParent());
        if (Files.notExists(pathToLogCheck)) {
            Files.createFile(pathToLogCheck);
        }

        // Setting up the log writer and reader to do the log first.
        PrintWriter pwLog = new PrintWriter(new FileWriter(pathToLog, true), true);
        BufferedReader logReader = new BufferedReader(new FileReader(pathToLog));

        // This is the columns expecting in the log file, if one exists already to the person logged in
        String logColumnWrite = "Invoice ID|Currency|Client Name|Client Address|Client Contact|VAT|Date|Subtotal (Before VAT)|Subtotal (After VAT)";

        //If the first line of the log file does not start with the column names, then add.
        String firstLine = logReader.readLine();
        if (firstLine == null || !firstLine.equals(logColumnWrite)) {
            pwLog.println(logColumnWrite);
        }

        // Now we move to writing the data, which would represent the invoice id (that can be connected to an invoice file), the currency, and client information the invoice was given to.
        String logRowWrite = invoiceId + "|" + currency + "|";
        for (String data: invoiceData) {
            logRowWrite += data + "|";
        }
        pwLog.println(logRowWrite);//Writing the data

        pwLog.close();
        logReader.close();
    }

    private static void createInvoiceOutput(String username, int invoiceId, DefaultTableModel tableModel) throws IOException {
        int rows = tableModel.getRowCount();
        int columns = tableModel.getColumnCount();

        String pathToOutput = "src/UserData/Invoices/" + username + "/" + invoiceId + ".txt";
        Path pathToOutputCheck = Paths.get(pathToOutput);

        // Creating the directories needed, one for the invoice items data and the other for the invoice information and customer
        Files.createDirectories(pathToOutputCheck.getParent());

        // Now we move to the invoice output that has the items data. we prepare the writer
        PrintWriter pwOutput = new PrintWriter(new FileWriter(pathToOutput), true);

        // we add the columns of the items table and write it on the file
        String columnOutput = "";
        for (int col = 0; col < columns; col++) {
            columnOutput += tableModel.getColumnName(col) + "|";
        }
        pwOutput.println(columnOutput);

        // Then we start to write the items data on each row as the ones in the table.
        for (int rowi = 0; rowi < rows; rowi++) {
            String rowContent = "";
            for (int coli = 0; coli < columns; coli++) {
                rowContent += tableModel.getValueAt(rowi, coli) + "|";
            }

            pwOutput.println(rowContent);
        }

        // We close the writers
        pwOutput.close();
    }

    public static int findLastInvoiceId(String username) {
        String pathToSearch = "src/UserData/AccountInvoiceTracks/" + username + ".txt";

        try {
            BufferedReader file = new BufferedReader(new FileReader(pathToSearch));
            file.readLine();
            String line = file.readLine();

            int lastInvoiceID = 0;
            while (line != null) {
                if (line.isEmpty()) {
                    line = file.readLine();
                    continue;
                }

                lastInvoiceID = Integer.parseInt(line.split("\\|")[0]);
                line = file.readLine();
            }

            return lastInvoiceID;

        } catch (IOException e) {
            return 0;
        }
    }

    public static Object[][] getUserInvoices(String username) {
        String pathToLog = "src/UserData/AccountInvoiceTracks/" + username + ".txt";
        ArrayList<Object[]> returnValue = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathToLog));
            reader.readLine();
            String line = reader.readLine();

            while (line != null) {
                if (line.isEmpty()) {
                    line = reader.readLine();
                    continue;
                }

                String[] split = line.split("\\|");
                Object[] rowValues = new Object[9];

                for (int i = 0; i < split.length; i++) {
                    rowValues[i] = split[i];
                }

                returnValue.add(rowValues);

                line = reader.readLine();
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            return null;
        }
        return returnValue.toArray(new Object[0][]);
    }

    public static String[] getInvoiceLog(String username, int invoiceId, JInternalFrame frame) {
        String pathToLog = "src/UserData/AccountInvoiceTracks/" + username + ".txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathToLog));
            reader.readLine();//skipping the first line which is the column headers

            String line = reader.readLine();

            while (line != null) {
                String[] split = line.split("\\|");

                if (split[0].equals(String.valueOf(invoiceId))) {
                    return split;
                }

                line = reader.readLine();
            }

            return null;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Data of the invoice not found!");
            return null;
        }

    }

    public static Object[][] getInvoiceItemsData(String username, int invoiceId, JInternalFrame frame) {
        String pathToOutput = "src/UserData/Invoices/" + username + "/" + invoiceId + ".txt";
        ArrayList<Object[]> returnValue = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathToOutput));
            reader.readLine();//skipping the first line whihc is the column headers

            String line = reader.readLine();

            while (line != null) {
                Object[] row = new Object[5];

                String[] split = line.split("\\|");

                for (int i = 0; i < 5; i++) {
                    row[i] = split[i];
                }

                returnValue.add(row);
                line = reader.readLine();
            }

            return returnValue.toArray(new Object[0][]);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Data of the invoice not found!");
            return null;
        }

    }

    public static void deleteInvoice(String username, int invoiceId) {
        String pathToDelete = "src/UserData/Invoices/" + username + "/" + invoiceId + ".txt";
        String pathToUpdate = "src/UserData/AccountInvoiceTracks/" + username + ".txt";

        try {
            Files.deleteIfExists(Paths.get(pathToDelete));

            String rows = "";

            BufferedReader readTrackFile = new BufferedReader(new FileReader(pathToUpdate));
            String line = readTrackFile.readLine();
            int lineCount = 0;

            while (line != null) {
                //System.out.println(line + "-" + line.length());
                String[] split = line.split("\\|");
                if (!split[0].equals(String.valueOf(invoiceId))) {
                    rows += line + "\n";
                    lineCount++;
                }

                line = readTrackFile.readLine();
            }

            PrintWriter pw = new PrintWriter(new FileWriter(pathToUpdate));

            if (lineCount < 2) {
                Files.deleteIfExists(Paths.get(pathToUpdate));
            } else {
                pw.println(rows);
            }

            readTrackFile.close();
            pw.close();

        } catch (IOException ignored) {
        }

    }


}
