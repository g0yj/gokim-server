package com.lms.api.admin.controller;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DisplayName("공통코드")
//@ActiveProfiles("test")
@ActiveProfiles("ec")
class AdminControllerTest extends ControllerTestSupport {

  @Autowired
  OrderAdminService orderAdminService;

  @Test
  @DisplayName("공통 코드가 조회")
  void a() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/options")
            .header(AUTHORIZATION, token)
            .param("fields", "TEACHERS")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.teachers").isArray())
        .andExpect(jsonPath("$.teachers.length()").value(greaterThan(0)))
        .andDo(print());

    mockMvc.perform(get("/admin/v1/commonCode")
            .header(AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());

    mockMvc.perform(get("/admin/v1/commonCode")
            .header(AUTHORIZATION, token)
            .param("codeGroup", "300")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("연락처 중복 체크")
  void b() throws Exception {
    String content = """
        {
          "cellPhone" : "010-8962-0954"
        }
        """.trim();
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(post("/admin/v1/consultations/cellphone")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("삼당고객목록")
  void c() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/consultations")
            .param("search", "name")
            .param("keyword", "이혜선")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("상담고객 상세 조회")
  void d() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/consultations/{id}", 27901)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("기간별 정산 목록이 날짜 및 처리자로 조회")
  void e() throws Exception {

//    SearchOrders searchOrders = SearchOrders.builder()
//        .dateFrom(DateUtils.parseDate("2024-10-11"))
//        .dateTo(DateUtils.parseDate("2024-10-11"))
//        .build();
//    List<Order> orders = orderAdminService.listOrders(searchOrders);

    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/orders/calculates")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-11")
            .param("dateTo", "2024-10-11")
            .param("creatorName", " ")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("CGT 수업 생성 테스트")
  @Transactional
  void f() throws Exception {
    // Prepare the request body
    String requestBody = """
        {
            "date": "2024-10-14",
            "startTime": "07:00",
            "teacherId": "M1400118325784523",
            "type": "CGT",
            "reservationLimit": 1
        }
        """;

    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(put("/admin/v1/teachers/cgt")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(requestBody))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  void g() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/teachers/cgt")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  void h() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/teachers/cgttimes")
            .contentType(MediaType.APPLICATION_JSON)
            .param("date", "2024-10-14")
            .param("teacherId", "M1400118325784523")
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @Transactional
  void i() throws Exception {
    String content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
          "shortCourseYN":"Y",
          "price":60000
        }
        """.trim();
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(post("/admin/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    MvcResult mvcResult = mockMvc.perform(get("/admin/v1/products/list")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(json);

    // "테스트상품"의 id 추출
    String productId = null;
    for (JsonNode node : jsonNode) {
      if ("테스트상품".equals(node.get("name").asText())) {
        productId = node.get("id").asText();
        break;
      }
    }

    assertNotNull(productId);

    mockMvc.perform(get("/admin/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.shortCourseYN").value("Y"))
        .andDo(print());

    content = """
        {
            "name":"테스트상품",
            "curriculumYN":"N",
            "shortCourseYN":"Y",
            "price":60000
        }
        """;
    mockMvc.perform(put("/admin/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isBadRequest())
        .andDo(print());

    content = """
        {
            "name":"테스트상품",
            "curriculumYN":"Y",
            "shortCourseYN":"Y",
            "price":60000
        }
        """;
    mockMvc.perform(put("/admin/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    mockMvc.perform(get("/admin/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.shortCourseYN").value("Y"))
        .andDo(print());

    content = """
        {
            "name":"테스트상품",
            "curriculumYN":"Y",
            "shortCourseYN":"N",
            "price":60000
        }
        """;
    mockMvc.perform(put("/admin/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    mockMvc.perform(get("/admin/v1/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.shortCourseYN").value("N"))
        .andDo(print());
  }

  @Test
  public void test() throws Exception {
    String id = "M1728632115402605";
    String orderId = "O1728961417661308";

    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/users/{id}/orders/{orderId}", id, orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void test1() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/users")
            .param("type", "A")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void test2() throws Exception {
    String content = """
        {
            "dateFrom": "2024-10-20",
            "dateTo": "2024-10-26",
            "workTime": "AM_16",
            "schedules": [
                {
                    "date": "2024-10-22",
                    "time": "08:30"
                }
            ]
        }
        """;
    String teacherId = "M1400118325784523";
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
