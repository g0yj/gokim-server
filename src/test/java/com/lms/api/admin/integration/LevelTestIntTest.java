package com.lms.api.admin.integration;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lms.api.admin.code.levelTest.TestConsonants;
import com.lms.api.admin.code.levelTest.TestRecommendLevel;
import com.lms.api.admin.code.levelTest.TestStudyType;
import com.lms.api.admin.code.levelTest.TestVowels;
import com.lms.api.common.dto.UserType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("레벨테스트")
@ActiveProfiles("test")
public class LevelTestIntTest extends IntegrationTestSupport {

  CreateUserLevelTestRequest createUserLevelTestRequest;

  String token = null;

  String loginId = null;

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

  @BeforeEach
  void setUp() throws Exception {
    token = login("jenchae@naver.com", "1111", UserType.A);
    loginId = generateRandomUserId();
  }

  @Test
  @Transactional
  @DisplayName("레벨테스트 등록")
  public void a() throws Exception {

    log.info("## loginId:{}", loginId);
    createUser(token, CREATE_USER.formatted(loginId, loginId));
    ResultActions resultActions = getUser(token, "S", "email", loginId)
        .andExpect(jsonPath("$.list").value(notNullValue()));

    final String userId = extractOtherFieldFromList(resultActions, "email", loginId, "id");
    //----------------------------------------------------------------------------------------------
    step("Step 1: 회원 등록", () -> System.out.println("Step 1: 회원 등록"));
    //----------------------------------------------------------------------------------------------

    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);
    createUserLevelTestRequest = new CreateUserLevelTestRequest(
        startTime,
        endTime,
        "John Doe",
        "LBT Value",
        "RBT Value",
        "OBT Value",
        "192.168.0.1",
        new MockMultipartFile("file", "test.txt", "text/plain", "Test file content".getBytes()),
        "This is a note",
        "Study Purpose",
        Arrays.asList(TestStudyType.EC, TestStudyType.BE),
        "Other study type",
        "Family Background",
        "Usage Type",
        "Occupation",
        "Spare Time Activities",
        "Travel Abroad",
        "Future Plans",
        Arrays.asList(TestConsonants.R, TestConsonants.B),
        Arrays.asList(TestVowels.A, TestVowels.B),
        "30",  // Clarity
        "40",  // Intonation
        "50",  // Vocabulary
        "50",  // Verbs Tense
        "40",  // Agreement
        "30",  // Prepositions
        "40",  // Articles
        "30",  // Plurals
        "20",  // Others
        "Strong Point",
        "Weak Point",
        "40",  // Comprehension
        "50",  // Confidence
        "Additional Comments",
        Collections.singletonList(TestRecommendLevel.R7),
        "Other recommended level"
    );

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    mockMvc.perform(multipart("/admin/v1/users/{id}/levelTests", userId)
            .file((MockMultipartFile) createUserLevelTestRequest.getFile())
            .param("testStartTime", createUserLevelTestRequest.getTestStartTime().format(formatter))
            .param("testEndTime", createUserLevelTestRequest.getTestEndTime().format(formatter))
            .param("interviewer", createUserLevelTestRequest.getInterviewer())
            .param("lbt", createUserLevelTestRequest.getLbt())
            .param("rbt", createUserLevelTestRequest.getRbt())
            .param("obt", createUserLevelTestRequest.getObt())
            .param("testIp", createUserLevelTestRequest.getTestIp())
            .param("note", createUserLevelTestRequest.getNote())
            .param("purpose", createUserLevelTestRequest.getPurpose())
            .param("studyType", createUserLevelTestRequest.getStudyType().stream().map(Enum::name)
                .toArray(String[]::new))
            .param("studyTypeEtc", createUserLevelTestRequest.getStudyTypeEtc())
            .param("familyBackground", createUserLevelTestRequest.getFamilyBackground())
            .param("usageType", createUserLevelTestRequest.getUsageType())
            .param("occupation", createUserLevelTestRequest.getOccupation())
            .param("spareTime", createUserLevelTestRequest.getSpareTime())
            .param("travelAbroad", createUserLevelTestRequest.getTravelAbroad())
            .param("futurePlans", createUserLevelTestRequest.getFuturePlans())
            .param("consonants", createUserLevelTestRequest.getConsonants().stream().map(Enum::name)
                .toArray(String[]::new))
            .param("vowels",
                createUserLevelTestRequest.getVowels().stream().map(Enum::name).toArray(String[]::new))
            .param("clarity", createUserLevelTestRequest.getClarity())
            .param("intonation", createUserLevelTestRequest.getIntonation())
            .param("vocabulary", createUserLevelTestRequest.getVocabulary())
            .param("verbsTense", createUserLevelTestRequest.getVerbsTense())
            .param("agreement", createUserLevelTestRequest.getAgreement())
            .param("prepositions", createUserLevelTestRequest.getPrepositions())
            .param("articles", createUserLevelTestRequest.getArticles())
            .param("plurals", createUserLevelTestRequest.getPlurals())
            .param("others", createUserLevelTestRequest.getOthers())
            .param("strongPoint", createUserLevelTestRequest.getStrongPoint())
            .param("weakPoint", createUserLevelTestRequest.getWeakPoint())
            .param("comprehension", createUserLevelTestRequest.getComprehension())
            .param("confidence", createUserLevelTestRequest.getConfidence())
            .param("comments", createUserLevelTestRequest.getComments())
            .param("recommendedLevel",
                createUserLevelTestRequest.getRecommendedLevel().stream().map(Enum::name)
                    .toArray(String[]::new))
            .param("recommendedLevelEtc", createUserLevelTestRequest.getRecommendedLevelEtc())
            .contentType(MediaType.MULTIPART_FORM_DATA) // 멀티파트 요청 설정
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 2: 레벨테스트 등록", () -> System.out.println("Step 2: 레벨테스트 등록"));
    //----------------------------------------------------------------------------------------------
    MvcResult mvcResult = mockMvc.perform(get("/admin/v1/users/{id}/levelTests", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.levelTest", hasSize(1)))
        .andExpect(jsonPath("$.levelTest[0].id").value(notNullValue()))
        .andDo(print())
        .andReturn();

    Integer levelTestId = extractValueFromJson(mvcResult, "$.levelTest[0].id", Integer.class);
    assertNotNull(levelTestId);

    //----------------------------------------------------------------------------------------------
    step("Step 3: 레벨테스트 목록 조회", () -> System.out.println("Step 3: 레벨테스트 목록 조회"));
    //----------------------------------------------------------------------------------------------
    mvcResult = mockMvc.perform(get("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();
    String fileUrl = extractValueFromJson(mvcResult, "$.levelTest.fileUrl", String.class);
    assertNotNull(fileUrl);

    //----------------------------------------------------------------------------------------------
    step("Step 4: 레벨테스트 상세 조회", () -> System.out.println("Step 4: 레벨테스트 상세 조회"));
    //----------------------------------------------------------------------------------------------

    // 기존 응답 데이터를 기반으로 수정된 값 설정
    mockMvc.perform(multipart("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .param("testStartTime", startTime.plusDays(1).format(formatter)) // 수정된 테스트 시작 시간
            .param("testEndTime", endTime.plusDays(1).format(formatter))     // 수정된 테스트 종료 시간
            .param("interviewer", "Updated Interviewer")                     // 수정된 인터뷰어
            .param("lbt", "Updated LBT")                                     // 수정된 LBT
            .param("rbt", "Updated RBT")                                     // 수정된 RBT
            .param("obt", "Updated OBT")                                     // 수정된 OBT
            .param("testIp", "192.168.0.10")                                 // 수정된 테스트 IP
            .param("note", "Updated Note")                                   // 수정된 노트
            .param("purpose", "Updated Purpose")                             // 수정된 목적
            .param("isDeleteFile", "false")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, token)
            .with(request -> {
              request.setMethod("PUT");  // PUT 요청으로 변경
              return request;
            }))
        .andExpect(status().isOk())
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 5: 레벨테스트 수정(첨부수정:X)", () -> System.out.println("Step 5: 레벨테스트 수정(첨부수정:X)"));
    //----------------------------------------------------------------------------------------------

    mockMvc.perform(get("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.levelTest.testStartTime").value(startTime.plusDays(1).format(formatter)))
        .andExpect(jsonPath("$.levelTest.testEndTime").value(endTime.plusDays(1).format(formatter)))
        .andExpect(jsonPath("$.levelTest.interviewer").value("Updated Interviewer"))
        .andExpect(jsonPath("$.levelTest.lbt").value("Updated LBT"))
        .andExpect(jsonPath("$.levelTest.rbt").value("Updated RBT"))
        .andExpect(jsonPath("$.levelTest.obt").value("Updated OBT"))
        .andExpect(jsonPath("$.levelTest.fileUrl").value(fileUrl)) // 기존 URL과 변경 없음
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 6: 수정된 레벨테스트 조회", () -> System.out.println("Step 6: 수정된 레벨테스트 조회"));
    //----------------------------------------------------------------------------------------------

    mockMvc.perform(multipart("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .param("testStartTime", startTime.plusDays(1).format(formatter)) // 수정된 테스트 시작 시간
            .param("testEndTime", endTime.plusDays(1).format(formatter))     // 수정된 테스트 종료 시간
            .param("interviewer", "Updated Interviewer1")                     // 수정된 인터뷰어
            .param("lbt", "Updated LBT1")                                     // 수정된 LBT
            .param("rbt", "Updated RBT1")                                     // 수정된 RBT
            .param("obt", "Updated OBT1")                                     // 수정된 OBT
            .param("testIp", "192.168.0.10")                                 // 수정된 테스트 IP
            .param("note", "Updated Note1")                                   // 수정된 노트
            .param("purpose", "Updated Purpose1")                             // 수정된 목적
            .param("isDeleteFile", "true")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, token)
            .with(request -> {
              request.setMethod("PUT");  // PUT 요청으로 변경
              return request;
            }))
        .andExpect(status().isOk())
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 7: 레벨테스트 수정(첨부삭제)", () -> System.out.println("Step 7: 레벨테스트 수정(첨부삭제)"));
    //----------------------------------------------------------------------------------------------

    mockMvc.perform(get("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.levelTest.testStartTime").value(startTime.plusDays(1).format(formatter)))
        .andExpect(jsonPath("$.levelTest.testEndTime").value(endTime.plusDays(1).format(formatter)))
        .andExpect(jsonPath("$.levelTest.interviewer").value("Updated Interviewer1"))
        .andExpect(jsonPath("$.levelTest.lbt").value("Updated LBT1"))
        .andExpect(jsonPath("$.levelTest.rbt").value("Updated RBT1"))
        .andExpect(jsonPath("$.levelTest.obt").value("Updated OBT1"))
        .andExpect(jsonPath("$.levelTest.fileUrl").value(nullValue()))
        .andExpect(jsonPath("$.levelTest.originalFile").value(nullValue()))
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 8: 수정된 레벨테스트 조회", () -> System.out.println("Step 8: 수정된 레벨테스트 조회"));
    //----------------------------------------------------------------------------------------------

    mockMvc.perform(multipart("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .file(new MockMultipartFile("file", "test1.txt", "text/plain", "New test file content".getBytes()))
            .param("testStartTime", startTime.plusDays(1).format(formatter)) // 수정된 테스트 시작 시간
            .param("testEndTime", endTime.plusDays(1).format(formatter))     // 수정된 테스트 종료 시간
            .param("interviewer", "Updated Interviewer2")                     // 수정된 인터뷰어
            .param("lbt", "Updated LBT2")                                     // 수정된 LBT
            .param("rbt", "Updated RBT2")                                     // 수정된 RBT
            .param("obt", "Updated OBT2")                                     // 수정된 OBT
            .param("testIp", "192.168.0.10")                                 // 수정된 테스트 IP
            .param("note", "Updated Note2")                                   // 수정된 노트
            .param("purpose", "Updated Purpose2")                             // 수정된 목적
            .param("isDeleteFile", "true")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, token)
            .with(request -> {
              request.setMethod("PUT");  // PUT 요청으로 변경
              return request;
            }))
        .andExpect(status().isOk())
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 8: 레벨테스트 수정(첨부수정)", () -> System.out.println("Step 8: 레벨테스트 수정(첨부수정)"));
    //----------------------------------------------------------------------------------------------

    mockMvc.perform(get("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.levelTest.testStartTime").value(startTime.plusDays(1).format(formatter)))
        .andExpect(jsonPath("$.levelTest.testEndTime").value(endTime.plusDays(1).format(formatter)))
        .andExpect(jsonPath("$.levelTest.interviewer").value("Updated Interviewer2"))
        .andExpect(jsonPath("$.levelTest.lbt").value("Updated LBT2"))
        .andExpect(jsonPath("$.levelTest.rbt").value("Updated RBT2"))
        .andExpect(jsonPath("$.levelTest.obt").value("Updated OBT2"))
        .andExpect(jsonPath("$.levelTest.fileUrl").value(notNullValue()))
        .andExpect(jsonPath("$.levelTest.originalFile").value("test1.txt"))
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 9: 수정된 레벨테스트 조회", () -> System.out.println("Step 9: 수정된 레벨테스트 조회"));
    //----------------------------------------------------------------------------------------------

    byte[] largeFileContent = new byte[2 * 1024 * 1024 + 1];
    Arrays.fill(largeFileContent, (byte) 1); // 1로 채움

    MockMultipartFile largeFile = new MockMultipartFile("file", "large_test_file.txt",
        "text/plain", largeFileContent);

    mockMvc.perform(multipart("/admin/v1/users/{id}/levelTests/{testId}", userId, levelTestId)
            .file(largeFile)
            .param("testStartTime", startTime.plusDays(1).format(formatter)) // 수정된 테스트 시작 시간
            .param("testEndTime", endTime.plusDays(1).format(formatter))     // 수정된 테스트 종료 시간
            .param("interviewer", "Updated Interviewer2")                     // 수정된 인터뷰어
            .param("lbt", "Updated LBT3")                                     // 수정된 LBT
            .param("rbt", "Updated RBT3")                                     // 수정된 RBT
            .param("obt", "Updated OBT3")                                     // 수정된 OBT
            .param("testIp", "192.168.0.10")                                 // 수정된 테스트 IP
            .param("note", "Updated Note3")                                   // 수정된 노트
            .param("purpose", "Updated Purpose3")                             // 수정된 목적
            .param("isDeleteFile", "true")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, token)
            .with(request -> {
              request.setMethod("PUT");  // PUT 요청으로 변경
              return request;
            }))
        .andExpect(status().isBadRequest())
        .andDo(print());

    //----------------------------------------------------------------------------------------------
    step("Step 10: 레벨테스트 수정(첨부용량제한)", () -> System.out.println("Step 10: 레벨테스트 수정(첨부용량제한)"));
    //----------------------------------------------------------------------------------------------
  }

}
