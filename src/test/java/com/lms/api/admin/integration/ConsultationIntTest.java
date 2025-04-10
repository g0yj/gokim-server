package com.lms.api.admin.integration;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.lms.api.common.dto.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("상담시나리오")
@ActiveProfiles("test")
class ConsultationIntTest extends IntegrationTestSupport {

  @Autowired
  private ConsultationRepository consultationRepository;

  String token = null;

  @BeforeEach
  void setUp() throws Exception {
    token = login("jenchae@naver.com", "1111", UserType.A);
  }

  /**
   * 상담등록 및 조회 검증
   */
  @Test
  @Transactional
  @DisplayName("추가 상담 이력을 통해 회원전환 성공")
  void a() throws Exception {
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    // 현재 시간을 LocalDateTime으로 가져오기
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String nowFormatted = now.format(formatter);

    // MultipartFile 모킹
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        "text/plain",
        "This is a test file.".getBytes()
    );

    // 상담등록
    mockMvc.perform(multipart("/admin/v1/consultations")
            .file(file)
            .param("type", "P")
            .param("status", "3")
            .param("name", "회원전환성공케이스")
            .param("gender", "F")
            .param("callTime", "TIME_02")
            .param("cellPhone", "010-1234-1234")
            .param("email", "zzz@naver.com")
            .param("company", "ABC Corp")
            .param("studyPurposes", "WORK", "ETC")
            .param("foundPath", "ONLINE")
            .param("foundPathNote", "영어회화")
            .param("job", "사무직")
            .param("consultationDate", nowFormatted)  // 현재 시간으로 설정
            .param("visitDate", nowFormatted)  // 현재 시간으로 설정
            .param("details", "상담했습니다.")
            .contentType("multipart/form-data")
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk());

    final Long consultationId = consultationRepository.findMaxId();
    log.debug("id: {}", consultationId);
    //----------------------------------------------------------------------------------------------
    step("Step 1: 상담 등록", () -> System.out.println("Step 1: 상담 등록"));
    //----------------------------------------------------------------------------------------------

