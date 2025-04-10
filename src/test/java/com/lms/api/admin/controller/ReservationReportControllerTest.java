package com.lms.api.admin.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;

//import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.lms.api.admin.code.SearchReservationCode.ReportCondition;
import com.lms.api.common.dto.UserType;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.support.ControllerTestSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("학사보고서")
@ActiveProfiles("test")
public class ReservationReportControllerTest extends ControllerTestSupport {

  @Autowired
  JdbcTemplate jdbcTemplate;
  @Autowired
  UserRepository userRepository;
  @Autowired
  TeacherRepository teacherRepository;
  @Autowired
  CourseRepository courseRepository;
  @Autowired
  private ReservationRepository reservationRepository;

  /**
   * 학사보고서는 report, todayLesson, nextLesson 필드의 값이 모두 null인 경우 미작성이고 report, todayLesson, nextLesson
   * 필드의 값중 하나라도 작성이 되어있으면 작성으로 판단합니다. 하나의 학사 보고서는 courseId, userId, teacherId, date,
   * startTime,endTime 이 모두 같아야 합니다. 하나의 학사 보고서는 startTime과 endTime이 30분 간격이어야 합니다. courseId,
   * userId, teacherId, date가 같고 endTime과 startTime이 동일한 시간이면 연속된 예약으로 판단합니다. 시간이 연속된 예약은 하나의 수업으로
   * 판단하여 학사보고서의 작성여부를 판단합니다. 예를 들면 아래와 같습니다.
   * <p>동일 courseId, userId, teacherId, date로 된 예약이 06:00, 06:30, 07:00, 11:00, 11:30 5개의 경우
   * 1. 모든 예약시간에 학사보고서가 작성되지 않은 경우 미작성 예약은 5건 입니다.
   * 2. 06:00 시간에 학사보고서가 작성된 우 미작성 예약은 11:00, 11:30의2건 입니다.
   * 3. 07:00 시간에 사보고서가 작성된 경우 미작성 예약은 11:00 , 11:30의 2건 입니다.
   * 4. 11:00 시간에 학사보고서가 작성된 경우 미작성 예약은 06:00 , 06:30, 07:00의 3건 입니다.
   * 5. 11:30 시간에 학사보고서가 작성된 경우 미작성 예약은 06:00 , 06:30, 07:00의 3건 입니다.
   * 6. 06:30, 11:00 시간에 학사보고서가 작성된 경우 미작성 예약은 0건 입니다.
   * </p>
   */
  @Test
  @Transactional
  void getList() throws Exception {

    // @formatter: off

    String token = login("jenchae@naver.com", "1111", UserType.A);

    UserEntity teacher = userRepository.findByLoginId("parangoly@gmail.com").get();
    UserEntity user = userRepository.findByLoginId("puruna123@gmail.com").get();
    CourseEntity course = courseRepository.findById(69502L).get();

    log.debug("## teacher_id:{}, user_id:{}, course_id:{}", teacher.getId(), user.getId(), course.getId());

    String insertSql = """
            INSERT INTO reservation (id, course_id, user_id, teacher_id, date, start_time, end_time,
            attendance_status, is_cancel, created_on, modified_on)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """;

    jdbcTemplate.update(insertSql, 1, course.getId(), user.getId(), teacher.getId(), LocalDate.of(2024, 10, 1), LocalTime.of(6, 0), LocalTime.of(6, 30), "R", 0);
    jdbcTemplate.update(insertSql, 2, course.getId(), user.getId(), teacher.getId(), LocalDate.of(2024, 10, 1), LocalTime.of(6, 30), LocalTime.of(7, 0), "R", 0);
    jdbcTemplate.update(insertSql, 3, course.getId(), user.getId(), teacher.getId(), LocalDate.of(2024, 10, 1), LocalTime.of(7, 0), LocalTime.of(7, 30), "R", 0);
    jdbcTemplate.update(insertSql, 4, course.getId(), user.getId(), teacher.getId(), LocalDate.of(2024, 10, 1), LocalTime.of(11, 0), LocalTime.of(11, 30), "Y", 0);
    jdbcTemplate.update(insertSql, 5, course.getId(), user.getId(), teacher.getId(), LocalDate.of(2024, 10, 1), LocalTime.of(11, 30), LocalTime.of(12, 0), "Y", 0);

    // ALL: 5건
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.ALL.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(5)))
        .andDo(print());

    // 모든 예약시간에 학사보고서가 작성되지 않은 경우 미작성 예약은 5건 입니다.
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.REPORT.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(5)))
        .andDo(print());

    // 6:00 에 학사보고서 등록
    int rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = 'test' WHERE id = 1");
    assertEquals(rowsAffected, 1);

    // 06:00 시간에 학사보고서가 작성된 우 미작성 예약은 11:00, 11:30 2건 입니다.
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.REPORT.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(2)))
        .andExpect(jsonPath("$.list[0].startTime").value("11:00"))
        .andExpect(jsonPath("$.list[1].startTime").value("11:30"))
        .andDo(result -> {
            String content = result.getResponse().getContentAsString();
            log.debug("Response content: {}", content);
        })
        .andDo(print());

    // 06:00 취소
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = null WHERE id = 1");
    assertEquals(rowsAffected, 1);
    // 06:30 등록
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = 'test' WHERE id = 2");
    assertEquals(rowsAffected, 1);

    // 06:30 시간에 사보고서가 작성된 경우 미작성 예약은 11:00, 11:30 2건 입니다.
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.REPORT.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(2)))
        .andExpect(jsonPath("$.list[0].startTime").value("11:00"))
        .andExpect(jsonPath("$.list[1].startTime").value("11:30"))
        .andDo(result -> {
          String content = result.getResponse().getContentAsString();
          log.debug("Response content: {}", content);
        })
        .andDo(print());

    // 06:30 취소
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = null WHERE id = 2");
    assertEquals(rowsAffected, 1);
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = 'test' WHERE id = 4");
    assertEquals(rowsAffected, 1);

    // 11:00 시간에 학사보고서가 작성된 경우 미작성 예약은 06:00 , 06:30, 07:00 3건 입니다.
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.REPORT.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list[0].startTime").value("06:00"))
        .andExpect(jsonPath("$.list[1].startTime").value("06:30"))
        .andExpect(jsonPath("$.list[2].startTime").value("07:00"))
        .andExpect(jsonPath("$.list", hasSize(3)))
        .andDo(print());

    // 11:00 취소
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = null WHERE id = 4");
    assertEquals(rowsAffected, 1);
    // 11:30 등록
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = 'test' WHERE id = 5");
    assertEquals(rowsAffected, 1);

    // 11:30 시간에 학사보고서가 작성된 경우 미작성 예약은 06:00, 06:30, 07:00의 3건 입니다.
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.REPORT.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(3)))
        .andExpect(jsonPath("$.list[0].startTime").value("06:00"))
        .andExpect(jsonPath("$.list[1].startTime").value("06:30"))
        .andExpect(jsonPath("$.list[2].startTime").value("07:00"))
        .andDo(print());

    // 06:30 등록
    rowsAffected = jdbcTemplate.update("UPDATE reservation SET report = 'test' WHERE id = 2");
    assertEquals(rowsAffected, 1);
    // 06:30, 11:00 시간에 학사보고서가 작성된 경우 미작성 예약은 0건 입니다.
    mockMvc.perform(get("/admin/v1/reservations/report")
            .header(AUTHORIZATION, token)
            .param("dateFrom", "2024-10-01")
            .param("dateTo", "2024-10-02")
            .param("order", "date")
            .param("reportCondition", ReportCondition.REPORT.name())
            .param("teacherId", teacher.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list", hasSize(0)))
        .andDo(print());

    // @formatter:on
  }

}