package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateTaskStatusRequest" , description = "수정 데이터")
public class UpdateTaskStatusRequest {
    @Schema(description = "상태명", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String name;
}
