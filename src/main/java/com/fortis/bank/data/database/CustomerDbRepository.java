package com.fortis.bank.data.database;

import com.fortis.bank.business.customer.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Database repository for customer records.
 *
 * @author Franck Merlin
 * @version v1.1.0
 */
public class CustomerDbRepository {

    private static final String CUSTOMER_TABLE = "customers";

    private final DatabaseGateway<Customer> gateway;

    public CustomerDbRepository(DatabaseGateway<Customer> gateway) {
        this.gateway = gateway;
    }

    public Customer create(Customer customer) {
        gateway.insert(CUSTOMER_TABLE, customer.getCustomerNumber(), customer);
        return customer;
    }

    public Optional<Customer> read(String customerNumber) {
        return gateway.select(CUSTOMER_TABLE, customerNumber);
    }

    public Customer update(Customer customer) {
        gateway.update(CUSTOMER_TABLE, customer.getCustomerNumber(), customer);
        return customer;
    }

    public boolean delete(String customerNumber) {
        return gateway.delete(CUSTOMER_TABLE, customerNumber);
    }

    public List<Customer> list() {
        return gateway.selectAll(CUSTOMER_TABLE);
    }
}
