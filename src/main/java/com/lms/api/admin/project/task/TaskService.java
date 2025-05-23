package com.lms.api.admin.project.task;


import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.project.file.dto.FileMeta;
import com.lms.api.admin.project.task.dto.*;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.QUserEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.id.ProjectMemberId;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectFunctionEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.QProjectMemberEntity;
import com.lms.api.common.entity.project.task.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.ProjectFunctionRepository;
import com.lms.api.common.repository.project.ProjectMemberRepository;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.*;
import com.lms.api.common.util.ObjectUtils;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final JpaConfig jpaConfig;
    private final S3FileStorageService s3FileStorageService;
    private final TaskServiceMapper taskServiceMapper;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectFunctionRepository functionRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskFileRepository taskFileRepository;


    @Transactional
    public List<CreateTaskStatusResponse> createTaskStatus(String loginId, CreateTaskStatusRequest createTaskStatusRequest){
        log.debug("createTaskStatus 메서드 진입 확인!! 프로젝트 ID 여부: {} ", createTaskStatusRequest.getProjectId());
        log.debug("createTaskStatus 메서드 진입 확인!! 프로젝트 기능 ID: {} ", createTaskStatusRequest.getProjectFunctionId());

        List<CreateTaskStatusResponse> responses = new ArrayList<>();
        // 만약 projectId가 없다면 기본만 추가
        if(createTaskStatusRequest.getProjectId() == null){
            log.debug("projectId 없을 때는 자동으로 4개 생성!");
            List<String> names = List.of("Idea","Todo","InProgress","Done");

            for (String name : names) {
                TaskStatusEntity taskStatusEntity = TaskStatusEntity.builder()
                        .name(name)
                        .projectId(null)
                        .projectFunctionId(createTaskStatusRequest.getProjectFunctionId())
                        .build();
                taskStatusEntity = taskStatusRepository.save(taskStatusEntity);
                responses.add(
                        CreateTaskStatusResponse.builder()
                                .taskStatusId(taskStatusEntity.getId())
                                .build());
                taskStatusEntity.setProjectId(createTaskStatusRequest.getProjectFunctionId());
                taskStatusEntity= taskStatusRepository.save(taskStatusEntity);
            }
            return responses;
        } else{
            log.debug("프로젝트 id 있을 때 진입하는 거 확인 projectId: {}", createTaskStatusRequest.getProjectId());
            TaskStatusEntity taskStatusEntity = TaskStatusEntity.builder()
                    .name(createTaskStatusRequest.getName())
                    .projectFunctionId(createTaskStatusRequest.getProjectFunctionId())
                    .projectId(createTaskStatusRequest.getProjectId())
                    .createdBy(loginId)
                    .build();
            taskStatusEntity = taskStatusRepository.save(taskStatusEntity);
            responses.add(
                    CreateTaskStatusResponse.builder()
                            .taskStatusId(taskStatusEntity.getId())
                            .build());
            return responses;
        }
    }

    @Transactional
    public List<ListTask.TaskStatus> listTaskStatus(String projectFunctionId) {
        List<TaskStatusEntity> taskStatusEntities = taskStatusRepository.findAll().stream()
                .filter(ts -> ts.getProjectFunctionId() != null
                                && ts.getProjectFunctionId().equals(projectFunctionId)
                        )
                .toList();

        return taskStatusEntities.stream()
                .map(ts -> ListTask.TaskStatus.builder()
                        .id(ts.getId())
                        .name(ts.getName())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTaskStatus(Long taskStatusId, UpdateTaskStatusRequest updateTaskStatusRequest) {
        TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));
        taskStatusEntity.setName(updateTaskStatusRequest.getName());
        taskStatusRepository.save(taskStatusEntity);
    }

    @Transactional
    public void deleteTaskStatus(Long taskStatusId) {
        taskStatusRepository.deleteById(taskStatusId);
    }
    @Transactional
    public String createTask(String loginId , CreateTaskRequest createTaskRequest){

        TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(createTaskRequest.getTaskStatusId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

        String taskId = "T" + System.nanoTime();
        log.debug("taskId: {}" , taskId);

        ProjectFunctionEntity functionEntity = functionRepository.findById(createTaskRequest.getProjectFunctionId())
                .orElseThrow(()-> new ApiException(ApiErrorCode.FUNCTION_NOT_FOUND));

        int sortOrder = functionEntity.getTaskEntities().size() + 1;
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title(createTaskRequest.getTitle())
                .taskStatusEntity(taskStatusEntity)
                .projectFunctionEntity(functionEntity)
                .sortOrder(sortOrder)
                .createdBy(loginId)
                .build();
        taskRepository.save(task);
        return taskId;
    }

    @Transactional
    public List<ListTask> listTask(ListTaskRequest listTaskRequest) {
        QTaskEntity qTaskEntity = QTaskEntity.taskEntity;
        QTaskStatusEntity qTaskStatusEntity = QTaskStatusEntity.taskStatusEntity;
        QSubTaskEntity qSubTaskEntity = QSubTaskEntity.subTaskEntity;
        QProjectMemberEntity qProjectMemberEntity = QProjectMemberEntity.projectMemberEntity;
        QUserEntity qUserEntity = QUserEntity.userEntity;

        ProjectFunctionEntity functionEntity = functionRepository.findById(listTaskRequest.getFunctionId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.FUNCTION_NOT_FOUND));
        String projectId = functionEntity.getProjectEntity().getId();

        BooleanBuilder where = new BooleanBuilder();
        where.and(qTaskEntity.projectFunctionEntity.id.eq(listTaskRequest.getFunctionId()));

        if (listTaskRequest.getSearch() != null && !listTaskRequest.getSearch().isEmpty()) {
            where.and(qTaskEntity.title.containsIgnoreCase(listTaskRequest.getSearch()));
        }


        List<TaskEntity> taskEntities = jpaConfig.queryFactory()
                .selectFrom(qTaskEntity)
                .leftJoin(qTaskEntity.taskStatusEntity, qTaskStatusEntity).fetchJoin()
                .where(where)
                .orderBy(qTaskEntity.sortOrder.asc())
                .fetch();

        List<String> taskIds = taskEntities.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toList());

        Map<String, List<SubTaskEntity>> subTaskMap = jpaConfig.queryFactory()
                .selectFrom(qSubTaskEntity)
                .where(qSubTaskEntity.taskEntity.id.in(taskIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(st -> st.getTaskEntity().getId()));

        Map<String, ProjectMemberEntity> projectMemberMap = jpaConfig.queryFactory()
                .selectFrom(qProjectMemberEntity)
                .leftJoin(qProjectMemberEntity.userEntity, qUserEntity).fetchJoin()
                .where(qProjectMemberEntity.projectEntity.id.eq(projectId))
                .fetch()
                .stream()
                .collect(Collectors.toMap(pm -> pm.getUserEntity().getId(), pm -> pm));

        List<TaskStatusEntity> allStatusEntities = jpaConfig.queryFactory()
                .selectFrom(qTaskStatusEntity)
                .fetch();

        Map<TaskStatusEntity, List<ListTask.TaskItem>> grouped = new LinkedHashMap<>();
        for (TaskStatusEntity status : allStatusEntities) {
            grouped.put(status, new ArrayList<>());
        }

        for (TaskEntity task : taskEntities) {
            TaskStatusEntity status = task.getTaskStatusEntity();

            List<ListTask.SubTask> subTasks = Optional.ofNullable(subTaskMap.get(task.getId())).orElse(List.of())
                    .stream()
                    .map(st -> ListTask.SubTask.builder()
                            .subTaskId(st.getId())
                            .taskStatus(ListTask.TaskStatus.builder()
                                    .id(st.getTaskStatusEntity().getId())
                                    .name(st.getTaskStatusEntity().getName())
                                    .build())
                            .build())
                    .toList();

            ListTask.ProjectMember member = null;
            if (task.getAssignedMember() != null) {
                ProjectMemberEntity pm = projectMemberMap.get(task.getAssignedMember());
                if (pm != null) {
                    UserEntity user = pm.getUserEntity();
                    member = ListTask.ProjectMember.builder()
                            .projectMemberId(pm.getProjectMemberId())
                            .projectMemberName(user.getName())
                            .fileUrl(user.getFileName())
                            .projectRole(pm.getProjectRole())
                            .build();
                }
            }

            if (listTaskRequest.getProjectMemberId() != null && !listTaskRequest.getProjectMemberId().isEmpty()) {
                if (member == null || !listTaskRequest.getProjectMemberId().equals(member.getProjectMemberId())) {
                    continue;
                }
            }

            ListTask.TaskItem item = ListTask.TaskItem.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .sortOrder(task.getSortOrder())
                    .taskStatus(ListTask.TaskStatus.builder()
                            .id(status.getId())
                            .name(status.getName())
                            .build())
                    .assignedMember(member)
                    .subTasks(subTasks)
                    .totalSubTask(subTasks.size())
                    .completedSubTaskCount((int) subTasks.stream()
                            .filter(st -> st.getTaskStatus().getId() == 4L)
                            .count())
                    .build();

            grouped.get(status).add(item);
        }

        return grouped.entrySet().stream()
                .map(entry -> ListTask.builder()
                        .taskStatus(ListTask.TaskStatus.builder()
                                .id(entry.getKey().getId())
                                .name(entry.getKey().getName())
                                .build())
                        .tasks(entry.getValue())
                        .build())
                .toList();
    }

    @Transactional
    public void changeTask(ChangeTask changeTask) {
        // changes가 null이 아니고 비어있지 않으면
        Optional<List<ChangeTask.Change>> changes = Optional.ofNullable(changeTask.getChanges());
        if (changes.isPresent() && !changes.get().isEmpty()) {
            // 첫 번째 TaskId를 사용하여 TaskEntity를 찾는다
            for (ChangeTask.Change change : changes.get()) {
                TaskEntity taskEntity = taskRepository.findById(change.getTaskId())
                        .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_NOT_FOUND));

                TaskStatusEntity taskStatusEntity = taskStatusRepository.findById((long) change.getTaskStatusId())
                        .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

                taskEntity.setTaskStatusEntity(taskStatusEntity);
                taskEntity.setSortOrder(change.getSortOrder());

                taskRepository.save(taskEntity);
            }
        } else {
            log.debug("변경 사항 없음");
        }
    }


    @Transactional
    public GetTask getTask(String id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_NOT_FOUND));

        UserEntity assignedMember = userRepository.findById(taskEntity.getAssignedMember())
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        UserEntity writer = userRepository.findById(taskEntity.getCreatedBy())
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        String taskId = taskEntity.getId();
        List<GetTask.SubTask> subTasks = subTaskRepository.findAll().stream()
                .filter(sub -> sub.getTaskEntity().getId().equals(taskId))
                .map(subTaskEntity -> {
                    UserEntity assigneeUser = userRepository.findById(subTaskEntity.getAssignee().getProjectMemberId())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

                    return GetTask.SubTask.builder()
                            .id(subTaskEntity.getId())
                            .userImgUrl(assigneeUser.getFileName())
                            .content(subTaskEntity.getContent())
                            .subTaskAssignedMemberName(assigneeUser.getName())
                            .subTaskAssignedMemberId(assigneeUser.getId())
                            .subTaskStatus(GetTask.TaskStatus.builder()
                                    .id(subTaskEntity.getTaskStatusEntity().getId())
                                    .name(subTaskEntity.getTaskStatusEntity().getName())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        long completedCount = subTasks.stream()
                .filter(sub -> sub.getSubTaskStatus() != null && sub.getSubTaskStatus().getId() == 4)
                .count();

        List<TaskCommentEntity> taskComments = taskCommentRepository.findAll().stream()
                .filter(comment -> comment.getTaskEntity().getId().equals(taskId))
                .collect(Collectors.toList());

        List<GetTask.TaskComment> commentDtos = taskComments.stream()
                .map(comment -> {
                    UserEntity commentUser = userRepository.findById(comment.getModifiedBy())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

                    return GetTask.TaskComment.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .commentProjectMemberId(commentUser.getId())
                            .commentProjectMemberName(commentUser.getName())
                            .userImgUrl(s3FileStorageService.getUrl(commentUser.getFileName()))
                            .build();
                })
                .collect(Collectors.toList());
        // 파일명 null 필터링 + fileUrl 직접 세팅
        List<TaskFileEntity> taskFiles = taskFileRepository.findAll().stream()
                .filter(file -> file.getTaskEntity().getId().equals(taskId))
                .filter(file -> file.getFileName() != null)
                .collect(Collectors.toList());

        // 직접 fileUrl 세팅 후 DTO 변환
        List<TaskFileEntity> filesWithUrl = taskFiles.stream()
                .peek(file -> file.setFileName(s3FileStorageService.getUrl(file.getFileName())))
                .collect(Collectors.toList());

        // task response 생성
        return GetTask.builder()
                .id(id)
                .title(taskEntity.getTitle())
                .assignedMember(
                        GetTask.ProjectMember.builder()
                                .projectMemberId(assignedMember.getId())
                                .projectMemberName(assignedMember.getName())
                                .userImgUrl(assignedMember.getFileName())
                                .build()
                )
                .description(taskEntity.getDescription())
                .writer(writer.getName())
                .taskStatus(GetTask.TaskStatus.builder()
                        .id(taskEntity.getTaskStatusEntity().getId())
                        .name(taskEntity.getTaskStatusEntity().getName())
                        .build())
                .totalSubTask(subTasks.size())
                .completedSubTaskCount((int) completedCount)
                .subTasks(subTasks)
                .taskComments(commentDtos)
                .files(taskServiceMapper.toFile(filesWithUrl))
                .build();
    }



    @Transactional
    public void updateTask(UpdateTask updateTask) {
        TaskEntity taskEntity = taskRepository.findById(updateTask.getId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_NOT_FOUND));

        taskServiceMapper.mapTaskEntity(updateTask, taskEntity);

        TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(updateTask.getTaskStatusId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

        taskEntity.setTaskStatusEntity(taskStatusEntity);

        // 삭제할 파일 S3에서 제거
        if (ObjectUtils.isNotEmpty(updateTask.getDeleteFiles())) {
            updateTask.getDeleteFiles().forEach(fileId -> {
                taskEntity.getTaskFileEntities().stream()
                        .filter(taskFileEntity -> taskFileEntity.getId().equals(fileId))
                        .findFirst()
                        .ifPresent(taskFileEntity -> {
                            String s3Key = taskFileEntity.getFileName();
                            if (s3Key != null && !s3Key.isBlank()) {
                                s3FileStorageService.delete(s3Key);
                            }
                        });
            });
        }

        // 새로 업로드된 파일 S3에 업로드
        List<FileMeta> uploadedFiles = s3FileStorageService.upload(updateTask.getMultipartFiles(), "project/tasks");

        // 기존 리스트에서 삭제할 파일 제거
        List<TaskFileEntity> newFileList = taskEntity.getTaskFileEntities().stream()
                .filter(file -> updateTask.getDeleteFiles() == null || !updateTask.getDeleteFiles().contains(file.getId()))
                .collect(Collectors.toList());

        // 업로드된 파일들을 TaskFileEntity로 변환하여 추가
        uploadedFiles.forEach(fileMeta -> {
            TaskFileEntity newFileEntity = TaskFileEntity.builder()
                    .originalFileName(fileMeta.getOriginalFileName())
                    .fileName(fileMeta.getS3Key())
                    .modifiedBy(updateTask.getModifiedBy())
                    .taskEntity(taskEntity)
                    .build();
            newFileList.add(newFileEntity);
        });

        // 컬렉션 재구성
        taskEntity.getTaskFileEntities().clear();
        taskEntity.getTaskFileEntities().addAll(newFileList);

        taskRepository.save(taskEntity);
    }

    @Transactional
    public void deleteTask(String userId, String taskId){
        TaskEntity taskEntity = taskRepository.findById(taskId)
                        .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_NOT_FOUND));
        taskRepository.delete(taskEntity);
    }

    @Transactional
    public List<ListSubTaskResponse> listSubTask(String id) {
        return subTaskRepository.findAll().stream()
                .filter(sub -> sub.getTaskEntity().getId().equals(id))
                .map(subTaskEntity -> {
                    UserEntity userEntity = userRepository.findById(subTaskEntity.getAssignee().getProjectMemberId())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

                    TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(subTaskEntity.getTaskStatusEntity().getId())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

                    // SubTask 매핑
                    return ListSubTaskResponse.builder()
                            .id(subTaskEntity.getId())
                            .content(subTaskEntity.getContent())
                            .projectMember(
                                    ListSubTaskResponse.ProjectMember.builder()
                                            .id(userEntity.getId())
                                            .name(userEntity.getName())
                                            .fileUrl(s3FileStorageService.getUrl(userEntity.getFileName()))
                                            .build()
                            )
                            .taskStatus(
                                    ListSubTaskResponse.TaskStatus.builder()
                                            .id(taskStatusEntity.getId())
                                            .name(taskStatusEntity.getName())
                                            .build()
                            )
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createSubTask(String loginId, String taskId, CreateSubTaskRequest createSubTaskRequest) {
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.TASK_NOT_FOUND));

        TaskStatusEntity taskStatusEntity = taskStatusRepository.findAll().get(0);

        ProjectEntity projectEntity = taskEntity.getProjectFunctionEntity().getProjectEntity();
        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findByProjectEntityAndUserEntity_Id(projectEntity, loginId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.PROJECT_MEMBER_NOT_FOUND));

        SubTaskEntity subTaskEntity = SubTaskEntity.builder()
                .content(createSubTaskRequest.getContent())
                .taskEntity(taskEntity)
                .taskStatusEntity(taskStatusEntity)
                .assignee(projectMemberEntity)
                .createdBy(loginId)
                .build();
        subTaskRepository.save(subTaskEntity);
    }

    @Transactional
    public void updateSubTask(String userId, long subTaskId, UpdateSubTaskRequest updateSubTaskRequest) {
        SubTaskEntity subTaskEntity = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.SUB_TASK_NOT_FOUND));

        TaskStatusEntity subStatus = taskStatusRepository.findById(updateSubTaskRequest.getSubTaskStatusId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

        // 복합키를 사용 중이라 이 절차 필요
        ProjectMemberId projectMemberId = new ProjectMemberId();
        projectMemberId.setProjectMemberId(updateSubTaskRequest.getSubTaskAssignedMemberId());
        projectMemberId.setProjectId(updateSubTaskRequest.getProjectId());

        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.PROJECT_MEMBER_NOT_FOUND));

        subTaskEntity.setContent(updateSubTaskRequest.getContent());
        subTaskEntity.setTaskStatusEntity(subStatus);
        subTaskEntity.setAssignee(projectMemberEntity);
        subTaskEntity.setModifiedBy(userId);

        subTaskRepository.save(subTaskEntity);
    }

    @Transactional
    public void createComment(String loginId, String taskId, CreateCommentRequest createCommentRequest) {
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(()-> new ApiException(ApiErrorCode.TASK_NOT_FOUND));

        TaskCommentEntity taskCommentEntity = TaskCommentEntity.builder()
                .content(createCommentRequest.getContent())
                .taskEntity(taskEntity)
                .createdBy(loginId)
                .build();
        taskCommentRepository.save(taskCommentEntity);
    }

    @Transactional
    public void updateComment(String loginId, Long taskCommentId, UpdateCommentRequest updateCommentRequest) {

        TaskCommentEntity taskCommentEntity = taskCommentRepository.findById(taskCommentId)
                        .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_NOT_FOUND));
        taskCommentEntity.setContent(updateCommentRequest.getContent());
        taskCommentEntity.setModifiedBy(loginId);

        taskCommentRepository.save(taskCommentEntity);
    }
    @Transactional
    public void deleteComment(Long taskCommentId) {
        taskCommentRepository.deleteById(taskCommentId);
    }
}



