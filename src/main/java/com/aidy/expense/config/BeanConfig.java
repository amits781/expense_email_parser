package com.aidy.expense.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import com.aidy.expense.entity.ProcessedEmail;
import com.aidy.expense.exception.ServiceAPIException;
import com.aidy.expense.parser.BankEmailParser;
import com.aidy.expense.repository.ProcessedEmailRepository;
import com.aidy.expense.service.EmailPersistenceService;
import com.microsoft.azure.functions.HttpStatus;

@Configuration
public class BeanConfig {

  @Bean
  public Function<EmailRequestBody, EmailResponseBody> processBankEmail(
      List<BankEmailParser> parsers, ProcessedEmailRepository emailRepository,
      EmailPersistenceService persistenceService) {

    return request -> {
      // Check duplicate message
      persistenceService.checkDuplicateProcessing(emailRepository, request);

      // Select appropriate parser
      BankEmailParser parser = parsers.stream().filter(p -> p.canParse(request)).findFirst()
          .orElseThrow(() -> new ServiceAPIException("No parser found for this email format",
              HttpStatus.BAD_REQUEST));
      // Parse and extract info
      EmailResponseBody response = parser.parse(request);

      // Check for data validness
      if (isInvalid(response.getTnxSource()) || isInvalid(response.getTnxAmount())) {
        throw new ServiceAPIException(
            "Parsing incomplete: Failed to extract Source or Amount from email.",
            HttpStatus.UNPROCESSABLE_ENTITY);
      }

      // Save Entity
      ProcessedEmail logEntity = mapToEntity(request, response);
      persistenceService.saveProcessedData(emailRepository, logEntity);

      // Return response
      return response;
    };
  }

  private ProcessedEmail mapToEntity(EmailRequestBody request,
      EmailResponseBody response) {
    ProcessedEmail logEntity =
        ProcessedEmail.builder().messageId(request.getMessageId()).from(request.getFrom())
            .tnxAmountDisplay(response.getTnxAmount()).tnxCategory(response.getTnxCategory())
            .tnxDate(response.getTnxDate()).tnxDetails(response.getTnxDetails())
            .tnxId(response.getTnxId()).tnxSource(response.getTnxSource()).build();
    setCurrencyAmount(response.getTnxAmount(), logEntity);
    return logEntity;
  }

  @Bean
  public Predicate<String> secretValidator(@Value("${PARSER_SECRET}") String secretValue) {
    // This returns a function that takes the header and returns true/false
    return inputHeaderSecret -> secretValue != null && secretValue.equals(inputHeaderSecret);
  }

  /**
   * Helper to check if a field is Null, Empty, or contains "Unknown" (from parser defaults)
   */
  private boolean isInvalid(String value) {
    return value == null || value.trim().isEmpty() || value.toLowerCase().contains("unknown");
  }

  private void setCurrencyAmount(String raw, ProcessedEmail logEntity) {

    if (raw == null || !raw.contains(" "))
      return;

    // Split by the first space found
    String[] parts = raw.trim().split("\\s+", 2);

    if (parts.length == 2) {
      String symbol = parts[0]; // e.g., "₹"
      String amount = parts[1]; // e.g., "898.00"

      // Standardize the symbol to ISO code
      String curr =
          symbol.equals("\u20B9") || symbol.equalsIgnoreCase("Rs") ? "INR" : symbol.toUpperCase();

      logEntity.setTnxCurrency(curr);
      logEntity.setTnxAmount(new BigDecimal(amount));
    }
  }
}
