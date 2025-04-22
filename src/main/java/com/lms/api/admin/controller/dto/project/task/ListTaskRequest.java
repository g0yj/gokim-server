package com.lms.api.admin.controller.dto.project.task;

import com.lms.api.common.service.dto.Search;
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

    String projectId;

    // 필터링
    String search;
    String projectMemberId;





}
