package com.lms.api.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.lms.api.common.dto.UserType;
import com.lms.api.common.controller.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@MockMvcConfigWithEncoding
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  public String login(String id, String pwd, UserType userType) throws Exception {
    if (userType != null) {
      return adminLogin(id, pwd, userType);
    } else {
      return mobileLogin(id, pwd);
    }
  }

  private String adminLogin(String id, String pwd, UserType userType) throws Exception {
    LoginRequest loginRequest = LoginRequest.builder()
        .id(id).password(pwd).type(userType).build();

    MvcResult result = mockMvc.perform(post("/admin/v1/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn();

    return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
  }

  private String mobileLogin(String id, String pwd) throws Exception {
    LoginRequest loginRequest = LoginRequest.builder()
        .id(id).password(pwd).build();

    MvcResult result = mockMvc.perform(post("/mobile/v1/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn();

    return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
  }

}
