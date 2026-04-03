package com.fortis.bank.business.account;

import com.fortis.bank.util.Constants;
import java.math.BigDecimal;

/**
 * Line of credit account with borrowing limit.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class LineOfCreditAccount extends Account {

    private final BigDecimal lineLimit;

    public LineOfCreditAccount(String accountNumber, String customerNumber) {
        super(accountNumber, customerNumber, AccountType.LINE_OF_CREDIT);
        this.lineLimit = Constants.LINE_OF_CREDIT_DEFAULT_LIMIT;
    }

    @Override
    protected boolean canWithdraw(BigDecimal amount) {
        return balance.subtract(amount).compareTo(lineLimit.negate()) >= 0;
    }

    public BigDecimal getLineLimit() {
        return lineLimit;
    }
}
