package com.lms.api.client.consul;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class EncryptionKeyServiceTest {

  @Autowired
  private EncryptionKeyService encryptionKeyService;

  @Test
  void getEncryptionKey() {
    String key = encryptionKeyService.getEncryptionKey();
    log.info("key: {}", key);
  }
}