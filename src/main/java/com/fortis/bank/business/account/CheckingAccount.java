package com.fortis.bank.business.account;

import com.fortis.bank.util.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Mandatory customer account with free monthly transaction quota.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class CheckingAccount extends Account {

    private final Map<YearMonth, Integer> monthlyTransactionCounts = new HashMap<>();

    public CheckingAccount(String accountNumber, String customerNumber) {
        super(accountNumber, customerNumber, AccountType.CHECKING);
    }

    public BigDecimal calculateFeeForTransaction(LocalDate date) {
        YearMonth key = YearMonth.from(date);
        int count = monthlyTransactionCounts.getOrDefault(key, 0) + 1;
        monthlyTransactionCounts.put(key, count);
        if (count > Constants.CHECKING_FREE_TRANSACTIONS_PER_MONTH) {
            return Constants.CHECKING_TRANSACTION_FEE;
        }
        return BigDecimal.ZERO;
    }
}
