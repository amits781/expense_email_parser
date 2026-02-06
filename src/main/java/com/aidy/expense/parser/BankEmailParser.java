package com.aidy.expense.parser;

import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;

public interface BankEmailParser {
  boolean canParse(EmailRequestBody email);

  EmailResponseBody parse(EmailRequestBody email);
}