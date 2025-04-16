package com.lms.api.admin.service.project;

import com.lms.api.admin.controller.dto.project.CreateProjectRequest;
import com.lms.api.admin.controller.dto.project.UpdateProjectRequest;
import com.lms.api.admin.service.dto.project.Function;
import com.lms.api.admin.service.dto.project.Project;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.dto.Role;
import com.lms.api.common.entity.QUserEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.QProjectEntity;
import com.lms.api.common.entity.project.QProjectMemberEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
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
    private final TaskRepository taskRepository;
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
        QProjectEntity qProjectEntity = QProjectEntity.projectEntity;
        QProjectMemberEntity qProjectMemberEntity  = QProjectMemberEntity.projectMemberEntity;
        QUserEntity qUserEntity = QUserEntity.userEntity;
        List<ProjectEntity> projectEntities = jpaConfig.queryFactory()
                .selectFrom(qProjectEntity)
                .join(qProjectEntity.userEntity).fetchJoin()
                .leftJoin(qProjectEntity.projectMemberEntities, qProjectMemberEntity).fetchJoin()
                .leftJoin(qProjectMemberEntity.userEntity, qUserEntity).fetchJoin()
                .where(qProjectEntity.userEntity.id.eq(userId))
                .distinct()
                .fetch();

        return projectEntities.stream()
                .map(projectServiceMapper::toProject)
                .collect(Collectors.toList());
    }

    /**
     * 템플릿을 추가할 때 마다 수정이 필요함
     * @param id : 프로젝트 식별키
     * @return taskId: task 관련 기능 목록
     */
    @Transactional
    public Function getProject(String id){
        // '보드' 관련 기능으로 할일에 관련한 기능이 들어간 곳에서 사용
        List<TaskEntity> taskEntities = taskRepository.findByProjectEntity_Id(id);
        List<String> taskIds = taskEntities.stream()
                .map(TaskEntity::getId)
                .toList();

        return Function.builder()
                .projectId(id)
                .taskId(taskIds)
                .build();
    }

    @Transactional
    public void updateProject(String modifiedBy,String id, UpdateProjectRequest updateProjectRequest){
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(()-> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        projectEntity.setModifiedBy(modifiedBy);
        projectEntity.setProjectName(updateProjectRequest.getProjectName());

        projectRepository.save(projectEntity);
    }

    @Transactional
    public void deleteProject(String userId, String projectId){
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findByProjectEntity_IdAndUserEntity_IdAndRole(projectId, userId, Role.OWNER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.ACCESS_DENIED));

        projectRepository.delete(projectEntity);
    }
}



