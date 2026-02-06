package com.aidy.expense.utils;

import java.util.List;

public class CurrencyUtil {
  public static String getCleanAmount(String rawAmount) {
    if (rawAmount == null || rawAmount.isEmpty()) {
      return "";
    }

    // 1. Convert to lowercase for easier matching and remove extra spaces
    String input = rawAmount.trim().toLowerCase();

    // 2. Identify the currency type and extract only the numbers/dots/commas
    // This regex looks for digits and standard decimal/thousand separators
    String numericPart = input.replaceAll("[^0-9.,]", "");
    numericPart = numericPart.replaceFirst("^[^0-9]+", "");

    // Standardize currency prefix
    String prefix = "";
    if (input.contains("aed")) {
      prefix = "AED";
    } else if (input.contains("inr") || input.contains("rs") || input.contains("₹")) {
      prefix = "₹";
    } else {
      prefix = "₹";
    }

    // 3. Return the formatted string
    return prefix + " " + numericPart;
  }


  public static void main(String args[]) {
    List<String> currencyString = List.of("Rs45", "Rs.45.67", "₹ 456.00", "rs 1,000.56");
    currencyString.forEach(c -> System.out.println(getCleanAmount(c)));
  }

}
