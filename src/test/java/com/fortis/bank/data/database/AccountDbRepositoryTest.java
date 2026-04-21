package com.fortis.bank.data.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fortis.bank.business.account.CheckingAccount;
import org.junit.jupiter.api.Test;

class AccountDbRepositoryTest {

    @Test
    void supportsAccountCrudFlow() {
        AccountDbRepository repository = new AccountDbRepository(new InMemoryDatabaseGateway<>());
        CheckingAccount account = new CheckingAccount("C901-ACC-001", "C901");

        repository.create(account);
        assertTrue(repository.read("C901-ACC-001").isPresent());

        account.deposit(new java.math.BigDecimal("25.00"));
        repository.update(account);

        assertEquals("25.00", repository.read("C901-ACC-001").orElseThrow().getBalance().toPlainString());
        assertTrue(repository.delete("C901-ACC-001"));
    }
}
