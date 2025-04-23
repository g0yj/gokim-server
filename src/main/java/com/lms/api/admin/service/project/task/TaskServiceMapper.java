package com.lms.api.admin.service.project.task;


import com.lms.api.admin.controller.dto.project.task.GetTaskResponse;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskCommentEntity;
import com.lms.api.common.entity.project.task.TaskFileEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface TaskServiceMapper {


    GetTaskResponse.SubTask toSubTask(SubTaskEntity subTaskEntity);

    List<GetTaskResponse.SubTask> toSubTask(List<SubTaskEntity> subTaskEntities);

    GetTaskResponse.TaskComment toTaskComment(TaskCommentEntity taskCommentEntity);

    List<GetTaskResponse.TaskComment> toTaskComment(List<TaskCommentEntity> taskCommentEntities);

    GetTaskResponse.File toFile(TaskFileEntity taskFileEntity);

    List<GetTaskResponse.File> toFile(List<TaskFileEntity> taskFileEntities);
}
