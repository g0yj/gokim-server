package com.lms.api.admin.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
//@ActiveProfiles("ec")
public class ScheduleControllerTest extends ControllerTestSupport {

  @Test
  void listSchedules() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);

    mockMvc.perform(get("/admin/v1/reservations/schedules")
            .param("date", "2024-09-11")
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void listSchedules2() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);

    mockMvc.perform(get("/admin/v1/reservations/schedules")
            .param("date", "2024-09-10")
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
