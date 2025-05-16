package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Task 조회 (필터링)")
public class ListTaskRequest {

    @NotBlank
    @Schema(description = "프로젝트 기능 식별키")
    String functionId;

    // 필터링
    @Schema(description = "필터링 조건으로, 제목(title)로 조회합니다")
    String search;
    @Schema(description = "필터링 조건으로, 담당자(projectMemberId)로 조회합니다")
    String projectMemberId;




}
