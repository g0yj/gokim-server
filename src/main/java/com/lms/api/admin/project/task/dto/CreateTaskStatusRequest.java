package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateTaskStatusRequest", description = "작업 상태를 나타내는 걸 만들기 위한 데이터")
public class CreateTaskStatusRequest {
    @Schema(description = "프로젝트 식별키")
    String projectId;

    @Schema(description = "프로젝트 기능 식별키")
    String projectFunctionId;

    @Schema(description = "추가할 상태명 ", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String name;

}
