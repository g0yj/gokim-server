package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateProjectFunctionRequest", description = "프로젝트 내 추가할 기능")
public class CreateProjectFunctionRequest {

    @Schema(description = "프로젝트 기능의 종류", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    ProjectFunctionType projectFunctionType;

    @Schema(description = "프로젝트 기능명", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    String projectFunctionName;

}
