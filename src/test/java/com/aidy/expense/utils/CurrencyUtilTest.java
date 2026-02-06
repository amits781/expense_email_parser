package com.aidy.expense.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CurrencyUtilTest {

    @ParameterizedTest
    @CsvSource({
        "₹754.05, ₹ 754.05", // Standard symbol
        "'inr 10,000.00', '₹ 10,000.00'", // Text prefix lowercase
        "rs.350.00, ₹ 350.00", // Leading dot after text
        "AED 726.90, AED 726.90", // UAE Currency
        "aed.100.00, AED 100.00", // UAE with leading dot
        "Rs 2545, ₹ 2545", // Standard text
        ", ''", // Null handling
        "'', ''", // Empty string handling
        "'rs 1,000.56','₹ 1,000.56'"
    })
    void testGetCleanAmount(String input, String expected) {
        assertEquals(expected, CurrencyUtil.getCleanAmount(input));
    }

    @Test
    void testLeadingPunctuationRemoval() {
        // Specifically testing the ^[^0-9]+ regex logic
        String raw = "...750.00";
        String result = CurrencyUtil.getCleanAmount(raw);
        // Assuming default prefix is empty if no currency detected, 
        // or update based on your specific default logic.
        assertEquals("₹ 750.00", result);
        assertFalse(result.startsWith("."));
    }

    @Test
    void testCurrencyStandardization() {
        // Verify that different Indian prefixes all result in the Rupee symbol
        assertEquals("₹ 100", CurrencyUtil.getCleanAmount("inr 100"));
        assertEquals("₹ 100", CurrencyUtil.getCleanAmount("rs 100"));
        assertEquals("₹ 100", CurrencyUtil.getCleanAmount("Rs. 100"));
    }
}