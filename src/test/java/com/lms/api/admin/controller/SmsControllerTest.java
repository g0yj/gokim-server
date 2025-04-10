package com.lms.api.admin.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;
import com.lms.api.support.ControllerTestSupport;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class SmsControllerTest extends ControllerTestSupport {

  String adminToken = null;

  @BeforeEach
  void setUp() throws Exception {
    adminToken = login("jenchae@naver.com", "1111", UserType.A);
  }

  @Test
  @DisplayName("SMS 테스트")
  @Transactional
  void a() throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String cotent = """
        {
            "senderPhone": "02-2082-1105",
            "content": "테스트",
            "recipients": [
                {
                    "name": "ricky",
                    "phone": "010-3320-4796",
                    "email": "ilovecorea@gmail.com"
                }
            ]
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/sms/send")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(cotent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusCode").value("202"))
        .andExpect(jsonPath("$.requestId").isNotEmpty())
        .andDo(print());
  }

  @Test
  @DisplayName("LMS 테스트")
  @Transactional
  void b() throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String cotent = """
        {
            "senderPhone": "02-2082-1105",
            "content": "[랭귀지큐브 영어학원] 안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.",
            "recipients": [
                {
                    "name": "ricky",
                    "phone": "010-3320-4796"
                }
            ]
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/sms/send")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(cotent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusCode").value("202"))
        .andExpect(jsonPath("$.requestId").isNotEmpty())
        .andDo(print());
  }

  @Test
  @DisplayName("MMS 테스트")
  @Transactional
  void c() throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String cotent = """
        {
            "senderPhone": "02-2082-1105",
            "content": "[랭귀지큐브 영어학원] 안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.안녕하세요, 홍길동님. 2024년 8월 12일 오후 4시에 예정된 레벨 테스트에 대한 안내입니다. 테스트는 30분 동안 진행되며, ZOOM을 통해 온라인으로 진행됩니다. 테스트 결과는 다음 주에 개별적으로 통보될 예정입니다. 문의사항은 02-1234-5678로 연락주시기 바랍니다. 감사합니다.",
            "recipients": [
                {
                    "name": "ricky",
                    "phone": "010-3320-4796"
                }
            ]
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/sms/send")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(cotent))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
}
