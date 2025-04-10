package com.lms.api.admin.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.admin.code.SearchSmsCode.SearchType;
import com.lms.api.admin.service.dto.statistics.Sms;
import com.lms.api.admin.service.dto.statistics.SmsTarget;
import com.lms.api.client.sms.SmsApiClientService;
import com.lms.api.common.dto.UserType;
import com.lms.api.common.exception.LmsException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
@DisplayName("SMS 시나리오")
@ActiveProfiles("test")
public class SmsIntTest extends IntegrationTestSupport {

  String adminToken = null;

  @Autowired
  SmsAdminService smsAdminService;

  @BeforeEach
  void setUp() throws Exception {
    adminToken = login("jenchae@naver.com", "1111", UserType.A);
  }

  @Test
  @DisplayName("SMS 즉시 발송")
  @Transactional
  void sendSms() throws Exception {
    // 사용자 생성
//    createUserStep("ilovecorea@gmail.com", "테스터", "010-3320-4796");

    String cotent = """
        {
            "senderPhone": "02-2082-1105",
            "content": "테스트",
            "recipients": [
                {
                    "name": "테스터",
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

    Thread.sleep(2000);

    // 대기중 목록 조회
    List<Sms> smsWaitingList = smsAdminService.listWaitingSms();
    assertEquals(smsWaitingList.size(), 1);
    assertEquals(smsWaitingList.get(0).getStatus(), SmsStatus.WAITING);

    Thread.sleep(5000);

    // 대기중 상태 갱신
    smsAdminService.checkUpdateSms();
    smsWaitingList = smsAdminService.listWaitingSms();
    assertEquals(smsWaitingList.size(), 0);

    // 완료 목록 조회
    List<Sms> smsSuccessList = smsAdminService.listSuccessSms();
    assertEquals(smsSuccessList.size(), 1);
    assertEquals(smsSuccessList.get(0).getStatus(), SmsStatus.SUCCESS);
  }

  @Test
  @DisplayName("SMS 예약 발송")
  @Transactional
  void sendReservedSms() throws Exception {
    // 사용자 생성
//    createUserStep("ilovecorea@gmail.com", "테스터", "010-3320-4796");

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime futureTime = now.plusMinutes(11).plusSeconds(30);
    String reservationDate = futureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    String cotent = """
        {
            "senderPhone": "02-2082-1105",
            "content": "예약발송[%s]",
            "reservationDate" : "%s",
            "recipients": [
                {
                    "name": "테스터",
                    "phone": "010-3320-4796"
                }
            ]
        }
        """.formatted(reservationDate, reservationDate);
    mockMvc.perform(post("/admin/v1/sms/send")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, adminToken)
            .content(cotent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusCode").value("202"))
        .andExpect(jsonPath("$.requestId").isNotEmpty())
        .andDo(print());

    Thread.sleep(2000);

    // 대기중 목록 조회
    List<Sms> smsWaitingList = smsAdminService.listWaitingSms();
    assertEquals(smsWaitingList.size(), 1);
    assertEquals(smsWaitingList.get(0).getStatus(), SmsStatus.WAITING);

    while (true) {
      Thread.sleep(10000);
      smsAdminService.checkUpdateSms();
      List<Sms> smsList = smsAdminService.listWaitingSms();
      if (smsList.size() == 0) {
        break;
      }
    }

    List<Sms> smsList = smsAdminService.listSuccessSms();
    log.debug("## smsList:{}", smsList);
    assertEquals(smsList.size(), 1);
    for (Sms sms : smsList) {
      assertEquals(sms.getStatus(), SmsStatus.SUCCESS);
      for (SmsTarget smsTarget : sms.getSmsTargets()) {
        assertEquals(smsTarget.getStatus(), SmsStatus.SUCCESS);
      }
    }
  }

  @Test
  @DisplayName("SMS 예약 발송")
  @Transactional
  void c() throws Exception {
    // 사용자 생성
    createUserStep("ilovecorea@gmail.com", "테스터", "010-3320-4796");

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime futureTime = now.plusMinutes(10);
    CreateSendSms createSendSms = CreateSendSms.builder()
        .senderName("junit")
        .sendType(SearchType.S)
        .reservationDate(futureTime)
        .content("즉시발송:" + LocalDateTime.now())
        .recipients(List.of(Recipient.builder()
            .phone("010-3320-4796")
            .name("ricky")
            .build()))
        .build();

    // 메시지 발송
    LmsException exception = assertThrows(LmsException.class, () -> {
      CreateSendSmsResponse response = smsAdminService.createSendSms(createSendSms);
    });
    // 예외 메시지 확인
    assertEquals("잘못된 요청입니다. 문자 메시지는 예약 시간을 현재 시각 기준으로 최소 10분 이후로 설정해야 합니다",
        exception.getMessage());
  }

  void createUserStep(String loginId, String name, String phone) throws Exception {
    String content = """
        {
             "address": "경기 안산시 상록구 가루개로 42-15 (양상동)",
             "addressType": "H",
             "cellPhone": "%s",
             "company": "등록",
             "coursePurposes": ["STUDY_ABROAD","TEST"],
             "detailedAddress": "detailedAddress",
             "email": "%s",
             "etcLanguage" : "etcLanguage",
             "firstNameEn": "firstNameEn",
             "foreignCountry": "foreignCountry",
             "foreignPeriod": "foreignPeriod",
             "foreignPurpose": "foreignPurpose",
             "gender": "M",
             "isActive": false,
             "isOfficeWorker":false,
             "isReceiveEmail": true,
             "isReceiveSms": true,
             "joinPath": "ONLINE",
             "languageSkills": [{"languageTest": "TOEIC", "score": "900점이상"}, {"languageTest": "TOEIC_S", "score": "8(190-200)"}],
             "languages": ["EN", "CN", "JP"],
             "lastNameEn": "lastNameEn",
             "loginId":  "%s",
             "name": "%s",
             "nickname": "nickname",
             "note": "note",
             "password": "1111",
             "phone": "%s",
             "phoneType":  "H",
             "position": "position",
             "textbook":  "textbook",
             "type": "S",
             "zipcode":"15208"
             }
        """.trim().formatted(phone, loginId, loginId, name, phone);
    createUser(adminToken, content);
  }
}