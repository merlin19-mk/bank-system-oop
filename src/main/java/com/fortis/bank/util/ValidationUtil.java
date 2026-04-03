package com.fortis.bank.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Input validation helpers shared across business services.
 *
 * @author Franck Merlin
 * @version v0.0.2
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+()\\- ]{7,20}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^[0-9]{4}$");

    private ValidationUtil() {
        // Utility class; no instances.
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    public static String validateEmail(String email) {
        String cleaned = requireNonBlank(email, "email");
        if (!EMAIL_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("email format is invalid");
        }
        return cleaned;
    }

    public static String validatePhone(String phone) {
        String cleaned = requireNonBlank(phone, "phone");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("phone format is invalid");
        }
        return cleaned;
    }

    public static String validatePin(String pin) {
        String cleaned = requireNonBlank(pin, "pin");
        if (!PIN_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("pin must be exactly 4 digits");
        }
        return cleaned;
    }

    public static BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        return amount;
    }
}
