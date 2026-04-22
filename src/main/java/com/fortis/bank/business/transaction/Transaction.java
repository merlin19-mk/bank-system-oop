package com.fortis.bank.business.transaction;

import com.fortis.bank.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable transaction record.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class Transaction {

    private final String transactionId;
    private final String sourceAccount;
    private final String targetAccount;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime createdAt;
    private final String note;

    public Transaction(
            String transactionId,
            String sourceAccount,
            String targetAccount,
            TransactionType type,
            BigDecimal amount,
            String note) {
        this(transactionId, sourceAccount, targetAccount, type, amount, LocalDateTime.now(), note);
    }

    public Transaction(
            String transactionId,
            String sourceAccount,
            String targetAccount,
            TransactionType type,
            BigDecimal amount,
            LocalDateTime createdAt,
            String note) {
        this.transactionId = ValidationUtil.requireNonBlank(transactionId, "transactionId");
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.type = type;
        this.amount = amount;
        this.note = note == null ? "" : note;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getNote() {
        return note;
    }
}
