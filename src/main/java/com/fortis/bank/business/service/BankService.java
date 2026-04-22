package com.fortis.bank.business.service;

import com.fortis.bank.business.account.Account;
import com.fortis.bank.business.account.AccountRequest;
import com.fortis.bank.business.account.AccountType;
import com.fortis.bank.business.account.CheckingAccount;
import com.fortis.bank.business.account.CreditAccount;
import com.fortis.bank.business.account.CurrencyAccount;
import com.fortis.bank.business.account.LineOfCreditAccount;
import com.fortis.bank.business.account.SavingsAccount;
import com.fortis.bank.business.customer.Customer;
import com.fortis.bank.business.customer.CustomerStatus;
import com.fortis.bank.business.transaction.Transaction;
import com.fortis.bank.business.transaction.TransactionType;
import com.fortis.bank.data.persistence.FileDataStore;
import com.fortis.bank.util.Constants;
import com.fortis.bank.util.IdGenerator;
import com.fortis.bank.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central business service coordinating customer and banking operations.
 *
 * @author Franck Merlin
 * @version v0.2.0
 */
public class BankService {

    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<String, List<Transaction>> accountTransactions = new LinkedHashMap<>();
    private final Map<String, AccountRequest> pendingRequests = new LinkedHashMap<>();
    private final List<String> notifications = new ArrayList<>();
    private final FileDataStore fileDataStore;

    public BankService(FileDataStore fileDataStore) {
        this.fileDataStore = fileDataStore;
        loadSnapshot();
    }

    public Customer createCustomer(String firstName, String lastName, String pin, String email, String phone) {
        String customerNumber = IdGenerator.nextCustomerNumber();
        Customer customer = new Customer(customerNumber, firstName, lastName, pin, email, phone);
        customers.put(customerNumber, customer);

        String checkingNumber = IdGenerator.nextAccountNumber(customerNumber);
        CheckingAccount checkingAccount = new CheckingAccount(checkingNumber, customerNumber);
        accounts.put(checkingNumber, checkingAccount);
        accountTransactions.put(checkingNumber, new ArrayList<>());

        notifyEvent("Created customer " + customerNumber + " with checking account " + checkingNumber);
        return customer;
    }

    public List<Customer> listCustomers() {
        return customers.values().stream()
                .sorted(Comparator.comparing(Customer::getCustomerNumber))
                .collect(Collectors.toList());
    }

    public List<Account> listCustomerAccounts(String customerNumber) {
        getCustomer(customerNumber);
        return accounts.values().stream()
                .filter(a -> a.getCustomerNumber().equals(customerNumber))
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    public BigDecimal getBalance(String accountNumber) {
        return getAccount(accountNumber).getBalance();
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        ValidationUtil.validateAmount(amount);
        account.deposit(amount);
        recordTransaction(accountNumber, accountNumber, TransactionType.DEPOSIT, amount, "Deposit");
        applyCheckingFeeIfNeeded(account);
        notifyIfLowBalance(account);
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        ValidationUtil.validateAmount(amount);
        account.withdraw(amount);
        recordTransaction(accountNumber, accountNumber, TransactionType.WITHDRAW, amount, "Withdraw");
        applyCheckingFeeIfNeeded(account);
        notifyIfLowBalance(account);
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);
        ValidationUtil.validateAmount(amount);

        if (!from.getCustomerNumber().equals(to.getCustomerNumber())) {
            throw new IllegalArgumentException("MVP allows transfers only between accounts of the same customer");
        }

        from.withdraw(amount);
        to.deposit(amount);
        recordTransaction(fromAccountNumber, toAccountNumber, TransactionType.TRANSFER, amount, "Transfer");
        applyCheckingFeeIfNeeded(from);
        notifyIfLowBalance(from);
        notifyIfLowBalance(to);
    }

    public String requestAccount(String customerNumber, AccountType requestedType) {
        Customer customer = getCustomer(customerNumber);
        if (customer.getStatus() == CustomerStatus.CLOSED) {
            throw new IllegalStateException("customer is closed");
        }
        if (requestedType == AccountType.CHECKING) {
            throw new IllegalArgumentException("checking account is automatically created and unique");
        }

        String requestId = IdGenerator.nextRequestId();
        AccountRequest request = new AccountRequest(requestId, customerNumber, requestedType);
        pendingRequests.put(requestId, request);
        notifyEvent("Customer " + customerNumber + " requested account " + requestedType + " (" + requestId + ")");
        return requestId;
    }

    public List<AccountRequest> listPendingRequests() {
        return pendingRequests.values().stream()
                .sorted(Comparator.comparing(AccountRequest::getRequestId))
                .collect(Collectors.toList());
    }

    public String approveRequest(String requestId) {
        AccountRequest request = Optional.ofNullable(pendingRequests.remove(requestId))
                .orElseThrow(() -> new IllegalArgumentException("request not found: " + requestId));

        String accountNumber = IdGenerator.nextAccountNumber(request.getCustomerNumber());
        Account account = createAccountByType(accountNumber, request.getCustomerNumber(), request.getRequestedType());
        accounts.put(accountNumber, account);
        accountTransactions.put(accountNumber, new ArrayList<>());
        notifyEvent("Manager approved request " + requestId + ". Opened account " + accountNumber);
        return accountNumber;
    }

