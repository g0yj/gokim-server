package com.lms.api.admin.controller.dto.project.task;


import com.lms.api.common.dto.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "todo(보드) 목록 조회")
public class ListTaskResponse {
    @Schema(description = "그룹핑 하여 전달하고 있습니다. 이걸 기준으로 목록을 출력할 수 있습니다.")
    TaskStatus taskStatus;

    @Schema(description = "task 목록")
    List<Task> tasks;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskStatus {
        @Schema(description = "taskStatus 식별키")
        Long id;
        @Schema(description = "taskStatus 이름")
        String name;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Task {
        @Schema(description = "task 식별키")
        String id;
        @Schema(description = "task 제목")
        String title;
        @Schema(description = "순서")
        String sortOrder;

        @Schema(description = "담당자")
        ProjectMember assignedMember;

        @Schema(description = "총 하위 항목 갯수")
        int totalSubTask;
        @Schema(description = "하위 항목의 상태가 '완료' 인 갯수")
        int completedSubTaskCount;

    }
    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        @Schema(description = "담당자 식별키")
        String projectMemberId;
        @Schema(description = "담당자 이름")
        String projectMemberName;
        @Schema(description = "담당자의 권한")
        Role role;
        @Schema(description = "담당자 이미지")
        String file;
    }


}
