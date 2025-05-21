package com.lms.api.admin.project.task.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTask {

    String id;
    String title;
    ProjectMember assignedMember;
    String description;
    String writer;
    TaskStatus taskStatus;

    int totalSubTask;
    int completedSubTaskCount;

    List<File> files;

    List<SubTask> subTasks;

    List<TaskComment> taskComments;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class File {
        long id;
        String fileUrl;
        String originalFileName;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubTask {
        long id;
        String userImgUrl;
        String content;
        String subTaskAssignedMemberId;
        String subTaskAssignedMemberName;
        TaskStatus subTaskStatus;

    }

    @Getter
    @Builder@AllArgsConstructor@NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskStatus {
        long id;
        String name;

    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskComment {
        long id;
        String content;
    }

    @Getter@AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        String projectMemberId;
        String projectMemberName;
    }

}
