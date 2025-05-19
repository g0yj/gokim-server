package com.lms.api.admin.project;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.project.dto.*;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.admin.project.enums.ProjectRole;
import com.lms.api.common.entity.QUserEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.*;
import com.lms.api.common.entity.project.task.TaskStatusEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.FunctionRepository;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
import com.lms.api.common.repository.project.task.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final FunctionRepository functionRepository;
    private final TaskStatusRepository taskStatusRepository;
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
                .projectRole(ProjectRole.OWNER)
                .build();

        projectMemberRepository.save(owner);

        List<String> statusNames = Arrays.asList("Idea", "Todo", "InProgress", "Done");

        statusNames.stream()
                .map(name -> TaskStatusEntity.builder()
                        .projectEntity(project)
                        .name(name)
                        .build())
                .forEach(taskStatusRepository::save);

        return projectId;
    }

    @Transactional(readOnly = true)
    public List<Project> listProject(String userId) {
        log.debug("로그인 된 아이디 확인 : {}" , userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        QProjectEntity qProjectEntity = QProjectEntity.projectEntity;
        QProjectMemberEntity qProjectMemberEntity  = QProjectMemberEntity.projectMemberEntity;
        QUserEntity qUserEntity = QUserEntity.userEntity;
        List<ProjectEntity> projectEntities = jpaConfig.queryFactory()
                .selectFrom(qProjectEntity)
                .join(qProjectEntity.userEntity).fetchJoin()
                .leftJoin(qProjectEntity.projectMemberEntities, qProjectMemberEntity).fetchJoin()
                .leftJoin(qProjectMemberEntity.userEntity, qUserEntity).fetchJoin()
                .where(
                        qProjectEntity.userEntity.id.eq(userId) // 만든사람
                                .or(qProjectMemberEntity.userEntity.id.eq(userId))// 참여한사람
                )
                .distinct()
                .fetch();

        return projectEntities.stream()
                .map(projectServiceMapper::toProject)
                .collect(Collectors.toList());
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

        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findByProjectEntity_IdAndUserEntity_IdAndProjectRole(projectId, userId, ProjectRole.OWNER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.ACCESS_DENIED));

        if(projectMemberEntity.getProjectRole() != ProjectRole.OWNER){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }
        projectRepository.delete(projectEntity);
    }

    @Transactional
    public ProjectFunction projectFunction(String projectId){
        List<FunctionEntity> functionEntities = functionRepository.findByProjectEntity_IdOrderByFunctionSortAsc(projectId);
        List<ProjectFunction.Function> functions = functionEntities.stream()
                .map(projectServiceMapper::toFunction)
                .toList();

        return ProjectFunction.builder()
                .projectId(projectId)
                .functions(functions)
                .build();
    }

    @Transactional
    public List<ProjectMember> listMember(String id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        List<ProjectMemberEntity> memberEntities = projectMemberRepository.findAll().stream()
                .filter(pm -> pm.getProjectId().equals(projectEntity.getId()))
                .toList();

        return memberEntities.stream()
                .map(pm -> {
                    UserEntity userEntity = userRepository.findById(pm.getProjectMemberId())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
                    return ProjectMember.builder()
                            .id(pm.getProjectMemberId())
                            .name(userEntity.getName())
                            .email(userEntity.getEmail())
                            .userImgUrl(s3FileStorageService.getUrl(userEntity.getFileName()))
                            .build();
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public void createMember(String projectId, String id){
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        ProjectMemberEntity projectMemberEntity = ProjectMemberEntity.builder()
                .projectMemberId(id)
                .projectId(projectId)
                .projectRole(ProjectRole.MEMBER)
                .projectEntity(project)
                .userEntity(user)
                .build();
        projectMemberRepository.save(projectMemberEntity);
    }
}



