package com.lms.api.admin.File;

import com.lms.api.admin.File.dto.OldFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
class OldFileServiceTest {

    @Autowired OldFileService oldFileService;
    @Autowired S3FileStorageService s3FileStorageService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void findOldFilesGroupedByUser_최근1시간정상조회() {
        //given
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(3);
        //when
        Map<String, List<OldFileInfo>> result = oldFileService.findOldFilesGroupedByUser(oneMinuteAgo);
        //then
        assertThat(result).isNotNull();
        result.forEach((userId, files)-> {
            for(OldFileInfo file : files) {
                System.out.println(" -s3Key : " + file.getS3Key());
                System.out.println(" -originalFileName : " + file.getOriginalFileName());
            }
        });
    }

    @Test
    void downloasdFile_정상작동_파일내용확인(){
        //given
        String s3Key = "dev/uploads/board/notice/b555bce8-fc98-4d0b-9f52-3ebb4b44f708_CSV파일.png";
        //when
        byte[] content = s3FileStorageService.downloadFile(s3Key);

        //then
        assertThat(content).isNotNull();
        assertThat(content.length).isGreaterThan(0); // 파일이 비어있는가
        System.out.println("다운로드된 파일 크기 : " + content.length);
        System.out.println("파일 내용 일부 : " + new String(content,0, Math.min(100, content.length)));
    }

    @Test
    void compressFilesGroupedByUser_정상작동() {
        // given - 최근 10분 이내 파일을 조회
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusHours(1);
        Map<String, List<OldFileInfo>> filesByUser = oldFileService.findOldFilesGroupedByUser(tenMinutesAgo);

        // when - 압축 수행
        Map<String, byte[]> result = oldFileService.compressFilesGroupedByUser(filesByUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(filesByUser.size());

        result.forEach((userId, zipData) -> {
            assertThat(zipData).isNotEmpty();
            System.out.println("압축된 zip 크기 (userId: " + userId + "): " + zipData.length);
        });
    }
    @Test
    void generatePresignedUrl_만료후_접근실패확인() throws InterruptedException {
        // given
        String s3Key = "dev/uploads/board/notice/sample.png"; // 실제 존재하는 S3 키
        Duration duration = Duration.ofSeconds(1); // 1초만 유효

        String url = s3FileStorageService.generatePresignedUrl(s3Key, duration);
        assertThat(url).contains("https://");

        System.out.println("⏳ 생성된 URL (만료 예정): " + url);

        // 2초 대기 → URL 만료 유도
        Thread.sleep(2000);

        // when & then
        try {
            restTemplate.getForEntity(url, byte[].class);
            fail("만료된 Presigned URL로 접근했는데 예외가 발생하지 않음");
        } catch (HttpClientErrorException e) {
            System.out.println("✅ 만료된 URL 접근 실패 확인 (status code: " + e.getStatusCode() + ")");
            assertThat(e.getStatusCode().value()).isEqualTo(403);
        }
    }
}

