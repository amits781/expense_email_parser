package com.aidy.expense.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.util.ObjectUtils;

public class HashUtil {
  public static String generateHash(String input) {
    if (ObjectUtils.isEmpty(input)) {
      return "";
    }
    try {
      // Use SHA-256 for a robust, one-way hash
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      // Java 17+ HexFormat is the cleanest way to convert bytes to a String
      return HexFormat.of().formatHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Hashing algorithm not found", e);
    }
  }

}
