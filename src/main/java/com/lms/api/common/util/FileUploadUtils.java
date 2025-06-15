package com.lms.api.common.util;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class FileUploadUtils {

    /**
     * S3에 파일을 업로드하고, 업로드 도중 예외 발생 시 업로드된 파일을 삭제하는 보상 로직 포함 메서드.
     *
     * @param files 업로드할 MultipartFile 리스트
     * @param uploadDir 업로드할 S3 디렉토리 경로 (예: "community/board")
     * @param s3FileStorageService S3 파일 저장 서비스
     * @return 업로드된 파일 메타데이터 리스트 (FileMeta)
     * @throws RuntimeException 업로드 도중 예외가 발생할 경우, 해당 예외를 다시 throw
     */
    public static List<FileMeta> uploadWithRollback(
            List<MultipartFile> files,
            String uploadDir,
            S3FileStorageService s3FileStorageService
    ) {
        List<FileMeta> uploadedFiles = new ArrayList<>();
        try {
            uploadedFiles = s3FileStorageService.upload(files, uploadDir);
            return uploadedFiles;
        } catch (Exception e) {
            for (FileMeta file : uploadedFiles) {
                try {
                    s3FileStorageService.delete(file.getS3Key());
                } catch (Exception ex) {
                    log.warn("S3 보상 삭제 실패: {}", file.getS3Key(), ex);
                }
            }
            throw e;
        }
    }

    /**
     * 단일 파일 업로드 후 예외 발생 시 보상 로직 포함
     *
     * @param file 업로드할 파일 (nullable 아님)
     * @param uploadDir S3 업로드 경로
     * @param s3FileStorageService 업로드/삭제 서비스
     * @return 업로드된 FileMeta 객체
     */
    public static FileMeta uploadOneWithRollback(
            MultipartFile file,
            String uploadDir,
            S3FileStorageService s3FileStorageService
    ) {
        try {
            return s3FileStorageService.upload(file, uploadDir);
        } catch (Exception e) {
            try {
                // 업로드된 파일이 있으나 실패 → 보상 삭제
                String fileName = file.getOriginalFilename();
                if (fileName != null && !fileName.isBlank()) {
                    s3FileStorageService.delete(uploadDir + "/" + fileName);
                }
            } catch (Exception ex) {
                log.warn("단일 파일 보상 삭제 실패: {}", file.getOriginalFilename(), ex);
            }
            throw e;
        }
    }

}
