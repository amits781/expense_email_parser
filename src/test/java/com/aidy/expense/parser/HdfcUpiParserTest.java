package com.aidy.expense.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import tools.jackson.databind.ObjectMapper;

class HdfcUpiParserTest {

  private BankEmailParser parser;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    parser = new HdfcUpiParser();
    objectMapper = new ObjectMapper();
  }

  /**
   * Helper to convert JSON string to EmailRequestBody object
   */
  private EmailRequestBody toRequestObject(String json) throws Exception {
    return objectMapper.readValue(json, EmailRequestBody.class);
  }

  /**
   * Helper to convert JSON string to EmailResponseBody object
   */
  private EmailResponseBody toResponseObject(String json) throws Exception {
    return objectMapper.readValue(json, EmailResponseBody.class);
  }

  @Test
  void testParse_useCase1() throws Exception {
    String json = "{\r\n" + "    \"from\": \"HDFC Bank InstaAlerts <alerts@hdfcbank.net>\",\r\n"
        + "    \"subject\": \"❗  You have done a UPI txn. Check details!\",\r\n"
        + "    \"body\": \" HDFC BANK <https://www.hdfc.bank.in/ways-to-bank?&utm_Source=Migration&utm_Medium=insta_alert&utm_Campaign=migration_insta_alert_entirebase&utm_Term=spinstaalertnov2025> Dear Customer, Rs.898.00 has been debited from account 2045 to VPA paytm-jiomobility@ptybl Jio Prepaid Recharges on 10-01-26. Your UPI transaction reference number is 298173099524. If you did not authorize this transaction, please report it immediately by calling 18002586161 Or SMS BLOCK UPI to 7308080808. Warm Regards, HDFC Bank For more details on Service charges and Fees, * click here.* <https://trkt.aclmails.in/v1/r/s3%2FFiwap5owYh7kJoj0%2FxAHS1537G07QaG%2BvGqKg0ZmIEmJk0GsG8yyf7bs1WSHG2NdoYMeIM4l%2FvNVUxGeEAsWIg4MgemfuY5ZCv2swm8B4UYqt8PRbJLIx0e%2F1arBX386Y%2FfNqVn3vFAJwG%2FrCxsVVrfOvV8GtqyK842v5y2jsdB%2B6OV3zH\",\r\n"
        + "    \"date\": \"Sat, 10 Jan 2026 07:36:21 GMT\",\r\n"
        + "    \"messageId\": \"19ba6d5b2b5446e6\"\r\n" + "}";
    String expectedResponseJson = "{\r\n" + "        \"tnxSource\": \"HDFC Bank account 2045\",\r\n"
        + "        \"tnxAmount\": \"₹ 898.00\",\r\n" + "        \"tnxId\": \"298173099524\",\r\n"
        + "        \"tnxDate\": \"Sat, 10 Jan 2026 13:06:21 IST\",\r\n"
        + "        \"tnxDetails\": \"paytm-jiomobility@ptybl Jio Prepaid Recharges\"\r\n" + "    }";

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

}
