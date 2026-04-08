package com.fortis.bank.business.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CheckingAccountTest {

    @Test
    void appliesFeeAfterFreeTransactionThreshold() {
        CheckingAccount account = new CheckingAccount("C001-ACC-001", "C001");
        LocalDate date = LocalDate.of(2026, 4, 2);

        assertEquals(BigDecimal.ZERO, account.calculateFeeForTransaction(date));
        assertEquals(BigDecimal.ZERO, account.calculateFeeForTransaction(date));
        assertEquals(BigDecimal.ZERO, account.calculateFeeForTransaction(date));
        assertEquals(new BigDecimal("0.25"), account.calculateFeeForTransaction(date));
    }
}
