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
@Schema(name = "CreateSubTaskRequest", description = "하위 항목 등록")
public class CreateSubTaskRequest {
    @Schema(description = "하위항목 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String content;
}
