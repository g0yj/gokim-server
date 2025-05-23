package com.lms.api.admin.project.file;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.project.file.dto.CreateProjectFile;
import com.lms.api.admin.project.file.dto.FileMeta;
import com.lms.api.admin.project.file.dto.ListProjectFileRequest;
import com.lms.api.admin.project.file.dto.ListProjectFileResponse;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectFunctionEntity;
import com.lms.api.common.entity.project.file.ProjectFileEntity;
import com.lms.api.common.entity.project.file.QProjectFileEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.ProjectFunctionRepository;
import com.lms.api.common.repository.project.file.ProjectFileRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFileService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;
    private final ProjectFileServiceMapper projectFileServiceMapper;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectFunctionRepository projectFunctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void projectFileUpload(CreateProjectFile createProjectFile) {
        ProjectFunctionEntity projectFunctionEntity = projectFunctionRepository.findById(createProjectFile.getProjectFunctionId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_FUNCTION_NOT_FOUND));

        int sortOrder = projectFileRepository.countByProjectFunctionEntity_Id(createProjectFile.getProjectFunctionId()) + 1;

        // S3 업로드
        List<FileMeta> uploadFiles = s3FileStorageService.upload(createProjectFile.getMultipartFiles(),"project/files");

        // DB 저장
        List<ProjectFileEntity> projectFileEntities = new ArrayList<>();
        for (FileMeta fileMeta : uploadFiles) {
            projectFileEntities.add(
                    ProjectFileEntity.builder()
                            .id("F" + UUID.randomUUID())
                            .originalFileName(fileMeta.getOriginalFileName())
                            .fileName(fileMeta.getS3Key())
                            .projectFunctionEntity(projectFunctionEntity)
                            .createdBy(createProjectFile.getCreatedBy())
                            .modifiedBy(createProjectFile.getCreatedBy())
                            .sortOrder(sortOrder++)
                            .build()
            );
        }

        projectFileRepository.saveAll(projectFileEntities);
    }
    @Transactional
    public List<ListProjectFileResponse> listProjectFile(String loginId, String projectFunctionId, ListProjectFileRequest listProjectFileRequest) {
        QProjectFileEntity qProjectFileEntity = QProjectFileEntity.projectFileEntity;


        ProjectFunctionEntity projectFunctionEntity = projectFunctionRepository.findById(projectFunctionId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_FUNCTION_NOT_FOUND));

        BooleanExpression where = qProjectFileEntity.projectFunctionEntity.id.eq(projectFunctionId);

        if(listProjectFileRequest.getKeyword() != null && !listProjectFileRequest.getKeyword().isEmpty()){
            where = where.and(qProjectFileEntity.originalFileName.contains(listProjectFileRequest.getKeyword()));
        }
        if(listProjectFileRequest.getProjectMemberId() != null && !listProjectFileRequest.getProjectMemberId().isEmpty()){
            where = where.and(qProjectFileEntity.modifiedBy.eq(listProjectFileRequest.getProjectMemberId()));
        }
        if(listProjectFileRequest.getExtension() != null && !listProjectFileRequest.getExtension().isEmpty()){
            where = where.and(qProjectFileEntity.originalFileName.endsWithIgnoreCase("."+ listProjectFileRequest.getExtension()));
        }

        //연결된 file 전체
        List<ProjectFileEntity> projectFileEntities = jpaConfig.queryFactory()
                .selectFrom(qProjectFileEntity)
                .where(where)
                .orderBy(qProjectFileEntity.sortOrder.asc())
                .fetch();

        List<ListProjectFileResponse> files = projectFileEntities.stream()
                .map(entity -> {
                    UserEntity projectMember = userRepository.findById(entity.getModifiedBy())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_MEMBER_NOT_FOUND));

                    return ListProjectFileResponse.builder()
                            .projectFileId(entity.getId())
                            .fileUrl(s3FileStorageService.getUrl(entity.getFileName()))
                            .originalFileName(entity.getOriginalFileName())
                            .extension(s3FileStorageService.getFileExtension(entity.getOriginalFileName()))
                            .projectMemberId(entity.getModifiedBy())
                            .projectMemberName(projectMember.getName())
                            .userImgUrl(s3FileStorageService.getUrl(projectMember.getFileName()))
                            .build();
                }).collect(Collectors.toList());

        return files;
    }
}



