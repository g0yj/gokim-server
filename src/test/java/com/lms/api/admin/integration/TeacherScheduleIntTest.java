package com.lms.api.admin.integration;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("강사일정")
@ActiveProfiles("test")
class TeacherScheduleIntTest extends ControllerTestSupport {

  /**
   * 출석율/결석율
   */
  @Test
  @DisplayName("특정 연월일의 출석율과 결석율이 조회")
  @Transactional
  void shouldRetrieveAttendanceAndAbsenceRatesForSpecificYearMonth() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/teachers/attendances")
            .header(AUTHORIZATION, token)
            .param("status", "Y")
            .param("yearMonth", "2024-01")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  /**
   * 출석율/결석율
   */
  @Test
  @DisplayName("특정 연월일의 출석율과 결석율이 조회")
  @Transactional
  void shouldRetrieveAttendanceAndAbsenceRatesForSpecificDate() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/teachers/attendances/by-date")
            .header(AUTHORIZATION, token)
            .param("status", "Y")
            .param("yearMonthDay", "2024-10-15")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  /**
   * 강의스케쥴 통합테스트
   */
  @Test
  @Transactional
  @DisplayName("강의스케줄이 등록되면 조회")
  void shouldRetrieveLectureScheduleAfterRegistration() throws Exception {
    String teacherId = "M1713239402907750";
    String content = """
        {
            "dateFrom": "2024-08-30",
            "dateTo": "2024-08-31",
            "workTime": "AM_16",
            "schedules": [
                {
                    "date": "2024-08-30",
                    "time": "06:30"
                },
                {
                    "date": "2024-08-30",
                    "time": "07:00"
                },
                {
                    "date": "2024-08-30",
                    "time": "07:30"
                },
                {
                    "date": "2024-08-30",
                    "time": "08:00"
                },
                {
                    "date": "2024-08-30",
                    "time": "08:30"
                },
                {
                    "date": "2024-08-30",
                    "time": "09:00"
                },
                {
                    "date": "2024-08-30",
                    "time": "09:30"
                },
                {
                    "date": "2024-08-30",
                    "time": "10:00",
                    "isScheduled": true
                },
                {
                    "date": "2024-08-30",
                    "time": "11:00"
                },
                {
                    "date": "2024-08-31",
                    "time": "06:30"
                },
                {
                    "date": "2024-08-31",
                    "time": "07:00"
                },
                {
                    "date": "2024-08-31",
                    "time": "07:30"
                },
                {
                    "date": "2024-08-31",
                    "time": "08:00"
                },
                {
                    "date": "2024-08-31",
                    "time": "08:30"
                },
                {
                    "date": "2024-08-31",
                    "time": "09:00"
                },
                {
                    "date": "2024-08-31",
                    "time": "09:30"
                },
                {
                    "date": "2024-08-31",
                    "time": "10:00"
                },
                {
                    "date": "2024-08-31",
                    "time": "11:00"
                }
            ]
        }
        """;

    // 1. 강의 스케쥴 저장
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(post("/admin/v1/teachers/{id}/schedules", teacherId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 1: 강의 스케쥴 등록", () -> System.out.println("Step 1: 강의 스케쥴 등록"));
    //----------------------------------------------------------------------------------------------

    // 2. 강의 스케쥴 조회
    mockMvc.perform(get("/admin/v1/teachers/{id}/schedules", teacherId)
        .param("dateFrom", "2024-08-30")
        .param("dateTo", "2024-08-31")
        .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.schedules[?(@.date == '2024-08-30' && @.isScheduled == true)]", hasSize(9)))
        .andExpect(jsonPath("$.schedules[?(@.date == '2024-08-31' && @.isScheduled == true)]", hasSize(9)))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 2: 강의 스케쥴 조회", () -> System.out.println("Step 2: 강의 스케쥴 조회"));
    //----------------------------------------------------------------------------------------------

    // 3. 강의 시간표 조회
    mockMvc.perform(get("/admin/v1/reservations/schedules")
            .param("date", "2024-08-31")
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.teachers[0].id").value("M1713239402907750"))
        .andExpect(jsonPath("$.teachers[0].name").value("박제은"))

        // schedules 배열 검증
        .andExpect(jsonPath("$.schedules[0].time").value("06:00"))
        .andExpect(jsonPath("$.schedules[0].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[0].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[0].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[0].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[1].time").value("06:30"))
        .andExpect(jsonPath("$.schedules[1].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[1].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[1].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[1].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[2].time").value("07:00"))
        .andExpect(jsonPath("$.schedules[2].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[2].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[2].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[2].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[3].time").value("07:30"))
        .andExpect(jsonPath("$.schedules[3].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[3].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[3].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[3].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[4].time").value("08:00"))
        .andExpect(jsonPath("$.schedules[4].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[4].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[4].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[4].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[5].time").value("08:30"))
        .andExpect(jsonPath("$.schedules[5].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[5].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[5].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[5].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[6].time").value("09:00"))
        .andExpect(jsonPath("$.schedules[6].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[6].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[6].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[6].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[7].time").value("09:30"))
        .andExpect(jsonPath("$.schedules[7].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[7].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[7].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[7].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[8].time").value("10:00"))
        .andExpect(jsonPath("$.schedules[8].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[8].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[8].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[8].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[9].time").value("10:30"))
        .andExpect(jsonPath("$.schedules[9].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[9].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[9].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[9].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[10].time").value("11:00"))
        .andExpect(jsonPath("$.schedules[10].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[10].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[10].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[10].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[11].time").value("11:30"))
        .andExpect(jsonPath("$.schedules[11].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[11].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[11].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[11].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[12].time").value("12:00"))
        .andExpect(jsonPath("$.schedules[12].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[12].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[12].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[12].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[13].time").value("12:30"))
        .andExpect(jsonPath("$.schedules[13].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[13].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[13].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[13].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[14].time").value("13:00"))
        .andExpect(jsonPath("$.schedules[14].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[14].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[14].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[14].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[15].time").value("13:30"))
        .andExpect(jsonPath("$.schedules[15].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[15].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[15].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[15].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[16].time").value("14:00"))
        .andExpect(jsonPath("$.schedules[16].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[16].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[16].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[16].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[17].time").value("14:30"))
        .andExpect(jsonPath("$.schedules[17].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[17].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[17].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[17].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[18].time").value("15:00"))
        .andExpect(jsonPath("$.schedules[18].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[18].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[18].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[18].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[19].time").value("15:30"))
        .andExpect(jsonPath("$.schedules[19].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[19].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[19].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[19].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[20].time").value("16:00"))
        .andExpect(jsonPath("$.schedules[20].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[20].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[20].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[20].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[21].time").value("16:30"))
        .andExpect(jsonPath("$.schedules[21].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[21].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[21].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[21].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[22].time").value("17:00"))
        .andExpect(jsonPath("$.schedules[22].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[22].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[22].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[22].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[23].time").value("17:30"))
        .andExpect(jsonPath("$.schedules[23].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[23].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[23].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[23].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[24].time").value("18:00"))
        .andExpect(jsonPath("$.schedules[24].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[24].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[24].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[24].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[25].time").value("18:30"))
        .andExpect(jsonPath("$.schedules[25].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[25].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[25].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[25].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[26].time").value("19:00"))
        .andExpect(jsonPath("$.schedules[26].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[26].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[26].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[26].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[27].time").value("19:30"))
        .andExpect(jsonPath("$.schedules[27].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[27].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[27].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[27].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[28].time").value("20:00"))
        .andExpect(jsonPath("$.schedules[28].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[28].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[28].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[28].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[29].time").value("20:30"))
        .andExpect(jsonPath("$.schedules[29].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[29].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[29].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[29].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[30].time").value("21:00"))
        .andExpect(jsonPath("$.schedules[30].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[30].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[30].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[30].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[31].time").value("21:30"))
        .andExpect(jsonPath("$.schedules[31].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[31].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[31].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[31].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[32].time").value("22:00"))
        .andExpect(jsonPath("$.schedules[32].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[32].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[32].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[32].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[33].time").value("22:30"))
        .andExpect(jsonPath("$.schedules[33].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[33].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[33].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[33].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[34].time").value("23:00"))
        .andExpect(jsonPath("$.schedules[34].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[34].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[34].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[34].reservations[0].isReported").value(false))

        .andExpect(jsonPath("$.schedules[35].time").value("23:30"))
        .andExpect(jsonPath("$.schedules[35].reservations[0].teacherId").value("M1713239402907750"))
        .andExpect(jsonPath("$.schedules[35].reservations[0].teacherName").value("박제은"))
        .andExpect(jsonPath("$.schedules[35].reservations[0].isRetakeRequired").value(false))
        .andExpect(jsonPath("$.schedules[35].reservations[0].isReported").value(false))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 3: 강의 기간표 조회", () -> System.out.println("Step 3: 강의 시간표 조회"));
    //----------------------------------------------------------------------------------------------
  }
}