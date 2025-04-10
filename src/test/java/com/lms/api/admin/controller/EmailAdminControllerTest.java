package com.lms.api.admin.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class EmailAdminControllerTest extends ControllerTestSupport {

  @Autowired private LdfRepository ldfRepository;
  @Autowired private EmailRepository emailRepository;

  @Test
  @Transactional
  void send() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    String content =
        """
        {
             "senderEmail": "webmaster@languagecube.kr",
             "title": "이메일 테스트!",
             "content": "이메일 테스트입니다.",
             "recipients": [
                 {
                     "name": "테스트",
                     "email": "ilovecorea@gmail.com"
                 }
             ]
         }
        """;
    mockMvc
        .perform(
            post("/admin/v1/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token)
                .content(content))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void sendLDFBadRequest() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    String content =
        """
        {
             "email": "ilovecorea@gmail.com",
             "name": "조원빈",
             "title": "이메일 테스트!",
             "lesson": "test",
             "lessonDate": "2024-08-22(Thu) 16:00 ~ 16:30",
             "teacher": "나강사",
             "contentSp": "테스트 contentSp",
             "contentV": "테스트 contentV",
             "contentSg": "테스트 contentSg",
             "contentC": "테스트 contentC"
         }
        """;
    mockMvc
        .perform(
            post("/admin/v1/email/ldf/send")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token)
                .content(content))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  void sendLDFNotFound() throws Exception {
    String token = login("jenchae@naver.com", "1111", UserType.A);
    String content =
        """
        {
             "email": "ilovecorea@gmail.com",
             "name": "조원빈",
             "title": "이메일 테스트!",
             "lesson": "test",
             "lessonDate": "2024-08-22(Thu) 16:00 ~ 16:30",
             "teacher": "나강사",
             "contentSp": "테스트 contentSp",
             "contentV": "테스트 contentV",
             "contentSg": "테스트 contentSg",
             "contentC": "테스트 contentC",
             "ldfId": 0
         }
        """;
    mockMvc
        .perform(
            post("/admin/v1/email/ldf/send")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token)
                .content(content))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @Transactional
  void sendLDFOk() throws Exception {
    LdfEntity ldfEntity = ldfRepository.findById(10740L).orElseThrow();

    String token = login("jenchae@naver.com", "1111", UserType.A);
    String content =
        """
        {
             "email": "ilovecorea@gmail.com",
             "name": "조원빈",
             "title": "이메일 테스트!",
             "lesson": "test",
             "lessonDate": "2024-08-22(Thu) 16:00 ~ 16:30",
             "teacher": "나강사",
             "contentSp": "테스트 contentSp",
             "contentV": "테스트 contentV",
             "contentSg": "테스트 contentSg",
             "contentC": "테스트 contentC",
             "ldfId": %d
         }
        """
            .formatted(ldfEntity.getId());
    mockMvc
        .perform(
            post("/admin/v1/email/ldf/send")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token)
                .content(content))
        .andExpect(status().isOk())
        .andDo(print());

    List<EmailEntity> emailEntities =
        emailRepository.findAllByLdfId(ldfEntity.getId());
    assertNotNull(emailEntities);
    assertTrue(emailEntities.size() > 0);
    for (EmailEntity email: emailEntities) {
      log.debug("## ldfId: {}", email.getLdfId());
    }
  }
}
