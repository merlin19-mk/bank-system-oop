package com.fortis.bank.business.transaction;

import com.fortis.bank.data.database.InMemoryDatabaseGateway;
import com.fortis.bank.data.database.TransactionDbRepository;
import java.util.List;
import java.util.Optional;

/**
 * Static CRUD bridge for transaction business objects.
 *
 * @author Franck Merlin
 * @version v1.1.1
 */
public final class TransactionCrudBridge {

    private static final TransactionDbRepository DB_REPOSITORY =
            new TransactionDbRepository(new InMemoryDatabaseGateway<>());

    private TransactionCrudBridge() {
        // Utility class; no instances.
    }

    public static Transaction create(Transaction transaction) {
        return DB_REPOSITORY.create(transaction);
    }

    public static Optional<Transaction> read(String transactionId) {
        return DB_REPOSITORY.read(transactionId);
    }

    public static Transaction update(Transaction transaction) {
        return DB_REPOSITORY.update(transaction);
    }

    public static boolean delete(String transactionId) {
        return DB_REPOSITORY.delete(transactionId);
    }

    public static List<Transaction> listAll() {
        return DB_REPOSITORY.list();
    }
}
