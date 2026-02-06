package com.aidy.expense.config;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import com.aidy.expense.exception.ServiceAPIException;
import com.aidy.expense.parser.BankEmailParser;
import com.microsoft.azure.functions.HttpStatus;

@Configuration
public class BeanConfig {

    @Bean
    public Function<EmailRequestBody, EmailResponseBody> processBankEmail(List<BankEmailParser> parsers) {
      
      return request -> {
        BankEmailParser parser = parsers.stream().filter(p -> p.canParse(request)).findFirst()
            .orElseThrow(() -> new ServiceAPIException("No parser found for this email format",
                HttpStatus.BAD_REQUEST));
        EmailResponseBody response = parser.parse(request);
  
        if (isInvalid(response.getTnxSource()) || isInvalid(response.getTnxAmount())) {
          throw new ServiceAPIException(
              "Parsing incomplete: Failed to extract Source or Amount from email.",
              HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return response;
      };
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
}