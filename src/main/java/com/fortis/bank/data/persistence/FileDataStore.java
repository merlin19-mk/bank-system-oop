package com.fortis.bank.data.persistence;

import com.fortis.bank.business.account.Account;
import com.fortis.bank.business.account.AccountRequest;
import com.fortis.bank.business.customer.Customer;
import com.fortis.bank.business.transaction.Transaction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * File-based persistence helper for MVP snapshots and statements.
 *
 * @author Franck Merlin
 * @version v0.2.0
 */
public class FileDataStore {

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

    private String toCustomerCsv(Customer customer) {
        return String.join(",",
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus().name());
    }

    private String toAccountCsv(Account account) {
        return String.join(",",
                account.getAccountNumber(),
                account.getCustomerNumber(),
                account.getAccountType().name(),
                account.getStatus().name(),
                account.getBalance().toPlainString());
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
}
