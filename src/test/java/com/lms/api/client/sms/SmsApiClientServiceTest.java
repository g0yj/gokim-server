package com.lms.api.client.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lms.api.client.sms.dto.CreateSendNaverSmsRequest;
import com.lms.api.client.sms.dto.CreateSendNaverSmsResponse;
import com.lms.api.client.sms.dto.ListSmsSendRequestResponse;
import com.lms.api.client.sms.dto.ListSmsSendRequestResponse.Message;
import com.lms.api.client.sms.dto.ListSmsSendResultResponse;
import com.lms.api.client.sms.dto.ListSmsSendResultResponse.SmsStatus;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class SmsApiClientServiceTest {

  @Autowired
  SmsApiClientService smsApiClientService;

  @Test
  @DisplayName("SMS 서비스 즉시 발송")
  void a() throws Exception {
    // 발송
    CreateSendNaverSmsResponse createSendNaverSmsResponse = sendStep();
    log.info("발송 완료");
    // 발송 요청 조회
    ListSmsSendRequestResponse listSmsSendRequestResponse = sendRequestStep(
        createSendNaverSmsResponse.getRequestId());
    log.info("발송 요청 조회 완료");
    sendResultStep(createSendNaverSmsResponse.getRequestId(), listSmsSendRequestResponse, SmsStatus.PROCESSING);
    log.info("발송 결과 조회 완료1");
    // 10초 대기
    Thread.sleep(10000);
    // 발송 요청 조회
    sendResultStep(createSendNaverSmsResponse.getRequestId(), listSmsSendRequestResponse, SmsStatus.COMPLETED);
    log.info("발송 결과 조회 완료2");
  }

  @Step("전송")
  CreateSendNaverSmsResponse sendStep() {
    // 현재 시간을 가져오기 위한 DateTimeFormatter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String currentTime = LocalDateTime.now().format(formatter);

    CreateSendNaverSmsRequest request = CreateSendNaverSmsRequest.builder()
        .from("02-2082-1105")
        .content("서비스 즉시 발송:" + currentTime)
        .messages(List.of(
            CreateSendNaverSmsRequest.Message.builder()
                .to("010-3320-4796")
                .build()
//            CreateSendNaverSmsRequest.Message.builder()
//                .to("010-9190-1376")
//                .build()
        ))
        .reserveTime(null)
        .build();

    // 발송
    CreateSendNaverSmsResponse createSendNaverSmsResponse = smsApiClientService.send(request)
        .doOnError(e -> log.error("발송 에러", e))
        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to send SMS.", e)))
        .block();

    assertEquals(createSendNaverSmsResponse.getStatusCode(), "202");
    return createSendNaverSmsResponse;
  }

  @Step("전송 요청 조회")
  ListSmsSendRequestResponse sendRequestStep(String requestId) {
    ListSmsSendRequestResponse listSmsSendRequestResponse = smsApiClientService.listSendRequest(requestId)
        .doOnError(e -> log.error("발송요청조회 에러", e))
        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to listSmsStatus.", e)))
        .block();

    assertEquals(listSmsSendRequestResponse.getStatusCode(), "202");
    return listSmsSendRequestResponse;
  }

  @Step("전송 결과 조회")
  void sendResultStep(String requestId, ListSmsSendRequestResponse listSmsSendRequestResponse, SmsStatus status) {
    for (Message message : listSmsSendRequestResponse.getMessages()) {
      // 발송 결과 조회
      ListSmsSendResultResponse listSmsSendResultResponse = smsApiClientService.listSendResult(
              message.getMessageId())
          .doOnError(e -> log.error("발송결과조회 에러", e))
          .onErrorResume(e -> Mono.error(new RuntimeException("Failed to listTargetStatus", e)))
          .block();

      assertEquals(listSmsSendResultResponse.getStatusCode(), "200");
      assertEquals(listSmsSendResultResponse.getMessages().get(0).getRequestId(), requestId);
      assertEquals(listSmsSendResultResponse.getMessages().get(0).getStatus(), status);
    }
  }
}