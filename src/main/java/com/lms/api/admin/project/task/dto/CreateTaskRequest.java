package com.lms.api.admin.project.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateTaskRequest", description = "이슈 만들기 (todo 생성 데이터)")
public class CreateTaskRequest {

    @Schema(description = "프로젝트 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String projectId;

    @Schema(description = "프로젝트 기능 식별키", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String projectFunctionId;

    @Schema(description = "TODO 제목", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title;

    @Schema(description = "status 식별키로, 검토중, 완료와 같은 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    Long taskStatusId;


}
