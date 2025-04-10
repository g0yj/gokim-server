package com.lms.api.admin.integration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.common.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("학사보고서 시나리오")
@ActiveProfiles("test")
public class ReportIntTest extends IntegrationTestSupport {

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
  @DisplayName("학사보고서 조회")
  @Transactional
  void a() throws Exception {
    long reservationId = 1317329L;

    mockMvc.perform(get("/admin/v1/reservations/report/{reservationId}", reservationId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.date").value("2018-01-03 (Wed)"))
            .andExpect(jsonPath("$.startTime").value("07:30"))
            .andExpect(jsonPath("$.endTime").value("08:00"))
            .andDo(print());
  }

  @Test
  @DisplayName("학사보고서 수정/등록")
  @Transactional
  void b() throws Exception {
    String content = """
            {
                "id": 1317329,
                "date": "2018-01-03 (Wed)",
                "startTime": "07:30",
                "endTime": "08:00",
                "attendanceStatus": "N",
                "report": "등록테스트",
                "todayLesson": "등록테스트",
                "userName": "박혜준"
            }
            """.trim();

    mockMvc.perform(put("/admin/v1/reservations/report/1317329")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
  }

  @Test
  @DisplayName("학사보고서 수정/등록 후 조회 및 스케줄 검증")
  @Transactional
  void c() throws Exception {
    String content = """
      {
          "id": 1317329,
          "date": "2018-01-03 (Wed)",
          "startTime": "07:30",
          "endTime": "08:00",
          "attendanceStatus": "N",
          "report": "등록테스트",
          "todayLesson": "등록테스트",
          "userName": "박혜준"
      }
      """.trim();

    // Perform PUT request to update the report
    mockMvc.perform(put("/admin/v1/reservations/report/1317329")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());

    long reservationId = 1317329L;

    // Perform GET request to retrieve the report
    mockMvc.perform(get("/admin/v1/reservations/report/{reservationId}", reservationId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attendanceStatus").value("N"))
            .andExpect(jsonPath("$.report").value("등록테스트"))
            .andExpect(jsonPath("$.todayLesson").value("등록테스트"))
            .andDo(print());

    // Perform GET request to list schedules
    mockMvc.perform(get("/admin/v1/reservations/schedules")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .param("date", "2018-01-03"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedules[*].reservations[?(@.reservationId == 1317329)].isReported").value(true))
            .andDo(print());
  }


}
