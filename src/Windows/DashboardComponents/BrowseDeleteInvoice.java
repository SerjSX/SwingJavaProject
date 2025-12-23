package Windows.DashboardComponents;

import Exceptions.NoRowSelectedException;
import Exceptions.NoInvoicesToShowException;
import Functionalities.ApplyStyling;
import Functionalities.FileHandling;
import Functionalities.LongAdd;
import Functionalities.PrintingUtils;
import Windows.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyVetoException;

public class BrowseDeleteInvoice extends JInternalFrame {
    private final Dashboard dashboard;
    private final String username;

    private JTable invoicesTable;


    public BrowseDeleteInvoice(Dashboard dashboard, String username) {
        super("Deleting an Invoice");
        this.username = username;
        this.dashboard = dashboard;

        try {
            JPanel topTextPanel = getTopInformationPanel();

            JScrollPane invoiceScrollPane = getInvoicesPane();

            JPanel buttonsPanel = getButtonsPanel();

            super.setLayout(new BorderLayout(10,10));
            super.add(topTextPanel, BorderLayout.NORTH);
            super.add(invoiceScrollPane, BorderLayout.CENTER);
            super.add(buttonsPanel, BorderLayout.SOUTH);

            super.setSize(1000,500);
            super.setVisible(true);
        } catch (NoInvoicesToShowException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }

    }

    private JPanel getTopInformationPanel() {
        JPanel returnPanel = new JPanel();

        JLabel guideTitle = new JLabel("Browsing Guide");
        JLabel guideText = new JLabel("Select an invoice, or multiple, then click a button to do an operation!");
        returnPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        returnPanel.setLayout(new GridLayout(2,1));

        ApplyStyling.applyBoldText(guideTitle);
        ApplyStyling.applyText(guideText);

        LongAdd.addToPanel(returnPanel, new JComponent[]{guideTitle, guideText});

        return returnPanel;
    }

    private JScrollPane getInvoicesPane() throws NoInvoicesToShowException {
        String[] invoiceColumnNames = {"Invoice ID",  "Currency", "Client Name", "Client Address", "Client Contact", "VAT", "Date"};
        Object[][] invoiceData = FileHandling.getUserInvoices(username);
        if (invoiceData == null) {
            throw new NoInvoicesToShowException();
        }

        // The cell of the subtotal cannot be added since it's calculated by the Calculate Subtotal button.
        DefaultTableModel invoicesModel = new DefaultTableModel(invoiceData, invoiceColumnNames) {
            // The cell of the subtotal cannot be added since it's calculated by the Calculate Subtotal button.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoicesTable = new JTable(invoicesModel);
        invoicesTable.setRowHeight(25);
        invoicesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        invoicesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane invoiceScrollPane = new JScrollPane(invoicesTable);
        invoiceScrollPane.setBorder(BorderFactory.createTitledBorder("All Invoices"));
        invoiceScrollPane.setPreferredSize(new Dimension(300,300));

        return invoiceScrollPane;
    }

    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Selected Invoices");
        deleteButton.addActionListener(e -> deleteSelectedInvoices());

        JButton viewButton = new JButton("View Selected Invoices Data");
        viewButton.addActionListener(e -> viewSelectedInvoices());

        JButton printButton = new JButton("Print Invoice List");
        printButton.addActionListener(e -> printInvoiceList());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> super.dispose());

        ApplyStyling.loopAndApplyBiggerButton(new JButton[]{deleteButton, viewButton, printButton});
        ApplyStyling.applyBasicButton(closeButton);

        buttonsPanel.setLayout(new GridLayout(1,2));
        LongAdd.addToPanel(buttonsPanel, new JComponent[]{deleteButton, viewButton, printButton, closeButton});
        return buttonsPanel;
    }

    private void deleteSelectedInvoices() {
        try {
            int[] selectedRows = this.invoicesTable.getSelectedRows();

            if (selectedRows.length == 0) {
                throw new NoRowSelectedException();
            }

            for (int rowIndex: selectedRows) {
                int invoiceIdSelected = Integer.parseInt((String) this.invoicesTable.getValueAt(rowIndex, 0));

                FileHandling.deleteInvoice(this.username, invoiceIdSelected);
            }

            JOptionPane.showMessageDialog(this, "Successfully deleted the invoices.");
            this.dashboard.browseInvoice(username);
        } catch (NoRowSelectedException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }

    }

    private void viewSelectedInvoices() {
        try {
            int[] selectedRows = this.invoicesTable.getSelectedRows();

            if (selectedRows.length == 0) {
                throw new NoRowSelectedException();
            }

            for (int rowIndex: selectedRows) {
                int invoiceIdSelected = Integer.parseInt((String) this.invoicesTable.getValueAt(rowIndex, 0));

                String[] invoiceData = FileHandling.getInvoiceLog(username, invoiceIdSelected, this);
                Object[][] itemsData = FileHandling.getInvoiceItemsData(username, invoiceIdSelected, this);

                JInternalFrame viewInvoice = new ViewInvoice(this.dashboard, username, invoiceIdSelected, invoiceData, itemsData);

                this.dashboard.getDesktopPane().add(viewInvoice);

                try {
                    viewInvoice.setSelected(true);
                }  catch (PropertyVetoException ignored) {}
            }
        } catch (NoRowSelectedException e) {
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }

    private void printInvoiceList() {
        PrintingUtils.printTable(invoicesTable, "Invoice List", this);
    }
}
