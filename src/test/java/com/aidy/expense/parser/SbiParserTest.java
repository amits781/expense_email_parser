package com.aidy.expense.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.aidy.expense.dto.EmailRequestBody;
import com.aidy.expense.dto.EmailResponseBody;
import tools.jackson.databind.ObjectMapper;

class SbiParserTest {

  private BankEmailParser parser;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    parser = new SbiAccountParser();
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
              "from": "<cbsalerts.sbi@alerts.sbi.co.in>",
              "subject": "CBSSBI ALERT",
              "body": "भारतीय स्टेट बैंक की ओर से शुभकामनाएं ! Greetings from SBI ! Dear Customer, Your A/C XXXXX266404 has a debit by transfer of Rs 236.00 on 16/10/24. Avl Bal Rs 31,07,139.12.-SBI प्रिय ग्राहक, आपके खाते XXXXX266404 को ट्रांस्फर द्वारा दि 16/10/24  को ₹236.00 नामे। खाता शेष ₹31,07,139.12 है।-SBI कृपया इस स्वतः उत्पन्न ईमेल का उत्तर न दें। किसी भी प्रश्न के लिए, कृपया हमारे टोल फ्री नंबर 18001234 और 18002100 पर संपर्क करें। Please do not reply to this auto generated email. For any query, please contact our toll free number 18001234 & 18002100.",
              "date": "Wed, 16 Oct 2024 02:26:50 GMT",
              "messageId": "19293264185c9c8f"
            }
            """;
    String expectedResponseJson = """
        {
            "tnxSource": "SBI Account XXXXX266404",
            "tnxAmount": "₹ 236.00",
            "tnxId": "tnx_id",
            "tnxDate": "Wed, 16 Oct 2024 07:56:50 IST",
            "tnxDetails": "debit by transfer"
        }
        """;

    EmailRequestBody request = toRequestObject(json);
    EmailResponseBody expectedResponse = toResponseObject(expectedResponseJson);
    EmailResponseBody response = parser.parse(request);

    assertEquals(expectedResponse, response);
  }

}
