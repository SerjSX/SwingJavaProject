import Exceptions.AccountAlreadyExistsException;
import Exceptions.InsufficientInputException;
import Exceptions.InsufficientPasswordException;
import Exceptions.InvalidRegisterInputException;
import Functionalities.AccountHandling;
import Functionalities.ApplyStyling;
import Functionalities.LongAdd;
import Windows.Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;

public class Driver extends JFrame {
        private final JDesktopPane desktopPane;
        private JInternalFrame loginFrame;
        private JInternalFrame registerFrame;

        private JButton loginButton;
        private JButton startRegisterButton;
        private JButton realRegisterButton;

        private JTextField loginUsernameInput;
        private JPasswordField loginPasswordField;

        private JTextField registerFirstNameField;
        private JTextField registerLastNameField;
        private JTextField registerUsernameField;
        private JPasswordField registerPasswordField;
        private JButton registerReturnButton;

         public Driver() {
            setTitle("Cashier Portal");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1500,1000);
            setResizable(true);

            //Creating the desktop pane
            desktopPane = new JDesktopPane();

            add(desktopPane, BorderLayout.CENTER);
            showLoginFrame();
        }

        private void showLoginFrame() {
            loginFrame = new JInternalFrame("Log in Your Account");

            JLabel welcomeText = new JLabel("Welcome to Cashier's Portal", SwingConstants.CENTER);

            JLabel usernameText = new JLabel("Username:", SwingConstants.CENTER);
            loginUsernameInput = new JTextField(10);

            JLabel passwordText = new JLabel("Password:", SwingConstants.CENTER);
            loginPasswordField = new JPasswordField(10);

            ApplyStyling.applyTitle(welcomeText, true);
            ApplyStyling.loopAndApplyText(new JLabel[]{usernameText, passwordText});
            ApplyStyling.loopAndApplyText(new JTextField[]{loginUsernameInput, loginPasswordField});

            loginFrame.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5,5,5,5);

            loginFrame.add(welcomeText, gbc);

            gbc.gridwidth = 1;
            gbc.gridy = 1;
            gbc.gridx = 0; loginFrame.add(usernameText, gbc);
            gbc.gridx = 1; loginFrame.add(loginUsernameInput, gbc);
            gbc.gridy = 2;
            gbc.gridx = 0; loginFrame.add(passwordText, gbc);
            gbc.gridx = 1; loginFrame.add(loginPasswordField, gbc);

            loginButton = new JButton("Login");
            startRegisterButton = new JButton("Register");
            loginButton.addActionListener(new Listener());
            startRegisterButton.addActionListener(new Listener());
            ApplyStyling.loopAndApplyBiggerButton(new JButton[]{loginButton, startRegisterButton});

            gbc.gridy = 3;
            gbc.gridx = 0; loginFrame.add(startRegisterButton, gbc);
            gbc.gridx = 1; loginFrame.add(loginButton, gbc);

            loginFrame.setSize(500, 300);
            loginFrame.setVisible(true);
            desktopPane.add(loginFrame);
        }

        private void showRegisterFrame() {
            registerFrame = new JInternalFrame("Register An Account");

            JLabel firstName = new JLabel("First Name:", SwingConstants.CENTER);
            registerFirstNameField = new JTextField(10);

            JLabel lastName = new JLabel("Last Name:", SwingConstants.CENTER);
            registerLastNameField = new JTextField(10);

            JLabel password = new JLabel("Password:", SwingConstants.CENTER);
            registerPasswordField = new JPasswordField(10);

            JLabel username = new JLabel("Username:", SwingConstants.CENTER);
            registerUsernameField = new JTextField(10);

            realRegisterButton = new JButton("Register");
            realRegisterButton.addActionListener(new Listener());
            registerReturnButton = new JButton("Return");
            registerReturnButton.addActionListener(new Listener());

            ApplyStyling.loopAndApplyText(new JLabel[]{firstName, lastName, password, username});
            ApplyStyling.loopAndApplyText(new JTextField[]{registerFirstNameField, registerLastNameField, registerPasswordField, registerUsernameField});
            ApplyStyling.loopAndApplyBiggerButton(new JButton[]{realRegisterButton, registerReturnButton});

            registerFrame.setLayout(new GridLayout(5, 2));

            JComponent[] componentsToAdd = {firstName, registerFirstNameField, lastName, registerLastNameField, password, registerPasswordField, username, registerUsernameField, registerReturnButton, realRegisterButton};
            LongAdd.addToFrame(registerFrame, componentsToAdd);

            registerFrame.setSize(500, 300);
            registerFrame.setVisible(true);
            desktopPane.add(registerFrame);

            registerFirstNameField.requestFocus();

        }

        public class Listener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();

                if (source == loginButton) {
                    //Returns the username
                    String loginCheck = AccountHandling.loginCheck(loginUsernameInput.getText(), loginPasswordField.getPassword());

                    if (loginCheck != null) {
                        desktopPane.add(new Dashboard(loginCheck, desktopPane, loginFrame));
                        loginFrame.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(loginFrame, "Wrong Username/Password, Try Again or Register.");
                    }

                } else if (source == startRegisterButton) {
                    loginFrame.setVisible(false);
                    showRegisterFrame();
                } else if (source == realRegisterButton) {
                    try {
                        AccountHandling.registerAttempt(registerUsernameField.getText(), registerFirstNameField.getText(), registerLastNameField.getText(), registerPasswordField.getPassword());

                        registerFrame.setVisible(false);
                        registerFrame.dispose();
                        loginFrame.setVisible(true);

                        JOptionPane.showMessageDialog(loginFrame, "Successfully created your account, please login!");
                    } catch (InvalidRegisterInputException |
                            NoSuchAlgorithmException |
                            InsufficientInputException |
                            InsufficientPasswordException |
                            AccountAlreadyExistsException ex) {
                        JOptionPane.showMessageDialog(registerFrame, ex.toString());
                    }
                } else if (source == registerReturnButton) {
                    registerFrame.setVisible(false);
                    registerFrame.dispose();
                    loginFrame.setVisible(true);
                }
            }
        }

        public static void main(String[] args) {
            new Driver().setVisible(true);
        }

}
