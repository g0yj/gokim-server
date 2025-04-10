package com.lms.api.admin.integration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import com.lms.api.support.ControllerTestSupport;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

public abstract class IntegrationTestSupport extends ControllerTestSupport {

  /**
   * 회원 등록
   */
  public ResultActions createUser(String token, String content) throws Exception {
    return mockMvc.perform(post("/admin/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
  }

  /**
   * 회원 조회
   */
  public ResultActions getUser(String token, String type, String search, String keyword)
      throws Exception {
    return mockMvc.perform(get("/admin/v1/users")
            .param("type", type)
            .param("search", search)
            .param("keyword", keyword)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  /**
   * 응답 추출
   */
  public String extractOtherFieldFromList(ResultActions resultActions, String fieldName,
      String expectedValue, String targetField) throws Exception {
    MvcResult mvcResult = resultActions
        .andExpect(status().isOk())
        .andReturn();  // 응답 결과를 반환

    // 응답 본문을 String으로 변환
    String responseBody = mvcResult.getResponse().getContentAsString();

    // ObjectMapper를 사용해 JSON 파싱
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);

    // list 배열에서 "name": "테스트상품"인 항목의 다른 필드 값 추출
    JsonNode listNode = jsonNode.get("list");
    if (listNode != null && listNode.isArray()) {
      for (JsonNode arrayItem : listNode) {
        // "name" 필드 값이 expectedValue와 일치하는지 확인
        if (arrayItem.has(fieldName) && expectedValue.equals(arrayItem.get(fieldName).asText())) {
          // targetField의 값을 반환
          return arrayItem.get(targetField).asText();
        }
      }
    }

    return null;  // 조건에 맞는 항목이 없거나 targetField가 없으면 null 반환
  }

  public String extractOtherFieldFromArray(ResultActions resultActions, String fieldName,
      String expectedValue, String targetField) throws Exception {
    MvcResult mvcResult = resultActions
        .andExpect(status().isOk())
        .andReturn();  // 응답 결과를 반환

    // 응답 본문을 String으로 변환
    String responseBody = mvcResult.getResponse().getContentAsString();

    // ObjectMapper를 사용해 JSON 파싱
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonArray = objectMapper.readTree(responseBody);

    // JSON 응답 자체가 배열인지 확인
    if (jsonArray.isArray()) {
      for (JsonNode arrayItem : jsonArray) {
        // "name" 필드 값이 expectedValue와 일치하는지 확인
        if (arrayItem.has(fieldName) && expectedValue.equals(arrayItem.get(fieldName).asText())) {
          // targetField의 값을 반환
          return arrayItem.get(targetField).asText();
        }
      }
    }

    return null;  // 조건에 맞는 항목이 없거나 targetField가 없으면 null 반환
  }

  public String addFieldToJson(String jsonString, String key, String value) throws Exception {
    // ObjectMapper 인스턴스 생성
    ObjectMapper objectMapper = new ObjectMapper();

    // JSON 문자열을 ObjectNode로 변환
    ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(jsonString);

    // 새로운 필드 추가
    jsonNode.put(key, value);

    // 수정된 JSON 문자열로 반환
    return objectMapper.writeValueAsString(jsonNode);
  }

  public String extractFieldFromResponse(MvcResult result, String fieldName) throws Exception {
    // 응답 본문을 문자열로 가져오기
    String responseBody = result.getResponse().getContentAsString();

    // ObjectMapper를 사용하여 JSON 파싱
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);

    // orderId 값 추출
    return jsonNode.get(fieldName).asText();
  }

  public <T> T extractValueFromJson(MvcResult result, String jsonPath, Class<T> clazz)
      throws Exception {
    // 응답 본문을 JSON으로 변환
    Object value = JsonPath.read(result.getResponse().getContentAsString(), jsonPath);

    // 값이 요청한 타입과 일치하는지 확인하고 반환
    if (clazz.isInstance(value)) {
      return clazz.cast(value);
    }

    // 리스트 타입이 아닌 경우, 단일 값을 처리
    if (clazz.equals(List.class) && value instanceof List) {
      return clazz.cast(value);
    } else if (clazz.equals(String.class) && !(value instanceof List)) {
      return clazz.cast(value.toString());
    }

    throw new IllegalArgumentException("Unexpected type: " + value.getClass().getName());
  }

  public String prettyPrint(ResultActions resultActions) throws Exception {
    String json = resultActions.andReturn().getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
  }

  public String generateRandomUserId() {
    return UUID.randomUUID().toString() + "@naver.com";
  }

  public Map<String, Object> getResultMap(MvcResult mvcResult) throws Exception {
    return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
  }
}
