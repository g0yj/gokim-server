package com.lms.api.admin.integration;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
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
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
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
public class ReservationIntTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;

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
  @DisplayName("관리자 예약 실패 (취소조건)")
  @Transactional
  void a() throws Exception {
    String content = """
        {
              "dateFrom": "2024-09-09",
              "dateTo": "2024-09-14",
              "workTime": "SP_16",
              "schedules": [
                  {
                      "date": "2024-09-09",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-09-09",
                      "time": "11:30"
                  },
                 {
                      "date": "2024-09-10",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-09-10",
                      "time": "11:30"
                  },
                 {
                      "date": "2024-09-13",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-09-13",
                      "time": "11:30"
                  },
                 {
                      "date": "2024-09-14",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-09-14",
                      "time": "11:30"
                  }
              ]
          }
        """;
    String teacherId = "M1709859614116619";
    // 1. 강의 스케쥴 등록
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 1: 강사스케쥴 등록", () -> System.out.println("Step 1: 강사스케쥴 등록"));
    //----------------------------------------------------------------------------------------------

    // 2. 강사 스케쥴 조회
    mockMvc.perform(get("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("dateFrom", "2024-09-08")
            .param("dateTo", "2024-09-14")
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.schedules[?(@.date == '2024-09-08' && @.isScheduled == true)]", hasSize(0)))
        .andExpect(jsonPath("$.schedules[?(@.date == '2024-09-09' && @.isScheduled == true)]",
            hasSize(12)))
        .andExpect(jsonPath("$.schedules[?(@.date == '2024-09-10' && @.isScheduled == true)]",
            hasSize(12)))
        .andExpect(
            jsonPath("$.schedules[?(@.date == '2024-09-11' && @.isScheduled == true)]", hasSize(0)))
        .andExpect(
            jsonPath("$.schedules[?(@.date == '2024-09-12' && @.isScheduled == true)]", hasSize(0)))
        .andExpect(jsonPath("$.schedules[?(@.date == '2024-09-13' && @.isScheduled == true)]",
            hasSize(12)))
        .andExpect(jsonPath("$.schedules[?(@.date == '2024-09-14' && @.isScheduled == true)]",
            hasSize(12)))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 2: 강사스케쥴 조회", () -> System.out.println("Step 1: 강사스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 3. 회원등록
    createUser(adminToken, CREATE_USER.formatted(loginId, loginId));
    //----------------------------------------------------------------------------------------------
    step("Step 3: 회원 등록", () -> System.out.println("Step 3: 회원 등록"));
    //----------------------------------------------------------------------------------------------

    // 3.1 회원조회
    ResultActions resultActions = getUser(adminToken, "S", "email", loginId)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list").value(notNullValue()))
        .andDo(print());

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    log.info("등록된 회원({}) 조회..", userId);

    // 4. 상품등록
    content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
          "price":60000
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    log.info("상품 등록..");
    //----------------------------------------------------------------------------------------------
    step("Step 4: 상품 등록", () -> System.out.println("Step 4: 상품 등록"));
    //----------------------------------------------------------------------------------------------

    // 5. 상품목록조회
    resultActions = mockMvc.perform(get("/admin/v1/products/list")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    final String productId = extractOtherFieldFromArray(resultActions, "name", "테스트상품", "id");
    assertNotNull(productId);
    log.info("등록된 상품({}) 조회..", productId);
    //----------------------------------------------------------------------------------------------
    step("Step 5: 상품목록 조회", () -> System.out.println("Step 5: 상품목록 조회"));
    //----------------------------------------------------------------------------------------------

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
    log.info("상품 주문({})..", orderId);
    //----------------------------------------------------------------------------------------------
    step("Step 6: 상품 주문", () -> System.out.println("Step 6: 상품 주문"));
    //----------------------------------------------------------------------------------------------

    // 7. 주문탭조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/orders", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.order[0].id").value(orderId))
        .andExpect(jsonPath("$.order[0].supplyAmount").value(600000))
        .andExpect(jsonPath("$.order[0].discountAmount").value(0))
        .andExpect(jsonPath("$.order[0].billingAmount").value(600000))
        .andExpect(jsonPath("$.order[0].paymentAmount").value(0))
        .andExpect(jsonPath("$.order[0].receivableAmount").value(600000))
        .andExpect(jsonPath("$.order[0].createdOn").value(
            startsWith(DateUtils.getString(LocalDate.now()))))
        .andExpect(jsonPath("$.order[0].creatorName").value("채인숙"))
        .andExpect(jsonPath("$.order[0].orderProductName").value("테스트상품/1개월/10회"))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 7: 주문탭 조회", () -> System.out.println("Step 7: 주문탭 조회"));
    //----------------------------------------------------------------------------------------------

    // 8. 회원 주문 조회
    mockMvc.perform(get("/admin/v1/users/{id}/orders/{orderId}", userId, orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId))
        .andExpect(jsonPath("$.supplyAmount").value(600000))
        .andExpect(jsonPath("$.discountAmount").value(0))
        .andExpect(jsonPath("$.billingAmount").value(600000))
        .andExpect(jsonPath("$.refundAmount").value(0))
        .andExpect(jsonPath("$.orderProducts", hasSize(1)))

        .andExpect(jsonPath("$.orderProducts[0].id").value(startsWith("I")))
        .andExpect(jsonPath("$.orderProducts[0].name").value("테스트상품/1개월/10회"))
        .andExpect(jsonPath("$.orderProducts[0].amount").value(600000))
        .andExpect(jsonPath("$.orderProducts[0].discountAmount").value(0))
        .andExpect(jsonPath("$.orderProducts[0].billingAmount").value(600000))
        .andExpect(jsonPath("$.orderProducts[0].refundAmount").value(0))
        .andExpect(jsonPath("$.orderProducts[0].productType", is("Y")))
        .andExpect(jsonPath("$.orderProducts[0].createdOn").value(
            startsWith(DateUtils.getString(LocalDate.now()))))
        .andExpect(jsonPath("$.orderProducts[0].orderType").value("신규"))
        .andExpect(jsonPath("$.orderProducts[0].refundType").value("CANCELABLE"))
        .andExpect(jsonPath("$.orderProducts[0].retake").value(false))
        .andExpect(jsonPath("$.orderProducts[0].hasReservations").value(false))
        .andExpect(jsonPath("$.orderProducts[0].hasPayments").value(false))

        .andExpect(jsonPath("$.isCancelable").value(true))
        .andDo(print())
        .andReturn();
    //----------------------------------------------------------------------------------------------
    step("Step 8: 회원주문 조회", () -> System.out.println("Step 8: 회원주문 조회"));
    //----------------------------------------------------------------------------------------------

    // 9. 결제등록
    content = """
        {
             "type": "I",
             "paymentDate": "2024-08-30",
             "cashAmount": null,
             "isReceiptIssued": false,
             "receiptNumber": "",
             "depositAmount": null,
             "accountHolder": "테스터",
             "receivableAmount": 0,
             "recallDate": "2024-09-01",
             "receivableReason": "test",
             "memo": "test",
             "cards": [
                 {
                     "amount": 200000,
                     "code": "2090073",
                     "cardNumber": "12345",
                     "installmentMonths": 0,
                     "approvalNumber": ""
                 },
                 {
                     "amount": 400000,
                     "code": "2090074",
                     "cardNumber": "00000",
                     "installmentMonths": 0,
                     "approvalNumber": ""
                 }
             ]
         }
        """;
    mockMvc.perform(post("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    log.info("결제 등록..");
    //----------------------------------------------------------------------------------------------
    step("Step 9: 결제 등록", () -> System.out.println("Step 9: 결제 등록"));
    //----------------------------------------------------------------------------------------------

    // 10.예약탭 실행 시 과정명 선택
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/courses", userId)
            .param("status", "VALID")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())  // HTTP 상태 코드가 200인지 확인
        .andExpect(jsonPath("$.list[0].listNumber").value(1))
        .andExpect(jsonPath("$.list[0].id").value(notNullValue()))
        .andExpect(jsonPath("$.list[0].name").value("테스트상품/10.0회"))
        .andExpect(jsonPath("$.list[0].lessonCount").value(10.0))
        .andExpect(jsonPath("$.list[0].assignmentCount").value(0.0))
        .andExpect(jsonPath("$.list[0].remainCount").value(10.0))
        .andExpect(
            jsonPath("$.list[0].startDate").value(DateUtils.getString(LocalDate.now().plusDays(7))))
        .andExpect(jsonPath("$.list[0].endDate").value(
            DateUtils.getString(LocalDate.now().plusDays(7).plusMonths(3).minusDays(1))))
        .andExpect(jsonPath("$.list[0].teacherId").value(teacherId))
        .andExpect(jsonPath("$.list[0].teacherName").value("Austin"))
        .andExpect(jsonPath("$.list[0].assistantTeacherId").value(teacherId))
        .andExpect(jsonPath("$.list[0].assistantTeacherName").value("Austin"))
        .andExpect(jsonPath("$.list[0].createDate").value(DateUtils.getString(LocalDate.now())))
        .andExpect(jsonPath("$.list[0].status").value("WAITING"))
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.limit").value(10))
        .andExpect(jsonPath("$.pageSize").value(10))
        .andExpect(jsonPath("$.startPage").value(1))
        .andExpect(jsonPath("$.totalPage").value(1))
        .andExpect(jsonPath("$.endPage").value(1))
        .andExpect(jsonPath("$.isFirst").value(true))
        .andExpect(jsonPath("$.isLast").value(true))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.hasPrev").value(false))
        .andDo(print());

    String courseId = extractOtherFieldFromList(resultActions, "listNumber", "1", "id");
    assertNotNull(courseId);
    log.info("courseId: {}", courseId);
    //----------------------------------------------------------------------------------------------
    step("Step 10: 예약탭 실행 과정명 선택", () -> System.out.println("Step 10: 예약탭 실행 과정명 선택"));
    //----------------------------------------------------------------------------------------------

    // 11.예약탭 목록 조회
    mockMvc.perform(get("/admin/v1/users/{id}/reservations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("courseId", courseId)
            .param("page", "1")
            .param("dateFrom", "2024-09-09")
            .param("dateTo", "2024-09-14")
            .param("excludeCancel", "true")
            .param("excludeAttendance", "true")
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(0)))  // list가 빈 배열인지 확인
        .andExpect(jsonPath("$.totalCount").value(0))  // totalCount가 0인지 확인
        .andExpect(jsonPath("$.page").value(1))  // page가 1인지 확인
        .andExpect(jsonPath("$.limit").value(10))  // limit이 10인지 확인
        .andExpect(jsonPath("$.pageSize").value(10))  // pageSize가 10인지 확인
        .andExpect(jsonPath("$.startPage").value(1))  // startPage가 1인지 확인
        .andExpect(jsonPath("$.totalPage").value(0))  // totalPage가 0인지 확인
        .andExpect(jsonPath("$.endPage").value(0))  // endPage가 0인지 확인
        .andExpect(jsonPath("$.isFirst").value(true))  // isFirst가 true인지 확인
        .andExpect(jsonPath("$.isLast").value(true))  // isLast가 true인지 확인
        .andExpect(jsonPath("$.hasNext").value(false))  // hasNext가 false인지 확인
        .andExpect(jsonPath("$.hasPrev").value(false)) // hasPrev가 false인지 확인
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 11: 예약탭 목록 조회", () -> System.out.println("Step 11: 예약탭 목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 12. 예약 가능 스케줄 조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-date", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("dateFrom", "2024-09-08") // 조회 시작일이 고정값?
            .param("dateTo", "2024-09-14") // 조회 종료일이 고정값?
            .param("teacherId", teacherId)
            .param("assistantTeacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath(
            "$.schedules[?(@.time == '06:00')].reservations[?(@.date == '2024-09-09')].teachers[*].id",
            hasItem(teacherId)))
        .andDo(print());

    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    JsonNode rootNode = objectMapper.readTree(jsonResponse);

    // "06:00" 및 "06:30" 시간의 2024-09-09에 해당하는 teacherScheduleId를 저장할 리스트
    List<Integer> availableScheduleIds = new ArrayList<>();

    // schedules 배열을 순회하며 해당 조건을 확인
    JsonNode schedules = rootNode.get("schedules");
    if (schedules.isArray()) {
      for (JsonNode schedule : schedules) {
        String time = schedule.get("time").asText();
        if ("06:00".equals(time) || "06:30".equals(time)) {
          JsonNode reservations = schedule.get("reservations");
          if (reservations.isArray()) {
            for (JsonNode reservation : reservations) {
              String date = reservation.get("date").asText();
              String teacherStatus = reservation.get("teacherStatus").asText();
              if ("2024-09-09".equals(date) && "AVAILABLE".equals(teacherStatus)) {
                // teachers 배열에서 id가 "M1709859614116619"인지 확인
                JsonNode teachers = reservation.get("teachers");
                if (teachers.isArray()) {
                  for (JsonNode teacher : teachers) {
                    if (teacherId.equals(teacher.get("id").asText())) {
                      JsonNode teacherScheduleIdNode = reservation.get("teacherScheduleId");
                      if (teacherScheduleIdNode != null && !teacherScheduleIdNode.isNull()) {
                        int teacherScheduleId = teacherScheduleIdNode.asInt();
                        availableScheduleIds.add(teacherScheduleId);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    log.info("Available Teacher Schedule IDs: " + availableScheduleIds);
    //----------------------------------------------------------------------------------------------
    step("Step 12: 예약가능스케쥴 조회", () -> System.out.println("Step 12: 예약가능스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 13. 예약
    content = """
        {
          "courseId" : %s,
          "scheduleIds" : %s
        }
        """.formatted(courseId, availableScheduleIds);

    mockMvc.perform(post("/admin/v1/users/{id}/reservations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 13: 예약", () -> System.out.println("Step 13: 예약"));
    //----------------------------------------------------------------------------------------------

    // 14. 예약 가능 스케줄 조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-date", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("dateFrom", "2024-09-08") // 조회 시작일이 고정값?
            .param("dateTo", "2024-09-14") // 조회 종료일이 고정값?
            .param("teacherId", teacherId)
            .param("assistantTeacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    rootNode = objectMapper.readTree(jsonResponse);

    // schedules 배열을 순회하며 09-09 06:00 및 06:30 시간의 조건을 확인
    schedules = rootNode.get("schedules");
    assertTrue(schedules.isArray(), "Schedules 배열이어야 합니다.");

    boolean conditionsMet = false;

    for (JsonNode schedule : schedules) {
      String time = schedule.get("time").asText();
      if ("06:00".equals(time) || "06:30".equals(time)) {
        JsonNode reservations = schedule.get("reservations");
        assertTrue(reservations.isArray(), "Reservations 배열이어야 합니다.");

        for (JsonNode reservation : reservations) {
          String date = reservation.get("date").asText();
          if ("2024-09-09".equals(date)) {
            // 조건 1: teachers 배열이 비어있는지 확인
            boolean isTeachersEmpty = reservation.get("teachers").isEmpty();

            // 조건 2: teacherStatus가 "USERS"인지 확인
            boolean isTeacherStatusUsers = "USERS".equals(
                reservation.get("teacherStatus").asText());

            // 조건 3: assistantTeacherStatus가 "USERS"인지 확인
            boolean isAssistantTeacherStatusUsers = "USERS".equals(
                reservation.get("assistantTeacherStatus").asText());

            // 세 가지 조건 모두 만족하면 true
            if (isTeachersEmpty && isTeacherStatusUsers && isAssistantTeacherStatusUsers) {
              conditionsMet = true;
            }
          }
        }
      }
    }

    // 적어도 한 번은 조건을 만족해야 함
    assertTrue(conditionsMet, "조건을 만족하는 예약이 존재해야 합니다.");
    //----------------------------------------------------------------------------------------------
    step("Step 14: 예약가능스케쥴 조회", () -> System.out.println("Step 14: 예약가능스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 15. 강의시간표
    mockMvc.perform(get("/admin/v1/reservations/schedules")
            .param("date", "2024-09-09")
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        // time이 06:00이고 teacherId가 M1709859614116619, status가 R인 reservation이 있는지 확인
        .andExpect(jsonPath(
            "$.schedules[?(@.time == '06:00')].reservations[?(@.teacherId == 'M1709859614116619' && @.status == 'R')]"
        ).exists())
        // time이 06:30이고 teacherId가 M1709859614116619, status가 R인 reservation이 있는지 확인
        .andExpect(jsonPath(
            "$.schedules[?(@.time == '06:30')].reservations[?(@.teacherId == 'M1709859614116619' && @.status == 'R')]"
        ).exists())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 15: 강의 시간표", () -> System.out.println("Step 15: 강의 시간표"));
    //----------------------------------------------------------------------------------------------

    // 16. 예약목록조회
    // 예약된 스케줄 식별키가 2개라고 가정
    int expectedReservationCount = 2;
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/reservations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("courseId", courseId)
            .param("page", "1")
            .param("limit", "20")
            .param("dateFrom", "2024-09-09")
            .param("dateTo", "2024-09-14")
            .param("excludeCancel", "true")
            .param("excludeAttendance", "true")
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        // 예약 식별키가 expectedReservationCount 개수인지 확인
        .andExpect(jsonPath("$.list", hasSize(expectedReservationCount)))
        // 첫 번째 예약의 attendanceStatus가 "예약"인지 확인
        .andExpect(jsonPath("$.list[0].attendanceStatus", is("예약")))
        // 두 번째 예약의 attendanceStatus가 "예약"인지 확인
        .andExpect(jsonPath("$.list[1].attendanceStatus", is("예약")))
        .andDo(print());

    jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    rootNode = objectMapper.readTree(jsonResponse);

    // id 값을 저장할 리스트
    List<Integer> reservationIds = new ArrayList<>();

    // list 배열에서 id 추출
    JsonNode listNode = rootNode.get("list");
    if (listNode.isArray()) {
      for (JsonNode item : listNode) {
        int id = item.get("id").asInt();
        reservationIds.add(id);
      }
    }
    //----------------------------------------------------------------------------------------------
    step("Step 16: 예약목록 조회", () -> System.out.println("Step 16: 예약목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 17. 예약 취소
    content = """
        {
            "reservations": [
                {
                    "id": %s,
                    "isCancel": true,
                    "cancelReason": "취소 사유"
                }
            ]
        }
        """.formatted(reservationIds.get(0)).trim();
    mockMvc.perform(put("/admin/v1/users/{id}/reservations", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("api-0602"))
            .andExpect(jsonPath("$.message").value("취소할 수 없는 예약을 포함하고 있습니다. 취소는 2일 전(일요일, 공휴일 제외)까지 가능합니다"))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 17: 예약 취소", () -> System.out.println("Step 17: 예약 취소"));
    //----------------------------------------------------------------------------------------------

  }

  @Test
  @DisplayName("관리자 예약 성공")
  @Transactional
  void b() throws Exception {
    String content = """
        {
              "dateFrom": "2024-10-09",
              "dateTo": "2024-10-14",
              "workTime": "SP_16",
              "schedules": [
                  {
                      "date": "2024-10-09",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-10-09",
                      "time": "11:30"
                  },
                 {
                      "date": "2024-10-10",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-10-10",
                      "time": "11:30"
                  },
                 {
                      "date": "2024-10-13",
                      "time": "06:00"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "06:30"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "07:00"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "07:30"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "08:00"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-10-13",
                      "time": "11:30"
                  },
                 {
                      "date": "2024-10-14",
                      "time": "06:00"
                  },
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
                  },
                  {
                      "date": "2024-10-14",
                      "time": "08:30"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "09:00"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "09:30"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "10:00"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "10:30"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "11:00"
                  },
                  {
                      "date": "2024-10-14",
                      "time": "11:30"
                  }
              ]
          }
        """;
    String teacherId = "M1709859614116619";
    // 1. 강의 스케쥴 등록
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 1: 강사스케쥴 등록", () -> System.out.println("Step 1: 강사스케쥴 등록"));
    //----------------------------------------------------------------------------------------------

    // 2. 강사 스케쥴 조회
    mockMvc.perform(get("/admin/v1/teachers/{id}/schedules", teacherId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("dateFrom", "2024-10-08")
                    .param("dateTo", "2024-10-14")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(
                    jsonPath("$.schedules[?(@.date == '2024-10-08' && @.isScheduled == true)]", hasSize(0)))
            .andExpect(jsonPath("$.schedules[?(@.date == '2024-10-09' && @.isScheduled == true)]",
                    hasSize(12)))
            .andExpect(jsonPath("$.schedules[?(@.date == '2024-10-10' && @.isScheduled == true)]",
                    hasSize(12)))
            .andExpect(
                    jsonPath("$.schedules[?(@.date == '2024-10-11' && @.isScheduled == true)]", hasSize(0)))
            .andExpect(
                    jsonPath("$.schedules[?(@.date == '2024-10-12' && @.isScheduled == true)]", hasSize(0)))
            .andExpect(jsonPath("$.schedules[?(@.date == '2024-10-13' && @.isScheduled == true)]",
                    hasSize(12)))
            .andExpect(jsonPath("$.schedules[?(@.date == '2024-10-14' && @.isScheduled == true)]",
                    hasSize(12)))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 2: 강사스케쥴 조회", () -> System.out.println("Step 1: 강사스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 3. 회원등록
    createUser(adminToken, CREATE_USER.formatted(loginId, loginId));
    //----------------------------------------------------------------------------------------------
    step("Step 3: 회원 등록", () -> System.out.println("Step 3: 회원 등록"));
    //----------------------------------------------------------------------------------------------

    // 3.1 회원조회
    ResultActions resultActions = getUser(adminToken, "S", "email", loginId)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.list").value(notNullValue()))
            .andDo(print());

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    log.info("등록된 회원({}) 조회..", userId);

    // 4. 상품등록
    content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
          "price":60000
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    log.info("상품 등록..");
    //----------------------------------------------------------------------------------------------
    step("Step 4: 상품 등록", () -> System.out.println("Step 4: 상품 등록"));
    //----------------------------------------------------------------------------------------------

    // 5. 상품목록조회
    resultActions = mockMvc.perform(get("/admin/v1/products/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());

    final String productId = extractOtherFieldFromArray(resultActions, "name", "테스트상품", "id");
    assertNotNull(productId);
    log.info("등록된 상품({}) 조회..", productId);
    //----------------------------------------------------------------------------------------------
    step("Step 5: 상품목록 조회", () -> System.out.println("Step 5: 상품목록 조회"));
    //----------------------------------------------------------------------------------------------

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
    log.info("상품 주문({})..", orderId);
    //----------------------------------------------------------------------------------------------
    step("Step 6: 상품 주문", () -> System.out.println("Step 6: 상품 주문"));
    //----------------------------------------------------------------------------------------------

    // 7. 주문탭조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/orders", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order[0].id").value(orderId))
            .andExpect(jsonPath("$.order[0].supplyAmount").value(600000))
            .andExpect(jsonPath("$.order[0].discountAmount").value(0))
            .andExpect(jsonPath("$.order[0].billingAmount").value(600000))
            .andExpect(jsonPath("$.order[0].paymentAmount").value(0))
            .andExpect(jsonPath("$.order[0].receivableAmount").value(600000))
            .andExpect(jsonPath("$.order[0].createdOn").value(
                    startsWith(DateUtils.getString(LocalDate.now()))))
            .andExpect(jsonPath("$.order[0].creatorName").value("채인숙"))
            .andExpect(jsonPath("$.order[0].orderProductName").value("테스트상품/1개월/10회"))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 7: 주문탭 조회", () -> System.out.println("Step 7: 주문탭 조회"));
    //----------------------------------------------------------------------------------------------

    // 8. 회원 주문 조회
    mockMvc.perform(get("/admin/v1/users/{id}/orders/{orderId}", userId, orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.supplyAmount").value(600000))
            .andExpect(jsonPath("$.discountAmount").value(0))
            .andExpect(jsonPath("$.billingAmount").value(600000))
            .andExpect(jsonPath("$.refundAmount").value(0))
            .andExpect(jsonPath("$.orderProducts", hasSize(1)))

            .andExpect(jsonPath("$.orderProducts[0].id").value(startsWith("I")))
            .andExpect(jsonPath("$.orderProducts[0].name").value("테스트상품/1개월/10회"))
            .andExpect(jsonPath("$.orderProducts[0].amount").value(600000))
            .andExpect(jsonPath("$.orderProducts[0].discountAmount").value(0))
            .andExpect(jsonPath("$.orderProducts[0].billingAmount").value(600000))
            .andExpect(jsonPath("$.orderProducts[0].refundAmount").value(0))
            .andExpect(jsonPath("$.orderProducts[0].productType").value("Y"))
            .andExpect(jsonPath("$.orderProducts[0].createdOn").value(
                    startsWith(DateUtils.getString(LocalDate.now()))))
            .andExpect(jsonPath("$.orderProducts[0].orderType").value("신규"))
            .andExpect(jsonPath("$.orderProducts[0].refundType").value("CANCELABLE"))
            .andExpect(jsonPath("$.orderProducts[0].retake").value(false))
            .andExpect(jsonPath("$.orderProducts[0].hasReservations").value(false))
            .andExpect(jsonPath("$.orderProducts[0].hasPayments").value(false))

            .andExpect(jsonPath("$.isCancelable").value(true))
            .andDo(print())
            .andReturn();
    //----------------------------------------------------------------------------------------------
    step("Step 8: 회원주문 조회", () -> System.out.println("Step 8: 회원주문 조회"));
    //----------------------------------------------------------------------------------------------

    // 9. 결제등록
    content = """
        {
             "type": "I",
             "paymentDate": "2024-08-30",
             "cashAmount": null,
             "isReceiptIssued": false,
             "receiptNumber": "",
             "depositAmount": null,
             "accountHolder": "테스터",
             "receivableAmount": 0,
             "recallDate": "2024-09-01",
             "receivableReason": "test",
             "memo": "test",
             "cards": [
                 {
                     "amount": 200000,
                     "code": "2090073",
                     "cardNumber": "12345",
                     "installmentMonths": 0,
                     "approvalNumber": ""
                 },
                 {
                     "amount": 400000,
                     "code": "2090074",
                     "cardNumber": "00000",
                     "installmentMonths": 0,
                     "approvalNumber": ""
                 }
             ]
         }
        """;
    mockMvc.perform(post("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    log.info("결제 등록..");
    //----------------------------------------------------------------------------------------------
    step("Step 9: 결제 등록", () -> System.out.println("Step 9: 결제 등록"));
    //----------------------------------------------------------------------------------------------

    // 10.예약탭 실행 시 과정명 선택
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/courses", userId)
                    .param("status", "VALID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())  // HTTP 상태 코드가 200인지 확인
            .andExpect(jsonPath("$.list[0].listNumber").value(1))
            .andExpect(jsonPath("$.list[0].id").value(notNullValue()))
            .andExpect(jsonPath("$.list[0].name").value("테스트상품/10.0회"))
            .andExpect(jsonPath("$.list[0].lessonCount").value(10.0))
            .andExpect(jsonPath("$.list[0].assignmentCount").value(0.0))
            .andExpect(jsonPath("$.list[0].remainCount").value(10.0))
            .andExpect(
                    jsonPath("$.list[0].startDate").value(DateUtils.getString(LocalDate.now().plusDays(7))))
            .andExpect(jsonPath("$.list[0].endDate").value(
                    DateUtils.getString(LocalDate.now().plusDays(7).plusMonths(3).minusDays(1))))
            .andExpect(jsonPath("$.list[0].teacherId").value(teacherId))
            .andExpect(jsonPath("$.list[0].teacherName").value("Austin"))
            .andExpect(jsonPath("$.list[0].assistantTeacherId").value(teacherId))
            .andExpect(jsonPath("$.list[0].assistantTeacherName").value("Austin"))
            .andExpect(jsonPath("$.list[0].createDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.list[0].status").value("WAITING"))
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.limit").value(10))
            .andExpect(jsonPath("$.pageSize").value(10))
            .andExpect(jsonPath("$.startPage").value(1))
            .andExpect(jsonPath("$.totalPage").value(1))
            .andExpect(jsonPath("$.endPage").value(1))
            .andExpect(jsonPath("$.isFirst").value(true))
            .andExpect(jsonPath("$.isLast").value(true))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.hasPrev").value(false))
            .andDo(print());

    String courseId = extractOtherFieldFromList(resultActions, "listNumber", "1", "id");
    assertNotNull(courseId);
    log.info("courseId: {}", courseId);
    //----------------------------------------------------------------------------------------------
    step("Step 10: 예약탭 실행 과정명 선택", () -> System.out.println("Step 10: 예약탭 실행 과정명 선택"));
    //----------------------------------------------------------------------------------------------

    // 11.예약탭 목록 조회
    mockMvc.perform(get("/admin/v1/users/{id}/reservations", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("courseId", courseId)
                    .param("page", "1")
                    .param("dateFrom", "2024-10-09")
                    .param("dateTo", "2024-10-14")
                    .param("excludeCancel", "true")
                    .param("excludeAttendance", "true")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.list", hasSize(0)))  // list가 빈 배열인지 확인
            .andExpect(jsonPath("$.totalCount").value(0))  // totalCount가 0인지 확인
            .andExpect(jsonPath("$.page").value(1))  // page가 1인지 확인
            .andExpect(jsonPath("$.limit").value(10))  // limit이 10인지 확인
            .andExpect(jsonPath("$.pageSize").value(10))  // pageSize가 10인지 확인
            .andExpect(jsonPath("$.startPage").value(1))  // startPage가 1인지 확인
            .andExpect(jsonPath("$.totalPage").value(0))  // totalPage가 0인지 확인
            .andExpect(jsonPath("$.endPage").value(0))  // endPage가 0인지 확인
            .andExpect(jsonPath("$.isFirst").value(true))  // isFirst가 true인지 확인
            .andExpect(jsonPath("$.isLast").value(true))  // isLast가 true인지 확인
            .andExpect(jsonPath("$.hasNext").value(false))  // hasNext가 false인지 확인
            .andExpect(jsonPath("$.hasPrev").value(false)) // hasPrev가 false인지 확인
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 11: 예약탭 목록 조회", () -> System.out.println("Step 11: 예약탭 목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 12. 예약 가능 스케줄 조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-date", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("dateFrom", "2024-10-08") // 조회 시작일이 고정값?
                    .param("dateTo", "2024-10-14") // 조회 종료일이 고정값?
                    .param("teacherId", teacherId)
                    .param("assistantTeacherId", teacherId)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                    "$.schedules[?(@.time == '06:00')].reservations[?(@.date == '2024-10-09')].teachers[*].id",
                    hasItem(teacherId)))
            .andDo(print());

    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    JsonNode rootNode = objectMapper.readTree(jsonResponse);

    // "06:00" 및 "06:30" 시간의 2024-09-09에 해당하는 teacherScheduleId를 저장할 리스트
    List<Integer> availableScheduleIds = new ArrayList<>();

    // schedules 배열을 순회하며 해당 조건을 확인
    JsonNode schedules = rootNode.get("schedules");
    if (schedules.isArray()) {
      for (JsonNode schedule : schedules) {
        String time = schedule.get("time").asText();
        if ("06:00".equals(time) || "06:30".equals(time)) {
          JsonNode reservations = schedule.get("reservations");
          if (reservations.isArray()) {
            for (JsonNode reservation : reservations) {
              String date = reservation.get("date").asText();
              String teacherStatus = reservation.get("teacherStatus").asText();
              if ("2024-10-09".equals(date) && "AVAILABLE".equals(teacherStatus)) {
                // teachers 배열에서 id가 "M1709859614116619"인지 확인
                JsonNode teachers = reservation.get("teachers");
                if (teachers.isArray()) {
                  for (JsonNode teacher : teachers) {
                    if (teacherId.equals(teacher.get("id").asText())) {
                      JsonNode teacherScheduleIdNode = reservation.get("teacherScheduleId");
                      if (teacherScheduleIdNode != null && !teacherScheduleIdNode.isNull()) {
                        int teacherScheduleId = teacherScheduleIdNode.asInt();
                        availableScheduleIds.add(teacherScheduleId);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    log.info("Available Teacher Schedule IDs: " + availableScheduleIds);
    //----------------------------------------------------------------------------------------------
    step("Step 12: 예약가능스케쥴 조회", () -> System.out.println("Step 12: 예약가능스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 13. 예약
    content = """
        {
          "courseId" : %s,
          "scheduleIds" : %s
        }
        """.formatted(courseId, availableScheduleIds);

    mockMvc.perform(post("/admin/v1/users/{id}/reservations", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 13: 예약", () -> System.out.println("Step 13: 예약"));
    //----------------------------------------------------------------------------------------------

    // 14. 예약 가능 스케줄 조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-date", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("dateFrom", "2024-10-08") // 조회 시작일이 고정값?
                    .param("dateTo", "2024-10-14") // 조회 종료일이 고정값?
                    .param("teacherId", teacherId)
                    .param("assistantTeacherId", teacherId)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());

    jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    rootNode = objectMapper.readTree(jsonResponse);

    // schedules 배열을 순회하며 09-09 06:00 및 06:30 시간의 조건을 확인
    schedules = rootNode.get("schedules");
    assertTrue(schedules.isArray(), "Schedules 배열이어야 합니다.");

    boolean conditionsMet = false;

    for (JsonNode schedule : schedules) {
      String time = schedule.get("time").asText();
      if ("06:00".equals(time) || "06:30".equals(time)) {
        JsonNode reservations = schedule.get("reservations");
        assertTrue(reservations.isArray(), "Reservations 배열이어야 합니다.");

        for (JsonNode reservation : reservations) {
          String date = reservation.get("date").asText();
          if ("2024-10-09".equals(date)) {
            // 조건 1: teachers 배열이 비어있는지 확인
            boolean isTeachersEmpty = reservation.get("teachers").isEmpty();

            // 조건 2: teacherStatus가 "USERS"인지 확인
            boolean isTeacherStatusUsers = "USERS".equals(
                    reservation.get("teacherStatus").asText());

            // 조건 3: assistantTeacherStatus가 "USERS"인지 확인
            boolean isAssistantTeacherStatusUsers = "USERS".equals(
                    reservation.get("assistantTeacherStatus").asText());

            // 세 가지 조건 모두 만족하면 true
            if (isTeachersEmpty && isTeacherStatusUsers && isAssistantTeacherStatusUsers) {
              conditionsMet = true;
            }
          }
        }
      }
    }

    // 적어도 한 번은 조건을 만족해야 함
    assertTrue(conditionsMet, "조건을 만족하는 예약이 존재해야 합니다.");
    //----------------------------------------------------------------------------------------------
    step("Step 14: 예약가능스케쥴 조회", () -> System.out.println("Step 14: 예약가능스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 15. 강의시간표
    mockMvc.perform(get("/admin/v1/reservations/schedules")
                    .param("date", "2024-10-09")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // time이 06:00이고 teacherId가 M1709859614116619, status가 R인 reservation이 있는지 확인
            .andExpect(jsonPath(
                    "$.schedules[?(@.time == '06:00')].reservations[?(@.teacherId == 'M1709859614116619' && @.status == 'R')]"
            ).exists())
            // time이 06:30이고 teacherId가 M1709859614116619, status가 R인 reservation이 있는지 확인
            .andExpect(jsonPath(
                    "$.schedules[?(@.time == '06:30')].reservations[?(@.teacherId == 'M1709859614116619' && @.status == 'R')]"
            ).exists())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 15: 강의 시간표", () -> System.out.println("Step 15: 강의 시간표"));
    //----------------------------------------------------------------------------------------------

    // 16. 예약목록조회
    // 예약된 스케줄 식별키가 2개라고 가정
    int expectedReservationCount = 2;
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/reservations", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("courseId", courseId)
                    .param("page", "1")
                    .param("limit", "20")
                    .param("dateFrom", "2024-10-09")
                    .param("dateTo", "2024-10-14")
                    .param("excludeCancel", "true")
                    .param("excludeAttendance", "true")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // 예약 식별키가 expectedReservationCount 개수인지 확인
            .andExpect(jsonPath("$.list", hasSize(expectedReservationCount)))
            // 첫 번째 예약의 attendanceStatus가 "예약"인지 확인
            .andExpect(jsonPath("$.list[0].attendanceStatus", is("예약")))
            // 두 번째 예약의 attendanceStatus가 "예약"인지 확인
            .andExpect(jsonPath("$.list[1].attendanceStatus", is("예약")))
            .andDo(print());

    jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    rootNode = objectMapper.readTree(jsonResponse);

    // id 값을 저장할 리스트
    List<Integer> reservationIds = new ArrayList<>();

    // list 배열에서 id 추출
    JsonNode listNode = rootNode.get("list");
    if (listNode.isArray()) {
      for (JsonNode item : listNode) {
        int id = item.get("id").asInt();
        reservationIds.add(id);
      }
    }
    //----------------------------------------------------------------------------------------------
    step("Step 16: 예약목록 조회", () -> System.out.println("Step 16: 예약목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 17. 예약 취소
    content = """
        {
            "reservations": [
                {
                    "id": %s,
                    "isCancel": true,
                    "cancelReason": "취소 사유"
                }
            ]
        }
        """.formatted(reservationIds.get(0)).trim();
    mockMvc.perform(put("/admin/v1/users/{id}/reservations", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 17: 예약 취소", () -> System.out.println("Step 17: 예약 취소"));
    //----------------------------------------------------------------------------------------------

    // 18. 강의시간표
    resultActions = mockMvc.perform(get("/admin/v1/reservations/schedules")
                    .param("date", "2024-10-09")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedules[?(@.time == '06:00')].reservations[0].status",
                    contains(nullValue())))
            .andExpect(
                    jsonPath("$.schedules[?(@.time == '06:30')].reservations[0].status", contains("R")))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 18: 강의 시간표", () -> System.out.println("Step 18: 강의 시간표"));
    //----------------------------------------------------------------------------------------------

    // 19. 수강이력테이블 조회
    mockMvc.perform(get("/admin/v1/users/{id}/courses/{courseId}/histories", userId, courseId)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // type이 "주문"인 항목이 존재하는지 확인
            .andExpect(jsonPath("$.list[?(@.type == '주문')]").exists())
            // type이 "배정"인 항목이 존재하는지 확인
            .andExpect(jsonPath("$.list[?(@.type == '배정')]").exists())
            // type이 "배정취소"인 항목이 존재하는지 확인
            .andExpect(jsonPath("$.list[?(@.type == '배정취소')]").exists())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 19: 수강이력 조회", () -> System.out.println("Step 19: 수강이력 조회"));
    //----------------------------------------------------------------------------------------------

    // 20. 예약 목록
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/reservations", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("courseId", courseId)
                    .param("page", "1")
                    .param("dateFrom", "2024-10-09")
                    .param("dateTo", "2024-10-14")
                    .param("excludeCancel", "true")
                    .param("excludeAttendance", "false")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // list 배열의 크기가 1인지 확인
            .andExpect(jsonPath("$.list", hasSize(1)))
            // list 배열의 첫 번째 항목의 date가 2024-09-09 (Mon)인지 확인
            .andExpect(jsonPath("$.list[0].date").value("2024-10-09 (Wed)"))
            // list 배열의 첫 번째 항목의 startTime이 06:30인지 확인
            .andExpect(jsonPath("$.list[0].startTime").value("06:30"))
            // list 배열의 첫 번째 항목의 endTime이 07:00인지 확인
            .andExpect(jsonPath("$.list[0].endTime").value("07:00"))
            // list 배열의 첫 번째 항목의 attendanceStatus가 예약인지 확인
            .andExpect(jsonPath("$.list[0].attendanceStatus").value("예약"))
            .andDo(print());

    String reservationId = extractValueFromJson(resultActions.andReturn(), "$.list[0].id",
            String.class);
    assertNotNull(reservationId);
    //----------------------------------------------------------------------------------------------
    step("Step 20: 예약목록 조회", () -> System.out.println("Step 20: 예약목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 21. 출석 처리
    content = """
        {
            "attendanceStatus" : "Y",
            "reservationId" : %s
        }
        """.formatted(reservationId).trim();
    log.debug("content: {}", content);
    mockMvc.perform(put("/admin/v1/reservations/attendanceStatus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 21: 출석 처리", () -> System.out.println("Step 21: 출석 처리"));
    //----------------------------------------------------------------------------------------------

    // 22. 수강탭 목록 조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/courses", userId)
                    .param("status", "VALID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // list 배열의 크기가 1인지 확인
            .andExpect(jsonPath("$.list", hasSize(1)))
            // list의 첫 번째 항목의 name이 '테스트상품/10.0회'인지 확인
            .andExpect(jsonPath("$.list[0].name").value("테스트상품/10.0회"))
            // lessonCount가 10.0인지 확인
            .andExpect(jsonPath("$.list[0].lessonCount").value(10.0))
            // assignmentCount가 0.5인지 확인
            .andExpect(jsonPath("$.list[0].assignmentCount").value(0.5))
            // remainCount가 9.0인지 확인
            .andExpect(jsonPath("$.list[0].remainCount").value(9.5))
            // 시작일
            .andExpect(
                    jsonPath("$.list[0].startDate").value(DateUtils.getString(LocalDate.now().plusDays(7))))
            // 종료일
            .andExpect(
                    jsonPath("$.list[0].endDate").value(
                            DateUtils.getString(LocalDate.now().plusDays(7).plusMonths(3).minusDays(1))))
            // teacherId가 'M1709859614116619'인지 확인
            .andExpect(jsonPath("$.list[0].teacherId").value(teacherId))
            // status가 'WAITING'인지 확인
            .andExpect(jsonPath("$.list[0].status").value("WAITING"))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 22: 수강탭목록 조회", () -> System.out.println("Step 22: 수강탭목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 23. 회원 수강 수정
    content = """
        {
            "lessonCount" : 2,
            "countChangeReason": "잔여횟수조정",
            "startDate": "2024-09-01",
            "endDate":"2024-12-25",
            "teacherId":"%s"
        }
        """.formatted(teacherId);
    mockMvc.perform(put("/admin/v1/users/{id}/courses/{courseId}", userId, courseId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 23: 회원수강 수정", () -> System.out.println("Step 23: 회원수강 수정"));
    //----------------------------------------------------------------------------------------------

    // 24. 출결 취소 처리
    content = """
        {
             "attendanceStatus" : "R",
             "reservationId": %s
        }
        """.formatted(reservationId).trim();
    mockMvc.perform(put("/admin/v1/reservations/attendanceStatus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 24: 출결 취소처리", () -> System.out.println("Step 24: 출결 취소처리"));
    //----------------------------------------------------------------------------------------------

    // 25. 수강탭 목록 조회
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/courses", userId)
                    .param("status", "VALID")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // list 배열의 크기가 1인지 확인
            .andExpect(jsonPath("$.list", hasSize(1)))
            // list의 첫 번째 항목의 name이 '테스트상품/2.0회'인지 확인
            .andExpect(jsonPath("$.list[0].name").value("테스트상품/2.0회"))
            // lessonCount가 2.0인지 확인
            .andExpect(jsonPath("$.list[0].lessonCount").value(2.0))
            // assignmentCount가 1.0인지 확인
            .andExpect(jsonPath("$.list[0].assignmentCount").value(0.5))
            // remainCount가 1.0인지 확인
            .andExpect(jsonPath("$.list[0].remainCount").value(1.5))
            // startDate가 '2024-09-01'인지 확인
            .andExpect(jsonPath("$.list[0].startDate").value("2024-09-01"))
            // endDate가 '2024-12-25'인지 확인
            .andExpect(jsonPath("$.list[0].endDate").value("2024-12-25"))
            // teacherId가 'M1709859614116619'인지 확인
            .andExpect(jsonPath("$.list[0].teacherId").value(teacherId))
            // status가 'NORMAL'인지 확인
            .andExpect(jsonPath("$.list[0].status").value("NORMAL"))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 25: 수강탭 목록 조회", () -> System.out.println("Step 25: 수강탭 목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 26. 결석 처리
    content = """
        {
            "attendanceStatus" : "N",
            "reservationId" : %s
        }
        """.formatted(reservationId).trim();
    log.debug("content: {}", content);
    mockMvc.perform(put("/admin/v1/reservations/attendanceStatus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 26: 결석처리", () -> System.out.println("Step 26: 결석처리"));
    //----------------------------------------------------------------------------------------------

    // 27. 강의시간표
    resultActions = mockMvc.perform(get("/admin/v1/reservations/schedules")
                    .param("date", "2024-10-09")
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedules[?(@.time == '06:00')].reservations[0].status",
                    contains(nullValue())))
            .andExpect(
                    jsonPath("$.schedules[?(@.time == '06:30')].reservations[0].status", contains("N")))
            .andExpect(
                    jsonPath("$.schedules[?(@.time == '06:30')].reservations[0].statusLabel",
                            contains("결석")))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 27: 강의시간표 조회", () -> System.out.println("Step 27: 강의시간표 조회"));
    //----------------------------------------------------------------------------------------------

    // 28. 수강이력테이블 조회
    mockMvc.perform(get("/admin/v1/users/{id}/courses/{courseId}/histories", userId, courseId)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            // TODO 아래의 순서가 실행될때마다 바뀌어 일단 주석 처리(나중에 확인 필요)
//        // listNumber 7의 type이 "주문"인지 확인
//        .andExpect(jsonPath("$.list[?(@.listNumber == 7)].type").value("주문"))
//        .andExpect(jsonPath("$.list[?(@.listNumber == 6)].type").value("배정"))
//        // listNumber 5의 type이 "배정취소"인지 확인
//        .andExpect(jsonPath("$.list[?(@.listNumber == 5)].type").value("배정취소"))
//        // listNumber 4의 type이 "출결"인지 확인
//        .andExpect(jsonPath("$.list[?(@.listNumber == 4)].type").value("출결"))
//        // listNumber 3의 type이 "변경"인지 확인
//        .andExpect(jsonPath("$.list[?(@.listNumber == 3)].type").value("변경"))
//        // listNumber 2의 type이 "출결"인지 확인
//        .andExpect(jsonPath("$.list[?(@.listNumber == 2)].type").value("출결"))
//        // listNumber 1의 type이 "출결"인지 확인
//        .andExpect(jsonPath("$.list[?(@.listNumber == 1)].type").value("출결"))
            .andExpect(jsonPath("$.list[?(@.type == '출결')]").exists())
            .andExpect(jsonPath("$.list[?(@.type == '변경')]").exists())
            .andExpect(jsonPath("$.list[?(@.type == '주문')]").exists())
            .andExpect(jsonPath("$.list[?(@.type == '배정')]").exists())
            .andExpect(jsonPath("$.list[?(@.type == '배정취소')]").exists())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 28: 수강이력테이블 조회", () -> System.out.println("Step 28: 수강이력테이블 조회"));
    //----------------------------------------------------------------------------------------------
  }



  @Test
  @DisplayName("모바일 예약 성공")
  @Transactional
  void c() throws Exception {
    String content = """
        {
            "dateFrom": "2024-09-30",
            "dateTo": "2024-10-01",
            "workTime": "AM_16",
            "schedules": [
                {
                    "date": "2024-09-30",
                    "time": "06:30"
                },
                {
                    "date": "2024-09-30",
                    "time": "07:00"
                },
                {
                    "date": "2024-09-30",
                    "time": "07:30"
                },
                {
                    "date": "2024-09-30",
                    "time": "08:00"
                },
                {
                    "date": "2024-09-30",
                    "time": "08:30"
                },
                {
                    "date": "2024-09-30",
                    "time": "09:00"
                },
                {
                    "date": "2024-09-30",
                    "time": "09:30"
                },
                {
                    "date": "2024-09-30",
                    "time": "10:00"
                },
                {
                    "date": "2024-09-30",
                    "time": "11:00"
                },
                {
                    "date": "2024-10-01",
                    "time": "06:30"
                },
                {
                    "date": "2024-10-01",
                    "time": "07:00"
                },
                {
                    "date": "2024-10-01",
                    "time": "07:30"
                },
                {
                    "date": "2024-10-01",
                    "time": "08:00"
                },
                {
                    "date": "2024-10-01",
                    "time": "08:30"
                },
                {
                    "date": "2024-10-01",
                    "time": "09:00"
                },
                {
                    "date": "2024-10-01",
                    "time": "09:30"
                },
                {
                    "date": "2024-10-01",
                    "time": "10:00"
                },
                {
                    "date": "2024-10-01",
                    "time": "11:00"
                }
            ]
        }
        """.trim();

    // 1. 강의 스케쥴 등록
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 1: 강사스케쥴 등록", () -> System.out.println("Step 1: 강사스케쥴 등록"));
    //----------------------------------------------------------------------------------------------

    // 2. 회원등록
    createUser(adminToken, CREATE_USER.formatted(loginId, loginId));

    ResultActions resultActions = getUser(adminToken, "S", "email", loginId)
        .andExpect(jsonPath("$.list").value(notNullValue()));

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    //----------------------------------------------------------------------------------------------
    step("Step 2: 회원 등록", () -> System.out.println("Step 2: 회원 등록"));
    //----------------------------------------------------------------------------------------------

    // 3. 상품등록
    content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
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
    //----------------------------------------------------------------------------------------------
    step("Step 3: 상품 등록", () -> System.out.println("Step 3: 상품 등록"));
    //----------------------------------------------------------------------------------------------

    // 4. 상품주문
    content = """
        {
             "productId": "%s",
             "quantity": 10,
             "teacherId": "%s",
             "assistantTeacherId": "M1681990831501448",
             "billingAmount": 60000
        }
        """.trim().formatted(productId, teacherId);
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
    //----------------------------------------------------------------------------------------------
    step("Step 4: 상품 주문", () -> System.out.println("Step 4: 상품 주문"));
    //----------------------------------------------------------------------------------------------

    // 5. 결제등록
    content = """
         {
            "type": "I",
            "paymentDate": "2024-08-30",
            "cashAmount": null,
            "isReceiptIssued": false,
            "receiptNumber": "",
            "depositAmount": null,
            "accountHolder": "테스터",
            "receivableAmount": 0,
            "recallDate": "2024-09-01",
            "receivableReason": "test",
            "memo": "test",
            "cards": [
                {
                    "amount": 30000,
                    "code": "2090073",
                    "cardNumber": "12345",
                    "installmentMonths": 0,
                    "approvalNumber": ""
                },
                {
                    "amount": 30000,
                    "code": "2090074",
                    "cardNumber": "00000",
                    "installmentMonths": 0,
                    "approvalNumber": ""
                }
            ]
        }
        """;
    mockMvc.perform(post("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 5: 결제 등록", () -> System.out.println("Step 5: 결제 등록"));
    //----------------------------------------------------------------------------------------------

    // 6. 모바일 로그인
    UserEntity user = userRepository.findByLoginId(loginId).get();
    user.setActive(true);
    userRepository.save(user);

    final String userToken = login(loginId, "1111", UserType.S);
    //----------------------------------------------------------------------------------------------
    step("Step 6: 로그인", () -> System.out.println("Step 6: 로그인"));
    //----------------------------------------------------------------------------------------------

    // 7. 수강중인 강의 조회
    resultActions = mockMvc.perform(get("/mobile/v1/main")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.courses[0].id").value(notNullValue()))
        .andExpect(jsonPath("$.courses[0].productName").value("테스트상품"))
        .andExpect(
            jsonPath("$.courses[0].startDate").value(
                DateUtils.getString(LocalDate.now().plusDays(7))))
        .andExpect(jsonPath("$.courses[0].endDate").value(
            DateUtils.getString(LocalDate.now().plusDays(7).plusMonths(3).minusDays(1))))
        .andExpect(jsonPath("$.courses[0].lessonCount").value(10.0))
        .andExpect(jsonPath("$.courses[0].remainCount").value(10.0))
        .andExpect(jsonPath("$.courses[0].attendanceCount").value(0.0))
        .andExpect(jsonPath("$.courses[0].reservationCount").value(0.0))
        .andExpect(jsonPath("$.courses[0].totalAttendanceCount").value(0.0))
        .andExpect(jsonPath("$.courses[0].totalReservationCount").value(0.0))
        .andExpect(jsonPath("$.courses[0].totalNonAttendanceCount").value(0.0))
        .andExpect(jsonPath("$.courses[0].reservations").isEmpty())
        .andExpect(jsonPath("$.courses[0].totalAttendanceRate").value("0.00"))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 7: 수강중인 강의 조회", () -> System.out.println("Step 7: 수강중인 강의 조회"));
    //----------------------------------------------------------------------------------------------

    final String courseId = extractValueFromJson(resultActions.andReturn(), "$.courses[0].id",
        String.class);
    assertNotNull(courseId);

    // 8. 예약 가능한 강사목록 조회
    mockMvc.perform(get("/mobile/v1/reservations/teachers")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .param("date", "2024-09-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.teachers[0].id").value(teacherId))
        .andExpect(jsonPath("$.teachers[0].name").value("Austin"))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 8: 예약가능한 강사목록 조회", () -> System.out.println("Step 8: 예약가능한 강사목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 9. 예약 가능한 시간목록 조회
    mockMvc.perform(get("/mobile/v1/reservations/times")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .param("date", "2024-09-30"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.times[0]").value("06:30"))
        .andExpect(jsonPath("$.times[1]").value("07:00"))
        .andExpect(jsonPath("$.times[2]").value("07:30"))
        .andExpect(jsonPath("$.times[3]").value("08:00"))
        .andExpect(jsonPath("$.times[4]").value("08:30"))
        .andExpect(jsonPath("$.times[5]").value("09:00"))
        .andExpect(jsonPath("$.times[6]").value("09:30"))
        .andExpect(jsonPath("$.times[7]").value("10:00"))
        .andExpect(jsonPath("$.times[8]").value("11:00"))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 9: 예약가능한 시간목록 조회", () -> System.out.println("Step 9: 예약가능한 시간목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 10. 예약 가능한 나머지 스케쥴 목록 조회
    resultActions = mockMvc.perform(get("/mobile/v1/reservations/schedules/remain")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .param("date", "2024-09-30")
            .param("time", "08:00"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.schedules").isArray())
        .andExpect(jsonPath("$.schedules[0].id").value(notNullValue()))
        .andExpect(jsonPath("$.schedules[0].teacherId").value(teacherId))
        .andExpect(jsonPath("$.schedules[0].teacherName").value("Austin"))
        .andExpect(jsonPath("$.schedules[0].startTime").value("07:30"))
        .andExpect(jsonPath("$.schedules[0].endTime").value("08:00"))
        .andExpect(jsonPath("$.schedules[1].id").value(notNullValue()))
        .andExpect(jsonPath("$.schedules[1].teacherId").value(teacherId))
        .andExpect(jsonPath("$.schedules[1].teacherName").value("Austin"))
        .andExpect(jsonPath("$.schedules[1].startTime").value("08:30"))
        .andExpect(jsonPath("$.schedules[1].endTime").value("09:00"))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 10: 예약가능한 나머지 스케쥴목록 조회", () -> System.out.println("Step 8: 예약가능한 나머지 스케쥴 목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 11. 모바일 예약
    final String remainScheduleId = extractValueFromJson(resultActions.andReturn(),
        "$.schedules[1].id", String.class);
    assertNotNull(remainScheduleId);
    content = """
        {
           "courseId" : %s,
           "date" : "2024-09-30",
           "remainScheduleId" : %s,
           "teacherId" : "%s",
           "time" : "08:00"
        }
        """.trim().formatted(courseId, remainScheduleId, teacherId);
    mockMvc.perform(post("/mobile/v1/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 11: 모바일 예약", () -> System.out.println("Step 11: 모바일 예약"));
    //----------------------------------------------------------------------------------------------

    // 12. 예약 목록 조회
    resultActions = mockMvc.perform(get("/mobile/v1/main/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .param("date", "2024-09-13"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(notNullValue()))
        .andExpect(jsonPath("$[0].date").value("2024-09-30"))
        .andExpect(jsonPath("$[0].startTime").value("08:00"))
        .andExpect(jsonPath("$[0].endTime").value("08:30"))
        .andExpect(jsonPath("$[0].attendanceStatus").value("R"))
        .andExpect(jsonPath("$[0].teacherName").value("Austin"))
        .andExpect(jsonPath("$[1].id").value(notNullValue()))
        .andExpect(jsonPath("$[1].date").value("2024-09-30"))
        .andExpect(jsonPath("$[1].startTime").value("08:30"))
        .andExpect(jsonPath("$[1].endTime").value("09:00"))
        .andExpect(jsonPath("$[1].attendanceStatus").value("R"))
        .andExpect(jsonPath("$[1].teacherName").value("Austin"))
        .andDo(print());

    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    JsonNode rootNode = objectMapper.readTree(jsonResponse);

    List<Integer> reservationIdList = new ArrayList<>();
    for (JsonNode node : rootNode) {
      reservationIdList.add(node.get("id").asInt());
    }

    assertTrue(reservationIdList.size() == 2);
    //----------------------------------------------------------------------------------------------
    step("Step 12: 예약목록 조회", () -> System.out.println("Step 12: 예약목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 13. 강의시간표
    resultActions = mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-date", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("dateFrom", "2024-09-30") // 조회 시작일이 고정값?
            .param("dateTo", "2024-10-04") // 조회 종료일이 고정값?
            .param("teacherId", teacherId)
            .param("assistantTeacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    rootNode = objectMapper.readTree(jsonResponse);
    JsonNode schedules = rootNode.get("schedules");

    if (schedules.isArray()) {
      for (JsonNode schedule : schedules) {
        String time = schedule.get("time").asText();
        JsonNode reservations = schedule.get("reservations");

        if ("08:00".equals(time)) {
          // Check if the date is 2024-09-09 and the teachers array is empty
          for (JsonNode reservation : reservations) {
            String date = reservation.get("date").asText();
            if ("2024-09-09".equals(date)) {
              assertTrue(reservation.get("teachers").isEmpty(), "Teachers array should be empty for 08:00 on 2024-09-09");
            }
          }
        } else if ("08:30".equals(time)) {
          // Check if the date is 2024-09-30 and the teachers array is empty
          for (JsonNode reservation : reservations) {
            String date = reservation.get("date").asText();
            if ("2024-09-30".equals(date)) {
              assertTrue(reservation.get("teachers").isEmpty(), "Teachers array should be empty for 08:30 on 2024-09-30");
            }
          }
        }
      }
    }
    //----------------------------------------------------------------------------------------------
    step("Step 13: 강의시간표 조회", () -> System.out.println("Step 13: 강의시간표 조회"));
    //----------------------------------------------------------------------------------------------

    // 14. 예약 취소
    content = """
        {
           "cancelReason": "test",
           "ids": %s
        }
        """.formatted(reservationIdList.toString());
    mockMvc.perform(put("/mobile/v1/reservations/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 14: 예약 취소", () -> System.out.println("Step 14: 예약 취소"));
    //----------------------------------------------------------------------------------------------

    // 15. 예약 목록 조회
    mockMvc.perform(get("/mobile/v1/main/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, userToken)
            .param("date", "2024-09-13"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 15: 예약목록 조회", () -> System.out.println("Step 15: 예약목록 조회"));
    //----------------------------------------------------------------------------------------------

  }
}
