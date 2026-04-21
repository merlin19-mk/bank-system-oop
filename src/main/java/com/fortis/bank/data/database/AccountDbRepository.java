package com.fortis.bank.data.database;

import com.fortis.bank.business.account.Account;
import java.util.List;
import java.util.Optional;

/**
 * Database repository for account records.
 *
 * @author Franck Merlin
 * @version v1.1.1
 */
public class AccountDbRepository {

    private static final String ACCOUNT_TABLE = "accounts";

    private final DatabaseGateway<Account> gateway;

    public AccountDbRepository(DatabaseGateway<Account> gateway) {
        this.gateway = gateway;
    }

    public Account create(Account account) {
        gateway.insert(ACCOUNT_TABLE, account.getAccountNumber(), account);
        return account;
    }

    public Optional<Account> read(String accountNumber) {
        return gateway.select(ACCOUNT_TABLE, accountNumber);
    }

    public Account update(Account account) {
        gateway.update(ACCOUNT_TABLE, account.getAccountNumber(), account);
        return account;
    }

    public boolean delete(String accountNumber) {
        return gateway.delete(ACCOUNT_TABLE, accountNumber);
    }

    public List<Account> list() {
        return gateway.selectAll(ACCOUNT_TABLE);
    }
}
