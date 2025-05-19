package com.lms.api.admin.project.task;


import com.lms.api.admin.File.FileStorageService;
import com.lms.api.admin.File.S3FileStorageService;
import com.lms.api.admin.project.task.dto.*;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.QUserEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.FunctionEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.QProjectMemberEntity;
import com.lms.api.common.entity.project.task.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.UserRepository;
import com.lms.api.common.repository.project.FunctionRepository;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final JpaConfig jpaConfig;
    private final FileStorageService fileStorageService;
    private final S3FileStorageService s3FileStorageService;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final FunctionRepository functionRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskFileRepository taskFileRepository;
    private final TaskServiceMapper taskServiceMapper;

    @Transactional
    public String createTask(UserEntity user , CreateTaskRequest createTaskRequest){

        TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(createTaskRequest.getTaskStatusId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

        String taskId = "T" + System.nanoTime();
        log.debug("taskId: {}" , taskId);

        FunctionEntity functionEntity = functionRepository.findById(createTaskRequest.getFunctionId())
                .orElseThrow(()-> new ApiException(ApiErrorCode.FUNCTION_NOT_FOUND));

        int sortOrder = functionEntity.getTaskEntities().size() + 1;
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title(createTaskRequest.getTitle())
                .taskStatusEntity(taskStatusEntity)
                .functionEntity(functionEntity)
                .sortOrder(sortOrder)
                .createdBy(user.getId())
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

        FunctionEntity functionEntity = functionRepository.findById(listTaskRequest.getFunctionId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.FUNCTION_NOT_FOUND));
        String projectId = functionEntity.getProjectEntity().getId();

        BooleanBuilder where = new BooleanBuilder();
        where.and(qTaskEntity.functionEntity.id.eq(listTaskRequest.getFunctionId()));

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
        log.debug("메서드 진입");
        // changes가 null이 아니고 비어있지 않으면
        Optional<List<ChangeTask.Change>> changes = Optional.ofNullable(changeTask.getChanges());
        log.debug("Optinal : {} ", changes);
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

        Optional<UserEntity> assignedMember = userRepository.findById(taskEntity.getAssignedMember());
        Optional<UserEntity> writer = userRepository.findById(taskEntity.getCreatedBy());

        String taskId = taskEntity.getId();
        List<GetTask.SubTask> subTasks = subTaskRepository.findAll().stream()
                .filter(sub -> sub.getTaskEntity().getId().equals(taskId))
                .map(subTaskEntity -> {
                    UserEntity assigneeUser = userRepository.findById(subTaskEntity.getAssignee().getProjectMemberId())
                            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

                    return GetTask.SubTask.builder()
                            .id(subTaskEntity.getId())
                            .userImgUrl(s3FileStorageService.getUrl(assigneeUser.getFileName()))
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

        // 파일명 null 필터링 + fileUrl 직접 세팅
        List<TaskFileEntity> taskFiles = taskFileRepository.findAll().stream()
                .filter(file -> file.getTaskEntity().getId().equals(taskId))
                .filter(file -> file.getFileName() != null)
                .collect(Collectors.toList());

        // 직접 fileUrl 세팅 후 DTO 변환
        List<TaskFileEntity> filesWithUrl = taskFiles.stream()
                .peek(file -> file.setFileName(s3FileStorageService.getUrl(file.getFileName())))
                .collect(Collectors.toList());

        log.debug("✅ fileUrl 확인 : {} ", filesWithUrl);

        // task response 생성
        return GetTask.builder()
                .id(id)
                .title(taskEntity.getTitle())
                .assignedMember(
                        GetTask.ProjectMember.builder()
                                .projectMemberId(assignedMember.get().getId())
                                .projectMemberName(assignedMember.get().getName())
                                .build()
                )
                .description(taskEntity.getDescription())
                .writer(writer.get().getName())
                .taskStatus(GetTask.TaskStatus.builder()
                        .id(taskEntity.getTaskStatusEntity().getId())
                        .name(taskEntity.getTaskStatusEntity().getName())
                        .build())
                .totalSubTask(subTasks.size())
                .completedSubTaskCount((int) completedCount)
                .subTasks(subTasks)
                .taskComments(taskServiceMapper.toTaskComment(taskComments))
                .files(taskServiceMapper.toFile(filesWithUrl))  // 여기서는 fileName에 URL이 들어있음
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

        // 삭제할 파일은 S3에서 먼저 삭제
        if (ObjectUtils.isNotEmpty(updateTask.getDeleteFiles())) {
            updateTask.getDeleteFiles()
                    .forEach(fileId -> {
                        taskEntity.getTaskFileEntities().stream()
                                .filter(taskFileEntity -> taskFileEntity.getId().equals(fileId))
                                .findFirst()
                                .ifPresent(taskFileEntity -> fileStorageService.delete(taskFileEntity.getFileName()));
                    });
        }

        // 새로 업로드한 파일들 업로드 및 DB 저장 준비
        Map<String, String> files = fileStorageService.upload(updateTask.getMultipartFiles());

        // 기존 컬렉션에서 삭제할 파일 제외하고, 새로 업로드한 파일까지 합친 새 리스트 생성
        List<TaskFileEntity> newFileList = taskEntity.getTaskFileEntities().stream()
                .filter(file -> updateTask.getDeleteFiles() == null || !updateTask.getDeleteFiles().contains(file.getId()))
                .collect(Collectors.toList());

        files.forEach((originalName, savedFileName) -> {
            TaskFileEntity newFileEntity = TaskFileEntity.builder()
                    .originalFileName(originalName)
                    .fileName(savedFileName)
                    .modifiedBy(updateTask.getModifiedBy())
                    .taskEntity(taskEntity)
                    .build();
            newFileList.add(newFileEntity);
        });

        // 컬렉션 인스턴스 유지하면서 내용 교체
        taskEntity.getTaskFileEntities().clear();
        taskEntity.getTaskFileEntities().addAll(newFileList);

        taskRepository.save(taskEntity);
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
                                            .fileUrl("")  // 필요하면 프로필 이미지 URL 넣기
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
}



