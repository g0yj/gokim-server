package com.lms.api.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ReservationAdminServiceTest {

  @Autowired
  ReservationAdminService reservationAdminService;

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void listAttendances() throws Exception {
    ListTeacherAttendancesResponse response = reservationAdminService.getTeacherAttendances("2023-05-01", "Y");

    // ObjectMapper 설정
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    // response 파일 저장
    String responseJson = objectMapper.writeValueAsString(response);
    Path outputPath = Paths.get("src/test/resources/response.json");
    Files.write(outputPath, responseJson.getBytes(StandardCharsets.UTF_8));

    // 202305.json 파일 경로 읽기
    Path jsonPath = ResourceUtils.getFile("classpath:202305.json").toPath();
    String json = Files.readString(jsonPath);

    // JSON 파일을 ListTeacherAttendancesResponse 객체로 로딩
    ListTeacherAttendancesResponse expectedResponse = objectMapper.readValue(json, ListTeacherAttendancesResponse.class);
  }


}