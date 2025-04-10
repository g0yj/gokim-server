package com.lms.api.admin.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DisplayName("공휴일정보")
@ActiveProfiles("test")
@SpringBootTest
public class CalendarSchedulingServiceIntTest {

  @Autowired
  private CalendarSchedulingService calendarSchedulingService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  @Transactional
  public void testUpdateHolidays() throws Exception {
    LocalDate testDate = LocalDate.of(2024, 9, 15);
    calendarSchedulingService.setTestToday(testDate);
    calendarSchedulingService.updateHolidays();

    // 공휴일이 제대로 업데이트되었는지 확인 (2024년 9월 16일 추석)
    String sql = "SELECT is_holiday FROM calendar WHERE calendar_date = '2024-09-16'";
    Boolean isHoliday = jdbcTemplate.queryForObject(sql, Boolean.class);
    assertTrue(isHoliday, "2024-09-16 (추석) should be marked as a holiday in the database");

    // 추가로 다른 공휴일도 확인 (2024년 9월 17일과 18일)
    sql = "SELECT is_holiday FROM calendar WHERE calendar_date = '2024-09-17'";
    isHoliday = jdbcTemplate.queryForObject(sql, Boolean.class);
    assertTrue(isHoliday, "2024-09-17 (추석) should be marked as a holiday in the database");

    sql = "SELECT is_holiday FROM calendar WHERE calendar_date = '2024-09-18'";
    isHoliday = jdbcTemplate.queryForObject(sql, Boolean.class);
    assertTrue(isHoliday, "2024-09-18 (추석) should be marked as a holiday in the database");
  }

  @Test
  public void insertHolidays() throws Exception {
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate currentDate = LocalDate.now();

    while (!startDate.isAfter(currentDate)) {
      calendarSchedulingService.setTestToday(startDate);
      calendarSchedulingService.updateHolidays();
      startDate = startDate.plusMonths(1);
    }
  }
}
