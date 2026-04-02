package com.fortis.bank.exception;

/**
 * Base exception for business-level banking errors.
 *
 * @author Franck Merlin
 * @version v0.0.1
 */
public class BankException extends RuntimeException {

    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
