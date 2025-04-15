package com.lms.api.admin.service.project;

import com.lms.api.admin.controller.dto.project.CreateProjectRequest;
import com.lms.api.admin.service.dto.project.Project;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.dto.Role;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.QProjectEntity;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final JpaConfig jpaConfig;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectServiceMapper projectServiceMapper;


    @Transactional
    public String createProject( UserEntity user, CreateProjectRequest createProjectRequest){
        String projectId = "P" + System.nanoTime();
        log.debug("projectId: {} ", projectId);
        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .projectName(createProjectRequest.getProjectName())
                .createdBy(user.getId())
                .userEntity(user)
                .build();
        projectRepository.save(project);

        ProjectMemberEntity owner = ProjectMemberEntity.builder()
                .projectId(projectId)
                .projectMemberId(user.getId())
                .projectEntity(project)
                .userEntity(user)
                .createdBy(user.getId())
                .role(Role.OWNER)
                .build();

        projectMemberRepository.save(owner);

        return projectId;
    }

    @Transactional(readOnly = true)
    public List<Project> listProject(String userId) {
        QProjectEntity project = QProjectEntity.projectEntity;

        List<ProjectEntity> projectEntities = jpaConfig.queryFactory()
                .selectFrom(project)
                .join(project.userEntity).fetchJoin() // 🔥 핵심!
                .where(project.userEntity.id.eq(userId))
                .fetch();

        return projectEntities.stream()
                .map(projectServiceMapper::toProject)
                .collect(Collectors.toList());
    }

}



