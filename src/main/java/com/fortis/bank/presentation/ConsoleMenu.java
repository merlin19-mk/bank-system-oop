package com.fortis.bank.presentation;

import com.fortis.bank.business.account.Account;
import com.fortis.bank.business.account.AccountRequest;
import com.fortis.bank.business.account.AccountType;
import com.fortis.bank.business.customer.Customer;
import com.fortis.bank.business.service.BankService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Console presentation layer for manager and customer workflows.
 *
 * @author Franck Merlin
 * @version v0.3.0
 */
public class ConsoleMenu {

    private final BankService bankService;
    private final Scanner scanner;

    public ConsoleMenu(BankService bankService) {
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Fortis Bank ===");
            System.out.println("1. Manager operations");
            System.out.println("2. Customer operations");
            System.out.println("3. Save snapshot");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> managerMenu();
                case "2" -> customerMenu();
                case "3" -> {
                    bankService.saveSnapshot();
                    System.out.println("Snapshot saved under data/.");
                }
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }

        bankService.saveSnapshot();
        System.out.println("Goodbye.");
    }

    private void managerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. Create customer");
            System.out.println("2. List customers");
            System.out.println("3. Approve account request");
            System.out.println("4. Close customer");
            System.out.println("5. Manager report");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createCustomerFlow();
                    case "2" -> listCustomersFlow();
                    case "3" -> approveRequestFlow();
                    case "4" -> closeCustomerFlow();
                    case "5" -> System.out.println(bankService.generateManagerReport());
                    case "0" -> back = true;
                    default -> System.out.println("Invalid option.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private void customerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer (own accounts)");
            System.out.println("4. View balance");
            System.out.println("5. View transaction history");
            System.out.println("6. Request additional account");
            System.out.println("7. Generate monthly statement");
            System.out.println("8. View notifications");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> depositFlow();
                    case "2" -> withdrawFlow();
                    case "3" -> transferFlow();
                    case "4" -> balanceFlow();
                    case "5" -> historyFlow();
                    case "6" -> requestAccountFlow();
                    case "7" -> statementFlow();
                    case "8" -> notificationsFlow();
                    case "0" -> back = true;
                    default -> System.out.println("Invalid option.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private void createCustomerFlow() {
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("PIN (4 digits): ");
        String pin = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        Customer customer = bankService.createCustomer(firstName, lastName, pin, email, phone);
        System.out.println("Created customer " + customer.getCustomerNumber());
        System.out.println("Accounts: " + formatAccounts(bankService.listCustomerAccounts(customer.getCustomerNumber())));
    }

    private void listCustomersFlow() {
        List<Customer> customers = bankService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : customers) {
            System.out.println(customer.getCustomerNumber() + " | "
                    + customer.getFirstName() + " " + customer.getLastName() + " | " + customer.getStatus());
            System.out.println("  Accounts: " + formatAccounts(bankService.listCustomerAccounts(customer.getCustomerNumber())));
        }
    }

    private void approveRequestFlow() {
        List<AccountRequest> requests = bankService.listPendingRequests();
        if (requests.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        for (AccountRequest request : requests) {
            System.out.println(request.getRequestId() + " | customer=" + request.getCustomerNumber()
                    + " | type=" + request.getRequestedType() + " | created=" + request.getCreatedAt());
        }
        System.out.print("Request ID to approve: ");
        String requestId = scanner.nextLine().trim();
        String accountNumber = bankService.approveRequest(requestId);
        System.out.println("Approved. New account: " + accountNumber);
    }

    private void closeCustomerFlow() {
        System.out.print("Customer number: ");
        String customerNumber = scanner.nextLine().trim();
        bankService.closeCustomer(customerNumber);
        System.out.println("Customer closed.");
    }

    private void depositFlow() {
        String accountNumber = resolveAccountNumber("Account number (or customer number): ");
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        bankService.deposit(accountNumber, amount);
        System.out.println("Deposit complete.");
    }

    private void withdrawFlow() {
        String accountNumber = resolveAccountNumber("Account number (or customer number): ");
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        bankService.withdraw(accountNumber, amount);
        System.out.println("Withdrawal complete.");
    }

    private void transferFlow() {
        System.out.print("From account: ");
        String from = scanner.nextLine().trim();
        System.out.print("To account: ");
        String to = scanner.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        bankService.transfer(from, to, amount);
        System.out.println("Transfer complete.");
    }

    private void balanceFlow() {
        String accountNumber = resolveAccountNumber("Account number (or customer number): ");
        System.out.println("Balance: " + bankService.getBalance(accountNumber));
    }

    private void historyFlow() {
        String accountNumber = resolveAccountNumber("Account number (or customer number): ");
        bankService.getTransactionHistory(accountNumber)
                .forEach(tx -> System.out.println(tx.getTransactionId() + " | " + tx.getType() + " | "
                        + tx.getAmount() + " | " + tx.getCreatedAt() + " | " + tx.getNote()));
    }

    private void requestAccountFlow() {
        System.out.print("Customer number: ");
        String customerNumber = scanner.nextLine().trim();
        System.out.print("Account type (SAVINGS/CREDIT/CURRENCY/LINE_OF_CREDIT): ");
        AccountType accountType = AccountType.valueOf(scanner.nextLine().trim().toUpperCase());
        String requestId = bankService.requestAccount(customerNumber, accountType);
        System.out.println("Request submitted: " + requestId);
    }

    private void statementFlow() {
        System.out.print("Customer number: ");
        String customerNumber = scanner.nextLine().trim();
        String statement = bankService.generateMonthlyStatement(customerNumber);
        System.out.println(statement);
    }

    private void notificationsFlow() {
        bankService.getNotifications().forEach(line -> System.out.println("- " + line));
    }

    private String formatAccounts(List<Account> accounts) {
        if (accounts.isEmpty()) {
            return "[]";
        }
        return accounts.stream()
                .map(a -> a.getAccountNumber() + "(" + a.getAccountType() + ")")
                .reduce((left, right) -> left + ", " + right)
                .orElse("[]");
    }

    private String resolveAccountNumber(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.contains("-ACC-")) {
            return input;
        }

        List<Account> customerAccounts = bankService.listCustomerAccounts(input);
        if (customerAccounts.isEmpty()) {
            throw new IllegalArgumentException("no accounts found for customer: " + input);
        }
        if (customerAccounts.size() == 1) {
            return customerAccounts.get(0).getAccountNumber();
        }

        System.out.println("Select account:");
        for (int i = 0; i < customerAccounts.size(); i++) {
            Account account = customerAccounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " (" + account.getAccountType() + ")");
        }
        System.out.print("Choice: ");
        int selected = Integer.parseInt(scanner.nextLine().trim());
        if (selected < 1 || selected > customerAccounts.size()) {
            throw new IllegalArgumentException("invalid account choice");
        }
        return customerAccounts.get(selected - 1).getAccountNumber();
    }
}
