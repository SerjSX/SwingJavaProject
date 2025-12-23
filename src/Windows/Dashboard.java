package Windows;

import Functionalities.ApplyStyling;
import Functionalities.FileHandling;
import Windows.DashboardComponents.CreateInvoice;
import Windows.DashboardComponents.BrowseDeleteInvoice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

public class Dashboard extends JInternalFrame {
    private final JDesktopPane desktopPane;

    private int lastInvoiceId; //Whenever a user logs in, the invoices of that is loaded and the highest id is updated here as +1 for the new ones
    private JInternalFrame browseInvoice;
    private JInternalFrame createInvoice;
    private final JInternalFrame loginFrame;

    public Dashboard(String username, JDesktopPane desktopPane, JInternalFrame loginFrame) {
        super("Primary Dashboard");
        this.loginFrame = loginFrame;
        this.lastInvoiceId = FileHandling.findLastInvoiceId(username) + 1;
        this.desktopPane = desktopPane;

        this.browseInvoice = null;

        super.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5,5,5,5);

        JLabel welcomeText = new JLabel("Welcome, " + username);
        ApplyStyling.applyTitle(welcomeText, true);

        JLabel informationText = new JLabel("Please pick an operation:");
        ApplyStyling.applyText(informationText);

        super.add(welcomeText, gbc);
        gbc.gridy = 1;
        super.add(informationText, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        String[] buttons = {"Create New Invoice", "Browse and Delete An Invoice", "Logout"};
        for (String str: buttons) {
            JButton button = new JButton(str);
            button.addActionListener(new Listener(username));
            ApplyStyling.applyBiggerButton(button);
            super.add(button, gbc);
            gbc.gridy++;
        }

        super.setSize(500, 350);
        super.setVisible(true);
    }

    public void createInvoice(String username, boolean increment) {
        if (createInvoice != null && increment) {
            increaseAvailableInvoiceId();
        }

        createInvoice = new CreateInvoice(this, username);

        desktopPane.add(createInvoice);
        try {
            createInvoice.setSelected(true);
        }  catch (PropertyVetoException ignored) {

        }
    }

    public void browseInvoice(String username) {
        if (browseInvoice != null) {
            browseInvoice.dispose();
        }

        browseInvoice = new BrowseDeleteInvoice(this, username);
        desktopPane.add(browseInvoice);

        try {
            browseInvoice.setSelected(true);
        }  catch (PropertyVetoException ignored) {}
    }

    public class Listener implements ActionListener {
        private final String username;

        public Listener(String username) {
            this.username = username;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            String buttonText = ((JButton) source).getText();

            switch (buttonText) {
                case "Create New Invoice":
                    createInvoice(username, true);
                    break;
                case "Browse and Delete An Invoice":
                    browseInvoice(username);
                    break;
                case "Logout":
                    loginFrame.show();
                    dispose();
            }
        }
    }

    public void increaseAvailableInvoiceId() {
        this.lastInvoiceId++;
    }

    public int getAvailableInvoiceId() {
        return this.lastInvoiceId;
    }

    public JDesktopPane getDesktopPane() {
        return this.desktopPane;
    }
}
