package com.fortis.bank.util;

import java.math.BigDecimal;

/**
 * Shared constants for MVP business rules.
 *
 * @author Franck Merlin
 * @version v0.0.1
 */
public final class Constants {

    public static final int CHECKING_FREE_TRANSACTIONS_PER_MONTH = 3;
    public static final BigDecimal CHECKING_TRANSACTION_FEE = new BigDecimal("0.25");
    public static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("50.00");
    public static final BigDecimal SAVINGS_ANNUAL_INTEREST_RATE = new BigDecimal("0.02");
    public static final BigDecimal CREDIT_DEFAULT_LIMIT = new BigDecimal("1000.00");
    public static final BigDecimal LINE_OF_CREDIT_DEFAULT_LIMIT = new BigDecimal("2000.00");

    private Constants() {
        // Utility class; no instances.
    }
}