    // 등록된 상담 조회
    MvcResult mvcResult = mockMvc.perform(get("/admin/v1/consultations/{id}", consultationId)
            .header(AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(consultationId))  // ID 값 검증
        .andExpect(jsonPath("$.consultationDate").value(nowFormatted))  // 상담 날짜 검증
        .andExpect(jsonPath("$.name").value("회원전환성공케이스"))  // 이름 검증
        .andExpect(jsonPath("$.gender").value("F"))  // 성별 검증
        .andExpect(jsonPath("$.job").value("사무직"))  // 직업 검증
        .andExpect(jsonPath("$.company").value("ABC Corp"))  // 회사명 검증
        .andExpect(jsonPath("$.cellPhone").value("010-1234-1234"))  // 휴대전화 검증
        .andExpect(jsonPath("$.foundPathNote").value("영어회화"))  // 발견 경로 비고 검증
        .andExpect(jsonPath("$.visitDate").value(nowFormatted))  // 방문 날짜 검증
        .andExpect(jsonPath("$.details").value("상담했습니다."))  // 상담 내용 검증
        .andExpect(jsonPath("$.type").value("P"))  // 상담 유형 검증
        .andExpect(jsonPath("$.studyPurposes", hasItems("WORK", "ETC")))  // 학습 목적 검증
        .andExpect(jsonPath("$.email").value("zzz@naver.com"))  // 이메일 검증
        .andExpect(jsonPath("$.status").value("3"))  // 상태 검증
        .andExpect(jsonPath("$.file").value(notNullValue()))  // 파일 검증
        .andExpect(jsonPath("$.originalFile").value("test.txt"))  // 원본 파일명 검증
        .andExpect(jsonPath("$.fileUrl").value(notNullValue()))  // 파일 URL 검증
        .andExpect(jsonPath("$.createdBy").value("U1370839971594082"))  // 생성자 검증
        .andExpect(
            jsonPath("$.createdOn").value((startsWith(nowFormatted.substring(0, 10)))))  // 생성일 검증
        .andExpect(jsonPath("$.modifiedBy").value("U1370839971594082"))  // 수정자 검증
        .andExpect(
            jsonPath("$.modifiedOn").value((startsWith(nowFormatted.substring(0, 10)))))  // 수정일 검증
        .andExpect(jsonPath("$.foundPath").value("ONLINE"))  // 발견 경로 검증
        .andExpect(jsonPath("$.callTime").value("TIME_02"))  // 통화 시간 검증
        .andExpect(jsonPath("$.modifiedName").value("채인숙"))  // 수정자 이름 검증
        .andExpect(jsonPath("$.createdName").value("채인숙"))  // 생성자 이름 검증
        .andDo(print())
        .andReturn();
    //----------------------------------------------------------------------------------------------
    step("Step 2: 상담 상세 조회", () -> System.out.println("Step 2: 상담 상세 조회"));
    //----------------------------------------------------------------------------------------------

    // 추가 상담 등록
    Map<String, Object> params = new HashMap<>();
    params.put("details", "추가상담이력등록");
    mockMvc.perform(post("/admin/v1/consultations/history/{id}", consultationId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(params)))
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 3: 추가상담 등록", () -> System.out.println("Step 3: 추가상담 등록"));
    //----------------------------------------------------------------------------------------------

    // 추가 상담 목록 조회
    mockMvc.perform(get("/admin/v1/consultations/history/{id}", consultationId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(params)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].details").value("추가상담이력등록"))
        .andExpect(jsonPath("$[0].date").value(startsWith(nowFormatted.substring(0, 10))))
        .andExpect(jsonPath("$[0].modifiedName").value("채인숙"))
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 4: 추가상담 목록조회", () -> System.out.println("Step 4: 추가상담 목록조회"));
    //----------------------------------------------------------------------------------------------

    // 응답에서 JSON 추출 (response body)
    String responseContent = mvcResult.getResponse().getContentAsString();

    // JSON을 직접 가공할 수 있도록 JSONObject 사용
    JSONObject jsonResponse = new JSONObject(responseContent);

    // 기본 JSON 데이터를 만들고, 필요한 정보를 MvcResult에서 대체
    JSONObject registerContent = new JSONObject()
        .put("address", "경기 안산시 상록구 가루개로 42-15 (양상동)")  // 기존 정보
        .put("addressType", "H")  // 기존 정보
        .put("cellPhone", jsonResponse.getString("cellPhone"))  // MvcResult에서 가져온 휴대전화 정보
        .put("company", jsonResponse.getString("company"))  // MvcResult에서 가져온 회사 정보
        .put("coursePurposes", new JSONArray(List.of("STUDY_ABROAD", "TEST")))  // 기존 정보
        .put("detailedAddress", "detailedAddress")  // 기존 정보
        .put("email", jsonResponse.getString("email"))  // MvcResult에서 가져온 이메일 정보
        .put("etcLanguage", jsonResponse.optString("foundPathNote",
            "etcLanguage"))  // MvcResult에서 가져온 발견 경로 비고 또는 기본값
        .put("firstNameEn", "firstNameEn")  // 기존 정보
        .put("foreignCountry", "foreignCountry")  // 기존 정보
        .put("foreignPeriod", "foreignPeriod")  // 기존 정보
        .put("foreignPurpose", "foreignPurpose")  // 기존 정보
        .put("gender", jsonResponse.getString("gender"))  // MvcResult에서 가져온 성별 정보
        .put("isActive", false)  // 기존 정보
        .put("isOfficeWorker",
            jsonResponse.getString("job").equals("사무직"))  // MvcResult에서 사무직 여부 판단
        .put("isReceiveEmail", false)  // 기존 정보
        .put("isReceiveSms", false)  // 기존 정보
        .put("joinPath", jsonResponse.getString("foundPath"))  // MvcResult에서 발견 경로 정보
        .put("languageSkills", new JSONArray(List.of(
            new JSONObject().put("languageTest", "TOEIC").put("score", "900점이상"),
            new JSONObject().put("languageTest", "TOEIC_S").put("score", "8(190-200)")
        )))  // 기존 정보
        .put("languages", new JSONArray(List.of("EN", "CN", "JP")))  // 기존 정보
        .put("lastNameEn", "lastNameEn")  // 기존 정보
        .put("loginId", jsonResponse.getString("email")) // 기존 정보
        .put("name", jsonResponse.getString("name"))  // MvcResult에서 가져온 이름 정보
        .put("nickname", "nickname")  // 기존 정보
        .put("note", "note")  // 기존 정보
        .put("password", "1111")  // 기존 정보
        .put("phone", jsonResponse.getString("cellPhone"))  // 기존 정보
        .put("phoneType", "H")  // 기존 정보
        .put("position", "position")  // 기존 정보
        .put("textbook", "textbook")  // 기존 정보
        .put("type", "S")  // 기존 정보
        .put("zipcode", "15208");  // 기존 정보

    // 두 번째 API 호출: 상담 회원 등록
    mockMvc.perform(post("/admin/v1/consultations/{id}/users", consultationId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token)
            .content(registerContent.toString()))  // 첫 번째 응답에서 가져온 데이터를 content로 사용
        .andExpect(status().isOk())
        .andDo(print());
    //----------------------------------------------------------------------------------------------
    step("Step 5: 회원상담 등록", () -> System.out.println("Step 5: 회원상담 등록"));
    //----------------------------------------------------------------------------------------------

    // 회원 조회
    ResultActions resultActions = getUser(token, "S", "email", jsonResponse.getString("email"))
        .andExpect(jsonPath("$.list").value(notNullValue()));

    final String userId = extractOtherFieldFromList(resultActions, "email",
        jsonResponse.getString("email"), "id");
    assertNotNull(userId);
    //----------------------------------------------------------------------------------------------
    step("Step 6: 회원 조회", () -> System.out.println("Step 6: 회원 조회"));
    //----------------------------------------------------------------------------------------------

    // 상담 회원 목록 조회
    mockMvc.perform(get("/admin/v1/users/{id}/consultations", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.consultations[0].id").value(notNullValue()))
        .andExpect(jsonPath("$.consultations[0].consultationDate").value(startsWith(DateUtils.getString(
            LocalDate.now()))))
        .andExpect(jsonPath("$.consultations[0].type").value("기타"))
        .andExpect(jsonPath("$.consultations[0].details").value("추가상담이력등록"))
        .andExpect(jsonPath("$.consultations[0].creatorName").value("채인숙"))
        .andExpect(jsonPath("$.consultations[0].topFixedYn").isEmpty())
        .andExpect(jsonPath("$.consultations[0].fontBoldYn").isEmpty())
        .andExpect(jsonPath("$.consultations[0].backgroundColor").isEmpty())

        .andExpect(jsonPath("$.consultations[1].id").value(notNullValue()))
        .andExpect(jsonPath("$.consultations[1].consultationDate").value(startsWith(DateUtils.getString(LocalDate.now()))))
        .andExpect(jsonPath("$.consultations[1].type").value("기타"))
        .andExpect(jsonPath("$.consultations[1].creatorName").value("채인숙"))
        .andExpect(jsonPath("$.consultations[1].topFixedYn").doesNotExist())
        .andExpect(jsonPath("$.consultations[1].fontBoldYn").doesNotExist())
        .andExpect(jsonPath("$.consultations[1].backgroundColor").doesNotExist())  // null 값 검
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 7: 회원상담 목록조회", () -> System.out.println("Step 7: 회원상담 목록조회"));
    //----------------------------------------------------------------------------------------------
  }

}