package com.fortis.bank.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates deterministic IDs for business entities.
 *
 * @author Franck Merlin
 * @version v0.0.2
 */
public final class IdGenerator {

    private static final AtomicInteger CUSTOMER_SEQUENCE = new AtomicInteger(0);
    private static final AtomicInteger ACCOUNT_SEQUENCE = new AtomicInteger(0);
    private static final AtomicInteger TRANSACTION_SEQUENCE = new AtomicInteger(0);
    private static final AtomicInteger REQUEST_SEQUENCE = new AtomicInteger(0);

    private IdGenerator() {
        // Utility class; no instances.
    }

    public static String nextCustomerNumber() {
        int value = CUSTOMER_SEQUENCE.incrementAndGet();
        return String.format("C%03d", value);
    }

    public static String nextAccountNumber(String customerNumber) {
        int value = ACCOUNT_SEQUENCE.incrementAndGet();
        return String.format("%s-ACC-%03d", customerNumber, value);
    }

    public static String nextTransactionId() {
        int value = TRANSACTION_SEQUENCE.incrementAndGet();
        return String.format("TX-%06d", value);
    }

    public static String nextRequestId() {
        int value = REQUEST_SEQUENCE.incrementAndGet();
        return String.format("REQ-%05d", value);
    }
}
