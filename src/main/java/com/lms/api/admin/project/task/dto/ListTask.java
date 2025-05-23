package com.lms.api.admin.project.task.dto;

import com.lms.api.admin.project.enums.ProjectRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListTask {

    TaskStatus taskStatus;
    List<TaskItem> tasks;


    @Getter@Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskItem {
        String id;
        String title;
        int sortOrder;
        TaskStatus taskStatus;

        ProjectMember assignedMember;

        List<SubTask> subTasks;
        int totalSubTask;
        int completedSubTaskCount;
    }


    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        String projectMemberId;
        String projectMemberName;
        ProjectRole projectRole;
        String fileUrl;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubTask {
        Long subTaskId;
        TaskStatus taskStatus;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskStatus {
        Long id;
        String name;
    }

}
