package Windows.DashboardComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;

import Functionalities.ApplyStyling;
import Functionalities.LongAdd;
import Functionalities.PrintingUtils;
import Windows.Dashboard;

public class ViewInvoice extends JInternalFrame {
    protected Dashboard dashboard;
    protected int currentInvoiceId;

    protected DefaultTableModel itemsTableModel;
    protected JTable itemsInformationTable;
    protected Object[][] itemsData;
    protected String[] invoiceData;

    protected final String issuerUsername;

    protected JLabel invoiceTitle;

    protected JLabel clientNameField;
    protected JLabel clientAddressField;
    protected JLabel clientContactField;
    protected JLabel currencyField;

    public ViewInvoice(Dashboard dashboard, String issuerUsername, int invoiceId, String[] invoiceData, Object[][] itemsData) {
        super("Invoice ID #" + invoiceId);
        this.dashboard = dashboard;
        this.issuerUsername = issuerUsername;
        this.currentInvoiceId = invoiceId;
        this.itemsData = itemsData;
        this.invoiceData = invoiceData;

        JScrollPane itemsScrollPane = getItemsScrollPane();
        JPanel topPanel = getTopPanel();
        JPanel bottomPanel = getBottomPanel();

        super.setLayout(new BorderLayout(10, 10));
        super.add(topPanel, BorderLayout.NORTH);
        super.add(itemsScrollPane, BorderLayout.CENTER);
        super.add(bottomPanel, BorderLayout.SOUTH);

        super.setSize(800, 800);
        super.setResizable(true);
        super.setVisible(true);

    }

    private JPanel getTopPanel() {
        JPanel textualPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> super.dispose());

        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> printInvoice());
        ApplyStyling.applyBasicButton(printButton);

        invoiceTitle = new JLabel("Invoice ID: " + currentInvoiceId, SwingConstants.CENTER);
        JLabel issuerTitle = new JLabel("Issuer: " + this.issuerUsername, SwingConstants.CENTER);
        ApplyStyling.applyTitle(invoiceTitle, false);
        ApplyStyling.applyBoldText(issuerTitle);

        textualPanel.setLayout(new GridLayout(1, 3));
        textualPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JComponent[] textualPanelComponents = {closeButton, printButton, invoiceTitle, issuerTitle};
        LongAdd.addToPanel(textualPanel, textualPanelComponents);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JComponent[] topPanelComponents = {textualPanel, getCustomerInformationPanel()};
        LongAdd.addToPanel(topPanel, topPanelComponents);

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        return topPanel;
    }

    private JPanel getCustomerInformationPanel() {
        JPanel customerInformationPanel = new JPanel();
        customerInformationPanel.setLayout(new GridLayout(4, 2));
        JLabel clientNameText = new JLabel("Client's Name:", SwingConstants.CENTER);
        clientNameField = new JLabel(this.invoiceData[2]);

        JLabel clientAddressText = new JLabel("Client's Address:", SwingConstants.CENTER);
        clientAddressField = new JLabel(this.invoiceData[3]);

        JLabel clientContactDetails = new JLabel("Client's Contact Details:", SwingConstants.CENTER);
        clientContactField = new JLabel(this.invoiceData[4]);

        JLabel currency = new JLabel("Transaction's Currency:", SwingConstants.CENTER);
        currencyField = new JLabel(this.invoiceData[1]);

        ApplyStyling.loopAndApplyText(new JLabel[]{clientNameField, clientAddressField, clientContactField, currencyField});
        ApplyStyling.loopAndApplyBoldText(new JLabel[]{clientNameText, clientAddressText, clientContactDetails, currency});

        customerInformationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // top, left, bottom, right

        JComponent[] componentsToAdd = {clientNameText, clientNameField, clientAddressText, clientAddressField, clientContactDetails, clientContactField, currency, currencyField};
        LongAdd.addToPanel(customerInformationPanel, componentsToAdd);

        return customerInformationPanel;
    }

    private JScrollPane getItemsScrollPane() {
        String[] itemsTableColumnName = {"N#", "Item Name", "Quantity Ordered", "Unit Cost", "Subtotal"};

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
        itemsScrollPane.setPreferredSize(new Dimension(700, 400));
        return itemsScrollPane;
    }

    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(4, 2));

        JLabel subtotalBeforeVat = new JLabel("Subtotal (Excl. VAT): " + invoiceData[7], SwingConstants.CENTER);
        JLabel subtotalAfterVat = new JLabel("Subtotal (Incl. VAT): " + invoiceData[8], SwingConstants.CENTER);

        ApplyStyling.loopAndApplyText(new JLabel[]{subtotalAfterVat, subtotalBeforeVat});

        LongAdd.addToPanel(bottomPanel, new JComponent[]{subtotalBeforeVat, subtotalAfterVat});
        subtotalBeforeVat.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // top, left, bottom, right

        bottomPanel.add(subtotalBeforeVat, subtotalAfterVat);

        return bottomPanel;
    }

    private void printInvoice() {
        PrintingUtils.printComponent(this, this);
    }
}
