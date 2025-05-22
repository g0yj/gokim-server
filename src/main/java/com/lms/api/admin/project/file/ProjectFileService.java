package com.lms.api.admin.project.file;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.project.file.dto.CreateProjectFile;
import com.lms.api.admin.project.file.dto.FileMeta;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.project.ProjectFunctionEntity;
import com.lms.api.common.entity.project.file.ProjectFileEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.project.ProjectFunctionRepository;
import com.lms.api.common.repository.project.file.ProjectFileRepository;
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
                            .sortOrder(sortOrder++)
                            .build()
            );
        }

        projectFileRepository.saveAll(projectFileEntities);
    }
}



