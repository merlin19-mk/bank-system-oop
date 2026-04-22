package com.fortis.bank.data.persistence;

import com.fortis.bank.business.account.Account;
import com.fortis.bank.business.account.AccountStatus;
import com.fortis.bank.business.account.AccountRequest;
import com.fortis.bank.business.account.AccountType;
import com.fortis.bank.business.account.CurrencyAccount;
import com.fortis.bank.business.customer.Customer;
import com.fortis.bank.business.customer.CustomerStatus;
import com.fortis.bank.business.transaction.Transaction;
import com.fortis.bank.business.transaction.TransactionType;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * File-based persistence helper for MVP snapshots and statements.
 *
 * @author Franck Merlin
 * @version v0.2.0
 */
public class FileDataStore {

    public record StoredCustomer(
            String customerNumber,
            String firstName,
            String lastName,
            String pin,
            String email,
            String phone,
            CustomerStatus status) {
    }

    public record StoredAccount(
            String accountNumber,
            String customerNumber,
            AccountType accountType,
            AccountStatus status,
            BigDecimal balance,
            String currencyCode) {
    }

    public record StoredTransaction(
            String transactionId,
            TransactionType type,
            BigDecimal amount,
            String sourceAccount,
            String targetAccount,
            LocalDateTime createdAt,
            String note) {
    }

    public record StoredRequest(
            String requestId,
            String customerNumber,
            AccountType requestedType,
            LocalDateTime createdAt) {
    }

    public record Snapshot(
            List<StoredCustomer> customers,
            List<StoredAccount> accounts,
            List<StoredTransaction> transactions,
            List<String> notifications,
            List<StoredRequest> pendingRequests) {
    }

    private final Path dataDir;

    public FileDataStore(Path dataDir) {
        this.dataDir = dataDir;
    }

    public void saveSnapshot(
            Collection<Customer> customers,
            Collection<Account> accounts,
            List<Transaction> transactions,
            List<String> notifications,
            Collection<AccountRequest> pendingRequests) {
        try {
            Files.createDirectories(dataDir);
            writeLines(dataDir.resolve("customers.csv"), customers.stream().map(this::toCustomerCsv).toList());
            writeLines(dataDir.resolve("accounts.csv"), accounts.stream().map(this::toAccountCsv).toList());
            writeLines(dataDir.resolve("transactions.csv"), transactions.stream().map(this::toTransactionCsv).toList());
            writeLines(dataDir.resolve("notifications.log"), notifications);
            writeLines(dataDir.resolve("pending_requests.csv"), pendingRequests.stream().map(this::toRequestCsv).toList());
        } catch (IOException ex) {
            throw new IllegalStateException("failed to persist snapshot", ex);
        }
    }

