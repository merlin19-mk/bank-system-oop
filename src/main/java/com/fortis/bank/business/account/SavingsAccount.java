package com.fortis.bank.business.account;

import com.fortis.bank.util.Constants;
import java.math.BigDecimal;

/**
 * Savings account that can accrue annual gains.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class SavingsAccount extends Account {

    private final BigDecimal annualInterestRate;

    public SavingsAccount(String accountNumber, String customerNumber) {
        super(accountNumber, customerNumber, AccountType.SAVINGS);
        this.annualInterestRate = Constants.SAVINGS_ANNUAL_INTEREST_RATE;
    }

    public SavingsAccount(
            String accountNumber,
            String customerNumber,
            AccountStatus status,
            BigDecimal initialBalance) {
        super(accountNumber, customerNumber, AccountType.SAVINGS, status, initialBalance);
        this.annualInterestRate = Constants.SAVINGS_ANNUAL_INTEREST_RATE;
    }

    public BigDecimal applyAnnualGain() {
        BigDecimal gain = balance.multiply(annualInterestRate);
        balance = balance.add(gain);
        return gain;
    }
}
