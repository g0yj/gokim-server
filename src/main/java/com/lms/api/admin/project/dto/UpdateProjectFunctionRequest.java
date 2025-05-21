package com.lms.api.admin.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateProjectFunctionRequest", description = "사이드바의 기능 목록을 수정합니다. 순서 변경시, 기능명 변경 시 사용")
public class UpdateProjectFunctionRequest {
    @Schema(description = "프로젝트 기능 식별키" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String projectFunctionId;

    @Schema(description = "프로젝트 기능명" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String projectFunctionName;

    @Schema(description = "프로젝트 기능 순서" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    int projectFunctionSort;
}
