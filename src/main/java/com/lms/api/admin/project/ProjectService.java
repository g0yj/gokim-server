package com.lms.api.admin.project;

import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.project.dto.*;
import com.lms.api.admin.project.enums.ProjectFunctionType;
import com.lms.api.admin.project.task.TaskService;
import com.lms.api.admin.project.task.dto.CreateTaskRequest;
import com.lms.api.admin.project.task.dto.CreateTaskStatusRequest;
import com.lms.api.admin.project.task.dto.CreateTaskStatusResponse;
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
import com.lms.api.common.repository.project.ProjectFunctionRepository;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
import com.lms.api.common.repository.project.task.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final ProjectFunctionRepository projectFunctionRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final FunctionRepository functionRepository;
    private final ProjectServiceMapper projectServiceMapper;
    private final TaskService taskService;

    @Transactional
    public List<FunctionResponse> listFunction() {
        List<FunctionEntity> functionEntities = functionRepository.findAll().stream()
                .toList();
        return functionEntities.stream()
                .map(projectServiceMapper::toFunctionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public String createProject( UserEntity user, CreateProjectRequest createProjectRequest){
        String projectId = "P" + System.nanoTime();

        // 프로젝트 추가
        ProjectEntity project = ProjectEntity.builder()
                .id(projectId)
                .projectName(createProjectRequest.getProjectName())
                .createdBy(user.getId())
                .userEntity(user)
                .projectMemberEntities(new ArrayList<>())
                .projectFunctionEntities(new ArrayList<>())
                .build();
        projectRepository.save(project);

        // 기본적으로 소유자로 projectMember 추가
        ProjectMemberEntity owner = ProjectMemberEntity.builder()
                .projectId(projectId)
                .projectMemberId(user.getId())
                .projectEntity(project)
                .userEntity(user)
                .createdBy(user.getId())
                .projectRole(ProjectRole.OWNER)
                .build();
        projectMemberRepository.save(owner);
        project.getProjectMemberEntities().add(owner);

        // 프로젝트 멤버 추가
        if(createProjectRequest.getProjectMemberId() != null && !createProjectRequest.getProjectMemberId().isEmpty()){
            for(String memberId : createProjectRequest.getProjectMemberId()){
                if(memberId.equals(user.getId())) continue; // 위에서 owner 추가함
                UserEntity userEntity = userRepository.findById(memberId)
                        .orElseThrow(() -> new ApiException( ApiErrorCode.USER_NOT_FOUND));

                ProjectMemberEntity memberEntity = ProjectMemberEntity.builder()
                        .projectId(projectId)
                        .projectMemberId(memberId)
                        .projectEntity(project)
                        .userEntity(userEntity)
                        .createdBy(user.getId())
                        .projectRole(ProjectRole.MEMBER)
                        .build();

                projectMemberRepository.save(memberEntity);
                project.getProjectMemberEntities().add(memberEntity);
            }
        }

        // 프로젝트 기능 추가
        if(createProjectRequest.getProjectFunction() != null && !createProjectRequest.getProjectFunction().isEmpty()){
            int sort = createProjectRequest.getProjectFunction().size()+ 1 ;

            for(CreateProjectRequest.ProjectFunction projectFunction : createProjectRequest.getProjectFunction()){
                String functionId = "PF" + System.nanoTime();

                ProjectFunctionEntity projectFunctionEntity = ProjectFunctionEntity.builder()
                        .id(functionId)
                        .projectFunctionName(
                                projectFunction.getProjectFunctionName() != null ?
                                        projectFunction.getProjectFunctionName() : projectFunction.getProjectFunctionType().getLabel()
                                )
                        .projectFunctionSort(
                                !createProjectRequest.getProjectFunction().isEmpty() ?
                                        projectFunction.getFunctionSort() : sort++
                        )
                        .projectFunctionType(projectFunction.getProjectFunctionType())
                        .projectEntity(project)
                        .build();
                projectFunctionRepository.save(projectFunctionEntity);
                project.getProjectFunctionEntities().add(projectFunctionEntity);

                // Task기능이 추가되면 taskStatus 생성이 필수
                if(projectFunction.getProjectFunctionType().equals(ProjectFunctionType.TASK)){
                    log.debug("TASK일 때 TaskStatus 생성은 필수!! ProjectFunctionType : {}", projectFunction.getProjectFunctionName());
                    CreateTaskStatusRequest createTaskStatusRequest = CreateTaskStatusRequest.builder()
                            .projectFunctionId(functionId)
                            .build();
                    List<CreateTaskStatusResponse> responses = taskService.createTaskStatus(user.getId(),createTaskStatusRequest);
                    for(CreateTaskStatusResponse response : responses){
                        TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(response.getTaskStatusId())
                                .orElseThrow(()-> new ApiException(ApiErrorCode.TASK_NOT_FOUND));
                        log.debug("taskEntity의 프로젝트 id 없는 상태 : {}", taskStatusEntity.getProjectId());
                        taskStatusEntity.setProjectId(projectId);
                        log.debug("프로젝트 id 있는 상태 : {}", taskStatusEntity.getProjectId());
                    }
                }
            }
        } else {
            log.debug("빈페이지 생성하는 곳으로 진입");
            String functionId = "PF" + System.nanoTime();
            ProjectFunctionEntity defaultFunction = ProjectFunctionEntity.builder()
                    .id(functionId)
                    .projectFunctionName(ProjectFunctionType.PAGE.getLabel())  // 원하는 기본 이름 설정 가능
                    .projectFunctionSort(1)
                    .projectFunctionType(ProjectFunctionType.PAGE)
                    .projectEntity(project)
                    .build();
            // todo 추후 페이지 관련한 기능 추가 로직 필요 (테이블 생성 될듯)
            projectFunctionRepository.save(defaultFunction);
            project.getProjectFunctionEntities().add(defaultFunction);

        }

        return projectId;
    }

    @Transactional(readOnly = true)
    public List<Project> listProject(String userId) {

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
    public ProjectFunctionResponse listProjectFunction(String projectId){
        List<ProjectFunctionEntity> functionEntities = projectFunctionRepository.findByProjectEntity_IdOrderByProjectFunctionSortAsc(projectId);
        List<ProjectFunctionResponse.Function> functions = functionEntities.stream()
                .map(projectServiceMapper::toFunction)
                .toList();

        return ProjectFunctionResponse.builder()
                .projectId(projectId)
                .functions(functions)
                .build();
    }

    @Transactional
    public List<ProjectMemberResponse> listMember(String id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        List<ProjectMemberEntity> memberEntities = projectMemberRepository.findAll().stream()
                .filter(pm -> pm.getProjectId().equals(projectEntity.getId()))
                .toList();

        return memberEntities.stream()
                .map(pm -> {
                    UserEntity userEntity = userRepository.findById(pm.getProjectMemberId())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
                    return ProjectMemberResponse.builder()
                            .id(pm.getProjectMemberId())
                            .name(userEntity.getName())
                            .email(userEntity.getEmail())
                            .userImgUrl(s3FileStorageService.getUrl(userEntity.getFileName()))
                            .projectRole(pm.getProjectRole())
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

    @Transactional
    public void deleteMember(String userId, String projectMemberId, String projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        // 소유자인지 체크
        boolean isOwner = projectEntity.getProjectMemberEntities().stream()
                .anyMatch(pm -> pm.getProjectRole() == ProjectRole.OWNER
                        && pm.getUserEntity().getId().equals(userId));
        log.debug("소유자 여부 확인 : {}", isOwner);
        if (!isOwner) {
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }

        // 삭제 대상 멤버 찾기 (projectEntity 컬렉션에서)
        ProjectMemberEntity targetMember = projectEntity.getProjectMemberEntities().stream()
                .filter(pm -> pm.getProjectMemberId().equals(projectMemberId))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_MEMBER_NOT_FOUND));

        // 양방향 관계 고려해 컬렉션에서 제거
        projectEntity.getProjectMemberEntities().remove(targetMember);
        targetMember.setProjectEntity(null);  // 연관 관계 끊기
        log.debug("멤버 삭제 처리 완료 - 트랜잭션 커밋 시 삭제 반영");
    }

    @Transactional
    public void updateMember(String loginId, UpdateMemberRequest updateMemberRequest, String projectId) {
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        // 소유자인지 체크
        boolean isOwner = projectEntity.getProjectMemberEntities().stream()
                .anyMatch(pm -> pm.getProjectRole() == ProjectRole.OWNER
                        && pm.getUserEntity().getId().equals(loginId));
        log.debug("소유자 여부 확인 : {}", isOwner);
        if (!isOwner) {
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }

        ProjectMemberEntity targetMember = projectEntity.getProjectMemberEntities().stream()
                .filter(pm -> pm.getProjectMemberId().equals(updateMemberRequest.getProjectMemberId()))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_MEMBER_NOT_FOUND));
        targetMember.setProjectRole(updateMemberRequest.getProjectRole());
        targetMember.setModifiedBy(loginId);

        projectMemberRepository.save(targetMember);

    }

    // todo 기능 추가 시 마다 로직 수정 필요
    @Transactional
    public void createProjectFunction(String loginId, String projectId, CreateProjectFunctionRequest createProjectFunctionRequest) {

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        boolean isOwner = projectMemberRepository.existsByProjectEntity_IdAndUserEntity_IdAndProjectRole(projectId,loginId, ProjectRole.OWNER );
        if(!isOwner){
            throw new ApiException(ApiErrorCode.ACCESS_DENIED);
        }

        int projectFunctionSort = projectFunctionRepository.findAll().stream().toList().size() + 1;


        String functionId = "PF" + System.nanoTime();
        ProjectFunctionEntity projectFunction = ProjectFunctionEntity.builder()
                .id(functionId)
                .projectFunctionName(createProjectFunctionRequest.getProjectFunctionName())
                .projectFunctionSort(projectFunctionSort)
                .projectFunctionType(createProjectFunctionRequest.getProjectFunctionType())
                .projectEntity(projectEntity)
                .build();
        projectFunctionRepository.save(projectFunction);

        switch (createProjectFunctionRequest.getProjectFunctionType()){
            case TASK -> {
                // taskStatus 생성
                CreateTaskStatusRequest createTaskStatusRequest = CreateTaskStatusRequest.builder()
                        .projectId(projectId)
                        .projectFunctionId(functionId)
                        .name(createProjectFunctionRequest.getProjectFunctionName())
                        .build();
                taskService.createTaskStatus(loginId, createTaskStatusRequest);
            }
            case FILE -> {
            }
            case BOARD -> {
            }
            case CALENDAR -> {
            }
            case PAGE -> {
            }
        }
    }




}



