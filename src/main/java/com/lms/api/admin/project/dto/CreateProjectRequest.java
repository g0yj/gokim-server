package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@AllArgsConstructor@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateProjectRequest", description = "프로젝트 생성 데이터")
public class CreateProjectRequest {

    @Schema(description = "프로젝트 명", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String projectName;

    @Schema(description = "프로젝트에 추가할 기능들 <br> NULL일 서버에서 빈페이지 하나를 추가합니다")
    List<CreateProjectFunctionRequest> projectFunctions;

    @Schema(description = "프로젝트 참여 목록으로 회원의 id 값입니다. <br>" +
            "회원의 id 값" +
            "<br> NULL일 프로젝트 생성자만 서버에서 처리합니다")
    List<String> projectMemberId;

}
