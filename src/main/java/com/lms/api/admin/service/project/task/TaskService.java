package com.lms.api.admin.service.project.task;


import com.lms.api.admin.controller.dto.project.task.CreateTaskRequest;
import com.lms.api.admin.controller.dto.project.task.ListTaskRequest;
import com.lms.api.admin.service.dto.project.task.ListTask;
import com.lms.api.common.config.JpaConfig;
import com.lms.api.common.entity.QUserEntity;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.QProjectEntity;
import com.lms.api.common.entity.project.QProjectMemberEntity;
import com.lms.api.common.entity.project.task.*;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
import com.lms.api.common.repository.project.task.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public List<ListTask> listTask(ListTaskRequest listTaskRequest){
/*
        QTaskEntity qTaskEntity = QTaskEntity.taskEntity;
        QSubTaskEntity qSubTaskEntity = QSubTaskEntity.subTaskEntity;
        QProjectMemberEntity qProjectMemberEntity = QProjectMemberEntity.projectMemberEntity;
        QUserEntity qUserEntity = QUserEntity.userEntity;

        ProjectEntity ProjectEntity = projectRepository.findById(listTaskRequest.getProjectId())
                .orElseThrow(()-> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        List<TaskEntity> taskEntities = jpaConfig.queryFactory()
                .selectFrom(qTaskEntity)
                .leftJoin(qTaskEntity.subTaskEntities,qSubTaskEntity).fetchJoin() // SubTaskEntity를 즉시 로딩
                .leftJoin(qTaskEntity.projectEntity.projectMemberEntities,qProjectMemberEntity).fetchJoin()
                .leftJoin(qTaskEntity.taskStatusEntity, qTaskEntity.taskStatusEntity).fetchJoin()
                .leftJoin(qProjectMemberEntity.userEntity,qUserEntity).fetchJoin()
                .where(qTaskEntity.projectEntity.id.eq(listTaskRequest.getProjectId()),
                        qTaskEntity.title.contains(listTaskRequest.getSearch()),
                        qTaskEntity.assignedMember.eq(listTaskRequest.getProjectMemberId()))
                .groupBy(qTaskEntity.taskStatusEntity)
                .orderBy(qTaskEntity.sortOrder.asc())
                .fetch();

        List<ListTask> taskInfoList = taskEntities.stream()
                .map(task -> {
                    List<ListTask.TaskStatus> taskStatuses = Collections.singletonList(
                            ListTask.TaskStatus.builder()
                                    .id(task.getTaskStatusEntity().getId())
                                    .name(task.getTaskStatusEntity().getName())
                                    .build()
                    );

                    List<ListTask.ProjectMember> projectMembers = task.getProjectEntity().getProjectMemberEntities().stream()
                            .map(member -> ListTask.ProjectMember.builder()
                                    .projectMemberId(member.getUserEntity().getId())
                                    .projectMemberName(member.getUserEntity().getName())
                                    .role(member.getRole())
                                    .file(member.getUserEntity().getFile())
                                    .build())
                            .collect(Collectors.toList());

                    List<ListTask.SubTask> subTasks = task.getSubTaskEntities().stream()
                            .map(subTask -> {
                                ListTask.SubTask.builder()
                                        .subTaskId(subTask.getId())
                                        .taskStatus(ListTask.TaskStatus.builder()
                                                .id(subTask.getTaskStatusEntity().getId())
                                                .name(subTask.getTaskStatusEntity().getName())
                                                .build())
                                        .build()
                            })

                })*/
        return null;
    }
}



