package com.fortis.bank.business.account;

import com.fortis.bank.util.ValidationUtil;
import java.math.BigDecimal;

/**
 * Currency account storing a customer-preferred currency code.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class CurrencyAccount extends Account {

    private final String currencyCode;

    public CurrencyAccount(String accountNumber, String customerNumber, String currencyCode) {
        super(accountNumber, customerNumber, AccountType.CURRENCY);
        this.currencyCode = ValidationUtil.requireNonBlank(currencyCode, "currencyCode").toUpperCase();
    }

    public CurrencyAccount(
            String accountNumber,
            String customerNumber,
            String currencyCode,
            AccountStatus status,
            BigDecimal initialBalance) {
        super(accountNumber, customerNumber, AccountType.CURRENCY, status, initialBalance);
        this.currencyCode = ValidationUtil.requireNonBlank(currencyCode, "currencyCode").toUpperCase();
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
