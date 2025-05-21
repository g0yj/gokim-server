package com.lms.api.admin.project.task;

import com.lms.api.admin.project.task.dto.*;
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

    @Mapping(target = "modifiedBy", source = "user")
    @Mapping(target = "changes", source = "changeTaskRequest.changes")
    ChangeTask toChangeTask(String user, ChangeTaskRequest changeTaskRequest);

    ChangeTask.Change toChange(ChangeTaskRequest.Change change);
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

    @Mapping(target = "multipartFiles" , source = "updateTaskRequest.files")
    @Mapping(target = "fileName" , ignore = true)
    @Mapping(target = "id" , source = "taskId")
    @Mapping(target = "modifiedBy" , source = "modifiedBy")
    @Mapping(target = "assignedMember" , source = "updateTaskRequest.assignedMemberId")
    @Mapping(target = "taskStatusId" , source = "updateTaskRequest.taskStatusId")
    UpdateTask toUpdateTask(String modifiedBy, String taskId, UpdateTaskRequest updateTaskRequest);

    List<ListTaskStatusResponse> toListTaskStatusResponse(List<ListTask.TaskStatus> taskStatuses);
    @Mapping(target = "taskStatusId", source = "id")
    ListTaskStatusResponse toListTaskStatusResponse(ListTask.TaskStatus taskStatuse);
}
