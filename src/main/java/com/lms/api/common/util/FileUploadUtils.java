package com.lms.api.common.util;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.File.dto.FileMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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


    /**
     * 기존 파일 삭제 → 새 파일 업로드 → 예외 발생 시 업로드 롤백까지 처리하는 공통 메서드
     *
     * @param filesToUpload      새로 업로드할 파일 리스트 (MultipartFile)
     * @param deleteFileEntities 삭제할 엔티티 리스트 (파일 엔티티)
     * @param s3KeyExtractor     삭제 대상에서 S3 key 추출하는 함수
     * @param uploadFunc         파일 업로드 함수 (List<MultipartFile> → List<FileMeta>)
     * @param deleteFunc         S3 key 삭제 함수 (String → void)
     * @return 업로드 성공한 FileMeta 리스트
     */
    public static <E> List<FileMeta> updateFilesWithRollback(
            List<MultipartFile> filesToUpload,
            List<E> deleteFileEntities,
            Function<E, String> s3KeyExtractor,
            Function<List<MultipartFile>, List<FileMeta>> uploadFunc,
            Consumer<String> deleteFunc
    ) {
        // ✅ S3 삭제는 항상 수행되어야 함
        deleteFileEntities.forEach(fileEntity -> {
            String s3Key = s3KeyExtractor.apply(fileEntity);
            if (s3Key != null && !s3Key.isBlank()) {
                deleteFunc.accept(s3Key);
            }
        });

        // ✅ 업로드 처리 및 예외 시 보상
        List<FileMeta> uploadedFiles = new ArrayList<>();
        if (filesToUpload == null || filesToUpload.isEmpty()) return uploadedFiles;

        try {
            uploadedFiles = uploadFunc.apply(filesToUpload);
        } catch (Exception e) {
            // 업로드 실패 시, 업로드된 파일 S3에서 삭제
            uploadedFiles.forEach(file -> {
                if (file.getS3Key() != null && !file.getS3Key().isBlank()) {
                    deleteFunc.accept(file.getS3Key());
                }
            });
            throw e;
        }

        return uploadedFiles;
    }

    /**
     * 주어진 파일 엔티티 리스트에서 S3 키를 추출하여 S3에서 삭제하는 유틸 메서드입니다.
     *
     * @param fileEntities 삭제할 파일 엔티티 리스트 (예: CommunityBoardFileEntity 등)
     * @param s3KeyExtractor 각 엔티티에서 S3 키(String)를 추출하는 함수
     *                       예: CommunityBoardFileEntity::getFileName
     * @param deleteFunc S3 키를 받아 실제 S3에서 삭제하는 함수
     *                   예: s3FileStorageService::delete
     *
     * @param <E> 파일 엔티티 타입
     */
    public static <E> void deleteS3Files(
            List<E> fileEntities,
            Function<E, String> s3KeyExtractor,
            Consumer<String> deleteFunc
    ) {
        for (E file : fileEntities) {
            String s3Key = s3KeyExtractor.apply(file);
            if (s3Key != null && !s3Key.isBlank()) {
                try {
                    deleteFunc.accept(s3Key);
                } catch (Exception e) {
                    log.warn("S3 파일 삭제 실패: {}", s3Key, e);
                }
            }
        }
    }

}
