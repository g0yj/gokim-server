package com.lms.api.admin.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lms.api.common.dto.UserType;
import com.lms.api.common.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Tag("integration")
@DisplayName("강사관리 시나리오")
@ActiveProfiles("test")
public class TeacherAdminIntTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;

  private String adminToken;
  private final String teacherId = "M1709859614116619";

  @BeforeEach
  void setUp() throws Exception {
    adminToken = login("jenchae@naver.com", "1111", UserType.A);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  @Test
  @Transactional
  @DisplayName("강사 상세 조회")
  void getTeacherDetails() throws Exception {
    String id = "M1574925585880330";
    mockMvc.perform(get("/admin/v1/teachers/{id}", id)
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("나기쁨"))
        .andDo(print());
  }

  @Test
  @DisplayName("강사등록")
  @Transactional
  void createTeacher() throws Exception {
    MockMultipartFile file = createMockFile("test.txt", "This is a test file.");

    mockMvc.perform(multipart("/admin/v1/teachers")
            .file(file)
            .param("name", "John Doe")
            .param("nameEn", "John")
            .param("password", "password123")
            .param("loginId", "johndoe")
            .param("email", "john.doe@example.com")
            .param("gender", Gender.M.toString())
            .param("cellPhone", "123-456-7890")
            .param("workStartDate", LocalDate.now().toString())
            .param("teacherType", TeacherType.LT.toString())
            .param("workTime", WorkTime.AM_8.toString())
            .param("workType", WorkType.A.toString())
            .param("language", Language.ENGLISH.toString())
            .param("partnerTeacherId", "partner123")
            .param("memo", "This is a memo")
            .param("active", String.valueOf(true))
            .param("isReceiveEmail", YN.Y.toString())
            .param("isReceiveSms", YN.Y.toString())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("강사 등록, 목록 조회 및 상세 조회 테스트")
  @Transactional
  void createAndVerifyTeacher() throws Exception {
    MockMultipartFile file1 = createMockFile("test1.txt", "This is a test1 file.");
    MockMultipartFile file2 = createMockFile("test2.txt", "This is a test2 file.");

    // Step 1: Create a teacher
    mockMvc.perform(multipart("/admin/v1/teachers")
            .file(file1)
            .file(file2)
            .param("name", "John Doe")
            .param("nameEn", "John")
            .param("password", "password123")
            .param("loginId", "johndoe")
            .param("email", "john.doe@example.com")
            .param("gender", Gender.M.toString())
            .param("cellPhone", "123-456-7890")
            .param("workStartDate", LocalDate.now().toString())
            .param("teacherType", TeacherType.LT.toString())
            .param("workTime", WorkTime.AM_8.toString())
            .param("workType", WorkType.A.toString())
            .param("language", Language.ENGLISH.toString())
            .param("partnerTeacherId", "partner123")
            .param("memo", "This is a memo")
            .param("active", String.valueOf(true))
            .param("isReceiveEmail", YN.Y.toString())
            .param("isReceiveSms", YN.Y.toString())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, adminToken))
        .andExpect(status().isOk())
        .andDo(print());

    // Step 2: List teachers
    MvcResult result = mockMvc.perform(get("/admin/v1/teachers")
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("active", "true"))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    String newTeacherUserId = extractTeacherIdFromList(result, "John Doe");

    // Step 3: Get details of the newly registered teacher
    mockMvc.perform(get("/admin/v1/teachers/{id}", newTeacherUserId)
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.files", hasSize(2)))
        .andDo(print());
  }

  @Test
  @DisplayName("강사 파일 첨부 업데이트")
  @Transactional
  void updateTeacherWithFiles() throws Exception {
    MockMultipartFile file1 = createMockFile("test1.txt", "This is a test file1.");
    MockMultipartFile file2 = createMockFile("test2.txt", "This is a test file2.");
    MockMultipartFile file3 = createMockFile("test3.txt", "This is a test file3.");

    String id = "M1574925585880330";

    // Step 1: Update teacher with files
    mockMvc.perform(multipart("/admin/v1/teachers/{id}", id)
            .file(file1)
            .file(file2)
            .file(file3)
            .param("name", "John Doe")
            .param("nameEn", "John")
            .param("password", "password123")
            .param("loginId", "johndoe")
            .param("email", "john.doe@example.com")
            .param("gender", Gender.M.toString())
            .param("cellPhone", "123-456-7890")
            .param("workStartDate", LocalDate.now().toString())
            .param("teacherType", TeacherType.LT.toString())
            .param("workTime", WorkTime.AM_8.toString())
            .param("workType", WorkType.A.toString())
            .param("partnerTeacherId", "partner123")
            .param("language", Language.ENGLISH.toString())
            .param("memo", "This is a memo")
            .param("active", "false")
            .param("sort", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, adminToken)
            .with(updateRequest -> {
              updateRequest.setMethod("PUT");
              return updateRequest;
            }))
        .andExpect(status().isOk())
        .andDo(print());

    MvcResult mvcResult = mockMvc.perform(get("/admin/v1/teachers/{id}", id)
            .header(AUTHORIZATION, adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.active").value(false))
        .andExpect(jsonPath("$.files", hasSize(3)))
        .andDo(print())
        .andReturn();

    String deleteFile01 = extractFirstFileId(mvcResult);

    // Step 2: Delete one file and update teacher
    mockMvc.perform(multipart("/admin/v1/teachers/{id}", id)
            .param("name", "U:John Doe")
            .param("nameEn", "U:John")
            .param("password", "password123")
            .param("loginId", "johndoe")
            .param("email", "john.doe@example.com")
            .param("gender", Gender.M.toString())
            .param("cellPhone", "123-456-7890")
            .param("workStartDate", LocalDate.now().toString())
            .param("teacherType", TeacherType.LT.toString())
            .param("workTime", WorkTime.AM_8.toString())
            .param("workType", WorkType.A.toString())
            .param("partnerTeacherId", "partner123")
            .param("language", Language.ENGLISH.toString())
            .param("memo", "U:This is a memo")
            .param("active", String.valueOf(false))
            .param("deleteFiles", deleteFile01)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, adminToken)
            .with(updateRequest -> {
              updateRequest.setMethod("PUT");
              return updateRequest;
            }))
        .andExpect(status().isOk())
        .andDo(print());

    // Step 3: Add files back
    mockMvc.perform(multipart("/admin/v1/teachers/{id}", id)
            .file(file1)
            .file(file2)
            .param("name", "U2:John Doe")
            .param("nameEn", "U2:John")
            .param("password", "password123")
            .param("loginId", "johndoe")
            .param("email", "john.doe@example.com")
            .param("gender", Gender.M.toString())
            .param("cellPhone", "123-456-7890")
            .param("workStartDate", LocalDate.now().toString())
            .param("teacherType", TeacherType.LT.toString())
            .param("workTime", WorkTime.AM_8.toString())
            .param("workType", WorkType.A.toString())
            .param("partnerTeacherId", "partner123")
            .param("language", Language.ENGLISH.toString())
            .param("memo", "U2:This is a memo")
            .param("active", String.valueOf(false))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, adminToken)
            .with(updateRequest -> {
              updateRequest.setMethod("PUT");
              return updateRequest;
            }))
        .andExpect(status().isOk())
        .andDo(print());
  }

  private MockMultipartFile createMockFile(String fileName, String content) {
    return new MockMultipartFile("files", fileName, "text/plain", content.getBytes());
  }

  private String extractTeacherIdFromList(MvcResult result, String teacherName) throws Exception {
    String responseBody = result.getResponse().getContentAsString();
    JsonNode rootNode = new ObjectMapper().readTree(responseBody);
    JsonNode listNode = rootNode.path("list");

    for (JsonNode node : listNode) {
      if (teacherName.equals(node.path("teacherName").asText())) {
        return node.path("userId").asText();
      }
    }
    return null;
  }

  private String extractFirstFileId(MvcResult result) throws Exception {
    Map<String, Object> map = getResultMap(result);
    List<Map<String, Object>> files = (List<Map<String, Object>>) map.get("files");
    return files.get(0).get("id").toString();
  }

  private List<String> extractRemainingFileIds(MvcResult result) throws Exception {
    Map<String, Object> map = getResultMap(result);
    List<Map<String, Object>> files = (List<Map<String, Object>>) map.get("files");
    return List.of(files.get(1).get("id").toString(), files.get(2).get("id").toString());
  }
}
