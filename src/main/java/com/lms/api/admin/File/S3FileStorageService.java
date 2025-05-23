package com.lms.api.admin.File;

import com.lms.api.admin.project.file.dto.FileMeta;
import com.lms.api.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.lms.api.common.exception.ApiErrorCode.FILE_SIZE_EXCEEDED;

@Service
@Profile({"dev","prod"})
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageService {

    private final Environment env;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${file.max-file-size}")
    private String maxFileSizeStr;

    private long getMaxFileSize() {
        return DataSize.parse(maxFileSizeStr).toBytes(); // 파일 크기 문자열을 바이트 단위로 변환
    }

    //profile 가져오기. 아래 getObjectKey()를 통해 key 만들기 위함
    private String getActiveProfile() {
        return env.getActiveProfiles()[0];
    }

    // prod와 dev 환경에서 버킷 하나로 동시에 쓰기 때문에 Prefix으로 구분해서 저장한거임! 실무에서는 버킷 따로 쓸 것.
    private String getObjectKey(String subDir, String filename) { //filename : 서버에 저장된 이름
        String profile = getActiveProfile();

        if (subDir == null || subDir.isBlank()) {
            return profile + "/" + filename;
        }

        // 슬래시 정리 없애기
        subDir = subDir.replaceAll("^/+", "").replaceAll("/+$", ""); // 앞뒤 슬래시 제거
        return profile + "/uploads/" + subDir + "/" + filename;
    }

    public String getUrl(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) return "";
        if (s3Key.startsWith("http")) return s3Key;
        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + s3Key;
    }


    // =============================
    // 업로드 메서드
    // =============================

    public FileMeta upload(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) return null;

        if (file.getSize() > getMaxFileSize()) {
            throw new ApiException(FILE_SIZE_EXCEEDED, maxFileSizeStr, file.getOriginalFilename());
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = getObjectKey(subDir, filename);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return new FileMeta(file.getOriginalFilename(), key, getUrl(key));
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    public FileMeta upload(MultipartFile file) {
        return upload(file, null);
    }

    public List<FileMeta> upload(List<MultipartFile> files, String subDir) {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> upload(file, subDir))
                .collect(Collectors.toList());
    }

    public List<FileMeta> upload(List<MultipartFile> files) {
        return upload(files, null);
    }

    // =============================
    // 삭제 메서드
    // =============================

    public void delete(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            log.warn("[S3 DELETE] 삭제 요청 무시됨 - key가 null 또는 빈 문자열입니다.");
            return;
        }

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(request);
            log.info("[S3 DELETE] 삭제 성공 - key: {}", s3Key);
        } catch (Exception e) {
            log.error("[S3 DELETE] 삭제 실패 - key: {}, error: {}", s3Key, e.getMessage(), e);
            throw new RuntimeException("S3 삭제 실패", e);
        }
    }

    public void delete(FileMeta fileMeta) {
        if (fileMeta == null || fileMeta.getS3Key() == null || fileMeta.getS3Key().isBlank()) {
            log.warn("[S3 DELETE] 삭제 요청 무시됨 - FileMeta에 key 없음");
            return;
        }
        delete(fileMeta.getS3Key());
    }

    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}