package com.lms.api.admin.project.dto;

import com.lms.api.admin.project.enums.ProjectFunctionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter@Setter
@AllArgsConstructor@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "프로젝트가 가진 기능 목록")
public class ProjectFunction {
    @Schema(description = "프로젝트 식별키")
    String projectId;
    List<Function> functions;

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Function {
        @Schema(description = "기능(카테고리) 식별키")
        String id;
        @Schema(description = "기능명")
        String functionName;
        @Schema(description = "기능(카테고리) 순서")
        Long functionSort;
        ProjectFunctionType projectFunctionType;
    }


}
