package com.aidy.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestBody {
  private String from;
  private String currUser;
  private String subject;
  private String body;
  private String date;
  private String messageId;
}
