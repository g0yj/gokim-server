package com.lms.api.admin.service.project.task;


import com.lms.api.admin.controller.dto.project.task.CreateTaskRequest;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.entity.project.ProjectEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.exception.ApiErrorCode;
import com.lms.api.common.exception.ApiException;
import com.lms.api.common.repository.project.ProjectRepository;
import com.lms.api.common.repository.project.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    @Transactional
    public String createTask(UserEntity user , CreateTaskRequest createTaskRequest){
        String taskId = "T" + System.nanoTime();
        log.debug("taskId: {}" , taskId);

        ProjectEntity projectEntity = projectRepository.findById(createTaskRequest.getProjectId())
                .orElseThrow(()-> new ApiException(ApiErrorCode.PROJECT_NOT_FOUND));

        int sortOrder = taskRepository.countByProjectEntity_Id(createTaskRequest.getProjectId()) + 1;
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title(createTaskRequest.getTitle())
                .taskStatusId(createTaskRequest.getTaskStatusId())
                .projectEntity(projectEntity)
                .sortOrder(sortOrder)
                .createdBy(user.getId())
                .build();
        taskRepository.save(task);
        return taskId;
    }
}



