package com.aidy.expense.utils;

import java.util.Optional;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.ResponseBody;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

public class ResponseHandler {

  public static HttpResponseMessage generateResponse(
      HttpRequestMessage<Optional<EmailRequestBody>> request, Object responseObj, HttpStatus status,
      String message) {
    ResponseBody responseBody =
        ResponseBody.builder().message(message).responseBody(responseObj).build();
    return request.createResponseBuilder(status).header("Content-Type", "application/json")
        .body(responseBody).build();
  }
}
