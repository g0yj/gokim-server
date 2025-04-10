package com.lms.api.common.mybatis;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CalculationMapperTest {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private CalculatesResponseMapper calculatesResponseMapper;

  ObjectMapper mapper = new ObjectMapper();

  @Test
  public void test() {
    String dateFrom = "2024-01-01";
    String dateTo = "2024-01-02";
//    String creatorName = "박수현";
    String creatorName = null;
    List<ListCalculatesResponse.Summary> summaries = orderRepository.listCalculateSummaryForPeriod(
            dateFrom, dateTo, creatorName)
        .stream()
        .map(calculatesResponseMapper::toSummary)
        .collect(Collectors.toUnmodifiableList());

    List<ListCalculatesResponse.Order> orders = orderRepository.listCalculatesOrderForPeriod(
        dateFrom, dateTo, creatorName)
        .stream()
        .map(calculatesResponseMapper::toOrder)
        .collect(Collectors.toUnmodifiableList());

    List<ListCalculatesResponse.Payment> payments = orderRepository.listCalculatesPaymentForPeriod(
        dateFrom, dateTo, creatorName)
        .stream()
        .map(calculatesResponseMapper::toPayment)
        .collect(Collectors.toUnmodifiableList());

    // Map으로 payments를 orderId 기준으로 그룹화
    Map<String, List<Payment>> paymentsMap = payments.stream()
        .collect(Collectors.groupingBy(ListCalculatesResponse.Payment::getOrderId));

    // orders 리스트를 순회하면서 해당하는 payments를 찾아서 추가
    for (ListCalculatesResponse.Order order : orders) {
      List<ListCalculatesResponse.Payment> matchingPayments = paymentsMap.get(order.getId());
      if (matchingPayments != null) {
        order.getPayments().addAll(matchingPayments);
      }
    }

    var result = ListCalculatesResponse.builder()
        .summaries(summaries)
        .orders(orders)
        .build();

    log.debug("## result:{}", result);
  }

  /**
   * 처리자 포함
   */
 /* @Test
  public void testListCalculateForPeriodWithCreatorName() throws Exception {
    String dateFrom = "2024-01-01";
    String dateTo = "2024-01-02";
    String creatorName = "박수현";

    // summary
    List<ListCalculatesResponse.Summary> summaries = calculationMapper.listCalculateSummaryForPeriod(
        dateFrom, dateTo,
        creatorName);
    log.debug("summaries: {}",
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(summaries));

    assertEquals(2, summaries.size());

    ListCalculatesResponse.Summary firstSummary = summaries.get(0);
    assertEquals("박수현", firstSummary.getCreatorName());
    assertEquals(3280000, firstSummary.getPaymentAmount());
    assertEquals(1640000, firstSummary.getRefundAmount());

    ListCalculatesResponse.Summary secondSummary = summaries.get(1);
    assertEquals("합계", secondSummary.getCreatorName());
    assertEquals(3280000, secondSummary.getPaymentAmount());
    assertEquals(1640000, secondSummary.getRefundAmount());

    // order
    List<ListCalculatesResponse.Order> orders = calculationMapper.listCalculatesOrderForPeriod(
        dateFrom, dateTo, creatorName);
    log.debug("orders: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(orders));
    assertEquals(3, orders.size());

    // payment
    List<ListCalculatesResponse.Payment> payments = calculationMapper.listCalculatesPaymentForPeriod(
        dateFrom, dateTo, creatorName);
    log.debug("payments: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payments));
    assertEquals(4, payments.size());
  }*/

  /**
   * 처리자 제외
   */
  /*@Test
  public void testListCalculateForPeriod() throws Exception {
    String dateFrom = "2024-01-01";
    String dateTo = "2024-01-02";

    // summary
    List<Summary> summaries = calculationMapper.listCalculateSummaryForPeriod(dateFrom, dateTo,
        null);
    log.debug("summaries: {}",
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(summaries));

    assertEquals(4, summaries.size());

    Summary firstSummary = summaries.get(0);
    assertEquals("박수현", firstSummary.getCreatorName());
    assertEquals(3280000, firstSummary.getPaymentAmount());
    assertEquals(1640000, firstSummary.getRefundAmount());

    Summary secondSummary = summaries.get(1);
    assertEquals("박지수", secondSummary.getCreatorName());
    assertEquals(4610000, secondSummary.getPaymentAmount());
    assertEquals(0, secondSummary.getRefundAmount());

    Summary thirdSummary = summaries.get(2);
    assertEquals("한시내", thirdSummary.getCreatorName());
    assertEquals(1640000, thirdSummary.getPaymentAmount());
    assertEquals(0, thirdSummary.getRefundAmount());

    Summary totalSummary = summaries.get(3);
    assertEquals("합계", totalSummary.getCreatorName());
    assertEquals(9530000, totalSummary.getPaymentAmount());
    assertEquals(1640000, totalSummary.getRefundAmount());

    // order
    List<ListCalculatesResponse.Order> orders = calculationMapper.listCalculatesOrderForPeriod(
        dateFrom, dateTo, null);
    log.debug("orders: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(orders));
    assertEquals(6, orders.size());

    // payment
    List<ListCalculatesResponse.Payment> payments = calculationMapper.listCalculatesPaymentForPeriod(
        dateFrom, dateTo, null);
    log.debug("payments: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payments));
    assertEquals(7, payments.size());
  }*/
}