package com.fortis.bank.data.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory gateway that mimics database tables for local development.
 *
 * @author Franck Merlin
 * @version v1.1.0
 */
public class InMemoryDatabaseGateway<T> implements DatabaseGateway<T> {

    private final Map<String, Map<String, T>> tables = new LinkedHashMap<>();

    @Override
    public void insert(String table, String key, T value) {
        table(table).put(key, value);
    }

    @Override
    public Optional<T> select(String table, String key) {
        return Optional.ofNullable(table(table).get(key));
    }

    @Override
    public void update(String table, String key, T value) {
        table(table).put(key, value);
    }

    @Override
    public boolean delete(String table, String key) {
        return table(table).remove(key) != null;
    }

    @Override
    public List<T> selectAll(String table) {
        return new ArrayList<>(table(table).values());
    }

    private Map<String, T> table(String tableName) {
        return tables.computeIfAbsent(tableName, ignored -> new LinkedHashMap<>());
    }
}
