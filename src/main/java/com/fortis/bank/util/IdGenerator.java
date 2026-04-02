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

    private IdGenerator() {
        // Utility class; no instances.
    }

    public static String nextCustomerNumber() {
        int value = CUSTOMER_SEQUENCE.incrementAndGet();
        return String.format("C%03d", value);
    }
}
