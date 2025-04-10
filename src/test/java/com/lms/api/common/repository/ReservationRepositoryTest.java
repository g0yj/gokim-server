package com.lms.api.common.repository;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ReservationRepositoryTest {

  @Autowired
  ReservationRepository reservationRepository;

  @Test
  void findReservationsForTomorrow() {
    List<ReservationNotification> reservationNotifications =  reservationRepository.findReservationsForTomorrow();
    for (ReservationNotification r : reservationNotifications) {
      log.debug("## getUserId:{}", r.getUserId());
      log.debug("## getTeacherId:{}", r.getTeacherId());
      log.debug("## date:{}", r.getDate());
      log.debug("## getStartTime:{}", r.getStartTime());
      log.debug("## getEndTime:{}", r.getEndTime());
      log.debug("## getName:{}", r.getName());
      log.debug("## getCellPhone:{}", r.getCellPhone());
    }
  }
}