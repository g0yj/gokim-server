package com.lms.api.admin.project.task;


import com.lms.api.admin.project.task.dto.GetTask;
import com.lms.api.admin.project.task.dto.UpdateTask;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.entity.project.task.TaskCommentEntity;
import com.lms.api.common.entity.project.task.TaskEntity;
import com.lms.api.common.entity.project.task.TaskFileEntity;
import com.lms.api.common.mapper.ServiceMapper;
import com.lms.api.common.mapper.ServiceMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;


@Mapper(componentModel = "spring", config = ServiceMapperConfig.class, uses = {ServiceMapper.class})
public interface TaskServiceMapper {


    GetTask.SubTask toSubTask(SubTaskEntity subTaskEntity);

    List<GetTask.SubTask> toSubTask(List<SubTaskEntity> subTaskEntities);

    GetTask.TaskComment toTaskComment(TaskCommentEntity taskCommentEntity);

    List<GetTask.TaskComment> toTaskComment(List<TaskCommentEntity> taskCommentEntities);

    GetTask.File toFile(TaskFileEntity taskFileEntity);

    List<GetTask.File> toFile(List<TaskFileEntity> taskFileEntities);

    @Mapping(target = "id", ignore = true)
    void mapTaskEntity (UpdateTask updateTask, @MappingTarget TaskEntity taskEntity);
}
