package com.lms.api.admin.integration;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("예약시나리오")
@ActiveProfiles("test")
public class ReservationInt2Test extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CourseRepository courseRepository;

  private final String teacherId = "M1709859614116619";

  private final String CREATE_USER = """
      {
           "address": "경기 안산시 상록구 가루개로 42-15 (양상동)",
           "addressType": "H",
           "cellPhone": "010-1234-1234",
           "company": "등록",
           "coursePurposes": ["STUDY_ABROAD","TEST"],
           "detailedAddress": "detailedAddress",
           "email": "%s",
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
           "loginId":  "%s",
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
  String loginId = null;

  @BeforeEach
  void setUp() throws Exception {
    loginId = generateRandomUserId();
    adminToken = login("jenchae@naver.com", "1111", UserType.A);
  }

  @Test
  @DisplayName("예약 성공")
  @Transactional
  void b() throws Exception {
    String content = """
        {
              "dateFrom": "2024-10-14",
              "dateTo": "2024-10-14",
              "workTime": "SP_16",
              "schedules": [
                  {
                      "date": "2024-10-14",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "08:00"
                  }
              ]
          }
        """;
    String teacherId = "M1709859614116619";
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    createUser(adminToken, CREATE_USER.formatted(loginId, loginId));
    ResultActions resultActions = getUser(adminToken, "S", "email", loginId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list").value(notNullValue()))
        .andDo(print());

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    assertNotNull(userId);

    content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
          "shortCourseYN":"Y",
          "price":60000
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    resultActions = mockMvc.perform(get("/admin/v1/products/list")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    final String productId = extractOtherFieldFromArray(resultActions, "name", "테스트상품", "id");
    assertNotNull(productId);

    // 6. 상품주문
    content = """
        {
            "productId": "%s",
            "quantity": 10,
            "teacherId": "%s",
            "assistantTeacherId": "%s",
            "isRetake" : true,
            "billingAmount": 600000
        }
        """.trim().formatted(productId, teacherId, teacherId);
    MvcResult mvcResult = mockMvc.perform(post("/admin/v1/users/{id}/orders/products", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(notNullValue()))
        .andDo(print())
        .andReturn();

    final String orderId = extractFieldFromResponse(mvcResult, "orderId");
    assertNotNull(orderId);

    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-date", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("dateFrom", "2024-10-14")
            .param("dateTo", "2024-10-14")
            .param("teacherId", teacherId)
            .param("assistantTeacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    JsonNode rootNode = objectMapper.readTree(jsonResponse);
    JsonNode schedulesNode = rootNode.path("schedules");
    List<Long> teacherScheduleIds = new ArrayList<>();
    // schedules 배열을 순회하면서 teacherScheduleId가 null이 아닌 항목만 리스트에 추가
    for (JsonNode schedule : schedulesNode) {
      JsonNode reservations = schedule.path("reservations");
      Iterator<JsonNode> iterator = reservations.elements();

      while (iterator.hasNext()) {
        JsonNode reservation = iterator.next();
        JsonNode teacherScheduleIdNode = reservation.path("teacherScheduleId");

        // teacherScheduleId가 null이 아닌 경우 Long 리스트에 추가
        if (!teacherScheduleIdNode.isNull()) {
          long teacherScheduleId = teacherScheduleIdNode.asLong();
          teacherScheduleIds.add(teacherScheduleId);
        }
      }
    }

    log.debug("## teacherScheduleIds:{}", teacherScheduleIds);

    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/courses", userId)
        .param("status", "VALID")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    String courseId = extractOtherFieldFromList(resultActions, "listNumber", "1", "id");

    content = """
        {
          "courseId" : %s,
          "scheduleIds" : %s
        }
        """.formatted(courseId, teacherScheduleIds.subList(0, 1));

    mockMvc.perform(post("/admin/v1/users/{id}/reservations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    mvcResult = mockMvc.perform(get("/admin/v1/users/{id}/reservations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("courseId", courseId)
            .param("excludeCancel", "true")
            .param("excludeAttendance", "true")
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    Long reservationId = null;
    rootNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    JsonNode listNode = rootNode.path("list");

    if (listNode.isArray() && listNode.size() > 0) {
      JsonNode firstItem = listNode.get(0);
      reservationId = firstItem.path("id").asLong();
    }
    assertNotNull(reservationId);

    content = """
      {
        "reservations": [
          {
            "id": %d,
            "isCancel": true,
            "cancelReason": "취소 사유"
          }
        ]
      }
    """.formatted(reservationId);
    mockMvc.perform(put("/admin/v1/users/{id}/reservations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());


    CourseEntity courseEntity = courseRepository.findById(Long.valueOf(courseId)).get();
    log.debug("courseEntity:{}", courseEntity);

//    mockMvc.perform(get("/admin/v1/users/{id}/courses/{courseId}/histories", userId, courseId)
//            .header(AUTHORIZATION, adminToken))
//        .andExpect(status().isOk())
//        .andDo(print());

  }
}
