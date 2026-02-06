package com.aidy.expense;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import com.aidy.expense.exception.ServiceAPIException;
import com.aidy.expense.utils.ResponseHandler;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

@Component
public class ControllerFunction {

  @Autowired
  Function<EmailRequestBody, EmailResponseBody> processBankEmail;

  @Autowired
  Predicate<String> secretValidator;


  @FunctionName("emailParser")
  public HttpResponseMessage emailParser(@HttpTrigger(name = "req", methods = {HttpMethod.POST},
      authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<EmailRequestBody>> request,
      ExecutionContext context) {
    // List<BankEmailParser> parsers = List.of(new HdfcUpiParser(), new SbiAccountParser(),
    // new IciciCreditCardParser(), new AmazonPayParser());
    try {
      String clientSecret = request.getHeaders().get("x-api-key");
      if (!secretValidator.test(clientSecret)) {
        throw new ServiceAPIException("Invalid secret key", HttpStatus.UNAUTHORIZED);
      }
      context.getLogger().info("==================== START EMAIL BODY ====================");
      context.getLogger().info("DEBUG - Raw Email Body: " + request.getBody().get().toString());
      context.getLogger().info("===================== END EMAIL BODY =====================");
      return ResponseHandler.generateResponse(request,
          processBankEmail.apply(request.getBody().get()), HttpStatus.OK, "Success");

    } catch (ServiceAPIException ex) {
      // This replaces your GlobalExceptionHandler logic
      context.getLogger().warning("Service Error: " + ex.getMessage());

      return ResponseHandler.generateResponse(request, null, ex.getStatus(), ex.getMessage());

    } catch (Exception ex) {
      context.getLogger().severe("Unexpected Error: " + ex.getMessage());
      return ResponseHandler.generateResponse(request, null, HttpStatus.INTERNAL_SERVER_ERROR,
          ex.getMessage());
    }
  }


}