    public void closeCustomer(String customerNumber) {
        Customer customer = getCustomer(customerNumber);
        customer.close();
        listCustomerAccounts(customerNumber).forEach(Account::close);
        notifyEvent("Closed customer " + customerNumber + " and all linked accounts");
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        getAccount(accountNumber);
        return new ArrayList<>(accountTransactions.getOrDefault(accountNumber, List.of()));
    }

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public String generateManagerReport() {
        long activeCustomers = customers.values().stream().filter(c -> c.getStatus() == CustomerStatus.ACTIVE).count();
        long closedCustomers = customers.size() - activeCustomers;
        int accountCount = accounts.size();
        long transactionCount = collectUniqueTransactions().size();

        return "Manager Report\n"
                + "- Customers total: " + customers.size() + "\n"
                + "- Customers active: " + activeCustomers + "\n"
                + "- Customers closed: " + closedCustomers + "\n"
                + "- Accounts total: " + accountCount + "\n"
                + "- Transactions total: " + transactionCount + "\n"
                + "- Pending requests: " + pendingRequests.size();
    }

    public String generateMonthlyStatement(String customerNumber) {
        Customer customer = getCustomer(customerNumber);
        List<Account> customerAccounts = listCustomerAccounts(customerNumber);
        StringBuilder statement = new StringBuilder();
        statement.append("Monthly Statement for ")
                .append(customer.getFirstName())
                .append(" ")
                .append(customer.getLastName())
                .append(" (")
                .append(customerNumber)
                .append(")\n");

        for (Account account : customerAccounts) {
            statement.append("Account ")
                    .append(account.getAccountNumber())
                    .append(" [")
                    .append(account.getAccountType())
                    .append("] balance=")
                    .append(account.getBalance())
                    .append("\n");
        }

        fileDataStore.writeStatement(customerNumber, statement.toString());
        notifyEvent("Generated monthly statement for customer " + customerNumber);
        return statement.toString();
    }

    public void saveSnapshot() {
        fileDataStore.saveSnapshot(
                customers.values(),
                accounts.values(),
                collectUniqueTransactions(),
                notifications,
                pendingRequests.values());
    }

    private Account createAccountByType(String accountNumber, String customerNumber, AccountType accountType) {
        return createAccountByType(accountNumber, customerNumber, accountType, null, null, null);
    }

    private Account createAccountByType(
            String accountNumber,
            String customerNumber,
            AccountType accountType,
            com.fortis.bank.business.account.AccountStatus status,
            BigDecimal initialBalance,
            String currencyCode) {
        BigDecimal balance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
        return switch (accountType) {
            case SAVINGS -> status == null
                    ? new SavingsAccount(accountNumber, customerNumber)
                    : new SavingsAccount(accountNumber, customerNumber, status, balance);
            case CREDIT -> status == null
                    ? new CreditAccount(accountNumber, customerNumber)
                    : new CreditAccount(accountNumber, customerNumber, status, balance);
            case CURRENCY -> {
                String effectiveCurrency = (currencyCode == null || currencyCode.isBlank()) ? "USD" : currencyCode;
                yield status == null
                        ? new CurrencyAccount(accountNumber, customerNumber, effectiveCurrency)
                        : new CurrencyAccount(accountNumber, customerNumber, effectiveCurrency, status, balance);
            }
            case LINE_OF_CREDIT -> status == null
                    ? new LineOfCreditAccount(accountNumber, customerNumber)
                    : new LineOfCreditAccount(accountNumber, customerNumber, status, balance);
            case CHECKING -> status == null
                    ? new CheckingAccount(accountNumber, customerNumber)
                    : new CheckingAccount(accountNumber, customerNumber, status, balance);
        };
    }

    private List<Transaction> collectUniqueTransactions() {
        Map<String, Transaction> uniqueTransactions = new LinkedHashMap<>();
        for (List<Transaction> txList : accountTransactions.values()) {
            for (Transaction transaction : txList) {
                uniqueTransactions.putIfAbsent(transaction.getTransactionId(), transaction);
            }
        }
        return new ArrayList<>(uniqueTransactions.values());
    }

