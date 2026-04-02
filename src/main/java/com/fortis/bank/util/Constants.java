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

    private Constants() {
        // Utility class; no instances.
    }
}
