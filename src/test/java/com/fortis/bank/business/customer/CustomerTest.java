package com.fortis.bank.business.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void closesCustomerStatus() {
        Customer customer = new Customer(
                "C001",
                "Franck",
                "Merlin",
                "1234",
                "franck@example.com",
                "+1234567890");

        customer.close();

        assertEquals(CustomerStatus.CLOSED, customer.getStatus());
    }
}
