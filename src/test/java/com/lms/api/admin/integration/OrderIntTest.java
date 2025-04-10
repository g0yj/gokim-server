package com.lms.api.admin.integration;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.common.dto.UserType;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
@Slf4j
@Tag("integration")
@DisplayName("주문시나리오")
@ActiveProfiles("test")
class OrderIntTest extends IntegrationTestSupport {

  //@formatter: off
  private final String CREATE_USER = """
      {
           "address": "경기 안산시 상록구 가루개로 42-15 (양상동)",
           "addressType": "H",
           "cellPhone": "010-1234-1234",
           "company": "등록",
           "coursePurposes": ["STUDY_ABROAD","TEST"],
           "detailedAddress": "detailedAddress",
           "email": "%s",
           "etcLanguage" : "etcLanguage",
           "firstNameEn": "firstNameEn",
           "foreignCountry": "foreignCountry",
           "foreignPeriod": "foreignPeriod",
           "foreignPurpose": "foreignPurpose",
           "gender": "M",
           "isActive": false,
           "isOfficeWorker":false,
           "isReceiveEmail": false,
           "isReceiveSms": false,
           "joinPath": "ONLINE",
           "languageSkills": [{"languageTest": "TOEIC", "score": "900점이상"}, {"languageTest": "TOEIC_S", "score": "8(190-200)"}],
           "languages": ["EN", "CN", "JP"],
           "lastNameEn": "lastNameEn",
           "loginId":  "%s",
           "name": "등록:홍길동",
           "nickname": "nickname",
           "note": "note",
           "password": "1111",
           "phone": "031-232-1234",
           "phoneType":  "H",
           "position": "position",
           "textbook":  "textbook",
           "type": "S",
           "zipcode":"15208"
           }
      """.trim();

  String token = null;

  String loginId = null;

  @BeforeEach
  void setUp() throws Exception {
    token = login("jenchae@naver.com", "1111", UserType.A);
    loginId = generateRandomUserId();
  }

