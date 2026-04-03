package com.fortis.bank.business.account;

import com.fortis.bank.util.ValidationUtil;
import java.math.BigDecimal;

/**
 * Base account abstraction for balance operations.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public abstract class Account {

    private final String accountNumber;
    private final String customerNumber;
    private final AccountType accountType;
    private AccountStatus status;
    protected BigDecimal balance;

    protected Account(String accountNumber, String customerNumber, AccountType accountType) {
        this.accountNumber = ValidationUtil.requireNonBlank(accountNumber, "accountNumber");
        this.customerNumber = ValidationUtil.requireNonBlank(customerNumber, "customerNumber");
        this.accountType = accountType;
        this.status = AccountStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        ValidationUtil.validateAmount(amount);
        ensureActive();
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        ValidationUtil.validateAmount(amount);
        ensureActive();
        if (!canWithdraw(amount)) {
            throw new IllegalArgumentException("insufficient available funds");
        }
        balance = balance.subtract(amount);
    }

    public void close() {
        status = AccountStatus.CLOSED;
    }

    protected boolean canWithdraw(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    private void ensureActive() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("account is closed");
        }
    }
}
