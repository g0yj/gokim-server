package com.lms.api.admin.controller.project.task;

import com.lms.api.admin.controller.dto.project.task.GetTaskResponse;
import com.lms.api.admin.controller.dto.project.task.ListTaskResponse;
import com.lms.api.admin.service.dto.project.task.GetTask;
import com.lms.api.admin.service.dto.project.task.ListTask;
import com.lms.api.common.entity.project.task.SubTaskEntity;
import com.lms.api.common.mapper.ControllerMapper;
import com.lms.api.common.mapper.ControllerMapperConfig;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = ControllerMapperConfig.class, uses = {
    ControllerMapper.class})
public interface TaskControllerMapper {
    List<ListTaskResponse> toListTaskResponse(List<ListTask> tasks);

    @Mapping(target = "taskStatus", source = "taskStatus")
    @Mapping(target = "tasks", source = "tasks")
    ListTaskResponse toListTaskResponse(ListTask task);

    ListTaskResponse.Task toTask(ListTask.TaskItem taskItem);

    ListTaskResponse.TaskStatus toTaskStatus(ListTask.TaskStatus taskStatus);

    ListTaskResponse.ProjectMember toProjectMember(ListTask.ProjectMember member);

    // GetTask -> GetTaskResponse 변환
    @Mapping(target = "assignedMemberId", source = "task.assignedMember.projectMemberId")
    @Mapping(target = "assignedName", source = "task.assignedMember.projectMemberName")
    @Mapping(target = "taskStatusId", source = "task.taskStatus.id")
    @Mapping(target = "taskStatusName", source = "task.taskStatus.name")
    @Mapping(target = "subTasks", source = "task.subTasks") // SubTask 리스트 변환
    @Mapping(target = "taskComments", source = "task.taskComments") // TaskComment 리스트 변환
    GetTaskResponse toGetTaskResponse(GetTask task);

    // SubTask 변환
    @Mapping(target = "subTaskId", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "subTaskAssignedMemberId", source = "subTaskAssignedMemberId")
    @Mapping(target = "subTaskAssignedMemberName", source = "subTaskAssignedMemberName")
    @Mapping(target = "subTaskStatusId", source = "subTaskStatus.id")
    @Mapping(target = "subStatusName", source = "subTaskStatus.name")
    GetTaskResponse.SubTask toSubTask(GetTask.SubTask subTask); // GetTask.SubTask -> GetTaskResponse.SubTask

    @Mapping(target = "taskCommentId", source = "id")
    @Mapping(target = "taskCommentContent", source = "content")
    GetTaskResponse.TaskComment toTaskComment(GetTask.TaskComment subTask);

    @IterableMapping(elementTargetType = GetTaskResponse.SubTask.class)
    List<GetTaskResponse.SubTask> toSubTask(List<GetTask.SubTask> subTasks);

    @IterableMapping(elementTargetType = GetTaskResponse.TaskComment.class)
    List<GetTaskResponse.TaskComment> toTaskComment(List<GetTask.TaskComment> taskComments);
}
