package com.lms.api.migration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class DatabaseResetServiceTest {

  @Autowired
  private DatabaseResetService databaseResetService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  @Transactional
  void test() {
    jdbcTemplate.execute("""
      INSERT INTO `user_` 
      (`id`, `login_id`, `name`, `password`, `type`, `gender`, `cell_phone`, `is_receive_sms`, `email`, `is_receive_email`, 
      `is_active`, `created_on`, `modified_on`) 
      VALUES 
      ('USER001', 'testuser', 'Test User', 'password', 'S', 'M', '010-9876-5432', 'Y', 'testuser@example.com', 'Y', 
      1, NOW(), NOW());
      """);
    databaseResetService.truncateTables();
    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_", Integer.class);
    assertEquals(0, count, "The table 'user_' should be truncated and empty.");
  }
}