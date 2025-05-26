package com.lms.api.admin.File;

import com.lms.api.admin.File.dto.OldFileInfo;
import com.lms.api.admin.File.enums.FileTableType;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.board.QNoticeFileEntity;
import com.lms.api.common.entity.project.file.QProjectFileEntity;
import com.lms.api.common.entity.project.task.QTaskFileEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 오래된 파일 배치로 제거 하기 위해 사용하는 서비스 로직
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OldFileService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;

    /**
     * 1. 오래된 파일 찾기
     * @param threshold : 기준 시간
     * @return 유저 별 파일 목록
     */
    public Map<String, List<OldFileInfo>> findOldFilesGroupedByUser(LocalDateTime threshold) {
        List<OldFileInfo> allFiles = new ArrayList<>();
        allFiles.addAll(findOldProjectFiles(threshold));
        allFiles.addAll(findOldTaskFiles(threshold));
        allFiles.addAll(findOldNoticeFiles(threshold));

        return allFiles.stream()
                .collect(Collectors.groupingBy(OldFileInfo::getUserId)); // .groupingBy는 stream API임!!
    }


    //================================================================================================
    public List<OldFileInfo> findOldProjectFiles(LocalDateTime threshold){
        QProjectFileEntity qProjectFileEntity = QProjectFileEntity.projectFileEntity;

        return jpaConfig.queryFactory()
                // Projections.constructor(): QueryDSL에서 제공하는 DTO 매핑 도우미 클래스의 메서드
                .select(Projections.constructor(OldFileInfo.class,
                        qProjectFileEntity.createdBy,
                        qProjectFileEntity.fileName,
                        qProjectFileEntity.originalFileName,
                        Expressions.constant(FileTableType.PROJECT)
                ))
                .from(qProjectFileEntity)
                .where(qProjectFileEntity.createdOn.goe(threshold))
                .fetch();
    }
    public List<OldFileInfo> findOldTaskFiles(LocalDateTime threshold) {
        QTaskFileEntity qTaskFileEntity = QTaskFileEntity.taskFileEntity;

        return jpaConfig.queryFactory()
                .select(Projections.constructor(OldFileInfo.class,
                        qTaskFileEntity.createdBy,
                        qTaskFileEntity.fileName,
                        qTaskFileEntity.originalFileName,
                        Expressions.constant(FileTableType.TASK)
                ))
                .from(qTaskFileEntity)
                .where(qTaskFileEntity.createdOn.goe(threshold))
                .fetch();
    }

    public List<OldFileInfo> findOldNoticeFiles(LocalDateTime threshold) {
        QNoticeFileEntity qNoticeFileEntity = QNoticeFileEntity.noticeFileEntity;

        return jpaConfig.queryFactory()
                .select(Projections.constructor(OldFileInfo.class,
                        qNoticeFileEntity.createdBy,
                        qNoticeFileEntity.fileName,
                        qNoticeFileEntity.originalFileName,
                        Expressions.constant(FileTableType.NOTICE)))
                .from(qNoticeFileEntity)
                .where(qNoticeFileEntity.createdOn.goe(threshold))
                .fetch();
    }

}
