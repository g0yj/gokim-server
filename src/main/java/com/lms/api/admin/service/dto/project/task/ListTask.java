package com.lms.api.admin.service.dto.project.task;

import com.lms.api.common.dto.Role;
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
    String id;
    String title;
    int sortOrder;
    TaskStatus taskStatus;

    int totalSubTask;
    int completedSubTaskCount;

    List<ProjectMember> projectMember;
    List<SubTask> subTasks;

    String projectId;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        String projectMemberId;
        String projectMemberName;
        Role role;
        String file;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubTask {
        String subTaskId;
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
