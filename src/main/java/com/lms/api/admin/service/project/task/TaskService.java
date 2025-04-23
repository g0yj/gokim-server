package com.lms.api.admin.service.project.task;


import com.lms.api.admin.controller.dto.project.task.CreateTaskRequest;
import com.lms.api.admin.controller.dto.project.task.ListTaskRequest;
import com.lms.api.admin.service.dto.project.task.ListTask;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.QUserEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.ProjectMemberEntity;
import com.lms.api.common.entity.project.QProjectMemberEntity;
import com.lms.api.common.entity.project.task.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
import com.lms.api.common.repository.project.task.TaskStatusRepository;
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
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Transactional
    public String createTask(UserEntity user , CreateTaskRequest createTaskRequest){

        // 현재 반환타입이 Long으로 Optional임. orElseThrow로 예외 처리 필수
        TaskStatusEntity taskStatusEntity = taskStatusRepository.findById(createTaskRequest.getTaskStatusId())
                .orElseThrow(() -> new ApiException(ApiErrorCode.TASK_STATUS_NOT_FOUND));

        String taskId = "T" + System.nanoTime();
        log.debug("taskId: {}" , taskId);

        ProjectEntity projectEntity = projectRepository.findById(createTaskRequest.getProjectId())
                .orElseThrow(()-> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        int sortOrder = taskRepository.countByProjectEntity_Id(createTaskRequest.getProjectId()) + 1;
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title(createTaskRequest.getTitle())
                .taskStatusEntity(taskStatusEntity)
                .projectEntity(projectEntity)
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

        BooleanBuilder where = new BooleanBuilder();
        where.and(qTaskEntity.projectEntity.id.eq(listTaskRequest.getProjectId()));

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
                .where(qProjectMemberEntity.projectEntity.id.eq(listTaskRequest.getProjectId()))
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
                            .file(user.getFile())
                            .role(pm.getRole())
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


}



