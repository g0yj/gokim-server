package com.lms.api.admin.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UpdateProjectRequest", description = "프로젝트 수정 데이터")
public class UpdateProjectRequest {

    @Schema(description = "프로젝트 명")
    @NotBlank(message = "필수 값")
    String projectName;

}
