package com.lms.api.migration.service;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserPhoneUpdateServiceTest {

  @Autowired
  private UserPhoneUpdateService userPhoneUpdateService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private EncryptionService encryptionService;

  @Test
  void updateUserPhonesTest() {
    userPhoneUpdateService.updateUserPhones();
  }

//  @Test
  void printDecryptedPhoneNumbersTest() {
    // 데이터베이스에서 암호화된 전화번호 조회
    String sql = "SELECT id, cell_phone FROM user_ WHERE cell_phone IS NOT NULL";
    List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);

    System.out.println("복호화된 전화번호 목록:");
    for (Map<String, Object> user : users) {
      String id = user.get("id").toString();
      String encryptedPhone = (String) user.get("cell_phone");
      String decryptedPhone = encryptionService.decrypt(encryptedPhone);
      System.out.printf("사용자 ID: %s, 전화번호: %s%n", id, decryptedPhone);
    }
    System.out.printf("총 %d 개의 전화번호를 복호화했습니다.%n", users.size());
  }
}