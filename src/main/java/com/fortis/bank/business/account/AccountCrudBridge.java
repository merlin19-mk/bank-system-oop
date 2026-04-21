package com.fortis.bank.business.account;

import com.fortis.bank.data.database.AccountDbRepository;
import com.fortis.bank.data.database.InMemoryDatabaseGateway;
import java.util.List;
import java.util.Optional;

/**
 * Static CRUD bridge for account business objects.
 *
 * @author Franck Merlin
 * @version v1.1.1
 */
public final class AccountCrudBridge {

    private static final AccountDbRepository DB_REPOSITORY =
            new AccountDbRepository(new InMemoryDatabaseGateway<>());

    private AccountCrudBridge() {
        // Utility class; no instances.
    }

    public static Account create(Account account) {
        return DB_REPOSITORY.create(account);
    }

    public static Optional<Account> read(String accountNumber) {
        return DB_REPOSITORY.read(accountNumber);
    }

    public static Account update(Account account) {
        return DB_REPOSITORY.update(account);
    }

    public static boolean delete(String accountNumber) {
        return DB_REPOSITORY.delete(accountNumber);
    }

    public static List<Account> listAll() {
        return DB_REPOSITORY.list();
    }
}
