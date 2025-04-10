package com.lms.api.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class EncryptionServiceTest {

  @Autowired
  EncryptionService encryptionService;

  @Test
  void test() {
    String phoneNumber = "010-1234-5678";
    String encrypted = encryptionService.encrypt(phoneNumber);
    log.debug("암호화된 전화번호: {}", encrypted);

    String decrypted = encryptionService.decrypt(encrypted);
    assertEquals(phoneNumber, decrypted);
  }
}