package com.fortis.bank.business.account;

import java.time.LocalDateTime;

/**
 * Captures a customer request for opening a new account type.
 *
 * @author Franck Merlin
 * @version v0.1.0
 */
public class AccountRequest {

    private final String requestId;
    private final String customerNumber;
    private final AccountType requestedType;
    private final LocalDateTime createdAt;

    public AccountRequest(String requestId, String customerNumber, AccountType requestedType) {
        this(requestId, customerNumber, requestedType, LocalDateTime.now());
    }

    public AccountRequest(
            String requestId,
            String customerNumber,
            AccountType requestedType,
            LocalDateTime createdAt) {
        this.requestId = requestId;
        this.customerNumber = customerNumber;
        this.requestedType = requestedType;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public AccountType getRequestedType() {
        return requestedType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
