package com.lms.api.client.email;

import com.lms.api.client.email.dto.CreateSendEmailRequest;
import com.lms.api.client.email.dto.CreateSendLDFEmailRequest;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class EmailSenderServiceTest {

  @Value("${lms.client.email.sender}")
  private String sender;

  @Autowired
  private EmailSenderService emailSenderService;

  @Test
  void sendEmail_shouldSendEmailSuccessfully() throws MessagingException {
    CreateSendEmailRequest request = CreateSendEmailRequest.builder()
        .from(sender)
        .to("ilovecorea@gmail.com")
        .subject("Test 이메일")
        .content("Test 내용")
        .build();
    emailSenderService.sendEmail(request);
  }

  @Test
  void sendEmail_shouldSendLDFEmailSuccessfully() throws MessagingException {
    CreateSendLDFEmailRequest request =
        CreateSendLDFEmailRequest.builder()
            .from(sender)
            .to("ilovecorea@gmail.com")
            .title("Test 이메일")
            .name("Test 이름")
            .lesson("Test 강의")
            .lessonDate("Test 강의 날짜")
            .teacher("Test 강사")
            .contentSp("Test Stress and Pronunciation")
            .contentV("Test Vocabulary")
            .contentSg("Test Sentence Structure & Grammar")
            .contentC("Test Comment")
            .build();
    emailSenderService.sendLDFEmail(request);
  }
}