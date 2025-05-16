package com.lms.api.admin.project.task.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Task 상세 조회")
public class GetTask {

    @Schema(description = "Task 식별키")
    String id;
    @Schema(description = "제목")
    String title;
    @Schema(description = "담당자")
    ProjectMember assignedMember;
    @Schema(description = "설명")
    String description;
    @Schema(description = "보고자")
    String writer;
    @Schema(description = "task 상태")
    TaskStatus taskStatus;

    @Schema(description = "총 하위 항목 갯수")
    int totalSubTask;
    @Schema(description = "하위 항목의 상태가 '완료' 인 갯수")
    int completedSubTaskCount;

    @Schema(description = "첨부된 파일 (변수명이 추후에 변경될 수 있습니다)")
    List<File> files;

    @Schema(description = "하위 업무")
    List<SubTask> subTasks;

    @Schema(description = "댓글")
    List<TaskComment> taskComments;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class File {
        @Schema(description = "이미지 식별키")
        long id;
        @Schema(description = "이미지 링크. src에 넣어야 할 주소")
        String file;
        @Schema(description = "이미지 파일명")
        String originalFile;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubTask {
        long id;
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