    private void loadSnapshot() {
        FileDataStore.Snapshot snapshot = fileDataStore.loadSnapshot();

        for (FileDataStore.StoredCustomer stored : snapshot.customers()) {
            Customer customer = new Customer(
                    stored.customerNumber(),
                    stored.firstName(),
                    stored.lastName(),
                    stored.pin(),
                    stored.email(),
                    stored.phone(),
                    stored.status());
            customers.put(customer.getCustomerNumber(), customer);
        }

        for (FileDataStore.StoredAccount stored : snapshot.accounts()) {
            if (!customers.containsKey(stored.customerNumber())) {
                continue;
            }
            Account account = createAccountByType(
                    stored.accountNumber(),
                    stored.customerNumber(),
                    stored.accountType(),
                    stored.status(),
                    stored.balance(),
                    stored.currencyCode());
            accounts.put(account.getAccountNumber(), account);
            accountTransactions.put(account.getAccountNumber(), new ArrayList<>());
        }

        Set<String> loadedTransactionIds = new HashSet<>();
        for (FileDataStore.StoredTransaction stored : snapshot.transactions()) {
            if (!loadedTransactionIds.add(stored.transactionId())) {
                continue;
            }

            String sourceAccount = stored.sourceAccount();
            String targetAccount = stored.targetAccount() == null ? sourceAccount : stored.targetAccount();
            if (!accounts.containsKey(sourceAccount)) {
                continue;
            }

            Transaction transaction = new Transaction(
                    stored.transactionId(),
                    sourceAccount,
                    targetAccount,
                    stored.type(),
                    stored.amount(),
                    stored.createdAt(),
                    stored.note());

            accountTransactions.computeIfAbsent(sourceAccount, ignored -> new ArrayList<>()).add(transaction);
            if (!sourceAccount.equals(targetAccount) && accounts.containsKey(targetAccount)) {
                accountTransactions.computeIfAbsent(targetAccount, ignored -> new ArrayList<>()).add(transaction);
            }
        }

        notifications.addAll(snapshot.notifications());

        for (FileDataStore.StoredRequest stored : snapshot.pendingRequests()) {
            if (!customers.containsKey(stored.customerNumber())) {
                continue;
            }
            AccountRequest request = new AccountRequest(
                    stored.requestId(),
                    stored.customerNumber(),
                    stored.requestedType(),
                    stored.createdAt());
            pendingRequests.put(request.getRequestId(), request);
        }

        seedIdGeneratorFromLoadedData();
    }

    private void seedIdGeneratorFromLoadedData() {
        int maxCustomerSequence = customers.keySet().stream()
                .mapToInt(this::extractTrailingNumber)
                .max()
                .orElse(0);
        int maxAccountSequence = accounts.keySet().stream()
                .mapToInt(this::extractTrailingNumber)
                .max()
                .orElse(0);
        int maxTransactionSequence = collectUniqueTransactions().stream()
                .map(Transaction::getTransactionId)
                .mapToInt(this::extractTrailingNumber)
                .max()
                .orElse(0);
        int maxRequestSequence = pendingRequests.keySet().stream()
                .mapToInt(this::extractTrailingNumber)
                .max()
                .orElse(0);

        IdGenerator.seedCustomerSequence(maxCustomerSequence);
        IdGenerator.seedAccountSequence(maxAccountSequence);
        IdGenerator.seedTransactionSequence(maxTransactionSequence);
        IdGenerator.seedRequestSequence(maxRequestSequence);
    }

    private int extractTrailingNumber(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int index = text.length() - 1;
        while (index >= 0 && Character.isDigit(text.charAt(index))) {
            index--;
        }
        String numeric = text.substring(index + 1);
        if (numeric.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(numeric);
    }

    private void applyCheckingFeeIfNeeded(Account account) {
        if (account instanceof CheckingAccount checkingAccount) {
            BigDecimal fee = checkingAccount.calculateFeeForTransaction(LocalDate.now());
            if (fee.compareTo(BigDecimal.ZERO) > 0) {
                account.withdraw(fee);
                recordTransaction(
                        account.getAccountNumber(),
                        account.getAccountNumber(),
                        TransactionType.FEE,
                        fee,
                        "Checking transaction fee");
                notifyEvent("Applied checking fee " + fee + " to account " + account.getAccountNumber());
            }
        }
    }

    private void recordTransaction(
            String sourceAccount,
            String targetAccount,
            TransactionType type,
            BigDecimal amount,
            String note) {
        Transaction transaction = new Transaction(
                IdGenerator.nextTransactionId(),
                sourceAccount,
                targetAccount,
                type,
                amount,
                note);

        accountTransactions.computeIfAbsent(sourceAccount, ignored -> new ArrayList<>()).add(transaction);
        if (!sourceAccount.equals(targetAccount)) {
            accountTransactions.computeIfAbsent(targetAccount, ignored -> new ArrayList<>()).add(transaction);
        }
    }

    private void notifyIfLowBalance(Account account) {
        if (account.getBalance().compareTo(Constants.LOW_BALANCE_THRESHOLD) < 0) {
            notifyEvent("Low balance alert for account " + account.getAccountNumber() + ": " + account.getBalance());
        }
    }

    private void notifyEvent(String text) {
        notifications.add(text);
        fileDataStore.appendAuditLine(text);
    }

    private Customer getCustomer(String customerNumber) {
        return Optional.ofNullable(customers.get(customerNumber))
                .orElseThrow(() -> new IllegalArgumentException("customer not found: " + customerNumber));
    }

    private Account getAccount(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber))
                .orElseThrow(() -> new IllegalArgumentException("account not found: " + accountNumber));
    }
}
