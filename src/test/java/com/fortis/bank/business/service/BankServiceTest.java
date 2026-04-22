package com.fortis.bank.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fortis.bank.business.account.AccountType;
import com.fortis.bank.business.customer.Customer;
import com.fortis.bank.data.persistence.FileDataStore;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class BankServiceTest {

    @Test
    void createsCustomerWithMandatoryCheckingAccount() throws Exception {
        Path tempDir = Files.createTempDirectory("fortis-test");
        BankService service = new BankService(new FileDataStore(tempDir));

        Customer customer = service.createCustomer(
                "Franck", "Merlin", "1234", "franck@example.com", "+1234567890");

        assertEquals(1, service.listCustomerAccounts(customer.getCustomerNumber()).size());
        assertEquals(
                AccountType.CHECKING,
                service.listCustomerAccounts(customer.getCustomerNumber()).get(0).getAccountType());
    }

    @Test
    void allowsOwnAccountTransferAfterApproval() throws Exception {
        Path tempDir = Files.createTempDirectory("fortis-test");
        BankService service = new BankService(new FileDataStore(tempDir));

        Customer customer = service.createCustomer(
                "Franck", "Merlin", "1234", "franck@example.com", "+1234567890");
        String checking = service.listCustomerAccounts(customer.getCustomerNumber()).get(0).getAccountNumber();

        String requestId = service.requestAccount(customer.getCustomerNumber(), AccountType.SAVINGS);
        String savings = service.approveRequest(requestId);

        service.deposit(checking, new BigDecimal("100.00"));
        service.transfer(checking, savings, new BigDecimal("20.00"));

        assertEquals(new BigDecimal("80.00"), service.getBalance(checking));
        assertEquals(new BigDecimal("20.00"), service.getBalance(savings));
        assertFalse(service.getTransactionHistory(checking).isEmpty());
    }

    @Test
    void loadsPersistedDataOnServiceStartup() throws Exception {
        Path tempDir = Files.createTempDirectory("fortis-test");
        BankService firstRun = new BankService(new FileDataStore(tempDir));

        Customer customer = firstRun.createCustomer(
                "Franck", "Merlin", "1234", "franck@example.com", "+1234567890");
        String checking = firstRun.listCustomerAccounts(customer.getCustomerNumber()).get(0).getAccountNumber();
        firstRun.deposit(checking, new BigDecimal("35.00"));
        firstRun.saveSnapshot();

        BankService secondRun = new BankService(new FileDataStore(tempDir));
        assertEquals(1, secondRun.listCustomers().size());
        assertEquals(new BigDecimal("35.00"), secondRun.getBalance(checking));

        Customer newCustomer = secondRun.createCustomer(
                "Alice", "Merlin", "5678", "alice@example.com", "+1987654321");
        assertNotEquals(customer.getCustomerNumber(), newCustomer.getCustomerNumber());
    }

    @Test
    void managerReportCountsUniqueTransactions() throws Exception {
        Path tempDir = Files.createTempDirectory("fortis-test");
        BankService service = new BankService(new FileDataStore(tempDir));

        Customer customer = service.createCustomer(
                "Franck", "Merlin", "1234", "franck@example.com", "+1234567890");
        String checking = service.listCustomerAccounts(customer.getCustomerNumber()).get(0).getAccountNumber();
        String requestId = service.requestAccount(customer.getCustomerNumber(), AccountType.SAVINGS);
        String savings = service.approveRequest(requestId);

        service.deposit(checking, new BigDecimal("100.00"));
        service.transfer(checking, savings, new BigDecimal("10.00"));

        String report = service.generateManagerReport();
        assertTrue(report.contains("- Transactions total: 2"));
    }
}
