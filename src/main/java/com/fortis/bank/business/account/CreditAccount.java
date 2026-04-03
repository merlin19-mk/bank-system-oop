package com.fortis.bank.business.account;

import com.fortis.bank.util.Constants;
import java.math.BigDecimal;

/**
 * Credit account with configurable borrowing limit.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class CreditAccount extends Account {

    private final BigDecimal creditLimit;

    public CreditAccount(String accountNumber, String customerNumber) {
        super(accountNumber, customerNumber, AccountType.CREDIT);
        this.creditLimit = Constants.CREDIT_DEFAULT_LIMIT;
    }

    @Override
    protected boolean canWithdraw(BigDecimal amount) {
        return balance.subtract(amount).compareTo(creditLimit.negate()) >= 0;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
}
