package com.aidy.expense.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import com.aidy.expense.utils.CurrencyUtil;
import com.aidy.expense.utils.DateUtils;

@Component
public class AxisForexParser implements BankEmailParser {

  private static final Pattern AMOUNT_PATTERN =
      Pattern.compile("([A-Z]{3}\\s\\d+(?:\\.\\d{1,2})?).*?\\sdebited");
  private static final Pattern SOURCE_PATTERN =
      Pattern.compile("(Axis Bank Forex Card no\\.\\s\\S+)");

  // Improved Regex: Looks for "at" followed by the merchant name,
  // stopping before the period and "Available balance"
  private static final Pattern DETAILS_PATTERN =
      Pattern.compile("\\s+at\\s+(.*?)\\s*\\.\\s*Available balance");


  @Override
  public boolean canParse(EmailRequestBody email) {
    return email.getFrom().contains("axis.bank.in")
        && email.getSubject().toLowerCase().contains("forex card");
  }

  @Override
  public EmailResponseBody parse(EmailRequestBody email) {
    // Step 1: Normalize the body by replacing all whitespace/newlines with a single space
    String body = email.getBody().replaceAll("\\s+", " ");

    String amount = extract(AMOUNT_PATTERN, body, "Unknown Amount");
    amount = CurrencyUtil.getCleanAmount(amount);
    String sourceRaw = extract(SOURCE_PATTERN, body, "Axis Forex Card");

    // Step 2: Extract Details (Merchant Name)
    String details = extract(DETAILS_PATTERN, body, "Unknown Merchant");

    String finalDate = DateUtils.getFormattedDate(email.getDate(), amount);

    return EmailResponseBody.builder().tnxSource(sourceRaw)
        .tnxAmount(amount)
        .tnxId(email.getMessageId()).tnxDate(finalDate).tnxDetails(details)
        .tnxCategory("Forex Card").build();
  }

  private String extract(Pattern pattern, String text, String defaultValue) {
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return defaultValue;
  }
}
