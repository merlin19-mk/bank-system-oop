package com.fortis.bank.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ValidationUtilTest {

    @Test
    void validatesPinSuccessfully() {
        assertEquals("1234", ValidationUtil.validatePin("1234"));
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateEmail("invalid-email"));
    }

    @Test
    void rejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonBlank(" ", "field"));
    }
}
