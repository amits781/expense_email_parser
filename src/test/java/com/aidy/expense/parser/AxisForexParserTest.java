package com.aidy.expense.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import tools.jackson.databind.ObjectMapper;

class AxisForexParserTest {

  private BankEmailParser parser;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    parser = new AxisForexParser();
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
    String json = "{\r\n" + "    \"from\": \"Axis Bank Alerts <alerts@axis.bank.in>\",\r\n"
        + "    \"subject\": \"Axis Bank Forex Card transaction\",\r\n"
        + "    \"body\": \"\\r  \\r   \\r   \\r  \\r  \\r   \\r    \\r     \\r      \\r       \\r        \\r         \\r          \\r         \\r         \\r          \\r           \\r            \\r             \\r             11-01-2026  Dear Customer, Thank you for banking with us. We would like to inform you that AED 23.10 was debited from Axis Bank Forex Card no. XX8389 on 11-01-2026 15:11:47 IST at LEBANESE AUTOMATIC . Available balance: AED 726.90. If not requested by you, please call +914067174100. Always open to help you. Regards, Axis Bank Ltd.    \\r             \\r             \\r               ****This is a system generated communication and does not require signature.**** E002330917_03_2023 \\r             \\r             \\r               \\r             \\r             \\r              Reach us at:     \\r             \\r         \",\r\n"
        + "    \"date\": \"Sun, 11 Jan 2026 09:41:50 GMT\",\r\n"
        + "    \"messageId\": \"19bac6ef33452067\"\r\n" + "}";
    String expectedResponseJson = """
        {
            "tnxSource": "Axis Bank Forex Card no. XX8389",
            "tnxAmount": "AED 23.10",
            "tnxId": "19bac6ef33452067",
            "tnxDate": "Sun, 11 Jan 2026 13:41:50 GST",
            "tnxDetails": "LEBANESE AUTOMATIC",
            "tnxCategory": "Forex Card"
        }
        """;

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

  @Test
  void testParse_useCase2() throws Exception {
    String json = "{\r\n" + "    \"from\": \"Axis Bank Alerts <alerts@axis.bank.in>\",\r\n"
        + "    \"subject\": \"Axis Bank Forex Card transaction\",\r\n"
        + "    \"body\": \"\\r  \\r   \\r   \\r  \\r  \\r   \\r    \\r     \\r      \\r       \\r        \\r         \\r          \\r         \\r         \\r          \\r           \\r            \\r             \\r             11-01-2026  Dear Customer, Thank you for banking with us. We would like to inform you that AED 78.00 was debited from Axis Bank Forex Card no. XX8389 on 11-01-2026 21:56:05 IST at EMIRATES LEISURE RET A . Available balance: AED 648.90. If not requested by you, please call +914067174100. Always open to help you. Regards, Axis Bank Ltd.    \\r             \\r             \\r               ****This is a system generated communication and does not require signature.**** E002330917_03_2023 \\r             \\r             \\r               \\r             \\r             \\r              Reach us at:     \\r             \\r     \",\r\n"
        + "    \"date\": \"Sun, 11 Jan 2026 16:26:11 GMT\",\r\n"
        + "    \"messageId\": \"19bade125694cf20\"\r\n" + "}";
    String expectedResponseJson = """
        {
            "tnxSource": "Axis Bank Forex Card no. XX8389",
            "tnxAmount": "AED 78.00",
            "tnxId": "19bade125694cf20",
            "tnxDate": "Sun, 11 Jan 2026 20:26:11 GST",
            "tnxDetails": "EMIRATES LEISURE RET A",
            "tnxCategory": "Forex Card"
        }
        """;

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

}
