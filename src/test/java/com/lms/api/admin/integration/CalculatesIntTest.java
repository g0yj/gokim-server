package com.lms.api.admin.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("기간별정산")
@ActiveProfiles("test")
//@ActiveProfiles("ec")
class CalculatesIntTest extends IntegrationTestSupport {

  String token = null;

  @BeforeEach
  void setUp() throws Exception {
    token = login("jenchae@naver.com", "1111", UserType.A);
  }

  @Test
  @DisplayName("기간별 정산 목록이 날짜 조건으로 조회")
  @Transactional
  void a() throws Exception {

    String responseJson = mockMvc.perform(get("/admin/v1/orders/calculates")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-01-01")
            .param("dateTo", "2024-01-02")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.summaries", hasSize(3)))
        .andExpect(jsonPath("$.summaries[0].creatorName").value("박수현"))
        .andExpect(jsonPath("$.summaries[0].paymentAmount").value(3280000))
        .andExpect(jsonPath("$.summaries[0].refundAmount").value(1640000))
        .andExpect(jsonPath("$.summaries[1].creatorName").value("박지수"))
        .andExpect(jsonPath("$.summaries[1].paymentAmount").value(4610000))
        .andExpect(jsonPath("$.summaries[1].refundAmount").value(0))
        .andExpect(jsonPath("$.summaries[2].creatorName").value("한시내"))
        .andExpect(jsonPath("$.summaries[2].paymentAmount").value(1640000))
        .andExpect(jsonPath("$.summaries[2].refundAmount").value(0))
        .andExpect(jsonPath("$.orders", hasSize(6)))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
        objectMapper.readTree(responseJson));

    System.out.println(prettyJson);
  }

  @Test
  @DisplayName("기간별 정산 목록이 날짜 및 처리자로 조회")
  @Transactional
  void b() throws Exception {

    String responseJson = mockMvc.perform(get("/admin/v1/orders/calculates")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-06-01")
            .param("dateTo", "2024-06-30")
            .param("creatorName", " ")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orders[*].id").isNotEmpty())
        .andExpect(jsonPath("$.orders[*].userId").isNotEmpty())
//        .andExpect(jsonPath("$.summaries", hasSize(1)))
//        .andExpect(jsonPath("$.summaries[0].creatorName").value("박수현"))
//        .andExpect(jsonPath("$.summaries[0].paymentAmount").value(3280000))
//        .andExpect(jsonPath("$.summaries[0].refundAmount").value(1640000))
//        .andExpect(jsonPath("$.orders", hasSize(3)))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
        objectMapper.readTree(responseJson));

    System.out.println(prettyJson);
  }

}