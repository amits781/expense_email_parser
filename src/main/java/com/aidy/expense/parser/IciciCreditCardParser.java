package com.aidy.expense.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import com.aidy.expense.utils.CurrencyUtil;
import com.aidy.expense.utils.DateUtils;

@Component
public class IciciCreditCardParser implements BankEmailParser {

  //@formatter:off
    /**
     * Updated Amount Regex:
     * (?:of\s+)?        -> Optionally matches "of " (non-capturing)
     * (?:INR|Rs)\.?\s+  -> Matches either "INR" or "Rs" (with optional dot) followed by spaces
     * ([\d,]+\.\d{2})   -> Captures the numeric amount including commas and decimals
     */
  //@formatter:on
  private static final Pattern AMOUNT_PATTERN =
      Pattern.compile("(?:of\\s+)?(?:INR|Rs)\\.?\\s+([\\d,]+\\.\\d{2})");

  // Matches everything after "Info: " until the first period
  private static final Pattern INFO_PATTERN = Pattern.compile("Info:\\s*(.*?)\\.");

  // Matches the card description: "ICICI Bank Credit Card XX1003"
  private static final Pattern SOURCE_PATTERN =
      Pattern.compile("Your\\s+(.*?)\\s+has\\s+been\\s+used");

  @Override
  public boolean canParse(EmailRequestBody email) {
    return email.getFrom().contains("icicibank.com")
        && email.getSubject().toLowerCase().contains("transaction alert");
  }

  @Override
  public EmailResponseBody parse(EmailRequestBody email) {
    // Clean up body whitespace to ensure multi-line strings don't break regex
    String body = email.getBody().replaceAll("\\s+", " ");

    String rawAmount = extract(AMOUNT_PATTERN, body, "0.00");
    String cleanAmount = CurrencyUtil.getCleanAmount(rawAmount);

    String details = extract(INFO_PATTERN, body, "Unknown Merchant");
    String source = extract(SOURCE_PATTERN, body, "ICICI Bank Card");

    return EmailResponseBody.builder().tnxSource(source).tnxId(email.getMessageId())
        .tnxAmount(cleanAmount)
        .tnxDate(DateUtils.getFormattedDate(email.getDate())).tnxDetails(details).build();
  }

  private String extract(Pattern pattern, String text, String defaultValue) {
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return defaultValue;
  }
}
