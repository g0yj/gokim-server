package com.lms.api.admin.project.task.dto;


import com.lms.api.admin.project.enums.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter@Builder
@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "하위 업무 항목")
public class ListSubTaskResponse {
    @Schema(description = "하위 업무 식별키")
    Long id;

    @Schema(description = "요약")
    String content;

    @Schema(description = "담당자")
    ProjectMember projectMember;

    @Schema(description = "상태")
    TaskStatus taskStatus;


    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskStatus {
        @Schema(description = "subTaskStatus 식별키")
        Long id;
        @Schema(description = "subTaskStatus 이름")
        String name;
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProjectMember {
        @Schema(description = "프로젝트 멤버 식별키")
        String id;
        @Schema(description = "프로젝트 멤버 이름")
        String name;
        @Schema(description = "프로젝트 멤버 이미지 주소")
        String fileUrl;
    }


}
