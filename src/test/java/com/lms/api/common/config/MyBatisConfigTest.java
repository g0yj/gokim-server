package com.lms.api.common.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MyBatisConfigTest {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void testDataSourceInitialization() {
    // DataSource가 null이 아닌지 확인
    assertNotNull(dataSource, "DataSource should not be null");

    // JdbcTemplate을 사용하여 간단한 쿼리 실행 (예: 현재 날짜 조회)
    String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
    assertNotNull(result, "JdbcTemplate query result should not be null");
  }

}