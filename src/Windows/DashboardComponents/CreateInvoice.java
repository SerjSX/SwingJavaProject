package Windows.DashboardComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

import Exceptions.EmptyTableRowException;
import Exceptions.InvalidInvoiceInputException;
import Exceptions.InvalidVatException;
import Exceptions.NoRowSelectedException;
import Functionalities.ApplyStyling;
import Functionalities.LongAdd;
import Functionalities.FileHandling;
import Functionalities.PrintingUtils;
import Windows.Dashboard;

public class CreateInvoice extends JInternalFrame {
    private final Dashboard dashboard;
    private final int currentInvoiceId;

    private DefaultTableModel itemsTableModel;
    private JTable itemsInformationTable;
    private int itemsRowCount;

    private final String issuerUsername;

    private JTextField clientNameField;
    private JTextField clientAddressField;
    private JTextField clientContactField;
    private JTextField currencyField;
    private JTextField vatField;

    private JLabel subtotalBeforeVat;
    private JLabel subtotalAfterVat;

    private final String creationDate;

    public CreateInvoice(Dashboard dashboard, String issuerUsername) {
        super("Creating an Invoice");
        this.dashboard = dashboard;
        this.issuerUsername = issuerUsername;
        this.currentInvoiceId = dashboard.getAvailableInvoiceId();
        this.creationDate = LocalDate.now().toString();

        JScrollPane itemsScrollPane = getItemsScrollPane();
        JPanel topPanel = getTopPanel();
        JPanel bottomPanel = getBottomPanel();

        super.setLayout(new BorderLayout(10, 10));
        super.add(topPanel, BorderLayout.NORTH);
        super.add(itemsScrollPane, BorderLayout.CENTER);
        super.add(bottomPanel, BorderLayout.SOUTH);

        super.setSize(800,800);
        super.setResizable(true);
        super.setVisible(true);
    }

    private JPanel getTopPanel() {
        JPanel textualPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> super.dispose());
        ApplyStyling.applyBasicButton(closeButton);

        JLabel invoiceTitle = new JLabel("Invoice ID: " + currentInvoiceId, SwingConstants.CENTER);
        JLabel issuerTitle = new JLabel(this.issuerUsername + ", Date: " + creationDate, SwingConstants.CENTER);
        ApplyStyling.applyTitle(invoiceTitle, false);
        ApplyStyling.applyBoldText(issuerTitle);

        textualPanel.setLayout(new GridLayout(1,2));
        textualPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

