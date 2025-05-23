package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@AllArgsConstructor@NoArgsConstructor@Builder@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "CreateProjectFunctionRequest", description = "프로젝트 내 추가할 기능")
public class CreateProjectFunctionRequest {

    @Schema(description = "프로젝트 기능의 종류", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    ProjectFunctionType projectFunctionType;

    @Schema(description = "프로젝트 기능명", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    String projectFunctionName;

    @Schema(description = "기능 카테고리 순서 <br> 배열의 인덱스 값으로 설정 되어야합니다.")
    Integer functionSort;

    @Schema(description = "프로젝트 존재 여부. <br> " +
            "처음 프로젝트를 생성할 때 호출할 api라면, false <br> " +
            "기존 프로젝트에 기능을 추가할 때 호출할 api라면, true <br>" +
            "필수값입니다!!" , requiredMode = Schema.RequiredMode.REQUIRED)
    boolean projectExists;

}


