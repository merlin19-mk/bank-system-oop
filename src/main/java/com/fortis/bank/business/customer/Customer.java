package com.fortis.bank.business.customer;

import com.fortis.bank.util.ValidationUtil;

/**
 * Core customer entity used by the business layer.
 *
 * @author Franck Merlin
 * @version v0.0.2
 */
public class Customer {

    private final String customerNumber;
    private String firstName;
    private String lastName;
    private String pin;
    private String email;
    private String phone;
    private CustomerStatus status;

    public Customer(
            String customerNumber,
            String firstName,
            String lastName,
            String pin,
            String email,
            String phone) {
        this.customerNumber = ValidationUtil.requireNonBlank(customerNumber, "customerNumber");
        this.firstName = ValidationUtil.requireNonBlank(firstName, "firstName");
        this.lastName = ValidationUtil.requireNonBlank(lastName, "lastName");
        this.pin = ValidationUtil.validatePin(pin);
        this.email = ValidationUtil.validateEmail(email);
        this.phone = ValidationUtil.validatePhone(phone);
        this.status = CustomerStatus.ACTIVE;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPin() {
        return pin;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void updateProfile(String firstName, String lastName, String email, String phone) {
        this.firstName = ValidationUtil.requireNonBlank(firstName, "firstName");
        this.lastName = ValidationUtil.requireNonBlank(lastName, "lastName");
        this.email = ValidationUtil.validateEmail(email);
        this.phone = ValidationUtil.validatePhone(phone);
    }

    public void updatePin(String pin) {
        this.pin = ValidationUtil.validatePin(pin);
    }

    public void close() {
        this.status = CustomerStatus.CLOSED;
    }
}
