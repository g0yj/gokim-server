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
     * 파일 업로드 후 연관관계 세팅 및 예외 발생 시 보상 로직 포함
     *
     * @param files 업로드할 파일 목록
     * @param uploadDir S3 업로드 경로 (예: "board/anon", "community/board")
     * @param s3FileStorageService 실제 파일 업로드 및 삭제를 담당하는 서비스
     * @param toEntity 업로드된 파일 메타 정보를 파일 엔티티로 변환하는 함수
     * @param addFileFn 파일 엔티티를 부모(상위) 엔티티에 연관관계로 추가하는 함수 (예: post.addFile(file))
     * @param <E> 파일 엔티티 타입 (예: AnonBoardFileEntity, CommunityBoardFileEntity 등)
     * @param <P> 부모 엔티티 타입 (예: AnonBoardEntity, CommunityBoardEntity 등)
     * @return 업로드된 파일 엔티티 목록
     */
    public static <E extends FileIdentifiable, P> List<E> uploadWithRollback(
            List<MultipartFile> files,
            String uploadDir,
            S3FileStorageService s3FileStorageService,
            Function<FileMeta, E> toEntity,
            BiConsumer<P, E> addFileFn,
            P parentEntity
    ) {
        List<FileMeta> uploadedFiles = new ArrayList<>();
        try {
            // 파일 업로드
            uploadedFiles = s3FileStorageService.upload(files, uploadDir);

            // 업로드된 파일 메타 -> 파일 엔티티로 변환 및 연관관계 설정
            List<E> fileEntities = uploadedFiles.stream()
                    .map(toEntity)
                    .peek(fileEntity -> addFileFn.accept(parentEntity, fileEntity))
                    .collect(Collectors.toList());

            return fileEntities;
        } catch (Exception e) {
            // 예외 발생 시 업로드된 파일 삭제 (보상 로직)
            for (FileMeta fileMeta : uploadedFiles) {
                try {
                    s3FileStorageService.delete(fileMeta.getS3Key());
                } catch (Exception ex) {
                    log.warn("S3 보상 삭제 실패: {}", fileMeta.getS3Key(), ex);
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
