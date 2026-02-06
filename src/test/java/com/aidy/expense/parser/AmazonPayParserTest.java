package com.aidy.expense.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import tools.jackson.databind.ObjectMapper;

class AmazonPayParserTest {

  private BankEmailParser parser;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    parser = new AmazonPayParser();
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
    String json =
        """
            {
              "from": "Amazon Pay India <no-reply@amazonpay.in>",
              "subject": "Your payment of ₹ 139.0 to RJIL Prepaid was successful",
              "body": "Hi Amit, Your payment to RJIL Prepaid was Approved Paid to Amount RJIL Prepaid ₹139.0 Seller RJIL Prepaid RJIL Prepaid Id BR000BBIHCH1 Transaction ID P04-6275687-2053856 Payment date Wednesday, 02 October, 2024 09:57:07 AM IST View all transactions Contact Information To report any unauthorized transaction, please click here Get Questions? Contact RJIL Prepaid Contact Amazon Pay Trusted & secure payments Terms & Conditions This email was sent from an email address that can't receive emails. Please don't reply to this email.",
              "date": "Wed, 02 Oct 2024 04:27:57 GMT",
              "messageId": "1924b7c1f5b12d6f"
            }
            """;
    String expectedResponseJson = """
        {
            "tnxSource": "Amazon Pay",
            "tnxAmount": "₹ 139.0",
            "tnxId": "P04-6275687-2053856",
            "tnxDate": "Wed, 02 Oct 2024 09:57:57 IST",
            "tnxDetails": "RJIL Prepaid"
        }
        """;

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }


  @Test
  void testParse_useCase2() throws Exception {
    String json =
        """
            {
              "from": "Amazon Pay India <no-reply@amazonpay.in>",
               "subject": "Rs 359.00 was paid on Amazon.in",
               "body": "Hi Amit, Thank you for using Amazon Pay Balance. Your payment was successful. Paid on Amount Amazon.in ₹359.00 To report any unauthorised transaction, please click here Order ID 402-1493940-6988337 Order Date 24 November 2024 Updated Amazon Pay Balance ₹0.00 Wallet ₹0.00 Gift Cards ₹0.00 View Statement Add Money View Statement Add Money Trusted & secure payments",
               "date": "Sun, 24 Nov 2024 15:37:45 GMT",
               "messageId": "1935ed263b2d8cbd"
            }
            """;
    String expectedResponseJson = """
        {
            "tnxSource": "Amazon Pay Balance",
            "tnxAmount": "₹ 359.00",
            "tnxId": "402-1493940-6988337",
            "tnxDate": "Sun, 24 Nov 2024 21:07:45 IST",
            "tnxDetails": "Amazon.in"
        }
        """;

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

  @Test
  void testParse_useCase3() throws Exception {
    String json =
        """
            {
                "from": "Amazon Pay India <no-reply@amazonpay.in>",
                "subject": "Your payment of ₹ 2229.05 to irctc was successful",
                "body": "Hi Amit, Your payment to irctc was Approved Paid to Amount irctc ₹2229.05 Seller irctc irctc Id 100006251717118 Transaction ID P04-1572540-7503890 Payment date Friday, 19 December, 2025 20:36:01 PM IST View all transactions Contact Information To report any unauthorized transaction, please click here Get Questions? Contact irctc Contact Amazon Pay Trusted & secure payments Terms & Conditions This email was sent from an email address that can't receive emails. Please don't reply to this email.",
                "date": "Fri, 19 Dec 2025 15:07:07 GMT",
                "messageId": "19b3726817126126"
            }
            """;
    String expectedResponseJson = """
        {
            "tnxSource": "Amazon Pay",
            "tnxAmount": "₹ 2229.05",
            "tnxId": "P04-1572540-7503890",
            "tnxDate": "Fri, 19 Dec 2025 20:37:07 IST",
            "tnxDetails": "irctc"
        }
        """;

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }
}
