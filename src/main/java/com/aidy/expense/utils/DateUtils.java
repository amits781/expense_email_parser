package com.aidy.expense.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

  public static String getFormattedDate(String date) {
    ZonedDateTime gmtDateTime = ZonedDateTime.parse(date, formatter);
    ZonedDateTime istDateTime = gmtDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
    return istDateTime.format(formatter);
  }

  public static String getFormattedDate(String date, String currency) {
    if (date == null || currency == null) {
      return "";
    }

    // Ensure currency is in caps for consistent matching
    String normalizedCurrency = currency.trim().split("\\s+")[0].toUpperCase();

    ZonedDateTime gmtDateTime = ZonedDateTime.parse(date, formatter);
    ZoneId targetZone;

    switch (normalizedCurrency) {
      case "AED":
        // UTC+04:00 for UAE
        targetZone = ZoneId.of("Asia/Dubai");
        break;
      case "INR":
        // UTC+05:30 for India
        targetZone = ZoneId.of("Asia/Kolkata");
        break;
      default:
        targetZone = ZoneId.of("Asia/Kolkata");
        break;
    }

    ZonedDateTime localDateTime = gmtDateTime.withZoneSameInstant(targetZone);
    return localDateTime.format(formatter);
  }

}
