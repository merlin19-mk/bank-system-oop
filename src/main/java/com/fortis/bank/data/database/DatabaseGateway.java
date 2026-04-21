package com.fortis.bank.data.database;

import java.util.List;
import java.util.Optional;

/**
 * Minimal database abstraction used by Part III starter repositories.
 *
 * @author Franck Merlin
 * @version v1.1.0
 */
public interface DatabaseGateway<T> {

    void insert(String table, String key, T value);

    Optional<T> select(String table, String key);

    void update(String table, String key, T value);

    boolean delete(String table, String key);

    List<T> selectAll(String table);
}
