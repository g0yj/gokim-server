package com.lms.api.admin.project.task.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class Task {
    Long id;
    String title;
    String content;
    Integer sortOrder;

    String projectId;
    String taskStatusId;

    List<SubTask> subTasks;
    List<TaskFile> taskFiles;
    List<TaskComment> taskComments;
}
