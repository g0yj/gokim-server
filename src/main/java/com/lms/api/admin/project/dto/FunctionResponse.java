package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "FunctionResponse", description = "프로젝트가 가진 기능 목록")
public class FunctionResponse {

    ProjectFunctionType projectFunctionType;
    String functionName;

}