    public void writeStatement(String customerNumber, String content) {
        try {
            Files.createDirectories(dataDir.resolve("statements"));
            Path statementFile = dataDir.resolve("statements").resolve(customerNumber + "-statement.txt");
            Files.writeString(statementFile, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("failed to write statement", ex);
        }
    }

    public void appendAuditLine(String line) {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(
                    dataDir.resolve("audit.log"),
                    LocalDateTime.now() + " | " + line + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException ex) {
            throw new IllegalStateException("failed to write audit log", ex);
        }
    }

    private void writeLines(Path path, List<String> lines) throws IOException {
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Snapshot loadSnapshot() {
        try {
            Files.createDirectories(dataDir);
            List<StoredCustomer> customers = parseCustomers(readLines(dataDir.resolve("customers.csv")));
            List<StoredAccount> accounts = parseAccounts(readLines(dataDir.resolve("accounts.csv")));
            List<StoredTransaction> transactions = parseTransactions(readLines(dataDir.resolve("transactions.csv")));
            List<String> notifications = readLines(dataDir.resolve("notifications.log"));
            List<StoredRequest> pendingRequests = parseRequests(readLines(dataDir.resolve("pending_requests.csv")));
            return new Snapshot(customers, accounts, transactions, notifications, pendingRequests);
        } catch (IOException ex) {
            throw new IllegalStateException("failed to load snapshot", ex);
        }
    }

    private String toCustomerCsv(Customer customer) {
        return String.join(",",
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPin(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus().name());
    }

    private String toAccountCsv(Account account) {
        String currencyCode = "";
        if (account instanceof CurrencyAccount currencyAccount) {
            currencyCode = currencyAccount.getCurrencyCode();
        }
        return String.join(",",
                account.getAccountNumber(),
                account.getCustomerNumber(),
                account.getAccountType().name(),
                account.getStatus().name(),
                account.getBalance().toPlainString(),
                currencyCode);
    }

    private String toTransactionCsv(Transaction tx) {
        String target = tx.getTargetAccount() == null ? "" : tx.getTargetAccount();
        return String.join(",",
                tx.getTransactionId(),
                tx.getType().name(),
                tx.getAmount().toPlainString(),
                tx.getSourceAccount(),
                target,
                tx.getCreatedAt().toString(),
                tx.getNote().replace(",", " "));
    }

    private String toRequestCsv(AccountRequest request) {
        return String.join(",",
                request.getRequestId(),
                request.getCustomerNumber(),
                request.getRequestedType().name(),
                request.getCreatedAt().toString());
    }

    private List<String> readLines(Path path) throws IOException {
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(path).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    private List<StoredCustomer> parseCustomers(List<String> lines) {
        List<StoredCustomer> result = new ArrayList<>();
        for (String line : lines) {
            String[] parts = splitCsv(line, 7);
            if (parts.length == 7) {
                result.add(new StoredCustomer(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5],
                        CustomerStatus.valueOf(parts[6])));
            } else if (parts.length == 6) {
                // Backward compatibility for older snapshots that did not store PIN.
                result.add(new StoredCustomer(
                        parts[0],
                        parts[1],
                        parts[2],
                        "0000",
                        parts[3],
                        parts[4],
                        CustomerStatus.valueOf(parts[5])));
            }
        }
        return result;
    }

    private List<StoredAccount> parseAccounts(List<String> lines) {
        List<StoredAccount> result = new ArrayList<>();
        for (String line : lines) {
            String[] parts = splitCsv(line, 6);
            if (parts.length < 5) {
                continue;
            }
            String currencyCode = parts.length >= 6 ? parts[5] : "";
            result.add(new StoredAccount(
                    parts[0],
                    parts[1],
                    AccountType.valueOf(parts[2]),
                    AccountStatus.valueOf(parts[3]),
                    new BigDecimal(parts[4]),
                    currencyCode));
        }
        return result;
    }

    private List<StoredTransaction> parseTransactions(List<String> lines) {
        List<StoredTransaction> result = new ArrayList<>();
        for (String line : lines) {
            String[] parts = splitCsv(line, 7);
            if (parts.length < 7) {
                continue;
            }
            result.add(new StoredTransaction(
                    parts[0],
                    TransactionType.valueOf(parts[1]),
                    new BigDecimal(parts[2]),
                    parts[3],
                    blankToNull(parts[4]),
                    LocalDateTime.parse(parts[5]),
                    parts[6]));
        }
        return result;
    }

    private List<StoredRequest> parseRequests(List<String> lines) {
        List<StoredRequest> result = new ArrayList<>();
        for (String line : lines) {
            String[] parts = splitCsv(line, 4);
            if (parts.length < 4) {
                continue;
            }
            result.add(new StoredRequest(
                    parts[0],
                    parts[1],
                    AccountType.valueOf(parts[2]),
                    LocalDateTime.parse(parts[3])));
        }
        return result;
    }

    private String[] splitCsv(String line, int expectedColumns) {
        String[] parts = line.split(",", -1);
        if (parts.length > expectedColumns) {
            String[] compact = new String[expectedColumns];
            System.arraycopy(parts, 0, compact, 0, expectedColumns - 1);
            compact[expectedColumns - 1] = String.join(",", Arrays.copyOfRange(parts, expectedColumns - 1, parts.length));
            return compact;
        }
        return parts;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
