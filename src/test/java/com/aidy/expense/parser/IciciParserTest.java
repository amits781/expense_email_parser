package com.aidy.expense.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import tools.jackson.databind.ObjectMapper;

class IciciParserTest {

  private BankEmailParser parser;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    parser = new IciciCreditCardParser();
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
    String json = "{\r\n"
        + "              \"from\": \"credit_cards@icicibank.com\",\r\n"
        + "              \"subject\": \"Transaction alert for your ICICI Bank Credit Card\",\r\n"
        + "              \"body\": \" ICICI Bank Online Dear Customer, Your ICICI Bank Credit Card XX1003 has been used for a transaction of INR 1,500.00 on Nov 20, 2024 at 04:09:47. Info: RAMCHANDRA GOPIRAM \\\\. The Available Credit Limit on your card is INR 7,98,500.00 and Total Credit Limit is INR 8,00,000.00. The above limits are a total of the limits of all the Credit Cards issued to the primary card holder, including any supplementary cards.In case you have not done this transaction, to report it please call on 18002662 or SMS BLOCK space 1003 to 9215676766 from your registered mobile number and if you are outside India, call on 04071403333. In case you require any further information, you may call our Customer Care or write to us at customer.care@icicibank.com Contact your RM @ Express Relationship Bank\",\r\n"
        + "              \"date\": \"Wed, 20 Nov 2024 10:39:56 GMT\",\r\n"
        + "              \"messageId\": \"19349284c561fd0f\"\r\n"
        + "            }";
    String expectedResponseJson =
        "{\r\n" + "            \"tnxSource\": \"ICICI Bank Credit Card XX1003\",\r\n"
            + "            \"tnxAmount\": \"₹ 1,500.00\",\r\n"
            + "            \"tnxDate\": \"Wed, 20 Nov 2024 16:09:56 IST\",\r\n"
            + "            \"tnxDetails\": \"RAMCHANDRA GOPIRAM \\\\\"\r\n" + "        }";

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

  @Test
  void testParse_useCase2() throws Exception {
    String json = "{\r\n" + "  \"from\": \"credit_cards@icicibank.com\",\r\n"
        + "  \"subject\": \"Transaction alert for your ICICI Bank Credit Card\",\r\n"
        + "  \"body\": \" ICICI Bank Online Dear Customer, Your ICICI Bank Credit Card XX0008 has been used for a transaction of INR 877.44 on Aug 14, 2024 at 07:08:12. Info: BOOK MY SHOW PAYU PG. The Available Credit Limit on your card is INR 7,92,275.56 and Total Credit Limit is INR 8,00,000.00. The above limits are a total of the limits of all the Credit Cards issued to the primary card holder, including any supplementary cards.In case you have not done this transaction, to report it please call on 18002662 or SMS BLOCK space 0008 to 9215676766 from your registered mobile number and if you are outside India, call on 04071403333. In case you require any further information, you may call our Customer Care or write to us at customer.care@icicibank.com Contact your RM @ Express Relationship Bankin\",\r\n"
        + "  \"date\": \"Wed, 14 Aug 2024 13:38:22 GMT\",\r\n"
        + "  \"messageId\": \"191511c6f7f79850\"\r\n" + "}";
    String expectedResponseJson =
        "{\r\n" + "        \"tnxSource\": \"ICICI Bank Credit Card XX0008\",\r\n"
            + "        \"tnxAmount\": \"₹ 877.44\",\r\n"
            + "        \"tnxDate\": \"Wed, 14 Aug 2024 19:08:22 IST\",\r\n"
            + "        \"tnxDetails\": \"BOOK MY SHOW PAYU PG\"\r\n" + "    }";

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

}
