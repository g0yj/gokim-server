package com.lms.api.admin.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DisplayName("공통코드")
//@ActiveProfiles("test")
@ActiveProfiles("ec")
class TeacherAdminControllerTest extends ControllerTestSupport {

  @Test
  void listTeachers() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/teachers")
            .header(AUTHORIZATION, token)
            .param("limit", "40")
            .param("active", "true")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.list.length()").value(40))
        .andExpect(jsonPath("$.limit").value(40))
        .andDo(print());
  }

  @Test
  @Transactional
  void deleteTeacher() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);

    mockMvc.perform(get("/admin/v1/teachers/{id}", "M1400118325784523")
            .header(AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true))
        .andDo(print());

    mockMvc.perform(delete("/admin/v1/teachers/{id}", "M1400118325784523")
            .header(AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());

    mockMvc.perform(get("/admin/v1/teachers/{id}", "M1400118325784523")
            .header(AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false))
        .andDo(print());
  }

  @Test
  void getTeacher() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    mockMvc.perform(get("/admin/v1/teachers/{id}", "M1400118325784523")
            .header(AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
