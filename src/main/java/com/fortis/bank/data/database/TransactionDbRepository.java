package com.fortis.bank.data.database;

import com.fortis.bank.business.transaction.Transaction;
import java.util.List;
import java.util.Optional;

/**
 * Database repository for transaction records.
 *
 * @author Franck Merlin
 * @version v1.1.1
 */
public class TransactionDbRepository {

    private static final String TRANSACTION_TABLE = "transactions";

    private final DatabaseGateway<Transaction> gateway;

    public TransactionDbRepository(DatabaseGateway<Transaction> gateway) {
        this.gateway = gateway;
    }

    public Transaction create(Transaction transaction) {
        gateway.insert(TRANSACTION_TABLE, transaction.getTransactionId(), transaction);
        return transaction;
    }

    public Optional<Transaction> read(String transactionId) {
        return gateway.select(TRANSACTION_TABLE, transactionId);
    }

    public Transaction update(Transaction transaction) {
        gateway.update(TRANSACTION_TABLE, transaction.getTransactionId(), transaction);
        return transaction;
    }

    public boolean delete(String transactionId) {
        return gateway.delete(TRANSACTION_TABLE, transactionId);
    }

    public List<Transaction> list() {
        return gateway.selectAll(TRANSACTION_TABLE);
    }
}
