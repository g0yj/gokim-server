package com.lms.api.common.util;

import com.lms.api.admin.File.dto.FileMeta;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileEntitySynUtil {
    /**
     * 기존 파일 컬렉션에서 삭제 대상은 제거하고, 새로 업로드된 파일을 변환하여 추가합니다.
     * @param originalFiles 원본 엔티티 컬렉션 (JPA 관리)
     * @param deleteIds 삭제할 파일 ID 목록 (nullable)
     * @param uploadedFiles 새로 업로드된 파일 메타 목록
     * @param s3DeleteFn S3 삭제 함수 (예: file -> s3Service.delete(file.getFileName()))
     * @param toEntity 업로드 파일을 엔티티로 변환하는 함수
     * @param <T> 파일 엔티티 타입
     */
    public static <T extends FileIdentifiable> void syncFileEntities(
            Collection<T> originalFiles,
            List<String> deleteIds,
            List<FileMeta> uploadedFiles,
            Consumer<T> s3DeleteFn,
            Function<FileMeta, T> toEntity
    ) {
        // S3 삭제 처리
        if (deleteIds != null) {
            originalFiles.stream()
                    .filter(f -> deleteIds.contains(f.getId()))
                    .forEach(s3DeleteFn);
        }

        // 삭제되지 않은 기존 파일 유지
        List<T> retained = originalFiles.stream()
                .filter(f -> deleteIds == null || !deleteIds.contains(f.getId()))
                .collect(Collectors.toList());

        // 신규 파일 변환
        List<T> newEntities = uploadedFiles.stream()
                .map(toEntity)
                .collect(Collectors.toList());

        originalFiles.clear();
        originalFiles.addAll(retained);
        originalFiles.addAll(newEntities);
    }
}