        JComponent[] textualPanelComponents = {closeButton, invoiceTitle, issuerTitle};
        LongAdd.addToPanel(textualPanel, textualPanelComponents);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JComponent[] topPanelComponents = {textualPanel, getCustomerInformationPanel()};
        LongAdd.addToPanel(topPanel, topPanelComponents);

        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        return topPanel;
    }

    private JPanel getCustomerInformationPanel() {
        JPanel customerInformationPanel = new JPanel();
        customerInformationPanel.setLayout(new GridLayout(5, 2));
        JLabel clientNameText = new JLabel("Client's Name:", SwingConstants.CENTER);
        clientNameField = new JTextField(10);
        JLabel clientAddressText = new JLabel("Client's Address:", SwingConstants.CENTER);
        clientAddressField = new JTextField(10);
        JLabel clientContactDetails = new JLabel("Client's Contact Details:", SwingConstants.CENTER);
        clientContactField = new JTextField(10);

        JLabel currency = new JLabel("Transaction's Currency:", SwingConstants.CENTER);
        currencyField = new JTextField(10);

        JLabel vatText = new JLabel("VAT (%):", SwingConstants.CENTER);
        vatField = new JTextField(5);
        vatField.setText("11");

        ApplyStyling.loopAndApplyText(new JLabel[]{clientNameText, clientAddressText, clientContactDetails, currency, vatText});
        ApplyStyling.loopAndApplyText(new JTextField[]{clientNameField, clientAddressField, clientContactField, currencyField, vatField});

        customerInformationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // top, left, bottom, right

        JComponent[] componentsToAdd = {clientNameText, clientNameField, clientAddressText, clientAddressField, clientContactDetails, clientContactField, currency, currencyField, vatText, vatField};
        LongAdd.addToPanel(customerInformationPanel, componentsToAdd);

        return customerInformationPanel;
    }

    private JScrollPane getItemsScrollPane() {
        String[] itemsTableColumnName = {"N#", "Item Name", "Quantity Ordered", "Unit Cost", "Subtotal"};
        Object[][] itemsData = {};
        itemsTableModel = new DefaultTableModel(itemsData, itemsTableColumnName) {
            // The cell of the subtotal cannot be added since it's calculated by the Calculate Subtotal button.
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 4 && column != 0;
            }
        };

        itemsInformationTable = new JTable(itemsTableModel);
        itemsInformationTable.setRowHeight(25);
        itemsInformationTable.setFont(new Font("Arial", Font.PLAIN, 14));
        itemsInformationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane itemsScrollPane = new JScrollPane(itemsInformationTable);
        itemsScrollPane.setBorder(BorderFactory.createTitledBorder("Products Sold"));
        itemsScrollPane.setPreferredSize(new Dimension(700,400));
        return itemsScrollPane;
    }

    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3,1));

        JPanel subtotalPanel = new JPanel();
        subtotalBeforeVat = new JLabel("Subtotal (Excl. VAT): N/A");
        subtotalAfterVat = new JLabel("Subtotal (Incl. VAT): N/A");
        ApplyStyling.loopAndApplyText(new JLabel[]{subtotalBeforeVat, subtotalAfterVat});
        LongAdd.addToPanel(subtotalPanel, new JLabel[]{subtotalBeforeVat, subtotalAfterVat});
        subtotalBeforeVat.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));

        JPanel operateWithTableButtonsPanel = new JPanel();
        JButton addRowButton = new JButton("Add an Item");
        addRowButton.addActionListener(e -> addRow());

        JButton deleteRowButton = new JButton("Delete Selected Item");
        deleteRowButton.addActionListener(e -> deleteSelectedRow());

        JButton clearTableButton = new JButton("Clear Items");
        clearTableButton.addActionListener(e -> clearTable());

        JButton[] tablesPanelComponents = {addRowButton, deleteRowButton, clearTableButton};
        ApplyStyling.loopAndApplyBasicButton(tablesPanelComponents);
        LongAdd.addToPanel(operateWithTableButtonsPanel, tablesPanelComponents);

        JPanel finalizeButtonsPanel = getFinalizeButtonsPanel();

        bottomPanel.add(subtotalPanel);
        bottomPanel.add(operateWithTableButtonsPanel);
        bottomPanel.add(finalizeButtonsPanel);
        return bottomPanel;
    }

    private JPanel getFinalizeButtonsPanel() {
        JPanel finalizeButtonsPanel = new JPanel();
        JButton calculateButton = new JButton("Calculate Subtotals");
        calculateButton.addActionListener(e -> {
            try {
                calculateSubtotals();
            } catch (EmptyTableRowException | InvalidVatException ex) {
                JOptionPane.showMessageDialog(this, ex.toString());
            }
        });

        JButton saveButton = new JButton("Save Invoice");
        saveButton.addActionListener(e -> saveInvoice());

        JButton printButton = new JButton("Print Invoice");
        printButton.addActionListener(e -> printInvoice());

        ApplyStyling.loopAndApplyBiggerButton(new JButton[]{calculateButton, saveButton, printButton});

        LongAdd.addToPanel(finalizeButtonsPanel, new JComponent[]{calculateButton, saveButton, printButton});

        return finalizeButtonsPanel;
    }

    private void addRow() {
        try {
            // Throws error message if user tries to add a row and the previous row is empty
            checkTableInputValidity(false);

            itemsRowCount++;
            itemsTableModel.addRow(new Object[]{itemsRowCount, "", "", "", "NOT CALCULATED"});
        } catch (EmptyTableRowException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }

    }

    private void deleteSelectedRow() {
        try {
            int selectedRow = itemsInformationTable.getSelectedRow();

            if (selectedRow != -1) {
                itemsTableModel.removeRow(selectedRow);
            } else {
                throw new NoRowSelectedException();
            }

            refreshRowNumbering();
        } catch (NoRowSelectedException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }

    // Refreshes the ID numbering of the items added when removing an item
    private void refreshRowNumbering() {
        int totalRows = itemsInformationTable.getRowCount();

        for (int i = 0; i < totalRows; i++) {
            itemsInformationTable.setValueAt(i+1, i, 0);
        }

        itemsRowCount = totalRows;
        itemsTableModel.fireTableDataChanged();

    }

    private void clearTable() {
        int rowCount = itemsTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            itemsTableModel.removeRow(i);
        }

        itemsRowCount = 0;
    }

    private void calculateSubtotals() throws EmptyTableRowException, InvalidVatException {
        int totalRows = itemsInformationTable.getRowCount();

        double vatPercentage = 0.0;

        try {
            vatPercentage = Double.parseDouble(vatField.getText()) / 100;
        } catch (NumberFormatException | NullPointerException e) {
            throw new InvalidVatException();
        }

        try {
            double sum = 0;

            for (int i = 0; i < totalRows; i++) {
                int quantity = Integer.parseInt(itemsInformationTable.getValueAt(i, 2).toString());
                double unitCost = Double.parseDouble(itemsInformationTable.getValueAt(i, 3).toString());

                double amount = quantity * unitCost;
                sum += amount;
                itemsInformationTable.setValueAt(amount, i, 4);
            }

            subtotalBeforeVat.setText("Subtotal (Excl. VAT): " + sum);
            subtotalAfterVat.setText("Subtotal (Incl. VAT): " + (sum + (sum * vatPercentage)));

            itemsTableModel.fireTableDataChanged();
        } catch (NumberFormatException e) {
            throw new EmptyTableRowException();
        }

    }

    private void saveInvoice() {
        //Add exception to check if any input is empty or contains the character |, throw an error if so.
        String clientName = clientNameField.getText();
        String clientAddress = clientAddressField.getText();
        String clientContact = clientContactField.getText();
        String currency = currencyField.getText();
        String vat = vatField.getText();

        try {
            if (clientName.isBlank() || clientName.contains("|") || clientAddress.isBlank() || clientAddress.contains("|") ||
                    clientContact.isBlank() || clientContact.contains("|") || currency.isBlank() || currency.contains("|") ||
                    vat.isBlank() || vat.contains("|")) {
                throw new InvalidInvoiceInputException();
            }

            calculateSubtotals();
            String[] invoiceData = {clientName, clientAddress, clientContact, vat, creationDate, subtotalBeforeVat.getText().split(":")[1].strip(), subtotalAfterVat.getText().split(":")[1].strip()};

            // Checks last row if it's empty, if yes then removes. Already a new row cannot be added if the previous one is empty.
            // The second one checks if there is an empty row without removing the row.
            checkTableInputValidity(true);

            FileHandling.writeInvoiceToFile(issuerUsername, invoiceData, currency, currentInvoiceId, itemsTableModel, this);

            // Close the current frame and open a new one.
            super.dispose();
            this.dashboard.increaseAvailableInvoiceId();
            this.dashboard.createInvoice(issuerUsername, false);
        } catch (InvalidInvoiceInputException | InvalidVatException | EmptyTableRowException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }

    }

    private void checkTableInputValidity(boolean removeRow) throws EmptyTableRowException{
        int rowsCount = itemsTableModel.getRowCount() - 1;

        if (rowsCount >= 0) {
            String itemName = itemsTableModel.getValueAt(rowsCount, 1).toString();
            String quantityOrdered = itemsTableModel.getValueAt(rowsCount, 2).toString();
            String unitCost = itemsTableModel.getValueAt(rowsCount, 3).toString();

            if (itemName.isBlank() || quantityOrdered.isBlank() || unitCost.isBlank()) {
                if (removeRow) {
                    if (rowsCount == 1) {
                        throw new EmptyTableRowException();
                    } else {
                        itemsTableModel.removeRow(rowsCount);
                    }
                } else {
                    throw new EmptyTableRowException();
                }
            }
        } else {
            if (removeRow) {
                throw new EmptyTableRowException();
            }
        }
    }

    private void printInvoice() {
        PrintingUtils.printComponent(this, this);
    }

}
