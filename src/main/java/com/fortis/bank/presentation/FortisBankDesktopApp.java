package com.fortis.bank.presentation;

import com.fortis.bank.business.customer.Customer;
import com.fortis.bank.business.service.BankService;
import com.fortis.bank.data.persistence.FileDataStore;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Part III starter graphical client based on Swing.
 *
 * @author Franck Merlin
 * @version v1.2.0
 */
public final class FortisBankDesktopApp {

    private final BankService bankService;
    private final JTextArea outputArea;

    private FortisBankDesktopApp() {
        this.bankService = new BankService(new FileDataStore(Path.of("data")));
        this.outputArea = new JTextArea(18, 70);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FortisBankDesktopApp().createAndShow());
    }

    private void createAndShow() {
        JFrame frame = new JFrame("Fortis Bank Desktop Starter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField pinField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        inputPanel.add(new JLabel("First Name"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("PIN (4 digits)"));
        inputPanel.add(pinField);
        inputPanel.add(new JLabel("Email"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone"));
        inputPanel.add(phoneField);

        JButton createCustomerButton = new JButton("Create Customer + Checking");
        createCustomerButton.addActionListener(event -> {
            try {
                Customer customer = bankService.createCustomer(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        pinField.getText(),
                        emailField.getText(),
                        phoneField.getText());
                String checkingAccount = bankService.listCustomerAccounts(customer.getCustomerNumber())
                        .get(0)
                        .getAccountNumber();
                outputArea.append("Created customer " + customer.getCustomerNumber() + "\n");
                outputArea.append("Checking account: " + checkingAccount + "\n");
                outputArea.append(bankService.generateManagerReport() + "\n\n");
                bankService.saveSnapshot();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Operation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton refreshReportButton = new JButton("Refresh Manager Report");
        refreshReportButton.addActionListener(event -> outputArea.append(bankService.generateManagerReport() + "\n\n"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(createCustomerButton);
        actionsPanel.add(refreshReportButton);
        topPanel.add(actionsPanel, BorderLayout.SOUTH);

        outputArea.setEditable(false);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
