package com.lms.api.admin.File;

import com.lms.api.admin.File.dto.OldFileInfo;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.board.QNoticeFileEntity;
import com.lms.api.common.entity.project.file.QProjectFileEntity;
import com.lms.api.common.entity.project.task.QTaskFileEntity;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                .collect(Collectors.groupingBy(OldFileInfo::getUserId));
    }

    /**
     * 2. 파일 다운로드  : S3 서비스 로직에 downloadFile 메서드 사용
     */


    /**
     * 3. 유저별로 zip 파일 압축
     * @param filesByUser Map<userId, List<OldFileInfo>>
     * @return Map<userId, 압축된 zip 바이트 배열>
     */
    public Map<String, byte[]> compressFilesGroupedByUser (Map<String, List<OldFileInfo>> filesByUser) {
        Map<String, byte[]> zipMap = new HashMap<>();

        for (Map.Entry<String, List<OldFileInfo>> entry : filesByUser.entrySet()){
            String userId = entry.getKey();
            List<OldFileInfo> files = entry.getValue();

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

                for (OldFileInfo file : files) {
                    byte[] content = s3FileStorageService.downloadFile(file.getS3Key());
                    ZipEntry zipEntry = new ZipEntry(file.getOriginalFileName());
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(content);
                    zipOutputStream.closeEntry();
                }
                zipOutputStream.finish();
                zipMap.put(userId, byteArrayOutputStream.toByteArray());
            } catch (IOException e){
                throw new RuntimeException(" 압축 실패 - userId : " + userId, e);
            }

        }
        return zipMap;
    }



    //================================================================================================
    public List<OldFileInfo> findOldProjectFiles(LocalDateTime threshold){
        QProjectFileEntity qProjectFileEntity = QProjectFileEntity.projectFileEntity;

        return jpaConfig.queryFactory()
                // Projections.constructor(): QueryDSL에서 제공하는 DTO 매핑 도우미 클래스의 메서드
                .select(Projections.constructor(OldFileInfo.class,
                        qProjectFileEntity.createdBy,
                        qProjectFileEntity.fileName,
                        qProjectFileEntity.originalFileName))
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
                        qTaskFileEntity.originalFileName))
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
                        qNoticeFileEntity.originalFileName))
                .from(qNoticeFileEntity)
                .where(qNoticeFileEntity.createdOn.goe(threshold))
                .fetch();
    }

}
