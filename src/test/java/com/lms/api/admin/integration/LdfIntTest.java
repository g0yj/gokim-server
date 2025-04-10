package com.lms.api.admin.integration;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.lms.api.common.dto.UserType;
import com.lms.api.common.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("LDF 시나리오")
@ActiveProfiles("test")
public class LdfIntTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LdfRepository ldfRepository;
  @Autowired
  private EmailRepository emailRepository;

  private final String teacherId = "M1709859614116619";

  private final String CREATE_USER = """
      {
           "address": "경기 안산시 상록구 가루개로 42-15 (양상동)",
           "addressType": "H",
           "cellPhone": "010-1234-1234",
           "company": "등록",
           "coursePurposes": ["STUDY_ABROAD","TEST"],
           "detailedAddress": "detailedAddress",
           "email": "3333@naver.com",
           "etcLanguage" : "etcLanguage",
           "firstNameEn": "firstNameEn",
           "foreignCountry": "foreignCountry",
           "foreignPeriod": "foreignPeriod",
           "foreignPurpose": "foreignPurpose",
           "gender": "M",
           "isActive": false,
           "isOfficeWorker":false,
           "isReceiveEmail": false,
           "isReceiveSms": false,
           "joinPath": "ONLINE",
           "languageSkills": [{"languageTest": "TOEIC", "score": "900점이상"}, {"languageTest": "TOEIC_S", "score": "8(190-200)"}],
           "languages": ["EN", "CN", "JP"],
           "lastNameEn": "lastNameEn",
           "loginId":  "3333@naver.com",
           "name": "등록:홍길동",
           "nickname": "nickname",
           "note": "note",
           "password": "1111",
           "phone": "031-232-1234",
           "phoneType":  "H",
           "position": "position",
           "textbook":  "textbook",
           "type": "S",
           "zipcode":"15208"
           }
      """.trim();

  String adminToken = null;

  @BeforeEach
  void setUp() throws Exception {
    adminToken = login("jenchae@naver.com", "1111", UserType.A);
  }

  @Test
  @DisplayName("LDF 생성 - id가 M1718674284012032이고 reservationId가 2730757일 때")
  @Transactional
  void a() throws Exception {
    String id = "M1718674284012032";
    String content = """
        {
            "id": 2730757,
            "lesson": "Sample Lesson",
            "contentSp": "Sample Content SP",
            "contentV": "Sample Content V",
            "contentSg": "Sample Content SG",
            "contentC": "Sample Content C"
        }
        """.trim();

    mockMvc.perform(post("/admin/v1/users/{id}/ldfs", id)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("LDF 조회 테스트")
  @Transactional
  void b() throws Exception {
    String id = "M1708509610806759";
    Long ldfId = 387970L;
    mockMvc.perform(get("/admin/v1/users/{id}/ldfs/{ldfId}", id, ldfId)
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName(" LDF 평가 등록")
  @Transactional
  void c() throws Exception {
    // Step 1: Login and retrieve token
    String loginContent = """
        {
            "type": "S",
            "id": "erin1217harry@naver.com",
            "password": "1111"
        }
        """;

    String token = mockMvc.perform(post("/mobile/v1/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginContent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String adminToken = objectMapper.readTree(token).get("token").asText();

    // Step 2: Register lesson feedback
    String feedbackContent = """
        {
            "id": 405726,
            "grade": 4.5,
            "evaluation": "수업만족도good"
        }
        """;

    mockMvc.perform(put("/mobile/v1/feedback")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(feedbackContent))
        .andExpect(status().isOk())
        .andDo(print());

    // Step 3: Retrieve LDF
    Long id = 2720891L;

    mockMvc.perform(get("/mobile/v1/feedback/{id}", id)
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(405726))
        .andExpect(jsonPath("$.grade").value(4.5))
        .andExpect(jsonPath("$.evaluation").value("수업만족도good"))
        .andDo(print());
  }

  @Test
  @DisplayName("평가현황 목록 조회 - 2023-12-04 날짜로")
  @Transactional
  void d() throws Exception {

    String date = "2023-12-04";
    String requestParams = "?date=" + date;

    mockMvc.perform(get("/admin/v1/statistics/evaluations" + requestParams)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath(
            "$[?(@.teacherName == 'Alex' && @.teacherId == 'M1487640799950353' && @.gradeCount == 1 && @.gradeAvg == 3.0 && @.total == 3.0 && @.date == '2023-12-01')]").exists())
        .andDo(print());
  }

  @Test
  @DisplayName("LDF 수정 및 조회 테스트")
  @Transactional
  void e() throws Exception {

    // Step 2: Update LDF
    String updateContent = """
        {
            "lesson": "Sample Lesson",
            "contentSp": "Sample Content SP",
            "contentV": "Sample Content V",
            "contentSg": "Sample Content SG",
            "contentC": "Sample Content C"
        }
        """;

    mockMvc.perform(put("/admin/v1/users/ldfs/10740")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(updateContent))
        .andExpect(status().isOk())
        .andDo(print());

    // Step 3: Retrieve LDF
    String id = "M1446473872875003";
    Long ldfId = 10740L;

    mockMvc.perform(get("/admin/v1/users/{id}/ldfs/{ldfId}", id, ldfId)
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ldfId))
        .andExpect(jsonPath("$.lesson").value("Sample Lesson"))
        .andExpect(jsonPath("$.contentSp").value("Sample Content SP"))
        .andExpect(jsonPath("$.contentV").value("Sample Content V"))
        .andExpect(jsonPath("$.contentSg").value("Sample Content SG"))
        .andExpect(jsonPath("$.contentC").value("Sample Content C"))
        .andDo(print());
  }

  @Test
  @DisplayName("LDF 목록 조회 테스트")
  @Transactional
  void f() throws Exception {

    // LDF 생성
    a();

    String id = "M1718674284012032";

    MvcResult mvcResult = mockMvc.perform(get("/admin/v1/users/{id}/ldfs", id)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andReturn();

    JsonNode rootNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    JsonNode listNode = rootNode.get("list");

    Long ldfId = null;
    if (listNode.isArray()) {
      for (JsonNode item : listNode) {
        JsonNode ldfIdNode = item.get("ldfId");
        if (ldfIdNode != null && !ldfIdNode.isNull()) {
          ldfId = ldfIdNode.asLong();
        }
      }
    }

    assertNotNull(ldfId);

    LdfEntity ldfEntity = ldfRepository.findById(ldfId).orElseThrow();

    EmailEntity emailEntity = new EmailEntity();
    emailEntity.setSenderName("test");
    emailEntity.setSenderEmail("test@test.com");
    emailEntity.setTitle("test");
    emailEntity.setContent("test");
    emailEntity.setRecipientName("test");
    emailEntity.setRecipientEmail("ilovecorea@gmail.com");
    emailEntity.setCreatedBy(id);
    emailEntity.setLdfId(ldfEntity.getId());
    emailRepository.save(emailEntity);

    List<EmailEntity> emailEntities = emailRepository.findAllByLdfId(ldfId);
    assertNotNull(emailEntities);
    assertFalse(emailEntities.isEmpty());

    Long finalLdfId = ldfId;
    boolean hasSpecificLdf = emailEntities.stream()
        .anyMatch(email -> email.getLdfId() != null &&
                           email.getLdfId().equals(finalLdfId));
    for (EmailEntity email : emailEntities) {
      if (email.getLdfId() != null) {
        log.debug("## ldfId:{}, email.ldfId:{}", finalLdfId, email.getLdfId());
      }
    }
    assertTrue(hasSpecificLdf, "특정 ldfId " + finalLdfId + "를 가진 EmailEntity가 존재하지 않습니다.");

    mockMvc.perform(get("/admin/v1/users/{id}/ldfs", id)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.list[?(@.ldfId == " + ldfId + ")].email", Matchers.contains("SENT")))
        .andDo(print());
  }
}
