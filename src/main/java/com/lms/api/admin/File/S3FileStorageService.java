package com.lms.api.admin.File;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.lms.api.common.exception.ApiErrorCode.FILE_SIZE_EXCEEDED;

@Service
@Profile({"dev","prod"})
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageService implements FileStorageService{

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
    private String getActiveProfile(){
        return env.getActiveProfiles()[0];
    }

    // prod와 dev 환경에서 버킷 하나로 동시에 쓰기 때문에 Prefix으로 구분해서 저장한거임! 실무에서는 버킷 따로 쓸 것.
    private String getObjectKey(String subDir, String filename) {
        String profile = getActiveProfile();

        if (subDir == null || subDir.isBlank()) {
            return profile + "/" + filename;
        }

        // 슬래시 정리
        subDir = subDir.replaceAll("^/+", "").replaceAll("/+$", ""); // 앞뒤 슬래시 제거
        return profile + "/uploads/" + subDir + "/" + filename;
    }

    @Override
    public String upload(MultipartFile file, String subDir) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = getObjectKey(subDir, filename);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 변경: 반환할 때 파일명(filename) 대신 S3에 저장된 key 전체를 반환
            return key;
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        return null;
    }

    @Override
    public Map<String, String> upload(List<MultipartFile> files, String subDir) {
        Map<String, String> fileNames = new HashMap<>();

        if (files == null || files.isEmpty()) return fileNames;

        files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .forEach(file -> {
                    if (file.getSize() > getMaxFileSize()) {
                        throw new ApiException(FILE_SIZE_EXCEEDED, maxFileSizeStr, file.getOriginalFilename());
                    }

                    String s3Key = upload(file, subDir); // 여기서 subDir 전달!
                    fileNames.put(file.getOriginalFilename(), s3Key); // key 전체 반환
                });

        return fileNames;
    }

    @Override
    public Map<String, String> upload(List<MultipartFile> files) {
        return null;
    }

    @Override
    public void delete(String subDir, String fileName) {
        log.debug("[S3 DELETE] 삭제 요청 파일명: {}", fileName);
        String key = getObjectKey(subDir, fileName);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            s3Client.deleteObject(request);
            log.debug("[S3 DELETE] 삭제 요청 성공 - key: {}", key);
        } catch (Exception e) {
            log.error("[S3 DELETE] 삭제 요청 실패 - key: {}, error: {}", key, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(String fullKey) {
        if (fullKey == null || fullKey.isBlank()) {
            log.warn("[S3 DELETE] 삭제 요청이 무시됨 - key가 null 또는 빈 문자열입니다.");
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fullKey)
                .build();

        try {
            s3Client.deleteObject(request);
            log.debug("[S3 DELETE] 삭제 성공 - key: {}", fullKey);
        } catch (Exception e) {
            log.error("[S3 DELETE] 삭제 실패 - key: {}, error: {}", fullKey, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public String getUrl(String fileName) {
        if (fileName == null) {
            return "";
        }
        if (fileName.startsWith("http")) {
            return fileName;
        }
        // 변경: getObjectKey 대신 fileName을 바로 넣음. fileName이 key이기 때문
        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
    }


}