  @Test
  @Transactional
  @DisplayName("결제 환불 시나리오")
  void a() throws Exception {
    createUser(token, CREATE_USER.formatted(loginId, loginId));
    ResultActions resultActions = getUser(token, "S", "email", loginId)
        .andExpect(jsonPath("$.list").value(notNullValue()));

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    assertNotNull(userId);
    //----------------------------------------------------------------------------------------------
    step("Step 1: 회원 등록", () -> System.out.println("Step 1: 회원 등록"));
    //----------------------------------------------------------------------------------------------

    String content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
          "price":60000
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 2: 상품 등록", () -> System.out.println("Step 2: 상품 등록"));
    //----------------------------------------------------------------------------------------------

    resultActions = mockMvc.perform(get("/admin/v1/products/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andDo(print());

    final String productId = extractOtherFieldFromArray(resultActions, "name", "테스트상품", "id");
    assertNotNull(productId);
    //----------------------------------------------------------------------------------------------
    step("Step 3: 상품목록 조회", () -> System.out.println("Step 3: 상품목록 조회"));
    //----------------------------------------------------------------------------------------------

    content = """
        {
             "quantity": 10,
             "teacherId": "M1709859614116619",
             "assistantTeacherId": "M1681990831501448",
             "billingAmount": 600000
        }
        """.trim();
    content = addFieldToJson(content, "productId", productId);
    assertNotNull(userId);
    MvcResult mvcResult = mockMvc.perform(post("/admin/v1/users/{id}/orders/products", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token)
                    .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(notNullValue()))
            .andDo(print())
            .andReturn();

    final String orderId = extractFieldFromResponse(mvcResult, "orderId");
    assertNotNull(orderId);
    //----------------------------------------------------------------------------------------------
    step("Step 4: 상품 주문", () -> System.out.println("Step 4: 상품 주문"));
    //----------------------------------------------------------------------------------------------

    mockMvc.perform(get("/admin/v1/users/{id}/orders", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order[0].id").value(orderId))
            .andExpect(jsonPath("$.order[0].supplyAmount").value(600000)) // (수정) 공급가액 계산 수정 60000->600000
            .andExpect(jsonPath("$.order[0].discountAmount").value(0))
            .andExpect(jsonPath("$.order[0].billingAmount").value(600000))
            .andExpect(jsonPath("$.order[0].paymentAmount").value(0))
            .andExpect(jsonPath("$.order[0].receivableAmount").value(600000))
            .andExpect(jsonPath("$.order[0].createdOn").value(startsWith(DateUtils.getString(LocalDate.now()))))
            .andExpect(jsonPath("$.order[0].creatorName").value("채인숙"))
            .andExpect(jsonPath("$.order[0].orderProductName").value("테스트상품/1개월/10회"))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 5: 주문목록 조회", () -> System.out.println("Step 5: 주문목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 결제 등록
    content = """
        {
           "type":"I",
           "paymentDate":"%s",
           "cashAmount":"10000",
           "depositAmount":"20000",
           "accountHolder":"예금주",
           "receivableAmount":"550000",
           "receivableReason":"예상보다 비쌈",
           "recallDate":"2024-09-10",
           "cards":[
             {
                 "amount": 15000,
                 "code": "2090073",
                 "cardNumber": "12345",
                 "installmentMonths": 0,
                 "approvalNumber": ""
             },
             {
                 "amount": 5000,
                 "code": "2090074",
                 "cardNumber": "00000",
                 "installmentMonths": 0,
                 "approvalNumber": ""
             }
           ]
         }
        """.trim();
    content = String.format(content, LocalDate.now());
    mockMvc.perform(post("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 6: 결제 등록", () -> System.out.println("Step 6: 결제 등록"));
    //----------------------------------------------------------------------------------------------

    mvcResult = mockMvc.perform(
                    get("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.billingAmount").value(600000))
            .andExpect(jsonPath("$.paymentAmount").value(50000))
            .andExpect(jsonPath("$.refundAmount").value(0))
            .andExpect(jsonPath("$.receivableAmount").value(550000))
            .andExpect(jsonPath("$.payments", hasSize(4)))

            // payments 배열의 첫 번째 항목 검증
            .andExpect(jsonPath("$.payments[0].id").value(notNullValue()))
            .andExpect(jsonPath("$.payments[0].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[0].type").value("신규"))
            .andExpect(jsonPath("$.payments[0].paymentMethod").value("현금"))
            .andExpect(jsonPath("$.payments[0].paymentAmount").value(10000))
            .andExpect(jsonPath("$.payments[0].isCancelable").value(true))
            .andExpect(jsonPath("$.payments[0].modifierName").value("채인숙"))

            // payments 배열의 두 번째 항목 검증
            .andExpect(jsonPath("$.payments[1].id").value(notNullValue()))
            .andExpect(jsonPath("$.payments[1].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[1].type").value("신규"))
            .andExpect(jsonPath("$.payments[1].paymentMethod").value("예금"))
            .andExpect(jsonPath("$.payments[1].paymentAmount").value(20000))
            .andExpect(jsonPath("$.payments[1].accountHolder").value("예금주"))
            .andExpect(jsonPath("$.payments[1].isCancelable").value(true))
            .andExpect(jsonPath("$.payments[1].modifierName").value("채인숙"))

            // payments 배열의 세 번째 항목 검증
            .andExpect(jsonPath("$.payments[2].id").value(notNullValue()))
            .andExpect(jsonPath("$.payments[2].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[2].type").value("신규"))
            .andExpect(jsonPath("$.payments[2].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[2].paymentAmount").value(15000))
            .andExpect(jsonPath("$.payments[2].code").value("2090073"))
            .andExpect(jsonPath("$.payments[2].cardNumber").value("12345"))
            .andExpect(jsonPath("$.payments[2].isCancelable").value(true))
            .andExpect(jsonPath("$.payments[2].modifierName").value("채인숙"))

            // payments 배열의 네 번째 항목 검증
            .andExpect(jsonPath("$.payments[3].id").value(notNullValue()))
            .andExpect(jsonPath("$.payments[3].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[3].type").value("신규"))
            .andExpect(jsonPath("$.payments[3].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[3].paymentAmount").value(5000))
            .andExpect(jsonPath("$.payments[3].code").value("2090074"))
            .andExpect(jsonPath("$.payments[3].cardNumber").value("00000"))
            .andExpect(jsonPath("$.payments[3].isCancelable").value(true))
            .andExpect(jsonPath("$.payments[3].modifierName").value("채인숙"))
            .andDo(print())
            .andReturn();

    List<String> paymentsIds = extractValueFromJson(mvcResult, "$.payments[*].id", List.class);
    assertTrue(paymentsIds.size() > 0);
    //----------------------------------------------------------------------------------------------
    step("Step 7: 결제목록 조회", () -> System.out.println("Step 7: 결제목록 조회"));
    //----------------------------------------------------------------------------------------------

    for (String paymentId : paymentsIds) {
      mockMvc.perform(
                      delete("/admin/v1/users/{id}/orders/{orderId}/payments/{paymentId}", userId, orderId,
                              paymentId)
                              .contentType(MediaType.APPLICATION_JSON)
                              .header(AUTHORIZATION, token))
              .andExpect(status().isOk())
              .andDo(print());
    }
    //----------------------------------------------------------------------------------------------
    step("Step 8: 결제 취소", () -> System.out.println("Step 8: 결제 취소"));
    //----------------------------------------------------------------------------------------------

    // 결제 목록 조회
    mockMvc.perform(
                    get("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.billingAmount").value(600000))
            .andExpect(jsonPath("$.paymentAmount").value(0))
            .andExpect(jsonPath("$.refundAmount").value(0))
            .andExpect(jsonPath("$.receivableAmount").value(600000))
            .andExpect(jsonPath("$.payments", hasSize(8)))

            // payments 배열 검증

            .andExpect(jsonPath("$.payments[0].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[0].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[0].type").value("신규"))
            .andExpect(jsonPath("$.payments[0].paymentMethod").value("현금"))
            .andExpect(jsonPath("$.payments[0].paymentAmount").value(10000))
            .andExpect(jsonPath("$.payments[0].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[0].modifierName").value("채인숙"))

            .andExpect(jsonPath("$.payments[1].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[1].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[1].type").value("신규"))
            .andExpect(jsonPath("$.payments[1].paymentMethod").value("예금"))
            .andExpect(jsonPath("$.payments[1].paymentAmount").value(20000))
            .andExpect(jsonPath("$.payments[1].accountHolder").value("예금주"))
            .andExpect(jsonPath("$.payments[1].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[1].modifierName").value("채인숙"))

            .andExpect(jsonPath("$.payments[2].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[2].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[2].type").value("신규"))
            .andExpect(jsonPath("$.payments[2].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[2].paymentAmount").value(15000))
            .andExpect(jsonPath("$.payments[2].code").value("2090073"))
            .andExpect(jsonPath("$.payments[2].cardNumber").value("12345"))
            .andExpect(jsonPath("$.payments[2].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[2].modifierName").value("채인숙"))

            .andExpect(jsonPath("$.payments[3].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[3].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[3].type").value("신규"))
            .andExpect(jsonPath("$.payments[3].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[3].paymentAmount").value(5000))
            .andExpect(jsonPath("$.payments[3].code").value("2090074"))
            .andExpect(jsonPath("$.payments[3].cardNumber").value("00000"))
            .andExpect(jsonPath("$.payments[3].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[3].modifierName").value("채인숙"))

            // 취소된 payments 배열 항목 검증
            .andExpect(jsonPath("$.payments[4].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[4].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[4].type").value("취소"))
            .andExpect(jsonPath("$.payments[4].paymentMethod").value("현금"))
            .andExpect(jsonPath("$.payments[4].paymentAmount").value(-10000))
            .andExpect(jsonPath("$.payments[4].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[4].isCancelable").value(false))

            .andExpect(jsonPath("$.payments[5].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[5].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[5].type").value("취소"))
            .andExpect(jsonPath("$.payments[5].paymentMethod").value("예금"))
            .andExpect(jsonPath("$.payments[5].paymentAmount").value(-20000))
            .andExpect(jsonPath("$.payments[5].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[5].isCancelable").value(false))

            .andExpect(jsonPath("$.payments[6].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[6].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[6].type").value("취소"))
            .andExpect(jsonPath("$.payments[6].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[6].paymentAmount").value(-15000))
            .andExpect(jsonPath("$.payments[6].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[6].isCancelable").value(false))

            .andExpect(jsonPath("$.payments[7].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[7].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[7].type").value("취소"))
            .andExpect(jsonPath("$.payments[7].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[7].paymentAmount").value(-5000))
            .andExpect(jsonPath("$.payments[7].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[7].isCancelable").value(false))

            .andDo(print())
            .andReturn();
    //----------------------------------------------------------------------------------------------
    step("Step 9: 결제목록 조회", () -> System.out.println("Step 9: 결제목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 회원 주문 조회
    mvcResult = mockMvc.perform(get("/admin/v1/users/{id}/orders/{orderId}", userId, orderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.supplyAmount").value(600000)) //(수정) 공급가액 계산 수정 60000->600000
            .andExpect(jsonPath("$.discountAmount").value(0))
            .andExpect(jsonPath("$.billingAmount").value(600000))
            .andExpect(jsonPath("$.refundAmount").value(0))
            .andExpect(jsonPath("$.orderProducts", hasSize(1)))

            .andExpect(jsonPath("$.orderProducts[0].id").value(startsWith("I")))
            .andExpect(jsonPath("$.orderProducts[0].name").value("테스트상품/1개월/10회"))
            .andExpect(jsonPath("$.orderProducts[0].amount").value(600000)) // (수정) 주문 별 공급가액
            .andExpect(jsonPath("$.orderProducts[0].discountAmount").value(0))
            .andExpect(jsonPath("$.orderProducts[0].billingAmount").value(600000))
            .andExpect(jsonPath("$.orderProducts[0].refundAmount").value(0))
            .andExpect(jsonPath("$.orderProducts[0].productType").value("Y"))
            .andExpect(jsonPath("$.orderProducts[0].createdOn").value(startsWith(DateUtils.getString(LocalDate.now()))))
            .andExpect(jsonPath("$.orderProducts[0].orderType").value("신규"))
            .andExpect(jsonPath("$.orderProducts[0].refundType").value("REFUNDABLE"))
            .andExpect(jsonPath("$.orderProducts[0].retake").value(false))
            .andExpect(jsonPath("$.orderProducts[0].hasReservations").value(false))
            .andExpect(jsonPath("$.orderProducts[0].hasPayments").value(true))

            .andExpect(jsonPath("$.isCancelable").value(false))
            .andDo(print())
            .andReturn();
    final String orderProductId = extractValueFromJson(mvcResult, "$.orderProducts[0].id",
            String.class);

    content = """
        {
            "refundDate":"%s",
            "cashAmount":600000,
            "refundReason":"다른 강의 주문"
        }
        """.trim();
    content = String.format(content, LocalDate.now());
    mockMvc.perform(
                    post("/admin/v1/users/{id}/orders/{orderId}/orderProducts/{orderProductId}/refund", userId,
                            orderId, orderProductId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, token)
                            .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 10: 환불 등록", () -> System.out.println("Step 10: 환불 등록"));
    //----------------------------------------------------------------------------------------------

    // 결제 목록 조회
    mockMvc.perform(
                    get("/admin/v1/users/{id}/orders/{orderId}/payments", userId, orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.billingAmount").value(600000))
            .andExpect(jsonPath("$.paymentAmount").value(0))
            .andExpect(jsonPath("$.refundAmount").value(600000))
            .andExpect(jsonPath("$.receivableAmount").value(0))
            .andExpect(jsonPath("$.payments", hasSize(9)))
            .andExpect(jsonPath("$.refunds", hasSize(1)))

            // payments 배열 검증

            .andExpect(jsonPath("$.payments[0].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[0].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[0].type").value("신규"))
            .andExpect(jsonPath("$.payments[0].paymentMethod").value("현금"))
            .andExpect(jsonPath("$.payments[0].paymentAmount").value(10000))
            .andExpect(jsonPath("$.payments[0].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[0].modifierName").value("채인숙"))

            .andExpect(jsonPath("$.payments[1].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[1].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[1].type").value("신규"))
            .andExpect(jsonPath("$.payments[1].paymentMethod").value("예금"))
            .andExpect(jsonPath("$.payments[1].paymentAmount").value(20000))
            .andExpect(jsonPath("$.payments[1].accountHolder").value("예금주"))
            .andExpect(jsonPath("$.payments[1].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[1].modifierName").value("채인숙"))

            .andExpect(jsonPath("$.payments[2].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[2].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[2].type").value("신규"))
            .andExpect(jsonPath("$.payments[2].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[2].paymentAmount").value(15000))
            .andExpect(jsonPath("$.payments[2].code").value("2090073"))
            .andExpect(jsonPath("$.payments[2].cardNumber").value("12345"))
            .andExpect(jsonPath("$.payments[2].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[2].modifierName").value("채인숙"))

            .andExpect(jsonPath("$.payments[3].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[3].paymentDate").value(LocalDate.now().toString()))
            .andExpect(jsonPath("$.payments[3].type").value("신규"))
            .andExpect(jsonPath("$.payments[3].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[3].paymentAmount").value(5000))
            .andExpect(jsonPath("$.payments[3].code").value("2090074"))
            .andExpect(jsonPath("$.payments[3].cardNumber").value("00000"))
            .andExpect(jsonPath("$.payments[3].isCancelable").value(false))
            .andExpect(jsonPath("$.payments[3].modifierName").value("채인숙"))

            // 취소된 payments 배열 항목 검증
            .andExpect(jsonPath("$.payments[4].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[4].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[4].type").value("취소"))
            .andExpect(jsonPath("$.payments[4].paymentMethod").value("현금"))
            .andExpect(jsonPath("$.payments[4].paymentAmount").value(-10000))
            .andExpect(jsonPath("$.payments[4].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[4].isCancelable").value(false))

            .andExpect(jsonPath("$.payments[5].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[5].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[5].type").value("취소"))
            .andExpect(jsonPath("$.payments[5].paymentMethod").value("예금"))
            .andExpect(jsonPath("$.payments[5].paymentAmount").value(-20000))
            .andExpect(jsonPath("$.payments[5].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[5].isCancelable").value(false))

            .andExpect(jsonPath("$.payments[6].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[6].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[6].type").value("취소"))
            .andExpect(jsonPath("$.payments[6].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[6].paymentAmount").value(-15000))
            .andExpect(jsonPath("$.payments[6].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[6].isCancelable").value(false))

            .andExpect(jsonPath("$.payments[7].id").value(startsWith("P")))
            .andExpect(jsonPath("$.payments[7].paymentDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.payments[7].type").value("취소"))
            .andExpect(jsonPath("$.payments[7].paymentMethod").value("카드"))
            .andExpect(jsonPath("$.payments[7].paymentAmount").value(-5000))
            .andExpect(jsonPath("$.payments[7].memo").value(startsWith("#P")))
            .andExpect(jsonPath("$.payments[7].isCancelable").value(false))

            .andExpect(jsonPath("$.refunds[0].id").value(startsWith("R")))
            .andExpect(jsonPath("$.refunds[0].refundDate").value(DateUtils.getString(LocalDate.now())))
            .andExpect(jsonPath("$.refunds[0].orderProductName").value("테스트상품/1개월/10회"))
            .andExpect(jsonPath("$.refunds[0].refundAmount").value(600000))
            .andExpect(jsonPath("$.refunds[0].cashAmount").value(600000))
            .andExpect(jsonPath("$.refunds[0].refundReason").value("다른 강의 주문"))

            .andDo(print())
            .andReturn();
    //----------------------------------------------------------------------------------------------
    step("Step 11: 결제이력 조회", () -> System.out.println("Step 11: 결제이력 조회"));
    //----------------------------------------------------------------------------------------------
  }

  /**
   * 주문 취소 시나리오
   */
  @Test
  @Transactional
  @DisplayName("주문 취소 시나리오")
  void b() throws Exception {
    // 회원 등록
    createUser(token, CREATE_USER.formatted(loginId, loginId));
    log.info("새로운 회원 등록..");
    //----------------------------------------------------------------------------------------------
    step("Step 1: 회원 등록", () -> System.out.println("Step 1: 회원 등록"));
    //----------------------------------------------------------------------------------------------

    // 회원 조회
    ResultActions resultActions = getUser(token, "S", "email", loginId)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.list").value(notNullValue()))
            .andDo(print());

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    assertNotNull(userId);
    log.info("등록된 회원({}) 조회..", userId);

    // 상품 등록
    String content = """
        {
          "name":"테스트상품",
          "curriculumYN":"Y",
          "price":60000
        }
        """.trim();
    mockMvc.perform(post("/admin/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token)
                    .content(content))
            .andExpect(status().isOk())
            .andDo(print());
    log.info("상품 등록..");
    //----------------------------------------------------------------------------------------------
    step("Step 2: 상품 등록", () -> System.out.println("Step 2: 상품 등록"));
    //----------------------------------------------------------------------------------------------

    // 상품 조회
    resultActions = mockMvc.perform(get("/admin/v1/products/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andDo(print());

    final String productId = extractOtherFieldFromArray(resultActions, "name", "테스트상품", "id");
    assertNotNull(productId);
    log.info("등록된 상품({}) 조회..", productId);
    //----------------------------------------------------------------------------------------------
    step("Step 3: 상품목록 조회", () -> System.out.println("Step 3: 상품목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 상품 주문
    content = """
        {
             "quantity": 10,
             "teacherId": "M1709859614116619",
             "assistantTeacherId": "M1681990831501448",
             "billingAmount": 600000
        }
        """.trim();
    content = addFieldToJson(content, "productId", productId);
    MvcResult mvcResult = mockMvc.perform(post("/admin/v1/users/{id}/orders/products", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token)
                    .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(notNullValue()))
            .andDo(print())
            .andReturn();

    final String orderId = extractFieldFromResponse(mvcResult, "orderId");
    assertNotNull(orderId);
    log.info("상품 주문({})..", orderId);
    //----------------------------------------------------------------------------------------------
    step("Step 3: 상품 주문1", () -> System.out.println("Step 3: 상품 주문1"));
    //----------------------------------------------------------------------------------------------

    // 회원 주문 목록 조회
    mockMvc.perform(get("/admin/v1/users/{id}/orders", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order[0].id").value(orderId))
            .andExpect(jsonPath("$.order[0].supplyAmount").value(600000)) //(수정) 공급가액 계산 수정 60000->600000
            .andExpect(jsonPath("$.order[0].discountAmount").value(0))
            .andExpect(jsonPath("$.order[0].billingAmount").value(600000))
            .andExpect(jsonPath("$.order[0].paymentAmount").value(0))
            .andExpect(jsonPath("$.order[0].receivableAmount").value(600000))
            .andExpect(jsonPath("$.order[0].createdOn").value(startsWith(DateUtils.getString(LocalDate.now()))))
            .andExpect(jsonPath("$.order[0].creatorName").value("채인숙"))
            .andExpect(jsonPath("$.order[0].orderProductName").value("테스트상품/1개월/10회"))
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 5: 주문목록 조회", () -> System.out.println("Step 5: 주문목록 조회"));
    //----------------------------------------------------------------------------------------------

    // 상품 주문2
    content = """
        {
            "quantity": 1,
             "teacherId": "M1709859614116619",
             "assistantTeacherId": "M1681990831501448",
             "billingAmount": 60000
        }
        """.trim();
    content = addFieldToJson(content, "orderId", orderId);
    content = addFieldToJson(content, "productId", productId);
    mockMvc.perform(post("/admin/v1/users/{id}/orders/products", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(content))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(notNullValue()))
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 6: 상품 주문1", () -> System.out.println("Step 6: 상품 주문1"));
    //----------------------------------------------------------------------------------------------

    // 회원 주문 조회
    mvcResult = mockMvc.perform(get("/admin/v1/users/{id}/orders/{orderId}", userId, orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId))
        .andExpect(jsonPath("$.supplyAmount").value(660000)) // (수정) 120000-> 660000
        .andExpect(jsonPath("$.discountAmount").value(0))
        .andExpect(jsonPath("$.billingAmount").value(660000))
        .andExpect(jsonPath("$.refundAmount").value(0))
        .andExpect(jsonPath("$.isCancelable").value(true))
        .andExpect(jsonPath("$.orderProducts", hasSize(2)))

        // orderProducts 배열의 첫 번째 항목 검증
        .andExpect(jsonPath("$.orderProducts[0].id").value(startsWith("I")))
        .andExpect(jsonPath("$.orderProducts[0].name").value("테스트상품/1개월/10회"))
        .andExpect(jsonPath("$.orderProducts[0].amount").value(600000)) //(수정) 주문 별 공급가액 변경 60000->600000
        .andExpect(jsonPath("$.orderProducts[0].discountAmount").value(0))
        .andExpect(jsonPath("$.orderProducts[0].billingAmount").value(600000))
        .andExpect(jsonPath("$.orderProducts[0].refundAmount").value(0))
        .andExpect(jsonPath("$.orderProducts[0].productType").value("Y"))  // null 값 검증
        .andExpect(jsonPath("$.orderProducts[0].productType").value("Y"))
        .andExpect(jsonPath("$.orderProducts[0].createdOn").value(
            startsWith(DateUtils.getString(LocalDate.now()))))
        .andExpect(jsonPath("$.orderProducts[0].orderType").value("신규"))
        .andExpect(jsonPath("$.orderProducts[0].refundType").value("CANCELABLE"))
        .andExpect(jsonPath("$.orderProducts[0].retake").value(false))
        .andExpect(jsonPath("$.orderProducts[0].hasReservations").value(false))
        .andExpect(jsonPath("$.orderProducts[0].hasPayments").value(false))

        // orderProducts 배열의 두 번째 항목 검증
        .andExpect(jsonPath("$.orderProducts[1].id").value(startsWith("I")))
        .andExpect(jsonPath("$.orderProducts[1].name").value("테스트상품/1개월/1회"))
        .andExpect(jsonPath("$.orderProducts[1].amount").value(60000))
        .andExpect(jsonPath("$.orderProducts[1].discountAmount").value(0))
        .andExpect(jsonPath("$.orderProducts[1].billingAmount").value(60000))
        .andExpect(jsonPath("$.orderProducts[1].refundAmount").value(0))
        .andExpect(jsonPath("$.orderProducts[1].productType").value("Y"))  // null 값 검증
        .andExpect(jsonPath("$.orderProducts[1].createdOn").value(
            startsWith(DateUtils.getString(LocalDate.now()))))
        .andExpect(jsonPath("$.orderProducts[1].orderType").value("신규"))
        .andExpect(jsonPath("$.orderProducts[1].refundType").value("CANCELABLE"))
        .andExpect(jsonPath("$.orderProducts[1].retake").value(false))
        .andExpect(jsonPath("$.orderProducts[1].hasReservations").value(false))
        .andExpect(jsonPath("$.orderProducts[1].hasPayments").value(false))
        .andDo(print())
        .andReturn();

    final String orderProductId1 = extractValueFromJson(mvcResult, "$.orderProducts[1].id",
        String.class);
    assertNotNull(orderProductId1);
    log.info("orderProductId1: {}", orderProductId1);
    //----------------------------------------------------------------------------------------------
    step("Step 8: 회원 주문 조회", () -> System.out.println("Step 8: 회원 주문 조회"));
    //----------------------------------------------------------------------------------------------

    // 주문 취소
    mockMvc.perform(
            delete("/admin/v1/users/{id}/orders/{orderId}/orderProducts/{orderProductId}", userId,
                orderId, orderProductId1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 9: 하나의 주문 취소", () -> System.out.println("Step 9: 하나의 주문 취소"));
    //----------------------------------------------------------------------------------------------

    // 회원 주문 목록 조회
    mockMvc.perform(get("/admin/v1/users/{id}/orders", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order[0].id").value(orderId))
            .andExpect(jsonPath("$.order[0].supplyAmount").value(600000)) // (수정)
            .andExpect(jsonPath("$.order[0].discountAmount").value(0))
            .andExpect(jsonPath("$.order[0].billingAmount").value(600000)) // (수정)
            .andExpect(jsonPath("$.order[0].paymentAmount").value(0))
            .andExpect(jsonPath("$.order[0].receivableAmount").value(600000))
            .andExpect(jsonPath("$.order[0].createdOn").value(startsWith(DateUtils.getString(LocalDate.now()))))
            .andExpect(jsonPath("$.order[0].creatorName").value("채인숙"))
            .andExpect(jsonPath("$.order[0].orderProductName").value("테스트상품/1개월/10회"))
            .andDo(print());

    final String orderProductId2 = extractValueFromJson(mvcResult, "$.orderProducts[0].id",
            String.class);
    assertNotNull(orderProductId2);
    log.info("orderProductId1: {}", orderProductId2);
    //----------------------------------------------------------------------------------------------
    step("Step 10: 회원 주문 조회", () -> System.out.println("Step 10: 회원 주문 조회"));
    //----------------------------------------------------------------------------------------------

    // 주문 취소
    mockMvc.perform(
                    delete("/admin/v1/users/{id}/orders/{orderId}/orderProducts/{orderProductId}", userId,
                            orderId, orderProductId2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 11: 남은 하나의 주문 취소", () -> System.out.println("Step 11: 남은 하나의 주문 취소"));
    //----------------------------------------------------------------------------------------------

    // 회원 주문 조회
    mockMvc.perform(get("/admin/v1/users/{id}/orders/{orderId}", userId, orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isNotFound())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 12: 회원 주문 조회", () -> System.out.println("Step 12: 회원 주문 조회"));
    //----------------------------------------------------------------------------------------------

  }
}