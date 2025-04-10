package com.lms.api.client.holdiay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lms.api.admin.integration.IntegrationTestSupport;
import com.lms.api.client.holiday.HolidayInfoService;
import com.lms.api.client.holiday.dto.HolidayRequest;
import com.lms.api.client.holiday.dto.HolidayResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@Tag("integration")
@DisplayName("공휴일정보")
@ActiveProfiles("test")
public class HolidayServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private HolidayInfoService holidayInfoService;

  @Test
  public void testGetHoliDeInfo() throws Exception {
    // items 가 배열인 경우
    HolidayRequest request = new HolidayRequest("2023", "01", 30);
    HolidayResponse response = holidayInfoService.getHolidayInfo(request);

    assertEquals("00", response.getResponse().getHeader().getResultCode());
    assertEquals(5, response.getResponse().getBody().getTotalCount());
    assertEquals("1월1일", response.getResponse().getBody().getItems().getItemList().get(0).getDateName());
    assertEquals(20230101, response.getResponse().getBody().getItems().getItemList().get(0).getLocdate());
    assertEquals("설날", response.getResponse().getBody().getItems().getItemList().get(1).getDateName());
    assertEquals(20230121, response.getResponse().getBody().getItems().getItemList().get(1).getLocdate());

    // items 가 단일 객체인 경우
    request = new HolidayRequest("2024", "01", 30);
    response = holidayInfoService.getHolidayInfo(request);

    assertEquals("00", response.getResponse().getHeader().getResultCode());
    assertEquals(1, response.getResponse().getBody().getTotalCount());
    assertEquals("1월1일", response.getResponse().getBody().getItems().getItemList().get(0).getDateName());
    assertEquals(20240101, response.getResponse().getBody().getItems().getItemList().get(0).getLocdate());

    // items가 "" 인 경우(공휴일이 없는 경우)
    request = new HolidayRequest("2023", "02", 30);
    response = holidayInfoService.getHolidayInfo(request);

    assertEquals("00", response.getResponse().getHeader().getResultCode());
    assertEquals(0, response.getResponse().getBody().getTotalCount());

  }

}
