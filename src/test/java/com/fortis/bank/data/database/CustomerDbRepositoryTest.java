package com.fortis.bank.data.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fortis.bank.business.customer.Customer;
import org.junit.jupiter.api.Test;

class CustomerDbRepositoryTest {

    @Test
    void supportsBasicCrudFlow() {
        CustomerDbRepository repository = new CustomerDbRepository(new InMemoryDatabaseGateway<>());
        Customer customer = new Customer("C900", "Franck", "Merlin", "1234", "franck@example.com", "+1234567890");

        repository.create(customer);
        assertTrue(repository.read("C900").isPresent());

        customer.updateProfile("Franck", "Merlin", "fmerlin@example.com", "+1987654321");
        repository.update(customer);

        assertEquals("fmerlin@example.com", repository.read("C900").orElseThrow().getEmail());
        assertTrue(repository.delete("C900"));
    }
}
