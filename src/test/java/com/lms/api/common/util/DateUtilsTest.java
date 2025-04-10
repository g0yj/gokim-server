package com.lms.api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DateUtilsTest {

  @Test
  void getWeekCount() {
    String yearMonth = "2024-07";
    int weekCount = DateUtils.getWeekCount(yearMonth);
    System.out.println("해당 월의 주 수: " + weekCount);
  }
}