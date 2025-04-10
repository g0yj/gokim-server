package com.lms.api.admin.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@DisplayName("강의스케쥴")
@ActiveProfiles("test")
class UserAdminControllerTest extends ControllerTestSupport  {

  @Autowired
  private ScheduleAdminService scheduleAdminService;

  @Test
  @DisplayName("회원 날짜에 주별 스케줄 조회\n")
  void a() throws Exception {
    String userId = "M1699689011700418";
    String teacherId = "M1661138591404520";
    String assistantTeacherId = "M1657064932771055";
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/users/{id}/schedules/by-week", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .param("dateMonth", "2024-10")
            .param("week", String.valueOf(2))
            .param("teacherId", teacherId)
            .param("assistantTeacherId", assistantTeacherId)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

}