package com.lms.api.admin.integration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@DisplayName("CGT 시나리오")
@ActiveProfiles("test")
public class CgtIntTest extends IntegrationTestSupport {

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
  @DisplayName("CGT 수업 생성 테스트")
  @Transactional
  void a() throws Exception {
    // Prepare the request body
    String requestBody = """
            {
                "date": "2023-11-04",
                "startTime": "07:30",
                "teacherId": "M1695013438115743",
                "type": "CGT",
                "reservationLimit": 4
            }
            """;

    mockMvc.perform(put("/admin/v1/teachers/cgt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andDo(print());
  }

  @Test
  @DisplayName("CGT 수업 생성 및 조회 테스트")
  @Transactional
  void combinedTest() throws Exception {

    // Step 2: Create CGT
    String createCgtContent = """
            {
                "date": "2025-08-30",
                "startTime": "06:30",
                "teacherId": "M1400118325784523",
                "type": "CGT",
                "reservationLimit": 4
            }
            """;

    mockMvc.perform(put("/admin/v1/teachers/cgt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(createCgtContent))
            .andExpect(status().isOk())
            .andDo(print());


    mockMvc.perform(get("/admin/v1/teachers/cgt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.content[0].date").value("2025-08-30"))
//            .andExpect(jsonPath("$.content[0].startTime").value("07:30"))
//            .andExpect(jsonPath("$.content[0].teacherId").value("M1695013438115743"))
//            .andExpect(jsonPath("$.content[0].type").value("CGT"))
//            .andExpect(jsonPath("$.content[0].reservationLimit").value(4))
            .andDo(print());
  }

  @Test
  @DisplayName("CGT 수업 생성 및 조회 테스트")
  @Transactional
  void b() throws Exception {
    // Step 1: Create CGT
    String createCgtContent = """
            {
                "date": "2023-11-04",
                "startTime": "07:30",
                "teacherId": "M1695013438115743",
                "type": "CGT",
                "reservationLimit": 4
            }
            """;

    mockMvc.perform(put("/admin/v1/teachers/cgt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken)
                    .content(createCgtContent))
            .andExpect(status().isOk())
            .andDo(print());

    // Step 2: Retrieve the created CGT
    mockMvc.perform(get("/admin/v1/teachers/cgt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, adminToken))
            .andExpect(status().isOk())
            //.andExpect(jsonPath("$.t[0].date").value("2023-11-04"))
            //.andExpect(jsonPath("$.t[0].startTime").value("07:30"))
            //.andExpect(jsonPath("$.t[0].teacherId").value("M1695013438115743"))
            //.andExpect(jsonPath("$.t[0].type").value("CGT"))
            //.andExpect(jsonPath("$.t[0].reservationLimit").value(4))
            .andDo(print());
  }

  @Test
  @DisplayName("CGT 등록 및 조회")
  @Transactional
  void cgt() throws Exception {
    String content = """
        {
              "dateFrom": "2024-10-14",
              "dateTo": "2024-10-14",
              "workTime": "SP_16",
              "schedules": [
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
                  }
              ]
          }
        """;
    String teacherId = "M1400118325784523";
    // 강의 일정 등록
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    // 등록 가능한 cgt 시간 조회
    mockMvc.perform(get("/admin/v1/teachers/cgttimes")
            .contentType(MediaType.APPLICATION_JSON)
            .param("date", "2024-10-14")
            .param("teacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("06:00"))
        .andExpect(jsonPath("$[1]").value("06:30"))
        .andExpect(jsonPath("$[2]").value("07:00"))
        .andExpect(jsonPath("$[3]").value("07:30"))
        .andExpect(jsonPath("$[4]").value("08:00"))
        .andDo(print());

    // cgt 등록
    String createCgtContent = """
            {
                "date": "2024-10-14",
                "startTime": "06:30",
                "teacherId": "%s",
                "type": "CGT",
                "reservationLimit": 1
            }
            """.formatted(teacherId);

    mockMvc.perform(put("/admin/v1/teachers/cgt")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(createCgtContent))
        .andExpect(status().isOk())
        .andDo(print());

    // 등록 가능한 cgt 시간 조회
    mockMvc.perform(get("/admin/v1/teachers/cgttimes")
            .contentType(MediaType.APPLICATION_JSON)
            .param("date", "2024-10-14")
            .param("teacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("07:30"))
        .andExpect(jsonPath("$[1]").value("08:00"))
        .andDo(print());

    // cgt 등록
    createCgtContent = """
            {
                "date": "2024-10-14",
                "startTime": "08:00",
                "teacherId": "%s",
                "type": "CGT",
                "reservationLimit": 1
            }
            """.formatted(teacherId);

    mockMvc.perform(put("/admin/v1/teachers/cgt")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(createCgtContent))
        .andExpect(status().isOk())
        .andDo(print());

    // 등록 가능한 cgt 시간 조회
    mockMvc.perform(get("/admin/v1/teachers/cgttimes")
            .contentType(MediaType.APPLICATION_JSON)
            .param("date", "2024-10-14")
            .param("teacherId", teacherId)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty())
        .andDo(print());
  }

}
